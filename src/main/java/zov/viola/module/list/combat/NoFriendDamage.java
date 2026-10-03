package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventAttack;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.util.friend.Friend;
import zov.viola.util.friend.FriendRepository;

@ModuleInformation(
   moduleName = "No Friend Damage",
   moduleDesc = "Не атакует друзей из списка",
   moduleCategory = ModuleCategory.COMBAT
)
public class NoFriendDamage extends Module {
   @Subscribe
   private void onAttack(EventAttack e) {
      for (Friend friend : FriendRepository.getFriends()) {
         if (e.getEntity() != this.mc.player && e.getEntity().getNameForScoreboard().equals(friend.name())) {
            e.cancelEvent();
         }
      }
   }
}
