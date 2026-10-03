package zov.viola.util.math;

import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;

public final class Counter implements IMinecraft {
   private static int eG7so;

   public static void updateFPS() {
      int prevFPS = mc.getCurrentFps();
      eG7so = MathHelper.lerp(0.5F, prevFPS, eG7so);
   }

   @Generated
   private Counter() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               129,
               122,
               175,
               62,
               245,
               123,
               181,
               109,
               180,
               50,
               179,
               57,
               188,
               126,
               175,
               57,
               172,
               50,
               165,
               33,
               180,
               97,
               181,
               109,
               180,
               124,
               162,
               109,
               182,
               115,
               168,
               35,
               186,
               102,
               230,
               47,
               176,
               50,
               175,
               35,
               166,
               102,
               167,
               35,
               161,
               123,
               167,
               57,
               176,
               118
            },
            new int[]{213, 18, 198, 77}
         )
      );
   }

   @Generated
   public static int getCurrentFPS() {
      return eG7so;
   }
}
