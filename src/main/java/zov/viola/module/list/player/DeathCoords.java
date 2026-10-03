package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Death Coords",
   moduleDesc = "Показывает координаты смерти",
   moduleCategory = ModuleCategory.PLAYER
)
public class DeathCoords extends Module {
   private boolean zh9fDev;

   @Subscribe
   private void onUpdate(EventTick e) {
      if (this.mc.player != null) {
         if (this.mc.player.isDead()) {
            if (!this.zh9fDev) {
               this.logDirect(
                  String.format(
                        D.k(
                           new int[]{
                              1091,
                              1075,
                              144,
                              1203,
                              1133,
                              1101,
                              1264,
                              1227,
                              1129,
                              88,
                              1165,
                              1216,
                              113,
                              32,
                              233,
                              170,
                              107,
                              88,
                              149,
                              222,
                              97,
                              30,
                              144,
                              213,
                              127,
                              72,
                              214,
                              208,
                              116,
                              86,
                              128,
                              150
                           },
                           new int[]{81, 120, 176, 240}
                        ),
                        this.mc.player.getX(),
                        this.mc.player.getY(),
                        this.mc.player.getZ()
                     )
                     .replace(",", ".")
               );
               this.zh9fDev = true;
            }
         } else {
            this.zh9fDev = false;
         }
      }
   }
}
