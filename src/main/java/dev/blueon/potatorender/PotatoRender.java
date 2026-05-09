package dev.blueon.potatorender;

import dev.blueon.potatorender.config.PotatoRenderConfig;
import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotatoRender implements ModInitializer {
    public static final String MOD_ID = "potato-render";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Load config (mimicking QuickLeafDecay's config pattern)
        PotatoRenderConfig.load();
        PotatoRenderConfig.save();
        LOGGER.info("PotatoRender config loaded — scale={}, disableAO={}, linearFilter={}",
                PotatoRenderConfig.getEffectiveScale(),
                PotatoRenderConfig.disableAO,
                PotatoRenderConfig.linearFilter);

        // Initialize resolution scaling
        ResolutionScale.init();

        if (PotatoRenderConfig.disableAO) {
            LOGGER.info("Ambient occlusion disabled for all block rendering.");
        }
        if (PotatoRenderConfig.isScalingEnabled()) {
            LOGGER.info("Resolution scaling enabled at {}x ({}x{} → {}x{}).",
                    PotatoRenderConfig.getEffectiveScale(),
                    "window", "window",
                    "scaled", "scaled");
        }
    }
}