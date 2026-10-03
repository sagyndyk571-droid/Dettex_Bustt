package zov.viola.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zov.viola.module.list.render.Cape;

@Mixin({AbstractClientPlayerEntity.class})
public class AbstractClientPlayerEntityMixin {
   private static final Identifier CUSTOM_CAPE = Identifier.of("mre", "textures/entity/cape.png");

   @Inject(
      method = {"getSkinTextures"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void onGetSkinTextures(CallbackInfoReturnable<SkinTextures> cir) {
      Cape capeModule = Cape.getInstance();
      if (capeModule != null && capeModule.shouldRenderCape((AbstractClientPlayerEntity)(Object)this)) {
         SkinTextures original = (SkinTextures)cir.getReturnValue();
         cir.setReturnValue(new SkinTextures(original.texture(), original.textureUrl(), CUSTOM_CAPE, CUSTOM_CAPE, original.model(), original.secure()));
      }
   }
}
