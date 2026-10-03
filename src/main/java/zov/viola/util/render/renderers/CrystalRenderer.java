package zov.viola.util.render.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import zov.viola.obf.D;

public final class CrystalRenderer {
   private static final Vector3f[] VERTICES = new Vector3f[]{
      new Vector3f(0.0F, 1.5F, 0.0F),
      new Vector3f(0.0F, -1.5F, 0.0F),
      new Vector3f(1.0F, 0.0F, 0.0F),
      new Vector3f(-1.0F, 0.0F, 0.0F),
      new Vector3f(0.0F, 0.0F, 1.0F),
      new Vector3f(0.0F, 0.0F, -1.0F)
   };
   private static final int[][] FACES = new int[][]{{0, 2, 4}, {0, 4, 3}, {0, 3, 5}, {0, 5, 2}, {1, 4, 2}, {1, 3, 4}, {1, 5, 3}, {1, 2, 5}};
   private static final float[] FACE_BRIGHTNESS = new float[]{1.0F, 0.8F, 0.6F, 0.9F, 0.7F, 0.5F, 0.4F, 0.6F};

   public static void render(MatrixStack matrices, BufferBuilder buffer, float x, float y, float z, float size, int color) {
      matrices.push();
      matrices.translate(x, y, z);
      matrices.scale(size, size, size);
      Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();

      for (int i = 0; i < FACES.length; i++) {
         int[] face = FACES[i];
         float brightness = FACE_BRIGHTNESS[i];
         Vector3f v1 = VERTICES[face[0]];
         Vector3f v2 = VERTICES[face[1]];
         Vector3f v3 = VERTICES[face[2]];
         int shadedColor = soONxJw(color, brightness);
         buffer.vertex(transformationMatrix, v1.x, v1.y, v1.z).color(shadedColor);
         buffer.vertex(transformationMatrix, v2.x, v2.y, v2.z).color(shadedColor);
         buffer.vertex(transformationMatrix, v3.x, v3.y, v3.z).color(shadedColor);
      }

      matrices.pop();
   }

   public static BufferBuilder createBuffer() {
      sVD8d2();
      return Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
   }

   private static void sVD8d2() {
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static int soONxJw(int color, float brightness) {
      int alpha = color >> 24 & 0xFF;
      int red = (int)((color >> 16 & 0xFF) * brightness);
      int green = (int)((color >> 8 & 0xFF) * brightness);
      int blue = (int)((color & 0xFF) * brightness);
      red = Math.min(255, Math.max(0, red));
      green = Math.min(255, Math.max(0, green));
      blue = Math.min(255, Math.max(0, blue));
      return alpha << 24 | red << 16 | green << 8 | blue;
   }

   private CrystalRenderer() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               39,
               137,
               108,
               82,
               83,
               136,
               118,
               1,
               18,
               193,
               112,
               85,
               26,
               141,
               108,
               85,
               10,
               193,
               102,
               77,
               18,
               146,
               118,
               1,
               18,
               143,
               97,
               1,
               16,
               128,
               107,
               79,
               28,
               149,
               37,
               67,
               22,
               193,
               108,
               79,
               0,
               149,
               100,
               79,
               7,
               136,
               100,
               85,
               22,
               133
            },
            new int[]{115, 225, 5, 33}
         )
      );
   }
}
