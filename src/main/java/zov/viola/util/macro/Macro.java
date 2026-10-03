package zov.viola.util.macro;

import lombok.Generated;

public class Macro {
   String message;
   int key;

   public String message() {
      return this.message;
   }

   public int key() {
      return this.key;
   }

   @Generated
   public Macro(String message, int key) {
      this.message = message;
      this.key = key;
   }
}
