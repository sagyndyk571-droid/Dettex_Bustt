package zov.viola.util.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;

public final class NetworkUtils {
   private static boolean do2J;

   private NetworkUtils() {
   }

   public static void sendSilentPacket(Packet<?> packet) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      if (minecraft.player != null && minecraft.getNetworkHandler() != null) {
         try {
            do2J = true;
            minecraft.getNetworkHandler().sendPacket(packet);
         } finally {
            do2J = false;
         }
      }
   }

   public static void sendPacket(Packet<?> packet) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      if (minecraft.player != null && minecraft.getNetworkHandler() != null) {
         minecraft.getNetworkHandler().sendPacket(packet);
      }
   }

   public static boolean isSendingSilent() {
      return do2J;
   }
}
