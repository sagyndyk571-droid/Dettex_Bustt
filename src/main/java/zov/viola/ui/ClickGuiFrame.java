package zov.viola.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4fStack;
import org.lwjgl.glfw.GLFW;
import zov.viola.module.ModuleCategory;
import zov.viola.module.list.render.ClickGui;
import zov.viola.module.settings.ItemModelSetting;
import zov.viola.obf.D;
import zov.viola.ui.component.ItemModelGalleryPopup;
import zov.viola.ui.component.SearchField;
import zov.viola.util.IMinecraft;
import zov.viola.util.base.Instance;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class ClickGuiFrame extends Screen implements IMinecraft {
   private final List<Panel> vdhdx = new ArrayList<>();
   private final SearchField k58E8;
   private ItemModelGalleryPopup p3mb;
   private final Animation sgp8 = new Animation(Easing.QUINTIC_OUT, 220L);
   private String m3aHCvU = null;
   private boolean fg2H = false;
   private long cTVPXi5;
   private long k99M9Jd;
   private long u7cr;
   private long ymtQ77s;
   private boolean cK9xy = false;
   private long qL3N = 0L;
   private String yvle = null;
   private String jkQX = "";

   private void ghr2V() {
      if (!this.cK9xy) {
         this.cTVPXi5 = GLFW.glfwCreateStandardCursor(221188);
         this.k99M9Jd = GLFW.glfwCreateStandardCursor(221186);
         this.u7cr = GLFW.glfwCreateStandardCursor(221188);
         this.ymtQ77s = GLFW.glfwCreateStandardCursor(221185);
         this.cK9xy = true;
      }
   }

   private void jssN(long cursor) {
      if (cursor != this.qL3N) {
         GLFW.glfwSetCursor(mc.getWindow().getHandle(), cursor);
         this.qL3N = cursor;
      }
   }

   public void playOpenAnimation() {
      this.fg2H = false;
      this.p3mb = null;

      for (Panel panel : this.vdhdx) {
         panel.slideAnim.reset(0.0F);
      }

      this.k58E8.resetAppear();
   }

   public ClickGuiFrame() {
      super(Text.of("Avalora Frame"));
      this.k58E8 = new SearchField("Search...");

      for (ModuleCategory category : ModuleCategory.values()) {
         this.vdhdx.add(new Panel(category, this));
      }
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      CursorManager.reset();
      CursorManager.resetIBeam();
      CursorManager.resetClick();
      int windowWidth = mc.getWindow().getScaledWidth();
      int windowHeight = mc.getWindow().getScaledHeight();
      float guiScale = this.yy8l();
      float centerX = windowWidth / 2.0F;
      float centerY = windowHeight / 2.0F;
      int mouseGx = (int)((mouseX - centerX) / guiScale + centerX);
      int mouseGy = (int)((mouseY - centerY) / guiScale + centerY);
      mouseX = mouseGx;
      mouseY = mouseGy;
      Scissor.setGuiTransform(guiScale, centerX, centerY);
      Matrix4fStack modelView = RenderSystem.getModelViewStack();
      modelView.pushMatrix();
      modelView.translate(centerX, centerY, 0.0F);
      modelView.scale(guiScale, guiScale, 1.0F);
      modelView.translate(-centerX, -centerY, 0.0F);
      float panelWidth = 120.0F;
      float spacing = 4.0F;
      float panelHeight = 270.0F;
      float panelTotalWidth = this.vdhdx.size() * (panelWidth + spacing) - spacing;
      float startX = (windowWidth - panelTotalWidth) / 2.0F;
      float panelY = (windowHeight - panelHeight) / 2.0F;
      float offscreen = windowHeight / 2.0F + panelHeight;

      for (int i = 0; i < this.vdhdx.size(); i++) {
         Panel panel = this.vdhdx.get(i);
         panel.slideDir = i % 2 == 0 ? 1 : -1;
         panel.slideAnim.run(this.fg2H ? 0.0F : 1.0F);
         float slide = MathHelper.clamp(panel.slideAnim.getValue(), 0.0F, 1.0F);
         float yOffset = (1.0F - slide) * panel.slideDir * offscreen;
         panel.setX(startX + i * (panelWidth + spacing));
         panel.setY(panelY + yOffset);
         panel.setWidth(panelWidth);
         panel.setHeight(panelHeight);
         panel.render(context, mouseX, mouseY, delta);
      }

      if (this.fg2H) {
         boolean allClosed = true;

         for (Panel panel : this.vdhdx) {
            if (panel.slideAnim.getValue() > 0.02F) {
               allClosed = false;
               break;
            }
         }

         if (allClosed) {
            this.fg2H = false;
            modelView.popMatrix();
            Scissor.resetGuiTransform();
            this.close();
            return;
         }
      }

      float searchW = 90.0F;
      float searchH = 18.0F;
      float searchX = windowWidth / 2.0F - searchW / 2.0F;
      float searchY = panelY + panelHeight + 35.0F;
      this.k58E8.setBounds(searchX, searchY, searchW, searchH);
      this.k58E8.render(context, mouseX, mouseY, delta);
      String hoveredDesc = null;

      for (Panel panelx : this.vdhdx) {
         boolean isMouseInPanel = HoverUtil.isHovered(mouseX, mouseY, panelx.getX(), panelx.getY(), panelx.getWidth(), panelx.getHeight());

         for (ModuleComponent component : panelx.getModuleComponents()) {
            if (component.isHovered() && isMouseInPanel && this.k58E8.isEmpty()) {
               String desc = component.getModule().getDesc();
               if (desc != null && !desc.isEmpty()) {
                  hoveredDesc = desc;
               }
            }
         }
      }

      if (hoveredDesc != null) {
         this.m3aHCvU = hoveredDesc;
      }

      this.sgp8.run(hoveredDesc != null);
      float da = this.sgp8.getValue();
      if (da > 0.01F && this.m3aHCvU != null) {
         float size = 7.5F;
         float padX = 8.0F;
         float padY = 5.5F;
         float textWidth = Fonts.SFREGULAR.get().getWidth(this.m3aHCvU, size);
         float tooltipW = textWidth + padX * 2.0F;
         float tooltipH = size + padY * 2.0F;
         float tooltipX = MathHelper.clamp(windowWidth / 2.0F - tooltipW / 2.0F, 4.0F, windowWidth - tooltipW - 4.0F);
         float tooltipY = panelY - tooltipH - 8.0F;
         int a = (int)(255.0F * da);
         float cx = tooltipX + tooltipW / 2.0F;
         float cy = tooltipY + tooltipH / 2.0F;
         float scale = 0.92F + 0.08F * da;
         context.getMatrices().push();
         context.getMatrices().translate(cx, cy, 0.0F);
         context.getMatrices().scale(scale, scale, 1.0F);
         context.getMatrices().translate(-cx, -cy, 0.0F);
         DrawUtil.drawRound(tooltipX, tooltipY, tooltipW, tooltipH, 4.0F, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), (int)(245.0F * da)));
         DrawUtil.drawRound(
            tooltipX - 0.5F, tooltipY - 0.5F, tooltipW + 1.0F, tooltipH + 1.0F, 4.5F, ColorProvider.rgba(255, 255, 255, (float)((int)(18.0F * da)))
         );
         DrawUtil.drawText(
            Fonts.SFREGULAR.get(),
            this.m3aHCvU,
            tooltipX + padX,
            tooltipY + (tooltipH - size) / 2.0F + 0.2F,
            ColorProvider.setAlpha(ColorProvider.getColorText(), a),
            size
         );
         context.getMatrices().pop();
      }

      if (this.p3mb != null) {
         this.p3mb.render(context, mouseX, mouseY, delta);
      }

      modelView.popMatrix();
      Scissor.resetGuiTransform();
      this.ghr2V();
      long desiredCursor;
      if (CursorManager.shouldBeHand()) {
         desiredCursor = this.cTVPXi5;
      } else if (CursorManager.shouldIBeam()) {
         desiredCursor = this.k99M9Jd;
      } else if (CursorManager.shouldClick()) {
         desiredCursor = this.u7cr;
      } else {
         desiredCursor = this.ymtQ77s;
      }

      this.jssN(desiredCursor);
   }

   @Override
   public void removed() {
      super.removed();
      if (this.cK9xy) {
         GLFW.glfwSetCursor(mc.getWindow().getHandle(), 0L);
         GLFW.glfwDestroyCursor(this.cTVPXi5);
         GLFW.glfwDestroyCursor(this.k99M9Jd);
         GLFW.glfwDestroyCursor(this.u7cr);
         GLFW.glfwDestroyCursor(this.ymtQ77s);
         this.cK9xy = false;
         this.qL3N = 0L;
      }
   }

   public boolean searchCheck(String text) {
      if (this.k58E8.isEmpty()) {
         return false;
      } else {
         String raw = this.k58E8.text;
         if (!raw.equals(this.yvle)) {
            this.yvle = raw;
            this.jkQX = raw.replaceAll(" ", "").toLowerCase();
         }

         return !text.replaceAll(" ", "").toLowerCase().contains(this.jkQX);
      }
   }

   public void openItemModelGallery(ItemModelSetting setting) {
      this.p3mb = new ItemModelGalleryPopup(setting);
   }

   private float yy8l() {
      ClickGui module = Instance.get(ClickGui.class);
      return module != null ? (float)module.size.getValue() : 1.0F;
   }

   private double v9ea4hc(double mouseX) {
      float s = this.yy8l();
      double cx = mc.getWindow().getScaledWidth() / 2.0;
      return (mouseX - cx) / s + cx;
   }

   private double pu4f(double mouseY) {
      float s = this.yy8l();
      double cy = mc.getWindow().getScaledHeight() / 2.0;
      return (mouseY - cy) / s + cy;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      mouseX = this.v9ea4hc(mouseX);
      mouseY = this.pu4f(mouseY);
      if (this.p3mb != null) {
         if (!this.p3mb.contains(mouseX, mouseY)) {
            this.p3mb = null;
         } else {
            this.p3mb.mouseClicked(mouseX, mouseY, button);
         }

         return true;
      } else {
         this.k58E8.mouseClicked(mouseX, mouseY, button);
         if (this.k58E8.isEmpty()) {
            for (Panel panel : this.vdhdx) {
               if (HoverUtil.isHovered(mouseX, mouseY, panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight())) {
                  panel.mouseClicked(mouseX, mouseY, button);
               }
            }
         } else {
            for (Panel panelx : this.vdhdx) {
               panelx.mouseClicked(mouseX, mouseY, button);
            }
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      mouseX = this.v9ea4hc(mouseX);
      mouseY = this.pu4f(mouseY);
      if (this.p3mb != null) {
         this.p3mb.mouseReleased(mouseX, mouseY, button);
         return true;
      } else {
         for (Panel panel : this.vdhdx) {
            panel.mouseReleased(mouseX, mouseY, button);
         }

         return super.mouseReleased(mouseX, mouseY, button);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      mouseX = this.v9ea4hc(mouseX);
      mouseY = this.pu4f(mouseY);
      if (this.p3mb != null) {
         this.p3mb.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
         return true;
      } else {
         for (Panel panel : this.vdhdx) {
            panel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
         }

         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.p3mb != null) {
         if (keyCode == 256) {
            this.p3mb = null;
         } else {
            this.p3mb.keyPressed(keyCode, scanCode, modifiers);
         }

         return true;
      } else {
         boolean anyModuleBinding = false;

         label67:
         for (Panel panel : this.vdhdx) {
            Iterator var7 = panel.getModuleComponents().iterator();

            while (true) {
               if (var7.hasNext()) {
                  ModuleComponent component = (ModuleComponent)var7.next();
                  if (!component.isBinding()) {
                     continue;
                  }

                  anyModuleBinding = true;
               }

               if (anyModuleBinding) {
                  break label67;
               }
               break;
            }
         }

         if (keyCode == 256 && anyModuleBinding) {
            for (Panel panel : this.vdhdx) {
               panel.keyPressed(keyCode, scanCode, modifiers);
            }

            return true;
         } else if (this.k58E8.isFocused()) {
            this.k58E8.keyPressed(keyCode, scanCode, modifiers);
            return true;
         } else if (keyCode == 256) {
            this.ghr2V();
            this.jssN(this.ymtQ77s);
            this.fg2H = true;
            return true;
         } else {
            for (Panel panel : this.vdhdx) {
               panel.keyPressed(keyCode, scanCode, modifiers);
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
         }
      }
   }

   @Override
   public boolean charTyped(char chr, int modifiers) {
      if (this.p3mb != null) {
         this.p3mb.charTyped(chr, modifiers);
         return true;
      } else if (this.k58E8.isFocused()) {
         this.k58E8.charTyped(chr, modifiers);
         return true;
      } else {
         return super.charTyped(chr, modifiers);
      }
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Generated
   public List<Panel> getPanels() {
      return this.vdhdx;
   }

   @Generated
   public SearchField getSearchField() {
      return this.k58E8;
   }

   @Generated
   public ItemModelGalleryPopup getItemModelGallery() {
      return this.p3mb;
   }

   @Generated
   public Animation getDescAnim() {
      return this.sgp8;
   }

   @Generated
   public String getLastDesc() {
      return this.m3aHCvU;
   }

   @Generated
   public boolean isClosing() {
      return this.fg2H;
   }

   @Generated
   public long getHandCursor() {
      return this.cTVPXi5;
   }

   @Generated
   public long getIBeamCursor() {
      return this.k99M9Jd;
   }

   @Generated
   public long getPointingCursor() {
      return this.u7cr;
   }

   @Generated
   public long getArrowCursor() {
      return this.ymtQ77s;
   }

   @Generated
   public boolean isCursorsCreated() {
      return this.cK9xy;
   }

   @Generated
   public long getCurrentCursor() {
      return this.qL3N;
   }

   @Generated
   public String getCachedRawQuery() {
      return this.yvle;
   }

   @Generated
   public String getCachedNormalizedQuery() {
      return this.jkQX;
   }
}
