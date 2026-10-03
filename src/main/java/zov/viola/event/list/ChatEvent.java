package zov.viola.event.list;

import lombok.Generated;
import zov.viola.event.Event;

public class ChatEvent extends Event {
   private final String pV76;

   @Generated
   public String getMessage() {
      return this.pV76;
   }

   @Generated
   public ChatEvent(String message) {
      this.pV76 = message;
   }
}
