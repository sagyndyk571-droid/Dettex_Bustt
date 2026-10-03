package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventCobweb;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.util.player.move.MoveUtil;

@ModuleInformation(
   moduleName = "No Web",
   moduleDesc = "Отменяет замедление в паутине",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class NoWeb extends Module {
   @Subscribe
   private void onCobweb(EventCobweb e) {
      Vec3d velocity = this.mc.player.getVelocity();
      float yaw = this.mc.player.getYaw();
      double forward = 0.0;
      double strafe = 0.0;
      if (this.mc.player.input.playerInput.forward()) {
         forward++;
      }

      if (this.mc.player.input.playerInput.backward()) {
         forward--;
      }

      if (this.mc.player.input.playerInput.left()) {
         strafe++;
      }

      if (this.mc.player.input.playerInput.right()) {
         strafe--;
      }

      if (forward != 0.0 || strafe != 0.0) {
         if (forward != 0.0) {
            if (strafe > 0.0) {
               yaw += forward > 0.0 ? -45 : 45;
            } else if (strafe < 0.0) {
               yaw += forward > 0.0 ? 45 : -45;
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else {
               forward = -1.0;
            }
         }

         double movementYaw = Math.toDegrees(Math.atan2(strafe, forward)) + yaw;
         yaw = (float)((movementYaw % 360.0 + 360.0) % 360.0);
      }

      float result = 0.2F;
      if ((!(yaw >= 313.0F) || !(yaw <= 317.0F))
         && (!(yaw >= 223.0F) || !(yaw <= 227.0F))
         && (!(yaw >= 133.0F) || !(yaw <= 137.0F))
         && (!(yaw >= 43.0F) || !(yaw <= 47.0F))) {
         if ((!(yaw >= 311.0F) || !(yaw <= 319.0F))
            && (!(yaw >= 221.0F) || !(yaw <= 229.0F))
            && (!(yaw >= 131.0F) || !(yaw <= 139.0F))
            && (!(yaw >= 41.0F) || !(yaw <= 49.0F))) {
            if ((!(yaw >= 310.8F) || !(yaw <= 320.8F))
               && (!(yaw >= 220.8F) || !(yaw <= 230.8F))
               && (!(yaw >= 130.8F) || !(yaw <= 140.8F))
               && (!(yaw >= 40.8F) || !(yaw <= 50.8F))) {
               if ((!(yaw >= 308.7F) || !(yaw <= 322.7F))
                  && (!(yaw >= 218.7F) || !(yaw <= 232.7F))
                  && (!(yaw >= 128.7F) || !(yaw <= 142.7F))
                  && (!(yaw >= 38.7F) || !(yaw <= 52.7F))) {
                  if ((!(yaw >= 306.5F) || !(yaw <= 324.5F))
                     && (!(yaw >= 216.5F) || !(yaw <= 234.5F))
                     && (!(yaw >= 126.5F) || !(yaw <= 144.5F))
                     && (!(yaw >= 36.5F) || !(yaw <= 54.5F))) {
                     if (yaw >= 304.0F && yaw <= 327.0F || yaw >= 214.0F && yaw <= 237.0F || yaw >= 124.0F && yaw <= 147.0F || yaw >= 34.0F && yaw <= 57.0F) {
                        result = 0.255F;
                     }
                  } else {
                     result = 0.268F;
                  }
               } else {
                  result = 0.275F;
               }
            } else {
               result = 0.284F;
            }
         } else {
            result = 0.292F;
         }
      } else {
         result = 0.295F;
      }

      if (this.mc.options.jumpKey.isPressed()) {
         this.mc.player.setVelocity(velocity.x, 1.219F, velocity.z);
      } else if (this.mc.options.sneakKey.isPressed()) {
         this.mc.player.setVelocity(velocity.x, this.mc.player.isGliding() ? -1.5 : -3.5, velocity.z);
      } else {
         this.mc.player.setVelocity(velocity.x, 0.0, velocity.z);
      }

      MoveUtil.setMotion(result);
   }
}
