package zov.viola.util.friend;

import lombok.Generated;

public class Friend {
   String name;
   String mark = "";
   String markColor = "";

   public Friend(String name) {
      this.name = name;
   }

   public String name() {
      return this.name;
   }

   public String mark() {
      return this.mark;
   }

   public void setMarkColor(String color) {
      this.markColor = color == null ? "" : color;
   }

   public String markColor() {
      return this.markColor == null ? "" : this.markColor;
   }

   @Generated
   public Friend(String name, String mark, String markColor) {
      this.name = name;
      this.mark = mark;
      this.markColor = markColor;
   }
}
