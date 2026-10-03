package zov.viola.util.render.stencil;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import zov.viola.util.IMinecraft;

public class StencilUtil implements IMinecraft {
   public static void checkSetupFBO(Framebuffer fbo) {
      if (fbo != null && fbo.useDepthAttachment) {
         int depthId = fbo.getDepthAttachment();
         GL11.glBindTexture(3553, depthId);
         int format = GL11.glGetTexLevelParameteri(3553, 0, 4099);
         if (format != 35056) {
            GL11.glTexImage2D(3553, 0, 35056, fbo.textureWidth, fbo.textureHeight, 0, 34041, 34042, (ByteBuffer)null);
            GL30.glBindFramebuffer(36160, fbo.fbo);
            GL30.glFramebufferTexture2D(36160, 33306, 3553, depthId, 0);
            fbo.beginWrite(false);
         }
      }
   }

   public static void push() {
      checkSetupFBO(mc.getFramebuffer());
      GL11.glEnable(2960);
      GL11.glClearStencil(0);
      GL11.glClear(1024);
      GL11.glStencilFunc(519, 1, 255);
      GL11.glStencilOp(7681, 7681, 7681);
      RenderSystem.colorMask(false, false, false, false);
   }

   public static void read(int ref) {
      RenderSystem.colorMask(true, true, true, true);
      GL11.glStencilFunc(514, ref, 255);
      GL11.glStencilOp(7680, 7680, 7680);
   }

   public static void pop() {
      GL11.glDisable(2960);
   }
}
