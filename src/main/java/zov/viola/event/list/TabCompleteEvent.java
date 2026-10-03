package zov.viola.event.list;

import zov.viola.event.Event;

public class TabCompleteEvent extends Event {
   private final String j36gn9Y;
   public String[] completions;

   public TabCompleteEvent(String prefix) {
      this.j36gn9Y = prefix;
   }

   public String getPrefix() {
      return this.j36gn9Y;
   }

   public void setCompletions(String[] completions) {
      this.completions = completions;
   }
}
