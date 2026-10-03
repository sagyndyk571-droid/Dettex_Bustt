package zov.viola.util;

import java.util.Arrays;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zov.viola.obf.D;

public interface QuickLogger {
   static Text getPrefix() {
      MutableText onetap = Text.literal("ViolaClient");
      onetap.setStyle(onetap.getStyle().withColor(Formatting.WHITE));
      MutableText prefix = Text.literal("");
      prefix.setStyle(onetap.getStyle().withColor(Formatting.DARK_GRAY));
      prefix.append(onetap);
      prefix.append(" -> ");
      return prefix;
   }

   default void logDirect(String message, Object... args) {
      this.logDirect(message.formatted(args));
   }

   default void logDirect(Text... components) {
      MutableText component = Text.literal("");
      component.append(getPrefix());
      component.append(Text.literal(" "));
      Arrays.asList(components).forEach(component::append);
      if (MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(component);
      }
   }

   default void logDirect(String message, Formatting color) {
      Stream.of(message.split("\n")).forEach(line -> {
         MutableText component = Text.literal(line.replace("\t", "    "));
         component.setStyle(component.getStyle().withColor(color));
         this.logDirect(component);
      });
   }

   default void logDirect(String message) {
      this.logDirect(message, Formatting.GRAY);
   }
}
