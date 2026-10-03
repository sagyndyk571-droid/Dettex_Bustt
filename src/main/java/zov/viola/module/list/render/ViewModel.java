package zov.viola.module.list.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "View Model",
   moduleDesc = "Настройка положения рук и предметов",
   moduleCategory = ModuleCategory.RENDER
)
public class ViewModel extends Module {
   private final SliderSetting d1DV = new SliderSetting(
      "Левая рука по X", 0.0, -2.0, 2.0, 0.1F
   );
   private final SliderSetting yJKL0c = new SliderSetting(
      "Левая рука по Y", 0.0, -2.0, 2.0, 0.1F
   );
   private final SliderSetting qBjHo4u = new SliderSetting(
      "Левая рука по Z", 0.0, -2.0, 2.0, 0.1F
   );
   private final SliderSetting osp8dJt = new SliderSetting(
      "Правая рука по X",
      0.0,
      -2.0,
      2.0,
      0.1F
   );
   private final SliderSetting f79Jqhv = new SliderSetting(
      "Правая рука по Y",
      0.0,
      -2.0,
      2.0,
      0.1F
   );
   private final SliderSetting kolyE = new SliderSetting(
      "Правая рука по Z",
      0.0,
      -2.0,
      2.0,
      0.1F
   );

   public void applyHandPosition(MatrixStack matrices, Arm arm) {
      if (this.isEnabled()) {
         if (arm == Arm.LEFT) {
            matrices.translate(this.d1DV.getValue(), this.yJKL0c.getValue(), this.qBjHo4u.getValue());
         } else {
            matrices.translate(this.osp8dJt.getValue(), this.f79Jqhv.getValue(), this.kolyE.getValue());
         }
      } else {
         matrices.translate(0.0F, 0.0F, 0.0F);
      }
   }
}
