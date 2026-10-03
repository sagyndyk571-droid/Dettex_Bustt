package zov.viola.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.module.list.render.Optimization;

@Mixin({BlockEntityRenderDispatcher.class})
public class BlockEntityRenderDispatcherMixin {
   @Inject(
      method = {"render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cullDistantBlockEntities(
      BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci
   ) {
      double max = Optimization.blockEntityMaxDistance();
      if (!(max <= 0.0)) {
         BlockEntityRenderDispatcher self = (BlockEntityRenderDispatcher)(Object)this;
         if (self.camera != null) {
            double dx = blockEntity.getPos().getX() + 0.5 - self.camera.getPos().x;
            double dy = blockEntity.getPos().getY() + 0.5 - self.camera.getPos().y;
            double dz = blockEntity.getPos().getZ() + 0.5 - self.camera.getPos().z;
            if (dx * dx + dy * dy + dz * dz > max * max) {
               ci.cancel();
            }
         }
      }
   }
}
