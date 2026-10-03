package zov.viola.util.math;

import lombok.Generated;

public class StopWatch {
   public long lastMS = System.currentTimeMillis();

   public void reset() {
      this.lastMS = System.currentTimeMillis();
   }

   public boolean isReached(long time) {
      return System.currentTimeMillis() - this.lastMS > time;
   }

   public boolean finished(double delay) {
      return System.currentTimeMillis() - delay >= this.lastMS;
   }

   public boolean every(double delay) {
      boolean finished = this.finished(delay);
      if (finished) {
         this.reset();
      }

      return finished;
   }

   public void setLastMS(long newValue) {
      this.lastMS = System.currentTimeMillis() + newValue;
   }

   public void setTime(long time) {
      this.lastMS = time;
   }

   public long getTime() {
      return System.currentTimeMillis() - this.lastMS;
   }

   public boolean isRunning() {
      return System.currentTimeMillis() - this.lastMS <= 0L;
   }

   public boolean hasTimeElapsed() {
      return this.lastMS < System.currentTimeMillis();
   }

   @Generated
   public long getLastMS() {
      return this.lastMS;
   }
}
