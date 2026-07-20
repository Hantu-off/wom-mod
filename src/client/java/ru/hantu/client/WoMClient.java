package ru.hantu.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import ru.hantu.network.ClientModListPayload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WoMClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientModListPayload.register();

		ClientPlayConnectionEvents.JOIN.register((ClientPacketListener handler, PacketSender sender, Minecraft client) -> {
			List<ClientModListPayload.ModInfo> mods = new ArrayList<>();

			FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
				ModMetadata metadata = modContainer.getMetadata();
				String id = metadata.getId();

				// Пропускаем системные "моды"
				if (!id.equals("minecraft") && !id.equals("java") && !id.equals("fabricloader")) {
					String name = metadata.getName();
					String version = metadata.getVersion().getFriendlyString();

					// Получаем первого автора
					String author = metadata.getAuthors().stream()
							.findFirst()
							.map(Person::getName)
							.orElse("Unknown");

					// Получаем реальный размер JAR файла(ов) мода
					long size = 0;
					try {
						for (Path path : modContainer.getOrigin().getPaths()) {
							size += Files.size(path);
						}
					} catch (IOException | UnsupportedOperationException e) {
						// Игнорируем ошибки.
						// UnsupportedOperationException возникает для вложенных (NESTED) модов,
						// у которых нет прямого пути к файлу (например, подмодули Fabric API).
						// Для них размер останется 0, что безопасно.
					}

					mods.add(new ClientModListPayload.ModInfo(id, name, version, author, size));
				}
			});

			long nonce = System.currentTimeMillis() ^ (long)(Math.random() * Long.MAX_VALUE);
			String version = FabricLoader.getInstance().getModContainer("wom")
					.map(container -> container.getMetadata().getVersion().getFriendlyString())
					.orElse("unknown");

			sender.sendPacket(new ClientModListPayload(mods, nonce, version));
		});
	}
}