package zov.viola.ui.clickgui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.Setting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;

public class DropdownGuiState {
   private final List<Module> h55Ek2 = new ArrayList<>();
   private final EnumMap<ModuleCategory, List<Module>> i4Ee = new EnumMap<>(ModuleCategory.class);
   private final EnumMap<ModuleCategory, Float> pp1P = new EnumMap<>(ModuleCategory.class);
   private final EnumMap<ModuleCategory, Animation> yvtjv6 = new EnumMap<>(ModuleCategory.class);
   private final Map<Module, Animation> si498 = new IdentityHashMap<>();
   private final Map<Module, Boolean> u3bfkN = new IdentityHashMap<>();
   private final Map<Module, Animation> x5R6 = new IdentityHashMap<>();
   private final Map<BooleanSetting, Animation> ypyiot0 = new IdentityHashMap<>();
   private final Map<BooleanSetting, Animation> tBkl = new IdentityHashMap<>();
   private final Map<SliderSetting, Animation> mph1fD = new IdentityHashMap<>();
   private float eXeH;
   private float hCwptcg;
   private float cxofYh;
   private boolean jTh4;
   private String pvazlPa = "";
   private int m1aMiU2;
   private Module r187l;
   private Setting w8u4s;
   private Setting mek0r;
   private int t771;
   private SliderSetting c5EZ;

   public DropdownGuiState() {
      this.refreshModules();
   }

   public void refreshModules() {
      this.h55Ek2.clear();
      List<Module> modules = Viola.getInstance().getModuleStorage().getModules();
      if (modules != null) {
         this.h55Ek2.addAll(modules);
      }

      for (ModuleCategory category : ModuleCategory.values()) {
         this.i4Ee
            .put(
               category,
               this.h55Ek2
                  .stream()
                  .filter(module -> module.getCategory() == category && !module.isHidden())
                  .sorted((a, b) -> a.getName().toLowerCase(Locale.ROOT).compareTo(b.getName().toLowerCase(Locale.ROOT)))
                  .toList()
            );
         this.pp1P.computeIfAbsent(category, c -> 0.0F);
         this.yvtjv6.computeIfAbsent(category, c -> new Animation(Easing.CUBIC_OUT, 250L));
      }
   }

   public float getX() {
      return this.eXeH;
   }

   public float getY() {
      return this.hCwptcg;
   }

   public void setPos(float x, float y) {
      this.eXeH = x;
      this.hCwptcg = y;
   }

   public float getRenderOffsetY() {
      return this.cxofYh;
   }

   public void setRenderOffsetY(float renderOffsetY) {
      this.cxofYh = renderOffsetY;
   }

   public List<Module> getModules(ModuleCategory category) {
      String query = this.pvazlPa.replaceAll(" ", "").toLowerCase(Locale.ROOT);
      if (query.isBlank()) {
         return this.i4Ee.getOrDefault(category, List.of());
      } else {
         List<Module> result = new ArrayList<>();

         for (Module module : this.i4Ee.getOrDefault(category, List.of())) {
            if (module.getName().toLowerCase(Locale.ROOT).contains(query)) {
               result.add(module);
            }
         }

         return result;
      }
   }

   public boolean isSearchActive() {
      return this.jTh4;
   }

   public void setSearchActive(boolean searchActive) {
      this.jTh4 = searchActive;
   }

   public String getSearchText() {
      return this.pvazlPa;
   }

   public void setSearchText(String searchText) {
      this.pvazlPa = searchText;
   }

   public int getSearchCursor() {
      return this.m1aMiU2;
   }

   public void setSearchCursor(int searchCursor) {
      this.m1aMiU2 = searchCursor;
   }

   public Module getBindingModule() {
      return this.r187l;
   }

   public void setBinding(Module module) {
      this.r187l = module;
   }

   public Setting getBindingSetting() {
      return this.w8u4s;
   }

   public void setBindingSetting(Setting bindingSetting) {
      this.w8u4s = bindingSetting;
   }

   public Setting getEditingStringSetting() {
      return this.mek0r;
   }

   public void setEditingStringSetting(Setting editingStringSetting) {
      this.mek0r = editingStringSetting;
   }

   public int getStringCursor() {
      return this.t771;
   }

   public void setStringCursor(int stringCursor) {
      this.t771 = stringCursor;
   }

   public SliderSetting getDraggingSlider() {
      return this.c5EZ;
   }

   public void setDraggingSlider(SliderSetting draggingSlider) {
      this.c5EZ = draggingSlider;
   }

   public boolean isModuleOpen(Module module) {
      return this.u3bfkN.getOrDefault(module, false);
   }

   public void toggleModuleOpen(Module module) {
      boolean open = this.u3bfkN.getOrDefault(module, false);
      this.u3bfkN.put(module, !open);
   }

   public void setModuleOpen(Module module, boolean open) {
      this.u3bfkN.put(module, open);
   }

   public Animation getModuleOpenAnimation(Module module) {
      return this.si498.computeIfAbsent(module, m -> new Animation(Easing.CUBIC_OUT, 200L));
   }

   public float getOpenProgress(Module module) {
      Animation animation = this.si498.get(module);
      if (animation == null) {
         return this.isModuleOpen(module) ? 1.0F : 0.0F;
      } else {
         return Math.max(0.0F, Math.min(1.0F, animation.getValue()));
      }
   }

   public Animation getModuleDotAnimation(Module module) {
      return this.x5R6.computeIfAbsent(module, m -> new Animation(Easing.QUINTIC_OUT, 200L));
   }

   public Animation getBooleanBackgroundAnimation(BooleanSetting setting) {
      return this.ypyiot0.computeIfAbsent(setting, s -> new Animation(Easing.CUBIC_OUT, 150L));
   }

   public Animation getBooleanCircleAnimation(BooleanSetting setting) {
      return this.tBkl.computeIfAbsent(setting, s -> new Animation(Easing.CUBIC_OUT, 150L));
   }

   public Animation getSliderAnimation(SliderSetting setting) {
      return this.mph1fD.computeIfAbsent(setting, s -> new Animation(Easing.CUBIC_OUT, 150L));
   }

   public float getSliderPos(SliderSetting setting) {
      double min = setting.getMin();
      double max = setting.getMax();
      return max <= min ? 0.0F : (float)((setting.getValue() - min) / (max - min));
   }

   public double getSliderValueFromPos(SliderSetting setting, double pos) {
      return setting.getMin() + pos * (setting.getMax() - setting.getMin());
   }

   public double getSliderValue(SliderSetting setting, float posX, double mouseX) {
      double delta = setting.getMax() - setting.getMin();
      float clickedX = (float)mouseX - posX;
      double value = Math.max(0.0F, Math.min(1.0F, clickedX / 120.0F));
      double outValue = setting.getMin() + delta * value;
      double increment = Math.max(setting.getStep(), 1.0E-4);
      outValue = Math.round(outValue / increment) * increment;
      return Math.max(setting.getMin(), Math.min(setting.getMax(), outValue));
   }

   public void setSliderValue(SliderSetting setting, double value) {
      double min = setting.getMin();
      double max = setting.getMax();
      double step = Math.max(setting.getStep(), 1.0E-4);
      double snapped = Math.round(value / step) * step;
      setting.setValue(Math.max(min, Math.min(max, snapped)));
   }

   public float getScroll(ModuleCategory category) {
      Animation animation = this.yvtjv6.get(category);
      float target = this.pp1P.getOrDefault(category, 0.0F);
      if (animation == null) {
         return target;
      } else {
         animation.run(target);
         return animation.getValue();
      }
   }

   public float getScrollTarget(ModuleCategory category) {
      return this.pp1P.getOrDefault(category, 0.0F);
   }

   public void setScrollTarget(ModuleCategory category, float scroll) {
      this.pp1P.put(category, scroll);
   }

   public void addScroll(ModuleCategory category, double verticalAmount, float contentHeight) {
      float maxScroll = Math.min(0.0F, contentHeight - this.getTotalModulesHeight(category));
      float newTarget = this.getScrollTarget(category) + (float)(verticalAmount * 20.0);
      this.pp1P.put(category, Math.max(maxScroll, Math.min(0.0F, newTarget)));
   }

   public void clampScroll(ModuleCategory category, float visibleHeight) {
      float maxScroll = Math.min(0.0F, visibleHeight - this.getTotalModulesHeight(category));
      float target = Math.min(0.0F, Math.max(maxScroll, this.getScrollTarget(category)));
      this.pp1P.put(category, target);
   }

   public float getTotalModulesHeight(ModuleCategory category) {
      float total = 0.0F;

      for (Module module : this.getModules(category)) {
         total += this.getModuleHeight(module) + 4.0F;
      }

      return total;
   }

   public float getModuleHeight(Module module) {
      return 37.0F + DropdownGuiLayout.calculateSettingsHeight(module) * this.getOpenProgress(module);
   }
}
