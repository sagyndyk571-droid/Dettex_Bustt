package zov.viola.util.commands.api;

import java.util.List;
import java.util.stream.Stream;
import zov.viola.util.QuickLogger;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;

public interface ICommand extends QuickLogger {
   void execute(String var1, IArgConsumer var2) throws CommandException;

   Stream<String> tabComplete(String var1, IArgConsumer var2) throws CommandException;

   String getShortDesc();

   List<String> getLongDesc();

   List<String> getNames();

   default boolean hiddenFromHelp() {
      return false;
   }
}
