package zov.viola.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.util.math.Smoother;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.Viola;
import zov.viola.event.list.EventKeyInput;
import zov.viola.event.list.LookEvent;
import zov.viola.module.list.combat.KillAura;

@Mixin({Mouse.class})
public abstract class MouseMixin {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   private double cursorDeltaX;
   @Shadow
   private double cursorDeltaY;
   @Final
   @Shadow
   private Smoother cursorXSmoother;
   @Final
   @Shadow
   private Smoother cursorYSmoother;

   @Inject(
      method = {"updateMouse"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onUpdateMouse(double timeDelta, CallbackInfo ci) {
      if (this.client.player != null) {
         double sensitivity = this.client.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
         double scaled = sensitivity * sensitivity * sensitivity * 8.0;
         double i;
         double j;
         if (this.client.options.smoothCameraEnabled) {
            i = this.cursorXSmoother.smooth(this.cursorDeltaX * scaled, timeDelta * scaled);
            j = this.cursorYSmoother.smooth(this.cursorDeltaY * scaled, timeDelta * scaled);
         } else if (this.client.options.getPerspective().isFirstPerson() && this.client.player.isUsingSpyglass()) {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            i = this.cursorDeltaX * sensitivity * sensitivity * sensitivity;
            j = this.cursorDeltaY * sensitivity * sensitivity * sensitivity;
         } else {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            i = this.cursorDeltaX * scaled;
            j = this.cursorDeltaY * scaled;
         }

         int invert = this.client.options.getInvertYMouse().getValue() ? -1 : 1;
         LookEvent event = new LookEvent(i, j * invert);
         event.post();
         if (Math.abs(event.getYaw()) > 0.01 || Math.abs(event.getPitch()) > 0.01) {
            KillAura.lastPhysicalMoveTime = System.currentTimeMillis();
         }

         if (!event.isCancelled()) {
            this.client.getTutorialManager().onUpdateMouse(event.getYaw(), event.getPitch());
            this.client.player.changeLookDirection(event.getYaw(), event.getPitch());
         }

         this.cursorDeltaX = 0.0;
         this.cursorDeltaY = 0.0;
         ci.cancel();
      }
   }

   @Inject(
      method = {"onMouseButton"},
      at = {@At("HEAD")}
   )
   private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
      if (MinecraftClient.getInstance().currentScreen == null && action != 2) {
         Viola.getInstance().getEventBus().post(new EventKeyInput(button, action));
      }
   }
}
