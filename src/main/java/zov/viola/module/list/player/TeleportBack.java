package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.obf.D;
import zov.viola.util.math.StopWatch;
import zov.viola.util.player.other.SlownessManager;

@ModuleInformation(
   moduleName = "Teleport Back",
   moduleDesc = "Телепортация на предыдущую позицию",
   moduleCategory = ModuleCategory.PLAYER
)
public class TeleportBack extends Module {
   private final StopWatch dmOB = new StopWatch();
   private boolean yh44bYi;

   @Subscribe
   private void onUpdate(EventTick e) {
      if (this.mc.player != null && this.mc.world != null) {
         if (!this.mc.player.isAlive() && !this.yh44bYi && this.dmOB.isReached(500L)) {
            this.mc
               .player
               .networkHandler
               .sendChatCommand("sethome gavno");
            SlownessManager.addTimeTask(new SlownessManager.TimeTask(100L, () -> {
               if (this.mc.player != null) {
                  this.mc.player.requestRespawn();
                  this.mc.player.networkHandler.sendChatCommand("home gavno");
                  this.yh44bYi = false;
               }
            }, true));
            this.dmOB.reset();
         }
      }
   }
}
