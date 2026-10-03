package zov.viola.util.license;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.fabricmc.loader.api.FabricLoader;
import zov.viola.obf.D;

public final class LicenseManager {
   private static final Path HWID_CACHE = Paths.get("viola", ".hwid_cache");
   private static String cVdhQd4;
   private static LicenseManager.State iilUk;

   private static String ms6N9r() {
      String s = System.getProperty("viola.server");
      if (s != null && !s.trim().isEmpty()) {
         return s.trim();
      } else {
         s = System.getenv("VIOLA_SERVER");
         if (s != null && !s.trim().isEmpty()) {
            return s.trim();
         } else {
            try {
               Path p = FabricLoader.getInstance().getGameDir().resolve("viola_server.txt");
               if (Files.exists(p)) {
                  String line = Files.readAllLines(p).stream().findFirst().orElse("").trim();
                  if (!line.isEmpty()) {
                     return line;
                  }
               }
            } catch (Exception var3) {
            }

            return D.k(
               new int[]{
                  17,
                  172,
                  163,
                  155,
                  10,
                  226,
                  248,
                  196,
                  15,
                  177,
                  184,
                  135,
                  24,
                  245,
                  182,
                  155,
                  16,
                  246,
                  161,
                  130,
                  22,
                  180,
                  182,
                  198,
                  10,
                  172,
                  178,
                  155,
                  24,
                  168,
                  165,
                  132,
                  87,
                  175,
                  184,
                  153,
                  18,
                  189,
                  165,
                  152,
                  87,
                  188,
                  178,
                  157
               },
               new int[]{121, 216, 215, 235}
            );
         }
      }
   }

   private LicenseManager() {
   }

   public static String hwid() {
      if (cVdhQd4 == null) {
         cVdhQd4 = xs16lc();
      }

      return cVdhQd4;
   }

   private static String xs16lc() {
      try {
         String raw = JarBinding.readLicense();
         if (raw != null) {
            String stored = aQ89ok(raw).get("HWID");
            if (stored != null && !stored.isEmpty()) {
               oG33M(stored);
               return stored;
            }
         }
      } catch (Exception var3) {
      }

      try {
         if (Files.exists(HWID_CACHE)) {
            String line = Files.readAllLines(HWID_CACHE).stream().findFirst().orElse("").trim();
            if (!line.isEmpty()) {
               return line;
            }
         }
      } catch (Exception var2) {
      }

      String c = e4MdaY();
      oG33M(c);
      return c;
   }

   private static void oG33M(String h) {
      try {
         Files.createDirectories(HWID_CACHE.getParent());
         Files.write(HWID_CACHE, (h + "\n").getBytes(StandardCharsets.UTF_8));
      } catch (Exception var2) {
      }
   }

   public static Path dataDir() {
      return Paths.get("viola", hwid());
   }

   public static Path file(String name) {
      return dataDir().resolve(name);
   }

   public static synchronized boolean isActivated() {
      return p93OAL().valid;
   }

   public static synchronized String status() {
      return p93OAL().reason;
   }

   public static synchronized Keys.Type activeType() {
      return p93OAL().type;
   }

   public static synchronized long expiry() {
      return p93OAL().expiry;
   }

   private static LicenseManager.State p93OAL() {
      if (iilUk == null) {
         iilUk = pvwmpx();
      }

      return iilUk;
   }

   private static LicenseManager.State pvwmpx() {
      try {
         String raw = JarBinding.readLicense();
         if (raw == null) {
            return new LicenseManager.State(false, "NO_LICENSE");
         } else {
            Map<String, String> m = aQ89ok(raw);
            String key = m.get("KEY");
            String hw = m.get("HWID");
            String type = m.get("TYPE");
            String exp = m.get("EXP");
            if (key == null || hw == null || type == null) {
               return new LicenseManager.State(false, "CORRUPT");
            } else if (!hw.equals(hwid())) {
               return new LicenseManager.State(false, "HWID_MISMATCH");
            } else {
               long expiry = exp == null ? 0L : Long.parseLong(exp);
               Map<String, Object> resp = r9aAnk("/api/cheat/verify", "{\"lic\":\"" + rV12(key) + "\",\"hwid\":\"" + rV12(hwid()) + "\"}");
               if (resp != null) {
                  boolean ok = Boolean.TRUE.equals(resp.get("ok"));
                  if (ok) {
                     long sExp = h8zGhD(resp.get("exp"));
                     if (sExp != expiry) {
                        JarBinding.writeLicense(key, hwid(), Keys.typeOf(type), sExp);
                     }

                     return new LicenseManager.State(true, "OK", Keys.typeOf(type), sExp);
                  } else {
                     return new LicenseManager.State(false, xw6k(resp.get("reason"), "SERVER_REJECT"));
                  }
               } else {
                  return expiry != 0L && System.currentTimeMillis() > expiry
                     ? new LicenseManager.State(false, "EXPIRED")
                     : new LicenseManager.State(true, "OK_OFFLINE", Keys.typeOf(type), expiry);
               }
            }
         }
      } catch (Exception var12) {
         return new LicenseManager.State(false, "CORRUPT");
      }
   }

   public static synchronized boolean tryActivate(String input) {
      Keys.Type t = Keys.match(Keys.normalize(input));
      if (t == null) {
         iilUk = new LicenseManager.State(false, "INVALID");
         return false;
      } else {
         String key = Keys.normalize(input);
         String existing = JarBinding.readLicense();
         if (existing != null) {
            Map<String, String> m = aQ89ok(existing);
            String hw = m.get("HWID");
            if (hw != null && !hw.equals(hwid())) {
               iilUk = new LicenseManager.State(false, "HWID_MISMATCH");
               return false;
            }
         }

         Map<String, Object> resp = r9aAnk("/api/cheat/activate", "{\"lic\":\"" + rV12(key) + "\",\"hwid\":\"" + rV12(hwid()) + "\"}");
         if (resp == null) {
            iilUk = new LicenseManager.State(false, "SERVER_DOWN");
            return false;
         } else if (Boolean.TRUE.equals(resp.get("ok"))) {
            long exp = h8zGhD(resp.get("exp"));
            JarBinding.writeLicense(key, hwid(), t, exp);
            iilUk = new LicenseManager.State(true, "OK", t, exp);
            return true;
         } else {
            iilUk = new LicenseManager.State(false, xw6k(resp.get("reason"), "SERVER_REJECT"));
            return false;
         }
      }
   }

   private static Map<String, Object> r9aAnk(String path, String body) {
      try {
         URL u = new URL(ms6N9r() + path);
         HttpURLConnection c = (HttpURLConnection)u.openConnection();
         c.setRequestMethod("POST");
         c.setDoOutput(true);
         c.setConnectTimeout(8000);
         c.setReadTimeout(8000);
         c.setRequestProperty(
            "Content-Type",
            D.k(
               new int[]{
                  118,
                  240,
                  221,
                  234,
                  126,
                  227,
                  204,
                  242,
                  126,
                  239,
                  195,
                  169,
                  125,
                  243,
                  194,
                  232,
                  44,
                  160,
                  206,
                  238,
                  118,
                  242,
                  222,
                  227,
                  99,
                  189,
                  216,
                  242,
                  113,
                  173,
                  149
               },
               new int[]{23, 128, 173, 134}
            )
         );

         try (OutputStream os = c.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
         }

         int code = c.getResponseCode();
         InputStream is = code == 200 ? c.getInputStream() : c.getErrorStream();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] buf = new byte[4096];

         int n;
         while ((n = is.read(buf)) > 0) {
            baos.write(buf, 0, n);
         }

         is.close();
         if (code != 200) {
            return null;
         } else {
            String text = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            Object parsed = dzsmj(text);
            return (Map<String, Object>)(parsed instanceof Map ? (Map)parsed : new HashMap<>());
         }
      } catch (Exception var14) {
         return null;
      }
   }

   private static Map<String, String> aQ89ok(String raw) {
      Map<String, String> map = new HashMap<>();

      for (String line : raw.split("\n")) {
         line = line.trim();
         int idx = line.indexOf(61);
         if (idx >= 0) {
            map.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
         }
      }

      return map;
   }

   private static String rV12(String s) {
      return s.replace("\\", "\\\\").replace("\"", "\\\"");
   }

   private static long h8zGhD(Object o) {
      if (o == null) {
         return 0L;
      } else if (o instanceof Number) {
         return ((Number)o).longValue();
      } else {
         try {
            return Long.parseLong(o.toString());
         } catch (Exception var2) {
            return 0L;
         }
      }
   }

   private static String xw6k(Object o, String def) {
      return o == null ? def : o.toString();
   }

   private static Object dzsmj(String s) {
      if (s == null) {
         return null;
      } else {
         int[] p = new int[]{0};
         return gchejK(s, p);
      }
   }

   private static void pb747ua(String s, int[] p) {
      while (p[0] < s.length()) {
         char c = s.charAt(p[0]);
         if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\r') {
            p[0]++;
            continue;
         }
         break;
      }
   }

   private static Object gchejK(String s, int[] p) {
      pb747ua(s, p);
      if (p[0] >= s.length()) {
         return null;
      } else {
         char c = s.charAt(p[0]);
         if (c == '{') {
            return zWzuLq(s, p);
         } else if (c == '[') {
            return c67au5A(s, p);
         } else if (c == '"') {
            return hhK9vjo(s, p);
         } else if (c == 't' || c == 'f') {
            return cWQld2j(s, p);
         } else if (c == 'n') {
            p[0] += 4;
            return null;
         } else {
            return la6FraI(s, p);
         }
      }
   }

   private static Map<String, Object> zWzuLq(String s, int[] p) {
      Map<String, Object> m = new HashMap<>();
      p[0]++;
      pb747ua(s, p);
      if (p[0] < s.length() && s.charAt(p[0]) == '}') {
         p[0]++;
         return m;
      } else {
         while (p[0] < s.length()) {
            pb747ua(s, p);
            String key = hhK9vjo(s, p);
            pb747ua(s, p);
            if (p[0] < s.length() && s.charAt(p[0]) == ':') {
               p[0]++;
            }

            Object val = gchejK(s, p);
            m.put(key, val);
            pb747ua(s, p);
            if (p[0] >= s.length() || s.charAt(p[0]) != ',') {
               if (p[0] < s.length() && s.charAt(p[0]) == '}') {
                  p[0]++;
               }
               break;
            }

            p[0]++;
         }

         return m;
      }
   }

   private static List<Object> c67au5A(String s, int[] p) {
      List<Object> list = new ArrayList<>();
      p[0]++;
      pb747ua(s, p);
      if (p[0] < s.length() && s.charAt(p[0]) == ']') {
         p[0]++;
         return list;
      } else {
         while (p[0] < s.length()) {
            Object v = gchejK(s, p);
            list.add(v);
            pb747ua(s, p);
            if (p[0] >= s.length() || s.charAt(p[0]) != ',') {
               if (p[0] < s.length() && s.charAt(p[0]) == ']') {
                  p[0]++;
               }
               break;
            }

            p[0]++;
         }

         return list;
      }
   }

   private static String hhK9vjo(String s, int[] p) {
      p[0]++;
      StringBuilder sb = new StringBuilder();

      while (p[0] < s.length()) {
         char c = s.charAt((int)(p[0]++));
         if (c == '\\') {
            if (p[0] < s.length()) {
               char e = s.charAt((int)(p[0]++));
               switch (e) {
                  case '"':
                     sb.append('"');
                     break;
                  case '/':
                     sb.append('/');
                     break;
                  case '\\':
                     sb.append('\\');
                     break;
                  case 'b':
                     sb.append('\b');
                     break;
                  case 'f':
                     sb.append('\f');
                     break;
                  case 'n':
                     sb.append('\n');
                     break;
                  case 'r':
                     sb.append('\r');
                     break;
                  case 't':
                     sb.append('\t');
                     break;
                  case 'u':
                     if (p[0] + 4 <= s.length()) {
                        String hex = s.substring(p[0], p[0] + 4);

                        try {
                           sb.append((char)Integer.parseInt(hex, 16));
                        } catch (Exception var7) {
                        }

                        p[0] += 4;
                     }
                     break;
                  default:
                     sb.append(e);
               }
            }
         } else {
            if (c == '"') {
               break;
            }

            sb.append(c);
         }
      }

      return sb.toString();
   }

   private static Boolean cWQld2j(String s, int[] p) {
      if (s.startsWith("true", p[0])) {
         p[0] += 4;
         return Boolean.TRUE;
      } else if (s.startsWith("false", p[0])) {
         p[0] += 5;
         return Boolean.FALSE;
      } else {
         return Boolean.FALSE;
      }
   }

   private static Number la6FraI(String s, int[] p) {
      int start = p[0];
      if (p[0] < s.length() && (s.charAt(p[0]) == '-' || s.charAt(p[0]) == '+')) {
         p[0]++;
      }

      while (p[0] < s.length()) {
         char c = s.charAt(p[0]);
         if ((c < '0' || c > '9') && c != '.' && c != 'e' && c != 'E' && c != '-' && c != '+') {
            break;
         }

         p[0]++;
      }

      String num = s.substring(start, p[0]);

      try {
         if (!num.contains(".") && !num.contains("e") && !num.contains("E")) {
            long l = Long.parseLong(num);
            return l;
         } else {
            return Double.parseDouble(num);
         }
      } catch (Exception var6) {
         return 0;
      }
   }

   private static String e4MdaY() {
      try {
         StringBuilder sb = new StringBuilder();
         sb.append(
               bYmD(
                  D.k(
                     new int[]{
                        112,
                        224,
                        64,
                        233,
                        117,
                        228,
                        76,
                        240,
                        17,
                        201,
                        86,
                        233,
                        57,
                        201,
                        70,
                        248,
                        120,
                        240,
                        76,
                        243,
                        107,
                        149,
                        122,
                        222,
                        55,
                        202,
                        85,
                        232,
                        44,
                        194,
                        87,
                        206,
                        33,
                        212,
                        81,
                        248,
                        53,
                        247,
                        87,
                        242,
                        60,
                        210,
                        70,
                        233,
                        113,
                        137,
                        112,
                        200,
                        17,
                        227
                     },
                     new int[]{88, 167, 37, 157}
                  )
               )
            )
            .append('|');
         sb.append(
               bYmD(
                  D.k(
                     new int[]{
                        125,
                        45,
                        246,
                        52,
                        120,
                        41,
                        250,
                        45,
                        28,
                        4,
                        224,
                        52,
                        52,
                        4,
                        240,
                        37,
                        117,
                        61,
                        250,
                        46,
                        102,
                        88,
                        204,
                        16,
                        39,
                        5,
                        240,
                        37,
                        38,
                        25,
                        252,
                        50,
                        124,
                        68,
                        195,
                        50,
                        58,
                        9,
                        246,
                        51,
                        38,
                        5,
                        225,
                        9,
                        49
                     },
                     new int[]{85, 106, 147, 64}
                  )
               )
            )
            .append('|');
         sb.append(
               bYmD(
                  D.k(
                     new int[]{
                        144,
                        151,
                        1,
                        224,
                        149,
                        147,
                        13,
                        249,
                        241,
                        190,
                        23,
                        224,
                        217,
                        190,
                        7,
                        241,
                        152,
                        135,
                        13,
                        250,
                        139,
                        226,
                        59,
                        214,
                        217,
                        163,
                        1,
                        214,
                        215,
                        177,
                        22,
                        240,
                        145,
                        254,
                        55,
                        241,
                        202,
                        185,
                        5,
                        248,
                        246,
                        165,
                        9,
                        246,
                        221,
                        162
                     },
                     new int[]{184, 208, 100, 148}
                  )
               )
            )
            .append('|');
         sb.append(
               bYmD(
                  D.k(
                     new int[]{
                        53,
                        166,
                        99,
                        17,
                        48,
                        162,
                        111,
                        8,
                        84,
                        143,
                        117,
                        17,
                        124,
                        143,
                        101,
                        0,
                        61,
                        182,
                        111,
                        11,
                        46,
                        211,
                        89,
                        39,
                        84,
                        174,
                        85,
                        76,
                        51,
                        178,
                        99,
                        23,
                        116,
                        128,
                        106,
                        43,
                        104,
                        140,
                        100,
                        0,
                        111
                     },
                     new int[]{29, 225, 6, 101}
                  )
               )
            )
            .append('|');

         try {
            for (NetworkInterface nif : Collections.list(NetworkInterface.getNetworkInterfaces())) {
               if (!nif.isLoopback() && !nif.isVirtual() && nif.isUp()) {
                  byte[] mac = nif.getHardwareAddress();
                  if (mac != null) {
                     for (byte b : mac) {
                        sb.append(String.format("%02x", b));
                     }

                     sb.append(',');
                  }
               }
            }
         } catch (Exception var8) {
         }

         sb.append(System.getenv("COMPUTERNAME"));
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         byte[] dig = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
         StringBuilder hex = new StringBuilder();

         for (byte b : dig) {
            hex.append(String.format("%02x", b));
         }

         return hex.toString();
      } catch (Exception var9) {
         return "UNKNOWN_" + System.getProperty("user.name");
      }
   }

   private static String bYmD(String script) {
      try {
         Process p = Runtime.getRuntime().exec(new String[]{"powershell", "-NoProfile", "-Command", script});
         boolean finished = p.waitFor(3L, TimeUnit.SECONDS);
         if (!finished) {
            p.destroyForcibly();
            return "";
         } else {
            StringBuilder out = new StringBuilder();

            String line;
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
               while ((line = r.readLine()) != null) {
                  line = line.trim();
                  if (!line.isEmpty()) {
                     out.append(line).append(' ');
                  }
               }
            }

            return out.toString().trim();
         }
      } catch (Exception var9) {
         return "";
      }
   }

   private static final class State {
      final boolean valid;
      final String reason;
      final Keys.Type type;
      final long expiry;

      State(boolean valid, String reason) {
         this(valid, reason, null, 0L);
      }

      State(boolean valid, String reason, Keys.Type type, long expiry) {
         this.valid = valid;
         this.reason = reason;
         this.type = type;
         this.expiry = expiry;
      }
   }
}
