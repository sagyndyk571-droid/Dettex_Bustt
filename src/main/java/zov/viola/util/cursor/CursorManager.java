package zov.viola.util.cursor;

public class CursorManager {
   private static boolean yMoCzX = false;
   private static boolean xzsyyv = false;
   private static boolean a187 = false;

   public static void requestHand() {
      yMoCzX = true;
   }

   public static void requestIBeam() {
      xzsyyv = true;
   }

   public static void requestClick() {
      a187 = true;
   }

   public static boolean shouldBeHand() {
      return yMoCzX;
   }

   public static boolean shouldIBeam() {
      return xzsyyv;
   }

   public static boolean shouldClick() {
      return a187;
   }

   public static void reset() {
      yMoCzX = false;
   }

   public static void resetIBeam() {
      xzsyyv = false;
   }

   public static void resetClick() {
      a187 = false;
   }
}
