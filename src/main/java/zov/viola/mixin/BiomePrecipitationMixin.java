package zov.viola.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zov.viola.Viola;
import zov.viola.module.list.render.Ambience;
import zov.viola.util.base.Instance;

@Mixin({Biome.class})
public class BiomePrecipitationMixin {
   @Inject(
      method = {"getPrecipitation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void forceSnow(BlockPos pos, int seaLevel, CallbackInfoReturnable<Biome.Precipitation> cir) {
      if (Viola.getInstance() != null && Viola.getInstance().getModuleStorage() != null) {
         Ambience ambience = Instance.get(Ambience.class);
         if (ambience != null && ambience.isEnabled() && ambience.isWinter()) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
         }
      }
   }
}
