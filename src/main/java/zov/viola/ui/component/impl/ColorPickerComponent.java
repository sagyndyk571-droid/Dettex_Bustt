package zov.viola.ui.component.impl;

import java.awt.Color;
import net.minecraft.client.util.math.MatrixStack;
import zov.viola.module.settings.ColorSetting;
import zov.viola.ui.component.Component;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class ColorPickerComponent extends Component {
   private final ColorSetting c44El;
   private boolean ehGzh;
   private final Animation ph8z3y = new Animation(Easing.QUINTIC_OUT, 300L);
   private boolean fyq44Q0;
   private boolean bP82dJx;
   private float b35ez3;
   private float azbxd7;
   private float ymtf;

   public ColorPickerComponent(ColorSetting setting) {
      this.c44El = setting;
      this.yuRt();
   }

   private void yuRt() {
      Color c = new Color(this.c44El.getValue(), true);
      float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
      this.b35ez3 = hsv[0];
      this.azbxd7 = hsv[1];
      this.ymtf = hsv[2];
   }

   @Override
   public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
      float alpha = Math.min(this.getAlphaAnimSetting().getValue(), 1.0F) * Math.max(Math.min(this.getAlphaAnim().getValue(), 1.0F), 0.0F);
      int alphaInt = (int)(255.0F * alpha);
      this.ph8z3y.run(this.ehGzh);
      float expandedHeight = 55.0F;
      DrawUtil.drawText(Fonts.SFREGULAR.get(), this.c44El.getName(), this.x + 4.5F, this.y + 3.0F, ColorProvider.rgba(255, 255, 255, (float)alphaInt), 7.5F);
      float previewSize = 8.0F;
      float previewX = this.x + this.width - previewSize - 5.0F;
      float previewY = this.y + 2.5F;
      if (HoverUtil.isHovered(mouseX, mouseY, previewX, previewY, previewSize, previewSize)) {
         CursorManager.requestHand();
      }

      DrawUtil.drawRound(
         previewX - 0.5F, previewY - 0.5F, previewSize + 1.0F, previewSize + 1.0F, 1.5F, ColorProvider.rgba(48, 66, 122, (float)((int)(140.0F * alpha)))
      );
      DrawUtil.drawRound(previewX, previewY, previewSize, previewSize, 1.5F, this.c44El.getValue());
      if (this.ph8z3y.getValue() > 0.01F) {
         float pickerY = this.y + 13.0F;
         float animH = this.ph8z3y.getValue() * expandedHeight;
         float animAlpha = alpha * this.ph8z3y.getValue();
         int animAlphaInt = (int)(255.0F * animAlpha);
         Scissor.push();
         Scissor.setFromComponentCoordinates((double)this.x, (double)pickerY, (double)this.width, (double)animH);
         DrawUtil.drawRound(
            this.x + 4.5F, pickerY - 0.5F, this.width - 9.0F, expandedHeight + 1.0F, 2.5F, ColorProvider.rgba(48, 66, 122, (float)((int)(140.0F * animAlpha)))
         );
         DrawUtil.drawRound(this.x + 5.0F, pickerY, this.width - 10.0F, expandedHeight, 2.5F, ColorProvider.rgba(16, 22, 42, (float)animAlphaInt));
         float svX = this.x + 9.0F;
         float svY = pickerY + 4.0F;
         float svSize = 40.0F;
         int cHue = ColorProvider.setAlpha(Color.HSBtoRGB(this.b35ez3, 1.0F, 1.0F), animAlphaInt);
         int cWhite = ColorProvider.rgba(255, 255, 255, (float)animAlphaInt);
         int cClearWhite = ColorProvider.rgba(255, 255, 255, 0.0F);
         int cBlack = ColorProvider.rgba(0, 0, 0, (float)animAlphaInt);
         int cClearBlack = ColorProvider.rgba(0, 0, 0, 0.0F);
         DrawUtil.drawRound(svX, svY, svSize, svSize, 2.0F, cHue);
         DrawUtil.drawRound(svX, svY, svSize, svSize, 2.0F, cWhite, cWhite, cClearWhite, cClearWhite);
         DrawUtil.drawRound(svX, svY, svSize, svSize, 2.0F, cClearBlack, cBlack, cBlack, cClearBlack);
         float svCursorX = svX + this.azbxd7 * svSize;
         float svCursorY = svY + (1.0F - this.ymtf) * svSize;
         DrawUtil.drawRound(svCursorX - 2.5F, svCursorY - 2.5F, 5.0F, 5.0F, 2.5F, ColorProvider.rgba(0, 0, 0, (float)((int)(180.0F * animAlpha))));
         DrawUtil.drawRound(svCursorX - 1.5F, svCursorY - 1.5F, 3.0F, 3.0F, 1.5F, ColorProvider.rgba(255, 255, 255, (float)animAlphaInt));
         float hueX = svX + svSize + 6.0F;
         float hueY = svY;
         float hueW = 6.0F;

         for (float i = 0.0F; i <= svSize; i += 0.5F) {
            int color = ColorProvider.setAlpha(Color.HSBtoRGB(i / svSize, 1.0F, 1.0F), animAlphaInt);
            DrawUtil.drawRound(hueX, hueY + i, hueW, 1.0F, 0.0F, color);
         }

         float hueCursorY = hueY + this.b35ez3 * svSize;
         DrawUtil.drawRound(hueX - 1.5F, hueCursorY - 2.5F, hueW + 3.0F, 5.0F, 2.0F, ColorProvider.rgba(0, 0, 0, (float)((int)(180.0F * animAlpha))));
         DrawUtil.drawRound(hueX - 0.5F, hueCursorY - 1.5F, hueW + 1.0F, 3.0F, 1.0F, ColorProvider.rgba(255, 255, 255, (float)animAlphaInt));
         if (this.fyq44Q0) {
            this.azbxd7 = Math.max(0.0F, Math.min(1.0F, (mouseX - svX) / svSize));
            this.ymtf = 1.0F - Math.max(0.0F, Math.min(1.0F, (mouseY - svY) / svSize));
            this.ugwok();
         } else if (this.bP82dJx) {
            this.b35ez3 = Math.max(0.0F, Math.min(1.0F, (mouseY - hueY) / svSize));
            this.ugwok();
         }

         Scissor.unset();
         Scissor.pop();
      }

      this.setHeight(13.0F + this.ph8z3y.getValue() * expandedHeight);
   }

   private void ugwok() {
      int rgb = Color.HSBtoRGB(this.b35ez3, this.azbxd7, this.ymtf);
      this.c44El.setValue(rgb | 0xFF000000);
   }

   @Override
   public void mouseClicked(double mouseX, double mouseY, int button) {
      float previewSize = 8.0F;
      float previewX = this.x + this.width - previewSize - 5.0F;
      float previewY = this.y + 2.5F;
      if (HoverUtil.isHovered(mouseX, mouseY, previewX, previewY, previewSize, previewSize) && button == 1) {
         this.ehGzh = !this.ehGzh;
      } else {
         if (this.ehGzh && button == 0) {
            float svX = this.x + 9.0F;
            float svY = this.y + 13.0F + 4.0F;
            float svSize = 40.0F;
            float hueX = svX + svSize + 6.0F;
            if (HoverUtil.isHovered(mouseX, mouseY, svX, svY, svSize, svSize)) {
               this.fyq44Q0 = true;
            } else if (HoverUtil.isHovered(mouseX, mouseY, hueX - 2.0F, svY, 10.0, svSize)) {
               this.bP82dJx = true;
            }
         }
      }
   }

   @Override
   public void mouseReleased(double mouseX, double mouseY, int button) {
      this.fyq44Q0 = false;
      this.bP82dJx = false;
   }

   @Override
   public boolean isVisible() {
      return this.c44El.visible.get();
   }
}
