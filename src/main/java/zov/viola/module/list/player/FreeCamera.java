package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.packet.NetworkUtils;
import zov.viola.util.player.move.MoveUtil;

@ModuleInformation(
   moduleName = "Free Camera",
   moduleDesc = "Свободная камера без движения тела",
   moduleCategory = ModuleCategory.PLAYER
)
public class FreeCamera extends Module {
   public final SliderSetting xyi = new SliderSetting(
      "Скорость по Y", 0.5, 0.1, 1.0, 0.1F
   );
   private Vec3d bv7ae;
   private float hGlm;
   private float c77i2xb;
   public OtherClientPlayerEntity fakePlayer;

   @Override
   public void onEnable() {
      if (this.mc.player != null && this.mc.world != null && this.mc.getNetworkHandler() != null) {
         this.bv7ae = this.mc.player.getPos();
         this.hGlm = this.mc.player.getYaw();
         this.c77i2xb = this.mc.player.getPitch();
         GameProfile profile = new GameProfile(UUID.randomUUID(), this.mc.player.getName().getString());
         this.fakePlayer = new OtherClientPlayerEntity(this.mc.world, profile);
         this.fakePlayer.copyFrom(this.mc.player);
         this.fakePlayer.setPos(this.bv7ae.x, this.bv7ae.y, this.bv7ae.z);
         this.fakePlayer.setYaw(this.hGlm);
         this.fakePlayer.setPitch(this.c77i2xb);
         this.mc.world.addEntity(this.fakePlayer);
         this.mc.player.noClip = true;
         this.mc.player.setVelocity(Vec3d.ZERO);
         super.onEnable();
      }
   }

   @Override
   public void onDisable() {
      if (this.mc.player != null && this.mc.world != null) {
         this.mc.player.noClip = false;
         this.mc.player.setPos(this.bv7ae.x, this.bv7ae.y, this.bv7ae.z);
         this.mc.player.setYaw(this.hGlm);
         this.mc.player.setPitch(this.c77i2xb);
         this.mc.player.setVelocity(Vec3d.ZERO);
         if (this.fakePlayer != null) {
            this.mc.world.removeEntity(this.fakePlayer.getId(), RemovalReason.DISCARDED);
            this.fakePlayer = null;
         }

         super.onDisable();
      }
   }

   @Subscribe
   private void onLivingUpdate(EventTick e) {
      if (this.mc.player != null) {
         this.mc.player.noClip = true;
         this.mc.player.setVelocity(Vec3d.ZERO);
         float speed = (float)this.xyi.getValue();
         Vec3d motion = Vec3d.ZERO;
         MoveUtil.setMotion(1.0);
         if (this.mc.options.jumpKey.isPressed()) {
            motion = motion.add(0.0, speed, 0.0);
         }

         if (this.mc.options.sneakKey.isPressed()) {
            motion = motion.subtract(0.0, speed, 0.0);
         }

         this.mc.player.setVelocity(this.mc.player.getVelocity().x, motion.y, this.mc.player.getVelocity().z);
      }
   }

   private void u656c(PlayerPosition pos, Set<PositionFlag> flags) {
      PlayerPosition playerPosition = PlayerPosition.fromEntityLerpTarget(this.fakePlayer);
      PlayerPosition playerPosition2 = PlayerPosition.apply(playerPosition, pos, flags);
      this.bv7ae = new Vec3d(playerPosition2.position().getX(), playerPosition2.position().getY(), playerPosition2.position().getZ());
      this.hGlm = playerPosition2.yaw();
      this.c77i2xb = playerPosition2.pitch();
   }

   @Subscribe
   private void onPacket(EventPacket e) {
      if (e.getPacket() instanceof PlayerPositionLookS2CPacket packet) {
         if (this.mc.world == null || this.mc.player == null || !this.mc.player.isAlive()) {
            return;
         }

         NetworkUtils.sendPacket(new TeleportConfirmC2SPacket(packet.teleportId()));
         if (!this.mc.player.hasVehicle()) {
            this.u656c(packet.change(), packet.relatives());
         }

         NetworkUtils.sendSilentPacket(new Full(this.bv7ae.getX(), this.bv7ae.getY(), this.bv7ae.getZ(), this.hGlm, this.c77i2xb, false, false));
         e.cancelEvent();
      }

      if (e.getPacket() instanceof PlayerMoveC2SPacket) {
         e.cancelEvent();
      }

      if (e.getPacket() instanceof PlayerInteractBlockC2SPacket p) {
         NetworkUtils.sendPacket(new PlayerInteractItemC2SPacket(p.getHand(), p.getSequence(), this.mc.player.getYaw(), this.mc.player.getPitch()));
         e.cancelEvent();
      }

      if (e.getPacket() instanceof PlayerRespawnS2CPacket) {
         this.setEnabled(false);
      }

      if (e.getPacket() instanceof GameJoinS2CPacket) {
         this.setEnabled(false);
      }

      if (e.getPacket() instanceof DisconnectS2CPacket) {
         this.setEnabled(false);
      }
   }
}
