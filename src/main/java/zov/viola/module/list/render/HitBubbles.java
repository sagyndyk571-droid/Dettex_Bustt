package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import zov.viola.event.list.EventAttack;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.impl.Theme;
import zov.viola.module.settings.impl.ThemeManager;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "HitBubbles",
   moduleDesc = "Круг при ударе игрока",
   moduleCategory = ModuleCategory.RENDER
)
public class HitBubbles extends Module {
   private static final long LIFE_MS = 1600L;
   private final BooleanSetting yxrbS = new BooleanSetting(
      "Цвет от темы", true
   );
   private final ColorSetting yoy4 = new ColorSetting("Свой цвет", -9021441)
      .setVisible(() -> !this.yxrbS.getValue());
   private final CopyOnWriteArrayList<HitBubbles.HitBubble> cByf25s = new CopyOnWriteArrayList<>();
   private final Identifier oJUg = Identifier.of("mre", "images/bubble.png");

   @Override
   public void onDisable() {
      this.cByf25s.clear();
      super.onDisable();
   }

   @Subscribe
   public void onAttack(EventAttack event) {
      if (event != null && event.getEntity() != null) {
         if (event.getEntity() instanceof LivingEntity living) {
            if (this.mc.player != null) {
               Vec3d sideDir = this.ggpj(living, this.mc.player.getPos());
               Vec3d pos = this.t7jfIeC(living, sideDir);
               float sideYaw = (float)Math.toDegrees(Math.atan2(sideDir.x, sideDir.z));
               this.cByf25s.add(new HitBubbles.HitBubble(pos, System.currentTimeMillis(), (float)(Math.random() * 360.0), sideYaw));
            }
         }
      }
   }

   @Subscribe
   public void onWorldRender(EventWorldRender event) {
      long now = System.currentTimeMillis();
      this.cByf25s.removeIf(b -> now - b.spawnTime() >= 1600L);
      if (!this.cByf25s.isEmpty() && this.mc.player != null) {
         MatrixStack stack = event.getMatrixStack();
         Vec3d cameraPos = this.mc.gameRenderer.getCamera().getPos();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.oJUg);

         for (HitBubbles.HitBubble bubble : this.cByf25s) {
            this.uxpkl(stack, cameraPos, bubble, now);
         }

         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private void uxpkl(MatrixStack stack, Vec3d cameraPos, HitBubbles.HitBubble bubble, long now) {
      float progress = (float)(now - bubble.spawnTime()) / 1600.0F;
      if (!(progress >= 1.0F)) {
         float inPhase = Math.max(0.0F, Math.min(1.0F, progress / 0.22F));
         float outPhase = Math.max(0.0F, Math.min(1.0F, (progress - 0.225F) / 0.4F));
         float scaleIn = inPhase * inPhase * (3.0F - 2.0F * inPhase);
         float scaleOut = 1.0F - outPhase * outPhase;
         float scale = 0.02F + 1.55F * scaleIn * scaleOut;
         float alpha = 1.0F - outPhase * outPhase * outPhase;
         float rotation = (float)(now - bubble.spawnTime()) / 1.5F + bubble.spinSeed();
         Vec3d rel = bubble.pos().subtract(cameraPos);
         int color = this.z8J53(this.v6qVug(), alpha);
         stack.push();
         stack.translate(rel.x, rel.y, rel.z);
         stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(bubble.sideYaw()));
         stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-210.0F));
         stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
         this.tGm1Lru(stack, -scale * 0.5F, -scale * 0.5F, scale, scale, color);
         stack.pop();
      }
   }

   private void tGm1Lru(MatrixStack stack, float x, float y, float width, float height, int color) {
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      int a = color >> 24 & 0xFF;
      if (a > 0) {
         Matrix4f mat = stack.peek().getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(mat, x, y, 0.0F).texture(0.0F, 0.0F).color(r, g, b, a);
         buffer.vertex(mat, x, y + height, 0.0F).texture(0.0F, 1.0F).color(r, g, b, a);
         buffer.vertex(mat, x + width, y + height, 0.0F).texture(1.0F, 1.0F).color(r, g, b, a);
         buffer.vertex(mat, x + width, y, 0.0F).texture(1.0F, 0.0F).color(r, g, b, a);
         BufferRenderer.drawWithGlobalProgram(buffer.end());
      }
   }

   private Vec3d ggpj(LivingEntity target, Vec3d attackerPos) {
      Vec3d dir = attackerPos.subtract(target.getPos());
      dir = new Vec3d(dir.x, 0.0, dir.z);
      if (dir.lengthSquared() < 1.0E-4) {
         Vec3d fallback = target.getRotationVector();
         dir = new Vec3d(fallback.x, 0.0, fallback.z);
      }

      if (dir.lengthSquared() < 1.0E-4) {
         dir = new Vec3d(0.0, 0.0, 1.0);
      }

      return dir.normalize();
   }

   private Vec3d t7jfIeC(LivingEntity target, Vec3d sideDir) {
      Vec3d head = new Vec3d(target.getX(), target.getY() + target.getHeight() + 0.18, target.getZ());
      return head.add(sideDir.multiply(0.1));
   }

   private int v6qVug() {
      if (this.yxrbS.getValue()) {
         Theme theme = ThemeManager.getInstance().getCurrentTheme();
         return theme != null ? theme.color1 : -1;
      } else {
         return this.yoy4.getValue();
      }
   }

   private int z8J53(int color, float alphaMultiplier) {
      int a = color >> 24 & 0xFF;
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      a = (int)(a * alphaMultiplier);
      return a << 24 | r << 16 | g << 8 | b;
   }

   private record HitBubble(Vec3d pos, long spawnTime, float spinSeed, float sideYaw) {
   }
}
