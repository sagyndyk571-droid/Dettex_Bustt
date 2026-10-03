package zov.viola.module.list.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Swing Animations",
   moduleDesc = "Анимация взмаха руки при ударе",
   moduleCategory = ModuleCategory.RENDER
)
public class SwingAnimations extends Module {
   private final ModeSetting zuEa0aD = new ModeSetting(
      "Мод",
      "Smooth",
      "Smooth",
      "Никакой",
      "Self",
      "Self 2",
      "Down",
      "Forward",
      "Touch",
      "BlockHit",
      "Pander",
      "Curt",
      "HMI"
   );
   private final SliderSetting mrv8 = new SliderSetting("Сила", 3.0, 0.0, 10.0, 1.0)
      .setVisible(
         () -> !this.zuEa0aD.getValue().equals("Pander")
            && !this.zuEa0aD.is("BlockHit")
            && !this.zuEa0aD.getValue().equals("Forward")
            && !this.zuEa0aD.getValue().equals("Curt")
            && !this.zuEa0aD.getValue().equals("Никакой")
            && !this.zuEa0aD.getValue().equals("HMI")
      );
   public final SliderSetting speed = new SliderSetting(
      "Скорость", 7.0, 0.0, 10.0, 1.0
   );
   public final SliderSetting angle = new SliderSetting("Угол", 90.0, 0.0, 360.0, 5.0)
      .setVisible(
         () -> this.zuEa0aD.getValue().equals("Self") || this.zuEa0aD.getValue().equals("Self 2")
      );
   public final SliderSetting hmiPosX = new SliderSetting(
         "Смещение X", 0.0, -2.0, 2.0, 0.01
      )
      .setVisible(() -> this.zuEa0aD.getValue().equals("HMI"));
   public final SliderSetting hmiPosY = new SliderSetting(
         "Смещение Y", 0.0, -2.0, 2.0, 0.01
      )
      .setVisible(() -> this.zuEa0aD.getValue().equals("HMI"));
   public final SliderSetting hmiPosZ = new SliderSetting(
         "Смещение Z", 0.0, -2.0, 2.0, 0.01
      )
      .setVisible(() -> this.zuEa0aD.getValue().equals("HMI"));
   public final SliderSetting hmiRotX = new SliderSetting(
         "Вращение X", 0.0, -180.0, 180.0, 1.0
      )
      .setVisible(() -> this.zuEa0aD.getValue().equals("HMI"));
   public final SliderSetting hmiRotY = new SliderSetting(
         "Вращение Y", 0.0, -180.0, 180.0, 1.0
      )
      .setVisible(() -> this.zuEa0aD.getValue().equals("HMI"));
   public final SliderSetting hmiRotZ = new SliderSetting(
         "Вращение Z", 0.0, -180.0, 180.0, 1.0
      )
      .setVisible(() -> this.zuEa0aD.getValue().equals("HMI"));
   public final SliderSetting hmiScale = new SliderSetting(
         "Масштаб", 1.0, 0.1, 3.0, 0.05
      )
      .setVisible(() -> this.zuEa0aD.getValue().equals("HMI"));
   private final HMIRenderer jpum = new HMIRenderer();

   public boolean isHMI() {
      return this.zuEa0aD.getValue().equals("HMI");
   }

   public HMIRenderer getHMIRenderer() {
      this.jpum.setSwingAnimations(this);
      return this.jpum;
   }

   public void renderSwordAnimation(MatrixStack matrices, float swingProgress, float equipProgress, Arm arm) {
      float anim = (float)Math.sin(swingProgress * (Math.PI / 2) * 2.0);
      float sin2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
      String var7 = this.zuEa0aD.getValue();
      switch (var7) {
         case "Никакой": {
            matrices.translate(0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
            float f = -0.4F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
            float g = 0.2F * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) (Math.PI * 2));
            float h = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
            matrices.translate(f, g, h);
            int i = arm == Arm.RIGHT ? 1 : -1;
            float f2 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (45.0F + f2 * -20.0F)));
            float g2 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * g2 * -20.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g2 * -80.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * -45.0F));
            break;
         }
         case "Smooth": {
            matrices.translate(0.56F, -0.52F, -0.72F);
            double swingPower = this.mrv8.getValue() * 10.0;
            float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(45.0 + f * (-swingPower / 4.0))));
            float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress * swingProgress) * (float) Math.PI);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)(sin2 * -(swingPower / 4.0))));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)(sin2 * -swingPower)));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0F));
            break;
         }
         case "Self 2":
            matrices.translate(0.56F, -0.52F, -0.72F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-30.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)(-this.angle.getValue() - this.mrv8.getValue() * 10.0 * anim)));
            break;
         case "Forward":
            matrices.translate(0.56F, -0.52F, -0.72F);
            matrices.translate(0.0, 0.0, -0.3 * sin2);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin2 * -35.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sin2 * 35.0F));
            break;
         case "Self":
            matrices.translate(0.56F, -0.52F, -0.72F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-60.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)(-this.angle.getValue() - this.mrv8.getValue() * 10.0 * anim)));
            break;
         case "Down":
            matrices.translate(0.56F, -0.52F - anim * this.mrv8.getValue() / 24.0, -0.72F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-30.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            break;
         case "Touch":
            matrices.translate(0.56F, -0.52F, -0.72F);
            matrices.scale(1.0F, 1.0F, (float)(1.0 + anim * this.mrv8.getValue() / 4.0));
            matrices.translate(0.0F, 0.0F, -0.265F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-100.0F));
            break;
         case "Curt":
            matrices.translate(0.56F, -0.52F, -0.72F);
            float sqrtSwing = MathHelper.sqrt(swingProgress);
            float sinMain = MathHelper.sin(sqrtSwing * (float) Math.PI);
            float sinExtra = MathHelper.sin(swingProgress * (float) Math.PI);
            matrices.translate(0.4F - sinMain * 0.2F, -0.2F + sinMain * 0.3F, -0.5F - sinExtra * 0.2F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(91.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40.0F + sinMain * -100.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-60.0F));
            break;
         case "Pander":
            matrices.translate(0.56F, -0.52F, -0.72F);
            matrices.scale(0.8F, 0.8F, 0.8F);
            float anim2 = 1.0F
               - MathHelper.lerp(
                  this.mc.getRenderTickCounter().getTickDelta(true),
                  this.mc.gameRenderer.firstPersonRenderer.prevEquipProgressMainHand,
                  this.mc.gameRenderer.firstPersonRenderer.equipProgressMainHand
               );
            matrices.translate(0.3 - anim * 0.15F, (double)(0.2F - anim2 * 0.12F), (double)(-0.15F - anim * 0.13F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(76.0F - 10.0F * anim));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-16.0F - 8.0F * anim));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-83.0F - 26.0F * anim));
            break;
         case "BlockHit": {
            matrices.translate(0.56F, -0.52F, -0.72F);
            float f = MathHelper.sin((float)(swingProgress * swingProgress * Math.PI));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
            float g = MathHelper.sin((float)(MathHelper.sqrt(swingProgress) * Math.PI));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f * -20.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(g * -20.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * -80.0F));
            matrices.translate(0.4F, 0.2F, 0.2F);
            matrices.translate(-0.5F, 0.08F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(20.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(20.0F));
         }
      }
   }
}
