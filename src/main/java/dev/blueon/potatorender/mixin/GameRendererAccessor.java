package dev.blueon.potatorender.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixin accessor interface to read and write GameRenderer's private mainRenderTarget field.
 * This is needed for resolution scaling to swap the render target during level rendering.
 */
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Accessor("mainRenderTarget")
    RenderTarget potatoRender$getMainRenderTarget();

    @Accessor("mainRenderTarget")
    @Mutable
    void potatoRender$setMainRenderTarget(RenderTarget target);
}