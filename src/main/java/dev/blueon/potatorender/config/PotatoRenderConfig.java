package dev.blueon.potatorender.config;

import dev.blueon.potatorender.configloader.ConfigLoader;

import static dev.blueon.potatorender.PotatoRender.MOD_ID;

public class PotatoRenderConfig {
    /** Render scale factor (0.25 to 1.0). Lower values render at reduced resolution for better performance. */
    public static float scale = 0.5f;

    /** Whether to disable ambient occlusion on block rendering. */
    public static boolean disableAO = true;

    /** Use linear filtering when upscaling (true) or nearest-neighbor (false). */
    public static boolean linearFilter = true;

    // Saving and loading

    public static void load() {
        ConfigLoader.load(PotatoRenderConfig.class, getFileName());
    }

    public static void save() {
        ConfigLoader.save(PotatoRenderConfig.class, getFileName());
    }

    private static String getFileName() {
        return MOD_ID + ".json";
    }

    /**
     * Returns the effective render scale, clamped to valid range.
     */
    public static float getEffectiveScale() {
        return Math.max(0.25f, Math.min(1.0f, scale));
    }

    /**
     * Whether resolution scaling is active (scale < 1.0).
     */
    public static boolean isScalingEnabled() {
        return getEffectiveScale() < 1.0f;
    }
}