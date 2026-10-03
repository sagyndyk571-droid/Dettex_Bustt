package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import zov.viola.event.list.EventHUD;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@ModuleInformation(
   moduleName = "HitIndicator",
   moduleDesc = "Показывает красную дугу со стороны, откуда пришел урон",
   moduleCategory = ModuleCategory.RENDER
)
public class HitIndicator extends Module {
   private final SliderSetting lGkzN2 = new SliderSetting(
      "Радиус", 125.0, 60.0, 260.0, 5.0
   );
   private final SliderSetting fVQx = new SliderSetting(
      "Длина дуги", 76.0, 30.0, 150.0, 1.0
   );
   private final SliderSetting oNfPH = new SliderSetting(
      "Толщина", 2.4F, 1.0, 7.0, 0.1F
   );
   private final SliderSetting zwOu = new SliderSetting(
      "Время показа", 700.0, 250.0, 1600.0, 25.0
   );
   private final SliderSetting d2ipPub = new SliderSetting(
      "Прозрачность", 0.9F, 0.2F, 1.0, 0.05F
   );
   private final SliderSetting n0vVC34 = new SliderSetting(
      D.k(
         new int[]{1104, 1126, 1032, 1210, 1140, 1123, 1039, 1216, 1035, 126, 1137, 1209, 1030, 1120, 1038, 1221, 1148, 1124, 1145}, new int[]{68, 94, 73, 248}
      ),
      32.0,
      6.0,
      64.0,
      1.0
   );
   private final BooleanSetting fp8ud4 = new BooleanSetting("Свечение", true);
   private final List<HitIndicator.Indicator> q1Iq = new ArrayList<>();
   private float piPH6L = -1.0F;

   @Override
   public void onEnable() {
      super.onEnable();
      this.piPH6L = this.fwfuvx6();
      this.q1Iq.clear();
   }

   @Override
   public void onDisable() {
      this.q1Iq.clear();
      this.piPH6L = -1.0F;
      super.onDisable();
   }

   @Subscribe
   private void onTick(EventTick event) {
      if (this.mc.player != null && this.mc.world != null) {
         float currentHealth = this.fwfuvx6();
         if (this.piPH6L < 0.0F) {
            this.piPH6L = currentHealth;
         } else {
            if (currentHealth + 0.01F < this.piPH6L) {
               Float yaw = this.resolveDamageYaw();
               if (yaw != null) {
                  this.bFdATkL(yaw);
               }
            }

            this.piPH6L = currentHealth;
         }
      } else {
         this.piPH6L = -1.0F;
         this.q1Iq.clear();
      }
   }

   @Subscribe
   private void onHud(EventHUD event) {
      if (this.mc.player != null && this.mc.world != null && !this.q1Iq.isEmpty() && !this.mc.options.hudHidden) {
         long now = System.currentTimeMillis();
         float width = this.mc.getWindow().getScaledWidth();
         float height = this.mc.getWindow().getScaledHeight();
         float centerX = width / 2.0F;
         float centerY = height / 2.0F;
         float maxRadius = Math.max(24.0F, Math.min(centerX, centerY) - 8.0F);
         float drawRadius = Math.min(this.lGkzN2.getFloatValue(), maxRadius);
         float cameraYaw = this.mc.gameRenderer.getCamera().getYaw();
         Iterator<HitIndicator.Indicator> iterator = this.q1Iq.iterator();

         while (iterator.hasNext()) {
            HitIndicator.Indicator indicator = iterator.next();
            float age = (float)(now - indicator.z5zm85y);
            float lifetime = this.zwOu.getFloatValue();
            if (age >= lifetime) {
               iterator.remove();
            } else {
               float progress = MathHelper.clamp(age / lifetime, 0.0F, 1.0F);
               float fadeIn = MathHelper.clamp(age / 90.0F, 0.0F, 1.0F);
               float alpha = this.d2ipPub.getFloatValue() * fadeIn * (1.0F - progress * progress);
               if (!(alpha <= 0.02F)) {
                  float relativeYaw = MathHelper.wrapDegrees(indicator.vuycVv - cameraYaw);
                  int baseAlpha = (int)(alpha * 255.0F);
                  int color = ColorProvider.rgba(255, 35, 45, (float)baseAlpha);
                  if (this.fp8ud4.getValue()) {
                     int glowAlpha = (int)(baseAlpha * 0.22F);
                     int glowColor = ColorProvider.rgba(255, 35, 45, (float)glowAlpha);
                     this.cmajj(centerX, centerY, drawRadius, relativeYaw, this.fVQx.getFloatValue(), this.oNfPH.getFloatValue() + 3.2F, glowColor);
                  }

                  this.cmajj(centerX, centerY, drawRadius, relativeYaw, this.fVQx.getFloatValue(), this.oNfPH.getFloatValue(), color);
               }
            }
         }
      }
   }

   private float fwfuvx6() {
      return this.mc.player == null ? -1.0F : this.mc.player.getHealth() + this.mc.player.getAbsorptionAmount();
   }

   private void bFdATkL(float worldYaw) {
      this.q1Iq.add(new HitIndicator.Indicator(worldYaw, System.currentTimeMillis()));

      while (this.q1Iq.size() > 8) {
         this.q1Iq.remove(0);
      }
   }

   private Float resolveDamageYaw() {
      Entity source = this.kGnilV();
      if (!this.qTU9leV(source)) {
         return null;
      } else {
         double dx = source.getX() - this.mc.player.getX();
         double dz = source.getZ() - this.mc.player.getZ();
         return dx * dx + dz * dz < 1.0E-4 ? null : (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      }
   }

   private Entity kGnilV() {
      Entity nearest = null;
      Entity nearestSwinging = null;
      double bestDistance = this.n0vVC34.getFloatValue() * this.n0vVC34.getFloatValue();
      double bestSwingingDistance = bestDistance;

      for (Entity entity : this.mc.world.getEntities()) {
         if (entity instanceof LivingEntity living && this.qTU9leV(entity)) {
            double distance = this.mc.player.squaredDistanceTo(entity);
            if (living.handSwingTicks < 10 && distance < bestSwingingDistance) {
               bestSwingingDistance = distance;
               nearestSwinging = entity;
            }

            if (distance < bestDistance) {
               bestDistance = distance;
               nearest = entity;
            }
         }
      }

      return nearestSwinging != null ? nearestSwinging : nearest;
   }

   private boolean qTU9leV(Entity entity) {
      return entity != null
         && entity != this.mc.player
         && entity.isAlive()
         && this.mc.player != null
         && this.mc.player.squaredDistanceTo(entity) <= this.n0vVC34.getFloatValue() * this.n0vVC34.getFloatValue();
   }

   private void cmajj(float centerX, float centerY, float radius, float yaw, float length, float lineWidth, int color) {
      float start = yaw - length / 2.0F;
      float end = yaw + length / 2.0F;
      DrawUtil.drawRingArc(centerX, centerY, radius, lineWidth, start, end, color);
   }

   private static class Indicator {
      private final float vuycVv;
      private final long z5zm85y;

      private Indicator(float worldYaw, long spawnTime) {
         this.vuycVv = worldYaw;
         this.z5zm85y = spawnTime;
      }
   }
}
