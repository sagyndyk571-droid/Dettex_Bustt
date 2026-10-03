package zov.viola.util.commands;

import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.Pair;
import zov.viola.Viola;
import zov.viola.event.list.ChatEvent;
import zov.viola.event.list.TabCompleteEvent;
import zov.viola.util.commands.api.IBaritoneChatControl;
import zov.viola.util.commands.api.argument.ICommandArgument;
import zov.viola.util.commands.api.exception.CommandNotEnoughArgumentsException;
import zov.viola.util.commands.api.exception.CommandNotFoundException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.commands.api.manager.ICommandManager;
import zov.viola.util.commands.argument.ArgConsumer;
import zov.viola.util.commands.argument.CommandArguments;
import zov.viola.util.commands.manager.CommandRepository;

public class CommandDispatcher {
   private final ICommandManager z9kR = Viola.getInstance().getCommandRepository();

   public CommandDispatcher() {
      Viola.getInstance().getEventBus().register(this);
   }

   @Subscribe
   public void onSendChatMessage(ChatEvent event) {
      String msg = event.getMessage();
      String prefix = ".";
      boolean forceRun = msg.startsWith(IBaritoneChatControl.FORCE_COMMAND_PREFIX);
      if (msg.startsWith(prefix) || forceRun) {
         event.setCancelled(true);
         String commandStr = msg.substring(forceRun ? IBaritoneChatControl.FORCE_COMMAND_PREFIX.length() : prefix.length());
         if (!this.runCommand(commandStr) && !commandStr.trim().isEmpty()) {
            new CommandNotFoundException(CommandRepository.expand(commandStr).getLeft()).handle(null, null);
         }
      }
   }

   public boolean runCommand(String msg) {
      if (msg.isEmpty()) {
         return this.runCommand("help");
      } else {
         Pair<String, List<ICommandArgument>> pair = CommandRepository.expand(msg);
         return this.z9kR.execute(pair);
      }
   }

   @Subscribe
   public void onPreTabComplete(TabCompleteEvent event) {
      String prefix = event.getPrefix();
      String commandPrefix = ".";
      if (prefix.startsWith(commandPrefix)) {
         String msg = prefix.substring(commandPrefix.length());
         List<ICommandArgument> args = CommandArguments.from(msg, true);
         Stream<String> stream = this.tabComplete(msg);
         if (args.size() == 1) {
            stream = stream.map(x -> commandPrefix + x);
         }

         event.completions = stream.toArray(String[]::new);
      }
   }

   public Stream<String> tabComplete(String msg) {
      try {
         List<ICommandArgument> args = CommandArguments.from(msg, true);
         ArgConsumer argc = new ArgConsumer(this.z9kR, args);
         return argc.hasAtMost(2) && argc.hasExactly(1)
            ? new TabCompleteHelper().addCommands(this.z9kR).filterPrefix(argc.getString()).stream()
            : this.z9kR.tabComplete(msg);
      } catch (CommandNotEnoughArgumentsException var4) {
         return Stream.empty();
      }
   }
}
