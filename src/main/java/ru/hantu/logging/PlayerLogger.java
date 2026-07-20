package ru.hantu.logging;

import net.fabricmc.loader.api.FabricLoader;
import ru.hantu.WoM;
import ru.hantu.network.ClientModListPayload;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerLogger {
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("HH-mm-ss_dd-MM-yy");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Map<UUID, Long> lastLogTime = new HashMap<>();

    public static void logPlayerMods(UUID playerId, String playerName, String playerIP,
                                     List<ClientModListPayload.ModInfo> mods, boolean passed, String violationReason) {
        if (!WoM.CONFIG.log_player_mods) return;

        long currentTime = System.currentTimeMillis();
        Long lastTime = lastLogTime.get(playerId);
        if (lastTime != null && (currentTime - lastTime) < WoM.CONFIG.log_cooldown_ms) {
            WoM.LOGGER.debug("[WoM] Skipping log for {} (cooldown active)", playerName);
            return;
        }
        lastLogTime.put(playerId, currentTime);

        CompletableFuture.runAsync(() -> {
            try { writeLog(playerId, playerName, playerIP, mods, passed, violationReason); }
            catch (IOException e) { WoM.LOGGER.error("[WoM] Failed to write log for player {}", playerName, e); }
        });
    }

    private static void writeLog(UUID playerId, String playerName, String playerIP,
                                 List<ClientModListPayload.ModInfo> mods, boolean passed, String violationReason) throws IOException {
        String safePlayerName = playerName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("wom");
        Path playerDir = configDir.resolve(WoM.CONFIG.log_path).resolve(safePlayerName);
        Files.createDirectories(playerDir);

        LocalDateTime now = LocalDateTime.now();
        Path logFile = playerDir.resolve(now.format(FILE_DATE_FORMAT) + ".txt");

        List<ClientModListPayload.ModInfo> displayMods = new ArrayList<>();
        for (ClientModListPayload.ModInfo mod : mods) {
            if (mod.id().startsWith("fabric-") && !mod.id().equals("fabric-api")) continue;
            displayMods.add(mod);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(logFile)) {
            writer.write("========================================\nWoM Player Log\n========================================\n\n");
            writer.write("Player: " + playerName + "\nUUID: " + playerId + "\nIP: " + playerIP + "\n");
            writer.write("Time: " + now.format(DISPLAY_DATE_FORMAT) + "\nWoM Version: " + getModVersion("wom") + "\n");
            writer.write("Minecraft Version: " + getModVersion("minecraft") + "\nStatus: " + (passed ? "§aPASSED" : "§cREJECTED") + "\n");
            if (!passed && violationReason != null) writer.write("Violation: " + violationReason + "\n");
            writer.write("\n----------------------------------------\nInstalled Mods (" + displayMods.size() + "):\n----------------------------------------\n\n");

            for (ClientModListPayload.ModInfo mod : displayMods) {
                writer.write("  • " + mod.name() + "\n    ID: " + mod.id() + "\n    Version: " + mod.version() + "\n");
                writer.write("    Author: " + mod.author() + "\n    Size: " + formatFileSize(mod.size()) + "\n\n");
            }
            writer.write("========================================\nEnd of log\n========================================\n");
        }
        WoM.LOGGER.info("[WoM] Logged mods for player {} to {}", playerName, logFile);
    }

    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    private static String getModVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("unknown");
    }
}