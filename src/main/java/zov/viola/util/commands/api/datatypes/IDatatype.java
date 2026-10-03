package zov.viola.util.commands.api.datatypes;

import java.util.stream.Stream;
import zov.viola.util.IMinecraft;
import zov.viola.util.commands.api.exception.CommandException;

public interface IDatatype extends IMinecraft {
   Stream<String> tabComplete(IDatatypeContext var1) throws CommandException;
}
