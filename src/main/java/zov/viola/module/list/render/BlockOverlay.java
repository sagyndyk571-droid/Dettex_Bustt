package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.providers.ResourceProvider;

@ModuleInformation(
   moduleName = "Block Overlay",
   moduleDesc = "Кастомная подсветка блока",
   moduleCategory = ModuleCategory.RENDER
)
public class BlockOverlay extends Module {
   private static final ShaderProgramKey ENERGY_SHADER = jydj("block_overlay");
   private static final ShaderProgramKey WAVE_SHADER = jydj("block_overlay_wave");
   private static final ShaderProgramKey CRYSTAL_SHADER = jydj("block_overlay_crystal");
   private static final ShaderProgramKey GALAXY_SHADER = jydj("block_overlay_galaxy");
   private static final ShaderProgramKey LIGHTNING_SHADER = jydj("block_overlay_lightning");
   public final ModeSetting shaderMode = new ModeSetting(
      "Шейдер", "Energy", "Energy", "Wave", "Crystal", "Galaxy", "Lightning"
   );
   public final BooleanSetting themeColor = new BooleanSetting(
      "Цвет от темы", true
   );
   public final ColorSetting overlayColor = new ColorSetting(
         "Свой цвет", -9673774
      )
      .setVisible(() -> !this.themeColor.getValue());
   public final SliderSetting fillOpacity = new SliderSetting(
      D.k(
         new int[]{1170, 1273, 1146, 1029, 1229, 1161, 1027, 1039, 1203, 1272, 1030, 1150, 173, 1166, 1140, 1033, 1205, 1163, 1150, 1034},
         new int[]{141, 185, 68, 50}
      ),
      0.32,
      0.0,
      1.0,
      0.05
   );
   public final BooleanSetting outline = new BooleanSetting("Обводка", true);
   public final SliderSetting outlineWidth = new SliderSetting(
         "Толщина обводки",
         2.0,
         0.5,
         5.0,
         0.1
      )
      .setVisible(() -> this.outline.getValue());
   public final SliderSetting outlineOpacity = new SliderSetting(
         D.k(
            new int[]{1116, 1201, 1166, 1058, 1027, 1217, 1271, 1064, 1149, 1200, 1266, 1113, 99, 1231, 1153, 1063, 1149, 1221, 1162, 1069},
            new int[]{67, 241, 176, 21}
         ),
         1.0,
         0.0,
         1.0,
         0.05
      )
      .setVisible(() -> this.outline.getValue());
   public final SliderSetting smoothness = new SliderSetting(
      "Плавность", 6.0, 1.0, 10.0, 1.0
   );
   private Box dDMe538;
   private long gEqm;
   private Object sZid0;
   private float mcPD;

   private static ShaderProgramKey jydj(String name) {
      return new ShaderProgramKey(ResourceProvider.getShaderIdentifier(name), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
   }

   @Subscribe
   public void onWorldRender(EventWorldRender event) {
      if (this.mc.world != null && this.mc.player != null) {
         long now = System.nanoTime();
         float dt = this.gEqm == 0L ? 0.016666668F : Math.min(0.1F, (float)(now - this.gEqm) / 1.0E9F);
         this.gEqm = now;
         Box targetBox = this.xc3Cft();
         if (this.sZid0 != this.mc.world) {
            this.sZid0 = this.mc.world;
            this.dDMe538 = targetBox;
            this.mcPD = 0.0F;
         }

         float smoothnessValue = this.smoothness.getFloatValue();
         if (smoothnessValue == 1.0F) {
            this.dDMe538 = targetBox;
            this.mcPD = targetBox == null ? 0.0F : 1.0F;
            if (this.dDMe538 == null) {
               return;
            }
         } else {
            float smoothnessProgress = Math.max(0.0F, Math.min(1.0F, (smoothnessValue - 2.0F) / 8.0F));
            float responseRate = 40.0F + -28.0F * smoothnessProgress;
            float easing = 1.0F - (float)Math.exp(-responseRate * dt);
            if (targetBox != null) {
               if (this.dDMe538 == null) {
                  this.dDMe538 = targetBox;
               } else {
                  this.dDMe538 = this.dSwRg(this.dDMe538, targetBox, easing);
               }

               this.mcPD = this.mcPD + (1.0F - this.mcPD) * easing;
            } else {
               this.mcPD = this.mcPD + (0.0F - this.mcPD) * easing;
               if (this.mcPD < 0.01F) {
                  this.dDMe538 = null;
                  this.mcPD = 0.0F;
                  return;
               }
            }
         }

         if (this.dDMe538 != null) {
            int color = this.themeColor.getValue() ? ColorProvider.getColorClient() : this.overlayColor.getValue();
            MatrixStack matrices = event.getMatrixStack();
            Vec3d camPos = this.mc.gameRenderer.getCamera().getPos();
            matrices.push();
            double minX = this.dDMe538.minX - camPos.x;
            double minY = this.dDMe538.minY - camPos.y;
            double minZ = this.dDMe538.minZ - camPos.z;
            double maxX = this.dDMe538.maxX - camPos.x;
            double maxY = this.dDMe538.maxY - camPos.y;
            double maxZ = this.dDMe538.maxZ - camPos.z;
            this.mYvnB8(matrices, minX, minY, minZ, maxX, maxY, maxZ, color, this.mcPD);
            if (this.outline.getValue()) {
               this.pw8mtkM(matrices, minX, minY, minZ, maxX, maxY, maxZ, color, this.mcPD);
            }

            matrices.pop();
         }
      }
   }

   @Override
   public void onDisable() {
      this.dDMe538 = null;
      this.gEqm = 0L;
      this.sZid0 = null;
      this.mcPD = 0.0F;
      super.onDisable();
   }

   private Box xc3Cft() {
      if (this.mc.crosshairTarget != null && this.mc.crosshairTarget.getType() == Type.BLOCK) {
         BlockPos pos = ((BlockHitResult)this.mc.crosshairTarget).getBlockPos();
         BlockState state = this.mc.world.getBlockState(pos);
         if (state.isAir()) {
            return null;
         } else {
            VoxelShape shape = state.getOutlineShape(this.mc.world, pos);
            return shape.isEmpty() ? null : shape.getBoundingBox().offset(pos);
         }
      } else {
         return null;
      }
   }

   private Box dSwRg(Box from, Box to, float delta) {
      return new Box(
         from.minX + (to.minX - from.minX) * delta,
         from.minY + (to.minY - from.minY) * delta,
         from.minZ + (to.minZ - from.minZ) * delta,
         from.maxX + (to.maxX - from.maxX) * delta,
         from.maxY + (to.maxY - from.maxY) * delta,
         from.maxZ + (to.maxZ - from.maxZ) * delta
      );
   }

   private void pw8mtkM(MatrixStack matrices, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color, float visibility) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.lineWidth(this.outlineWidth.getFloatValue());
      Matrix4f matrix = matrices.peek().getPositionMatrix();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      float outlineAlpha = this.outlineOpacity.getFloatValue() * Math.max(0.0F, Math.min(1.0F, visibility));
      buffer.vertex(matrix, (float)minX, (float)minY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)minY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)minY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)minY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)minY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)minY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)minY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)minY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)maxY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)maxY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)maxY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)maxY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)maxY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)maxY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)maxY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)maxY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)minY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)maxY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)minY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)maxY, (float)minZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)minY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)maxX, (float)maxY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)minY, (float)maxZ).color(r, g, b, outlineAlpha);
      buffer.vertex(matrix, (float)minX, (float)maxY, (float)maxZ).color(r, g, b, outlineAlpha);
      BufferRenderer.drawWithGlobalProgram(buffer.end());
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
      RenderSystem.lineWidth(1.0F);
   }

   private void mYvnB8(MatrixStack matrices, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color, float visibility) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.disableCull();
      ShaderProgram shader = RenderSystem.setShader(this.d8a9e8X());
      if (shader == null) {
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      } else if (shader.getUniform("Time") != null) {
         shader.getUniform("Time").set((float)(System.currentTimeMillis() % 100000L) / 1000.0F);
      }

      Matrix4f matrix = matrices.peek().getPositionMatrix();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float r = (color >> 16 & 0xFF) / 255.0F;
      float g = (color >> 8 & 0xFF) / 255.0F;
      float b = (color & 0xFF) / 255.0F;
      float a = this.fillOpacity.getFloatValue() * Math.max(0.0F, Math.min(1.0F, visibility));
      this.f2Mho(buffer, matrix, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
      this.f2Mho(buffer, matrix, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
      this.f2Mho(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, r, g, b, a);
      this.f2Mho(buffer, matrix, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
      this.f2Mho(buffer, matrix, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);
      this.f2Mho(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, r, g, b, a);
      BufferRenderer.drawWithGlobalProgram(buffer.end());
      RenderSystem.enableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
   }

   private ShaderProgramKey d8a9e8X() {
      String var1 = this.shaderMode.getValue();

      return switch (var1) {
         case "Wave" -> WAVE_SHADER;
         case "Crystal" -> CRYSTAL_SHADER;
         case "Galaxy" -> GALAXY_SHADER;
         case "Lightning" -> LIGHTNING_SHADER;
         default -> ENERGY_SHADER;
      };
   }

   private void f2Mho(
      BufferBuilder buffer,
      Matrix4f matrix,
      double x1,
      double y1,
      double z1,
      double x2,
      double y2,
      double z2,
      double x3,
      double y3,
      double z3,
      double x4,
      double y4,
      double z4,
      float r,
      float g,
      float b,
      float a
   ) {
      buffer.vertex(matrix, (float)x1, (float)y1, (float)z1).texture(0.0F, 0.0F).color(r, g, b, a);
      buffer.vertex(matrix, (float)x2, (float)y2, (float)z2).texture(1.0F, 0.0F).color(r, g, b, a);
      buffer.vertex(matrix, (float)x3, (float)y3, (float)z3).texture(1.0F, 1.0F).color(r, g, b, a);
      buffer.vertex(matrix, (float)x4, (float)y4, (float)z4).texture(0.0F, 1.0F).color(r, g, b, a);
   }
}
