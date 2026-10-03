package zov.viola.module.list.misc;

import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.Friend;
import zov.viola.util.friend.FriendRepository;

@ModuleInformation(
   moduleName = "Streamer Mode",
   moduleDesc = "Скрывает никнеймы игроков",
   moduleCategory = ModuleCategory.MISC
)
public class NameProtect extends Module {
   public final BooleanSetting hideFriends = new BooleanSetting(
      "Скрыть друзей", false
   );

   public String getCustomName() {
      return this.isEnabled() ? "viola" : this.mc.player.getNameForScoreboard();
   }

   public String getCustomName(String originalName) {
      if (this.isEnabled() && this.mc.player != null) {
         String me = this.mc.player.getNameForScoreboard();
         if (originalName.contains(me)) {
            return originalName.replace(me, "viola");
         } else {
            if (this.hideFriends.getValue()) {
               for (Friend friend : FriendRepository.getFriends()) {
                  if (originalName.contains(friend.name())) {
                     return originalName.replace(friend.name(), "viola");
                  }
               }
            }

            return originalName;
         }
      } else {
         return originalName;
      }
   }
}
