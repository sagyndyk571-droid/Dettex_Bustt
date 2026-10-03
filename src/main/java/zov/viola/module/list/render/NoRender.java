package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.effect.StatusEffects;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Removals",
   moduleDesc = "Убирает лишние элементы интерфейса",
   moduleCategory = ModuleCategory.RENDER
)
public class NoRender extends Module {
   public final ModeListSetting elements = new ModeListSetting(
      "Убрать элементы",
      new BooleanSetting("Огонь", true),
      new BooleanSetting(
         "Размытие в воде", true
      ),
      new BooleanSetting(
         "Зрение в блоках", true
      ),
      new BooleanSetting("Камераклип", true),
      new BooleanSetting("Тряска камеры", true),
      new BooleanSetting("Тотем", true),
      new BooleanSetting("Удочка", true),
      new BooleanSetting("Дождь", true),
      new BooleanSetting("Плохие эффекты", true)
   );

   @Subscribe
   public void onUpdate(EventTick e) {
      if (this.mc.player != null) {
         if (this.elements.isEnabled("Тряска камеры")) {
            this.mc.options.getDamageTiltStrength().setValue(0.0);
         } else {
            this.mc.options.getDamageTiltStrength().setValue(0.5);
         }

         if (this.elements
            .isEnabled("Плохие эффекты")) {
            this.mc.player.removeStatusEffect(StatusEffects.BLINDNESS);
            this.mc.player.removeStatusEffect(StatusEffects.NAUSEA);
            this.mc.player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
            this.mc.player.removeStatusEffect(StatusEffects.WEAKNESS);
            this.mc.player.removeStatusEffect(StatusEffects.SLOWNESS);
            this.mc.player.removeStatusEffect(StatusEffects.WITHER);
            this.mc.player.removeStatusEffect(StatusEffects.POISON);
            this.mc.player.removeStatusEffect(StatusEffects.HUNGER);
            this.mc.player.removeStatusEffect(StatusEffects.LEVITATION);
            this.mc.player.removeStatusEffect(StatusEffects.UNLUCK);
            this.mc.player.removeStatusEffect(StatusEffects.DARKNESS);
         }
      }
   }

   @Override
   public void onDisable() {
      this.mc.options.getDamageTiltStrength().setValue(0.5);
      super.onDisable();
   }
}
