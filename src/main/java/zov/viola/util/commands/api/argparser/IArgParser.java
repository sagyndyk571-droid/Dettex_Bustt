package zov.viola.util.commands.api.argparser;

import zov.viola.util.commands.api.argument.ICommandArgument;

public interface IArgParser<T> {
   Class<T> getTarget();

   public interface Stated<T, S> extends IArgParser<T> {
      Class<S> getStateType();

      T parseArg(ICommandArgument var1, S var2) throws Exception;
   }

   public interface Stateless<T> extends IArgParser<T> {
      T parseArg(ICommandArgument var1) throws Exception;
   }
}
