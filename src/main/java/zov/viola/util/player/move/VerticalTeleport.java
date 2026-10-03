package zov.viola.util.player.move;

import lombok.Generated;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.packet.NetworkUtils;

public final class VerticalTeleport implements IMinecraft {
   public static void teleport(double offset) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         double x = mc.player.getX();
         double y = mc.player.getY();
         double z = mc.player.getZ();

         for (int i = 0; i < 3; i++) {
            NetworkUtils.sendSilentPacket(new OnGroundOnly(mc.player.isOnGround(), mc.player.horizontalCollision));
         }

         NetworkUtils.sendSilentPacket(new PositionAndOnGround(x, y + offset, z, false, mc.player.horizontalCollision));
         mc.player.setPosition(x, y + offset, z);
      }
   }

   @Generated
   private VerticalTeleport() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               167,
               209,
               129,
               214,
               211,
               208,
               155,
               133,
               146,
               153,
               157,
               209,
               154,
               213,
               129,
               209,
               138,
               153,
               139,
               201,
               146,
               202,
               155,
               133,
               146,
               215,
               140,
               133,
               144,
               216,
               134,
               203,
               156,
               205,
               200,
               199,
               150,
               153,
               129,
               203,
               128,
               205,
               137,
               203,
               135,
               208,
               137,
               209,
               150,
               221
            },
            new int[]{243, 185, 232, 165}
         )
      );
   }
}
