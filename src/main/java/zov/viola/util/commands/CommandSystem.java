package zov.viola.util.commands;

import zov.viola.util.commands.api.ICommandSystem;
import zov.viola.util.commands.api.argparser.IArgParserManager;
import zov.viola.util.commands.argparser.ArgParserManager;

public enum CommandSystem implements ICommandSystem {
   INSTANCE;

   @Override
   public IArgParserManager getParserManager() {
      return ArgParserManager.INSTANCE;
   }
}
