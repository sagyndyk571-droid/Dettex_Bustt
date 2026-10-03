package zov.viola.util.commands.api.exception;

import java.util.List;
import zov.viola.obf.D;
import zov.viola.util.QuickLogger;
import zov.viola.util.commands.api.ICommand;
import zov.viola.util.commands.api.argument.ICommandArgument;

public class CommandNotFoundException extends CommandException implements QuickLogger {
   public final String command;

   public CommandNotFoundException(String command) {
      super(
         String.format(
            D.k(
               new int[]{1129, 1132, 1275, 1279, 1102, 1126, 1271, 239, 1102, 1127, 231, 1266, 1091, 1131, 1267, 1274, 1102, 1122, 253, 239, 86, 33},
               new int[]{115, 82, 199, 207}
            ),
            command
         )
      );
      this.command = command;
   }

   @Override
   public void handle(ICommand command, List<ICommandArgument> args) {
      this.logDirect(this.getMessage());
   }
}
