package zov.viola.util.aim;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public abstract class AimMode {
   private final String jbmKmx8;

   protected AimMode(String text) {
      this.jbmKmx8 = text;
   }

   public double getDouble() {
      return 15.0;
   }

   public abstract void update();

   public abstract void update2();

   public abstract double[] getDoubleArrayByEntity(Entity var1);

   public abstract double[] getDoubleArrayByDoubleLongDoubleDoubleDoubleDoubleDoubleLongPlayerEntityDouble(
      double var1, long var3, double var5, double var7, double var9, double var11, double var13, long var15, PlayerEntity var17, double var18
   );

   public String getText() {
      return this.jbmKmx8;
   }

   public abstract void onDoubleLongEntity(double var1, long var3, Entity var5);

   public abstract double getDouble2();
}
