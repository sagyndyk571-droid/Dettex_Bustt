package zov.viola.util.commands.api.exception;

import zov.viola.obf.D;

public class CommandNotEnoughArgumentsException extends CommandErrorMessageException {
   public CommandNotEnoughArgumentsException(int minArgs) {
      super(
         String.format(
            D.k(
               new int[]{
                  1232,
                  1278,
                  1110,
                  1059,
                  1164,
                  1161,
                  1106,
                  1119,
                  1267,
                  1164,
                  1119,
                  1059,
                  237,
                  1275,
                  1058,
                  1070,
                  1166,
                  1271,
                  1111,
                  1056,
                  1167,
                  1269,
                  1104,
                  49,
                  237,
                  1270,
                  1057,
                  1067,
                  1272,
                  1270,
                  66,
                  56,
                  169,
                  235,
                  1106,
                  1117,
                  1278,
                  1160,
                  1118,
                  1064,
                  1264,
                  1161
               },
               new int[]{205, 203, 98, 29}
            ),
            minArgs
         )
      );
   }
}
