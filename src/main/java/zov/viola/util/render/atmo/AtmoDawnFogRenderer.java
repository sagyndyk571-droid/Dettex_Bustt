package zov.viola.util.render.atmo;

import java.nio.FloatBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import zov.viola.module.list.render.AtmoDawnFog;
import zov.viola.util.render.shader.GlShaderProgram;
import zov.viola.util.render.shader.GlStateSnapshot;
import zov.viola.util.render.shader.RenderCapabilities;

public final class AtmoDawnFogRenderer {
   private static final MinecraftClient MC = MinecraftClient.getInstance();
   private static final String[] UNIFORMS = new String[]{
      "u_ScreenTexture",
      "u_DepthTexture",
      "u_Resolution",
      "u_Time",
      "u_CameraPos",
      "u_InverseProjectionMatrix",
      "u_InverseViewMatrix",
      "u_SunDirection",
      "u_SunScreen",
      "u_FogDensity",
      "u_FogMinHeight",
      "u_FogMaxHeight",
      "u_ViewDistance",
      "u_PaletteZenith",
      "u_PaletteHorizonWarm",
      "u_PaletteHorizonCool",
      "u_PaletteFogWarm",
      "u_PaletteFogCool",
      "u_PaletteRay",
      "u_Rainbow",
      "u_RainbowDir",
      "u_RainbowSize",
      "u_GodRays",
      "u_Softness",
      "u_Night",
      "u_Snow",
      "u_SnowSpeed",
      "u_SnowSize"
   };
   private static final int[] uniformLocations = new int[UNIFORMS.length];
   private static final Matrix4f viewMatrix = new Matrix4f();
   private static final Matrix4f projectionMatrix = new Matrix4f();
   private static final Matrix4f inverseProjection = new Matrix4f();
   private static final Matrix4f inverseView = new Matrix4f();
   private static final Vector4f vec4 = new Vector4f();
   private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
   private static final float[] palette = new float[18];
   private static GlShaderProgram nzsb;
   private static int gjBidDu;
   private static int b0aQ3X;
   private static int kR76CAE;
   private static int xrS0;
   private static int r2im2;
   private static int bMdkk;
   private static int xNrZkJ = -1;
   private static int zqx5 = -1;
   private static boolean i4e7bEB;
   private static boolean pXh85bx;
   private static float vK7ky = 0.5F;
   private static float dmet = 0.5F;
   private static float cDg5lgz;
   private static float lTG8w9;
   private static float pF9c7z = -0.39F;

   private AtmoDawnFogRenderer() {
   }

   public static void apply(AtmoDawnFog module, Camera camera, Matrix4f view, Matrix4f projection, float tickDelta) {
      if (module == null || !module.isEnabled()) {
         reset();
      } else if (!pXh85bx && camera != null && view != null && projection != null) {
         if (MC.world != null && MC.player != null) {
            Window window = MC.getWindow();
            if (window != null && !window.hasZeroWidthOrHeight()) {
               int width = window.getFramebufferWidth();
               int height = window.getFramebufferHeight();
               if (width > 1 && height > 1) {
                  Framebuffer framebuffer = MC.getFramebuffer();
                  if (framebuffer != null) {
                     int colorAttachment = framebuffer.getColorAttachment();
                     int depthAttachment = framebuffer.getDepthAttachment();
                     if (colorAttachment > 0 && depthAttachment > 0) {
                        if (!(module.getPlotnost() <= 1.0E-4F)) {
                           float time = ((float)(MC.world.getTime() % 100000L) + tickDelta) * 0.05F;
                           float skyAngle = MC.world.getSkyAngleRadians(tickDelta);
                           float sunDirY = -((float)Math.sin(skyAngle));
                           int mode = module.getModeIndex();
                           float sunAngle = z5di(mode);
                           float sign = sunDirY >= 0.0F ? 1.0F : -1.0F;
                           float sunX = sign * (float)Math.cos(sunAngle);
                           float sunY = (float)Math.sin(sunAngle);
                           lTG8w9 = -sign * (float)Math.cos(0.3F);
                           pF9c7z = -((float)Math.sin(0.3F));
                           int themeColor = module.resolveColor(mode);
                           float viewDistance = 192.0F;
                           if (MC.options != null) {
                              viewDistance = MC.options.getViewDistance().getValue().intValue() * 16.0F;
                           }

                           GlStateSnapshot.Snapshot glState = GlStateSnapshot.captureGlState();
                           boolean drawn = false;

                           try {
                              mzACAdn();
                              if (!pXh85bx) {
                                 if (wv14oh(width, height) && oow5(framebuffer.fbo, colorAttachment, width, height)) {
                                    Vec3d cameraPos = camera.getPos();
                                    viewMatrix.set(view);
                                    projectionMatrix.set(projection);
                                    fP4AXt8(sunX, sunY);
                                    inverseProjection.set(projectionMatrix).invert();
                                    inverseView.set(viewMatrix).invert();
                                    inverseView.m30((float)cameraPos.x);
                                    inverseView.m31((float)cameraPos.y);
                                    inverseView.m32((float)cameraPos.z);
                                    w9f00E(mode, themeColor);
                                    drawn = px8OQ2(colorAttachment, depthAttachment, width, height, cameraPos, time, sunX, sunY, module, viewDistance);
                                 }

                                 return;
                              }
                           } catch (Throwable var27) {
                              pXh85bx = true;
                              System.err.println("[AtmoDawnFog] renderer disabled: " + var27.getMessage());
                              return;
                           } finally {
                              if (drawn) {
                                 if (kR76CAE != 0) {
                                    GL30.glBindFramebuffer(36160, kR76CAE);
                                    GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                 }

                                 GL13.glActiveTexture(33985);
                                 GL11.glBindTexture(3553, 0);
                                 GL13.glActiveTexture(33984);
                                 GL11.glBindTexture(3553, 0);
                                 GL20.glUseProgram(0);
                              }

                              GlStateSnapshot.restoreGlState(glState);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static void reset() {
      if (kR76CAE != 0) {
         GL30.glDeleteFramebuffers(kR76CAE);
         kR76CAE = 0;
      }

      if (r2im2 != 0) {
         GL30.glDeleteFramebuffers(r2im2);
         r2im2 = 0;
      }

      if (bMdkk != 0) {
         GL30.glDeleteFramebuffers(bMdkk);
         bMdkk = 0;
      }

      if (xrS0 != 0) {
         GL11.glDeleteTextures(xrS0);
         xrS0 = 0;
      }

      if (gjBidDu != 0) {
         GL30.glDeleteVertexArrays(gjBidDu);
         gjBidDu = 0;
      }

      if (b0aQ3X != 0) {
         GL15.glDeleteBuffers(b0aQ3X);
         b0aQ3X = 0;
      }

      if (nzsb != null) {
         nzsb.delete();
         nzsb = null;
      }

      xNrZkJ = -1;
      zqx5 = -1;
      i4e7bEB = false;
      pXh85bx = false;
   }

   private static void mzACAdn() {
      if (!i4e7bEB) {
         nzsb = GlShaderProgram.resolve("assets/mre/shaders/atmo/world_volume.vert", "assets/mre/shaders/atmo/world_fog_fresnel.frag");
         gjBidDu = GL30.glGenVertexArrays();
         b0aQ3X = GL15.glGenBuffers();
         GL30.glBindVertexArray(gjBidDu);
         GL15.glBindBuffer(34962, b0aQ3X);
         float[] vertices = new float[]{
            -1.0F,
            -1.0F,
            0.0F,
            0.0F,
            1.0F,
            -1.0F,
            1.0F,
            0.0F,
            1.0F,
            1.0F,
            1.0F,
            1.0F,
            -1.0F,
            -1.0F,
            0.0F,
            0.0F,
            1.0F,
            1.0F,
            1.0F,
            1.0F,
            -1.0F,
            1.0F,
            0.0F,
            1.0F
         };
         GL15.glBufferData(34962, vertices, 35044);
         int stride = 16;
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 2, 5126, false, stride, 0L);
         GL20.glEnableVertexAttribArray(1);
         GL20.glVertexAttribPointer(1, 2, 5126, false, stride, 8L);
         GL15.glBindBuffer(34962, 0);
         GL30.glBindVertexArray(0);
         kR76CAE = GL30.glGenFramebuffers();

         for (int i = 0; i < UNIFORMS.length; i++) {
            uniformLocations[i] = nzsb.getUniformLocation(UNIFORMS[i]);
         }

         i4e7bEB = true;
      }
   }

   private static boolean wv14oh(int width, int height) {
      if (width > 0 && height > 0) {
         if (xrS0 != 0 && (xNrZkJ != width || zqx5 != height || r2im2 == 0)) {
            kaNqjq();
         }

         if (xrS0 == 0) {
            xrS0 = GL11.glGenTextures();
            GL11.glBindTexture(3553, xrS0);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            RenderCapabilities.texImage2D(32856, width, height, 6408, 5121);
            r2im2 = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(36160, r2im2);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, xrS0, 0);
            GL11.glDrawBuffer(36064);
            if (GL30.glCheckFramebufferStatus(36160) != 36053) {
               kaNqjq();
               return false;
            }
         }

         xNrZkJ = width;
         zqx5 = height;
         return true;
      } else {
         return false;
      }
   }

   private static void kaNqjq() {
      if (r2im2 != 0) {
         GL30.glDeleteFramebuffers(r2im2);
         r2im2 = 0;
      }

      if (xrS0 != 0) {
         GL11.glDeleteTextures(xrS0);
         xrS0 = 0;
      }

      xNrZkJ = -1;
      zqx5 = -1;
   }

   private static boolean oow5(int mainFbo, int colorAttachment, int width, int height) {
      if (colorAttachment > 0 && r2im2 != 0) {
         GL11.glDisable(3089);
         GL11.glDisable(3042);
         GL11.glDisable(2884);
         GL11.glDisable(2929);
         GL11.glDisable(36281);
         GL30.glBindFramebuffer(36008, mainFbo);
         GL11.glReadBuffer(36064);
         GL30.glBindFramebuffer(36009, r2im2);
         GL11.glDrawBuffer(36064);
         GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, 16384, 9728);
         return true;
      } else {
         return false;
      }
   }

   private static boolean px8OQ2(
      int colorAttachment,
      int depthAttachment,
      int width,
      int height,
      Vec3d cameraPos,
      float time,
      float sunX,
      float sunY,
      AtmoDawnFog module,
      float viewDistance
   ) {
      GL30.glBindFramebuffer(36160, kR76CAE);
      GL30.glFramebufferTexture2D(36160, 36064, 3553, colorAttachment, 0);
      GL11.glDrawBuffer(36064);
      if (GL30.glCheckFramebufferStatus(36160) != 36053) {
         return false;
      } else {
         GL11.glViewport(0, 0, Math.max(0, width), Math.max(0, height));
         GL11.glDisable(3089);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         GL11.glDisable(3042);
         GL11.glDisable(36281);
         GL11.glColorMask(true, true, true, true);
         GL11.glDepthMask(false);
         nzsb.use();
         float fogMinHeight = module.getVysotaRasseivaniya() - 18.0F;
         ubitsI("u_ScreenTexture", 0);
         ubitsI("u_DepthTexture", 1);
         mNuhtB6("u_Resolution", width, height);
         uz5RZ("u_Time", time);
         jav1L("u_CameraPos", (float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
         nsP2u("u_InverseProjectionMatrix", inverseProjection);
         nsP2u("u_InverseViewMatrix", inverseView);
         jav1L("u_SunDirection", sunX, sunY, 0.0F);
         jav1L("u_SunScreen", vK7ky, dmet, cDg5lgz);
         uz5RZ("u_FogDensity", q33Mhmx(module.getPlotnost(), 0.05F, 0.8F));
         uz5RZ("u_FogMinHeight", fogMinHeight);
         uz5RZ("u_FogMaxHeight", module.getVysotaRasseivaniya());
         uz5RZ("u_ViewDistance", viewDistance);
         rbiD();
         uz5RZ("u_Rainbow", module.getModeIndex() == 3 ? 0.0F : (module.isRadugaEnabled() ? q33Mhmx(module.getYarkostRadugi(), 0.0F, 1.0F) : 0.0F));
         jav1L("u_RainbowDir", lTG8w9, pF9c7z, 0.0F);
         uz5RZ("u_RainbowSize", q33Mhmx(module.getRazmerRadugi(), 40.0F, 64.0F));
         uz5RZ("u_GodRays", q33Mhmx(module.getLuchiSveta(), 0.0F, 1.0F));
         uz5RZ("u_Softness", q33Mhmx(module.getMyagkost(), 0.0F, 1.0F));
         uz5RZ("u_Night", module.getModeIndex() == 3 ? 1.0F : 0.0F);
         uz5RZ("u_Snow", module.isSnowEnabled() ? q33Mhmx(module.getIntensivnostSnega(), 0.0F, 2.0F) : 0.0F);
         uz5RZ("u_SnowSpeed", q33Mhmx(module.getSkorostSnega(), 0.1F, 2.0F));
         uz5RZ("u_SnowSize", q33Mhmx(module.getRazmerSnega(), 0.5F, 2.5F));
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, xrS0);
         GL13.glActiveTexture(33985);
         GL11.glBindTexture(3553, depthAttachment);
         GL13.glActiveTexture(33984);
         GL30.glBindVertexArray(gjBidDu);
         GL11.glDrawArrays(5, 0, 6);
         GL30.glBindVertexArray(0);
         return true;
      }
   }

   private static void fP4AXt8(float f, float g) {
      vK7ky = 0.5F;
      dmet = 0.5F;
      cDg5lgz = 0.0F;
      vec4.set(f, g, 0.0F, 0.0F);
      viewMatrix.transform(vec4);
      float x = vec4.x;
      float y = vec4.y;
      float z = vec4.z;
      float negZ = -z;
      if (negZ > 1.0E-4F) {
         vec4.set(x * 1000.0F, y * 1000.0F, z * 1000.0F, 1.0F);
         projectionMatrix.transform(vec4);
         if (vec4.w > 1.0E-4F) {
            vK7ky = vec4.x / vec4.w * 0.5F + 0.5F;
            dmet = vec4.y / vec4.w * 0.5F + 0.5F;
            cDg5lgz = q33Mhmx(negZ * 4.0F, 0.0F, 1.0F);
         }
      }
   }

   private static void w9f00E(int mode, int themeColor) {
      float colorR = (themeColor >> 16 & 0xFF) / 255.0F;
      float colorG = (themeColor >> 8 & 0xFF) / 255.0F;
      float colorB = (themeColor & 0xFF) / 255.0F;
      if (mode == 1) {
         p4mmiw(0, 0.135F, 0.125F, 0.3F);
         p4mmiw(3, 0.89F, 0.46F, 0.55F);
         p4mmiw(6, 0.38F, 0.35F, 0.56F);
         p4mmiw(9, 0.8F, 0.52F, 0.62F);
         p4mmiw(12, 0.47F, 0.44F, 0.64F);
         p4mmiw(15, 0.92F, 0.56F, 0.72F);
      } else if (mode == 2) {
         tb2jp(0, 0.085F, 0.1F, 0.2F, colorR, colorG, colorB, 0.3F);
         tb2jp(3, colorR, colorG, colorB, 1.0F, 0.93F, 0.82F, 0.35F);
         tb2jp(6, 0.52F, 0.58F, 0.74F, colorR, colorG, colorB, 0.28F);
         tb2jp(9, colorR, colorG, colorB, 0.97F, 0.93F, 0.88F, 0.45F);
         tb2jp(12, 0.58F, 0.63F, 0.76F, colorR, colorG, colorB, 0.35F);
         tb2jp(15, colorR, colorG, colorB, 1.0F, 0.96F, 0.88F, 0.3F);
      } else if (mode == 3) {
         p4mmiw(0, 0.02F, 0.025F, 0.07F);
         p4mmiw(3, 0.05F, 0.07F, 0.17F);
         p4mmiw(6, 0.03F, 0.045F, 0.12F);
         p4mmiw(9, 0.11F, 0.13F, 0.2F);
         p4mmiw(12, 0.05F, 0.06F, 0.1F);
         p4mmiw(15, 0.42F, 0.51F, 0.68F);
      } else {
         tb2jp(0, 0.16F, 0.19F, 0.38F, colorR, colorG, colorB, 0.14F);
         p4mmiw(3, q33Mhmx(colorR * 1.12F, 0.0F, 1.0F), q33Mhmx(colorG * 0.88F, 0.0F, 1.0F), q33Mhmx(colorB * 0.62F, 0.0F, 1.0F));
         p4mmiw(6, 0.56F, 0.62F, 0.8F);
         tb2jp(9, colorR, colorG, colorB, 0.95F, 0.55F, 0.63F, 0.42F);
         p4mmiw(12, 0.6F, 0.67F, 0.82F);
         p4mmiw(15, q33Mhmx(colorR * 1.08F, 0.0F, 1.0F), q33Mhmx(colorG * 0.94F, 0.0F, 1.0F), q33Mhmx(colorB * 0.72F, 0.0F, 1.0F));
      }
   }

   private static void p4mmiw(int offset, float f, float g, float h) {
      palette[offset] = f;
      palette[offset + 1] = g;
      palette[offset + 2] = h;
   }

   private static void tb2jp(int offset, float r1, float g1, float b1, float r2, float g2, float b2, float t) {
      float mix = q33Mhmx(t, 0.0F, 1.0F);
      float a1 = w1f9Pf(r1);
      float a2 = w1f9Pf(g1);
      float a3 = w1f9Pf(b1);
      float b3 = w1f9Pf(r2);
      float c1 = w1f9Pf(g2);
      float c2 = w1f9Pf(b2);
      float l1 = (float)Math.cbrt(0.41222146F * a1 + 0.53633255F * a2 + 0.051445995F * a3);
      float m1 = (float)Math.cbrt(0.2119035F * a1 + 0.6806995F * a2 + 0.10739696F * a3);
      float s1 = (float)Math.cbrt(0.08830246F * a1 + 0.28171885F * a2 + 0.6299787F * a3);
      float l2 = (float)Math.cbrt(0.41222146F * b3 + 0.53633255F * c1 + 0.051445995F * c2);
      float m2 = (float)Math.cbrt(0.2119035F * b3 + 0.6806995F * c1 + 0.10739696F * c2);
      float s2 = (float)Math.cbrt(0.08830246F * b3 + 0.28171885F * c1 + 0.6299787F * c2);
      float l3 = l1 + (l2 - l1) * mix;
      float m3 = m1 + (m2 - m1) * mix;
      float s3 = s1 + (s2 - s1) * mix;
      float r = l3 * l3 * l3;
      float g = m3 * m3 * m3;
      float b = s3 * s3 * s3;
      palette[offset] = uosc4(4.0767417F * r - 3.3077116F * g + 0.23096994F * b);
      palette[offset + 1] = uosc4(-1.268438F * r + 2.6097574F * g - 0.34131938F * b);
      palette[offset + 2] = uosc4(-0.0041960864F * r - 0.7034186F * g + 1.7076147F * b);
   }

   private static float w1f9Pf(float f) {
      return f <= 0.04045F ? f / 12.92F : (float)Math.pow((f + 0.055F) / 1.055F, 2.4);
   }

   private static float uosc4(float f) {
      f = q33Mhmx(f, 0.0F, 1.0F);
      return f <= 0.0031308F ? f * 12.92F : (float)(1.055 * Math.pow(f, 0.4166666666666667) - 0.055);
   }

   private static float z5di(int mode) {
      if (mode == 1) {
         return -0.045F;
      } else if (mode == 3) {
         return 0.5F;
      } else {
         return mode == 2 ? 0.13F : 0.11F;
      }
   }

   private static float q33Mhmx(float value, float min, float max) {
      return !Float.isFinite(value) ? min : Math.max(min, Math.min(max, value));
   }

   private static void ubitsI(String name, int value) {
      int location = ymew(name);
      if (location >= 0) {
         GL20.glUniform1i(location, value);
      }
   }

   private static void uz5RZ(String name, float value) {
      int location = ymew(name);
      if (location >= 0) {
         GL20.glUniform1f(location, value);
      }
   }

   private static void mNuhtB6(String name, float x, float y) {
      int location = ymew(name);
      if (location >= 0) {
         GL20.glUniform2f(location, x, y);
      }
   }

   private static void jav1L(String name, float x, float y, float z) {
      int location = ymew(name);
      if (location >= 0) {
         GL20.glUniform3f(location, x, y, z);
      }
   }

   private static void nsP2u(String name, Matrix4f matrix) {
      int location = ymew(name);
      if (location >= 0) {
         matrixBuffer.clear();
         matrix.get(matrixBuffer);
         GL20.glUniformMatrix4fv(location, false, matrixBuffer);
      }
   }

   private static void rbiD() {
      jav1L("u_PaletteZenith", palette[0], palette[1], palette[2]);
      jav1L("u_PaletteHorizonWarm", palette[3], palette[4], palette[5]);
      jav1L("u_PaletteHorizonCool", palette[6], palette[7], palette[8]);
      jav1L("u_PaletteFogWarm", palette[9], palette[10], palette[11]);
      jav1L("u_PaletteFogCool", palette[12], palette[13], palette[14]);
      jav1L("u_PaletteRay", palette[15], palette[16], palette[17]);
   }

   private static int ymew(String name) {
      for (int i = 0; i < UNIFORMS.length; i++) {
         if (UNIFORMS[i].equals(name)) {
            return uniformLocations[i];
         }
      }

      return -1;
   }
}
