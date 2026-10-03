package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import java.util.Locale;
import net.minecraft.util.math.MathHelper;
import zov.viola.event.list.EventHUD;
import zov.viola.event.list.FireworkEvent;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@ModuleInformation(
   moduleName = "SuperFirework",
   moduleDesc = "Ускорение фейерверков",
   moduleCategory = ModuleCategory.MOVEMENT
)
public final class ElytraBooster extends Module {
   public static final ElytraBooster INSTANCE = new ElytraBooster();
   private static final int[] AI_YAW_VECTORS = new int[]{-45, 45, 135, -135};
   private static final int[] AI_PITCH_VECTORS = new int[]{-45, 45};
   private final BooleanSetting rRI9 = new BooleanSetting(
      "Показать инфо", true
   );
   private final BooleanSetting xGde9Ti = new BooleanSetting(
      "Цвет от темы", true
   );
   private final ColorSetting y2bS2jq = new ColorSetting(
         "Свой цвет", -9021441
      )
      .setVisible(() -> !this.xGde9Ti.getValue());
   private final BooleanSetting ctdgyh = new BooleanSetting(
      "Ускорение", true
   );
   private final ModeSetting o9GBk56 = new ModeSetting(
         "Режим ускорения",
         "Bravo",
         "Bravo",
         "Кастомный",
         "ReallyWorld"
      )
      .setVisible(this.ctdgyh::getValue);
   private final BooleanSetting zSnY = new BooleanSetting(
         "Скорость по Углам", false
      )
      .setVisible(
         () -> this.ctdgyh.getValue() && this.o9GBk56.is("Кастомный")
      );
   private final SliderSetting nwsqZKi = new SliderSetting(
         "Скорость XZ", 1.65, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && !this.zSnY.getValue()
      );
   private final SliderSetting xWeWm = new SliderSetting(
         "Скорость Y", 1.59, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && !this.zSnY.getValue()
      );
   private final SliderSetting nnq26 = new SliderSetting(
         "Yaw 0-5\u00b0", 1.6, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting ao8u7 = new SliderSetting(
         "Yaw 5-10\u00b0", 1.62, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting seaq = new SliderSetting(
         "Yaw 10-15\u00b0", 1.65, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting wipqj = new SliderSetting(
         "Yaw 15-20\u00b0", 1.68, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting n2V4uU = new SliderSetting(
         "Yaw 20-25\u00b0", 1.74, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting lcnDqkk = new SliderSetting(
         "Yaw 25-30\u00b0", 1.8, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting i2ty = new SliderSetting(
         "Yaw 30-35\u00b0", 1.8, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting tmYv = new SliderSetting(
         "Yaw 35-40\u00b0", 1.8, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting qPi0 = new SliderSetting(
         "Yaw 40-45\u00b0", 1.82, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting jPh7q7 = new SliderSetting(
         "Pitch 0-5\u00b0", 1.59, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting n2z0 = new SliderSetting(
         "Pitch 5-10\u00b0", 1.6, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting rpgLj = new SliderSetting(
         "Pitch 10-15\u00b0", 1.61, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting mXvd = new SliderSetting(
         "Pitch 15-20\u00b0", 1.62, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting sj0J = new SliderSetting(
         "Pitch 20-25\u00b0", 1.68, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting xTzw = new SliderSetting(
         "Pitch 25-30\u00b0", 1.74, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting tyg4zqL = new SliderSetting(
         "Pitch 30-35\u00b0", 1.95, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting zcalkTI = new SliderSetting(
         "Pitch 35-40\u00b0", 2.0, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting sU8zth = new SliderSetting(
         "Pitch 40-45\u00b0", 2.2, 1.5, 2.5, 0.01
      )
      .setVisible(
         () -> this.ctdgyh.getValue()
            && this.o9GBk56.is("Кастомный")
            && this.zSnY.getValue()
      );
   private final SliderSetting[] nC5s = new SliderSetting[]{
      this.nnq26, this.ao8u7, this.seaq, this.wipqj, this.n2V4uU, this.lcnDqkk, this.i2ty, this.tmYv, this.qPi0
   };
   private final SliderSetting[] btrq3ag = new SliderSetting[]{
      this.jPh7q7, this.n2z0, this.rpgLj, this.mXvd, this.sj0J, this.xTzw, this.tyg4zqL, this.zcalkTI, this.sU8zth
   };

   public ElytraBooster.BoostValues computeBoost(float yaw, float pitch) {
      if (this.ctdgyh.getValue() && this.mc.player != null) {
         KillAura aura = Instance.get(KillAura.class);
         if (aura != null && aura.isEnabled()) {
            yaw = MathHelper.wrapDegrees(aura.lastYaw);
            pitch = aura.lastPitch;
         }

         if (pitch < -75.0F) {
            return new ElytraBooster.BoostValues(1.5F, 1.5F);
         } else if (this.o9GBk56.is("Кастомный")) {
            return this.so968(yaw, pitch);
         } else if (this.o9GBk56.is("Bravo")) {
            return new ElytraBooster.BoostValues(1.8F, 1.8F);
         } else {
            return this.o9GBk56.is("ReallyWorld") ? this.om5r6t(yaw, pitch) : new ElytraBooster.BoostValues(1.5F, 1.5F);
         }
      } else {
         return new ElytraBooster.BoostValues(1.5F, 1.5F);
      }
   }

   private ElytraBooster.BoostValues so968(float yaw, float pitch) {
      if (!this.zSnY.getValue()) {
         return new ElytraBooster.BoostValues(this.nwsqZKi.getFloatValue(), this.xWeWm.getFloatValue());
      } else {
         float convertedYaw = this.kSEyx(MathHelper.wrapDegrees(yaw));
         float convertedPitch = this.kSEyx(Math.abs(pitch));
         float xzSpeed = this.getSpeedForYaw(convertedYaw);
         float ySpeed = this.getSpeedForPitch(convertedPitch);
         if (ySpeed > xzSpeed) {
            xzSpeed = ySpeed;
         }

         return new ElytraBooster.BoostValues(xzSpeed, ySpeed);
      }
   }

   private ElytraBooster.BoostValues om5r6t(float yaw, float pitch) {
      float speed = this.fxvVv(pitch, yaw, false, true);
      return new ElytraBooster.BoostValues(speed, speed);
   }

   private float getSpeedForYaw(float yaw) {
      int index = (int)(yaw / 5.0F);
      if (index >= this.nC5s.length) {
         index = this.nC5s.length - 1;
      }

      if (index < 0) {
         index = 0;
      }

      return this.nC5s[index].getFloatValue();
   }

   private float getSpeedForPitch(float pitch) {
      int index = (int)(pitch / 5.0F);
      if (index >= this.btrq3ag.length) {
         index = this.btrq3ag.length - 1;
      }

      if (index < 0) {
         index = 0;
      }

      return this.btrq3ag[index].getFloatValue();
   }

   private float kSEyx(float angle) {
      float absAngle = Math.abs(angle);
      if (absAngle > 90.0F) {
         absAngle = 180.0F - absAngle;
      }

      if (absAngle > 45.0F) {
         absAngle = 90.0F - absAngle;
      }

      return absAngle;
   }

   private float fxvVv(float pitch, float yaw, boolean isBravo, boolean applyRwCap) {
      if (Math.abs(pitch) > 55.0F) {
         return 1.55F;
      } else {
         float boost = this.adjustBoostForYaw(yaw, applyRwCap);
         boost = this.adjustBoostForPitch(pitch, boost);
         boost = Math.max(isBravo ? 1.65F : 1.6F, boost);
         return Math.min(boost, isBravo ? 1.9F : 2.2F);
      }
   }

   private float adjustBoostForYaw(float yaw, boolean applyRwCap) {
      int idx = zj4Q84c(yaw, AI_YAW_VECTORS);
      if (idx == -1) {
         return 1.6F;
      } else {
         float dist = Math.abs(MathHelper.wrapDegrees(yaw) - AI_YAW_VECTORS[idx]);
         float maxBoost = 2.2F;
         float minBoostVal = 1.6F;
         float maxDistance = 12.0F;
         float smartBoost = 0.0F;
         if (dist <= maxDistance) {
            float ratio = dist / maxDistance;
            smartBoost = maxBoost - (maxBoost - minBoostVal) * ratio;
         }

         float variableSpeed = xjaf(dist);
         float finalSpeed = Math.max(smartBoost, variableSpeed);
         return applyRwCap ? Math.min(finalSpeed, 1.8F) : finalSpeed;
      }
   }

   private float adjustBoostForPitch(float pitch, float boost) {
      int idx = zj4Q84c(pitch, AI_PITCH_VECTORS);
      if (idx == -1) {
         return boost;
      } else {
         float dist = Math.abs(Math.abs(pitch) - Math.abs(AI_PITCH_VECTORS[idx]));
         if (dist < 30.0F) {
            boost += 0.4F * (1.0F - dist / 30.0F);
         }

         return boost;
      }
   }

   private static float xjaf(float dist) {
      float[] thresholds = new float[]{4.0F, 8.0F, 11.0F, 15.0F, 21.0F, 28.0F};
      float[] speeds = new float[]{2.2F, 2.1F, 2.0F, 1.9F, 1.8F, 1.7F, 1.6F};
      int level = 0;

      while (level < thresholds.length && dist >= thresholds[level]) {
         level++;
      }

      return speeds[level];
   }

   private static int zj4Q84c(float angle, int[] vectors) {
      int minIdx = -1;
      float minDist = Float.MAX_VALUE;

      for (int i = 0; i < vectors.length; i++) {
         float d = Math.abs(MathHelper.wrapDegrees(angle) - vectors[i]);
         if (d < minDist) {
            minDist = d;
            minIdx = i;
         }
      }

      return minIdx;
   }

   @Subscribe
   private void onHud(EventHUD event) {
      if (this.rRI9.getValue() && this.mc.player != null) {
         if (this.mc.player.isGliding()) {
            float yaw = this.mc.player.getYaw();
            float pitch = this.mc.player.getPitch();
            KillAura aura = Instance.get(KillAura.class);
            if (aura != null && aura.isEnabled()) {
               yaw = MathHelper.wrapDegrees(aura.lastYaw);
               pitch = aura.lastPitch;
            }

            String pitchText = String.format(Locale.US, "Y: %.1f\u00b0", pitch);
            String yawText = String.format(
               Locale.US, "XZ: %.1f\u00b0", MathHelper.wrapDegrees(yaw)
            );
            String text = pitchText + "  ·  " + yawText;
            int color = this.xGde9Ti.getValue() ? ColorProvider.getColorClient() : this.y2bS2jq.getValue();
            int sw = this.mc.getWindow().getScaledWidth();
            int sh = this.mc.getWindow().getScaledHeight();
            float w = Fonts.SFBOLD.get().getWidth(text, 8.0F);
            float x = sw / 2.0F - w / 2.0F;
            float y = sh / 2.0F + 32.0F;
            DrawUtil.drawText(Fonts.SFBOLD.get(), text, x, y, ColorProvider.setAlpha(color, 230), 8.0F);
         }
      }
   }

   @Subscribe
   private void onFirework(FireworkEvent event) {
      if (this.mc.player != null && this.ctdgyh.getValue() && event.getBoostedEntity() == this.mc.player) {
         float yaw = this.mc.player.getYaw();
         float pitch = this.mc.player.getPitch();
         KillAura aura = Instance.get(KillAura.class);
         if (aura != null && aura.isEnabled()) {
            yaw = MathHelper.wrapDegrees(aura.lastYaw);
            pitch = aura.lastPitch;
         }

         ElytraBooster.BoostValues values = this.computeBoost(yaw, pitch);
         event.setSpeed(Math.max(values.speedXZ(), values.speedY()));
      }
   }

   public record BoostValues(float speedXZ, float speedY) {
   }
}
