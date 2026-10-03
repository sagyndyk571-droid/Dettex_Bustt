package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.item.Items;
import zov.viola.event.list.EventKeyInput;
import zov.viola.event.list.EventPlayerSync;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;
import zov.viola.util.player.other.InventoryUtil;

@ModuleInformation(
   moduleName = "Click Pearl",
   moduleDesc = "Быстрый бросок перлы по биндy",
   moduleCategory = ModuleCategory.PLAYER
)
public class ClickPearl extends Module {
   private final ModeSetting f9j5m = new ModeSetting(
      "Мод",
      "Обычный",
      "Обычный",
      "Легитный"
   );
   private final BindSetting pqMoO = new BindSetting(
      "Клавиша броска", -98
   );
   private boolean ki5b;
   private int jwna;

   @Subscribe
   private void onKey(EventKeyInput e) {
      if (e.getAction() != 0) {
         if (e.getKey() == this.pqMoO.getValue()) {
            if (this.f9j5m.is("Обычный")) {
               InventoryUtil.swapAndUseHvH(Items.ENDER_PEARL);
            } else {
               this.ki5b = true;
            }
         }
      }
   }

   @Subscribe
   private void onPlayerTick(EventPlayerUpdate ignored) {
      if (this.mc.player != null) {
         if (!this.ki5b && this.jwna > 0) {
            this.jwna--;
         }

         if (this.ki5b || this.jwna > 0) {
            this.mc.player.setSprinting(false);
         }
      }
   }

   @Subscribe
   private void onPlayerSync(EventPlayerSync ignored) {
      if (this.mc.player != null && this.ki5b) {
         int slotHotbar = InventoryUtil.searchItem(Items.ENDER_PEARL, 0, 9);
         if (slotHotbar != -1) {
            InventoryUtil.swapAndUseLegit(Items.ENDER_PEARL);
         } else {
            if (this.jwna == 0) {
               this.jwna++;
               return;
            }

            InventoryUtil.swapAndUseLegit(Items.ENDER_PEARL);
            this.jwna = 2;
         }

         this.ki5b = false;
      }
   }
}
