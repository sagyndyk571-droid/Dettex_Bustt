package zov.viola.mixin;
import com.google.common.base.MoreObjects;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.HeldItemRenderer.HandRenderType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.module.list.render.GlassHands;
import zov.viola.module.list.render.HMIRenderer;
import zov.viola.module.list.render.HeldItemRendererAccessor;
import zov.viola.module.list.render.SwingAnimations;
import zov.viola.module.list.render.ViewModel;
import zov.viola.util.base.Instance;
import zov.viola.util.render.hands.GlassHandsRenderer;
@Mixin({HeldItemRenderer.class})
public abstract class HeldItemRendererMixin implements HeldItemRendererAccessor {
   @Shadow
   private ItemStack mainHand;
   @Shadow
   private float equipProgressMainHand;
   @Shadow
   private float prevEquipProgressMainHand;
   @Shadow
   private float prevEquipProgressOffHand;
   @Shadow
   private float equipProgressOffHand;
   @Shadow
   private ItemStack offHand;
   @Shadow
   protected abstract void renderFirstPersonItem(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      VertexConsumerProvider var9,
      int var10
   );
   @Shadow
   protected abstract void swingArm(float var1, float var2, MatrixStack var3, int var4, Arm var5);
   @Shadow
   private static HandRenderType getHandRenderType(ClientPlayerEntity player) {
      throw new AssertionError();
   }
   @Invoker("renderArmHoldingItem")
   @Override
   public abstract void invokeRenderPlayerArm(MatrixStack var1, VertexConsumerProvider var2, int var3, float var4, float var5, Arm var6);
   @Invoker("applyEquipOffset")
   @Override
   public abstract void invokeApplyItemArmTransform(MatrixStack var1, Arm var2, float var3);
   @Invoker("swingArm")
   @Override
   public abstract void invokeSwingArm(float var1, float var2, MatrixStack var3, int var4, Arm var5);
   @Invoker("renderItem")
   @Override
   public abstract void invokeRenderItem(
      LivingEntity var1, ItemStack var2, ModelTransformationMode var3, boolean var4, MatrixStack var5, VertexConsumerProvider var6, int var7
   );
   @Invoker("renderMapInBothHands")
   @Override
   public abstract void invokeRenderTwoHandedMap(MatrixStack var1, VertexConsumerProvider var2, int var3, float var4, float var5, float var6);
   @Invoker("renderMapInOneHand")
   @Override
   public abstract void invokeRenderOneHandedMap(MatrixStack var1, VertexConsumerProvider var2, int var3, float var4, Arm var5, float var6, ItemStack var7);
   @Accessor("offHand")
   @Override
   public abstract ItemStack getOffHand();
   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void injectHMIHead(
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      CallbackInfo ci
   ) {
      SwingAnimations swingAnimation = Instance.get(SwingAnimations.class);
      if (swingAnimation != null && swingAnimation.isEnabled() && swingAnimation.isHMI()) {
         HMIRenderer hmi = swingAnimation.getHMIRenderer();
         hmi.renderHMI((HeldItemRenderer)(Object)this, this, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
         ci.cancel();
      }
   }
   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
         shift = Shift.AFTER,
         ordinal = 0
      )}
   )
   public void injectAfterMatrixPushHandPosition(
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      CallbackInfo ci
   ) {
      ViewModel viewModel = Instance.get(ViewModel.class);
      if (viewModel != null && viewModel.isEnabled() && !item.contains(DataComponentTypes.MAP_ID)) {
         boolean isMainHand = hand == Hand.MAIN_HAND;
         Arm arm = isMainHand ? player.getMainArm() : player.getMainArm().getOpposite();
         viewModel.applyHandPosition(matrices, arm);
      }
   }
   @Redirect(
      method = {"renderFirstPersonItem"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;swingArm(FFLnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/util/Arm;)V",
         ordinal = 2
      )
   )
   public void redirectSwingArmForCustomAnim(HeldItemRenderer instance, float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm) {
      SwingAnimations swingAnimation = Instance.get(SwingAnimations.class);
      if (swingAnimation == null || !swingAnimation.isEnabled()) {
         this.swingArm(swingProgress, equipProgress, matrices, armX, arm);
      } else if (arm == Arm.RIGHT) {
         swingAnimation.renderSwordAnimation(matrices, swingProgress, equipProgress, arm);
      } else {
         this.swingArm(swingProgress, equipProgress, matrices, armX, arm);
      }
   }
   @Overwrite
   public void renderItem(float tickDelta, MatrixStack matrices, Immediate vertexConsumers, ClientPlayerEntity player, int light) {
      GlassHands glassHands = Instance.get(GlassHands.class);
      boolean glassActive = glassHands != null && glassHands.isEnabled();
      if (glassActive) {
         GlassHandsRenderer.getInstance().captureBeforeHands(glassHands);
      }
      float f = player.getHandSwingProgress(tickDelta);
      Hand hand = (Hand)MoreObjects.firstNonNull(player.preferredHand, Hand.MAIN_HAND);
      float g = player.getLerpedPitch(tickDelta);
      HandRenderType handRenderType = getHandRenderType(player);
      if (handRenderType.renderMainHand) {
         float j = hand == Hand.MAIN_HAND ? f : 0.0F;
         float k = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
         this.renderFirstPersonItem(player, tickDelta, g, Hand.MAIN_HAND, j, this.mainHand, k, matrices, vertexConsumers, light);
      }
      if (handRenderType.renderOffHand) {
         float j = hand == Hand.OFF_HAND ? f : 0.0F;
         float k = 1.0F - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
         this.renderFirstPersonItem(player, tickDelta, g, Hand.OFF_HAND, j, this.offHand, k, matrices, vertexConsumers, light);
      }
      vertexConsumers.draw();
      if (glassActive) {
         GlassHandsRenderer.getInstance().captureAfterHands(glassHands);
         GlassHandsRenderer.getInstance().renderOverlayIfPending(glassHands);
      }
   }
}
