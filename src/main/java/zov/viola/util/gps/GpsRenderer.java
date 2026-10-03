package zov.viola.util.gps;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import zov.viola.Viola;
import zov.viola.event.list.EventHUD;
import zov.viola.util.IMinecraft;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public final class GpsRenderer implements IMinecraft {
   private static final GpsRenderer INSTANCE = new GpsRenderer();
   private static final Identifier GPS_ICON = Identifier.of("mre", "textures/gps.png");
   private double u4YZaHW;
   private double zXqvv;
   private boolean mV3e;

   public static GpsRenderer get() {
      return INSTANCE;
   }

   private GpsRenderer() {
      Viola.getInstance().getEventBus().register(this);
   }

   public void setTarget(double x2, double z2) {
      this.u4YZaHW = x2;
      this.zXqvv = z2;
   }

   @Subscribe
   private void onHud(EventHUD e2) {
      if (this.mV3e) {
         if (MinecraftClient.getInstance().player != null) {
            DrawContext drawContext = e2.getDrawContext();
            MatrixStack ms = drawContext.getMatrices();
            int sw = mc.getWindow().getScaledWidth();
            int sh = mc.getWindow().getScaledHeight();
            Vec3d pos = mc.player.getPos();
            double dx = this.u4YZaHW - pos.x;
            double dz = this.zXqvv - pos.z;
            int dist = (int)Math.sqrt(dx * dx + dz * dz);
            float angle = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float rot = MathHelper.wrapDegrees(angle - mc.player.getYaw());
            String distTxt = dist + "m";
            int white = ColorProvider.rgba(255, 255, 255, 255.0F);
            double cx = sw / 2.0;
            double cy = sh / 2.0 - 80.0;
            float w1 = Fonts.SFSEMIBOLD.get().getWidth(distTxt, 7.0F);
            DrawUtil.drawText(Fonts.SFSEMIBOLD.get(), distTxt, (float)(cx - w1 / 2.0F), (float)(cy + 8.0), white, 7.0F);
            int iconSize = 16;
            float centerX = (float)cx;
            float centerY = (float)(cy + 22.0);
            ms.push();
            ms.translate(centerX, centerY, 0.0F);
            ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rot));
            ms.translate(-iconSize / 2.0F, -iconSize / 2.0F, 0.0F);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawContext.drawTexture(RenderLayer::getGuiTextured, GPS_ICON, 0, 0, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
            ms.pop();
         }
      }
   }

   public void setEnabled(boolean enabled) {
      this.mV3e = enabled;
   }
}
