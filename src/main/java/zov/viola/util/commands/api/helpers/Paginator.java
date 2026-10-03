package zov.viola.util.commands.api.helpers;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;
import zov.viola.obf.D;
import zov.viola.util.QuickLogger;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.exception.CommandInvalidTypeException;

public class Paginator<E> implements QuickLogger {
   public final List<E> entries;
   public int pageSize = 8;
   public int page = 1;

   public Paginator(List<E> entries) {
      this.entries = entries;
   }

   @SafeVarargs
   public Paginator(E... entries) {
      this.entries = Arrays.asList(entries);
   }

   public Paginator<E> setPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
   }

   public int getMaxPage() {
      return (this.entries.size() - 1) / this.pageSize + 1;
   }

   public boolean validPage(int page) {
      return page > 0 && page <= this.getMaxPage();
   }

   public void skipPages(int pages) {
      this.page += pages;
   }

   public void display(Function<E, Text> transform, String commandPrefix) {
      int offset = (this.page - 1) * this.pageSize;

      for (int i = offset; i < offset + this.pageSize; i++) {
         if (i < this.entries.size()) {
            this.logDirect(new Text[]{transform.apply(this.entries.get(i))});
         } else {
            this.logDirect("--", Formatting.DARK_GRAY);
         }
      }

      boolean hasPrevPage = commandPrefix != null && this.validPage(this.page - 1);
      boolean hasNextPage = commandPrefix != null && this.validPage(this.page + 1);
      MutableText prevPageComponent = Text.literal("<<");
      if (hasPrevPage) {
         prevPageComponent.setStyle(
            prevPageComponent.getStyle()
               .withClickEvent(
                  new ClickEvent(
                     Action.RUN_COMMAND, commandPrefix.contains("help") ? commandPrefix + " " + (this.page - 1) : commandPrefix + " list " + (this.page - 1)
                  )
               )
               .withHoverEvent(
                  new HoverEvent(
                     net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                     Text.literal(
                        D.k(
                           new int[]{
                              229, 22, 227, 192, 205, 90, 254, 204, 134, 12, 227, 198, 209, 90, 250, 209, 195, 12, 227, 204, 211, 9, 170, 211, 199, 29, 239
                           },
                           new int[]{166, 122, 138, 163}
                        )
                     )
                  )
               )
         );
      } else {
         prevPageComponent.setStyle(prevPageComponent.getStyle().withColor(Formatting.DARK_GRAY));
      }

      MutableText nextPageComponent = Text.literal(">>");
      if (hasNextPage) {
         nextPageComponent.setStyle(
            nextPageComponent.getStyle()
               .withClickEvent(
                  new ClickEvent(
                     Action.RUN_COMMAND, commandPrefix.contains("help") ? commandPrefix + " " + (this.page + 1) : commandPrefix + " list " + (this.page + 1)
                  )
               )
               .withHoverEvent(
                  new HoverEvent(
                     net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                     Text.literal(
                        D.k(
                           new int[]{148, 48, 50, 180, 188, 124, 47, 184, 247, 42, 50, 178, 160, 124, 53, 178, 175, 40, 123, 167, 182, 59, 62},
                           new int[]{215, 92, 91, 215}
                        )
                     )
                  )
               )
         );
      } else {
         nextPageComponent.setStyle(nextPageComponent.getStyle().withColor(Formatting.DARK_GRAY));
      }

      MutableText pagerComponent = Text.literal("");
      pagerComponent.setStyle(pagerComponent.getStyle().withColor(Formatting.GRAY));
      pagerComponent.append(prevPageComponent);
      pagerComponent.append(" | ");
      pagerComponent.append(nextPageComponent);
      pagerComponent.append(String.format(" %d/%d", this.page, this.getMaxPage()));
      this.logDirect(new Text[]{pagerComponent});
   }

   public void display(Function<E, Text> transform) {
      this.display(transform, null);
   }

   public static <T> void paginate(IArgConsumer consumer, Paginator<T> pagi, Runnable pre, Function<T, Text> transform, String commandPrefix) throws CommandException {
      int page = 1;
      consumer.requireMax(1);
      if (consumer.hasAny()) {
         page = consumer.getAs(Integer.class);
         if (!pagi.validPage(page)) {
            throw new CommandInvalidTypeException(consumer.consumed(), "Вы указали неверную страницу, пределы: 1-" + pagi.getMaxPage());
         }
      }

      pagi.skipPages(page - pagi.page);
      if (pre != null) {
         pre.run();
      }

      pagi.display(transform, commandPrefix);
   }

   public static <T> void paginate(IArgConsumer consumer, List<T> elems, Runnable pre, Function<T, Text> transform, String commandPrefix) throws CommandException {
      paginate(consumer, new Paginator<>(elems), pre, transform, commandPrefix);
   }

   public static <T> void paginate(IArgConsumer consumer, T[] elems, Runnable pre, Function<T, Text> transform, String commandPrefix) throws CommandException {
      paginate(consumer, Arrays.asList(elems), pre, transform, commandPrefix);
   }

   public static <T> void paginate(IArgConsumer consumer, Paginator<T> pagi, Function<T, Text> transform, String commandPrefix) throws CommandException {
      paginate(consumer, pagi, null, transform, commandPrefix);
   }

   public static <T> void paginate(IArgConsumer consumer, List<T> elems, Function<T, Text> transform, String commandPrefix) throws CommandException {
      paginate(consumer, new Paginator<>(elems), null, transform, commandPrefix);
   }

   public static <T> void paginate(IArgConsumer consumer, T[] elems, Function<T, Text> transform, String commandPrefix) throws CommandException {
      paginate(consumer, Arrays.asList(elems), null, transform, commandPrefix);
   }

   public static <T> void paginate(IArgConsumer consumer, Paginator<T> pagi, Runnable pre, Function<T, Text> transform) throws CommandException {
      paginate(consumer, pagi, pre, transform, null);
   }

   public static <T> void paginate(IArgConsumer consumer, List<T> elems, Runnable pre, Function<T, Text> transform) throws CommandException {
      paginate(consumer, new Paginator<>(elems), pre, transform, null);
   }

   public static <T> void paginate(IArgConsumer consumer, T[] elems, Runnable pre, Function<T, Text> transform) throws CommandException {
      paginate(consumer, Arrays.asList(elems), pre, transform, null);
   }

   public static <T> void paginate(IArgConsumer consumer, Paginator<T> pagi, Function<T, Text> transform) throws CommandException {
      paginate(consumer, pagi, null, transform, null);
   }

   public static <T> void paginate(IArgConsumer consumer, List<T> elems, Function<T, Text> transform) throws CommandException {
      paginate(consumer, new Paginator<>(elems), null, transform, null);
   }

   public static <T> void paginate(IArgConsumer consumer, T[] elems, Function<T, Text> transform) throws CommandException {
      paginate(consumer, Arrays.asList(elems), null, transform, null);
   }
}
