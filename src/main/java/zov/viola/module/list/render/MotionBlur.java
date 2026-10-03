package zov.viola.module.list.render;

import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.ambience.MotionBlurRenderer;

@ModuleInformation(
   moduleName = "Motion Blur",
   moduleDesc = "Размытие движения",
   moduleCategory = ModuleCategory.RENDER
)
public class MotionBlur extends Module {
   public final SliderSetting strength = new SliderSetting("Сила", 50.0, 0.0, 99.0, 1.0);

   @Override
   public void onEnable() {
      super.onEnable();
      MotionBlurRenderer.reset();
   }

   public float getStrength() {
      return this.strength.getFloatValue() / 100.0F;
   }
}
