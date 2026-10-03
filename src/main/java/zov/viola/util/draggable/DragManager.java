package zov.viola.util.draggable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import zov.viola.module.Module;
import zov.viola.util.IMinecraft;
import zov.viola.util.license.LicenseManager;

public class DragManager implements IMinecraft {
   @Expose
   public static LinkedHashMap<String, Draggable> draggableElements = new LinkedHashMap<>();
   private static final File dataFile = new File(MinecraftClient.getInstance().runDirectory, LicenseManager.dataDir().resolve("draggable.json").toString());
   private static final Gson jsonParser = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
   private static Draggable f7800yw = null;

   public static Draggable installDrag(Module module, String name, float x, float y) {
      draggableElements.put(name, new Draggable(module, name, x, y));
      return draggableElements.get(name);
   }

   public void load() {
      if (!dataFile.exists()) {
         dataFile.getParentFile().mkdirs();
      } else {
         try (Reader reader = new FileReader(dataFile)) {
            Type type = (new TypeToken<LinkedHashMap<String, Draggable>>() {}).getType();
            LinkedHashMap<String, Draggable> loaded = (LinkedHashMap<String, Draggable>)jsonParser.fromJson(reader, type);
            if (loaded != null) {
               for (Entry<String, Draggable> entry : loaded.entrySet()) {
                  Draggable loadedDraggable = entry.getValue();
                  Draggable existing = draggableElements.get(entry.getKey());
                  if (existing != null) {
                     existing.setX(loadedDraggable.getX());
                     existing.setY(loadedDraggable.getY());
                  } else {
                     draggableElements.put(entry.getKey(), loadedDraggable);
                  }
               }
            }
         } catch (IOException var10) {
            var10.printStackTrace();
         }
      }
   }

   public void saveDraggables() {
      try {
         dataFile.getParentFile().mkdirs();

         try (Writer writer = new FileWriter(dataFile)) {
            jsonParser.toJson(draggableElements, writer);
         }
      } catch (IOException var6) {
         var6.printStackTrace();
      }
   }

   public static void onClickAll(int button) {
      if (button == 0) {
         List<Draggable> elements = new ArrayList<>(draggableElements.values());
         Collections.reverse(elements);

         for (Draggable draggable : elements) {
            if (draggable.isHovering() && draggable.getModule() != null && draggable.getModule().isEnabled()) {
               f7800yw = draggable;
               draggable.onClick(button);
               break;
            }
         }
      }
   }

   public static void onDrawAll() {
      for (Draggable draggable : draggableElements.values()) {
         if (draggable.getModule() != null && draggable.getModule().isEnabled()) {
            draggable.onDraw();
         }
      }
   }

   public static void onReleaseAll(int button) {
      if (f7800yw != null) {
         f7800yw.onRelease(button);
         f7800yw = null;
      }
   }
}
