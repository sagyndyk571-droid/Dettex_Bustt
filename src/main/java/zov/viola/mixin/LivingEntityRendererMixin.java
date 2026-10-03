package zov.viola.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.list.render.SeeInvisible;
import zov.viola.util.IMinecraft;
import zov.viola.util.base.Instance;
import zov.viola.util.math.RotationUtil;
import zov.viola.util.player.combat.PredictUtils;

@Mixin({LivingEntityRenderer.class})
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
   extends EntityRenderer<T, S>
   implements FeatureRendererContext<S, M>,
   IMinecraft {
   @Shadow
   private static float clampBodyYaw(LivingEntity entity, float degrees, float tickDelta) {
      return 0.0F;
   }

   @Shadow
   public static boolean shouldFlipUpsideDown(LivingEntity entity) {
      return false;
   }

   protected LivingEntityRendererMixin(Context context) {
      super(context);
   }

   @Inject(
      method = {"updateRenderState"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void viola$updateRenderState(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci) {
      ci.cancel();
      super.updateRenderState(livingEntity, livingEntityRenderState, f);
      float yaw = MathHelper.lerpAngleDegrees(f, livingEntity.prevHeadYaw, livingEntity.headYaw);
      livingEntityRenderState.bodyYaw = clampBodyYaw(livingEntity, yaw, f);
      livingEntityRenderState.yawDegrees = MathHelper.wrapDegrees(yaw - livingEntityRenderState.bodyYaw);
      livingEntityRenderState.pitch = livingEntity.getLerpedPitch(f);
      livingEntityRenderState.customName = livingEntity.getCustomName();
      livingEntityRenderState.flipUpsideDown = shouldFlipUpsideDown(livingEntity);
      if (livingEntityRenderState.flipUpsideDown) {
         livingEntityRenderState.pitch *= -1.0F;
         livingEntityRenderState.yawDegrees *= -1.0F;
      }

      if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
         livingEntityRenderState.limbFrequency = livingEntity.limbAnimator.getPos(f);
         livingEntityRenderState.limbAmplitudeMultiplier = livingEntity.limbAnimator.getSpeed(f);
      } else {
         livingEntityRenderState.limbFrequency = 0.0F;
         livingEntityRenderState.limbAmplitudeMultiplier = 0.0F;
      }

      if (livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
         livingEntityRenderState.headItemAnimationProgress = livingEntity2.limbAnimator.getPos(f);
      } else {
         livingEntityRenderState.headItemAnimationProgress = livingEntityRenderState.limbFrequency;
      }

      livingEntityRenderState.baseScale = livingEntity.getScale();
      livingEntityRenderState.ageScale = livingEntity.getScaleFactor();
      livingEntityRenderState.pose = livingEntity.getPose();
      livingEntityRenderState.sleepingDirection = livingEntity.getSleepingDirection();
      if (livingEntityRenderState.sleepingDirection != null) {
         livingEntityRenderState.standingEyeHeight = livingEntity.getEyeHeight(EntityPose.STANDING);
      }

      livingEntityRenderState.shaking = livingEntity.isFrozen();
      livingEntityRenderState.baby = livingEntity.isBaby();
      livingEntityRenderState.touchingWater = livingEntity.isTouchingWater();
      livingEntityRenderState.usingRiptide = livingEntity.isUsingRiptide();
      livingEntityRenderState.hurt = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
      livingEntityRenderState.deathTime = livingEntity.deathTime > 0 ? livingEntity.deathTime + f : 0.0F;
      livingEntityRenderState.invisibleToPlayer = livingEntityRenderState.invisible && livingEntity.isInvisibleTo(MinecraftClient.getInstance().player);
      livingEntityRenderState.hasOutline = MinecraftClient.getInstance().hasOutline(livingEntity);
      if (livingEntityRenderState.invisible && livingEntity instanceof PlayerEntity) {
         SeeInvisible seeInvisible = Instance.get(SeeInvisible.class);
         if (seeInvisible != null && seeInvisible.isEnabled()) {
            if (seeInvisible.isOpaque()) {
               livingEntityRenderState.invisible = false;
            } else {
               livingEntityRenderState.invisibleToPlayer = false;
            }
         }
      }
   }
}
