package zov.viola.event.list;

import lombok.Generated;
import zov.viola.event.Event;

public class EventKeyInput extends Event {
   private final int i9R5cY;
   private final int e65Fp;

   @Generated
   public int getKey() {
      return this.i9R5cY;
   }

   @Generated
   public int getAction() {
      return this.e65Fp;
   }

   @Generated
   public EventKeyInput(int key, int action) {
      this.i9R5cY = key;
      this.e65Fp = action;
   }
}
