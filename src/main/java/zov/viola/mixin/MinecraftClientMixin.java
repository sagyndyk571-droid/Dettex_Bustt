package zov.viola.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zov.viola.Viola;
import zov.viola.event.EventGameUpdate;
import zov.viola.event.list.EventMinecraftInit;
import zov.viola.event.list.EventTick;
import zov.viola.event.list.EventTickEnd;
import zov.viola.obf.D;

@Mixin({MinecraftClient.class})
public class MinecraftClientMixin {
   @Unique
   private long lastHookTime = Util.getMeasuringTimeNano();
   @Unique
   private int accumulatedCalls = 0;

   @Inject(
      method = {"getWindowTitle"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getWindowTitle(CallbackInfoReturnable<String> cir) {
       if (Viola.getInstance().isPanicMode()) {
          cir.setReturnValue("Sodium 1.21.11");
       } else {
          cir.setReturnValue(
             "DettexClient 1.21.11"
          );
      }
   }

   @Inject(
      method = {"<init>"},
      at = {@At(
         value = "NEW",
         target = "(Lnet/minecraft/client/MinecraftClient;Ljava/io/File;)Lnet/minecraft/client/option/GameOptions;"
      )}
   )
   private void initOptions(RunArgs args, CallbackInfo ci) {
      new EventMinecraftInit().post();
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void tick(CallbackInfo ci) {
      new EventTick().post();
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void tickEnd(CallbackInfo ci) {
      new EventTickEnd().post();
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void render(boolean tick, CallbackInfo ci) {
      long timeNano = Util.getMeasuringTimeNano();
      long deltaTime = timeNano - this.lastHookTime;
      this.accumulatedCalls += (int)(deltaTime / 4166666L);
      this.lastHookTime = this.lastHookTime + this.accumulatedCalls * 4166666L;

      for (this.accumulatedCalls = Math.min(this.accumulatedCalls, 240); this.accumulatedCalls > 0; this.accumulatedCalls--) {
         new EventGameUpdate().post();
      }
   }
}
