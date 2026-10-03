package zov.viola.module.settings;

import java.util.function.Supplier;

public abstract class Setting implements ISetting {
   private final String wkh8DaC;
   public Supplier<Boolean> visible = () -> true;

   public Setting(String name) {
      this.wkh8DaC = name;
   }

   public String getName() {
      return this.wkh8DaC;
   }

   public abstract String getValueAsString();

   public abstract void setValueFromString(String var1);

   public Supplier<Boolean> getVisible() {
      return this.visible;
   }

   public String getVisibleName() {
      return this.getName();
   }

   public String getDescription() {
      return this.getName();
   }

   public void reset() {
   }

   public int getType() {
      return 0;
   }

   public void setType(int type) {
   }
}
