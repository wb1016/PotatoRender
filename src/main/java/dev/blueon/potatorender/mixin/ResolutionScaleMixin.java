package dev.blueon.potatorender.mixin;

import dev.blueon.potatorender.ResolutionScale;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into GameRenderer to intercept renderLevel() and resize() for resolution scaling.
 * <p>
 * At renderLevel HEAD: swaps mainRenderTarget to a lower-resolution scaled target.
 * At renderLevel RETURN: blits the scaled result back to the original target and restores it.
 * At resize tail: also resizes the scaled target to match the new window dimensions.
 * <p>
 * The blit operation uses MC 26.2's unified GPU abstraction (RenderPass, RenderPipelines.TRACY_BLIT)
 * which works with both OpenGL and Vulkan renderers without any backend-specific code.
 */
@Mixin(GameRenderer.class)
public class ResolutionScaleMixin {

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void potatoRender$onRenderLevelHead(CallbackInfo ci) {
        ResolutionScale.getInstance().onRenderLevelStart(this);
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void potatoRender$onRenderLevelReturn(CallbackInfo ci) {
        ResolutionScale.getInstance().onRenderLevelEnd(this);
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void potatoRender$onResize(int width, int height, CallbackInfo ci) {
        ResolutionScale.getInstance().onResize(width, height);
    }
}