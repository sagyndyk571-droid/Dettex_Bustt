package zov.viola.mixin;

import net.minecraft.client.render.Fog;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.module.list.render.Ambience;
import zov.viola.util.base.Instance;
import zov.viola.util.render.ambience.SkyShaderRenderer;

@Mixin({SkyRendering.class})
public class SkyRenderingMixin {
   @Inject(
      method = {"renderSky(FFF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderCustomSky(float red, float green, float blue, CallbackInfo ci) {
      Ambience ambience = Instance.get(Ambience.class);
      if (ambience != null && ambience.isEnabled() && ambience.isSkyShaderEnabled()) {
         SkyShaderRenderer.render(ambience);
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderStars"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void controlVanillaStars(Fog fog, float brightness, MatrixStack matrices, CallbackInfo ci) {
      Ambience ambience = Instance.get(Ambience.class);
      if (ambience != null && ambience.isEnabled() && ambience.isSkyShaderEnabled()) {
         ci.cancel();
      }
   }
}
