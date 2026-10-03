package zov.viola.util.render.body;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.SkinTextures.Model;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;
import zov.viola.mixin.LivingEntityRendererAccessor;
import zov.viola.module.list.render.GlassBody;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.providers.ResourceProvider;

public class GlassBodyRenderer {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static GlassBodyRenderer rdGi;
   private static final ShaderProgramKey KEY_GLASS = j378lsS("glass_hands_glass");
   private static final ShaderProgramKey KEY_GLASS_GLOW = j378lsS("glass_hands_glass_glow");
   private static final ShaderProgramKey KEY_FILL = j378lsS("glass_hands_fill");
   private static final ShaderProgramKey KEY_FILL_GLOW = j378lsS("glass_hands_fill_glow");
   private static final ShaderProgramKey KEY_FILL_OUTLINE = j378lsS("glass_hands_fill_outline");
   private static final ShaderProgramKey KEY_WAVE = j378lsS("glass_hands_wave");
   private static final ShaderProgramKey KEY_COLOR_MASK = j378lsS("glass_hands_color_mask");
   private static final ShaderProgramKey KEY_KAWASE_DOWN = j378lsS("glass_hands_kawase_down");
   private static final ShaderProgramKey KEY_KAWASE_UP = j378lsS("glass_hands_kawase_up");
   private Framebuffer tlkzu;
   private Framebuffer oocs3;
   private Framebuffer ac22ior;
   private final List<Framebuffer> pqY8 = new ArrayList<>();
   private final List<Framebuffer> uv8V65 = new ArrayList<>();
   private int hzkM6 = -1;
   private int gu2Tx9 = -1;
   private int v3h3m4t = -1;

   private static ShaderProgramKey j378lsS(String name) {
      return new ShaderProgramKey(ResourceProvider.getShaderIdentifier(name), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
   }

   public static GlassBodyRenderer getInstance() {
      if (rdGi == null) {
         rdGi = new GlassBodyRenderer();
      }

      return rdGi;
   }

   public void invalidate() {
      this.v3h3m4t = -1;
   }

   public void renderBody(GlassBody module, MatrixStack matrices, float tickDelta) {
      if (module != null && module.isEnabled()) {
         if (mc.world != null && mc.player != null) {
            if (!mc.options.getPerspective().isFirstPerson()) {
               this.exCQu();
               if (this.tlkzu != null) {
                  this.tlkzu.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                  this.tlkzu.clear();
                  this.tlkzu.beginWrite(false);
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.disableCull();
                  RenderSystem.disableDepthTest();
                  RenderSystem.depthMask(false);
                  this.q8yml(matrices, mc.player, tickDelta);
                  RenderSystem.depthMask(true);
                  RenderSystem.enableDepthTest();
                  RenderSystem.enableCull();
                  RenderSystem.disableBlend();
                  mc.getFramebuffer().beginWrite(true);
                  int color1 = ColorProvider.getThemeColor();
                  int color2 = ColorProvider.getThemeColorTwo();
                  if (module.mode.is("Стекло")) {
                     this.bE5C4(module, color1, color2);
                  } else if (module.mode.is("Заливка")) {
                     this.q57373(module, color1, color2);
                  } else if (module.mode.is("Волна")) {
                     this.eje3Yj(module, color1, color2);
                  }

                  this.fPpwe();
               }
            }
         }
      }
   }

   private void bE5C4(GlassBody module, int color1, int color2) {
      int blurredBgTex = this.zs3Cy38(mc.getFramebuffer().getColorAttachment(), 3);
      ShaderProgram glassShader = mc.getShaderLoader().getOrCreateProgram(KEY_GLASS);
      if (glassShader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_GLASS);
         RenderSystem.setShaderTexture(0, blurredBgTex);
         RenderSystem.setShaderTexture(1, mc.getFramebuffer().getColorAttachment());
         RenderSystem.setShaderTexture(2, this.tlkzu.getColorAttachment());
         this.c2CO(glassShader, "mixFactor", module.glassMixFactor.getFloatValue());
         this.lr5I();
         RenderSystem.defaultBlendFunc();
         if (module.glassGlowEnabled.getValue() && module.glassOuterGlow.getValue()) {
            this.z70lQ5(module);
         }
      }
   }

   private void z70lQ5(GlassBody module) {
      int iterations = Math.max(1, Math.min(6, (int)module.glassGlowRadius.getFloatValue()));
      int glowTex = this.zs3Cy38(this.tlkzu.getColorAttachment(), iterations);
      int c1;
      int c2;
      if (module.glassRainbow.getValue()) {
         int[] rb = this.bpCdR(module.glassRainbowSpeed.getFloatValue());
         c1 = rb[0];
         c2 = rb[1];
      } else {
         c1 = module.glassGlowColor1.getValue();
         c2 = module.glassGlowColor2.getValue();
      }

      ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_GLASS_GLOW);
      if (glowShader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_GLASS_GLOW);
         RenderSystem.setShaderTexture(0, glowTex);
         RenderSystem.setShaderTexture(1, this.tlkzu.getColorAttachment());
         this.rsc13z8(glowShader, "glowColor1", this.ut3B8(c1), this.sncfrk(c1), this.iKsQG(c1));
         this.rsc13z8(glowShader, "glowColor2", this.ut3B8(c2), this.sncfrk(c2), this.iKsQG(c2));
         this.c2CO(glowShader, "exposure", module.glassGlowExposure.getFloatValue());
         this.lr5I();
         RenderSystem.defaultBlendFunc();
      }
   }

   private void q57373(GlassBody module, int color1, int color2) {
      if (module.fillGlowEnabled.getValue() || module.fillOutlineEnabled.getValue()) {
         this.xnKk(module);
      }

      this.myE64(module);
   }

   private void xnKk(GlassBody module) {
      boolean doBloom = module.fillGlowEnabled.getValue() && module.fillOuterGlow.getValue();
      if (doBloom) {
         this.qjLfGh9(module);
      }

      if (module.fillOutlineEnabled.getValue()) {
         this.wWqeFG8(module);
      }
   }

   private void qjLfGh9(GlassBody module) {
      int iter = Math.max(1, Math.min(6, (int)module.fillGlowRadius.getFloatValue()));
      this.oocs3 = this.utLXN(this.oocs3, false, 1);
      this.oocs3.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.oocs3.clear();
      this.oocs3.beginWrite(false);
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, mc.getFramebuffer().getColorAttachment());
      RenderSystem.setShaderTexture(1, this.tlkzu.getColorAttachment());
      ShaderProgram colorMaskShader = mc.getShaderLoader().getOrCreateProgram(KEY_COLOR_MASK);
      if (colorMaskShader != null) {
         RenderSystem.setShader(KEY_COLOR_MASK);
         this.lr5I();
      }

      int cur = this.oocs3.getColorAttachment();
      cur = this.zs3Cy38(cur, iter);
      this.v3h3m4t = cur;
      int g1;
      int g2;
      float auto;
      if (module.fillRainbow.getValue()) {
         int[] rb = this.bpCdR(module.fillRainbowSpeed.getFloatValue());
         g1 = rb[0];
         g2 = rb[1];
         auto = 0.0F;
      } else if (module.fillAutoColor.getValue()) {
         g1 = ColorProvider.getThemeColor();
         g2 = ColorProvider.getThemeColorTwo();
         auto = 1.0F;
      } else {
         g1 = module.fillGlowColor1.getValue();
         g2 = module.fillGlowColor2.getValue();
         auto = 0.0F;
      }

      ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_GLOW);
      if (glowShader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_FILL_GLOW);
         RenderSystem.setShaderTexture(0, cur);
         RenderSystem.setShaderTexture(1, this.tlkzu.getColorAttachment());
         this.rsc13z8(glowShader, "glowColor1", this.ut3B8(g1), this.sncfrk(g1), this.iKsQG(g1));
         this.rsc13z8(glowShader, "glowColor2", this.ut3B8(g2), this.sncfrk(g2), this.iKsQG(g2));
         this.c2CO(glowShader, "exposure", module.fillGlowExposure.getFloatValue());
         this.c2CO(glowShader, "autoColor", auto);
         this.c2CO(glowShader, "saturation", module.fillSaturation.getFloatValue());
         this.lr5I();
         RenderSystem.defaultBlendFunc();
      }
   }

   private void wWqeFG8(GlassBody module) {
      boolean auto = module.fillAutoColor.getValue() && this.v3h3m4t != -1;
      int colorMode = auto ? 2 : 0;
      int oc = module.fillOutlineColor.getValue();
      Color col = new Color(oc, true);
      float a = col.getAlpha() / 255.0F;
      ShaderProgram outlineShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_OUTLINE);
      if (outlineShader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_FILL_OUTLINE);
         RenderSystem.setShaderTexture(0, this.tlkzu.getColorAttachment());
         if (auto) {
            RenderSystem.setShaderTexture(1, this.v3h3m4t);
         }

         int fw = Math.max(1, mc.getWindow().getFramebufferWidth());
         int fh = Math.max(1, mc.getWindow().getFramebufferHeight());
         this.c2CO(outlineShader, "colorMode", colorMode);
         this.c2CO(outlineShader, "width", module.fillOutlineWidth.getFloatValue());
         this.vXyqEZ(outlineShader, "texelSize", 1.0F / fw, 1.0F / fh);
         this.c2CO(outlineShader, "alpha", a);
         this.c2CO(outlineShader, "saturation", module.fillSaturation.getFloatValue());
         this.rsc13z8(outlineShader, "solidColor", col.getRed() / 255.0F, col.getGreen() / 255.0F, col.getBlue() / 255.0F);
         this.lr5I();
         RenderSystem.defaultBlendFunc();
      }
   }

   private void myE64(GlassBody module) {
      ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL);
      if (shader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(KEY_FILL);
         RenderSystem.setShaderTexture(0, mc.getFramebuffer().getColorAttachment());
         RenderSystem.setShaderTexture(1, this.tlkzu.getColorAttachment());
         boolean rainbow = module.fillRainbow.getValue();
         int fc = rainbow ? this.bpCdR(module.fillRainbowSpeed.getFloatValue())[0] : module.fillColor.getValue();
         this.rsc13z8(shader, "fillColor", this.ut3B8(fc), this.sncfrk(fc), this.iKsQG(fc));
         this.c2CO(shader, "fillAlpha", module.fillAlpha.getFloatValue());
         this.c2CO(shader, "keepShading", module.fillKeepShading.getValue() ? 1.0F : 0.0F);
         this.c2CO(shader, "shadingStrength", module.fillShadingStrength.getFloatValue());
         this.c2CO(shader, "rainbow", rainbow ? 1.0F : 0.0F);
         this.c2CO(shader, "rainbowTime", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
         this.c2CO(shader, "rainbowSpeed", module.fillRainbowSpeed.getFloatValue());
         this.c2CO(shader, "rainbowScale", 0.5F);
         this.c2CO(shader, "screenH", Math.max(1, mc.getWindow().getFramebufferHeight()));
         this.lr5I();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
      }
   }

   private void eje3Yj(GlassBody module, int color1, int color2) {
      ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(KEY_WAVE);
      if (shader != null) {
         if (module.waveRainbow.getValue() && !module.waveAutoColor.getValue()) {
            int[] rb = this.bpCdR(module.waveRainbowSpeed.getFloatValue());
            color1 = rb[0];
            color2 = rb[0];
         }

         this.ac22ior = this.utLXN(this.ac22ior, false, 1);
         this.ac22ior.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.ac22ior.clear();
         this.ac22ior.beginWrite(false);
         RenderSystem.disableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(KEY_WAVE);
         RenderSystem.setShaderTexture(0, this.tlkzu.getColorAttachment());
         RenderSystem.setShaderTexture(1, mc.getFramebuffer().getColorAttachment());
         float t = (float)(System.currentTimeMillis() % 100000L) / 1000.0F;
         this.c2CO(shader, "time", t);
         this.rsc13z8(shader, "color1", this.ut3B8(color1), this.sncfrk(color1), this.iKsQG(color1));
         this.rsc13z8(shader, "color2", this.ut3B8(color2), this.sncfrk(color2), this.iKsQG(color2));
         this.c2CO(shader, "waveSpeedX", module.waveSpeedX.getFloatValue());
         this.c2CO(shader, "waveSpeedY", module.waveSpeedY.getFloatValue());
         this.c2CO(shader, "waveScale", module.waveScaleM.getFloatValue());
         this.c2CO(shader, "waveDensity", module.waveDensity.getFloatValue());
         this.c2CO(shader, "waveGlow", module.waveGlow.getFloatValue());
         this.c2CO(shader, "fillAlpha", 1.0F);
         this.c2CO(shader, "modelVisibility", module.waveModelVisibility.getFloatValue());
         this.lr5I();
         if (module.waveGlowEnabled.getValue() && module.waveOuterGlow.getValue()) {
            this.jr2dP(module, color1, color2);
         }

         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(1, 771);
         RenderSystem.disableDepthTest();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, module.waveFillAlpha.getFloatValue());
         RenderSystem.setShaderTexture(0, this.ac22ior.getColorAttachment());
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
         this.lr5I();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private void jr2dP(GlassBody module, int color1, int color2) {
      int iter = Math.max(1, Math.min(6, (int)module.waveGlowRadius.getFloatValue()));
      int bloomSource = module.waveAutoColor.getValue() ? this.ac22ior.getColorAttachment() : this.tlkzu.getColorAttachment();
      int cur = this.zs3Cy38(bloomSource, iter);
      int g1;
      int g2;
      float auto;
      if (module.waveAutoColor.getValue()) {
         g1 = color1;
         g2 = color2;
         auto = 1.0F;
      } else if (module.waveRainbow.getValue()) {
         int[] rb = this.bpCdR(module.waveRainbowSpeed.getFloatValue());
         g1 = rb[0];
         g2 = rb[0];
         auto = 0.0F;
      } else {
         g1 = module.waveGlowColor1.getValue();
         g2 = module.waveGlowColor2.getValue();
         auto = 0.0F;
      }

      ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_GLOW);
      if (glowShader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_FILL_GLOW);
         RenderSystem.setShaderTexture(0, cur);
         RenderSystem.setShaderTexture(1, this.tlkzu.getColorAttachment());
         this.rsc13z8(glowShader, "glowColor1", this.ut3B8(g1), this.sncfrk(g1), this.iKsQG(g1));
         this.rsc13z8(glowShader, "glowColor2", this.ut3B8(g2), this.sncfrk(g2), this.iKsQG(g2));
         this.c2CO(glowShader, "exposure", module.waveGlowExposure.getFloatValue());
         this.c2CO(glowShader, "autoColor", auto);
         this.c2CO(glowShader, "saturation", module.waveSaturation.getFloatValue());
         this.lr5I();
         RenderSystem.defaultBlendFunc();
      }
   }

   private void q8yml(MatrixStack matrices, PlayerEntity player, float tickDelta) {
      if (player instanceof AbstractClientPlayerEntity clientPlayer) {
         if ((Object)mc.getEntityRenderDispatcher().getRenderer(player) instanceof PlayerEntityRenderer renderer) {
            PlayerEntityRenderState state = renderer.createRenderState();
            renderer.updateRenderState(clientPlayer, state, tickDelta);
            PlayerEntityModel model = renderer.getModel();
            model.setAngles(state);
            matrices.push();
            Vec3d cam = mc.gameRenderer.getCamera().getPos();
            Vec3d pos = player.getLerpedPos(tickDelta);
            matrices.translate(pos.x - cam.x, pos.y - cam.y, pos.z - cam.z);
            if (state.sleepingDirection != null) {
               float off = state.standingEyeHeight - 0.1F;
               matrices.translate(-state.sleepingDirection.getOffsetX() * off, 0.0F, -state.sleepingDirection.getOffsetZ() * off);
            }

            float scale = state.baseScale;
            matrices.scale(scale, scale, scale);
            LivingEntityRendererAccessor accessor = (LivingEntityRendererAccessor)renderer;
            accessor.viola$setupTransforms(state, matrices, state.bodyYaw, scale);
            accessor.viola$scale(state, matrices);
            matrices.scale(-1.0F, -1.0F, 1.0F);
            matrices.translate(0.0F, -1.501F, 0.0F);
            RenderSystem.setShaderTexture(0, clientPlayer.getSkinTextures().texture());
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            ModelPart root = model.getRootPart();
            BufferBuilder buf = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            boolean slim = clientPlayer.getSkinTextures().model() == Model.SLIM;
            this.xyL5KU(matrices, buf, root, model.head, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 16.0F, 16.0F);
            this.xyL5KU(matrices, buf, root, model.body, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 20.0F, 20.0F, 28.0F, 32.0F);
            if (slim) {
               this.xyL5KU(matrices, buf, root, model.rightArm, -2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 44.0F, 16.0F, 47.0F, 28.0F);
               this.xyL5KU(matrices, buf, root, model.leftArm, -1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 36.0F, 52.0F, 39.0F, 64.0F);
            } else {
               this.xyL5KU(matrices, buf, root, model.rightArm, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 44.0F, 16.0F, 48.0F, 28.0F);
               this.xyL5KU(matrices, buf, root, model.leftArm, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 36.0F, 52.0F, 40.0F, 64.0F);
            }

            this.xyL5KU(matrices, buf, root, model.rightLeg, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 4.0F, 16.0F, 8.0F, 28.0F);
            this.xyL5KU(matrices, buf, root, model.leftLeg, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 20.0F, 48.0F, 24.0F, 60.0F);
            BufferRenderer.drawWithGlobalProgram(buf.end());
            matrices.pop();
         }
      }
   }

   private void xyL5KU(
      MatrixStack stack,
      BufferBuilder buf,
      ModelPart root,
      ModelPart part,
      float ox,
      float oy,
      float oz,
      float w,
      float h,
      float d,
      float u1,
      float v1,
      float u2,
      float v2
   ) {
      stack.push();
      root.rotate(stack);
      part.rotate(stack);
      float s = 0.0625F;
      float x0 = ox * s;
      float y0 = oy * s;
      float z0 = oz * s;
      float x1 = (ox + w) * s;
      float y1 = (oy + h) * s;
      float z1 = (oz + d) * s;
      Matrix4f m = stack.peek().getPositionMatrix();
      float tu1 = u1 / 64.0F;
      float tv1 = v1 / 64.0F;
      float tu2 = u2 / 64.0F;
      float tv2 = v2 / 64.0F;
      this.mBfqoTh(buf, m, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, tu1, tv1, tu2, tv2);
      this.mBfqoTh(buf, m, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1, tu1, tv1, tu2, tv2);
      this.mBfqoTh(buf, m, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0, tu1, tv1, tu2, tv2);
      this.mBfqoTh(buf, m, x1, y0, z1, x1, y1, z1, x0, y1, z1, x0, y0, z1, tu1, tv1, tu2, tv2);
      this.mBfqoTh(buf, m, x0, y0, z1, x0, y1, z1, x0, y1, z0, x0, y0, z0, tu1, tv1, tu2, tv2);
      this.mBfqoTh(buf, m, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1, tu1, tv1, tu2, tv2);
      stack.pop();
   }

   private void mBfqoTh(
      BufferBuilder b,
      Matrix4f m,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float x4,
      float y4,
      float z4,
      float u1,
      float v1,
      float u2,
      float v2
   ) {
      b.vertex(m, x1, y1, z1).texture(u1, v1).color(255, 255, 255, 255);
      b.vertex(m, x2, y2, z2).texture(u1, v2).color(255, 255, 255, 255);
      b.vertex(m, x3, y3, z3).texture(u2, v2).color(255, 255, 255, 255);
      b.vertex(m, x4, y4, z4).texture(u2, v1).color(255, 255, 255, 255);
   }

   private void exCQu() {
      int w = mc.getWindow().getFramebufferWidth();
      int h = mc.getWindow().getFramebufferHeight();
      if (w != this.hzkM6 || h != this.gu2Tx9 || this.tlkzu == null) {
         if (this.tlkzu != null) {
            this.tlkzu.delete();
         }

         if (this.oocs3 != null) {
            this.oocs3.delete();
            this.oocs3 = null;
         }

         if (this.ac22ior != null) {
            this.ac22ior.delete();
            this.ac22ior = null;
         }

         this.tlkzu = new SimpleFramebuffer(w, h, true);
         this.gb6XntP(this.tlkzu);
         this.hzkM6 = w;
         this.gu2Tx9 = h;
         this.pqY8.forEach(Framebuffer::delete);
         this.pqY8.clear();
         this.uv8V65.forEach(Framebuffer::delete);
         this.uv8V65.clear();
         this.v3h3m4t = -1;
      }
   }

   private Framebuffer utLXN(Framebuffer buf, boolean depth, int divisor) {
      int fw = mc.getWindow().getFramebufferWidth();
      int fh = mc.getWindow().getFramebufferHeight();
      int w = Math.max(2, fw / divisor);
      int h = Math.max(2, fh / divisor);
      if (buf == null) {
         buf = new SimpleFramebuffer(w, h, depth);
         this.gb6XntP(buf);
      } else if (buf.textureWidth != w || buf.textureHeight != h) {
         buf.delete();
         buf = new SimpleFramebuffer(w, h, depth);
         this.gb6XntP(buf);
      }

      return buf;
   }

   private int zs3Cy38(int sourceTex, int iterations) {
      this.wtR80b(iterations);
      if (this.pqY8.isEmpty()) {
         return sourceTex;
      } else {
         ShaderProgram downShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_DOWN);
         ShaderProgram upShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_UP);
         if (downShader != null && upShader != null) {
            int currentTex = sourceTex;

            for (int i = 0; i < iterations; i++) {
               Framebuffer dst = this.pqY8.get(i);
               dst.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               dst.clear();
               dst.beginWrite(true);
               RenderSystem.setShader(KEY_KAWASE_DOWN);
               RenderSystem.setShaderTexture(0, currentTex);
               this.vhq5TBu(downShader, dst.textureWidth, dst.textureHeight, 1.0F + i);
               this.lr5I();
               currentTex = dst.getColorAttachment();
            }

            for (int i = iterations - 1; i >= 1; i--) {
               Framebuffer dst = this.pqY8.get(i - 1);
               dst.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               dst.clear();
               dst.beginWrite(true);
               RenderSystem.setShader(KEY_KAWASE_UP);
               RenderSystem.setShaderTexture(0, currentTex);
               this.vhq5TBu(upShader, dst.textureWidth, dst.textureHeight, 1.0F + i);
               this.rsc13z8(upShader, "color", 1.0F, 1.0F, 1.0F);
               this.lr5I();
               currentTex = dst.getColorAttachment();
            }

            mc.getFramebuffer().beginWrite(true);
            return currentTex;
         } else {
            return sourceTex;
         }
      }
   }

   private void wtR80b(int iterations) {
      while (this.pqY8.size() > iterations) {
         this.pqY8.remove(this.pqY8.size() - 1).delete();
      }

      for (int i = 0; i < iterations; i++) {
         int w = Math.max(2, this.hzkM6 >> i + 1);
         int h = Math.max(2, this.gu2Tx9 >> i + 1);
         if (i >= this.pqY8.size()) {
            Framebuffer fb = new SimpleFramebuffer(w, h, false);
            this.gb6XntP(fb);
            this.pqY8.add(fb);
         } else {
            Framebuffer fb = this.pqY8.get(i);
            if (fb.textureWidth != w || fb.textureHeight != h) {
               fb.delete();
               Framebuffer var7 = new SimpleFramebuffer(w, h, false);
               this.gb6XntP(var7);
               this.pqY8.set(i, var7);
            }
         }
      }
   }

   private void gb6XntP(Framebuffer fb) {
      RenderSystem.bindTexture(fb.getColorAttachment());
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      RenderSystem.bindTexture(0);
   }

   private void fPpwe() {
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.setShaderTexture(1, 0);
      RenderSystem.setShaderTexture(2, 0);
      mc.getFramebuffer().beginWrite(true);
   }

   private void lr5I() {
      float sw = Math.max(mc.getWindow().getScaledWidth(), 1);
      float sh = Math.max(mc.getWindow().getScaledHeight(), 1);
      Matrix4f prevProj = new Matrix4f(RenderSystem.getProjectionMatrix());
      ProjectionType prevType = RenderSystem.getProjectionType();
      RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, sw, sh, 0.0F, -1000.0F, 1000.0F), ProjectionType.ORTHOGRAPHIC);
      Matrix4fStack mvStack = RenderSystem.getModelViewStack();
      mvStack.pushMatrix();
      mvStack.identity();
      BufferBuilder b = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      b.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      b.vertex(0.0F, sh, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      b.vertex(sw, sh, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      b.vertex(sw, 0.0F, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(b.end());
      mvStack.popMatrix();
      RenderSystem.setProjectionMatrix(prevProj, prevType);
   }

   private void c2CO(ShaderProgram s, String name, float v) {
      GlUniform u = s.getUniform(name);
      if (u != null) {
         u.set(v);
      }
   }

   private void vXyqEZ(ShaderProgram s, String name, float x, float y) {
      GlUniform u = s.getUniform(name);
      if (u != null) {
         u.set(x, y);
      }
   }

   private void rsc13z8(ShaderProgram s, String name, float x, float y, float z) {
      GlUniform u = s.getUniform(name);
      if (u != null) {
         u.set(x, y, z);
      }
   }

   private void vhq5TBu(ShaderProgram s, int w, int h, float offset) {
      this.vXyqEZ(s, "uSize", Math.max(1, w), Math.max(1, h));
      this.vXyqEZ(s, "uOffset", offset, offset);
      this.vXyqEZ(s, "uHalfPixel", 0.5F / Math.max(1, w), 0.5F / Math.max(1, h));
   }

   private int[] bpCdR(float speed) {
      float hue = (float)(System.currentTimeMillis() % (long)(10000.0 / speed)) / (10000.0F / speed);
      int c1 = Color.HSBtoRGB(hue, 1.0F, 1.0F) & 16777215;
      int c2 = Color.HSBtoRGB((hue + 0.5F) % 1.0F, 1.0F, 1.0F) & 16777215;
      return new int[]{c1, c2};
   }

   private float ut3B8(int c) {
      return (c >> 16 & 0xFF) / 255.0F;
   }

   private float sncfrk(int c) {
      return (c >> 8 & 0xFF) / 255.0F;
   }

   private float iKsQG(int c) {
      return (c & 0xFF) / 255.0F;
   }
}
