package zov.viola.ui;

import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import zov.viola.obf.D;
import zov.viola.util.alt.AltManager;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.builders.Builder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.menu.MenuBackgroundRenderer;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;
import zov.viola.util.render.renderers.IRenderer;

public class AltScreen extends Screen {
   private final Screen c52y;
   private String vwqb44 = "";
   private float xm2j610 = 0.0F;
   private float w7642l = 0.0F;
   private float h64sTY = 0.0F;
   private float[] s1hdQ = new float[0];
   private static final float PANEL_W = 470.0F;
   private static final float PADDING = 14.0F;
   private static final float ROW_H = 38.0F;
   private static final float ROW_GAP = 5.0F;

   public AltScreen(Screen parent) {
      super(Text.of("Alt Manager"));
      this.c52y = parent;
      AltManager.load();
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      int screenW = context.getScaledWindowWidth();
      int screenH = context.getScaledWindowHeight();
      if (screenW > 0 && screenH > 0) {
         MenuBackgroundRenderer.render(context);
         int white = -1;
         int textMain = -986379;
         int textDim = -6248268;
         int cardBg = -16448249;
         int cardBgHover = -15856110;
         int cardBorder = -12961214;
         boolean backHover = HoverUtil.isHovered(mouseX, mouseY, 16.0, 16.0, 70.0, 28.0);
         this.h64sTY = this.h64sTY + ((backHover ? 1.0F : 0.0F) - this.h64sTY) * 0.15F;
         if (backHover) {
            CursorManager.requestHand();
         }

         DrawUtil.drawRound(16.0F, 16.0F, 70.0F, 28.0F, 3.0F, backHover ? cardBgHover : cardBg);
         DrawUtil.drawText(Fonts.SFMEDIUM.get(), "Назад", 23.0F, 23.0F, white, 11.0F);
         float panelW = Math.min(470.0F, screenW - 40.0F);
         float panelH = Math.min(screenH * 0.82F, 620.0F);
         float panelX = (screenW - panelW) / 2.0F;
         float panelY = screenH * 0.08F;
         String title = "Аккаунты";
         float titleSize = 24.0F;
         float titleW = Fonts.SFBOLD.get().getWidth(title, titleSize);
         DrawUtil.drawText(Fonts.SFBOLD.get(), title, screenW / 2.0F - titleW / 2.0F, panelY - 28.0F, white, titleSize);
         DrawUtil.drawRound(panelX, panelY, panelW, panelH, 9.0F, -435878649);
         plG72(panelX, panelY, panelW, panelH, cardBorder);
         float fieldX = panelX + 14.0F;
         float fieldW = panelW - 28.0F;
         float fieldH = 32.0F;
         float inputY = panelY + 44.0F;
         float buttonsY = inputY + fieldH + 9.0F;
         float btnH = 26.0F;
         float listY = buttonsY + btnH + 13.0F;
         float listBottom = panelY + panelH - 14.0F;
         float listH = listBottom - listY;
         String current = AltManager.getCurrentAlt();
         String curLabel = "Текущий аккаунт";
         float curLabelW = Fonts.SFMEDIUM.get().getWidth(curLabel, 10.0F);
         DrawUtil.drawText(Fonts.SFMEDIUM.get(), curLabel, fieldX, panelY + 16.0F, textDim, 10.0F);
         DrawUtil.drawText(Fonts.SFMEDIUM.get(), current, fieldX + curLabelW + 10.0F, panelY + 16.0F, -8201086, 10.0F);
         DrawUtil.drawRound(fieldX, panelY + 30.0F, fieldW, 1.0F, 0.5F, cardBorder);
         boolean fieldHover = HoverUtil.isHovered(mouseX, mouseY, fieldX, inputY, fieldW, fieldH);
         if (fieldHover) {
            CursorManager.requestIBeam();
         }

         DrawUtil.drawRound(fieldX, inputY, fieldW, fieldH, 5.0F, fieldHover ? cardBgHover : cardBg);
         plG72(fieldX, inputY, fieldW, fieldH, cardBorder);
         String shown = this.vwqb44.isEmpty()
            ? "Введите ник..."
            : this.vwqb44;
         int placeholderColor = this.vwqb44.isEmpty() ? ColorProvider.setAlpha(textDim, 150) : white;
         DrawUtil.drawText(Fonts.SFREGULAR.get(), shown, fieldX + 9.0F, inputY + fieldH / 2.0F - 5.0F, placeholderColor, 10.5F);
         float btnGap = 6.0F;
         float btnW = (fieldW - btnGap * 2.0F) / 3.0F;
         int[] var10000 = new int[]{cardBg, cardBg, cardBg};
         boolean[] btnHov = new boolean[]{
            HoverUtil.isHovered(mouseX, mouseY, fieldX, buttonsY, btnW, btnH),
            HoverUtil.isHovered(mouseX, mouseY, fieldX + btnW + btnGap, buttonsY, btnW, btnH),
            HoverUtil.isHovered(mouseX, mouseY, fieldX + (btnW + btnGap) * 2.0F, buttonsY, btnW, btnH)
         };
         String[] btnLabels = new String[]{
            "Случайный",
            "Добавить",
            "Войти"
         };

         for (int i = 0; i < 3; i++) {
            if (btnHov[i]) {
               CursorManager.requestHand();
            }

            float bx = fieldX + i * (btnW + btnGap);
            DrawUtil.drawRound(bx, buttonsY, btnW, btnH, 3.0F, btnHov[i] ? cardBgHover : cardBg);
            int labelColor = i == 2 ? -15987700 : white;
            float lw = Fonts.SFMEDIUM.get().getWidth(btnLabels[i], 11.0F);
            DrawUtil.drawText(Fonts.SFMEDIUM.get(), btnLabels[i], bx + btnW / 2.0F - lw / 2.0F, buttonsY + 7.0F, labelColor, 11.0F);
         }

         List<String> alts = AltManager.getAlts();
         float contentH = alts.size() * 43.0F;
         this.w7642l = Math.max(0.0F, contentH - listH);
         if (this.xm2j610 > this.w7642l) {
            this.xm2j610 = this.w7642l;
         }

         if (this.xm2j610 < 0.0F) {
            this.xm2j610 = 0.0F;
         }

         if (this.s1hdQ.length != alts.size()) {
            this.s1hdQ = new float[alts.size()];
         }

         int clipX = (int)(panelX + 14.0F);
         int clipY = (int)listY;
         int clipW = (int)fieldW;
         int clipH = (int)listH;
         context.enableScissor(clipX, clipY, clipX + clipW, clipY + clipH);
         if (alts.isEmpty()) {
            DrawUtil.drawText(
               Fonts.SFREGULAR.get(),
               D.k(
                  new int[]{
                     1139,
                     1216,
                     1090,
                     1262,
                     1056,
                     1223,
                     1082,
                     1248,
                     1105,
                     218,
                     1095,
                     1248,
                     1113,
                     1226,
                     88,
                     1251,
                     1110,
                     1208,
                     88,
                     8394,
                     67,
                     1230,
                     1094,
                     1263,
                     1107,
                     1224,
                     1076,
                     254,
                     1115,
                     1217,
                     1088,
                     254,
                     1058,
                     1225,
                     1101,
                     1251,
                     1110,
                     1210,
                     1088,
                     1182,
                     1056,
                     1219,
                     88,
                     1251,
                     1115,
                     1216
                  },
                  new int[]{99, 250, 120, 222}
               ),
               panelX + 14.0F + 4.0F,
               listY + 14.0F,
               ColorProvider.setAlpha(textDim, 150),
               11.0F
            );
         }

         for (int i = 0; i < alts.size(); i++) {
            float rowY = listY - this.xm2j610 + i * 43.0F;
            if (!(rowY + 38.0F < listY) && !(rowY > listBottom)) {
               String alt = alts.get(i);
               boolean isCurrent = alt.equalsIgnoreCase(current);
               boolean hovered = HoverUtil.isHovered(mouseX, mouseY, panelX + 14.0F, rowY, fieldW, 38.0);
               this.s1hdQ[i] = this.s1hdQ[i] + ((hovered ? 1.0F : 0.0F) - this.s1hdQ[i]) * 0.2F;
               if (hovered) {
                  CursorManager.requestHand();
               }

               int bg;
               if (isCurrent) {
                  bg = ColorProvider.interpolate(ColorProvider.rgba(16, 36, 24, 230.0F), ColorProvider.rgba(26, 56, 38, 230.0F), this.s1hdQ[i]);
               } else {
                  bg = ColorProvider.interpolate(-1072425960, -1071899614, this.s1hdQ[i]);
               }

               DrawUtil.drawRound(panelX + 14.0F, rowY, fieldW, 38.0F, 3.0F, bg);
               DrawUtil.drawCircle(panelX + 14.0F + 15.0F, rowY + 19.0F, isCurrent ? 4.0F : 3.0F, isCurrent ? -8201086 : ColorProvider.setAlpha(textDim, 200));
               int textColor = isCurrent ? white : ColorProvider.interpolate(ColorProvider.rgba(225, 225, 225, 255.0F), white, this.s1hdQ[i]);
               DrawUtil.drawText(Fonts.SFREGULAR.get(), alt, panelX + 14.0F + 27.0F, rowY + 19.0F - 5.0F, textColor, 10.0F);
               float delX = panelX + panelW - 14.0F - 32.0F;
               boolean delHover = HoverUtil.isHovered(mouseX, mouseY, delX, rowY + 4.0F, 28.0, 30.0);
               if (delHover) {
                  CursorManager.requestHand();
               }

               DrawUtil.drawRound(delX, rowY + 4.0F, 28.0F, 30.0F, 3.0F, delHover ? -423079856 : -1634976708);
               DrawUtil.drawText(Fonts.ICONS_MINCED.get(), "x", delX + 10.0F, rowY + 19.0F - 5.0F, white, 9.0F);
               if (isCurrent) {
                  String tag = "Текущий";
                  float tagSize = 8.5F;
                  float tagW = Fonts.SFMEDIUM.get().getWidth(tag, tagSize);
                  float tagRight = delX - 10.0F;
                  DrawUtil.drawRound(tagRight - tagW - 14.0F, rowY + 19.0F - 9.0F, tagW + 14.0F, 18.0F, 5.0F, 1384307842);
                  DrawUtil.drawText(Fonts.SFMEDIUM.get(), tag, tagRight - tagW - 7.0F, rowY + 19.0F - 4.5F, -8201086, tagSize);
               }
            }
         }

         context.disableScissor();
         if (this.w7642l > 0.5F) {
            float barH = Math.max(30.0F, listH * (listH / contentH));
            float barY = listY + this.xm2j610 / this.w7642l * (listH - barH);
            DrawUtil.drawRound(panelX + panelW - 14.0F - 3.0F, barY, 3.0F, barH, 1.5F, ColorProvider.setAlpha(textDim, 160));
         }
      }
   }

   private static void plG72(float x, float y, float width, float height, int color) {
      Builder.border()
         .size(new SizeState(width, height))
         .radius(new QuadRadiusState(6.0F))
         .color(new QuadColorState(color))
         .thickness(1.5F)
         .smoothness(1.0F, 1.0F)
         .build()
         .render(IRenderer.DEFAULT_MATRIX, x, y, 0.0F);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      int screenW = this.client.getWindow().getScaledWidth();
      int screenH = this.client.getWindow().getScaledHeight();
      if (HoverUtil.isHovered(mouseX, mouseY, 16.0, 16.0, 70.0, 28.0)) {
         this.client.setScreen(this.c52y);
         return true;
      } else {
         float panelW = Math.min(470.0F, screenW - 40.0F);
         float panelH = Math.min(screenH * 0.82F, 620.0F);
         float panelX = (screenW - panelW) / 2.0F;
         float panelY = screenH * 0.08F;
         float fieldX = panelX + 14.0F;
         float fieldW = panelW - 28.0F;
         float fieldH = 32.0F;
         float inputY = panelY + 44.0F;
         float buttonsY = inputY + fieldH + 9.0F;
         float btnH = 26.0F;
         if (HoverUtil.isHovered(mouseX, mouseY, fieldX, inputY, fieldW, fieldH)) {
            return super.mouseClicked(mouseX, mouseY, button);
         } else {
            float btnGap = 6.0F;
            float btnW = (fieldW - btnGap * 2.0F) / 3.0F;
            if (HoverUtil.isHovered(mouseX, mouseY, fieldX, buttonsY, btnW, btnH)) {
               this.vwqb44 = AltManager.randomNick();
               return true;
            } else if (HoverUtil.isHovered(mouseX, mouseY, fieldX + btnW + btnGap, buttonsY, btnW, btnH)) {
               if (!this.vwqb44.trim().isEmpty()) {
                  AltManager.addAlt(this.vwqb44.trim());
                  this.vwqb44 = "";
               }

               return true;
            } else if (HoverUtil.isHovered(mouseX, mouseY, fieldX + (btnW + btnGap) * 2.0F, buttonsY, btnW, btnH)) {
               if (!this.vwqb44.trim().isEmpty()) {
                  AltManager.login(this.vwqb44.trim());
               }

               return true;
            } else {
               List<String> alts = AltManager.getAlts();
               float listY = buttonsY + btnH + 13.0F;
               float listBottom = panelY + panelH - 14.0F;

               for (int i = 0; i < alts.size(); i++) {
                  float rowY = listY - this.xm2j610 + i * 43.0F;
                  if (!(rowY + 38.0F < listY) && !(rowY > listBottom)) {
                     float delX = panelX + panelW - 14.0F - 32.0F;
                     if (HoverUtil.isHovered(mouseX, mouseY, delX, rowY + 4.0F, 28.0, 30.0)) {
                        AltManager.removeAlt(alts.get(i));
                        return true;
                     }

                     if (HoverUtil.isHovered(mouseX, mouseY, panelX + 14.0F, rowY, fieldW, 38.0)) {
                        AltManager.login(alts.get(i));
                        return true;
                     }
                  }
               }

               return super.mouseClicked(mouseX, mouseY, button);
            }
         }
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.xm2j610 -= (float)verticalAmount * 20.0F;
      return true;
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.client.setScreen(this.c52y);
         return true;
      } else if (keyCode == 259 && !this.vwqb44.isEmpty()) {
         this.vwqb44 = this.vwqb44.substring(0, this.vwqb44.length() - 1);
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      if (chr >= ' ' && this.vwqb44.length() < 32) {
         this.vwqb44 = this.vwqb44 + chr;
         return true;
      } else {
         return super.charTyped(chr, modifiers);
      }
   }

   @Override
   public void close() {
      this.client.setScreen(this.c52y);
   }

   @Override
   public boolean shouldPause() {
      return true;
   }
}
