package zov.viola.util.staff;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import lombok.Generated;
import zov.viola.util.license.LicenseManager;

public class StaffManager {
   private static final Set<Staff> staffList = new HashSet<>();
   private final File jEFi = LicenseManager.file("staff.json").toFile();

   public static void addStaff(Staff staff) {
      staffList.add(staff);
   }

   public static void removeStaff(String staff) {
      staffList.removeIf(s -> s.name.equalsIgnoreCase(staff));
   }

   public static boolean isStaff(String name) {
      for (Staff staff : staffList) {
         if (staff.name.equalsIgnoreCase(name)) {
            return true;
         }
      }

      return false;
   }

   public static void clearStaff() {
      staffList.clear();
   }

   public void save() {
      JsonArray array = new JsonArray();

      for (Staff staff : staffList) {
         array.add(staff.name);
      }

      try {
         if (!this.jEFi.getParentFile().exists()) {
            this.jEFi.getParentFile().mkdirs();
         }

         try (Writer writer = new OutputStreamWriter(new FileOutputStream(this.jEFi), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(array, writer);
         }
      } catch (IOException var7) {
         var7.printStackTrace();
      }
   }

   public void load() {
      if (this.jEFi.exists()) {
         try {
            try (Reader reader = new InputStreamReader(new FileInputStream(this.jEFi), StandardCharsets.UTF_8)) {
               JsonElement element = new JsonParser().parse(reader);
               if (element.isJsonArray()) {
                  JsonArray array = element.getAsJsonArray();
                  staffList.clear();

                  for (JsonElement el : array) {
                     String name = el.getAsString();
                     staffList.add(new Staff(name));
                  }

                  return;
               }
            }
         } catch (IOException var9) {
            var9.printStackTrace();
         }
      }
   }

   @Generated
   public static Set<Staff> getStaffList() {
      return staffList;
   }
}
