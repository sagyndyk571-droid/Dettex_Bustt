package zov.viola.util.commands.manager;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.util.Pair;
import zov.viola.Viola;
import zov.viola.obf.D;
import zov.viola.util.commands.api.ICommand;
import zov.viola.util.commands.api.argument.ICommandArgument;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.exception.CommandUnhandledException;
import zov.viola.util.commands.api.exception.ICommandException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.commands.api.manager.ICommandManager;
import zov.viola.util.commands.api.registry.Registry;
import zov.viola.util.commands.argument.ArgConsumer;
import zov.viola.util.commands.argument.CommandArguments;
import zov.viola.util.commands.defaults.DefaultCommands;

public class CommandRepository implements ICommandManager {
   private final Registry<ICommand> qghC8n5 = new Registry<>();

   public CommandRepository() {
      DefaultCommands.createAll().forEach(this.qghC8n5::register);
   }

   @Override
   public Registry<ICommand> getRegistry() {
      return this.qghC8n5;
   }

   @Override
   public ICommand getCommand(String name) {
      for (ICommand command : this.qghC8n5.entries) {
         if (command.getNames().contains(name.toLowerCase(Locale.US))) {
            return command;
         }
      }

      return null;
   }

   @Override
   public boolean execute(String string) {
      return this.execute(expand(string));
   }

   @Override
   public boolean execute(Pair<String, List<ICommandArgument>> expanded) {
      CommandRepository.ExecutionWrapper execution = this.gwDT9sw(expanded);
      if (execution != null) {
         execution.q6WFAp2();
      }

      return execution != null;
   }

   @Override
   public Stream<String> tabComplete(Pair<String, List<ICommandArgument>> expanded) {
      CommandRepository.ExecutionWrapper execution = this.gwDT9sw(expanded);
      return execution == null ? Stream.empty() : execution.rpv14();
   }

   @Override
   public Stream<String> tabComplete(String prefix) {
      Pair<String, List<ICommandArgument>> pair = tWd6E(prefix, true);
      String label = pair.getLeft();
      List<ICommandArgument> args = pair.getRight();
      return args.isEmpty()
         ? new TabCompleteHelper().addCommands(Viola.getInstance().getCommandRepository()).filterPrefix(label).stream()
         : this.tabComplete(pair);
   }

   private CommandRepository.ExecutionWrapper gwDT9sw(Pair<String, List<ICommandArgument>> expanded) {
      String label = expanded.getLeft();
      ArgConsumer args = new ArgConsumer(this, expanded.getRight());
      ICommand command = this.getCommand(label);
      return command == null ? null : new CommandRepository.ExecutionWrapper(command, label, args);
   }

   private static Pair<String, List<ICommandArgument>> tWd6E(String string, boolean preserveEmptyLast) {
      String label = string.split("\\s", 2)[0];
      List<ICommandArgument> args = CommandArguments.from(string.substring(label.length()), preserveEmptyLast);
      return new Pair<>(label, args);
   }

   public static Pair<String, List<ICommandArgument>> expand(String string) {
      return tWd6E(string, false);
   }

   private static final class ExecutionWrapper {
      private ICommand c2c8;
      private String sDbf8A;
      private ArgConsumer wI02;

      private ExecutionWrapper(ICommand command, String label, ArgConsumer args) {
         this.c2c8 = command;
         this.sDbf8A = label;
         this.wI02 = args;
      }

      private void q6WFAp2() {
         try {
            this.c2c8.execute(this.sDbf8A, this.wI02);
         } catch (Throwable var3) {
            ICommandException exception = (ICommandException)(var3 instanceof ICommandException ? (ICommandException)var3 : new CommandUnhandledException(var3));
            exception.handle(this.c2c8, this.wI02.getArgs());
         }
      }

      private Stream<String> rpv14() {
         try {
            return this.c2c8.tabComplete(this.sDbf8A, this.wI02);
         } catch (CommandException var2) {
         } catch (Throwable var3) {
            var3.printStackTrace();
         }

         return Stream.empty();
      }
   }
}
