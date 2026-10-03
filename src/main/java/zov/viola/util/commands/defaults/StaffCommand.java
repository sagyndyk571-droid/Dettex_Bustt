package zov.viola.util.commands.defaults;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;
import zov.viola.Viola;
import zov.viola.obf.D;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.IBaritoneChatControl;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.Paginator;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.staff.Staff;
import zov.viola.util.staff.StaffManager;

public class StaffCommand extends Command {
   private final StaffManager yPynz5;

   public StaffCommand(Viola onetap) {
      super("staff");
      this.yPynz5 = onetap.getStaffManager();
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      String action = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
      switch (action) {
         case "add":
            this.cyyIq(args);
            break;
         case "remove":
            this.absK(args);
            break;
         case "list":
            this.alA1(args, label);
            break;
         case "clear":
            this.cO5rx(args);
            break;
         default:
            this.logDirect(
               D.k(
                  new int[]{
                     1039,
                     1095,
                     1239,
                     1040,
                     1056,
                     1095,
                     1198,
                     1125,
                     1071,
                     1090,
                     1184,
                     7,
                     1069,
                     1100,
                     1243,
                     1053,
                     1068,
                     1102,
                     1247,
                     1050,
                     1062,
                     1090,
                     193,
                     7,
                     1034,
                     1075,
                     1232,
                     1049,
                     1065,
                     1086,
                     1240,
                     1124,
                     1067,
                     82,
                     142,
                     67,
                     118,
                     93,
                     157,
                     66,
                     127,
                     29,
                     153,
                     66,
                     61,
                     30,
                     134,
                     84,
                     102,
                     93,
                     140,
                     75,
                     119,
                     19,
                     157,
                     9
                  },
                  new int[]{18, 114, 239, 39}
               ),
               Formatting.GRAY
            );
      }
   }

   private void cyyIq(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String name = args.getString();
      if (StaffManager.isStaff(name)) {
         this.logDirect(
            D.k(
               new int[]{
                  1190,
                  1209,
                  1273,
                  1122,
                  171,
                  1219,
                  1268,
                  1120,
                  1205,
                  1217,
                  231,
                  1123,
                  1213,
                  1230,
                  231,
                  1042,
                  171,
                  1210,
                  1272,
                  1048,
                  1226,
                  1217,
                  1266,
                  0,
                  1207,
                  1221,
                  1267,
                  1045,
                  1227,
                  1227,
                  1157,
                  1054,
                  1227,
                  1221,
                  1269
               },
               new int[]{139, 251, 199, 32}
            ),
            Formatting.GRAY
         );
      } else {
         StaffManager.addStaff(new Staff(name));
         this.logDirect("Игрок с именем " + Formatting.WHITE + name + Formatting.GRAY + " успешно добавлен в список модераторов");
      }
   }

   private void absK(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String name = args.getString();
      if (!StaffManager.isStaff(name)) {
         this.logDirect(
            D.k(
               new int[]{
                  1059,
                  1278,
                  1036,
                  1182,
                  1080,
                  238,
                  1038,
                  1171,
                  1089,
                  1264,
                  1036,
                  128,
                  1075,
                  238,
                  1143,
                  1183,
                  1081,
                  1167,
                  1036,
                  1173,
                  33,
                  1266,
                  1032,
                  1172,
                  1076,
                  1166,
                  1030,
                  1250,
                  1087,
                  1166,
                  1032,
                  1170,
                  33,
                  1267,
                  1027,
                  128,
                  1084,
                  1278,
                  1039,
                  1172,
                  1076,
                  1267
               },
               new int[]{1, 206, 54, 160}
            ),
            Formatting.GRAY
         );
      } else {
         StaffManager.removeStaff(name);
         this.logDirect("Игрок с именем " + Formatting.WHITE + name + Formatting.GRAY + " успешно удален из списка модераторов");
      }
   }

   private void alA1(IArgConsumer args, String label) throws CommandException {
      args.requireMax(1);
      Set<Staff> staffSet = StaffManager.getStaffList();
      List<Staff> sorted = staffSet.stream().sorted(Comparator.comparing(s -> s.name.toLowerCase())).toList();
      Paginator.paginate(
         args,
         new Paginator<>(sorted),
         () -> this.logDirect(
            D.k(
               new int[]{1195, 1144, 1242, 1214, 1204, 1149, 194, 1219, 1204, 1139, 1239, 1215, 1210, 1029, 1244, 1215, 1204, 1141, 216},
               new int[]{138, 71, 226, 255}
            ),
            Formatting.GRAY
         ),
         staff -> {
            Text nameText = Text.literal(Formatting.GRAY + "- " + Formatting.WHITE + staff.name);
            Text deleteText = Text.literal(Formatting.RED + " [Удалить]")
               .styled(
                  style -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, IBaritoneChatControl.FORCE_COMMAND_PREFIX + "staff remove " + staff.name))
                     .withHoverEvent(
                        new HoverEvent(
                           net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                           Text.literal(
                              D.k(
                                 new int[]{138, 183, 127, 208, 162, 251, 98, 220, 233, 191, 115, 223, 172, 175, 115, 147, 186, 175, 119, 213, 175},
                                 new int[]{201, 219, 22, 179}
                              )
                           )
                        )
                     )
               );
            return nameText.copy().append(deleteText);
         },
         IBaritoneChatControl.FORCE_COMMAND_PREFIX + label
      );
   }

   private void cO5rx(IArgConsumer args) throws CommandException {
      args.requireMax(1);
      StaffManager.clearStaff();
      this.logDirect(
         D.k(
            new int[]{
               1217,
               1071,
               1197,
               1198,
               1246,
               1066,
               181,
               1235,
               1246,
               1060,
               1184,
               1199,
               1232,
               1106,
               1195,
               1199,
               1246,
               1058,
               181,
               1233,
               1191,
               1064,
               1244,
               1242,
               1245
            },
            new int[]{224, 16, 149, 239}
         ),
         Formatting.GRAY
      );
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
      if (args.hasAny() && args.hasExactlyOne()) {
         return new TabCompleteHelper().prepend("add", "remove", "list", "clear").filterPrefix(args.getString()).sortAlphabetically().stream();
      } else {
         if (args.hasAny()) {
            String action = args.peekString(0).toLowerCase(Locale.ROOT);
            if (action.equals("remove") && args.hasExactly(2)) {
               String prefix = args.peekString(1).toLowerCase(Locale.ROOT);
               return StaffManager.getStaffList()
                  .stream()
                  .map(staff -> staff.name)
                  .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
                  .sorted()
                  .distinct();
            }

            if (action.equals("add") && args.hasExactly(2)) {
               String prefix = args.peekString(1).toLowerCase(Locale.ROOT);
               return MinecraftClient.getInstance()
                  .getNetworkHandler()
                  .getPlayerList()
                  .stream()
                  .map(info -> info.getProfile().getName())
                  .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
                  .sorted()
                  .distinct();
            }
         }

         return Stream.empty();
      }
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{
            1038,
            1147,
            1073,
            1260,
            1055,
            1151,
            1092,
            1249,
            1045,
            1137,
            81,
            1181,
            1042,
            1148,
            1072,
            1254,
            1043,
            1144,
            81,
            1248,
            1043,
            1136,
            1092,
            1180,
            1053,
            1030,
            1103,
            1180,
            1043,
            1142
         },
         new int[]{45, 68, 113, 220}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         D.k(
            new int[]{
               1165,
               1079,
               1172,
               17,
               1178,
               1099,
               1176,
               1025,
               1181,
               1089,
               1172,
               17,
               1183,
               1099,
               1171,
               1027,
               1182,
               1102,
               1259,
               1028,
               1250,
               85,
               1255,
               1038,
               1248,
               1093,
               1174,
               1034,
               1263,
               1079,
               1256,
               17,
               1249,
               1098,
               1180,
               1136,
               1178,
               1099,
               1176,
               17,
               1180,
               1099,
               1168,
               1028,
               1248,
               1093,
               1254,
               1039,
               1248,
               1099,
               1174,
               31
            },
            new int[]{160, 117, 164, 49}
         ),
         "",
         "Использование:",
         D.k(
            new int[]{
               187,
               244,
               245,
               70,
               228,
               178,
               224,
               18,
               228,
               176,
               226,
               18,
               185,
               186,
               231,
               95,
               224,
               234,
               166,
               31,
               165,
               1216,
               1208,
               1027,
               1205,
               1254,
               1213,
               1149,
               1200,
               1174,
               166,
               1034,
               1206,
               1172,
               1208,
               1032,
               1205,
               244,
               1204,
               18,
               1220,
               1259,
               1214,
               1139,
               1211,
               1262,
               166,
               1038,
               1211,
               1248,
               1203,
               1138,
               1205,
               1174,
               1208,
               1138,
               1211,
               1254,
               168
            },
            new int[]{133, 212, 134, 50}
         ),
         D.k(
            new int[]{
               8,
               79,
               167,
               72,
               87,
               9,
               178,
               28,
               68,
               10,
               185,
               83,
               64,
               10,
               244,
               0,
               88,
               14,
               185,
               89,
               8,
               79,
               249,
               28,
               1045,
               1115,
               1252,
               1031,
               1145,
               1114,
               1174,
               28,
               1038,
               1116,
               1172,
               1026,
               1036,
               1119,
               244,
               1028,
               1025,
               79,
               1173,
               1027,
               1038,
               1070,
               1262,
               1036,
               22,
               1107,
               1258,
               1032,
               1027,
               1071,
               1252,
               1150,
               1032,
               1071,
               1258,
               1038,
               24
            },
            new int[]{54, 111, 212, 60}
         ),
         D.k(
            new int[]{
               124,
               117,
               141,
               103,
               35,
               51,
               152,
               51,
               46,
               60,
               141,
               103,
               98,
               120,
               222,
               1036,
               1148,
               1135,
               1230,
               1060,
               1033,
               1127,
               1230,
               1062,
               1024,
               117,
               1215,
               1068,
               1146,
               1044,
               1216,
               1065,
               98,
               1129,
               1216,
               1063,
               1143,
               1045,
               1230,
               1105,
               1148,
               1045,
               1216,
               1057,
               108
            },
            new int[]{66, 85, 254, 19}
         ),
         D.k(
            new int[]{
               68,
               85,
               110,
               144,
               27,
               19,
               123,
               196,
               25,
               25,
               120,
               133,
               8,
               85,
               48,
               196,
               1092,
               1074,
               1061,
               1197,
               1098,
               1088,
               1119,
               196,
               1096,
               1088,
               1116,
               1192,
               90,
               1076,
               1058,
               1244,
               1083,
               1099,
               1063,
               196,
               1094,
               1099,
               1065,
               1233,
               1082,
               1093,
               1119,
               1242,
               1082,
               1099,
               1071,
               202
            },
            new int[]{122, 117, 29, 228}
         )
      );
   }
}
