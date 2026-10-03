package zov.viola.util.commands.argument;

import java.util.stream.Stream;
import zov.viola.util.commands.api.argument.ICommandArgument;
import zov.viola.util.commands.api.exception.CommandInvalidTypeException;
import zov.viola.util.commands.argparser.ArgParserManager;

class CommandArgument implements ICommandArgument {
   private final int jTq1;
   private final String dFqFj8h;
   private final String kevR8h;

   CommandArgument(int index, String value, String rawRest) {
      this.jTq1 = index;
      this.dFqFj8h = value;
      this.kevR8h = rawRest;
   }

   @Override
   public int getIndex() {
      return this.jTq1;
   }

   @Override
   public String getValue() {
      return this.dFqFj8h;
   }

   @Override
   public String getRawRest() {
      return this.kevR8h;
   }

   @Override
   public <E extends Enum<?>> E getEnum(Class<E> enumClass) throws CommandInvalidTypeException {
      return Stream.of(enumClass.getEnumConstants())
         .filter(e -> e.name().equalsIgnoreCase(this.dFqFj8h))
         .findFirst()
         .orElseThrow(() -> new CommandInvalidTypeException(this, enumClass.getSimpleName()));
   }

   @Override
   public <T> T getAs(Class<T> type) throws CommandInvalidTypeException {
      return ArgParserManager.INSTANCE.parseStateless(type, this);
   }

   @Override
   public <T> boolean is(Class<T> type) {
      try {
         this.getAs(type);
         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   @Override
   public <T, S> T getAs(Class<T> type, Class<S> stateType, S state) throws CommandInvalidTypeException {
      return ArgParserManager.INSTANCE.parseStated(type, stateType, this, state);
   }

   @Override
   public <T, S> boolean is(Class<T> type, Class<S> stateType, S state) {
      try {
         this.getAs(type, stateType, state);
         return true;
      } catch (Throwable var5) {
         return false;
      }
   }
}
