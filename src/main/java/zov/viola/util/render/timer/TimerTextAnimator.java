package zov.viola.util.render.timer;

import java.util.HashMap;
import java.util.Map;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.renderers.DrawUtil;

public final class TimerTextAnimator {
   private static final long ANIM_MS = 170L;
   private static final float SLIDE_PX = 5.0F;
   private static final Map<String, TimerTextAnimator.Entry> ENTRIES = new HashMap<>();
   private static final ThreadLocal<char[]> CHAR_BUF = ThreadLocal.withInitial(() -> new char[1]);

   private TimerTextAnimator() {
   }

   private static String hfmd(char c) {
      char[] buf = CHAR_BUF.get();
      buf[0] = c;
      return new String(buf);
   }

   public static float getAnimatedWidth(MsdfFont font, String key, String text, float size) {
      TimerTextAnimator.Entry entry = ENTRIES.get(key);
      String previous = entry != null ? entry.tc0y : text;
      String current = entry != null ? entry.k3liwx : text;
      if (previous.length() != current.length()) {
         return font.getWidth(current, size);
      } else {
         int maxLen = Math.max(previous.length(), current.length());
         float width = 0.0F;

         for (int i = 0; i < maxLen; i++) {
            char oldChar = i < previous.length() ? previous.charAt(i) : 0;
            char newChar = i < current.length() ? current.charAt(i) : 0;
            float oldW = oldChar == 0 ? 0.0F : font.getWidth(hfmd(oldChar), size);
            float newW = newChar == 0 ? 0.0F : font.getWidth(hfmd(newChar), size);
            width += Math.max(oldW, newW);
         }

         return width;
      }
   }

   public static void draw(MsdfFont font, String key, String text, float x, float y, int color, float size) {
      long now = System.currentTimeMillis();
      TimerTextAnimator.Entry entry = ENTRIES.computeIfAbsent(key, k -> new TimerTextAnimator.Entry(text));
      if (!text.equals(entry.k3liwx)) {
         if (text.length() != entry.k3liwx.length()) {
            entry.tc0y = text;
         } else {
            entry.tc0y = entry.k3liwx;
         }

         entry.k3liwx = text;
         entry.x2q2 = now;
      }

      entry.h3srPiJ = now;
      p8ac(now);
      String previous = entry.tc0y;
      String current = entry.k3liwx;
      if (previous.length() != current.length()) {
         DrawUtil.drawText(font, current, x, y, color, size);
      } else {
         float progress = Math.min(1.0F, (float)(now - entry.x2q2) / 170.0F);
         if (progress >= 1.0F) {
            DrawUtil.drawText(font, current, x, y, color, size);
         } else {
            int baseAlpha = color >>> 24 & 0xFF;
            if (baseAlpha == 0) {
               baseAlpha = 255;
            }

            int maxLen = Math.max(previous.length(), current.length());
            float cursor = x;

            for (int i = 0; i < maxLen; i++) {
               char oldChar = i < previous.length() ? previous.charAt(i) : 0;
               char newChar = i < current.length() ? current.charAt(i) : 0;
               float oldW = oldChar == 0 ? 0.0F : font.getWidth(hfmd(oldChar), size);
               float newW = newChar == 0 ? 0.0F : font.getWidth(hfmd(newChar), size);
               float charW = Math.max(oldW, newW);
               boolean animate = Character.isDigit(oldChar) && Character.isDigit(newChar) && oldChar != newChar && progress < 1.0F;
               if (animate) {
                  int oldAlpha = (int)(baseAlpha * (1.0F - progress));
                  int newAlpha = (int)(baseAlpha * progress);
                  DrawUtil.drawText(font, hfmd(oldChar), cursor, y - progress * 5.0F, oldAlpha << 24 | color & 16777215, size);
                  DrawUtil.drawText(font, hfmd(newChar), cursor, y + (1.0F - progress) * 5.0F, newAlpha << 24 | color & 16777215, size);
               } else if (newChar != 0) {
                  DrawUtil.drawText(font, hfmd(newChar), cursor, y, color, size);
               }

               cursor += charW;
            }
         }
      }
   }

   private static void p8ac(long now) {
      if (ENTRIES.size() >= 512) {
         ENTRIES.entrySet().removeIf(e -> now - e.getValue().h3srPiJ > 10000L);
      }
   }

   private static final class Entry {
      private String tc0y;
      private String k3liwx;
      private long x2q2;
      private long h3srPiJ;

      private Entry(String text) {
         this.tc0y = text;
         this.k3liwx = text;
         this.x2q2 = System.currentTimeMillis();
         this.h3srPiJ = this.x2q2;
      }
   }
}
