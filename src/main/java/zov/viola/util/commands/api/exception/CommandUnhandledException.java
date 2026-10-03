package zov.viola.util.commands.api.exception;

import java.util.List;
import zov.viola.util.QuickLogger;
import zov.viola.util.commands.api.ICommand;
import zov.viola.util.commands.api.argument.ICommandArgument;

public class CommandUnhandledException extends RuntimeException implements ICommandException, QuickLogger {
   public CommandUnhandledException(String message) {
      super(message);
   }

   public CommandUnhandledException(Throwable cause) {
      super(cause);
   }

   @Override
   public void handle(ICommand command, List<ICommandArgument> args) {
   }
}
