package ru.hantu.config;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import ru.hantu.WoM;
import ru.hantu.i18n.Translations;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WoMConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("wom.json");

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(MultiLineString.class, new MultiLineStringAdapter())
            .create();

    public static class MultiLineString {
        public String value;
        public MultiLineString(String value) { this.value = value; }
    }

    public static class MultiLineStringAdapter extends TypeAdapter<MultiLineString> {
        @Override
        public void write(JsonWriter out, MultiLineString value) throws IOException {
            if (value == null || value.value == null) {
                out.nullValue();
                return;
            }
            out.beginArray();
            for (String line : value.value.split("\\r?\\n", -1)) {
                out.value(line);
            }
            out.endArray();
        }

        @Override
        public MultiLineString read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.STRING) {
                return new MultiLineString(in.nextString().replace("\\n", "\n"));
            } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                in.beginArray();
                StringBuilder sb = new StringBuilder();
                while (in.hasNext()) {
                    sb.append(in.nextString()).append("\n");
                }
                in.endArray();
                return new MultiLineString(sb.toString());
            }
            return new MultiLineString("");
        }
    }

    @SerializedName("_documentation")
    public MultiLineString documentation = new MultiLineString("""
        ============================================
        WoM (Whitelist of Mods) Configuration Guide
        ============================================
        
        INSTALLATION:
          - Server: Place wom.jar in server's mods/ folder
          - Client: All players must also install wom.jar
        
        LANGUAGE:
          - Available: en_us, ru_ru, de_de, es_es, fr_fr, pt_br,
            ja_jp, zh_cn, ko_kr, tr_tr, it_it, pl_pl
          - You can override any message in the "messages" section
        
        CHECK_TYPE:
          - "WHITELIST": Only mods in the whitelist are allowed
          - "BLACKLIST": All mods allowed except those in the blacklist
          - "BOTH": Check both lists (whitelist first, then blacklist)
        
        ACTION:
          - "KICK": Disconnect the player immediately
          - "WARN": Send warning to player and online admins
        
        TIMEOUT_MS:
          - Time in milliseconds to wait for client response
          - Default: 5000 (5 seconds)
        
        STRICT_MODE:
          - true: Verify mod metadata (version, author, size)
          - false: Only check mod ID (faster but less secure)
        
        AUTO-ALLOWED MODS:
          - If "fabric-api" is in whitelist and client has it,
            all "fabric-*" mods are automatically allowed
          - "mixinextras" and "placeholder-api" are always allowed
            if "fabric-api" is present
        
        LOGGING:
          - log_player_mods: Enable/disable player mod logging
          - log_cooldown_ms: Minimum time between logs for same player
            (prevents spam from reconnections). Default: 3600000 (1 hour)
          - log_path: Relative path from config folder for player logs
        
        MESSAGES:
          - Override any built-in message by adding it to this section
          - Use %s for placeholders (e.g., player name, mod name)
          - Leave empty or delete to use built-in translation
        
        WHITELIST/BLACKLIST RULES:
          - "id": Mod ID (required)
          - "version": Version pattern (optional, e.g., "1.0.*" or "1.0.0")
          - "author": Exact author name (optional)
          - "min_size": Minimum JAR size in bytes (optional)
          - "max_size": Maximum JAR size in bytes (optional)
        
        EXAMPLES:
          Simple rule: {"id": "sodium"}
          With version: {"id": "sodium", "version": "0.6.*"}
          Full check: {"id": "sodium", "version": "0.6.*", "author": "CaffeineMC", "min_size": 500000}
        
        Use /wom commands in-game to modify this config safely.
        ============================================
        """);

    @SerializedName("language")
    public String language = "en_us";

    @SerializedName("check_type")
    public String check_type = "WHITELIST";

    @SerializedName("action")
    public String action = "KICK";

    @SerializedName("timeout_ms")
    public int timeout_ms = 5000;

    @SerializedName("strict_mode")
    public boolean strict_mode = true;

    @SerializedName("log_player_mods")
    public boolean log_player_mods = true;

    @SerializedName("log_cooldown_ms")
    public long log_cooldown_ms = 3600000L;

    @SerializedName("log_path")
    public String log_path = "players";

    @SerializedName("messages")
    public Map<String, String> messages = new HashMap<>();

    @SerializedName("whitelist")
    public List<ModRule> whitelist = new ArrayList<>();

    @SerializedName("blacklist")
    public List<ModRule> blacklist = new ArrayList<>();

    public static class ModRule {
        @SerializedName("id") public String id;
        @SerializedName("version") public String version;
        @SerializedName("author") public String author;
        @SerializedName("min_size") public Long min_size;
        @SerializedName("max_size") public Long max_size;

        public ModRule(String id) { this.id = id; }
    }

    /**
     * Gets a translated message with format arguments.
     */
    public String formatMessage(String key, Object... args) {
        return String.format(getMessage(key), args);
    }

    /**
     * Gets a translated message, checking custom overrides first.
     */
    public String getMessage(String key) {
        String custom = messages.get(key);
        if (custom != null && !custom.isEmpty()) {
            return custom;
        }
        return Translations.get(language, key);
    }

    public static WoMConfig load() {
        if (!CONFIG_PATH.toFile().exists()) {
            WoMConfig defaultConfig = new WoMConfig();

            defaultConfig.whitelist.add(new ModRule("fabric-api"));
            defaultConfig.whitelist.add(new ModRule("wom"));

            defaultConfig.save();
            return defaultConfig;
        }
        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            WoMConfig config = GSON.fromJson(reader, WoMConfig.class);

            // Убеждаемся, что поле language не null
            if (config.language == null) {
                config.language = "en_us";
            }


            return config;
        } catch (IOException e) {
            WoM.LOGGER.error("Failed to load WoM config!", e);
            return new WoMConfig();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
            WoM.LOGGER.info("WoM config successfully saved!");
        } catch (IOException e) {
            WoM.LOGGER.error("Failed to save WoM config!", e);
        }
    }
}