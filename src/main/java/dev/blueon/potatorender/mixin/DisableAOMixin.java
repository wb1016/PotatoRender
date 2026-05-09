package dev.blueon.potatorender.mixin;

import dev.blueon.potatorender.config.PotatoRenderConfig;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelBlockRenderer.class)
public class DisableAOMixin {
    /**
     * Replaces every read of the ambientOcclusion field with the config value.
     * When disableAO is true (default), this forces tesselateBlock() to always
     * take the tesselateFlat() path, removing all baked AO from block vertex colors.
     */
    @Redirect(
        method = "tesselateBlock",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;ambientOcclusion:Z"
        )
    )
    private boolean disableAmbientOcclusion(final ModelBlockRenderer instance) {
        return !PotatoRenderConfig.disableAO;
    }
}