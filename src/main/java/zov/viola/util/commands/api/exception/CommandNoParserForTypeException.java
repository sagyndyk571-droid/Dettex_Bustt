package zov.viola.util.commands.api.exception;

import zov.viola.obf.D;

public class CommandNoParserForTypeException extends CommandUnhandledException {
   public CommandNoParserForTypeException(Class<?> klass) {
      super(
         String.format(
            D.k(
               new int[]{
                  228,
                  190,
                  197,
                  120,
                  195,
                  241,
                  222,
                  123,
                  211,
                  241,
                  214,
                  125,
                  201,
                  181,
                  144,
                  117,
                  135,
                  185,
                  209,
                  122,
                  195,
                  189,
                  213,
                  102,
                  135,
                  183,
                  223,
                  102,
                  135,
                  165,
                  201,
                  100,
                  194,
                  241,
                  149,
                  103
               },
               new int[]{167, 209, 176, 20}
            ),
            klass.getSimpleName()
         )
      );
   }
}
