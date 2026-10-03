package zov.viola.util.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
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

public record BuiltGlow(SizeState size, QuadRadiusState radius, QuadColorState color, float glowRadius, float softness, float intensity, boolean additive)
   implements IRenderer {


private static final ShaderProgramKey GLOW_SHADER_KEY = new ShaderProgramKey(
      ResourceProvider.getShaderIdentifier("glow"), VertexFormats.POSITION_COLOR, Defines.EMPTY
   );

   

   @Override
   public void render(Matrix4f matrix, float x, float y, float z) {
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      RenderSystem.defaultBlendFunc();
      float w = this.size.width();
      float h = this.size.height();
      ShaderProgram shader = RenderSystem.setShader(GLOW_SHADER_KEY);
      shader.getUniform("Size").set(w, h);
      shader.getUniform("Radius").set(this.radius.radius1(), this.radius.radius2(), this.radius.radius3(), this.radius.radius4());
      shader.getUniform("GlowRadius").set(this.glowRadius);
      shader.getUniform("Softness").set(this.softness);
      shader.getUniform("Intensity").set(this.intensity);
      BufferBuilder bb = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bb.vertex(matrix, x, y, z).color(this.color.color1());
      bb.vertex(matrix, x, y + h, z).color(this.color.color2());
      bb.vertex(matrix, x + w, y + h, z).color(this.color.color3());
      bb.vertex(matrix, x + w, y, z).color(this.color.color4());
      BufferRenderer.drawWithGlobalProgram(bb.end());
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   

   

   

   

   

   

   
}
