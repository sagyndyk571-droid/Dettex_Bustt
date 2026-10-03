package zov.viola.module.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModeSetting extends Setting {
   private String u99U1;
   private final List<String> q2gDgD2;
   private final Map<String, String> hjtS = new HashMap<>();

   public ModeSetting(String name, String defaultValue, String... modes) {
      super(name);
      this.u99U1 = defaultValue;
      this.q2gDgD2 = Arrays.asList(modes);
   }

   public ModeSetting addAlias(String from, String to) {
      this.hjtS.put(from, to);
      return this;
   }

   private String st697(String value) {
      return this.hjtS.getOrDefault(value, value);
   }

   public String getValue() {
      return this.u99U1;
   }

   public void setValue(String value) {
      String resolved = this.st697(value);
      if (this.q2gDgD2.contains(resolved)) {
         this.u99U1 = resolved;
      }
   }

   public boolean is(String equalsIgnoreCase) {
      return this.u99U1.equalsIgnoreCase(equalsIgnoreCase);
   }

   public List<String> getModes() {
      return this.q2gDgD2;
   }

   public int getIndex() {
      return this.q2gDgD2.indexOf(this.u99U1);
   }

   public void setIndex(int index) {
      if (index >= 0 && index < this.q2gDgD2.size()) {
         this.u99U1 = this.q2gDgD2.get(index);
      }
   }

   public void cycle() {
      int nextIndex = (this.getIndex() + 1) % this.q2gDgD2.size();
      this.setValue(this.q2gDgD2.get(nextIndex));
   }

   @Override
   public String getValueAsString() {
      return this.u99U1;
   }

   @Override
   public void setValueFromString(String value) {
      String resolved = this.st697(value);
      if (this.q2gDgD2.contains(resolved)) {
         this.u99U1 = resolved;
      }
   }

   public ModeSetting setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
      return this;
   }

   public String getSelected() {
      return this.getValue();
   }

   public void setSelected(String value) {
      this.setValue(value);
   }

   public List<String> getList() {
      return this.getModes();
   }
}
