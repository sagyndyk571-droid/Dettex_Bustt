package zov.viola.event.list;

import zov.viola.event.Event;

public class MoveInputEvent extends Event {
   public float forward;
   public float strafe;
   public boolean jump;
   public boolean sneak;
   public double sneakSlow;
   public boolean cancelled = false;

   public MoveInputEvent(float forward, float strafe, boolean jump, boolean sneak, double sneakSlow) {
      this.forward = forward;
      this.strafe = strafe;
      this.jump = jump;
      this.sneak = sneak;
      this.sneakSlow = sneakSlow;
   }

   public void cancel() {
      this.cancelled = true;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   public double getSneakSlow() {
      return this.sneakSlow;
   }

   public float getForward() {
      return this.forward;
   }

   public float getStrafe() {
      return this.strafe;
   }

   public boolean isJump() {
      return this.jump;
   }

   public boolean isSneaking() {
      return this.sneak;
   }
}
