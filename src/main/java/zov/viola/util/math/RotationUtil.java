package zov.viola.util.math;

import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.rotation.Rotation;

public final class RotationUtil implements IMinecraft {
   public static Vec2f calculate(Vec3d fromVec, Vec3d toVec) {
      double TO_DEGREES = 180.0 / Math.PI;
      Vec3d diff = toVec.subtract(fromVec);
      double distance = Math.hypot(diff.x, diff.z);
      float yaw = (float)(MathHelper.atan2(diff.z, diff.x) * (180.0 / Math.PI)) - 90.0F;
      float pitch = (float)(-(MathHelper.atan2(diff.y, distance) * (180.0 / Math.PI)));
      return new Vec2f(yaw, pitch);
   }

   public static Vec2f calculate(Entity entity) {
      return calculate(entity.getPos().add(0.0, entity.getEyeHeight(entity.getPose()), 0.0));
   }

   public static Vec2f calculate(Vec3d toVec) {
      return calculate(mc.player.getPos().add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0), toVec);
   }

   public static float getAngleDifference(float dir, float yaw) {
      float f = Math.abs(yaw - dir) % 360.0F;
      return f > 180.0F ? 360.0F - f : f;
   }

   public static Vec3d getEyesPos(Entity entity) {
      return entity.getPos().add(0.0, entity.getEyeHeight(entity.getPose()), 0.0);
   }

   public static float[] calculateAngle(Vec3d to) {
      return calculateAngle(getEyesPos(mc.player), to);
   }

   public static float[] calculateAngle(Vec3d from, Vec3d to) {
      double difX = to.x - from.x;
      double difY = (to.y - from.y) * -1.0;
      double difZ = to.z - from.z;
      double dist = MathHelper.sqrt((float)(difX * difX + difZ * difZ));
      float yD = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0);
      float pD = (float)MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))), -90.0, 90.0);
      return new float[]{yD, pD};
   }

   public static Vector3f getDirectionVector(float yaw, float pitch) {
      float yawRadians = (float)Math.toRadians(yaw);
      float pitchRadians = (float)Math.toRadians(pitch);
      float x = -MathHelper.cos(pitchRadians) * MathHelper.sin(yawRadians);
      float y = -MathHelper.sin(pitchRadians);
      float z = MathHelper.cos(pitchRadians) * MathHelper.cos(yawRadians);
      return new Vector3f(x, y, z);
   }

   public static float calculateFov(float cameraYaw, float cameraPitch, float targetYaw, float targetPitch) {
      Vector3f cameraDirection = getDirectionVector(cameraYaw, cameraPitch);
      Vector3f targetDirection = getDirectionVector(targetYaw, targetPitch);
      float dotProduct = cameraDirection.dot(targetDirection);
      dotProduct = MathHelper.clamp(dotProduct, -1.0F, 1.0F);
      float angleRadians = (float)Math.acos(dotProduct);
      return (float)Math.toDegrees(angleRadians);
   }

   public static Rotation fromVec3d(Vec3d vector) {
      return new Rotation(
         (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vector.z, vector.x)) - 90.0),
         (float)MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(vector.y, Math.hypot(vector.x, vector.z))))
      );
   }

   @Generated
   private RotationUtil() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               7,
               223,
               199,
               91,
               115,
               222,
               221,
               8,
               50,
               151,
               219,
               92,
               58,
               219,
               199,
               92,
               42,
               151,
               205,
               68,
               50,
               196,
               221,
               8,
               50,
               217,
               202,
               8,
               48,
               214,
               192,
               70,
               60,
               195,
               142,
               74,
               54,
               151,
               199,
               70,
               32,
               195,
               207,
               70,
               39,
               222,
               207,
               92,
               54,
               211
            },
            new int[]{83, 183, 174, 40}
         )
      );
   }
}
