package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.item.Items;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "Fast Exp",
   moduleDesc = "Ускоренное использование опыта",
   moduleCategory = ModuleCategory.PLAYER
)
public class FastExp extends Module {
   @Subscribe
   private void onPlayerUpdate(EventPlayerUpdate e) {
      if (this.mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE || this.mc.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
         this.mc.itemUseCooldown = 0;
      }
   }
}
