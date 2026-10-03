package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.network.packet.Packet;
import zov.viola.event.Event;

public class EventPacket extends Event {
   private final Packet<?> uGkdnZ;
   private EventPacket.Type eM8ux;

   private boolean ju18Op() {
      return this.eM8ux == EventPacket.Type.SEND;
   }

   private boolean r7ou() {
      return this.eM8ux == EventPacket.Type.RECEIVE;
   }

   @Generated
   public Packet<?> getPacket() {
      return this.uGkdnZ;
   }

   @Generated
   public EventPacket.Type getType() {
      return this.eM8ux;
   }

   @Generated
   public EventPacket(Packet<?> packet, EventPacket.Type type) {
      this.uGkdnZ = packet;
      this.eM8ux = type;
   }

   public static enum Type {
      SEND,
      RECEIVE;
   }
}
