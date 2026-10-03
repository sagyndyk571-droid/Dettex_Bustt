package zov.viola.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.module.list.render.Optimization;

@Mixin({EntityRenderDispatcher.class})
public class EntityRenderDispatcherMixin {
   @Inject(
      method = {"configure(Lnet/minecraft/world/World;Lnet/minecraft/client/render/Camera;Lnet/minecraft/entity/Entity;)V"},
      at = {@At("TAIL")}
   )
   private void applyShadowSetting(World world, Camera camera, Entity focusedEntity, CallbackInfo ci) {
      EntityRenderDispatcher self = (EntityRenderDispatcher)(Object)this;
      self.setRenderShadows(!Optimization.isNoShadows());
   }

   @Inject(
      method = {"render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cullDistantEntities(
      Entity entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci
   ) {
      double max = Optimization.entityMaxDistance();
      if (!(max <= 0.0)) {
         EntityRenderDispatcher self = (EntityRenderDispatcher)(Object)this;
         double distSq = self.getSquaredDistanceToCamera(entity);
         if (distSq > max * max) {
            ci.cancel();
         }
      }
   }
}
