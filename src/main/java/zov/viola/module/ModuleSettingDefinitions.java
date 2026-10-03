package zov.viola.module;

import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

public final class ModuleSettingDefinitions {
   private ModuleSettingDefinitions() {
   }

   public static ModeSetting killAuraRotation() {
      return new ModeSetting(
         "Ротация",
         "ReallyWorld",
         "ReallyWorld",
         "Smooth",
         "Funtime",
         "SPtest",
         "AresMine",
          "Sloth",
          "Legit",
          "ApexTime"
       );
   }

   public static BooleanSetting killAuraOnlySpace() {
      return new BooleanSetting(
         "Только с пробелом", false
      );
   }

   public static ModeSetting killAuraSprintReset() {
      return new ModeSetting(
            "Сброс спринта",
            "Легитный",
            "Легитный",
            "Перед ударом",
            "По радиусу",
            "Не сбрасывать"
         )
         .addAlias(
            "По тику",
            "Легитный"
         )
         .addAlias(
            "На эликре",
            "Перед ударом"
         )
         .addAlias(
            "По тику по трассе",
            "По радиусу"
         );
   }

   public static ModeSetting autoSwapFrom() {
      return new ModeSetting(
         "Свапать с",
         "Шар",
         "Гепл",
         "Щит",
         "Талисман",
         "Шар"
      );
   }

   public static ModeSetting autoSwapTo() {
      return new ModeSetting(
         "Свапать на",
         "Гепл",
         "Щит",
         "Талисман",
         "Шар"
      );
   }

   public static ModeSetting autoSwapThird() {
      return new ModeSetting(
         "Третий предмет",
         "Талисман",
         "Гепл",
         "Щит",
         "Талисман",
         "Шар"
      );
   }

   public static BooleanSetting triggerBotSmartCriticals() {
      return new BooleanSetting("Умные криты", false);
   }

   public static SliderSetting clientSoundsVolume() {
      return new SliderSetting("Громкость", 100.0, 0.0, 100.0, 1.0);
   }

   public static BooleanSetting autoTpOnlyFriends() {
      return new BooleanSetting("Только друзья", true);
   }
}
