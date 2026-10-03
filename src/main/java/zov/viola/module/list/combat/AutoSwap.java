package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import zov.viola.event.list.EventKeyInput;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.movement.Sprint;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.ui.SwapRadialScreen;
import zov.viola.util.base.Instance;
import zov.viola.util.player.other.InventoryUtil;

@ModuleInformation(
   moduleName = "Auto Swap",
   moduleDesc = "Быстрый своп предметов через радиальное меню",
   moduleCategory = ModuleCategory.COMBAT
)
public class AutoSwap extends Module {
   private final BindSetting yHx23s = new BindSetting(
      "Клавиша свапа", -1
   );
   private final SliderSetting dsb06bX = new SliderSetting(
      "Слотов", 3.0, 2.0, 5.0, 1.0
   );
   private final ItemStack[] t16mP = new ItemStack[8];
   private int wMyCh1;
   private boolean rwEb1m;

   public int getSwapCount() {
      return Math.min(this.t16mP.length, (int)Math.round(this.dsb06bX.getValue()));
   }

   public ItemStack getSlot(int index) {
      if (index >= 0 && index < this.t16mP.length) {
         ItemStack s = this.t16mP[index];
         return s == null ? ItemStack.EMPTY : s;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setSlot(int index, ItemStack stack) {
      if (index >= 0 && index < this.t16mP.length) {
         this.t16mP[index] = stack == null ? ItemStack.EMPTY : stack.copy();
      }
   }

   public void clearSlot(int index) {
      this.setSlot(index, ItemStack.EMPTY);
   }

   public int getSwapKeyValue() {
      return this.yHx23s.getValue();
   }

   public void choose(ItemStack stack) {
      if (this.mc.player != null && stack != null && !stack.isEmpty()) {
         int target = InventoryUtil.searchItemStack(s -> s.getItem() == stack.getItem());
         if (target != -1) {
            if (this.mc.getNetworkHandler() != null) {
               boolean wasSprinting = this.mc.player.isSprinting();
               boolean sprintModuleOn = Instance.get(Sprint.class).isEnabled();
               if (wasSprinting) {
                  this.mc.player.setSprinting(false);
                  if (!sprintModuleOn) {
                     this.mc.options.sprintKey.setPressed(false);
                  }
               }

               this.mc.interactionManager.clickSlot(0, target, 40, SlotActionType.SWAP, this.mc.player);
               if (wasSprinting) {
                  this.rwEb1m = true;
                  this.wMyCh1 = 40;
                  if (sprintModuleOn) {
                     Sprint.blockSprint(40);
                  }
               }
            }
         }
      }
   }

   @Subscribe
   private void onKey(EventKeyInput e) {
      if (e.getAction() == 1 && e.getKey() == this.yHx23s.getValue() && this.mc.currentScreen == null) {
         this.mc.setScreen(new SwapRadialScreen(this, this.yHx23s.getValue()));
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Sprint.clearSprintBlock();
      this.rwEb1m = false;
      this.wMyCh1 = 0;
   }

   @Subscribe
   private void onTick(EventTick e) {
      if (this.rwEb1m && this.mc.player != null) {
         if (this.wMyCh1 > 0) {
            this.wMyCh1--;
            boolean sprintModuleOn = Instance.get(Sprint.class).isEnabled();
            if (this.mc.player != null) {
               this.mc.player.setSprinting(false);
               if (!sprintModuleOn && this.mc.options != null) {
                  this.mc.options.sprintKey.setPressed(false);
               }
            }
         } else {
            this.rwEb1m = false;
            boolean sprintModuleOn = Instance.get(Sprint.class).isEnabled();
            this.mc.player.setSprinting(true);
            if (!sprintModuleOn) {
               this.mc.options.sprintKey.setPressed(true);
            }
         }
      }
   }
}
