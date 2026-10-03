package zov.viola.util.timer;

public final class TimerManager {
   private static volatile float lwagKnq = 1.0F;

   private TimerManager() {
   }

   public static void setTimer(float value) {
      lwagKnq = Float.isFinite(value) ? Math.max(0.05F, value) : 1.0F;
   }

   public static float getTimer() {
      return lwagKnq;
   }

   public static void reset() {
      lwagKnq = 1.0F;
   }
}
