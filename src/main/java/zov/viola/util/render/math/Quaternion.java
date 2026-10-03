package zov.viola.util.render.math;

import org.joml.Vector3f;
import zov.viola.obf.D;

public final class Quaternion {
   public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private float bzCcKp;
   private float gp49C9;
   private float gzyYMkX;
   private float qzfk;

   public Quaternion(float x, float y, float z, float w) {
      this.bzCcKp = x;
      this.gp49C9 = y;
      this.gzyYMkX = z;
      this.qzfk = w;
   }

   public Quaternion(Vector3f axis, float angle, boolean degrees) {
      if (degrees) {
         angle *= (float) (Math.PI / 180.0);
      }

      float f = xTzy5(angle / 2.0F);
      this.bzCcKp = axis.x() * f;
      this.gp49C9 = axis.y() * f;
      this.gzyYMkX = axis.z() * f;
      this.qzfk = tm6xmK(angle / 2.0F);
   }

   public Quaternion(float xAngle, float yAngle, float zAngle, boolean degrees) {
      if (degrees) {
         xAngle *= (float) (Math.PI / 180.0);
         yAngle *= (float) (Math.PI / 180.0);
         zAngle *= (float) (Math.PI / 180.0);
      }

      float f = xTzy5(0.5F * xAngle);
      float f1 = tm6xmK(0.5F * xAngle);
      float f2 = xTzy5(0.5F * yAngle);
      float f3 = tm6xmK(0.5F * yAngle);
      float f4 = xTzy5(0.5F * zAngle);
      float f5 = tm6xmK(0.5F * zAngle);
      this.bzCcKp = f * f3 * f5 + f1 * f2 * f4;
      this.gp49C9 = f1 * f2 * f5 - f * f3 * f4;
      this.gzyYMkX = f * f2 * f5 + f1 * f3 * f4;
      this.qzfk = f1 * f3 * f5 - f * f2 * f4;
   }

   public Quaternion(Quaternion quaternionIn) {
      this.bzCcKp = quaternionIn.bzCcKp;
      this.gp49C9 = quaternionIn.gp49C9;
      this.gzyYMkX = quaternionIn.gzyYMkX;
      this.qzfk = quaternionIn.qzfk;
   }

   @Override
   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Quaternion quaternion = (Quaternion)p_equals_1_;
         if (Float.compare(quaternion.bzCcKp, this.bzCcKp) != 0) {
            return false;
         } else if (Float.compare(quaternion.gp49C9, this.gp49C9) != 0) {
            return false;
         } else {
            return Float.compare(quaternion.gzyYMkX, this.gzyYMkX) != 0 ? false : Float.compare(quaternion.qzfk, this.qzfk) == 0;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int i = Float.floatToIntBits(this.bzCcKp);
      i = 31 * i + Float.floatToIntBits(this.gp49C9);
      i = 31 * i + Float.floatToIntBits(this.gzyYMkX);
      return 31 * i + Float.floatToIntBits(this.qzfk);
   }

   @Override
   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("Quaternion[")
         .append(this.getW())
         .append(" + ");
      stringbuilder.append(this.getX()).append("i + ");
      stringbuilder.append(this.getY()).append("j + ");
      stringbuilder.append(this.getZ()).append("k]");
      return stringbuilder.toString();
   }

   public float getX() {
      return this.bzCcKp;
   }

   public float getY() {
      return this.gp49C9;
   }

   public float getZ() {
      return this.gzyYMkX;
   }

   public float getW() {
      return this.qzfk;
   }

   public void multiply(Quaternion quaternionIn) {
      float f = this.getX();
      float f1 = this.getY();
      float f2 = this.getZ();
      float f3 = this.getW();
      float f4 = quaternionIn.getX();
      float f5 = quaternionIn.getY();
      float f6 = quaternionIn.getZ();
      float f7 = quaternionIn.getW();
      this.bzCcKp = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
      this.gp49C9 = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
      this.gzyYMkX = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
      this.qzfk = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
   }

   public void multiply(float valueIn) {
      this.bzCcKp *= valueIn;
      this.gp49C9 *= valueIn;
      this.gzyYMkX *= valueIn;
      this.qzfk *= valueIn;
   }

   public void conjugate() {
      this.bzCcKp = -this.bzCcKp;
      this.gp49C9 = -this.gp49C9;
      this.gzyYMkX = -this.gzyYMkX;
   }

   public void set(float p_227066_1_, float p_227066_2_, float p_227066_3_, float p_227066_4_) {
      this.bzCcKp = p_227066_1_;
      this.gp49C9 = p_227066_2_;
      this.gzyYMkX = p_227066_3_;
      this.qzfk = p_227066_4_;
   }

   public static float fastInvSqrt(float number) {
      float f = 0.5F * number;
      int i = Float.floatToIntBits(number);
      i = 1597463007 - (i >> 1);
      number = Float.intBitsToFloat(i);
      return number * (1.5F - f * number * number);
   }

   private static float tm6xmK(float p_214904_0_) {
      return (float)Math.cos(p_214904_0_);
   }

   private static float xTzy5(float p_214903_0_) {
      return (float)Math.sin(p_214903_0_);
   }

   public void normalize() {
      float f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
      if (f > 1.0E-6F) {
         float f1 = fastInvSqrt(f);
         this.bzCcKp *= f1;
         this.gp49C9 *= f1;
         this.gzyYMkX *= f1;
         this.qzfk *= f1;
      } else {
         this.bzCcKp = 0.0F;
         this.gp49C9 = 0.0F;
         this.gzyYMkX = 0.0F;
         this.qzfk = 0.0F;
      }
   }

   public Quaternion copy() {
      return new Quaternion(this);
   }
}
