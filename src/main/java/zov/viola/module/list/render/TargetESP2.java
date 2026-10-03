package zov.viola.module.list.render;

import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Target ESP 2",
   moduleDesc = "Второй Target ESP для двух эффектов сразу",
   moduleCategory = ModuleCategory.RENDER
)
public class TargetESP2 extends TargetESP {
   public TargetESP2() {
      this.mode.setValue("Молнии");
   }
}
