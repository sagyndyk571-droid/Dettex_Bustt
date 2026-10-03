package zov.viola.util.player.move;

import java.util.Objects;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;

public final class MoveUtil implements IMinecraft {
   public static boolean hasPlayerMovement() {
      return mc.player.input.movementForward != 0.0F || mc.player.input.movementSideways != 0.0F;
   }

   public static boolean isMoving() {
      return hasPlayerMovement();
   }

   public static double getDirection() {
      return direction(mc.player.getYaw(), mc.player.input.movementForward, mc.player.input.movementSideways);
   }

   public static double[] calculateDirection(double distance) {
      float forward = mc.player.input.movementForward;
      float sideways = mc.player.input.movementSideways;
      float yaw = mc.player.getYaw();
      if (forward != 0.0F) {
         if (sideways > 0.0F) {
            yaw += forward > 0.0F ? -45.0F : 45.0F;
         } else if (sideways < 0.0F) {
            yaw += forward > 0.0F ? 45.0F : -45.0F;
         }

         sideways = 0.0F;
         forward = forward > 0.0F ? 1.0F : -1.0F;
      }

      double sinYaw = Math.sin(Math.toRadians(yaw + 90.0F));
      double cosYaw = Math.cos(Math.toRadians(yaw + 90.0F));
      double xMovement = forward * distance * cosYaw + sideways * distance * sinYaw;
      double zMovement = forward * distance * sinYaw - sideways * distance * cosYaw;
      return new double[]{xMovement, zMovement};
   }

   public static double getSpeedSqrt(Entity entity) {
      double dx = entity.getX() - entity.prevX;
      double dy = entity.getY() - entity.prevY;
      double dz = entity.getZ() - entity.prevZ;
      return Math.sqrt(dx * dx + dz * dz + dy * dy);
   }

   public static void setMotion(double velocity) {
      double[] direction = calculateDirection(velocity);
      Objects.requireNonNull(mc.player).setVelocity(direction[0], mc.player.getVelocity().getY(), direction[1]);
   }

   public static void setMotion(double velocity, double y) {
      double[] direction = calculateDirection(velocity);
      Objects.requireNonNull(mc.player).setVelocity(direction[0], y, direction[1]);
   }

   public static double getDegreesRelativeToView(Vec3d positionRelativeToPlayer, float yaw) {
      float optimalYaw = (float)Math.atan2(-positionRelativeToPlayer.x, positionRelativeToPlayer.z);
      double currentYaw = Math.toRadians(MathHelper.wrapDegrees(yaw));
      return Math.toDegrees(MathHelper.wrapDegrees(optimalYaw - currentYaw));
   }

   public static PlayerInput getDirectionalInputForDegrees(PlayerInput input, double dgs, float deadAngle) {
      boolean forwards = input.forward();
      boolean backwards = input.backward();
      boolean left = input.left();
      boolean right = input.right();
      if (dgs >= -90.0F + deadAngle && dgs <= 90.0F - deadAngle) {
         forwards = true;
      } else if (dgs < -90.0F - deadAngle || dgs > 90.0F + deadAngle) {
         backwards = true;
      }

      if (dgs >= 0.0F + deadAngle && dgs <= 180.0F - deadAngle) {
         right = true;
      } else if (dgs >= -180.0F + deadAngle && dgs <= 0.0F - deadAngle) {
         left = true;
      }

      return new PlayerInput(forwards, backwards, left, right, input.jump(), input.sneak(), input.sprint());
   }

   public static double direction(float rotationYaw, float moveForward, float moveStrafing) {
      if (moveForward < 0.0F) {
         rotationYaw += 180.0F;
      }

      float forward = 1.0F;
      if (moveForward < 0.0F) {
         forward = -0.5F;
      }

      if (moveForward > 0.0F) {
         forward = 0.5F;
      }

      if (moveStrafing > 0.0F) {
         rotationYaw -= 90.0F * forward;
      }

      if (moveStrafing < 0.0F) {
         rotationYaw += 90.0F * forward;
      }

      return Math.toRadians(rotationYaw);
   }

   public static PlayerInput getDirectionalInputForDegrees(PlayerInput input, double dgs) {
      return getDirectionalInputForDegrees(input, dgs, 20.0F);
   }

   @Generated
   private MoveUtil() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               218,
               138,
               78,
               103,
               174,
               139,
               84,
               52,
               239,
               194,
               82,
               96,
               231,
               142,
               78,
               96,
               247,
               194,
               68,
               120,
               239,
               145,
               84,
               52,
               239,
               140,
               67,
               52,
               237,
               131,
               73,
               122,
               225,
               150,
               7,
               118,
               235,
               194,
               78,
               122,
               253,
               150,
               70,
               122,
               250,
               139,
               70,
               96,
               235,
               134
            },
            new int[]{142, 226, 39, 20}
         )
      );
   }
}
