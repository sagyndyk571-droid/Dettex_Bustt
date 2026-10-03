package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.EventTick;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.module.settings.impl.ThemeManager;
import zov.viola.obf.D;
import zov.viola.util.render.ambience.SkyShaderRenderer;

@ModuleInformation(
   moduleName = "Ambience",
   moduleDesc = "Кастомное время и туман",
   moduleCategory = ModuleCategory.RENDER
)
public class Ambience extends Module {
   public final ModeSetting timePreset = new ModeSetting(
      "Время",
      "День",
      "День",
      "Рассвет",
      "Закат",
      "Сумерки",
      "Ночь",
      "Полночь"
   );
   public final BooleanSetting customFog = new BooleanSetting("Туман", false);
   public final SliderSetting fogDistance = new SliderSetting(
         "Дальность тумана",
         100.0,
         10.0,
         500.0,
         10.0
      )
      .setVisible(() -> this.customFog.getValue());
   public final SliderSetting fogDensity = new SliderSetting(
         "Плотность тумана",
         0.75,
         0.0,
         2.0,
         0.05
      )
      .setVisible(() -> this.customFog.getValue());
   public final BooleanSetting themeFogColor = new BooleanSetting(
         D.k(
            new int[]{1145, 1129, 1166, 1053, 127, 1049, 1272, 1123, 1135, 1126, 1163, 127, 1121, 1049, 155, 1053, 1130, 1127, 1264},
            new int[]{95, 91, 187, 95}
         ),
         false
      )
      .setVisible(() -> this.customFog.getValue());
   public final ColorSetting fogColor = new ColorSetting(
         "Цвет тумана", -6250336
      )
      .setVisible(() -> this.customFog.getValue() && !this.themeFogColor.getValue());
   public final BooleanSetting skyShader = new BooleanSetting(
      "Шейдер неба", false
   );
   public final ModeSetting skyShaderMode = new ModeSetting(
         "Режим",
         "Aurora",
         "Aurora",
         "Energy",
         "Nebula",
         "Cosmic Veil",
         "Deep Space",
         "Matrix",
         "Void",
         "Plasma",
         "IQ Plasma",
         "Sakura",
         "Summer",
         "FogBlur",
         "Black Hole",
         "Pulse Nebula"
      )
      .setVisible(() -> this.skyShader.getValue());
   public final SliderSetting plasmaScale = new SliderSetting(
         "Плазма: масштаб",
         1.0,
         0.2,
         3.0,
         0.05
      )
      .setVisible(() -> this.skyShader.getValue() && this.skyShaderMode.getValue().equals("Plasma"));
   public final SliderSetting plasmaSpeed = new SliderSetting(
         "Плазма: скорость",
         1.0,
         0.0,
         3.0,
         0.05
      )
      .setVisible(() -> this.skyShader.getValue() && this.skyShaderMode.getValue().equals("Plasma"));
   public final BooleanSetting showStars = new BooleanSetting("Звёзды", true)
      .setVisible(() -> this.skyShader.getValue());
   public final BooleanSetting syncWithTheme = new BooleanSetting(
         D.k(
            new int[]{1239, 1180, 1169, 1173, 209, 1254, 1169, 1262, 1221, 1179, 1252, 1255, 209, 1168, 1254, 247, 1203, 1179, 1176, 1180},
            new int[]{241, 174, 164, 215}
         ),
         true
      )
      .setVisible(() -> this.skyShader.getValue());
   public final ColorSetting shaderColor = new ColorSetting(
         "Цвет шейдера", -9673774
      )
      .setVisible(() -> this.skyShader.getValue() && !this.syncWithTheme.getValue());
   public final BooleanSetting removeFog = new BooleanSetting(
         "Удалять туман", true
      )
      .setVisible(() -> this.skyShader.getValue());
   public final BooleanSetting worldSaturation = new BooleanSetting(
      "Насыщенность мира", false
   );
    public final SliderSetting saturationValue = new SliderSetting(
         "Насыщенность", 1.0, 0.0, 3.0, 0.05
      )
      .setVisible(() -> this.worldSaturation.getValue());
    public final BooleanSetting wetRain = new BooleanSetting(
       "Мокрый дождь", false
    );
    public final BooleanSetting winter = new BooleanSetting(
       "Зима", false
    );
    private long duOh = 0L;

   @Override
   public void onEnable() {
      super.onEnable();
      this.duOh = System.currentTimeMillis();
      if (this.skyShader.getValue()) {
         this.qGyV();
      }
   }

    @Subscribe
    public void onTick(EventTick event) {
       if (this.skyShader.getValue()) {
          this.qGyV();
       }
       if ((this.wetRain.getValue() || this.winter.getValue()) && this.mc.world != null) {
          this.mc.world.getLevelProperties().setRaining(true);
          this.mc.world.setRainGradient(1.0F);
       }
    }

   @Subscribe
   public void onWorldRender(EventWorldRender event) {
      if (this.skyShader.getValue()) {
         this.qGyV();
      }
   }

   private void qGyV() {
      SkyShaderRenderer.updateConfig(this);
   }

   public long getShaderStartTime() {
      return this.duOh;
   }

   public long getCustomTime() {
      String var1 = this.timePreset.getValue();

      return switch (var1) {
         case "Рассвет" -> 23000L;
         case "День" -> 6000L;
         case "Закат" -> 12500L;
         case "Сумерки" -> 13500L;
         case "Ночь" -> 16000L;
         case "Полночь" -> 18000L;
         default -> 6000L;
      };
   }

   public boolean isFogEnabled() {
      return this.customFog.getValue();
   }

   public float getFogDistance() {
      return (float)this.fogDistance.getValue();
   }

   public float getFogDensity() {
      return this.fogDensity.getFloatValue();
   }

   public boolean isThemeFogColorEnabled() {
      return this.themeFogColor.getValue();
   }

   public int getFogColor() {
      return this.themeFogColor.getValue() ? ThemeManager.getInstance().getCurrentTheme().getColorFirst() : this.fogColor.getValue();
   }

   public int getSkyShaderColor() {
      return this.syncWithTheme.getValue() ? ThemeManager.getInstance().getCurrentTheme().getColorFirst() : this.shaderColor.getValue();
   }

   public boolean isSkyShaderEnabled() {
      return this.skyShader.getValue();
   }

   public String getSkyShaderMode() {
      return this.skyShaderMode.getValue();
   }

   public float getStarDensity() {
      return 0.7F;
   }

   public float getNebulaStrength() {
      return 0.8F;
   }

   public float getNebulaSpeed() {
      return 0.5F;
   }

   public float getPlasmaScale() {
      return this.plasmaScale.getFloatValue();
   }

   public float getPlasmaSpeed() {
      return this.plasmaSpeed.getFloatValue();
   }

    public boolean isRemoveFogEnabled() {
       return this.removeFog.getValue();
    }

    public boolean isWetRain() {
       return this.wetRain.getValue();
    }

    public boolean isWinter() {
       return this.winter.getValue();
    }

    @Override
    public void onDisable() {
       if (this.mc.world != null && !this.wetRain.getValue() && !this.winter.getValue()) {
          this.mc.world.getLevelProperties().setRaining(false);
       }
       super.onDisable();
    }
}
