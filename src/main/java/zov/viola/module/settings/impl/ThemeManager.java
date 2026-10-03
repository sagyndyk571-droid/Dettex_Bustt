package zov.viola.module.settings.impl;

import java.util.List;
import zov.viola.obf.D;
import zov.viola.ui.CsPalette;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.providers.Palette;

public class ThemeManager {
   private static ThemeManager t651;
   private Theme r09fr0w;
   private final List<Theme> qZ9y17r = this.uwhma();

   private ThemeManager() {
      this.r09fr0w = this.qZ9y17r.get(0);
      this.yK3uf7();
      CsPalette.applyTheme(this.r09fr0w);
   }

   public static ThemeManager getInstance() {
      if (t651 == null) {
         t651 = new ThemeManager();
      }

      return t651;
   }

   public List<Theme> getThemes() {
      return this.qZ9y17r;
   }

   public Theme getCurrentTheme() {
      this.yK3uf7();
      return this.r09fr0w;
   }

   public void setCurrentTheme(Theme theme) {
      if (theme != null && this.qZ9y17r.contains(theme)) {
         this.r09fr0w = theme;
         Palette.ACCENT = theme.color1 & 16777215 | 0xFF000000;
         this.yK3uf7();
         CsPalette.applyTheme(this.r09fr0w);
      }
   }

   public void setCurrentTheme(String name) {
      for (Theme t : this.qZ9y17r) {
         if (t.name.equals(name)) {
            this.setCurrentTheme(t);
            return;
         }
      }
   }

   private List<Theme> uwhma() {
      return List.of(
         this.rR1Mf("Палитра", -9021441, -12982017),
         this.rR1Mf("Синяя", -12816651, -12466433),
         this.rR1Mf("Голубая", -13907713, -8984833),
         this.rR1Mf("Зелёная", -13710223, -5701768),
         this.rR1Mf("Красная", -2084022, -34212),
         this.rR1Mf("Оранжевая", -1010114, -11206),
         this.rR1Mf("Розовая", -41054, -20250)
      );
   }

   private Theme rR1Mf(String name, int color1, int color2) {
      Theme t = new Theme(name, color1, color2);
      t.colorMain = -15066081;
      t.colorVisualModules = t.color1;
      t.colorText = -854793;
      t.colorInactiveText = -7696233;
      t.colorHeaderBg = -15526633;
      t.colorHeaderText = t.color1;
      t.colorSlider = t.color1;
      t.colorSliderCircle = -262915;
      t.colorSliderWindow = -13749705;
      t.colorIndicator = t.color1;
      t.colorInactiveIndicator = -13749705;
      t.colorButton = t.color1;
      t.colorInactiveButton = -13749705;
      t.colorSeparator = -13749705;
      t.colorField = -14671065;
      t.colorInactiveField = -13749705;
      t.colorTooltipText = -854793;
      t.colorWindowBg = -14737115;
      t.colorIcons = t.color1;
      t.colorClient = t.color1;
      t.colorClickGui = -14737115;
      t.colorInterfaceBg = -586610165;
      return t;
   }

   private void yK3uf7() {
      int c = Palette.ACCENT;
      this.r09fr0w.color1 = c;
      this.r09fr0w.color2 = c;
      this.r09fr0w.colorMain = -15329770;
      this.r09fr0w.colorVisualModules = c;
      this.r09fr0w.colorClient = c;
      this.r09fr0w.colorIcons = c;
      this.r09fr0w.colorHeaderText = c;
      this.r09fr0w.colorSlider = c;
      this.r09fr0w.colorIndicator = c;
      this.r09fr0w.colorButton = c;
      this.r09fr0w.colorText = c;
      this.r09fr0w.colorInactiveText = ColorProvider.setAlpha(c, 114.75);
      this.r09fr0w.colorHeaderBg = -15856114;
      this.r09fr0w.colorWindowBg = -15329770;
      this.r09fr0w.colorClickGui = -15461356;
      this.r09fr0w.colorInterfaceBg = -16777216;
   }
}
