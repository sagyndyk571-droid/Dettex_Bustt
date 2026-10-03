package zov.viola.util.text;

public final class ValueUnit {
   private final ValueUnit.Kind h4v12p;
   private final String fPr56;
   private final String x5x6;
   private final String vjdm0;
   private final String h8K55a;

   private ValueUnit(ValueUnit.Kind kind, String one, String few, String many, String raw) {
      this.h4v12p = kind;
      this.fPr56 = one;
      this.x5x6 = few;
      this.vjdm0 = many;
      this.h8K55a = raw;
   }

   public static ValueUnit countable(String one, String few, String many) {
      return new ValueUnit(ValueUnit.Kind.COUNTABLE, one, few, many, null);
   }

   public static ValueUnit abbreviation(String raw) {
      return new ValueUnit(ValueUnit.Kind.ABBREVIATION, null, null, null, raw);
   }

   public String format(double value) {
      if (this.h4v12p == ValueUnit.Kind.ABBREVIATION) {
         return this.h8K55a;
      } else {
         double abs = Math.abs(value);
         double fraction = abs - Math.floor(abs);
         if (fraction > 0.0) {
            return fraction < 0.5 ? this.x5x6 : this.vjdm0;
         } else {
            int v = (int)abs % 100;
            int v1 = v % 10;
            if (v >= 11 && v <= 19) {
               return this.vjdm0;
            } else if (v1 == 1) {
               return this.fPr56;
            } else {
               return v1 >= 2 && v1 <= 4 ? this.x5x6 : this.vjdm0;
            }
         }
      }
   }

   public static enum Kind {
      COUNTABLE,
      ABBREVIATION;
   }
}
