package zov.viola.module.list.render;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Last;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.math.MathUtil;
import zov.viola.util.render.providers.ColorProvider;

@ModuleInformation(
   moduleName = "Target ESP",
   moduleDesc = "Визуальный эффект на текущей цели",
   moduleCategory = ModuleCategory.RENDER
)
public class TargetESP extends Module {
   protected final ModeSetting mode = new ModeSetting(
      "Режим",
      "Crystals",
      "Crystals",
      "Crystals2",
      "Cubes",
      "Prizraki",
      "Ghosts2",
      "Души",
      "Черепа",
      "Молнии",
      "Кольцо",
      "Картинка 1",
      "Картинка 2"
   );
   private final BooleanSetting sE2Mz = new BooleanSetting(
      "При наведении", true
   );
   private final BooleanSetting jNg43pp = new BooleanSetting(
         "Цвет от темы", true
      )
      .setVisible(() -> !this.mode.is("Черепа"));
   private final ColorSetting uyf8ovK = new ColorSetting(
         "Свой цвет", -9021441
      )
      .setVisible(() -> !this.mode.is("Черепа") && !this.jNg43pp.getValue());
   private final SliderSetting achC = new SliderSetting(
         D.k(
            new int[]{1198, 1229, 1184, 1074, 1201, 1206, 1244, 1086, 175, 1224, 1246, 1098, 1208, 1207, 1198, 1096, 1201, 1221}, new int[]{143, 247, 158, 114}
         ),
         1.0,
         0.5,
         2.0,
         0.1F
      )
      .setVisible(() -> this.mode.is("Prizraki") || this.mode.is("Ghosts2"));
   private final SliderSetting mt4k6g = new SliderSetting(
         "Размер призраков",
         0.4F,
         0.1F,
         1.0,
         0.05F
      )
      .setVisible(() -> this.mode.is("Prizraki"));
   private final SliderSetting ahpmDnC = new SliderSetting(
         "Кол-во молний", 6.0, 1.0, 50.0, 1.0
      )
      .setVisible(() -> this.mode.is("Молнии"));
   private final SliderSetting mhm5eI2 = new SliderSetting(
         "Толщина", 1.0, 0.5, 3.0, 0.1F
      )
      .setVisible(() -> this.mode.is("Молнии"));
   private final SliderSetting e9G7M = new SliderSetting(
         "Размер", 1.15F, 0.6F, 2.5, 0.05F
      )
      .setVisible(
         () -> this.mode.is("Картинка 1")
            || this.mode.is("Картинка 2")
      );
   private final SliderSetting a3tgZ0l = new SliderSetting(
         "Скорость вращения",
         1.2F,
         0.2F,
         4.0,
         0.05F
      )
      .setVisible(
         () -> this.mode.is("Картинка 1")
            || this.mode.is("Картинка 2")
      );
   private final SliderSetting k1gbhU = new SliderSetting(
         "Радиус кольца", 0.5, 0.3F, 1.5, 0.05F
      )
      .setVisible(() -> this.mode.is("Кольцо"));
   private final SliderSetting dN4x = new SliderSetting(
         "Скорость кольца",
         1.0,
         0.3F,
         3.0,
         0.1F
      )
      .setVisible(() -> this.mode.is("Кольцо"));
   private final Animation e0k4y = new Animation(Easing.EXPO_OUT, 500L);
   private Entity bPgf4z = null;
   private boolean a7kZI = false;
   private static long y5qqymp = System.currentTimeMillis();
   private final ArrayList<TargetESP.Particle> vfB8l79 = new ArrayList<>();
   private static final int PARTICLES_PER_SPAWN = 1;
   private static final float SPAWN_INTERVAL = 0.017F;
   private float mbEkU2 = 0.0F;
   private long pS0tg0o = 0L;
   private final Animation p7wx3u3 = new Animation(Easing.EXPO_OUT, 350L);
   private Entity akiLdx1 = null;
   private final ArrayList<TargetESP.LightningBolt> gUnAhv8 = new ArrayList<>();
   private float xih2ub6 = 0.0F;
   private LivingEntity egdhS = null;
   private Vec3d joqeyJ = null;
   private long xoEr0 = 0L;
   private float ooSpfo = 0.0F;
   private float ss9k = 0.0F;
   private Vec3d w5m12 = null;
   private float dPjwNa = 1.8F;
   private long oi7Za = System.currentTimeMillis();
   private float d7eejFb = 0.0F;
   private float cpfP1wW = -280.0F;
   private float iyaej = 280.0F;
   private final Last ujww5 = context -> this.om610(context.matrixStack(), context.camera(), context.tickCounter().getTickDelta(true));
   private static final int RIDER_MAX_PARTICLES = 3;
   private static final float RIDER_BASE_MUL = 0.05F;
   private static final float RIDER_ALPHA_STEP = 0.005F;
   private final List<TargetESP.RiderParticle> np2dgBx = new ArrayList<>();
   private final Animation shC1i = new Animation(Easing.EXPO_OUT, 300L);
   private float byWxsg = 0.0F;
   private final Animation zrlZl = new Animation(Easing.EXPO_OUT, 500L);
   private LivingEntity jyyhV = null;
   private final Animation pjUb9 = new Animation(Easing.CUBIC_OUT, 300L);
   private LivingEntity qe28 = null;
   private float wEfJ3 = 0.0F;

   @Override
   public void onEnable() {
      if (!this.a7kZI) {
         WorldRenderEvents.LAST.register(this.ujww5);
         this.a7kZI = true;
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.gUnAhv8.clear();
      this.xih2ub6 = 0.0F;
      this.egdhS = null;
      this.joqeyJ = null;
      super.onDisable();
   }

   private void om610(MatrixStack matrices, Camera camera, float tickDelta) {
      if (this.isEnabled()) {
         Entity target = Viola.getInstance().getModuleStorage().get(KillAura.class).getTarget();
         if (target == null && this.sE2Mz.getValue()) {
            target = this.rdAXAJ();
         }

         if (target != null && target != this.mc.player && !(target instanceof ArmorStandEntity)) {
            this.bPgf4z = target;
            this.e0k4y.run(1.0F);
         } else {
            this.e0k4y.run(0.0F);
            if (this.e0k4y.getValue() == 0.0F) {
               this.bPgf4z = null;
            }
         }

         if (target != null && this.bPgf4z != null) {
            String var9 = this.mode.getValue();
            switch (var9) {
               case "Crystals":
                  this.akiLdx1 = this.bPgf4z;
                  this.p7wx3u3.run(1.0F);
                  float animx = this.p7wx3u3.getValue();
                  if (animx > 0.001F) {
                     this.bjs6b5(matrices, camera, (LivingEntity)this.akiLdx1, false, animx, tickDelta);
                  }
                  break;
               case "Crystals2":
                  this.akiLdx1 = this.bPgf4z;
                  this.p7wx3u3.run(1.0F);
                  float anim = this.p7wx3u3.getValue();
                  if (anim > 0.001F) {
                     this.bjs6b5(matrices, camera, (LivingEntity)this.akiLdx1, true, anim, tickDelta);
                  }
                  break;
               case "Cubes":
                  this.i6SGGvG(matrices, camera, (LivingEntity)this.bPgf4z, tickDelta);
                  break;
               case "Prizraki":
                  this.e6rOHXn(matrices, camera, (LivingEntity)this.bPgf4z, tickDelta);
                  break;
               case "GhostRider":
                  this.ix32Nv(matrices, camera, (LivingEntity)this.bPgf4z, tickDelta);
                  break;
               case "Ghosts2":
                  this.py5sMp(matrices, tickDelta);
                  break;
               case "Души":
                  this.qe28 = (LivingEntity)this.bPgf4z;
                  this.pjUb9.run(1.0F);
                  if (this.pjUb9.getValue() > 0.001F) {
                     this.nUTgFvj(matrices, camera, (LivingEntity)this.bPgf4z, tickDelta);
                  }
                  break;
               case "Черепа":
                  this.fayour(matrices, camera, (LivingEntity)this.bPgf4z, tickDelta);
                  break;
               case "Молнии":
                  this.yz879(matrices, camera, tickDelta, true);
                  break;
               case "Кольцо":
                  this.ju4k(matrices, camera, (LivingEntity)this.bPgf4z, tickDelta, true);
                  break;
               case "Картинка 1":
               case "Картинка 2":
                  this.dkfj3zA(matrices, camera, (LivingEntity)this.bPgf4z, tickDelta, true);
            }
         } else {
            this.pS0tg0o = 0L;
            this.mbEkU2 = 0.0F;
            this.np2dgBx.clear();
            if (this.mode.is("Ghosts2")) {
               this.py5sMp(matrices, tickDelta);
            }

            if (this.mode.is("Молнии")) {
               this.yz879(matrices, camera, tickDelta, false);
            }

            if (this.mode.is("Души")) {
               this.pjUb9.run(0.0F);
               if (this.pjUb9.getValue() > 0.001F && this.qe28 != null && this.qe28.isAlive()) {
                  this.nUTgFvj(matrices, camera, this.qe28, tickDelta);
               } else if (this.pjUb9.getValue() <= 0.001F) {
                  this.qe28 = null;
               }
            }

            if (this.mode.is("Crystals") || this.mode.is("Crystals2")) {
               this.p7wx3u3.run(0.0F);
               float animx = this.p7wx3u3.getValue();
               if (animx > 0.001F && this.akiLdx1 != null) {
                  this.bjs6b5(matrices, camera, (LivingEntity)this.akiLdx1, this.mode.is("Crystals2"), animx, tickDelta);
               } else if (animx <= 0.001F) {
                  this.akiLdx1 = null;
               }
            }

            if (this.mode.is("Cubes") && !this.vfB8l79.isEmpty()) {
               this.i6SGGvG(matrices, camera, null, tickDelta);
            }

            if (this.mode.is("Кольцо") && this.w5m12 != null) {
               this.ju4k(matrices, camera, null, tickDelta, false);
            }

            if ((
                  this.mode.is("Картинка 1")
                     || this.mode.is("Картинка 2")
               )
               && this.w5m12 != null) {
               this.dkfj3zA(matrices, camera, null, tickDelta, false);
            }
         }
      }
   }

   public void renderTargetESPLate(MatrixStack stack, float tickDelta) {
      if (this.isEnabled()) {
         Camera camera = this.mc.gameRenderer.getCamera();
         this.om610(stack, camera, tickDelta);
      }
   }

   private Entity rdAXAJ() {
      if (this.mc.targetedEntity != null
         && this.mc.targetedEntity != this.mc.player
         && this.mc.targetedEntity instanceof LivingEntity
         && this.mc.targetedEntity.isAlive()
         && !(this.mc.targetedEntity instanceof ArmorStandEntity)) {
         return this.mc.targetedEntity;
      } else {
         if (this.mc.player != null && this.mc.world != null) {
            Entity cam = (Entity)(this.mc.cameraEntity != null ? this.mc.cameraEntity : this.mc.player);
            double range = 8.0;
            Vec3d cameraVec = cam.getCameraPosVec(1.0F);
            Vec3d rotationVec = cam.getRotationVec(1.0F);
            Vec3d end = cameraVec.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
            Box box = cam.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);
            EntityHitResult hit = ProjectileUtil.raycast(
               cam,
               cameraVec,
               end,
               box,
               e -> !e.isSpectator() && e != this.mc.player && e instanceof LivingEntity && e.isAlive() && !(e instanceof ArmorStandEntity),
               range * range
            );
            if (hit != null && hit.getEntity() instanceof LivingEntity) {
               return hit.getEntity();
            }
         }

         return null;
      }
   }

   private void e6rOHXn(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
      Vec3d camPos = camera.getPos();
      float camYaw = camera.getYaw();
      float camPitch = camera.getPitch();
      double tx = MathHelper.lerp((double)partialTicks, target.lastRenderX, target.getX());
      double ty = MathHelper.lerp((double)partialTicks, target.lastRenderY, target.getY()) + 0.38 + target.getHeight() / 2.0;
      double tz = MathHelper.lerp((double)partialTicks, target.lastRenderZ, target.getZ());
      double rx = tx - camPos.x;
      double ry = ty - camPos.y;
      double rz = tz - camPos.z;
      double radius = 0.4 + target.getWidth() / 2.0;
      float speed = 30.0F / this.achC.getFloatValue();
      float size = this.mt4k6g.getFloatValue();
      double distance = 6.0;
      int length = 34;
      long now = System.currentTimeMillis();
      int colorInt = this.xzI01();
      float r = ColorProvider.red(colorInt) / 255.0F;
      float g = ColorProvider.green(colorInt) / 255.0F;
      float b = ColorProvider.blue(colorInt) / 255.0F;
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.setShaderTexture(0, this.kdGo());
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (int trail = 0; trail < 3; trail++) {
         for (int i = 0; i < length; i++) {
            double angle = 0.05F * (now - y5qqymp - i * distance) / speed;
            double s = Math.sin(angle * Math.PI) * radius;
            double c = Math.cos(angle * Math.PI) * radius;
            double o = trail == 0 ? Math.cos(angle * Math.PI) * radius : Math.sin(angle * Math.PI) * radius;
            float t = (float)i / (length - 1);
            float curSize = size * (1.0F - t * 0.5F);
            float alpha = 1.0F - t * 0.9F;
            double px = rx + (trail == 1 ? -s : s);
            double py = ry + o;
            double pz = rz + (trail == 2 ? c : -c);
            float cr = Math.min(1.0F, r * 1.5F);
            float cg = Math.min(1.0F, g * 1.5F);
            float cb = Math.min(1.0F, b * 1.5F);
            this.j2F93r(buffer, matrices, px, py, pz, curSize * 0.6F, r, g, b, alpha * 0.15F, camYaw, camPitch);
            this.j2F93r(buffer, matrices, px, py, pz, curSize * 0.35F, r, g, b, alpha * 0.35F, camYaw, camPitch);
            this.j2F93r(buffer, matrices, px, py, pz, curSize * 0.15F, cr, cg, cb, alpha, camYaw, camPitch);
         }
      }

      BufferRenderer.drawWithGlobalProgram(buffer.end());
      this.to08wo();
   }

   private void bjs6b5(MatrixStack matrices, Camera camera, LivingEntity target, boolean sharp, float anim, float partialTicks) {
      if (target != null && this.mc.player != null) {
         float eased = bh2gtku(anim);
         float time = (this.mc.player.age + partialTicks) * 6.0F;
         Vec3d camPos = camera.getPos();
         double tx = MathHelper.lerp((double)partialTicks, target.lastRenderX, target.getX());
         double ty = MathHelper.lerp((double)partialTicks, target.lastRenderY, target.getY());
         double tz = MathHelper.lerp((double)partialTicks, target.lastRenderZ, target.getZ());
         float camYaw = camera.getYaw();
         float camPitch = camera.getPitch();
         float entityHeight = target.getHeight();
         float halfWidth = target.getWidth() * 0.5F;
         int baseColor = this.xzI01();
         float r = Math.min(1.0F, ColorProvider.red(baseColor) / 255.0F * 1.3F);
         float g = Math.min(1.0F, ColorProvider.green(baseColor) / 255.0F * 1.3F);
         float b = Math.min(1.0F, ColorProvider.blue(baseColor) / 255.0F * 1.3F);
         matrices.push();
         matrices.translate(tx - camPos.x, ty - camPos.y, tz - camPos.z);
         int crystalCount = 18;
         float pelvisY = entityHeight * 0.35F;
         float torsoY = entityHeight * 0.55F;
         float neckY = entityHeight * 0.74F;
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
         BufferBuilder buf = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
         boolean hasCrystals = false;

         for (int i = 0; i < crystalCount; i++) {
            float s1 = (float)Math.sin(i * 1.7F + 0.3F) * 0.5F + 0.5F;
            float s2 = (float)Math.cos(i * 2.3F + 0.7F) * 0.5F + 0.5F;
            float s3 = (float)Math.sin(i * 3.1F + 1.1F) * 0.5F + 0.5F;
            float angle = time + i * (360.0F / crystalCount) + s1 * 12.0F;
            float radius = halfWidth + 0.25F + s3 * 0.15F;
            float cx = radius * (float)Math.cos(Math.toRadians(angle));
            float cz = radius * (float)Math.sin(Math.toRadians(angle));
            float cy = s2 * entityHeight;
            float scale = 0.18F * eased;
            if (!(scale < 0.001F)) {
               float lookY = this.getCrystalLookY(cy, entityHeight, pelvisY, torsoY, neckY);
               float dx = -cx;
               float dy = lookY - cy;
               float dz = -cz;
               float yaw = (float)Math.toDegrees(Math.atan2(dz, dx));
               float pitch = (float)Math.toDegrees(Math.atan2(dy, (float)Math.sqrt(dx * dx + dz * dz)));
               this.y2jgX(buf, matrices, cx, cy, cz, scale, yaw, pitch, (int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(200.0F * anim), sharp);
               hasCrystals = true;
            }
         }

         if (hasCrystals) {
            BufferRenderer.drawWithGlobalProgram(buf.end());
         }

         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.kdGo());
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
         BufferBuilder glowBuf = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         boolean hasGlow = false;

         for (int ix = 0; ix < crystalCount; ix++) {
            float s1 = (float)Math.sin(ix * 1.7F + 0.3F) * 0.5F + 0.5F;
            float s2 = (float)Math.cos(ix * 2.3F + 0.7F) * 0.5F + 0.5F;
            float s3 = (float)Math.sin(ix * 3.1F + 1.1F) * 0.5F + 0.5F;
            float angle = time + ix * (360.0F / crystalCount) + s1 * 12.0F;
            float radius = halfWidth + 0.25F + s3 * 0.15F;
            float cx = radius * (float)Math.cos(Math.toRadians(angle));
            float cz = radius * (float)Math.sin(Math.toRadians(angle));
            float cy = s2 * entityHeight;
            float scale = 0.18F * eased;
            if (anim * 0.15F > 0.001F && scale > 1.0E-4F) {
               this.j2F93r(glowBuf, matrices, cx, cy, cz, scale * 5.5F, r, g, b, anim * 0.15F, camYaw, camPitch);
               this.j2F93r(glowBuf, matrices, cx, cy, cz, scale * 3.5F, r, g, b, anim * 0.25F, camYaw, camPitch);
               hasGlow = true;
            }
         }

         if (hasGlow) {
            BufferRenderer.drawWithGlobalProgram(glowBuf.end());
         }

         matrices.pop();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
      }
   }

   private float getCrystalLookY(float cy, float h, float pelvis, float torso, float neck) {
      float n = cy / h;
      if (n < 0.33F) {
         return pelvis;
      } else {
         return n < 0.6F ? torso : neck;
      }
   }

   private void y2jgX(
      BufferBuilder buf, MatrixStack ms, float x, float y, float z, float scale, float yaw, float pitch, int r, int g, int b, int a, boolean sharp
   ) {
      ms.push();
      ms.translate(x, y, z);
      ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
      ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(pitch));
      ms.scale(scale, scale, scale);
      Matrix4f m = ms.peek().getPositionMatrix();
      float w = sharp ? 0.35F : 0.7F;
      float h = sharp ? 1.2F : 1.0F;
      this.b62A8z(buf, m, h, 0.0F, 0.0F, 0.0F, w, 0.0F, 0.0F, 0.0F, w, r, g, b, a);
      this.b62A8z(buf, m, h, 0.0F, 0.0F, 0.0F, 0.0F, w, 0.0F, -w, 0.0F, r, g, b, a);
      this.b62A8z(buf, m, h, 0.0F, 0.0F, 0.0F, -w, 0.0F, 0.0F, 0.0F, -w, r, g, b, a);
      this.b62A8z(buf, m, h, 0.0F, 0.0F, 0.0F, 0.0F, -w, 0.0F, w, 0.0F, r, g, b, a);
      this.b62A8z(buf, m, -h, 0.0F, 0.0F, 0.0F, w, 0.0F, 0.0F, 0.0F, w, r, g, b, a);
      this.b62A8z(buf, m, -h, 0.0F, 0.0F, 0.0F, 0.0F, w, 0.0F, -w, 0.0F, r, g, b, a);
      this.b62A8z(buf, m, -h, 0.0F, 0.0F, 0.0F, -w, 0.0F, 0.0F, 0.0F, -w, r, g, b, a);
      this.b62A8z(buf, m, -h, 0.0F, 0.0F, 0.0F, 0.0F, -w, 0.0F, w, 0.0F, r, g, b, a);
      ms.pop();
   }

   private void b62A8z(
      BufferBuilder buf, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int r, int g, int b, int a
   ) {
      buf.vertex(m, x1, y1, z1).color(r, g, b, a);
      buf.vertex(m, x2, y2, z2).color(r, g, b, a);
      buf.vertex(m, x3, y3, z3).color(r, g, b, a);
   }

   private void i6SGGvG(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
      long now = System.currentTimeMillis();
      if (this.pS0tg0o == 0L) {
         this.pS0tg0o = now;
      }

      float dt = Math.min((float)(now - this.pS0tg0o) / 1000.0F, 0.1F);
      this.pS0tg0o = now;
      if (target != null) {
         this.mbEkU2 += dt;

         while (this.mbEkU2 >= 0.017F) {
            this.mbEkU2 -= 0.017F;

            for (int i = 0; i < 1; i++) {
               double rand = MathUtil.random(0.0F, 360.0F);
               double px = Math.cos(Math.toRadians(rand)) * 0.7;
               double py = MathUtil.random(0.04, 0.2);
               double pz = Math.sin(Math.toRadians(rand)) * 0.7;
               this.vfB8l79.add(new TargetESP.Particle(target, px, py, pz));
            }
         }
      }

      float camYaw = camera.getYaw();
      float camPitch = camera.getPitch();
      Iterator<TargetESP.Particle> it = this.vfB8l79.iterator();
      ArrayList<TargetESP.Particle> toRender = new ArrayList<>();

      while (it.hasNext()) {
         TargetESP.Particle p = it.next();
         p.update(dt);
         if (now - p.time > 1000L) {
            it.remove();
         } else {
            toRender.add(p);
         }
      }

      int color = this.xzI01();

      for (TargetESP.Particle p : toRender) {
         p.renderCube(matrices, camera, color, partialTicks);
      }

      if (!toRender.isEmpty()) {
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.kdGo());
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         boolean hasBloom = false;

         for (TargetESP.Particle p : toRender) {
            if (p.renderBloom(builder, matrices, camera, color, camYaw, camPitch, partialTicks)) {
               hasBloom = true;
            }
         }

         if (hasBloom) {
            BufferRenderer.drawWithGlobalProgram(builder.end());
         }

         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.enableCull();
      }
   }

   private void ix32Nv(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
      while (this.np2dgBx.size() < 3) {
         this.np2dgBx.add(new TargetESP.RiderParticle(new Vec3d(target.getX(), target.getY() + target.getHeight() / 2.0, target.getZ())));
      }

      float fps = Math.max(this.mc.getCurrentFps(), 5);
      float fpsFactor = 500.0F / fps;
      this.byWxsg = (this.byWxsg + 20.0F * fpsFactor / 55.0F) % 36000.0F;
      this.shC1i.run(target.hurtTime > 7 ? 1.0F : 0.0F);
      Vec3d camPos = camera.getPos();
      float camYaw = camera.getYaw();
      float camPitch = camera.getPitch();
      double tx = MathHelper.lerp((double)partialTicks, target.lastRenderX, target.getX());
      double ty = MathHelper.lerp((double)partialTicks, target.lastRenderY, target.getY());
      double tz = MathHelper.lerp((double)partialTicks, target.lastRenderZ, target.getZ());
      int colorInt = this.xzI01();
      float r = ColorProvider.red(colorInt) / 255.0F;
      float g = ColorProvider.green(colorInt) / 255.0F;
      float b = ColorProvider.blue(colorInt) / 255.0F;
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.setShaderTexture(0, this.kdGo());
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      List<TargetESP.RiderParticle> toRemove = new ArrayList<>();
      float mul = 0.05F * fpsFactor;

      for (int i = 0; i < this.np2dgBx.size(); i++) {
         TargetESP.RiderParticle p = this.np2dgBx.get(i);
         float angleOffset = i * 120.0F;
         float currentAngle = this.byWxsg + angleOffset;
         double rad = Math.toRadians(currentAngle);
         float collapseAnim = this.shC1i.getValue();
         float orbitRadius = 0.6F * (1.0F - collapseAnim);
         double targetX = tx + Math.sin(rad) * orbitRadius;
         double targetY = ty + 0.2 + target.getHeight() / 2.0 * Math.sin(Math.toRadians(this.byWxsg / (i + 1.0F)));
         double targetZ = tz + Math.cos(rad) * orbitRadius;
         double mx = (targetX - p.pos.x) * mul;
         double my = (targetY - p.pos.y) * mul;
         double mz = (targetZ - p.pos.z) * mul;
         p.pos = p.pos.add(mx, my, mz);
         p.alpha = Math.max(0.0F, p.alpha - 0.005F * fpsFactor);
         if (p.alpha <= 0.0F) {
            toRemove.add(p);
         } else {
            double px = p.pos.x - camPos.x;
            double py = p.pos.y - camPos.y;
            double pz = p.pos.z - camPos.z;
            float cr = Math.min(1.0F, r * 1.5F);
            float cg = Math.min(1.0F, g * 1.5F);
            float cb = Math.min(1.0F, b * 1.5F);
            this.j2F93r(builder, matrices, px, py, pz, 0.6F, r, g, b, p.alpha * 0.15F, camYaw, camPitch);
            this.j2F93r(builder, matrices, px, py, pz, 0.35F, r, g, b, p.alpha * 0.35F, camYaw, camPitch);
            this.j2F93r(builder, matrices, px, py, pz, 0.15F, cr, cg, cb, p.alpha, camYaw, camPitch);
         }
      }

      this.np2dgBx.removeAll(toRemove);
      if (!this.np2dgBx.isEmpty()) {
         BufferRenderer.drawWithGlobalProgram(builder.end());
      } else {
         builder.end();
      }

      this.to08wo();
   }

   private void py5sMp(MatrixStack matrices, float tickDelta) {
      Camera camera = this.mc.gameRenderer.getCamera();
      if (camera != null) {
         Vec3d camPos = camera.getPos();
         LivingEntity target = null;
         KillAura killAura = Viola.getInstance().getModuleStorage().get(KillAura.class);
         if (killAura != null && killAura.isEnabled()) {
            target = killAura.getTarget();
         }

         if (target != null) {
            this.jyyhV = target;
            this.zrlZl.run(1.0F);
         } else {
            this.zrlZl.run(0.0F);
            if (this.zrlZl.getValue() == 0.0F) {
               this.jyyhV = null;
            }
         }

         if (this.jyyhV != null) {
            if (!(this.zrlZl.getValue() <= 0.01)) {
               float alphaAnim = this.zrlZl.getValue();
               float easing = 1.0F - (float)Math.pow(1.0F - alphaAnim, 3.0);
               this.nbg7xT6(matrices, camera, camPos, tickDelta, this.jyyhV, easing);
            }
         }
      }
   }

   private void nbg7xT6(MatrixStack matrices, Camera camera, Vec3d camPos, float tickDelta, Entity entity, float alpha) {
      double x = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
      double y = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY()) + entity.getHeight() / 2.0;
      double z = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
      double t = System.currentTimeMillis() * 0.001 * this.achC.getFloatValue();
      double h = entity.getHeight();
      double[] oy = new double[]{-h * 0.35 + 0.15, 0.0, h * 0.45 - 0.15};
      double[] r = new double[]{0.62, 0.58, 0.56};
      double[] phase = new double[]{0.0, 0.33, 0.66};
      float baseScale = (float)(0.16F * Math.max(0.2, (double)alpha));
      int segments = 47;
      double tailSpan = Math.PI * 3.0 / 5.0;
      double headCut = 1.0 - alpha;
      double headFadeWidth = 0.002;
      int colorTheme = this.xzI01();
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
      RenderSystem.enableBlend();

      for (int i = 0; i < 3; i++) {
         double headAng = (t + phase[i]) * Math.PI * 2.0;

         for (int j = 0; j < segments; j++) {
            double s = (double)j / (segments - 1);
            if (!(s < headCut - headFadeWidth)) {
               double headFade = MathHelper.clamp((s - (headCut - headFadeWidth)) / headFadeWidth, 0.0, 1.0);
               double ease = 1.0 - s;
               double ang = headAng - s * tailSpan;
               double rr = r[i] * (1.0 - 0.06 * s) + 0.1 * Math.sin(ang * 1.45 + i);
               double dx = Math.cos(ang) * rr;
               double dz = Math.sin(ang) * rr;
               double dy = oy[i] + 0.12 * Math.sin(ang * 1.25 + i * 0.6);
               float headSize = i == 1 ? 1.1F : 1.0F;
               float tailSize = 0.002F;
               float size = (float)(baseScale * (tailSize + (headSize - tailSize) * Math.pow(ease, 0.6)));
               float whiteAlpha = Math.min(1.0F, 1.05F * (float)(alpha * Math.pow(ease, 0.9) * headFade));
               float colorAlpha = Math.min(0.55F, 1.1F * (float)(alpha * Math.pow(ease, 1.1) * headFade));
               if (!(size < 1.0E-4F) || !(whiteAlpha < 0.001F) || !(colorAlpha < 0.001F)) {
                  float colorHaloSize = size * 1.34F;
                  float whiteHaloSize = size * 0.8F;
                  int whiteA = Math.max(0, Math.min(255, (int)(whiteAlpha * 255.0F)));
                  int colA = Math.max(0, Math.min(255, (int)(colorAlpha * 255.0F)));
                  int cr = ColorProvider.red(colorTheme);
                  int cg = ColorProvider.green(colorTheme);
                  int cb = ColorProvider.blue(colorTheme);
                  matrices.push();
                  matrices.translate(x - camPos.x + dx, y - camPos.y + dy, z - camPos.z + dz);
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                  matrices.scale(-1.0F, -1.0F, 1.0F);
                  Matrix4f m = matrices.peek().getPositionMatrix();
                  RenderSystem.disableDepthTest();
                  RenderSystem.enableBlend();
                  RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
                  RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
                  RenderSystem.setShaderTexture(0, this.kdGo());
                  BufferBuilder buf = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                  buf.vertex(m, -whiteHaloSize, -whiteHaloSize, 0.0F).texture(0.0F, 1.0F).color(255, 255, 255, whiteA);
                  buf.vertex(m, whiteHaloSize, -whiteHaloSize, 0.0F).texture(1.0F, 1.0F).color(255, 255, 255, whiteA);
                  buf.vertex(m, whiteHaloSize, whiteHaloSize, 0.0F).texture(1.0F, 0.0F).color(255, 255, 255, whiteA);
                  buf.vertex(m, -whiteHaloSize, whiteHaloSize, 0.0F).texture(0.0F, 0.0F).color(255, 255, 255, whiteA);
                  BufferRenderer.drawWithGlobalProgram(buf.end());
                  buf = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                  buf.vertex(m, -colorHaloSize, -colorHaloSize, 0.0F).texture(0.0F, 1.0F).color(cr, cg, cb, colA);
                  buf.vertex(m, colorHaloSize, -colorHaloSize, 0.0F).texture(1.0F, 1.0F).color(cr, cg, cb, colA);
                  buf.vertex(m, colorHaloSize, colorHaloSize, 0.0F).texture(1.0F, 0.0F).color(cr, cg, cb, colA);
                  buf.vertex(m, -colorHaloSize, colorHaloSize, 0.0F).texture(0.0F, 0.0F).color(cr, cg, cb, colA);
                  BufferRenderer.drawWithGlobalProgram(buf.end());
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.disableBlend();
                  RenderSystem.enableDepthTest();
                  matrices.pop();
               }
            }
         }
      }

      RenderSystem.depthMask(true);
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableCull();
   }

   private void nUTgFvj(MatrixStack matrices, Camera camera, LivingEntity target, float tickDelta) {
      Vec3d camPos = camera.getPos();
      double tx = MathHelper.lerp((double)tickDelta, target.lastRenderX, target.getX());
      double ty = MathHelper.lerp((double)tickDelta, target.lastRenderY, target.getY());
      double tz = MathHelper.lerp((double)tickDelta, target.lastRenderZ, target.getZ());
      double x = tx - camPos.x;
      double y = ty - camPos.y;
      double z = tz - camPos.z;
      float width = target.getWidth() * 1.5F;
      int color = this.xzI01();
      float animVal = this.pjUb9.getValue();
      float alpha = animVal;
      this.wEfJ3 = (float)(System.currentTimeMillis() % 100000L) / 1.5F;
      int r = ColorProvider.red(color);
      int g = ColorProvider.green(color);
      int b = ColorProvider.blue(color);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.setShaderTexture(0, this.kdGo());
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      int step = 2;
      int wormTick = 0;
      int wormCD = 0;

      for (int i = 0; i < 360; i += step) {
         float size = 0.13F + 0.005F * wormTick;
         float bigSize = 0.7F + 0.005F * wormTick;
         if (wormCD > 0) {
            wormCD -= step;
         } else if ((wormTick += step) > 50) {
            wormCD = 100;
            wormTick = 0;
         } else {
            float val = Math.max(0.5F, 1.2F - 0.5F * animVal);
            float angleRad = (float)Math.toRadians(i + this.wEfJ3);
            float sin = (float)(Math.sin(angleRad) * width * val);
            float cos = (float)(Math.cos(angleRad) * width * val);
            float waveY = (float)Math.sin(Math.toRadians(i / 2.0F + this.wEfJ3 / 5.0F));
            matrices.push();
            matrices.translate(x + sin, y + target.getHeight() / 1.5F + target.getHeight() / 3.0F * waveY, z + cos);
            matrices.multiply(camera.getRotation());
            Matrix4f mat = matrices.peek().getPositionMatrix();
            float bigA = alpha * 0.05F;
            int bigAlpha = Math.min(255, Math.max(0, (int)(bigA * 255.0F)));
            builder.vertex(mat, -bigSize / 2.0F, bigSize / 2.0F, 0.0F).texture(0.0F, 1.0F).color(r, g, b, bigAlpha);
            builder.vertex(mat, bigSize / 2.0F, bigSize / 2.0F, 0.0F).texture(1.0F, 1.0F).color(r, g, b, bigAlpha);
            builder.vertex(mat, bigSize / 2.0F, -bigSize / 2.0F, 0.0F).texture(1.0F, 0.0F).color(r, g, b, bigAlpha);
            builder.vertex(mat, -bigSize / 2.0F, -bigSize / 2.0F, 0.0F).texture(0.0F, 0.0F).color(r, g, b, bigAlpha);
            int coreAlpha = Math.min(255, Math.max(0, (int)(alpha * 255.0F)));
            builder.vertex(mat, -size / 2.0F, size / 2.0F, 0.0F).texture(0.0F, 1.0F).color(r, g, b, coreAlpha);
            builder.vertex(mat, size / 2.0F, size / 2.0F, 0.0F).texture(1.0F, 1.0F).color(r, g, b, coreAlpha);
            builder.vertex(mat, size / 2.0F, -size / 2.0F, 0.0F).texture(1.0F, 0.0F).color(r, g, b, coreAlpha);
            builder.vertex(mat, -size / 2.0F, -size / 2.0F, 0.0F).texture(0.0F, 0.0F).color(r, g, b, coreAlpha);
            matrices.pop();
         }
      }

      BufferRenderer.drawWithGlobalProgram(builder.end());
      this.to08wo();
   }

   private static float ufgkC(float current, float target, float delta) {
      if (current < target) {
         return Math.min(current + delta, target);
      } else {
         return current > target ? Math.max(current - delta, target) : current;
      }
   }

   private static int eRrp4gf(int color, int alpha) {
      alpha = Math.max(0, Math.min(255, alpha));
      return alpha << 24 | color & 16777215;
   }

   private void nuumZnx(Camera camera, LivingEntity target, float partialTicks, boolean hasTarget) {
      this.ooSpfo = ufgkC(this.ooSpfo, hasTarget ? 1.0F : 0.0F, 0.05F);
      if (hasTarget && target != null) {
         this.w5m12 = new Vec3d(
            MathHelper.lerp((double)partialTicks, target.lastRenderX, target.getX()),
            MathHelper.lerp((double)partialTicks, target.lastRenderY, target.getY()),
            MathHelper.lerp((double)partialTicks, target.lastRenderZ, target.getZ())
         );
         this.dPjwNa = target.getHeight();
      }

      if (this.ooSpfo <= 0.001F && !hasTarget) {
         this.w5m12 = null;
         this.d7eejFb = 0.0F;
      }
   }

   private void dkfj3zA(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks, boolean hasTarget) {
      this.nuumZnx(camera, target, partialTicks, hasTarget);
      if (this.w5m12 != null && !(this.ooSpfo <= 0.001F)) {
         Vec3d cam = camera.getPos();
         double worldX = this.w5m12.x;
         double worldY = this.w5m12.y + (this.dPjwNa + 0.4F) * 0.5F;
         double worldZ = this.w5m12.z;
         float baseSize = (float)(this.e9G7M.getValue() * 12.0);
         this.ss9k = ufgkC(this.ss9k, hasTarget ? 1.0F : 0.5F, 0.05F);
         float renderSize = baseSize * this.ss9k;
         long now = System.currentTimeMillis();
         float dt = Math.max(0.001F, (float)(now - this.oi7Za) / 1000.0F);
         this.oi7Za = now;
         float cycleDuration = Math.max(0.35F, (float)(2.2F / this.a3tgZ0l.getValue()));

         for (this.d7eejFb += dt / cycleDuration; this.d7eejFb >= 1.0F; this.d7eejFb--) {
            this.cpfP1wW = this.iyaej;
            this.iyaej = this.iyaej > 0.0F ? -280.0F : 280.0F;
         }

         float eased = 0.5F - 0.5F * MathHelper.cos((float)(Math.PI * this.d7eejFb));
         float rotation = MathHelper.lerp(eased, this.cpfP1wW, this.iyaej);
         int color = eRrp4gf(this.xzI01(), (int)(255.0F * this.ooSpfo));
         RenderSystem.enableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.vg27());
         this.uaY8Tg(matrices, cam, worldX, worldY, worldZ, renderSize, color, rotation);
         this.to08wo();
      }
   }

   private void ju4k(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks, boolean hasTarget) {
      this.nuumZnx(camera, target, partialTicks, hasTarget);
      if (this.w5m12 != null && !(this.ooSpfo <= 0.001F)) {
         Vec3d cam = camera.getPos();
         double x = this.w5m12.x - cam.x;
         double y = this.w5m12.y - cam.y;
         double z = this.w5m12.z - cam.z;
         double entityHeight = this.dPjwNa;
         double duration = 2000.0 / this.dN4x.getValue();
         double elapsed = System.currentTimeMillis() % (long)duration;
         boolean side = elapsed > duration / 2.0;
         double progress = elapsed / (duration / 2.0);
         if (side) {
            progress--;
         } else {
            progress = 1.0 - progress;
         }

         progress = progress < 0.5 ? 2.0 * progress * progress : 1.0 - Math.pow(-2.0 * progress + 2.0, 2.0) / 2.0;
         double eased = entityHeight / 1.2 * (progress > 0.5 ? 1.0 - progress : progress) * (side ? -1 : 1);
         int baseCol = this.xzI01();
         int colorWithAlpha = fn46(baseCol, 0.882F * this.ooSpfo);
         int colorTransparent = fn46(baseCol, 0.0039F * this.ooSpfo);
         int colorFull = fn46(baseCol, this.ooSpfo);
         double radius = this.k1gbhU.getValue();
         Matrix4f matrix = matrices.peek().getPositionMatrix();
         RenderSystem.depthMask(false);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
         RenderSystem.disableCull();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

         for (int i = 0; i <= 360; i++) {
            double rad = Math.toRadians(i);
            float px = (float)(x + Math.cos(rad) * radius);
            float pz = (float)(z + Math.sin(rad) * radius);
            float py1 = (float)(y + entityHeight * progress);
            float py2 = (float)(y + entityHeight * progress + eased);
            buffer.vertex(matrix, px, py1, pz).color(colorWithAlpha);
            buffer.vertex(matrix, px, py2, pz).color(colorTransparent);
         }

         BufferRenderer.drawWithGlobalProgram(buffer.end());
         RenderSystem.lineWidth(1.5F);
         BufferBuilder lineBuffer = Tessellator.getInstance().begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

         for (int i = 0; i <= 360; i++) {
            double rad = Math.toRadians(i);
            float px = (float)(x + Math.cos(rad) * radius);
            float pz = (float)(z + Math.sin(rad) * radius);
            float py = (float)(y + entityHeight * progress);
            lineBuffer.vertex(matrix, px, py, pz).color(colorFull);
         }

         BufferRenderer.drawWithGlobalProgram(lineBuffer.end());
         this.to08wo();
      }
   }

   private static int fn46(int color, float alpha) {
      alpha = Math.max(0.0F, Math.min(1.0F, alpha));
      return color & 16777215 | (int)(alpha * 255.0F) << 24;
   }

   private float ti5q(Vec3d cameraPos, double worldX, double worldY, double worldZ) {
      double dx = worldX - cameraPos.x;
      double dy = worldY - cameraPos.y;
      double dz = worldZ - cameraPos.z;
      double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
      return (float)Math.max(0.1, distance * 0.007);
   }

   private void uaY8Tg(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float baseScreenSize, int color, float rotation) {
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      int a = color >> 24 & 0xFF;
      if (a > 0) {
         float half = baseScreenSize * this.ti5q(cameraPos, worldX, worldY, worldZ) * 0.5F;
         matrices.push();
         matrices.translate(worldX - cameraPos.x, worldY - cameraPos.y, worldZ - cameraPos.z);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-this.mc.gameRenderer.getCamera().getYaw()));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.mc.gameRenderer.getCamera().getPitch()));
         if (rotation != 0.0F) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
         }

         Matrix4f matrix = matrices.peek().getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(matrix, -half, -half, 0.0F).texture(0.0F, 1.0F).color(r, g, b, a);
         buffer.vertex(matrix, -half, half, 0.0F).texture(0.0F, 0.0F).color(r, g, b, a);
         buffer.vertex(matrix, half, half, 0.0F).texture(1.0F, 0.0F).color(r, g, b, a);
         buffer.vertex(matrix, half, -half, 0.0F).texture(1.0F, 1.0F).color(r, g, b, a);
         BufferRenderer.drawWithGlobalProgram(buffer.end());
         matrices.pop();
      }
   }

   private Identifier vg27() {
      return this.mode.is("Картинка 2")
         ? Identifier.of("mre", "images/targetesp_3.png")
         : Identifier.of("mre", "images/targetesp_2.png");
   }

   private float ofvxp(TargetESP.LightningBolt bolt, long now) {
      float life = MathHelper.clamp((float)(now - bolt.spawn) / (float)bolt.life, 0.0F, 1.0F);
      float fade = life < 0.18F ? life / 0.18F : 1.0F - (life - 0.18F) / 0.82F;
      float flicker = 0.78F + 0.22F * (float)Math.sin((float)now * 0.045F + bolt.phase);
      return MathHelper.clamp(fade, 0.0F, 1.0F) * flicker;
   }

   private void yz879(MatrixStack matrices, Camera camera, float tickDelta, boolean hasTarget) {
      LivingEntity tgt = hasTarget && this.bPgf4z instanceof LivingEntity le && le.isAlive() ? le : null;
      this.xih2ub6 = ufgkC(this.xih2ub6, hasTarget ? 1.0F : 0.0F, 0.05F);
      if (tgt != null) {
         this.egdhS = tgt;
      }

      if (this.xih2ub6 <= 0.001F && !hasTarget) {
         this.egdhS = null;
         this.joqeyJ = null;
      } else {
         if (tgt != null) {
            this.joqeyJ = new Vec3d(
               MathHelper.lerp((double)tickDelta, tgt.lastRenderX, tgt.getX()),
               MathHelper.lerp((double)tickDelta, tgt.lastRenderY, tgt.getY()),
               MathHelper.lerp((double)tickDelta, tgt.lastRenderZ, tgt.getZ())
            );
         }

         if (this.egdhS != null || this.joqeyJ != null) {
            long now = System.currentTimeMillis();
            int maxBolts = (int)this.ahpmDnC.getFloatValue();
            if (hasTarget && this.egdhS != null && this.egdhS.isAlive() && this.gUnAhv8.size() < maxBolts && now - this.xoEr0 > 15L) {
               int burst = Math.min(maxBolts - this.gUnAhv8.size(), 4);

               for (int i = 0; i < burst; i++) {
                  this.gUnAhv8.add(this.yZBH(this.egdhS));
               }

               this.xoEr0 = now;
            }

            this.gUnAhv8.removeIf(b -> now - b.spawn > b.life);
            if (!this.gUnAhv8.isEmpty()) {
               Vec3d cam = camera.getPos();
               Vec3d basePos;
               if (this.egdhS != null && this.egdhS.isAlive()) {
                  basePos = new Vec3d(
                     MathHelper.lerp((double)tickDelta, this.egdhS.lastRenderX, this.egdhS.getX()),
                     MathHelper.lerp((double)tickDelta, this.egdhS.lastRenderY, this.egdhS.getY()),
                     MathHelper.lerp((double)tickDelta, this.egdhS.lastRenderZ, this.egdhS.getZ())
                  );
               } else {
                  basePos = this.joqeyJ;
               }

               if (basePos != null) {
                  int baseColor = this.xzI01();
                  int glowColor = baseColor;
                  int coreColor = -1;
                  float widthScale = this.mhm5eI2.getFloatValue();
                  float appearValue = this.xih2ub6;
                  RenderSystem.disableDepthTest();
                  RenderSystem.enableBlend();
                  RenderSystem.depthMask(false);
                  RenderSystem.disableCull();
                  RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
                  RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
                  boolean hasOuter = false;
                  BufferBuilder outerBuffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                  for (TargetESP.LightningBolt bolt : this.gUnAhv8) {
                     float alpha = this.ofvxp(bolt, now) * appearValue;
                     if (!(alpha <= 0.02F)) {
                        int outerA = (int)(alpha * 55.0F);
                        if (outerA > 0 && zzg8Xf9(outerBuffer, matrices, basePos, cam, bolt.points, 0.06F * widthScale, eRrp4gf(glowColor, outerA))) {
                           hasOuter = true;
                        }

                        for (List<Vec3d> branch : bolt.branches) {
                           if (outerA > 0 && zzg8Xf9(outerBuffer, matrices, basePos, cam, branch, 0.04F * widthScale, eRrp4gf(glowColor, (int)(outerA * 0.8F)))
                              )
                            {
                              hasOuter = true;
                           }
                        }
                     }
                  }

                  if (hasOuter) {
                     BufferRenderer.drawWithGlobalProgram(outerBuffer.end());
                  }

                  boolean hasGlow = false;
                  BufferBuilder glowBuffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                  for (TargetESP.LightningBolt boltx : this.gUnAhv8) {
                     float alpha = this.ofvxp(boltx, now) * appearValue;
                     if (!(alpha <= 0.02F)) {
                        int glowA = (int)(alpha * 120.0F);
                        if (glowA > 0 && zzg8Xf9(glowBuffer, matrices, basePos, cam, boltx.points, 0.025F * widthScale, eRrp4gf(glowColor, glowA))) {
                           hasGlow = true;
                        }

                        for (List<Vec3d> branchx : boltx.branches) {
                           if (glowA > 0 && zzg8Xf9(glowBuffer, matrices, basePos, cam, branchx, 0.017F * widthScale, eRrp4gf(glowColor, (int)(glowA * 0.75F)))
                              )
                            {
                              hasGlow = true;
                           }
                        }
                     }
                  }

                  if (hasGlow) {
                     BufferRenderer.drawWithGlobalProgram(glowBuffer.end());
                  }

                  boolean hasCore = false;
                  BufferBuilder coreBuffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

                  for (TargetESP.LightningBolt boltxx : this.gUnAhv8) {
                     float alpha = this.ofvxp(boltxx, now) * appearValue;
                     if (!(alpha <= 0.02F)) {
                        int coreA = (int)(alpha * 255.0F);
                        if (coreA > 0 && zzg8Xf9(coreBuffer, matrices, basePos, cam, boltxx.points, 0.0085F * widthScale, eRrp4gf(coreColor, coreA))) {
                           hasCore = true;
                        }

                        for (List<Vec3d> branchxx : boltxx.branches) {
                           if (coreA > 0
                              && zzg8Xf9(coreBuffer, matrices, basePos, cam, branchxx, 0.006F * widthScale, eRrp4gf(coreColor, (int)(coreA * 0.85F)))) {
                              hasCore = true;
                           }
                        }
                     }
                  }

                  if (hasCore) {
                     BufferRenderer.drawWithGlobalProgram(coreBuffer.end());
                  }

                  RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
                  RenderSystem.setShaderTexture(0, this.kdGo());

                  for (TargetESP.LightningBolt boltxxx : this.gUnAhv8) {
                     float alpha = this.ofvxp(boltxxx, now) * appearValue;
                     if (!(alpha <= 0.05F)) {
                        int bloomA = (int)(alpha * 150.0F);
                        if (bloomA > 0) {
                           int bloomColor = eRrp4gf(glowColor, bloomA);

                           for (int i = 0; i < boltxxx.points.size(); i += 2) {
                              Vec3d p = basePos.add(boltxxx.points.get(i));
                              this.j4bch(matrices, cam, p.x, p.y, p.z, 0.22F * widthScale, bloomColor);
                           }

                           Vec3d impact = basePos.add(boltxxx.points.get(boltxxx.points.size() - 1));
                           this.j4bch(matrices, cam, impact.x, impact.y, impact.z, 0.55F * widthScale, bloomColor);
                           Vec3d source = basePos.add(boltxxx.points.get(0));
                           this.j4bch(matrices, cam, source.x, source.y, source.z, 0.35F * widthScale, eRrp4gf(glowColor, (int)(bloomA * 0.7F)));
                        }
                     }
                  }

                  RenderSystem.enableCull();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.disableBlend();
                  RenderSystem.depthMask(true);
                  RenderSystem.enableDepthTest();
               }
            }
         }
      }
   }

   private TargetESP.LightningBolt yZBH(LivingEntity target) {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      float radius = target.getWidth() * 0.5F;
      float height = target.getHeight();
      double startAngle = random.nextDouble() * Math.PI * 2.0;
      double startY = 0.15 + random.nextDouble() * Math.max(0.2, height - 0.4);
      Vec3d start = new Vec3d(Math.cos(startAngle) * radius * 0.7, startY, Math.sin(startAngle) * radius * 0.7);
      double theta = random.nextDouble() * Math.PI * 2.0;
      double phi = Math.toRadians((random.nextDouble() - 0.5) * 150.0);
      Vec3d dir = new Vec3d(Math.cos(phi) * Math.cos(theta), Math.sin(phi) * 0.8 + 0.25, Math.cos(phi) * Math.sin(theta)).normalize();
      double length = 0.35 + random.nextDouble() * 0.45;
      Vec3d end = start.add(dir.multiply(length));
      ArrayList<Vec3d> points = new ArrayList<>(List.of(start, end));
      points = this.pkfsFdK(points, 0.09, 3);
      ArrayList<List<Vec3d>> branches = new ArrayList<>();
      int branchCount = 1 + random.nextInt(3);

      for (int i = 0; i < branchCount; i++) {
         int anchorIndex = 1 + random.nextInt(Math.max(1, points.size() - 2));
         Vec3d anchor = points.get(anchorIndex);
         Vec3d branchDir = oxsyed(dir);
         if (random.nextBoolean()) {
            branchDir = branchDir.multiply(-1.0);
         }

         double branchLength = 0.12 + random.nextDouble() * 0.18;
         Vec3d branchEnd = anchor.add(branchDir.multiply(branchLength));
         ArrayList<Vec3d> branchPoints = new ArrayList<>(List.of(anchor, branchEnd));
         branches.add(this.pkfsFdK(branchPoints, 0.05, 2));
      }

      return new TargetESP.LightningBolt(points, branches, 250L + random.nextLong(250L), random.nextFloat() * 6.28F);
   }

   private ArrayList<Vec3d> pkfsFdK(ArrayList<Vec3d> input, double amount, int passes) {
      ArrayList<Vec3d> points = new ArrayList<>(input);
      double amt = amount;

      for (int pass = 0; pass < passes; pass++) {
         ArrayList<Vec3d> next = new ArrayList<>(points.size() * 2);

         for (int i = 0; i < points.size() - 1; i++) {
            Vec3d a = points.get(i);
            Vec3d b = points.get(i + 1);
            next.add(a);
            Vec3d dir = b.subtract(a);
            if (dir.lengthSquared() < 1.0E-7) {
               next.add(a.add(b).multiply(0.5));
            } else {
               Vec3d perp = oxsyed(dir.normalize());
               double offset = (ThreadLocalRandom.current().nextDouble() - 0.5) * 2.0 * amt;
               next.add(a.add(b).multiply(0.5).add(perp.multiply(offset)));
            }
         }

         next.add(points.get(points.size() - 1));
         points = next;
         amt *= 0.5;
      }

      return points;
   }

   private static Vec3d oxsyed(Vec3d dir) {
      Vec3d arbitrary = Math.abs(dir.y) < 0.9 ? new Vec3d(0.0, 1.0, 0.0) : new Vec3d(1.0, 0.0, 0.0);
      Vec3d perp1 = dir.crossProduct(arbitrary).normalize();
      Vec3d perp2 = dir.crossProduct(perp1).normalize();
      double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0;
      return perp1.multiply(Math.cos(angle)).add(perp2.multiply(Math.sin(angle)));
   }

   private static boolean zzg8Xf9(BufferBuilder buffer, MatrixStack matrices, Vec3d base, Vec3d cam, List<Vec3d> points, float width, int color) {
      int a = color >> 24 & 0xFF;
      if (a > 0 && points.size() >= 2) {
         int r = color >> 16 & 0xFF;
         int g = color >> 8 & 0xFF;
         int b = color & 0xFF;
         Matrix4f matrix = matrices.peek().getPositionMatrix();
         float half = width * 0.5F;
         int appended = 0;

         for (int i = 0; i < points.size() - 1; i++) {
            Vec3d p1 = base.add(points.get(i));
            Vec3d p2 = base.add(points.get(i + 1));
            Vec3d segDir = p2.subtract(p1);
            if (!(segDir.lengthSquared() < 1.0E-8)) {
               Vec3d viewDir = p1.add(p2).multiply(0.5).subtract(cam);
               if (!(viewDir.lengthSquared() < 1.0E-8)) {
                  Vec3d side = segDir.crossProduct(viewDir);
                  double sideLen = side.length();
                  if (!(sideLen < 1.0E-8) && Double.isFinite(sideLen)) {
                     side = side.multiply(1.0 / sideLen);
                     float x1 = (float)(p1.x - cam.x);
                     float y1 = (float)(p1.y - cam.y);
                     float z1 = (float)(p1.z - cam.z);
                     float x2 = (float)(p2.x - cam.x);
                     float y2 = (float)(p2.y - cam.y);
                     float z2 = (float)(p2.z - cam.z);
                     float sx = (float)side.x * half;
                     float sy = (float)side.y * half;
                     float sz = (float)side.z * half;
                     buffer.vertex(matrix, x1 - sx, y1 - sy, z1 - sz).color(r, g, b, a);
                     buffer.vertex(matrix, x1 + sx, y1 + sy, z1 + sz).color(r, g, b, a);
                     buffer.vertex(matrix, x2 + sx, y2 + sy, z2 + sz).color(r, g, b, a);
                     buffer.vertex(matrix, x2 - sx, y2 - sy, z2 - sz).color(r, g, b, a);
                     appended++;
                  }
               }
            }
         }

         return appended > 0;
      } else {
         return false;
      }
   }

   private void lKzt(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float half, int color) {
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      int a = color >> 24 & 0xFF;
      if (a > 0) {
         matrices.push();
         matrices.translate(worldX - cameraPos.x, worldY - cameraPos.y, worldZ - cameraPos.z);
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-this.mc.gameRenderer.getCamera().getYaw()));
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.mc.gameRenderer.getCamera().getPitch()));
         Matrix4f matrix = matrices.peek().getPositionMatrix();
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         buffer.vertex(matrix, -half, -half, 0.0F).texture(0.0F, 1.0F).color(r, g, b, a);
         buffer.vertex(matrix, -half, half, 0.0F).texture(0.0F, 0.0F).color(r, g, b, a);
         buffer.vertex(matrix, half, half, 0.0F).texture(1.0F, 0.0F).color(r, g, b, a);
         buffer.vertex(matrix, half, -half, 0.0F).texture(1.0F, 1.0F).color(r, g, b, a);
         BufferRenderer.drawWithGlobalProgram(buffer.end());
         matrices.pop();
      }
   }

   private void j4bch(MatrixStack matrices, Vec3d cameraPos, double worldX, double worldY, double worldZ, float worldSize, int color) {
      this.lKzt(matrices, cameraPos, worldX, worldY, worldZ, worldSize * 0.5F, color);
   }

   private void fayour(MatrixStack matrices, Camera camera, LivingEntity target, float partialTicks) {
      if (target != null && this.mc.player != null) {
         Vec3d camPos = camera.getPos();
         double tx = MathHelper.lerp((double)partialTicks, target.lastRenderX, target.getX());
         double ty = MathHelper.lerp((double)partialTicks, target.lastRenderY, target.getY());
         double tz = MathHelper.lerp((double)partialTicks, target.lastRenderZ, target.getZ());
         double x = tx - camPos.x;
         double y = ty - camPos.y;
         double z = tz - camPos.z;
         int color = this.xzI01();
         float r = ColorProvider.red(color) / 255.0F;
         float g = ColorProvider.green(color) / 255.0F;
         float b = ColorProvider.blue(color) / 255.0F;
         float size = target.getWidth() * 1.4F;
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, Identifier.of("mre", "images/skull.png"));
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         matrices.push();
         matrices.translate(x, y + target.getHeight() * 0.55F, z);
         matrices.multiply(camera.getRotation());
         matrices.scale(size, size, size);
         Matrix4f mat = matrices.peek().getPositionMatrix();
         builder.vertex(mat, -0.5F, 0.5F, 0.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255);
         builder.vertex(mat, 0.5F, 0.5F, 0.0F).texture(1.0F, 0.0F).color(255, 255, 255, 255);
         builder.vertex(mat, 0.5F, -0.5F, 0.0F).texture(1.0F, 1.0F).color(255, 255, 255, 255);
         builder.vertex(mat, -0.5F, -0.5F, 0.0F).texture(0.0F, 1.0F).color(255, 255, 255, 255);
         matrices.pop();
         BufferRenderer.drawWithGlobalProgram(builder.end());
         this.to08wo();
      }
   }

   private void j2F93r(
      BufferBuilder builder, MatrixStack ms, double x, double y, double z, float scale, float r, float g, float b, float a, float camYaw, float camPitch
   ) {
      if (!(a <= 0.001F) && !(scale <= 1.0E-4F)) {
         ms.push();
         ms.translate(x, y, z);
         ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camYaw));
         ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camPitch));
         ms.scale(scale, scale, scale);
         Matrix4f m = ms.peek().getPositionMatrix();
         int ri = (int)(r * 255.0F);
         int gi = (int)(g * 255.0F);
         int bi = (int)(b * 255.0F);
         int ai = (int)(a * 255.0F);
         builder.vertex(m, -0.5F, 0.5F, 0.0F).texture(0.0F, 1.0F).color(ri, gi, bi, ai);
         builder.vertex(m, 0.5F, 0.5F, 0.0F).texture(1.0F, 1.0F).color(ri, gi, bi, ai);
         builder.vertex(m, 0.5F, -0.5F, 0.0F).texture(1.0F, 0.0F).color(ri, gi, bi, ai);
         builder.vertex(m, -0.5F, -0.5F, 0.0F).texture(0.0F, 0.0F).color(ri, gi, bi, ai);
         ms.pop();
      }
   }

   private static float bh2gtku(float t) {
      float u = 1.0F - t;
      return 1.0F - u * u * u;
   }

   private void to08wo() {
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.enableCull();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private Identifier kdGo() {
      return Identifier.of("mre", "images/bloom.png");
   }

   private int xzI01() {
      return this.jNg43pp.getValue() ? ColorProvider.getColorClient() : this.uyf8ovK.getValue();
   }

   private static class LightningBolt {
      final List<Vec3d> points;
      final List<List<Vec3d>> branches;
      final long spawn;
      final long life;
      final float phase;

      LightningBolt(List<Vec3d> points, List<List<Vec3d>> branches, long life, float phase) {
         this.points = points;
         this.branches = branches;
         this.spawn = System.currentTimeMillis();
         this.life = life;
         this.phase = phase;
      }
   }

   public static class Particle {
      double x;
      double y;
      double z;
      long time;
      LivingEntity entity;

      public Particle(LivingEntity entity, double x, double y, double z) {
         this.entity = entity;
         this.x = x;
         this.y = y;
         this.z = z;
         this.time = System.currentTimeMillis();
      }

      public void update(float dt) {
         this.y = this.y + MathUtil.random(0.01, 0.04) * (dt * 60.0F);
      }

      public void renderCube(MatrixStack ms, Camera camera, int colorInt, float partialTicks) {
         if (this.entity != null) {
            double alive = System.currentTimeMillis() - this.time;
            float life = Math.min(1.0F, (float)alive / 1000.0F);
            float alpha = life > 0.8F ? 1.0F - (life - 0.8F) * 5.0F : (alive < 200.0 ? (float)alive / 200.0F : 1.0F);
            if (!(alpha <= 0.001F)) {
               Vec3d cam = camera.getPos();
               double ex = MathHelper.lerp((double)partialTicks, this.entity.lastRenderX, this.entity.getX());
               double ey = MathHelper.lerp((double)partialTicks, this.entity.lastRenderY, this.entity.getY());
               double ez = MathHelper.lerp((double)partialTicks, this.entity.lastRenderZ, this.entity.getZ());
               float scale = 0.12F;
               int color = ColorProvider.setAlpha(colorInt, (int)(alpha * 255.0F));
               ms.push();
               ms.translate(ex - cam.x + this.x, ey - cam.y + this.y, ez - cam.z + this.z);
               double rot = alive / 10.0;
               ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)rot));
               ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)rot));
               ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)rot));
               ms.scale(scale, scale, scale);
               RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
               RenderSystem.enableBlend();
               RenderSystem.enableDepthTest();
               RenderSystem.disableCull();
               RenderSystem.depthMask(false);
               RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
               this.zuET(ms, color);
               RenderSystem.depthMask(true);
               RenderSystem.disableBlend();
               RenderSystem.enableCull();
               ms.pop();
            }
         }
      }

      public boolean renderBloom(BufferBuilder builder, MatrixStack ms, Camera camera, int colorInt, float camYaw, float camPitch, float partialTicks) {
         if (this.entity == null) {
            return false;
         } else {
            double alive = System.currentTimeMillis() - this.time;
            float life = Math.min(1.0F, (float)alive / 1000.0F);
            float alpha = life > 0.8F ? 1.0F - (life - 0.8F) * 5.0F : (alive < 200.0 ? (float)alive / 200.0F : 1.0F);
            if (alpha <= 0.001F) {
               return false;
            } else {
               float r = ColorProvider.red(colorInt) / 255.0F;
               float g = ColorProvider.green(colorInt) / 255.0F;
               float b = ColorProvider.blue(colorInt) / 255.0F;
               Vec3d cam = camera.getPos();
               double ex = MathHelper.lerp((double)partialTicks, this.entity.lastRenderX, this.entity.getX());
               double ey = MathHelper.lerp((double)partialTicks, this.entity.lastRenderY, this.entity.getY());
               double ez = MathHelper.lerp((double)partialTicks, this.entity.lastRenderZ, this.entity.getZ());
               ms.push();
               ms.translate(ex - cam.x + this.x, ey - cam.y + this.y, ez - cam.z + this.z);
               ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camYaw));
               ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camPitch));
               ms.scale(0.9F, 0.9F, 0.9F);
               Matrix4f m = ms.peek().getPositionMatrix();
               int ai = (int)(alpha * 0.12F * 255.0F);
               int ri = (int)(r * 255.0F);
               int gi = (int)(g * 255.0F);
               int bi = (int)(b * 255.0F);
               builder.vertex(m, -0.5F, 0.5F, 0.0F).texture(0.0F, 1.0F).color(ri, gi, bi, ai);
               builder.vertex(m, 0.5F, 0.5F, 0.0F).texture(1.0F, 1.0F).color(ri, gi, bi, ai);
               builder.vertex(m, 0.5F, -0.5F, 0.0F).texture(1.0F, 0.0F).color(ri, gi, bi, ai);
               builder.vertex(m, -0.5F, -0.5F, 0.0F).texture(0.0F, 0.0F).color(ri, gi, bi, ai);
               ms.pop();
               return true;
            }
         }
      }

      private void zuET(MatrixStack ms, int color) {
         float min = -0.5F;
         float max = 0.5F;
         float r = ColorProvider.red(color) / 255.0F;
         float g = ColorProvider.green(color) / 255.0F;
         float b = ColorProvider.blue(color) / 255.0F;
         float a = ColorProvider.alpha(color) / 255.0F;
         float lineA = a / 4.0F;
         float fillA = a / 12.0F;
         BufferBuilder buf = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
         Matrix4f m = ms.peek().getPositionMatrix();
         this.dg5Eb5J(buf, m, min, min, min, max, min, min, r, g, b, lineA);
         this.dg5Eb5J(buf, m, min, max, min, max, max, min, r, g, b, lineA);
         this.dg5Eb5J(buf, m, min, min, max, max, min, max, r, g, b, lineA);
         this.dg5Eb5J(buf, m, min, max, max, max, max, max, r, g, b, lineA);
         this.dg5Eb5J(buf, m, min, min, min, min, max, min, r, g, b, lineA);
         this.dg5Eb5J(buf, m, max, min, min, max, max, min, r, g, b, lineA);
         this.dg5Eb5J(buf, m, min, min, max, min, max, max, r, g, b, lineA);
         this.dg5Eb5J(buf, m, max, min, max, max, max, max, r, g, b, lineA);
         this.dg5Eb5J(buf, m, min, min, min, min, min, max, r, g, b, lineA);
         this.dg5Eb5J(buf, m, max, min, min, max, min, max, r, g, b, lineA);
         this.dg5Eb5J(buf, m, min, max, min, min, max, max, r, g, b, lineA);
         this.dg5Eb5J(buf, m, max, max, min, max, max, max, r, g, b, lineA);
         BufferRenderer.drawWithGlobalProgram(buf.end());
         BufferBuilder fb = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         this.lyXz(fb, m, min, min, min, max, min, max, r, g, b, fillA);
         this.lyXz(fb, m, min, max, min, max, max, max, r, g, b, fillA);
         this.lyXz(fb, m, min, min, min, max, max, min, r, g, b, fillA);
         this.lyXz(fb, m, min, min, max, max, max, max, r, g, b, fillA);
         this.lyXz(fb, m, min, min, min, min, max, max, r, g, b, fillA);
         this.lyXz(fb, m, max, min, min, max, max, max, r, g, b, fillA);
         BufferRenderer.drawWithGlobalProgram(fb.end());
      }

      private void dg5Eb5J(BufferBuilder buf, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
         buf.vertex(m, x1, y1, z1).color(r, g, b, a);
         buf.vertex(m, x2, y2, z2).color(r, g, b, a);
      }

      private void lyXz(BufferBuilder buf, Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
         buf.vertex(m, x1, y1, z1).color(r, g, b, a);
         buf.vertex(m, x2, y1, z1).color(r, g, b, a);
         buf.vertex(m, x2, y1, z2).color(r, g, b, a);
         buf.vertex(m, x1, y1, z2).color(r, g, b, a);
         buf.vertex(m, x1, y2, z1).color(r, g, b, a);
         buf.vertex(m, x1, y2, z2).color(r, g, b, a);
         buf.vertex(m, x2, y2, z2).color(r, g, b, a);
         buf.vertex(m, x2, y2, z1).color(r, g, b, a);
         buf.vertex(m, x1, y1, z1).color(r, g, b, a);
         buf.vertex(m, x1, y2, z1).color(r, g, b, a);
         buf.vertex(m, x2, y2, z1).color(r, g, b, a);
         buf.vertex(m, x2, y1, z1).color(r, g, b, a);
         buf.vertex(m, x1, y1, z2).color(r, g, b, a);
         buf.vertex(m, x2, y1, z2).color(r, g, b, a);
         buf.vertex(m, x2, y2, z2).color(r, g, b, a);
         buf.vertex(m, x1, y2, z2).color(r, g, b, a);
         buf.vertex(m, x1, y1, z1).color(r, g, b, a);
         buf.vertex(m, x1, y1, z2).color(r, g, b, a);
         buf.vertex(m, x1, y2, z2).color(r, g, b, a);
         buf.vertex(m, x1, y2, z1).color(r, g, b, a);
         buf.vertex(m, x2, y1, z1).color(r, g, b, a);
         buf.vertex(m, x2, y2, z1).color(r, g, b, a);
         buf.vertex(m, x2, y2, z2).color(r, g, b, a);
         buf.vertex(m, x2, y1, z2).color(r, g, b, a);
      }
   }

   private static class RiderParticle {
      Vec3d pos;
      float alpha;

      RiderParticle(Vec3d pos) {
         this.pos = pos;
         this.alpha = 0.3F;
      }
   }
}
