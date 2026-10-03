package zov.viola.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.list.render.Ambience;
import zov.viola.module.list.render.AtmoDawnFog;
import zov.viola.module.list.render.MotionBlur;
import zov.viola.module.list.render.NoRender;
import zov.viola.module.list.render.TargetESP;
import zov.viola.module.list.render.TargetESP2;
import zov.viola.module.list.render.Wings;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;
import zov.viola.util.render.ambience.AmbiencePostProcessor;
import zov.viola.util.render.ambience.MotionBlurRenderer;
import zov.viola.util.render.atmo.AtmoDawnFogRenderer;
import zov.viola.util.render.renderers.DrawUtil;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Inject(
      method = {"renderWorld"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z",
         opcode = 180,
         ordinal = 0
      )}
   )
   public void hookWorldRender(
      RenderTickCounter tickCounter,
      CallbackInfo ci,
      @Local(ordinal = 0) Matrix4f projectionMatrix,
      @Local(ordinal = 2) Matrix4f matrix4f,
      @Local(ordinal = 0) Camera camera
   ) {
      MatrixStack matrixStack = new MatrixStack();
      matrixStack.multiplyPositionMatrix(matrix4f);
      EventWorldRender event = new EventWorldRender(matrixStack, tickCounter.getTickDelta(false));
      event.post();
      DrawUtil.onRender3D(event.getMatrixStack());
      AmbiencePostProcessor.apply(Instance.get(Ambience.class));
      MotionBlurRenderer.apply(Instance.get(MotionBlur.class));
      AtmoDawnFogRenderer.apply(Instance.get(AtmoDawnFog.class), camera, matrix4f, projectionMatrix, tickCounter.getTickDelta(false));
      MatrixStack lateStack = new MatrixStack();
      lateStack.multiplyPositionMatrix(matrix4f);
      Instance.get(Wings.class).renderWingsLate(lateStack, tickCounter.getTickDelta(false));
      Instance.get(TargetESP.class).renderTargetESPLate(lateStack, tickCounter.getTickDelta(false));
      TargetESP2 secondESP = Instance.get(TargetESP2.class);
      if (secondESP.isEnabled()) {
         secondESP.renderTargetESPLate(lateStack, tickCounter.getTickDelta(false));
      }
   }

   @Inject(
      method = {"showFloatingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hideTotemAnimation(ItemStack floatingItem, CallbackInfo ci) {
      NoRender removals = Instance.get(NoRender.class);
      if (removals != null && removals.isEnabled() && removals.elements.isEnabled("Тотем")) {
         ci.cancel();
      }
   }
}
