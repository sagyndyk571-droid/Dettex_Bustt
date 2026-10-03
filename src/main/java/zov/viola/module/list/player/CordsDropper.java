package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventKeyInput;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BindSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Cords Dropper",
   moduleDesc = "Скидывает координаты в чат",
   moduleCategory = ModuleCategory.PLAYER
)
public class CordsDropper extends Module {
   private final BindSetting dsfq4c = new BindSetting("Key", -1);

   @Subscribe
   private void onKey(EventKeyInput e) {
      if (e.getAction() != 0) {
         if (e.getKey() == this.dsfq4c.getValue() && this.mc.player != null) {
            String message = String.format(
               "! %.0f %.0f !!!",
               this.mc.player.getX(),
               this.mc.player.getZ()
            );
            this.mc.player.networkHandler.sendChatMessage(message);
         }
      }
   }
}
