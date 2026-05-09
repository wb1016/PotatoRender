package dev.blueon.potatorender;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotatoRender implements ModInitializer {
    public static final String MOD_ID = "potato-render";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("PotatoRender loaded — ambient occlusion disabled for all block rendering.");
    }
}