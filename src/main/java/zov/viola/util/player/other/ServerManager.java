package zov.viola.util.player.other;

import com.google.common.eventbus.Subscribe;
import lombok.Generated;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import zov.viola.Viola;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.event.list.EventPopTotem;
import zov.viola.event.list.EventTick;
import zov.viola.util.IMinecraft;

public class ServerManager implements IMinecraft {
   private int fk8YG;
   private float p91AC;
   private float j7ct;
   private float svvwc;
   private double mgkvvM3;
   private double wJPJ2;
   private double uvLtD8b;
   private boolean ucm0hg;
   private boolean sm8pPX;
   private boolean t8cL44t;
   private boolean jqgh;
   private float a292;

   public ServerManager() {
      Viola.getInstance().getEventBus().register(this);
   }

   @Subscribe
   private void onTick(EventTick ignored) {
      if (mc.player != null) {
         double y = mc.player.prevY - mc.player.getY();
         if (mc.player.isOnGround()) {
            this.svvwc = 0.0F;
         } else if (y > 0.0) {
            this.svvwc += (float)y;
         }
      }
   }

   @Subscribe
   private void onPlayerUpdate(EventPlayerUpdate ignored) {
      this.a292++;
   }

   @Subscribe
   public void onPacketSend(EventPacket e) {
      if (e.getPacket() instanceof PlayerMoveC2SPacket packet) {
         if (packet.changesPosition()) {
            this.mgkvvM3 = packet.getX(mc.player.getX());
            this.wJPJ2 = packet.getY(mc.player.getY());
            this.uvLtD8b = packet.getZ(mc.player.getZ());
         }

         if (packet.changesLook()) {
            this.p91AC = packet.getYaw(mc.player.getYaw());
            this.j7ct = packet.getPitch(mc.player.getPitch());
         }

         this.ucm0hg = packet.isOnGround();
         this.jqgh = packet.horizontalCollision();
      }

      if (e.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
         this.fk8YG = packet.getSelectedSlot();
      }

      if (e.getPacket() instanceof ClientCommandC2SPacket packet) {
         switch (packet.getMode()) {
            case PRESS_SHIFT_KEY:
               this.t8cL44t = true;
               break;
            case RELEASE_SHIFT_KEY:
               this.t8cL44t = false;
         }
      }

      if (e.getPacket() instanceof ClientCommandC2SPacket command) {
         if (command.getMode().equals(Mode.START_SPRINTING)) {
            e.setCancelled(this.sm8pPX);
            if (!e.isCancelled()) {
               this.a292 = 0.0F;
            }

            this.sm8pPX = true;
         } else if (command.getMode().equals(Mode.STOP_SPRINTING)) {
            e.setCancelled(!this.sm8pPX);
            if (!e.isCancelled()) {
               this.a292 = 0.0F;
            }

            this.sm8pPX = false;
         }
      }
   }

   @Subscribe
   public void onPacketReceive(EventPacket e) {
      if (e.getPacket() instanceof EntityStatusS2CPacket packet && packet.getStatus() == 35) {
         if (!(packet.getEntity(mc.world) instanceof PlayerEntity player)) {
            return;
         }

         new EventPopTotem(player).post();
      }
   }

   @Generated
   public int getServerSlot() {
      return this.fk8YG;
   }

   @Generated
   public float getServerYaw() {
      return this.p91AC;
   }

   @Generated
   public float getServerPitch() {
      return this.j7ct;
   }

   @Generated
   public float getFallDistance() {
      return this.svvwc;
   }

   @Generated
   public double getServerX() {
      return this.mgkvvM3;
   }

   @Generated
   public double getServerY() {
      return this.wJPJ2;
   }

   @Generated
   public double getServerZ() {
      return this.uvLtD8b;
   }

   @Generated
   public boolean isServerOnGround() {
      return this.ucm0hg;
   }

   @Generated
   public boolean isServerSprinting() {
      return this.sm8pPX;
   }

   @Generated
   public boolean isServerSneaking() {
      return this.t8cL44t;
   }

   @Generated
   public boolean isServerHorizontalCollision() {
      return this.jqgh;
   }

   @Generated
   public float getSprintingChangeTicks() {
      return this.a292;
   }
}
