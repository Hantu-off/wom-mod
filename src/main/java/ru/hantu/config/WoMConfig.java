package ru.hantu.config;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import ru.hantu.WoM;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WoMConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("wom.json");

    // Регистрация адаптера для красивого много-строчного вывода документации
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(MultiLineString.class, new MultiLineStringAdapter())
            .create();

    // Обёртка для много-строчного текста
    public static class MultiLineString {
        public String value;
        public MultiLineString(String value) { this.value = value; }
    }

    // Адаптер, который превращает текст в красивый массив строк в JSON
    public static class MultiLineStringAdapter extends TypeAdapter<MultiLineString> {
        @Override
        public void write(JsonWriter out, MultiLineString value) throws IOException {
            if (value == null || value.value == null) {
                out.nullValue();
                return;
            }
            out.beginArray();
            // Разбиваем текст по строкам и записываем каждую как отдельный элемент массива
            for (String line : value.value.split("\\r?\\n", -1)) {
                out.value(line);
            }
            out.endArray();
        }

        @Override
        public MultiLineString read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.STRING) {
                // Обратная совместимость: читаем старый формат с \n
                return new MultiLineString(in.nextString().replace("\\n", "\n"));
            } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                // Читаем новый красивый формат массива
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
        
        Use /mwl commands in-game to modify this config safely.
        ============================================
        """);

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

    @SerializedName("whitelist")
    public List<ModRule> whitelist = new ArrayList<>();

    @SerializedName("blacklist")
    public List<ModRule> blacklist = new ArrayList<>();

    public static class ModRule {
        @SerializedName("id")
        public String id;

        @SerializedName("version")
        public String version;

        @SerializedName("author")
        public String author;

        @SerializedName("min_size")
        public Long min_size;

        @SerializedName("max_size")
        public Long max_size;

        public ModRule(String id) {
            this.id = id;
        }
    }

    public static WoMConfig load() {
        if (!CONFIG_PATH.toFile().exists()) {
            WoMConfig defaultConfig = new WoMConfig();

            defaultConfig.whitelist.add(new ModRule("fabric-api"));
            defaultConfig.whitelist.add(new ModRule("wom"));

            ModRule sodium = new ModRule("sodium");
            sodium.version = "0.6.*";
            sodium.author = "CaffeineMC";
            sodium.min_size = 500000L;
            defaultConfig.whitelist.add(sodium);

            defaultConfig.save();
            return defaultConfig;
        }
        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            return GSON.fromJson(reader, WoMConfig.class);
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