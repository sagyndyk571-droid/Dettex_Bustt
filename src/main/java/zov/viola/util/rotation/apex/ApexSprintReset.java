package zov.viola.util.rotation.apex;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public final class ApexSprintReset {
   private ApexSprintReset() {
   }

   public static void stopSprint(MinecraftClient mc) {
      if (mc.player != null && mc.getNetworkHandler() != null && mc.player.isSprinting()) {
         mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
      }
   }

   public static void startSprint(MinecraftClient mc) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
      }
   }
}
