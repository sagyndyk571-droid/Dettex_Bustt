package zov.viola.util.alt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.Session.AccountType;
import zov.viola.mixin.IMinecraftClientAccessor;
import zov.viola.util.license.LicenseManager;

public final class AltManager {
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final Path ALTS_FILE = LicenseManager.dataDir().resolve("alts.json");
   private static final List<String> alts = new ArrayList<>();
   private static final String RANDOM_CONSONANTS = "bcdfghjklmnpqrstvwxz";
   private static final String RANDOM_VOWELS = "aeiouy";
   private static final String RANDOM_LETTERS = "abcdefghijklmnopqrstuvwxyz";
   private static String wye6Yc = "";
   private static String wa1qp = "";

   private AltManager() {
   }

   public static void load() {
      alts.clear();
      if (Files.exists(ALTS_FILE)) {
         try (Reader reader = Files.newBufferedReader(ALTS_FILE)) {
            AltManager.AltData data = (AltManager.AltData)gson.fromJson(reader, AltManager.AltData.class);
            if (data != null) {
               if (data.alts != null) {
                  alts.addAll(data.alts);
               }

               wa1qp = data.lastNick == null ? "" : data.lastNick;
            }
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }
   }

   public static void save() {
      try {
         Files.createDirectories(ALTS_FILE.getParent());
         AltManager.AltData data = new AltManager.AltData();
         data.alts = new ArrayList<>(alts);
         data.lastNick = wa1qp;
         Files.write(ALTS_FILE, gson.toJson(data).getBytes());
      } catch (IOException var1) {
         var1.printStackTrace();
      }
   }

   public static void applyLastNick() {
      if (!wa1qp.isEmpty()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.getSession() != null) {
            if (!wa1qp.equalsIgnoreCase(mc.getSession().getUsername())) {
               j9Uf(wa1qp);
            }
         }
      }
   }

   public static List<String> getAlts() {
      return new ArrayList<>(alts);
   }

   public static boolean isEmpty() {
      return alts.isEmpty();
   }

   public static void addAlt(String name) {
      String trimmed = name.trim();
      if (!trimmed.isEmpty()) {
         for (String alt : alts) {
            if (alt.equalsIgnoreCase(trimmed)) {
               return;
            }
         }

         alts.add(trimmed);
         save();
      }
   }

   public static void removeAlt(String name) {
      alts.removeIf(a -> a.equalsIgnoreCase(name));
      save();
   }

   public static String getCurrentAlt() {
      return MinecraftClient.getInstance().getSession().getUsername();
   }

   public static String randomNick() {
      Random random = new Random();
      int attempts = 0;

      String nick;
      do {
         StringBuilder sb = new StringBuilder();
         int length = 10 + random.nextInt(7);
         boolean consonant = random.nextBoolean();

         for (int i = 0; i < length; i++) {
            String pool = consonant ? "bcdfghjklmnpqrstvwxz" : "aeiouy";
            sb.append(pool.charAt(random.nextInt(pool.length())));
            if (random.nextInt(4) == 0) {
               consonant = !consonant;
            }
         }

         if (random.nextInt(3) == 0) {
            sb.append(random.nextInt(10)).append(random.nextInt(10));
         }

         if (sb.length() > 16) {
            sb.setLength(16);
         }

         nick = sb.toString();
      } while (nick.equals(wye6Yc) && ++attempts < 50);

      wye6Yc = nick;
      return nick;
   }

   public static void login(String name) {
      String username = name.trim();
      j9Uf(username);
      if (!username.equalsIgnoreCase(wa1qp)) {
         wa1qp = username;
         save();
      }
   }

   private static void j9Uf(String username) {
      MinecraftClient mc = MinecraftClient.getInstance();
      Session newSession = new Session(username, UUID.randomUUID(), "0", Optional.empty(), Optional.empty(), AccountType.MOJANG);
      ((IMinecraftClientAccessor)mc).setSession(newSession);
   }

   private static final class AltData {
      public List<String> alts = new ArrayList<>();
      public String lastNick = "";
   }
}
