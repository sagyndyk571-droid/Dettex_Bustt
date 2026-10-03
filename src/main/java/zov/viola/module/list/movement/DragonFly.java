package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.player.move.MoveUtil;

@ModuleInformation(
   moduleName = "Dragon Fly",
   moduleDesc = "Ускоряет уже активный полёт",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class DragonFly extends Module {
   private final SliderSetting tGBb = new SliderSetting(
      "Скорость по X/Z", 1.0, 0.0, 2.0, 0.1
   );
   private final SliderSetting yzl2 = new SliderSetting(
      "Скорость по Y", 1.0, 0.0, 2.0, 0.1
   );

   @Subscribe
   public void onTick(EventTick event) {
      if (this.mc.player != null && this.mc.world != null && this.mc.player.getAbilities().flying) {
         MoveUtil.setMotion(this.tGBb.getValue());
         if (this.mc.options.jumpKey.isPressed()) {
            this.mc.player.setVelocity(this.mc.player.getVelocity().x, this.yzl2.getValue(), this.mc.player.getVelocity().z);
         }

         if (this.mc.options.sneakKey.isPressed()) {
            this.mc.player.setVelocity(this.mc.player.getVelocity().x, -this.yzl2.getValue(), this.mc.player.getVelocity().z);
         }
      }
   }
}
