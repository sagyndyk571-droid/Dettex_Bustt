package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.math.TimerUtil;

@ModuleInformation(
   moduleName = "Timer",
   moduleDesc = "Изменяет скорость игры",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class Timer extends Module {
   private final SliderSetting sFSrgc7 = new SliderSetting(
      "Скорость", 2.0, 0.1F, 10.0, 0.1F
   );

   @Subscribe
   private void onTick(EventTick e) {
      TimerUtil.setTimer((float)this.sFSrgc7.getValue());
   }

   @Override
   public void onDisable() {
      TimerUtil.setTimer(1.0F);
      super.onDisable();
   }
}
