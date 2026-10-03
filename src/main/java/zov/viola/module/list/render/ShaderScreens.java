package zov.viola.module.list.render;

import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "ShaderScreens",
   moduleDesc = "Шейдерный фон скорборда",
   moduleCategory = ModuleCategory.RENDER
)
public class ShaderScreens extends Module {
   private final BooleanSetting tYwjI25 = new BooleanSetting(
      "Шейдер скорборда", true
   );
   private final ModeSetting y1zIuq = new ModeSetting(
      "Шейдер",
      "Aurora",
      "Aurora",
      "Energy",
      "Nebula",
      "Cosmic Veil",
      "Deep Space",
      "Matrix",
      "Void",
      "Plasma"
   );
   private final SliderSetting i1fS = new SliderSetting(
      "Прозрачность", 0.65F, 0.1F, 1.0, 0.01F
   );

   public boolean isSidebarShader() {
      return this.tYwjI25.getValue();
   }

   public String getShaderMode() {
      return this.y1zIuq.getValue();
   }

   public float getShaderOpacity() {
      return this.i1fS.getFloatValue();
   }
}
