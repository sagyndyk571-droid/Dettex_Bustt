package zov.viola.ui.component.impl;

import java.util.function.Consumer;
import net.minecraft.client.util.math.MatrixStack;
import zov.viola.module.settings.ItemModelSetting;
import zov.viola.ui.component.Component;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class ItemModelComponent extends Component {
   private static final float ROW_HEIGHT = 18.0F;
   private static final float PADDING = 4.5F;
   private final ItemModelSetting pPoqZV;
   private final Consumer<ItemModelSetting> tqnOX;

   public ItemModelComponent(ItemModelSetting setting, Consumer<ItemModelSetting> galleryOpener) {
      this.pPoqZV = setting;
      this.tqnOX = galleryOpener;
      this.setHeight(20.0F);
   }

   @Override
   public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      float alpha = Math.max(0.0F, Math.min(1.0F, this.getAlphaAnimSetting().getValue() * this.getAlphaAnim().getValue()));
      this.setHeight(20.0F * this.getAlphaAnimSetting().getValue());
      if (!(alpha < 0.02F)) {
         float rowX = this.x + 4.5F;
         float rowY = this.y + 1.0F;
         float rowW = this.width - 9.0F;
         boolean hovered = HoverUtil.isHovered(mouseX, mouseY, rowX, rowY, rowW, 18.0);
         if (hovered) {
            CursorManager.requestHand();
         }

         int alphaInt = (int)(255.0F * alpha);
         int border = hovered
            ? ColorProvider.setAlpha(ColorProvider.getColorClient(), (int)(150.0F * alpha))
            : ColorProvider.rgba(48, 66, 122, (float)((int)(90.0F * alpha)));
         DrawUtil.drawRound(rowX - 0.5F, rowY - 0.5F, rowW + 1.0F, 19.0F, 2.5F, border);
         DrawUtil.drawRound(rowX, rowY, rowW, 18.0F, 2.0F, ColorProvider.setAlpha(ColorProvider.getColorInactiveButton(), alphaInt));
         DrawUtil.drawText(Fonts.SFREGULAR.get(), "Model", rowX + 4.0F, rowY + 5.1F, ColorProvider.setAlpha(ColorProvider.getColorText(), alphaInt), 7.5F);
         String value = this.ie1ou(this.pPoqZV.getValue(), rowW - 42.0F);
         float valueWidth = Fonts.SFREGULAR.get().getWidth(value, 6.5F);
         DrawUtil.drawText(
            Fonts.SFREGULAR.get(),
            value,
            rowX + rowW - valueWidth - 4.0F,
            rowY + 5.5F,
            ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), alphaInt),
            6.5F
         );
      }
   }

   private String ie1ou(String value, float maxWidth) {
      if (Fonts.SFREGULAR.get().getWidth(value, 6.5F) <= maxWidth) {
         return value;
      } else {
         String shortened = value;

         while (shortened.length() > 1 && Fonts.SFREGULAR.get().getWidth(shortened + "...", 6.5F) > maxWidth) {
            shortened = shortened.substring(0, shortened.length() - 1);
         }

         return shortened + "...";
      }
   }

   @Override
   public void mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, this.x + 4.5F, this.y + 1.0F, this.width - 9.0F, 18.0)) {
         this.tqnOX.accept(this.pPoqZV);
      }
   }

   @Override
   public boolean isVisible() {
      return this.pPoqZV.visible.get();
   }
}
