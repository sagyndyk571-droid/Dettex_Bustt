package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Gold Eat",
   moduleDesc = "Автоматически ест золотые яблоки",
   moduleCategory = ModuleCategory.COMBAT
)
public class GoldEat extends Module {
   private final SliderSetting rH7w = new SliderSetting(
      "Здоровье", 18.0, 4.0, 20.0, 1.0
   );
   private final BooleanSetting hl799 = new BooleanSetting(
      "Есть зачарованные", true
   );
   private boolean jZvidan = false;

   @Subscribe
   private void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         if (this.mc.currentScreen == null) {
            boolean isGolden = this.mc.player.getMainHandStack().isOf(Items.GOLDEN_APPLE)
               || this.hl799.getValue() && this.mc.player.getMainHandStack().isOf(Items.ENCHANTED_GOLDEN_APPLE);
            if (isGolden && this.mc.player.getHealth() <= this.rH7w.getValue()) {
               this.jZvidan = true;
               if (!this.mc.player.isUsingItem()) {
                  this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
                  this.mc.options.useKey.setPressed(true);
                  this.mc.player.swingHand(Hand.MAIN_HAND);
               }
            } else if (this.jZvidan && this.mc.player.isUsingItem()) {
               this.mc.interactionManager.stopUsingItem(this.mc.player);
               this.mc.options.useKey.setPressed(false);
               this.jZvidan = false;
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.jZvidan) {
         if (this.mc.player != null && this.mc.player.isUsingItem() && this.mc.interactionManager != null) {
            this.mc.interactionManager.stopUsingItem(this.mc.player);
         }

         this.mc.options.useKey.setPressed(false);
         this.jZvidan = false;
      }
   }
}
