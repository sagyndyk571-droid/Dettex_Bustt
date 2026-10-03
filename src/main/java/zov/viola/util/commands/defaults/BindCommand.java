package zov.viola.util.commands.defaults;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.module.ModuleStorage;
import zov.viola.obf.D;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.IBaritoneChatControl;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.argument.ICommandArgument;
import zov.viola.util.commands.api.datatypes.KeyDataType;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.Paginator;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.keyboard.KeyStorage;

public class BindCommand extends Command {
   private final ModuleStorage ptglth;

   public BindCommand(Viola onetap) {
      super("bind");
      this.ptglth = onetap.getModuleStorage();
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      String action = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
      switch (action) {
         case "add":
            this.tmPMual(label, args);
            break;
         case "remove":
            this.y6jGx3(args);
            break;
         case "list":
            this.gimn2(args, label);
            break;
         case "clear":
            this.wndIq(args);
            break;
         default:
            this.logDirect(
               D.k(
                  new int[]{
                     1190,
                     1172,
                     1081,
                     1052,
                     1161,
                     1172,
                     1088,
                     1129,
                     1158,
                     1169,
                     1102,
                     11,
                     1156,
                     1183,
                     1077,
                     1041,
                     1157,
                     1181,
                     1073,
                     1046,
                     1167,
                     1169,
                     47,
                     11,
                     1187,
                     1248,
                     1086,
                     1045,
                     1152,
                     1261,
                     1078,
                     1128,
                     1154,
                     129,
                     96,
                     79,
                     223,
                     142,
                     115,
                     78,
                     214,
                     206,
                     119,
                     78,
                     148,
                     205,
                     104,
                     88,
                     207,
                     142,
                     98,
                     71,
                     222,
                     192,
                     115,
                     5
                  },
                  new int[]{187, 161, 1, 43}
               ),
               Formatting.GRAY
            );
      }
   }

   private void tmPMual(String label, IArgConsumer args) throws CommandException {
      args.requireMin(2);
      String moduleName = args.getString();
      Module module = this.ptglth.get(moduleName);
      if (module == null) {
         this.logDirect(Formatting.GRAY + "Модуль с названием " + Formatting.WHITE + moduleName + Formatting.GRAY + " не найден");
      } else {
         int key = args.<Entry<String, Integer>, KeyDataType>getDatatypeFor(KeyDataType.INSTANCE).getValue();
         module.setKey(key);
         this.logDirect(
            Formatting.GRAY
               + "Модуль "
               + Formatting.WHITE
               + module.getName()
               + Formatting.GRAY
               + " успешно привязан к клавише "
               + Formatting.WHITE
               + KeyStorage.getKey(key).toUpperCase()
         );
      }
   }

   private void y6jGx3(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      String moduleName = args.getString();
      Module module = this.ptglth.get(moduleName);
      if (module == null) {
         this.logDirect(Formatting.GRAY + "Модуль с названием " + Formatting.WHITE + moduleName + Formatting.GRAY + " не найден");
      } else {
         module.setKey(-1);
         this.logDirect(Formatting.GRAY + "Модуль " + Formatting.WHITE + module.getName() + Formatting.GRAY + " больше не имеет привязку к клавише");
      }
   }

   private void gimn2(IArgConsumer args, String label) throws CommandException {
      args.requireMax(1);
      List<Module> boundModules = this.ptglth.getModules().stream().filter(module -> module.getKey() != -1).collect(Collectors.toList());
      Paginator.paginate(
         args,
         new Paginator<>(boundModules),
         () -> this.logDirect(
            D.k(
               new int[]{1151, 1080, 1194, 1144, 1071, 1103, 1186, 1143, 1117, 1075, 1191, 106, 1116, 1094, 1190, 1033, 1115, 1088, 168},
               new int[]{96, 120, 146, 74}
            )
         ),
         module -> {
            int key = module.getKey();
            String keyName = KeyStorage.getKey(key).toUpperCase();
            return Text.literal(Formatting.GRAY + module.getName() + ": " + Formatting.WHITE + keyName);
         },
         IBaritoneChatControl.FORCE_COMMAND_PREFIX + label
      );
   }

   private void wndIq(IArgConsumer args) throws CommandException {
      args.requireMax(1);

      for (Module m : this.ptglth.getModules()) {
         m.setKey(-1);
      }

      this.logDirect(Formatting.GRAY + "Все модули успешно отвязаны и больше не имеют привязку к клавише");
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
      List<ICommandArgument> raw = args.getArgs();
      int size = raw.size();
      if (size <= 1) {
         String prefix = size == 1 ? args.peekString(0) : "";
         return new TabCompleteHelper().prepend("add", "remove", "list", "clear").filterPrefix(prefix).sortAlphabetically().stream();
      } else {
         String action = args.peekString(0).toLowerCase(Locale.ROOT);
         if (action.equals("add") && size == 2) {
            String modulePrefix = args.peekString(1);
            return this.ptglth
               .getModules()
               .stream()
               .map(Module::getName)
               .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(modulePrefix.toLowerCase(Locale.ROOT)))
               .sorted()
               .distinct();
         } else if (action.equals("add") && size == 3) {
            String keyPrefix = args.peekString(2);
            return KeyStorage.keyMap
               .keySet()
               .stream()
               .filter(k -> k.toLowerCase(Locale.ROOT).startsWith(keyPrefix.toLowerCase(Locale.ROOT)))
               .sorted()
               .distinct();
         } else if (action.equals("remove") && size == 2) {
            String modulePrefix = args.peekString(1);
            return this.ptglth
               .getModules()
               .stream()
               .filter(m -> m.getKey() != -1)
               .map(Module::getName)
               .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(modulePrefix.toLowerCase(Locale.ROOT)))
               .sorted()
               .distinct();
         } else {
            return Stream.empty();
         }
      }
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{
            1024,
            1100,
            1132,
            1227,
            1041,
            1096,
            1049,
            1222,
            1051,
            1094,
            12,
            1220,
            1123,
            1099,
            1054,
            1204,
            1044,
            1097,
            1042,
            1218,
            3,
            1103,
            1042,
            1231,
            1120,
            1096,
            1123,
            219,
            1049,
            83,
            1046,
            1216,
            1043,
            1089,
            1044,
            1203,
            1046
         },
         new int[]{35, 115, 44, 251}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         D.k(
            new int[]{
               1068,
               1160,
               1042,
               1210,
               1035,
               1154,
               1054,
               170,
               1026,
               1165,
               1121,
               170,
               1141,
               1271,
               1132,
               1210,
               1035,
               1160,
               1052,
               1200,
               1038,
               150,
               1046,
               170,
               1141,
               1154,
               1054,
               1201,
               1027,
               1163,
               1046,
               1221,
               22,
               1161,
               1134,
               1202,
               1028,
               1273,
               1049,
               1200,
               1038,
               150,
               1042,
               1204,
               1026,
               1269,
               1045,
               1221,
               22,
               1164,
               14,
               1200,
               1037,
               1158,
               1052,
               1202,
               1150,
               1155
            },
            new int[]{54, 182, 46, 138}
         ),
         "",
         "Использование:",
         D.k(
            new int[]{
               0,
               224,
               30,
               241,
               80,
               164,
               92,
               249,
               90,
               164,
               92,
               164,
               1026,
               1278,
               1096,
               1243,
               1029,
               1164,
               66,
               184,
               2,
               1274,
               1095,
               1192,
               1036,
               1272,
               1076,
               1192,
               0,
               224,
               8296,
               184,
               1025,
               1152,
               1092,
               1194,
               1137,
               1271,
               1100,
               1242,
               1138,
               224,
               1088,
               1190,
               1034,
               1155,
               1095,
               1236,
               30,
               1274,
               92,
               1186,
               1029,
               1264,
               1102,
               1184,
               1142,
               1269
            },
            new int[]{62, 192, 124, 152}
         ),
         D.k(
            new int[]{
               229,
               102,
               69,
               147,
               181,
               34,
               7,
               136,
               190,
               43,
               72,
               140,
               190,
               102,
               27,
               1222,
               1253,
               1138,
               1124,
               1217,
               1175,
               120,
               7,
               8430,
               251,
               1144,
               1125,
               1224,
               1172,
               1137,
               1047,
               1208,
               1175,
               102,
               1051,
               1220,
               1263,
               1029,
               1052,
               1206,
               251,
               1144,
               1125,
               218,
               1249,
               1149,
               1047,
               1224,
               1251,
               1038,
               1055
            },
            new int[]{219, 70, 39, 250}
         ),
         D.k(
            new int[]{
               126,
               85,
               1,
               208,
               46,
               17,
               67,
               213,
               41,
               6,
               23,
               153,
               8276,
               85,
               1116,
               1159,
               1146,
               1093,
               1108,
               1161,
               1026,
               1081,
               67,
               1163,
               1025,
               1088,
               67,
               1158,
               1024,
               1101,
               1105,
               1270,
               1143,
               1093,
               1118,
               1156,
               1035,
               1088,
               67,
               1157,
               1150,
               1089,
               1056,
               1154,
               1144
            },
            new int[]{64, 117, 99, 185}
         ),
         D.k(
            new int[]{
               31,
               93,
               182,
               171,
               79,
               25,
               244,
               161,
               77,
               24,
               181,
               176,
               1,
               8297,
               244,
               1276,
               1123,
               1103,
               1179,
               1269,
               1041,
               1087,
               1176,
               226,
               1043,
               1084,
               1249,
               226,
               1053,
               1091,
               1248,
               1153,
               1050,
               1093,
               244,
               1276,
               1123,
               93,
               1173,
               1264,
               1055,
               1093,
               1169,
               226,
               1051,
               1094,
               1252,
               1264,
               1049,
               1077
            },
            new int[]{33, 125, 212, 194}
         )
      );
   }
}
