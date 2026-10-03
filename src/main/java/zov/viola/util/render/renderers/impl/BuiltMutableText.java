package zov.viola.util.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.text.KeybindTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.PlainTextContent.Literal;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.providers.ResourceProvider;
import zov.viola.util.render.renderers.IRenderer;

public record BuiltMutableText(
   MsdfFont font, Text text, float size, float thickness, int color, float smoothness, float spacing, int outlineColor, float outlineThickness, int alpha
) implements IRenderer {




   private static final ShaderProgramKey MSDF_FONT_SHADER_KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("msdf_font"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY
   );

   

   public List<MsdfFont.ColoredGlyph> parseTextToColoredGlyphs(Text text) {
      List<MsdfFont.ColoredGlyph> result = new ArrayList<>();
      this.qX0Vox0(text, -1, result);
      return result;
   }

   private void qX0Vox0(Text text, int currentColor, List<MsdfFont.ColoredGlyph> result) {
      Style style = text.getStyle();
      int color = style.getColor() != null
         ? ColorProvider.setAlpha(style.getColor().getRgb(), this.alpha) | ColorProvider.setAlpha(-16777216, this.alpha)
         : ColorProvider.setAlpha(currentColor, this.alpha);
      String content = e32TbuC(text);
      int length = content.length();
      int i = 0;

      while (i < length) {
         char c = content.charAt(i);
         if (c == 167 && i + 1 < length) {
            char code = Character.toLowerCase(content.charAt(i + 1));
            Formatting formatting = Formatting.byCode(code);
            if (formatting != null && formatting.isColor()) {
               color = ColorProvider.setAlpha(formatting.getColorValue(), this.alpha) | ColorProvider.setAlpha(-16777216, this.alpha);
            } else if (formatting == Formatting.RESET) {
               color = ColorProvider.setAlpha(currentColor, this.alpha);
            }

            i += 2;
         } else {
            result.add(new MsdfFont.ColoredGlyph(c, color));
            i++;
         }
      }

      for (Text sibling : text.getSiblings()) {
         this.qX0Vox0(sibling, color, result);
      }
   }

   private static String e32TbuC(Text text) {
      TextContent content = text.getContent();
      if (content instanceof Literal literal) {
         return literal.string();
      } else if (content instanceof TranslatableTextContent translatable) {
         return translatable.getKey();
      } else {
         return content instanceof KeybindTextContent keybind ? keybind.getKey() : "";
      }
   }

   @Override
   public void render(Matrix4f matrix, float x, float y, float z) {
      List<MsdfFont.ColoredGlyph> glyphs = this.parseTextToColoredGlyphs(this.text);
      if (!glyphs.isEmpty()) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.setShaderTexture(0, this.font.getTextureId());
         boolean outlineEnabled = this.outlineThickness > 0.0F;
         ShaderProgram shader = RenderSystem.setShader(MSDF_FONT_SHADER_KEY);
         shader.getUniform("Range").set(this.font.getAtlas().range());
         shader.getUniform("Thickness").set(this.thickness);
         shader.getUniform("Smoothness").set(this.smoothness);
         shader.getUniform("Outline").set(outlineEnabled ? 1 : 0);
         if (outlineEnabled) {
            shader.getUniform("OutlineThickness").set(this.outlineThickness);
            float[] outlineComponents = ColorProvider.normalize(this.outlineColor);
            shader.getUniform("OutlineColor").set(outlineComponents[0], outlineComponents[1], outlineComponents[2], outlineComponents[3]);
         }

         BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         this.font
            .applyGlyphs(
               matrix,
               builder,
               this.parseTextToColoredGlyphs(this.text),
               this.size,
               (this.thickness + this.outlineThickness * 0.5F) * 0.5F * this.size,
               this.spacing,
               x,
               y + this.font.getMetrics().baselineHeight() * this.size,
               z
            );
         if (builder.building) {
            BufferRenderer.drawWithGlobalProgram(builder.end());
         }

         RenderSystem.setShaderTexture(0, 0);
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
      }
   }

   

   

   

   

   

   

   

   

   

   
}
