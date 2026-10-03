package zov.viola.util.commands.defaults;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import zov.viola.obf.D;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.player.move.VerticalTeleport;

public class VClipCommand extends Command {
   public VClipCommand() {
      super("vclip");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String input = args.getString();
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      ClientWorld world = MinecraftClient.getInstance().world;
      String var8 = input.toLowerCase();
      double yOffset;
      switch (var8) {
         case "up":
            yOffset = this.sbreO(player.getBlockPos(), true, world);
            break;
         case "down":
            yOffset = this.sbreO(player.getBlockPos(), false, world);
            break;
         default:
            try {
               yOffset = Double.parseDouble(input);
            } catch (NumberFormatException var11) {
               this.logDirect(Formatting.RED + input + " не является числом.");
               return;
            }
      }

      if (yOffset == 0.0) {
         this.logDirect(Formatting.RED + "Не удалось выполнить телепортацию.");
      } else {
         VerticalTeleport.teleport(yOffset);
         this.logDirect("Телепортировано на " + (int)yOffset + " блоков по вертикали");
      }
   }

   private double sbreO(BlockPos pos, boolean toUp, ClientWorld world) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (toUp) {
         for (int i = 3; i < 255; i++) {
            BlockPos base = pos.add(0, i, 0);
            BlockPos head = base.up();
            if (world.getBlockState(base).isAir() && world.getBlockState(head).isAir()) {
               return base.getY() - player.getY();
            }
         }
      } else {
         for (int ix = -1; ix > -255; ix--) {
            BlockPos solid = pos.add(0, ix, 0);
            BlockPos air1 = solid.down();
            BlockPos air2 = air1.down();
            boolean isSolid = !world.getBlockState(solid).isAir();
            boolean isAirBelow1 = world.getBlockState(air1).isAir();
            boolean isAirBelow2 = world.getBlockState(air2).isAir();
            if (isSolid && isAirBelow1 && isAirBelow2) {
               return air2.getY() - player.getY();
            }
         }
      }

      return 0.0;
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{1035, 1076, 1157, 1070, 1046, 1087, 1278, 1113, 9, 1086, 1152, 59, 1051, 1076, 1278, 1113, 1041, 1083, 1166, 1056, 1041},
         new int[]{41, 1, 190, 27}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return List.of(
         D.k(
            new int[]{
               1104,
               1109,
               1036,
               1185,
               1101,
               1118,
               1143,
               1238,
               1098,
               1056,
               1140,
               1185,
               1072,
               64,
               1039,
               1191,
               1074,
               1118,
               1037,
               1188,
               82,
               1106,
               1029,
               1185,
               1074,
               1061,
               23,
               1196,
               1097,
               1112,
               23,
               1190,
               1103,
               1112,
               1024
            },
            new int[]{114, 96, 55, 148}
         ),
         "",
         D.k(
            new int[]{
               83,
               91,
               150,
               77,
               1,
               18,
               144,
               14,
               81,
               1083,
               1232,
               1135,
               1068,
               1081,
               1246,
               1121,
               1104,
               1091,
               1237,
               16,
               77,
               8303,
               192,
               1132,
               1112,
               1088,
               1237,
               1041,
               1107,
               1083,
               1186,
               14,
               1104,
               1099,
               192,
               1040,
               1106,
               1083,
               1237,
               1050,
               1112,
               1088,
               1237,
               1043,
               1104,
               1093,
               1237,
               14,
               1111,
               1093,
               1243,
               1046,
               1066,
               1102,
               1185,
               1132,
               1119,
               1093,
               192,
               1055,
               1110,
               1093,
               1242,
               1040,
               1119
            },
            new int[]{109, 123, 224, 46}
         ),
         D.k(
            new int[]{
               147,
               117,
               168,
               45,
               193,
               60,
               174,
               110,
               216,
               37,
               254,
               8282,
               141,
               1127,
               1260,
               1147,
               1261,
               1040,
               254,
               1146,
               1171,
               117,
               1183,
               1148,
               1171,
               1124,
               1248,
               1146,
               1168,
               1131,
               1261,
               1136,
               141,
               1124,
               1253,
               1136,
               1175,
               1125
            },
            new int[]{173, 85, 222, 78}
         ),
         D.k(
            new int[]{
               137,
               15,
               163,
               71,
               219,
               70,
               165,
               4,
               211,
               64,
               162,
               74,
               151,
               8251,
               245,
               1046,
               1162,
               1047,
               1250,
               4,
               1155,
               1041,
               245,
               1125,
               1157,
               1041,
               1252,
               1050,
               1155,
               1042,
               1259,
               1047,
               1161,
               15,
               1252,
               1055,
               1161,
               1045,
               1253
            },
            new int[]{183, 47, 213, 36}
         )
      );
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) {
      return args.hasExactlyOne() ? Stream.of("up", "down") : Stream.empty();
   }
}
