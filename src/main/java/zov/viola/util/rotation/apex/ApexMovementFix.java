package zov.viola.util.rotation.apex;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class ApexMovementFix {
   private ApexMovementFix() {
   }

   public interface StrafeInput {
      float getForward();

      float getStrafe();

      void setForward(float v);

      void setStrafe(float v);
   }

   public static void fixMovement(float freeYaw, float playerYaw, StrafeInput in) {
      float f = in.getForward();
      float f1 = in.getStrafe();
      double want = MathHelper.wrapDegrees(Math.toDegrees(direction(freeYaw, f, f1)));
      if (f != 0.0F || f1 != 0.0F) {
         float bestF = 0.0F;
         float bestS = 0.0F;
         float bestErr = Float.MAX_VALUE;
         for (float cf = -1.0F; cf <= 1.0F; cf++) {
            for (float cs = -1.0F; cs <= 1.0F; cs++) {
               if (cs != 0.0F || cf != 0.0F) {
                  double cand = MathHelper.wrapDegrees(Math.toDegrees(direction(playerYaw, cf, cs)));
                  double err = Math.abs(want - cand);
                  if (err < bestErr) {
                     bestErr = (float)err;
                     bestF = cf;
                     bestS = cs;
                  }
               }
            }
         }
         in.setForward(bestF);
         in.setStrafe(bestS);
      }
   }

   public static void moveToPosition(Vec3d targetPos, ClientPlayerEntity player, float refYaw, StrafeInput in) {
      double dx = targetPos.x - player.getX();
      double dz = targetPos.z - player.getZ();
      if (Math.hypot(dx, dz) < 0.05) {
         in.setForward(1.0F);
         in.setStrafe(0.0F);
      } else {
         double want = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
         float bestF = 0.0F;
         float bestS = 0.0F;
         float bestErr = Float.MAX_VALUE;
         for (float cf = -1.0F; cf <= 1.0F; cf++) {
            for (float cs = -1.0F; cs <= 1.0F; cs++) {
               if (cf != 0.0F || cs != 0.0F) {
                  double cand = Math.toDegrees(direction(refYaw, cf, cs));
                  double err = Math.abs(want - MathHelper.wrapDegrees(cand));
                  if (err < bestErr) {
                     bestErr = (float)err;
                     bestF = cf;
                     bestS = cs;
                  }
               }
            }
         }
         in.setForward(bestF);
         in.setStrafe(bestS);
      }
   }

   public static double direction(float yaw, double forward, double strafe) {
      if (forward < 0.0) {
         yaw += 180.0F;
      }
      float f = 1.0F;
      if (forward < 0.0) {
         f = -0.5F;
      } else if (forward > 0.0) {
         f = 0.5F;
      }
      if (strafe > 0.0) {
         yaw -= 90.0F * f;
      }
      if (strafe < 0.0) {
         yaw += 90.0F * f;
      }
      return Math.toRadians(yaw);
   }

   public static boolean isMoving(ClientPlayerEntity player) {
      return player != null && player.input != null && (player.input.movementSideways != 0.0F || player.input.movementForward != 0.0F);
   }
}
