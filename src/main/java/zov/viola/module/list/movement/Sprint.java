package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.player.PlayerEntity;
import zov.viola.Viola;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.player.FreeCamera;

@ModuleInformation(
   moduleName = "Sprint",
   moduleDesc = "Автоматический спринт",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class Sprint extends Module {
   private static int h3j28u;

   public static boolean shouldBlockSprint() {
      return h3j28u > 0;
   }

   public static void blockSprint(int ticks) {
      h3j28u = Math.max(h3j28u, ticks);
   }

   public static void clearSprintBlock() {
      h3j28u = 0;
   }

   @Subscribe
   public void onUpdate(EventTick event) {
      if (this.mc.player != null) {
         if (h3j28u > 0) {
            h3j28u--;
            this.mc.options.sprintKey.setPressed(false);
            this.mc.player.setSprinting(false);
         } else {
            this.mc.options.sprintKey.setPressed(false);
            PlayerEntity fakePlayer = Viola.getInstance().getModuleStorage().get(FreeCamera.class).fakePlayer;
            this.mc
               .player
               .setSprinting(
                  fakePlayer == null
                     && (!this.mc.player.isTouchingWater() || this.mc.player.isSubmergedInWater())
                     && !this.mc.player.isGliding()
                     && this.mc.player.isWalking()
                     && this.mc.player.canSprint()
                     && !this.mc.player.isUsingItem()
                     && !this.mc.player.isBlind()
                     && (
                        !this.mc.player.hasVehicle()
                           || this.mc.player.getVehicle().canSprintAsVehicle()
                              && this.mc.player.getVehicle().isLogicalSideForUpdatingMovement()
                              && !this.mc.player.isGliding()
                              && (!this.mc.player.shouldSlowDown() || this.mc.player.isSubmergedInWater())
                     )
                     && this.mc.player.input.hasForwardMovement()
                     && !this.mc.player.horizontalCollision
                     && !this.mc.player.collidedSoftly
               );
         }
      }
   }
}
