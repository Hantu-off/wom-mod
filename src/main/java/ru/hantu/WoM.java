package ru.hantu;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hantu.config.WoMConfig;

public class WoM implements ModInitializer {
	public static final String MOD_ID = "wom";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static WoMConfig CONFIG;

	@Override
	public void onInitialize() {
		CONFIG = WoMConfig.load();
		LOGGER.info("WoM (Whitelist of Mods) loaded! Config is at config/wom.json");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}