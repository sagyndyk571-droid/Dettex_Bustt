package zov.viola.util.commands.api;

import zov.viola.util.commands.api.argparser.IArgParserManager;

public interface ICommandSystem {
   IArgParserManager getParserManager();
}
