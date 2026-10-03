package zov.viola.ui.clickgui;

import java.util.List;
import net.minecraft.util.math.MathHelper;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.Setting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.module.settings.TextSetting;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.msdf.MsdfFont;

public class DropdownGuiInputHandler {
   private static final MsdfFont REGULAR = Fonts.SFPRO_REGULAR.get();
   private static final int STRING_MAX_LENGTH = 64;
   public final DropdownGuiState state;

   public DropdownGuiInputHandler(DropdownGuiState state) {
      this.state = state;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      float searchX = DropdownGuiLayout.getSearchX(this.state.getX());
      float searchY = DropdownGuiLayout.getSearchY(this.state.getY() + this.state.getRenderOffsetY());
      if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, searchX, searchY, 120.0, 22.0)) {
         this.state.setSearchActive(true);
         this.state.setSearchCursor(this.state.getSearchText().length());
         return true;
      } else {
         if (button == 0 && this.state.isSearchActive()) {
            this.state.setSearchActive(false);
         }

         ModuleCategory[] categories = DropdownGuiLayout.DROPDOWN_CATEGORIES;

         for (int i = 0; i < categories.length; i++) {
            ModuleCategory category = categories[i];
            float panelX = DropdownGuiLayout.getCategoryPanelX(this.state.getX(), i);
            float panelY = this.state.getY() + this.state.getRenderOffsetY();
            float contentY = DropdownGuiLayout.getContentY(panelY);
            float contentHeight = DropdownGuiLayout.getContentHeight();
            if (HoverUtil.isHovered(mouseX, mouseY, panelX, contentY, 144.0, contentHeight)) {
               this.state.clampScroll(category, contentHeight);
               float moduleY = contentY + 2.0F + this.state.getScroll(category);

               for (Module module : this.state.getModules(category)) {
                  float openProgress = this.state.getOpenProgress(module);
                  float moduleHeight = this.state.getModuleHeight(module);
                  if (HoverUtil.isHovered(mouseX, mouseY, panelX + 4.0F, moduleY, 136.0, 37.0)) {
                     if (button != 0) {
                        if (button == 1) {
                           if (DropdownGuiLayout.hasVisibleSettings(module)) {
                              this.state.toggleModuleOpen(module);
                              this.state.clampScroll(category, contentHeight);
                           }

                           return true;
                        }

                        if (button == 2) {
                           this.state.setBinding(module);
                           return true;
                        }

                        return true;
                     }

                     if (mouseX >= panelX + 144.0F - 34.0F && mouseX < panelX + 144.0F - 22.0F && DropdownGuiLayout.hasVisibleSettings(module)) {
                        this.state.toggleModuleOpen(module);
                     } else {
                        module.toggle();
                     }

                     return true;
                  }

                  if (this.state.isModuleOpen(module)
                     && openProgress > 0.95F
                     && mouseY >= moduleY + 37.0F
                     && mouseY < moduleY + moduleHeight
                     && this.hRht6(mouseX, mouseY, button, panelX, moduleY, module.getSettings())) {
                     return true;
                  }

                  moduleY += 4.0F + moduleHeight;
               }
            }
         }

         return false;
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.state.getDraggingSlider() != null) {
         this.state.setDraggingSlider(null);
         return true;
      } else {
         return false;
      }
   }

   private boolean hRht6(double mouseX, double mouseY, int button, float moduleX, float moduleY, List<Setting> settings) {
      float settingYoffset = 37.0F;
      List<Setting> visibleSettings = DropdownGuiLayout.getVisibleSettings(settings);

      for (int i = 0; i < visibleSettings.size(); i++) {
         Setting setting = visibleSettings.get(i);
         float settingY = moduleY + settingYoffset + 3.0F;
         float currentAddedHeight;
         if (setting instanceof BooleanSetting booleanSetting) {
            if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, moduleX + 12.0F, settingY - 2.0F, 120.0, 12.0)) {
               booleanSetting.toggle();
               return true;
            }

            if (button == 2 && HoverUtil.isHovered(mouseX, mouseY, moduleX + 12.0F, settingY - 2.0F, 120.0, 12.0)) {
               this.state.setBindingSetting(booleanSetting);
               return true;
            }

            currentAddedHeight = 12.0F;
         } else if (setting instanceof SliderSetting sliderSetting) {
            if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, moduleX + 12.0F, settingY + 9.0F, 120.0, 6.0)) {
               this.state.setSliderValue(sliderSetting, this.state.getSliderValue(sliderSetting, moduleX + 12.0F, mouseX));
               this.state.setDraggingSlider(sliderSetting);
               return true;
            }

            currentAddedHeight = 22.0F;
         } else if (setting instanceof ModeSetting modeSetting) {
            float x = moduleX + 12.0F;
            float y = settingY + 10.0F;
            List<String> values = modeSetting.getModes();
            if (values == null) {
               values = List.of();
            }

            for (String value : values) {
               float chipW = REGULAR.getWidth(value, 6.0F) + 8.0F;
               if (x + chipW > moduleX + 132.0F) {
                  x = moduleX + 12.0F;
                  y += 14.0F;
               }

               if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, x, y, chipW, 11.0)) {
                  modeSetting.setValue(value);
                  return true;
               }

               x += chipW + 3.0F;
            }

            currentAddedHeight = DropdownGuiLayout.calculateModeSettingHeight(modeSetting);
         } else if (!(setting instanceof ModeListSetting modeListSetting)) {
            if (setting instanceof BindSetting bindSetting) {
               if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, moduleX + 12.0F, settingY - 2.0F, 132.0, 12.0)) {
                  this.state.setBindingSetting(bindSetting);
                  return true;
               }

               currentAddedHeight = 12.0F;
            } else if (setting instanceof TextSetting textSetting) {
               if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, moduleX + 12.0F, settingY + 9.0F, 120.0, 10.0)) {
                  this.state.setEditingStringSetting(textSetting);
                  this.state.setStringCursor(textSetting.getValue().length());
                  return true;
               }

               currentAddedHeight = 22.0F;
            } else {
               currentAddedHeight = 12.0F;
            }
         } else {
            float x = moduleX + 12.0F;
            float y = settingY + 10.0F;
            List<BooleanSetting> subSettings = modeListSetting.getSettings();
            if (subSettings == null) {
               subSettings = List.of();
            }

            for (BooleanSetting subSetting : subSettings) {
               String name = subSetting.getName();
               float chipWx = REGULAR.getWidth(name, 6.0F) + 8.0F;
               if (x + chipWx > moduleX + 132.0F) {
                  x = moduleX + 12.0F;
                  y += 14.0F;
               }

               if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, x, y, chipWx, 11.0)) {
                  subSetting.toggle();
                  return true;
               }

               x += chipWx + 3.0F;
            }

            currentAddedHeight = DropdownGuiLayout.calculateModeListHeight(modeListSetting);
         }

         settingYoffset += currentAddedHeight;
         if (i < visibleSettings.size() - 1) {
            settingYoffset += 2.5F;
         }
      }

      return false;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
      ModuleCategory[] categories = DropdownGuiLayout.DROPDOWN_CATEGORIES;

      for (int i = 0; i < categories.length; i++) {
         ModuleCategory category = categories[i];
         float panelX = DropdownGuiLayout.getCategoryPanelX(this.state.getX(), i);
         float panelY = this.state.getY() + this.state.getRenderOffsetY();
         float contentY = DropdownGuiLayout.getContentY(panelY);
         float contentHeight = DropdownGuiLayout.getContentHeight();
         if (HoverUtil.isHovered(mouseX, mouseY, panelX, contentY, 144.0, contentHeight)) {
            this.state.addScroll(category, verticalAmount, contentHeight);
            return true;
         }
      }

      return false;
   }

   public boolean keyPressed(int keyCode, int modifiers) {
      if (keyCode == 70
         && (modifiers & 2) != 0
         && this.state.getBindingModule() == null
         && this.state.getBindingSetting() == null
         && this.state.getEditingStringSetting() == null) {
         this.state.setSearchActive(true);
         this.state.setSearchCursor(this.state.getSearchText().length());
         return true;
      } else if (this.state.getEditingStringSetting() != null) {
         TextSetting setting = (TextSetting)this.state.getEditingStringSetting();
         String text = setting.getValue();
         int cursor = MathHelper.clamp(this.state.getStringCursor(), 0, text.length());
         if (keyCode == 256 || keyCode == 257) {
            this.state.setEditingStringSetting(null);
            return true;
         } else if (keyCode == 259) {
            if (cursor > 0) {
               setting.setValue(text.substring(0, cursor - 1) + text.substring(cursor));
               this.state.setStringCursor(cursor - 1);
            }

            return true;
         } else if (keyCode == 261) {
            if (cursor < text.length()) {
               setting.setValue(text.substring(0, cursor) + text.substring(cursor + 1));
            }

            return true;
         } else if (keyCode == 263) {
            this.state.setStringCursor(Math.max(0, cursor - 1));
            return true;
         } else if (keyCode == 262) {
            this.state.setStringCursor(Math.min(text.length(), cursor + 1));
            return true;
         } else {
            return true;
         }
      } else if (this.state.isSearchActive()) {
         String text = this.state.getSearchText();
         int cursor = MathHelper.clamp(this.state.getSearchCursor(), 0, text.length());
         if (keyCode == 256 || keyCode == 257) {
            this.state.setSearchActive(false);
            return true;
         } else if (keyCode == 259) {
            if (cursor > 0) {
               this.state.setSearchText(text.substring(0, cursor - 1) + text.substring(cursor));
               this.state.setSearchCursor(cursor - 1);
            }

            return true;
         } else if (keyCode == 261) {
            if (cursor < text.length()) {
               this.state.setSearchText(text.substring(0, cursor) + text.substring(cursor + 1));
            }

            return true;
         } else if (keyCode == 263) {
            this.state.setSearchCursor(Math.max(0, cursor - 1));
            return true;
         } else if (keyCode == 262) {
            this.state.setSearchCursor(Math.min(text.length(), cursor + 1));
            return true;
         } else {
            return true;
         }
      } else if (this.state.getBindingModule() != null) {
         if (keyCode == 256) {
            this.state.setBinding(null);
         } else if (keyCode != 261 && keyCode != 259) {
            this.state.getBindingModule().setKey(keyCode);
            this.state.setBinding(null);
         } else {
            this.state.getBindingModule().setKey(-1);
            this.state.setBinding(null);
         }

         return true;
      } else if (this.state.getBindingSetting() == null) {
         return false;
      } else {
         Setting setting = this.state.getBindingSetting();
         if (setting instanceof BindSetting bindSetting) {
            if (keyCode == 256) {
               this.state.setBindingSetting(null);
            } else if (keyCode != 261 && keyCode != 259) {
               bindSetting.setValue(keyCode);
               this.state.setBindingSetting(null);
            } else {
               bindSetting.setValue(-1);
               this.state.setBindingSetting(null);
            }
         } else if (setting instanceof BooleanSetting booleanSetting) {
            if (keyCode == 256) {
               this.state.setBindingSetting(null);
            } else if (keyCode != 261 && keyCode != 259) {
               booleanSetting.setKey(keyCode);
               this.state.setBindingSetting(null);
            } else {
               booleanSetting.setKey(-1);
               this.state.setBindingSetting(null);
            }
         }

         return true;
      }
   }

   public boolean charTyped(char chr, int modifiers) {
      if (this.state.getEditingStringSetting() != null) {
         if (Character.isISOControl(chr)) {
            return false;
         } else {
            TextSetting setting = (TextSetting)this.state.getEditingStringSetting();
            String text = setting.getValue();
            if (text.length() >= 64) {
               return true;
            } else {
               int cursor = MathHelper.clamp(this.state.getStringCursor(), 0, text.length());
               setting.setValue(text.substring(0, cursor) + chr + text.substring(cursor));
               this.state.setStringCursor(cursor + 1);
               return true;
            }
         }
      } else if (!this.state.isSearchActive()) {
         return false;
      } else if (Character.isISOControl(chr)) {
         return false;
      } else {
         String text = this.state.getSearchText();
         if (text.length() >= 24) {
            return true;
         } else {
            int cursor = MathHelper.clamp(this.state.getSearchCursor(), 0, text.length());
            this.state.setSearchText(text.substring(0, cursor) + chr + text.substring(cursor));
            this.state.setSearchCursor(cursor + 1);
            return true;
         }
      }
   }
}
