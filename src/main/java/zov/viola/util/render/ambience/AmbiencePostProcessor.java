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
import zov.viola.module.list.render.Ambience;
import zov.viola.util.render.providers.ResourceProvider;

public final class AmbiencePostProcessor {
   private static final MinecraftClient MC = MinecraftClient.getInstance();
   private static final ShaderProgramKey SATURATION_KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("saturation"), VertexFormats.POSITION_COLOR, Defines.EMPTY
   );
   private static Framebuffer goyIzw;
   private static int h2967lv = -1;
   private static int ceYLk5 = -1;

   private AmbiencePostProcessor() {
   }

   public static void apply(Ambience module) {
      if (module != null && module.isEnabled() && module.worldSaturation.getValue()) {
         if (MC.world != null && MC.getFramebuffer() != null) {
            int w = Math.max(1, MC.getWindow().getFramebufferWidth());
            int h = Math.max(1, MC.getWindow().getFramebufferHeight());
            xrs9v(w, h);
            if (goyIzw != null) {
               int readFbo = GL11.glGetInteger(36010);
               int drawFbo = GL11.glGetInteger(36006);
               int sourceFbo = readFbo != 0 ? readFbo : drawFbo;
               GL30.glBindFramebuffer(36008, sourceFbo);
               GL30.glBindFramebuffer(36009, goyIzw.fbo);
               GL30.glBlitFramebuffer(0, 0, w, h, 0, 0, w, h, 16384, 9728);
               GL30.glBindFramebuffer(36008, readFbo);
               GL30.glBindFramebuffer(36009, drawFbo);

               try {
                  GL30.glBindFramebuffer(36160, drawFbo);
                  RenderSystem.viewport(0, 0, w, h);
                  RenderSystem.disableDepthTest();
                  RenderSystem.depthMask(false);
                  RenderSystem.disableBlend();
                  RenderSystem.setShaderTexture(0, goyIzw.getColorAttachment());
                  ShaderProgram shader = RenderSystem.setShader(SATURATION_KEY);
                  if (shader != null) {
                     if (shader.getUniform("u_Saturation") != null) {
                        shader.getUniform("u_Saturation").set(module.saturationValue.getFloatValue());
                     }

                     if (shader.getUniform("u_Resolution") != null) {
                        shader.getUniform("u_Resolution")
                           .set((float)Math.max(1, MC.getWindow().getScaledWidth()), (float)Math.max(1, MC.getWindow().getScaledHeight()));
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
                  GL30.glBindFramebuffer(36008, readFbo);
                  GL30.glBindFramebuffer(36009, drawFbo);
                  RenderSystem.viewport(0, 0, w, h);
               }
            }
         }
      }
   }

   private static void xrs9v(int w, int h) {
      if (goyIzw == null || h2967lv != w || ceYLk5 != h) {
         if (goyIzw != null) {
            goyIzw.delete();
         }

         goyIzw = new SimpleFramebuffer(w, h, false);
         goyIzw.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         h2967lv = w;
         ceYLk5 = h;
      }
   }
}
