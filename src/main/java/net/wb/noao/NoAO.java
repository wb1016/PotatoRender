package net.wb.noao;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NoAO implements ModInitializer {
	public static final String MOD_ID = "no-ao";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("NoAO loaded — ambient occlusion disabled for all block rendering.");
	}
}