package ru.hantu.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;
import ru.hantu.MWL;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MwlConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mwl.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Bilingual info field acting as a comment in the JSON file
    @SerializedName("_info")
    public String info = "RU: Этот файл генерируется автоматически. Используйте команды /mwl в игре для безопасного изменения. \nEN: This file is auto-generated. Use /mwl commands in-game to modify it safely.";

    @SerializedName("check_type")
    public String check_type = "WHITELIST";

    @SerializedName("action")
    public String action = "KICK";

    @SerializedName("timeout_ms")
    public int timeout_ms = 5000;

    @SerializedName("whitelist")
    public List<String> whitelist = new ArrayList<>(Arrays.asList(
            "fabric-api", "fabric-api-base", "fabric-api-lookup-api-v1", "fabric-biome-api-v1",
            "fabric-block-api-v1", "fabric-block-getter-api-v2", "fabric-command-api-v2",
            "fabric-content-registries-v0", "fabric-convention-tags-v2", "fabric-crash-report-info-v1",
            "fabric-creative-tab-api-v1", "fabric-data-attachment-api-v1", "fabric-data-generation-api-v1",
            "fabric-debug-api-v1", "fabric-dimensions-v1", "fabric-entity-events-v1",
            "fabric-events-interaction-v0", "fabric-game-rule-api-v1", "fabric-item-api-v1",
            "fabric-key-mapping-api-v1", "fabric-lifecycle-events-v1", "fabric-loot-api-v3",
            "fabric-menu-api-v1", "fabric-message-api-v1", "fabric-model-loading-api-v1",
            "fabric-networking-api-v1", "fabric-object-builder-api-v1", "fabric-particles-v1",
            "fabric-permission-api-v1", "fabric-recipe-api-v1", "fabric-registry-sync-v0",
            "fabric-renderer-api-v1", "fabric-renderer-indigo", "fabric-rendering-fluids-v1",
            "fabric-rendering-v1", "fabric-resource-conditions-api-v1", "fabric-resource-loader-v0",
            "fabric-resource-loader-v1", "fabric-screen-api-v1", "fabric-serialization-api-v1",
            "fabric-sound-api-v1", "fabric-tag-api-v1", "fabric-transfer-api-v1",
            "fabric-transitive-access-wideners-v1", "mixinextras", "mwl", "modmenu", "placeholder-api"
    ));

    @SerializedName("blacklist")
    public List<String> blacklist = new ArrayList<>(Arrays.asList(
            "meteor-client", "wurst", "aristois", "xray", "killaura"
    ));

    public static MwlConfig load() {
        if (!CONFIG_PATH.toFile().exists()) {
            MwlConfig defaultConfig = new MwlConfig();
            defaultConfig.save();
            return defaultConfig;
        }
        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            return GSON.fromJson(reader, MwlConfig.class);
        } catch (IOException e) {
            MWL.LOGGER.error("Failed to load MWL config, using defaults!", e);
            return new MwlConfig();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
            MWL.LOGGER.info("MWL config successfully saved!");
        } catch (IOException e) {
            MWL.LOGGER.error("Failed to save MWL config!", e);
        }
    }
}