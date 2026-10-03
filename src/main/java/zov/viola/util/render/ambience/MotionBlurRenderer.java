package zov.viola.util.render.ambience;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import zov.viola.module.list.render.MotionBlur;
import zov.viola.util.render.providers.ResourceProvider;

public final class MotionBlurRenderer {
   private static final MinecraftClient MC = MinecraftClient.getInstance();
   private static final ShaderProgramKey MOTION_BLUR_KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("motion_blur"), VertexFormats.POSITION_COLOR, Defines.EMPTY
   );
   private static SimpleFramebuffer j3sbMv;
   private static boolean r3izW21;
   private static int yh3FIa8 = -1;
   private static int aL788c = -1;
   private static int yemb = -1;
   private static int oXPlom5 = -1;

   private MotionBlurRenderer() {
   }

   public static void apply(MotionBlur module) {
      if (module != null && module.isEnabled() && MC.world != null && MC.getFramebuffer() != null) {
         float strength = module.getStrength();
         if (strength <= 0.0F) {
            reset();
         } else {
            Framebuffer main = MC.getFramebuffer();
            int w = Math.max(1, main.textureWidth);
            int h = Math.max(1, main.textureHeight);
            int pw = Math.max(1, w / 2);
            int ph = Math.max(1, h / 2);
            p7U2d9(w, h, pw, ph);
            if (j3sbMv != null) {
               if (!r3izW21) {
                  lJyxck(main.fbo, j3sbMv.fbo, w, h, pw, ph);
                  r3izW21 = true;
               } else {
                  eu7hV(main, strength, w, h);
                  lJyxck(main.fbo, j3sbMv.fbo, w, h, pw, ph);
               }
            }
         }
      } else {
         reset();
      }
   }

   private static void eu7hV(Framebuffer main, float strength, int w, int h) {
      int readFbo = GL11.glGetInteger(36010);
      int drawFbo = GL11.glGetInteger(36006);

      try {
         GL30.glBindFramebuffer(36160, main.fbo);
         RenderSystem.viewport(0, 0, w, h);
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(770, 771, 1, 771);
         RenderSystem.setShaderTexture(0, j3sbMv.getColorAttachment());
         ShaderProgram shader = RenderSystem.setShader(MOTION_BLUR_KEY);
         if (shader != null) {
            if (shader.getUniform("u_Strength") != null) {
               shader.getUniform("u_Strength").set(strength);
            }

            BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buffer.vertex(-1.0F, 1.0F, 0.0F).color(-1);
            buffer.vertex(-1.0F, -1.0F, 0.0F).color(-1);
            buffer.vertex(1.0F, -1.0F, 0.0F).color(-1);
            buffer.vertex(1.0F, 1.0F, 0.0F).color(-1);
            BufferRenderer.drawWithGlobalProgram(buffer.end());
            return;
         }
      } finally {
         RenderSystem.setShaderTexture(0, 0);
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         GL30.glBindFramebuffer(36008, readFbo);
         GL30.glBindFramebuffer(36009, drawFbo);
         RenderSystem.viewport(0, 0, w, h);
      }
   }

   private static void lJyxck(int srcFbo, int dstFbo, int w, int h, int pw, int ph) {
      int readFbo = GL11.glGetInteger(36010);
      int drawFbo = GL11.glGetInteger(36006);
      GL30.glBindFramebuffer(36008, srcFbo);
      GL30.glBindFramebuffer(36009, dstFbo);
      GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, pw, ph, 16384, 9729);
      GL30.glBindFramebuffer(36008, readFbo);
      GL30.glBindFramebuffer(36009, drawFbo);
   }

   private static void p7U2d9(int w, int h, int pw, int ph) {
      if (j3sbMv == null || yh3FIa8 != w || aL788c != h || yemb != pw || oXPlom5 != ph) {
         if (j3sbMv != null) {
            j3sbMv.delete();
         }

         j3sbMv = new SimpleFramebuffer(pw, ph, false);
         j3sbMv.setClearColor(0.0F, 0.0F, 0.0F, 1.0F);
         j3sbMv.clear();
         yh3FIa8 = w;
         aL788c = h;
         yemb = pw;
         oXPlom5 = ph;
         r3izW21 = false;
      }
   }

   public static void reset() {
      if (j3sbMv != null) {
         j3sbMv.delete();
         j3sbMv = null;
      }

      yh3FIa8 = -1;
      aL788c = -1;
      yemb = -1;
      oXPlom5 = -1;
      r3izW21 = false;
   }
}
