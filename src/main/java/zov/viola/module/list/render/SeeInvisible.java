package zov.viola.module.list.render;

import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "SeeInvisible",
   moduleDesc = "Делает невидимых игроков полупрозрачными",
   moduleCategory = ModuleCategory.RENDER
)
public class SeeInvisible extends Module {
   private final SliderSetting iuit = new SliderSetting(
      "Прозрачность", 0.5, 0.1, 1.0, 0.05
   );

   public float getAlpha() {
      return this.iuit.getFloatValue();
   }

   public boolean isOpaque() {
      return this.iuit.getFloatValue() >= 0.9F;
   }
}
