package zov.viola.ui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import zov.viola.module.settings.ItemModelSetting;
import zov.viola.obf.D;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class ItemModelGalleryPopup implements IComponent {
   private static final int COLUMNS = 6;
   private static final float WIDTH = 380.0F;
   private static final float MAX_HEIGHT = 292.0F;
   private static final float PADDING = 10.0F;
   private static final float CELL_GAP = 4.0F;
   private static final float CELL_HEIGHT = 45.0F;
   private static final float ROW_PITCH = 49.0F;
   private final ItemModelSetting jlLvtvv;
   private final SearchField a86oltf = new SearchField(
      "Search models..."
   );
   private final List<String> iUhV = new ArrayList<>();
   private String i6e76 = "";
   private String joDy61;
   private float qOq8;
   private float mrlU0;
   private float tkHkpX4;
   private float vt64488;
   private float lN6s5YI;
   private float wNCC;
   private float s8Ekwc9;
   private float m0ih3ws;
   private float oJ721Fl;

   public ItemModelGalleryPopup(ItemModelSetting setting) {
      this.jlLvtvv = setting;
      this.iUhV.addAll(setting.getModes());
      this.a86oltf.resetAppear();
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
      int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
      this.tkHkpX4 = Math.min(380.0F, screenWidth - 20.0F);
      this.vt64488 = Math.min(292.0F, screenHeight - 20.0F);
      this.qOq8 = (screenWidth - this.tkHkpX4) / 2.0F;
      this.mrlU0 = (screenHeight - this.vt64488) / 2.0F;
      this.iFUH();
      DrawUtil.drawRound(0.0F, 0.0F, screenWidth, screenHeight, 0.0F, ColorProvider.rgba(0, 0, 0, 95.0F));
      DrawUtil.drawRoundBlur(this.qOq8, this.mrlU0, this.tkHkpX4, this.vt64488, 7.0F, ColorProvider.rgba(200, 200, 200, 255.0F), 16.0F);
      DrawUtil.drawRound(this.qOq8 - 1.0F, this.mrlU0 - 1.0F, this.tkHkpX4 + 2.0F, this.vt64488 + 2.0F, 7.5F, ColorProvider.rgba(48, 66, 122, 145.0F));
      DrawUtil.drawRound(this.qOq8, this.mrlU0, this.tkHkpX4, this.vt64488, 7.0F, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), 235));
      DrawUtil.drawText(
         Fonts.SFMEDIUM.get(),
         "Item Replacer models",
         this.qOq8 + 10.0F,
         this.mrlU0 + 9.0F,
         ColorProvider.getColorText(),
         9.0F
      );
      String count = this.iUhV.size() + " / " + this.jlLvtvv.getModes().size();
      float countWidth = Fonts.SFREGULAR.get().getWidth(count, 7.0F);
      DrawUtil.drawText(
         Fonts.SFREGULAR.get(), count, this.qOq8 + this.tkHkpX4 - 10.0F - countWidth, this.mrlU0 + 10.0F, ColorProvider.getColorInactiveText(), 7.0F
      );
      this.a86oltf.setBounds(this.qOq8 + 10.0F, this.mrlU0 + 25.0F, this.tkHkpX4 - 20.0F, 18.0F);
      this.a86oltf.render(context, mouseX, mouseY, delta);
      this.lN6s5YI = this.qOq8 + 10.0F;
      this.wNCC = this.mrlU0 + 50.0F;
      this.s8Ekwc9 = this.tkHkpX4 - 20.0F;
      this.m0ih3ws = Math.max(1.0F, this.vt64488 - 75.0F);
      this.joDy61 = null;
      Scissor.push();
      Scissor.setFromComponentCoordinates((double)this.lN6s5YI, (double)this.wNCC, (double)this.s8Ekwc9, (double)this.m0ih3ws);
      this.kfYnsA(context, mouseX, mouseY);
      Scissor.unset();
      Scissor.pop();
      this.zHEgi5();
      this.w2sz();
   }

   private void iFUH() {
      String query = this.a86oltf.text.trim().toLowerCase(Locale.ROOT);
      if (!query.equals(this.i6e76)) {
         this.i6e76 = query;
         this.iUhV.clear();

         for (String model : this.jlLvtvv.getModes()) {
            if (query.isEmpty() || model.toLowerCase(Locale.ROOT).contains(query)) {
               this.iUhV.add(model);
            }
         }

         this.oJ721Fl = 0.0F;
      }
   }

   private void kfYnsA(DrawContext context, int mouseX, int mouseY) {
      if (this.iUhV.isEmpty()) {
         String empty = "No matching models";
         float textWidth = Fonts.SFREGULAR.get().getWidth(empty, 8.0F);
         DrawUtil.drawText(
            Fonts.SFREGULAR.get(),
            empty,
            this.lN6s5YI + (this.s8Ekwc9 - textWidth) / 2.0F,
            this.wNCC + this.m0ih3ws / 2.0F - 4.0F,
            ColorProvider.getColorInactiveText(),
            8.0F
         );
      } else {
         float maxScroll = this.yli0();
         this.oJ721Fl = MathHelper.clamp(this.oJ721Fl, 0.0F, maxScroll);
         float cellWidth = (this.s8Ekwc9 - 20.0F) / 6.0F;
         int rowCount = this.ihP2y();
         int firstRow = Math.max(0, (int)Math.floor(this.oJ721Fl / 49.0F));
         int lastRow = Math.min(rowCount - 1, (int)Math.floor((this.oJ721Fl + this.m0ih3ws - 0.001F) / 49.0F));

         for (int row = firstRow; row <= lastRow; row++) {
            float cellY = this.wNCC + row * 49.0F - this.oJ721Fl;

            for (int column = 0; column < 6; column++) {
               int index = row * 6 + column;
               if (index >= this.iUhV.size()) {
                  break;
               }

               String model = this.iUhV.get(index);
               float cellX = this.lN6s5YI + column * (cellWidth + 4.0F);
               this.teL8P(context, model, cellX, cellY, cellWidth, mouseX, mouseY);
            }
         }
      }
   }

   private void teL8P(DrawContext context, String model, float cellX, float cellY, float cellWidth, int mouseX, int mouseY) {
      boolean hovered = HoverUtil.isHovered(mouseX, mouseY, cellX, cellY, cellWidth, 45.0)
         && HoverUtil.isHovered(mouseX, mouseY, this.lN6s5YI, this.wNCC, this.s8Ekwc9, this.m0ih3ws);
      boolean selected = this.jlLvtvv.is(model);
      if (hovered) {
         this.joDy61 = model;
         CursorManager.requestHand();
      }

      int outline = selected
         ? ColorProvider.setAlpha(ColorProvider.getColorClient(), 255)
         : (hovered ? ColorProvider.rgba(100, 130, 220, 180.0F) : ColorProvider.rgba(48, 66, 122, 90.0F));
      float outlineSize = selected ? 1.5F : 0.75F;
      DrawUtil.drawRound(cellX - outlineSize, cellY - outlineSize, cellWidth + outlineSize * 2.0F, 45.0F + outlineSize * 2.0F, 4.0F, outline);
      DrawUtil.drawRound(
         cellX, cellY, cellWidth, 45.0F, 3.0F, ColorProvider.setAlpha(hovered ? ColorProvider.getColorButton() : ColorProvider.getColorInactiveButton(), 205)
      );
      ItemStack preview = this.jlLvtvv.getPreviewStack(model);
      if (preview != null) {
         float iconSize = 31.0F;
         float scale = iconSize / 16.0F;
         context.getMatrices().push();
         context.getMatrices().translate(cellX + (cellWidth - iconSize) / 2.0F, cellY + (45.0F - iconSize) / 2.0F, 100.0F);
         context.getMatrices().scale(scale, scale, 1.0F);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         context.drawItem(preview, 0, 0);
         context.getMatrices().pop();
      }
   }

   private void zHEgi5() {
      float maxScroll = this.yli0();
      if (!(maxScroll <= 0.0F)) {
         float contentHeight = this.ihP2y() * 49.0F - 4.0F;
         float trackX = this.qOq8 + this.tkHkpX4 - 4.0F;
         float thumbHeight = Math.max(18.0F, this.m0ih3ws * (this.m0ih3ws / contentHeight));
         float thumbY = this.wNCC + (this.m0ih3ws - thumbHeight) * (this.oJ721Fl / maxScroll);
         DrawUtil.drawRound(trackX, this.wNCC, 1.5F, this.m0ih3ws, 0.75F, ColorProvider.rgba(48, 66, 122, 100.0F));
         DrawUtil.drawRound(trackX, thumbY, 1.5F, thumbHeight, 0.75F, ColorProvider.setAlpha(ColorProvider.getColorClient(), 210));
      }
   }

   private void w2sz() {
      String label = this.joDy61 != null
         ? this.joDy61
         : D.k(
            new int[]{148, 95, 19, 87, 190, 94, 80, 20, 189, 69, 19, 28, 241, 67, 5, 3, 162, 69, 20, 18, 241, 88, 31, 87, 178, 64, 31, 4, 180},
            new int[]{209, 44, 112, 119}
         );
      float textWidth = Fonts.SFREGULAR.get().getWidth(label, 7.0F);
      DrawUtil.drawText(
         Fonts.SFREGULAR.get(),
         label,
         this.qOq8 + (this.tkHkpX4 - textWidth) / 2.0F,
         this.mrlU0 + this.vt64488 - 14.0F,
         this.joDy61 != null ? ColorProvider.getColorText() : ColorProvider.getColorInactiveText(),
         7.0F
      );
   }

   @Override
   public void mouseClicked(double mouseX, double mouseY, int button) {
      this.a86oltf.mouseClicked(mouseX, mouseY, button);
      if (button == 0 && HoverUtil.isHovered(mouseX, mouseY, this.lN6s5YI, this.wNCC, this.s8Ekwc9, this.m0ih3ws)) {
         float cellWidth = (this.s8Ekwc9 - 20.0F) / 6.0F;
         int row = (int)Math.floor((mouseY - this.wNCC + this.oJ721Fl) / 49.0);
         int column = (int)Math.floor((mouseX - this.lN6s5YI) / (cellWidth + 4.0F));
         if (column >= 0 && column < 6) {
            float localX = (float)(mouseX - this.lN6s5YI - column * (cellWidth + 4.0F));
            float localY = (float)(mouseY - this.wNCC + this.oJ721Fl - row * 49.0F);
            if (!(localX > cellWidth) && !(localY > 45.0F)) {
               int index = row * 6 + column;
               if (index >= 0 && index < this.iUhV.size()) {
                  this.jlLvtvv.setValue(this.iUhV.get(index));
               }
            }
         }
      }
   }

   @Override
   public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (HoverUtil.isHovered(mouseX, mouseY, this.lN6s5YI, this.wNCC, this.s8Ekwc9, this.m0ih3ws)) {
         this.oJ721Fl = MathHelper.clamp(this.oJ721Fl - (float)verticalAmount * 28.0F, 0.0F, this.yli0());
      }
   }

   @Override
   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      this.a86oltf.keyPressed(keyCode, scanCode, modifiers);
   }

   @Override
   public void charTyped(char chr, int modifiers) {
      this.a86oltf.charTyped(chr, modifiers);
   }

   public boolean contains(double mouseX, double mouseY) {
      return HoverUtil.isHovered(mouseX, mouseY, this.qOq8, this.mrlU0, this.tkHkpX4, this.vt64488);
   }

   private int ihP2y() {
      return (this.iUhV.size() + 6 - 1) / 6;
   }

   private float yli0() {
      float contentHeight = Math.max(0.0F, this.ihP2y() * 49.0F - 4.0F);
      return Math.max(0.0F, contentHeight - this.m0ih3ws);
   }
}
