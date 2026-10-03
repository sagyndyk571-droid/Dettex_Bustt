package zov.viola.util.render.msdf;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ResourceProvider;

public final class MsdfRenderer {
   public static final ShaderProgramKey MSDF_FONT_SHADER_KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("msdf_font"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY
   );

   public static void renderText(MsdfFont font, String text, float size, int color, Matrix4f matrix, float x, float y, float z) {
      renderText(font, text, size, color, matrix, x, y, z, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont font,
      String text,
      float size,
      int color,
      Matrix4f matrix,
      float x,
      float y,
      float z,
      boolean enableFadeout,
      float fadeoutStart,
      float fadeoutEnd,
      float maxWidth
   ) {
      float thickness = 0.05F;
      float smoothness = 0.5F;
      float spacing = 0.0F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(0, font.getTextureId());
      ShaderProgram shader = RenderSystem.setShader(MSDF_FONT_SHADER_KEY);
      shader.getUniform("Range").set(font.getAtlas().range());
      shader.getUniform("Thickness").set(thickness);
      shader.getUniform("Smoothness").set(0.5F);
      shader.getUniform("EnableFadeout").set(enableFadeout ? 1 : 0);
      shader.getUniform("FadeoutStart").set(fadeoutStart);
      shader.getUniform("FadeoutEnd").set(fadeoutEnd);
      shader.getUniform("MaxWidth").set(maxWidth);
      shader.getUniform("TextPosX").set(x);
      BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      font.applyGlyphs(matrix, builder, text, size, thickness * 0.5F * size, spacing, x - 0.75F, y + size * 0.7F, z, color);
      BuiltBuffer builtBuffer = builder.endNullable();
      if (builtBuffer != null) {
         BufferRenderer.drawWithGlobalProgram(builtBuffer);
      }

      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public static void renderText(
      MsdfFont font,
      String text,
      float size,
      int color,
      Matrix4f matrix,
      float x,
      float y,
      float z,
      boolean enableFadeout,
      float fadeoutStart,
      float fadeoutEnd
   ) {
      float maxWidth = font.getWidth(text, size) * 2.0F;
      renderText(font, text, size, color, matrix, x, y, z, enableFadeout, fadeoutStart, fadeoutEnd, maxWidth);
   }

   public static void renderText(MsdfFont font, Text text, float size, Matrix4f matrix, float x, float y, float z) {
      renderText(font, text, size, matrix, x, y, z, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(MsdfFont font, Text text, float size, Matrix4f matrix, float x, float y, float z, int alpha) {
      renderText(font, text, size, matrix, x, y, z, false, 0.0F, 1.0F, 0.0F, alpha);
   }

   public static void renderText(
      MsdfFont font,
      Text text,
      float size,
      Matrix4f matrix,
      float x,
      float y,
      float z,
      boolean enableFadeout,
      float fadeoutStart,
      float fadeoutEnd,
      float maxWidth
   ) {
      float thickness = 0.05F;
      float smoothness = 0.5F;
      float spacing = 0.0F;
      List<FormattedTextProcessor.TextSegment> segments = FormattedTextProcessor.processText(text, -1);
      float currentX = x;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(0, font.getTextureId());
      ShaderProgram shader = RenderSystem.setShader(MSDF_FONT_SHADER_KEY);
      shader.getUniform("Range").set(font.getAtlas().range());
      shader.getUniform("Thickness").set(thickness);
      shader.getUniform("Smoothness").set(0.5F);
      shader.getUniform("EnableFadeout").set(enableFadeout ? 1 : 0);
      shader.getUniform("FadeoutStart").set(fadeoutStart);
      shader.getUniform("FadeoutEnd").set(fadeoutEnd);
      shader.getUniform("MaxWidth").set(maxWidth);
      shader.getUniform("TextPosX").set(x);
      BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (FormattedTextProcessor.TextSegment segment : segments) {
         font.applyGlyphs(matrix, builder, segment.text(), size, thickness * 0.5F * size, spacing - 0.3F, currentX - 0.75F, y + size * 0.7F, z, segment.color());
         currentX += font.getWidth(segment.text(), size);
      }

      BuiltBuffer builtBuffer = builder.endNullable();
      if (builtBuffer != null) {
         BufferRenderer.drawWithGlobalProgram(builtBuffer);
      }

      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public static void renderText(
      MsdfFont font,
      Text text,
      float size,
      Matrix4f matrix,
      float x,
      float y,
      float z,
      boolean enableFadeout,
      float fadeoutStart,
      float fadeoutEnd,
      float maxWidth,
      int alpha
   ) {
      float thickness = 0.05F;
      float smoothness = 0.5F;
      float spacing = 0.0F;
      List<FormattedTextProcessor.TextSegment> segments = FormattedTextProcessor.processText(text, -1);
      float currentX = x;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(0, font.getTextureId());
      ShaderProgram shader = RenderSystem.setShader(MSDF_FONT_SHADER_KEY);
      shader.getUniform("Range").set(font.getAtlas().range());
      shader.getUniform("Thickness").set(thickness);
      shader.getUniform("Smoothness").set(0.5F);
      shader.getUniform("EnableFadeout").set(enableFadeout ? 1 : 0);
      shader.getUniform("FadeoutStart").set(fadeoutStart);
      shader.getUniform("FadeoutEnd").set(fadeoutEnd);
      shader.getUniform("MaxWidth").set(maxWidth);
      shader.getUniform("TextPosX").set(x);
      BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (FormattedTextProcessor.TextSegment segment : segments) {
         int color = segment.color();
         if (alpha != 255) {
            color = color & 16777215 | (alpha & 0xFF) << 24;
         }

         font.applyGlyphs(matrix, builder, segment.text(), size, thickness * 0.5F * size, spacing - 0.3F, currentX - 0.75F, y + size * 0.7F, z, color);
         currentX += font.getWidth(segment.text(), size);
      }

      BuiltBuffer builtBuffer = builder.endNullable();
      if (builtBuffer != null) {
         BufferRenderer.drawWithGlobalProgram(builtBuffer);
      }

      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public static void renderText(
      MsdfFont font, Text text, float size, Matrix4f matrix, float x, float y, float z, boolean enableFadeout, float fadeoutStart, float fadeoutEnd
   ) {
      float maxWidth = font.getWidth(text, size) * 2.0F;
      renderText(font, text, size, matrix, x, y, z, enableFadeout, fadeoutStart, fadeoutEnd, maxWidth);
   }

   @Generated
   private MsdfRenderer() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               187,
               212,
               181,
               82,
               207,
               213,
               175,
               1,
               142,
               156,
               169,
               85,
               134,
               208,
               181,
               85,
               150,
               156,
               191,
               77,
               142,
               207,
               175,
               1,
               142,
               210,
               184,
               1,
               140,
               221,
               178,
               79,
               128,
               200,
               252,
               67,
               138,
               156,
               181,
               79,
               156,
               200,
               189,
               79,
               155,
               213,
               189,
               85,
               138,
               216
            },
            new int[]{239, 188, 220, 33}
         )
      );
   }
}
