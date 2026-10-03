package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.screen.DeathScreen;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "Auto Rebirth",
   moduleDesc = "Автоматически возрождается после смерти",
   moduleCategory = ModuleCategory.PLAYER
)
public class AutoRebirth extends Module {
   @Subscribe
   private void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.mc.currentScreen instanceof DeathScreen && this.mc.player.deathTime > 2) {
            this.mc.player.requestRespawn();
            this.mc.setScreen(null);
         }
      }
   }
}
