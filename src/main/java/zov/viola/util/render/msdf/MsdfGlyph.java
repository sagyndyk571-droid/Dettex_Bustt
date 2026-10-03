package zov.viola.util.render.msdf;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class MsdfGlyph {
   private final int cgdU;
   private final float uFra;
   private final float o2R7o;
   private final float eqpy8;
   private final float kKlHc;
   private final float hSl8;
   private final float jJyJDBb;
   private final float otx9uO;
   private final float pWAeh;

   public MsdfGlyph(FontData.GlyphData data, float atlasWidth, float atlasHeight) {
      this.cgdU = data.unicode();
      this.hSl8 = data.advance();
      FontData.BoundsData atlasBounds = data.atlasBounds();
      if (atlasBounds != null) {
         this.uFra = atlasBounds.left() / atlasWidth;
         this.o2R7o = atlasBounds.right() / atlasWidth;
         this.eqpy8 = 1.0F - atlasBounds.top() / atlasHeight;
         this.kKlHc = 1.0F - atlasBounds.bottom() / atlasHeight;
      } else {
         this.uFra = this.o2R7o = this.eqpy8 = this.kKlHc = 0.0F;
      }

      FontData.BoundsData planeBounds = data.planeBounds();
      if (planeBounds != null) {
         this.otx9uO = planeBounds.right() - planeBounds.left();
         this.pWAeh = planeBounds.top() - planeBounds.bottom();
         this.jJyJDBb = planeBounds.top();
      } else {
         this.otx9uO = this.pWAeh = this.jJyJDBb = 0.0F;
      }
   }

   public float apply(Matrix4f matrix, VertexConsumer consumer, float size, float x, float y, float z, int color) {
      y -= this.jJyJDBb * size;
      float width = this.otx9uO * size;
      float height = this.pWAeh * size;
      consumer.vertex(matrix, x, y, z).texture(this.uFra, this.eqpy8).color(color);
      consumer.vertex(matrix, x, y + height, z).texture(this.uFra, this.kKlHc).color(color);
      consumer.vertex(matrix, x + width, y + height, z).texture(this.o2R7o, this.kKlHc).color(color);
      consumer.vertex(matrix, x + width, y, z).texture(this.o2R7o, this.eqpy8).color(color);
      return this.hSl8 * size;
   }

   public float getWidth(float size) {
      return this.hSl8 * size;
   }

   public float getVisualWidth(float size) {
      return this.otx9uO * size;
   }

   public float getVisualHeight(float size) {
      return this.pWAeh * size;
   }

   public float getTopPosition() {
      return this.jJyJDBb;
   }

   public int getCharCode() {
      return this.cgdU;
   }
}
