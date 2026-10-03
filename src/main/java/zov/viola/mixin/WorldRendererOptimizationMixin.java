package zov.viola.mixin;

import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.module.list.render.Optimization;

@Mixin({WorldRenderer.class})
public class WorldRendererOptimizationMixin {
   @Inject(
      method = {"renderClouds"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void disableClouds(
      FrameGraphBuilder frameGraph,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CloudRenderMode renderMode,
      Vec3d cameraPos,
      float tickDelta,
      int solidLayer,
      float alpha,
      CallbackInfo ci
   ) {
      if (Optimization.isNoClouds()) {
         ci.cancel();
      }
   }
}
