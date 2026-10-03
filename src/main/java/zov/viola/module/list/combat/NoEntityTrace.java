package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.LivingEntity;
import zov.viola.event.list.EventEntityHitBox;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "NoEntityTrace",
   moduleDesc = "Увеличение хитбокса сущностей",
   moduleCategory = ModuleCategory.COMBAT
)
public class NoEntityTrace extends Module {
   private final BooleanSetting daXyp = new BooleanSetting(
      "Невидимки", true
   );

   @Subscribe
   private void onEntityHitBox(EventEntityHitBox e2) {
      if (e2.getEntity() instanceof LivingEntity entity2) {
         if (!entity2.isInvisible() || this.daXyp.getValue()) {
            e2.setSize(-0.75F);
         }
      }
   }
}
