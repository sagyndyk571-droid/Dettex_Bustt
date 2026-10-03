package zov.viola.util.aim;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import zov.viola.obf.D;

public class CritChecks {
   public static boolean check() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return mc.player == null
         ? false
         : mc.player.isTouchingWater()
            || mc.player.isSwimming()
            || mc.player.isGliding()
            || mc.player.isClimbing()
            || mc.player.isInsideWall()
            || mc.player.isInLava()
            || mc.player.hasVehicle();
   }

   public static boolean check2() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == null) {
         return false;
      } else {
         Item item = mc.player.getMainHandStack().getItem();
         return item instanceof AxeItem || item instanceof TridentItem || item instanceof MaceItem;
      }
   }

   public static boolean check3() {
      return isFloat(-0.01F);
   }

   public static boolean check4() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == null || mc.options == null) {
         return false;
      } else if (mc.player.isTouchingWater() && !mc.player.isSubmergedInWater() && !mc.player.isSwimming()) {
         return !mc.options.jumpKey.isPressed() ? false : mc.player.isOnGround() || mc.player.getVelocity().y > 0.0;
      } else {
         return false;
      }
   }

   public static boolean check5() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.world != null) {
         BlockPos blockpos = mc.player.getBlockPos().up(2);
         BlockState blockstate = mc.world.getBlockState(blockpos);
         return !blockstate.isAir() && blockstate.isFullCube(mc.world, blockpos);
      } else {
         return false;
      }
   }

   public static boolean isFloat(float value) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == null) {
         return false;
      } else if (!mc.player.isOnGround() && !mc.player.isInLava() && !mc.player.isTouchingWater() && !mc.player.hasVehicle()) {
         double d0 = mc.player.getVelocity().y;
         if (check5() && d0 < -0.01F) {
            double d1 = mc.player.getY() - (mc.player.getBlockPos().down().getY() + 1.0);
            if (d1 > 0.4) {
               return true;
            }
         }

         return d0 < value;
      } else {
         return false;
      }
   }

   public static boolean isLivingEntity(LivingEntity livingEntity) {
      return !livingEntity.isBlocking() ? false : livingEntity.getActiveItem().getItem() == Items.SHIELD;
   }

   public static int getInt() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == null) {
         return -1;
      } else {
         for (int i = 0; i < 9; i++) {
            ItemStack itemstack = mc.player.getInventory().getStack(i);
            if (!itemstack.isEmpty() && itemstack.getItem() instanceof AxeItem) {
               return i;
            }
         }

         return -1;
      }
   }

   public static boolean isItemStack(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return false;
      } else {
         Item item = itemStack.getItem();
         return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem || item instanceof MaceItem;
      }
   }

   public static boolean isClass(Class value) {
      MinecraftClient mc = MinecraftClient.getInstance();
      return mc.player == null ? false : value.isInstance(mc.player.getMainHandStack().getItem()) || value.isInstance(mc.player.getOffHandStack().getItem());
   }

   public static boolean check6() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return mc.player == null ? false : isItemStack(mc.player.getMainHandStack()) || isItemStack(mc.player.getOffHandStack());
   }

   public static void onEntity(Entity entity2) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.interactionManager != null && mc.player != null) {
         mc.interactionManager.attackEntity(mc.player, entity2);
         mc.player.swingHand(Hand.MAIN_HAND);
         mc.player.resetLastAttackedTicks();
      }
   }

   public static float getFloat() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == null) {
         return 0.8F;
      } else {
         boolean flag = check2();
         if (!mc.player.isOnGround() && !check()) {
            return flag ? 0.85F : 0.75F;
         } else {
            return flag ? 0.95F : 0.8F;
         }
      }
   }

   public static boolean isStringBoolean(String text, boolean flag) {
      float f3 = getFloat();
      float f2 = -0.01F;
      float f = 0.0F;
      return isStringFloatBooleanFloatFloat(text, f3, flag, f2, f);
   }

   public static boolean isStringFloatBooleanFloatFloat(String text, float value, boolean flag2, float value2, float value3) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && flag2) {
         float f = mc.player.getAttackCooldownProgress(value3);
         if (f < value) {
            return false;
         } else {
            boolean flag = isFloat(value2);
            if ("Только криты".equals(text)) {
               return check() || flag;
            } else if (!check() && mc.player.getVelocity().y > 0.0) {
               return false;
            } else {
               return mc.player.isOnGround() ? f >= value : check() || flag;
            }
         }
      } else {
         return false;
      }
   }
}
