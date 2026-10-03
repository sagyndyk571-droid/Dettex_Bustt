package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.decoration.EndCrystalEntity;
import zov.viola.event.list.EventAttack;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "Crystal Optimizer",
   moduleDesc = "Оптимизация рендера кристаллов",
   moduleCategory = ModuleCategory.MISC
)
public class CrystalOptimizer extends Module {
   @Subscribe
   private void onAttack(EventAttack e) {
      if (e.getEntity() instanceof EndCrystalEntity entity) {
         entity.remove(RemovalReason.DISCARDED);
      }
   }
}
