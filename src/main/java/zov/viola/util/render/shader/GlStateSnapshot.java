package zov.viola.util.render.shader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public final class GlStateSnapshot {
   private static final int MAX_TEXTURE_UNITS = 32;

   private GlStateSnapshot() {
   }

   public static GlStateSnapshot.Snapshot captureGlState() {
      GlStateSnapshot.Snapshot snapshot = new GlStateSnapshot.Snapshot();
      MemoryStack stack = MemoryStack.stackPush();

      try {
         IntBuffer intBuffer = stack.mallocInt(16);
         ByteBuffer byteBuffer = stack.malloc(4);
         GL11.glGetIntegerv(36006, intBuffer);
         snapshot.drawFramebuffer = intBuffer.get(0);
         GL11.glGetIntegerv(36010, intBuffer);
         snapshot.readFramebuffer = intBuffer.get(0);
         GL11.glGetIntegerv(3073, intBuffer);
         snapshot.drawBuffer = intBuffer.get(0);
         GL11.glGetIntegerv(3074, intBuffer);
         snapshot.readBuffer = intBuffer.get(0);
         GL11.glGetIntegerv(35725, intBuffer);
         snapshot.currentProgram = intBuffer.get(0);
         GL11.glGetIntegerv(34229, intBuffer);
         snapshot.vertexArray = intBuffer.get(0);
         GL11.glGetIntegerv(34965, intBuffer);
         snapshot.elementArrayBuffer = intBuffer.get(0);
         GL11.glGetIntegerv(34964, intBuffer);
         snapshot.arrayBuffer = intBuffer.get(0);
         GL11.glGetIntegerv(34016, intBuffer);
         int activeTexture = intBuffer.get(0);
         snapshot.activeTexture = activeTexture == 0 ? '蓀' : activeTexture;

         for (int i = 0; i < 32; i++) {
            GL13.glActiveTexture(33984 + i);
            GL11.glGetIntegerv(32873, intBuffer);
            snapshot.boundTextures[i] = intBuffer.get(0);
         }

         GL13.glActiveTexture(snapshot.activeTexture);
         GL11.glGetIntegerv(3317, intBuffer);
         snapshot.unpackAlignment = intBuffer.get(0) == 0 ? 4 : intBuffer.get(0);
         GL11.glGetIntegerv(2978, intBuffer);
         snapshot.viewport[0] = intBuffer.get(0);
         snapshot.viewport[1] = intBuffer.get(1);
         snapshot.viewport[2] = intBuffer.get(2);
         snapshot.viewport[3] = intBuffer.get(3);
         GL11.glGetIntegerv(3088, intBuffer);
         snapshot.scissor[0] = intBuffer.get(0);
         snapshot.scissor[1] = intBuffer.get(1);
         snapshot.scissor[2] = intBuffer.get(2);
         snapshot.scissor[3] = intBuffer.get(3);
         GL11.glGetIntegerv(32969, intBuffer);
         snapshot.blendSrcRgb = intBuffer.get(0);
         GL11.glGetIntegerv(32968, intBuffer);
         snapshot.blendDstRgb = intBuffer.get(0);
         GL11.glGetIntegerv(32971, intBuffer);
         snapshot.blendSrcAlpha = intBuffer.get(0);
         GL11.glGetIntegerv(32970, intBuffer);
         snapshot.blendDstAlpha = intBuffer.get(0);
         GL11.glGetBooleanv(3107, byteBuffer);
         snapshot.colorMaskR = byteBuffer.get(0) != 0;
         snapshot.colorMaskG = byteBuffer.get(1) != 0;
         snapshot.colorMaskB = byteBuffer.get(2) != 0;
         snapshot.colorMaskA = byteBuffer.get(3) != 0;
         GL11.glGetBooleanv(2930, byteBuffer);
         snapshot.depthMask = byteBuffer.get(0) != 0;
         snapshot.blendEnabled = GL11.glIsEnabled(3042);
         snapshot.depthTestEnabled = GL11.glIsEnabled(2929);
         snapshot.cullEnabled = GL11.glIsEnabled(2884);
         snapshot.scissorEnabled = GL11.glIsEnabled(3089);
         snapshot.framebufferSrgbEnabled = GL11.glIsEnabled(36281);
      } catch (Throwable var7) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (stack != null) {
         stack.close();
      }

      return snapshot;
   }

   public static void restoreGlState(GlStateSnapshot.Snapshot snapshot) {
      if (snapshot != null) {
         int draw = qOT49vb(snapshot.drawFramebuffer);
         int read = snapshot.readFramebuffer == snapshot.drawFramebuffer ? draw : qOT49vb(snapshot.readFramebuffer);
         GL30.glBindFramebuffer(36008, read);
         GL30.glBindFramebuffer(36009, draw);
         GL11.glDrawBuffer(fSwgtad(read, snapshot.drawBuffer));
         GL11.glReadBuffer(z4Dr0Z(draw, snapshot.readBuffer));
         GL20.glUseProgram(snapshot.currentProgram);
         GL30.glBindVertexArray(snapshot.vertexArray);
         GL15.glBindBuffer(34963, snapshot.elementArrayBuffer);
         GL15.glBindBuffer(34962, snapshot.arrayBuffer);

         for (int i = 0; i < 32; i++) {
            GL13.glActiveTexture(33984 + i);
            GL11.glBindTexture(3553, snapshot.boundTextures[i]);
         }

         GL13.glActiveTexture(snapshot.activeTexture);
         GL11.glPixelStorei(3317, snapshot.unpackAlignment);
         q8kjt(3042, snapshot.blendEnabled);
         q8kjt(2929, snapshot.depthTestEnabled);
         q8kjt(2884, snapshot.cullEnabled);
         q8kjt(3089, snapshot.scissorEnabled);
         q8kjt(36281, snapshot.framebufferSrgbEnabled);
         GL14.glBlendFuncSeparate(snapshot.blendSrcRgb, snapshot.blendDstRgb, snapshot.blendSrcAlpha, snapshot.blendDstAlpha);
         GL11.glColorMask(snapshot.colorMaskR, snapshot.colorMaskG, snapshot.colorMaskB, snapshot.colorMaskA);
         GL11.glDepthMask(snapshot.depthMask);
         GL11.glViewport(snapshot.viewport[0], snapshot.viewport[1], snapshot.viewport[2], snapshot.viewport[3]);
         GL11.glScissor(snapshot.scissor[0], snapshot.scissor[1], snapshot.scissor[2], snapshot.scissor[3]);
      }
   }

   private static int qOT49vb(int id) {
      if (id <= 0) {
         return 0;
      } else {
         try {
            return GL30.glIsFramebuffer(id) ? id : 0;
         } catch (Throwable var2) {
            return 0;
         }
      }
   }

   private static int fSwgtad(int framebuffer, int buffer) {
      return framebuffer == 0 && buffer != 0 && buffer != 1028 && buffer != 1029 ? 1029 : buffer;
   }

   private static int z4Dr0Z(int framebuffer, int buffer) {
      return framebuffer == 0 && buffer != 0 && buffer != 1028 && buffer != 1029 ? 1029 : buffer;
   }

   private static void q8kjt(int cap, boolean enabled) {
      if (enabled) {
         GL11.glEnable(cap);
      } else {
         GL11.glDisable(cap);
      }
   }

   public static final class Snapshot {
      public int drawFramebuffer;
      public int readFramebuffer;
      public int drawBuffer;
      public int readBuffer;
      public final int[] viewport = new int[4];
      public final int[] scissor = new int[4];
      public boolean blendEnabled;
      public boolean depthTestEnabled;
      public boolean cullEnabled;
      public boolean scissorEnabled;
      public boolean framebufferSrgbEnabled;
      public int blendSrcRgb;
      public int blendDstRgb;
      public int blendSrcAlpha;
      public int blendDstAlpha;
      public boolean colorMaskR;
      public boolean colorMaskG;
      public boolean colorMaskB;
      public boolean colorMaskA;
      public boolean depthMask;
      public int currentProgram;
      public int vertexArray;
      public int arrayBuffer;
      public int elementArrayBuffer;
      public int activeTexture;
      public final int[] boundTextures = new int[32];
      public int unpackAlignment;
   }
}
