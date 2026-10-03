package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "No Jump Delay",
   moduleDesc = "Убирает задержку между прыжками",
   moduleCategory = ModuleCategory.MISC
)
public class NoJumpDelay extends Module {
   @Subscribe
   private void onUpdate(EventTick e) {
      if (this.mc.player != null && this.mc.world != null) {
         this.mc.player.jumpingCooldown = 0;
      }
   }
}
