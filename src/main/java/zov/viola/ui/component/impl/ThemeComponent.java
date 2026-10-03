package zov.viola.ui.component.impl;

import net.minecraft.client.util.math.MatrixStack;
import zov.viola.module.settings.ThemeSetting;
import zov.viola.module.settings.impl.Theme;
import zov.viola.ui.component.Component;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class ThemeComponent extends Component {
   private final ThemeSetting yiv2h6Q;

   public ThemeComponent(ThemeSetting option) {
      this.yiv2h6Q = option;
   }

   @Override
   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      float startX = this.x + 2.0F;
      float currentX = startX;
      float circleY = this.y + 8.0F;

      for (Theme theme : this.yiv2h6Q.getThemes()) {
         theme.checkAnimation.run(this.yiv2h6Q.getValue() == theme);
         if (theme.checkAnimation.getValue() > 0.01F) {
            float alphaAnim = theme.checkAnimation.getValue();
            DrawUtil.drawRound(currentX - 1.5F, circleY - 1.5F, 11.0F, 11.0F, 5.5F, ColorProvider.rgba(255, 255, 255, (float)((int)(255.0F * alphaAnim))));
         }

         theme.x = currentX;
         theme.y = circleY;
         theme.drawTheme(this.getAlphaAnimSetting().getValue() * Math.max(Math.min(this.getAlphaAnim().getValue(), 1.0F), 0.0F));
         currentX += 14.0F;
      }

      this.setHeight(20.0F);
   }

   @Override
   public void mouseClicked(double mouseX, double mouseY, int button) {
      float currentX = this.x + 2.0F;
      float circleY = this.y + 8.0F;

      for (Theme theme : this.yiv2h6Q.getThemes()) {
         if (HoverUtil.isHovered(mouseX, mouseY, currentX - 1.0F, circleY - 1.0F, 10.0, 10.0) && this.yiv2h6Q.getValue() != theme && button == 0) {
            this.yiv2h6Q.setValue(theme);
            theme.animation.setValue(0.0F);
         }

         currentX += 14.0F;
      }
   }

   @Override
   public void mouseReleased(double mouseX, double mouseY, int button) {
   }

   @Override
   public void mouseScrolled(double mouseX, double mouseY, double delta) {
   }

   @Override
   public void keyPressed(int keyCode, int scanCode, int modifiers) {
   }

   @Override
   public boolean isVisible() {
      return true;
   }
}
