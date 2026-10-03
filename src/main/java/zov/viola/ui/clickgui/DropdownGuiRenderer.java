package zov.viola.ui.clickgui;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector4f;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.obf.D;
import zov.viola.ui.CsPalette;
import zov.viola.util.keyboard.KeyStorage;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class DropdownGuiRenderer {
   private static final MsdfFont REGULAR = Fonts.SFPRO_REGULAR.get();
   private static final MsdfFont MEDIUM = Fonts.SFPRO_MEDIUM.get();
   private static final MsdfFont ICONS = Fonts.CLICKGUI.get();
   private static final float CARD_RADIUS = 5.0F;
   private static final float ACCENT_RADIUS = 5.0F;
   private final DropdownGuiState d0wp;
   private final DropdownGuiSettingRenderer uakaZ = new DropdownGuiSettingRenderer();

   public DropdownGuiRenderer(DropdownGuiState state) {
      this.d0wp = state;
   }

   public void render(double mouseX, double mouseY, float alphaMul) {
      if (!(alphaMul <= 0.001F)) {
         ModuleCategory[] categories = DropdownGuiLayout.DROPDOWN_CATEGORIES;

         for (int i = 0; i < categories.length; i++) {
            this.hVdAC(categories[i], DropdownGuiLayout.getCategoryPanelX(this.d0wp.getX(), i), mouseX, mouseY, alphaMul);
         }

         this.tTn5(mouseX, mouseY, alphaMul);
      }
   }

   private void hVdAC(ModuleCategory category, float columnX, double mouseX, double mouseY, float alphaMul) {
      float columnY = this.d0wp.getY() + this.d0wp.getRenderOffsetY();
      DrawUtil.drawRound(columnX, columnY, 144.0F, 396.0F, 5.0F, frrFlu(29, 26, 34, 240, alphaMul));
      DrawUtil.drawRound(columnX, columnY, 144.0F, 26.0F, new Vector4f(5.0F, 5.0F, 0.0F, 0.0F), frrFlu(24, 22, 31, 255, alphaMul));
      String icon = sPllb5(category);
      DrawUtil.drawText(ICONS, icon, columnX + 7.0F, columnY + 10.0F, ColorProvider.setAlpha(CsPalette.ACCENT, (int)(255.0F * alphaMul)), 6.0F);
      String title = djHua(category);
      DrawUtil.drawText(MEDIUM, title, columnX + 17.0F, columnY + 10.5F, frrFlu(249, 247, 252, 255, alphaMul), 6.5F);
      int moduleCount = this.d0wp.getModules(category).size();
      String countText = String.valueOf(moduleCount);
      float countW = REGULAR.getWidth(countText, 5.5F);
      float badgeW = Math.max(12.0F, countW + 6.0F);
      float badgeX = columnX + 144.0F - badgeW - 8.0F;
      DrawUtil.drawRound(badgeX, columnY + 8.0F, badgeW, 11.0F, 5.5F, frrFlu(20, 18, 25, 240, alphaMul));
      DrawUtil.drawText(REGULAR, countText, badgeX + (badgeW - countW) / 2.0F, columnY + 10.0F, frrFlu(224, 216, 236, 255, alphaMul), 5.5F);
      float contentTop = DropdownGuiLayout.getContentY(columnY);
      float contentHeight = DropdownGuiLayout.getContentHeight();
      this.d0wp.clampScroll(category, contentHeight - 4.0F);
      float scroll = this.d0wp.getScroll(category);
      Scissor.push();
      Scissor.setFromComponentCoordinates((double)columnX, (double)contentTop, 144.0, (double)contentHeight);
      List<Module> modules = this.d0wp.getModules(category);
      if (modules.isEmpty() && !this.d0wp.getSearchText().isBlank()) {
         DrawUtil.drawText(
            REGULAR,
            "Ничего не найдено",
            columnX + 12.0F,
            contentTop + 16.0F,
            frrFlu(153, 146, 163, 245, alphaMul),
            6.0F
         );
      } else {
         float moduleY = contentTop + 2.0F + scroll;

         for (Module module : modules) {
            float moduleHeight = this.d0wp.getModuleHeight(module);
            if (moduleY + moduleHeight > contentTop && moduleY < contentTop + contentHeight) {
               this.nl4ell(module, columnX, moduleY, mouseX, mouseY, alphaMul);
            }

            moduleY += moduleHeight + 4.0F;
         }
      }

      Scissor.unset();
      Scissor.pop();
   }

   private void nl4ell(Module module, float panelX, float y, double mouseX, double mouseY, float alphaMul) {
      float x = panelX + 4.0F;
      float moduleHeight = this.d0wp.getModuleHeight(module);
      boolean hovered = HoverUtil.isHovered(mouseX, mouseY, x, y, 136.0, 37.0);
      boolean enabled = module.isEnabled();
      boolean open = this.d0wp.isModuleOpen(module);
      DrawUtil.drawRound(
         x,
         y,
         136.0F,
         moduleHeight,
         5.0F,
         enabled
            ? ColorProvider.setAlpha(CsPalette.ACCENT, (int)(46.0F * alphaMul))
            : (hovered ? ColorProvider.rgba(33, 29, 40, (float)((int)(255.0F * alphaMul))) : ColorProvider.rgba(25, 23, 30, (float)((int)(255.0F * alphaMul))))
      );
      float dotProgress = this.d0wp.getModuleDotAnimation(module).run(enabled ? 1.0F : 0.0F);
      float openProgress = this.d0wp.getModuleOpenAnimation(module).run(open ? 1.0F : 0.0F);
      if (dotProgress > 0.01F) {
         DrawUtil.drawRound(x + 12.0F, y + 9.0F, 5.0F, 5.0F, 1.5F, ColorProvider.setAlpha(CsPalette.ACCENT, (int)(255.0F * alphaMul * dotProgress)));
      }

      float labelX = x + 12.0F + 10.0F * dotProgress;
      String name = o7oa(module.getName(), 6.5F, x + 104.0F - labelX);
      DrawUtil.drawText(
         MEDIUM,
         name,
         labelX,
         y + 9.0F,
         enabled ? ColorProvider.setAlpha(-527108, (int)(255.0F * alphaMul)) : ColorProvider.rgba(185, 177, 192, (float)((int)(255.0F * alphaMul))),
         6.5F
      );
      if (this.d0wp.getBindingModule() == module) {
         String bindingText = D.k(
            new int[]{1213, 1091, 1033, 1184, 1176, 1073, 1034, 188, 1178, 1096, 1039, 1198, 1176, 1083, 1148, 178, 142, 93}, new int[]{160, 115, 63, 156}
         );
         DrawUtil.drawText(REGULAR, bindingText, x + 12.0F, y + 9.0F, ColorProvider.setAlpha(CsPalette.ACCENT, (int)(255.0F * alphaMul)), 5.5F);
      } else if (module.getKey() != -1) {
         String key = KeyStorage.getKey(module.getKey());
         DrawUtil.drawText(REGULAR, key, x + 12.0F, y + 31.0F, ColorProvider.setAlpha(CsPalette.ACCENT, (int)(245.0F * alphaMul)), 5.0F);
      }

      if (this.wbI9ZgM(module)) {
         List<String> lines = this.tjHoxS0(module);
         float descY = y + 20.0F;

         for (String line : lines) {
            DrawUtil.drawText(REGULAR, line, x + 12.0F, descY, frrFlu(153, 146, 163, 245, alphaMul), 5.5F);
            descY += 6.0F;
         }
      }

      if (DropdownGuiLayout.hasVisibleSettings(module)) {
         String expandSymbol = this.d0wp.isModuleOpen(module) ? "-" : "+";
         DrawUtil.drawText(MEDIUM, expandSymbol, x + 113.0F, y + 11.0F, frrFlu(160, 152, 176, 255, alphaMul), 9.0F);
      }

      float toggleX = x + 122.0F;
      float toggleY = y + 8.0F;
      DrawUtil.drawRound(
         toggleX,
         toggleY,
         10.0F,
         6.0F,
         3.0F,
         ColorProvider.interpolateColor(
            ColorProvider.rgba(44, 41, 49, (float)((int)(255.0F * alphaMul))), ColorProvider.setAlpha(CsPalette.ACCENT, (int)(255.0F * alphaMul)), dotProgress
         )
      );
      DrawUtil.drawRound(
         toggleX + 1.0F + 4.0F * dotProgress,
         toggleY + 1.0F,
         4.0F,
         4.0F,
         2.0F,
         ColorProvider.interpolateColor(
            ColorProvider.rgba(91, 86, 100, (float)((int)(255.0F * alphaMul))), ColorProvider.setAlpha(-657931, (int)(255.0F * alphaMul)), dotProgress
         )
      );
      if (openProgress > 0.001F) {
         this.uakaZ.render(module, panelX, y, openProgress, mouseX, mouseY, this.d0wp, alphaMul);
      }
   }

   private void tTn5(double mouseX, double mouseY, float alphaMul) {
      float searchX = DropdownGuiLayout.getSearchX(this.d0wp.getX());
      float searchY = DropdownGuiLayout.getSearchY(this.d0wp.getY() + this.d0wp.getRenderOffsetY());
      boolean active = this.d0wp.isSearchActive() || !this.d0wp.getSearchText().isBlank();
      DrawUtil.drawRound(searchX, searchY, 120.0F, 22.0F, 6.0F, frrFlu(24, 22, 29, active ? 245 : 210, alphaMul));
      String textult = active
         ? this.d0wp.getSearchText()
         : "Ctrl+F поиск";
      String shown = o7oa(textult, 6.5F, 106.0F);
      float textX;
      if (active) {
         textX = searchX + 7.0F;
      } else {
         float textW = REGULAR.getWidth(shown, 6.5F);
         textX = searchX + (120.0F - textW) / 2.0F;
      }

      DrawUtil.drawText(
         REGULAR,
         shown,
         textX,
         searchY + 7.75F,
         active ? ColorProvider.rgba(220, 212, 230, (float)((int)(255.0F * alphaMul))) : ColorProvider.rgba(170, 158, 184, (float)((int)(140.0F * alphaMul))),
         6.5F
      );
      if (active && this.d0wp.isSearchActive() && lp21()) {
         float cursorX = textX + REGULAR.getWidth(shown, 6.5F);
         DrawUtil.drawRound(cursorX, searchY + 4.0F, 1.0F, 14.0F, 0.5F, ColorProvider.rgba(220, 212, 230, (float)((int)(255.0F * alphaMul))));
      }
   }

   private static boolean lp21() {
      return System.currentTimeMillis() % 1000L < 500L;
   }

   private static String sPllb5(ModuleCategory category) {
      return switch (category) {
         case COMBAT -> "M";
         case MOVEMENT -> "L";
         case RENDER -> "G";
         case PLAYER -> "H";
         case MISC -> "I";
      };
   }

   private static String djHua(ModuleCategory category) {
      String name = category.name();
      return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
   }

   private boolean wbI9ZgM(Module module) {
      String desc = module.getDesc();
      return desc != null && !desc.isBlank() && this.d0wp.getBindingModule() != module;
   }

   private List<String> tjHoxS0(Module module) {
      String desc = module.getDesc();
      List<String> lines = new ArrayList<>();
      if (desc != null && !desc.isEmpty()) {
         float maxWidth = 122.0F;
         if (REGULAR.getWidth(desc, 5.5F) <= maxWidth) {
            lines.add(desc);
            return lines;
         } else {
            String[] words = desc.split(" ");
            StringBuilder current = new StringBuilder();

            for (String word : words) {
               String candidate = current.length() == 0 ? word : current + " " + word;
               if (REGULAR.getWidth(candidate, 5.5F) <= maxWidth) {
                  current.append(current.length() == 0 ? word : " " + word);
               } else {
                  if (current.length() > 0) {
                     lines.add(current.toString());
                     current.setLength(0);
                     if (lines.size() >= 2) {
                        return lines;
                     }
                  }

                  current.append(word);
               }
            }

            if (current.length() > 0 && lines.size() < 2) {
               lines.add(current.toString());
            }

            return lines;
         }
      } else {
         return lines;
      }
   }

   private static String o7oa(String text, float size, float maxWidth) {
      if (text != null && !text.isEmpty() && !(maxWidth <= 0.0F)) {
         if (REGULAR.getWidth(text, size) <= maxWidth) {
            return text;
         } else {
            for (int i = text.length(); i > 0; i--) {
               String candidate = text.substring(0, i) + "...";
               if (REGULAR.getWidth(candidate, size) <= maxWidth) {
                  return candidate;
               }
            }

            return text.substring(0, 1) + "...";
         }
      } else {
         return text == null ? "" : text;
      }
   }

   private static int frrFlu(int r, int g, int b, int a, float alphaMul) {
      return ColorProvider.rgba(r, g, b, (float)((int)(a * alphaMul)));
   }
}
