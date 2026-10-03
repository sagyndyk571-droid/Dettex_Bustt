package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ColorProvider;

@ModuleInformation(
   moduleName = "FT Helper",
   moduleDesc = "Границы фантайм-предметов линиями",
   moduleCategory = ModuleCategory.RENDER
)
public class FtHelper extends Module {
   public final BooleanSetting themeColor = new BooleanSetting(
      "Цвет от темы", true
   );
   public final ColorSetting customColor = new ColorSetting(
         "Свой цвет", -9673774
      )
      .setVisible(() -> !this.themeColor.getValue());
   public final SliderSetting lineWidth = new SliderSetting(
      "Толщина", 2.0, 0.5, 5.0, 0.1
   );
   public final SliderSetting opacity = new SliderSetting(
      "Прозрачность", 0.85, 0.05, 1.0, 0.05
   );
   public final SliderSetting circleSegments = new SliderSetting(
      "Точность круга", 64.0, 16.0, 128.0, 8.0
   );

   @Subscribe
   private void onWorldRender(EventWorldRender event) {
      if (this.mc.player != null && this.mc.world != null) {
         FtHelper.ZoneType zone = this.wrtzm();
         if (zone != FtHelper.ZoneType.NONE) {
            float tickDelta = event.getTickDelta();
            Vec3d pos = this.mc.player.getLerpedPos(tickDelta).subtract(this.mc.gameRenderer.getCamera().getPos());
            if (!(pos.length() > 360.0)) {
               int color = this.themeColor.getValue() ? ColorProvider.getColorClient() : this.customColor.getValue();
               float zoneRadius = 10.0F;
               if (this.dz2P(zoneRadius)) {
                  color = ColorProvider.rgba(82, 200, 82, 255.0F);
               }

               float yaw = MathHelper.lerpAngleDegrees(tickDelta, this.mc.player.prevYaw, this.mc.player.getYaw());
               MatrixStack matrices = event.getMatrixStack();
               matrices.push();
               switch (zone) {
                  case DEORIT:
                     this.iudxp(matrices, pos, 10.0, color);
                     break;
                  case DUST:
                     this.iudxp(matrices, pos, 10.0, color);
               }

               matrices.pop();
            }
         }
      }
   }

   private boolean dz2P(double radius) {
      PlayerEntity me = this.mc.player;
      double r2 = radius * radius;

      for (PlayerEntity player : this.mc.world.getPlayers()) {
         if (player != me && !player.isSpectator() && player.isAlive() && player.squaredDistanceTo(me) <= r2) {
            return true;
         }
      }

      return false;
   }

   private FtHelper.ZoneType wrtzm() {
      ItemStack main = this.mc.player.getMainHandStack();
      ItemStack off = this.mc.player.getOffHandStack();
      if (main.isOf(Items.ENDER_EYE) || off.isOf(Items.ENDER_EYE)) {
         return FtHelper.ZoneType.DEORIT;
      } else {
         return !main.isOf(Items.SUGAR) && !off.isOf(Items.SUGAR) ? FtHelper.ZoneType.NONE : FtHelper.ZoneType.DUST;
      }
   }

   private void iudxp(MatrixStack matrices, Vec3d p, double radius, int color) {
      double y = p.y;
      int segments = this.circleSegments.getIntValue();
      double[] xs = new double[segments + 1];
      double[] zs = new double[segments + 1];

      for (int i = 0; i <= segments; i++) {
         double angle = (double)i / segments * Math.PI * 2.0;
         xs[i] = p.x + Math.cos(angle) * radius;
         zs[i] = p.z + Math.sin(angle) * radius;
      }

      for (int i = 0; i < segments; i++) {
         this.rE7sx(matrices, xs[i], y, zs[i], xs[i + 1], y, zs[i + 1], color);
      }
   }

   private void rE7sx(MatrixStack matrices, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(this.lineWidth.getFloatValue());
      Matrix4f matrix = matrices.peek().getPositionMatrix();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      float a = this.opacity.getFloatValue();
      buffer.vertex(matrix, (float)x1, (float)y1, (float)z1).color(r, g, b, a);
      buffer.vertex(matrix, (float)x2, (float)y2, (float)z2).color(r, g, b, a);
      BufferRenderer.drawWithGlobalProgram(buffer.end());
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
      RenderSystem.lineWidth(1.0F);
   }

   private static enum ZoneType {
      NONE,
      DEORIT,
      DUST;
   }
}
