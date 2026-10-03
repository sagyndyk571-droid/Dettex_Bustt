package zov.viola.util.commands.defaults;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;
import zov.viola.obf.D;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.IBaritoneChatControl;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.exception.CommandNotEnoughArgumentsException;
import zov.viola.util.commands.api.helpers.Paginator;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.config.ConfigManager;
import zov.viola.util.license.LicenseManager;

public class CfgCommand extends Command {
   public CfgCommand() {
      super("cfg");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      String action = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
      switch (action) {
         case "save":
            this.tFanVMz(args);
            break;
         case "load":
            this.lMray3x(args);
            break;
         case "list":
            this.ryqH(args, label);
            break;
         case "clear":
            for (String name : ConfigManager.getConfigs()) {
               Path file = LicenseManager.dataDir().resolve("configs").resolve(name + ".json");
               if (Files.exists(file)) {
                  try {
                     Files.delete(file);
                  } catch (IOException var12) {
                     this.logDirect(Formatting.GRAY + "Ошибка при удалении файла.");
                     var12.printStackTrace();
                  }
               }
            }

            this.logDirect(
               D.k(
                  new int[]{1261, 1178, 1070, 1047, 1266, 1183, 54, 1132, 1266, 1176, 1106, 1134, 1279, 1179, 1060, 118, 1266, 1250, 1070, 1055, 1273, 1176},
                  new int[]{204, 165, 22, 86}
               ),
               Formatting.GRAY
            );
            break;
         case "dir":
            try {
               File dir = LicenseManager.dataDir().resolve("configs").toFile();
               if (!dir.exists()) {
                  this.logDirect(Formatting.GRAY + "Зач папку удалил?");
                  dir.mkdirs();
               } else {
                  this.logDirect(Formatting.GRAY + "Открываю папку с конфигами...");
               }

               Runtime.getRuntime().exec("explorer " + dir.getAbsolutePath());
            } catch (IOException var11) {
               this.logDirect(Formatting.GRAY + "Ошибка при открытии папки: " + Formatting.WHITE + var11.getMessage());
            }
            break;
         case "remove":
            this.ina5NWv(args);
            break;
         default:
            this.logDirect(
               D.k(
                  new int[]{
                     1102,
                     1138,
                     1181,
                     1206,
                     1121,
                     1138,
                     1252,
                     1219,
                     1134,
                     1143,
                     1258,
                     161,
                     1132,
                     1145,
                     1169,
                     1211,
                     1133,
                     1147,
                     1173,
                     1212,
                     1127,
                     1143,
                     139,
                     161,
                     1099,
                     1030,
                     1178,
                     1215,
                     1128,
                     1035,
                     1170,
                     1218,
                     1130,
                     103,
                     201,
                     238,
                     50,
                     35,
                     138,
                     242,
                     50,
                     49,
                     192,
                     174,
                     33,
                     34,
                     200,
                     238,
                     37,
                     34,
                     138,
                     237,
                     58,
                     52,
                     209,
                     174,
                     55,
                     46,
                     215,
                     175
                  },
                  new int[]{83, 71, 165, 129}
               ),
               Formatting.GRAY
            );
      }
   }

   private void tFanVMz(IArgConsumer args) throws CommandException {
      args.requireExactly(1);
      String name = args.getString();
      ConfigManager.save(name);
      this.logDirect(Formatting.GRAY + "Конфиг с именем " + Formatting.WHITE + name + Formatting.GRAY + " успешно сохранён");
   }

   private void lMray3x(IArgConsumer args) throws CommandException {
      args.requireExactly(1);
      String name = args.getString();
      if (!ConfigManager.getConfigs().contains(name)) {
         this.logDirect(Formatting.GRAY + "Конфиг с таким именем не найден");
      } else {
         ConfigManager.load(name);
         this.logDirect(Formatting.GRAY + "Конфиг с именем " + Formatting.WHITE + name + Formatting.GRAY + " успешно загружен");
      }
   }

   private void ryqH(IArgConsumer args, String label) throws CommandException {
      args.requireMax(1);
      List<String> configs = ConfigManager.getConfigs();
      this.logDirect(
         "Список конфигов:",
         Formatting.GRAY
      );
      Paginator.paginate(
         args,
         new Paginator<>(configs),
         name -> {
            Text nameText = Text.literal(Formatting.GRAY + "- " + Formatting.WHITE + name + " ");
            Text loadText = Text.literal(Formatting.GREEN + "[Загрузить]")
               .styled(
                  style -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, IBaritoneChatControl.FORCE_COMMAND_PREFIX + "cfg load " + name))
                     .withHoverEvent(
                        new HoverEvent(
                           net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                           Text.literal(
                              D.k(
                                 new int[]{178, 153, 41, 20, 154, 213, 52, 24, 209, 153, 47, 22, 149, 213, 35, 24, 159, 147, 41, 16},
                                 new int[]{241, 245, 64, 119}
                              )
                           )
                        )
                     )
               );
            Text deleteText = Text.literal(Formatting.RED + " [Удалить]")
               .styled(
                  style -> style.withClickEvent(new ClickEvent(Action.RUN_COMMAND, IBaritoneChatControl.FORCE_COMMAND_PREFIX + "cfg remove " + name))
                     .withHoverEvent(
                        new HoverEvent(
                           net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                           Text.literal(
                              D.k(
                                 new int[]{44, 50, 157, 145, 4, 126, 128, 157, 79, 58, 145, 158, 10, 42, 145, 210, 12, 49, 154, 148, 6, 57},
                                 new int[]{111, 94, 244, 242}
                              )
                           )
                        )
                     )
               );
            return nameText.copy().append(loadText).append(deleteText);
         },
         IBaritoneChatControl.FORCE_COMMAND_PREFIX + label
      );
   }

   private void ina5NWv(IArgConsumer args) throws CommandException {
      args.requireExactly(1);
      String name = args.getString();
      Path file = LicenseManager.dataDir().resolve("configs").resolve(name + ".json");
      if (Files.exists(file)) {
         try {
            Files.delete(file);
            this.logDirect(Formatting.GRAY + "Конфиг " + Formatting.WHITE + name + Formatting.GRAY + " успешно удалён");
         } catch (IOException var5) {
            this.logDirect(Formatting.GRAY + "Ошибка при удалении файла.");
            var5.printStackTrace();
         }
      } else {
         this.logDirect(Formatting.GRAY + "Конфиг не найден");
      }
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
      if (args.hasAny() && args.hasExactlyOne()) {
         return new TabCompleteHelper().sortAlphabetically().prepend("load", "save", "remove", "list", "clear", "dir").filterPrefix(args.getString()).stream();
      } else {
         if (args.hasAny()) {
            String arg = args.getString();
            if (args.hasExactlyOne() && (arg.equalsIgnoreCase("load") || arg.equalsIgnoreCase("remove"))) {
               return ConfigManager.getConfigs().stream().filter(cfg -> {
                  try {
                     return cfg.startsWith(args.peekString());
                  } catch (CommandNotEnoughArgumentsException var3x) {
                     throw new RuntimeException(var3x);
                  }
               }).sorted();
            }
         }

         return Stream.empty();
      }
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{1164, 1076, 1112, 1147, 1181, 1072, 1069, 1142, 1175, 1086, 56, 1137, 1169, 1078, 1116, 1139, 1180, 1083, 1060, 1139},
         new int[]{175, 11, 24, 75}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         D.k(
            new int[]{
               1247,
               1232,
               1188,
               1028,
               1272,
               1242,
               1192,
               20,
               1265,
               1237,
               1239,
               20,
               1158,
               1233,
               1240,
               1028,
               1271,
               1237,
               1197,
               1033,
               1277,
               1185,
               184,
               1038,
               1275,
               1235,
               1244,
               1036,
               1270,
               1197,
               1240,
               1028,
               1155,
               1238,
               1239,
               1032,
               1277,
               206,
               1186,
               1039,
               1277,
               1243,
               1189,
               1142,
               1269,
               192
            },
            new int[]{197, 238, 152, 52}
         ),
         "",
         "Использование:",
         D.k(
            new int[]{
               156,
               129,
               170,
               52,
               197,
               129,
               186,
               51,
               212,
               196,
               233,
               110,
               204,
               192,
               164,
               55,
               156,
               129,
               228,
               114,
               1155,
               1183,
               1164,
               1042,
               1170,
               1180,
               1158,
               1127,
               1248,
               129,
               1163,
               1127,
               1176,
               1250,
               1152,
               1041,
               1260,
               129,
               1267,
               1132,
               1183,
               1253,
               1265,
               1121,
               1249,
               1249,
               1273,
               1044,
               1178,
               1263,
               231
            },
            new int[]{162, 161, 201, 82}
         ),
         D.k(
            new int[]{
               224,
               70,
               146,
               231,
               185,
               70,
               157,
               238,
               191,
               2,
               209,
               189,
               176,
               7,
               156,
               228,
               224,
               70,
               220,
               161,
               1225,
               1110,
               1218,
               1217,
               1181,
               1104,
               1217,
               1204,
               1180,
               70,
               1227,
               1215,
               1251,
               1058,
               1225,
               1202,
               1181,
               1062,
               1217,
               1223,
               1254,
               1064,
               223
            },
            new int[]{222, 102, 241, 129}
         ),
         D.k(
            new int[]{
               19,
               119,
               105,
               222,
               74,
               119,
               102,
               209,
               94,
               35,
               42,
               149,
               13,
               1096,
               1076,
               1154,
               1053,
               1120,
               1089,
               1162,
               1053,
               1122,
               1096,
               152,
               1055,
               1046,
               1087,
               152,
               1049,
               1129,
               1099,
               1274,
               1134,
               1128,
               1079,
               1267,
               1048,
               119,
               1072,
               1158,
               1040,
               1043,
               1074,
               1163,
               1045,
               121
            },
            new int[]{45, 87, 10, 184}
         ),
         D.k(
            new int[]{
               222,
               141,
               238,
               115,
               135,
               141,
               255,
               112,
               141,
               194,
               251,
               112,
               192,
               145,
               227,
               116,
               141,
               200,
               179,
               53,
               205,
               141,
               1198,
               1057,
               1232,
               1174,
               1218,
               1056,
               1186,
               141,
               1207,
               1067,
               1245,
               1257,
               1205,
               1062,
               192,
               1170,
               1203,
               53,
               1240,
               1169,
               1208,
               1064,
               1240,
               131
            },
            new int[]{224, 173, 141, 21}
         )
      );
   }
}
