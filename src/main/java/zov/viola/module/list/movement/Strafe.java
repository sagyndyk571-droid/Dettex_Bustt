package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.util.math.MathHelper;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.player.move.MoveUtil;
import zov.viola.util.rotation.Rotation;
import zov.viola.util.rotation.RotationComponent;

@ModuleInformation(
   moduleName = "Strafe",
   moduleDesc = "Горизонтальные стрейфы с матрицей скорости",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class Strafe extends Module {
   private final ModeSetting fuz9735 = new ModeSetting(
      "Режим",
      "Матрикс",
      "Матрикс",
      "Грим"
   );
   private final SliderSetting alsC8tB = new SliderSetting(
      "Скорость", 0.42, 0.0, 1.0, 0.01
   );

   @Subscribe
   public void onTick(EventTick event) {
      if (this.mc.player != null && this.mc.world != null) {
         boolean moving = MoveUtil.hasPlayerMovement();
         if (this.fuz9735.is("Матрикс")) {
            MoveUtil.setMotion(moving ? this.alsC8tB.getValue() * 1.5 : 0.0);
         } else if (this.fuz9735.is("Грим")) {
            if (!moving) {
               return;
            }

            MoveUtil.setMotion(this.alsC8tB.getValue() * 1.5);
            float forward = this.mc.player.input.movementForward;
            float strafe = this.mc.player.input.movementSideways;
            float moveYaw = (float)Math.toDegrees(RotationComponent.direction(this.mc.player.getYaw(), forward, strafe));
            moveYaw = MathHelper.wrapDegrees(moveYaw);
            RotationComponent.update(new Rotation(moveYaw, this.mc.player.getPitch()), 360.0F, 360.0F, 2, 0);
         }
      }
   }
}
