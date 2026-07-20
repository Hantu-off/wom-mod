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

/**
 * Handles logging of player mod lists to files.
 * Logs are organized by player name in a configurable directory.
 */
public class PlayerLogger {

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("HH-mm-ss_dd-MM-yy");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Tracks last log time per player to prevent spam
    private static final Map<UUID, Long> lastLogTime = new HashMap<>();

    /**
     * Logs player's mod list to a file if cooldown has passed.
     * This method is non-blocking and runs in a separate thread.
     */
    public static void logPlayerMods(UUID playerId, String playerName, String playerIP,
                                     List<ClientModListPayload.ModInfo> mods,
                                     boolean passed, String violationReason) {
        if (!WoM.CONFIG.log_player_mods) {
            return;
        }

        // Check cooldown
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastLogTime.get(playerId);
        if (lastTime != null && (currentTime - lastTime) < WoM.CONFIG.log_cooldown_ms) {
            WoM.LOGGER.debug("[WoM] Skipping log for {} (cooldown active)", playerName);
            return;
        }

        lastLogTime.put(playerId, currentTime);

        // Run file I/O in separate thread to avoid blocking main server thread
        CompletableFuture.runAsync(() -> {
            try {
                writeLog(playerId, playerName, playerIP, mods, passed, violationReason);
            } catch (IOException e) {
                WoM.LOGGER.error("[WoM] Failed to write log for player {}", playerName, e);
            }
        });
    }

    private static void writeLog(UUID playerId, String playerName, String playerIP,
                                 List<ClientModListPayload.ModInfo> mods,
                                 boolean passed, String violationReason) throws IOException {
        // Sanitize player name for filesystem
        String safePlayerName = sanitizeFilename(playerName);

        // Create player directory
        Path configDir = FabricLoader.getInstance().getConfigDir().resolve("wom");
        Path playersDir = configDir.resolve(WoM.CONFIG.log_path);
        Path playerDir = playersDir.resolve(safePlayerName);

        Files.createDirectories(playerDir);

        // Generate filename
        LocalDateTime now = LocalDateTime.now();
        String filename = now.format(FILE_DATE_FORMAT) + ".txt";
        Path logFile = playerDir.resolve(filename);

        // Filter out Fabric API submodules to keep the log clean and readable
        List<ClientModListPayload.ModInfo> displayMods = new ArrayList<>();
        for (ClientModListPayload.ModInfo mod : mods) {
            // Keep 'fabric-api', but hide its submodules (e.g., fabric-api-base, fabric-biome-api-v1)
            if (mod.id().startsWith("fabric-") && !mod.id().equals("fabric-api")) {
                continue;
            }
            displayMods.add(mod);
        }

        // Write log content
        try (BufferedWriter writer = Files.newBufferedWriter(logFile)) {
            writer.write("========================================\n");
            writer.write("WoM Player Log\n");
            writer.write("========================================\n\n");

            writer.write("Player: " + playerName + "\n");
            writer.write("UUID: " + playerId.toString() + "\n");
            writer.write("IP: " + playerIP + "\n");
            writer.write("Time: " + now.format(DISPLAY_DATE_FORMAT) + "\n");
            writer.write("WoM Version: " + getModVersion("wom") + "\n");
            writer.write("Minecraft Version: " + getModVersion("minecraft") + "\n");
            writer.write("Status: " + (passed ? "§aPASSED" : "§cREJECTED") + "\n");

            if (!passed && violationReason != null) {
                writer.write("Violation: " + violationReason + "\n");
            }

            writer.write("\n----------------------------------------\n");
            writer.write("Installed Mods (" + displayMods.size() + "):\n");
            writer.write("----------------------------------------\n\n");

            for (ClientModListPayload.ModInfo mod : displayMods) {
                writer.write("  • " + mod.name() + "\n");
                writer.write("    ID: " + mod.id() + "\n");
                writer.write("    Version: " + mod.version() + "\n");
                writer.write("    Author: " + mod.author() + "\n");
                writer.write("    Size: " + formatFileSize(mod.size()) + "\n");
                writer.write("\n");
            }

            writer.write("========================================\n");
            writer.write("End of log\n");
            writer.write("========================================\n");
        }

        WoM.LOGGER.info("[WoM] Logged mods for player {} to {}", playerName, logFile);
    }

    /**
     * Sanitizes a string to be safe for use as a filename.
     */
    private static String sanitizeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    /**
     * Formats file size in bytes to human-readable format.
     */
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }

    /**
     * Gets the version of a mod by its ID.
     */
    private static String getModVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }
}