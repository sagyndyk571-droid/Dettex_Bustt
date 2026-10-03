package zov.viola.util.commands.defaults;

import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zov.viola.Viola;
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
import zov.viola.util.macro.MacroRepository;

public class MacroCommand extends Command {
   private final MacroRepository jy6eA;

   public MacroCommand(Viola onetap) {
      super("macro");
      this.jy6eA = onetap.getMacroRepository();
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      String action = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
      switch (action) {
         case "add":
            this.wJS3k9r(args);
            break;
         case "remove":
            this.iXJPmZ(args);
            break;
         case "list":
            this.lPkqT(args, label);
            break;
         case "clear":
            this.nkwh3Z(args);
            break;
         default:
            this.logDirect(
               D.k(
                  new int[]{
                     1110,
                     1258,
                     1120,
                     1064,
                     1141,
                     1255,
                     1128,
                     1109,
                     1143,
                     145,
                     127,
                     119,
                     42,
                     207,
                     127,
                     57,
                     110,
                     217,
                     58,
                     123,
                     33,
                     221,
                     58,
                     54,
                     97,
                     139,
                     51,
                     127,
                     61,
                     223,
                     127,
                     57,
                     110,
                     200,
                     51,
                     115,
                     47,
                     217
                  },
                  new int[]{78, 171, 95, 22}
               ),
               Formatting.GRAY
            );
      }
   }

   private void wJS3k9r(IArgConsumer args) throws CommandException {
      args.requireMin(2);
      int key = args.<Entry<String, Integer>, KeyDataType>getDatatypeFor(KeyDataType.INSTANCE).getValue();
      String command = args.rawRest();
      this.jy6eA.addMacro(command, key);
      this.logDirect(
         Formatting.GRAY + "Макрос добавлен: " + Formatting.WHITE + KeyStorage.getKey(key).toLowerCase() + Formatting.GRAY + " → " + Formatting.WHITE + command
      );
   }

   private void iXJPmZ(IArgConsumer args) throws CommandException {
      args.requireMin(1);
      int key = args.<Entry<String, Integer>, KeyDataType>getDatatypeFor(KeyDataType.INSTANCE).getValue();
      if (this.jy6eA.removeByKey(key)) {
         this.logDirect(Formatting.GRAY + "Макрос на клавише " + Formatting.WHITE + KeyStorage.getKey(key).toLowerCase() + Formatting.GRAY + " удалён");
      } else {
         this.logDirect(
            D.k(
               new int[]{
                  1123,
                  1042,
                  1053,
                  1277,
                  1089,
                  1123,
                  7,
                  1152,
                  1103,
                  2,
                  1130,
                  1279,
                  1089,
                  1051,
                  7,
                  1159,
                  1092,
                  1042,
                  1045,
                  1157,
                  1079,
                  1047,
                  7,
                  1152,
                  1098,
                  2,
                  1050,
                  1165,
                  1094,
                  1046,
                  1042,
                  1152
               },
               new int[]{127, 34, 39, 189}
            ),
            Formatting.GRAY
         );
      }
   }

   private void lPkqT(IArgConsumer args, String label) throws CommandException {
      Paginator.paginate(
         args,
         new Paginator<>(this.jy6eA.getMacroList()),
         () -> this.logDirect(
            "Список макросов:"
         ),
         macro -> Text.literal(
            Formatting.GRAY + " " + KeyStorage.getKey(macro.key()).toLowerCase() + Formatting.DARK_GRAY + " → " + Formatting.WHITE + macro.message()
         ),
         IBaritoneChatControl.FORCE_COMMAND_PREFIX + label
      );
   }

   private void nkwh3Z(IArgConsumer args) {
      this.jy6eA.clearList();
      this.logDirect(
         D.k(
            new int[]{1265, 1245, 1219, 206, 1247, 1196, 1228, 1198, 1245, 1245, 1213, 206, 1245, 1243, 1230, 1191, 1238, 1185, 1213},
            new int[]{227, 156, 246, 238}
         ),
         Formatting.GRAY
      );
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
      List<ICommandArgument> raw = args.getArgs();
      int size = raw.size();
      if (args.hasExactlyOne()) {
         return new TabCompleteHelper().prepend("add", "remove", "list", "clear").filterPrefix(args.getString()).sortAlphabetically().stream();
      } else {
         if (args.hasAny()) {
            String action = args.getString();
            if ((action.equalsIgnoreCase("add") || action.equalsIgnoreCase("remove")) && size == 2) {
               return args.tabCompleteDatatype(KeyDataType.INSTANCE);
            }
         }

         return Stream.empty();
      }
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{1136, 1257, 1026, 1191, 1106, 1176, 1139, 199, 1107, 1255, 24, 1245, 1111, 1257, 1034, 1247, 1060, 1257, 1028}, new int[]{108, 217, 56, 231}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return List.of(
         "Использование:",
         D.k(
            new int[]{77, 131, 111, 56, 17, 129, 46, 58, 7, 138, 46, 103, 8, 139, 119, 101, 67, 210, 109, 52, 14, 131, 111, 53, 7, 208},
            new int[]{99, 238, 14, 91}
         ),
         ".macro remove <key>",
         ".macro list",
         ".macro clear"
      );
   }
}
