package zov.viola.event.list;

import lombok.Generated;
import zov.viola.event.Event;

public class LookEvent extends Event {
   private double w5RuZEe;
   private double vdYd1;

   @Generated
   public double getYaw() {
      return this.w5RuZEe;
   }

   @Generated
   public double getPitch() {
      return this.vdYd1;
   }

   @Generated
   public LookEvent(double yaw, double pitch) {
      this.w5RuZEe = yaw;
      this.vdYd1 = pitch;
   }
}
