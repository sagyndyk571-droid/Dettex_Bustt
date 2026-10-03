package zov.viola.module.list.render;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;

@ModuleInformation(
   moduleName = "Cape",
   moduleDesc = "Свой плащ для игрока",
   moduleCategory = ModuleCategory.RENDER
)
public class Cape extends Module {
   private final ModeListSetting n29M = new ModeListSetting(
      "На кого работать",
      new BooleanSetting("Себя", true),
      new BooleanSetting("Друзья", true)
   );

   public static Cape getInstance() {
      return Viola.getInstance().getModuleStorage().get(Cape.class);
   }

   public boolean shouldRenderCape(AbstractClientPlayerEntity player) {
      if (this.isEnabled() && player != null) {
         boolean isSelf = player == this.mc.player;
         boolean isFriend = FriendRepository.isFriend(player.getNameForScoreboard());
         boolean selfEnabled = this.n29M.isEnabled("Себя");
         boolean friendsEnabled = this.n29M.isEnabled("Друзья");
         if (isSelf) {
            return selfEnabled;
         } else {
            return isFriend ? friendsEnabled : false;
         }
      } else {
         return false;
      }
   }
}
