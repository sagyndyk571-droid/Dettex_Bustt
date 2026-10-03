package zov.viola.util.config;

import java.util.ArrayList;
import java.util.List;
import zov.viola.obf.D;

final class ConfigKeyAliases {
   private static final List<List<String>> ALIAS_GROUPS = List.of(
      List.of(
         "Dog Fly", "Bib Fly"
      ),
      List.of(
         "Только с пробелом",
         "Умные криты"
      ),
      List.of(
         "Режим замедления",
         "Сброс спринта",
         "Замедление"
      ),
      List.of(
         "Свапать с",
         "Свапать на"
      )
   );

   private ConfigKeyAliases() {
   }

   static List<String> candidates(String currentName) {
      for (List<String> group : ALIAS_GROUPS) {
         if (group.contains(currentName)) {
            List<String> candidates = new ArrayList<>(group.size());
            candidates.add(currentName);

            for (String alias : group) {
               if (!alias.equals(currentName)) {
                  candidates.add(alias);
               }
            }

            return List.copyOf(candidates);
         }
      }

      return List.of(currentName);
   }
}
