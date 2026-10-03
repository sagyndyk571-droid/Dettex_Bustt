package zov.viola.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.event.list.ChatEvent;
import zov.viola.event.list.EventEntitySpawn;

@Mixin({ClientPlayNetworkHandler.class})
public class ClientPlayNetworkHandlerMixin {
   @Shadow
   private ClientWorld world;

   @Inject(
      method = {"sendChatMessage(Ljava/lang/String;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendChatMessage(String string, CallbackInfo ci) {
      ChatEvent event = new ChatEvent(string);
      event.post();
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"sendChatCommand(Ljava/lang/String;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendChatCommand(String string, CallbackInfo ci) {
      ChatEvent event = new ChatEvent(string);
      event.post();
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"onEntitySpawn"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;playSpawnSound(Lnet/minecraft/entity/Entity;)V",
         shift = Shift.AFTER
      )}
   )
   private void hookEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity != null) {
         EventEntitySpawn event = new EventEntitySpawn(entity);
         event.post();
      }
   }
}
