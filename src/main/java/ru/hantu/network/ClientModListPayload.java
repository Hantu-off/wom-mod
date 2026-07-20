package ru.hantu.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import ru.hantu.WoM;

import java.util.ArrayList;
import java.util.List;

public record ClientModListPayload(List<ModInfo> mods, long nonce, String womVersion) implements CustomPacketPayload {
    public static final Type<ClientModListPayload> ID =
            new Type<>(WoM.id("client_mod_list"));

    public record ModInfo(String id, String name, String version, String author, long size) {}

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientModListPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeVarInt(payload.mods().size());
                        for (ModInfo mod : payload.mods()) {
                            buf.writeUtf(mod.id());
                            buf.writeUtf(mod.name());
                            buf.writeUtf(mod.version());
                            buf.writeUtf(mod.author());
                            buf.writeLong(mod.size());
                        }
                        buf.writeLong(payload.nonce());
                        buf.writeUtf(payload.womVersion());
                    },
                    buf -> {
                        int count = buf.readVarInt();
                        List<ModInfo> mods = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            String id = buf.readUtf();
                            String name = buf.readUtf();
                            String version = buf.readUtf();
                            String author = buf.readUtf();
                            long size = buf.readLong();
                            mods.add(new ModInfo(id, name, version, author, size));
                        }
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