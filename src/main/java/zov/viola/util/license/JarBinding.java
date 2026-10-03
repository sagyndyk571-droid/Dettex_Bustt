package zov.viola.util.license;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import zov.viola.Viola;

public final class JarBinding {
   public static final String LIC_ENTRY = "META-INF/viola.lic";
   private static final String FALLBACK = ".viola_license";

   private JarBinding() {
   }

   public static String readLicense() {
      String fromJar = mhx3w8W();
      if (fromJar != null) {
         return fromJar;
      } else {
         File fb = new File(".viola_license");
         if (fb.exists()) {
            try {
               return new String(Files.readAllBytes(fb.toPath()), StandardCharsets.UTF_8);
            } catch (IOException var3) {
            }
         }

         return null;
      }
   }

   public static boolean writeLicense(String key, String hwid, Keys.Type type, long exp) {
      String content = "VIOLA_LIC_v1\nKEY=" + key + "\nHWID=" + hwid + "\nTYPE=" + type.name() + "\nEXP=" + exp + "\n";
      byte[] data = content.getBytes(StandardCharsets.UTF_8);
      if (eesp(data)) {
         return true;
      } else {
         try {
            Files.write(new File(".viola_license").toPath(), data);
            return true;
         } catch (IOException var8) {
            return false;
         }
      }
   }

   private static String mhx3w8W() {
      try {
         File jar = tWuqO1();
         if (jar != null && jar.isFile() && jar.getName().endsWith(".jar")) {
            String var7;
            try (JarFile jf = new JarFile(jar)) {
               ZipEntry e = jf.getEntry("META-INF/viola.lic");
               if (e == null) {
                  return null;
               }

               try (InputStream is = jf.getInputStream(e)) {
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  byte[] buf = new byte[4096];

                  int n;
                  while ((n = is.read(buf)) > 0) {
                     baos.write(buf, 0, n);
                  }

                  var7 = new String(baos.toByteArray(), StandardCharsets.UTF_8);
               }
            }

            return var7;
         } else {
            return null;
         }
      } catch (Exception var12) {
         return null;
      }
   }

   private static boolean eesp(byte[] data) {
      try {
         File jar = tWuqO1();
         if (jar != null && jar.isFile() && jar.getName().endsWith(".jar")) {
            File tmp = new File(jar.getAbsolutePath() + ".tmp");
            JarFile in = new JarFile(jar);

            try {
               Manifest manifest = in.getManifest();

               try (JarOutputStream out = manifest != null
                     ? new JarOutputStream(new FileOutputStream(tmp), manifest)
                     : new JarOutputStream(new FileOutputStream(tmp))) {
                  Enumeration<JarEntry> entries = in.entries();

                  while (entries.hasMoreElements()) {
                     JarEntry je = entries.nextElement();
                     String name = je.getName();
                     if (!name.equals("META-INF/viola.lic") && (manifest == null || !name.equalsIgnoreCase("META-INF/MANIFEST.MF"))) {
                        if (je.isDirectory()) {
                           out.putNextEntry(new JarEntry(name));
                           out.closeEntry();
                        } else {
                           out.putNextEntry(new JarEntry(name));

                           try (InputStream is = in.getInputStream(je)) {
                              byte[] buf = new byte[8192];

                              int n;
                              while ((n = is.read(buf)) > 0) {
                                 out.write(buf, 0, n);
                              }
                           }

                           out.closeEntry();
                        }
                     }
                  }

                  out.putNextEntry(new JarEntry("META-INF/viola.lic"));
                  out.write(data);
                  out.closeEntry();
               }
            } catch (Throwable var17) {
               try {
                  in.close();
               } catch (Throwable var12) {
                  var17.addSuppressed(var12);
               }

               throw var17;
            }

            in.close();
            File bak = new File(jar.getAbsolutePath() + ".bak");
            if (bak.exists()) {
               bak.delete();
            }

            Files.copy(jar.toPath(), bak.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.move(tmp.toPath(), jar.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (bak.exists()) {
               bak.delete();
            }

            return true;
         } else {
            return false;
         }
      } catch (Exception var18) {
         return false;
      }
   }

   private static File tWuqO1() {
      try {
         return new File(Viola.class.getProtectionDomain().getCodeSource().getLocation().toURI());
      } catch (Exception var1) {
         return null;
      }
   }
}
