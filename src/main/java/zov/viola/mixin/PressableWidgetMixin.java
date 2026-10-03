package zov.viola.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@Mixin({PressableWidget.class})
public class PressableWidgetMixin {
   @Inject(
      method = {"renderWidget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vio_renderCustomButton(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      ClickableWidget self = (ClickableWidget)(Object)this;
      if (self.getMessage() != null && !self.getMessage().getString().isEmpty()) {
         ci.cancel();
         float x = self.getX();
         float y = self.getY();
         float width = self.getWidth();
         float height = self.getHeight();
         boolean hovered = self.isHovered();
         boolean active = self.active;
         boolean focused = self.isFocused();
         float a = ((ClickableWidgetAccessor)(Object)this).viola_getAlpha();
         int cardBg = ColorProvider.setAlpha(-16448249, (int)(255.0F * a));
         int cardBgHover = ColorProvider.setAlpha(-15856110, (int)(255.0F * a));
         int bg;
         if (!active) {
            bg = ColorProvider.setAlpha(ColorProvider.rgba(30, 30, 34, 255.0F), (int)(90.0F * a));
         } else if (hovered) {
            bg = cardBgHover;
         } else {
            bg = cardBg;
         }

         DrawUtil.drawRound(x, y, width, height, 3.0F, bg);
         String label = self.getMessage().getString();
         float textSize = Math.min(13.5F, height * 0.6F);
         float textW = Fonts.SFMEDIUM.get().getWidth(label, textSize);
         float maxW = width - 16.0F;
         if (textW > maxW && textW > 0.0F) {
            textSize = Math.max(8.0F, textSize * (maxW / textW));
            textW = Fonts.SFMEDIUM.get().getWidth(label, textSize);
         }

         int textColor;
         if (!active) {
            textColor = ColorProvider.setAlpha(16777215, (int)(130.0F * a));
         } else {
            textColor = hovered ? -1 : ColorProvider.setAlpha(-986379, (int)(255.0F * a));
         }

         DrawUtil.drawText(Fonts.SFMEDIUM.get(), label, x + width / 2.0F - textW / 2.0F, y + height / 2.0F - textSize / 2.0F + 1.0F, textColor, textSize);
      }
   }
}
