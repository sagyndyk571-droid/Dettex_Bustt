package zov.viola.util.commands.api.datatypes;

import java.util.List;
import java.util.stream.Stream;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;

public enum ModuleDataType implements IDatatypeFor<Module> {
   INSTANCE;

   @Override
   public Stream<String> tabComplete(IDatatypeContext datatypeContext) throws CommandException {
      Stream<String> source = this.mpjwAJo().stream().map(Module::getName);
      String context = datatypeContext.getConsumer().getString();
      return new TabCompleteHelper().append(source).filterPrefix(context).sortAlphabetically().stream();
   }

   public Module get(IDatatypeContext datatypeContext) throws CommandException {
      String name = datatypeContext.getConsumer().getString();
      return this.mpjwAJo().stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
   }

   private List<? extends Module> mpjwAJo() {
      return Viola.getInstance().getModuleStorage().getModules();
   }
}
