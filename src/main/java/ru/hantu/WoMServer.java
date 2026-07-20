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
import ru.hantu.i18n.Translations;
import ru.hantu.logging.PlayerLogger;
import ru.hantu.network.ClientModListPayload;

import java.util.*;

public class WoMServer implements DedicatedServerModInitializer {
    private static final Map<UUID, PendingPlayer> pendingPlayers = new HashMap<>();
    private static final Set<String> HONEYPOT_MODS = Set.of("fake-mod-123", "definitely-not-a-cheat", "totally-legit-mod");
    private static final Set<String> FABRIC_RELATED_MODS = Set.of("mixinextras", "placeholder-api");

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
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> WoMCommand.register(dispatcher));
        ClientModListPayload.register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            long nonce = System.currentTimeMillis() ^ (long)(Math.random() * Long.MAX_VALUE);
            pendingPlayers.put(handler.player.getUUID(), new PendingPlayer(System.currentTimeMillis(), nonce));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> pendingPlayers.remove(handler.player.getUUID()));

        ServerPlayNetworking.registerGlobalReceiver(ClientModListPayload.ID, (payload, context) -> {
            UUID playerId = context.player().getUUID();
            PendingPlayer pending = pendingPlayers.remove(playerId);

            context.server().execute(() -> {
                if (pending == null) {
                    handleViolation(context.player(), payload.mods(), WoM.CONFIG.getMessage(Translations.VIOLATION_DUPLICATE));
                    return;
                }
                long timeDiff = System.currentTimeMillis() - pending.joinTime;
                if (timeDiff > WoM.CONFIG.timeout_ms) {
                    handleViolation(context.player(), payload.mods(), WoM.CONFIG.getMessage(Translations.VIOLATION_TIMEOUT));
                    return;
                }
                checkPlayerMods(context.player(), payload);
            });
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long currentTime = System.currentTimeMillis();
            List<UUID> toRemove = new ArrayList<>();
            for (Map.Entry<UUID, PendingPlayer> entry : pendingPlayers.entrySet()) {
                if (currentTime - entry.getValue().joinTime > WoM.CONFIG.timeout_ms) {
                    ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                    if (player != null) handleViolation(player, List.of(), WoM.CONFIG.getMessage(Translations.VIOLATION_TIMEOUT));
                    toRemove.add(entry.getKey());
                }
            }
            toRemove.forEach(pendingPlayers::remove);
        });
    }

    private void checkPlayerMods(ServerPlayer player, ClientModListPayload payload) {
        List<ClientModListPayload.ModInfo> clientMods = payload.mods();

        for (String honeypot : HONEYPOT_MODS) {
            if (clientMods.stream().anyMatch(mod -> mod.id().equals(honeypot))) {
                handleViolation(player, clientMods, WoM.CONFIG.formatMessage(Translations.VIOLATION_HONEYPOT, honeypot));
                return;
            }
        }

        String serverVersion = FabricLoader.getInstance().getModContainer("wom")
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
        if (!serverVersion.equals(payload.womVersion())) {
            handleViolation(player, clientMods, WoM.CONFIG.formatMessage(Translations.VIOLATION_VERSION_MISMATCH, serverVersion, payload.womVersion()));
            return;
        }

        if (clientMods.size() < 40) {
            handleViolation(player, clientMods, WoM.CONFIG.formatMessage(Translations.VIOLATION_TOO_FEW_MODS, clientMods.size()));
            return;
        }

        if (clientMods.stream().noneMatch(mod -> mod.id().equals("wom"))) {
            handleViolation(player, clientMods, WoM.CONFIG.getMessage(Translations.VIOLATION_WOM_MISSING));
            return;
        }

        boolean hasFabricApi = clientMods.stream().anyMatch(mod -> mod.id().equals("fabric-api"));
        boolean whitelistHasFabricApi = WoM.CONFIG.whitelist.stream().anyMatch(rule -> rule.id.equals("fabric-api"));
        List<String> violatedMods = new ArrayList<>();

        if (WoM.CONFIG.check_type.equals("WHITELIST") || WoM.CONFIG.check_type.equals("BOTH")) {
            for (ClientModListPayload.ModInfo mod : clientMods) {
                if (hasFabricApi && whitelistHasFabricApi && (mod.id().startsWith("fabric-") || FABRIC_RELATED_MODS.contains(mod.id()))) continue;

                WoMConfig.ModRule rule = findRule(WoM.CONFIG.whitelist, mod.id());
                if (rule == null) {
                    violatedMods.add(formatModName(mod) + " §7(§f" + WoM.CONFIG.getMessage(Translations.VIOLATION_NOT_IN_WHITELIST) + "§7)");
                } else if (WoM.CONFIG.strict_mode && !validateMod(mod, rule)) {
                    violatedMods.add(formatModName(mod) + " §7(§f" + WoM.CONFIG.getMessage(Translations.VIOLATION_METADATA_MISMATCH) + "§7)");
                }
            }
        }

        if (WoM.CONFIG.check_type.equals("BLACKLIST") || WoM.CONFIG.check_type.equals("BOTH")) {
            for (ClientModListPayload.ModInfo mod : clientMods) {
                WoMConfig.ModRule rule = findRule(WoM.CONFIG.blacklist, mod.id());
                if (rule != null && (!WoM.CONFIG.strict_mode || validateMod(mod, rule))) {
                    violatedMods.add(formatModName(mod) + " §7(§f" + WoM.CONFIG.getMessage(Translations.VIOLATION_IN_BLACKLIST) + "§7)");
                }
            }
        }

        if (!violatedMods.isEmpty()) {
            handleViolation(player, clientMods, String.join("§r, ", violatedMods));
        } else {
            PlayerLogger.logPlayerMods(player.getUUID(), player.getName().getString(), player.getIpAddress(), clientMods, true, null);
        }
    }

    private String formatModName(ClientModListPayload.ModInfo mod) { return "§e" + mod.name() + " §7(§f" + mod.id() + "§7)"; }
    private WoMConfig.ModRule findRule(List<WoMConfig.ModRule> rules, String modId) { return rules.stream().filter(rule -> rule.id.equals(modId)).findFirst().orElse(null); }

    private boolean validateMod(ClientModListPayload.ModInfo mod, WoMConfig.ModRule rule) {
        if (rule.version != null && !rule.version.isEmpty() && !matchVersion(mod.version(), rule.version)) return false;
        if (rule.author != null && !rule.author.isEmpty() && !rule.author.equalsIgnoreCase(mod.author())) return false;
        if (rule.min_size != null && mod.size() < rule.min_size) return false;
        if (rule.max_size != null && mod.size() > rule.max_size) return false;
        return true;
    }

    private boolean matchVersion(String actual, String pattern) {
        if (pattern.equals("*")) return true;
        if (pattern.endsWith("*")) return actual.startsWith(pattern.substring(0, pattern.length() - 1));
        return actual.equals(pattern);
    }

    private void handleViolation(ServerPlayer player, List<ClientModListPayload.ModInfo> clientMods, String logReason) {
        PlayerLogger.logPlayerMods(player.getUUID(), player.getName().getString(), player.getIpAddress(), clientMods, false, logReason);

        if (WoM.CONFIG.action.equals("KICK")) {
            Component kickMessage = Component.literal(
                    WoM.CONFIG.getMessage(Translations.KICK_TITLE) + "\n\n" +
                            WoM.CONFIG.getMessage(Translations.KICK_LINE1) + "\n" +
                            WoM.CONFIG.formatMessage(Translations.KICK_LINE2, logReason) + "\n\n" +
                            WoM.CONFIG.getMessage(Translations.KICK_LINE3)
            );
            player.connection.disconnect(kickMessage);
            WoM.LOGGER.warn("[WoM] Player {} was kicked. Reason: {}", player.getName().getString(), logReason);
        } else if (WoM.CONFIG.action.equals("WARN")) {
            player.sendSystemMessage(Component.literal(WoM.CONFIG.formatMessage(Translations.WARN_PREFIX, logReason)));
            player.level().getServer().getPlayerList().getPlayers().forEach(admin -> {
                if (admin.level().getServer().getPlayerList().isOp(admin.nameAndId())) {
                    admin.sendSystemMessage(Component.literal(WoM.CONFIG.formatMessage(Translations.ALERT_PREFIX, player.getName().getString(), logReason)));
                }
            });
            WoM.LOGGER.warn("[WoM] Warning for {}: {}", player.getName().getString(), logReason);
        }
    }
}