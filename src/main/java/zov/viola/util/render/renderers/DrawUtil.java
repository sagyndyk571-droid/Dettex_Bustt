package zov.viola.util.render.renderers;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import zov.viola.util.IMinecraft;
import zov.viola.util.render.builders.Builder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.msdf.MsdfRenderer;
import zov.viola.util.render.providers.ResourceProvider;
import zov.viola.util.render.renderers.impl.BuiltBlur;
import zov.viola.util.render.renderers.impl.BuiltRectangle;

public class DrawUtil implements IMinecraft {
   public static final List<DrawUtil.Line> LINE = new ArrayList<>();
   public static final List<DrawUtil.Line> LINE_DEPTH = new ArrayList<>();
   private static final ShaderProgramKey CIRCLE_SHADER_KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("circle"), VertexFormats.POSITION_COLOR, Defines.EMPTY
   );
   private static final ShaderProgramKey BORDER_ARC_SHADER_KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("border_arc"), VertexFormats.POSITION_COLOR, Defines.EMPTY
   );

   public static void onRender3D(MatrixStack matrix) {
      if (!LINE.isEmpty()) {
         GL11.glEnable(2881);
         Set<Float> widths = LINE.stream().map(DrawUtil.Line::width).collect(Collectors.toCollection(LinkedHashSet::new));
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
         widths.forEach(width -> {
            RenderSystem.lineWidth(width);
            BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.LINES, VertexFormats.LINES);
            LINE.stream().filter(line -> line.width() == width).forEach(line -> vertexLine(matrix, buffer, line.start(), line.end(), line.colorStart(), line.colorEnd()));
            BufferRenderer.drawWithGlobalProgram(buffer.end());
         });
         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         LINE.clear();
         GL11.glDisable(2881);
      }

      if (!LINE_DEPTH.isEmpty()) {
         GL11.glEnable(2881);
         Set<Float> widths = LINE_DEPTH.stream().map(DrawUtil.Line::width).collect(Collectors.toCollection(LinkedHashSet::new));
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderSystem.setShader(ShaderProgramKeys.RENDERTYPE_LINES);
         widths.forEach(
            width -> {
               RenderSystem.lineWidth(width);
               BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.LINES, VertexFormats.LINES);
               LINE_DEPTH.stream()
                  .filter(line -> line.width() == width)
                  .forEach(line -> vertexLine(matrix, buffer, line.start(), line.end(), line.colorStart(), line.colorEnd()));
               BufferRenderer.drawWithGlobalProgram(buffer.end());
            }
         );
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         LINE_DEPTH.clear();
         GL11.glDisable(2881);
      }
   }

   public static void vertexLine(@NotNull MatrixStack matrices, @NotNull VertexConsumer buffer, Vec3d start, Vec3d end, int lineColor) {
      vertexLine(matrices, buffer, start.toVector3f(), end.toVector3f(), lineColor, lineColor);
   }

   public static void vertexLine(@NotNull MatrixStack matrices, @NotNull VertexConsumer buffer, Vec3d start, Vec3d end, int startColor, int endColor) {
      vertexLine(matrices, buffer, start.toVector3f(), end.toVector3f(), startColor, endColor);
   }

   public static void vertexLine(@NotNull MatrixStack matrices, @NotNull VertexConsumer buffer, Vector3f start, Vector3f end, int startColor, int endColor) {
      matrices.push();
      Entry entry = matrices.peek();
      Vector3f vec = getNormal(start.x, start.y, start.z, end.x, end.y, end.z);
      buffer.vertex(entry, start).color(startColor).normal(entry, vec.x(), vec.y(), vec.z());
      buffer.vertex(entry, end).color(endColor).normal(entry, vec.x(), vec.y(), vec.z());
      matrices.pop();
   }

   @NotNull
   public static Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
      float xNormal = x2 - x1;
      float yNormal = y2 - y1;
      float zNormal = z2 - z1;
      float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);
      return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
   }

   public static void drawLine(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color, float width, boolean depth) {
      drawLine(new Vec3d(minX, minY, minZ), new Vec3d(maxX, maxY, maxZ), color, width, depth);
   }

   public static void drawLine(Vec3d start, Vec3d end, int color, float width, boolean depth) {
      drawLine(start, end, color, color, width, depth);
   }

   public static void drawLine(Vec3d start, Vec3d end, int colorStart, int colorEnd, float width, boolean depth) {
      Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getPos();
      DrawUtil.Line line = new DrawUtil.Line(start.subtract(cameraPos), end.subtract(cameraPos), colorStart, colorEnd, width);
      if (depth) {
         LINE_DEPTH.add(line);
      } else {
         LINE.add(line);
      }
   }

   public static void drawRound(float x, float y, float width, float height, float radius, int color) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color))
         .radius(new QuadRadiusState(radius))
         .smoothness(1.0F)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRound(float x, float y, float width, float height, float radius, int color, int color2) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color, color2, color2))
         .radius(new QuadRadiusState(radius))
         .smoothness(1.0F)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRound(float x, float y, float width, float height, float radius, int color, int color2, int color3, int color4) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color2, color3, color4))
         .radius(new QuadRadiusState(radius))
         .smoothness(1.0F)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRound(float x, float y, float width, float height, float radius, float smoothness, int color) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color))
         .radius(new QuadRadiusState(radius))
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRound(float x, float y, float width, float height, float radius, float smoothness, int color, int color2) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color, color2, color2))
         .radius(new QuadRadiusState(radius))
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRound(float x, float y, float width, float height, float radius, float smoothness, int color, int color2, int color3, int color4) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color2, color3, color4))
         .radius(new QuadRadiusState(radius))
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRound(float x, float y, float width, float height, Vector4f vector4f, int color) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .smoothness(1.0F)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRound(float x, float y, float width, float height, Vector4f vector4f, int color, int color2) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color, color2, color2))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .smoothness(1.0F)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRound(float x, float y, float width, float height, Vector4f vector4f, int color, int color2, int color3, int color4) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color2, color3, color4))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .smoothness(1.0F)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRound(float x, float y, float width, float height, Vector4f vector4f, float smoothness, int color) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRound(float x, float y, float width, float height, Vector4f vector4f, float smoothness, int color, int color2) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color, color2, color2))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRound(float x, float y, float width, float height, Vector4f vector4f, float smoothness, int color, int color2, int color3, int color4) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color2, color3, color4))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRoundBlur(float x, float y, float width, float height, float radius, int color, float blurIntensivity) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color))
         .radius(new QuadRadiusState(radius))
         .blurRadius(blurIntensivity)
         .smoothness(1.0F)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRoundBlur(float x, float y, float width, float height, float radius, int color, int color2, float blurIntensivity) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color, color2, color2))
         .radius(new QuadRadiusState(radius))
         .blurRadius(blurIntensivity)
         .smoothness(1.0F)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRoundBlur(
      float x, float y, float width, float height, float radius, int color, int color2, int color3, int color4, float blurIntensivity
   ) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color2, color3, color4))
         .radius(new QuadRadiusState(radius))
         .blurRadius(blurIntensivity)
         .smoothness(1.0F)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRoundBlur(float x, float y, float width, float height, float radius, int color, float smoothness, float blurIntensivity) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color))
         .radius(new QuadRadiusState(radius))
         .blurRadius(blurIntensivity)
         .smoothness(smoothness)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRoundBlur(float x, float y, float width, float height, float radius, int color, int color2, float smoothness, float blurIntensivity) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color, color2, color2))
         .radius(new QuadRadiusState(radius))
         .blurRadius(blurIntensivity)
         .smoothness(smoothness)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRoundBlur(
      float x, float y, float width, float height, float radius, int color, int color2, int color3, int color4, float smoothness, float blurIntensivity
   ) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color2, color3, color4))
         .radius(new QuadRadiusState(radius))
         .blurRadius(blurIntensivity)
         .smoothness(smoothness)
         .build();
      rectangle.render(x, y);
   }

   public static void drawRoundBlur(float x, float y, float width, float height, Vector4f vector4f, int color, float blurIntensivity) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .blurRadius(blurIntensivity)
         .smoothness(1.0F)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRoundBlur(float x, float y, float width, float height, Vector4f vector4f, int color, int color2, float blurIntensivity) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color, color2, color2))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .blurRadius(blurIntensivity)
         .smoothness(1.0F)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRoundBlur(
      float x, float y, float width, float height, Vector4f vector4f, int color, int color2, int color3, int color4, float blurIntensivity
   ) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color2, color3, color4))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .smoothness(1.0F)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRoundBlur(float x, float y, float width, float height, Vector4f vector4f, int color, float smoothness, float blurIntensivity) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .blurRadius(blurIntensivity)
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRoundBlur(
      float x, float y, float width, float height, Vector4f vector4f, int color, int color2, float smoothness, float blurIntensivity
   ) {
      BuiltBlur rectangle = Builder.blur()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color, color2, color2))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .blurRadius(blurIntensivity)
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawRoundBlur(
      float x, float y, float width, float height, Vector4f vector4f, int color, int color2, int color3, int color4, float smoothness, float blurIntensivity
   ) {
      BuiltRectangle rectangle = Builder.rectangle()
         .size(new SizeState(width + 0.5F, height + 0.25F))
         .color(new QuadColorState(color, color2, color3, color4))
         .radius(new QuadRadiusState(vector4f.x, vector4f.y, vector4f.z, vector4f.w))
         .smoothness(smoothness)
         .build();
      rectangle.render(x - 0.5F, y - 0.5F);
   }

   public static void drawText(MsdfFont font, String text, float x, float y, int color, float size) {
      MsdfRenderer.renderText(font, text, size, color, IRenderer.DEFAULT_MATRIX, x, y + 2.0F, 0.0F);
   }

   public static void drawText(MsdfFont font, String text, float x, float y, int color, float size, float fadeoutStart, float fadeoutEnd, float maxWidth) {
      MsdfRenderer.renderText(font, text, size, color, IRenderer.DEFAULT_MATRIX, x, y, 0.0F, true, fadeoutStart, fadeoutEnd, maxWidth);
   }

   public static void drawText(MsdfFont font, Text text, float x, float y, float size, int alpha) {
      MsdfRenderer.renderText(font, text, size, IRenderer.DEFAULT_MATRIX, x, y + 2.0F, 0.0F, alpha);
   }

   public static void drawColoredText(MsdfFont font, Text text, float x, float y, float size) {
      MsdfRenderer.renderText(font, text, size, IRenderer.DEFAULT_MATRIX, x, y + 2.0F, 0.0F);
   }

   public static void drawCircle(float centerX, float centerY, float radius, int color) {
      kH59vA(centerX - radius, centerY - radius, radius * 2.0F, 0.0F, 0.0F, (float) (Math.PI * 2), color);
   }

   public static void drawRingArc(float centerX, float centerY, float radius, float thickness, float startDeg, float endDeg, int color) {
      kH59vA(centerX - radius, centerY - radius, radius * 2.0F, thickness, (float)Math.toRadians(startDeg), (float)Math.toRadians(endDeg), color);
   }

   public static void drawBorderArc(float centerX, float centerY, float size, float cornerRadius, float thickness, float startDeg, float endDeg, int color) {
      if (!(size <= 0.0F)) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         ShaderProgram shader = RenderSystem.setShader(BORDER_ARC_SHADER_KEY);
         shader.getUniform("Size").set(size, size);
         shader.getUniform("Radius").set(cornerRadius, cornerRadius, cornerRadius, cornerRadius);
         shader.getUniform("Thickness").set(thickness);
         shader.getUniform("Smoothness").set(1.0F, 1.0F);
         shader.getUniform("StartAngle").set((float)Math.toRadians(startDeg));
         shader.getUniform("EndAngle").set((float)Math.toRadians(endDeg));
         float x = centerX - size / 2.0F;
         float y = centerY - size / 2.0F;
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x, y, 0.0F).color(color);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x, y + size, 0.0F).color(color);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x + size, y + size, 0.0F).color(color);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x + size, y, 0.0F).color(color);
         BufferRenderer.drawWithGlobalProgram(buffer.end());
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
      }
   }

   private static void kH59vA(float x, float y, float size, float thickness, float start, float end, int color) {
      if (!(size <= 0.0F)) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         ShaderProgram shader = RenderSystem.setShader(CIRCLE_SHADER_KEY);
         shader.getUniform("Size").set(size, size);
         shader.getUniform("Thickness").set(thickness);
         shader.getUniform("StartAngle").set(start);
         shader.getUniform("EndAngle").set(end);
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x, y, 0.0F).color(color);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x, y + size, 0.0F).color(color);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x + size, y + size, 0.0F).color(color);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x + size, y, 0.0F).color(color);
         BufferRenderer.drawWithGlobalProgram(buffer.end());
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
      }
   }

   public record Line(Vec3d start, Vec3d end, int colorStart, int colorEnd, float width) {



      

      

      

      

      
   }
}
