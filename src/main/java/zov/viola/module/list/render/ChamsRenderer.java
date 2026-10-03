package zov.viola.module.list.render;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import zov.viola.obf.D;

public final class ChamsRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
   private static final double PULSE_PERIOD_NANOS = 1.0E9;
   private static final float PULSE_LOW = 0.2F;
   private static final long ANIMATION_START_NANOS = System.nanoTime();

   public ChamsRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
      super(context);
   }

   public void render(
      MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance
   ) {
      Chams module = Chams.getInstance();
      MinecraftClient mc = MinecraftClient.getInstance();
      if (module != null && module.isEnabled() && mc.player != null && mc.world != null) {
         if (state.id != mc.player.getId() || !mc.options.getPerspective().isFirstPerson()) {
            if (vertexConsumers instanceof Immediate immediate) {
               immediate.draw();
            }

            boolean depthEnabled = GL11.glIsEnabled(2929);
            boolean blendEnabled = GL11.glIsEnabled(3042);
            boolean cullEnabled = GL11.glIsEnabled(2884);
            boolean depthMask = GL11.glGetBoolean(2930);

            try {
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableCull();
               long nowNanos = System.nanoTime();
               float pulse = module.isBlinking() ? utH6dj(nowNanos) : 1.0F;
               float baseAlpha = MathHelper.clamp(module.getAlpha(), 0.0F, 1.0F);
               float fillAlpha = (module.isMode("Vanilla") ? baseAlpha : MathHelper.clamp(module.getShaderAlpha(), 0.0F, 1.0F)) * pulse;
               float outlineBaseAlpha = MathHelper.clamp(baseAlpha + 0.2F, 0.0F, 1.0F);
               int sourceColor = module.getColor();
               float brightness = module.getBrightness();
               float red = (sourceColor >> 16 & 0xFF) / 255.0F * brightness;
               float green = (sourceColor >> 8 & 0xFF) / 255.0F * brightness;
               float blue = (sourceColor & 0xFF) / 255.0F * brightness;
               EntityModel<?> model = this.getContextModel();
               float animTime = (float)(nowNanos - ANIMATION_START_NANOS) / 1.0E9F;
               RenderSystem.enableDepthTest();
               RenderSystem.depthMask(false);
               if (module.hasGlow()) {
                  RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ONE, DstFactor.ZERO);
                  int layers = module.getGlowLayers();
                  float intensity = module.getGlowIntensity();

                  for (int i = layers; i >= 1; i--) {
                     float scale = 1.0F + i * 0.004F * intensity;
                     float layerAlpha = outlineBaseAlpha * (1.0F / (i + 1.0F)) * 0.7F * pulse;
                     lgoi9T(matrices, model, scale, red, green, blue, layerAlpha);
                  }

                  RenderSystem.defaultBlendFunc();
               }

               float outlineScale = 1.0F + module.getLineWidth() * 0.006F;
               lgoi9T(
                  matrices,
                  model,
                  outlineScale,
                  MathHelper.clamp(red + 0.15F, 0.0F, 1.0F),
                  MathHelper.clamp(green + 0.15F, 0.0F, 1.0F),
                  MathHelper.clamp(blue + 0.15F, 0.0F, 1.0F),
                  outlineBaseAlpha * pulse
               );
               nClwkn0(matrices, model, state.skinTextures.texture(), module, red, green, blue, fillAlpha, animTime);
               if (module.isThroughWalls()) {
                  RenderSystem.disableDepthTest();
                  if (module.hasGlow()) {
                     RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ONE, DstFactor.ZERO);
                     int layers = module.getGlowLayers();
                     float intensity = module.getGlowIntensity();

                     for (int i = layers; i >= 1; i--) {
                        float scale = 1.0F + i * 0.004F * intensity;
                        float layerAlpha = outlineBaseAlpha * (1.0F / (i + 1.0F)) * 0.7F * pulse * 0.5F;
                        lgoi9T(matrices, model, scale, red, green, blue, layerAlpha);
                     }

                     RenderSystem.defaultBlendFunc();
                  }

                  lgoi9T(
                     matrices,
                     model,
                     outlineScale,
                     MathHelper.clamp(red + 0.15F, 0.0F, 1.0F),
                     MathHelper.clamp(green + 0.15F, 0.0F, 1.0F),
                     MathHelper.clamp(blue + 0.15F, 0.0F, 1.0F),
                     outlineBaseAlpha * pulse * 0.5F
                  );
                  nClwkn0(matrices, model, state.skinTextures.texture(), module, red, green, blue, fillAlpha * 0.5F, animTime);
               }
            } finally {
               RenderSystem.defaultBlendFunc();
               RenderSystem.depthMask(depthMask);
               if (depthEnabled) {
                  RenderSystem.enableDepthTest();
               } else {
                  RenderSystem.disableDepthTest();
               }

               if (cullEnabled) {
                  RenderSystem.enableCull();
               } else {
                  RenderSystem.disableCull();
               }

               if (blendEnabled) {
                  RenderSystem.enableBlend();
               } else {
                  RenderSystem.disableBlend();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }
   }

   private static float utH6dj(double nowNanos) {
      double phase = nowNanos % 1.0E9 / 1.0E9;
      float wave = (float)(0.5 - 0.5 * Math.cos(phase * Math.PI * 2.0));
      return 0.2F + 0.8F * wave;
   }

   private static void nClwkn0(
      MatrixStack matrices, EntityModel<?> model, Identifier skin, Chams module, float red, float green, float blue, float alpha, float timeSeconds
   ) {
      if (!(alpha <= 0.001F)) {
         if (module.isMode("Animated Fill")) {
            Chams.bindAnimatedFill(timeSeconds, module.getShaderSpeed(), module.getShaderScale(), module.hasGlow() ? module.getGlowIntensity() : 0.0F);
         } else if (module.isMode("Wave")) {
            Chams.bindWave(timeSeconds * module.getShaderSpeed());
         } else {
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            RenderSystem.setShaderTexture(0, skin);
         }

         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         ChamsRenderer.ModelVertexConsumer consumer = new ChamsRenderer.ModelVertexConsumer(buffer, true);
         model.render(matrices, consumer, 15728880, OverlayTexture.DEFAULT_UV, llLjN0(red, green, blue, alpha));
         ysrmBg4(buffer);
      }
   }

   private static void lgoi9T(MatrixStack matrices, EntityModel<?> model, float scale, float red, float green, float blue, float alpha) {
      if (!(alpha <= 0.001F)) {
         matrices.push();
         matrices.scale(scale, scale, scale);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         ChamsRenderer.ModelVertexConsumer consumer = new ChamsRenderer.ModelVertexConsumer(buffer, false);
         model.render(matrices, consumer, 15728880, OverlayTexture.DEFAULT_UV, llLjN0(red, green, blue, alpha));
         ysrmBg4(buffer);
         matrices.pop();
      }
   }

   private static int llLjN0(float red, float green, float blue, float alpha) {
      int a = MathHelper.clamp((int)(alpha * 255.0F), 0, 255);
      int r = MathHelper.clamp((int)(red * 255.0F), 0, 255);
      int g = MathHelper.clamp((int)(green * 255.0F), 0, 255);
      int b = MathHelper.clamp((int)(blue * 255.0F), 0, 255);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private static void ysrmBg4(BufferBuilder buffer) {
      BuiltBuffer built = buffer.endNullable();
      if (built != null) {
         BufferRenderer.drawWithGlobalProgram(built);
      }
   }

   private static final class ModelVertexConsumer implements VertexConsumer {
      private final BufferBuilder dagrMa8;
      private final boolean pLrrc;
      private float nq7Dc;
      private float dGq1;
      private float mckkO;
      private float z3lu6;
      private float rmjdJt4;
      private int z5Bcer = 255;
      private int nMewVU = 255;
      private int rCvLd0h = 255;
      private int gE7a = 255;

      private ModelVertexConsumer(BufferBuilder target, boolean textured) {
         this.dagrMa8 = target;
         this.pLrrc = textured;
      }

      @Override
      public VertexConsumer vertex(float x, float y, float z) {
         this.nq7Dc = x;
         this.dGq1 = y;
         this.mckkO = z;
         return this;
      }

      @Override
      public VertexConsumer color(int red, int green, int blue, int alpha) {
         this.z5Bcer = red;
         this.nMewVU = green;
         this.rCvLd0h = blue;
         this.gE7a = alpha;
         return this;
      }

      @Override
      public VertexConsumer texture(float u, float v) {
         this.z3lu6 = u;
         this.rmjdJt4 = v;
         return this;
      }

      @Override
      public VertexConsumer overlay(int u, int v) {
         return this;
      }

      @Override
      public VertexConsumer light(int u, int v) {
         return this;
      }

      @Override
      public VertexConsumer normal(float x, float y, float z) {
         if (this.pLrrc) {
            this.dagrMa8.vertex(this.nq7Dc, this.dGq1, this.mckkO).texture(this.z3lu6, this.rmjdJt4).color(this.z5Bcer, this.nMewVU, this.rCvLd0h, this.gE7a);
         } else {
            this.dagrMa8.vertex(this.nq7Dc, this.dGq1, this.mckkO).color(this.z5Bcer, this.nMewVU, this.rCvLd0h, this.gE7a);
         }

         return this;
      }
   }
}
