package zov.viola.util.render.providers;

public final class ClientPalette {
   private ClientPalette() {
   }

   public static int pick(int themed) {
      return Palette.UNIFIED ? ColorProvider.getAccent() : themed;
   }

   public static int pickTwo(int themed) {
      return Palette.UNIFIED ? ColorProvider.getAccent() : themed;
   }
}
