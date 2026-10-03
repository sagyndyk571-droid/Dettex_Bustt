package zov.viola.util.friend;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

final class CaseInsensitiveNameIndex<T> {
   private final Function<T, String> cgQQ;
   private final Map<String, T> aLvds = new LinkedHashMap<>();

   CaseInsensitiveNameIndex(Function<T, String> nameExtractor) {
      this.cgQQ = nameExtractor;
   }

   synchronized boolean add(T value) {
      String key = this.q0iz(value);
      if (key != null && !this.aLvds.containsKey(key)) {
         this.aLvds.put(key, value);
         return true;
      } else {
         return false;
      }
   }

   synchronized T remove(String name) {
      return name == null ? null : this.aLvds.remove(zLanki(name));
   }

   synchronized boolean contains(String name) {
      return name != null && this.aLvds.containsKey(zLanki(name));
   }

   synchronized T get(String name) {
      return name == null ? null : this.aLvds.get(zLanki(name));
   }

   synchronized void replaceAll(Collection<? extends T> values) {
      this.aLvds.clear();

      for (T value : values) {
         this.add(value);
      }
   }

   synchronized void clear() {
      this.aLvds.clear();
   }

   synchronized List<T> values() {
      return List.copyOf(this.aLvds.values());
   }

   private String q0iz(T value) {
      if (value == null) {
         return null;
      } else {
         String name = this.cgQQ.apply(value);
         return name == null ? null : zLanki(name);
      }
   }

   private static String zLanki(String name) {
      return name.toLowerCase(Locale.ROOT);
   }
}
