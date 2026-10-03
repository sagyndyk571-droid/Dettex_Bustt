package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import zov.viola.Viola;
import zov.viola.event.list.EventNoSlow;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;
import zov.viola.util.player.simulate.SimulatedPlayer;

@ModuleInformation(
   moduleName = "No Slow",
   moduleDesc = "Убирает замедление при использовании",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class NoSlow extends Module {
   private final ModeSetting rcn25 = new ModeSetting("Мод", "Vanilla", "Vanilla", "Grim");

   @Subscribe
   private void onNoSlow(EventNoSlow e) {
      String var2 = this.rcn25.getValue();
      switch (var2) {
         case "Vanilla":
            if (Viola.getInstance().getModuleStorage().get(KillAura.class).getTarget() == null || !(SimulatedPlayer.simulateLocalPlayer(1).fallDistance > 0.0F)
               )
             {
               this.mc.player.setSprinting(true);
            }

            e.cancelEvent();
            break;
         case "Grim":
            this.mc
               .player
               .setSprinting(
                  this.mc.player.getItemUseTime() > 4
                     && (
                        Viola.getInstance().getModuleStorage().get(KillAura.class).getTarget() == null
                           || !(SimulatedPlayer.simulateLocalPlayer(1).fallDistance > 0.0F)
                     )
                     && Viola.getInstance().getServerManager().getSprintingChangeTicks() > 0.0F
               );
            if (this.mc.player.getItemUseTime() % 2 == 0) {
               e.cancelEvent();
            }
      }
   }
}
