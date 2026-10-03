package zov.viola.util.render.hands;

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
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import zov.viola.module.list.render.GlassHands;
import zov.viola.obf.D;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.providers.ResourceProvider;

public class GlassHandsRenderer {
   private static final float EPSILON = 0.001F;
   private static GlassHandsRenderer vjtjk8;
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final ShaderProgramKey KEY_MASK_DIFF = eFylrBZ("glass_hands_mask_diff");
   private static final ShaderProgramKey KEY_GLOW = eFylrBZ("glass_hands_glow");
   private static final ShaderProgramKey KEY_OVERLAY = eFylrBZ("glass_hands_overlay");
   private static final ShaderProgramKey KEY_KAWASE_DOWN = eFylrBZ("glass_hands_kawase_down");
   private static final ShaderProgramKey KEY_KAWASE_UP = eFylrBZ("glass_hands_kawase_up");
   private static final ShaderProgramKey KEY_PRETTY = eFylrBZ("glass_hands_pretty");
   private static final ShaderProgramKey KEY_BLUR_HANDS = eFylrBZ("glass_hands_blur");
   private static final ShaderProgramKey KEY_TRAIL = eFylrBZ("glass_hands_trail");
   private static final ShaderProgramKey KEY_TRAIL_MOTION = eFylrBZ("glass_hands_trail_motion");
   private static final ShaderProgramKey KEY_TRAIL_ACCUMULATE = eFylrBZ("glass_hands_trail_accumulate");
   private static final ShaderProgramKey KEY_TRAIL_COMPOSITE = eFylrBZ("glass_hands_trail_composite");
   private static final ShaderProgramKey KEY_GLASS = eFylrBZ("glass_hands_glass");
   private static final ShaderProgramKey KEY_GLASS_GLOW = eFylrBZ("glass_hands_glass_glow");
   private static final ShaderProgramKey KEY_PLASMA = eFylrBZ("glass_hands_plasma");
   private static final ShaderProgramKey KEY_FILL = eFylrBZ("glass_hands_fill");
   private static final ShaderProgramKey KEY_FILL_GLOW = eFylrBZ("glass_hands_fill_glow");
   private static final ShaderProgramKey KEY_FILL_OUTLINE = eFylrBZ("glass_hands_fill_outline");
   private static final ShaderProgramKey KEY_FILL_TRAIL_FADE = eFylrBZ("glass_hands_fill_trail_fade");
   private static final ShaderProgramKey KEY_FILL_TRAIL_COLOR = eFylrBZ("glass_hands_fill_trail_color");
   private static final ShaderProgramKey KEY_WAVE = eFylrBZ("glass_hands_wave");
   private Framebuffer ja6jwn;
   private Framebuffer vL08pn;
   private Framebuffer b0f7;
   private Framebuffer ap3cs;
   private Framebuffer d1ftf;
   private Framebuffer cpbI390;
   private Framebuffer qX0FXQK;
   private final List<Framebuffer> wzkHxbR = new ArrayList<>();
   private final List<Framebuffer> euA3 = new ArrayList<>();
   private final List<Framebuffer> jIa7U = new ArrayList<>();
   private int dSQuh2c = -1;
   private int uMx63j = -1;
   private boolean h2856n8 = false;
   private boolean gyhQ5g = false;
   private int nMJg = -1;
   private int qmi4 = -1;
   private float e1h9xtB = 0.0F;
   private float oLyz = 0.0F;
   private float da2n = 0.0F;
   private float jFss1 = 0.0F;
   private float xbLl = 0.0F;
   private float pp1p = 0.0F;
   private float t3xj0Bj = 1.0F;
   private long nDsnny = 0L;
   private float qxk7 = 0.016666668F;
   private float gCjeXc;
   private float mAiR;
   private float dqBiJC;
   private long leQY5 = -10000L;
   private boolean bO6yN2 = false;
   private int aeVx1 = -1;
   private Framebuffer yZujih;
   private Framebuffer i4fzg;
   private Framebuffer syyuP6j;
   private long l0rHQiT = 0L;
   private float pCXhb = 0.016666668F;
   private float xwHo;
   private float a09tndM;
   private float rglp;
   private long mSyT = -10000L;
   private boolean sU022iZ = false;
   private String lHJ772W = "";

   private static ShaderProgramKey eFylrBZ(String name) {
      return new ShaderProgramKey(ResourceProvider.getShaderIdentifier(name), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
   }

   public static GlassHandsRenderer getInstance() {
      if (vjtjk8 == null) {
         vjtjk8 = new GlassHandsRenderer();
      }

      return vjtjk8;
   }

   public void captureBeforeHands(GlassHands module) {
      if (!this.mzp5U(module)) {
         this.invalidateState();
      } else {
         this.n5pwy();
         if (this.ja6jwn != null) {
            int currentDrawFbo = GL11.glGetInteger(36006);
            this.w7Np(this.ja6jwn);
            this.h2856n8 = true;
         }
      }
   }

   public void captureAfterHands(GlassHands module) {
      if (!this.mzp5U(module)) {
         this.invalidateState();
      } else {
         this.n5pwy();
         if (this.ja6jwn != null && this.vL08pn != null && this.b0f7 != null) {
            if (this.h2856n8) {
               this.w7Np(this.vL08pn);
               this.gyhQ5g = true;
            }
         }
      }
   }

   public void renderOverlayIfPending(GlassHands module) {
      if (this.gyhQ5g) {
         this.n5pwy();
         if (this.ja6jwn != null && this.vL08pn != null && this.b0f7 != null) {
            if (!this.mzp5U(module)) {
               this.invalidateState();
            } else {
               ShaderProgram maskShader = mc.getShaderLoader().getOrCreateProgram(KEY_MASK_DIFF);
               boolean haveMask;
               if (maskShader != null) {
                  haveMask = true;

                  try {
                     this.b0f7.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                     this.b0f7.clear();
                     this.b0f7.beginWrite(false);
                     RenderSystem.disableDepthTest();
                     RenderSystem.disableBlend();
                     RenderSystem.setShader(KEY_MASK_DIFF);
                     RenderSystem.setShaderTexture(0, this.ja6jwn.getColorAttachment());
                     RenderSystem.setShaderTexture(1, this.vL08pn.getColorAttachment());
                     int beforeDepth = this.ja6jwn.getDepthAttachment();
                     int afterDepth = this.vL08pn.getDepthAttachment();
                     if (beforeDepth != 0 && beforeDepth != this.nMJg) {
                        this.nbmk(beforeDepth);
                        this.nMJg = beforeDepth;
                     }

                     if (afterDepth != 0 && afterDepth != this.qmi4) {
                        this.nbmk(afterDepth);
                        this.qmi4 = afterDepth;
                     }

                     RenderSystem.setShaderTexture(2, beforeDepth);
                     RenderSystem.setShaderTexture(3, afterDepth);
                     this.y3HB();
                  } catch (Exception var18) {
                     System.out.println("[Hands] mask build failed: " + var18);
                     haveMask = false;
                  }
               } else {
                  System.out
                     .println(
                        D.k(
                           new int[]{
                              105,
                              146,
                              71,
                              152,
                              86,
                              169,
                              123,
                              214,
                              95,
                              187,
                              85,
                              157,
                              18,
                              169,
                              78,
                              151,
                              86,
                              191,
                              84,
                              214,
                              71,
                              180,
                              71,
                              128,
                              83,
                              179,
                              74,
                              151,
                              80,
                              182,
                              67,
                              214,
                              8230,
                              250,
                              1126,
                              1219,
                              1028,
                              1250,
                              1050,
                              1213,
                              18,
                              1179,
                              6,
                              1226,
                              1026,
                              1179,
                              1052,
                              1224,
                              1035,
                              250,
                              1051,
                              1219,
                              18,
                              1178,
                              1046,
                              1223,
                              1036,
                              1176,
                              1046,
                              1208,
                              1136
                           },
                           new int[]{50, 218, 38, 246}
                        )
                     );
                  haveMask = false;
               }

               RenderSystem.enableDepthTest();
               if (!haveMask && !module.mode.is("Блюр")) {
                  this.invalidateState();
               } else {
                  float glowValue = module.glow.getFloatValue();
                  float fillValue = module.fill.getFloatValue();
                  float alphaValue = module.alpha.getFloatValue();
                  float outlineValue = module.outline.getFloatValue();
                  int color1 = ColorProvider.getThemeColor();
                  int color2 = ColorProvider.getThemeColorTwo();
                  String currentMode = module.mode.getValue();
                  if (!currentMode.equals(this.lHJ772W)) {
                     this.sF6Ik8g(this.i4fzg);
                     this.sF6Ik8g(this.syyuP6j);
                     this.sF6Ik8g(this.d1ftf);
                     this.sF6Ik8g(this.cpbI390);
                     this.l0rHQiT = 0L;
                     this.nDsnny = 0L;
                     this.lHJ772W = currentMode;
                  }

                  if (module.mode.is("Блюр")) {
                     this.qvh6F(module);
                     this.invalidateState();
                  } else if (module.mode.is("Красивый")) {
                     this.ir2i2u(module, color1, color2, glowValue, fillValue, alphaValue, outlineValue);
                     this.invalidateState();
                  } else if (module.mode.is("Обводка")) {
                     this.acfm3at(module, color1, color2);
                     this.invalidateState();
                  } else if (module.mode.is("Шлейф")) {
                     this.buzF1(module, color1, color2);
                     this.invalidateState();
                  } else if (module.mode.is("Trail")) {
                     this.hHqcq9n(module, color1, color2);
                     this.invalidateState();
                  } else if (module.mode.is("Стекло")) {
                     this.fLa3xD(module, color1, color2);
                     this.invalidateState();
                  } else if (module.mode.is("Plasma")) {
                     this.e8UrbA(module, color1, color2);
                     this.invalidateState();
                  } else if (module.mode.is("Заливка")) {
                     this.gn1g1l(module, color1, color2);
                     this.invalidateState();
                  } else if (module.mode.is("Волна")) {
                     this.l2zPmo(module, color1, color2);
                     this.invalidateState();
                  } else {
                     boolean hasGlow = glowValue > 0.001F;
                     boolean hasFill = fillValue > 0.001F && alphaValue > 0.001F;
                     int blurredMaskTex = 0;
                     if (hasGlow) {
                        int iterations = Math.max(3, Math.min(8, 4 + Math.round(outlineValue * 0.7F)));
                        blurredMaskTex = this.enXMprk(iterations);
                     }

                     mc.getFramebuffer().beginWrite(true);
                     RenderSystem.enableBlend();
                     RenderSystem.colorMask(true, true, true, false);
                     RenderSystem.disableDepthTest();
                     if (hasGlow) {
                        ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_GLOW);
                        if (glowShader != null) {
                           RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
                           RenderSystem.setShader(KEY_GLOW);
                           RenderSystem.setShaderTexture(0, blurredMaskTex);
                           RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
                           int effectiveColor1 = color1;
                           int effectiveColor2 = color2;
                           if (module.mode.is("Свечение")
                              && module.glowItemColor.getValue()) {
                              int itemColor = this.dlq6fT();
                              effectiveColor1 = itemColor;
                              effectiveColor2 = itemColor;
                           }

                           this.yoQi(glowShader, "color", this.bd532Le(effectiveColor1), this.d9vBtn(effectiveColor1), this.oeab7t(effectiveColor1));
                           this.yoQi(glowShader, "color2", this.bd532Le(effectiveColor2), this.d9vBtn(effectiveColor2), this.oeab7t(effectiveColor2));
                           this.d0BTmy(glowShader, "exposure", 1.0F + glowValue * 1.8F);
                           this.y3HB();
                        }
                     }

                     if (hasFill) {
                        ShaderProgram overlayShader = mc.getShaderLoader().getOrCreateProgram(KEY_OVERLAY);
                        if (overlayShader != null) {
                           RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
                           RenderSystem.setShader(KEY_OVERLAY);
                           RenderSystem.setShaderTexture(0, this.b0f7.getColorAttachment());
                           int effectiveColor = color1;
                           if (module.mode.is("Свечение")
                              && module.glowItemColor.getValue()) {
                              effectiveColor = this.dlq6fT();
                           }

                           this.yoQi(overlayShader, "color", this.bd532Le(effectiveColor), this.d9vBtn(effectiveColor), this.oeab7t(effectiveColor));
                           this.d0BTmy(overlayShader, "fill", fillValue);
                           this.d0BTmy(overlayShader, "alpha", alphaValue);
                           this.y3HB();
                        }
                     }

                     this.c5Wx();
                     this.invalidateState();
                  }
               }
            }
         }
      }
   }

   public void invalidateState() {
      this.h2856n8 = false;
      this.gyhQ5g = false;
      this.nMJg = -1;
      this.qmi4 = -1;
      this.aeVx1 = -1;
   }

   private void qvh6F(GlassHands module) {
      int iterations = Math.max(1, Math.min(8, (int)module.blurStrength.getFloatValue()));
      int blurredBgTex = this.qwC4(this.ja6jwn.getColorAttachment(), iterations);
      int color1 = module.blurRainbow.getValue() ? this.yRFv(module.blurRainbowSpeed.getFloatValue())[0] : ColorProvider.getThemeColor();
      float tint = module.blurTint.getFloatValue();
      mc.getFramebuffer().beginWrite(false);
      RenderSystem.disableDepthTest();
      RenderSystem.enableBlend();
      ShaderProgram sh = mc.getShaderLoader().getOrCreateProgram(KEY_BLUR_HANDS);
      if (sh != null) {
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_BLUR_HANDS);
         RenderSystem.setShaderTexture(0, this.ja6jwn.getColorAttachment());
         RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
         this.yoQi(sh, "tintColor", this.bd532Le(color1), this.d9vBtn(color1), this.oeab7t(color1));
         this.d0BTmy(sh, "tintStrength", 0.0F);
         this.y3HB();
         RenderSystem.setShader(KEY_BLUR_HANDS);
         RenderSystem.setShaderTexture(0, blurredBgTex);
         RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
         this.yoQi(sh, "tintColor", this.bd532Le(color1), this.d9vBtn(color1), this.oeab7t(color1));
         this.d0BTmy(sh, "tintStrength", tint);
         this.y3HB();
      }

      this.c5Wx();
   }

   private void acfm3at(GlassHands module, int color1, int color2) {
      mc.getFramebuffer().beginWrite(true);
      RenderSystem.enableBlend();
      RenderSystem.colorMask(true, true, true, false);
      RenderSystem.disableDepthTest();
      ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_GLOW);
      if (glowShader != null) {
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_GLOW);
         RenderSystem.setShaderTexture(0, this.b0f7.getColorAttachment());
         RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
         this.yoQi(glowShader, "color", this.bd532Le(color1), this.d9vBtn(color1), this.oeab7t(color1));
         this.yoQi(glowShader, "color2", this.bd532Le(color2), this.d9vBtn(color2), this.oeab7t(color2));
         this.d0BTmy(glowShader, "exposure", 1.0F);
         this.y3HB();
      }

      this.c5Wx();
   }

   private void buzF1(GlassHands module, int color1, int color2) {
      ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(KEY_TRAIL);
      if (shader != null) {
         int iterations = Math.max(3, Math.min(10, (int)module.trailBlur.getFloatValue()));
         int blurredMaskTex = this.enXMprk(iterations);
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_TRAIL);
         RenderSystem.setShaderTexture(0, this.b0f7.getColorAttachment());
         RenderSystem.setShaderTexture(1, blurredMaskTex);
         int fw = Math.max(1, mc.getWindow().getFramebufferWidth());
         int fh = Math.max(1, mc.getWindow().getFramebufferHeight());
         this.jfsfLej(shader, "texelSize", 1.0F / fw, 1.0F / fh);
         this.yoQi(shader, "color", this.bd532Le(color1), this.d9vBtn(color1), this.oeab7t(color1));
         this.yoQi(shader, "color2", this.bd532Le(color2), this.d9vBtn(color2), this.oeab7t(color2));
         this.d0BTmy(shader, "trailIntensity", module.trailIntensity.getFloatValue());
         this.d0BTmy(shader, "glowSize", module.trailGlowSize.getFloatValue());
         this.d0BTmy(shader, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
         this.d0BTmy(shader, "rainbowEnabled", module.rainbow.getValue() ? 1.0F : 0.0F);
         this.d0BTmy(shader, "rainbowSpeed", module.rainbowSpeed.getFloatValue());
         this.y3HB();
         this.c5Wx();
      }
   }

   public void pulseAttack(float direction) {
      this.pp1p = 1.0F;
      this.t3xj0Bj = direction >= 0.0F ? 1.0F : -1.0F;
   }

   private int dlq6fT() {
      if (mc.player == null) {
         return ColorProvider.getThemeColor();
      } else {
         ItemStack stack = mc.player.getMainHandStack();
         if (stack.isEmpty()) {
            stack = mc.player.getOffHandStack();
         }

         if (stack.isEmpty()) {
            return ColorProvider.getThemeColor();
         } else {
            Item item = stack.getItem();
            String path = item.toString().toLowerCase();
            if (path.contains("diamond")) {
               return 5627360;
            } else if (path.contains("netherite")) {
               return 5982823;
            } else if (path.contains("golden") || path.contains("gold")) {
               return 16766042;
            } else if (path.contains("iron")) {
               return 14212840;
            } else if (path.contains("stone")) {
               return 9079434;
            } else if (path.contains("wood")) {
               return 12157509;
            } else if (path.contains("emerald")) {
               return 3526767;
            } else if (path.contains("redstone")) {
               return 14826299;
            } else if (path.contains("lapis")) {
               return 3233492;
            } else if (path.contains("copper")) {
               return 13728325;
            } else if (path.contains("amethyst")) {
               return 11562239;
            } else if (path.contains("ender") || path.contains("prismarine")) {
               return 4579520;
            } else {
               return path.contains("blaze") ? 16747050 : ColorProvider.getThemeColor();
            }
         }
      }
   }

   private void hHqcq9n(GlassHands module, int color1, int color2) {
      ShaderProgram accumShader = mc.getShaderLoader().getOrCreateProgram(KEY_TRAIL_ACCUMULATE);
      if (accumShader != null && this.ap3cs != null) {
         this.xbLl += 0.016F;
         float currentYaw = mc.player != null ? mc.player.getYaw() : 0.0F;
         float currentPitch = mc.player != null ? mc.player.getPitch() : 0.0F;
         float deltaYaw = currentYaw - this.e1h9xtB;
         float deltaPitch = currentPitch - this.oLyz;
         this.e1h9xtB = currentYaw;
         this.oLyz = currentPitch;

         while (deltaYaw > 180.0F) {
            deltaYaw -= 360.0F;
         }

         while (deltaYaw < -180.0F) {
            deltaYaw += 360.0F;
         }

         this.da2n = this.da2n * 0.85F + deltaYaw * 0.15F;
         this.jFss1 = this.jFss1 * 0.85F + deltaPitch * 0.15F;
         this.pp1p = Math.max(0.0F, this.pp1p - 0.045F);
         int fw = Math.max(1, mc.getWindow().getFramebufferWidth());
         int fh = Math.max(1, mc.getWindow().getFramebufferHeight());
         float cameraInfluence = module.trailCamera.getFloatValue();
         float camX = this.da2n * -3.5E-4F * cameraInfluence;
         float camY = this.jFss1 * 3.5E-4F * cameraInfluence;
         Framebuffer tempBuffer = new SimpleFramebuffer(fw, fh, false);
         tempBuffer.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         tempBuffer.clear();
         tempBuffer.beginWrite(false);
         RenderSystem.disableDepthTest();
         RenderSystem.disableBlend();
         RenderSystem.setShader(KEY_TRAIL_ACCUMULATE);
         RenderSystem.setShaderTexture(0, this.ap3cs.getColorAttachment());
         RenderSystem.setShaderTexture(1, this.vL08pn.getColorAttachment());
         RenderSystem.setShaderTexture(2, this.b0f7.getColorAttachment());
         this.gN7h(accumShader, "resolution", fw, fh, this.xbLl, module.trailIntensityM.getFloatValue());
         this.gN7h(accumShader, "glowColor", this.bd532Le(color1), this.d9vBtn(color1), this.oeab7t(color1), 1.0F);
         this.gN7h(
            accumShader,
            "settings",
            module.trailItemColor.getValue() ? 1.0F : 0.0F,
            module.trailSpeed.getFloatValue(),
            module.trailLength.getFloatValue(),
            module.trailSoftness.getFloatValue()
         );
         this.gN7h(
            accumShader, "settings2", module.trailBlurRadius.getFloatValue(), module.trailSmoke.getFloatValue(), module.trailAttack.getFloatValue(), 0.0F
         );
         this.gN7h(accumShader, "reserved", this.pp1p, this.t3xj0Bj, camX, camY);
         this.y3HB();
         GL30.glBindFramebuffer(36008, tempBuffer.fbo);
         GL30.glBindFramebuffer(36009, this.ap3cs.fbo);
         GL30.glBlitFramebuffer(0, 0, fw, fh, 0, 0, fw, fh, 16384, 9728);
         tempBuffer.delete();
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShaderTexture(0, this.ap3cs.getColorAttachment());
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
         this.y3HB();
         this.c5Wx();
      }
   }

   private int qwC4(int sourceTex, int iterations) {
      this.xMXPY3(iterations);
      if (this.wzkHxbR.isEmpty()) {
         return sourceTex;
      } else {
         ShaderProgram downShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_DOWN);
         ShaderProgram upShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_UP);
         if (downShader != null && upShader != null) {
            int currentTex = sourceTex;

            for (int i = 0; i < iterations; i++) {
               Framebuffer dst = this.wzkHxbR.get(i);
               dst.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               dst.clear();
               dst.beginWrite(true);
               RenderSystem.setShader(KEY_KAWASE_DOWN);
               RenderSystem.setShaderTexture(0, currentTex);
               this.rNv7e2b(downShader, dst.textureWidth, dst.textureHeight, 1.0F + i);
               this.y3HB();
               currentTex = dst.getColorAttachment();
            }

            for (int i = iterations - 1; i >= 1; i--) {
               Framebuffer dst = this.wzkHxbR.get(i - 1);
               dst.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               dst.clear();
               dst.beginWrite(true);
               RenderSystem.setShader(KEY_KAWASE_UP);
               RenderSystem.setShaderTexture(0, currentTex);
               this.rNv7e2b(upShader, dst.textureWidth, dst.textureHeight, 1.0F + i);
               this.yoQi(upShader, "color", 1.0F, 1.0F, 1.0F);
               this.y3HB();
               currentTex = dst.getColorAttachment();
            }

            mc.getFramebuffer().beginWrite(true);
            return currentTex;
         } else {
            return sourceTex;
         }
      }
   }

   private void ir2i2u(GlassHands module, int color1, int color2, float glowValue, float fillValue, float alphaValue, float outlineValue) {
      ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(KEY_PRETTY);
      if (shader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(KEY_PRETTY);
         RenderSystem.setShaderTexture(0, this.b0f7.getColorAttachment());
         int fw = Math.max(1, mc.getWindow().getFramebufferWidth());
         int fh = Math.max(1, mc.getWindow().getFramebufferHeight());
         this.jfsfLej(shader, "texelSize", 1.0F / fw, 1.0F / fh);
         this.yoQi(shader, "color", this.bd532Le(color1), this.d9vBtn(color1), this.oeab7t(color1));
         this.yoQi(shader, "color2", this.bd532Le(color2), this.d9vBtn(color2), this.oeab7t(color2));
         this.d0BTmy(shader, "time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
         this.d0BTmy(shader, "speed", module.waveSpeed.getFloatValue());
         this.d0BTmy(shader, "scale", module.waveScale.getFloatValue());
         this.d0BTmy(shader, "outline", outlineValue);
         this.d0BTmy(shader, "glow", glowValue);
         this.d0BTmy(shader, "fill", fillValue);
         this.d0BTmy(shader, "alpha", alphaValue);
         this.y3HB();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         this.c5Wx();
      }
   }

   private int enXMprk(int iterations) {
      return this.qwC4(this.b0f7.getColorAttachment(), iterations);
   }

   private void w7Np(Framebuffer target) {
      int currentFbo = GL11.glGetInteger(36006);
      int sourceFbo = currentFbo != 0 ? currentFbo : mc.getFramebuffer().fbo;
      GL30.glBindFramebuffer(36008, sourceFbo);
      GL30.glBindFramebuffer(36009, target.fbo);
      GL30.glBlitFramebuffer(0, 0, this.dSQuh2c, this.uMx63j, 0, 0, this.dSQuh2c, this.uMx63j, 16640, 9728);
      GL30.glBindFramebuffer(36160, currentFbo);
   }

   private void nbmk(int depthTex) {
      RenderSystem.bindTexture(depthTex);
      GL11.glTexParameteri(3553, 34892, 0);
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
      RenderSystem.bindTexture(0);
   }

   private void n5pwy() {
      int w = mc.getWindow().getFramebufferWidth();
      int h = mc.getWindow().getFramebufferHeight();
      if (w != this.dSQuh2c || h != this.uMx63j || this.ja6jwn == null || this.vL08pn == null || this.b0f7 == null || this.ap3cs == null) {
         this.hmx6();
         this.ja6jwn = new SimpleFramebuffer(w, h, true);
         this.vL08pn = new SimpleFramebuffer(w, h, true);
         this.b0f7 = new SimpleFramebuffer(w, h, true);
         this.ap3cs = new SimpleFramebuffer(w, h, false);
         this.dSQuh2c = w;
         this.uMx63j = h;
         this.nMJg = -1;
         this.qmi4 = -1;
      }
   }

   private void hmx6() {
      if (this.ja6jwn != null) {
         this.ja6jwn.delete();
         this.ja6jwn = null;
      }

      if (this.vL08pn != null) {
         this.vL08pn.delete();
         this.vL08pn = null;
      }

      if (this.b0f7 != null) {
         this.b0f7.delete();
         this.b0f7 = null;
      }

      if (this.ap3cs != null) {
         this.ap3cs.delete();
         this.ap3cs = null;
      }

      if (this.d1ftf != null) {
         this.d1ftf.delete();
         this.d1ftf = null;
      }

      if (this.cpbI390 != null) {
         this.cpbI390.delete();
         this.cpbI390 = null;
      }

      if (this.qX0FXQK != null) {
         this.qX0FXQK.delete();
         this.qX0FXQK = null;
      }

      if (this.yZujih != null) {
         this.yZujih.delete();
         this.yZujih = null;
      }

      if (this.i4fzg != null) {
         this.i4fzg.delete();
         this.i4fzg = null;
      }

      if (this.syyuP6j != null) {
         this.syyuP6j.delete();
         this.syyuP6j = null;
      }

      for (Framebuffer fb : this.wzkHxbR) {
         fb.delete();
      }

      this.wzkHxbR.clear();

      for (Framebuffer fb : this.jIa7U) {
         fb.delete();
      }

      this.jIa7U.clear();
   }

   private void xMXPY3(int iterations) {
      while (this.wzkHxbR.size() > iterations) {
         int last = this.wzkHxbR.size() - 1;
         this.wzkHxbR.get(last).delete();
         this.wzkHxbR.remove(last);
      }

      for (int i = 0; i < iterations; i++) {
         int w = Math.max(2, this.dSQuh2c >> i + 1);
         int h = Math.max(2, this.uMx63j >> i + 1);
         if (i >= this.wzkHxbR.size()) {
            Framebuffer fb = new SimpleFramebuffer(w, h, false);
            this.s9t2Ylg(fb);
            this.wzkHxbR.add(fb);
         } else {
            Framebuffer fb = this.wzkHxbR.get(i);
            if (fb.textureWidth != w || fb.textureHeight != h) {
               fb.delete();
               Framebuffer var8 = new SimpleFramebuffer(w, h, false);
               this.s9t2Ylg(var8);
               this.wzkHxbR.set(i, var8);
            }
         }
      }
   }

   private void s9t2Ylg(Framebuffer fb) {
      RenderSystem.bindTexture(fb.getColorAttachment());
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      RenderSystem.bindTexture(0);
   }

   private void sF6Ik8g(Framebuffer buf) {
      if (buf != null) {
         buf.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         buf.clear();
      }
   }

   private void c5Wx() {
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
      RenderSystem.setShaderTexture(3, 0);
      mc.getFramebuffer().beginWrite(true);
   }

   private void fLa3xD(GlassHands module, int color1, int color2) {
      int iterations = 3;
      int blurredBgTex = this.qwC4(this.ja6jwn.getColorAttachment(), iterations);
      ShaderProgram glassShader = mc.getShaderLoader().getOrCreateProgram(KEY_GLASS);
      if (glassShader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_GLASS);
         RenderSystem.setShaderTexture(0, blurredBgTex);
         RenderSystem.setShaderTexture(1, this.vL08pn.getColorAttachment());
         RenderSystem.setShaderTexture(2, this.b0f7.getColorAttachment());
         this.d0BTmy(glassShader, "mixFactor", module.glassMixFactor.getFloatValue());
         this.y3HB();
         RenderSystem.defaultBlendFunc();
         if (module.glassGlowEnabled.getValue() && module.glassOuterGlow.getValue()) {
            this.mfcXJD9(module, color1, color2);
         }

         this.c5Wx();
      }
   }

   private void mfcXJD9(GlassHands module, int color1, int color2) {
      int iterations = Math.max(1, Math.min(6, (int)module.glassGlowRadius.getFloatValue()));
      int glowTex = this.enXMprk(iterations);
      int c1;
      int c2;
      if (module.glassRainbow.getValue()) {
         int[] rb = this.yRFv(module.glassRainbowSpeed.getFloatValue());
         c1 = rb[0];
         c2 = rb[1];
      } else {
         c1 = module.glassGlowColor1.getValue();
         c2 = module.glassGlowColor2.getValue();
      }

      float[] col1 = new float[]{this.bd532Le(c1), this.d9vBtn(c1), this.oeab7t(c1)};
      float[] col2 = new float[]{this.bd532Le(c2), this.d9vBtn(c2), this.oeab7t(c2)};
      ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_GLASS_GLOW);
      if (glowShader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_GLASS_GLOW);
         RenderSystem.setShaderTexture(0, glowTex);
         RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
         this.yoQi(glowShader, "glowColor1", col1[0], col1[1], col1[2]);
         this.yoQi(glowShader, "glowColor2", col2[0], col2[1], col2[2]);
         this.d0BTmy(glowShader, "exposure", module.glassGlowExposure.getFloatValue());
         this.y3HB();
         RenderSystem.defaultBlendFunc();
      }
   }

   private void e8UrbA(GlassHands module, int color1, int color2) {
      ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(KEY_PLASMA);
      if (shader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(KEY_PLASMA);
         RenderSystem.setShaderTexture(0, this.b0f7.getColorAttachment());
         float time = (float)(System.currentTimeMillis() % 100000L) / 1000.0F;
         this.d0BTmy(shader, "iTime", time * module.plasmaSpeed.getFloatValue());
         this.yoQi(shader, "uColor", this.bd532Le(color1), this.d9vBtn(color1), this.oeab7t(color1));
         this.d0BTmy(shader, "plasmaScale", module.plasmaScale.getFloatValue());
         this.d0BTmy(shader, "uShowStars", module.plasmaStars.getValue() ? 1.0F : 0.0F);
         this.y3HB();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         this.c5Wx();
      }
   }

   private void l2zPmo(GlassHands module, int color1, int color2) {
      ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(KEY_WAVE);
      if (shader != null) {
         if (module.waveRainbow.getValue() && !module.waveAutoColor.getValue()) {
            int[] rb = this.yRFv(module.waveRainbowSpeed.getFloatValue());
            color1 = rb[0];
            color2 = rb[0];
         }

         this.yZujih = this.gYfkwi(this.yZujih, false, 1);
         this.yZujih.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.yZujih.clear();
         this.yZujih.beginWrite(false);
         RenderSystem.disableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(KEY_WAVE);
         RenderSystem.setShaderTexture(0, this.b0f7.getColorAttachment());
         float t = (float)(System.currentTimeMillis() % 100000L) / 1000.0F;
         this.d0BTmy(shader, "time", t);
         this.yoQi(shader, "color1", this.bd532Le(color1), this.d9vBtn(color1), this.oeab7t(color1));
         this.yoQi(shader, "color2", this.bd532Le(color2), this.d9vBtn(color2), this.oeab7t(color2));
         this.d0BTmy(shader, "waveSpeedX", module.waveSpeedX.getFloatValue());
         this.d0BTmy(shader, "waveSpeedY", module.waveSpeedY.getFloatValue());
         this.d0BTmy(shader, "waveScale", module.waveScaleM.getFloatValue());
         this.d0BTmy(shader, "waveDensity", module.waveDensity.getFloatValue());
         this.d0BTmy(shader, "waveGlow", module.waveGlow.getFloatValue());
         this.d0BTmy(shader, "fillAlpha", 1.0F);
         this.d0BTmy(shader, "modelVisibility", module.waveModelVisibility.getFloatValue());
         RenderSystem.setShaderTexture(1, this.vL08pn.getColorAttachment());
         this.y3HB();
         if (module.waveGlowEnabled.getValue() && module.waveOuterGlow.getValue()) {
            this.q60j61j(module, color1, color2);
         }

         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(1, 771);
         RenderSystem.disableDepthTest();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, module.waveFillAlpha.getFloatValue());
         RenderSystem.setShaderTexture(0, this.yZujih.getColorAttachment());
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
         this.y3HB();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         this.c5Wx();
      }
   }

   private void q60j61j(GlassHands module, int color1, int color2) {
      int iter = (int)module.waveGlowRadius.getFloatValue();
      int bloomSource = module.waveAutoColor.getValue() ? this.yZujih.getColorAttachment() : this.b0f7.getColorAttachment();
      this.yvxsMQ3(iter);
      ShaderProgram downShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_DOWN);
      ShaderProgram upShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_UP);
      if (downShader != null && upShader != null) {
         int cur = bloomSource;

         for (int i = 0; i < iter; i++) {
            Framebuffer b = this.jIa7U.get(i);
            b.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            b.clear();
            b.beginWrite(true);
            RenderSystem.setShader(KEY_KAWASE_DOWN);
            RenderSystem.setShaderTexture(0, cur);
            this.rNv7e2b(downShader, b.textureWidth, b.textureHeight, 1.0F + i);
            this.y3HB();
            cur = b.getColorAttachment();
         }

         for (int i = iter - 1; i >= 1; i--) {
            Framebuffer b = this.jIa7U.get(i - 1);
            b.beginWrite(true);
            RenderSystem.setShader(KEY_KAWASE_UP);
            RenderSystem.setShaderTexture(0, cur);
            this.rNv7e2b(upShader, b.textureWidth, b.textureHeight, 1.0F + i);
            this.yoQi(upShader, "color", 1.0F, 1.0F, 1.0F);
            this.y3HB();
            cur = b.getColorAttachment();
         }

         this.aeVx1 = cur;
         int g1;
         int g2;
         if (module.waveRainbow.getValue() && !module.waveAutoColor.getValue()) {
            g1 = color1;
            g2 = color1;
         } else {
            g1 = module.waveGlowColor1.getValue();
            g2 = module.waveGlowColor2.getValue();
         }

         float[] c1 = new float[]{this.bd532Le(g1), this.d9vBtn(g1), this.oeab7t(g1)};
         float[] c2 = new float[]{this.bd532Le(g2), this.d9vBtn(g2), this.oeab7t(g2)};
         if (module.waveOuterGlow.getValue()) {
            if (module.waveTrailEnabled.getValue()) {
               this.hYmj0m4(module, cur, c1, c2);
            } else {
               mc.getFramebuffer().beginWrite(true);
               RenderSystem.enableBlend();
               RenderSystem.blendFunc(770, 1);
               ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_GLOW);
               if (glowShader != null) {
                  RenderSystem.setShader(KEY_FILL_GLOW);
                  RenderSystem.setShaderTexture(0, cur);
                  RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
                  this.yoQi(glowShader, "glowColor1", c1[0], c1[1], c1[2]);
                  this.yoQi(glowShader, "glowColor2", c2[0], c2[1], c2[2]);
                  this.d0BTmy(glowShader, "exposure", module.waveGlowExposure.getFloatValue());
                  this.d0BTmy(glowShader, "autoColor", module.waveAutoColor.getValue() ? 1.0F : 0.0F);
                  this.d0BTmy(glowShader, "saturation", module.waveSaturation.getFloatValue());
                  this.y3HB();
               }

               RenderSystem.defaultBlendFunc();
            }
         }
      }
   }

   private void hYmj0m4(GlassHands module, int bloomTex, float[] c1, float[] c2) {
      this.i4fzg = this.gYfkwi(this.i4fzg, false, 2);
      this.syyuP6j = this.gYfkwi(this.syyuP6j, false, 2);
      long now = System.currentTimeMillis();
      float rawDt = this.l0rHQiT > 0L ? (float)(now - this.l0rHQiT) / 1000.0F : 0.016666668F;
      this.l0rHQiT = now;
      if (rawDt <= 0.0F || rawDt > 0.05F) {
         rawDt = this.pCXhb;
      }

      rawDt = Math.max(0.0069444445F, Math.min(0.016666668F, rawDt));
      this.pCXhb = this.pCXhb + (rawDt - this.pCXhb) * 0.08F;
      float dt = this.pCXhb;
      float t = (float)(now % 100000L) / 1000.0F;
      float swayFreq = 3.6F;
      float upDelta = module.waveTrailRise.getFloatValue() * dt;
      float amp = module.waveTrailSway.getFloatValue();
      float swayDelta = (float)(Math.sin(t * swayFreq) - Math.sin((t - dt) * swayFreq)) * amp;
      float trailSmooth = 1.0F - (float)Math.exp(-dt * 8.0F);
      this.xwHo = this.xwHo + (upDelta - this.xwHo) * trailSmooth;
      this.a09tndM = this.a09tndM + (swayDelta - this.a09tndM) * trailSmooth;
      boolean swinging = mc.player != null && mc.player.handSwingProgress > 0.0F;
      if (module.waveTrailBurst.getValue() && swinging && !this.sU022iZ) {
         this.mSyT = now;
      }

      this.sU022iZ = swinging;
      float burstAge = (float)(now - this.mSyT) / 1000.0F;
      float burst = Math.max(0.0F, 1.0F - burstAge / 0.45F);
      this.rglp = this.rglp + (burst - this.rglp) * (1.0F - (float)Math.exp(-dt * 6.0F));
      float fadeAdd = this.rglp * module.waveTrailBurstPower.getFloatValue() * 0.012F;
      this.syyuP6j.beginWrite(false);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glClear(16384);
      RenderSystem.disableBlend();
      ShaderProgram fadeShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_TRAIL_FADE);
      if (fadeShader != null) {
         RenderSystem.setShader(KEY_FILL_TRAIL_FADE);
         RenderSystem.setShaderTexture(0, this.i4fzg.getColorAttachment());
         this.jfsfLej(fadeShader, "offset", this.a09tndM, this.xwHo);
         this.d0BTmy(fadeShader, "fade", module.waveTrailFade.getFloatValue() + fadeAdd);
         this.d0BTmy(fadeShader, "t", t);
         this.d0BTmy(fadeShader, "dt", dt);
         this.d0BTmy(fadeShader, "turb", module.waveTrailTurb.getFloatValue());
         this.d0BTmy(fadeShader, "flickAmp", module.waveTrailFlicker.getFloatValue());
         this.jfsfLej(fadeShader, "texSize", this.i4fzg.textureWidth, this.i4fzg.textureHeight);
         this.y3HB();
      }

      RenderSystem.enableBlend();
      RenderSystem.blendFunc(1, 771);
      ShaderProgram colorShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_TRAIL_COLOR);
      if (colorShader != null) {
         RenderSystem.setShader(KEY_FILL_TRAIL_COLOR);
         RenderSystem.setShaderTexture(0, bloomTex);
         this.yoQi(colorShader, "glowColor1", c1[0], c1[1], c1[2]);
         this.yoQi(colorShader, "glowColor2", c2[0], c2[1], c2[2]);
         this.d0BTmy(colorShader, "exposure", module.waveGlowExposure.getFloatValue());
         this.d0BTmy(colorShader, "autoColor", module.waveAutoColor.getValue() ? 1.0F : 0.0F);
         this.d0BTmy(colorShader, "saturation", module.waveSaturation.getFloatValue());
         this.y3HB();
      }

      if (module.waveTrailModel.getValue()) {
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 771);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, module.waveTrailModelAlpha.getFloatValue());
         RenderSystem.setShaderTexture(0, this.qX0FXQK != null ? this.qX0FXQK.getColorAttachment() : this.yZujih.getColorAttachment());
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
         this.y3HB();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      mc.getFramebuffer().beginWrite(true);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(1, 771);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.syyuP6j.getColorAttachment());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
      this.y3HB();
      RenderSystem.defaultBlendFunc();
      Framebuffer tmp = this.i4fzg;
      this.i4fzg = this.syyuP6j;
      this.syyuP6j = tmp;
   }

   private void gn1g1l(GlassHands module, int color1, int color2) {
      if (module.fillGlowEnabled.getValue() || module.fillOutlineEnabled.getValue()) {
         this.otAc(module, color1, color2);
      }

      if (module.fillGlassMode.getValue()) {
         this.jSlIK(module);
      } else {
         this.j8o4o(module);
      }

      this.c5Wx();
   }

   private void j8o4o(GlassHands module) {
      ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL);
      if (shader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(KEY_FILL);
         RenderSystem.setShaderTexture(0, this.vL08pn.getColorAttachment());
         RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
         int fc = module.fillRainbow.getValue() ? this.yRFv(module.fillRainbowSpeed.getFloatValue())[0] : module.fillColor.getValue();
         this.yoQi(shader, "fillColor", this.bd532Le(fc), this.d9vBtn(fc), this.oeab7t(fc));
         this.d0BTmy(shader, "fillAlpha", module.fillAlpha.getFloatValue());
         this.d0BTmy(shader, "keepShading", module.fillKeepShading.getValue() ? 1.0F : 0.0F);
         this.d0BTmy(shader, "shadingStrength", module.fillShadingStrength.getFloatValue());
         this.y3HB();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
      }
   }

   private void jSlIK(GlassHands module) {
      int iterations = 3;
      int blurredBgTex = this.qwC4(this.ja6jwn.getColorAttachment(), iterations);
      ShaderProgram glassShader = mc.getShaderLoader().getOrCreateProgram(KEY_GLASS);
      if (glassShader != null) {
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
         RenderSystem.setShader(KEY_GLASS);
         RenderSystem.setShaderTexture(0, blurredBgTex);
         RenderSystem.setShaderTexture(1, this.vL08pn.getColorAttachment());
         RenderSystem.setShaderTexture(2, this.b0f7.getColorAttachment());
         this.d0BTmy(glassShader, "mixFactor", module.fillGlassMix.getFloatValue());
         this.y3HB();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
      }
   }

   private void otAc(GlassHands module, int color1, int color2) {
      boolean doBloom = module.fillGlowEnabled.getValue() && module.fillOuterGlow.getValue();
      if (doBloom) {
         this.fnx5(module);
      } else if (module.fillOutlineEnabled.getValue() && module.fillAutoColor.getValue()) {
         this.u2vM(module);
      }

      if (module.fillOutlineEnabled.getValue()) {
         this.oJon(module);
      }
   }

   private void fnx5(GlassHands module) {
      int iter = (int)module.fillGlowRadius.getFloatValue();
      this.qX0FXQK = this.gYfkwi(this.qX0FXQK, false, 1);
      this.qX0FXQK.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.qX0FXQK.clear();
      this.qX0FXQK.beginWrite(false);
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.vL08pn.getColorAttachment());
      RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
      ShaderProgram colorMaskShader = mc.getShaderLoader()
         .getOrCreateProgram(
            new ShaderProgramKey(ResourceProvider.getShaderIdentifier("glass_hands_color_mask"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY)
         );
      if (colorMaskShader != null) {
         RenderSystem.setShader(
            new ShaderProgramKey(ResourceProvider.getShaderIdentifier("glass_hands_color_mask"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY)
         );
         this.y3HB();
      }

      int cur = this.qX0FXQK.getColorAttachment();
      this.yvxsMQ3(iter);
      ShaderProgram downShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_DOWN);
      ShaderProgram upShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_UP);
      if (downShader != null && upShader != null) {
         for (int i = 0; i < iter; i++) {
            Framebuffer b = this.jIa7U.get(i);
            b.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            b.clear();
            b.beginWrite(true);
            RenderSystem.setShader(KEY_KAWASE_DOWN);
            RenderSystem.setShaderTexture(0, cur);
            this.rNv7e2b(downShader, b.textureWidth, b.textureHeight, 1.0F + i);
            this.y3HB();
            cur = b.getColorAttachment();
         }

         for (int i = iter - 1; i >= 1; i--) {
            Framebuffer b = this.jIa7U.get(i - 1);
            b.beginWrite(true);
            RenderSystem.setShader(KEY_KAWASE_UP);
            RenderSystem.setShaderTexture(0, cur);
            this.rNv7e2b(upShader, b.textureWidth, b.textureHeight, 1.0F + i);
            this.yoQi(upShader, "color", 1.0F, 1.0F, 1.0F);
            this.y3HB();
            cur = b.getColorAttachment();
         }

         this.aeVx1 = cur;
         int g1;
         int g2;
         if (module.fillRainbow.getValue()) {
            int[] rb = this.yRFv(module.fillRainbowSpeed.getFloatValue());
            g1 = rb[0];
            g2 = rb[1];
         } else {
            g1 = module.fillGlowColor1.getValue();
            g2 = module.fillGlowColor2.getValue();
         }

         float[] c1 = new float[]{this.bd532Le(g1), this.d9vBtn(g1), this.oeab7t(g1)};
         float[] c2 = new float[]{this.bd532Le(g2), this.d9vBtn(g2), this.oeab7t(g2)};
         if (module.fillTrailEnabled.getValue()) {
            this.dTBwr(module, cur, c1, c2);
         } else {
            mc.getFramebuffer().beginWrite(true);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_GLOW);
            if (glowShader != null) {
               RenderSystem.setShader(KEY_FILL_GLOW);
               RenderSystem.setShaderTexture(0, cur);
               RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
               this.yoQi(glowShader, "glowColor1", c1[0], c1[1], c1[2]);
               this.yoQi(glowShader, "glowColor2", c2[0], c2[1], c2[2]);
               this.d0BTmy(glowShader, "exposure", module.fillGlowExposure.getFloatValue());
               this.d0BTmy(glowShader, "autoColor", module.fillAutoColor.getValue() ? 1.0F : 0.0F);
               this.d0BTmy(glowShader, "saturation", module.fillSaturation.getFloatValue());
               this.y3HB();
            }

            RenderSystem.defaultBlendFunc();
         }
      }
   }

   private void u2vM(GlassHands module) {
      int iter = 3;
      this.qX0FXQK = this.gYfkwi(this.qX0FXQK, false, 1);
      this.qX0FXQK.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.qX0FXQK.clear();
      this.qX0FXQK.beginWrite(false);
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.vL08pn.getColorAttachment());
      RenderSystem.setShaderTexture(1, this.b0f7.getColorAttachment());
      ShaderProgram colorMaskShader = mc.getShaderLoader()
         .getOrCreateProgram(
            new ShaderProgramKey(ResourceProvider.getShaderIdentifier("glass_hands_color_mask"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY)
         );
      if (colorMaskShader != null) {
         RenderSystem.setShader(
            new ShaderProgramKey(ResourceProvider.getShaderIdentifier("glass_hands_color_mask"), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY)
         );
         this.y3HB();
      }

      this.yvxsMQ3(iter);
      int cur = this.qX0FXQK.getColorAttachment();
      ShaderProgram downShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_DOWN);
      ShaderProgram upShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_UP);
      if (downShader != null && upShader != null) {
         for (int i = 0; i < iter; i++) {
            Framebuffer b = this.jIa7U.get(i);
            b.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            b.clear();
            b.beginWrite(true);
            RenderSystem.setShader(KEY_KAWASE_DOWN);
            RenderSystem.setShaderTexture(0, cur);
            this.rNv7e2b(downShader, b.textureWidth, b.textureHeight, 1.0F + i);
            this.y3HB();
            cur = b.getColorAttachment();
         }

         for (int i = iter - 1; i >= 1; i--) {
            Framebuffer b = this.jIa7U.get(i - 1);
            b.beginWrite(true);
            RenderSystem.setShader(KEY_KAWASE_UP);
            RenderSystem.setShaderTexture(0, cur);
            this.rNv7e2b(upShader, b.textureWidth, b.textureHeight, 1.0F + i);
            this.yoQi(upShader, "color", 1.0F, 1.0F, 1.0F);
            this.y3HB();
            cur = b.getColorAttachment();
         }

         this.aeVx1 = cur;
         mc.getFramebuffer().beginWrite(true);
      }
   }

   private void dTBwr(GlassHands module, int bloomTex, float[] c1, float[] c2) {
      this.d1ftf = this.gYfkwi(this.d1ftf, false, 2);
      this.cpbI390 = this.gYfkwi(this.cpbI390, false, 2);
      long now = System.currentTimeMillis();
      float rawDt = this.nDsnny > 0L ? (float)(now - this.nDsnny) / 1000.0F : 0.016666668F;
      this.nDsnny = now;
      if (rawDt <= 0.0F || rawDt > 0.05F) {
         rawDt = this.qxk7;
      }

      rawDt = Math.max(0.0069444445F, Math.min(0.016666668F, rawDt));
      this.qxk7 = this.qxk7 + (rawDt - this.qxk7) * 0.08F;
      float dt = this.qxk7;
      float t = (float)(now % 100000L) / 1000.0F;
      float swayFreq = 3.6F;
      float upDelta = module.fillTrailRise.getFloatValue() * dt;
      float amp = module.fillTrailSway.getFloatValue();
      float swayDelta = (float)(Math.sin(t * swayFreq) - Math.sin((t - dt) * swayFreq)) * amp;
      float trailSmooth = 1.0F - (float)Math.exp(-dt * 8.0F);
      this.gCjeXc = this.gCjeXc + (upDelta - this.gCjeXc) * trailSmooth;
      this.mAiR = this.mAiR + (swayDelta - this.mAiR) * trailSmooth;
      boolean swinging = mc.player != null && mc.player.handSwingProgress > 0.0F;
      if (module.fillTrailBurst.getValue() && swinging && !this.bO6yN2) {
         this.leQY5 = now;
      }

      this.bO6yN2 = swinging;
      float burstAge = (float)(now - this.leQY5) / 1000.0F;
      float burst = Math.max(0.0F, 1.0F - burstAge / 0.45F);
      this.dqBiJC = this.dqBiJC + (burst - this.dqBiJC) * (1.0F - (float)Math.exp(-dt * 6.0F));
      float fadeAdd = this.dqBiJC * module.fillTrailBurstPower.getFloatValue() * 0.012F;
      this.cpbI390.beginWrite(false);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      GL11.glClear(16384);
      RenderSystem.disableBlend();
      ShaderProgram trailFadeShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_TRAIL_FADE);
      if (trailFadeShader != null) {
         RenderSystem.setShader(KEY_FILL_TRAIL_FADE);
         RenderSystem.setShaderTexture(0, this.d1ftf.getColorAttachment());
         this.jfsfLej(trailFadeShader, "offset", this.mAiR, this.gCjeXc);
         this.d0BTmy(trailFadeShader, "fade", module.fillTrailFade.getFloatValue() + fadeAdd);
         this.d0BTmy(trailFadeShader, "t", t);
         this.d0BTmy(trailFadeShader, "dt", dt);
         this.d0BTmy(trailFadeShader, "turb", module.fillTrailTurb.getFloatValue());
         this.d0BTmy(trailFadeShader, "flickAmp", module.fillTrailFlicker.getFloatValue());
         this.jfsfLej(trailFadeShader, "texSize", this.d1ftf.textureWidth, this.d1ftf.textureHeight);
         this.y3HB();
      }

      RenderSystem.enableBlend();
      RenderSystem.blendFunc(1, 771);
      ShaderProgram trailColorShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_TRAIL_COLOR);
      if (trailColorShader != null) {
         RenderSystem.setShader(KEY_FILL_TRAIL_COLOR);
         RenderSystem.setShaderTexture(0, bloomTex);
         this.yoQi(trailColorShader, "glowColor1", c1[0], c1[1], c1[2]);
         this.yoQi(trailColorShader, "glowColor2", c2[0], c2[1], c2[2]);
         this.d0BTmy(trailColorShader, "exposure", module.fillGlowExposure.getFloatValue());
         this.d0BTmy(trailColorShader, "autoColor", module.fillAutoColor.getValue() ? 1.0F : 0.0F);
         this.d0BTmy(trailColorShader, "saturation", module.fillSaturation.getFloatValue());
         this.y3HB();
      }

      if (module.fillTrailModel.getValue()) {
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 771);
         float modelAlpha = module.fillTrailModelAlpha.getFloatValue();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, modelAlpha);
         if (this.qX0FXQK != null) {
            RenderSystem.setShaderTexture(0, this.qX0FXQK.getColorAttachment());
         } else {
            RenderSystem.setShaderTexture(0, this.vL08pn.getColorAttachment());
         }

         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
         this.y3HB();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      mc.getFramebuffer().beginWrite(true);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(1, 771);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, this.cpbI390.getColorAttachment());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
      this.y3HB();
      RenderSystem.defaultBlendFunc();
      Framebuffer tmp = this.d1ftf;
      this.d1ftf = this.cpbI390;
      this.cpbI390 = tmp;
   }

   private void oJon(GlassHands module) {
      boolean auto = module.fillAutoColor.getValue() && this.aeVx1 != -1;
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
         RenderSystem.setShaderTexture(0, this.b0f7.getColorAttachment());
         if (auto) {
            RenderSystem.setShaderTexture(1, this.aeVx1);
         }

         int fw = Math.max(1, mc.getWindow().getFramebufferWidth());
         int fh = Math.max(1, mc.getWindow().getFramebufferHeight());
         this.d0BTmy(outlineShader, "colorMode", colorMode);
         this.d0BTmy(outlineShader, "width", module.fillOutlineWidth.getFloatValue());
         this.jfsfLej(outlineShader, "texelSize", 1.0F / fw, 1.0F / fh);
         this.d0BTmy(outlineShader, "alpha", a);
         this.d0BTmy(outlineShader, "saturation", module.fillSaturation.getFloatValue());
         this.yoQi(outlineShader, "solidColor", col.getRed() / 255.0F, col.getGreen() / 255.0F, col.getBlue() / 255.0F);
         this.y3HB();
         RenderSystem.defaultBlendFunc();
      }
   }

   private static float a87hjP(float v) {
      return v < 0.0F ? 0.0F : Math.min(v, 1.0F);
   }

   private int[] yRFv(float speed) {
      float hue = (float)(System.currentTimeMillis() % (long)(10000.0 / speed)) / (10000.0F / speed);
      int c1 = Color.HSBtoRGB(hue, 1.0F, 1.0F) & 16777215;
      int c2 = Color.HSBtoRGB((hue + 0.5F) % 1.0F, 1.0F, 1.0F) & 16777215;
      return new int[]{c1, c2};
   }

   private Framebuffer gYfkwi(Framebuffer buf, boolean depth, int divisor) {
      int fw = mc.getWindow().getFramebufferWidth();
      int fh = mc.getWindow().getFramebufferHeight();
      int w = Math.max(2, fw / divisor);
      int h = Math.max(2, fh / divisor);
      if (buf == null) {
         buf = new SimpleFramebuffer(w, h, depth);
         this.s9t2Ylg(buf);
      } else if (buf.textureWidth != w || buf.textureHeight != h) {
         buf.delete();
         buf = new SimpleFramebuffer(w, h, depth);
         this.s9t2Ylg(buf);
      }

      return buf;
   }

   private void yvxsMQ3(int n) {
      int fw = mc.getWindow().getFramebufferWidth();
      int fh = mc.getWindow().getFramebufferHeight();
      if (this.jIa7U.size() < n) {
         this.jIa7U.forEach(Framebuffer::delete);
         this.jIa7U.clear();

         for (int i = 0; i < n; i++) {
            Framebuffer f = new SimpleFramebuffer(Math.max(2, fw >> i + 1), Math.max(2, fh >> i + 1), false);
            this.s9t2Ylg(f);
            this.jIa7U.add(f);
         }
      }

      for (int i = 0; i < n; i++) {
         int w = Math.max(2, fw >> i + 1);
         int h = Math.max(2, fh >> i + 1);
         Framebuffer b = this.jIa7U.get(i);
         if (b.textureWidth != w || b.textureHeight != h) {
            b.delete();
            Framebuffer var10 = new SimpleFramebuffer(w, h, false);
            this.s9t2Ylg(var10);
            this.jIa7U.set(i, var10);
         }
      }
   }

   private boolean mzp5U(GlassHands module) {
      if (module != null && module.isEnabled()) {
         if (module.mode.is("Блюр")) {
            return true;
         } else if (module.mode.is("Обводка")) {
            return true;
         } else if (module.mode.is("Шлейф")) {
            return true;
         } else if (module.mode.is("Trail")) {
            return true;
         } else if (module.mode.is("Стекло")) {
            return true;
         } else if (module.mode.is("Plasma")) {
            return true;
         } else if (module.mode.is("Заливка")) {
            return true;
         } else if (module.mode.is("Волна")) {
            return true;
         } else {
            boolean hasGlow = module.glow.getFloatValue() > 0.001F;
            boolean hasFill = module.fill.getFloatValue() > 0.001F && module.alpha.getFloatValue() > 0.001F;
            return hasGlow || hasFill;
         }
      } else {
         return false;
      }
   }

   private void y3HB() {
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

   private void d0BTmy(ShaderProgram s, String name, float v) {
      GlUniform u = s.getUniform(name);
      if (u != null) {
         u.set(v);
      }
   }

   private void jfsfLej(ShaderProgram s, String name, float x, float y) {
      GlUniform u = s.getUniform(name);
      if (u != null) {
         u.set(x, y);
      }
   }

   private void yoQi(ShaderProgram s, String name, float x, float y, float z) {
      GlUniform u = s.getUniform(name);
      if (u != null) {
         u.set(x, y, z);
      }
   }

   private void gN7h(ShaderProgram s, String name, float x, float y, float z, float w) {
      GlUniform u = s.getUniform(name);
      if (u != null) {
         u.set(x, y, z, w);
      }
   }

   private void rNv7e2b(ShaderProgram s, int w, int h, float offset) {
      this.jfsfLej(s, "uSize", Math.max(1, w), Math.max(1, h));
      this.jfsfLej(s, "uOffset", offset, offset);
      this.jfsfLej(s, "uHalfPixel", 0.5F / Math.max(1, w), 0.5F / Math.max(1, h));
   }

   private float bd532Le(int c) {
      return (c >> 16 & 0xFF) / 255.0F;
   }

   private float d9vBtn(int c) {
      return (c >> 8 & 0xFF) / 255.0F;
   }

   private float oeab7t(int c) {
      return (c & 0xFF) / 255.0F;
   }
}
