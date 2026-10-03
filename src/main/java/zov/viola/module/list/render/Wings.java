package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.render.providers.ColorProvider;

@ModuleInformation(
   moduleName = "Wings",
   moduleDesc = "РћР±СЉС‘РјРЅС‹Рµ РґСЂР°РєРѕРЅСЊРё РєСЂС‹Р»СЊСЏ Р·Р° СЃРїРёРЅРѕР№",
   moduleCategory = ModuleCategory.RENDER
)
public class Wings extends Module {
   private static final Wings.WingPoint[] POINTS = new Wings.WingPoint[]{
      new Wings.WingPoint(0.04F, 0.1F, -0.05F, 0.0F, 0.36F, 1.0F),
      new Wings.WingPoint(0.36F, 0.58F, -0.1F, 0.25F, 0.0F, 0.98F),
      new Wings.WingPoint(0.73F, 0.42F, 0.02F, 0.53F, 0.12F, 1.0F),
      new Wings.WingPoint(1.0F, 0.57F, 0.09F, 0.76F, 0.04F, 0.94F),
      new Wings.WingPoint(1.28F, 0.55F, 0.13F, 1.0F, 0.06F, 0.72F),
      new Wings.WingPoint(1.02F, 0.33F, 0.1F, 0.79F, 0.25F, 0.94F),
      new Wings.WingPoint(1.31F, 0.11F, 0.14F, 1.0F, 0.36F, 0.7F),
      new Wings.WingPoint(0.98F, 0.09F, 0.06F, 0.76F, 0.47F, 0.94F),
      new Wings.WingPoint(1.19F, -0.3F, 0.08F, 0.91F, 0.72F, 0.68F),
      new Wings.WingPoint(0.78F, -0.14F, -0.01F, 0.6F, 0.62F, 0.94F),
      new Wings.WingPoint(0.87F, -0.65F, -0.04F, 0.67F, 1.0F, 0.66F),
      new Wings.WingPoint(0.2F, -0.42F, -0.08F, 0.14F, 0.83F, 0.84F),
      new Wings.WingPoint(1.0F, 0.32F, 0.11F, 0.77F, 0.28F, 0.82F),
      new Wings.WingPoint(1.01F, -0.05F, 0.08F, 0.78F, 0.54F, 0.8F),
      new Wings.WingPoint(0.82F, -0.35F, 0.0F, 0.63F, 0.78F, 0.78F)
   };
   private static final int[][] PANELS = new int[][]{
      {0, 1, 2},
      {2, 3, 4},
      {2, 4, 12},
      {2, 12, 6},
      {2, 6, 5},
      {2, 6, 13},
      {2, 13, 8},
      {2, 8, 7},
      {2, 8, 14},
      {2, 14, 10},
      {2, 10, 9},
      {0, 2, 9},
      {0, 9, 10},
      {0, 10, 11}
   };
   private static final int[] MEMBRANE_EDGE = new int[]{0, 1, 2, 3, 4, 12, 6, 13, 8, 14, 10, 11};
   private static final Wings.WingBone[] BONES = new Wings.WingBone[]{
      new Wings.WingBone(0, 1, 0.052F, 0.044F),
      new Wings.WingBone(1, 2, 0.044F, 0.038F),
      new Wings.WingBone(2, 3, 0.032F, 0.025F),
      new Wings.WingBone(3, 4, 0.025F, 0.012F),
      new Wings.WingBone(2, 5, 0.03F, 0.023F),
      new Wings.WingBone(5, 6, 0.023F, 0.011F),
      new Wings.WingBone(2, 7, 0.028F, 0.021F),
      new Wings.WingBone(7, 8, 0.021F, 0.01F),
      new Wings.WingBone(2, 9, 0.027F, 0.02F),
      new Wings.WingBone(9, 10, 0.02F, 0.01F),
      new Wings.WingBone(0, 11, 0.03F, 0.012F)
   };
   private static final Wings.WingJoint[] JOINTS = new Wings.WingJoint[]{
      new Wings.WingJoint(0, 0.06F),
      new Wings.WingJoint(1, 0.055F),
      new Wings.WingJoint(2, 0.05F),
      new Wings.WingJoint(3, 0.032F),
      new Wings.WingJoint(5, 0.03F),
      new Wings.WingJoint(7, 0.028F),
      new Wings.WingJoint(9, 0.027F)
   };
   private static final float MEMBRANE_HALF_THICKNESS = 0.012F;
   private final ModeSetting hgnEkS9 = new ModeSetting(
      "Р РµР¶РёРј",
      "Vanilla",
      "Vanilla",
      "Animated Fill",
      "Wave"
   );
   private final BooleanSetting lGzcxfO = new BooleanSetting(
      "Р¦РІРµС‚ РѕС‚ С‚РµРјС‹", true
   );
   private final ColorSetting pHPh = new ColorSetting(
         "РЎРІРѕР№ С†РІРµС‚", -9021441
      )
      .setVisible(() -> !this.lGzcxfO.getValue());
   private final SliderSetting dO3E3QT = new SliderSetting(
      "РџСЂРѕР·СЂР°С‡РЅРѕСЃС‚СЊ", 0.72, 0.1, 1.0, 0.05
   );
   private final SliderSetting w0DqFOv = new SliderSetting(
         "РЎРєРѕСЂРѕСЃС‚СЊ", 1.0, 0.1, 3.0, 0.05
      )
      .setVisible(() -> !this.hgnEkS9.is("Vanilla"));
   private final SliderSetting uD35v = new SliderSetting(
         "РњР°СЃС€С‚Р°Р± СѓР·РѕСЂР°", 1.35, 0.5, 3.0, 0.05
      )
      .setVisible(() -> this.hgnEkS9.is("Animated Fill"));
   private final BooleanSetting y9mf4mY = new BooleanSetting("РќР° СЃРµР±СЏ", true);
   private final BooleanSetting uOuO = new BooleanSetting(
      "РќР° РёРіСЂРѕРєРѕРІ", false
   );
   private final SliderSetting xfL8bSv = new SliderSetting(
      "Р Р°Р·РјРµСЂ", 1.0, 0.75, 1.35, 0.05
   );
   private float roN9hv1;
   private boolean zos3Ak;

   @Subscribe
   public void onWorldRender(EventWorldRender event) {
      this.renderWingsLate(event.getMatrixStack(), event.getTickDelta());
   }

   public void renderWingsLate(MatrixStack stack, float tickDelta) {
      List<AbstractClientPlayerEntity> players = this.mc.world == null ? List.of() : this.mc.world.getPlayers();
      this.renderWings(stack, this.mc.gameRenderer.getCamera(), tickDelta, players);
   }

   public void renderWings(MatrixStack stack, Camera camera, float tickDelta, Iterable<? extends Entity> players) {
      if (this.isEnabled() && this.mc.player != null && this.mc.world != null) {
         Vec3d cameraPos = camera.getPos();
         Wings.GlState state = Wings.GlState.capture();
         stack.push();

         try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            if (this.y9mf4mY.getValue() && !this.mc.options.getPerspective().isFirstPerson() && this.mc.player.isAlive()) {
               this.yj7d7N(stack, this.mc.player, tickDelta, cameraPos);
            }

            if (this.uOuO.getValue()) {
               for (Entity entity : players) {
                  if (entity instanceof PlayerEntity player && player != this.mc.player && player.isAlive()) {
                     this.yj7d7N(stack, player, tickDelta, cameraPos);
                  }
               }
            }
         } finally {
            stack.pop();
            state.restore();
         }
      }
   }

   private void yj7d7N(MatrixStack stack, PlayerEntity player, float tickDelta, Vec3d camera) {
      if (!player.isGliding()) {
         Wings.WingPose pose = this.ghLxg(player, tickDelta);
         if (pose != null) {
            double x = MathHelper.lerp((double)tickDelta, player.prevX, player.getX()) - camera.x;
            double y = MathHelper.lerp((double)tickDelta, player.prevY, player.getY()) - camera.y;
            double z = MathHelper.lerp((double)tickDelta, player.prevZ, player.getZ()) - camera.z;
            float bodyYaw = this.resolveBodyYaw(player, tickDelta);
            float move = MathHelper.clamp(player.limbAnimator.getSpeed(tickDelta), 0.0F, 1.0F);
            float flap = (float)Math.sin((player.age + tickDelta) * pose.flapSpeed()) * pose.flapAmplitude();
            float open = (pose.baseSpread() + flap + move * pose.motionSpreadBoost()) * pose.openMultiplier();
            float wingScale = this.xfL8bSv.getFloatValue() * pose.scaleMultiplier();
            int sourceColor = player != this.mc.player && FriendRepository.isFriend(player.getNameForScoreboard())
               ? -16711936
               : (this.lGzcxfO.getValue() ? ColorProvider.getColorClient() : this.pHPh.getValue());
            int baseColor = pcm1zw(sourceColor, Math.round(this.dO3E3QT.getFloatValue() * 255.0F));
            int coreColor = pcm1zw(ColorProvider.interpolateColor(baseColor, -1, 0.42F), Math.round(this.dO3E3QT.getFloatValue() * 255.0F));
            int glowColor = pcm1zw(baseColor, Math.round(this.dO3E3QT.getFloatValue() * 68.0F));
            stack.push();
            stack.translate(x, y, z);
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
            stack.translate(0.0F, pose.preTranslateY(), pose.preTranslateZ());
            if (pose.pitchRotation() != 0.0F) {
               stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pose.pitchRotation()));
            }

            stack.translate(0.0F, pose.anchorY(), pose.anchorZ());
            stack.scale(wingScale, wingScale, wingScale);
            this.jfVx(stack, -1.0F, open, baseColor, coreColor, glowColor, player, tickDelta, pose);
            this.jfVx(stack, 1.0F, open, baseColor, coreColor, glowColor, player, tickDelta, pose);
            stack.pop();
         }
      }
   }

   private void jfVx(
      MatrixStack stack, float side, float open, int baseColor, int coreColor, int glowColor, PlayerEntity player, float tickDelta, Wings.WingPose pose
   ) {
      stack.push();
      stack.translate(side * pose.sideOffset(), pose.sideYOffset(), pose.sideZOffset());
      stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * open));
      stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(side * pose.sideRoll()));
      stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pose.sidePitch()));
      boolean textured = !this.hgnEkS9.is("Vanilla");
      this.f9e9y(player, tickDelta);
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      this.iozeh(stack, side, 1.055F, glowColor, glowColor, textured);
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
      this.iozeh(stack, side, 1.0F, baseColor, coreColor, textured);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      this.vxPLE(stack, side, pcm1zw(baseColor, Math.round(this.dO3E3QT.getFloatValue() * 42.0F)), 1.65F);
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
      this.vxPLE(stack, side, pcm1zw(coreColor, Math.round(this.dO3E3QT.getFloatValue() * 220.0F)), 1.0F);
      stack.pop();
   }

   private void f9e9y(PlayerEntity player, float tickDelta) {
      float speed = this.w0DqFOv.getFloatValue();
      float time = (player.age + tickDelta) * 0.05F * speed;
      if (this.hgnEkS9.is("Animated Fill")) {
         Chams.bindAnimatedFill(time, speed, this.uD35v.getFloatValue(), 1.15F);
      } else if (this.hgnEkS9.is("Wave")) {
         Chams.bindWave(time);
      } else {
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      }
   }

   private void iozeh(MatrixStack stack, float side, float scale, int membraneColor, int coreColor, boolean textured) {
      Matrix4f matrix = stack.peek().getPositionMatrix();
      BufferBuilder buffer = Tessellator.getInstance()
         .begin(DrawMode.TRIANGLES, textured ? VertexFormats.POSITION_TEXTURE_COLOR : VertexFormats.POSITION_COLOR);

      for (int panelIndex = 0; panelIndex < PANELS.length; panelIndex++) {
         int[] panel = PANELS[panelIndex];
         float shade = (panelIndex & 1) == 0 ? 1.0F : 0.84F;
         this.q0z7(buffer, matrix, side, scale, panel, membraneColor, coreColor, shade, 0.012F, textured, false);
         this.q0z7(buffer, matrix, side, scale, panel, membraneColor, coreColor, shade * 0.76F, -0.012F, textured, true);
      }

      int edgeColor = ka3ng8(membraneColor, 0.62F);

      for (int i = 0; i < MEMBRANE_EDGE.length; i++) {
         Wings.WingPoint a = POINTS[MEMBRANE_EDGE[i]];
         Wings.WingPoint b = POINTS[MEMBRANE_EDGE[(i + 1) % MEMBRANE_EDGE.length]];
         this.rEmcw2(buffer, matrix, side, scale, a, b, edgeColor, textured);
      }

      BufferRenderer.drawWithGlobalProgram(buffer.end());
   }

   private void q0z7(
      BufferBuilder buffer,
      Matrix4f matrix,
      float side,
      float scale,
      int[] panel,
      int membraneColor,
      int coreColor,
      float shade,
      float zOffset,
      boolean textured,
      boolean reverse
   ) {
      for (int i = 0; i < 3; i++) {
         int pointIndex = panel[reverse ? 2 - i : i];
         Wings.WingPoint point = POINTS[pointIndex];
         int source = e4WOg6(pointIndex) ? coreColor : membraneColor;
         int shaded = ka3ng8(source, shade);
         shaded = pcm1zw(shaded, Math.round(cYdni(shaded) * point.alphaMul()));
         this.bhRf(
            buffer, matrix, side * point.x() * scale, point.y() * scale, (point.z() + zOffset) * scale, point.u(), point.v(), shaded, textured
         );
      }
   }

   private void rEmcw2(BufferBuilder buffer, Matrix4f matrix, float side, float scale, Wings.WingPoint a, Wings.WingPoint b, int color, boolean textured) {
      int edgeAlpha = Math.round(cYdni(color) * Math.min(a.alphaMul(), b.alphaMul()));
      int shaded = pcm1zw(color, edgeAlpha);
      float ax = side * a.x() * scale;
      float ay = a.y() * scale;
      float azFront = (a.z() + 0.012F) * scale;
      float azBack = (a.z() - 0.012F) * scale;
      float bx = side * b.x() * scale;
      float by = b.y() * scale;
      float bzFront = (b.z() + 0.012F) * scale;
      float bzBack = (b.z() - 0.012F) * scale;
      this.bhRf(buffer, matrix, ax, ay, azFront, a.u(), a.v(), shaded, textured);
      this.bhRf(buffer, matrix, bx, by, bzFront, b.u(), b.v(), shaded, textured);
      this.bhRf(buffer, matrix, bx, by, bzBack, b.u(), b.v(), shaded, textured);
      this.bhRf(buffer, matrix, ax, ay, azFront, a.u(), a.v(), shaded, textured);
      this.bhRf(buffer, matrix, bx, by, bzBack, b.u(), b.v(), shaded, textured);
      this.bhRf(buffer, matrix, ax, ay, azBack, a.u(), a.v(), shaded, textured);
   }

   private void vxPLE(MatrixStack stack, float side, int color, float radiusScale) {
      Matrix4f matrix = stack.peek().getPositionMatrix();
      BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

      for (Wings.WingBone bone : BONES) {
         this.v3lb7b(buffer, matrix, side, bone, color, radiusScale);
      }

      for (Wings.WingJoint joint : JOINTS) {
         this.j8by(buffer, matrix, side, joint, color, radiusScale);
      }

      BufferRenderer.drawWithGlobalProgram(buffer.end());
   }

   private void v3lb7b(BufferBuilder buffer, Matrix4f matrix, float side, Wings.WingBone bone, int color, float radiusScale) {
      Wings.WingPoint a = POINTS[bone.from()];
      Wings.WingPoint b = POINTS[bone.to()];
      float ax = side * a.x();
      float ay = a.y();
      float az = a.z();
      float bx = side * b.x();
      float by = b.y();
      float bz = b.z();
      float dx = bx - ax;
      float dy = by - ay;
      float dz = bz - az;
      float length = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
      if (!(length < 1.0E-4F)) {
         dx /= length;
         dy /= length;
         dz /= length;
         float uy = -dx;
         float uz = 0.0F;
         float uLength = (float)Math.sqrt(dy * dy + uy * uy);
         float ux;
         if (uLength < 1.0E-4F) {
            ux = 1.0F;
            uy = 0.0F;
            uz = 0.0F;
         } else {
            ux = dy / uLength;
            uy /= uLength;
         }

         float vx = dy * uz - dz * uy;
         float vy = dz * ux - dx * uz;
         float vz = dx * uy - dy * ux;
         int sides = 8;

         for (int i = 0; i < 8; i++) {
            float angle0 = (float)((Math.PI * 2) * i / 8.0);
            float angle1 = (float)((Math.PI * 2) * (i + 1) / 8.0);
            float c0 = (float)Math.cos(angle0);
            float s0 = (float)Math.sin(angle0);
            float c1 = (float)Math.cos(angle1);
            float s1 = (float)Math.sin(angle1);
            float ar = bone.fromRadius() * radiusScale;
            float br = bone.toRadius() * radiusScale;
            float a0x = ax + (ux * c0 + vx * s0) * ar;
            float a0y = ay + (uy * c0 + vy * s0) * ar;
            float a0z = az + (uz * c0 + vz * s0) * ar;
            float a1x = ax + (ux * c1 + vx * s1) * ar;
            float a1y = ay + (uy * c1 + vy * s1) * ar;
            float a1z = az + (uz * c1 + vz * s1) * ar;
            float b0x = bx + (ux * c0 + vx * s0) * br;
            float b0y = by + (uy * c0 + vy * s0) * br;
            float b0z = bz + (uz * c0 + vz * s0) * br;
            float b1x = bx + (ux * c1 + vx * s1) * br;
            float b1y = by + (uy * c1 + vy * s1) * br;
            float b1z = bz + (uz * c1 + vz * s1) * br;
            int faceColor = ka3ng8(color, 0.72F + 0.28F * Math.max(0.0F, c0));
            this.eEn3u(buffer, matrix, a0x, a0y, a0z, faceColor);
            this.eEn3u(buffer, matrix, b0x, b0y, b0z, faceColor);
            this.eEn3u(buffer, matrix, b1x, b1y, b1z, faceColor);
            this.eEn3u(buffer, matrix, a0x, a0y, a0z, faceColor);
            this.eEn3u(buffer, matrix, b1x, b1y, b1z, faceColor);
            this.eEn3u(buffer, matrix, a1x, a1y, a1z, faceColor);
            int capColor = ka3ng8(color, 0.68F);
            this.eEn3u(buffer, matrix, ax, ay, az, capColor);
            this.eEn3u(buffer, matrix, a1x, a1y, a1z, capColor);
            this.eEn3u(buffer, matrix, a0x, a0y, a0z, capColor);
            this.eEn3u(buffer, matrix, bx, by, bz, capColor);
            this.eEn3u(buffer, matrix, b0x, b0y, b0z, capColor);
            this.eEn3u(buffer, matrix, b1x, b1y, b1z, capColor);
         }
      }
   }

   private void j8by(BufferBuilder buffer, Matrix4f matrix, float side, Wings.WingJoint joint, int color, float radiusScale) {
      Wings.WingPoint point = POINTS[joint.point()];
      float x = side * point.x();
      float y = point.y();
      float z = point.z();
      float r = joint.radius() * radiusScale;
      float[][] vertices = new float[][]{{x + r, y, z}, {x - r, y, z}, {x, y + r, z}, {x, y - r, z}, {x, y, z + r}, {x, y, z - r}};
      int[][] faces = new int[][]{{2, 0, 4}, {2, 4, 1}, {2, 1, 5}, {2, 5, 0}, {3, 4, 0}, {3, 1, 4}, {3, 5, 1}, {3, 0, 5}};

      for (int i = 0; i < faces.length; i++) {
         int shaded = ka3ng8(color, i < 4 ? 1.0F : 0.72F);

         for (int index : faces[i]) {
            float[] coords = vertices[index];
            this.eEn3u(buffer, matrix, coords[0], coords[1], coords[2], shaded);
         }
      }
   }

   private static boolean e4WOg6(int pointIndex) {
      return pointIndex == 0 || pointIndex == 1 || pointIndex == 2;
   }

   private void bhRf(BufferBuilder buffer, Matrix4f matrix, float x, float y, float z, float u, float v, int color, boolean textured) {
      if (textured) {
         buffer.vertex(matrix, x, y, z).texture(u, v).color(mf7Yx2h(color), gtMn(color), fbkliT(color), cYdni(color) / 255.0F);
      } else {
         buffer.vertex(matrix, x, y, z).color(mf7Yx2h(color), gtMn(color), fbkliT(color), cYdni(color) / 255.0F);
      }
   }

   private void eEn3u(BufferBuilder buffer, Matrix4f matrix, float x, float y, float z, int color) {
      buffer.vertex(matrix, x, y, z).color(mf7Yx2h(color), gtMn(color), fbkliT(color), cYdni(color) / 255.0F);
   }

   private float resolveBodyYaw(PlayerEntity player, float tickDelta) {
      float target = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
      if (player != this.mc.player) {
         return target;
      } else if (this.zos3Ak && player.age >= 2) {
         float delta = MathHelper.clamp(MathHelper.wrapDegrees(target - this.roN9hv1), -14.0F, 14.0F);
         this.roN9hv1 += delta;
         return this.roN9hv1;
      } else {
         this.roN9hv1 = target;
         this.zos3Ak = true;
         return this.roN9hv1;
      }
   }

   private Wings.WingPose ghLxg(PlayerEntity player, float tickDelta) {
      float pitch = MathHelper.lerp(tickDelta, player.prevPitch, player.getPitch());
      if (player.isGliding()) {
         float flightTicks = player.getGlidingTicks() + tickDelta;
         float flightProgress = MathHelper.clamp(flightTicks * flightTicks / 100.0F, 0.0F, 1.0F);
         return new Wings.WingPose(
            0.33F, 0.43F, 0.78F, 0.16F, flightProgress * (-90.0F - pitch), 27.0F, 1.18F, 0.94F, 2.5F, 3.2F, 0.055F, 0.0F, 0.08F, -4.0F, -8.0F, 0.18F
         );
      } else if (player.isTouchingWater()) {
         return null;
      } else {
         return player.isSneaking()
            ? new Wings.WingPose(0.0F, 0.0F, 0.96F, 0.2F, 18.0F, 20.0F, 0.92F, 0.98F, 3.0F, 5.5F, 0.06F, 0.0F, 0.03F, -10.0F, -5.0F, 0.13F)
            : new Wings.WingPose(0.0F, 0.0F, 1.36F, 0.19F, 0.0F, 22.0F, 1.0F, 1.0F, 3.5F, 6.5F, 0.06F, 0.0F, 0.03F, -12.0F, -5.0F, 0.13F);
      }
   }

   private static int pcm1zw(int color, int alpha) {
      return MathHelper.clamp(alpha, 0, 255) << 24 | color & 16777215;
   }

   private static int ka3ng8(int color, float factor) {
      int r = MathHelper.clamp(Math.round((color >> 16 & 0xFF) * factor), 0, 255);
      int g = MathHelper.clamp(Math.round((color >> 8 & 0xFF) * factor), 0, 255);
      int b = MathHelper.clamp(Math.round((color & 0xFF) * factor), 0, 255);
      return color & 0xFF000000 | r << 16 | g << 8 | b;
   }

   private static int cYdni(int color) {
      return color >>> 24 & 0xFF;
   }

   private static float mf7Yx2h(int color) {
      return (color >>> 16 & 0xFF) / 255.0F;
   }

   private static float gtMn(int color) {
      return (color >>> 8 & 0xFF) / 255.0F;
   }

   private static float fbkliT(int color) {
      return (color & 0xFF) / 255.0F;
   }

   @Override
   public void onDisable() {
      this.zos3Ak = false;
      super.onDisable();
   }

   private static final class GlState {
      private final boolean tFc5tC;
      private final boolean g36ALv;
      private final boolean sdbt;
      private final boolean vofq;
      private final int j2txJ;
      private final int o5jQg;
      private final int gE04;
      private final int hty3;
      private final float e9Zz;
      private final float[] b1Ir;
      private final ShaderProgram gwxXn3;

      private GlState(
         boolean blend,
         boolean cull,
         boolean depthTest,
         boolean depthMask,
         int srcRgb,
         int dstRgb,
         int srcAlpha,
         int dstAlpha,
         float lineWidth,
         float[] shaderColor,
         ShaderProgram shader
      ) {
         this.tFc5tC = blend;
         this.g36ALv = cull;
         this.sdbt = depthTest;
         this.vofq = depthMask;
         this.j2txJ = srcRgb;
         this.o5jQg = dstRgb;
         this.gE04 = srcAlpha;
         this.hty3 = dstAlpha;
         this.e9Zz = lineWidth;
         this.b1Ir = shaderColor;
         this.gwxXn3 = shader;
      }

      static Wings.GlState capture() {
         return new Wings.GlState(
            GL11.glIsEnabled(3042),
            GL11.glIsEnabled(2884),
            GL11.glIsEnabled(2929),
            GL11.glGetBoolean(2930),
            GL11.glGetInteger(32969),
            GL11.glGetInteger(32968),
            GL11.glGetInteger(32971),
            GL11.glGetInteger(32970),
            RenderSystem.getShaderLineWidth(),
            (float[])RenderSystem.getShaderColor().clone(),
            RenderSystem.getShader()
         );
      }

      void restore() {
         RenderSystem.depthMask(this.vofq);
         if (this.sdbt) {
            RenderSystem.enableDepthTest();
         } else {
            RenderSystem.disableDepthTest();
         }

         if (this.g36ALv) {
            RenderSystem.enableCull();
         } else {
            RenderSystem.disableCull();
         }

         RenderSystem.blendFuncSeparate(this.j2txJ, this.o5jQg, this.gE04, this.hty3);
         if (this.tFc5tC) {
            RenderSystem.enableBlend();
         } else {
            RenderSystem.disableBlend();
         }

         RenderSystem.lineWidth(this.e9Zz);
         RenderSystem.setShaderColor(this.b1Ir[0], this.b1Ir[1], this.b1Ir[2], this.b1Ir[3]);
         if (this.gwxXn3 != null) {
            RenderSystem.setShader(this.gwxXn3);
         } else {
            RenderSystem.clearShader();
         }
      }
   }

   private record WingBone(int from, int to, float fromRadius, float toRadius) {


      

      

      

      

      
   }

   private record WingJoint(int point, float radius) {

      

      

      
   }

   private record WingPoint(float x, float y, float z, float u, float v, float alphaMul) {



      

      

      

      

      

      

      
   }

   private record WingPose(
      float preTranslateY,
      float preTranslateZ,
      float anchorY,
      float anchorZ,
      float pitchRotation,
      float baseSpread,
      float openMultiplier,
      float scaleMultiplier,
      float motionSpreadBoost,
      float flapAmplitude,
      float sideOffset,
      float sideYOffset,
      float sideZOffset,
      float sideRoll,
      float sidePitch,
      float flapSpeed
   ) {








      

      

      

      

      

      

      

      

      

      

      

      

      

      

      

      

      
   }
}
