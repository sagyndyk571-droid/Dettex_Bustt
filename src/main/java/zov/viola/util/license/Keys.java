package zov.viola.util.license;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zov.viola.obf.D;

public final class Keys {
   private static final Set<String> LIFETIME = dQTZ("viola/license/keys_lifetime.txt");
   private static final Set<String> THREE_DAY = dQTZ("viola/license/keys_3day.txt");
   private static final Pattern KEY_PATTERN = Pattern.compile(
      D.k(
         new int[]{77, 218, 36, 24, 38, 182, 48, 31, 109, 175, 116, 106, 41, 161, 36, 25, 87, 182, 83, 114, 59, 162, 84, 57, 34, 230, 32, 57, 37, 230},
         new int[]{22, 155, 9, 66}
      )
   );

   private Keys() {
   }

   public static Keys.Type typeOf(String s) {
      return "THREE_DAY".equalsIgnoreCase(s) ? Keys.Type.THREE_DAY : Keys.Type.LIFETIME;
   }

   public static boolean isValid(String key, Keys.Type t) {
      return t == Keys.Type.THREE_DAY ? THREE_DAY.contains(key) : LIFETIME.contains(key);
   }

   public static Keys.Type match(String key) {
      if (LIFETIME.contains(key)) {
         return Keys.Type.LIFETIME;
      } else {
         return THREE_DAY.contains(key) ? Keys.Type.THREE_DAY : null;
      }
   }

   public static int lifetimeCount() {
      return LIFETIME.size();
   }

   public static int threeDayCount() {
      return THREE_DAY.size();
   }

   public static String normalize(String raw) {
      return raw == null ? "" : raw.trim().toUpperCase().replace(" ", "");
   }

   private static Set<String> dQTZ(String resource) {
      Set<String> set = new HashSet<>();

      try {
         label71: {
            Object var10;
            try (InputStream is = Keys.class.getClassLoader().getResourceAsStream(resource)) {
               if (is != null) {
                  BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                  String line;
                  while ((line = reader.readLine()) != null) {
                     String s = line.trim().toUpperCase();
                     if (!s.isEmpty() && !s.startsWith("=")) {
                        s = s.replaceFirst("^\\d+[.:)]\\s*", "");
                        Matcher m = KEY_PATTERN.matcher(s);
                        if (m.find()) {
                           set.add(m.group());
                        }
                     }
                  }
                  break label71;
               }

               var10 = set;
            }

            return (Set<String>)var10;
         }
      } catch (Exception var9) {
      }

      return set;
   }

   public static enum Type {
      LIFETIME,
      THREE_DAY;
   }
}
