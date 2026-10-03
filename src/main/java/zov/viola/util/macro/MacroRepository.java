package zov.viola.util.macro;

import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.util.Formatting;
import zov.viola.Viola;
import zov.viola.event.list.EventKeyInput;
import zov.viola.util.IMinecraft;
import zov.viola.util.QuickLogger;
import zov.viola.util.keyboard.KeyStorage;
import zov.viola.util.license.LicenseManager;

public class MacroRepository implements IMinecraft, QuickLogger {
   private final File qCbz = LicenseManager.file("macros.json").toFile();
   private final Gson ogb3 = new GsonBuilder().setPrettyPrinting().create();
   private final List<Macro> a6iYPa = new ArrayList<>();

   public MacroRepository() {
      Viola.getInstance().getEventBus().register(this);
   }

   public boolean isEmpty() {
      return this.a6iYPa.isEmpty();
   }

   public void addMacro(String message, int key) {
      this.a6iYPa.add(new Macro(message, key));
   }

   public boolean hasMacro(String macroName) {
      for (Macro macro : this.a6iYPa) {
         if (KeyStorage.getKey(macro.key()).equalsIgnoreCase(macroName)) {
            return true;
         }
      }

      return false;
   }

   public void deleteMacro(String name) {
      this.a6iYPa.removeIf(macro -> KeyStorage.getKey(macro.key()).equalsIgnoreCase(name));
   }

   public void clearList() {
      this.a6iYPa.clear();
   }

   public void save() {
      try {
         this.qCbz.getParentFile().mkdirs();

         try (Writer writer = new OutputStreamWriter(new FileOutputStream(this.qCbz), StandardCharsets.UTF_8)) {
            this.ogb3.toJson(this.a6iYPa, writer);
         }
      } catch (IOException var6) {
         this.logDirect("Ошибка при сохранении макросов: " + var6.getMessage(), Formatting.RED);
      }
   }

   public boolean removeByKey(int key) {
      return this.a6iYPa.removeIf(m -> m.key() == key);
   }

   public void load() {
      if (this.qCbz.exists()) {
         try (Reader reader = new InputStreamReader(new FileInputStream(this.qCbz), StandardCharsets.UTF_8)) {
            Type listType = (new TypeToken<ArrayList<Macro>>() {}).getType();
            List<Macro> loaded = (List<Macro>)this.ogb3.fromJson(reader, listType);
            if (loaded != null) {
               this.a6iYPa.clear();
               this.a6iYPa.addAll(loaded);
            }
         } catch (IOException var6) {
            this.logDirect("Ошибка при загрузке макросов: " + var6.getMessage(), Formatting.RED);
         }
      }
   }

   @Subscribe
   public void onKey(EventKeyInput event) {
      int key = event.getKey();
      if (key != -1) {
         if (mc.player != null && event.getAction() != 0 && mc.currentScreen == null) {
            for (Macro macro : this.a6iYPa) {
               if (macro.key() == key) {
                  String msg = macro.message();
                  if (msg.startsWith(".")) {
                     msg = msg.substring(1);
                     Viola.getInstance().getCommandDispatcher().runCommand(msg);
                  } else if (msg.startsWith("/")) {
                     mc.getNetworkHandler().sendChatCommand(msg.replaceFirst("/", ""));
                  } else {
                     mc.getNetworkHandler().sendChatMessage(msg);
                  }
               }
            }
         }
      }
   }

   @Generated
   public File getFile() {
      return this.qCbz;
   }

   @Generated
   public Gson getGson() {
      return this.ogb3;
   }

   @Generated
   public List<Macro> getMacroList() {
      return this.a6iYPa;
   }
}
