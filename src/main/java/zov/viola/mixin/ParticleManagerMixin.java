package zov.viola.mixin;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.module.list.render.Optimization;

@Mixin({ParticleManager.class})
public class ParticleManagerMixin {
   private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

   @Inject(
      method = {"addParticle(Lnet/minecraft/client/particle/Particle;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void throttleParticles(Particle particle, CallbackInfo ci) {
      int percent = Optimization.particlePercent();
      if (percent < 100) {
         if (RANDOM.nextInt(100) >= percent) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"addBlockBreakParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void disableBlockBreakParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
      if (Optimization.isNoBlockBreak()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"addBlockBreakingParticles(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void disableBlockBreakingParticles(BlockPos pos, Direction dir, CallbackInfo ci) {
      if (Optimization.isNoBlockBreak()) {
         ci.cancel();
      }
   }
}
