package zov.viola.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.event.list.FireworkEvent;

@Mixin({FireworkRocketEntity.class})
public abstract class FireworkRocketEntityMixin {
   @Shadow
   public LivingEntity shooter;
   @Unique
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   @ModifyConstant(
      method = {"tick"},
      constant = {@Constant(
         doubleValue = 1.5
      )}
   )
   private double replaceSpeed(double original) {
      FireworkEvent event = new FireworkEvent(this.shooter, (float)original);
      event.post();
      return event.getSpeed();
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void onTick(CallbackInfo ci) {
      if (mc.player != null && mc.world != null && mc.player.isGliding()) {
         if (this.shooter != mc.player) {
            if (!this.hasActiveOwnFirework()) {
               FireworkRocketEntity self = (FireworkRocketEntity)(Object)this;
               Vec3d fireworkPos = self.getPos();
               Vec3d playerPos = mc.player.getPos();
               double distance = fireworkPos.distanceTo(playerPos);
               if (distance < 5.0) {
                  Vec3d fireworkVel = self.getVelocity();
                  if (fireworkVel.lengthSquared() < 0.01) {
                     return;
                  }

                  Vec3d toPlayer = playerPos.subtract(fireworkPos).normalize();
                  double dot = fireworkVel.normalize().dotProduct(toPlayer);
                  if (dot > 0.4) {
                     Vec3d fireworkDir = fireworkVel.normalize();
                     Vec3d right = new Vec3d(fireworkDir.x, 0.0, -fireworkDir.z).normalize();
                     double dotRight = toPlayer.dotProduct(right);
                     if (dotRight < 0.0) {
                        right = right.multiply(-1.0);
                     }

                     double urgency = 1.0 - distance / 5.0;
                     double strength = 0.25 * urgency;
                     double upward = 0.1 * urgency;
                     Vec3d currentVel = mc.player.getVelocity();
                     Vec3d dodge = right.multiply(strength).add(0.0, upward, 0.0);
                     mc.player.setVelocity(currentVel.add(dodge));
                  }
               }
            }
         }
      }
   }

   @Unique
   private boolean hasActiveOwnFirework() {
      for (Entity entity : mc.world.getEntities()) {
         if (entity instanceof FireworkRocketEntity firework) {
            IFireworkRocketEntityAccessor accessor = (IFireworkRocketEntityAccessor)firework;
            if (accessor.getShooter() == mc.player) {
               return true;
            }
         }
      }

      return false;
   }
}
