# =============================================================================
# 1. АГРЕССИВНЫЕ НАСТРОЙКИ
# =============================================================================
-ignorewarnings
-dontoptimize
-dontshrink

# Используем словарь для максимально запутанных имён (I, l, 1 и т.д.)
-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt

# Удаляем ВСЮ отладочную информацию (номера строк, имена файлов, локальные переменные)
-dontusemixedcaseclassnames
-keepattributes Signature, InnerClasses, EnclosingMethod
# Мы НАМЕРЕННО не сохраняем LineNumberTable и SourceFile

# =============================================================================
# 2. ЗАЩИТА БИБЛИОТЕК (Не трогать!)
# =============================================================================
-keep class net.minecraft.** { *; }
-keep class net.fabricmc.** { *; }
-keep class com.mojang.** { *; }

# =============================================================================
# 3. ТОЧКИ ВХОДА (Сохраняем ТОЛЬКО имена классов, всё внутри будет обфусцировано)
# =============================================================================
-keep class * implements net.fabricmc.api.ModInitializer
-keep class * implements net.fabricmc.api.ClientModInitializer
-keep class * implements net.fabricmc.api.DedicatedServerModInitializer

# =============================================================================
# 4. СЕТЕВЫЕ ПАКЕТЫ (Сохраняем структуру, необходимую для Fabric Networking)
# =============================================================================
-keepclassmembers class * implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
    public static final net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type ID;
    public static final net.minecraft.network.codec.StreamCodec CODEC;
    public net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type type();
}

# =============================================================================
# 5. КОНФИГУРАЦИЯ (Сохраняем только поля с @SerializedName для Gson)
# =============================================================================
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}