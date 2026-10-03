package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Last;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.packet.NetworkUtils;

@ModuleInformation(
   moduleName = "Blink",
   moduleDesc = "Задерживает пакеты движения",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class Blink extends Module {
   private final BooleanSetting oC7bd5I = new BooleanSetting(
      "Фейк игрок", true
   );
   private final BooleanSetting xodi = new BooleanSetting("Засада", false);
   private final BooleanSetting dv9bxm = new BooleanSetting(
      "Рендер бокса", true
   );
   private final BooleanSetting osbB6 = new BooleanSetting(
      "Авто сброс", false
   );
   private final SliderSetting bR2n = new SliderSetting(
         "Сброс после", 100.0, 1.0, 1000.0, 1.0
      )
      .setVisible(this.osbB6::getValue);
   private final ModeSetting bKFT = new ModeSetting(
         "Действие", "Blink", "Blink", "Reset"
      )
      .setVisible(this.osbB6::getValue);
   private final BooleanSetting mcv37e9 = new BooleanSetting(
         "Авто выкл", true
      )
      .setVisible(this.osbB6::getValue);
   private final CopyOnWriteArrayList<Packet<?>> bp2Z = new CopyOnWriteArrayList<>();
   private OtherClientPlayerEntity l8g1AZK = null;
   private Box fj17GA = null;
   private boolean oP2p = false;
   private final Last k0fj983 = this::ss7lB3Z;

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.mc.player != null && this.mc.world != null) {
         this.fj17GA = this.mc.player.getBoundingBox();
         this.bp2Z.clear();
         if (this.oC7bd5I.getValue()) {
            this.r5L2x8();
         }

         if (!this.oP2p) {
            WorldRenderEvents.LAST.register(this.k0fj983);
            this.oP2p = true;
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.jpWHBv();
      this.aBRmf();
      this.bp2Z.clear();
      this.fj17GA = null;
   }

   @Subscribe
   public void onPacket(EventPacket e) {
      if (this.mc.player != null && this.mc.world != null) {
         Packet<?> packet = e.getPacket();
         if (e.getType() != EventPacket.Type.RECEIVE || !(packet instanceof PlayerRespawnS2CPacket) && !(packet instanceof GameJoinS2CPacket)) {
            if (e.getType() == EventPacket.Type.SEND) {
               if (packet instanceof ClientStatusC2SPacket status && status.getMode() == Mode.PERFORM_RESPAWN) {
                  this.setEnabled(false);
                  return;
               }

               if (this.xodi.getValue() && packet instanceof PlayerInteractEntityC2SPacket) {
                  this.setEnabled(false);
                  return;
               }

               this.bp2Z.add(packet);
               e.setCancelled(true);
            }
         } else {
            this.setEnabled(false);
         }
      }
   }

   @Subscribe
   public void onTick(EventTick event) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.osbB6.getValue() && this.bp2Z.size() > this.bR2n.getIntValue()) {
            if (this.bKFT.is("Reset")) {
               this.udjF6m();
            } else if (this.bKFT.is("Blink")) {
               this.jpWHBv();
               if (this.l8g1AZK != null) {
                  this.l8g1AZK.copyPositionAndRotation(this.mc.player);
               }

               this.fj17GA = this.mc.player.getBoundingBox();
            }

            if (this.mcv37e9.getValue()) {
               this.setEnabled(false);
            }
         }
      }
   }

   private void ss7lB3Z(WorldRenderContext context) {
      if (this.isEnabled() && this.mc.player != null && this.dv9bxm.getValue() && this.fj17GA != null) {
         Camera camera = context.camera();
         double camX = camera.getPos().x;
         double camY = camera.getPos().y;
         double camZ = camera.getPos().z;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder buffer = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
         Matrix4f matrix = context.matrixStack().peek().getPositionMatrix();
         double minX = this.fj17GA.minX - camX;
         double minY = this.fj17GA.minY - camY;
         double minZ = this.fj17GA.minZ - camZ;
         double maxX = this.fj17GA.maxX - camX;
         double maxY = this.fj17GA.maxY - camY;
         double maxZ = this.fj17GA.maxZ - camZ;
         buffer.vertex(matrix, (float)minX, (float)minY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)minY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)minY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)minY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)minY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)minY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)minY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)minY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)maxY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)maxY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)maxY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)maxY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)maxY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)maxY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)maxY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)maxY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)minY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)maxY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)minY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)maxY, (float)minZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)minY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)maxX, (float)maxY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)minY, (float)maxZ).color(255, 255, 255, 255);
         buffer.vertex(matrix, (float)minX, (float)maxY, (float)maxZ).color(255, 255, 255, 255);
         BufferRenderer.drawWithGlobalProgram(buffer.end());
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
      }
   }

   private void r5L2x8() {
      if (this.mc.player != null && this.mc.world != null) {
         GameProfile profile = new GameProfile(UUID.randomUUID(), this.mc.player.getName().getString());
         this.l8g1AZK = new OtherClientPlayerEntity(this.mc.world, profile);
         this.l8g1AZK.copyFrom(this.mc.player);
         this.l8g1AZK.setPos(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ());
         this.l8g1AZK.setHeadYaw(this.mc.player.getHeadYaw());
         this.mc.world.addEntity(this.l8g1AZK);
      }
   }

   private void aBRmf() {
      if (this.l8g1AZK != null && this.mc.world != null) {
         this.mc.world.removeEntity(this.l8g1AZK.getId(), RemovalReason.DISCARDED);
         this.l8g1AZK = null;
      }
   }

   private void jpWHBv() {
      for (Packet<?> packet : this.bp2Z) {
         NetworkUtils.sendSilentPacket(packet);
      }

      this.bp2Z.clear();
   }

   private void udjF6m() {
      this.bp2Z.clear();
   }
}
