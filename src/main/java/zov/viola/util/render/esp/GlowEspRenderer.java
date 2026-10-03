package zov.viola.util.render.esp;

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
import org.lwjgl.opengl.GL11;
import zov.viola.mixin.LivingEntityRendererAccessor;
import zov.viola.module.list.render.GlowEsp;
import zov.viola.module.settings.impl.ThemeManager;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.providers.ResourceProvider;

public class GlowEspRenderer {
   private static GlowEspRenderer hI0oL;
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final ShaderProgramKey KEY_KAWASE_DOWN = rtukVx("glass_hands_kawase_down");
   private static final ShaderProgramKey KEY_KAWASE_UP = rtukVx("glass_hands_kawase_up");
   private static final ShaderProgramKey KEY_FILL_GLOW = rtukVx("glass_hands_fill_glow");
   private static final ShaderProgramKey KEY_FILL_OUTLINE = rtukVx("glass_hands_fill_outline");
   private Framebuffer ntzTcJ9;
   private final List<Framebuffer> kafrb8P = new ArrayList<>();
   private int s09n = -1;
   private int c22B = -1;
   private boolean lntr = false;

   private static ShaderProgramKey rtukVx(String name) {
      return new ShaderProgramKey(ResourceProvider.getShaderIdentifier(name), VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
   }

   public static GlowEspRenderer getInstance() {
      if (hI0oL == null) {
         hI0oL = new GlowEspRenderer();
      }

      return hI0oL;
   }

   public void renderMask(GlowEsp module, MatrixStack worldMatrices, float tickDelta) {
      if (mc.world != null && mc.player != null) {
         this.opJeMNF();
         if (this.ntzTcJ9 != null) {
            this.ntzTcJ9.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            this.ntzTcJ9.clear();
            this.ntzTcJ9.beginWrite(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);

            for (PlayerEntity player : mc.world.getPlayers()) {
               if (this.cwT0q28(player)) {
                  this.q3H652(worldMatrices, player, tickDelta);
               }
            }

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            mc.getFramebuffer().beginWrite(true);
            this.lntr = true;
         }
      }
   }

   public void compositeIfReady(GlowEsp module) {
      if (this.lntr) {
         this.lntr = false;
         int iter = (int)module.glowRadius.getFloatValue();
         this.hB8p(iter);
         ShaderProgram downShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_DOWN);
         ShaderProgram upShader = mc.getShaderLoader().getOrCreateProgram(KEY_KAWASE_UP);
         if (downShader != null && upShader != null) {
            int cur = this.ntzTcJ9.getColorAttachment();

            for (int i = 0; i < iter; i++) {
               Framebuffer b = this.kafrb8P.get(i);
               b.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               b.clear();
               b.beginWrite(true);
               RenderSystem.setShader(KEY_KAWASE_DOWN);
               RenderSystem.setShaderTexture(0, cur);
               this.gup1B(downShader, b.textureWidth, b.textureHeight, 1.0F + i);
               this.jqEZ();
               cur = b.getColorAttachment();
            }

            for (int i = iter - 1; i >= 1; i--) {
               Framebuffer b = this.kafrb8P.get(i - 1);
               b.beginWrite(true);
               RenderSystem.setShader(KEY_KAWASE_UP);
               RenderSystem.setShaderTexture(0, cur);
               this.gup1B(upShader, b.textureWidth, b.textureHeight, 1.0F + i);
               this.zDs9b(upShader, "color", 1.0F, 1.0F, 1.0F);
               this.jqEZ();
               cur = b.getColorAttachment();
            }

            mc.getFramebuffer().beginWrite(true);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            RenderSystem.disableDepthTest();
            ShaderProgram glowShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_GLOW);
            if (glowShader != null) {
               boolean isAuto = module.autoColor.getValue();
               int gc1;
               int gc2;
               if (isAuto) {
                  int t1 = ThemeManager.getInstance().getCurrentTheme().getColorFirst();
                  int t2 = ThemeManager.getInstance().getCurrentTheme().getColorSecond();
                  gc1 = t1 & 16777215;
                  gc2 = t2 & 16777215;
               } else if (module.rainbow.getValue()) {
                  float speed = module.rainbowSpeed.getFloatValue();
                  float hue = (float)(System.currentTimeMillis() % (long)(10000.0 / speed)) / (10000.0F / speed);
                  gc1 = Color.HSBtoRGB(hue, 1.0F, 1.0F) & 16777215;
                  gc2 = gc1;
               } else {
                  gc1 = module.glowColor1.getValue();
                  gc2 = module.glowColor2.getValue();
               }

               RenderSystem.setShader(KEY_FILL_GLOW);
               RenderSystem.setShaderTexture(0, cur);
               RenderSystem.setShaderTexture(1, this.ntzTcJ9.getColorAttachment());
               this.zDs9b(glowShader, "glowColor1", this.ud3o(gc1), this.fvsI1o(gc1), this.qpcb(gc1));
               this.zDs9b(glowShader, "glowColor2", this.ud3o(gc2), this.fvsI1o(gc2), this.qpcb(gc2));
               this.oU5Up(glowShader, "exposure", module.glowExposure.getFloatValue());
               this.oU5Up(glowShader, "autoColor", 0.0F);
               this.oU5Up(glowShader, "saturation", module.saturation.getFloatValue());
               this.jqEZ();
            }

            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getFramebuffer().beginWrite(true);
            if (module.outlineEnabled.getValue()) {
               this.lXImvR(module);
            }
         }
      }
   }

   public void invalidate() {
      this.lntr = false;
   }

   private void lXImvR(GlowEsp module) {
      ShaderProgram outlineShader = mc.getShaderLoader().getOrCreateProgram(KEY_FILL_OUTLINE);
      if (outlineShader != null) {
         boolean isAuto = module.autoColor.getValue();
         boolean isRainbow = !isAuto && module.rainbow.getValue();
         float a;
         int oc;
         float mode;
         if (isRainbow) {
            mode = 1.0F;
            a = 1.0F;
            oc = -1;
         } else if (isAuto) {
            mode = 0.0F;
            a = 1.0F;
            oc = ThemeManager.getInstance().getCurrentTheme().getColorFirst();
         } else {
            mode = 0.0F;
            oc = module.outlineColor.getValue();
            a = new Color(oc, true).getAlpha() / 255.0F;
         }

         Color col = new Color(oc | 0xFF000000, true);
         int fw = Math.max(1, mc.getWindow().getFramebufferWidth());
         int fh = Math.max(1, mc.getWindow().getFramebufferHeight());
         mc.getFramebuffer().beginWrite(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(770, 771, 0, 1);
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(KEY_FILL_OUTLINE);
         RenderSystem.setShaderTexture(0, this.ntzTcJ9.getColorAttachment());
         if (isAuto && !this.kafrb8P.isEmpty()) {
            RenderSystem.setShaderTexture(1, this.kafrb8P.get(0).getColorAttachment());
         }

         this.oU5Up(outlineShader, "colorMode", mode);
         this.oU5Up(outlineShader, "width", module.outlineWidth.getFloatValue());
         this.b2DW(outlineShader, "texelSize", 1.0F / fw, 1.0F / fh);
         this.oU5Up(outlineShader, "alpha", isRainbow ? 1.0F : a);
         this.oU5Up(outlineShader, "saturation", module.saturation.getFloatValue());
         this.zDs9b(outlineShader, "solidColor", col.getRed() / 255.0F, col.getGreen() / 255.0F, col.getBlue() / 255.0F);
         this.jqEZ();
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         mc.getFramebuffer().beginWrite(true);
      }
   }

   private boolean cwT0q28(PlayerEntity player) {
      if (player != null && player.isAlive()) {
         if (player == mc.player) {
            GlowEsp module = GlowEsp.getInstance();
            if (module == null || !module.renderSelf.getValue()) {
               return false;
            }

            if (mc.options.getPerspective().isFirstPerson()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void q3H652(MatrixStack matrices, PlayerEntity player, float tickDelta) {
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
            this.q5r8dy(matrices, buf, root, model.head, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 16.0F, 16.0F);
            this.q5r8dy(matrices, buf, root, model.body, -4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 20.0F, 20.0F, 28.0F, 32.0F);
            if (slim) {
               this.q5r8dy(matrices, buf, root, model.rightArm, -2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 44.0F, 16.0F, 47.0F, 28.0F);
               this.q5r8dy(matrices, buf, root, model.leftArm, -1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, 36.0F, 52.0F, 39.0F, 64.0F);
            } else {
               this.q5r8dy(matrices, buf, root, model.rightArm, -3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 44.0F, 16.0F, 48.0F, 28.0F);
               this.q5r8dy(matrices, buf, root, model.leftArm, -1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 36.0F, 52.0F, 40.0F, 64.0F);
            }

            this.q5r8dy(matrices, buf, root, model.rightLeg, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 4.0F, 16.0F, 8.0F, 28.0F);
            this.q5r8dy(matrices, buf, root, model.leftLeg, -2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 20.0F, 48.0F, 24.0F, 60.0F);
            BufferRenderer.drawWithGlobalProgram(buf.end());
            matrices.pop();
         }
      }
   }

   private int jMkmk9r(PlayerEntity player) {
      int hash = player.getUuid().hashCode();
      float hue = (hash & 65535) / 65535.0F;
      Color color = Color.getHSBColor(hue, 0.7F, 0.9F);
      return ColorProvider.rgba(color.getRed(), color.getGreen(), color.getBlue(), 255.0F);
   }

   private void q5r8dy(
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
      this.eI2830(buf, m, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, tu1, tv1, tu2, tv2);
      this.eI2830(buf, m, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1, tu1, tv1, tu2, tv2);
      this.eI2830(buf, m, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0, tu1, tv1, tu2, tv2);
      this.eI2830(buf, m, x1, y0, z1, x1, y1, z1, x0, y1, z1, x0, y0, z1, tu1, tv1, tu2, tv2);
      this.eI2830(buf, m, x0, y0, z1, x0, y1, z1, x0, y1, z0, x0, y0, z0, tu1, tv1, tu2, tv2);
      this.eI2830(buf, m, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1, tu1, tv1, tu2, tv2);
      stack.pop();
   }

   private void eI2830(
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

   private void rMpX(
      MatrixStack stack, BufferBuilder buf, ModelPart root, ModelPart part, float ox, float oy, float oz, float w, float h, float d, float expand, int color
   ) {
      stack.push();
      root.rotate(stack);
      part.rotate(stack);
      float s = 0.0625F;
      float e = expand * s;
      float x0 = ox * s - e;
      float y0 = oy * s - e;
      float z0 = oz * s - e;
      float x1 = (ox + w) * s + e;
      float y1 = (oy + h) * s + e;
      float z1 = (oz + d) * s + e;
      Matrix4f m = stack.peek().getPositionMatrix();
      int r = ColorProvider.red(color);
      int g = ColorProvider.green(color);
      int b = ColorProvider.blue(color);
      int a = ColorProvider.alpha(color);
      this.kaNdg1(buf, m, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0, r, g, b, a);
      this.kaNdg1(buf, m, x0, y0, z1, x0, y0, z0, x1, y0, z0, x1, y0, z1, r, g, b, a);
      this.kaNdg1(buf, m, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0, r, g, b, a);
      this.kaNdg1(buf, m, x1, y0, z1, x1, y1, z1, x0, y1, z1, x0, y0, z1, r, g, b, a);
      this.kaNdg1(buf, m, x0, y0, z1, x0, y1, z1, x0, y1, z0, x0, y0, z0, r, g, b, a);
      this.kaNdg1(buf, m, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1, r, g, b, a);
      stack.pop();
   }

   private void kaNdg1(
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
      int r,
      int g,
      int bl,
      int a
   ) {
      b.vertex(m, x1, y1, z1).color(r, g, bl, a);
      b.vertex(m, x2, y2, z2).color(r, g, bl, a);
      b.vertex(m, x3, y3, z3).color(r, g, bl, a);
      b.vertex(m, x4, y4, z4).color(r, g, bl, a);
   }

   private void opJeMNF() {
      int w = mc.getWindow().getFramebufferWidth();
      int h = mc.getWindow().getFramebufferHeight();
      if (w != this.s09n || h != this.c22B || this.ntzTcJ9 == null) {
         if (this.ntzTcJ9 != null) {
            this.ntzTcJ9.delete();
         }

         this.ntzTcJ9 = new SimpleFramebuffer(w, h, true);
         this.hUtja(this.ntzTcJ9);
         this.s09n = w;
         this.c22B = h;
         this.kafrb8P.forEach(Framebuffer::delete);
         this.kafrb8P.clear();
      }
   }

   private void hB8p(int n) {
      int fw = mc.getWindow().getFramebufferWidth();
      int fh = mc.getWindow().getFramebufferHeight();
      if (this.kafrb8P.size() != n) {
         this.kafrb8P.forEach(Framebuffer::delete);
         this.kafrb8P.clear();

         for (int i = 0; i < n; i++) {
            Framebuffer f = new SimpleFramebuffer(Math.max(2, fw >> i + 1), Math.max(2, fh >> i + 1), false);
            this.hUtja(f);
            this.kafrb8P.add(f);
         }
      } else {
         for (int i = 0; i < n; i++) {
            int w = Math.max(2, fw >> i + 1);
            int h = Math.max(2, fh >> i + 1);
            Framebuffer b = this.kafrb8P.get(i);
            if (b.textureWidth != w || b.textureHeight != h) {
               b.delete();
               Framebuffer var10 = new SimpleFramebuffer(w, h, false);
               this.hUtja(var10);
               this.kafrb8P.set(i, var10);
            }
         }
      }
   }

   private void jqEZ() {
      float sw = Math.max(mc.getWindow().getScaledWidth(), 1);
      float sh = Math.max(mc.getWindow().getScaledHeight(), 1);
      BufferBuilder b = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      b.vertex(0.0F, 0.0F, 0.0F).texture(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      b.vertex(0.0F, sh, 0.0F).texture(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      b.vertex(sw, sh, 0.0F).texture(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      b.vertex(sw, 0.0F, 0.0F).texture(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F);
      BufferRenderer.drawWithGlobalProgram(b.end());
   }

   private void gup1B(ShaderProgram s, int w, int h, float off) {
      this.b2DW(s, "uSize", Math.max(1, w), Math.max(1, h));
      this.b2DW(s, "uOffset", off, off);
      this.b2DW(s, "uHalfPixel", 0.5F / Math.max(1, w), 0.5F / Math.max(1, h));
   }

   private void hUtja(Framebuffer fb) {
      RenderSystem.bindTexture(fb.getColorAttachment());
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      RenderSystem.bindTexture(0);
   }

   private void oU5Up(ShaderProgram s, String n, float v) {
      GlUniform u = s.getUniform(n);
      if (u != null) {
         u.set(v);
      }
   }

   private void b2DW(ShaderProgram s, String n, float x, float y) {
      GlUniform u = s.getUniform(n);
      if (u != null) {
         u.set(x, y);
      }
   }

   private void zDs9b(ShaderProgram s, String n, float x, float y, float z) {
      GlUniform u = s.getUniform(n);
      if (u != null) {
         u.set(x, y, z);
      }
   }

   private float ud3o(int c) {
      return (c >> 16 & 0xFF) / 255.0F;
   }

   private float fvsI1o(int c) {
      return (c >> 8 & 0xFF) / 255.0F;
   }

   private float qpcb(int c) {
      return (c & 0xFF) / 255.0F;
   }
}
