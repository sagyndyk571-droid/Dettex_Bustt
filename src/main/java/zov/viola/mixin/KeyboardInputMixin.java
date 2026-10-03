package zov.viola.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.event.list.MoveInputEvent;

@Mixin({KeyboardInput.class})
public abstract class KeyboardInputMixin extends Input {
   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void onTick(CallbackInfo ci) {
      float forward = this.movementForward;
      float strafe = this.movementSideways;
      boolean jump = ((KeyboardInput)(Object)this).playerInput.jump();
      boolean sneak = ((KeyboardInput)(Object)this).playerInput.sneak();
      MoveInputEvent event = new MoveInputEvent(forward, strafe, jump, sneak, 0.3);
      event.post();
      if (event.isCancelled()) {
         this.movementForward = 0.0F;
         this.movementSideways = 0.0F;
      } else {
         this.movementForward = event.getForward();
         this.movementSideways = event.getStrafe();
         ((KeyboardInput)(Object)this).playerInput = new PlayerInput(
            event.getForward() > 0.0F,
            event.getForward() < 0.0F,
            event.getStrafe() > 0.0F,
            event.getStrafe() < 0.0F,
            event.isJump(),
            event.isSneaking(),
            ((KeyboardInput)(Object)this).playerInput.sprint()
         );
      }
   }
}
