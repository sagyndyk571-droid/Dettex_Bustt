package zov.viola.util.commands.api.exception;

import zov.viola.obf.D;
import zov.viola.util.commands.api.argument.ICommandArgument;

public abstract class CommandInvalidArgumentException extends CommandErrorMessageException {
   public final ICommandArgument arg;

   protected CommandInvalidArgumentException(ICommandArgument arg, String message) {
      super(a2tQsm2(arg, message));
      this.arg = arg;
   }

   protected CommandInvalidArgumentException(ICommandArgument arg, String message, Throwable cause) {
      super(a2tQsm2(arg, message), cause);
      this.arg = arg;
   }

   private static String a2tQsm2(ICommandArgument arg, String message) {
      return String.format(
         D.k(
            new int[]{
               1035, 1252, 1194, 1173, 1071, 1180, 178, 1174, 53, 137, 225, 132, 1061, 1260, 1185, 1255, 1065, 1177, 1199, 1254, 1056, 150, 178, 129, 102
            },
            new int[]{21, 172, 146, 164}
         ),
         arg.getIndex() == -1 ? "<unknown>" : Integer.toString(arg.getIndex() + 1),
         message
      );
   }
}
