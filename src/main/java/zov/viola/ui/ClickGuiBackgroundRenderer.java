package zov.viola.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ResourceProvider;
import zov.viola.util.render.renderers.IRenderer;

public final class ClickGuiBackgroundRenderer {
   private static final MinecraftClient MC = MinecraftClient.getInstance();
   private static final ShaderProgramKey KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("clickgui_bg"), VertexFormats.POSITION_COLOR, Defines.EMPTY
   );

   private ClickGuiBackgroundRenderer() {
   }

   public static void render(String mode, int accent, float opacity, float x, float y, float width, float height) {
      render(mode, accent, opacity, x, y, width, height, 8.0F, 1.0F);
   }

   public static void render(String mode, int accent, float opacity, float x, float y, float width, float height, float radius) {
      render(mode, accent, opacity, x, y, width, height, radius, 1.0F);
   }

   public static void render(String mode, int accent, float opacity, float x, float y, float width, float height, float radius, float timeScale) {
      float r = (accent >> 16 & 0xFF) / 255.0F;
      float g = (accent >> 8 & 0xFF) / 255.0F;
      float b = (accent & 0xFF) / 255.0F;
      ShaderProgram shader = RenderSystem.setShader(KEY);
      if (shader != null) {
         oL9J(shader, "Resolution", width, height);
         psVox6Q(shader, "uRadius", radius);
         psVox6Q(shader, "Time", (float)(System.currentTimeMillis() % 1000000L) / 1000.0F * timeScale);
         psVox6Q(shader, "Mode", pL7tr4d(mode));
         psVox6Q(shader, "uOpacity", opacity);
         i17D(shader, "ShowStars", 1);
         psVox6Q(shader, "NebulaStrength", 0.8F);
         psVox6Q(shader, "NebIntensity", 0.8F);
         psVox6Q(shader, "StarDensity", 0.7F);
         psVox6Q(shader, "PlasmaScale", 1.0F);
         psVox6Q(shader, "PlasmaSpeed", 1.0F);
         jH1t1q5(shader, "SkyZenith", r * 0.1F, g * 0.1F, b * 0.18F + 0.02F);
         jH1t1q5(shader, "SkyHorizon", r * 0.45F + 0.04F, g * 0.45F + 0.04F, b * 0.45F + 0.06F);
         jH1t1q5(shader, "NebColor1", r, g, b);
         jH1t1q5(shader, "NebColor2", b, Math.min(1.0F, r + 0.25F), Math.min(1.0F, g + 0.2F));
         jH1t1q5(shader, "StarColor", 0.9F, 0.94F, 1.0F);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x, y, 0.0F).color(-1);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x, y + height, 0.0F).color(-1);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x + width, y + height, 0.0F).color(-1);
         buffer.vertex(IRenderer.DEFAULT_MATRIX, x + width, y, 0.0F).color(-1);
         BufferRenderer.drawWithGlobalProgram(buffer.end());
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
      }
   }

   private static float pL7tr4d(String mode) {
      return switch (mode) {
         case "Aurora" -> 0.0F;
         case "Energy" -> 1.0F;
         case "Nebula" -> 2.0F;
         case "Cosmic Veil" -> 3.0F;
         case "Deep Space" -> 4.0F;
         case "Matrix" -> 5.0F;
         case "Void" -> 6.0F;
         case "Plasma" -> 7.0F;
         default -> 2.0F;
      };
   }

   private static void psVox6Q(ShaderProgram shader, String name, float value) {
      if (shader.getUniform(name) != null) {
         shader.getUniform(name).set(value);
      }
   }

   private static void i17D(ShaderProgram shader, String name, int value) {
      if (shader.getUniform(name) != null) {
         shader.getUniform(name).set(value);
      }
   }

   private static void oL9J(ShaderProgram shader, String name, float x, float y) {
      if (shader.getUniform(name) != null) {
         shader.getUniform(name).set(x, y);
      }
   }

   private static void jH1t1q5(ShaderProgram shader, String name, float x, float y, float z) {
      if (shader.getUniform(name) != null) {
         shader.getUniform(name).set(x, y, z);
      }
   }
}
