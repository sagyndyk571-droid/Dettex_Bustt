package zov.viola.util.rotation.apex;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class ApexRotationUtils {
   private ApexRotationUtils() {
   }

   public static boolean rayTraceSingleEntity(ClientPlayerEntity player, float yaw, float pitch, double dist, Entity target) {
      Vec3d eye = player.getCameraPosVec(1.0F);
      Vec3d look = getVectorForRotation(pitch, yaw);
      Vec3d end = eye.add(look.multiply(dist));
      Box bb = target.getBoundingBox();
      return bb.contains(eye) || bb.raycast(eye, end).isPresent();
   }

   public static boolean rayTraceSmallHitBox(ClientPlayerEntity player, float yaw, float pitch, double dist, Entity target) {
      Vec3d eye = player.getCameraPosVec(1.0F);
      Vec3d look = getVectorForRotation(pitch, yaw);
      Vec3d end = eye.add(look.multiply(dist));
      Box bb = target.getBoundingBox();
      float shrinkXZ = 1.6F;
      double cx = (bb.minX + bb.maxX) / 2.0;
      double cy = (bb.minY + bb.maxY) / 2.0;
      double cz = (bb.minZ + bb.maxZ) / 2.0;
      double hx = (bb.maxX - bb.minX) / 2.0 / shrinkXZ;
      double hy = (bb.maxY - bb.minY) / 2.0 / 1.2F;
      double hz = (bb.maxZ - bb.minZ) / 2.0 / shrinkXZ;
      Box small = new Box(cx - hx, cy - hy, cz - hz, cx + hx, cy + hy, cz + hz);
      return small.contains(eye) || small.raycast(eye, end).isPresent();
   }

   public static Vec3d getVectorForRotation(float pitch, float yaw) {
      float f = -yaw * (float)(Math.PI / 180.0) - (float)Math.PI;
      float f1 = -pitch * (float)(Math.PI / 180.0);
      float f2 = MathHelper.cos(f);
      float f3 = MathHelper.sin(f);
      float f4 = -MathHelper.cos(f1);
      float f5 = MathHelper.sin(f1);
      return new Vec3d(f3 * f4, f5, f2 * f4);
   }
}
