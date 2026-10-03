package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Auto Eat",
   moduleDesc = "Автоматически ест когда голод ниже порога",
   moduleCategory = ModuleCategory.PLAYER
)
public class AutoEat extends Module {
   private final SliderSetting sZjl8 = new SliderSetting(
      "Порог голода", 14.0, 1.0, 20.0, 1.0
   );
   private boolean u2yXvoa = false;
   private int q4djx4c = -1;

   @Subscribe
   private void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null && this.mc.world != null) {
         int currentHunger = this.mc.player.getHungerManager().getFoodLevel();
         if (currentHunger <= this.sZjl8.getValue()) {
            int foodSlot = this.gjRqgI();
            if (foodSlot != -1) {
               if (!this.u2yXvoa) {
                  this.q4djx4c = this.mc.player.getInventory().selectedSlot;
                  this.mc.player.getInventory().selectedSlot = foodSlot;
                  this.u2yXvoa = true;
               }

               this.mc.options.useKey.setPressed(true);
            }
         } else if (this.u2yXvoa) {
            this.mc.options.useKey.setPressed(false);
            if (this.q4djx4c != -1) {
               this.mc.player.getInventory().selectedSlot = this.q4djx4c;
               this.q4djx4c = -1;
            }

            this.u2yXvoa = false;
         }
      }
   }

   private int gjRqgI() {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = this.mc.player.getInventory().getStack(i);
         if (!stack.isEmpty()) {
            FoodComponent food = stack.get(DataComponentTypes.FOOD);
            if (food != null) {
               return i;
            }
         }
      }

      return -1;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.u2yXvoa) {
         this.mc.options.useKey.setPressed(false);
         if (this.q4djx4c != -1 && this.mc.player != null) {
            this.mc.player.getInventory().selectedSlot = this.q4djx4c;
         }
      }

      this.u2yXvoa = false;
      this.q4djx4c = -1;
   }
}
