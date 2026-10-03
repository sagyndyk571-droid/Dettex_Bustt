package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;

@ModuleInformation(
   moduleName = "Auto Leave",
   moduleDesc = "Автоматический выход с сервера",
   moduleCategory = ModuleCategory.PLAYER
)
public class AutoLeave extends Module {
   private final SliderSetting h6wKb = new SliderSetting(
      "Дистанция", 8.0, 1.0, 64.0, 1.0
   );
   private final ModeSetting kMxZNkd = new ModeSetting(
      "Действие", "Hub", "Hub", "Spawn", "Disconnect"
   );

   @Subscribe
   private void onTick(EventTick event) {
      if (this.mc.player != null && this.mc.world != null && this.mc.getNetworkHandler() != null) {
         double triggerDistance = this.h6wKb.getValue();

         for (PlayerEntity player : this.mc.world.getPlayers()) {
            if (player != this.mc.player
               && !player.isSpectator()
               && !FriendRepository.isFriend(player.getNameForScoreboard())
               && !(this.mc.player.distanceTo(player) > triggerDistance)) {
               if (this.kMxZNkd.is("Hub")) {
                  this.mc.getNetworkHandler().sendChatCommand("hub");
               } else if (this.kMxZNkd.is("Spawn")) {
                  this.mc.getNetworkHandler().sendChatCommand("spawn");
               } else {
                  this.mc.getNetworkHandler().getConnection().disconnect(Text.literal("AutoLeave"));
               }

               this.setEnabled(false);
               return;
            }
         }
      }
   }
}
