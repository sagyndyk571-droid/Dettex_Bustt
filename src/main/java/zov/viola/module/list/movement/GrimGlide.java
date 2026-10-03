package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventOnTravelPost;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.util.packet.NetworkUtils;

@ModuleInformation(
   moduleName = "Elytra Exploit",
   moduleDesc = "Эксплойт для элитр",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class GrimGlide extends Module {
   private int qdzvf;
   private boolean ye448;

   @Subscribe
   public void onPacket(EventPacket e2) {
      if (this.mc.player != null) {
         if (e2.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.qdzvf = 2;
            this.ye448 = true;
         }

         if (e2.getPacket() instanceof PlayerMoveC2SPacket) {
            if (this.mc.player.isGliding() && this.qdzvf == 0 && !this.ye448) {
               NetworkUtils.sendSilentPacket(new OnGroundOnly(true, true));
               e2.cancelEvent();
            }

            this.ye448 = false;
         }
      }
   }

   @Subscribe
   public void onTick(EventTick e2) {
      if (this.qdzvf > 0) {
         this.qdzvf--;
      }
   }

   @Subscribe
   public void onTravelPost(EventOnTravelPost eventOnTravelPost) {
      if (this.mc.player != null) {
         Vec3d oldVelocity = this.mc.player.getVelocity();
         Vec3d vec3d = this.mc.player.getRotationVector();
         float f2 = this.mc.player.getPitch() * (float) (Math.PI / 180.0);
         double d2 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
         double e2 = oldVelocity.horizontalLength();
         boolean bl = this.mc.player.getVelocity().y <= 0.0;
         double g2 = bl && this.mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
            ? Math.min(this.mc.player.getFinalGravity(), 0.01)
            : this.mc.player.getFinalGravity();
         double h2 = MathHelper.square(Math.cos(f2));
         oldVelocity = oldVelocity.add(0.0, g2 * (-1.0 + h2 * 0.75), 0.0);
         if (oldVelocity.y < 0.0 && d2 > 0.0) {
            double i2 = oldVelocity.y * -0.1 * h2;
            oldVelocity = oldVelocity.add(vec3d.x * i2 / d2, i2, vec3d.z * i2 / d2);
         }

         if (f2 < 0.0F && d2 > 0.0) {
            double i2 = e2 * -MathHelper.sin(f2) * 0.04F;
            oldVelocity = oldVelocity.add(-vec3d.x * i2 / d2, i2 * 3.2, -vec3d.z * i2 / d2);
         }

         if (d2 > 0.0) {
            oldVelocity = oldVelocity.add((vec3d.x / d2 * e2 - oldVelocity.x) * 0.1, 0.0, (vec3d.z / d2 * e2 - oldVelocity.z) * 0.1);
         }

         double yaw = Math.toRadians(this.mc.player.getYaw());
         double xt = -Math.sin(yaw);
         double zt = Math.cos(yaw);
         if (this.qdzvf >= 1) {
            double bst = 0.09F;
            eventOnTravelPost.setOldVelocity(oldVelocity.multiply(0.99F, 0.98F, 0.99F).add(xt * bst, 0.03F, zt * bst));
         } else {
            eventOnTravelPost.setOldVelocity(oldVelocity.multiply(0.3F, 0.3F, 0.3F));
         }
      }
   }
}
