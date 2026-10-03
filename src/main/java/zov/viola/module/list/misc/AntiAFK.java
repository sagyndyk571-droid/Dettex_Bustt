package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.util.Hand;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.util.math.StopWatch;

@ModuleInformation(
   moduleName = "Anti AFK",
   moduleDesc = "Предотвращает кик за AFK",
   moduleCategory = ModuleCategory.MISC
)
public class AntiAFK extends Module {
   private final StopWatch dyBxyn = new StopWatch();

   @Subscribe
   private void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.dyBxyn.isReached(10000L)) {
            this.mc.player.swingHand(Hand.MAIN_HAND);
            this.mc.player.jump();
            this.dyBxyn.reset();
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.dyBxyn.reset();
   }
}
