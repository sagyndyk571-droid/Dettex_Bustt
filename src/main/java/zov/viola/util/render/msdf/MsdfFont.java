package zov.viola.util.render.msdf;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import zov.viola.util.parse.ParseTextUtil;
import zov.viola.util.render.providers.ResourceProvider;

public final class MsdfFont {
   private final String vpzs;
   private final Identifier t4gfbA;
   private AbstractTexture y92qKzV;
   private int q1Irg5p = -1;
   private final FontData.AtlasData fWvSo0;
   private final FontData.MetricsData okjHX;
   private final Map<Integer, MsdfGlyph> acQylOb;
   private final Map<Integer, Map<Integer, Float>> yuyK3c3;

   private MsdfFont(
      String name,
      Identifier atlasIdentifier,
      AbstractTexture texture,
      FontData.AtlasData atlas,
      FontData.MetricsData metrics,
      Map<Integer, MsdfGlyph> glyphs,
      Map<Integer, Map<Integer, Float>> kernings
   ) {
      this.vpzs = name;
      this.t4gfbA = atlasIdentifier;
      this.y92qKzV = texture;
      this.fWvSo0 = atlas;
      this.okjHX = metrics;
      this.acQylOb = glyphs;
      this.yuyK3c3 = kernings;
   }

   public int getTextureId() {
      AbstractTexture current = MinecraftClient.getInstance().getTextureManager().getTexture(this.t4gfbA);
      if (current != null) {
         this.y92qKzV = current;
      }

      int glId = this.y92qKzV.getGlId();
      if (glId > 0 && glId != this.q1Irg5p) {
         this.q1Irg5p = glId;
         RenderSystem.recordRenderCall(() -> this.y92qKzV.setFilter(true, false));
      }

      return glId;
   }

   public void applyGlyphs(
      Matrix4f matrix, VertexConsumer consumer, String text, float size, float thickness, float spacing, float x, float y, float z, int color
   ) {
      int prevChar = -1;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == 167 && i + 1 < text.length()) {
            i++;
         } else {
            MsdfGlyph glyph = this.acQylOb.get(Integer.valueOf(c));
            if (glyph != null) {
               Map<Integer, Float> kerning = this.yuyK3c3.get(prevChar);
               if (kerning != null) {
                  x += kerning.getOrDefault(Integer.valueOf(c), 0.0F) * size;
               }

               x += glyph.apply(matrix, consumer, size, x, y, z, color) + thickness + spacing;
               prevChar = c;
            }
         }
      }
   }

   public void applyGlyphs(
      Matrix4f matrix, VertexConsumer consumer, List<MsdfFont.ColoredGlyph> glyphs, float size, float thickness, float spacing, float x, float y, float z
   ) {
      int prevChar = -1;

      for (int i = 0; i < glyphs.size(); i++) {
         MsdfFont.ColoredGlyph glyphData = glyphs.get(i);
         int _char = glyphData.c();
         int color = glyphData.color();
         MsdfGlyph glyph = this.acQylOb.get(_char);
         if (glyph != null) {
            Map<Integer, Float> kerning = this.yuyK3c3.get(prevChar);
            if (kerning != null) {
               x += kerning.getOrDefault(_char, 0.0F) * size;
            }

            x += glyph.apply(matrix, consumer, size, x, y, z, color) + thickness + spacing;
            prevChar = _char;
         }
      }
   }

   public float getWidth(Text text, float size) {
      List<MsdfFont.ColoredGlyph> glyphs = ParseTextUtil.parseTextToColoredGlyphs(text);
      int prevChar = -1;
      float width = 0.0F;

      for (int i = 0; i < glyphs.size(); i++) {
         int _char = glyphs.get(i).c();
         MsdfGlyph glyph = this.acQylOb.get(_char);
         if (glyph != null) {
            Map<Integer, Float> kerning = this.yuyK3c3.get(prevChar);
            if (kerning != null) {
               width += kerning.getOrDefault(_char, 0.0F) * size;
            }

            width += glyph.getWidth(size);
            prevChar = _char;
         }
      }

      return width;
   }

   public float getWidth(String text, float size) {
      int prevChar = -1;
      float width = 0.0F;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == 167 && i + 1 < text.length()) {
            i++;
         } else {
            MsdfGlyph glyph = this.acQylOb.get(Integer.valueOf(c));
            if (glyph != null) {
               Map<Integer, Float> kerning = this.yuyK3c3.get(prevChar);
               if (kerning != null) {
                  width += kerning.getOrDefault(Integer.valueOf(c), 0.0F) * size;
               }

               width += glyph.getWidth(size);
               prevChar = c;
            }
         }
      }

      return width;
   }

   public MsdfGlyph getGlyph(char c) {
      return this.acQylOb.get(Integer.valueOf(c));
   }

   public String getName() {
      return this.vpzs;
   }

   public FontData.AtlasData getAtlas() {
      return this.fWvSo0;
   }

   public FontData.MetricsData getMetrics() {
      return this.okjHX;
   }

   public static MsdfFont.Builder builder() {
      return new MsdfFont.Builder();
   }

   public static class Builder {
      private String dcYHR = "?";
      private Identifier nx3u;
      private Identifier qswtmw;

      private Builder() {
      }

      public MsdfFont.Builder name(String name) {
         this.dcYHR = name;
         return this;
      }

      public MsdfFont.Builder data(String dataFileName) {
         this.nx3u = Identifier.of("mre", "fonts/" + dataFileName + ".json");
         return this;
      }

      public MsdfFont.Builder atlas(String atlasFileName) {
         this.qswtmw = Identifier.of("mre", "fonts/" + atlasFileName + ".png");
         return this;
      }

      public MsdfFont build() {
         FontData data = ResourceProvider.fromJsonToInstance(this.nx3u, FontData.class);
         AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(this.qswtmw);
         if (data == null) {
            throw new RuntimeException(
               "Failed to read font data file: " + this.nx3u.toString() + "; Are you sure this is json file? Try to check the correctness of its syntax."
            );
         } else {
            RenderSystem.recordRenderCall(() -> texture.setFilter(true, false));
            float aWidth = data.atlas().width();
            float aHeight = data.atlas().height();
            Map<Integer, MsdfGlyph> glyphs = data.glyphs()
               .stream()
               .collect(Collectors.toMap(glyphData -> glyphData.unicode(), glyphData -> new MsdfGlyph(glyphData, aWidth, aHeight)));
            Map<Integer, Map<Integer, Float>> kernings = new HashMap<>();
            data.kernings().forEach(kerning -> {
               Map<Integer, Float> map = kernings.get(kerning.leftChar());
               if (map == null) {
                  map = new HashMap<>();
                  kernings.put(kerning.leftChar(), map);
               }

               map.put(kerning.rightChar(), kerning.advance());
            });
            return new MsdfFont(this.dcYHR, this.qswtmw, texture, data.atlas(), data.metrics(), glyphs, kernings);
         }
      }
   }

   public record ColoredGlyph(char c, int color) {

      

      

      
   }
}
