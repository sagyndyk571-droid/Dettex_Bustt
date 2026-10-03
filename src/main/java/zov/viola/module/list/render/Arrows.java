package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import zov.viola.event.list.EventHUD;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.render.providers.ColorProvider;

@ModuleInformation(
   moduleName = "Arrows",
   moduleDesc = "HUD-стрелки, указывающие на игроков",
   moduleCategory = ModuleCategory.RENDER
)
public class Arrows extends Module {
   private static final Identifier ICON = Identifier.of("mre", "images/triangle.png");
   private final SliderSetting fxxvt46 = new SliderSetting(
      "Радиус", 50.0, 30.0, 120.0, 1.0
   );
   private final SliderSetting p6as5Q = new SliderSetting(
      "Размер", 32.0, 16.0, 64.0, 1.0
   );
   private final BooleanSetting myL0 = new BooleanSetting(
      "Цвет от темы", true
   );
   private final ColorSetting qJZM1cG = new ColorSetting(
         "Свой цвет", -6913281
      )
      .setVisible(() -> !this.myL0.getValue());

   @Subscribe
   private void onHud(EventHUD e) {
      if (this.mc.world != null && this.mc.player != null) {
         if (!this.mc.options.hudHidden) {
            if (this.mc.options.getPerspective().equals(Perspective.FIRST_PERSON)) {
               List<AbstractClientPlayerEntity> players = this.mc.world.getPlayers();
               if (!players.isEmpty()) {
                  DrawContext ctx = e.getDrawContext();
                  MatrixStack matrix = ctx.getMatrices();
                  float tickDelta = this.mc.getRenderTickCounter().getTickDelta(true);
                  float cameraYaw = this.mc.gameRenderer.getCamera().getYaw();
                  float middleW = this.mc.getWindow().getScaledWidth() / 2.0F;
                  float middleH = this.mc.getWindow().getScaledHeight() / 2.0F;
                  float posY = middleH - this.fxxvt46.getFloatValue();
                  float size = this.p6as5Q.getFloatValue();
                  RenderSystem.enableBlend();
                  RenderSystem.disableCull();
                  RenderSystem.disableDepthTest();
                  RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
                  RenderSystem.setShaderTexture(0, ICON);
                  RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
                  BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

                  for (int i = 0; i < players.size(); i++) {
                     AbstractClientPlayerEntity player = players.get(i);
                     if (player != this.mc.player && player.isAlive() && !player.isRemoved() && !player.isSpectator()) {
                        int col = FriendRepository.isFriend(player.getNameForScoreboard())
                           ? -16711936
                           : (this.myL0.getValue() ? ColorProvider.getColorClient() : this.qJZM1cG.getValue());
                        int baseCol = ColorProvider.setAlpha(col, ColorProvider.alpha(col) / 2);
                        float yaw = this.getRotation(player, tickDelta) - cameraYaw;
                        matrix.push();
                        matrix.translate(middleW, middleH, 0.0F);
                        matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(yaw));
                        matrix.translate(-middleW, -middleH, 0.0F);
                        Matrix4f m = matrix.peek().getPositionMatrix();
                        buffer.vertex(m, middleW - size / 2.0F, posY + size, 0.0F).texture(0.0F, 1.0F).color(baseCol);
                        buffer.vertex(m, middleW + size / 2.0F, posY + size, 0.0F).texture(1.0F, 1.0F).color(baseCol);
                        buffer.vertex(m, middleW + size / 2.0F, posY, 0.0F).texture(1.0F, 0.0F).color(col);
                        buffer.vertex(m, middleW - size / 2.0F, posY, 0.0F).texture(0.0F, 0.0F).color(col);
                        matrix.pop();
                     }
                  }

                  BuiltBuffer builtBuffer = buffer.endNullable();
                  if (builtBuffer != null) {
                     BufferRenderer.drawWithGlobalProgram(builtBuffer);
                  }

                  RenderSystem.enableDepthTest();
                  RenderSystem.enableCull();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.disableBlend();
               }
            }
         }
      }
   }

   private float getRotation(AbstractClientPlayerEntity entity, float tickDelta) {
      Vec3d diff = entity.getLerpedPos(tickDelta).subtract(this.mc.player.getLerpedPos(tickDelta));
      return (float)(-(Math.atan2(diff.x, diff.z) * (180.0 / Math.PI)));
   }
}
