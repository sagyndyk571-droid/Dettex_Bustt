package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;
import zov.viola.util.player.other.InventoryUtil;

@ModuleInformation(
   moduleName = "Auto Tool",
   moduleDesc = "Выбирает лучший инструмент для добычи блоков",
   moduleCategory = ModuleCategory.PLAYER
)
public class AutoTool extends Module {
   private final ModeSetting dmo0don = new ModeSetting("Мод", "Vanilla", "Vanilla", "Grim");
   private int os47 = -1;
   private int r3sm3 = -1;
   private boolean wW2reQ7;

   @Subscribe
   private void onTick(EventPlayerUpdate ignored) {
      if (this.mc.player == null || this.mc.player.isCreative()) {
         this.os47 = -1;
      } else if (!this.mc.player.isUsingItem()) {
         if (this.sm7cM()) {
            if (this.r3sm3 == -1) {
               this.os47 = this.d4pe();
               if (this.os47 != -1) {
                  if (this.os47 >= 0 && this.os47 <= 8) {
                     this.r3sm3 = this.mc.player.getInventory().selectedSlot;
                     this.mc.player.getInventory().selectedSlot = this.os47;
                     this.mc.interactionManager.syncSelectedSlot();
                     this.wW2reQ7 = true;
                  } else {
                     String var2 = this.dmo0don.getValue();
                     switch (var2) {
                        case "Vanilla":
                           this.gs8w();
                           break;
                        case "Grim":
                           InventoryUtil.swapWithBypassGrim(this::gs8w);
                           break;
                        case "ReallyWorld":
                           if (this.mc.player.isOnGround()) {
                              InventoryUtil.swapWithBypassPolar(this::gs8w);
                           } else {
                              InventoryUtil.swapWithBypassGrim(this::gs8w);
                           }
                     }

                     this.r3sm3 = this.os47;
                  }
               }
            }
         } else if (this.r3sm3 != -1) {
            if (this.r3sm3 >= 0 && this.r3sm3 <= 8) {
               this.mc.player.getInventory().selectedSlot = this.r3sm3;
               this.mc.interactionManager.syncSelectedSlot();
               this.r3sm3 = -1;
               this.wW2reQ7 = false;
            } else {
               String var4 = this.dmo0don.getValue();
               switch (var4) {
                  case "Vanilla":
                     this.dUjxE5t();
                     break;
                  case "Grim":
                     InventoryUtil.swapWithBypassGrim(this::dUjxE5t);
                     break;
                  case "ReallyWorld":
                     if (this.mc.player.isOnGround()) {
                        InventoryUtil.swapWithBypassPolar(this::dUjxE5t);
                     } else {
                        InventoryUtil.swapWithBypassGrim(this::dUjxE5t);
                     }
               }
            }
         }
      }
   }

   private void gs8w() {
      if (this.os47 != -1) {
         this.wW2reQ7 = true;
         this.mc.interactionManager.clickSlot(0, this.os47, this.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, this.mc.player);
      }
   }

   private void dUjxE5t() {
      if (this.r3sm3 != -1 && this.wW2reQ7) {
         this.mc.interactionManager.clickSlot(0, this.r3sm3, this.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, this.mc.player);
         this.r3sm3 = -1;
         this.wW2reQ7 = false;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.wW2reQ7 = false;
      this.os47 = -1;
      this.r3sm3 = -1;
   }

   private int d4pe() {
      if (this.mc.crosshairTarget instanceof BlockHitResult blockHitResult) {
         BlockPos pos = blockHitResult.getBlockPos();
         BlockState state = this.mc.world.getBlockState(pos);
         int bestSlot = -1;
         float bestSpeed = 1.0F;

         for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = this.mc.player.getInventory().getStack(slot);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
               bestSpeed = speed;
               bestSlot = slot;
            }
         }

         return bestSlot;
      } else {
         return -1;
      }
   }

   private boolean sm7cM() {
      return this.mc.crosshairTarget != null && this.mc.options.attackKey.isPressed();
   }
}
