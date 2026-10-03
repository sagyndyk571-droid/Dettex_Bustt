package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import zov.viola.event.list.EventPacket;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "Velocity",
   moduleDesc = "Убирает отбрасывание от ударов",
   moduleCategory = ModuleCategory.COMBAT
)
public class Velocity extends Module {
   @Subscribe
   private void onPacket(EventPacket e) {
      if (e.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
         if (packet.getEntityId() != this.mc.player.getId()) {
            return;
         }

         e.cancelEvent();
      }
   }
}
