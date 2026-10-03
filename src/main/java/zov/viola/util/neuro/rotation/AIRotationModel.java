package zov.viola.util.neuro.rotation;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Activation;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;
import ai.djl.nn.norm.BatchNorm;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.dataset.ArrayDataset;
import ai.djl.training.dataset.ArrayDataset.Builder;
import ai.djl.training.initializer.XavierInitializer;
import ai.djl.training.listener.TrainingListener.Defaults;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Adam;
import ai.djl.training.tracker.Tracker;
import ai.djl.translate.TranslateException;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import zov.viola.obf.D;
import zov.viola.util.chat.ChatUtil;

public class AIRotationModel implements Closeable {
   private static final int NUM_EPOCH = 500;
   private static final int BATCH_SIZE = 32;
   private static final int INPUT_SIZE = 4;
   private static final int OUTPUT_SIZE = 2;
   private final Model a8pRiv9;
   private final Predictor<float[], float[]> h6qysj;
   private final String d5VG5;

   public AIRotationModel(String name) {
      this.d5VG5 = name;
      this.a8pRiv9 = Model.newInstance(name);
      this.a8pRiv9.setBlock(gcWo4m());
      this.h6qysj = this.a8pRiv9.newPredictor(new FloatArrayTranslator());
   }

   public float[] predict(float[] input) throws TranslateException {
      return (float[])this.h6qysj.predict(input);
   }

   public void train(float[][] features, float[][] labels) throws ModelException, IOException, TranslateException {
      if (features.length == labels.length && features.length != 0) {
         ChatUtil.send("§aНачинаю обучение модели §e" + this.d5VG5 + "§a...");
         ChatUtil.send("§7Сэмплов: §f" + features.length + " §7| Эпох: §f500");
         TrainingConfig trainingConfig = new DefaultTrainingConfig(Loss.l2Loss())
            .optInitializer(new XavierInitializer(), "weight")
            .optOptimizer(Adam.builder().optLearningRateTracker(Tracker.fixed(0.001F)).build())
            .addTrainingListeners(Defaults.logging("train"));
         Trainer trainer = this.a8pRiv9.newTrainer(trainingConfig);

         try {
            NDManager manager = NDManager.newBaseManager();

            try {
               ArrayDataset trainingSet = ((Builder)new Builder()
                     .setData(new NDArray[]{manager.create(features)})
                     .optLabels(new NDArray[]{manager.create(labels)})
                     .setSampling(32, true))
                  .build();
               trainer.initialize(new Shape[]{new Shape(new long[]{32L, 4L})});
               EasyTrain.fit(trainer, 500, trainingSet, null);
               ChatUtil.send(
                  D.k(
                     new int[]{132, 5, 1104, 1054, 1120, 1059, 1147, 1042, 1051, 1105, 110, 1048, 1043, 1110, 1147, 1135, 1131, 1105, 1139, 1041, 2},
                     new int[]{35, 100, 78, 47}
                  )
               );
            } catch (Throwable var10) {
               if (manager != null) {
                  try {
                     manager.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (manager != null) {
               manager.close();
            }
         } catch (Throwable var11) {
            if (trainer != null) {
               try {
                  trainer.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (trainer != null) {
            trainer.close();
         }
      } else {
         throw new IllegalArgumentException(
            D.k(
               new int[]{
                  179,
                  184,
                  151,
                  46,
                  128,
                  175,
                  147,
                  41,
                  213,
                  188,
                  152,
                  62,
                  213,
                  177,
                  151,
                  56,
                  144,
                  177,
                  133,
                  122,
                  152,
                  168,
                  133,
                  46,
                  213,
                  181,
                  151,
                  44,
                  144,
                  253,
                  130,
                  50,
                  144,
                  253,
                  133,
                  59,
                  152,
                  184,
                  214,
                  41,
                  156,
                  167,
                  147,
                  122,
                  148,
                  179,
                  146,
                  122,
                  151,
                  184,
                  214,
                  52,
                  154,
                  179,
                  219,
                  63,
                  152,
                  173,
                  130,
                  35
               },
               new int[]{245, 221, 246, 90}
            )
         );
      }
   }

   public void load(Path path) throws IOException, ModelException {
      this.a8pRiv9.load(path, "model");
      ChatUtil.send("§aМодель §e" + this.d5VG5 + " §aзагружена");
   }

   public void save(Path path) throws IOException {
      this.a8pRiv9.save(path, "model");
      ChatUtil.send("§aМодель §e" + this.d5VG5 + " §aсохранена");
   }

   @Override
   public void close() {
      this.h6qysj.close();
      this.a8pRiv9.close();
   }

   private static SequentialBlock gcWo4m() {
      return new SequentialBlock()
         .add(Linear.builder().setUnits(128L).build())
         .add(Blocks.batchFlattenBlock())
         .add(BatchNorm.builder().build())
         .add(Activation.reluBlock())
         .add(Linear.builder().setUnits(64L).build())
         .add(Blocks.batchFlattenBlock())
         .add(BatchNorm.builder().build())
         .add(Activation.reluBlock())
         .add(Linear.builder().setUnits(32L).build())
         .add(Blocks.batchFlattenBlock())
         .add(BatchNorm.builder().build())
         .add(Activation.reluBlock())
         .add(Linear.builder().setUnits(2L).build());
   }
}
