package zov.viola.module.list.render;

import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.module.settings.impl.ThemeManager;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "AtmoDawnFog",
   moduleDesc = "Кинематографичная атмосфера: туман, лучи света, заря, ночь и снег",
   moduleCategory = ModuleCategory.RENDER
)
public class AtmoDawnFog extends Module {
   public final ModeSetting rezhim = new ModeSetting(
      "Режим",
      "Рассвет",
      "Рассвет",
      "Сумерки",
      "Тема",
      "Ночь"
   );
   public final SliderSetting plotnost = new SliderSetting(
      "Плотность", 0.35, 0.05, 0.8, 0.01
   );
   public final SliderSetting vysotaRasseivaniya = new SliderSetting(
      "Высота рассеивания",
      76.0,
      60.0,
      120.0,
      1.0
   );
   public final SliderSetting luchiSveta = new SliderSetting(
      "Лучи света", 0.75, 0.0, 1.0, 0.01
   );
   public final SliderSetting myagkost = new SliderSetting(
      "Мягкость", 0.6, 0.0, 1.0, 0.01
   );
   public final BooleanSetting raduga = new BooleanSetting("Радуга", true);
   public final SliderSetting yarkostRadugi = new SliderSetting(
         "Яркость радуги", 0.55, 0.1, 1.0, 0.01
      )
      .setVisible(() -> this.raduga.getValue());
   public final SliderSetting razmerRadugi = new SliderSetting(
         "Размер радуги", 54.0, 46.0, 60.0, 0.5
      )
      .setVisible(() -> this.raduga.getValue());
   public final ColorSetting tsvetZari = new ColorSetting(
         "Цвет зари", -21126
      )
      .setVisible(() -> this.rezhim.is("Рассвет"));
   public final BooleanSetting sneg = new BooleanSetting("Снег", true);
   public final SliderSetting intensivnostSnega = new SliderSetting(
         D.k(
            new int[]{1130, 1132, 1230, 1058, 1103, 1040, 1204, 1061, 1103, 1135, 1229, 1109, 1086, 113, 1229, 1066, 1095, 1122, 1212},
            new int[]{114, 81, 140, 23}
         ),
         0.35,
         0.05,
         2.0,
         0.01
      )
      .setVisible(() -> this.sneg.getValue());
   public final SliderSetting skorostSnega = new SliderSetting(
         "Скорость снега", 0.35, 0.1, 2.0, 0.01
      )
      .setVisible(() -> this.sneg.getValue());
   public final SliderSetting razmerSnega = new SliderSetting(
         "Размер снежинок",
         0.7,
         0.5,
         2.5,
         0.05
      )
      .setVisible(() -> this.sneg.getValue());

   public int getModeIndex() {
      if (this.rezhim.is("Сумерки")) {
         return 1;
      } else if (this.rezhim.is("Тема")) {
         return 2;
      } else {
         return this.rezhim.is("Ночь") ? 3 : 0;
      }
   }

   public int resolveColor(int mode) {
      if (mode == 1) {
         return 13203624;
      } else if (mode == 2) {
         try {
            return ThemeManager.getInstance().getCurrentTheme().getColorFirst() & 16777215;
         } catch (Throwable var3) {
            return 8230143;
         }
      } else {
         return mode == 3 ? 3158064 : this.tsvetZari.getValue() & 16777215;
      }
   }

   public float getPlotnost() {
      return this.plotnost.getFloatValue();
   }

   public float getVysotaRasseivaniya() {
      return this.vysotaRasseivaniya.getFloatValue();
   }

   public float getLuchiSveta() {
      return this.luchiSveta.getFloatValue();
   }

   public float getMyagkost() {
      return this.myagkost.getFloatValue();
   }

   public boolean isRadugaEnabled() {
      return this.raduga.getValue();
   }

   public float getYarkostRadugi() {
      return this.yarkostRadugi.getFloatValue();
   }

   public float getRazmerRadugi() {
      return this.razmerRadugi.getFloatValue();
   }

   public boolean isSnowEnabled() {
      return this.sneg.getValue();
   }

   public float getIntensivnostSnega() {
      return this.intensivnostSnega.getFloatValue();
   }

   public float getSkorostSnega() {
      return this.skorostSnega.getFloatValue();
   }

   public float getRazmerSnega() {
      return this.razmerSnega.getFloatValue();
   }
}
