package zov.viola.util.render.renderers.impl;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
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
import org.joml.Matrix4f;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.providers.ResourceProvider;
import zov.viola.util.render.renderers.IRenderer;

public record BuiltBlur(SizeState size, QuadRadiusState radius, QuadColorState color, float smoothness, float blurRadius) implements IRenderer {

private static final ShaderProgramKey BLUR_SHADER_KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("blur"), VertexFormats.POSITION_COLOR, Defines.EMPTY
   );
   private static final Supplier<SimpleFramebuffer> TEMP_FBO_SUPPLIER = Suppliers.memoize(() -> new SimpleFramebuffer(1920, 1024, false));
   private static boolean d4he = false;

   

   public static void beginFrame() {
      d4he = false;
   }

   @Override
   public void render(Matrix4f matrix, float x, float y, float z) {
      MinecraftClient client = MinecraftClient.getInstance();
      Framebuffer main = client.getFramebuffer();
      SimpleFramebuffer fbo = (SimpleFramebuffer)TEMP_FBO_SUPPLIER.get();
      boolean resized = false;
      if (fbo.textureWidth != main.textureWidth || fbo.textureHeight != main.textureHeight) {
         fbo.resize(main.textureWidth, main.textureHeight);
         resized = true;
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      if (resized || !d4he) {
         fbo.beginWrite(false);
         main.draw(fbo.textureWidth, fbo.textureHeight);
         d4he = true;
      }

      main.beginWrite(false);
      RenderSystem.setShaderTexture(0, fbo.getColorAttachment());
      float width = this.size.width();
      float height = this.size.height();
      ShaderProgram shader = RenderSystem.setShader(BLUR_SHADER_KEY);
      shader.getUniform("Size").set(width, height);
      shader.getUniform("Radius").set(this.radius.radius1(), this.radius.radius2(), this.radius.radius3(), this.radius.radius4());
      shader.getUniform("Smoothness").set(this.smoothness);
      shader.getUniform("BlurRadius").set(this.blurRadius);
      BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      builder.vertex(matrix, x, y, z).color(this.color.color1());
      builder.vertex(matrix, x, y + height, z).color(this.color.color2());
      builder.vertex(matrix, x + width, y + height, z).color(this.color.color3());
      builder.vertex(matrix, x + width, y, z).color(this.color.color4());
      BufferRenderer.drawWithGlobalProgram(builder.end());
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   

   

   

   

   
}
