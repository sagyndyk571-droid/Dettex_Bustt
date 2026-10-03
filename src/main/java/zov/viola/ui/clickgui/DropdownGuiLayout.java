package zov.viola.ui.clickgui;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.Setting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.module.settings.TextSetting;
import zov.viola.module.settings.ThemeSetting;
import zov.viola.util.render.msdf.Fonts;

public final class DropdownGuiLayout {
   public static final float CATEGORY_HEADER_HEIGHT = 26.0F;
   public static final float WIDTH = 144.0F;
   public static final float HEIGHT = 396.0F;
   public static final float CATEGORY_PANEL_STEP = 148.0F;
   public static final ModuleCategory[] DROPDOWN_CATEGORIES = ModuleCategory.values();
   public static final float MODULE_PADDING = 4.0F;
   public static final float MODULE_GAP = 4.0F;
   public static final float MODULE_HEADER_HEIGHT = 37.0F;
   public static final float MODULE_INNER_WIDTH = 136.0F;
   public static final float SETTING_START_Y = 37.0F;
   public static final float SETTING_PADDING = 3.0F;
   public static final float SETTING_BOTTOM_PADDING = 6.0F;
   public static final float SETTING_LEFT = 12.0F;
   public static final float SETTING_RIGHT = 132.0F;
   public static final float SLIDER_WIDTH = 120.0F;
   private static final float SETTING_GLOBAL_GAP = 2.5F;
   public static final float CHIP_GAP_X = 3.0F;
   public static final float CHIP_GAP_Y = 3.0F;
   public static final float CHIP_PADDING_X = 4.0F;
   public static final float CHIP_PADDING_Y = 2.0F;
   public static final int SEARCH_MAX_CHARS = 24;
   public static final float SEARCH_WIDTH = 120.0F;
   public static final float SEARCH_HEIGHT = 22.0F;
   public static final float SEARCH_GAP = 8.0F;
   private static final Map<Setting, DropdownGuiLayout.CachedChipHeight> CHIP_HEIGHT_CACHE = new IdentityHashMap<>();

   private DropdownGuiLayout() {
   }

   public static float getCategoryPanelX(float x, int index) {
      return x + index * 148.0F;
   }

   public static float getTotalCategoriesWidth() {
      return (DROPDOWN_CATEGORIES.length - 1) * 148.0F + 144.0F;
   }

   public static float getContentY(float y) {
      return y + 26.0F;
   }

   public static float getContentHeight() {
      return 368.0F;
   }

   public static float getSearchX(float x) {
      return x + getTotalCategoriesWidth() / 2.0F - 60.0F;
   }

   public static float getSearchY(float y) {
      return y + 396.0F + 8.0F;
   }

   public static boolean hasVisibleSettings(Module module) {
      List<Setting> settings = module.getSettings();
      if (settings != null && !settings.isEmpty()) {
         for (Setting setting : settings) {
            if (setting != null && setting.getVisible().get()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static List<Setting> getVisibleSettings(Module module) {
      List<Setting> settings = module.getSettings();
      return (List<Setting>)(settings == null ? new ArrayList<>() : getVisibleSettings(settings));
   }

   public static List<Setting> getVisibleSettings(List<Setting> settings) {
      List<Setting> visible = new ArrayList<>();
      if (settings == null) {
         return visible;
      } else {
         for (Setting setting : settings) {
            if (setting.getVisible().get()) {
               visible.add(setting);
            }
         }

         return visible;
      }
   }

   public static float settingHeight(Setting setting) {
      if (setting instanceof BooleanSetting) {
         return 12.0F;
      } else if (setting instanceof SliderSetting) {
         return 22.0F;
      } else if (setting instanceof ModeSetting modeSetting) {
         return calculateModeSettingHeight(modeSetting);
      } else if (setting instanceof ModeListSetting modeListSetting) {
         return calculateModeListHeight(modeListSetting);
      } else if (setting instanceof BindSetting) {
         return 12.0F;
      } else if (setting instanceof TextSetting) {
         return 22.0F;
      } else if (setting instanceof ColorSetting) {
         return 22.0F;
      } else {
         return setting instanceof ThemeSetting ? 14.0F : 12.0F;
      }
   }

   public static float calculateSettingsHeight(Module module) {
      List<Setting> visible = getVisibleSettings(module);
      if (visible.isEmpty()) {
         return 0.0F;
      } else {
         float height = 0.0F;

         for (int i = 0; i < visible.size(); i++) {
            height += settingHeight(visible.get(i));
            if (i < visible.size() - 1) {
               height += 2.5F;
            }
         }

         return height + 6.0F;
      }
   }

   public static float calculateModeSettingHeight(ModeSetting setting) {
      List<String> values = setting.getModes();
      return mUJk9(setting, values == null ? List.of() : values);
   }

   public static float calculateModeListHeight(ModeListSetting setting) {
      List<BooleanSetting> values = setting.getSettings();
      List<String> names = new ArrayList<>();
      if (values != null) {
         for (BooleanSetting value : values) {
            names.add(value.getName());
         }
      }

      return mUJk9(setting, names);
   }

   private static float mUJk9(Setting setting, List<String> values) {
      int valueCount = values.size();
      DropdownGuiLayout.CachedChipHeight cached = CHIP_HEIGHT_CACHE.get(setting);
      if (cached != null && cached.valueCount() == valueCount) {
         return cached.height();
      } else {
         float x = 12.0F;
         int rows = 1;

         for (String value : values) {
            float chipWidth = Fonts.SFPRO_REGULAR.get().getWidth(value, 6.0F) + 8.0F;
            if (x + chipWidth > 132.0F) {
               x = 12.0F;
               rows++;
            }

            x += chipWidth + 3.0F;
         }

         float height = 9.0F + rows * 11.0F + (rows - 1) * 3.0F + 2.0F;
         CHIP_HEIGHT_CACHE.put(setting, new DropdownGuiLayout.CachedChipHeight(valueCount, height));
         if (CHIP_HEIGHT_CACHE.size() > 300) {
            CHIP_HEIGHT_CACHE.clear();
         }

         return height;
      }
   }

   private record CachedChipHeight(int valueCount, float height) {

      

      

      
   }
}
