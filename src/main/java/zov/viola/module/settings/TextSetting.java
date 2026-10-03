package zov.viola.module.settings;

import java.util.function.Supplier;

public class TextSetting extends Setting {
   private String uLii;

   public TextSetting(String name, String defaultValue) {
      super(name);
      this.uLii = defaultValue == null ? "" : defaultValue;
   }

   public String getValue() {
      return this.uLii;
   }

   public void setValue(String value) {
      this.uLii = value == null ? "" : value;
   }

   @Override
   public String getValueAsString() {
      return this.uLii;
   }

   @Override
   public void setValueFromString(String value) {
      this.uLii = value == null ? "" : value;
   }

   public TextSetting setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
      return this;
   }
}
