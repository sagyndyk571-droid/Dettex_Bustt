package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventTick;
import zov.viola.mixin.SimpleOptionAccessor;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Full Bright",
   moduleDesc = "Настраиваемая яркость без тьмы",
   moduleCategory = ModuleCategory.RENDER
)
public class FullBright extends Module {
   public final SliderSetting brightness = new SliderSetting(
      "Яркость", 100.0, 0.0, 100.0, 1.0
   );
   private double q7ydCI = -1.0;
   private boolean b6X0 = false;

   private static double eYd0(double percent) {
      return 0.5 * Math.pow(2.0, percent / 20.0);
   }

   private void setGammaUnchecked(double value) {
      ((SimpleOptionAccessor)(Object) this.mc.options.getGamma()).setRawValue(value);
   }

   @Subscribe
   private void onUpdate(EventTick e) {
      if (this.mc.options != null) {
         if (!this.b6X0) {
            this.q7ydCI = this.mc.options.getGamma().getValue();
            this.b6X0 = true;
         }

         double target = eYd0(this.brightness.getValue());
         if (Math.abs(this.mc.options.getGamma().getValue() - target) > 1.0E-4) {
            this.setGammaUnchecked(target);
         }
      }
   }

   @Override
   public void onEnable() {
      this.b6X0 = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.b6X0 && this.mc.options != null) {
         this.setGammaUnchecked(this.q7ydCI);
      }

      this.b6X0 = false;
      super.onDisable();
   }
}
