package zov.viola.util.commands.defaults;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import zov.viola.obf.D;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;

public class TpCommand extends Command {
   public TpCommand() {
      super("tp");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String name = args.getString();
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.world != null && mc.player != null) {
         PlayerEntity entityPlayer = mc.world
            .getPlayers()
            .stream()
            .filter(player -> player.getName().getString().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
         if (entityPlayer == null) {
            this.logDirect(Formatting.RED + "Игрок " + name + " не найден.");
         } else {
            double x = entityPlayer.getX();
            double y = entityPlayer.getY();
            double z = entityPlayer.getZ();
            mc.player.setPosition(x, y, z);
            this.logDirect("Телепортировано к игроку " + entityPlayer.getName().getString());
         }
      } else {
         this.logDirect(Formatting.RED + "Невозможно выполнить команду.");
      }
   }

   @Override
   public String getShortDesc() {
      return "Телепорт к игроку";
   }

   @Override
   public List<String> getLongDesc() {
      return List.of(
         D.k(
            new int[]{
               1125,
               1113,
               1049,
               1246,
               1144,
               1106,
               1122,
               1193,
               1151,
               1068,
               1121,
               1246,
               1029,
               76,
               1040,
               1243,
               1030,
               76,
               1048,
               203,
               1028,
               1110,
               1042,
               1244,
               1143,
               1105,
               1055,
               1237,
               1147,
               1071,
               2,
               1235,
               1140,
               1068,
               1052,
               1233,
               1028
            },
            new int[]{71, 108, 34, 235}
         ),
         "",
         D.k(
            new int[]{
               157,
               26,
               26,
               237,
               131,
               6,
               1110,
               1185,
               1260,
               4,
               78,
               8329,
               131,
               1144,
               1115,
               1190,
               1174,
               1029,
               1104,
               1245,
               1249,
               26,
               1108,
               189,
               1179,
               1033,
               1070,
               1187,
               1177,
               1145
            },
            new int[]{163, 58, 110, 157}
         )
      );
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) {
      if (args.hasExactlyOne()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.world != null) {
            return mc.world.getPlayers().stream().map(player -> player.getName().getString());
         }
      }

      return Stream.empty();
   }
}
