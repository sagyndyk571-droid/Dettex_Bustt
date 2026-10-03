package zov.viola.util.rotation.apex;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class ApexTargetCorrection {
   private ApexTargetCorrection() {
   }

   public enum Mode {
      FREE,
      FOCUSED,
      FULL_TARGET
   }

   public static Vec3d pointNearTarget(ClientPlayerEntity player, LivingEntity target, double dist) {
      if (target == null) {
         return player.getPos();
      }
      if (dist <= 0.0) {
         return target.getPos();
      }
      double dx = player.getX() - target.getX();
      double dz = player.getZ() - target.getZ();
      double len = Math.sqrt(dx * dx + dz * dz);
      if (len < 0.001) {
         return target.getPos();
      }
      dx /= len;
      dz /= len;
      return new Vec3d(target.getX() + dx * dist, target.getY(), target.getZ() + dz * dist);
   }

   public static void correctFullTarget(ClientPlayerEntity player, float refYaw, LivingEntity target, ApexMovementFix.StrafeInput in) {
      ApexMovementFix.moveToPosition(pointNearTarget(player, target, 0.0), player, refYaw, in);
   }

   public static void correctFocused(ClientPlayerEntity player, float refYaw, LivingEntity target, ApexMovementFix.StrafeInput in) {
      ApexMovementFix.moveToPosition(pointNearTarget(player, target, 0.0), player, refYaw, in);
   }

   public static void correctFree(float freeYaw, float playerYaw, ApexMovementFix.StrafeInput in) {
      ApexMovementFix.fixMovement(freeYaw, playerYaw, in);
   }

   public static boolean hasWall(ClientPlayerEntity player, Entity target) {
      if (player == null || target == null || player.getWorld() == null) {
         return false;
      }
      Vec3d eye = player.getCameraPosVec(1.0F);
      Vec3d tEye = new Vec3d(target.getX(), target.getEyeY(), target.getZ());
      Vec3d diff = tEye.subtract(eye);
      double len = diff.length();
      if (len < 0.001) {
         return false;
      }
      Vec3d dir = diff.normalize();
      for (double d = 0.0; d < len; d += 0.5) {
         BlockPos pos = new BlockPos((int)Math.floor(eye.x + dir.x * d), (int)Math.floor(eye.y + dir.y * d), (int)Math.floor(eye.z + dir.z * d));
         if (!player.getWorld().getBlockState(pos).isAir()) {
            return true;
         }
      }
      return false;
   }
}
