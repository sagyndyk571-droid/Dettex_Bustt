package zov.viola.ui.component.impl;

import net.minecraft.client.util.math.MatrixStack;
import zov.viola.module.settings.BindSetting;
import zov.viola.ui.component.Component;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.keyboard.KeyStorage;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class BindComponent extends Component {
   private final BindSetting fo72;
   private boolean iv2o;
   private static final float SETTING_HEIGHT = 14.0F;
   private static final float BIND_H = 10.0F;
   private static final float PADDING = 4.5F;
   private static final float RADIUS = 1.0F;

   public BindComponent(BindSetting setting) {
      this.fo72 = setting;
   }

   private String e2EzhR() {
      if (this.iv2o) {
         return "...";
      } else {
         return this.fo72.getValue() == -1 ? "None" : KeyStorage.getKey(this.fo72.getValue());
      }
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      float alpha = Math.max(Math.min(this.getAlphaAnimSetting().getValue(), 1.0F), 0.0F) * Math.max(Math.min(this.getAlphaAnim().getValue(), 1.0F), 0.0F);
      int alphaInt = (int)(255.0F * alpha);
      if (!(alpha < 0.02F)) {
         String keyText = this.e2EzhR();
         float keyTextW = Fonts.SFREGULAR.get().getWidth(keyText, 6.5F);
         float bindBoxW = Math.max(16.0F, keyTextW + 8.0F);
         float bindX = this.x + this.width - bindBoxW - 4.5F;
         float bindY = this.y + 2.0F;
         if (HoverUtil.isHovered(mouseX, mouseY, bindX, bindY, bindBoxW, 10.0)) {
            CursorManager.requestHand();
         }

         DrawUtil.drawText(
            Fonts.SFREGULAR.get(), this.fo72.getName(), this.x + 4.5F, this.y + 3.5F, ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt), 7.5F
         );
         boolean hasBind = this.fo72.getValue() != -1;
         int bgColor;
         if (this.iv2o) {
            bgColor = ColorProvider.setAlpha(ColorProvider.getColorButton(), (int)(35.0F * alpha));
         } else if (hasBind) {
            bgColor = ColorProvider.setAlpha(ColorProvider.getColorButton(), (int)(80.0F * alpha));
         } else {
            bgColor = ColorProvider.setAlpha(ColorProvider.getColorInactiveButton(), (int)(80.0F * alpha));
         }

         DrawUtil.drawRound(bindX, bindY, bindBoxW, 10.0F, 1.0F, bgColor);
         int textColor = !this.iv2o && !hasBind
            ? ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), alphaInt)
            : ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt);
         float keyX = bindX + (bindBoxW - keyTextW) / 2.0F;
         DrawUtil.drawText(Fonts.SFREGULAR.get(), keyText, keyX, bindY + 1.25F, textColor, 7.5F);
         this.setHeight(14.0F);
      }
   }

   @Override
   public void mouseClicked(double mouseX, double mouseY, int button) {
      String keyText = this.e2EzhR();
      float keyTextW = Fonts.SFREGULAR.get().getWidth(keyText, 7.5F);
      float bindBoxW = Math.max(16.0F, keyTextW + 8.0F);
      float bindX = this.x + this.width - bindBoxW - 4.5F;
      float bindY = this.y + 2.0F;
      if (this.iv2o) {
         if (button != 0) {
            this.fo72.setValue(button);
         }

         this.iv2o = false;
      } else if (HoverUtil.isHovered(mouseX, mouseY, bindX, bindY, bindBoxW, 10.0) && button == 0) {
         this.iv2o = true;
      }
   }

   @Override
   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.iv2o) {
         if (keyCode != 256 && keyCode != 261) {
            this.fo72.setValue(keyCode);
         } else {
            this.fo72.setValue(-1);
         }

         this.iv2o = false;
      }
   }

   @Override
   public boolean isVisible() {
      return this.fo72.visible.get();
   }
}
