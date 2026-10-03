package zov.viola.module.settings;

import java.util.function.Supplier;
import lombok.Generated;
import zov.viola.util.text.ValueUnit;

public class SliderSetting extends Setting {
   private double s5cl;
   private ValueUnit w0Maj5;
   private final double d0gw;
   private final double g2NS88;
   private final double qP7e;

   public SliderSetting(String name, ValueUnit unit, double defaultValue, double min, double max, double step) {
      super(name);
      this.s5cl = defaultValue;
      this.w0Maj5 = unit;
      this.d0gw = min;
      this.g2NS88 = max;
      this.qP7e = step;
   }

   public SliderSetting(String name, double defaultValue, double min, double max, double step) {
      super(name);
      this.s5cl = defaultValue;
      this.d0gw = min;
      this.g2NS88 = max;
      this.qP7e = step;
   }

   public String getFormattedValue() {
      return this.w0Maj5.format(this.s5cl);
   }

   public int getIntValue() {
      return (int)this.s5cl;
   }

   public float getFloatValue() {
      return (float)this.s5cl;
   }

   public void setValue(double value) {
      this.s5cl = Math.max(this.d0gw, Math.min(this.g2NS88, value));
   }

   public void increment() {
      this.setValue(this.s5cl + this.qP7e);
   }

   public void decrement() {
      this.setValue(this.s5cl - this.qP7e);
   }

   @Override
   public String getValueAsString() {
      return Double.toString(this.s5cl);
   }

   @Override
   public void setValueFromString(String value) {
      try {
         this.setValue(Double.parseDouble(value));
      } catch (NumberFormatException var3) {
      }
   }

   public SliderSetting setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
      return this;
   }

   public double getMin() {
      return this.d0gw;
   }

   public double getMax() {
      return this.g2NS88;
   }

   public boolean isInteger() {
      return this.qP7e == Math.floor(this.qP7e) && this.qP7e >= 1.0;
   }

   @Generated
   public double getValue() {
      return this.s5cl;
   }

   @Generated
   public ValueUnit getUnit() {
      return this.w0Maj5;
   }

   @Generated
   public double getStep() {
      return this.qP7e;
   }

   @Generated
   public void setUnit(ValueUnit unit) {
      this.w0Maj5 = unit;
   }
}
