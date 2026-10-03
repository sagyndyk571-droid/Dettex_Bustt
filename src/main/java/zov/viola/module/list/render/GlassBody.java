package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.body.GlassBodyRenderer;

@ModuleInformation(
   moduleName = "Glass Body",
   moduleDesc = "Стеклянное тело (Glass Hands без блюра)",
   moduleCategory = ModuleCategory.RENDER
)
public class GlassBody extends Module {
   public final ModeSetting mode = new ModeSetting(
      "Режим",
      "Стекло",
      "Стекло",
      "Заливка",
      "Волна"
   );
   public final SliderSetting glassMixFactor = new SliderSetting(
         "Смешивание", 0.0, 0.0, 1.0, 0.01
      )
      .setVisible(() -> this.mode.is("Стекло"));
   public final BooleanSetting glassGlowEnabled = new BooleanSetting("Глов", true)
      .setVisible(() -> this.mode.is("Стекло"));
   public final SliderSetting glassGlowRadius = new SliderSetting(
         "Радиус глова", 3.0, 1.0, 6.0, 1.0
      )
      .setVisible(() -> this.mode.is("Стекло") && this.glassGlowEnabled.getValue());
   public final BooleanSetting glassOuterGlow = new BooleanSetting(
         "Внешний глов", true
      )
      .setVisible(() -> this.mode.is("Стекло") && this.glassGlowEnabled.getValue());
   public final SliderSetting glassGlowExposure = new SliderSetting(
         "Яркость глова", 2.0, 0.5, 5.0, 0.1
      )
      .setVisible(
         () -> this.mode.is("Стекло")
            && this.glassGlowEnabled.getValue()
            && this.glassOuterGlow.getValue()
      );
   public final ColorSetting glassGlowColor1 = new ColorSetting(
         "Цвет глова 1",
         new Color(138, 152, 255, 255).getRGB()
      )
      .setVisible(() -> this.mode.is("Стекло") && this.glassGlowEnabled.getValue());
   public final ColorSetting glassGlowColor2 = new ColorSetting(
         "Цвет глова 2",
         new Color(255, 107, 172, 255).getRGB()
      )
      .setVisible(() -> this.mode.is("Стекло") && this.glassGlowEnabled.getValue());
   public final BooleanSetting glassRainbow = new BooleanSetting(
         "Радужный цвет", false
      )
      .setVisible(() -> this.mode.is("Стекло") && this.glassGlowEnabled.getValue());
   public final SliderSetting glassRainbowSpeed = new SliderSetting(
         "Скорость радуги",
         1.0,
         0.1,
         5.0,
         0.1
      )
      .setVisible(
         () -> this.mode.is("Стекло")
            && this.glassGlowEnabled.getValue()
            && this.glassRainbow.getValue()
      );
   public final ColorSetting fillColor = new ColorSetting(
         "Цвет заливки", new Color(255, 68, 68, 255).getRGB()
      )
      .setVisible(() -> this.mode.is("Заливка"));
   public final BooleanSetting fillRainbow = new BooleanSetting(
         "Радужный цвет", false
      )
      .setVisible(() -> this.mode.is("Заливка"));
   public final SliderSetting fillRainbowSpeed = new SliderSetting(
         "Скорость радуги",
         1.0,
         0.1,
         5.0,
         0.1
      )
      .setVisible(() -> this.mode.is("Заливка") && this.fillRainbow.getValue());
   public final SliderSetting fillAlpha = new SliderSetting(
         D.k(
            new int[]{1126, 1169, 1030, 1208, 1081, 1249, 1151, 1202, 1095, 1168, 1146, 1219, 89, 1254, 1032, 1204, 1089, 1251, 1026, 1207},
            new int[]{121, 209, 56, 143}
         ),
         0.8,
         0.0,
         1.0,
         0.05
      )
      .setVisible(() -> this.mode.is("Заливка"));
   public final BooleanSetting fillKeepShading = new BooleanSetting(
         "Сохранить тени", true
      )
      .setVisible(() -> this.mode.is("Заливка"));
   public final SliderSetting fillShadingStrength = new SliderSetting(
         "Сила теней", 0.3, 0.0, 1.0, 0.05
      )
      .setVisible(
         () -> this.mode.is("Заливка") && this.fillKeepShading.getValue()
      );
   public final BooleanSetting fillOutlineEnabled = new BooleanSetting(
         "Обводка", false
      )
      .setVisible(() -> this.mode.is("Заливка"));
   public final SliderSetting fillOutlineWidth = new SliderSetting(
         "Толщина обводки",
         1.0,
         0.5,
         3.0,
         0.5
      )
      .setVisible(
         () -> this.mode.is("Заливка") && this.fillOutlineEnabled.getValue()
      );
   public final ColorSetting fillOutlineColor = new ColorSetting(
         "Цвет обводки",
         new Color(138, 152, 255, 255).getRGB()
      )
      .setVisible(
         () -> this.mode.is("Заливка") && this.fillOutlineEnabled.getValue()
      );
   public final BooleanSetting fillGlowEnabled = new BooleanSetting("Глов", true)
      .setVisible(() -> this.mode.is("Заливка"));
   public final SliderSetting fillGlowRadius = new SliderSetting(
         "Размытие", 4.0, 1.0, 6.0, 1.0
      )
      .setVisible(() -> this.mode.is("Заливка") && this.fillGlowEnabled.getValue());
   public final BooleanSetting fillOuterGlow = new BooleanSetting(
         "Внешний глов", true
      )
      .setVisible(() -> this.mode.is("Заливка") && this.fillGlowEnabled.getValue());
   public final SliderSetting fillGlowExposure = new SliderSetting(
         "Яркость", 2.0, 0.5, 5.0, 0.1
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
      );
   public final BooleanSetting fillAutoColor = new BooleanSetting(
         "Авто цвет", false
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && (this.fillGlowEnabled.getValue() || this.fillOutlineEnabled.getValue())
      );
   public final SliderSetting fillSaturation = new SliderSetting(
         "Насыщенность", 1.4, 0.5, 3.0, 0.1
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && (this.fillGlowEnabled.getValue() || this.fillOutlineEnabled.getValue())
            && this.fillAutoColor.getValue()
      );
   public final ColorSetting fillGlowColor1 = new ColorSetting(
         "Цвет глова 1", new Color(138, 152, 255, 255).getRGB()
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && !this.fillAutoColor.getValue()
      );
   public final ColorSetting fillGlowColor2 = new ColorSetting(
         "Цвет глова 2",
         new Color(255, 107, 172, 255).getRGB()
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && !this.fillAutoColor.getValue()
      );
   public final SliderSetting waveSpeedX = new SliderSetting(
         "Скорость X", 0.22, 0.0, 1.5, 0.01
      )
      .setVisible(() -> this.mode.is("Волна"));
   public final SliderSetting waveSpeedY = new SliderSetting(
         "Скорость Y", 0.15, 0.0, 1.5, 0.01
      )
      .setVisible(() -> this.mode.is("Волна"));
   public final SliderSetting waveScaleM = new SliderSetting(
         "Масштаб", 1.35, 0.2, 4.0, 0.05
      )
      .setVisible(() -> this.mode.is("Волна"));
   public final SliderSetting waveDensity = new SliderSetting(
         "Плотность", 1.15, 0.5, 3.0, 0.05
      )
      .setVisible(() -> this.mode.is("Волна"));
   public final SliderSetting waveGlow = new SliderSetting(
         "Сила волн", 1.0, 0.2, 3.0, 0.05
      )
      .setVisible(() -> this.mode.is("Волна"));
   public final SliderSetting waveFillAlpha = new SliderSetting(
         "Прозрачность", 0.85, 0.1, 1.0, 0.05
      )
      .setVisible(() -> this.mode.is("Волна"));
   public final SliderSetting waveModelVisibility = new SliderSetting(
         "Видимость модели",
         0.25,
         0.0,
         1.0,
         0.05
      )
      .setVisible(() -> this.mode.is("Волна"));
   public final BooleanSetting waveGlowEnabled = new BooleanSetting("Глов", true)
      .setVisible(() -> this.mode.is("Волна"));
   public final SliderSetting waveGlowRadius = new SliderSetting(
         "Размытие", 4.0, 1.0, 6.0, 1.0
      )
      .setVisible(() -> this.mode.is("Волна") && this.waveGlowEnabled.getValue());
   public final BooleanSetting waveOuterGlow = new BooleanSetting(
         "Внешний глов", true
      )
      .setVisible(() -> this.mode.is("Волна") && this.waveGlowEnabled.getValue());
   public final SliderSetting waveGlowExposure = new SliderSetting(
         "Яркость", 2.0, 0.5, 5.0, 0.1
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
      );
   public final BooleanSetting waveAutoColor = new BooleanSetting(
         "Авто цвет", false
      )
      .setVisible(() -> this.mode.is("Волна") && this.waveGlowEnabled.getValue());
   public final SliderSetting waveSaturation = new SliderSetting(
         "Насыщенность", 1.4, 0.5, 3.0, 0.1
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveAutoColor.getValue()
      );
   public final ColorSetting waveGlowColor1 = new ColorSetting(
         "Цвет глова 1", new Color(138, 152, 255, 255).getRGB()
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && !this.waveAutoColor.getValue()
      );
   public final ColorSetting waveGlowColor2 = new ColorSetting(
         "Цвет глова 2", new Color(255, 107, 172, 255).getRGB()
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && !this.waveAutoColor.getValue()
      );
   public final BooleanSetting waveRainbow = new BooleanSetting(
         "Радужный цвет", false
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && !this.waveAutoColor.getValue()
      );
   public final SliderSetting waveRainbowSpeed = new SliderSetting(
         "Скорость радуги", 1.0, 0.1, 5.0, 0.1
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && !this.waveAutoColor.getValue()
            && this.waveRainbow.getValue()
      );

   @Override
   public void onDisable() {
      GlassBodyRenderer.getInstance().invalidate();
      super.onDisable();
   }

   @Subscribe
   public void onWorldRender(EventWorldRender event) {
      GlassBodyRenderer.getInstance().renderBody(this, event.getMatrixStack(), event.getTickDelta());
   }
}
