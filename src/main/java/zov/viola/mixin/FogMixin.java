package zov.viola.mixin;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zov.viola.Viola;
import zov.viola.module.list.render.Ambience;

@Mixin({BackgroundRenderer.class})
public class FogMixin {
   @Inject(
      method = {"applyFog"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private static void onApplyFog(
      Camera camera, FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta, CallbackInfoReturnable<Fog> cir
   ) {
      try {
         if (camera.getSubmersionType() != CameraSubmersionType.NONE) {
            return;
         }

         if (Viola.getInstance() == null || Viola.getInstance().getModuleStorage() == null) {
            return;
         }

         Ambience ambience = Viola.getInstance().getModuleStorage().get(Ambience.class);
         if (ambience == null || !ambience.isEnabled()) {
            return;
         }

         float edgeEnd = getSafeEdgeEnd(viewDistance);
         if (ambience.isFogEnabled()) {
            float density = MathHelper.clamp(ambience.getFogDensity(), 0.0F, 2.0F);
            float denseShrink = density > 1.0F ? MathHelper.lerp(density - 1.0F, 1.0F, 0.45F) : 1.0F;
            float fogEnd = Math.max(2.0F, Math.min(ambience.getFogDistance(), edgeEnd) * denseShrink);
            fogEnd = Math.min(fogEnd, edgeEnd);
            float fogStart = fogEnd * (1.0F - Math.min(density, 1.0F));
            cir.setReturnValue(createFog(ambience.getFogColor(), color.w, fogStart, fogEnd));
         } else if (ambience.isSkyShaderEnabled() && ambience.isRemoveFogEnabled()) {
            float fogStart = edgeEnd * 0.84F;
            cir.setReturnValue(createFog(ambience.getFogColor(), color.w, fogStart, edgeEnd));
         }
      } catch (Exception var13) {
      }
   }

   private static float getSafeEdgeEnd(float viewDistance) {
      float edgeBuffer = MathHelper.clamp(viewDistance * 0.08F, 8.0F, 24.0F);
      return Math.max(4.0F, viewDistance - edgeBuffer);
   }

   private static Fog createFog(int color, float alpha, float start, float end) {
      float red = (color >> 16 & 0xFF) / 255.0F;
      float green = (color >> 8 & 0xFF) / 255.0F;
      float blue = (color & 0xFF) / 255.0F;
      return new Fog(start, end, FogShape.SPHERE, red, green, blue, alpha);
   }
}
