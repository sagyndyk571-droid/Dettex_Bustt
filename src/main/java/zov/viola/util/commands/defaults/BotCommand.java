package zov.viola.util.commands.defaults;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import zov.viola.obf.D;
import zov.viola.util.bot.BotSessionManager;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;

public class BotCommand extends Command {
   public BotCommand() {
      super("bot");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      String action = args.hasAny() ? args.getString().toLowerCase(Locale.ROOT) : "list";
      switch (action) {
         case "connect":
            this.t2oEb(args);
            break;
         case "remove":
            this.hfdfB(args);
            break;
         case "return":
            this.mcufq27();
            break;
         case "control":
            this.zdU5H(args);
            break;
         case "say":
            this.ibi6sgo(args);
            break;
         case "sayall":
            this.gGp2cIs(args);
            break;
         case "list":
            this.v5x1();
            break;
         default:
            this.logDirect(
               D.k(
                  new int[]{
                     1261,
                     1249,
                     1270,
                     1027,
                     1218,
                     1249,
                     1167,
                     1142,
                     1229,
                     1252,
                     1153,
                     20,
                     1231,
                     1258,
                     1274,
                     1038,
                     1230,
                     1256,
                     1278,
                     1033,
                     1220,
                     1252,
                     224,
                     20,
                     1256,
                     1173,
                     1265,
                     1034,
                     1227,
                     1176,
                     1273,
                     1143,
                     1225,
                     244,
                     173,
                     91,
                     158,
                     186,
                     171,
                     87,
                     132,
                     251,
                     188,
                     81,
                     157,
                     187,
                     184,
                     81,
                     223,
                     166,
                     171,
                     64,
                     133,
                     166,
                     160,
                     27,
                     147,
                     187,
                     160,
                     64,
                     130,
                     187,
                     162,
                     27,
                     131,
                     181,
                     183,
                     27,
                     131,
                     181,
                     183,
                     85,
                     156,
                     184,
                     225,
                     88,
                     153,
                     167,
                     186
                  },
                  new int[]{240, 212, 206, 52}
               )
            );
      }
   }

   private void t2oEb(IArgConsumer args) throws CommandException {
      args.requireMin(2);
      String name = args.getString();
      String ip = args.getString();
      BotSessionManager.connect(name, ip);
      this.logDirect("Подключение выполнено: " + name + " -> " + ip + " (Предыдущая сессия заморожена)");
   }

   private void hfdfB(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String name = args.getString();
      if (BotSessionManager.remove(name)) {
         this.logDirect("Бот отключен и удален: " + name);
      } else {
         this.logDirect("Бот не найден: " + name);
      }
   }

   private void zdU5H(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String name = args.getString();
      if (BotSessionManager.control(name)) {
         this.logDirect("Переключаюсь на бота: " + name);
      } else {
         this.logDirect("Бот не найден: " + name);
      }
   }

   private void ibi6sgo(IArgConsumer args) throws CommandException {
      args.requireMin(2);
      String name = args.getString();
      StringBuilder message = new StringBuilder();

      while (args.hasAny()) {
         message.append(args.getString()).append(" ");
      }

      if (BotSessionManager.say(name, message.toString().trim())) {
         this.logDirect("Сообщение от лица " + name + " отправлено.");
      } else {
         this.logDirect("Бот не найден: " + name);
      }
   }

   private void gGp2cIs(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      StringBuilder message = new StringBuilder();

      while (args.hasAny()) {
         message.append(args.getString()).append(" ");
      }

      BotSessionManager.sayAll(message.toString().trim());
      this.logDirect(
         D.k(
            new int[]{
               1042,
               1181,
               1024,
               1068,
               1146,
               1174,
               1027,
               1061,
               1030,
               131,
               1024,
               1119,
               1036,
               1251,
               1038,
               1071,
               1032,
               1174,
               1027,
               1059,
               19,
               1181,
               1148,
               61,
               1025,
               1250,
               1035,
               1112,
               19,
               1170,
               1024,
               1119,
               1037,
               1169,
               16
            },
            new int[]{51, 163, 62, 29}
         )
      );
   }

   private void mcufq27() {
      if (BotSessionManager.restore()) {
         this.logDirect(
            D.k(
               new int[]{
                  1069, 1221, 1174, 1147, 1151, 1227, 1256, 1145, 1137, 219, 1182, 1033, 1025, 1203, 1178, 1034, 1137, 219, 1248, 1148, 1150, 1210, 1177, 1031
               },
               new int[]{63, 251, 161, 73}
            )
         );
      } else {
         this.logDirect(
            D.k(
               new int[]{
                  1155,
                  1152,
                  1074,
                  25,
                  1247,
                  1163,
                  1077,
                  1145,
                  1198,
                  1160,
                  1093,
                  1028,
                  1187,
                  1163,
                  1097,
                  25,
                  1247,
                  1152,
                  1073,
                  1144,
                  1190,
                  1165,
                  80,
                  1037,
                  1189,
                  1274,
                  80,
                  1035,
                  1184,
                  1154,
                  1090,
                  1145,
                  1198,
                  1271,
                  1088
               },
               new int[]{158, 181, 112, 57}
            )
         );
      }
   }

   private void v5x1() {
      List<BotSessionManager.BotConnection> connections = BotSessionManager.getConnections();
      if (connections.isEmpty()) {
         this.logDirect(
            "Список ботов пуст"
         );
      } else {
         this.logDirect(
            "Подключенные боты:"
         );

         for (BotSessionManager.BotConnection connection : connections) {
            this.logDirect("- " + connection.name() + " @ " + connection.address());
         }
      }
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
      if (args.hasExactlyOne()) {
         return new TabCompleteHelper().prepend("connect", "remove", "return", "control", "say", "sayall", "list").filterPrefix(args.getString()).stream();
      } else {
         if (args.hasExactly(2)) {
            String action = args.peekString(0).toLowerCase(Locale.ROOT);
            if (List.of("remove", "control", "say").contains(action)) {
               String searchName = args.peekString(1).toLowerCase(Locale.ROOT);
               return BotSessionManager.getConnections()
                  .stream()
                  .map(BotSessionManager.BotConnection::name)
                  .filter(n -> n.toLowerCase().startsWith(searchName));
            }

            if (action.equals("connect")) {
               return Stream.of("<ник_бота>");
            }

            if (action.equals("sayall")) {
               return Stream.of("<сообщение>");
            }
         }

         return args.hasExactly(3) && args.peekString(0).equalsIgnoreCase("connect")
            ? Stream.of("<айпи_сервера>")
            : Stream.empty();
      }
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{1225, 1271, 1188, 1174, 1240, 1267, 1233, 1179, 1234, 1277, 196, 1175, 1236, 1162, 201, 1255, 1247, 1161, 1189, 1182, 1189, 1268, 1244},
         new int[]{234, 200, 228, 166}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return List.of(
         D.k(
            new int[]{
               1080,
               1126,
               1079,
               1217,
               1065,
               1122,
               1090,
               1228,
               1059,
               1132,
               87,
               1216,
               1061,
               1051,
               90,
               1200,
               1070,
               1048,
               1078,
               1225,
               1108,
               1125,
               1103,
               209,
               1059,
               121,
               1094,
               1210,
               1114,
               1051,
               1079,
               1210,
               1063,
               121,
               1096,
               1220,
               1115,
               1132,
               1101,
               1226,
               1109,
               1054,
               1090,
               1228,
               1059,
               1132,
               1099,
               223
            },
            new int[]{27, 89, 119, 241}
         ),
         "",
         "Использование:",
         D.k(
            new int[]{168, 223, 111, 186, 226, 223, 110, 186, 248, 145, 104, 182, 226, 223, 49, 187, 247, 146, 104, 235, 182, 195, 100, 165, 168},
            new int[]{150, 255, 13, 213}
         ),
         "> bot control <name>",
         D.k(
            new int[]{99, 207, 203, 220, 41, 207, 218, 210, 36, 207, 149, 221, 60, 130, 204, 141, 125, 211, 196, 214, 46, 156, 200, 212, 56, 209},
            new int[]{93, 239, 169, 179}
         ),
         "> bot sayall <message>",
         "> bot remove <name>",
         "> bot return",
         "> bot list"
      );
   }
}
