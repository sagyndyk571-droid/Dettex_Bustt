package zov.viola.module.settings;

import java.util.Arrays;
import java.util.function.Supplier;
import lombok.Generated;
import zov.viola.module.settings.impl.Theme;
import zov.viola.obf.D;

public class ThemeSetting extends Setting {
   private final Theme[] i7kH;
   private Theme bjUne3F;

   public ThemeSetting(Theme defaultValue, Theme... themes) {
      super("Гавно");
      this.setValue(defaultValue);
      this.i7kH = themes;
   }

   public void setValue(Theme theme) {
      if (this.bjUne3F == null) {
         this.bjUne3F = theme;
      }

      if (this.bjUne3F != theme) {
         theme.startAnimation(this.bjUne3F.color1, this.bjUne3F.color2);
         this.bjUne3F = theme;
      }
   }

   public Theme getValue() {
      return this.bjUne3F;
   }

   @Override
   public String getValueAsString() {
      return this.getValue().name;
   }

   @Override
   public void setValueFromString(String value) {
      Arrays.stream(this.i7kH).filter(theme -> theme.name.equals(value)).forEach(this::setValue);
   }

   public ThemeSetting setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
      return this;
   }

   @Generated
   public Theme[] getThemes() {
      return this.i7kH;
   }

   @Generated
   public Theme getCurrent() {
      return this.bjUne3F;
   }
}
