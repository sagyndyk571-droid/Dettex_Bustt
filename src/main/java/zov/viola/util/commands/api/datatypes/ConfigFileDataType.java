package zov.viola.util.commands.api.datatypes;

import java.util.List;
import java.util.stream.Stream;
import zov.viola.Viola;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.config.ConfigManager;

public enum ConfigFileDataType implements IDatatypeFor<String> {
   INSTANCE;

   @Override
   public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
      Stream<String> friends = this.getConfigs().stream().map(String::toString);
      String context = ctx.getConsumer().getString();
      return new TabCompleteHelper().append(friends).filterPrefix(context).sortAlphabetically().stream();
   }

   public String get(IDatatypeContext datatypeContext) throws CommandException {
      String username = datatypeContext.getConsumer().getString();
      return this.getConfigs().stream().filter(s -> s.equalsIgnoreCase(username)).findFirst().orElse(null);
   }

   public List<String> getConfigs() {
      Viola.getInstance().getConfigManager();
      return ConfigManager.getConfigs();
   }
}
