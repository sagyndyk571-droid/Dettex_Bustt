package zov.viola.util.aim;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

public class FreeAim extends AimMode {
   private final SliderSetting g3ea;
   private final SliderSetting r8dKl5;
   private final SliderSetting i89zk;
   private final ModeListSetting ufssK;
   private final SliderSetting vzIbf;
   private double sevv;
   private double lml1r;
   private double gcbLv;
   private double xzd640w;
   private double p97sE;
   private double p5i7;
   private double hs9J35;
   private double m4gmz6u;
   private double u0q8;
   private boolean wE5uI;
   private double jf85kg;
   private double e2SCr0;
   private double h5pk;
   private double vrbd;

   public FreeAim(SliderSetting speed, SliderSetting strengthKorrekcii, SliderSetting svobodaVniz, ModeListSetting povedenie, SliderSetting porogZamedl) {
      super("Свободная");
      this.g3ea = speed;
      this.r8dKl5 = strengthKorrekcii;
      this.i89zk = svobodaVniz;
      this.ufssK = povedenie;
      this.vzIbf = porogZamedl;
   }

   @Override
   public double getDouble() {
      return this.g3ea.getValue();
   }

   @Override
   public void update() {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      this.sevv = threadlocalrandom.nextDouble(Math.PI * 2);
      this.lml1r = threadlocalrandom.nextDouble(Math.PI * 2);
      this.gcbLv = threadlocalrandom.nextDouble(0.06, 0.18);
      this.xzd640w = threadlocalrandom.nextDouble(0.04, 0.14);
      this.p97sE = threadlocalrandom.nextDouble(-0.08, 0.08);
      this.p5i7 = 0.0;
      this.hs9J35 = threadlocalrandom.nextDouble(0.85, 1.15);
      this.m4gmz6u = 0.0;
      this.u0q8 = 0.0;
      this.wE5uI = false;
      this.vrbd = 0.0;
   }

   @Override
   public void update2() {
      this.sevv = 0.0;
      this.lml1r = 0.0;
      this.gcbLv = 0.0;
      this.xzd640w = 0.0;
      this.p97sE = 0.0;
      this.p5i7 = 0.0;
      this.hs9J35 = 1.0;
      this.m4gmz6u = 0.0;
      this.u0q8 = 0.0;
      this.wE5uI = false;
      this.vrbd = 0.0;
   }

   private boolean f4Pk(String text) {
      return this.ufssK.isEnabled(text);
   }

   @Override
   public double[] getDoubleArrayByDoubleLongDoubleDoubleDoubleDoubleDoubleLongPlayerEntityDouble(
      double value,
      long time,
      double value4,
      double value5,
      double value6,
      double value15,
      double value16,
      long time2,
      PlayerEntity playerEntity,
      double value17
   ) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      boolean flagx = this.wpna(value6, playerEntity);
      double d0 = flagx ? 0.0 : 1.0;
      if (!flagx) {
         this.u0q8 = Math.min(this.u0q8 + 0.06 * value17, 3.0);
      } else {
         this.u0q8 = this.u0q8 * Math.max(0.0, 1.0 - 0.15 * value17);
      }

      double d1 = flagx ? 0.08 : 0.08 + this.u0q8 * 0.04;
      this.m4gmz6u = this.m4gmz6u + (d0 - this.m4gmz6u) * d1 * value17;
      this.m4gmz6u = MathHelper.clamp(this.m4gmz6u, 0.0, 1.0);
      double d2 = this.r8dKl5.getValue() * this.p5i7 * this.hs9J35;
      double d3 = 1.0;
      if (this.f4Pk("Учитывать сенсу")) {
         double d4 = MinecraftClient.getInstance().options.getMouseSensitivity().getValue();
         d3 = MathHelper.clamp(d4 * 2.0, 0.3, 3.0);
      }

      double d21 = MathHelper.clamp((time2 - time) / (450.0 + threadlocalrandom.nextDouble(150.0)), 0.0, 1.0);
      double d5 = d21 * d21 * (3.0 - 2.0 * d21);
      double d6 = value16 * d2 * d3 * d5;
      if (this.f4Pk(
         "Замедление вблизи"
      )) {
         double d7 = this.vzIbf.getValue();
         if (value6 < d7) {
            double d8 = Math.max(0.2, value6 / d7);
            d6 *= d8;
         }
      }

      double d22 = this.d040Ln(value6);
      d6 *= d22;
      d6 *= 1.0 + this.u0q8 * 0.5;
      if (value6 > 80.0 && !this.wE5uI) {
         this.wE5uI = true;
         this.jf85kg = threadlocalrandom.nextDouble(2.0, 3.5);
         this.e2SCr0 = threadlocalrandom.nextDouble(5.0, 12.0);
         this.h5pk = threadlocalrandom.nextDouble(0.15, 0.4);
         this.vrbd = threadlocalrandom.nextDouble(Math.PI * 2);
      }

      if (value6 < 6.0 && this.wE5uI) {
         this.wE5uI = false;
      }

      if (this.wE5uI) {
         double d23 = Math.sin(this.vrbd) * this.h5pk;
         d6 *= this.jf85kg * (1.0 + d23);
      }

      double d24 = value16 / 15.0;
      double d9 = threadlocalrandom.nextDouble(-0.002, 0.002);
      double d10 = MathHelper.clamp(d6 * 0.03 * d24 + d9, 0.003, 0.85);
      double d11 = value * d10;
      double d12 = value5 * d10 * (0.85 + threadlocalrandom.nextDouble(0.3));
      if (this.f4Pk("Шум при наводке")) {
         double d13 = 0.05 + threadlocalrandom.nextDouble(0.07);
         d11 += Math.sin(this.sevv) * d13 * (0.5 + threadlocalrandom.nextDouble(0.6));
         d12 += Math.cos(this.lml1r) * d13 * 0.45 * (0.4 + threadlocalrandom.nextDouble(0.5));
      }

      if (this.u0q8 > 0.3 && !flagx) {
         double d25 = MathHelper.clamp(this.u0q8 * 0.07, 0.02, 0.2);
         double d14 = value * d25;
         double d15 = value5 * d25 * 0.85;
         if (Math.abs(d11) < Math.abs(d14)) {
            d11 = d14;
         }

         if (Math.abs(d12) < Math.abs(d15)) {
            d12 = d15;
         }
      }

      double d26 = d11 * this.m4gmz6u;
      double d27 = d12 * this.m4gmz6u;
      double d28 = 1.0 + Math.min(value6 / 30.0, 4.0) * d24;
      if (this.wE5uI) {
         d28 *= 2.0 + threadlocalrandom.nextDouble(1.5);
      }

      double d16 = (1.8 + threadlocalrandom.nextDouble(1.2)) * value17 * d28;
      double d17 = (1.3 + threadlocalrandom.nextDouble(0.8)) * value17 * d28;
      double d18 = d26 - value4;
      double d19 = d27 - value15;
      if (Math.abs(d18) > d16) {
         d26 = value4 + Math.signum(d18) * d16;
      }

      if (Math.abs(d19) > d17) {
         d27 = value15 + Math.signum(d19) * d17;
      }

      double d20 = RandomUtil.getDouble();
      d26 = RandomUtil.getDoubleByDoubleDouble(d20, d26);
      d27 = RandomUtil.getDoubleByDoubleDouble(d20, d27);
      return new double[]{d26, d27, d26, d27};
   }

   @Override
   public double[] getDoubleArrayByEntity(Entity entity2) {
      return new double[]{0.0, 0.0, 0.0};
   }

   @Override
   public double getDouble2() {
      return this.p97sE;
   }

   @Override
   public void onDoubleLongEntity(double value, long time, Entity entity2) {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      this.sevv = this.sevv + this.gcbLv * value;
      this.lml1r = this.lml1r + this.xzd640w * value;
      if (this.f4Pk("Плавный вход")) {
         this.p5i7 = Math.min(1.0, this.p5i7 + 0.035 * value);
      } else {
         this.p5i7 = 1.0;
      }

      if (threadlocalrandom.nextDouble() < 0.02) {
         this.hs9J35 = threadlocalrandom.nextDouble(0.8, 1.2);
      }

      if (threadlocalrandom.nextDouble() < 0.015) {
         this.p97sE = this.p97sE + threadlocalrandom.nextDouble(-0.04, 0.04);
         this.p97sE = MathHelper.clamp(this.p97sE, -0.15, 0.1);
      }

      if (this.wE5uI) {
         this.vrbd = this.vrbd + this.e2SCr0 * value * 0.02;
      }
   }

   private boolean wpna(double value2, PlayerEntity playerEntity) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (value2 > 45.0) {
         return false;
      } else if (mc.world == null) {
         return false;
      } else {
         Vec3d vec3d = playerEntity.getEyePos();
         Vec3d vec3d1 = playerEntity.getRotationVec(1.0F);
         double d0 = 6.0;
         Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d0));

         for (Entity entity : mc.world.getEntities()) {
            if (entity != playerEntity && entity.isAlive() && !(playerEntity.squaredDistanceTo(entity) > d0 * d0)) {
               Box box = entity.getBoundingBox();
               if (value2 > 0.0) {
                  box = box.expand(value2);
               }

               double d1 = this.i89zk.getValue();
               if (d1 != 0.0) {
                  box = new Box(box.minX, box.minY - d1, box.minZ, box.maxX, box.maxY, box.maxZ);
               }

               if (box.raycast(vec3d, vec3d2).isPresent() || box.contains(vec3d2)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private double d040Ln(double value) {
      if (value < 1.5) {
         return 0.12 + value * 0.12;
      } else if (value < 8.0) {
         double d3 = (value - 1.5) / 6.5;
         return 0.3 + d3 * 0.4;
      } else if (value < 40.0) {
         double d2 = (value - 8.0) / 32.0;
         double d1 = d2 * d2 * (3.0 - 2.0 * d2);
         return 0.7 + d1 * 1.8;
      } else {
         double d0 = Math.min((value - 40.0) / 140.0, 1.0);
         return 2.5 + d0 * 2.5;
      }
   }
}
