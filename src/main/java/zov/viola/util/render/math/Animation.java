package zov.viola.util.render.math;

import lombok.Generated;

public class Animation {
   private long mjJ1;
   private float km7lS;
   private Easing s18mmfX;
   private long favMy;
   private float rGF9J;
   private float nRFH;
   private boolean ysOyF;
   private boolean b7Bqmq;
   private static float tglbx = 1.0F;

   public Animation(long duration, float initialValue, Easing easing) {
      this.mjJ1 = duration;
      this.s18mmfX = easing;
      this.km7lS = initialValue;
      this.rGF9J = initialValue;
      this.nRFH = initialValue;
      this.ysOyF = true;
   }

   public Animation(Easing easing, long duration) {
      this(duration, 0.0F, easing);
   }

   public void run(boolean bool) {
      this.run(bool ? 1.0F : 0.0F);
   }

   public void setValue(float newValue) {
      this.km7lS = newValue;
      this.rGF9J = newValue;
      this.nRFH = newValue;
      this.ysOyF = true;
   }

   public void reset(float initialValue) {
      this.km7lS = initialValue;
      this.rGF9J = initialValue;
      this.nRFH = initialValue;
      this.ysOyF = true;
   }

   public void reset() {
      this.reset(0.0F);
   }

   public void animateTo(float newTarget) {
      if (newTarget != this.nRFH) {
         this.rGF9J = this.km7lS;
         this.nRFH = newTarget;
         this.favMy = System.currentTimeMillis();
         this.ysOyF = false;
      }
   }

   public float run() {
      return this.run(this.nRFH);
   }

   public static void setGlobalSpeed(float speed) {
      tglbx = Math.max(0.05F, speed);
   }

   public static float getGlobalSpeed() {
      return tglbx;
   }

   public float run(float newValue) {
      if (this.ysOyF && newValue == this.nRFH) {
         return this.km7lS;
      } else {
         long currentTime = System.currentTimeMillis();
         if (newValue != this.nRFH) {
            this.rGF9J = this.km7lS;
            this.nRFH = newValue;
            this.favMy = currentTime;
            this.ysOyF = false;
         }

         long elapsed = currentTime - this.favMy;
         long scaledElapsed = (long)((float)elapsed * tglbx);
         if (scaledElapsed >= this.mjJ1) {
            this.km7lS = this.nRFH;
            this.ysOyF = true;
            return this.km7lS;
         } else {
            float progress = (float)scaledElapsed / (float)this.mjJ1;
            float easedProgress = this.s18mmfX.ease(progress, 0.0F, 1.0F, 1.0F);
            this.km7lS = this.rGF9J + (this.nRFH - this.rGF9J) * easedProgress;
            return this.km7lS;
         }
      }
   }

   @Generated
   public long getDuration() {
      return this.mjJ1;
   }

   @Generated
   public float getValue() {
      return this.km7lS;
   }

   @Generated
   public Easing getEasing() {
      return this.s18mmfX;
   }

   @Generated
   public long getStartTime() {
      return this.favMy;
   }

   @Generated
   public float getStartValue() {
      return this.rGF9J;
   }

   @Generated
   public float getTargetValue() {
      return this.nRFH;
   }

   @Generated
   public boolean isDone() {
      return this.ysOyF;
   }

   @Generated
   public boolean isDirection() {
      return this.b7Bqmq;
   }

   @Generated
   public void setDuration(long duration) {
      this.mjJ1 = duration;
   }

   @Generated
   public void setEasing(Easing easing) {
      this.s18mmfX = easing;
   }

   @Generated
   public void setStartTime(long startTime) {
      this.favMy = startTime;
   }

   @Generated
   public void setStartValue(float startValue) {
      this.rGF9J = startValue;
   }

   @Generated
   public void setTargetValue(float targetValue) {
      this.nRFH = targetValue;
   }

   @Generated
   public void setDone(boolean done) {
      this.ysOyF = done;
   }

   @Generated
   public void setDirection(boolean direction) {
      this.b7Bqmq = direction;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Animation other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getDuration() != other.getDuration()) {
         return false;
      } else if (Float.compare(this.getValue(), other.getValue()) != 0) {
         return false;
      } else if (this.getStartTime() != other.getStartTime()) {
         return false;
      } else if (Float.compare(this.getStartValue(), other.getStartValue()) != 0) {
         return false;
      } else if (Float.compare(this.getTargetValue(), other.getTargetValue()) != 0) {
         return false;
      } else if (this.isDone() != other.isDone()) {
         return false;
      } else if (this.isDirection() != other.isDirection()) {
         return false;
      } else {
         Object this$easing = this.getEasing();
         Object other$easing = other.getEasing();
         return this$easing == null ? other$easing == null : this$easing.equals(other$easing);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof Animation;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $duration = this.getDuration();
      result = result * 59 + (int)($duration >>> 32 ^ $duration);
      result = result * 59 + Float.floatToIntBits(this.getValue());
      long $startTime = this.getStartTime();
      result = result * 59 + (int)($startTime >>> 32 ^ $startTime);
      result = result * 59 + Float.floatToIntBits(this.getStartValue());
      result = result * 59 + Float.floatToIntBits(this.getTargetValue());
      result = result * 59 + (this.isDone() ? 79 : 97);
      result = result * 59 + (this.isDirection() ? 79 : 97);
      Object $easing = this.getEasing();
      return result * 59 + ($easing == null ? 43 : $easing.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "Animation(duration="
         + this.getDuration()
         + ", value="
         + this.getValue()
         + ", easing="
         + this.getEasing()
         + ", startTime="
         + this.getStartTime()
         + ", startValue="
         + this.getStartValue()
         + ", targetValue="
         + this.getTargetValue()
         + ", done="
         + this.isDone()
         + ", direction="
         + this.isDirection()
         + ")";
   }
}
