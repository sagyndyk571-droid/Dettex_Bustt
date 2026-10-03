package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import zov.viola.event.list.EventHUD;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.esp.GlowEspRenderer;

@ModuleInformation(
   moduleName = "Glow ESP",
   moduleDesc = "Glow вокруг игроков как Fill в GlassHands",
   moduleCategory = ModuleCategory.RENDER
)
public class GlowEsp extends Module {
   private static GlowEsp cfoo;
   public final SliderSetting glowRadius = new SliderSetting(
      "Размытие", 4.0, 1.0, 6.0, 1.0
   );
   public final SliderSetting glowExposure = new SliderSetting(
      "Яркость", 2.0, 0.5, 5.0, 0.1
   );
   public final SliderSetting saturation = new SliderSetting(
      "Насыщенность", 1.0, 0.0, 2.0, 0.1
   );
   public final BooleanSetting autoColor = new BooleanSetting(
      "Авто цвет", true
   );
   public final BooleanSetting renderSelf = new BooleanSetting("На себя", false);
   public final ColorSetting glowColor1 = new ColorSetting(
         "Цвет 1", new Color(138, 152, 255, 255).getRGB()
      )
      .setVisible(() -> !this.autoColor.getValue());
   public final ColorSetting glowColor2 = new ColorSetting(
         "Цвет 2", new Color(255, 107, 172, 255).getRGB()
      )
      .setVisible(() -> !this.autoColor.getValue());
   public final BooleanSetting rainbow = new BooleanSetting(
         "Радужный цвет", false
      )
      .setVisible(() -> !this.autoColor.getValue());
   public final SliderSetting rainbowSpeed = new SliderSetting(
         "Скорость радуги",
         1.0,
         0.1,
         5.0,
         0.1
      )
      .setVisible(() -> !this.autoColor.getValue() && this.rainbow.getValue());
   public final BooleanSetting outlineEnabled = new BooleanSetting(
      "Обводка", false
   );
   public final SliderSetting outlineWidth = new SliderSetting(
         "Толщина обводки",
         1.0,
         0.5,
         3.0,
         0.5
      )
      .setVisible(() -> this.outlineEnabled.getValue());
   public final ColorSetting outlineColor = new ColorSetting(
         "Цвет обводки",
         new Color(138, 152, 255, 255).getRGB()
      )
      .setVisible(() -> this.outlineEnabled.getValue() && !this.autoColor.getValue());

   public GlowEsp() {
      cfoo = this;
   }

   public static GlowEsp getInstance() {
      return cfoo;
   }

   @Override
   public void onDisable() {
      GlowEspRenderer.getInstance().invalidate();
      super.onDisable();
   }

   @Subscribe
   public void onWorldRender(EventWorldRender event) {
      GlowEspRenderer.getInstance().renderMask(this, event.getMatrixStack(), event.getTickDelta());
   }

   @Subscribe
   public void onHUD(EventHUD event) {
      GlowEspRenderer.getInstance().compositeIfReady(this);
   }
}
