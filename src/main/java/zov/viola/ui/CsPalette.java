package zov.viola.ui;

import zov.viola.module.settings.impl.Theme;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.providers.Palette;

public final class CsPalette {
   public static int ACCENT = -2697514;
   public static int ACCENT_DIM;
   public static int ACCENT_GLOW;
   public static int TEXT = -657931;
   public static int TEXT_DIM = -7697782;
   public static int TEXT_MUTED = -11184811;
   public static int PANEL_BG = -15856112;
   public static int PANEL_BG_2 = -15329767;
   public static int PANEL_BG_3 = -14935008;
   public static int HEADER_BG = -16119284;
   public static int BORDER = -13750738;
   public static int BORDER_LIGHT;
   public static int HOVER = -14803422;
   public static int HOVER_STRONG;
   public static int SHADOW = 1073741824;
   public static int CARD_BG;
   public static int CARD_BG_HOVER;
   public static int SIDEBAR_BG = -16250870;
   public static final int OVERLAY = -1945630712;

   public static void applyTheme(Theme theme) {
      ACCENT = Palette.ACCENT;
      ACCENT_DIM = ColorProvider.setAlpha(Palette.ACCENT, 114.75);
      ACCENT_GLOW = ColorProvider.setAlpha(Palette.ACCENT, 38.25);
      BORDER_LIGHT = ColorProvider.setAlpha(Palette.ACCENT, 45.9);
      HOVER_STRONG = ColorProvider.setAlpha(Palette.ACCENT, 30.599999999999998);
      CARD_BG = -15658732;
      CARD_BG_HOVER = -15329766;
      TEXT = -854793;
      TEXT_DIM = -8749435;
      TEXT_MUTED = -11907757;
      PANEL_BG = -15987698;
      PANEL_BG_2 = -15461353;
      PANEL_BG_3 = -15066594;
      HEADER_BG = -16250870;
      BORDER = -14540250;
      HOVER = -15066594;
      SIDEBAR_BG = -16382456;
   }

   public static void refreshAccent() {
      ACCENT = Palette.ACCENT;
      ACCENT_DIM = ColorProvider.setAlpha(Palette.ACCENT, 114.75);
      ACCENT_GLOW = ColorProvider.setAlpha(Palette.ACCENT, 38.25);
      BORDER_LIGHT = ColorProvider.setAlpha(Palette.ACCENT, 45.9);
      HOVER_STRONG = ColorProvider.setAlpha(Palette.ACCENT, 30.599999999999998);
   }

   private CsPalette() {
   }
}
