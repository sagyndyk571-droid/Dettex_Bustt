package zov.viola.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zov.viola.event.list.RotationEvent;
import zov.viola.module.list.render.NoRender;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;

@Mixin({Camera.class})
public abstract class CameraMixin {
   @Redirect(
      method = {"update"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"
      )
   )
   private void redirectSetRotation(
      Camera instance, float yaw, float pitch, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta
   ) {
      RotationEvent event = new RotationEvent(yaw, pitch, tickDelta);
      event.post();
      float newYaw = event.getYaw();
      float newPitch = event.getPitch();
      instance.setRotation(newYaw, newPitch);
   }

   @Redirect(
      method = {"update"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"
      )
   )
   private float redirectClipToSpace(Camera instance, float f) {
      return Instance.get(NoRender.class).isEnabled()
            && Instance.get(NoRender.class)
               .elements
               .isEnabled("Камераклип")
         ? f
         : instance.clipToSpace(f);
   }
}
