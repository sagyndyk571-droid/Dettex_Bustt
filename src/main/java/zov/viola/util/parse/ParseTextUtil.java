package zov.viola.util.parse;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.KeybindTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.PlainTextContent.Literal;
import zov.viola.util.IMinecraft;
import zov.viola.util.render.msdf.MsdfFont;

public class ParseTextUtil implements IMinecraft {
   public static List<MsdfFont.ColoredGlyph> parseTextToColoredGlyphs(Text text) {
      List<MsdfFont.ColoredGlyph> result = new ArrayList<>();
      cjz6s(text, -1, result);
      return result;
   }

   private static void cjz6s(Text text, int currentColor, List<MsdfFont.ColoredGlyph> result) {
      Style style = text.getStyle();
      int color = style.getColor() != null ? style.getColor().getRgb() | 0xFF000000 : currentColor;
      TextContent content = text.getContent();
      String raw = "";
      if (content instanceof Literal literal) {
         raw = literal.string();
      } else if (content instanceof TranslatableTextContent translatable) {
         raw = translatable.getKey();
      } else if (content instanceof KeybindTextContent keybind) {
         raw = keybind.getKey();
      }

      for (int i = 0; i < raw.length(); i++) {
         char c = raw.charAt(i);
         if (c == 167 && i + 1 < raw.length()) {
            i++;
         } else {
            result.add(new MsdfFont.ColoredGlyph(c, color));
         }
      }

      for (Text sibling : text.getSiblings()) {
         cjz6s(sibling, color, result);
      }
   }
}
