package zov.viola.ui.clickgui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Matrix4fStack;
import zov.viola.module.list.render.ClickGui;
import zov.viola.obf.D;
import zov.viola.ui.CsPalette;
import zov.viola.util.IMinecraft;
import zov.viola.util.base.Instance;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class DropdownClickGuiScreen extends Screen implements IMinecraft {
   private final DropdownGuiState udc7jic = new DropdownGuiState();
   private final DropdownGuiRenderer bL4wfc = new DropdownGuiRenderer(this.udc7jic);
   private final DropdownGuiInputHandler g5124 = new DropdownGuiInputHandler(this.udc7jic);
   private final Animation kJdLLzx = new Animation(Easing.CUBIC_OUT, 200L);
   private boolean w4d60n2 = false;

   public DropdownClickGuiScreen() {
      super(Text.of("Click Gui"));
      this.playOpenAnimation();
   }

   public void playOpenAnimation() {
      this.w4d60n2 = false;
      this.kJdLLzx.setValue(0.0F);
   }

   public boolean isClosing() {
      return this.w4d60n2;
   }

    private float ds7ib() {
       ClickGui module = Instance.get(ClickGui.class);
       float raw;
       if (module == null) {
          raw = 1.0F;
       } else {
          float windowWidth = mc.getWindow().getScaledWidth();
          float windowHeight = mc.getWindow().getScaledHeight();
          float baseScale = Math.min(windowWidth / 740.0F, windowHeight / 395.0F);
          raw = Math.max(0.9F, baseScale * (float)module.size.getValue());
       }
       return Math.round(raw * 20.0F) / 20.0F;
    }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      CsPalette.refreshAccent();
      float progress = this.kJdLLzx.run(this.w4d60n2 ? 0.0F : 1.0F);
      if (this.w4d60n2 && progress < 0.02F) {
         this.close();
      } else {
         int windowWidth = mc.getWindow().getScaledWidth();
         int windowHeight = mc.getWindow().getScaledHeight();
         float guiScale = this.ds7ib();
         float centerX = windowWidth / 2.0F;
         float centerY = windowHeight / 2.0F;
         int mouseGx = (int)((mouseX - centerX) / guiScale + centerX);
         int mouseGy = (int)((mouseY - centerY) / guiScale + centerY);
          DrawUtil.drawRoundBlur(0.0F, 0.0F, windowWidth, windowHeight, 0.0F, ColorProvider.setAlpha(-15331297, (int)(245.0F * progress)), 10.0F);
          DrawUtil.drawRound(0.0F, 0.0F, windowWidth, windowHeight, 0.0F, ColorProvider.setAlpha(-16447990, (int)(90.0F * progress)));
         Scissor.setGuiTransform(guiScale, centerX, centerY);
         Matrix4fStack modelView = RenderSystem.getModelViewStack();
         modelView.pushMatrix();
         modelView.translate(centerX, centerY, 0.0F);
         modelView.scale(guiScale, guiScale, 1.0F);
         modelView.translate(-centerX, -centerY, 0.0F);
          float posX = Math.round((windowWidth / guiScale - DropdownGuiLayout.getTotalCategoriesWidth()) / 2.0F);
          float posY = Math.max(12.0F, Math.round((windowHeight / guiScale - 396.0F) * 0.49F));
         this.udc7jic.setPos(posX, posY);
         this.udc7jic.setRenderOffsetY((1.0F - progress) * 15.0F);
         this.bL4wfc.render(mouseGx, mouseGy, progress);
         modelView.popMatrix();
         Scissor.resetGuiTransform();
      }
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.w4d60n2) {
         return true;
      } else {
         return this.g5124.mouseClicked(this.kowzpYh(mouseX), this.aj9b8a4(mouseY), button) ? true : super.mouseClicked(mouseX, mouseY, button);
      }
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.w4d60n2) {
         return true;
      } else {
         return this.g5124.mouseReleased(this.kowzpYh(mouseX), this.aj9b8a4(mouseY), button) ? true : super.mouseReleased(mouseX, mouseY, button);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.w4d60n2) {
         return true;
      } else {
         return verticalAmount != 0.0 && this.g5124.mouseScrolled(this.kowzpYh(mouseX), this.aj9b8a4(mouseY), verticalAmount)
            ? true
            : super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.w4d60n2) {
         return true;
      } else if (this.g5124.keyPressed(keyCode, modifiers)) {
         return true;
      } else if (keyCode == 256) {
         this.w4d60n2 = true;
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      if (this.w4d60n2) {
         return true;
      } else {
         return this.g5124.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
      }
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   private double kowzpYh(double mouseX) {
      float guiScale = this.ds7ib();
      double cx = mc.getWindow().getScaledWidth() / 2.0;
      return (mouseX - cx) / guiScale + cx;
   }

   private double aj9b8a4(double mouseY) {
      float guiScale = this.ds7ib();
      double cy = mc.getWindow().getScaledHeight() / 2.0;
      return (mouseY - cy) / guiScale + cy;
   }
}
