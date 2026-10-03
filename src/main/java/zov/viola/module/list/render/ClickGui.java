package zov.viola.module.list.render;

import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.ui.ClickGuiFrame;
import zov.viola.util.base.Instance;
import zov.viola.util.render.math.Animation;

@ModuleInformation(
   moduleName = "Click Gui",
   moduleDesc = "Графическое меню настройки модулей",
   moduleCategory = ModuleCategory.RENDER,
   moduleKeybind = 344
)
public class ClickGui extends Module {
   public final SliderSetting size = new SliderSetting("Размер", 1.0, 0.7, 1.4, 0.05);
   public final SliderSetting animSpeed = new SliderSetting(
      "Скорость анимаций",
      1.0,
      0.25,
      3.0,
      0.25
   );
   public final SliderSetting glowIntensity = new SliderSetting(
      "Свечение", 1.0, 0.0, 2.0, 0.1
   );
   public final ColorSetting accent = new ColorSetting(
         "Цвет клиента", -9021441
      )
      .setGlobal();
   public final BooleanSetting twoColor = new BooleanSetting(
      "Двухцветная тема", false
   );
   public final ColorSetting accent2 = new ColorSetting(
         "Второй цвет", -12851024
      )
      .setGlobalTwo()
      .setVisible(() -> this.twoColor.getValue());
   public final BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
   private static ClickGuiFrame defMT;

   public static float getAnimSpeed() {
      ClickGui m = Instance.get(ClickGui.class);
      return m != null ? (float)m.animSpeed.getValue() : 1.0F;
   }

   public static float getGlow() {
      ClickGui m = Instance.get(ClickGui.class);
      return m != null ? (float)m.glowIntensity.getValue() : 1.0F;
   }

   @Override
   public void onEnable() {
      Animation.setGlobalSpeed((float)this.animSpeed.getValue());
      if (this.mc.world == null) {
         this.toggle();
      } else {
         if (defMT == null) {
            defMT = new ClickGuiFrame();
         }

         defMT.playOpenAnimation();
         this.mc.setScreen(defMT);
         this.toggle();
      }
   }
}
