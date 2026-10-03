package zov.viola.util.aim;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class RotationUtil {
   private long ye5c = 0L;

   public void update() {
      this.ye5c = System.nanoTime();
   }

   public void onDoubleFloatFloat(double value, float value2, float value3) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         double d0 = this.getDouble();
         float f = mc.player.getYaw();
         float f1 = mc.player.getPitch();
         double d1 = RandomUtil.getDoubleByDouble(value3 - f);
         double d2 = value2 - f1;
         if (!(Math.sqrt(d1 * d1 + d2 * d2) < 0.05)) {
            double d3 = MathHelper.clamp(value * 0.04 * d0, 0.01, 0.95);
            double d4 = d1 * d3;
            double d5 = d2 * d3;
            double d6 = RandomUtil.getDouble();
            d4 = RandomUtil.getDoubleByDoubleDouble(d6, d4);
            d5 = RandomUtil.getDoubleByDoubleDouble(d6, d5);
            if (d4 == 0.0 && Math.abs(d1) > d6) {
               d4 = Math.signum(d1) * d6;
            }

            if (d5 == 0.0 && Math.abs(d2) > d6) {
               d5 = Math.signum(d2) * d6;
            }

            mc.player.setYaw(f + (float)d4);
            mc.player.setPitch(MathHelper.clamp(f1 + (float)d5, -90.0F, 90.0F));
         }
      }
   }

   public double getDouble() {
      long i = System.nanoTime();
      double d0 = this.ye5c > 0L ? (i - this.ye5c) / 1.66666667E7 : 1.0;
      this.ye5c = i;
      return MathHelper.clamp(d0, 0.05, 3.0);
   }

   public void onDoubleVec3d(double value, Vec3d vec3d) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         float[] afloat = getFloatArrayByVec3d(vec3d);
         this.onDoubleFloatFloat(value, afloat[1], afloat[0]);
      }
   }

   public static float[] getFloatArrayByVec3d(Vec3d vec3d2) {
      MinecraftClient mc = MinecraftClient.getInstance();
      Vec3d vec3d = mc.player.getEyePos();
      double d0 = vec3d2.x - vec3d.x;
      double d1 = vec3d2.y - vec3d.y;
      double d2 = vec3d2.z - vec3d.z;
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      float f = (float)(Math.atan2(d2, d0) * 180.0 / Math.PI) - 90.0F;
      float f1 = (float)(-(Math.atan2(d1, d3) * 180.0 / Math.PI));
      return new float[]{f, f1};
   }

   public void setTime() {
      this.ye5c = 0L;
   }
}
