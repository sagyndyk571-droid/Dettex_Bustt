package zov.viola.util.commands.defaults;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.chat.ChatUtil;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.exception.CommandNotEnoughArgumentsException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.party.connection.PartyApiClient;

public class PartyCommand extends Command implements IMinecraft {
   public PartyCommand() {
      super("party");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      String action = args.hasAny() ? args.getString().toLowerCase(Locale.ROOT) : "help";
      switch (action) {
         case "create":
            this.peacR6(args);
            break;
         case "invite":
            this.aJ2or1(args);
            break;
         case "join":
            this.al25s(args);
            break;
         case "leave":
            this.m2Uq94();
            break;
         case "disband":
            this.gHlX();
            break;
         case "list":
            this.gJG87();
            break;
         case "kick":
            this.z5kdtT(args);
            break;
         default:
            this.logDirect(
               D.k(
                  new int[]{1260, 1134, 1243, 1073, 1219, 1134, 1186, 1092, 1228, 1131, 1196, 38, 1230, 1125, 1239, 1084, 1231, 1127, 1235, 1083, 1221, 1131},
                  new int[]{241, 91, 227, 6}
               )
            );
      }
   }

   private String nz83d1() {
      return mc.player.getNameForScoreboard();
   }

   private void peacR6(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String name = args.getString();
      JsonObject req = new JsonObject();
      req.addProperty("leader", this.nz83d1());
      req.addProperty("name", name);
      PartyApiClient.postAsync("/party/create", req, json -> ChatUtil.send(Formatting.GRAY + "Вы создали пати с названием " + Formatting.WHITE + name));
   }

   private void aJ2or1(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String target = args.getString();
      JsonObject req = new JsonObject();
      req.addProperty("leader", this.nz83d1());
      req.addProperty("target", target);
      PartyApiClient.postAsync("/party/invite", req, json -> ChatUtil.send(Formatting.GRAY + "Вы пригласили игрока " + Formatting.WHITE + target));
   }

   private void al25s(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String party = args.getString();
      JsonObject req = new JsonObject();
      req.addProperty("player", this.nz83d1());
      req.addProperty("party", party);
      PartyApiClient.postAsync("/party/join", req, json -> ChatUtil.send(Formatting.GRAY + "Вы вошли в пати " + Formatting.WHITE + party));
   }

   private void m2Uq94() {
      JsonObject req = new JsonObject();
      req.addProperty("player", this.nz83d1());
      PartyApiClient.postAsync("/party/leave", req, json -> ChatUtil.send(Formatting.GRAY + "Вы покинули пати"));
   }

   private void gHlX() {
      JsonObject req = new JsonObject();
      req.addProperty("leader", this.nz83d1());
      PartyApiClient.postAsync("/party/disband", req, json -> ChatUtil.send(Formatting.GRAY + "Вы распустили пати"));
   }

   private void z5kdtT(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String target = args.getString();
      JsonObject req = new JsonObject();
      req.addProperty("leader", this.nz83d1());
      req.addProperty("target", target);
      PartyApiClient.postAsync("/party/kick", req, json -> ChatUtil.send(Formatting.GRAY + "Вы кикнули игрока " + Formatting.WHITE + target));
   }

   private void gJG87() {
      JsonObject req = new JsonObject();
      req.addProperty("player", this.nz83d1());
      PartyApiClient.postAsync("/party/list", req, json -> {
         String[] members = json.get("message").getAsString().split(", ");
         ChatUtil.send(Formatting.GRAY + "Игроки в пати:");

         for (String m : members) {
            ChatUtil.send(" " + Formatting.WHITE + m);
         }
      });
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandNotEnoughArgumentsException {
      int size = args.getArgs().size();
      if (size <= 1) {
         String prefix = size == 1 ? args.peekString(0) : "";
         return new TabCompleteHelper()
            .prepend("create", "invite", "join", "leave", "disband", "list", "kick")
            .filterPrefix(prefix)
            .sortAlphabetically()
            .stream();
      } else {
         return Stream.empty();
      }
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{
            1220,
            1251,
            1204,
            1204,
            1253,
            1254,
            1228,
            1203,
            1177,
            253,
            1201,
            1214,
            1263,
            1256,
            1217,
            1226,
            251,
            1253,
            1200,
            1222,
            1253,
            1255,
            1213,
            1204,
            251,
            1253,
            1204,
            166,
            1252,
            1261,
            1217,
            1214,
            251,
            1248,
            1203,
            166,
            1248,
            1171,
            1202,
            1208,
            1250,
            253,
            1207,
            1214,
            1178,
            1183,
            1203,
            1211,
            1181,
            1253,
            1211
         },
         new int[]{219, 221, 131, 134}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return List.of(
         D.k(
            new int[]{
               1198,
               1167,
               1092,
               1058,
               1161,
               1157,
               1096,
               50,
               1152,
               1162,
               1079,
               50,
               1271,
               1166,
               1080,
               1058,
               1158,
               1162,
               1101,
               1071,
               1164,
               1278,
               88,
               1069,
               1156,
               1267,
               1088,
               50,
               1269,
               1161,
               1081,
               1104,
               1153,
               1165,
               1094,
               1067
            },
            new int[]{180, 177, 120, 18}
         ),
         "",
         "Использование:",
         D.k(
            new int[]{
               116,
               168,
               17,
               66,
               56,
               252,
               24,
               3,
               41,
               250,
               4,
               66,
               62,
               237,
               65,
               31,
               36,
               233,
               12,
               70,
               116,
               168,
               76,
               3,
               1131,
               1206,
               1110,
               1047,
               1146,
               1226,
               1069,
               3,
               1141,
               1208,
               1059,
               1051
            },
            new int[]{74, 136, 97, 35}
         ),
         D.k(
            new int[]{
               229,
               23,
               77,
               85,
               169,
               67,
               68,
               20,
               178,
               89,
               75,
               93,
               175,
               82,
               29,
               8,
               171,
               91,
               92,
               77,
               190,
               69,
               3,
               20,
               246,
               23,
               1058,
               1140,
               1251,
               1028,
               1030,
               1028,
               1178,
               1039,
               1151,
               1144,
               251,
               1039,
               1038,
               1140,
               1253,
               1037,
               1037,
               20,
               243,
               1036,
               1029,
               1024,
               1262,
               1143,
               20
            },
            new int[]{219, 55, 61, 52}
         ),
         D.k(
            new int[]{
               50,
               19,
               0,
               101,
               126,
               71,
               9,
               36,
               102,
               92,
               25,
               106,
               44,
               15,
               30,
               101,
               97,
               86,
               78,
               36,
               33,
               19,
               1122,
               1082,
               1077,
               1137,
               1096,
               36,
               1086,
               19,
               1103,
               1076,
               1102,
               1035
            },
            new int[]{12, 51, 112, 4}
         ),
         D.k(
            new int[]{
               89, 179, 2, 86, 21, 231, 11, 23, 11, 246, 19, 65, 2, 179, 95, 23, 1141, 1240, 1099, 1141, 1119, 179, 1098, 1024, 71, 1196, 1090, 1141, 1119
            },
            new int[]{103, 147, 114, 55}
         ),
         D.k(
            new int[]{
               208,
               138,
               97,
               77,
               156,
               222,
               104,
               12,
               138,
               195,
               98,
               78,
               143,
               196,
               117,
               12,
               195,
               138,
               1073,
               1052,
               1199,
               1173,
               1106,
               1133,
               1196,
               1170,
               1107,
               1120,
               206,
               1173,
               1057,
               1134,
               1238,
               138,
               57,
               1047,
               1238,
               1182,
               1060,
               1132,
               199
            },
            new int[]{238, 170, 17, 44}
         ),
         D.k(
            new int[]{
               47,
               240,
               193,
               26,
               99,
               164,
               200,
               91,
               125,
               185,
               194,
               15,
               49,
               253,
               145,
               1124,
               1071,
               1169,
               1165,
               1093,
               1107,
               1168,
               1156,
               1081,
               1117,
               240,
               1264,
               1092,
               1065,
               1169,
               1167,
               1089,
               49,
               1256,
               1154,
               1083,
               1071,
               1258,
               1167,
               1097,
               49,
               1250,
               145,
               1092,
               1057,
               1170,
               1161
            },
            new int[]{17, 208, 177, 123}
         ),
         D.k(
            new int[]{
               108,
               166,
               7,
               23,
               32,
               242,
               14,
               86,
               57,
               239,
               20,
               29,
               114,
               186,
               7,
               26,
               51,
               255,
               18,
               4,
               108,
               166,
               90,
               86,
               1096,
               1214,
               1101,
               1099,
               1041,
               1220,
               1083,
               86,
               1130,
               1205,
               1079,
               1096,
               1128,
               1206,
               87,
               94,
               1129,
               1214,
               1091,
               1091,
               1042,
               175
            },
            new int[]{82, 134, 119, 118}
         )
      );
   }
}
