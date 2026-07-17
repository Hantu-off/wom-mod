package ru.hantu.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ru.hantu.MWL;

import java.util.HashMap;
import java.util.Map;

public record ClientModListPayload(Map<String, String> modIdToName, long nonce, String mwlVersion) implements CustomPacketPayload {
    public static final Type<ClientModListPayload> ID =
            new Type<>(MWL.id("client_mod_list"));

    // Используем StreamCodec с ручной сериализацией Map (гарантированно работает в 26.2)
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientModListPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        // 1. Записываем размер карты
                        buf.writeVarInt(payload.modIdToName().size());
                        // 2. Записываем каждую пару Ключ (ID) -> Значение (Название)
                        for (Map.Entry<String, String> entry : payload.modIdToName().entrySet()) {
                            buf.writeUtf(entry.getKey());
                            buf.writeUtf(entry.getValue());
                        }
                        // 3. Записываем nonce и версию
                        buf.writeLong(payload.nonce());
                        buf.writeUtf(payload.mwlVersion());
                    },
                    buf -> {
                        // 1. Читаем размер карты
                        int count = buf.readVarInt();
                        Map<String, String> mods = new HashMap<>();
                        // 2. Читаем каждую пару
                        for (int i = 0; i < count; i++) {
                            mods.put(buf.readUtf(), buf.readUtf());
                        }
                        // 3. Читаем nonce и версию
                        long nonce = buf.readLong();
                        String version = buf.readUtf();
                        return new ClientModListPayload(mods, nonce, version);
                    }
            );

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(ID, CODEC);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}