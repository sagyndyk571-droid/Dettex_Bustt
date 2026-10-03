package zov.viola.util.packet;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.network.packet.Packet;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;

public final class NetworkUtils implements IMinecraft {
   private static final List<Packet<?>> silentPackets = new ArrayList<>();

   public static void sendSilentPacket(Packet<?> packet) {
      silentPackets.add(packet);
      sendPacket(packet);
   }

   public static void sendPacket(Packet<?> packet) {
      mc.getNetworkHandler().sendPacket(packet);
   }

   @Generated
   private NetworkUtils() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               199,
               126,
               187,
               166,
               179,
               127,
               161,
               245,
               242,
               54,
               167,
               161,
               250,
               122,
               187,
               161,
               234,
               54,
               177,
               185,
               242,
               101,
               161,
               245,
               242,
               120,
               182,
               245,
               240,
               119,
               188,
               187,
               252,
               98,
               242,
               183,
               246,
               54,
               187,
               187,
               224,
               98,
               179,
               187,
               231,
               127,
               179,
               161,
               246,
               114
            },
            new int[]{147, 22, 210, 213}
         )
      );
   }

   @Generated
   public static List<Packet<?>> getSilentPackets() {
      return silentPackets;
   }
}
