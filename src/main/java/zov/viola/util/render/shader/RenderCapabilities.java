package zov.viola.util.render.shader;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public final class RenderCapabilities {
   private RenderCapabilities() {
   }

   public static void texImage2D(int internalFormat, int width, int height, int format, int type) {
      int pbo = GL11.glGetInteger(35055);
      if (pbo != 0) {
         GL15.glBindBuffer(35052, 0);
      }

      try {
         GL11.glTexImage2D(3553, 0, internalFormat, width, height, 0, format, type, (ByteBuffer)null);
      } finally {
         if (pbo != 0) {
            GL15.glBindBuffer(35052, pbo);
         }
      }
   }
}
