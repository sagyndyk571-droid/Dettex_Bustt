package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket.Status;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import zov.viola.event.list.EventPacket;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.util.packet.NetworkUtils;

@ModuleInformation(
   moduleName = "RP Spoofer",
   moduleDesc = "Изменяет отправку пакетов ресурспаков",
   moduleCategory = ModuleCategory.MISC
)
public class RPSpoofer extends Module {
   @Subscribe
   private void onPacket(EventPacket e) {
      if (e.getPacket() instanceof ResourcePackSendS2CPacket) {
         NetworkUtils.sendPacket(new ResourcePackStatusC2SPacket(this.mc.player.getUuid(), Status.ACCEPTED));
         NetworkUtils.sendPacket(new ResourcePackStatusC2SPacket(this.mc.player.getUuid(), Status.DOWNLOADED));
         NetworkUtils.sendPacket(new ResourcePackStatusC2SPacket(this.mc.player.getUuid(), Status.SUCCESSFULLY_LOADED));
         e.cancelEvent();
      }
   }
}
