package zov.viola.event.list;

import lombok.Generated;
import zov.viola.event.Event;

public class RotationEvent extends Event {
   private float x8g9;
   private float y363ez;
   private float eyRO;

   @Generated
   public float getYaw() {
      return this.x8g9;
   }

   @Generated
   public float getPitch() {
      return this.y363ez;
   }

   @Generated
   public float getPartialTicks() {
      return this.eyRO;
   }

   @Generated
   public void setYaw(float yaw) {
      this.x8g9 = yaw;
   }

   @Generated
   public void setPitch(float pitch) {
      this.y363ez = pitch;
   }

   @Generated
   public void setPartialTicks(float partialTicks) {
      this.eyRO = partialTicks;
   }

   @Generated
   public RotationEvent(float yaw, float pitch, float partialTicks) {
      this.x8g9 = yaw;
      this.y363ez = pitch;
      this.eyRO = partialTicks;
   }
}
