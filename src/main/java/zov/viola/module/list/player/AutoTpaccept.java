package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import zov.viola.event.list.EventPacket;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.ModuleSettingDefinitions;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.util.friend.FriendRepository;

@ModuleInformation(
   moduleName = "Auto Tpaccept",
   moduleDesc = "Автоматически принимает тп запросы",
   moduleCategory = ModuleCategory.PLAYER
)
public class AutoTpaccept extends Module {
   private final BooleanSetting tXdrh = ModuleSettingDefinitions.autoTpOnlyFriends();

   @Subscribe
   public void onPacket(EventPacket e) {
      if (this.mc.player != null && this.mc.world != null) {
         if (e.getPacket() instanceof GameMessageS2CPacket p
            && TpRequestRecognizer.shouldAccept(p.content().getString(), this.tXdrh.getValue(), FriendRepository::isFriend)) {
            this.mc.getNetworkHandler().sendChatCommand("tpaccept");
         }
      }
   }
}
