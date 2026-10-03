package zov.viola.mixin;

import java.util.Comparator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnderPearlEntity.class})
public class EnderPearlEntityMixin {
   @Inject(
      method = {"<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V"},
      at = {@At("RETURN")}
   )
   private void fixOwner(CallbackInfo ci) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         EnderPearlEntity entity = (EnderPearlEntity)(Object)this;
         entity.setOwner(
            MinecraftClient.getInstance()
               .world
               .getPlayers()
               .stream()
               .filter(me -> me.distanceTo(entity) > 1.0F)
               .min(Comparator.comparingDouble(me -> me.distanceTo(entity)))
               .orElse(null)
         );
      }
   }
}
