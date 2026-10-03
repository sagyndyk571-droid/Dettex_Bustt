package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.LivingEntity;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;

@ModuleInformation(
   moduleName = "Elytra Motion",
   moduleDesc = "Управление движением элитр",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class ElytraMotion extends Module {
   private boolean sQMyEMy;
   private final SliderSetting qfAC06 = new SliderSetting(
      "Дистанция", 3.0, 1.0, 6.0, 0.1F
   );
   private final BooleanSetting lnVe5dm = new BooleanSetting("Обход", false);

   @Subscribe
   private void onPlayerTick(EventPlayerUpdate e2) {
      if (this.mc.player != null) {
         LivingEntity target = Instance.get(KillAura.class).getTarget();
         if (target == null) {
            if (!this.sQMyEMy) {
               this.mc.player.setNoGravity(false);
               this.sQMyEMy = true;
            }
         } else {
            this.sQMyEMy = false;
            float dist = (float)this.mc.player.getEyePos().distanceTo(target.getBoundingBox().getCenter());
            if (this.mc.player.isGliding() && dist < this.qfAC06.getFloatValue()) {
               if (this.lnVe5dm.getValue()) {
                  float yaw = this.mc.player.getYaw();
                  double rad = Math.toRadians(yaw);
                  double forward = 0.01;
                  double down = -1.0E-4;
                  double moveX = -Math.sin(rad) * forward;
                  double moveZ = Math.cos(rad) * forward;
                  this.mc.player.setVelocity(moveX, down, moveZ);
               } else {
                  this.mc.player.setVelocity(0.0, 0.0, 0.0);
               }

               this.mc.player.setNoGravity(true);
            } else {
               this.mc.player.setNoGravity(false);
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.mc.player != null) {
         this.sQMyEMy = false;
         this.mc.player.setNoGravity(false);
      }
   }
}
