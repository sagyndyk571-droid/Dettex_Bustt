package zov.viola.util.render.ambience;

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
import zov.viola.module.list.render.Ambience;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ResourceProvider;

public final class SkyShaderRenderer {
   private static final MinecraftClient MC = MinecraftClient.getInstance();
   private static final ShaderProgramKey SKY = lfbwX("sky");
   private static final ShaderProgramKey AURORA = lfbwX("sky_aurora");
   private static final ShaderProgramKey NEBULA = lfbwX("sky_nebula");
   private static final ShaderProgramKey MATRIX = lfbwX("sky_matrix");
   private static final ShaderProgramKey PLASMA = lfbwX("sky_plasma");
   private static final ShaderProgramKey IQ_PLASMA = lfbwX("sky_iq_plasma");
   private static final ShaderProgramKey SAKURA = lfbwX("sky_sakura");
   private static final ShaderProgramKey SUMMER = lfbwX("sky_summer");
   private static final ShaderProgramKey WORLD_TWEAKS = lfbwX("sky_worldtweaks");
   private static final ShaderProgramKey BLACK_HOLE = lfbwX("sky_black_hole");
   private static final ShaderProgramKey PULSE_NEBULA = lfbwX("sky_pulse_nebula");
   private static volatile SkyShaderRenderer.Config aGayuo;

   private SkyShaderRenderer() {
   }

   private static ShaderProgramKey lfbwX(String name) {
      return new ShaderProgramKey(ResourceProvider.getShaderIdentifier(name), VertexFormats.POSITION_COLOR, Defines.EMPTY);
   }

   public static void updateConfig(Ambience module) {
      if (module != null) {
         int primary = module.getSkyShaderColor();
         float r = (primary >> 16 & 0xFF) / 255.0F;
         float g = (primary >> 8 & 0xFF) / 255.0F;
         float b = (primary & 0xFF) / 255.0F;
         int fog = module.getFogColor();
         float fogR = (fog >> 16 & 0xFF) / 255.0F;
         float fogG = (fog >> 8 & 0xFF) / 255.0F;
         float fogB = (fog & 0xFF) / 255.0F;
         aGayuo = new SkyShaderRenderer.Config(
            module.getSkyShaderMode(),
            primary,
            r,
            g,
            b,
            fog,
            fogR,
            fogG,
            fogB,
            module.getFogDensity(),
            module.isFogEnabled(),
            module.showStars.getValue(),
            module.getStarDensity(),
            module.getNebulaStrength(),
            module.getPlasmaScale(),
            module.getPlasmaSpeed()
         );
      }
   }

   public static void render(Ambience module) {
      if (module != null && module.isEnabled() && module.isSkyShaderEnabled()) {
         SkyShaderRenderer.Config c = aGayuo;
         if (c == null
            || !c.mode().equals(module.getSkyShaderMode())
            || c.color() != module.getSkyShaderColor()
            || c.fogColor() != module.getFogColor()
            || c.fogDensity() != module.getFogDensity()
            || c.fogEnabled() != module.isFogEnabled()) {
            updateConfig(module);
            c = aGayuo;
         }

         if (c != null) {
            String shaderMode = c.mode();

            ShaderProgramKey key = switch (shaderMode) {
               case "Aurora" -> AURORA;
               case "Nebula" -> NEBULA;
               case "Matrix" -> MATRIX;
               case "Plasma" -> PLASMA;
               case "IQ Plasma" -> IQ_PLASMA;
               case "Sakura" -> SAKURA;
               case "Summer" -> SUMMER;
               case "FogBlur" -> WORLD_TWEAKS;
               case "Black Hole" -> BLACK_HOLE;
               case "Pulse Nebula" -> PULSE_NEBULA;
               default -> SKY;
            };
            ShaderProgram shader = RenderSystem.setShader(key);
            if (shader != null) {
               float time = (float)(System.currentTimeMillis() - module.getShaderStartTime()) / 1000.0F;
               rr3Bv(shader, "AnimTime", time);
               rr3Bv(shader, "iTime", time);
               rr3Bv(shader, "SkyMode", gxxE8(c.mode()));
               rr3Bv(shader, "StarDensity", c.showStars() ? c.starDensity() : 0.0F);
               rr3Bv(shader, "ShowStars", c.showStars() ? 1.0F : 0.0F);
               egjg3(shader, "uShowStars", c.showStars() ? 1 : 0);
               rr3Bv(shader, "NebulaStrength", c.nebulaStrength());
               rr3Bv(shader, "NebIntensity", 0.8F);
               rr3Bv(shader, "PlasmaScale", c.plasmaScale());
               rr3Bv(shader, "PlasmaSpeed", c.plasmaSpeed());
               rr3Bv(shader, "uScale", c.plasmaScale());
               rr3Bv(shader, "uSpeed", c.plasmaSpeed());
               rr3Bv(shader, "uIntensity", 1.0F);
               xrxn1(shader, "SkyFogColor", c.fogR(), c.fogG(), c.fogB());
               rr3Bv(shader, "FogDensity", c.fogEnabled() ? c.fogDensity() : 0.0F);
               xUfE1cn(shader, c);
               RenderSystem.disableDepthTest();
               RenderSystem.depthMask(false);
               RenderSystem.disableCull();
               RenderSystem.disableBlend();
               t4xdB();
               RenderSystem.depthMask(true);
               RenderSystem.enableDepthTest();
               RenderSystem.enableCull();
            }
         }
      }
   }

   private static void xUfE1cn(ShaderProgram shader, SkyShaderRenderer.Config c) {
      if (c.color() != -1) {
         xrxn1(shader, "SkyZenith", c.r() * 0.25F, c.g() * 0.25F, c.b() * 0.35F);
         xrxn1(shader, "SkyHorizon", c.r() * 0.45F + 0.04F, c.g() * 0.45F + 0.04F, c.b() * 0.45F + 0.06F);
      } else {
         xrxn1(shader, "SkyZenith", 0.02F, 0.03F, 0.07F);
         xrxn1(shader, "SkyHorizon", 0.045F, 0.085F, 0.16F);
      }

      xrxn1(shader, "NebColor1", c.r(), c.g(), c.b());
      xrxn1(shader, "NebColor2", c.b(), Math.min(1.0F, c.r() + 0.25F), Math.min(1.0F, c.g() + 0.2F));
      xrxn1(shader, "StarColor", 0.9F, 0.94F, 1.0F);
   }

   private static float gxxE8(String mode) {
      return switch (mode) {
         case "Cosmic Veil" -> 2.0F;
         case "Deep Space" -> 3.0F;
         case "Void" -> 4.0F;
         case "FogBlur" -> 2.0F;
         default -> 0.0F;
      };
   }

   private static void t4xdB() {
      float s = 100.0F;
      BufferBuilder b = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      wJxa(b, -s, -s, s, s, -s, s, s, s, s, -s, s, s);
      wJxa(b, s, -s, -s, -s, -s, -s, -s, s, -s, s, s, -s);
      wJxa(b, -s, -s, -s, -s, -s, s, -s, s, s, -s, s, -s);
      wJxa(b, s, -s, s, s, -s, -s, s, s, -s, s, s, s);
      wJxa(b, -s, s, s, s, s, s, s, s, -s, -s, s, -s);
      wJxa(b, -s, -s, -s, s, -s, -s, s, -s, s, -s, -s, s);
      BufferRenderer.drawWithGlobalProgram(b.end());
   }

   private static void wJxa(
      BufferBuilder b, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4
   ) {
      b.vertex(x1, y1, z1).color(-1);
      b.vertex(x2, y2, z2).color(-1);
      b.vertex(x3, y3, z3).color(-1);
      b.vertex(x4, y4, z4).color(-1);
   }

   private static void rr3Bv(ShaderProgram shader, String name, float value) {
      if (shader.getUniform(name) != null) {
         shader.getUniform(name).set(value);
      }
   }

   private static void egjg3(ShaderProgram shader, String name, int value) {
      if (shader.getUniform(name) != null) {
         shader.getUniform(name).set(value);
      }
   }

   private static void xrxn1(ShaderProgram shader, String name, float x, float y, float z) {
      if (shader.getUniform(name) != null) {
         shader.getUniform(name).set(x, y, z);
      }
   }

   private record Config(
      String mode,
      int color,
      float r,
      float g,
      float b,
      int fogColor,
      float fogR,
      float fogG,
      float fogB,
      float fogDensity,
      boolean fogEnabled,
      boolean showStars,
      float starDensity,
      float nebulaStrength,
      float plasmaScale,
      float plasmaSpeed
   ) {








      

      

      

      

      

      

      

      

      

      

      

      

      

      

      

      

      
   }
}
