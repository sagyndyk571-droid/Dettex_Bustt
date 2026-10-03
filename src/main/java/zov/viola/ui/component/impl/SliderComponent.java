package zov.viola.ui.component.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import zov.viola.module.settings.SliderSetting;
import zov.viola.ui.component.Component;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class SliderComponent extends Component {
   private final SliderSetting qNer;
   private boolean yexPwz;
   private final Animation vSltZ = new Animation(Easing.QUINTIC_OUT, 100L);

   public SliderComponent(SliderSetting setting) {
      this.qNer = setting;
   }

   private double px3dn(double num, double increment) {
      double v = Math.round(num / increment) * increment;
      return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
   }

   private String u4hj06(double value) {
      return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      float alpha = Math.min(this.getAlphaAnimSetting().getValue(), 1.0F) * Math.max(Math.min(this.getAlphaAnim().getValue(), 1.0F), 0.0F);
      int alphaInt = (int)(255.0F * alpha);
      String numberText = this.u4hj06(this.qNer.getValue());
      float trackWidth = this.width - 9.0F;
      this.vSltZ.run((float)(trackWidth * (this.qNer.getValue() - this.qNer.getMin()) / (this.qNer.getMax() - this.qNer.getMin())));
      DrawUtil.drawText(
         Fonts.SFREGULAR.get(),
         this.qNer.getName(),
         this.x + 4.5F,
         this.y + 3.0F,
         ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt),
         7.5F,
         0.6F,
         1.0F,
         trackWidth
      );
      DrawUtil.drawText(
         Fonts.SFREGULAR.get(),
         numberText,
         this.x + this.width - 4.5F - Fonts.SFREGULAR.get().getWidth(numberText, 7.5F),
         this.y + 1.0F,
         ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), alphaInt),
         7.5F
      );
      float trackY = this.y + 14.0F;
      DrawUtil.drawRound(
         this.x + 3.0F, trackY - 3.5F, trackWidth + 1.0F, 4.0F, 1.0F, ColorProvider.setAlpha(ColorProvider.getColorSliderWindow(), (int)(100.0F * alpha))
      );
      DrawUtil.drawRound(this.x + 3.5F, trackY - 3.0F, trackWidth, 3.0F, 1.0F, ColorProvider.setAlpha(ColorProvider.getColorSliderWindow(), alphaInt));
      float fillWidth = MathHelper.clamp(this.vSltZ.getValue(), 0.0F, trackWidth);
      int sliderColor = ColorProvider.setAlpha(ColorProvider.getColorSlider(), alphaInt);
      DrawUtil.drawRound(this.x + 3.5F, trackY - 3.5F, fillWidth, 4.0F, 1.0F, sliderColor);
      float circleSize = this.yexPwz ? 7.0F : 5.5F;
      float circleX = this.x + 3.5F + fillWidth;
      float circleY = trackY - 1.5F;
      DrawUtil.drawRound(
         circleX - circleSize / 2.0F,
         circleY - circleSize / 2.0F,
         circleSize,
         circleSize,
         circleSize / 2.0F,
         ColorProvider.setAlpha(ColorProvider.getColorSliderCircle(), alphaInt)
      );
      if (this.yexPwz) {
         double val = (mouseX - (this.x + 3.5F)) / trackWidth * (this.qNer.getMax() - this.qNer.getMin()) + this.qNer.getMin();
         this.qNer.setValue((float)MathHelper.clamp(this.px3dn(val, this.qNer.getStep()), this.qNer.getMin(), this.qNer.getMax()));
      }

      this.setHeight(15.0F);
   }

   @Override
   public void mouseClicked(double mouseX, double mouseY, int button) {
      if (HoverUtil.isHovered(mouseX, mouseY, this.x + 3.0F, this.y + 8.0F, this.width - 6.0F, 8.0) && button == 0) {
         this.yexPwz = true;
      }
   }

   @Override
   public void mouseReleased(double mouseX, double mouseY, int button) {
      this.yexPwz = false;
   }

   @Override
   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.yexPwz = false;
      }
   }

   @Override
   public boolean isVisible() {
      return this.qNer.visible.get();
   }
}
