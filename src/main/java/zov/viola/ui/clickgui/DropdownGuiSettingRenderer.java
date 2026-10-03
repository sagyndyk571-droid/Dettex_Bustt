package zov.viola.ui.clickgui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zov.viola.module.Module;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.Setting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.module.settings.TextSetting;
import zov.viola.module.settings.ThemeSetting;
import zov.viola.obf.D;
import zov.viola.ui.CsPalette;
import zov.viola.util.keyboard.KeyStorage;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class DropdownGuiSettingRenderer {
   private static final MsdfFont REGULAR = Fonts.SFPRO_REGULAR.get();
   private static final float CURSOR_BLINK_MS = 500.0F;
   private static final Map<String, DropdownGuiSettingRenderer.TruncatedName> TRUNCATION_CACHE = new HashMap<>();

   public void render(Module module, float moduleX, float moduleY, float openProgress, double mouseX, double mouseY, DropdownGuiState state, float alphaMul) {
      List<Setting> settings = module.getSettings();
      if (settings != null && !settings.isEmpty()) {
         if (!(openProgress <= 0.001F) && !(alphaMul <= 0.001F)) {
            List<Setting> visible = DropdownGuiLayout.getVisibleSettings(module);
            if (!visible.isEmpty()) {
               float maxSettingHeight = DropdownGuiLayout.calculateSettingsHeight(module);
               float settingsClipY = moduleY + 37.0F;
               float settingsClipHeight = Math.max(1.0F, maxSettingHeight * openProgress);
               Scissor.push();
               Scissor.setFromComponentCoordinates((double)(moduleX + 4.0F), (double)settingsClipY, 136.0, (double)(settingsClipHeight + 1.0F));
               float settingYoffset = 37.0F;

               for (int i = 0; i < visible.size(); i++) {
                  Setting setting = visible.get(i);
                  float settingY = moduleY + settingYoffset + 3.0F;
                  int alpha = (int)(255.0F * openProgress * alphaMul);
                  float current = DropdownGuiLayout.settingHeight(setting);
                  if (setting instanceof BooleanSetting booleanSetting) {
                     this.q1s1Q(moduleX, settingY, alpha, mouseX, mouseY, booleanSetting, state);
                  } else if (setting instanceof SliderSetting sliderSetting) {
                     this.qCRp(moduleX, settingY, alpha, mouseX, mouseY, sliderSetting, state);
                  } else if (setting instanceof ModeSetting modeSetting) {
                     this.bbYk80b(moduleX, settingY, alpha, mouseX, mouseY, modeSetting);
                  } else if (setting instanceof ModeListSetting modeListSetting) {
                     this.v90w(moduleX, settingY, alpha, mouseX, mouseY, modeListSetting);
                  } else if (setting instanceof BindSetting bindSetting) {
                     this.qx79lL(moduleX, settingY, alpha, bindSetting, state);
                  } else if (setting instanceof TextSetting textSetting) {
                     this.gmDaF(moduleX, settingY, alpha, textSetting, state);
                  } else if (setting instanceof ColorSetting colorSetting) {
                     this.gqLe(moduleX, settingY, alpha, colorSetting);
                  } else if (setting instanceof ThemeSetting themeSetting) {
                     this.vAaKA2(moduleX, settingY, alpha, themeSetting);
                  }

                  settingYoffset += current;
                  if (i < visible.size() - 1) {
                     settingYoffset += 2.5F;
                  }
               }

               Scissor.unset();
               Scissor.pop();
            }
         }
      }
   }

   private void q1s1Q(float moduleX, float settingY, int alpha, double mouseX, double mouseY, BooleanSetting setting, DropdownGuiState state) {
      gnjJ(wG1l2(setting.getName(), 6.0F, 100.0F), moduleX + 12.0F + 1.5F, settingY, 6.5F, ColorProvider.rgba(201, 193, 211, (float)alpha));
      boolean binding = state.getBindingSetting() == setting;
      String bind;
      if (binding) {
         bind = "[...]";
      } else {
         int key = setting.getKey();
         bind = key == -1 ? "" : "[" + KeyStorage.getKey(key) + "]";
      }

      if (!bind.isEmpty()) {
         float bindW = REGULAR.getWidth(bind, 6.0F);
         gnjJ(bind, moduleX + 132.0F - 14.0F - bindW, settingY + 1.0F, 6.0F, ColorProvider.rgba(168, 156, 186, binding ? 255.0F : alpha / 2));
      }

      float trackW = 14.0F;
      float trackH = 7.0F;
      float trackX = moduleX + 132.0F - trackW;
      float trackY = settingY + 2.5F;
      boolean isOn = setting.getValue();
      float bgProgress = state.getBooleanBackgroundAnimation(setting).run(isOn ? 1.0F : 0.0F);
      float circleProgress = state.getBooleanCircleAnimation(setting).run(isOn ? 1.0F : 0.0F);
      DrawUtil.drawRound(
         trackX,
         trackY,
         trackW,
         trackH,
         trackH / 2.0F,
         ColorProvider.interpolateColor(
            ColorProvider.rgba(46, 43, 52, (float)alpha), ColorProvider.setAlpha(CsPalette.ACCENT, (int)(alpha * 0.55F)), bgProgress
         )
      );
      float knobSize = 6.0F;
      float knobX = trackX + 1.0F + (trackW - knobSize - 2.0F) * circleProgress;
      float knobY = trackY + (trackH - knobSize) / 2.0F;
      DrawUtil.drawRound(
         knobX,
         knobY,
         knobSize,
         knobSize,
         knobSize / 2.0F,
         ColorProvider.interpolateColor(ColorProvider.rgba(216, 210, 226, (float)alpha), ColorProvider.setAlpha(CsPalette.ACCENT, alpha), circleProgress)
      );
   }

   private void qCRp(float moduleX, float settingY, int alpha, double mouseX, double mouseY, SliderSetting setting, DropdownGuiState state) {
      gnjJ(wG1l2(setting.getName(), 6.0F, 100.0F), moduleX + 12.0F + 1.5F, settingY, 6.0F, ColorProvider.rgba(201, 193, 211, (float)((int)(alpha * 0.6F))));
      String value = String.format("%.1f", setting.getValue());
      float valueW = REGULAR.getWidth(value, 6.0F);
      gnjJ(value, moduleX + 132.0F - 14.0F - valueW, settingY + 1.0F, 6.0F, ColorProvider.rgba(168, 156, 186, (float)((int)(alpha * 0.7F))));
      float sliderX = moduleX + 12.0F;
      float sliderY = settingY + 9.0F;
      float sliderW = 120.0F;
      float sliderH = 4.0F;
      float pos = state.getSliderPos(setting);
      if (state.getDraggingSlider() == setting) {
         double mx = Math.max(0.0, Math.min(1.0, (mouseX - sliderX) / (sliderW - 2.0F)));
         pos = (float)mx;
         state.setSliderValue(setting, state.getSliderValueFromPos(setting, mx));
      }

      float animated = state.getSliderAnimation(setting).run(pos);
      DrawUtil.drawRound(sliderX, sliderY, sliderW, sliderH, sliderH / 2.0F, ColorProvider.rgba(46, 43, 52, (float)alpha));
      float fillW = (sliderW - 2.0F) * animated;
      if (fillW > 0.1F) {
         DrawUtil.drawRound(sliderX + 1.0F, sliderY, fillW, sliderH, sliderH / 2.0F, ColorProvider.setAlpha(CsPalette.ACCENT, (int)(alpha * 0.8F)));
      }

      float knobSize = 4.0F;
      float knobX = sliderX + 1.0F + (sliderW - 2.0F - knobSize) * animated;
      float knobY = sliderY + (sliderH - knobSize) / 2.0F;
      DrawUtil.drawRound(knobX, knobY, knobSize, knobSize, knobSize / 2.0F, ColorProvider.setAlpha(CsPalette.ACCENT, alpha));
   }

   private void bbYk80b(float moduleX, float settingY, int alpha, double mouseX, double mouseY, ModeSetting setting) {
      gnjJ(wG1l2(setting.getName(), 7.0F, 106.0F), moduleX + 12.0F + 1.5F, settingY + 1.5F, 7.0F, ColorProvider.rgba(201, 193, 211, (float)alpha));
      float chipX = moduleX + 12.0F;
      float chipY = settingY + 10.0F;
      List<String> values = setting.getModes();
      if (values != null) {
         for (String value : values) {
            float chipW = REGULAR.getWidth(value, 6.0F) + 8.0F;
            if (chipX + chipW > moduleX + 132.0F) {
               chipX = moduleX + 12.0F;
               chipY += 14.0F;
            }

            boolean selected = value.equals(setting.getValue());
            boolean hovered = HoverUtil.isHovered(mouseX, mouseY, chipX, chipY, chipW, 11.0);
            int bg = selected
               ? ColorProvider.setAlpha(CsPalette.ACCENT, (int)(alpha * 0.85F))
               : ColorProvider.rgba(32, 28, 39, hovered ? (int)(alpha * 0.9F) : alpha);
            int fg = selected ? ColorProvider.setAlpha(-657931, alpha) : ColorProvider.rgba(181, 173, 189, (float)((int)(alpha * 0.7F)));
            DrawUtil.drawRound(chipX, chipY, chipW, 11.0F, 4.0F, bg);
            gnjJ(value, chipX + (chipW - REGULAR.getWidth(value, 6.0F)) / 2.0F, chipY + 2.5F, 6.0F, fg);
            chipX += chipW + 3.0F;
         }
      }
   }

   private void v90w(float moduleX, float settingY, int alpha, double mouseX, double mouseY, ModeListSetting setting) {
      gnjJ(wG1l2(setting.getName(), 7.0F, 104.0F), moduleX + 12.0F + 1.5F, settingY + 1.5F, 7.0F, ColorProvider.rgba(201, 193, 211, (float)alpha));
      List<BooleanSetting> subSettings = setting.getSettings();
      if (subSettings != null) {
         int selectedCount = 0;

         for (BooleanSetting subSetting : subSettings) {
            if (subSetting.getValue()) {
               selectedCount++;
            }
         }

         String counter = selectedCount + "/" + subSettings.size();
         float counterW = REGULAR.getWidth(counter, 6.5F);
         gnjJ(counter, moduleX + 132.0F - 14.0F - counterW, settingY + 2.0F, 6.5F, ColorProvider.rgba(168, 156, 186, (float)((int)(alpha * 0.7F))));
         float chipX = moduleX + 12.0F;
         float chipY = settingY + 10.0F;

         for (BooleanSetting subSettingx : subSettings) {
            String name = subSettingx.getName();
            float chipW = REGULAR.getWidth(name, 6.0F) + 8.0F;
            if (chipX + chipW > moduleX + 132.0F) {
               chipX = moduleX + 12.0F;
               chipY += 14.0F;
            }

            boolean selected = subSettingx.getValue();
            boolean hovered = HoverUtil.isHovered(mouseX, mouseY, chipX, chipY, chipW, 11.0);
            int bg = selected
               ? ColorProvider.setAlpha(CsPalette.ACCENT, (int)(alpha * 0.85F))
               : ColorProvider.rgba(32, 28, 39, hovered ? (int)(alpha * 0.9F) : alpha);
            int fg = selected ? ColorProvider.setAlpha(-657931, alpha) : ColorProvider.rgba(181, 173, 189, (float)((int)(alpha * 0.7F)));
            DrawUtil.drawRound(chipX, chipY, chipW, 11.0F, 4.0F, bg);
            gnjJ(name, chipX + (chipW - REGULAR.getWidth(name, 6.0F)) / 2.0F, chipY + 2.5F, 6.0F, fg);
            chipX += chipW + 3.0F;
         }
      }
   }

   private void qx79lL(float moduleX, float settingY, int alpha, BindSetting setting, DropdownGuiState state) {
      gnjJ(wG1l2(setting.getName(), 6.0F, 100.0F), moduleX + 12.0F + 1.5F, settingY, 6.5F, ColorProvider.rgba(201, 193, 211, (float)alpha));
      boolean binding = state.getBindingSetting() == setting;
      String bind = binding ? "[...]" : "[" + KeyStorage.getKey(setting.getValue()) + "]";
      float bindW = REGULAR.getWidth(bind, 6.0F);
      float chipW = bindW + 12.0F;
      float chipX = moduleX + 132.0F - 14.0F - chipW;
      float chipH = 12.0F;
      DrawUtil.drawRound(chipX, settingY, chipW, chipH, 4.0F, ColorProvider.rgba(32, 28, 39, (float)alpha));
      gnjJ(bind, chipX + 6.0F, settingY + 3.0F, 6.0F, ColorProvider.rgba(220, 212, 230, binding ? 255.0F : alpha));
   }

   private void gmDaF(float moduleX, float settingY, int alpha, TextSetting setting, DropdownGuiState state) {
      gnjJ(wG1l2(setting.getName(), 6.5F, 106.0F), moduleX + 12.0F + 1.5F, settingY, 6.5F, ColorProvider.rgba(201, 193, 211, (float)((int)(alpha * 0.6F))));
      float fieldX = moduleX + 12.0F;
      float fieldY = settingY + 9.0F;
      float fieldW = 120.0F;
      float fieldH = 11.0F;
      DrawUtil.drawRound(fieldX, fieldY, fieldW, fieldH, 4.0F, ColorProvider.rgba(32, 28, 39, (float)alpha));
      String value = setting.getValue();
      String fitted = value == null ? "" : wmE1e7p(value, fieldW - 12.0F);
      gnjJ(fitted, fieldX + 6.0F, fieldY + (fieldH - 6.0F) / 2.0F, 6.0F, ColorProvider.rgba(220, 212, 230, (float)alpha));
      if (state.getEditingStringSetting() == setting && a9MRZ()) {
         float cursorX = fieldX + 6.0F + REGULAR.getWidth(fitted, 6.0F);
         DrawUtil.drawRound(cursorX, fieldY + 3.0F, 1.0F, fieldH - 6.0F, 0.5F, ColorProvider.rgba(220, 212, 230, (float)alpha));
      }
   }

   private void gqLe(float moduleX, float settingY, int alpha, ColorSetting setting) {
      gnjJ(wG1l2(setting.getName(), 6.5F, 98.0F), moduleX + 12.0F + 1.5F, settingY + 1.5F, 6.5F, ColorProvider.rgba(201, 193, 211, (float)alpha));
      float swatchSize = 12.0F;
      float swatchX = moduleX + 132.0F - 14.0F - swatchSize;
      float swatchY = settingY + 2.0F;
      DrawUtil.drawRound(swatchX - 0.5F, swatchY - 0.5F, swatchSize + 1.0F, swatchSize + 1.0F, 4.5F, ColorProvider.rgba(32, 28, 39, (float)alpha));
      DrawUtil.drawRound(swatchX, swatchY, swatchSize, swatchSize, 4.0F, ColorProvider.setAlpha(setting.getValue(), (int)(alpha * 0.9F)));
   }

   private void vAaKA2(float moduleX, float settingY, int alpha, ThemeSetting setting) {
      gnjJ(wG1l2(setting.getName(), 6.5F, 90.0F), moduleX + 12.0F + 1.5F, settingY + 1.5F, 6.5F, ColorProvider.rgba(201, 193, 211, (float)alpha));
      String value = setting.getValue().name;
      float valueW = REGULAR.getWidth(value, 6.0F);
      gnjJ(value, moduleX + 132.0F - 14.0F - valueW, settingY + 2.0F, 6.0F, ColorProvider.setAlpha(CsPalette.ACCENT, (int)(alpha * 0.85F)));
   }

   private static boolean a9MRZ() {
      return (float)(System.currentTimeMillis() % 1000L) < 500.0F;
   }

   private static void gnjJ(String text, float x, float y, float size, int color) {
      DrawUtil.drawText(REGULAR, text, x, y, color, size);
   }

   private static String wmE1e7p(String text, float maxWidth) {
      if (text != null && !text.isEmpty()) {
         if (REGULAR.getWidth(text, 6.0F) <= maxWidth) {
            return text;
         } else {
            for (int i = text.length(); i > 0; i--) {
               String candidate = text.substring(0, i) + "...";
               if (REGULAR.getWidth(candidate, 6.0F) <= maxWidth) {
                  return candidate;
               }
            }

            return text.substring(0, 1) + "...";
         }
      } else {
         return text;
      }
   }

   private static String wG1l2(String text, float size, float maxWidth) {
      if (text != null && !text.isEmpty()) {
         String key = text + "@" + size;
         DropdownGuiSettingRenderer.TruncatedName cached = TRUNCATION_CACHE.get(key);
         if (cached != null && cached.maxWidth() == maxWidth && cached.text() != null) {
            return cached.text();
         } else if (REGULAR.getWidth(text, size) <= maxWidth) {
            return text;
         } else {
            String result = text;

            for (int i = text.length(); i > 0; i--) {
               String candidate = text.substring(0, i) + "...";
               if (REGULAR.getWidth(candidate, size) <= maxWidth) {
                  result = candidate;
                  break;
               }
            }

            TRUNCATION_CACHE.put(key, new DropdownGuiSettingRenderer.TruncatedName(maxWidth, result));
            if (TRUNCATION_CACHE.size() > 300) {
               TRUNCATION_CACHE.clear();
            }

            return result;
         }
      } else {
         return text;
      }
   }

   private record TruncatedName(float maxWidth, String text) {

      

      

      
   }
}
