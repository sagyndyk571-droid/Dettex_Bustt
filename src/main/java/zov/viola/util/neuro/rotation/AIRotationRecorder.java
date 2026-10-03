package zov.viola.util.neuro.rotation;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zov.viola.Viola;
import zov.viola.event.list.EventTick;
import zov.viola.module.list.combat.KillAura;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.math.BestPoint;
import zov.viola.util.math.RotationUtil;
import zov.viola.util.rotation.Rotation;

public class AIRotationRecorder implements IMinecraft {
   private static boolean vsol = false;
   private static final List<TrainingSample> samples = new ArrayList<>();
   private static Rotation xjhw = null;
   private static float dsdikbR = 0.0F;
   private static float r5zjmZu = 0.0F;

   @Subscribe
   public void onTick(EventTick event) {
      if (vsol && mc.player != null) {
         LivingEntity target = KillAura.lastTarget;
         if (target != null && target.isAlive()) {
            KillAura killAura = Viola.getInstance().getModuleStorage().get(KillAura.class);
            if (killAura != null && killAura.isEnabled()) {
               Rotation currentRotation = new Rotation(MathHelper.wrapDegrees(mc.player.getYaw()), mc.player.getPitch());
               if (xjhw == null) {
                  xjhw = currentRotation;
                  System.out
                     .println(
                        "AI RECORDER: Initialized with rotation "
                           + String.format(
                              "%.2f, %.2f",
                              currentRotation.getYaw(),
                              currentRotation.getPitch()
                           )
                     );
               } else {
                  float actualDeltaYaw = MathHelper.wrapDegrees(currentRotation.getYaw() - xjhw.getYaw());
                  float actualDeltaPitch = currentRotation.getPitch() - xjhw.getPitch();
                  double distance = killAura.distance.getValue();
                  Vec3d targetPoint = BestPoint.getMultipoint(target, distance);
                  Rotation targetRotation = new Rotation(RotationUtil.calculate(targetPoint));
                  float targetDeltaYaw = MathHelper.wrapDegrees(targetRotation.getYaw() - currentRotation.getYaw());
                  float targetDeltaPitch = targetRotation.getPitch() - currentRotation.getPitch();
                  if (!dghax(actualDeltaYaw, actualDeltaPitch, targetDeltaYaw, targetDeltaPitch)) {
                     System.out
                        .println(
                           D.k(
                              new int[]{
                                 158,
                                 20,
                                 236,
                                 79,
                                 154,
                                 30,
                                 131,
                                 79,
                                 155,
                                 24,
                                 158,
                                 39,
                                 255,
                                 20,
                                 162,
                                 107,
                                 190,
                                 49,
                                 165,
                                 121,
                                 255,
                                 57,
                                 173,
                                 105,
                                 190,
                                 113,
                                 236,
                                 110,
                                 180,
                                 52,
                                 188,
                                 109,
                                 182,
                                 51,
                                 171,
                                 61,
                                 172,
                                 60,
                                 161,
                                 109,
                                 179,
                                 56
                              },
                              new int[]{223, 93, 204, 29}
                           )
                        );
                     xjhw = currentRotation;
                     dsdikbR = actualDeltaYaw;
                     r5zjmZu = actualDeltaPitch;
                  } else {
                     float[] input = new float[]{dsdikbR, r5zjmZu, targetDeltaYaw, targetDeltaPitch};
                     float[] output = new float[]{actualDeltaYaw, actualDeltaPitch};
                     samples.add(new TrainingSample(input, output));
                     if (samples.size() % 20 == 0) {
                        System.out
                           .println(
                              "AI RECORDER: Sample "
                                 + samples.size()
                                 + " | Input: ["
                                 + String.format(
                                    D.k(
                                       new int[]{194, 158, 166, 188, 203, 144, 177, 244, 213, 214, 184, 250, 194, 158, 166, 188, 203, 144, 177, 244, 213, 214},
                                       new int[]{231, 176, 148, 218}
                                    ),
                                    input[0],
                                    input[1],
                                    input[2],
                                    input[3]
                                 )
                                 + "] | Output: ["
                                 + String.format("%.2f, %.2f", output[0], output[1])
                                 + "]"
                           );
                     }

                     xjhw = currentRotation;
                     dsdikbR = actualDeltaYaw;
                     r5zjmZu = actualDeltaPitch;
                  }
               }
            }
         }
      }
   }

   private static boolean dghax(float actualDeltaYaw, float actualDeltaPitch, float targetDeltaYaw, float targetDeltaPitch) {
      if (Float.isFinite(actualDeltaYaw) && Float.isFinite(actualDeltaPitch) && Float.isFinite(targetDeltaYaw) && Float.isFinite(targetDeltaPitch)) {
         return Math.abs(actualDeltaYaw) > 180.0F
               || Math.abs(actualDeltaPitch) > 90.0F
               || Math.abs(targetDeltaYaw) > 180.0F
               || Math.abs(targetDeltaPitch) > 90.0F
            ? false
            : !(Math.abs(actualDeltaYaw) < 0.01F) || !(Math.abs(actualDeltaPitch) < 0.01F);
      } else {
         return false;
      }
   }

   public static void startRecording() {
      vsol = true;
      samples.clear();
      xjhw = null;
      dsdikbR = 0.0F;
      r5zjmZu = 0.0F;
      System.out
         .println(
            D.k(
               new int[]{
                  189, 0, 200, 145, 185, 10, 167, 145, 184, 12, 186, 249, 220, 26, 156, 162, 142, 61, 141, 167, 220, 59, 141, 160, 147, 59, 140, 170, 146, 46
               },
               new int[]{252, 73, 232, 195}
            )
         );
   }

   public static int stopRecording() {
      vsol = false;
      int count = samples.size();
      xjhw = null;
      System.out.println("AI RECORDER: Stopped recording, collected " + count + " samples");
      return count;
   }

   public static List<TrainingSample> getSamples() {
      return new ArrayList<>(samples);
   }

   public static void clearSamples() {
      samples.clear();
   }

   @Generated
   public static boolean isRecording() {
      return vsol;
   }
}
