package zov.viola.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import zov.viola.util.math.TimerUtil;

@Mixin({MinecraftClient.class})
public abstract class TimerMixin {
   @Unique
   private long violaLastRealTime = 0L;
   @Unique
   private long violaFakeTime = 0L;

   @ModifyArg(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;beginRenderTick(JZ)I"
      ),
      index = 0
   )
   private long modifyRenderTime(long currentTime) {
      if (TimerUtil.speed == 1.0F) {
         this.violaLastRealTime = currentTime;
         this.violaFakeTime = currentTime;
         return currentTime;
      } else if (this.violaLastRealTime == 0L) {
         this.violaLastRealTime = currentTime;
         this.violaFakeTime = currentTime;
         return currentTime;
      } else {
         long realElapsed = currentTime - this.violaLastRealTime;
         this.violaLastRealTime = currentTime;
         this.violaFakeTime = this.violaFakeTime + (long)((double)realElapsed * TimerUtil.speed);
         return this.violaFakeTime;
      }
   }
}
