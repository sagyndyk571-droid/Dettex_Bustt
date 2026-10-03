package zov.viola.module.list.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class HMIRenderer {
   private static double g8uwE = 0.0;
   public static double deltaTime = 0.0;
   private boolean vdq9b8 = false;
   private float r890 = 0.0F;
   private double lF11n7 = 0.0;
   private float y7kv = 0.0F;
   private float w0iC = 0.0F;
   private float pw5rZ = 0.0F;
   private float mXzuna = 0.0F;
   private float k5pjwK = 0.0F;
   private float s072srp = 0.0F;
   private float kgby2n = 0.0F;
   private float ji0l = 0.0F;
   private float e39g = 0.0F;
   private float aeCjqb6 = 0.0F;
   private float exWl = 0.0F;
   private float yOzM = 0.0F;
   private float rntWqg = 0.0F;
   private float owuiLYK = 0.0F;
   private float rM49 = 0.0F;
   private float crU9Bn = 0.0F;
   private float kZvP = 0.0F;
   private float jZurk48 = 1.0F;
   private boolean ufvo3 = false;
   private boolean x0zOvr = false;
   private final boolean[] a68sDn = new boolean[]{false, false};
   private final float[] t5cmmx = new float[]{0.0F, 0.0F};
   private final float[] bvq1lBl = new float[]{0.0F, 0.0F};
   private final float[] oh5jnui = new float[]{0.0F, 0.0F};
   private SwingAnimations dti475r;
   private static final boolean ENABLE_SWIMMING_ANIM = true;
   private static final boolean ENABLE_CLIMB_AND_CRAWL = true;
   private static final boolean ENABLE_PUNCHING = true;
   private static final boolean MB3D_COMPAT = false;

   private float getEffectivePitch(AbstractClientPlayerEntity player) {
      float pitch = player.getPitch();
      if (!player.isSwimming() && !player.isCrawling() && !player.isClimbing()) {
         boolean nearGround = player.isOnGround();
         if (!nearGround) {
            double feetY = player.getY();

            for (int i = 0; i <= 2; i++) {
               BlockPos checkPos = BlockPos.ofFloored(player.getX(), feetY - i, player.getZ());
               if (player.getWorld().getBlockState(checkPos).isSolid()) {
                  double dist = feetY - checkPos.getY() - 1.0;
                  if (dist <= 1.5) {
                     nearGround = true;
                  }
                  break;
               }
            }
         }

         if (!nearGround) {
            return pitch;
         } else {
            for (Entity entity : player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(6.0))) {
               double yDiff = entity.getY() - player.getY();
               if (yDiff < -1.9 || yDiff > 2.5) {
                  return pitch;
               }
            }

            return MathHelper.clamp(pitch, 35.0F, 48.0F);
         }
      } else {
         return pitch;
      }
   }

   public void setSwingAnimations(SwingAnimations sa) {
      this.dti475r = sa;
   }

   public void updateDeltaTime() {
      long currentTime = System.nanoTime();
      double currentSec = currentTime / 1.0E9;
      deltaTime = currentSec - g8uwE;
      g8uwE = currentSec;
      if (MinecraftClient.getInstance().isPaused()) {
         deltaTime = 0.0;
      } else {
         deltaTime = Math.min(0.05, deltaTime);
      }
   }

   private float pjZe(float x) {
      float c1 = 1.70158F;
      float c2 = c1 * 1.525F;
      return (float)(
         x < 0.5
            ? Math.pow(2.0 * x, 2.0) * ((c2 + 1.0) * 2.0 * x - c2) / 2.0
            : (Math.pow(2.0 * x - 2.0, 2.0) * ((c2 + 1.0) * (x * 2.0F - 2.0F) + c2) + 2.0) / 2.0
      );
   }

   private float wS6v(ItemStack stack) {
      float totalDamage = 0.0F;
      AttributeModifiersComponent modifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (modifiers == null) {
         return totalDamage;
      } else {
         for (Entry entry : modifiers.modifiers()) {
            if (entry.attribute().equals(EntityAttributes.ATTACK_DAMAGE)) {
               totalDamage += (float)entry.modifier().value();
            }
         }

         return totalDamage;
      }
   }

   private void t6caBh(MatrixStack matrices, Arm arm, float swingProgress) {
      int direction = arm == Arm.RIGHT ? 1 : -1;
      float swingSin = MathHelper.sin(swingProgress * (float) Math.PI);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(direction * (45.0F + swingSin * 0.0F)));
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(direction * -45.0F));
   }

   private boolean pJ0k6(ItemStack stack) {
      return stack.isIn(ItemTags.SWORDS) ? true : stack.getItem() instanceof SwordItem;
   }

   private boolean h0pGOmi(ItemStack stack) {
      return stack.isIn(ItemTags.AXES) || stack.isIn(ItemTags.HOES) || stack.isIn(ItemTags.PICKAXES) || stack.isIn(ItemTags.SHOVELS) || this.pJ0k6(stack);
   }

   private boolean hlr7CUy(ItemStack stack) {
      Block block = Block.getBlockFromItem(stack.getItem());
      BlockState state = block.getDefaultState();
      return stack.isOf(Items.STRING)
         || stack.isOf(Items.REDSTONE)
         || stack.isOf(Items.LEVER)
         || stack.isOf(Items.TRIPWIRE_HOOK)
         || state.isIn(BlockTags.IMPERMEABLE)
         || state.isIn(BlockTags.RAILS)
         || state.isIn(BlockTags.CLIMBABLE)
         || stack.isIn(ItemTags.DOORS);
   }

   public void renderHMI(
      HeldItemRenderer renderer,
      HeldItemRendererAccessor acc,
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack stack,
      float equipProgress,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light
   ) {
      this.updateDeltaTime();
      boolean useVanillaLike = stack.getUseAction() == UseAction.BOW || stack.getUseAction() == UseAction.SPEAR;
      if (useVanillaLike) {
         boolean bl = hand == Hand.MAIN_HAND;
         Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
         float kj = bl ? 1.0F : -1.0F;
         int l = arm == Arm.RIGHT ? 1 : -1;
         matrices.push();
         if (this.dti475r != null) {
            double vx = this.dti475r.hmiPosX.getValue();
            double vy = this.dti475r.hmiPosY.getValue();
            double vz = this.dti475r.hmiPosZ.getValue();
            matrices.translate(vx * kj, vy, vz);
         }

         if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
            UseAction useAction = stack.getUseAction();
            if (useAction == UseAction.BOW) {
               matrices.push();
               float m1 = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
               float f1 = m1 / 20.0F;
               if (f1 > 1.0F) {
                  f1 = 1.0F;
               }

               if (f1 > 0.1F) {
                  float g1 = MathHelper.sin((m1 - 0.1F) * 1.3F);
                  float j1 = g1 * f1;
                  matrices.translate(j1 * 0.0F, j1 * 0.004F, j1 * 0.0F);
               }

               acc.invokeApplyItemArmTransform(matrices, arm, equipProgress);
               matrices.translate(l * -0.2785682F, 0.18312F, 0.15731531F);
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935F));
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(l * 35.3F));
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(l * -9.785F));
               float h = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
               float g = h / 20.0F;
               g = (g * g + g * 2.0F) / 3.0F;
               if (g > 1.0F) {
                  g = 1.0F;
               }

               if (g > 0.1F) {
                  float i = MathHelper.sin((h - 0.1F) * 1.3F);
                  float j = g - 0.1F;
                  float k = i * j;
                  matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
               }

               matrices.translate(g * 0.0F, g * 0.0F, g * 0.04F);
               matrices.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
               matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(l * 45.0F));
               acc.invokeRenderItem(
                  player,
                  stack,
                  arm == Arm.RIGHT ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
                  arm != Arm.RIGHT,
                  matrices,
                  vertexConsumers,
                  light
               );
               matrices.pop();
            } else if (useAction == UseAction.SPEAR) {
               acc.invokeApplyItemArmTransform(matrices, arm, equipProgress);
               float dt = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
               float f = dt / 10.0F;
               if (f > 1.0F) {
                  f = 1.0F;
               }

               if (f > 0.1F) {
                  float gx = MathHelper.sin((dt - 0.1F) * 1.3F);
                  float j = gx * (f - 0.1F);
                  matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
               }

               matrices.translate(0.0F, 0.0F, 0.1F);
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10.0F));
               matrices.translate(0.0F, 0.0F, -0.1F);
               acc.invokeRenderItem(
                  player,
                  stack,
                  arm == Arm.RIGHT ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
                  arm != Arm.RIGHT,
                  matrices,
                  vertexConsumers,
                  light
               );
            }
         } else {
            acc.invokeSwingArm(swingProgress, equipProgress, matrices, bl ? 1 : -1, arm);
            acc.invokeRenderItem(
               player,
               stack,
               bl ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
               !bl,
               matrices,
               vertexConsumers,
               light
            );
         }

         matrices.pop();
      } else {
         float yaw = player.getYaw();
         double radians = Math.toRadians(yaw);
         double forwardX = -Math.sin(radians);
         double forwardZ = Math.cos(radians);
         Vec3d vel = player.getVelocity();
         double dotProduct = vel.x * forwardX + vel.z * forwardZ;
         double crossProduct = vel.x * forwardZ - vel.z * forwardX;
         float effectivePitch = this.getEffectivePitch(player);
         float al = effectivePitch != 0.0F ? 90.0F / effectivePitch / 10.0F : 1.0F;
         if (al > 1.0F) {
            al = 1.0F;
         }

         if (al < 0.0F) {
            al = 1.0F;
         }

         boolean blx = hand == Hand.MAIN_HAND;
         Arm armx = blx ? player.getMainArm() : player.getMainArm().getOpposite();
         float kjx = blx ? 1.0F : -1.0F;
         double vx = 0.0;
         double vy = 0.0;
         double vz = 0.0;
         double rx = 0.0;
         double ry = 0.0;
         double rz = 0.0;
         double scale = 1.0;
         double animSpeed = 8.0;
         if (this.dti475r != null) {
            vx = this.dti475r.hmiPosX.getValue();
            vy = this.dti475r.hmiPosY.getValue();
            vz = this.dti475r.hmiPosZ.getValue();
            rx = this.dti475r.hmiRotX.getValue();
            ry = this.dti475r.hmiRotY.getValue();
            rz = this.dti475r.hmiRotZ.getValue();
            scale = this.dti475r.hmiScale.getValue();
            animSpeed = 8.0;
         }

         matrices.push();
         matrices.push();
         matrices.translate(vx * kjx, vy, vz);
         if (rx != 0.0) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)rx));
         }

         if (ry != 0.0) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)ry * kjx));
         }

         if (rz != 0.0) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)rz * kjx));
         }

         if (scale != 1.0) {
            matrices.scale((float)scale, (float)scale, (float)scale);
         }

         double tt = deltaTime * animSpeed;
         float speedMultiplier = 1.0F;
         if (this.dti475r != null) {
            speedMultiplier = (float)(0.5 + this.dti475r.speed.getValue() / 10.0 * 2.0);
         }

         if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR
            && (
               !this.h0pGOmi(stack)
                  || stack.isIn(ItemTags.TRIMMABLE_ARMOR)
                  || stack.isIn(ItemTags.BOOKSHELF_BOOKS)
                  || stack.getUseAction() == UseAction.EAT
                  || !stack.isEnchantable()
            )
            && stack.getUseAction() != UseAction.BOW
            && stack.getUseAction() != UseAction.SPYGLASS
            && this.wS6v(stack) == 0.0F
            && stack.getUseAction() != UseAction.BLOCK
            && !stack.isOf(Items.WARPED_FUNGUS_ON_A_STICK)
            && !stack.isOf(Items.CARROT_ON_A_STICK)
            && !(stack.getItem() instanceof FishingRodItem)
            && !stack.isOf(Items.SHEARS)) {
            swingProgress = (float)(swingProgress * 0.45 * speedMultiplier);
            if (swingProgress > 1.0F) {
               swingProgress = 0.0F;
            }
         } else if (!stack.isIn(ItemTags.SHOVELS)) {
            swingProgress = (float)(swingProgress * 0.45 * speedMultiplier);
            if (swingProgress > 1.0F) {
               swingProgress = 0.0F;
            }
         }

         float raw_swing_rot = swingProgress < 0.6
            ? MathHelper.sin(MathHelper.clamp(swingProgress, 0.0F, 0.12506F) * 12.56F)
            : MathHelper.sin(MathHelper.clamp(swingProgress, 0.62532F, 0.75038F) * 12.56F);
         float raw_swing = MathHelper.sin(swingProgress * 3.14F);
         raw_swing = this.pjZe(raw_swing);
         float swing_rot = raw_swing_rot;
         float swing = raw_swing;
         int handIdx = blx ? 0 : 1;
         if (swingProgress > 0.001F) {
            this.a68sDn[handIdx] = true;
            this.bvq1lBl[handIdx] = raw_swing;
            this.oh5jnui[handIdx] = raw_swing_rot;
            this.t5cmmx[handIdx] = 1.0F;
         } else if (this.a68sDn[handIdx] && this.t5cmmx[handIdx] > 0.0F) {
            this.t5cmmx[handIdx] = this.t5cmmx[handIdx] - (float)(deltaTime * 5.5);
            if (this.t5cmmx[handIdx] < 0.0F) {
               this.t5cmmx[handIdx] = 0.0F;
            }

            float t = this.t5cmmx[handIdx] * this.t5cmmx[handIdx];
            swing = this.bvq1lBl[handIdx] * t;
            swing_rot = this.oh5jnui[handIdx] * t;
         } else {
            this.a68sDn[handIdx] = false;
            this.t5cmmx[handIdx] = 0.0F;
         }

         if ((
               stack.isOf(Items.EXPERIENCE_BOTTLE)
                  || stack.isOf(Items.EGG)
                  || stack.isOf(Items.ENDER_EYE)
                  || stack.isOf(Items.SNOWBALL)
                  || stack.isOf(Items.ENDER_PEARL)
                  || stack.getItem() instanceof SplashPotionItem
                  || stack.getItem() instanceof LingeringPotionItem
            )
            && player.getOffHandStack().isEmpty()
            && stack.getUseAction() != UseAction.SPEAR
            && !stack.isOf(Items.FIRE_CHARGE)
            && !player.isSwimming()
            && !player.isCrawling()
            && !player.isClimbing()) {
            if (player.getMainArm() == Arm.LEFT) {
               blx = !blx;
            }

            float ll = blx ? 1.0F : -1.0F;
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-25.0F * ll));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25.0F * ll * swing));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F * swing));
            matrices.translate(-0.15 * ll, 0.1, 0.1);
            matrices.translate(0.0, -0.55 * swing, 0.4 * swing * 3.14F);
            acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, 0.0F, armx.getOpposite());
            matrices.pop();
         }

         if (this.a68sDn[handIdx] && this.t5cmmx[handIdx] <= 0.0F) {
            this.x0zOvr = !this.x0zOvr;
         }

         if (!stack.isEmpty()) {
            if (player.getMainArm() == Arm.LEFT) {
               blx = !blx;
            }

            float ll = blx ? 1.0F : -1.0F;
            if ((this.x0zOvr || stack.isIn(ItemTags.AXES) || stack.getUseAction() == UseAction.SPEAR || stack.getUseAction() == UseAction.BLOCK)
               && !stack.isIn(ItemTags.SHOVELS)) {
               if (!this.pJ0k6(stack) && !stack.isIn(ItemTags.AXES)) {
                  if (stack.getUseAction() == UseAction.SPEAR) {
                     matrices.translate(0.0, 0.0, 0.45 * swing_rot);
                     matrices.translate((double)(-0.25F * kjx * swing), -0.35 * swing_rot, -0.6 * swing);
                     matrices.translate(0.0, 0.1 * swing, 0.0);
                     matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(15.0F * swing_rot * ll));
                     matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(30.0F * swing_rot * ll));
                  } else if (this.h0pGOmi(stack) && stack.getUseAction() != UseAction.BLOCK && !stack.isIn(ItemTags.SHOVELS)) {
                     matrices.translate(0.1 * ll * swing_rot, 0.1 * swing_rot, (double)(-0.5F * swing));
                     matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-30.0F * swing_rot));
                     matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0F * swing_rot * ll));
                     matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(40.0F * swing));
                  } else if (stack.getUseAction() != UseAction.BLOCK) {
                     matrices.translate(0.1 * ll * swing_rot, 0.1 * swing_rot, -0.1 * swing);
                     matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-30.0F * swing_rot));
                     matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-10.0F * swing_rot * ll));
                     matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(40.0F * swing));
                     matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(10.0F * swing * ll));
                  } else {
                     matrices.translate(0.1 * ll * swing_rot, 0.1 * swing_rot, -0.2 * swing);
                     matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-10.0F * swing_rot));
                     matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-10.0F * swing_rot * ll));
                     matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(20.0F * swing));
                  }
               } else {
                  matrices.translate(0.8 * ll * swing_rot, 0.3 * swing_rot, (double)(-0.5F * swing));
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(15.0F * swing_rot * ll));
                  matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-20.0F * swing_rot));
                  matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-70.0F * swing_rot * ll));
                  if (this.pJ0k6(stack)) {
                     matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(40.0F * swing));
                  } else {
                     matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(30.0F * swing));
                  }
               }
            } else if (!stack.isIn(ItemTags.SHOVELS)) {
               if (this.pJ0k6(stack)) {
                  matrices.translate(-0.55 * ll * swing_rot, -0.8 * swing_rot, -0.77 * swing);
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(5.0F * swing_rot * ll));
                  matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-30.0F * swing_rot));
                  matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(70.0F * swing_rot * ll));
                  matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(50.0F * swing));
               } else if (this.h0pGOmi(stack) && !stack.isIn(ItemTags.SHOVELS)) {
                  matrices.translate(0.1 * ll * swing_rot, 0.1 * swing_rot, (double)(-0.5F * swing));
                  matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-30.0F * swing_rot));
                  matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0F * swing_rot * ll));
                  matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(40.0F * swing));
               } else {
                  matrices.translate(0.1 * ll * swing_rot, 0.1 * swing_rot, -0.1 * swing);
                  matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(-30.0F * swing_rot));
                  matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-10.0F * swing_rot * ll));
                  matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(40.0F * swing));
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(10.0F * swing * ll));
               }
            } else if (stack.isIn(ItemTags.SHOVELS)) {
               matrices.translate(0.0, 0.15 * swing_rot, (double)(-0.25F * swing_rot));
               matrices.translate(0.0, 0.0, -0.2 * swing);
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(15.0F * swing_rot));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-35.0F * swing_rot));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F * swing));
            }
         }

         if (vel.length() >= 0.08) {
            this.rM49 = (float)(this.rM49 + 0.1 * vel.length() * 2.0 * tt);
            this.crU9Bn = (float)(this.crU9Bn + 0.1 * dotProduct * 4.0 * tt);
            this.crU9Bn = (float)(this.crU9Bn + (dotProduct > 0.0 ? 0.1 * Math.abs(crossProduct) * 4.0 * tt : 0.1 * Math.abs(crossProduct) * -1.0 * 4.0 * tt));
         }

         if (vel.y > 0.0) {
            this.kZvP = (float)(this.kZvP + 0.1 * tt);
         }

         if (vel.y < 0.0) {
            this.kZvP = (float)(this.kZvP - 0.1 * tt);
         }

         if ((player.isCrawling() || player.isClimbing() && !player.isOnGround() && Math.abs(vel.y) > 0.0) && !player.isUsingItem() && swingProgress == 0.0F) {
            this.owuiLYK = (float)(this.owuiLYK + 0.1 * tt);
            if (this.owuiLYK > 1.0F) {
               this.owuiLYK = 1.0F;
            }

            if (!stack.isOf(Items.LANTERN) && !stack.isOf(Items.SOUL_LANTERN)) {
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-20.0F * this.owuiLYK));
            }
         } else {
            this.owuiLYK = (float)(this.owuiLYK * Math.pow(0.88F, tt));
         }

         if (swingProgress == 0.0F) {
            matrices.translate(blx ? effectivePitch / 650.0F * this.owuiLYK * -1.0F : effectivePitch / 650.0F * this.owuiLYK, 0.0F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(effectivePitch * this.owuiLYK));
         }

         if (!stack.isOf(Items.LANTERN) && !stack.isOf(Items.SOUL_LANTERN)) {
            matrices.translate(0.0F, 0.0F, effectivePitch / 120.0F * this.owuiLYK);
         } else if (swingProgress == 0.0F) {
            matrices.translate(0.0F, 0.0F, effectivePitch / 80.0F * this.owuiLYK);
         }

         if (player.isClimbing() && !player.isOnGround() && !stack.isOf(Items.LANTERN) && !stack.isOf(Items.SOUL_LANTERN) && !player.isUsingItem()) {
            matrices.translate(0.0, 0.1, -0.2);
         }

         if ((player.isTouchingWater() || player.isFrozen()) && !player.isSwimming() && !player.isSubmergedInWater()) {
            this.yOzM = (float)(this.yOzM + 0.1 * tt);
            if (this.yOzM >= 1.0F) {
               this.yOzM = 1.0F;
            }
         } else {
            this.yOzM = (float)(this.yOzM * Math.pow(0.88F, tt));
         }

         float freezingScale = MathHelper.clamp((float)player.getFrozenTicks() / player.getMinFreezeDamageTicks(), 0.0F, 1.0F);
         if (player.isFrozen() && freezingScale > 0.1) {
            this.rntWqg = (float)(this.rntWqg + 0.1 * tt);
         } else {
            this.rntWqg = (float)(this.rntWqg * Math.pow(0.88F, tt));
         }

         matrices.translate(0.0, 0.02 * this.yOzM, 0.0);
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(8.0F * kjx * this.yOzM));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.3F * MathHelper.sin(this.rntWqg * 5.0F)));
         if (vel.y < -0.85 && stack.isOf(Items.MACE) && player.getMainHandStack() == stack) {
            this.exWl = (float)(this.exWl + 0.1 * tt);
            if (this.exWl >= 1.0F) {
               this.exWl = 1.0F;
            }
         } else {
            this.exWl = (float)(this.exWl * Math.pow(0.88F, tt));
         }

         if (blx) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F * this.exWl));
            matrices.translate(0.0, -0.2 * this.exWl, 0.0);
         }

         this.s072srp = (float)(this.s072srp + vel.y * 0.015F * tt);
         this.s072srp = (float)(this.s072srp - 0.1F * this.s072srp * tt);
         this.s072srp = (float)(this.s072srp * Math.pow(0.88F, tt));
         this.kgby2n = (float)(this.kgby2n + vel.y * 0.015F * tt);
         this.kgby2n = (float)(this.kgby2n - 0.1F * this.ji0l * tt);
         this.kgby2n = (float)(this.kgby2n * Math.pow(0.88F, tt));
         this.ji0l = (float)(this.ji0l + this.kgby2n * tt);
         matrices.translate(0.0F, this.s072srp * -1.0F, 0.0F);
         matrices.translate(0.0, Math.sin(player.age * 0.1) * 0.007 * kjx, 0.0);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0.15F * MathHelper.sin(player.age * 0.15F) * kjx));
         if (!stack.isEmpty() || player.isCrawling() || player.isClimbing() && !player.isOnGround() || player.isSwimming()) {
            if (player.getMainArm() == Arm.LEFT) {
               blx = !blx;
            }

            if (stack.getUseAction() == UseAction.BLOCK) {
               matrices.translate(0.0F, 0.0F, 0.0F);
            } else {
               matrices.translate(0.0, -0.1, 0.1);
            }
         }

         if (stack.isOf(Items.LANTERN) || stack.isOf(Items.SOUL_LANTERN) || stack.isIn(ItemTags.HANGING_SIGNS)) {
            matrices.translate(0.0, 0.1, 0.0);
            if (player.isSwimming()) {
               matrices.translate(0.0, -0.1, 0.1);
            }
         }

         if (player.isSwimming() && swingProgress == 0.0F) {
            double s = (player.age + tickDelta) * 0.1 * 2.0;
            double handRotation = Math.sin(s) * 1.5;
            double smoothRotation = handRotation * 0.8 + this.lF11n7 * 0.2;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(blx ? smoothRotation : -smoothRotation)));
            matrices.translate(0.0F, 0.0F, (float)(smoothRotation * 0.2));
            double k = (player.age + tickDelta) * 0.2;
            double a = Math.cos(k);
            double b = a <= 0.0 ? a * 0.5 : a;
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)(blx ? b * 30.0 : b * -30.0)));
            matrices.translate(0.0F, 0.0F, (float)(a * 0.2));
            if (stack.isEmpty() && !blx && !player.isInvisible()) {
               float j1 = blx ? 1.0F : -1.0F;
               matrices.translate((double)j1, 0.0 - equipProgress * 0.3, 0.3);
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F * j1));
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40.0F * j1));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F));
               this.t6caBh(matrices, armx, swingProgress);
               matrices.scale(0.9F, 0.9F, 0.9F);
               acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, 0.0F, 0.0F, armx);
            }

            this.lF11n7 = smoothRotation;
         }

         if ((player.isClimbing() && !player.isOnGround() || player.isCrawling() && swingProgress == 0.0F) && !player.isUsingItem()) {
            double s = (player.age + tickDelta) * 0.1;
            float hx = MathHelper.cos((float)s * 2.0F);
            float j = blx ? 1.0F : -1.0F;
            if (player.isClimbing()) {
               if (!stack.isOf(Items.LANTERN) && !stack.isOf(Items.SOUL_LANTERN)) {
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20.0F * hx * j));
               } else {
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(1.0F * hx * j));
               }
            }

            if (player.isCrawling() && !player.isUsingItem() && swingProgress == 0.0F) {
               float timeValue = (player.age + tickDelta) * 0.4F;
               float lx = MathHelper.sin(timeValue * this.jZurk48);
               float dtx = MathHelper.cos(timeValue * this.jZurk48);
               if (stack.isOf(Items.LANTERN) || stack.isOf(Items.SOUL_LANTERN)) {
                  lx *= 0.14F;
                  dtx *= 0.14F;
               }

               matrices.translate(0.2 * lx, 0.3 * lx * j, -0.2 * lx * j * al);
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25.0F * lx));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.clamp(20.0F * dtx * j, 0.0F, 20.0F)));
            }

            if (stack.isEmpty() && !blx && !player.isInvisible() && (!player.isOnGround() && player.isClimbing() || player.isCrawling())) {
               float lx = blx ? 1.0F : -1.0F;
               matrices.translate((double)lx, 0.0 - equipProgress * 0.3, 0.3);
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F * lx));
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40.0F * lx));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F));
               this.t6caBh(matrices, armx, swingProgress);
               matrices.scale(0.9F, 0.9F, 0.9F);
               acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, 0.0F, 0.0F, armx);
            }
         }

         if (stack.isEmpty()) {
            if (blx && !player.isInvisible()) {
               float ll = blx ? 1.0F : -1.0F;
               if ((player.isOnGround() || !player.isClimbing()) && !player.isSwimming() && !player.isCrawling()) {
                  if (player.getMainArm() == Arm.LEFT) {
                     blx = !blx;
                  }

                  matrices.translate(0.0, 0.2 * swing_rot, 0.15 * swing_rot);
                  matrices.translate(0.1 * ll * swing, 0.15 * swing, -0.45 * swing);
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(35.0F * swing * ll));
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-30.0F * swing));
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-10.0F * swing_rot * ll));
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10.0F * swing_rot));
                  acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, 0.0F, 0.0F, armx);
               } else {
                  matrices.translate((double)ll, 0.0 - equipProgress * 0.3, 0.3);
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F * ll));
                  matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40.0F * ll));
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F));
                  this.t6caBh(matrices, armx, swingProgress);
                  matrices.scale(0.9F, 0.9F, 0.9F);
                  acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, 0.0F, 0.0F, armx);
               }
            }
         } else if (stack.contains(DataComponentTypes.MAP_ID)) {
            if (blx && acc.getOffHand().isEmpty()) {
               matrices.translate(0.0, 0.1, 0.0);
               acc.invokeRenderTwoHandedMap(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
            } else {
               matrices.translate(blx ? -0.1F : 0.1F, 0.1F, 0.0F);
               acc.invokeRenderOneHandedMap(matrices, vertexConsumers, light, equipProgress, armx, swingProgress, stack);
            }
         } else if (stack.getUseAction() == UseAction.CROSSBOW) {
            this.s6feN(acc, player, tickDelta, pitch, hand, swingProgress, stack, equipProgress, matrices, vertexConsumers, light, blx, armx, kjx);
         } else {
            this.g53zWU(
               acc, player, tickDelta, pitch, hand, swingProgress, stack, equipProgress, matrices, vertexConsumers, light, blx, armx, kjx, tt, swing, swing_rot
            );
         }

         matrices.pop();
         matrices.pop();
         this.ufvo3 = MinecraftClient.getInstance().options.attackKey.isPressed();
      }
   }

   private void s6feN(
      HeldItemRendererAccessor acc,
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack stack,
      float equipProgress,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      boolean bl,
      Arm arm,
      float kj
   ) {
      matrices.push();
      boolean bl2 = arm == Arm.RIGHT;
      int i = bl2 ? 1 : -1;
      if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
         acc.invokeApplyItemArmTransform(matrices, arm, equipProgress);
         matrices.translate(i * -0.4785682F, -0.24387F, 0.05731531F);
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935F));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 65.3F));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * 9.785F));
         float f = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
         float g = f / CrossbowItem.getPullTime(stack, player);
         if (g > 1.0F) {
            g = 1.0F;
         }

         if (g > 0.1F) {
            float h = MathHelper.sin((f - 0.1F) * 1.3F);
            float yawDelta = h * (g - 0.1F);
            matrices.translate(yawDelta * 0.0F, yawDelta * 0.004F, yawDelta * 0.0F);
         }

         matrices.translate(g * 0.0F, g * 0.0F, g * 0.04F);
         matrices.scale(1.0F, 1.0F, 1.0F);
         matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(i * 45.0F));
      } else {
         acc.invokeSwingArm(swingProgress, equipProgress, matrices, i, arm);
         if (CrossbowItem.isCharged(stack) && swingProgress < 0.001F && bl) {
            matrices.translate(i * -0.341864F, 0.0F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * 10.0F));
         }
      }

      float yawDelta = bl ? 1.0F : -1.0F;
      matrices.translate(0.0F, 0.0F, -1.0F);
      matrices.translate(-0.45 * i, 0.45, 1.7);
      matrices.translate((double)yawDelta, 0.0 - equipProgress * 0.3, 0.3);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F * yawDelta));
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40.0F * yawDelta));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F));
      this.t6caBh(matrices, arm, swingProgress);
      matrices.scale(0.9F, 0.9F, 0.9F);
      acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, 0.0F, 0.0F, arm);
      matrices.translate((double)(-0.25F * i), 1.25, 0.05);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90 * i));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(77.0F));
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(85 * i));
      matrices.scale(1.2F, 1.2F, 1.2F);
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10.0F));
      matrices.translate(0.0, -0.15, 0.15);
      acc.invokeRenderItem(
         player,
         stack,
         bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
         !bl2,
         matrices,
         vertexConsumers,
         light
      );
      matrices.pop();
      if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
         float fx = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
         float gx = fx / CrossbowItem.getPullTime(stack, player);
         if (gx > 1.0F) {
            gx = 1.0F;
         }

         if (gx > 0.1F) {
            float h = MathHelper.sin((fx - 0.1F) * 1.3F);
            float k = h * (gx - 0.1F);
            matrices.translate(k * 0.0F, k * 0.004F, k * 0.0F);
         }

         matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(gx <= 0.2 ? 75.0F * gx * 5.0F * i : 75 * i));
         matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(10.0F * gx * 1.5F));
         matrices.translate(-0.37 * i, 0.0, 0.6);
         matrices.translate(0.15 * gx * i, 0.0, 0.0);
         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm.getOpposite());
      }
   }

   private void g53zWU(
      HeldItemRendererAccessor acc,
      AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack stack,
      float equipProgress,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      boolean bl,
      Arm arm,
      float kj,
      double tt,
      float swing,
      float swing_rot
   ) {
      boolean bl2 = arm == Arm.RIGHT;
      int l = bl2 ? 1 : -1;
      if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
         this.jhsoBFx(acc, player, tickDelta, hand, swingProgress, stack, equipProgress, matrices, vertexConsumers, light, bl, arm, kj, l, tt, swing, swing_rot);
      } else if (player.isUsingRiptide() && stack.getUseAction() == UseAction.SPEAR) {
         this.e39g = (float)(this.e39g + 0.15 * tt);
         float dt = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
         float f = dt / 10.0F;
         if (f > 1.0F) {
            f = 1.0F;
         }

         if (f > 0.1F) {
            float g = MathHelper.sin((dt - 0.1F) * 1.3F);
            float j = g * (f - 0.1F);
            matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
         }

         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F - this.e39g * 2.0F));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25 * l));
         matrices.translate(0.2 * l, 0.0, 0.75);
         matrices.translate(0.0, 0.0, 0.01 * MathHelper.sin(this.e39g * 6.28F));
         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(135.0F));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-65 * l));
         matrices.translate((double)(0.65F * l), -1.0, -0.6);
      } else {
         this.e39g = 0.0F;
         if (!stack.isOf(Items.LANTERN) && !stack.isOf(Items.SOUL_LANTERN) && !stack.isIn(ItemTags.HANGING_SIGNS)) {
            if (stack.getUseAction() == UseAction.BLOCK) {
               matrices.translate(0.0, -0.2, 0.0);
            }
         } else {
            matrices.translate(0.1 * l, 0.0, -0.1);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10.0F));
         }

         matrices.translate((double)l, 0.0 - equipProgress * 0.3, 0.3);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45 * l));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40 * l));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F));
         this.t6caBh(matrices, arm, swingProgress);
         matrices.scale(0.9F, 0.9F, 0.9F);
         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, 0.0F, 0.0F, arm);
      }

      if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand && stack.getUseAction() == UseAction.BOW) {
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(75.0F));
         matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(-15 * l));
         matrices.translate(0.8 * l, (double)(0.0F - equipProgress * 0.3F), -0.1);
      } else {
         matrices.translate(-0.3 * l, 0.65, -0.1);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-65 * l));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(10.0F));
      }

      if (stack.getItem() instanceof BlockItem && !stack.isIn(ItemTags.TRIMMABLE_ARMOR) && !stack.isIn(ItemTags.BOOKSHELF_BOOKS)) {
         this.siS2i(acc, player, stack, matrices, vertexConsumers, light, bl, bl2, l, arm, tt, swing, swing_rot, equipProgress, swingProgress);
      } else {
         boolean isToolOrWeapon = this.h0pGOmi(stack)
               && !stack.isIn(ItemTags.TRIMMABLE_ARMOR)
               && !stack.isIn(ItemTags.BOOKSHELF_BOOKS)
               && stack.getUseAction() != UseAction.EAT
               && stack.isEnchantable()
            || stack.getUseAction() == UseAction.BOW
            || stack.getUseAction() == UseAction.SPYGLASS
            || this.wS6v(stack) != 0.0F
            || stack.getUseAction() == UseAction.BLOCK
            || stack.isOf(Items.WARPED_FUNGUS_ON_A_STICK)
            || stack.isOf(Items.CARROT_ON_A_STICK)
            || stack.getItem() instanceof FishingRodItem
            || stack.isOf(Items.SHEARS);
         if (isToolOrWeapon) {
            if (this.pJ0k6(stack)) {
               matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(75 * l));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(70.0F));
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45 * l));
               matrices.scale(1.2F, 1.2F, 1.2F);
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-60.0F * swing));
               matrices.translate(0.0, 0.1 * swing, -0.1 * swing);
            } else if (stack.getUseAction() == UseAction.SPEAR) {
               matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(75 * l));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45 * l));
               matrices.translate(-0.3F * l, 0.0F, 0.0F);
               matrices.scale(1.2F, 1.2F, 1.2F);
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-40.0F * swing_rot));
               matrices.translate(0.0, 0.1 * swing_rot, -0.1 * swing_rot);
            } else if (stack.getUseAction() == UseAction.BOW) {
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(160 * l));
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-60 * l));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-70.0F));
               matrices.scale(0.75F, 0.75F, 0.75F);
               matrices.translate(0.15 * l, bl ? 0.35F : 0.45F, bl ? -0.15F : -0.1F);
               matrices.translate(0.17 * l, 0.0, 0.3);
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90 * l));
            } else if (stack.getUseAction() == UseAction.BLOCK) {
               matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(-154 * l));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(5.0F));
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90 * l));
               matrices.translate((double)(-0.012F * l), 0.38, -0.175);
               matrices.scale(0.55F, 0.55F, 0.55F);
            } else if (stack.isIn(ItemTags.SHOVELS)) {
               matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(75 * l));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(70.0F));
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45 * l));
               matrices.scale(1.2F, 1.2F, 1.2F);
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F * swing_rot));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F * swing));
               matrices.translate(0.07 * l, 0.0, 0.05);
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * l));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-15.0F));
            } else {
               matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(75 * l));
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(70.0F));
               matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45 * l));
               matrices.scale(1.2F, 1.2F, 1.2F);
               matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-25.0F * swing));
               matrices.translate(0.0, 0.05 * swing, -0.05 * swing);
            }
         } else {
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(5 * l));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(75 * l));
            matrices.translate(-0.015 * l, -0.055, -0.06);
            matrices.scale(0.7F, 0.7F, 0.7F);
         }

         if (!stack.isOf(Items.NETHER_STAR)) {
            if (stack.isOf(Items.END_CRYSTAL)) {
            }

            this.aeCjqb6 = 0.0F;
         } else {
            this.aeCjqb6 = (float)(this.aeCjqb6 + 0.9 * tt);
            matrices.translate(0.0, 0.25 + 0.02 * MathHelper.sin(this.aeCjqb6 * 0.1F), 0.0);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(3.0F * MathHelper.sin(this.aeCjqb6 * 0.2F)));
            float ns = 1.0F + 0.01F * MathHelper.sin(this.aeCjqb6);
            matrices.scale(ns, ns, ns);
         }

         acc.invokeRenderItem(
            player,
            stack,
            bl2 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            !bl2,
            matrices,
            vertexConsumers,
            light
         );
      }
   }

   private void jhsoBFx(
      HeldItemRendererAccessor acc,
      AbstractClientPlayerEntity player,
      float tickDelta,
      Hand hand,
      float swingProgress,
      ItemStack stack,
      float equipProgress,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      boolean bl,
      Arm arm,
      float kj,
      int l,
      double tt,
      float swing,
      float swing_rot
   ) {
      UseAction useAction = stack.getUseAction();
      if (useAction == UseAction.NONE) {
         acc.invokeApplyItemArmTransform(matrices, arm, equipProgress);
      } else if (useAction == UseAction.EAT || useAction == UseAction.DRINK) {
         float yawDelta = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
         float pitchDelta = yawDelta / 5.0F;
         if (pitchDelta > 1.0F) {
            pitchDelta = 1.0F;
         }

         float k = MathHelper.sin(yawDelta / 2.0F * 3.14F) / 10.0F;
         matrices.translate((double)l, 0.1, 0.3);
         matrices.translate(0.2 * l * pitchDelta, -0.7 * pitchDelta, -0.2 * pitchDelta);
         matrices.translate(0.0, -0.2 * k, -0.2 * k);
         matrices.translate(0.0F, 0.1F * this.pjZe(MathHelper.sin(pitchDelta * 3.14F)), 0.0F);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45 * l));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40 * l));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F));
         this.t6caBh(matrices, arm, swingProgress);
         matrices.scale(0.9F, 0.9F, 0.9F);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F * pitchDelta * l));
         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, 0.0F, swingProgress, arm);
      } else if (useAction == UseAction.BLOCK) {
         float k = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
         double s = k / 4.0F;
         float s2 = k / 6.0F;
         if (s > 1.0) {
            s = 1.0;
         }

         if (s2 > 1.0F) {
            s2 = 1.0F;
         }

         matrices.translate(0.0, -0.2, 0.0);
         matrices.translate((double)l, 0.0, 0.3);
         matrices.translate(0.7 * s * l, 0.0, -1.3 * s);
         matrices.translate(-0.2 * l * s2, 0.0, 0.0);
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)(15.0 * Math.sin(s2 * 3.14))));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(70.0 * s * l)));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45 * l));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40 * l));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0F));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(5 * l * (float)s));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)(-10.0 * s)));
         matrices.translate(0.0, 0.0, -0.2 * s);
         this.t6caBh(matrices, arm, swingProgress);
         matrices.scale(0.9F, 0.9F, 0.9F);
         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, 0.0F, swingProgress, arm);
         if (l == 1) {
            matrices.translate(0.19F, 0.1, -0.24);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(4.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10.4F));
            matrices.translate(-0.2, -0.04, 0.15);
         } else {
            matrices.translate(-0.19F, 0.1, -0.18);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-4.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10.4F));
            matrices.translate(0.2, -0.04, 0.15);
         }
      } else if (useAction == UseAction.BOW) {
         matrices.push();
         float m1 = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
         float f1 = m1 / 20.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F;
         }

         if (f1 > 0.1F) {
            float g1 = MathHelper.sin((m1 - 0.1F) * 1.3F);
            float j1 = g1 * f1;
            matrices.translate(j1 * 0.0F, j1 * 0.004F, j1 * 0.0F);
         }

         matrices.push();
         matrices.translate(bl ? -0.1F : 0.1F, 0.0F, f1 * 0.15F);
         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
         matrices.pop();
         matrices.translate(bl ? -0.5 : 0.5, -0.45, 0.1);
         matrices.multiply(RotationAxis.POSITIVE_X.rotation(0.3F));
         if (bl) {
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotation(-0.3F));
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotation(1.0F));
         } else {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(-0.3F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(1.0F));
         }

         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm.getOpposite());
         if (bl) {
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotation(2.5F));
         } else {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(2.5F));
         }

         matrices.translate(bl ? -0.65 : 0.65, -0.35, 0.27);
         matrices.pop();
      } else if (useAction == UseAction.SPEAR) {
         if (player.getOffHandStack().isEmpty() && !player.isCrawling() && !player.isSwimming() && !player.isClimbing()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-25 * l));
            matrices.translate(-0.15 * l, 0.1, 0.1);
            acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm.getOpposite());
            matrices.pop();
         }

         float dt = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
         float f = dt / 10.0F;
         if (f > 1.0F) {
            f = 1.0F;
         }

         if (f > 0.1F) {
            float g = MathHelper.sin((dt - 0.1F) * 1.3F);
            float j = g * (f - 0.1F);
            matrices.translate(j * 0.0F, j * 0.004F, j * 0.0F);
         }

         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25 * l));
         matrices.translate(0.2 * l, 0.0, 0.8);
         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(100.0F));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-55 * l));
         matrices.translate((double)(0.5F * l), -0.7, -0.35);
      } else if (useAction == UseAction.SPYGLASS) {
         float g1 = player.getItemUseTimeLeft() % 10;
         float h1 = g1 - tickDelta + 1.0F;
         float j1 = 1.0F - h1 / 10.0F;
         float n = -15.0F + 75.0F * MathHelper.cos(j1 * 2.0F * (float) Math.PI);
         float z = stack.getMaxUseTime(player) - (player.getItemUseTimeLeft() - tickDelta + 1.0F);
         float x = z / 4.0F;
         if (x > 1.0F) {
            x = 1.0F;
         }

         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25 * l * x));
         matrices.translate((double)(0.3F * l * x), 0.3 * x, 0.1 * x);
         if (x == 1.0F) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(n / 20.0F));
         }

         acc.invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
      }
   }

   private void siS2i(
      HeldItemRendererAccessor acc,
      AbstractClientPlayerEntity player,
      ItemStack stack,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      boolean bl,
      boolean bl2,
      int l,
      Arm arm,
      double tt,
      float swing,
      float swing_rot,
      float equipProgress,
      float swingProgress
   ) {
      Block block = Block.getBlockFromItem(stack.getItem());
      if (block != Blocks.AIR) {
         BlockState state = block.getDefaultState();
         if (this.hlr7CUy(stack)) {
            matrices.translate(0.0, 0.0, -0.1);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(5 * l));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(15.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(75 * l));
         } else if (stack.getName().getString().toLowerCase().contains("torch")) {
            matrices.scale(1.5F, 1.5F, 1.5F);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(25 * l));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(5.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(75 * l));
            matrices.translate(0.2 * l, 0.2, 0.05);
         } else if (!stack.isOf(Items.LANTERN) && !stack.isOf(Items.SOUL_LANTERN) && !stack.isIn(ItemTags.HANGING_SIGNS)) {
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(25 * l));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(5.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(75 * l));
            matrices.translate(0.2 * l, 0.2, 0.05);
         } else {
            float dt = (float)(deltaTime * 8.0);
            float yawDelta = player.prevYaw - player.getYaw();
            float pitchDelta = player.prevPitch - player.getPitch();
            this.pw5rZ += yawDelta * 0.015F * dt;
            this.pw5rZ += swingProgress * 2.0F * dt;
            this.mXzuna += pitchDelta * 0.015F * dt;
            this.pw5rZ = this.pw5rZ - 0.1F * this.y7kv * dt;
            this.mXzuna = this.mXzuna - 0.1F * this.w0iC * dt;
            this.pw5rZ = (float)(this.pw5rZ * Math.pow(0.88F, dt));
            this.mXzuna = (float)(this.mXzuna * Math.pow(0.88F, dt));
            this.y7kv = this.y7kv + this.pw5rZ * dt;
            this.w0iC = this.w0iC + this.mXzuna * dt;
            double currentSpeed = player.getVelocity().length();
            this.k5pjwK = (float)(this.k5pjwK + (bl ? currentSpeed * -15.0 - this.k5pjwK : currentSpeed * 15.0 - this.k5pjwK) * 0.1F * dt);
            if ((currentSpeed > 0.09 && player.isOnGround() || player.isSwimming() || player.isClimbing() && !player.isOnGround())
               && MinecraftClient.getInstance().options.getBobView().getValue()) {
               boolean randomBool = Math.random() > 0.5;
               this.pw5rZ = (float)(this.pw5rZ + (randomBool ? -5.5 * currentSpeed * dt : 5.5 * currentSpeed * dt));
            }

            matrices.translate(0.0, 0.0, -0.1);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(35 * l + this.y7kv));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(15.0F + this.w0iC));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(75 * l + this.k5pjwK));
            if (stack.isIn(ItemTags.HANGING_SIGNS)) {
               matrices.translate(0.0, -0.1, 0.0);
               matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45 * l));
            }

            matrices.translate(0.3 * l, -0.35, 0.0);
            matrices.translate(0.0, 0.0, 0.1);
            matrices.scale(1.5F, 1.5F, 1.5F);
         }

         BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
         matrices.push();
         if (!bl2) {
            matrices.translate(-0.4F, 0.0F, 0.0F);
         }

         matrices.scale(0.35F, 0.35F, 0.35F);
         matrices.translate(-0.9 * l, -0.45, -0.5);
         if (state.isIn(BlockTags.BUTTONS)) {
            matrices.translate(0.2 * l, -0.15, -0.2);
         }

         if (state.isIn(BlockTags.PRESSURE_PLATES)) {
            matrices.translate(0.0, 0.1, 0.0);
         }

         if (stack.isOf(Items.SLIME_BLOCK)
            || stack.isOf(Items.HONEY_BLOCK)
            || state.isIn(BlockTags.FLOWERS)
            || state.isIn(BlockTags.LEAVES)
            || state.isIn(BlockTags.SAPLINGS)
            || state.isIn(BlockTags.SWORD_EFFICIENT)) {
            this.kgby2n = (float)(this.kgby2n + swingProgress * 0.03 * deltaTime * 8.0);
            if ((
                  player.getVelocity().length() > 0.09 && player.isOnGround()
                     || player.isSwimming()
                     || player.isCrawling()
                     || player.isClimbing() && !player.isOnGround()
               )
               && MinecraftClient.getInstance().options.getBobView().getValue()) {
               this.kgby2n = (float)(this.kgby2n + -0.05F * player.getVelocity().length() * deltaTime * 8.0);
            }

            matrices.scale(1.0F, 1.0F + this.ji0l * -2.0F, 1.0F);
         }

         if (player.age - this.r890 >= 100.0F) {
            this.vdq9b8 = !this.vdq9b8;
            this.r890 = player.age;
         }

         if (stack.isIn(ItemTags.BEDS) && bl) {
            matrices.translate(0.9, 0.0, 0.8);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * l));
         }

         blockRenderManager.renderBlockAsEntity(state, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
         matrices.pop();
      }
   }
}
