package zov.viola.util.neuro.rotation;

import ai.djl.ModelException;
import ai.djl.translate.TranslateException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.Generated;
import zov.viola.obf.D;
import zov.viola.util.chat.ChatUtil;

public class AIRotationManager {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Path AI_DIR = Paths.get(".onetap", "ai");
   private static final Path DATASETS_DIR = AI_DIR.resolve("datasets");
   private static final Path MODELS_DIR = AI_DIR.resolve("models");
   private static AIRotationModel na5D = null;

   public static void saveDataset(String name) {
      List<TrainingSample> samples = AIRotationRecorder.getSamples();
      if (samples.isEmpty()) {
         ChatUtil.send(
            D.k(
               new int[]{
                  180,
                  156,
                  1252,
                  1082,
                  1105,
                  223,
                  1229,
                  1087,
                  1070,
                  1218,
                  1202,
                  1098,
                  51,
                  1227,
                  1218,
                  1088,
                  51,
                  1214,
                  1223,
                  1098,
                  1107,
                  1231,
                  1220,
                  1082,
                  1070,
                  1223,
                  1206,
                  46,
                  51,
                  1255,
                  1208,
                  1072,
                  1069,
                  1220,
                  1205,
                  1080,
                  1104,
                  1222,
                  1211,
                  1082,
                  51,
                  209,
                  152,
                  102,
                  51,
                  140,
                  141,
                  110,
                  97,
                  139,
                  217,
                  1083,
                  1064,
                  1200,
                  217,
                  1074,
                  1059,
                  1208,
                  1225,
                  1076,
                  1059,
                  223,
                  1230,
                  1087,
                  1068,
                  1223,
                  1208,
                  1079
               },
               new int[]{19, 255, 249, 15}
            )
         );
      } else {
         try {
            Path datasetPath = DATASETS_DIR.resolve(name + ".json");

            try (FileWriter writer = new FileWriter(datasetPath.toFile())) {
               GSON.toJson(samples, writer);
            }

            ChatUtil.send("§aДатасет §e" + name + " §aсохранен (§f" + samples.size() + " §aсэмплов)");
            ChatUtil.send("§7Путь: §f" + datasetPath.toAbsolutePath());
         } catch (IOException var8) {
            ChatUtil.send("§cОшибка сохранения датасета: " + var8.getMessage());
            var8.printStackTrace();
         }
      }
   }

   public static void trainModel(String datasetName, String modelName) {
      try {
         Path datasetPath = DATASETS_DIR.resolve(datasetName + ".json");
         if (!Files.exists(datasetPath)) {
            ChatUtil.send("§cДатасет §e" + datasetName + " §cне найден!");
            return;
         }

         Type listType = (new TypeToken<List<TrainingSample>>() {}).getType();
         FileReader reader = new FileReader(datasetPath.toFile());

         List<TrainingSample> samples;
         try {
            samples = (List<TrainingSample>)GSON.fromJson(reader, listType);
         } catch (Throwable var10) {
            try {
               reader.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }

            throw var10;
         }

         reader.close();
         if (samples == null || samples.isEmpty()) {
            ChatUtil.send("\u00a7cДатасет пуст!");
            return;
         }

         float[][] features = new float[samples.size()][];
         float[][] labels = new float[samples.size()][];

         for (int i = 0; i < samples.size(); i++) {
            features[i] = samples.get(i).getInput();
            labels[i] = samples.get(i).getOutput();
         }

         AIRotationModel model = new AIRotationModel(modelName);
         model.train(features, labels);
         Path modelPath = MODELS_DIR.resolve(modelName);
         model.save(modelPath);
         model.close();
         ChatUtil.send("§aМодель §e" + modelName + " §aуспешно обучена и сохранена!");
      } catch (ModelException | TranslateException | IOException var11) {
         ChatUtil.send("§cОшибка обучения модели: " + var11.getMessage());
         var11.printStackTrace();
      }
   }

   public static void loadModel(String modelName) {
      try {
         Path modelPath = MODELS_DIR.resolve(modelName);
         if (!Files.exists(modelPath)) {
            ChatUtil.send("§cМодель §e" + modelName + " §cне найдена!");
            System.out.println("MODEL PATH NOT FOUND: " + modelPath.toAbsolutePath());
            return;
         }

         if (na5D != null) {
            na5D.close();
         }

         System.out.println("Loading model from: " + modelPath.toAbsolutePath());
         na5D = new AIRotationModel(modelName);
         na5D.load(modelPath);
         ChatUtil.send("§aМодель §e" + modelName + " §aактивна!");
         System.out.println("MODEL LOADED SUCCESSFULLY: " + modelName);
      } catch (ModelException | IOException var2) {
         ChatUtil.send("§cОшибка загрузки модели: " + var2.getMessage());
         System.out.println("MODEL LOAD ERROR: " + var2.getMessage());
         var2.printStackTrace();
      }
   }

   public static float[] predict(float[] input) {
      if (na5D == null) {
         System.out
            .println(
               D.k(
                  new int[]{
                     192,
                     134,
                     30,
                     47,
                     206,
                     139,
                     123,
                     46,
                     161,
                     134,
                     109,
                     66,
                     207,
                     154,
                     114,
                     46,
                     160,
                     239,
                     114,
                     13,
                     224,
                     171,
                     30,
                     3,
                     161,
                     162,
                     81,
                     6,
                     228,
                     163,
                     30,
                     4,
                     232,
                     189,
                     77,
                     22,
                     175
                  },
                  new int[]{129, 207, 62, 98}
               )
            );
         return new float[]{0.0F, 0.0F};
      } else {
         try {
            System.out.println("AI Input: [" + input[0] + ", " + input[1] + ", " + input[2] + ", " + input[3] + "]");
            float[] result = na5D.predict(input);
            System.out.println("AI Output: [" + result[0] + ", " + result[1] + "]");
            return result;
         } catch (Exception var2) {
            System.out.println("AI PREDICTION ERROR: " + var2.getMessage());
            var2.printStackTrace();
            return new float[]{0.0F, 0.0F};
         }
      }
   }

   public static void listFiles() {
      ChatUtil.send(
         D.k(
            new int[]{195, 111, 142, 227, 89, 55, 20, 175, 37, 67, 9, 221, 11, 126, 72, 251, 13, 101, 71, 175, 34, 99, 69, 234, 23, 42, 20, 178, 89},
            new int[]{100, 10, 41, 143}
         )
      );
      File[] datasets = DATASETS_DIR.toFile().listFiles((dir, namex) -> namex.endsWith(".json"));
      if (datasets != null && datasets.length > 0) {
         ChatUtil.send("\u00a7aДатасеты:");

         for (File dataset : datasets) {
            String name = dataset.getName().replace(".json", "");
            ChatUtil.send("  §7- §f" + name);
         }
      } else {
         ChatUtil.send(
            "\u00a77Датасеты: \u00a7cнет"
         );
      }

      File[] models = MODELS_DIR.toFile().listFiles(File::isDirectory);
      if (models != null && models.length > 0) {
         ChatUtil.send("\u00a7aМодели:");

         for (File model : models) {
            String name = model.getName();
            String status = na5D != null && na5D.toString().contains(name)
               ? " \u00a7a(активна)"
               : "";
            ChatUtil.send("  §7- §f" + name + status);
         }
      } else {
         ChatUtil.send("\u00a77Модели: \u00a7cнет");
      }
   }

   public static void openDirectory() {
      try {
         Desktop.getDesktop().open(AI_DIR.toFile());
         ChatUtil.send(
            "\u00a7aПапка AI открыта"
         );
      } catch (IOException var1) {
         ChatUtil.send("§cОшибка открытия папки: " + var1.getMessage());
         ChatUtil.send("§7Путь: §f" + AI_DIR.toAbsolutePath());
      }
   }

   @Generated
   public static AIRotationModel getCurrentModel() {
      return na5D;
   }

   static {
      try {
         Files.createDirectories(DATASETS_DIR);
         Files.createDirectories(MODELS_DIR);
      } catch (IOException var1) {
         var1.printStackTrace();
      }
   }
}
