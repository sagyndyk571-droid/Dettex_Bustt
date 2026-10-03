package zov.viola.util.commands.argument;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.argument.ICommandArgument;
import zov.viola.util.commands.api.datatypes.IDatatype;
import zov.viola.util.commands.api.datatypes.IDatatypeContext;
import zov.viola.util.commands.api.datatypes.IDatatypeFor;
import zov.viola.util.commands.api.datatypes.IDatatypePost;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.exception.CommandInvalidTypeException;
import zov.viola.util.commands.api.exception.CommandNotEnoughArgumentsException;
import zov.viola.util.commands.api.exception.CommandTooManyArgumentsException;
import zov.viola.util.commands.api.manager.ICommandManager;

public class ArgConsumer implements IArgConsumer {
   private final ICommandManager bhvM;
   private final IDatatypeContext jucxh;
   private final LinkedList<ICommandArgument> vavSo0;
   private final Deque<ICommandArgument> eDWvu8;

   private ArgConsumer(ICommandManager manager, Deque<ICommandArgument> args, Deque<ICommandArgument> consumed) {
      this.bhvM = manager;
      this.jucxh = new ArgConsumer.Context();
      this.vavSo0 = new LinkedList<>(args);
      this.eDWvu8 = new LinkedList<>(consumed);
   }

   public ArgConsumer(ICommandManager manager, List<ICommandArgument> args) {
      this(manager, new LinkedList<>(args), new LinkedList<>());
   }

   @Override
   public LinkedList<ICommandArgument> getArgs() {
      return this.vavSo0;
   }

   @Override
   public Deque<ICommandArgument> getConsumed() {
      return this.eDWvu8;
   }

   @Override
   public boolean has(int num) {
      return this.vavSo0.size() >= num;
   }

   @Override
   public boolean hasAny() {
      return this.has(1);
   }

   @Override
   public boolean hasAtMost(int num) {
      return this.vavSo0.size() <= num;
   }

   @Override
   public boolean hasAtMostOne() {
      return this.hasAtMost(1);
   }

   @Override
   public boolean hasExactly(int num) {
      return this.vavSo0.size() == num;
   }

   @Override
   public boolean hasExactlyOne() {
      return this.hasExactly(1);
   }

   @Override
   public ICommandArgument peek(int index) throws CommandNotEnoughArgumentsException {
      this.requireMin(index + 1);
      return this.vavSo0.get(index);
   }

   @Override
   public ICommandArgument peek() throws CommandNotEnoughArgumentsException {
      return this.peek(0);
   }

   @Override
   public boolean is(Class<?> type, int index) throws CommandNotEnoughArgumentsException {
      return this.peek(index).is(type);
   }

   @Override
   public boolean is(Class<?> type) throws CommandNotEnoughArgumentsException {
      return this.is(type, 0);
   }

   @Override
   public String peekString(int index) throws CommandNotEnoughArgumentsException {
      return this.peek(index).getValue();
   }

   @Override
   public String peekString() throws CommandNotEnoughArgumentsException {
      return this.peekString(0);
   }

   @Override
   public <E extends Enum<?>> E peekEnum(Class<E> enumClass, int index) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.peek(index).getEnum(enumClass);
   }

   @Override
   public <E extends Enum<?>> E peekEnum(Class<E> enumClass) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.peekEnum(enumClass, 0);
   }

   @Override
   public <E extends Enum<?>> E peekEnumOrNull(Class<E> enumClass, int index) throws CommandNotEnoughArgumentsException {
      try {
         return this.peekEnum(enumClass, index);
      } catch (CommandInvalidTypeException var4) {
         return null;
      }
   }

   @Override
   public <E extends Enum<?>> E peekEnumOrNull(Class<E> enumClass) throws CommandNotEnoughArgumentsException {
      return this.peekEnumOrNull(enumClass, 0);
   }

   @Override
   public <T> T peekAs(Class<T> type, int index) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.peek(index).getAs(type);
   }

   @Override
   public <T> T peekAs(Class<T> type) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.peekAs(type, 0);
   }

   @Override
   public <T> T peekAsOrDefault(Class<T> type, T def, int index) throws CommandNotEnoughArgumentsException {
      try {
         return this.peekAs(type, index);
      } catch (CommandInvalidTypeException var5) {
         return def;
      }
   }

   @Override
   public <T> T peekAsOrDefault(Class<T> type, T def) throws CommandNotEnoughArgumentsException {
      return this.peekAsOrDefault(type, def, 0);
   }

   @Override
   public <T> T peekAsOrNull(Class<T> type, int index) throws CommandNotEnoughArgumentsException {
      return this.peekAsOrDefault(type, null, index);
   }

   @Override
   public <T> T peekAsOrNull(Class<T> type) throws CommandNotEnoughArgumentsException {
      return this.peekAsOrNull(type, 0);
   }

   @Override
   public <T> T peekDatatype(IDatatypeFor<T> datatype) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.copy().getDatatypeFor(datatype);
   }

   @Override
   public <T, O> T peekDatatype(IDatatypePost<T, O> datatype) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.peekDatatype(datatype, null);
   }

   @Override
   public <T, O> T peekDatatype(IDatatypePost<T, O> datatype, O original) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.copy().getDatatypePost(datatype, original);
   }

   @Override
   public <T> T peekDatatypeOrNull(IDatatypeFor<T> datatype) {
      return this.copy().getDatatypeForOrNull(datatype);
   }

   @Override
   public <T, O> T peekDatatypeOrNull(IDatatypePost<T, O> datatype) {
      return this.copy().getDatatypePostOrNull(datatype, null);
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T peekDatatypePost(D datatype, O original) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.copy().getDatatypePost(datatype, original);
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T peekDatatypePostOrDefault(D datatype, O original, T def) {
      return this.copy().getDatatypePostOrDefault(datatype, original, def);
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T peekDatatypePostOrNull(D datatype, O original) {
      return this.peekDatatypePostOrDefault(datatype, original, null);
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T peekDatatypeFor(Class<D> datatype) {
      return this.copy().peekDatatypeFor(datatype);
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T peekDatatypeForOrDefault(Class<D> datatype, T def) {
      return this.copy().peekDatatypeForOrDefault(datatype, def);
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T peekDatatypeForOrNull(Class<D> datatype) {
      return this.peekDatatypeForOrDefault(datatype, null);
   }

   @Override
   public ICommandArgument get() throws CommandNotEnoughArgumentsException {
      this.requireMin(1);
      ICommandArgument arg = this.vavSo0.removeFirst();
      this.eDWvu8.add(arg);
      return arg;
   }

   @Override
   public String getString() throws CommandNotEnoughArgumentsException {
      return this.get().getValue();
   }

   @Override
   public <E extends Enum<?>> E getEnum(Class<E> enumClass) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.get().getEnum(enumClass);
   }

   @Override
   public <E extends Enum<?>> E getEnumOrDefault(Class<E> enumClass, E def) throws CommandNotEnoughArgumentsException {
      try {
         this.peekEnum(enumClass);
         return this.getEnum(enumClass);
      } catch (CommandInvalidTypeException var4) {
         return def;
      }
   }

   @Override
   public <E extends Enum<?>> E getEnumOrNull(Class<E> enumClass) throws CommandNotEnoughArgumentsException {
      return this.getEnumOrDefault(enumClass, null);
   }

   @Override
   public <T> T getAs(Class<T> type) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      return this.get().getAs(type);
   }

   @Override
   public <T> T getAsOrDefault(Class<T> type, T def) throws CommandNotEnoughArgumentsException {
      try {
         T val = this.peek().getAs(type);
         this.get();
         return val;
      } catch (CommandInvalidTypeException var4) {
         return def;
      }
   }

   @Override
   public <T> T getAsOrNull(Class<T> type) throws CommandNotEnoughArgumentsException {
      return this.getAsOrDefault(type, null);
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T getDatatypePost(D datatype, O original) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      try {
         return datatype.apply(this.jucxh, original);
      } catch (Exception var4) {
         var4.printStackTrace();
         throw new CommandInvalidTypeException(this.hasAny() ? this.peek() : this.consumed(), datatype.getClass().getSimpleName(), var4);
      }
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T getDatatypePostOrDefault(D datatype, O original, T _default) {
      List<ICommandArgument> argsSnapshot = new ArrayList<>(this.vavSo0);
      List<ICommandArgument> consumedSnapshot = new ArrayList<>(this.eDWvu8);

      try {
         return this.getDatatypePost(datatype, original);
      } catch (Exception var7) {
         this.vavSo0.clear();
         this.vavSo0.addAll(argsSnapshot);
         this.eDWvu8.clear();
         this.eDWvu8.addAll(consumedSnapshot);
         return _default;
      }
   }

   @Override
   public <T, O, D extends IDatatypePost<T, O>> T getDatatypePostOrNull(D datatype, O original) {
      return this.getDatatypePostOrDefault(datatype, original, null);
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T getDatatypeFor(D datatype) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException {
      try {
         return datatype.get(this.jucxh);
      } catch (Exception var3) {
         var3.printStackTrace();
         throw new CommandInvalidTypeException(this.hasAny() ? this.peek() : this.consumed(), datatype.getClass().getSimpleName(), var3);
      }
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T getDatatypeForOrDefault(D datatype, T def) {
      List<ICommandArgument> argsSnapshot = new ArrayList<>(this.vavSo0);
      List<ICommandArgument> consumedSnapshot = new ArrayList<>(this.eDWvu8);

      try {
         return this.getDatatypeFor(datatype);
      } catch (Exception var6) {
         this.vavSo0.clear();
         this.vavSo0.addAll(argsSnapshot);
         this.eDWvu8.clear();
         this.eDWvu8.addAll(consumedSnapshot);
         return def;
      }
   }

   @Override
   public <T, D extends IDatatypeFor<T>> T getDatatypeForOrNull(D datatype) {
      return this.getDatatypeForOrDefault(datatype, null);
   }

   @Override
   public <T extends IDatatype> Stream<String> tabCompleteDatatype(T datatype) {
      try {
         return datatype.tabComplete(this.jucxh);
      } catch (CommandException var3) {
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return Stream.empty();
   }

   @Override
   public String rawRest() {
      return this.vavSo0.size() > 0 ? this.vavSo0.getFirst().getRawRest() : "";
   }

   @Override
   public void requireMin(int min) throws CommandNotEnoughArgumentsException {
      if (this.vavSo0.size() < min) {
         throw new CommandNotEnoughArgumentsException(min + this.eDWvu8.size());
      }
   }

   @Override
   public void requireMax(int max) throws CommandTooManyArgumentsException {
      if (this.vavSo0.size() > max) {
         throw new CommandTooManyArgumentsException(max + this.eDWvu8.size());
      }
   }

   @Override
   public void requireExactly(int args) throws CommandException {
      this.requireMin(args);
      this.requireMax(args);
   }

   @Override
   public boolean hasConsumed() {
      return !this.eDWvu8.isEmpty();
   }

   @Override
   public ICommandArgument consumed() {
      return (ICommandArgument)(this.eDWvu8.size() > 0 ? this.eDWvu8.getLast() : CommandArguments.unknown());
   }

   @Override
   public String consumedString() {
      return this.consumed().getValue();
   }

   public ArgConsumer copy() {
      return new ArgConsumer(this.bhvM, this.vavSo0, this.eDWvu8);
   }

   private final class Context implements IDatatypeContext {
      public ArgConsumer getConsumer() {
         return ArgConsumer.this;
      }
   }
}
