// Resolution scaling implementation for PotatoRender.
// Blit approach referenced from RenderScale (blitAndBlendToTexture for MC >= 26):
//   Source: https://github.com/Zolo101/RenderScale (src/main/java/dev/zelo/renderscale/RenderScale.java)
//   Uses RenderPipelines.TRACY_BLIT (core/screenquad + core/blit_screen shaders) which is
//   backend-agnostic and works with both OpenGL and Vulkan renderers in MC 26.2.
package dev.blueon.potatorender;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import dev.blueon.potatorender.config.PotatoRenderConfig;
import dev.blueon.potatorender.mixin.GameRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Manages a scaled render target for resolution downscaling.
 * <p>
 * During level rendering, the main render target is swapped to a lower-resolution one.
 * After level rendering completes, the low-res result is upscaled back to the original
 * target using a full-screen blit pass.
 * <p>
 * This approach uses MC 26.2's unified GPU abstraction layer (GpuDevice, CommandEncoder,
 * RenderPass) which works with both OpenGL and Vulkan backends without any
 * backend-specific code.
 */
public class ResolutionScale {
    private static final Minecraft client = Minecraft.getInstance();

    private static ResolutionScale instance;

    /** The scaled (lower-resolution) render target. */
    @Nullable
    private RenderTarget scaledTarget;

    /** The original main render target, saved during level rendering. */
    @Nullable
    private RenderTarget originalTarget;

    private boolean active = false;

    public static void init() {
        instance = new ResolutionScale();
    }

    public static ResolutionScale getInstance() {
        return instance;
    }

    /**
     * Called at the start of renderLevel(). Swaps the main render target to a
     * lower-resolution scaled target if resolution scaling is enabled.
     *
     * @param gameRenderer the GameRenderer instance (passed from mixin)
     */
    public void onRenderLevelStart(Object gameRenderer) {
        if (!PotatoRenderConfig.isScalingEnabled()) {
            return;
        }

        float scale = PotatoRenderConfig.getEffectiveScale();
        int windowWidth = client.getWindow().getWidth();
        int windowHeight = client.getWindow().getHeight();
        int scaledWidth = Math.max(1, (int) (windowWidth * scale));
        int scaledHeight = Math.max(1, (int) (windowHeight * scale));

        // Create or resize the scaled target
        if (scaledTarget == null) {
            scaledTarget = new MainTarget(scaledWidth, scaledHeight);
        } else if (scaledTarget.width != scaledWidth || scaledTarget.height != scaledHeight) {
            scaledTarget.resize(scaledWidth, scaledHeight);
        }

        // Save original and swap
        originalTarget = getMainRenderTarget(gameRenderer);
        setMainRenderTarget(gameRenderer, scaledTarget);
        active = true;
    }

    /**
     * Called at the end of renderLevel(). Blits the scaled render target back to
     * the original main render target (upscaling), then restores the original.
     * <p>
     * The blit uses RenderPipelines.TRACY_BLIT which is a simple fullscreen blit
     * pipeline (core/screenquad + core/blit_screen shaders). This pipeline is
     * backend-agnostic and works correctly with both OpenGL and Vulkan renderers.
     *
     * @param gameRenderer the GameRenderer instance (passed from mixin)
     */
    public void onRenderLevelEnd(Object gameRenderer) {
        if (!active || originalTarget == null || scaledTarget == null) {
            return;
        }

        // Restore the original render target
        setMainRenderTarget(gameRenderer, originalTarget);

        // Blit the scaled target onto the original (upscale)
        blitScaledToOriginal();

        active = false;
    }

    /**
     * Blits the scaled render target's color texture onto the original render target
     * using a fullscreen render pass.
     * <p>
     * Referenced from RenderScale's blitAndBlendToTexture method (MC >= 26 path):
     * Uses RenderSystem.getDevice().createCommandEncoder() which returns the
     * appropriate encoder for the active backend (GL or Vulkan).
     */
    private void blitScaledToOriginal() {
        if (scaledTarget == null || originalTarget == null) {
            return;
        }

        RenderSystem.assertOnRenderThread();

        FilterMode filter = PotatoRenderConfig.linearFilter ? FilterMode.LINEAR : FilterMode.NEAREST;

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                        () -> "PotatoRender resolution scale blit",
                        originalTarget.getColorTextureView(),
                        Optional.<Vector4fc>empty())) {
            renderPass.setPipeline(RenderPipelines.TRACY_BLIT);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture(
                    "InSampler",
                    scaledTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(filter));
            renderPass.draw(3, 1, 0, 0);
        }
    }

    /**
     * Called when the window is resized, to also resize the scaled render target.
     */
    public void onResize(int windowWidth, int windowHeight) {
        if (scaledTarget != null) {
            float scale = PotatoRenderConfig.getEffectiveScale();
            int scaledWidth = Math.max(1, (int) (windowWidth * scale));
            int scaledHeight = Math.max(1, (int) (windowHeight * scale));
            if (scaledTarget.width != scaledWidth || scaledTarget.height != scaledHeight) {
                scaledTarget.resize(scaledWidth, scaledHeight);
            }
        }
    }

    /**
     * Cleans up resources when the mod or game is shutting down.
     */
    public void close() {
        if (scaledTarget != null) {
            scaledTarget.destroyBuffers();
            scaledTarget = null;
        }
        originalTarget = null;
        active = false;
    }

    // -- Reflection helpers to access GameRenderer.mainRenderTarget (private final) --
    // Using @Shadow @Mutable in the mixin to allow field access, these methods
    // provide a clean API for the mixin to call.

    private static RenderTarget getMainRenderTarget(Object gameRenderer) {
        return ((GameRendererAccessor) gameRenderer).potatoRender$getMainRenderTarget();
    }

    private static void setMainRenderTarget(Object gameRenderer, RenderTarget target) {
        ((GameRendererAccessor) gameRenderer).potatoRender$setMainRenderTarget(target);
    }
}
