package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.LivingEntity;
import zov.viola.Viola;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "FixHP",
   moduleDesc = "Чинит отображение HP таргета на ReallyWorld",
   moduleCategory = ModuleCategory.COMBAT
)
public class FixHP extends Module {
   private float ttVfuwk = -1.0F;

   @Subscribe
   private void onTick(EventTick event) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.mc.getCurrentServerEntry() != null) {
            if (this.mc.getCurrentServerEntry().address.toLowerCase().contains("reallyworld")) {
               LivingEntity target = Viola.getInstance().getModuleStorage().get(KillAura.class).getTarget();
               if (target != null) {
                  if (this.h246()) {
                     if (this.ttVfuwk > 0.0F) {
                        target.setHealth(this.ttVfuwk);
                     }
                  } else {
                     target.setHealth(target.getMaxHealth() + target.getAbsorptionAmount());
                     this.ttVfuwk = target.getMaxHealth();
                  }
               }
            }
         }
      }
   }

   private boolean h246() {
      return this.mc.world.getPlayers().stream().filter(p -> p != this.mc.player).anyMatch(p -> p.squaredDistanceTo(this.mc.player) < 100.0);
   }
}
