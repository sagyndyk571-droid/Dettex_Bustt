package zov.viola.module.list.player;

import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "No Push",
   moduleDesc = "Убирает толкание от игроков и блоков",
   moduleCategory = ModuleCategory.PLAYER
)
public class NoPush extends Module {
   public final ModeListSetting objects = new ModeListSetting(
      "Обьекты",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("Блоки", true)
   );
}
