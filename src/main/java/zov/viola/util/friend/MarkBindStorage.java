package zov.viola.util.friend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class MarkBindStorage {
   private static final File file = new File("viola/markbind.json");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static int vStf = -1;

   public static int get() {
      return vStf;
   }

   public static void set(int key) {
      vStf = key;
      save();
   }

   public static void save() {
      try {
         file.getParentFile().mkdirs();

         try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(new MarkBindStorage.Data(vStf), writer);
         }
      } catch (IOException var5) {
      }
   }

   public static void load() {
      if (file.exists()) {
         try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            MarkBindStorage.Data data = (MarkBindStorage.Data)gson.fromJson(reader, MarkBindStorage.Data.class);
            if (data != null && data.bind != 0) {
               vStf = data.bind;
            }
         } catch (IOException var5) {
         }
      }
   }

   private static class Data {
      final int bind;

      Data(int bind) {
         this.bind = bind;
      }
   }
}
