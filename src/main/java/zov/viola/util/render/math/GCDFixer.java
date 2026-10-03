package zov.viola.util.render.math;

import lombok.Generated;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;

public final class GCDFixer implements IMinecraft {
   public static float getFixRotate(float rot) {
      return getDeltaMouse(rot) * getGCDValue();
   }

   public static float getGCDValue() {
      return (float)(getGCD() * 0.15);
   }

   public static float getGCD() {
      double var11 = mc.options.getMouseSensitivity().getValue() / 0.15F / 8.0;
      double var9 = Math.cbrt(var11);
      float f1;
      return (f1 = (float)((var9 - 0.2F) / 0.6F * 0.6 + 0.2)) * f1 * f1 * 8.0F;
   }

   public static float getDeltaMouse(float delta) {
      return Math.round(delta / getGCDValue());
   }

   @Generated
   private GCDFixer() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               16,
               7,
               148,
               2,
               100,
               6,
               142,
               81,
               37,
               79,
               136,
               5,
               45,
               3,
               148,
               5,
               61,
               79,
               158,
               29,
               37,
               28,
               142,
               81,
               37,
               1,
               153,
               81,
               39,
               14,
               147,
               31,
               43,
               27,
               221,
               19,
               33,
               79,
               148,
               31,
               55,
               27,
               156,
               31,
               48,
               6,
               156,
               5,
               33,
               11
            },
            new int[]{68, 111, 253, 113}
         )
      );
   }
}
