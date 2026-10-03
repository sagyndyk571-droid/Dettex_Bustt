package zov.viola.util.commands.api.datatypes;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.keyboard.KeyStorage;

public enum KeyDataType implements IDatatypeFor<Entry<String, Integer>> {
   INSTANCE;

   @Override
   public Stream<String> tabComplete(IDatatypeContext datatypeContext) throws CommandException {
      Stream<String> keys = zuzxnF().keySet().stream();
      String context = datatypeContext.getConsumer().getString();
      return new TabCompleteHelper().append(keys).filterPrefix(context).sortAlphabetically().stream();
   }

   public Entry<String, Integer> get(IDatatypeContext datatypeContext) throws CommandException {
      String key = datatypeContext.getConsumer().getString();
      return zuzxnF().entrySet().stream().filter(s -> s.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
   }

   private static Map<String, Integer> zuzxnF() {
      return KeyStorage.keyMap;
   }
}
