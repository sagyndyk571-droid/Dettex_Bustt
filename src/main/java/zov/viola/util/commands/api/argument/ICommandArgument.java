package zov.viola.util.commands.api.argument;

import zov.viola.util.commands.api.exception.CommandInvalidTypeException;

public interface ICommandArgument {
   int getIndex();

   String getValue();

   String getRawRest();

   <E extends Enum<?>> E getEnum(Class<E> var1) throws CommandInvalidTypeException;

   <T> T getAs(Class<T> var1) throws CommandInvalidTypeException;

   <T> boolean is(Class<T> var1);

   <T, S> T getAs(Class<T> var1, Class<S> var2, S var3) throws CommandInvalidTypeException;

   <T, S> boolean is(Class<T> var1, Class<S> var2, S var3);
}
