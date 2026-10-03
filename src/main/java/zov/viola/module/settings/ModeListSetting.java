package zov.viola.module.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import zov.viola.obf.D;

public class ModeListSetting extends Setting {
   private final List<BooleanSetting> fq8rwR;

   public ModeListSetting(String name, BooleanSetting... settings) {
      super(name);
      this.fq8rwR = Arrays.asList(settings);
   }

   public List<BooleanSetting> getSettings() {
      return this.fq8rwR;
   }

   public List<String> getEnabledModules() {
      return this.fq8rwR.stream().filter(BooleanSetting::getValue).map(Setting::getName).collect(Collectors.toList());
   }

   public boolean isEnabled(String moduleName) {
      return this.fq8rwR.stream().anyMatch(setting -> setting.getName().equalsIgnoreCase(moduleName) && setting.getValue());
   }

   @Override
   public String getValueAsString() {
      List<String> enabled = this.getEnabledModules();
      return enabled.isEmpty() ? "None" : String.join(", ", enabled);
   }

   @Override
   public void setValueFromString(String value) {
      List<String> names = Arrays.asList(value.split(",\\s*"));

      for (BooleanSetting setting : this.fq8rwR) {
         setting.setValue(names.contains(setting.getName()));
      }
   }

   public ModeListSetting setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
      return this;
   }

   public List<String> getSelected() {
      List<String> out = new ArrayList<>();

      for (BooleanSetting s : this.fq8rwR) {
         if (s.getValue()) {
            out.add(s.getName());
         }
      }

      return out;
   }

   public void setSelected(List<String> selected) {
      for (BooleanSetting s : this.fq8rwR) {
         s.setValue(selected.contains(s.getName()));
      }
   }

   public List<String> getList() {
      List<String> out = new ArrayList<>();

      for (BooleanSetting s : this.fq8rwR) {
         out.add(s.getName());
      }

      return out;
   }
}
