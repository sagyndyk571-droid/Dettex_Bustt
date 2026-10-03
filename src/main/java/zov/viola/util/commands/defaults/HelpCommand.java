package zov.viola.util.commands.defaults;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;
import zov.viola.Viola;
import zov.viola.obf.D;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.IBaritoneChatControl;
import zov.viola.util.commands.api.ICommand;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.exception.CommandNotFoundException;
import zov.viola.util.commands.api.helpers.Paginator;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.commands.manager.CommandRepository;

public class HelpCommand extends Command {
   Viola onetap;

   protected HelpCommand(Viola onetap) {
      super("help");
      this.onetap = onetap;
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      args.requireMax(1);
      CommandRepository commandRepository = this.onetap.getCommandRepository();
      if (args.hasAny() && !args.is(Integer.class)) {
         String commandName = args.getString().toLowerCase();
         ICommand command = commandRepository.getCommand(commandName);
         if (command == null) {
            throw new CommandNotFoundException(commandName);
         }

         this.logDirect("");
         command.getLongDesc().forEach(this::logDirect);
         this.logDirect("");
         MutableText returnComponent = Text.literal(
            D.k(
               new int[]{
                  1215,
                  1026,
                  1228,
                  1070,
                  1178,
                  1136,
                  1231,
                  50,
                  1253,
                  1136,
                  1220,
                  50,
                  1171,
                  1145,
                  218,
                  1056,
                  1175,
                  1138,
                  1223,
                  1105,
                  1248,
                  1150,
                  1211,
                  1117,
                  130,
                  1036,
                  1227,
                  1106,
                  1170,
                  1136,
                  1223,
                  1068,
                  130,
                  1024,
                  218,
                  1070,
                  1175,
                  1039,
                  1204
               },
               new int[]{162, 50, 250, 18}
            )
         );
         returnComponent.setStyle(
            returnComponent.getStyle().withClickEvent(new ClickEvent(Action.RUN_COMMAND, IBaritoneChatControl.FORCE_COMMAND_PREFIX + label))
         );
         this.logDirect(new Text[]{returnComponent});
      } else {
         Paginator.paginate(
            args,
            new Paginator<>(commandRepository.getRegistry().descendingStream().filter(commandx -> !commandx.hiddenFromHelp()).collect(Collectors.toList())),
            () -> this.logDirect(
               D.k(
                  new int[]{
                     1162,
                     1051,
                     1219,
                     1213,
                     1245,
                     1050,
                     1215,
                     1204,
                     1195,
                     5,
                     1208,
                     1217,
                     1186,
                     1045,
                     1215,
                     1227,
                     1237,
                     5,
                     1200,
                     223,
                     1241,
                     1053,
                     1216,
                     1226,
                     164
                  },
                  new int[]{158, 37, 130, 255}
               )
            ),
            commandx -> {
               String names = String.join("/", commandx.getNames());
               String name = commandx.getNames().get(0);
               MutableText shortDescComponent = Text.literal(Formatting.DARK_GRAY + " - " + Formatting.GRAY + commandx.getShortDesc());
               shortDescComponent.setStyle(shortDescComponent.getStyle().withColor(Formatting.GRAY));
               MutableText namesComponent = Text.literal(names);
               namesComponent.setStyle(namesComponent.getStyle().withColor(Formatting.WHITE));
               MutableText hoverComponent = Text.literal("");
               hoverComponent.setStyle(hoverComponent.getStyle().withColor(Formatting.GRAY));
               hoverComponent.append(namesComponent);
               hoverComponent.append("\n" + commandx.getShortDesc());
               hoverComponent.append(
                  D.k(
                     new int[]{
                        235,
                        185,
                        1132,
                        1213,
                        1239,
                        1167,
                        1097,
                        1231,
                        1236,
                        159,
                        81,
                        1226,
                        1187,
                        1165,
                        1088,
                        1222,
                        193,
                        1164,
                        1073,
                        1203,
                        1184,
                        1167,
                        1103,
                        1231,
                        1185,
                        1158,
                        1075,
                        1217,
                        193,
                        1164,
                        1103,
                        1206,
                        1244,
                        1264,
                        1087,
                        173,
                        1184,
                        1164,
                        1073,
                        1213,
                        1235,
                        1161,
                        1074,
                        173,
                        1247,
                        147,
                        1099,
                        1203,
                        1245,
                        1155,
                        1100,
                        1209,
                        1236
                     },
                     new int[]{225, 179, 113, 141}
                  )
               );
               String clickCommand = IBaritoneChatControl.FORCE_COMMAND_PREFIX
                  + String.format("%s %s", label, commandx.getNames().get(0));
               MutableText component = Text.literal(name);
               component.setStyle(component.getStyle().withColor(Formatting.GRAY));
               component.append(shortDescComponent);
               component.setStyle(
                  component.getStyle()
                     .withHoverEvent(new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, hoverComponent))
                     .withClickEvent(new ClickEvent(Action.RUN_COMMAND, clickCommand))
               );
               return component;
            },
            IBaritoneChatControl.FORCE_COMMAND_PREFIX + label
         );
      }
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
      return args.hasExactlyOne()
         ? new TabCompleteHelper().addCommands(Viola.getInstance().getCommandRepository()).filterPrefix(args.getString()).stream()
         : Stream.empty();
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{
            1033,
            1249,
            1269,
            1159,
            1066,
            1183,
            1161,
            1158,
            54,
            1171,
            1162,
            1267,
            1107,
            129,
            1279,
            1272,
            1111,
            1251,
            1160,
            1273,
            1067,
            1258,
            1166,
            230,
            1068,
            1183,
            1271,
            1270,
            1067,
            1173
         },
         new int[]{22, 161, 203, 198}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         D.k(
            new int[]{
               231,
               76,
               208,
               117,
               181,
               28,
               152,
               61,
               249,
               1139,
               1165,
               1104,
               1260,
               1067,
               1152,
               1105,
               1250,
               1059,
               1165,
               1106,
               249,
               1118,
               1273,
               1061,
               249,
               1110,
               1158,
               1068,
               1257,
               1105,
               1164,
               1115,
               249,
               1108,
               152,
               1064,
               1180,
               76,
               1154,
               1104,
               1257,
               1070,
               1154,
               1064,
               1260,
               76,
               1158,
               1071,
               1249,
               1069,
               1160,
               1069,
               1249,
               1059,
               150
            },
            new int[]{217, 108, 184, 16}
         ),
         D.k(
            new int[]{
               166,
               3,
               250,
               106,
               244,
               83,
               178,
               51,
               251,
               76,
               255,
               98,
               249,
               77,
               246,
               49,
               184,
               14,
               178,
               1041,
               1242,
               1053,
               1187,
               1103,
               1192,
               1045,
               1191,
               1074,
               1184,
               1046,
               178,
               1102,
               1191,
               1123,
               1186,
               1085,
               1190,
               1124,
               1199,
               1073,
               1185,
               3,
               1194,
               1074,
               1244,
               1053,
               1234,
               1075,
               1192,
               1125,
               1194,
               1079,
               184,
               1052,
               1196,
               47,
               1186,
               1053,
               1199,
               1077,
               1240,
               1046,
               1232,
               1074,
               1190,
               1050,
               178,
               1077,
               1190,
               1055,
               1186,
               1074,
               1196,
               1046,
               188
            },
            new int[]{152, 35, 146, 15}
         )
      );
   }
}
