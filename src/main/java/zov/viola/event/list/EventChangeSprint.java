package zov.viola.event.list;

import lombok.Generated;
import zov.viola.event.Event;

public class EventChangeSprint extends Event {
   private boolean sxhQ;

   @Generated
   public boolean isSprinting() {
      return this.sxhQ;
   }

   @Generated
   public void setSprinting(boolean sprinting) {
      this.sxhQ = sprinting;
   }

   @Generated
   public EventChangeSprint(boolean sprinting) {
      this.sxhQ = sprinting;
   }
}
