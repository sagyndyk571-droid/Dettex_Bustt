package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import zov.viola.event.list.EventAttack;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "Criticals",
   moduleDesc = "Все удары будут критическими",
   moduleCategory = ModuleCategory.COMBAT
)
public class Criticals extends Module {
   @Subscribe
   private void onAttack(EventAttack e) {
      if (this.mc.player.fallDistance == 0.0F && !this.mc.player.isOnGround()) {
         this.mc.player.fallDistance = 0.001F;
         this.mc
            .player
            .networkHandler
            .sendPacket(
               new Full(
                  this.mc.player.getX(),
                  this.mc.player.getY() - 9.99999999E-5,
                  this.mc.player.getZ(),
                  this.mc.player.getYaw(),
                  this.mc.player.getPitch(),
                  false,
                  this.mc.player.horizontalCollision
               )
            );
      }
   }
}
