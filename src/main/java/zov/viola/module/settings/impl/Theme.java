package zov.viola.module.settings.impl;

import java.awt.Color;
import zov.viola.obf.D;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class Theme {
   public Animation animation = new Animation(Easing.QUINTIC_OUT, 550L);
   public Animation checkAnimation = new Animation(Easing.QUINTIC_OUT, 350L);
   public float x;
   public float y;
   public String name;
   public int color1;
   public int color2;
   public int colorMain;
   public int colorVisualModules;
   public int colorText;
   public int colorInactiveText;
   public int colorHeaderBg;
   public int colorHeaderText;
   public int colorSlider;
   public int colorSliderCircle;
   public int colorSliderWindow;
   public int colorIndicator;
   public int colorInactiveIndicator;
   public int colorButton;
   public int colorInactiveButton;
   public int colorSeparator;
   public int colorField;
   public int colorInactiveField;
   public int colorTooltipText;
   public int colorWindowBg;
   public int colorIcons;
   public int colorClient;
   public int colorClickGui = -13619152;
   public int colorInterfaceBg = -16777216;
   private int yLYOQa;
   private int vrugke6;

   public Theme(String name, int color1, int color2) {
      this.name = name;
      this.color1 = this.sBucuto(color1);
      this.color2 = this.sBucuto(color2);
      this.yLYOQa = this.color1;
      this.vrugke6 = this.color2;
      this.godABV(this.color1, this.color2);
   }

   private void godABV(int c1, int c2) {
      this.colorMain = -14671840;
      this.colorVisualModules = c1;
      this.colorText = -1513240;
      this.colorInactiveText = -7697782;
      this.colorHeaderBg = -14671840;
      this.colorHeaderText = c1;
      this.colorSlider = c1;
      this.colorSliderCircle = -1;
      this.colorSliderWindow = -12961222;
      this.colorIndicator = c1;
      this.colorInactiveIndicator = -12961222;
      this.colorButton = c1;
      this.colorInactiveButton = -12961222;
      this.colorSeparator = -12961222;
      this.colorField = -14013910;
      this.colorInactiveField = c2;
      this.colorTooltipText = -1;
      this.colorWindowBg = c2;
      this.colorIcons = c1;
      this.colorClient = c1;
   }

   public void setColors(int newColor1, int newColor2) {
      int sat1 = this.sBucuto(newColor1);
      int sat2 = this.sBucuto(newColor2);
      if (this.color1 != sat1 || this.color2 != sat2) {
         this.startAnimation(this.color1, this.color2);
         this.color1 = sat1;
         this.color2 = sat2;
      }
   }

   public void setAllColors(
      int c1,
      int c2,
      int main,
      int visualModules,
      int text,
      int inactiveText,
      int headerBg,
      int headerText,
      int slider,
      int sliderCircle,
      int sliderWindow,
      int indicator,
      int inactiveIndicator,
      int button,
      int inactiveButton,
      int separator,
      int field,
      int inactiveField,
      int tooltipText,
      int windowBg,
      int icons,
      int client
   ) {
      int sat1 = this.sBucuto(c1);
      int sat2 = this.sBucuto(c2);
      if (this.color1 != sat1 || this.color2 != sat2) {
         this.startAnimation(this.color1, this.color2);
         this.color1 = sat1;
         this.color2 = sat2;
      }

      this.colorMain = main;
      this.colorVisualModules = visualModules;
      this.colorText = text;
      this.colorInactiveText = inactiveText;
      this.colorHeaderBg = headerBg;
      this.colorHeaderText = headerText;
      this.colorSlider = slider;
      this.colorSliderCircle = sliderCircle;
      this.colorSliderWindow = sliderWindow;
      this.colorIndicator = indicator;
      this.colorInactiveIndicator = inactiveIndicator;
      this.colorButton = button;
      this.colorInactiveButton = inactiveButton;
      this.colorSeparator = separator;
      this.colorField = field;
      this.colorInactiveField = inactiveField;
      this.colorTooltipText = tooltipText;
      this.colorWindowBg = windowBg;
      this.colorIcons = icons;
      this.colorClient = client;
   }

   public void setAccent(int c) {
      this.color1 = c;
      this.yLYOQa = c;
      this.colorClient = c;
      this.colorVisualModules = c;
      this.colorIndicator = c;
      this.colorButton = c;
      this.colorSlider = c;
      this.colorHeaderText = c;
   }

   public void startAnimation(int oldColor1, int oldColor2) {
      this.yLYOQa = oldColor1;
      this.vrugke6 = oldColor2;
      this.animation.reset();
   }

   public int getColorFirst() {
      this.animation.run(true);
      float progress = Math.max(0.0F, Math.min(1.0F, this.animation.getValue()));
      return interpolateColorClean(this.yLYOQa, this.color1, progress);
   }

   public int getColorSecond() {
      float progress = Math.max(0.0F, Math.min(1.0F, this.animation.getValue()));
      return interpolateColorClean(this.vrugke6, this.color2, progress);
   }

   public int getColorTheme(int index) {
      return getGradientClean(3, index, this.getColorFirst(), this.getColorSecond());
   }

   public int getStaticColorTheme(int index) {
      return getGradientClean(Integer.MAX_VALUE, index, this.getColorFirst(), this.getColorSecond());
   }

   public static int getGradientClean(int speed, int index, int c1, int c2) {
      if (speed != Integer.MAX_VALUE && speed != 0) {
         int time = (int)(System.currentTimeMillis() / Math.max(1, speed) + index);
         float angle = time % 360 / 360.0F;
         float factor = (float)(-Math.cos(angle * Math.PI * 2.0) * 0.5 + 0.5);
         return interpolateColorClean(c1, c2, factor);
      } else {
         return c1;
      }
   }

   public static int interpolateColorClean(int color1, int color2, float amount) {
      amount = Math.min(1.0F, Math.max(0.0F, amount));
      int a1 = color1 >> 24 & 0xFF;
      int r1 = color1 >> 16 & 0xFF;
      int g1 = color1 >> 8 & 0xFF;
      int b1 = color1 & 0xFF;
      int a2 = color2 >> 24 & 0xFF;
      int r2 = color2 >> 16 & 0xFF;
      int g2 = color2 >> 8 & 0xFF;
      int b2 = color2 & 0xFF;
      return (int)(a1 + (a2 - a1) * amount) << 24 | (int)(r1 + (r2 - r1) * amount) << 16 | (int)(g1 + (g2 - g1) * amount) << 8 | (int)(b1 + (b2 - b1) * amount);
   }

   public void drawTheme(double alpha) {
      if (this.name != null && this.name.equals("Радужный")) {
         int[] rainbowColors = new int[4];

         for (int i = 0; i < 4; i++) {
            rainbowColors[i] = Color.HSBtoRGB(i / 4.0F, 0.8F, 1.0F);
         }

         DrawUtil.drawRound(
            this.x,
            this.y,
            16.0F,
            15.0F,
            1.0F,
            ColorProvider.setAlpha(rainbowColors[0], alpha),
            ColorProvider.setAlpha(rainbowColors[1], alpha),
            ColorProvider.setAlpha(rainbowColors[2], alpha),
            ColorProvider.setAlpha(rainbowColors[3], alpha)
         );
      } else {
         String renderName = this.name != null ? this.name : "Custom";
         DrawUtil.drawText(
            Fonts.SFREGULAR.get(),
            renderName,
            this.x + 2.0F,
            this.y + 2.25F,
            ColorProvider.setAlpha(-1, (160.0F + this.checkAnimation.getValue() * 95.0F) * alpha),
            7.0F
         );
         DrawUtil.drawRound(
            this.x + 71.5F,
            this.y,
            19.0F,
            9.0F,
            2.0F,
            ColorProvider.brighter(ColorProvider.setAlpha(this.color1, alpha * 255.0), 0.5F + this.checkAnimation.getValue() * 0.5F),
            ColorProvider.brighter(ColorProvider.setAlpha(this.color1, alpha * 255.0), 0.5F + this.checkAnimation.getValue() * 0.5F),
            ColorProvider.brighter(ColorProvider.setAlpha(this.color2, alpha * 255.0), 0.5F + this.checkAnimation.getValue() * 0.5F),
            ColorProvider.brighter(ColorProvider.setAlpha(this.color2, alpha * 255.0), 0.5F + this.checkAnimation.getValue() * 0.5F)
         );
      }
   }

   private int sBucuto(int color) {
      int alpha = color >> 24 & 0xFF;
      float[] hsb = Color.RGBtoHSB(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, null);
      hsb[1] = Math.min(1.0F, hsb[1] * 1.5F);
      int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
      return rgb & 16777215 | alpha << 24;
   }
}
