package zov.viola.ui.component.impl;

import net.minecraft.client.util.math.MatrixStack;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.ui.component.Component;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class BooleanComponent extends Component {
   private final BooleanSetting we9md0y;
   private boolean ovri3kp;

   public BooleanComponent(BooleanSetting setting) {
      this.we9md0y = setting;
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      float alpha = Math.max(Math.min(this.getAlphaAnimSetting().getValue(), 1.0F), 0.0F) * Math.max(Math.min(this.getAlphaAnim().getValue(), 1.0F), 0.0F);
      int alphaInt = (int)(255.0F * alpha);
      float toggleW = 15.0F;
      float toggleH = 8.0F;
      float toggleX = this.x + this.width - toggleW - 2.5F;
      float toggleY = this.y + 4.0F;
      if (HoverUtil.isHovered(mouseX, mouseY, toggleX, toggleY, toggleW, toggleH)) {
         CursorManager.requestHand();
      }

      DrawUtil.drawText(
         Fonts.SFREGULAR.get(),
         this.ovri3kp ? "Binding..." : this.we9md0y.getName(),
         this.x + 4.5F,
         this.y + 5.0F,
         ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt),
         7.5F,
         0.4F,
         1.0F,
         this.width - toggleW - 8.0F
      );
      float anim = this.we9md0y.getAnimation().getValue();
      if (anim > 0.01F) {
         int indicator = ColorProvider.setAlpha(ColorProvider.getColorIndicator(), alphaInt);
         int inactiveIndicator = ColorProvider.setAlpha(ColorProvider.getColorInactiveIndicator(), alphaInt);
         int bgColor = ColorProvider.interpolateColor(inactiveIndicator, indicator, anim);
         DrawUtil.drawRound(toggleX, toggleY, toggleW, toggleH, toggleH / 2.0F, bgColor);
      } else {
         DrawUtil.drawRound(toggleX, toggleY, toggleW, toggleH, toggleH / 2.0F, ColorProvider.setAlpha(ColorProvider.getColorInactiveIndicator(), alphaInt));
      }

      float knobSize = toggleH - 1.0F;
      float knobMinX = toggleX + 0.5F;
      float knobMaxX = toggleX + toggleW - knobSize - 0.5F;
      float knobX = knobMinX + (knobMaxX - knobMinX) * anim;
      float knobY = toggleY + 0.5F;
      DrawUtil.drawCircle(
         knobX + knobSize / 2.0F, knobY + knobSize / 2.0F, knobSize / 2.0F, ColorProvider.setAlpha(ColorProvider.getColorSliderCircle(), alphaInt)
      );
      this.setHeight(16.0F);
   }

   @Override
   public void mouseClicked(double mouseX, double mouseY, int button) {
      float toggleW = 15.0F;
      float toggleH = 8.0F;
      float toggleX = this.x + this.width - toggleW - 2.5F;
      float toggleY = this.y + 4.0F;
      if (HoverUtil.isHovered(mouseX, mouseY, toggleX, toggleY, toggleW, toggleH)) {
         if (button == 0) {
            this.we9md0y.setValue(!this.we9md0y.getValue());
         }

         if (button == 2 && this.ovri3kp) {
            this.ovri3kp = false;
            return;
         }

         if (this.ovri3kp) {
            this.we9md0y.setKey(button);
            this.ovri3kp = false;
         }

         if (button == 2) {
            this.ovri3kp = true;
         }
      }
   }

   @Override
   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.ovri3kp) {
         if (keyCode == 256) {
            this.ovri3kp = false;
            return;
         }

         if (keyCode == 261) {
            this.we9md0y.setKey(-1);
            return;
         }

         this.we9md0y.setKey(keyCode);
         this.ovri3kp = false;
      }
   }

   @Override
   public boolean isVisible() {
      return this.we9md0y.visible.get();
   }
}
