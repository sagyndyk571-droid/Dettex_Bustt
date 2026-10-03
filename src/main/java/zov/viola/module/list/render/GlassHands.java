package zov.viola.module.list.render;

import java.awt.Color;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.hands.GlassHandsRenderer;

@ModuleInformation(
   moduleName = "Hands",
   moduleDesc = "Красивые Шейдеры на руки",
   moduleCategory = ModuleCategory.RENDER
)
public class GlassHands extends Module {
   public final ModeSetting mode = new ModeSetting(
      "Режим",
      "Блюр",
      "Блюр",
      "Стекло",
      "Заливка",
      "Волна"
   );
   public final SliderSetting waveSpeed = new SliderSetting(
         "Скорость волн", 1.2, 0.1, 5.0, 0.1
      )
      .setVisible(() -> this.mode.is("Красивый"));
   public final SliderSetting waveScale = new SliderSetting(
         "Частота волн", 1.0, 1.0, 3.0, 0.1
      )
      .setVisible(() -> this.mode.is("Красивый"));
   public final SliderSetting outline = new SliderSetting(
         "Ширина обводки", 1.2, 0.01, 5.0, 0.01
      )
      .setVisible(
         () -> !this.mode.is("Блюр")
            && !this.mode.is("Обводка")
            && !this.mode.is("Стекло")
            && !this.mode.is("Заливка")
            && !this.mode.is("Волна")
      );
   public final SliderSetting glow = new SliderSetting(
         "Сила свечения", 1.0, 0.0, 5.0, 0.01
      )
      .setVisible(
         () -> !this.mode.is("Блюр")
            && !this.mode.is("Обводка")
            && !this.mode.is("Стекло")
            && !this.mode.is("Заливка")
            && !this.mode.is("Волна")
      );
   public final SliderSetting fill = new SliderSetting(
         "Заливка", 0.6, 0.0, 1.0, 0.01
      )
      .setVisible(
         () -> !this.mode.is("Блюр")
            && !this.mode.is("Обводка")
            && !this.mode.is("Стекло")
            && !this.mode.is("Заливка")
            && !this.mode.is("Волна")
      );
   public final SliderSetting alpha = new SliderSetting(
         "Прозрачность", 1.0, 0.0, 1.0, 0.05
      )
      .setVisible(
         () -> !this.mode.is("Блюр")
            && !this.mode.is("Обводка")
            && !this.mode.is("Стекло")
            && !this.mode.is("Заливка")
            && !this.mode.is("Волна")
      );
   public final BooleanSetting glowItemColor = new BooleanSetting(
         "Цвет предмета", false
      )
      .setVisible(() -> this.mode.is("Свечение"));
   public final SliderSetting blurStrength = new SliderSetting(
         "Сила блюра", 4.0, 1.0, 8.0, 1.0
      )
      .setVisible(() -> this.mode.is("Блюр"));
   public final SliderSetting blurTint = new SliderSetting(
         "Оттенок", 0.3, 0.0, 1.0, 0.05
      )
      .setVisible(() -> this.mode.is("Блюр"));
   public final BooleanSetting blurRainbow = new BooleanSetting(
         "Радужный цвет", false
      )
      .setVisible(() -> this.mode.is("Блюр"));
   public final SliderSetting blurRainbowSpeed = new SliderSetting(
         "Скорость радуги",
         1.0,
         0.1,
         5.0,
         0.1
      )
      .setVisible(() -> this.mode.is("Блюр") && this.blurRainbow.getValue());
   public final SliderSetting trailIntensity = new SliderSetting(
         "Яркость шлейфа", 0.8, 0.3, 3.0, 0.1
      )
      .setVisible(() -> this.mode.is("Шлейф"));
   public final SliderSetting trailGlowSize = new SliderSetting(
         "Размер свечения",
         2.0,
         0.5,
         5.0,
         0.1
      )
      .setVisible(() -> this.mode.is("Шлейф"));
   public final SliderSetting trailBlur = new SliderSetting(
         "Размытие", 6.0, 1.0, 10.0, 1.0
      )
      .setVisible(() -> this.mode.is("Шлейф"));
   public final BooleanSetting rainbow = new BooleanSetting(
         "Радужный цвет", false
      )
      .setVisible(() -> this.mode.is("Шлейф"));
   public final SliderSetting rainbowSpeed = new SliderSetting(
         "Скорость радуги",
         1.0,
         0.1,
         5.0,
         0.1
      )
      .setVisible(() -> this.mode.is("Шлейф") && this.rainbow.getValue());
   public final SliderSetting trailIntensityM = new SliderSetting(
         "Интенсивность", 0.72, 0.1, 1.5, 0.05
      )
      .setVisible(() -> this.mode.is("Trail"));
   public final SliderSetting trailSpeed = new SliderSetting(
         "Скорость", 1.05, 0.2, 3.0, 0.05
      )
      .setVisible(() -> this.mode.is("Trail"));
   public final SliderSetting trailLength = new SliderSetting(
         "Длина шлейфа", 0.32, 0.1, 1.0, 0.05
      )
      .setVisible(() -> this.mode.is("Trail"));
   public final SliderSetting trailSoftness = new SliderSetting(
         "Мягкость", 1.05, 0.4, 2.0, 0.05
      )
      .setVisible(() -> this.mode.is("Trail"));
   public final SliderSetting trailBlurRadius = new SliderSetting(
         "Размытие", 1.05, 0.2, 2.5, 0.05
      )
      .setVisible(() -> this.mode.is("Trail"));
   public final SliderSetting trailSmoke = new SliderSetting("Дым", 0.22, 0.0, 0.8, 0.05)
      .setVisible(() -> this.mode.is("Trail"));
   public final SliderSetting trailAttack = new SliderSetting("Удар", 0.58, 0.0, 1.0, 0.05)
      .setVisible(() -> this.mode.is("Trail"));
   public final SliderSetting trailCamera = new SliderSetting(
         "Следование камеры",
         0.45,
         0.0,
         1.5,
         0.05
      )
      .setVisible(() -> this.mode.is("Trail"));
   public final BooleanSetting trailItemColor = new BooleanSetting(
         "Цвет предмета", false
      )
      .setVisible(() -> this.mode.is("Trail"));
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
         "Цвет глова 1", new Color(138, 152, 255, 255).getRGB()
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
   public final SliderSetting plasmaSpeed = new SliderSetting(
         "Скорость", 1.0, 0.1, 3.0, 0.1
      )
      .setVisible(() -> this.mode.is("Plasma"));
   public final SliderSetting plasmaScale = new SliderSetting(
         "Масштаб", 1.0, 0.5, 3.0, 0.1
      )
      .setVisible(() -> this.mode.is("Plasma"));
   public final BooleanSetting plasmaStars = new BooleanSetting("Звёзды", true)
      .setVisible(() -> this.mode.is("Plasma"));
   public final BooleanSetting fillGlassMode = new BooleanSetting("Стекло", false)
      .setVisible(() -> this.mode.is("Заливка"));
   public final SliderSetting fillGlassMix = new SliderSetting(
         "Смешивание", 0.0, 0.0, 1.0, 0.01
      )
      .setVisible(() -> this.mode.is("Заливка") && this.fillGlassMode.getValue());
   public final ColorSetting fillColor = new ColorSetting(
         "Цвет заливки", new Color(255, 68, 68, 255).getRGB()
      )
      .setVisible(() -> this.mode.is("Заливка") && !this.fillGlassMode.getValue());
   public final BooleanSetting fillRainbow = new BooleanSetting(
         "Радужный цвет", false
      )
      .setVisible(() -> this.mode.is("Заливка") && !this.fillGlassMode.getValue());
   public final SliderSetting fillRainbowSpeed = new SliderSetting(
         "Скорость радуги",
         1.0,
         0.1,
         5.0,
         0.1
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && !this.fillGlassMode.getValue()
            && this.fillRainbow.getValue()
      );
   public final SliderSetting fillAlpha = new SliderSetting(
         D.k(
            new int[]{1099, 1202, 1175, 1168, 1044, 1218, 1262, 1178, 1130, 1203, 1259, 1259, 116, 1221, 1177, 1180, 1132, 1216, 1171, 1183},
            new int[]{84, 242, 169, 167}
         ),
         0.8,
         0.0,
         1.0,
         0.05
      )
      .setVisible(() -> this.mode.is("Заливка") && !this.fillGlassMode.getValue());
   public final BooleanSetting fillKeepShading = new BooleanSetting(
         "Сохранить тени", true
      )
      .setVisible(() -> this.mode.is("Заливка") && !this.fillGlassMode.getValue());
   public final SliderSetting fillShadingStrength = new SliderSetting(
         "Сила теней", 0.3, 0.0, 1.0, 0.05
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && !this.fillGlassMode.getValue()
            && this.fillKeepShading.getValue()
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
         "Цвет глова 1",
         new Color(138, 152, 255, 255).getRGB()
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
   public final BooleanSetting fillTrailEnabled = new BooleanSetting("Шлейф", true)
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
      );
   public final SliderSetting fillTrailFade = new SliderSetting(
         "Скорость затухания",
         0.009,
         0.002,
         0.2,
         0.002
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
      );
   public final SliderSetting fillTrailRise = new SliderSetting(
         "Подъём", 0.14, 0.0, 1.5, 0.05
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
      );
   public final SliderSetting fillTrailSway = new SliderSetting(
         "Качание", 0.025, 0.0, 0.2, 0.005
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
      );
   public final SliderSetting fillTrailTurb = new SliderSetting(
         "Турбулентность", 0.0, 0.0, 0.6, 0.01
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
      );
   public final SliderSetting fillTrailFlicker = new SliderSetting(
         "Мерцание", 0.0, 0.0, 0.2, 0.01
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
      );
   public final BooleanSetting fillTrailBurst = new BooleanSetting(
         "Сдув при ударе", true
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
      );
   public final SliderSetting fillTrailBurstPower = new SliderSetting(
         "Сила сдува", 2.5, 1.0, 10.0, 0.5
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
            && this.fillTrailBurst.getValue()
      );
   public final BooleanSetting fillTrailModel = new BooleanSetting(
         "Шлейф модели", true
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
      );
   public final SliderSetting fillTrailModelAlpha = new SliderSetting(
         D.k(
            new int[]{1113, 1056, 1249, 1168, 1030, 1104, 1176, 1178, 1144, 1057, 1181, 1259, 102, 1116, 1249, 1171, 1139, 1115, 1255},
            new int[]{70, 96, 223, 167}
         ),
         0.4,
         0.1,
         1.0,
         0.05
      )
      .setVisible(
         () -> this.mode.is("Заливка")
            && this.fillGlowEnabled.getValue()
            && this.fillOuterGlow.getValue()
            && this.fillTrailEnabled.getValue()
            && this.fillTrailModel.getValue()
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
   public final BooleanSetting waveTrailEnabled = new BooleanSetting("Шлейф", true)
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
      );
   public final SliderSetting waveTrailFade = new SliderSetting(
         D.k(
            new int[]{1253, 1232, 1214, 1238, 1274, 1195, 1218, 1242, 228, 1245, 1200, 1236, 1159, 1199, 1200, 1195, 1276, 1189}, new int[]{196, 234, 128, 150}
         ),
         0.009,
         0.002,
         0.2,
         0.002
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
      );
   public final SliderSetting waveTrailRise = new SliderSetting(
         "Подъём", 0.14, 0.0, 1.5, 0.05
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
      );
   public final SliderSetting waveTrailSway = new SliderSetting(
         "Качание", 0.025, 0.0, 0.2, 0.005
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
      );
   public final SliderSetting waveTrailTurb = new SliderSetting(
         "Турбулентность", 0.0, 0.0, 0.6, 0.01
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
      );
   public final SliderSetting waveTrailFlicker = new SliderSetting(
         "Мерцание", 0.0, 0.0, 0.2, 0.01
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
      );
   public final BooleanSetting waveTrailBurst = new BooleanSetting(
         "Сдув при ударе", true
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
      );
   public final SliderSetting waveTrailBurstPower = new SliderSetting(
         "Сила сдува", 2.5, 1.0, 10.0, 0.5
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
            && this.waveTrailBurst.getValue()
      );
   public final BooleanSetting waveTrailModel = new BooleanSetting(
         "Шлейф модели", true
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
      );
   public final SliderSetting waveTrailModelAlpha = new SliderSetting(
         D.k(
            new int[]{1157, 1270, 1157, 1131, 1242, 1158, 1276, 1121, 1188, 1271, 1273, 1040, 186, 1162, 1157, 1128, 1199, 1165, 1155},
            new int[]{154, 182, 187, 92}
         ),
         0.4,
         0.1,
         1.0,
         0.05
      )
      .setVisible(
         () -> this.mode.is("Волна")
            && this.waveGlowEnabled.getValue()
            && this.waveOuterGlow.getValue()
            && this.waveTrailEnabled.getValue()
            && this.waveTrailModel.getValue()
      );
   private final GlassHandsRenderer qArlLi = GlassHandsRenderer.getInstance();

   @Override
   public void onDisable() {
      this.qArlLi.invalidateState();
      super.onDisable();
   }
}
