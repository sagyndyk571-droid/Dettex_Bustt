package zov.viola.util.render.providers;

import java.awt.Color;
import net.minecraft.util.math.MathHelper;
import zov.viola.module.list.render.ClickGui;
import zov.viola.util.base.Instance;
import zov.viola.util.render.math.MathUtil;

public final class ColorProvider {
   public static int red(int c) {
      return c >> 16 & 0xFF;
   }

   public static int green(int c) {
      return c >> 8 & 0xFF;
   }

   public static int blue(int c) {
      return c & 0xFF;
   }

   public static int alpha(int c) {
      return c >> 24 & 0xFF;
   }

   public static int getAccent() {
      ClickGui gui = Instance.get(ClickGui.class);
      if (gui != null) {
         if (gui.rainbow.getValue()) {
            float hue = (float)(System.currentTimeMillis() % 3000L) / 3000.0F;
            return 0xFF000000 | Color.HSBtoRGB(hue, 0.85F, 1.0F);
         }

         if (gui.twoColor.getValue()) {
            double t = System.currentTimeMillis() % 1700L / 1700.0;
            float amount = (float)((Math.sin(t * Math.PI * 2.0) + 1.0) / 2.0);
            return interpolateColor(Palette.ACCENT, Palette.ACCENT2, amount);
         }
      }

      return Palette.ACCENT;
   }

   public static int getThemeColor() {
      return getAccent();
   }

   public static int getThemeColorTwo() {
      ClickGui gui = Instance.get(ClickGui.class);
      return gui != null && gui.twoColor.getValue() ? Palette.ACCENT2 : getAccent();
   }

   public static int getColorMain() {
      return -15329770;
   }

   public static int getColorVisualModules() {
      return getAccent();
   }

   public static int getColorText() {
      return -854793;
   }

   public static int getColorInactiveText() {
      return -7696233;
   }

   public static int getColorHeaderBg() {
      return -15856114;
   }

   public static int getColorHeaderText() {
      return -854793;
   }

   public static int getColorSlider() {
      return Palette.ACCENT;
   }

   public static int getColorSliderCircle() {
      return -1;
   }

   public static int getColorSliderWindow() {
      return -14013910;
   }

   public static int getColorIndicator() {
      return Palette.ACCENT;
   }

   public static int getColorInactiveIndicator() {
      return -14013910;
   }

   public static int getColorButton() {
      return Palette.ACCENT;
   }

   public static int getColorInactiveButton() {
      return -14013910;
   }

   public static int getColorSeparator() {
      return setAlpha(Palette.ACCENT, 56.1);
   }

   public static int getColorField() {
      return -15066598;
   }

   public static int getColorInactiveField() {
      return -15461356;
   }

   public static int getColorTooltipText() {
      return -854793;
   }

   public static int getColorWindowBg() {
      return -15329770;
   }

   public static int getColorIcons() {
      return Palette.ACCENT;
   }

   public static int getColorClient() {
      return getAccent();
   }

   public static int getColorClickGui() {
      return -15461356;
   }

   public static int getColorInterfaceBg() {
      return -16777216;
   }

   public static int[] getOrbitalRect(int c1, int c2, double speed, int alpha) {
      int[] colors = new int[4];
      double time = System.currentTimeMillis() / speed;

      for (int i = 0; i < 4; i++) {
         double phase = i * (Math.PI / 2);
         int color = interpolateColor(c1, c2, (float)(Math.sin(time + phase) * 0.5 + 0.5));
         colors[i] = setAlpha(color, alpha);
      }

      return colors;
   }

   public static int gradient(int speed, int index, int... colors) {
      int angle = (int)((System.currentTimeMillis() / speed + index) % 360L);
      angle = (angle > 180 ? 360 - angle : angle) + 180;
      int colorIndex = (int)(angle / 360.0F * colors.length);
      if (colorIndex == colors.length) {
         colorIndex--;
      }

      int color1 = colors[colorIndex];
      int color2 = colors[colorIndex == colors.length - 1 ? 0 : colorIndex + 1];
      return interpolateColor(color1, color2, angle / 360.0F * colors.length - colorIndex);
   }

   public static int interpolateColor(int from, int to, float amount) {
      amount = Math.min(1.0F, Math.max(0.0F, amount));
      int red1 = red(to);
      int green1 = green(to);
      int blue1 = blue(to);
      int alpha1 = alpha(to);
      int red2 = red(from);
      int green2 = green(from);
      int blue2 = blue(from);
      int alpha2 = alpha(from);
      int interpolatedRed = gy7cD(red1, red2, amount);
      int interpolatedGreen = gy7cD(green1, green2, amount);
      int interpolatedBlue = gy7cD(blue1, blue2, amount);
      int interpolatedAlpha = gy7cD(alpha1, alpha2, amount);
      return interpolatedAlpha << 24 | interpolatedRed << 16 | interpolatedGreen << 8 | interpolatedBlue;
   }

   private static int gy7cD(int oldValue, int newValue, double interpolationValue) {
      return interpolate(oldValue, newValue, (float)interpolationValue);
   }

   public static int interpolate(int start, int end, float value) {
      float[] startColor = rgba(start);
      float[] endColor = rgba(end);
      return rgba(
         (int)MathUtil.interpolate(startColor[0] * 255.0F, endColor[0] * 255.0F, value),
         (int)MathUtil.interpolate(startColor[1] * 255.0F, endColor[1] * 255.0F, value),
         (int)MathUtil.interpolate(startColor[2] * 255.0F, endColor[2] * 255.0F, value),
         (float)((int)MathUtil.interpolate(startColor[3] * 255.0F, endColor[3] * 255.0F, value))
      );
   }

   public static float[] rgba(int color) {
      return new float[]{(color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, (color >> 24 & 0xFF) / 255.0F};
   }

   public static int rgba(int r, int g, int b, double a) {
      return (int)a << 24 | r << 16 | g << 8 | b;
   }

   public static int rgba(int r, int g, int b, float a) {
      return (int)a << 24 | r << 16 | g << 8 | b;
   }

   public static int rgb(int r, int g, int b) {
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   public static int setAlpha(int color, int alpha) {
      return MathHelper.clamp(alpha, 0, 255) << 24 | color & 16777215;
   }

   public static int setAlpha(int color, double alpha) {
      return MathHelper.clamp((int)alpha, 0, 255) << 24 | color & 16777215;
   }

   public static int brighter(int color, float factor) {
      int r = Math.min(255, (int)(red(color) * factor));
      int g = Math.min(255, (int)(green(color) * factor));
      int b = Math.min(255, (int)(blue(color) * factor));
      int a = alpha(color);
      return rgba(r, g, b, (float)a);
   }

   public static int pack(int red, int green, int blue, int alpha) {
      return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF) << 0;
   }

   public static int[] unpack(int color) {
      return new int[]{color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF};
   }

   public static float[] normalize(Color color) {
      return new float[]{color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F};
   }

   public static float[] normalize(int color) {
      int[] components = unpack(color);
      return new float[]{components[0] / 255.0F, components[1] / 255.0F, components[2] / 255.0F, components[3] / 255.0F};
   }
}
