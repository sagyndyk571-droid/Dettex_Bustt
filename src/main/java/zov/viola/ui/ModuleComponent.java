package zov.viola.ui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import zov.viola.module.Module;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ItemModelSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.Setting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.module.settings.ThemeSetting;
import zov.viola.obf.D;
import zov.viola.ui.component.Component;
import zov.viola.ui.component.impl.BindComponent;
import zov.viola.ui.component.impl.BooleanComponent;
import zov.viola.ui.component.impl.ColorPickerComponent;
import zov.viola.ui.component.impl.ItemModelComponent;
import zov.viola.ui.component.impl.ModeComponent;
import zov.viola.ui.component.impl.ModeListComponent;
import zov.viola.ui.component.impl.SliderComponent;
import zov.viola.ui.component.impl.ThemeComponent;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class ModuleComponent extends Component {
   private final Module olNjcjc;
   private final Panel oprTtJg;
   private final Animation smpleiT = new Animation(Easing.QUINTIC_OUT, 320L);
   private final Animation t4Pl = new Animation(Easing.QUINTIC_OUT, 300L);
   private final Animation iE1nJET = new Animation(Easing.QUINTIC_OUT, 400L);
   public boolean open;
   private boolean gn4f;
   private boolean fR3u5;
   private final ObjectArrayList<Component> yx8M = new ObjectArrayList();

   public ModuleComponent(Module module, Panel panel) {
      this.olNjcjc = module;
      this.oprTtJg = panel;

      for (Setting setting : module.getSettings()) {
         switch (setting) {
            case BooleanSetting option:
               this.yx8M.add(new BooleanComponent(option));
               break;
            case ItemModelSetting optionx:
               this.yx8M.add(new ItemModelComponent(optionx, panel::openItemModelGallery));
               break;
            case ModeSetting optionxx:
               this.yx8M.add(new ModeComponent(optionxx));
               break;
            case ModeListSetting optionxxx:
               this.yx8M.add(new ModeListComponent(optionxxx));
               break;
            case SliderSetting optionxxxx:
               this.yx8M.add(new SliderComponent(optionxxxx));
               break;
            case BindSetting optionxxxxx:
               this.yx8M.add(new BindComponent(optionxxxxx));
               break;
            case ThemeSetting optionxxxxxx:
               this.yx8M.add(new ThemeComponent(optionxxxxxx));
               break;
            case ColorSetting optionxxxxxxx:
               this.yx8M.add(new ColorPickerComponent(optionxxxxxxx));
               break;
            default:
         }
      }

      if (module.getName().equals("Interface")) {
      }
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
      this.gn4f = HoverUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, 19.0);
      this.t4Pl.run(this.gn4f);
      this.smpleiT.run(this.open);
      this.iE1nJET.run(this.olNjcjc.isEnabled());
      if (HoverUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, 19.0)) {
         CursorManager.requestHand();
      }

      float alpha = Math.max(Math.min(this.oprTtJg.getAnimationAlpha().getValue(), 1.0F), 0.0F);
      int textColor = ColorProvider.interpolateColor(
         ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), (int)(255.0F * alpha)),
         ColorProvider.setAlpha(ColorProvider.getColorText(), (int)(255.0F * alpha)),
         this.iE1nJET.getValue()
      );
      float highlightProgress = Math.max(this.t4Pl.getValue(), this.iE1nJET.getValue());
      int outlineAlpha = (int)((25.0F + 40.0F * highlightProgress) * alpha);
      int outlineColor = ColorProvider.rgba(255, 255, 255, (float)outlineAlpha);
      float currentHeight = 19.0F + (this.height - 19.0F) * this.smpleiT.getValue();
      float visTop = this.oprTtJg.getY() + 25.0F;
      float visBottom = this.oprTtJg.getY() + this.oprTtJg.getHeight() - 4.0F;
      if (!(this.y + currentHeight < visTop) && !(this.y > visBottom)) {
         DrawUtil.drawRound(this.x - 1.0F, this.y - 1.0F, this.width + 2.0F, currentHeight + 1.0F, 3.5F, outlineColor);
         DrawUtil.drawRound(
            this.x, this.y, this.width, currentHeight - 0.5F, 3.0F, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), (int)(235.0F * alpha))
         );
         if (this.fR3u5) {
            DrawUtil.drawText(
               Fonts.SFREGULAR.get(),
               D.k(
                  new int[]{1069, 1100, 1228, 1251, 1032, 1086, 1231, 255, 1034, 1095, 1226, 1261, 1032, 1076, 1209, 241, 30, 82}, new int[]{48, 124, 250, 223}
               ),
               this.x
                  + this.width / 2.0F
                  - Fonts.SFREGULAR
                        .get()
                        .getWidth(
                           D.k(
                              new int[]{1109, 1149, 1063, 1226, 1136, 1039, 1060, 214, 1138, 1142, 1057, 1220, 1136, 1029, 1106, 216, 102, 99},
                              new int[]{72, 77, 17, 246}
                           ),
                           7.5F
                        )
                     / 2.0F,
               this.y + 5.75F,
               ColorProvider.rgba(255, 255, 255, (float)((int)(255.0F * alpha))),
               7.5F
            );
         } else {
            float textY = this.y + 5.75F;
            DrawUtil.drawText(Fonts.SFREGULAR.get(), this.olNjcjc.getName(), this.x + 3.0F, textY, textColor, 7.5F);
            float toggleW = 20.0F;
            float toggleH = 10.0F;
            float toggleX = this.x + this.width - toggleW - 4.0F;
            float toggleY = this.y + (19.0F - toggleH) / 2.0F;
            int toggleBgColor = ColorProvider.interpolateColor(
               ColorProvider.rgba(40, 52, 92, (float)((int)(150.0F * alpha))),
               ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), (int)(200.0F * alpha)),
               this.iE1nJET.getValue()
            );
            DrawUtil.drawRound(toggleX, toggleY, toggleW, toggleH, toggleH / 2.0F, toggleBgColor);
            float circleSize = toggleH - 2.5F;
            float circleX = toggleX + 1.25F + (toggleW - circleSize - 2.5F) * this.iE1nJET.getValue();
            float circleY = toggleY + 1.25F;
            DrawUtil.drawRound(circleX, circleY, circleSize, circleSize, circleSize / 2.0F, ColorProvider.rgba(255, 255, 255, (float)((int)(255.0F * alpha))));
         }

         if (this.smpleiT.getValue() > 0.01F) {
            float compY = this.y + 22.0F;
            float panelTop = this.oprTtJg.getY() + 25.0F;
            float panelBottom = this.oprTtJg.getY() + this.oprTtJg.getHeight() - 4.0F;
            float boxBottom = this.y + currentHeight;
            float intersectY = Math.max(this.y + 19.0F, panelTop);
            float intersectBottom = Math.min(boxBottom, panelBottom);
            float intersectHeight = Math.max(0.0F, intersectBottom - intersectY);
            float darkHeight = currentHeight - 19.0F;
            if (darkHeight > 0.0F) {
               DrawUtil.drawRound(
                  this.x + 1.0F,
                  this.y + 19.0F,
                  this.width - 2.0F,
                  darkHeight,
                  0.0F,
                  ColorProvider.rgba(0, 0, 0, (float)((int)(30.0F * alpha * this.smpleiT.getValue())))
               );
            }

            ObjectListIterator var32 = this.yx8M.iterator();

            while (var32.hasNext()) {
               Component component = (Component)var32.next();
               component.getAlphaAnim().setValue(Math.min(this.oprTtJg.getAnimationAlpha().getValue(), 1.0F) * this.smpleiT.getValue());
               component.getAlphaAnimSetting().run(component.isVisible());
               float visibleProgress = MathHelper.clamp(component.getAlphaAnimSetting().getValue(), 0.0F, 1.0F);
               if (component.isVisible() || visibleProgress > 0.0F) {
                  component.setX(this.x);
                  component.setY(compY);
                  component.setWidth(this.width - 4.0F);
                  Scissor.push();
                  Scissor.setFromComponentCoordinates((double)this.x, (double)intersectY, (double)this.width, (double)intersectHeight);
                  component.render(context, mouseX, mouseY, partialTicks);
                  Scissor.unset();
                  Scissor.pop();
                  compY += component.getHeight() * visibleProgress;
               }
            }
         }
      } else {
         this.gn4f = false;
      }
   }

   @Override
   public void mouseClicked(double mouseX, double mouseY, int button) {
      if (this.h96As(mouseX, mouseY, 19.0F)) {
         if (button == 0) {
            this.olNjcjc.setEnabled(!this.olNjcjc.isEnabled());
         }

         if (button == 1 && !this.yx8M.isEmpty()) {
            this.open = !this.open;
         }

         if (button == 2) {
            this.fR3u5 = !this.fR3u5;
         }
      }

      if (this.open) {
         ObjectListIterator var6 = this.yx8M.iterator();

         while (var6.hasNext()) {
            Component component = (Component)var6.next();
            if (component.isVisible() && component.getAlphaAnimSetting().getValue() > 0.5F) {
               component.mouseClicked(mouseX, mouseY, button);
            }
         }
      }
   }

   @Override
   public void mouseReleased(double mouseX, double mouseY, int button) {
      if (this.open) {
         ObjectListIterator var6 = this.yx8M.iterator();

         while (var6.hasNext()) {
            Component component = (Component)var6.next();
            component.mouseReleased(mouseX, mouseY, button);
         }
      }
   }

   @Override
   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.fR3u5) {
         if (keyCode != 256 && keyCode != 261) {
            this.olNjcjc.setKey(keyCode);
         } else {
            this.olNjcjc.setKey(-1);
         }

         this.fR3u5 = false;
      }

      if (this.open) {
         ObjectListIterator var4 = this.yx8M.iterator();

         while (var4.hasNext()) {
            Component component = (Component)var4.next();
            component.keyPressed(keyCode, scanCode, modifiers);
         }
      }
   }

   public boolean isBinding() {
      return this.fR3u5;
   }

   private boolean h96As(double mouseX, double mouseY, float heightCheck) {
      return HoverUtil.isHovered(mouseX, mouseY, this.x, this.y, this.width, heightCheck);
   }

   @Generated
   public Module getModule() {
      return this.olNjcjc;
   }

   @Generated
   public Panel getPanel() {
      return this.oprTtJg;
   }

   @Generated
   public Animation getAnimation() {
      return this.smpleiT;
   }

   @Generated
   public Animation getHoverAnim() {
      return this.t4Pl;
   }

   @Generated
   public Animation getEnabledAnim() {
      return this.iE1nJET;
   }

   @Generated
   public boolean isOpen() {
      return this.open;
   }

   @Generated
   public boolean isHovered() {
      return this.gn4f;
   }

   @Generated
   public ObjectArrayList<Component> getComponents() {
      return this.yx8M;
   }
}
