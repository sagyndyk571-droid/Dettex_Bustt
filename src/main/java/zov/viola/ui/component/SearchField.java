package zov.viola.ui.component;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import zov.viola.obf.D;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class SearchField {
   private float oJ6mQhr;
   private float qZHlnm;
   private float lz556v2;
   private float mgJw;
   public String text = "";
   private boolean xaPVDxR;
   private final String rm5uZ5G;
   private int pV8k;
   private int eqc8;
   private float e1qqr39;
   private static final float ICON_BOX_W = 17.0F;
   private static final float RADIUS = 4.0F;
   private static final float FONT_SIZE = 7.0F;
   private static final int MAX_LENGTH = 64;
   private final Animation sSE4flO = new Animation(Easing.QUINTIC_OUT, 220L);
   private final Animation dfuOmfX = new Animation(Easing.QUINTIC_OUT, 340L);

   public SearchField(String placeholder) {
      this.rm5uZ5G = placeholder;
   }

   public void resetAppear() {
      this.dfuOmfX.reset(0.0F);
   }

   public void setBounds(float x, float y, float width, float height) {
      this.oJ6mQhr = x;
      this.qZHlnm = y;
      this.lz556v2 = width;
      this.mgJw = height;
   }

   private float h611qc() {
      return this.oJ6mQhr + 17.0F + 3.0F;
   }

   private float kay9IC1() {
      return this.lz556v2 - 17.0F - 6.0F;
   }

   private float waBg(String s) {
      return Fonts.SFREGULAR.get().getWidth(s, 7.0F);
   }

   private boolean xEqXSW() {
      return this.pV8k != this.eqc8;
   }

   private int opkg5() {
      return Math.min(this.pV8k, this.eqc8);
   }

   private int bZbfhw() {
      return Math.max(this.pV8k, this.eqc8);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
      boolean hovered = HoverUtil.isHovered(mouseX, mouseY, this.oJ6mQhr, this.qZHlnm, this.lz556v2, this.mgJw);
      if (hovered) {
         CursorManager.requestIBeam();
      }

      this.sSE4flO.run(this.xaPVDxR);
      float fa = this.sSE4flO.getValue();
      this.dfuOmfX.run(true);
      float ap = this.dfuOmfX.getValue();
      float ay = this.qZHlnm + (1.0F - ap) * 9.0F;
      DrawUtil.drawRoundBlur(this.oJ6mQhr, ay, this.lz556v2, this.mgJw, 4.0F, ColorProvider.rgba(200, 200, 200, (float)((int)(255.0F * ap))), 14.0F);
      int borderColor = ColorProvider.interpolateColor(
         ColorProvider.rgba(48, 66, 122, (float)((int)(70.0F * ap))), ColorProvider.setAlpha(ColorProvider.getColorClient(), (int)(150.0F * ap)), fa
      );
      DrawUtil.drawRound(this.oJ6mQhr - 0.5F, ay - 0.5F, this.lz556v2 + 1.0F, this.mgJw + 1.0F, 4.5F, borderColor);
      DrawUtil.drawRound(this.oJ6mQhr, ay, this.lz556v2, this.mgJw, 4.0F, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), (int)(130.0F * ap)));
      float iconW = Fonts.ICONS_MINCED.get().getWidth("l", 10.0F);
      float iconX = this.oJ6mQhr + (17.0F - iconW) / 2.0F + 1.0F;
      float iconY = ay + this.mgJw / 2.0F - 4.5F;
      int iconColor = ColorProvider.interpolateColor(
         ColorProvider.setAlpha(ColorProvider.getColorIcons(), (int)(160.0F * ap)),
         ColorProvider.setAlpha(ColorProvider.getColorIcons(), (int)(255.0F * ap)),
         fa
      );
      DrawUtil.drawText(Fonts.ICONS_MINCED.get(), "l", iconX, iconY, iconColor, 10.0F);
      float taX = this.h611qc();
      float taW = this.kay9IC1();
      float textY = ay + this.mgJw / 2.0F - 3.5F + 0.2F;
      this.qI7Vx2(taW);
      Scissor.push();
      Scissor.setFromComponentCoordinates((double)taX, (double)ay, (double)taW, (double)this.mgJw);
      if (this.text.isEmpty() && !this.xaPVDxR) {
         DrawUtil.drawText(
            Fonts.SFREGULAR.get(), this.rm5uZ5G, taX, textY, ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), (int)(255.0F * ap)), 7.0F
         );
      } else {
         if (this.xEqXSW()) {
            float selX1 = taX - this.e1qqr39 + this.waBg(this.text.substring(0, this.opkg5()));
            float selX2 = taX - this.e1qqr39 + this.waBg(this.text.substring(0, this.bZbfhw()));
            DrawUtil.drawRound(
               selX1, ay + 3.0F, selX2 - selX1, this.mgJw - 6.0F, 1.5F, ColorProvider.setAlpha(ColorProvider.getColorClient(), (int)(90.0F * ap))
            );
         }

         DrawUtil.drawText(
            Fonts.SFREGULAR.get(), this.text, taX - this.e1qqr39, textY, ColorProvider.setAlpha(ColorProvider.getColorText(), (int)(255.0F * ap)), 7.0F
         );
         if (this.xaPVDxR && System.currentTimeMillis() % 1000L > 500L) {
            float caretX = taX - this.e1qqr39 + this.waBg(this.text.substring(0, this.pV8k));
            DrawUtil.drawRound(caretX, ay + 3.0F, 0.8F, this.mgJw - 6.0F, 0.0F, ColorProvider.setAlpha(ColorProvider.getColorText(), (int)(230.0F * ap)));
         }
      }

      Scissor.unset();
      Scissor.pop();
   }

   private void qI7Vx2(float areaW) {
      float caretPos = this.waBg(this.text.substring(0, this.pV8k));
      if (caretPos - this.e1qqr39 > areaW - 2.0F) {
         this.e1qqr39 = caretPos - areaW + 2.0F;
      }

      if (caretPos - this.e1qqr39 < 0.0F) {
         this.e1qqr39 = caretPos;
      }

      float total = this.waBg(this.text);
      if (total - this.e1qqr39 < areaW - 2.0F) {
         this.e1qqr39 = Math.max(0.0F, total - areaW + 2.0F);
      }

      if (total <= areaW) {
         this.e1qqr39 = 0.0F;
      }
   }

   public void charTyped(char codePoint, int modifiers) {
      if (this.xaPVDxR) {
         if ((modifiers & 14) == 0) {
            if (codePoint >= ' ' && codePoint != 127) {
               this.jOY5(String.valueOf(codePoint));
            }
         }
      }
   }

   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.xaPVDxR) {
         boolean ctrl = (modifiers & 2) != 0 || (modifiers & 8) != 0;
         boolean shift = (modifiers & 1) != 0;
         if (ctrl) {
            switch (keyCode) {
               case 65:
                  this.lWvJ();
                  return;
               case 67:
                  this.mf8fCLy();
                  return;
               case 86:
                  this.xJckjyw();
                  return;
               case 88:
                  this.hm2D();
                  return;
            }
         }

         switch (keyCode) {
            case 256:
            case 257:
            case 335:
               this.xaPVDxR = false;
               break;
            case 259:
               if (this.xEqXSW()) {
                  this.oq3QY();
               } else if (ctrl) {
                  int start = this.q8leBef(this.pV8k);
                  this.text = this.text.substring(0, start) + this.text.substring(this.pV8k);
                  this.pV8k = start;
                  this.eqc8 = this.pV8k;
               } else if (this.pV8k > 0) {
                  this.text = this.text.substring(0, this.pV8k - 1) + this.text.substring(this.pV8k);
                  this.pV8k--;
                  this.eqc8 = this.pV8k;
               }
               break;
            case 261:
               if (this.xEqXSW()) {
                  this.oq3QY();
               } else if (this.pV8k < this.text.length()) {
                  this.text = this.text.substring(0, this.pV8k) + this.text.substring(this.pV8k + 1);
               }
               break;
            case 262: {
               int target = ctrl ? this.dxt01p(this.pV8k) : Math.min(this.text.length(), this.pV8k + 1);
               this.tWOeEf(target, shift);
               break;
            }
            case 263: {
               int target = ctrl ? this.q8leBef(this.pV8k) : Math.max(0, this.pV8k - 1);
               this.tWOeEf(target, shift);
               break;
            }
            case 268:
               this.tWOeEf(0, shift);
               break;
            case 269:
               this.tWOeEf(this.text.length(), shift);
         }
      }
   }

   private void jOY5(String s) {
      if (this.xEqXSW()) {
         this.oq3QY();
      }

      int free = 64 - this.text.length();
      if (free > 0) {
         if (s.length() > free) {
            s = s.substring(0, free);
         }

         this.text = this.text.substring(0, this.pV8k) + s + this.text.substring(this.pV8k);
         this.pV8k = this.pV8k + s.length();
         this.eqc8 = this.pV8k;
      }
   }

   private void oq3QY() {
      int a = this.opkg5();
      int b = this.bZbfhw();
      this.text = this.text.substring(0, a) + this.text.substring(b);
      this.pV8k = a;
      this.eqc8 = a;
   }

   private void tWOeEf(int target, boolean keepSelection) {
      this.pV8k = Math.max(0, Math.min(this.text.length(), target));
      if (!keepSelection) {
         this.eqc8 = this.pV8k;
      }
   }

   private void lWvJ() {
      this.eqc8 = 0;
      this.pV8k = this.text.length();
   }

   private void mf8fCLy() {
      if (this.xEqXSW()) {
         MinecraftClient.getInstance().keyboard.setClipboard(this.text.substring(this.opkg5(), this.bZbfhw()));
      }
   }

   private void hm2D() {
      if (this.xEqXSW()) {
         this.mf8fCLy();
         this.oq3QY();
      }
   }

   private void xJckjyw() {
      String clip = MinecraftClient.getInstance().keyboard.getClipboard();
      if (clip != null && !clip.isEmpty()) {
         clip = clip.replaceAll("[\\n\\r\\t]", " ");
         this.jOY5(clip);
      }
   }

   private int q8leBef(int from) {
      int i = from;

      while (i > 0 && this.text.charAt(i - 1) == ' ') {
         i--;
      }

      while (i > 0 && this.text.charAt(i - 1) != ' ') {
         i--;
      }

      return i;
   }

   private int dxt01p(int from) {
      int i = from;
      int len = this.text.length();

      while (i < len && this.text.charAt(i) == ' ') {
         i++;
      }

      while (i < len && this.text.charAt(i) != ' ') {
         i++;
      }

      return i;
   }

   public void mouseClicked(double mouseX, double mouseY, int button) {
      boolean inside = HoverUtil.isHovered(mouseX, mouseY, this.oJ6mQhr, this.qZHlnm, this.lz556v2, this.mgJw);
      this.xaPVDxR = inside;
      if (inside) {
         int index = this.dswol((float)mouseX);
         this.pV8k = index;
         this.eqc8 = index;
      }
   }

   private int dswol(float mouseX) {
      float local = mouseX - this.h611qc() + this.e1qqr39;
      int best = 0;
      float bestDist = Float.MAX_VALUE;

      for (int i = 0; i <= this.text.length(); i++) {
         float w = this.waBg(this.text.substring(0, i));
         float d = Math.abs(w - local);
         if (d < bestDist) {
            bestDist = d;
            best = i;
         }
      }

      return best;
   }

   public boolean isFocused() {
      return this.xaPVDxR;
   }

   public boolean isEmpty() {
      return this.text.isEmpty();
   }
}
