package zov.viola.module.list.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.entity.EntityType;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.providers.ResourceProvider;

@ModuleInformation(
   moduleName = "Chams",
   moduleDesc = "Рисует игроков моделькой как в Totem Angel",
   moduleCategory = ModuleCategory.RENDER
)
public class Chams extends Module {
   private static final ShaderProgramKey FILL_SHADER = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("chams_fill"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY
   );
   private static final ShaderProgramKey WAVE_SHADER = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("chams_wave"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY
   );
   private static Chams hkwUr6h;
   private final ModeSetting uCu94B = new ModeSetting(
      "Режим",
      "Vanilla",
      "Vanilla",
      "Animated Fill",
      "Wave"
   );
   private final BooleanSetting qmr7Seu = new BooleanSetting(
      "Цвет от темы", true
   );
   private final ColorSetting qrYY = new ColorSetting("Свой цвет", -9021441)
      .setVisible(() -> !this.qmr7Seu.getValue());
   private final SliderSetting oe1ojr = new SliderSetting(
      "Прозрачность", 0.35F, 0.05F, 1.0, 0.05F
   );
   private final SliderSetting e25rICV = new SliderSetting(
         D.k(
            new int[]{1105, 1119, 1154, 1109, 1038, 1071, 1275, 1119, 1136, 1118, 1278, 1070, 110, 1111, 1161, 1115, 1146, 1066, 1276, 1106},
            new int[]{78, 31, 188, 98}
         ),
         0.35F,
         0.05F,
         1.0,
         0.05F
      )
      .setVisible(
         () -> this.uCu94B.is("Animated Fill")
            || this.uCu94B.is("Wave")
      );
   private final SliderSetting rgITA1K = new SliderSetting(
      "Яркость", 0.6F, 0.1F, 1.0, 0.05F
   );
   private final SliderSetting qXwK = new SliderSetting(
         "Скорость", 1.0, 0.1F, 3.0, 0.05F
      )
      .setVisible(() -> !this.uCu94B.is("Vanilla"));
   private final SliderSetting uB807 = new SliderSetting(
         "Масштаб", 1.35F, 0.5, 3.0, 0.05F
      )
      .setVisible(() -> this.uCu94B.is("Animated Fill"));
   private final SliderSetting iJa7a = new SliderSetting(
      "Толщина линий", 1.0, 0.1F, 3.0, 0.05F
   );
   private final BooleanSetting uLwcRuf = new BooleanSetting(
      "Сквозь стены", true
   );
   private final BooleanSetting mrmnh = new BooleanSetting("Свечение", true);
   private final BooleanSetting mRpcMs2 = new BooleanSetting("Мигать", false);
   private final SliderSetting kn8d = new SliderSetting(
      "Сила свечения", 1.5, 0.5, 4.0, 0.1F
   );
   private final SliderSetting f9ec15k = new SliderSetting(
      "Слои свечения", 3.0, 1.0, 6.0, 1.0
   );

   public Chams() {
      hkwUr6h = this;
      LivingEntityFeatureRendererRegistrationCallback.EVENT
         .register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType == EntityType.PLAYER) {
               registrationHelper.register(new ChamsRenderer((FeatureRendererContext)entityRenderer));
            }
         });
   }

   static Chams getInstance() {
      return hkwUr6h;
   }

   boolean isMode(String value) {
      return this.uCu94B.is(value);
   }

   int getColor() {
      return this.qmr7Seu.getValue() ? ColorProvider.getColorClient() : this.qrYY.getValue();
   }

   float getAlpha() {
      return this.oe1ojr.getFloatValue();
   }

   float getShaderAlpha() {
      return this.e25rICV.getFloatValue();
   }

   float getBrightness() {
      return this.rgITA1K.getFloatValue();
   }

   float getShaderSpeed() {
      return this.qXwK.getFloatValue();
   }

   float getShaderScale() {
      return this.uB807.getFloatValue();
   }

   float getLineWidth() {
      return this.iJa7a.getFloatValue();
   }

   boolean isThroughWalls() {
      return this.uLwcRuf.getValue();
   }

   boolean hasGlow() {
      return this.mrmnh.getValue();
   }

   boolean isBlinking() {
      return this.mRpcMs2.getValue();
   }

   float getGlowIntensity() {
      return this.kn8d.getFloatValue();
   }

   int getGlowLayers() {
      return Math.max(1, this.f9ec15k.getIntValue());
   }

   static ShaderProgram bindAnimatedFill(float time, float speed, float scale, float glowStrength) {
      ShaderProgram shader = RenderSystem.setShader(FILL_SHADER);
      ypzW(shader, "time", time);
      ypzW(shader, "speedX", 0.22F * speed);
      ypzW(shader, "speedY", 0.15F * speed);
      ypzW(shader, "scale", scale);
      ypzW(shader, "density", 1.15F);
      ypzW(shader, "glowStrength", glowStrength);
      return shader;
   }

   static ShaderProgram bindWave(float time) {
      ShaderProgram shader = RenderSystem.setShader(WAVE_SHADER);
      ypzW(shader, "Time", time);
      return shader;
   }

   private static void ypzW(ShaderProgram shader, String name, float value) {
      if (shader != null && shader.getUniform(name) != null) {
         shader.getUniform(name).set(value);
      }
   }
}
