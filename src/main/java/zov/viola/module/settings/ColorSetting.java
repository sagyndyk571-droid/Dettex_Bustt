package zov.viola.module.settings;

import java.util.function.Supplier;
import zov.viola.util.render.providers.Palette;

public class ColorSetting extends Setting {
   private int t3Jf9;
   private boolean blaw = false;
   private boolean cfeCU6o = false;

   public ColorSetting(String name, Integer defaultValue) {
      super(name);
      this.t3Jf9 = defaultValue;
   }

   public Integer getValue() {
      return Palette.UNIFIED ? this.cfeCU6o ? Palette.ACCENT2 : Palette.ACCENT : this.t3Jf9;
   }

   public void setValue(Integer value) {
      this.t3Jf9 = value;
      if (this.blaw) {
         if (this.cfeCU6o) {
            Palette.ACCENT2 = value;
         } else {
            Palette.ACCENT = value;
         }
      }
   }

   @Override
   public String getValueAsString() {
      return Integer.toString(this.t3Jf9);
   }

   @Override
   public void setValueFromString(String value) {
      this.t3Jf9 = Integer.parseInt(value);
      if (this.blaw) {
         if (this.cfeCU6o) {
            Palette.ACCENT2 = this.t3Jf9;
         } else {
            Palette.ACCENT = this.t3Jf9;
         }
      }
   }

   public ColorSetting setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
      return this;
   }

   public ColorSetting setGlobal() {
      this.blaw = true;
      this.cfeCU6o = false;
      return this;
   }

   public ColorSetting setGlobalTwo() {
      this.blaw = true;
      this.cfeCU6o = true;
      return this;
   }
}
