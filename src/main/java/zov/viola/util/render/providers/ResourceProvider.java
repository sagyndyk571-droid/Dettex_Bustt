package zov.viola.util.render.providers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public final class ResourceProvider {
   private static final Gson GSON = new Gson();

   public static Identifier getShaderIdentifier(String name) {
      return Identifier.of("mre", "core/" + name);
   }

   public static JsonObject toJson(Identifier identifier) {
      return JsonParser.parseString(toString(identifier)).getAsJsonObject();
   }

   public static <T> T fromJsonToInstance(Identifier identifier, Class<T> clazz) {
      return (T)GSON.fromJson(toString(identifier), clazz);
   }

   public static String toString(Identifier identifier) {
      return toString(identifier, "\n");
   }

   public static String toString(Identifier identifier, String delimiter) {
      try {
         String var4;
         try (
            InputStream inputStream = MinecraftClient.getInstance().getResourceManager().open(identifier);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
         ) {
            var4 = reader.lines().collect(Collectors.joining(delimiter));
         }

         return var4;
      } catch (IOException var10) {
         throw new RuntimeException(var10);
      }
   }
}
