package zov.viola.util.commands.defaults;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import zov.viola.obf.D;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.gps.GpsRenderer;

public class GpsCommand extends Command {
   public GpsCommand() {
      super("gps");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      GpsRenderer gps = GpsRenderer.get();
      if (args.peekString().equalsIgnoreCase("off")) {
         gps.setEnabled(false);
      } else {
         double z2;
         double x2;
         try {
            x2 = Double.parseDouble(args.getString());
            z2 = Double.parseDouble(args.getString());
         } catch (NumberFormatException var9) {
            return;
         }

         gps.setTarget(x2, z2);
         gps.setEnabled(true);
      }
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{
            1050,
            1262,
            1130,
            1064,
            1028,
            1183,
            1050,
            1059,
            1025,
            1181,
            1048,
            1069,
            1147,
            143,
            1129,
            1114,
            1145,
            1178,
            1043,
            1058,
            1146,
            143,
            1045,
            1064,
            25,
            1260,
            1042,
            1064,
            1038,
            1183,
            1045,
            1061,
            1138,
            1178,
            8,
            1058,
            1031,
            1169,
            1128,
            1068,
            1025,
            1170,
            1048,
            1114,
            1138
         },
         new int[]{57, 175, 40, 24}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         D.k(
            new int[]{
               1277,
               104,
               1109,
               1224,
               1248,
               1142,
               1059,
               1210,
               1170,
               104,
               1063,
               1204,
               1250,
               1137,
               74,
               1228,
               1250,
               1140,
               1114,
               1227,
               1256,
               1027,
               74,
               1226,
               1250,
               1150,
               1111,
               1224,
               252,
               1035,
               1067,
               1204,
               1260,
               1141,
               1108,
               1220,
               1252,
               1034,
               1062,
               214,
               1181,
               1034,
               1066,
               1219,
               1255,
               1138,
               1065,
               214,
               1183,
               1138,
               1114,
               1217,
               1175,
               1146,
               1114,
               1208,
               1173,
               1035,
               1060,
               214,
               1249,
               1144,
               74,
               1205,
               1254,
               1144,
               1117,
               1222,
               1249,
               1141,
               1057,
               1219,
               252,
               1138,
               1108,
               1224,
               1180,
               1148,
               1106,
               1227,
               1260,
               1034,
               1057
            },
            new int[]{220, 72, 106, 246}
         ),
         "",
         "Использование:",
         D.k(
            new int[]{
               30,
               65,
               255,
               179,
               83,
               65,
               164,
               187,
               30,
               65,
               164,
               185,
               30,
               65,
               181,
               227,
               1025,
               1059,
               1192,
               1265,
               1048,
               1059,
               184,
               1154,
               1122,
               1057,
               1197,
               1272,
               1050,
               1058,
               184,
               1278,
               1040,
               65,
               1243,
               1273,
               1040,
               1110,
               1192,
               1278,
               1053,
               1066,
               1197,
               227,
               1050,
               1119,
               1190,
               1155,
               1044,
               1113,
               1189,
               1267,
               1122,
               1066,
               182
            },
            new int[]{32, 97, 152, 195}
         ),
         D.k(
            new int[]{
               21,
               227,
               57,
               106,
               88,
               227,
               49,
               124,
               77,
               227,
               115,
               58,
               1077,
               1153,
               1124,
               1057,
               1125,
               1156,
               1134,
               1071,
               1129,
               227,
               1055,
               1112,
               1131,
               1270,
               1125,
               1056,
               1128,
               237
            },
            new int[]{43, 195, 94, 26}
         )
      );
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) {
      if (!args.hasAny() || args.hasExactlyOne() && args.getArgs().getFirst().getValue().isEmpty()) {
         ClientPlayerEntity p2 = MinecraftClient.getInstance().player;
         return p2 == null ? Stream.of("off") : Stream.of("off", (int)p2.getX() + " " + (int)p2.getZ());
      } else {
         return Stream.empty();
      }
   }
}
