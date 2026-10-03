package zov.viola.event;

import lombok.Generated;
import zov.viola.Viola;

public class Event {
   private boolean puHp4F2;

   public void post() {
      Viola.getInstance().getEventBus().post(this);
   }

   public void cancelEvent() {
      this.setCancelled(true);
   }

   @Generated
   public boolean isCancelled() {
      return this.puHp4F2;
   }

   @Generated
   public void setCancelled(boolean cancelled) {
      this.puHp4F2 = cancelled;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Event other)) {
         return false;
      } else {
         return !other.canEqual(this) ? false : this.isCancelled() == other.isCancelled();
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof Event;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      return result * 59 + (this.isCancelled() ? 79 : 97);
   }

   @Generated
   @Override
   public String toString() {
      return "Event(cancelled=" + this.isCancelled() + ")";
   }
}
