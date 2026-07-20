package ru.hantu;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import ru.hantu.commands.WoMCommand;
import ru.hantu.config.WoMConfig;
import ru.hantu.logging.PlayerLogger;
import ru.hantu.network.ClientModListPayload;

import java.util.*;

public class WoMServer implements DedicatedServerModInitializer {

    private static final Map<UUID, PendingPlayer> pendingPlayers = new HashMap<>();

    private static final Set<String> HONEYPOT_MODS = Set.of(
            "fake-mod-123", "definitely-not-a-cheat", "totally-legit-mod"
    );

    private static final Set<String> FABRIC_RELATED_MODS = Set.of(
            "mixinextras", "placeholder-api"
    );

    private static class PendingPlayer {
        long joinTime;
        long expectedNonce;

        PendingPlayer(long joinTime, long expectedNonce) {
            this.joinTime = joinTime;
            this.expectedNonce = expectedNonce;
        }
    }

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            WoMCommand.register(dispatcher);
        });

        ClientModListPayload.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            long nonce = System.currentTimeMillis() ^ (long)(Math.random() * Long.MAX_VALUE);
            pendingPlayers.put(handler.player.getUUID(), new PendingPlayer(System.currentTimeMillis(), nonce));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            pendingPlayers.remove(handler.player.getUUID());
        });

        ServerPlayNetworking.registerGlobalReceiver(
                ClientModListPayload.ID,
                (payload, context) -> {
                    UUID playerId = context.player().getUUID();
                    PendingPlayer pending = pendingPlayers.remove(playerId);

                    context.server().execute(() -> {
                        if (pending == null) {
                            handleViolation(context.player(), payload.mods(), "§cПовторная отправка списка модов (Anti-Cheat)");
                            return;
                        }

                        long timeDiff = System.currentTimeMillis() - pending.joinTime;
                        if (timeDiff > WoM.CONFIG.timeout_ms) {
                            handleViolation(context.player(), payload.mods(), "§cПревышено время ожидания (Anti-Cheat)");
                            return;
                        }

                        checkPlayerMods(context.player(), payload);
                    });
                }
        );

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long currentTime = System.currentTimeMillis();
            List<UUID> toRemove = new ArrayList<>();

            for (Map.Entry<UUID, PendingPlayer> entry : pendingPlayers.entrySet()) {
                if (currentTime - entry.getValue().joinTime > WoM.CONFIG.timeout_ms) {
                    ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                    if (player != null) {
                        handleViolation(player, List.of(), "§cМод WoM не ответил вовремя");
                    }
                    toRemove.add(entry.getKey());
                }
            }
            toRemove.forEach(pendingPlayers::remove);
        });
    }

    private void checkPlayerMods(ServerPlayer player, ClientModListPayload payload) {
        List<ClientModListPayload.ModInfo> clientMods = payload.mods();
        String playerIP = player.getIpAddress();

        // Anti-spoofing 1: Honeypot check
        for (String honeypot : HONEYPOT_MODS) {
            if (clientMods.stream().anyMatch(mod -> mod.id().equals(honeypot))) {
                handleViolation(player, clientMods, "§cОбнаружен несуществующий мод: " + honeypot + " (Anti-Cheat)");
                return;
            }
        }

        // Anti-spoofing 2: Version check
        String serverVersion = FabricLoader.getInstance().getModContainer("wom")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");

        if (!serverVersion.equals(payload.womVersion())) {
            handleViolation(player, clientMods, "§cНесоответствие версии WoM: сервер=" + serverVersion + ", клиент=" + payload.womVersion());
            return;
        }

        // Anti-spoofing 3: Minimum mod count
        int minExpectedMods = 40;
        if (clientMods.size() < minExpectedMods) {
            handleViolation(player, clientMods, "§cПодозрительно малое количество модов: " + clientMods.size() + " (Anti-Cheat)");
            return;
        }

        // Anti-spoofing 4: Must contain WoM
        if (clientMods.stream().noneMatch(mod -> mod.id().equals("wom"))) {
            handleViolation(player, clientMods, "§cМод WoM не обнаружен в списке");
            return;
        }

        boolean hasFabricApi = clientMods.stream().anyMatch(mod -> mod.id().equals("fabric-api"));
        boolean whitelistHasFabricApi = WoM.CONFIG.whitelist.stream().anyMatch(rule -> rule.id.equals("fabric-api"));

        List<String> violatedMods = new ArrayList<>();

        if (WoM.CONFIG.check_type.equals("WHITELIST") || WoM.CONFIG.check_type.equals("BOTH")) {
            for (ClientModListPayload.ModInfo mod : clientMods) {
                if (hasFabricApi && whitelistHasFabricApi && mod.id().startsWith("fabric-")) {
                    continue;
                }

                if (hasFabricApi && whitelistHasFabricApi && FABRIC_RELATED_MODS.contains(mod.id())) {
                    continue;
                }

                WoMConfig.ModRule rule = findRule(WoM.CONFIG.whitelist, mod.id());

                if (rule == null) {
                    violatedMods.add(formatModName(mod) + " §7(§fне в белом списке§7)");
                } else if (WoM.CONFIG.strict_mode && !validateMod(mod, rule)) {
                    violatedMods.add(formatModName(mod) + " §7(§fнесоответствие метаданных§7)");
                }
            }
        }

        if (WoM.CONFIG.check_type.equals("BLACKLIST") || WoM.CONFIG.check_type.equals("BOTH")) {
            for (ClientModListPayload.ModInfo mod : clientMods) {
                WoMConfig.ModRule rule = findRule(WoM.CONFIG.blacklist, mod.id());

                if (rule != null) {
                    if (!WoM.CONFIG.strict_mode || validateMod(mod, rule)) {
                        violatedMods.add(formatModName(mod) + " §7(§fв чёрном списке§7)");
                    }
                }
            }
        }

        if (!violatedMods.isEmpty()) {
            String reason = String.join("§r, ", violatedMods);
            handleViolation(player, clientMods, reason);
        } else {
            // Player passed the check - log their mods
            PlayerLogger.logPlayerMods(
                    player.getUUID(),
                    player.getName().getString(),
                    playerIP,
                    clientMods,
                    true,
                    null
            );
        }
    }

    private String formatModName(ClientModListPayload.ModInfo mod) {
        return "§e" + mod.name() + " §7(§f" + mod.id() + "§7)";
    }

    private WoMConfig.ModRule findRule(List<WoMConfig.ModRule> rules, String modId) {
        return rules.stream()
                .filter(rule -> rule.id.equals(modId))
                .findFirst()
                .orElse(null);
    }

    private boolean validateMod(ClientModListPayload.ModInfo mod, WoMConfig.ModRule rule) {
        if (rule.version != null && !rule.version.isEmpty()) {
            if (!matchVersion(mod.version(), rule.version)) {
                return false;
            }
        }

        if (rule.author != null && !rule.author.isEmpty()) {
            if (!rule.author.equalsIgnoreCase(mod.author())) {
                return false;
            }
        }

        if (rule.min_size != null && mod.size() < rule.min_size) {
            return false;
        }
        if (rule.max_size != null && mod.size() > rule.max_size) {
            return false;
        }

        return true;
    }

    private boolean matchVersion(String actual, String pattern) {
        if (pattern.equals("*")) return true;
        if (pattern.endsWith("*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return actual.startsWith(prefix);
        }
        return actual.equals(pattern);
    }

    private void handleViolation(ServerPlayer player, List<ClientModListPayload.ModInfo> clientMods, String logReason) {
        PlayerLogger.logPlayerMods(
                player.getUUID(),
                player.getName().getString(),
                player.getIpAddress(),
                clientMods,
                false,
                logReason
        );

        if (WoM.CONFIG.action.equals("KICK")) {
            Component kickMessage = Component.literal(
                    "§c§l⚠ ДОСТУП ЗАПРЕЩЁН ⚠\n\n" +
                            "§7Мод §cWoM §7заблокировал ваше подключение.\n" +
                            "§7Причина: §f" + logReason + "\n\n" +
                            "§7Если вы считаете, что это ошибка, обратитесь к администрации."
            );

            player.connection.disconnect(kickMessage);
            WoM.LOGGER.warn("[WoM] Player {} was kicked. Reason: {}", player.getName().getString(), logReason);

        } else if (WoM.CONFIG.action.equals("WARN")) {
            player.sendSystemMessage(Component.literal("§e§l[⚠ WoM] §fВнимание: §c" + logReason));

            player.level().getServer().getPlayerList().getPlayers().forEach(admin -> {
                if (admin.level().getServer().getPlayerList().isOp(admin.nameAndId())) {
                    admin.sendSystemMessage(Component.literal("§c§l[WoM Alert] §fИгрок §e" + player.getName().getString() + " §f: §c" + logReason));
                }
            });
            WoM.LOGGER.warn("[WoM] Warning for {}: {}", player.getName().getString(), logReason);
        }
    }
}