package zov.viola.util.commands.api.datatypes;

import java.util.List;
import java.util.stream.Stream;
import zov.viola.Viola;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.keyboard.KeyStorage;
import zov.viola.util.macro.Macro;

public enum MacroDataType implements IDatatypeFor<Macro> {
   INSTANCE;

   @Override
   public Stream<String> tabComplete(IDatatypeContext datatypeContext) throws CommandException {
      Stream<String> macros = this.jhzN().stream().map(macro -> KeyStorage.getKey(macro.key()));
      String context = datatypeContext.getConsumer().getString();
      return new TabCompleteHelper().append(macros).filterPrefix(context).sortAlphabetically().stream();
   }

   public Macro get(IDatatypeContext datatypeContext) throws CommandException {
      String username = datatypeContext.getConsumer().getString();
      return this.jhzN().stream().filter(s -> KeyStorage.getKey(s.key()).equalsIgnoreCase(username)).findFirst().orElse(null);
   }

   private List<? extends Macro> jhzN() {
      return Viola.getInstance().getMacroRepository().getMacroList();
   }
}
