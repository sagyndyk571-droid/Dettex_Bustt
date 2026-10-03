package zov.viola.util.rotation.apex;

import java.util.Arrays;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ApexRotation {
   private long aimStartTime = -1L;
   private final float[] speedHistory = new float[5];
   private int historyIndex = 0;
   private long lastHistoryTick = 0L;
   private long farIdleUntil = 0L;
   private float smoothYaw = 0.0F;
   private float smoothPitch = 0.0F;

   public enum Mode {
      SPOOKY,
      APEX
   }

   public static class Result {
      public float targetYaw;
      public float targetPitch;
      public float yawSpeed;
      public float pitchSpeed;
      public float resetSpeed;
      public float extra;
      public float angleDiff;
   }

   public Result tick(ClientPlayerEntity player, LivingEntity target, float attackRange, float chaseRange, float partialTicks, Mode mode) {
      if (target == null || player == null) {
         this.aimStartTime = -1L;
         return null;
      }
      if (mode == Mode.APEX) {
         return this.apexTick(player, target, attackRange, chaseRange);
      }
      return this.spookyTick(player, target, attackRange, chaseRange, partialTicks);
   }

   public Result spookyTick(ClientPlayerEntity player, LivingEntity target, float attackRange, float chaseRange, float partialTicks) {
      float f2 = attackRange + chaseRange + 0.1F;
      float playerYaw = player.getYaw();
      float playerPitch = player.getPitch();
      boolean aimed = ApexRotationUtils.rayTraceSingleEntity(player, playerYaw, playerPitch, attackRange + f2, target);
      long now = System.currentTimeMillis();
      double h = target.getHeight() * 0.55;
      double eyeY = player.getCameraPosVec(partialTicks).y;
      Vec3d eye = player.getCameraPosVec(partialTicks);
      Vec3d vec = target.getPos().add(0.0, MathHelper.clamp(eyeY - target.getY(), 0.0, h), 0.0).subtract(eye).normalize();
      float targetYaw = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
      float targetPitch = MathHelper.clamp((float)-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))), -90.0F, 90.0F);
      float yawDiff = MathHelper.wrapDegrees(targetYaw - playerYaw);
      double dist = player.distanceTo(target);
      float rnd = (float)Math.random();
      float absDiff = Math.abs(yawDiff);
      float base;
      if (dist < 2.5) {
         base = absDiff < 8.0F ? 0.32F + rnd * 0.05F : 0.42F + rnd * 0.06F;
      } else if (dist > 5.5) {
         base = absDiff < 8.0F ? 0.11F + rnd * 0.03F : 0.15F + rnd * 0.04F;
      } else {
         base = absDiff < 8.0F ? 0.22F + rnd * 0.04F : 0.30F + rnd * 0.05F;
      }
      if (aimed) {
         boolean center = ApexRotationUtils.rayTraceSmallHitBox(player, playerYaw, playerPitch, f2, target);
         base *= center ? 0.7F : 0.92F;
         this.aimStartTime = -1L;
      } else {
         if (this.aimStartTime == -1L) {
            this.aimStartTime = now;
         }
         base *= Math.min(1.0F + (now - this.aimStartTime) / 380.0F, 1.9F);
      }
      double yawSpeed = absDiff * base + (Math.random() - 0.5) * 0.05;
      yawSpeed = aimed ? MathHelper.clamp(yawSpeed, 0.12, dist < 3.0 ? 4.8 : 3.0) : MathHelper.clamp(yawSpeed, 1.2, 11.0);
      if (now != this.lastHistoryTick) {
         this.speedHistory[this.historyIndex] = (float)Math.abs(yawSpeed);
         this.historyIndex = (this.historyIndex + 1) % this.speedHistory.length;
         this.lastHistoryTick = now;
      }
      float sum = 0.0F;
      for (float v : this.speedHistory) {
         sum += v;
      }
      if (sum > 20.0F && aimed) {
         yawSpeed *= 20.0F / sum;
         this.speedHistory[(this.historyIndex - 1 + this.speedHistory.length) % this.speedHistory.length] = (float)Math.abs(yawSpeed);
      }
      double pitchSpeed;
      if (aimed) {
         boolean center = ApexRotationUtils.rayTraceSmallHitBox(player, playerYaw, playerPitch, f2, target);
         pitchSpeed = center ? (Math.random() - 0.5) * 0.018 : 0.2 + Math.random() * 0.26;
      } else {
         pitchSpeed = 1.7 + Math.random() * 0.9;
      }
      yawSpeed += (Math.random() - 0.5) * 0.05;
      yawSpeed = Math.max(yawSpeed, 0.08);
      double resetSpeed = 8.0 + Math.random() * 2.0;
      double extra = 1.7 + Math.random() * 0.9;
      targetPitch = playerPitch + (targetPitch - playerPitch) * 0.4F;
      Result r = new Result();
      r.targetYaw = playerYaw + (float)Math.ceil(MathHelper.wrapDegrees(targetYaw) - MathHelper.wrapDegrees(playerYaw));
      r.targetPitch = playerPitch + (float)Math.ceil(MathHelper.wrapDegrees(targetPitch) - MathHelper.wrapDegrees(playerPitch));
      r.yawSpeed = (float)yawSpeed;
      r.pitchSpeed = (float)pitchSpeed;
      r.resetSpeed = (float)resetSpeed;
      r.extra = (float)extra;
      r.angleDiff = yawDiff;
      return r;
   }

   public Result apexTick(ClientPlayerEntity player, LivingEntity target, float attackRange, float chaseRange) {
      float f2 = attackRange + chaseRange + 0.1F;
      float playerYaw = player.getYaw();
      float playerPitch = player.getPitch();
      boolean aimed = ApexRotationUtils.rayTraceSingleEntity(player, playerYaw, playerPitch, attackRange + f2, target);
      double h = target.getHeight() * 0.55;
      float pt = 1.0F;
      Vec3d eye = player.getCameraPosVec(pt);
      Vec3d vec = target.getPos().add(0.0, MathHelper.clamp(eye.y - target.getY(), 0.0, h), 0.0).subtract(eye).normalize();
      float targetYaw = (float)Math.toDegrees(Math.atan2(-vec.x, vec.z));
      float targetPitch = MathHelper.clamp((float)-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))), -90.0F, 90.0F);
      float yawDiff = MathHelper.wrapDegrees(targetYaw - playerYaw);
      double dist = player.distanceTo(target);
      float absYaw = Math.abs(yawDiff);
      float absPitch = Math.abs(MathHelper.wrapDegrees(targetPitch - playerPitch));
      if (absYaw < 0.35F && absPitch < 0.35F) {
         return null;
      }
      float base = aimed ? (absYaw < 8.0F ? 0.18F : 0.26F) : (absYaw < 8.0F ? 0.16F : 0.22F);
      if (dist > 5.0) {
         base *= 0.75F;
      } else if (dist < 2.5) {
         base *= 1.15F;
      }
      double yawSpeed = absYaw * base + (Math.random() - 0.5) * 0.03;
      double pitchSpeed;
      if (aimed) {
         boolean center = ApexRotationUtils.rayTraceSmallHitBox(player, playerYaw, playerPitch, f2, target);
         pitchSpeed = center ? (Math.random() - 0.5) * 0.014 : 0.14 + Math.random() * 0.18;
      } else {
         pitchSpeed = 0.9 + Math.random() * 0.5;
      }
      boolean moving = player.input.movementSideways != 0.0F || player.input.movementForward != 0.0F;
      double capYaw = moving ? 5.0 : 1.7;
      double capPitch = moving ? 2.0 : 0.5;
      if (dist >= 3.5) {
         double want = absYaw * (aimed ? 0.04 + Math.random() * 0.035 : 0.05 + Math.random() * 0.04) + (Math.random() - 0.5) * 0.03;
         yawSpeed = 0.40 * this.smoothYaw + 0.60 * want;
         this.smoothYaw = (float)yawSpeed;
         yawSpeed = Math.min(yawSpeed, absYaw * 0.45 + 0.12);
         double wantP = aimed ? (Math.random() - 0.5) * 0.02 : absPitch * (0.10 + Math.random() * 0.08) + 0.02;
         pitchSpeed = 0.40 * this.smoothPitch + 0.60 * wantP;
         this.smoothPitch = (float)pitchSpeed;
         pitchSpeed = Math.min(pitchSpeed, absPitch * 0.40 + 0.08);
         capYaw = moving ? 1.6 : 0.8;
         capPitch = moving ? 0.55 : 0.25;
         long now = System.currentTimeMillis();
         if (!moving && absYaw < 1.5F && absPitch < 1.5F) {
            if (now >= this.farIdleUntil) {
               this.farIdleUntil = now + 120L + (long)(Math.random() * 360.0);
               if (Math.random() < 0.85) {
                  return null;
               }
            }
         } else {
            this.farIdleUntil = 0L;
         }
      }
      yawSpeed = MathHelper.clamp(yawSpeed, 0.05, capYaw);
      pitchSpeed = MathHelper.clamp(pitchSpeed, 0.03, capPitch);
      targetPitch = playerPitch + (targetPitch - playerPitch) * 0.4F;
      Result r = new Result();
      r.targetYaw = playerYaw + (float)Math.ceil(MathHelper.wrapDegrees(targetYaw) - MathHelper.wrapDegrees(playerYaw));
      r.targetPitch = playerPitch + (float)Math.ceil(MathHelper.wrapDegrees(targetPitch) - MathHelper.wrapDegrees(playerPitch));
      r.yawSpeed = (float)yawSpeed;
      r.pitchSpeed = (float)pitchSpeed;
      r.resetSpeed = (float)(8.0 + Math.random() * 2.0);
      r.extra = (float)(1.7 + Math.random() * 0.9);
      r.angleDiff = yawDiff;
      return r;
   }

   public void reset() {
      this.aimStartTime = -1L;
      Arrays.fill(this.speedHistory, 0.0F);
      this.historyIndex = 0;
      this.lastHistoryTick = 0L;
      this.farIdleUntil = 0L;
      this.smoothYaw = 0.0F;
      this.smoothPitch = 0.0F;
   }
}
