package zov.viola.util.commands.defaults;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
import zov.viola.util.friend.Friend;
import zov.viola.util.friend.FriendRepository;

public class FriendCommand extends Command {
   public FriendCommand(Viola onetap) {
      super("friend");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      String action = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
      switch (action) {
         case "add":
            this.zuDhC0(args);
            break;
         case "remove":
            this.xaYN7ib(args);
            break;
         case "list":
            this.xB6x(args, label);
            break;
         case "clear":
            this.egqdjs(args);
            break;
         default:
            this.logDirect(
               D.k(
                  new int[]{
                     1155,
                     1252,
                     1074,
                     1049,
                     1196,
                     1252,
                     1099,
                     1132,
                     1187,
                     1249,
                     1093,
                     14,
                     1185,
                     1263,
                     1086,
                     1044,
                     1184,
                     1261,
                     1082,
                     1043,
                     1194,
                     1249,
                     36,
                     14,
                     1158,
                     1168,
                     1077,
                     1040,
                     1189,
                     1181,
                     1085,
                     1133,
                     1191,
                     241,
                     107,
                     74,
                     250,
                     254,
                     120,
                     75,
                     243,
                     190,
                     124,
                     75,
                     177,
                     189,
                     99,
                     93,
                     234,
                     254,
                     105,
                     66,
                     251,
                     176,
                     120,
                     0
                  },
                  new int[]{158, 209, 10, 46}
               ),
               Formatting.GRAY
            );
      }
   }

   private void zuDhC0(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String name = args.getString();
      if (FriendRepository.isFriend(name)) {
         this.logDirect(
            D.k(
               new int[]{
                  1082,
                  1158,
                  1066,
                  1068,
                  55,
                  1276,
                  1063,
                  1070,
                  1065,
                  1278,
                  52,
                  1069,
                  1057,
                  1265,
                  52,
                  1116,
                  55,
                  1157,
                  1067,
                  1110,
                  1110,
                  1278,
                  1057,
                  78,
                  1059,
                  1156,
                  1111,
                  1113,
                  1058,
                  1277
               },
               new int[]{23, 196, 20, 110}
            ),
            Formatting.GRAY
         );
      } else {
         FriendRepository.addFriend(name);
         this.logDirect(Formatting.GRAY + "Игрок с именем " + Formatting.WHITE + name + Formatting.GRAY + " успешно добавлен в друзья");
      }
   }

   private void xaYN7ib(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String name = args.getString();
      if (!FriendRepository.isFriend(name)) {
         this.logDirect(
            D.k(
               new int[]{1275, 1103, 1167, 1047, 1248, 95, 1153, 1129, 1178, 1100, 149, 1044, 1260, 95, 1160, 1049, 1248, 1099, 1152, 1044},
               new int[]{217, 127, 181, 41}
            ),
            Formatting.GRAY
         );
      } else {
         FriendRepository.removeFriend(name);
         this.logDirect("Игрок с именем " + Formatting.WHITE + name + Formatting.GRAY + " успешно удален из друзей");
      }
   }

   private void xB6x(IArgConsumer args, String label) throws CommandException {
      args.requireMax(1);
      List<Friend> friends = FriendRepository.getFriends();
      Paginator.paginate(
         args,
         new Paginator<>(friends),
         () -> this.logDirect(
            "Список друзей:", Formatting.GRAY
         ),
         friend -> {
            Text nameText = Text.literal(Formatting.GRAY + "- " + Formatting.WHITE + friend.name());
            Text deleteText = Text.literal(Formatting.RED + " [Удалить]")
               .styled(
                  style -> style.withClickEvent(
                        new ClickEvent(Action.RUN_COMMAND, IBaritoneChatControl.FORCE_COMMAND_PREFIX + "friend remove " + friend.name())
                     )
                     .withHoverEvent(
                        new HoverEvent(
                           net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                           Text.literal(
                              D.k(
                                 new int[]{104, 122, 43, 15, 64, 54, 54, 3, 11, 114, 39, 0, 78, 98, 39, 76, 77, 100, 43, 9, 69, 114},
                                 new int[]{43, 22, 66, 108}
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

   private void egqdjs(IArgConsumer args) throws CommandException {
      args.requireMax(1);
      FriendRepository.clear();
      this.logDirect(
         D.k(
            new int[]{1105, 1215, 1079, 1107, 1102, 1210, 47, 1062, 1072, 1219, 1080, 1063, 1097, 160, 1073, 1109, 1096, 1225, 1082, 1071},
            new int[]{112, 128, 15, 18}
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
               return FriendRepository.getFriends()
                  .stream()
                  .map(Friend::name)
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
         new int[]{1179, 1230, 1194, 1024, 1162, 1226, 1247, 1037, 1152, 1220, 202, 1028, 1272, 1202, 1245, 1148, 1271, 1229, 1234},
         new int[]{184, 241, 234, 48}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         D.k(
            new int[]{
               1254,
               1244,
               1186,
               185,
               1265,
               1184,
               1198,
               1193,
               1270,
               1194,
               1186,
               185,
               1268,
               1184,
               1189,
               1195,
               1269,
               1189,
               1245,
               1196,
               1161,
               190,
               1233,
               1190,
               1163,
               1198,
               1184,
               1186,
               1156,
               1244,
               1246,
               185,
               1162,
               1185,
               1194,
               1240,
               1265,
               1184,
               1198,
               185,
               1279,
               1246,
               1233,
               1198,
               1278,
               1191,
               188
            },
            new int[]{203, 158, 146, 153}
         ),
         "",
         "Использование:",
         D.k(
            new int[]{
               229,
               252,
               255,
               51,
               178,
               185,
               247,
               37,
               251,
               189,
               253,
               37,
               251,
               224,
               247,
               32,
               182,
               185,
               167,
               97,
               246,
               252,
               1165,
               1151,
               1258,
               1260,
               1195,
               1146,
               1172,
               1257,
               1243,
               97,
               1251,
               1263,
               1241,
               1151,
               1249,
               1260,
               185,
               1139,
               251,
               1256,
               1241,
               1026,
               1260,
               1168,
               1238,
               111
            },
            new int[]{219, 220, 153, 65}
         ),
         D.k(
            new int[]{
               110,
               10,
               109,
               87,
               57,
               79,
               101,
               65,
               112,
               88,
               110,
               72,
               63,
               92,
               110,
               5,
               108,
               68,
               106,
               72,
               53,
               20,
               43,
               8,
               112,
               1033,
               1087,
               1045,
               1131,
               1125,
               1086,
               1127,
               112,
               1042,
               1080,
               1125,
               1134,
               1040,
               1083,
               5,
               1128,
               1053,
               43,
               1041,
               1040,
               1129,
               1084,
               1040,
               1129,
               4
            },
            new int[]{80, 42, 11, 37}
         ),
         D.k(
            new int[]{
               198,
               61,
               74,
               96,
               145,
               120,
               66,
               118,
               216,
               113,
               69,
               97,
               140,
               61,
               1,
               50,
               1255,
               1059,
               1046,
               1058,
               1231,
               1110,
               1054,
               1058,
               1229,
               1119,
               12,
               1107,
               1223,
               1061,
               1133,
               1068,
               1218,
               61,
               1054,
               1107,
               1229,
               1112,
               12,
               1062,
               1208,
               1118,
               1051,
               1063,
               1217,
               51
            },
            new int[]{248, 29, 44, 18}
         ),
         D.k(
            new int[]{
               113,
               93,
               169,
               53,
               38,
               24,
               161,
               35,
               111,
               30,
               163,
               34,
               46,
               15,
               239,
               106,
               111,
               1118,
               1275,
               1143,
               1140,
               1074,
               1274,
               1029,
               111,
               1103,
               1166,
               1138,
               1034,
               93,
               1275,
               1031,
               1036,
               1098,
               1274,
               1150,
               97
            },
            new int[]{79, 125, 207, 71}
         )
      );
   }
}
