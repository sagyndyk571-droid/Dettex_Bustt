package zov.viola.util.commands.api.helpers;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;
import zov.viola.util.commands.api.manager.ICommandManager;

public class TabCompleteHelper {
   private Stream<String> yt4t7;

   public TabCompleteHelper(String[] base) {
      this.yt4t7 = Stream.of(base);
   }

   public TabCompleteHelper(List<String> base) {
      this.yt4t7 = base.stream();
   }

   public TabCompleteHelper() {
      this.yt4t7 = Stream.empty();
   }

   public TabCompleteHelper append(Stream<String> source) {
      this.yt4t7 = Stream.concat(this.yt4t7, source);
      return this;
   }

   public TabCompleteHelper append(String... source) {
      return this.append(Stream.of(source));
   }

   public TabCompleteHelper append(Class<? extends Enum<?>> num) {
      return this.append(Stream.of(num.getEnumConstants()).map(Enum::name).map(String::toLowerCase));
   }

   public TabCompleteHelper prepend(Stream<String> source) {
      this.yt4t7 = Stream.concat(source, this.yt4t7);
      return this;
   }

   public TabCompleteHelper prepend(String... source) {
      return this.prepend(Stream.of(source));
   }

   public TabCompleteHelper prepend(Class<? extends Enum<?>> num) {
      return this.prepend(Stream.of(num.getEnumConstants()).map(Enum::name).map(String::toLowerCase));
   }

   public TabCompleteHelper map(Function<String, String> transform) {
      this.yt4t7 = this.yt4t7.map(transform);
      return this;
   }

   public TabCompleteHelper filter(Predicate<String> filter) {
      this.yt4t7 = this.yt4t7.filter(filter);
      return this;
   }

   public TabCompleteHelper sort(Comparator<String> comparator) {
      this.yt4t7 = this.yt4t7.sorted(comparator);
      return this;
   }

   public TabCompleteHelper sortAlphabetically() {
      return this.sort(String.CASE_INSENSITIVE_ORDER);
   }

   public TabCompleteHelper filterPrefix(String prefix) {
      return this.filter(x -> x.toLowerCase(Locale.US).startsWith(prefix.toLowerCase(Locale.US)));
   }

   public TabCompleteHelper filterPrefixNamespaced(String prefix) {
      return this.filterPrefix(Identifier.of(prefix).toString());
   }

   public String[] build() {
      return this.yt4t7.toArray(String[]::new);
   }

   public Stream<String> stream() {
      return this.yt4t7;
   }

   public TabCompleteHelper addCommands(ICommandManager manager) {
      return this.append(manager.getRegistry().descendingStream().flatMap(command -> command.getNames().stream()).distinct());
   }
}
