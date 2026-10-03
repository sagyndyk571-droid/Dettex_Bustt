package zov.viola.util.commands.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import zov.viola.Viola;
import zov.viola.util.commands.api.ICommand;

public final class DefaultCommands {
   public static List<ICommand> createAll() {
      List<ICommand> commands = new ArrayList<>(
         Arrays.asList(
            new CfgCommand(),
            new RotationCommand(),
            new HelpCommand(Viola.getInstance()),
            new MacroCommand(Viola.getInstance()),
            new BindCommand(Viola.getInstance()),
            new FriendCommand(Viola.getInstance()),
            new StaffCommand(Viola.getInstance()),
            new VClipCommand(),
            new TpCommand(),
            new PartyCommand(),
            new GpsCommand(),
            new AICommand(),
            new BotCommand()
         )
      );
      return Collections.unmodifiableList(commands);
   }
}
