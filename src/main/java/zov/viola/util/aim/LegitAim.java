package zov.viola.util.aim;

import java.security.SecureRandom;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

public class LegitAim extends AimMode {
   private final SliderSetting gzZ5Jz;
   private final SliderSetting qmrb2v;
   private final SliderSetting wb7C1jk;
   private final SliderSetting dTvh;
   private final BooleanSetting c14i;
   private final SecureRandom muDc;
   private int my2F2;
   private double jZku6;
   private long rxKpa;
   private final double[] b0k9wmV;
   private long r6no;
   private final float[] ptT5;
   private int doM3pnv;
   private float jitd;
   private long gA4iulz;

   public LegitAim(SliderSetting speed, SliderSetting sglazhivanie, SliderSetting maxLerp, SliderSetting minLerp, BooleanSetting randomizaciya) {
      super("Легит");
      this.gzZ5Jz = speed;
      this.qmrb2v = sglazhivanie;
      this.wb7C1jk = maxLerp;
      this.dTvh = minLerp;
      this.c14i = randomizaciya;
      this.muDc = new SecureRandom();
      this.b0k9wmV = new double[15];
      this.ptT5 = new float[5];
   }

   private float th6jv4J(float value) {
      if (!this.c14i.getValue()) {
         return value < 20.0F ? 1.0F : (value > 90.0F ? 0.95F : 0.97F);
      } else {
         return value < 20.0F
            ? 0.95F + this.muDc.nextFloat() * 0.1F
            : (value > 90.0F ? 0.8F + this.muDc.nextFloat() * 0.3F : 0.85F + this.muDc.nextFloat() * 0.25F);
      }
   }

   private void wpC4() {
      for (int i = 0; i < 5; i++) {
         int j = i * 3;
         long k = this.r6no + i * 7919L;
         this.b0k9wmV[j] = this.r20vu7U(k, 0.3, 0.7);
         this.b0k9wmV[j + 1] = this.r20vu7U(k + 1L, 0.2, 0.9);
         this.b0k9wmV[j + 2] = this.r20vu7U(k + 2L, 0.1, 0.8);
      }
   }

   @Override
   public double getDouble() {
      return this.gzZ5Jz.getValue();
   }

   @Override
   public void update() {
      this.aE7w3aa();
   }

   @Override
   public void update2() {
      this.my2F2 = 0;
      this.jZku6 = 0.0;
      this.rxKpa = System.currentTimeMillis();
      this.r6no = System.nanoTime();
      this.jitd = 0.0F;
      this.gA4iulz = System.currentTimeMillis();
      this.doM3pnv = 0;
      Arrays.fill(this.ptT5, 0.0F);
      this.wpC4();
   }

   private float jn459Qn(float value2) {
      boolean flag = this.c14i.getValue();
      long i = System.currentTimeMillis() - this.gA4iulz;
      float f1;
      if (flag) {
         f1 = switch (this.my2F2) {
            case 0 -> 0.6F + this.muDc.nextFloat() * 0.4F;
            case 1 -> 0.8F + this.muDc.nextFloat() * 0.3F;
            case 2 -> 0.5F + this.muDc.nextFloat() * 0.3F;
            case 3 -> 0.7F + this.muDc.nextFloat() * 0.5F;
            case 4 -> 0.4F + this.muDc.nextFloat() * 0.4F;
            case 5 -> 0.9F + this.muDc.nextFloat() * 0.2F;
            default -> 0.7F + this.muDc.nextFloat() * 0.4F;
         } * (0.9F + this.muDc.nextFloat() * 0.3F);
         if (i < 45L) {
            f1 *= 0.7F + this.muDc.nextFloat() * 0.4F;
         } else if (i > 300L) {
            f1 *= 1.1F + this.muDc.nextFloat() * 0.4F;
         }

         if (value2 > 120.0F) {
            f1 *= 1.3F;
         } else if (value2 < 15.0F) {
            f1 *= 0.5F + this.muDc.nextFloat() * 0.4F;
         }

         if (this.jitd > 0.0F) {
            float f = f1 - this.jitd;
            if (Math.abs(f) > 0.35F) {
               f1 = this.jitd + (f > 0.0F ? 0.35F : -0.35F);
            }
         }
      } else {
         f1 = 1.0F;
         if (value2 > 120.0F) {
            f1 *= 1.3F;
         } else if (value2 < 15.0F) {
            f1 *= 0.7F;
         }
      }

      float f2 = MathHelper.clamp(f1, 0.15F, 1.8F);
      this.jitd = f2;
      this.gA4iulz = System.currentTimeMillis();
      return f2;
   }

   private float i3Cg3() {
      float f = 0.0F;
      int i = 0;

      for (float f1 : this.ptT5) {
         if (f1 > 0.0F) {
            f += f1;
            i++;
         }
      }

      return i > 0 ? f / i : (this.jitd > 0.0F ? this.jitd : 1.0F);
   }

   private double r20vu7U(long time, double value, double value2) {
      double d0 = Math.abs(Math.sin(time * 0.001)) % 1.0;
      return value + d0 * (value2 - value);
   }

   private void yGNnb(long time2) {
      long i = time2 - this.rxKpa;
      double d0 = 3000.0 + this.b0k9wmV[0] * 2000.0;
      this.jZku6 = i / d0;
      if (this.jZku6 >= 1.0) {
         this.my2F2 = (this.my2F2 + 1) % 5;
         this.jZku6 = 0.0;
         this.rxKpa = time2;
      }
   }

   private void aE7w3aa() {
      this.r6no = System.nanoTime();
      this.my2F2 = 0;
      this.jZku6 = 0.0;
      this.rxKpa = System.currentTimeMillis();
      this.jitd = 0.0F;
      this.gA4iulz = System.currentTimeMillis();
      this.doM3pnv = 0;
      Arrays.fill(this.ptT5, 0.0F);
      this.wpC4();
   }

   @Override
   public void onDoubleLongEntity(double value, long time, Entity entity2) {
      this.yGNnb(time);
   }

   @Override
   public double getDouble2() {
      return 0.0;
   }

   @Override
   public double[] getDoubleArrayByEntity(Entity entity2) {
      return new double[]{0.0, 0.0, 0.0};
   }

   @Override
   public double[] getDoubleArrayByDoubleLongDoubleDoubleDoubleDoubleDoubleLongPlayerEntityDouble(
      double value, long time, double value2, double value4, double value5, double value6, double value7, long time2, PlayerEntity playerEntity, double value8
   ) {
      float f = (float)value5;
      float f1 = this.jn459Qn(f);
      this.ptT5[this.doM3pnv] = f1;
      this.doM3pnv = (this.doM3pnv + 1) % this.ptT5.length;
      float f2 = this.i3Cg3();
      float f3 = this.th6jv4J(f);
      double d0 = this.qmrb2v.getValue();
      double d1 = value8 / 3.0 * value7 / d0;
      double d2 = this.dTvh.getValue();
      double d3 = this.wb7C1jk.getValue();
      if (d2 > d3) {
         d2 = d3;
      }

      double d4 = MathHelper.clamp(d1 * f2 * f3, d2, d3);
      double d5 = value * d4;
      double d6 = 0.0;
      double d7 = RandomUtil.getDouble();
      d5 = RandomUtil.getDoubleByDoubleDouble(d7, d5);
      d6 = RandomUtil.getDoubleByDoubleDouble(d7, d6);
      return new double[]{d5, d6, d5, d6};
   }
}
