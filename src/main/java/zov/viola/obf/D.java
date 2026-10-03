package zov.viola.obf;

public final class D {
   public static String k(int[] var0, int[] var1) {
      StringBuilder var2 = new StringBuilder(var0.length);

      for (int var3 = 0; var3 < var0.length; var3++) {
         var2.append((char)(var0[var3] ^ var1[var3 % var1.length] & 0xFF));
      }

      return var2.toString();
   }
}
