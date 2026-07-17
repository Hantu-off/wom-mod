package ru.hantu;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import ru.hantu.commands.MWLCommand;
import ru.hantu.network.ClientModListPayload;

import java.util.*;

public class MWLServer implements DedicatedServerModInitializer {

    private static final Map<UUID, PendingPlayer> pendingPlayers = new HashMap<>();

    private static final Set<String> HONEYPOT_MODS = Set.of(
            "fake-mod-123", "definitely-not-a-cheat", "totally-legit-mod"
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
            MWLCommand.register(dispatcher);
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
                            handleViolation(context.player(), "§cПовторная отправка списка модов (Anti-Cheat)");
                            return;
                        }

                        long timeDiff = System.currentTimeMillis() - pending.joinTime;
                        if (timeDiff > MWL.CONFIG.timeout_ms) {
                            handleViolation(context.player(), "§cПревышено время ожидания (Anti-Cheat)");
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
                if (currentTime - entry.getValue().joinTime > MWL.CONFIG.timeout_ms) {
                    ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                    if (player != null) {
                        handleViolation(player, "§cМод MWL не ответил вовремя");
                    }
                    toRemove.add(entry.getKey());
                }
            }
            toRemove.forEach(pendingPlayers::remove);
        });
    }

    private void checkPlayerMods(ServerPlayer player, ClientModListPayload payload) {
        Map<String, String> clientMods = payload.modIdToName();

        // Anti-spoofing 1: Honeypot проверка по ID
        for (String honeypot : HONEYPOT_MODS) {
            if (clientMods.containsKey(honeypot)) {
                handleViolation(player, "§cОбнаружен несуществующий мод: " + honeypot + " (Anti-Cheat)");
                return;
            }
        }

        // Anti-spoofing 2: Проверка версии
        String serverVersion = FabricLoader.getInstance().getModContainer("mwl")
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");

        if (!serverVersion.equals(payload.mwlVersion())) {
            handleViolation(player, "§cНесоответствие версии MWL: сервер=" + serverVersion + ", клиент=" + payload.mwlVersion());
            return;
        }

        // Anti-spoofing 3: Минимальное количество модов
        int minExpectedMods = 40;
        if (clientMods.size() < minExpectedMods) {
            handleViolation(player, "§cПодозрительно малое количество модов: " + clientMods.size() + " (Anti-Cheat)");
            return;
        }

        // Anti-spoofing 4: Наличие самого MWL
        if (!clientMods.containsKey("mwl")) {
            handleViolation(player, "§cМод MWL не обнаружен в списке");
            return;
        }

        // Стандартная проверка
        List<String> violatedIds = new ArrayList<>();

        if (MWL.CONFIG.check_type.equals("WHITELIST") || MWL.CONFIG.check_type.equals("BOTH")) {
            for (String modId : clientMods.keySet()) {
                if (!MWL.CONFIG.whitelist.contains(modId)) {
                    violatedIds.add(modId);
                }
            }
        }

        if (MWL.CONFIG.check_type.equals("BLACKLIST") || MWL.CONFIG.check_type.equals("BOTH")) {
            for (String modId : clientMods.keySet()) {
                if (MWL.CONFIG.blacklist.contains(modId) && !violatedIds.contains(modId)) {
                    violatedIds.add(modId);
                }
            }
        }

        if (!violatedIds.isEmpty()) {
            List<String> formattedNames = new ArrayList<>();
            for (String modId : violatedIds) {
                // Берем красивое название из Map, если его нет (теоретически невозможно), используем ID
                String name = clientMods.getOrDefault(modId, modId);
                formattedNames.add("§e" + name + " §7(§f" + modId + "§7)");
            }

            String reason = String.join("§r, ", formattedNames);
            handleViolation(player, reason);
        }
    }

    private void handleViolation(ServerPlayer player, String reason) {
        if (MWL.CONFIG.action.equals("KICK")) {
            Component kickMessage = Component.literal(
                    "§c§l⚠ ДОСТУП ЗАПРЕЩЁН ⚠\n\n" +
                            "§7Мод §cModWhiteList §7заблокировал ваше подключение.\n" +
                            "§7Причина: §f" + reason + "\n\n" +
                            "§7Если вы считаете, что это ошибка, обратитесь к администрации."
            );

            player.connection.disconnect(kickMessage);
            MWL.LOGGER.warn("[MWL] Player {} was kicked. Reason: {}", player.getName().getString(), reason);

        } else if (MWL.CONFIG.action.equals("WARN")) {
            player.sendSystemMessage(Component.literal("§e§l[⚠ MWL] §fВнимание: §c" + reason));

            player.level().getServer().getPlayerList().getPlayers().forEach(admin -> {
                if (admin.level().getServer().getPlayerList().isOp(admin.nameAndId())) {
                    admin.sendSystemMessage(Component.literal("§c§l[MWL Alert] §fИгрок §e" + player.getName().getString() + " §f: §c" + reason));
                }
            });
            MWL.LOGGER.warn("[MWL] Warning for {}: {}", player.getName().getString(), reason);
        }
    }
}