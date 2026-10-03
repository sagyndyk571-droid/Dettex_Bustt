package zov.viola.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.module.list.player.Panic;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.Setting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.module.settings.TextSetting;
import zov.viola.module.settings.ThemeSetting;
import zov.viola.module.settings.impl.Theme;
import zov.viola.obf.D;
import zov.viola.util.license.LicenseManager;

public class ConfigManager {
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final Path CONFIG_FOLDER = LicenseManager.dataDir().resolve("configs");

   public static void save(String name) {
      JsonObject root = new JsonObject();

      for (Module module : Viola.getInstance().getModuleStorage().getModules()) {
         if (!(module instanceof Panic)) {
            JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("enabled", module.isEnabled());
            moduleObject.addProperty("keybind", module.getKey());
            JsonObject settingsObject = new JsonObject();

            for (Setting setting : module.getSettings()) {
               if (setting instanceof BooleanSetting s) {
                  settingsObject.addProperty(setting.getName(), s.getValue());
               } else if (setting instanceof BindSetting s) {
                  settingsObject.addProperty(setting.getName(), s.getValue());
               } else if (setting instanceof ModeSetting s) {
                  settingsObject.addProperty(setting.getName(), s.getValue());
               } else if (setting instanceof SliderSetting s) {
                  settingsObject.addProperty(setting.getName(), s.getValue());
               } else if (setting instanceof ColorSetting s) {
                  settingsObject.addProperty(setting.getName(), Integer.toUnsignedLong(s.getValue()));
               } else if (setting instanceof ThemeSetting s) {
                  settingsObject.addProperty(setting.getName(), s.getValue().name);
               } else if (!(setting instanceof ModeListSetting s)) {
                  if (setting instanceof TextSetting sx) {
                     settingsObject.addProperty(setting.getName(), sx.getValue());
                  }
               } else {
                  JsonArray enabledModes = new JsonArray();

                  for (String name2 : s.getEnabledModules()) {
                     enabledModes.add(name2);
                  }

                  settingsObject.add(setting.getName(), enabledModes);
               }
            }

            moduleObject.add("settings", settingsObject);
            root.add(module.getName(), moduleObject);
         }
      }

      try {
         Files.createDirectories(CONFIG_FOLDER);
         Path configFile = CONFIG_FOLDER.resolve(name + ".json");
         Files.write(configFile, gson.toJson(root).getBytes());
      } catch (IOException var19) {
         var19.printStackTrace();
      }
   }

   public static void load(String name) {
      Path configFile = CONFIG_FOLDER.resolve(name + ".json");
      if (Files.exists(configFile)) {
         try (Reader reader = Files.newBufferedReader(configFile)) {
            JsonObject root = (JsonObject)gson.fromJson(reader, JsonObject.class);

            for (Module module : Viola.getInstance().getModuleStorage().getModules()) {
               if (!(module instanceof Panic)) {
                  JsonElement moduleElement = em8gd6b(root, module.getName());
                  if (moduleElement != null && moduleElement.isJsonObject()) {
                     JsonObject moduleObject = moduleElement.getAsJsonObject();
                     if (moduleObject.has("enabled")) {
                        boolean enabled = moduleObject.get("enabled").getAsBoolean();
                        module.setEnabled(enabled);
                     }

                     if (moduleObject.has("keybind")) {
                        int keybind = moduleObject.get("keybind").getAsInt();
                        module.setKey(keybind);
                     }

                     if (moduleObject.has("settings")) {
                        JsonObject settingsObject = moduleObject.getAsJsonObject("settings");

                        for (Setting setting : module.getSettings()) {
                           JsonElement element = em8gd6b(settingsObject, setting.getName());
                           if (element != null) {
                              if (setting instanceof BooleanSetting s) {
                                 s.setValue(element.getAsBoolean());
                              } else if (setting instanceof BindSetting s) {
                                 s.setValue(element.getAsInt());
                              } else if (setting instanceof ModeSetting s) {
                                 s.setValue(element.getAsString());
                              } else if (setting instanceof SliderSetting s) {
                                 s.setValue(element.getAsDouble());
                              } else if (setting instanceof ColorSetting s) {
                                 Integer color = sNnWjy(element);
                                 if (color != null) {
                                    s.setValue(color);
                                 }
                              } else if (setting instanceof ThemeSetting sx) {
                                 String themeName = element.getAsString();

                                 for (Theme theme : sx.getThemes()) {
                                    if (theme.name.equals(themeName)) {
                                       sx.setValue(theme);
                                       break;
                                    }
                                 }
                              } else if (setting instanceof ModeListSetting sx && element.isJsonArray()) {
                                 JsonArray array = element.getAsJsonArray();
                                 List<String> enabled = new ArrayList<>();

                                 for (JsonElement e : array) {
                                    enabled.add(e.getAsString());
                                 }

                                 for (BooleanSetting subSetting : sx.getSettings()) {
                                    subSetting.setValue(enabled.contains(subSetting.getName()));
                                 }
                              } else if (setting instanceof TextSetting sx && element.isJsonPrimitive()) {
                                 sx.setValue(element.getAsString());
                              }
                           }
                        }
                     }
                  }
               }
            }
         } catch (IOException var27) {
            var27.printStackTrace();
         }
      }
   }

   private static JsonElement em8gd6b(JsonObject object, String currentName) {
      if (object == null) {
         return null;
      } else {
         for (String candidate : ConfigKeyAliases.candidates(currentName)) {
            if (object.has(candidate)) {
               return object.get(candidate);
            }
         }

         return null;
      }
   }

   private static Integer sNnWjy(JsonElement element) {
      if (element != null && !element.isJsonNull() && element.isJsonPrimitive()) {
         String value = element.getAsString().trim();
         int radix = 10;
         if (value.startsWith("#")) {
            value = value.substring(1);
            radix = 16;
         } else if (value.regionMatches(true, 0, "0x", 0, 2)) {
            value = value.substring(2);
            radix = 16;
         }

         try {
            BigInteger parsed = new BigInteger(value, radix);
            BigInteger minimum = radix == 10 ? BigInteger.valueOf(-2147483648L) : BigInteger.ZERO;
            BigInteger maximum = BigInteger.valueOf(4294967295L);
            return parsed.compareTo(minimum) >= 0 && parsed.compareTo(maximum) <= 0 ? parsed.intValue() : null;
         } catch (NumberFormatException var6) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static List<String> getConfigs() {
      List<String> configs = new ArrayList<>();

      try {
         if (Files.exists(CONFIG_FOLDER)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(CONFIG_FOLDER, "*.json")) {
               for (Path path : stream) {
                  String fileName = path.getFileName().toString();
                  if (fileName.endsWith(".json")) {
                     configs.add(fileName.substring(0, fileName.length() - 5));
                  }
               }
            }
         }
      } catch (IOException var7) {
         var7.printStackTrace();
      }

      return configs;
   }
}
