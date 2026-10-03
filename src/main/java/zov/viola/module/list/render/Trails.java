package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.render.effects.MasEffectRenderer;
import zov.viola.util.render.providers.ColorProvider;

@ModuleInformation(
   moduleName = "Trails",
   moduleDesc = "Оставляет след за сущностями",
   moduleCategory = ModuleCategory.RENDER
)
public class Trails extends Module {
   private static final int PRETTY_HARD_CAP = 1000;
   private final ModeListSetting ei19fF = new ModeListSetting(
      "Режим",
      new BooleanSetting("Entity Trails", true),
      new BooleanSetting("Pearl Trail", false),
      new BooleanSetting("Красивый", false)
   );
   private final SliderSetting m49ps = new SliderSetting(
      "Длительность", 1000.0, 500.0, 2000.0, 100.0
   );
   private final SliderSetting p9Zv = new SliderSetting(
      "Толщина", 2.0, 0.5, 5.0, 0.1F
   );
   private final SliderSetting wN7Vj = new SliderSetting(
      "Непрозрачность", 100.0, 0.0, 255.0, 5.0
   );
   private final BooleanSetting kPj1aj = new BooleanSetting(
      "От первого лица", false
   );
   private final BooleanSetting gf4d = new BooleanSetting("Себе", true);
   private final BooleanSetting diOrj9R = new BooleanSetting(
      "На друзьях", true
   );
   private final BooleanSetting dAF7 = new BooleanSetting(
      "На игроках", true
   );
   private final BooleanSetting nHxsD9r = new BooleanSetting(
      "На мобах", false
   );
   private final SliderSetting xItn = new SliderSetting(
         "Кол-во палочек", 2.0, 1.0, 10.0, 1.0
      )
      .setVisible(() -> this.ei19fF.isEnabled("Красивый"));
   private final SliderSetting a9PgU = new SliderSetting(
         "Длительность (мс)",
         1800.0,
         500.0,
         5000.0,
         100.0
      )
      .setVisible(() -> this.ei19fF.isEnabled("Красивый"));
   private final SliderSetting znqqFC = new SliderSetting(
         "Сближенность", 0.4F, 0.1F, 1.5, 0.1F
      )
      .setVisible(() -> this.ei19fF.isEnabled("Красивый"));
   private final SliderSetting aws4 = new SliderSetting(
         "Длина палочки", 0.3F, 0.1F, 1.0, 0.05F
      )
      .setVisible(() -> this.ei19fF.isEnabled("Красивый"));
   private final BooleanSetting yVuK2t = new BooleanSetting(
         "Свечение палочек", true
      )
      .setVisible(() -> this.ei19fF.isEnabled("Красивый"));
   private final BooleanSetting aic7vnq = new BooleanSetting(
         "Радужный цвет", false
      )
      .setVisible(() -> this.ei19fF.isEnabled("Красивый"));
   private final BooleanSetting vwSecfq = new BooleanSetting(
      "Цвет от темы", true
   );
   private final ColorSetting oizr = new ColorSetting("Свой цвет", -9021441)
      .setVisible(() -> !this.vwSecfq.getValue());
   private final Map<Integer, Deque<Trails.Point>> jd1uwcL = new HashMap<>();
   private final Map<Integer, Vec3d> qpxQf8 = new HashMap<>();
   private final Map<Integer, Long> fa5Y = new HashMap<>();
   private final Map<Integer, Trails.EntityType> kjjH = new HashMap<>();
   private long les0a4u = 0L;
   private final ArrayDeque<Trails.PrettyFig> gK3h = new ArrayDeque<>();
   private Vec3d i240q6p = null;
   private final Random hGtzJ = new Random();

   public Trails() {
      WorldRenderEvents.LAST.register(this::jvFDDU);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.cy0D54c();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.cy0D54c();
   }

   private void cy0D54c() {
      this.jd1uwcL.clear();
      this.qpxQf8.clear();
      this.fa5Y.clear();
      this.kjjH.clear();
      this.gK3h.clear();
      this.i240q6p = null;
      this.les0a4u = 0L;
   }

   @Subscribe
   private void onTick(EventTick e) {
      if (this.isEnabled() && this.mc.player != null && this.mc.world != null) {
         if (this.ei19fF.isEnabled("Красивый")) {
            this.qTocprJ();
         } else if (!this.gK3h.isEmpty()) {
            this.gK3h.clear();
         }
      }
   }

   private void qTocprJ() {
      long now = System.currentTimeMillis();
      Iterator<Trails.PrettyFig> it = this.gK3h.iterator();

      while (it.hasNext()) {
         Trails.PrettyFig f = it.next();
         f.tick();
         if (now - f.spawnTime > f.lifeMs) {
            it.remove();
         }
      }

      boolean shouldShow = this.kPj1aj.getValue() || !this.mc.options.getPerspective().isFirstPerson();
      if (shouldShow) {
         Vec3d pos = this.mc.player.getPos();
         Vec3d vel = this.mc.player.getVelocity();
         double horizontalSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
         boolean isRunning = horizontalSpeed > 0.01;
         if (isRunning) {
            int spawnPerTick = (int)this.xItn.getValue();
            int canSpawn = Math.min(spawnPerTick, 1000 - this.gK3h.size());

            for (int i = 0; i < canSpawn; i++) {
               this.w8aXFh(pos);
            }
         }

         while (this.gK3h.size() > 1000) {
            this.gK3h.pollFirst();
         }
      }
   }

   private void w8aXFh(Vec3d base) {
      float yaw = this.mc.player.getYaw();
      double yawRad = Math.toRadians(yaw);
      double backwardX = Math.sin(yawRad);
      double backwardZ = -Math.cos(yawRad);
      float spreadMultiplier = this.znqqFC.getFloatValue();
      double backDist = (0.3 + this.hGtzJ.nextDouble() * 0.5) * spreadMultiplier;
      double sideOffset = (this.hGtzJ.nextDouble() - 0.5) * 0.4 * spreadMultiplier;
      double sideX = -Math.cos(yawRad) * sideOffset;
      double sideZ = -Math.sin(yawRad) * sideOffset;
      double ox = backwardX * backDist + sideX;
      double oz = backwardZ * backDist + sideZ;
      double oy = this.mc.player.getHeight() * (0.5 + (this.hGtzJ.nextDouble() - 0.5) * 0.2 * spreadMultiplier);
      Vec3d p = base.add(ox, oy, oz);
      Vec3d vel = new Vec3d((this.hGtzJ.nextDouble() - 0.5) * 0.02, 0.01 + this.hGtzJ.nextDouble() * 0.02, (this.hGtzJ.nextDouble() - 0.5) * 0.02);
      long life = (long)this.a9PgU.getValue();
      float rot = this.hGtzJ.nextFloat() * 360.0F;
      float rotSpeed = (this.hGtzJ.nextFloat() - 0.5F) * 6.0F;
      int color = this.aic7vnq.getValue()
         ? 0xFF000000 | Color.HSBtoRGB((float)(System.currentTimeMillis() % 3600L) / 3600.0F + this.hGtzJ.nextFloat() * 0.1F, 0.7F, 1.0F) & 16777215
         : (this.vwSecfq.getValue() ? ColorProvider.getColorClient() : this.oizr.getValue());
      this.gK3h.addLast(new Trails.PrettyFig(p, vel, life, rot, rotSpeed, color));
   }

   private void jvFDDU(WorldRenderContext context) {
      if (this.isEnabled()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.player != null && mc.world != null && mc.player.isAlive()) {
            float tickDelta = context.tickCounter().getTickDelta(true);
            boolean selfVisible = this.kPj1aj.getValue() || !mc.options.getPerspective().isFirstPerson();
            long currentTime = System.currentTimeMillis();
            boolean entityEnabled = this.ei19fF
               .isEnabled("Entity Trails");
            boolean pearlEnabled = this.ei19fF.isEnabled("Pearl Trail");
            boolean prettyEnabled = this.ei19fF.isEnabled("Красивый");
            if (pearlEnabled) {
               this.fUfthS(context, tickDelta, currentTime);
            }

            if (prettyEnabled) {
               this.mddN(context, tickDelta);
            }

            if (!entityEnabled) {
               if (!this.jd1uwcL.isEmpty()) {
                  this.jd1uwcL.clear();
                  this.qpxQf8.clear();
                  this.fa5Y.clear();
                  this.kjjH.clear();
               }
            } else {
               boolean scanNow = currentTime - this.les0a4u >= 15L;
               if (scanNow) {
                  this.les0a4u = currentTime;

                  for (Entity entity : mc.world.getEntities()) {
                     if (entity != null && entity.isAlive() && entity instanceof LivingEntity) {
                        boolean isTarget = false;
                        Trails.EntityType entityType = null;
                        if (entity == mc.player) {
                           isTarget = this.gf4d.getValue() && selfVisible;
                           entityType = Trails.EntityType.SELF;
                        } else if (entity instanceof PlayerEntity player) {
                           boolean isFriend = FriendRepository.isFriend(player.getNameForScoreboard());
                           if (isFriend) {
                              isTarget = this.diOrj9R.getValue();
                              entityType = Trails.EntityType.FRIEND;
                           } else {
                              isTarget = this.dAF7.getValue();
                              entityType = Trails.EntityType.PLAYER;
                           }
                        } else if (entity instanceof MobEntity) {
                           isTarget = this.nHxsD9r.getValue();
                           entityType = Trails.EntityType.MOB;
                        }

                        if (!isTarget) {
                           this.ex2q8ez(entity.getId());
                        } else {
                           this.kjjH.put(entity.getId(), entityType);
                           Vec3d currentPos = entity.getLerpedPos(tickDelta);
                           long lastTime = this.fa5Y.getOrDefault(entity.getId(), 0L);
                           boolean shouldAddPoint = currentTime - lastTime >= 15L;
                           if (shouldAddPoint) {
                              Vec3d lastPos = this.qpxQf8.get(entity.getId());
                              if (lastPos != null) {
                                 double distanceMoved = lastPos.distanceTo(currentPos);
                                 Deque<Trails.Point> existing = this.jd1uwcL.get(entity.getId());
                                 shouldAddPoint = distanceMoved > 0.01 || existing == null || existing.isEmpty();
                              }

                              if (shouldAddPoint) {
                                 Deque<Trails.Point> points = this.jd1uwcL.computeIfAbsent(entity.getId(), k -> new ArrayDeque<>());
                                 points.addFirst(new Trails.Point(currentPos, currentTime));
                                 this.fa5Y.put(entity.getId(), currentTime);
                                 this.qpxQf8.put(entity.getId(), currentPos);
                              }
                           }
                        }
                     }
                  }
               }

               Iterator<Entry<Integer, Deque<Trails.Point>>> iterator = this.jd1uwcL.entrySet().iterator();

               while (iterator.hasNext()) {
                  Entry<Integer, Deque<Trails.Point>> entry = iterator.next();
                  Entity entityx = mc.world.getEntityById(entry.getKey());
                  if (entityx != null && entityx.isAlive()) {
                     Deque<Trails.Point> points = entry.getValue();
                     points.removeIf(point -> (float)(currentTime - point.timestamp) > (float)this.m49ps.getValue());
                     if (points.isEmpty()) {
                        iterator.remove();
                        this.ex2q8ez(entry.getKey());
                     }
                  } else {
                     iterator.remove();
                     this.ex2q8ez(entry.getKey());
                  }
               }

               for (Entry<Integer, Deque<Trails.Point>> entry : this.jd1uwcL.entrySet()) {
                  Deque<Trails.Point> points = entry.getValue();
                  if (points.size() >= 2) {
                     Entity entityx = mc.world.getEntityById(entry.getKey());
                     if (entityx != null && entityx.isAlive()) {
                        Trails.EntityType entityTypex = this.kjjH.get(entry.getKey());
                        this.usH7(points, context, currentTime, entityx.getHeight(), entityTypex);
                     }
                  }
               }
            }
         } else {
            if (!this.jd1uwcL.isEmpty()) {
               this.cy0D54c();
            }
         }
      }
   }

   private void mddN(WorldRenderContext context, float tickDelta) {
      if (!this.gK3h.isEmpty()) {
         Camera camera = context.camera();
         Vec3d camPos = camera.getPos();
         MatrixStack stack = context.matrixStack();
         boolean glow = this.yVuK2t.getValue();
         float lineLength = this.aws4.getFloatValue();
         float lineWidth = 0.05F;
         long now = System.currentTimeMillis();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShaderTexture(0, Identifier.of("mre", "images/glow.png"));
         BufferBuilder buffer = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         boolean hasAnyQuads = false;

         for (Trails.PrettyFig f : this.gK3h) {
            float life = (float)(now - f.spawnTime) / (float)f.lifeMs;
            if (!(life >= 1.0F)) {
               float alpha = life < 0.2F ? life * 5.0F : 1.0F - (life - 0.2F) / 0.8F;
               if (!(alpha <= 0.01F)) {
                  double px = MathHelper.lerp((double)tickDelta, f.prevX, f.x) - camPos.x;
                  double py = MathHelper.lerp((double)tickDelta, f.prevY, f.y) - camPos.y;
                  double pz = MathHelper.lerp((double)tickDelta, f.prevZ, f.z) - camPos.z;
                  float r = ColorProvider.red(f.color) / 255.0F;
                  float g = ColorProvider.green(f.color) / 255.0F;
                  float b = ColorProvider.blue(f.color) / 255.0F;
                  stack.push();
                  stack.translate(px, py, pz);
                  stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                  stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                  stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f.rotation + f.rotSpeed * tickDelta));
                  Matrix4f m = stack.peek().getPositionMatrix();
                  if (glow) {
                     float glowSize = lineWidth * 4.0F;
                     float glowHeight = lineLength * 1.5F;
                     this.nke3N(buffer, m, glowSize, glowHeight, r, g, b, alpha * 0.3F);
                     hasAnyQuads = true;
                  }

                  this.nke3N(buffer, m, lineWidth, lineLength, r, g, b, alpha);
                  hasAnyQuads = true;
                  stack.pop();
               }
            }
         }

         if (hasAnyQuads) {
            BufferRenderer.drawWithGlobalProgram(buffer.end());
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, 0);
      }
   }

   private void nke3N(BufferBuilder buffer, Matrix4f m, float width, float height, float r, float g, float b, float alpha) {
      int ri = (int)(r * 255.0F);
      int gi = (int)(g * 255.0F);
      int bi = (int)(b * 255.0F);
      int ai = (int)(MathHelper.clamp(alpha, 0.0F, 1.0F) * 255.0F);
      float halfWidth = width / 2.0F;
      float halfHeight = height / 2.0F;
      buffer.vertex(m, -halfWidth, halfHeight, 0.0F).texture(0.0F, 0.0F).color(ri, gi, bi, ai);
      buffer.vertex(m, halfWidth, halfHeight, 0.0F).texture(1.0F, 0.0F).color(ri, gi, bi, ai);
      buffer.vertex(m, halfWidth, -halfHeight, 0.0F).texture(1.0F, 1.0F).color(ri, gi, bi, ai);
      buffer.vertex(m, -halfWidth, -halfHeight, 0.0F).texture(0.0F, 1.0F).color(ri, gi, bi, ai);
   }

   private void fUfthS(WorldRenderContext context, float tickDelta, long now) {
      int color = 0xFF000000 | (this.vwSecfq.getValue() ? ColorProvider.getColorClient() : this.oizr.getValue()) & 16777215;

      for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
         if (entity instanceof EnderPearlEntity pearl && pearl.isAlive()) {
            Vec3d position = pearl.getLerpedPos(tickDelta);
            Vec3d velocity = pearl.getVelocity();

            for (int i = 0; i < 4; i++) {
               Vec3d framePosition = position.subtract(velocity.multiply(i * 0.45));
               long frameAge = (now + i * 120L) % 600L;
               MasEffectRenderer.render(
                  "pearl_trail", context.matrixStack(), context.camera(), framePosition, frameAge, 600L, 0.32F * (1.0F - i * 0.14F), color
               );
            }
         }
      }
   }

   private void usH7(Deque<Trails.Point> points, WorldRenderContext context, long currentTime, float entityHeight, Trails.EntityType entityType) {
      MatrixStack matrixStack = context.matrixStack();
      Vec3d camPos = context.camera().getPos();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.disableCull();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator tessellator = Tessellator.getInstance();
      matrixStack.push();
      matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);
      Matrix4f matrix = matrixStack.peek().getPositionMatrix();
      BufferBuilder buffer = tessellator.begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
      int index = 0;

      for (Trails.Point point : points) {
         float lifeRatio = Math.min(1.0F, (float)((currentTime - point.timestamp) / this.m49ps.getValue()));
         float alpha = (float)(this.wN7Vj.getValue() / 255.0 * (1.0F - this.jniZ(lifeRatio)));
         int color = this.brhUVf(entityType, index);
         float r = (color >> 16 & 0xFF) / 255.0F;
         float g = (color >> 8 & 0xFF) / 255.0F;
         float b = (color & 0xFF) / 255.0F;
         buffer.vertex(matrix, (float)point.pos.x, (float)point.pos.y, (float)point.pos.z).color(r, g, b, alpha);
         buffer.vertex(matrix, (float)point.pos.x, (float)(point.pos.y + entityHeight - 0.1), (float)point.pos.z).color(r, g, b, alpha);
         index++;
      }

      BufferRenderer.drawWithGlobalProgram(buffer.end());
      RenderSystem.lineWidth((float)this.p9Zv.getValue());
      BufferBuilder bufferLineBottom = tessellator.begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
      index = 0;

      for (Trails.Point point : points) {
         float lifeRatio = Math.min(1.0F, (float)((currentTime - point.timestamp) / this.m49ps.getValue()));
         float alpha = (float)(this.wN7Vj.getValue() / 255.0 * (1.0F - this.jniZ(lifeRatio)));
         int color = this.brhUVf(entityType, index);
         float r = (color >> 16 & 0xFF) / 255.0F;
         float g = (color >> 8 & 0xFF) / 255.0F;
         float b = (color & 0xFF) / 255.0F;
         bufferLineBottom.vertex(matrix, (float)point.pos.x, (float)point.pos.y, (float)point.pos.z).color(r, g, b, alpha);
         index++;
      }

      BufferRenderer.drawWithGlobalProgram(bufferLineBottom.end());
      BufferBuilder bufferLineTop = tessellator.begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
      index = 0;

      for (Trails.Point point : points) {
         float lifeRatio = Math.min(1.0F, (float)((currentTime - point.timestamp) / this.m49ps.getValue()));
         float alpha = (float)(this.wN7Vj.getValue() / 255.0 * (1.0F - this.jniZ(lifeRatio)));
         int color = this.brhUVf(entityType, index);
         float r = (color >> 16 & 0xFF) / 255.0F;
         float g = (color >> 8 & 0xFF) / 255.0F;
         float b = (color & 0xFF) / 255.0F;
         bufferLineTop.vertex(matrix, (float)point.pos.x, (float)(point.pos.y + entityHeight - 0.1), (float)point.pos.z).color(r, g, b, alpha);
         index++;
      }

      BufferRenderer.drawWithGlobalProgram(bufferLineTop.end());
      matrixStack.pop();
      RenderSystem.enableCull();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private int brhUVf(Trails.EntityType entityType, int index) {
      int clientColor = this.vwSecfq.getValue() ? ColorProvider.getColorClient() : this.oizr.getValue();
      if (entityType == null) {
         return clientColor;
      } else {
         switch (entityType) {
            case SELF:
            case FRIEND:
            default:
               return clientColor;
            case PLAYER:
               return new Color(255, 85, 85).getRGB();
            case MOB:
               return new Color(255, 170, 0).getRGB();
         }
      }
   }

   private float jniZ(float x) {
      return 1.0F - (1.0F - x) * (1.0F - x);
   }

   private void ex2q8ez(int entityId) {
      this.qpxQf8.remove(entityId);
      this.fa5Y.remove(entityId);
      this.kjjH.remove(entityId);
   }

   private static enum EntityType {
      SELF,
      FRIEND,
      PLAYER,
      MOB;
   }

   public static class Point {
      public final Vec3d pos;
      public final long timestamp;

      public Point(Vec3d pos, long timestamp) {
         this.pos = pos;
         this.timestamp = timestamp;
      }
   }

   private static final class PrettyFig {
      double x;
      double y;
      double z;
      double prevX;
      double prevY;
      double prevZ;
      double vx;
      double vy;
      double vz;
      final long spawnTime = System.currentTimeMillis();
      final long lifeMs;
      float rotation;
      final float rotSpeed;
      final int color;

      PrettyFig(Vec3d pos, Vec3d vel, long lifeMs, float rotation, float rotSpeed, int color) {
         this.x = pos.x;
         this.y = pos.y;
         this.z = pos.z;
         this.prevX = this.x;
         this.prevY = this.y;
         this.prevZ = this.z;
         this.vx = vel.x;
         this.vy = vel.y;
         this.vz = vel.z;
         this.lifeMs = lifeMs;
         this.rotation = rotation;
         this.rotSpeed = rotSpeed;
         this.color = color;
      }

      void tick() {
         this.prevX = this.x;
         this.prevY = this.y;
         this.prevZ = this.z;
         this.x = this.x + this.vx;
         this.y = this.y + this.vy;
         this.z = this.z + this.vz;
         this.vy *= 0.96;
         this.vx *= 0.98;
         this.vz *= 0.98;
         this.rotation = this.rotation + this.rotSpeed;
      }
   }
}
