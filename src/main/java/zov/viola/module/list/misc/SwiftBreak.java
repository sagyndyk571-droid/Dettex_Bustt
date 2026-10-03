package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.mixin.ClientPlayerInteractionManagerAccessor;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "Swift Break",
   moduleDesc = "Убирает задержку ломания блоков",
   moduleCategory = ModuleCategory.MISC
)
public class SwiftBreak extends Module {
   @Subscribe
   private void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null && this.mc.interactionManager != null) {
         if (!this.mc.player.isUsingItem()) {
            ((ClientPlayerInteractionManagerAccessor)this.mc.interactionManager).violaSetBlockBreakingCooldown(0);
         }
      }
   }
}
