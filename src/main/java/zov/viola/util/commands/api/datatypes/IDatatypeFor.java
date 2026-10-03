package zov.viola.util.commands.api.datatypes;

import zov.viola.util.commands.api.exception.CommandException;

public interface IDatatypeFor<T> extends IDatatype {
   T get(IDatatypeContext var1) throws CommandException;
}
