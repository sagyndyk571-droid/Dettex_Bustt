package zov.viola.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.module.settings.impl.ThemeManager;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@Mixin({SliderWidget.class})
public class SliderWidgetMixin {
   @Inject(
      method = {"renderWidget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vio_renderCustomSlider(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      ci.cancel();
      ClickableWidget self = (ClickableWidget)(Object)this;
      float x = self.getX();
      float y = self.getY();
      float width = self.getWidth();
      float height = self.getHeight();
      boolean hovered = self.isHovered();
      float a = ((ClickableWidgetAccessor)(Object)this).viola_getAlpha();
      double value = MathHelper.clamp(((SliderWidgetAccessor)(Object)this).viola_getValue(), 0.0, 1.0);
      int accent = ThemeManager.getInstance().getCurrentTheme().getColorFirst();
      int cardBg = ColorProvider.setAlpha(-16448249, (int)(255.0F * a));
      int cardHover = ColorProvider.setAlpha(-15856110, (int)(255.0F * a));
      DrawUtil.drawRound(x, y, width, height, 3.0F, hovered ? cardHover : cardBg);
      float fillW = width * (float)value;
      if (fillW > 0.5F) {
         DrawUtil.drawRound(x, y, fillW, height, 3.0F, ColorProvider.setAlpha(accent, (int)(70.0F * a)));
      }

      String label = self.getMessage() == null ? "" : self.getMessage().getString();
      float textSize = Math.min(12.0F, height * 0.6F);
      float textW = Fonts.SFMEDIUM.get().getWidth(label, textSize);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), label, x + width / 2.0F - textW / 2.0F, y + height / 2.0F - textSize / 2.0F + 1.0F, -1, textSize);
   }
}
