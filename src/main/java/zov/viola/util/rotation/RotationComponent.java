package zov.viola.util.rotation;

import com.google.common.eventbus.Subscribe;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import zov.viola.event.list.EventTick;
import zov.viola.event.list.MoveInputEvent;
import zov.viola.util.base.Instance;
import zov.viola.util.render.math.GCDFixer;

public class RotationComponent extends Component {
   private RotationComponent.RotationTask c4zQ = RotationComponent.RotationTask.IDLE;
   private float hGcv;
   private float wIMSeW;
   private float scW8q;
   private float zr9Qr;
   private int yPhs05c;
   private int xTpngy;
   private int c2O6n;
   private Rotation zUv0u;
   private int c5Z73y9;
   private static final int MAX_TASK_AGE = 60;

   public static RotationComponent getInstance() {
      return Instance.getComponent(RotationComponent.class);
   }

   public static double direction(float rotationYaw, float moveForward, float moveStrafing) {
      if (moveForward < 0.0F) {
         rotationYaw += 180.0F;
      }

      float forward = 1.0F;
      if (moveForward < 0.0F) {
         forward = -0.5F;
      } else if (moveForward > 0.0F) {
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

   public static void fixMovement(MoveInputEvent event, float inputYaw, float moveYaw) {
      float forward = event.getForward();
      float strafe = event.getStrafe();
      if (forward != 0.0F || strafe != 0.0F) {
         double targetAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(inputYaw, forward, strafe)));
         float bestForward = 0.0F;
         float bestStrafe = 0.0F;
         float smallestDifference = Float.MAX_VALUE;

         for (float testForward = -1.0F; testForward <= 1.0F; testForward++) {
            for (float testStrafe = -1.0F; testStrafe <= 1.0F; testStrafe++) {
               if (testForward != 0.0F || testStrafe != 0.0F) {
                  double testAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(moveYaw, testForward, testStrafe)));
                  float difference = Math.abs(MathHelper.wrapDegrees((float)(targetAngle - testAngle)));
                  if (difference < smallestDifference) {
                     smallestDifference = difference;
                     bestForward = testForward;
                     bestStrafe = testStrafe;
                  }
               }
            }
         }

         event.forward = bestForward;
         event.strafe = bestStrafe;
      }
   }

   @Subscribe
   public void onEvent(MoveInputEvent event) {
      if (this.isRotating() && mc.player != null) {
         fixMovement(event, MathHelper.wrapDegrees(mc.gameRenderer.getCamera().getYaw()), mc.player.getYaw());
      }
   }

   private void resetRotation() {
      Rotation targetRotation = new Rotation(FreeLookComponent.getFreeYaw(), FreeLookComponent.getFreePitch());
      if (this.updateRotation(targetRotation, this.currentYawReturnSpeed(), this.currentPitchReturnSpeed())) {
         this.stopRotation();
      }
   }

   @Subscribe
   public void onEvent(EventTick event) {
      if (mc.player != null) {
         if (!this.currentTask().equals(RotationComponent.RotationTask.IDLE)) {
            if (++this.c5Z73y9 > 60) {
               this.stopRotation();
               this.c5Z73y9 = 0;
               return;
            }
         } else {
            this.c5Z73y9 = 0;
         }

         if (this.currentTask().equals(RotationComponent.RotationTask.AIM) && this.idleTicks() > this.currentTimeout()) {
            this.currentTask(RotationComponent.RotationTask.RESET);
         }

         if (this.currentTask().equals(RotationComponent.RotationTask.RESET)) {
            this.resetRotation();
         }

         this.c2O6n++;
      }
   }

   public static void update(
      Rotation target, float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed, int timeout, int priority, boolean clientRotation
   ) {
      RotationComponent instance = getInstance();
      if (instance.currentPriority() <= priority) {
         if (instance.currentTask().equals(RotationComponent.RotationTask.IDLE) && !clientRotation) {
            FreeLookComponent.setActive(true);
         }

         instance.currentYawSpeed(yawSpeed);
         instance.currentPitchSpeed(pitchSpeed);
         instance.currentYawReturnSpeed(yawReturnSpeed);
         instance.currentPitchReturnSpeed(pitchReturnSpeed);
         instance.currentTimeout(timeout);
         instance.currentPriority(priority);
         instance.currentTask(RotationComponent.RotationTask.AIM);
         instance.targetRotation(target);
         instance.c5Z73y9 = 0;
         instance.updateRotation(target, yawSpeed, pitchSpeed);
      }
   }

   public static void update(Rotation targetRotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
      update(targetRotation, turnSpeed, turnSpeed, returnSpeed, returnSpeed, timeout, priority, false);
   }

   public static void update(Rotation targetRotation, float yawSpeed, float pitchSpeed, float returnSpeed, int timeout, int priority) {
      update(targetRotation, yawSpeed, pitchSpeed, returnSpeed, returnSpeed, timeout, priority, false);
   }

   private boolean updateRotation(Rotation targetRotation, float yawSpeed, float pitchSpeed) {
      if (mc.player == null) {
         return false;
      } else {
         Rotation currentRotation = new Rotation(mc.player);
         float yawDelta = MathHelper.wrapDegrees(targetRotation.getYaw() - currentRotation.getYaw());
         float pitchDelta = targetRotation.getPitch() - currentRotation.getPitch();
         float clampedYaw = Math.min(Math.abs(yawDelta), yawSpeed);
         float clampedPitch = Math.min(Math.abs(pitchDelta), pitchSpeed);
         float yaw = mc.player.getYaw();
         yaw += GCDFixer.getFixRotate(MathHelper.clamp(yawDelta, -clampedYaw, clampedYaw));
         mc.player.setYaw(yaw);
         mc.player
            .setPitch(MathHelper.clamp(mc.player.getPitch() + GCDFixer.getFixRotate(MathHelper.clamp(pitchDelta, -clampedPitch, clampedPitch)), -90.0F, 90.0F));
         this.idleTicks(0);
         return new Rotation(mc.player).getDelta(targetRotation) < 1.0F;
      }
   }

   public void stopRotation() {
      this.currentTask(RotationComponent.RotationTask.IDLE);
      this.currentPriority(0);
      FreeLookComponent.setActive(false);
   }

   public boolean isRotating() {
      return !this.c4zQ.equals(RotationComponent.RotationTask.IDLE);
   }

   @Generated
   public RotationComponent.RotationTask currentTask() {
      return this.c4zQ;
   }

   @Generated
   public float currentYawSpeed() {
      return this.hGcv;
   }

   @Generated
   public float currentPitchSpeed() {
      return this.wIMSeW;
   }

   @Generated
   public float currentYawReturnSpeed() {
      return this.scW8q;
   }

   @Generated
   public float currentPitchReturnSpeed() {
      return this.zr9Qr;
   }

   @Generated
   public int currentPriority() {
      return this.yPhs05c;
   }

   @Generated
   public int currentTimeout() {
      return this.xTpngy;
   }

   @Generated
   public int idleTicks() {
      return this.c2O6n;
   }

   @Generated
   public Rotation targetRotation() {
      return this.zUv0u;
   }

   @Generated
   public int taskAge() {
      return this.c5Z73y9;
   }

   @Generated
   public RotationComponent currentTask(RotationComponent.RotationTask currentTask) {
      this.c4zQ = currentTask;
      return this;
   }

   @Generated
   public RotationComponent currentYawSpeed(float currentYawSpeed) {
      this.hGcv = currentYawSpeed;
      return this;
   }

   @Generated
   public RotationComponent currentPitchSpeed(float currentPitchSpeed) {
      this.wIMSeW = currentPitchSpeed;
      return this;
   }

   @Generated
   public RotationComponent currentYawReturnSpeed(float currentYawReturnSpeed) {
      this.scW8q = currentYawReturnSpeed;
      return this;
   }

   @Generated
   public RotationComponent currentPitchReturnSpeed(float currentPitchReturnSpeed) {
      this.zr9Qr = currentPitchReturnSpeed;
      return this;
   }

   @Generated
   public RotationComponent currentPriority(int currentPriority) {
      this.yPhs05c = currentPriority;
      return this;
   }

   @Generated
   public RotationComponent currentTimeout(int currentTimeout) {
      this.xTpngy = currentTimeout;
      return this;
   }

   @Generated
   public RotationComponent idleTicks(int idleTicks) {
      this.c2O6n = idleTicks;
      return this;
   }

   @Generated
   public RotationComponent targetRotation(Rotation targetRotation) {
      this.zUv0u = targetRotation;
      return this;
   }

   @Generated
   public RotationComponent taskAge(int taskAge) {
      this.c5Z73y9 = taskAge;
      return this;
   }

   public static enum RotationTask {
      AIM,
      RESET,
      IDLE;
   }
}
