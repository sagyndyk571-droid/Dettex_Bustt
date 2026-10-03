package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventPopTotem;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;

@ModuleInformation(
   moduleName = "Fake Player",
   moduleDesc = "Спавнит фейкового игрока",
   moduleCategory = ModuleCategory.MISC
)
public class FakePlayer extends Module {
   private OtherClientPlayerEntity zv8vR;
   private final float kHweH = 20.0F;
   private float qC6cV = 20.0F;
   private static final long INVULN_MS = 500L;
   private long u4fLCg;

   @Override
   public void onEnable() {
      super.onEnable();
      this.b8C5();
   }

   private void b8C5() {
      if (this.mc.player != null && this.mc.world != null && this.mc.getNetworkHandler() != null) {
         if (this.zv8vR == null) {
            this.qC6cV = 20.0F;
            this.u4fLCg = 0L;
            GameProfile profile = new GameProfile(UUID.randomUUID(), this.mc.player.getName().getString());
            this.zv8vR = new OtherClientPlayerEntity(this.mc.world, profile);
            this.zv8vR.copyFrom(this.mc.player);
            this.zv8vR.setPos(this.mc.player.getX(), this.mc.player.getY(), this.mc.player.getZ());
            this.zv8vR.setYaw(this.mc.player.getYaw());
            this.zv8vR.setPitch(this.mc.player.getPitch());
            this.zv8vR.setHealth(20.0F);
            this.mc.world.addEntity(this.zv8vR);
         }
      }
   }

   @Override
   public void onDisable() {
      this.yYwXhk8();
      super.onDisable();
   }

   private void yYwXhk8() {
      if (this.zv8vR != null) {
         if (this.mc.world != null) {
            this.mc.world.removeEntity(this.zv8vR.getId(), RemovalReason.DISCARDED);
         }

         this.zv8vR = null;
      }

      this.qC6cV = 20.0F;
   }

   public boolean owns(Entity entity) {
      return entity != null && this.zv8vR != null && entity == this.zv8vR;
   }

   public void handleLocalAttack(PlayerEntity attacker) {
      if (attacker != null && this.zv8vR != null && this.mc.world != null) {
         long now = System.currentTimeMillis();
         if (now - this.u4fLCg >= 500L) {
            this.u4fLCg = now;
            float cooldown = attacker.getAttackCooldownProgress(0.5F);
            float base = (float)attacker.getAttributeValue(EntityAttributes.ATTACK_DAMAGE);
            float damage = base * (0.2F + cooldown * cooldown * 0.8F);
            boolean charged = cooldown > 0.9F;
            boolean crit = charged
               && attacker.fallDistance > 0.0F
               && !attacker.isOnGround()
               && !attacker.isClimbing()
               && !attacker.isTouchingWater()
               && !attacker.hasStatusEffect(StatusEffects.BLINDNESS)
               && !attacker.hasVehicle();
            if (crit) {
               damage *= 1.5F;
            }

            boolean enchanted = attacker.getMainHandStack().hasEnchantments();
            boolean sweeping = charged
               && !crit
               && attacker.isOnGround()
               && !attacker.isSprinting()
               && attacker.getMainHandStack().getItem() instanceof SwordItem;
            this.i8EFgw(attacker, charged, crit, sweeping, damage);
            this.ct2dy1t(attacker);
            if (crit) {
               attacker.addCritParticles(this.zv8vR);
            }

            if (enchanted) {
               attacker.addEnchantedHitParticles(this.zv8vR);
            }

            if (sweeping) {
               this.yc462(attacker);
            }

            if (this.qC6cV - damage <= 0.0F) {
               this.wak2t();
               this.qC6cV = 20.0F;
            } else {
               this.qC6cV -= damage;
            }

            this.zv8vR.setHealth(Math.max(this.qC6cV, 1.0F));
         }
      }
   }

   private void i8EFgw(PlayerEntity attacker, boolean charged, boolean crit, boolean sweeping, float damage) {
      SoundEvent sound;
      if (sweeping) {
         sound = SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP;
      } else if (crit) {
         sound = SoundEvents.ENTITY_PLAYER_ATTACK_CRIT;
      } else if (charged) {
         sound = SoundEvents.ENTITY_PLAYER_ATTACK_STRONG;
      } else if (damage <= 0.0F) {
         sound = SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE;
      } else {
         sound = SoundEvents.ENTITY_PLAYER_ATTACK_WEAK;
      }

      this.mc.world.playSound((PlayerEntity)null, attacker.getX(), attacker.getY(), attacker.getZ(), sound, attacker.getSoundCategory(), 1.0F, 1.0F);
   }

   private void ct2dy1t(PlayerEntity attacker) {
      double dx = this.zv8vR.getX() - attacker.getX();
      double dz = this.zv8vR.getZ() - attacker.getZ();
      float hitYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - attacker.getYaw());
      this.zv8vR.hurtTime = this.zv8vR.maxHurtTime = 10;
      this.zv8vR.animateDamage(hitYaw);
      this.mc
         .world
         .playSound(
            (PlayerEntity)null,
            this.zv8vR.getX(),
            this.zv8vR.getY(),
            this.zv8vR.getZ(),
            SoundEvents.ENTITY_PLAYER_HURT,
            this.zv8vR.getSoundCategory(),
            1.0F,
            1.0F
         );
   }

   private void yc462(PlayerEntity attacker) {
      double rad = Math.toRadians(attacker.getYaw());
      double dx = -Math.sin(rad);
      double dz = Math.cos(rad);
      this.mc.world.addParticle(ParticleTypes.SWEEP_ATTACK, this.zv8vR.getX() + dx, this.zv8vR.getBodyY(0.5), this.zv8vR.getZ() + dz, dx, 0.0, dz);
   }

   private void wak2t() {
      for (int i = 0; i < 40; i++) {
         this.mc
            .world
            .addParticle(
               ParticleTypes.TOTEM_OF_UNDYING,
               this.zv8vR.getX(),
               this.zv8vR.getBodyY(0.5),
               this.zv8vR.getZ(),
               (this.mc.world.random.nextDouble() - 0.5) * 2.0,
               this.mc.world.random.nextDouble() * 0.5 + 0.5,
               (this.mc.world.random.nextDouble() - 0.5) * 2.0
            );
      }

      this.mc
         .world
         .playSound(
            (PlayerEntity)null, this.zv8vR.getX(), this.zv8vR.getY(), this.zv8vR.getZ(), SoundEvents.ITEM_TOTEM_USE, this.zv8vR.getSoundCategory(), 1.0F, 1.0F
         );
      new EventPopTotem(this.zv8vR).post();
   }

   @Subscribe
   private void onPacket(EventPacket e) {
      if (e.getPacket() instanceof DisconnectS2CPacket || e.getPacket() instanceof GameJoinS2CPacket || e.getPacket() instanceof PlayerRespawnS2CPacket) {
         this.setEnabled(false);
      }
   }
}
