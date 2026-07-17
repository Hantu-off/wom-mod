package ru.hantu;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hantu.config.MwlConfig;

public class MWL implements ModInitializer {
	public static final String MOD_ID = "mwl";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MwlConfig CONFIG;

	@Override
	public void onInitialize() {
		CONFIG = MwlConfig.load();
		LOGGER.info("ModWhiteList загружен! Конфиг находится в config/mwl.json");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path); // Современный аналог fromNamespaceAndPath
	}
}