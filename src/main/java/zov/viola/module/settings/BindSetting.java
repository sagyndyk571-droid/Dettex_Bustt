package zov.viola.module.settings;

import java.util.function.Supplier;

public class BindSetting extends Setting {
   private int ooU70yy;

   public BindSetting(String name, Integer defaultValue) {
      super(name);
      this.ooU70yy = defaultValue;
   }

   public Integer getValue() {
      return this.ooU70yy;
   }

   public void setValue(Integer value) {
      this.ooU70yy = value;
   }

   @Override
   public String getValueAsString() {
      return Integer.toString(this.ooU70yy);
   }

   @Override
   public void setValueFromString(String value) {
      this.ooU70yy = Integer.parseInt(value);
   }

   public BindSetting setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
      return this;
   }

   public int getKey() {
      return this.getValue();
   }

   public void setKey(int key) {
      this.setValue(key);
   }

   @Override
   public String getDescription() {
      return this.getName();
   }
}
