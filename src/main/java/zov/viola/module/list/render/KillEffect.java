package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Last;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventAttack;
import zov.viola.event.list.EventPopTotem;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;
import zov.viola.util.render.effects.MasEffectRenderer;
import zov.viola.util.render.providers.ColorProvider;

@ModuleInformation(
   moduleName = "Kill Effect",
   moduleDesc = "Визуальный эффект при убийстве цели",
   moduleCategory = ModuleCategory.RENDER
)
public class KillEffect extends Module {
   private final ModeSetting kbfziv9 = new ModeSetting(
      "Эффект",
      "Lightning",
      "Lightning",
      "Explosion",
      "Blood Rain",
      "Disintegrate",
      "Black Hole",
      "Shockwave",
      "Ascension",
      "Tornado",
      "Meteor",
      "Orbital Strike",
      "Phoenix Rise",
      "Pig Bomb",
      "Shatter",
      "Thunder Clap",
      "Crown Drop",
      "Rainbow Burst",
      "Soul Ascend",
      "Firework Burst",
      "Death Skull",
      "Death Spark",
      "Diamond Scrap",
      "Netherite Scrap",
      "Flash",
      "Flick",
      "Smash"
   );
   private final ModeSetting mU1Q = new ModeSetting(
      "Эффект тотема",
      "Revive",
      "Off",
      "Revive",
      "Revive Spark",
      "Shield Wave"
   );
   private final BooleanSetting itGy5sz = new BooleanSetting(
      "Цвет от темы", true
   );
   private final ColorSetting sCbB = new ColorSetting(
         "Свой цвет", -9021441
      )
      .setVisible(() -> !this.itGy5sz.getValue());
   private final List<KillEffect.AnimatedEffect> f0rive = new ArrayList<>();
   private final List<KillEffect.MasInstance> fsld8 = new ArrayList<>();
   private Entity mOkszf;

   public KillEffect() {
      WorldRenderEvents.LAST.register((Last)context -> {
         if (this.isEnabled()) {
            long now = System.currentTimeMillis();
            int color = 0xFF000000 | (this.itGy5sz.getValue() ? ColorProvider.getColorClient() : this.sCbB.getValue()) & 16777215;

            for (KillEffect.MasInstance fx : this.fsld8) {
               MasEffectRenderer.render(fx.effect(), context.matrixStack(), context.camera(), fx.position(), now - fx.startedAt(), fx.duration(), fx.size(), color);
            }
         }
      });
   }

   @Subscribe
   private void onAttack(EventAttack e) {
      if (e.getEntity() instanceof PlayerEntity) {
         this.mOkszf = e.getEntity();
      }
   }

   @Subscribe
   private void onTotemPop(EventPopTotem event) {
      if (!this.mU1Q.is("Off") && event.getPlayer() != null) {
         String totemMode = this.mU1Q.getValue();

         String name = switch (totemMode) {
            case "Revive Spark" -> "revive_spark";
            case "Shield Wave" -> "shield_wave";
            default -> "revive";
         };
         PlayerEntity player = event.getPlayer();
         this.fsld8
            .add(
               new KillEffect.MasInstance(
                  name,
                  new Vec3d(player.getX(), player.getY() + player.getHeight() * 0.5, player.getZ()),
                  System.currentTimeMillis(),
                  900L,
                  this.mU1Q.is("Shield Wave") ? 2.2F : 1.2F
               )
            );
      }
   }

   @Subscribe
   public void onUpdate(EventTick ignored) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.mOkszf != null && this.mOkszf.getRemovalReason() == RemovalReason.DISCARDED) {
            this.tnK4(this.mOkszf);
            this.mOkszf = null;
         }

         this.gPpuVkq();
         this.fsld8.removeIf(fx -> System.currentTimeMillis() - fx.startedAt() >= fx.duration());
      }
   }

   private void gPpuVkq() {
      Iterator<KillEffect.AnimatedEffect> iterator = this.f0rive.iterator();

      while (iterator.hasNext()) {
         KillEffect.AnimatedEffect fx = iterator.next();
         fx.tick();
         if (fx.isFinished()) {
            iterator.remove();
         }
      }
   }

   private void tnK4(Entity entity) {
      if (entity != null) {
         double x = entity.getX();
         double y = entity.getY();
         double z = entity.getZ();
         double centerY = y + entity.getHeight() / 2.0F;
         double height = entity.getHeight();
         String effectSetting = this.kbfziv9.getValue();

         String masEffect = switch (effectSetting) {
            case "Death Skull" -> "death_skull";
            case "Death Spark" -> "death_spark";
            case "Diamond Scrap" -> "diamond_scrap";
            case "Netherite Scrap" -> "netherite_scrap";
            case "Flash" -> "flash";
            case "Flick" -> "flick";
            case "Smash" -> "smash";
            default -> null;
         };
         if (masEffect != null) {
            float effectSize = this.kbfziv9.is("Smash") ? 2.4F : (this.kbfziv9.is("Flash") ? 1.8F : 1.25F);
            this.fsld8.add(new KillEffect.MasInstance(masEffect, new Vec3d(x, centerY, z), System.currentTimeMillis(), 850L, effectSize));
         } else {
            switch (effectSetting) {
               case "Lightning":
                  this.msojd(x, y, z);
                  break;
               case "Explosion":
                  this.pA1feR(x, centerY, z);
                  break;
               case "Blood Rain":
                  this.f0rive.add(new KillEffect.BloodRainEffect(x, y, z, height));
                  break;
               case "Disintegrate":
                  this.f0rive.add(new KillEffect.DisintegrateEffect(x, y, z, height));
                  break;
               case "Black Hole":
                  this.f0rive.add(new KillEffect.BlackHoleEffect(x, y, z, height));
                  break;
               case "Shockwave":
                  this.f0rive.add(new KillEffect.ShockwaveEffect(x, y, z));
                  break;
               case "Ascension":
                  this.f0rive.add(new KillEffect.AscensionEffect(x, y, z, height));
                  break;
               case "Tornado":
                  this.f0rive.add(new KillEffect.TornadoEffect(x, y, z, height));
                  break;
               case "Meteor":
                  this.f0rive.add(new KillEffect.MeteorEffect(x, y, z));
                  break;
               case "Orbital Strike":
                  this.f0rive.add(new KillEffect.OrbitalStrikeEffect(x, y, z, height));
                  break;
               case "Phoenix Rise":
                  this.f0rive.add(new KillEffect.PhoenixRiseEffect(x, y, z, height));
                  break;
               case "Pig Bomb":
                  this.f0rive.add(new KillEffect.PigBombEffect(x, y, z));
                  break;
               case "Shatter":
                  this.f0rive.add(new KillEffect.ShatterEffect(x, y, z, height));
                  break;
               case "Thunder Clap":
                  this.f0rive.add(new KillEffect.ThunderClapEffect(x, y, z, height));
                  break;
               case "Crown Drop":
                  this.f0rive.add(new KillEffect.CrownDropEffect(x, y, z, height));
                  break;
               case "Rainbow Burst":
                  this.f0rive.add(new KillEffect.RainbowBurstEffect(x, y, z, height));
                  break;
               case "Soul Ascend":
                  this.f0rive.add(new KillEffect.SoulAscendEffect(x, y, z, height));
                  break;
               case "Firework Burst":
                  this.f0rive.add(new KillEffect.FireworkBurstEffect(x, y, z, height));
            }
         }
      }
   }

   private void msojd(double x, double y, double z) {
      LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, this.mc.world);
      lightning.refreshPositionAfterTeleport(x, y, z);
      lightning.setCosmetic(true);
      this.mc.world.addEntity(lightning);
      this.mc.world.playSound(this.mc.player, x, y, z, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 5.0F, 1.0F);
   }

   private void pA1feR(double x, double y, double z) {
      for (int i = 0; i < 20; i++) {
         double ox = (this.mc.world.random.nextDouble() - 0.5) * 2.0;
         double oy = (this.mc.world.random.nextDouble() - 0.5) * 2.0;
         double oz = (this.mc.world.random.nextDouble() - 0.5) * 2.0;
         this.mc.world.addParticle(ParticleTypes.EXPLOSION, x + ox, y + oy, z + oz, ox * 0.1, oy * 0.1, oz * 0.1);
      }

      this.mc.world.playSound(this.mc.player, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 10.0F, 1.0F);
   }

   private abstract class AnimatedEffect {
      protected double x;
      protected double y;
      protected double z;
      protected int ticks = 0;
      protected int maxTicks;

      AnimatedEffect(double x, double y, double z, int maxTicks) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.maxTicks = maxTicks;
      }

      abstract void tick();

      boolean isFinished() {
         return this.ticks >= this.maxTicks;
      }
   }

   private class AscensionEffect extends KillEffect.AnimatedEffect {
      private final double iYwP;

      AscensionEffect(double x, double y, double z, double height) {
         super(x, y, z, 50);
         this.iYwP = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 2.0F, 1.2F);
      }

      @Override
      void tick() {
         this.ticks++;
         double beamHeight = this.ticks * 0.5;

         for (int i = 0; i < 10; i++) {
            double py = this.y + beamHeight * i / 10.0;
            if (!(py > this.y + 15.0)) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3;
               KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, this.x + ox, py, this.z + oz, 0.0, 0.05, 0.0);
            }
         }

         if (this.ticks < 30) {
            double radius = 0.8;

            for (int ix = 0; ix < 8; ix++) {
               double angle = (Math.PI / 4) * ix + this.ticks * 0.2;
               double px = this.x + Math.cos(angle) * radius;
               double pz = this.z + Math.sin(angle) * radius;
               KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, px, this.y + 0.1, pz, 0.0, 0.1, 0.0);
            }
         }

         if (this.ticks == 40) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 2.0F, 1.5F);

            for (int ix = 0; ix < 30; ix++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double speed = 0.2 + KillEffect.this.mc.world.random.nextDouble() * 0.3;
               KillEffect.this.mc
                  .world
                  .addParticle(
                     ParticleTypes.END_ROD,
                     this.x,
                     this.y + 10.0,
                     this.z,
                     Math.cos(angle) * speed,
                     KillEffect.this.mc.world.random.nextDouble() * 0.2,
                     Math.sin(angle) * speed
                  );
            }
         }
      }
   }

   private class BlackHoleEffect extends KillEffect.AnimatedEffect {
      private final double hrNgDyi;

      BlackHoleEffect(double x, double y, double z, double height) {
         super(x, y, z, 50);
         this.hrNgDyi = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 2.0F, 0.3F);
      }

      @Override
      void tick() {
         this.ticks++;
         double centerY = this.y + this.hrNgDyi / 2.0;
         if (this.ticks < 35) {
            double radius = 3.0 - this.ticks * 0.07;

            for (int i = 0; i < 15; i++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double r = radius * (0.5 + KillEffect.this.mc.world.random.nextDouble() * 0.5);
               double px = this.x + Math.cos(angle) * r;
               double pz = this.z + Math.sin(angle) * r;
               double py = centerY + (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 2.0;
               double vx = (this.x - px) * 0.1;
               double vy = (centerY - py) * 0.1;
               double vz = (this.z - pz) * 0.1;
               if (i % 2 == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.REVERSE_PORTAL, px, py, pz, vx, vy, vz);
               } else {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.SQUID_INK, px, py, pz, vx, vy, vz);
               }
            }

            for (int ix = 0; ix < 5; ix++) {
               KillEffect.this.mc
                  .world
                  .addParticle(
                     ParticleTypes.DRAGON_BREATH,
                     this.x + (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3,
                     centerY + (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3,
                     this.z + (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3,
                     0.0,
                     0.0,
                     0.0
                  );
            }
         }

         if (this.ticks == 36) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 3.0F, 0.5F);

            for (int ix = 0; ix < 50; ix++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double pitch = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * Math.PI;
               double speed = 0.5 + KillEffect.this.mc.world.random.nextDouble() * 0.5;
               double vx = Math.cos(angle) * Math.cos(pitch) * speed;
               double vy = Math.sin(pitch) * speed;
               double vz = Math.sin(angle) * Math.cos(pitch) * speed;
               KillEffect.this.mc.world.addParticle(ParticleTypes.REVERSE_PORTAL, this.x, centerY, this.z, vx, vy, vz);
               if (ix % 3 == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.EXPLOSION, this.x, centerY, this.z, vx * 0.5, vy * 0.5, vz * 0.5);
               }
            }
         }
      }
   }

   private class BloodRainEffect extends KillEffect.AnimatedEffect {
      private final double vDGQg1;

      BloodRainEffect(double x, double y, double z, double height) {
         super(x, y, z, 50);
         this.vDGQg1 = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 2.0F, 0.5F);
      }

      @Override
      void tick() {
         this.ticks++;
         if (this.ticks < 40) {
            for (int i = 0; i < 8; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 3.0;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 3.0;
               double startY = this.y + this.vDGQg1 + 3.0 + KillEffect.this.mc.world.random.nextDouble() * 2.0;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FALLING_DRIPSTONE_LAVA, this.x + ox, startY, this.z + oz, 0.0, -0.5, 0.0);
               if (i % 2 == 0) {
                  KillEffect.this.mc
                     .world
                     .addParticle(
                        ParticleTypes.DAMAGE_INDICATOR,
                        this.x + ox,
                        this.y + KillEffect.this.mc.world.random.nextDouble() * this.vDGQg1,
                        this.z + oz,
                        0.0,
                        0.0,
                        0.0
                     );
               }
            }
         }

         if (this.ticks % 10 == 0 && this.ticks < 35) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundCategory.BLOCKS, 1.0F, 0.5F);
         }
      }
   }

   private class CrownDropEffect extends KillEffect.AnimatedEffect {
      private final double pZmycU;
      private double ymjP5;
      private double vm4lef2 = 0.0;

      CrownDropEffect(double x, double y, double z, double height) {
         super(x, y, z, 50);
         this.pZmycU = height;
         this.ymjP5 = y + height + 5.0;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 2.0F, 1.2F);
      }

      @Override
      void tick() {
         this.ticks++;
         this.vm4lef2 += 0.15;
         if (this.ticks < 35) {
            this.ymjP5 -= 0.15;
            double crownRadius = 0.4;

            for (int i = 0; i < 5; i++) {
               double angle = this.vm4lef2 + (Math.PI * 2.0 / 5.0) * i;
               double px = this.x + Math.cos(angle) * crownRadius;
               double pz = this.z + Math.sin(angle) * crownRadius;
               KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, px, this.ymjP5, pz, 0.0, 0.02, 0.0);
               KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, px, this.ymjP5 + 0.3, pz, 0.0, 0.01, 0.0);
            }

            for (int i = 0; i < 5; i++) {
               double angle = this.vm4lef2 + (Math.PI * 2.0 / 5.0) * i + (Math.PI / 5);
               double px = this.x + Math.cos(angle) * crownRadius * 0.7;
               double pz = this.z + Math.sin(angle) * crownRadius * 0.7;
               KillEffect.this.mc.world.addParticle(ParticleTypes.GLOW, px, this.ymjP5 + 0.5, pz, 0.0, 0.01, 0.0);
            }

            if (this.ticks % 3 == 0) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               KillEffect.this.mc
                  .world
                  .addParticle(ParticleTypes.FIREWORK, this.x + Math.cos(angle) * 0.3, this.ymjP5 + 0.2, this.z + Math.sin(angle) * 0.3, 0.0, -0.02, 0.0);
            }
         }

         if (this.ticks == 35) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 2.0F, 1.0F);

            for (int i = 0; i < 30; i++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double speed = 0.1 + KillEffect.this.mc.world.random.nextDouble() * 0.2;
               KillEffect.this.mc
                  .world
                  .addParticle(
                     ParticleTypes.END_ROD,
                     this.x,
                     this.ymjP5,
                     this.z,
                     Math.cos(angle) * speed,
                     0.1 + KillEffect.this.mc.world.random.nextDouble() * 0.1,
                     Math.sin(angle) * speed
                  );
               KillEffect.this.mc
                  .world
                  .addParticle(ParticleTypes.GLOW, this.x, this.ymjP5, this.z, Math.cos(angle) * speed * 0.5, 0.05, Math.sin(angle) * speed * 0.5);
            }
         }

         if (this.ticks > 35 && this.ticks < 50) {
            for (int i = 0; i < 3; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.5;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.5;
               KillEffect.this.mc
                  .world
                  .addParticle(ParticleTypes.END_ROD, this.x + ox, this.ymjP5 + KillEffect.this.mc.world.random.nextDouble() * 0.5, this.z + oz, 0.0, 0.03, 0.0);
            }
         }
      }
   }

   private class DisintegrateEffect extends KillEffect.AnimatedEffect {
      private final double fnw0;

      DisintegrateEffect(double x, double y, double z, double height) {
         super(x, y, z, 35);
         this.fnw0 = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 2.0F, 0.3F);
      }

      @Override
      void tick() {
         this.ticks++;
         double progress = (double)this.ticks / this.maxTicks;
         int particleCount = (int)(20.0 * (1.0 - progress * 0.5));

         for (int i = 0; i < particleCount; i++) {
            double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.6;
            double oy = KillEffect.this.mc.world.random.nextDouble() * this.fnw0;
            double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.6;
            double speed = 0.1 + progress * 0.3;
            double vx = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * speed;
            double vy = (KillEffect.this.mc.world.random.nextDouble() - 0.3) * speed;
            double vz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * speed;
            if (i % 3 == 0) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.ASH, this.x + ox, this.y + oy, this.z + oz, vx, vy, vz);
            } else if (i % 3 == 1) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.WHITE_ASH, this.x + ox, this.y + oy, this.z + oz, vx, vy, vz);
            } else {
               KillEffect.this.mc.world.addParticle(ParticleTypes.SMOKE, this.x + ox, this.y + oy, this.z + oz, vx, vy, vz);
            }
         }

         if (this.ticks == this.maxTicks - 5) {
            for (int ix = 0; ix < 30; ix++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double speed = 0.3 + KillEffect.this.mc.world.random.nextDouble() * 0.2;
               KillEffect.this.mc
                  .world
                  .addParticle(
                     ParticleTypes.POOF,
                     this.x,
                     this.y + this.fnw0 / 2.0,
                     this.z,
                     Math.cos(angle) * speed,
                     (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.2,
                     Math.sin(angle) * speed
                  );
            }
         }
      }
   }

   private class FireworkBurstEffect extends KillEffect.AnimatedEffect {
      private final double gr00;
      private int o4lGpWM = 0;

      FireworkBurstEffect(double x, double y, double z, double height) {
         super(x, y, z, 45);
         this.gr00 = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 2.0F, 1.0F);
      }

      @Override
      void tick() {
         this.ticks++;
         double centerY = this.y + this.gr00 / 2.0;
         if (this.ticks < 10) {
            for (int i = 0; i < 5; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FIREWORK, this.x + ox, this.y + this.ticks * 0.3, this.z + oz, 0.0, 0.1, 0.0);
            }
         }

         if (this.ticks == 10 || this.ticks == 20 || this.ticks == 30) {
            this.o4lGpWM++;
            double burstY = centerY + (this.o4lGpWM - 2) * 1.5;
            KillEffect.this.mc
               .world
               .playSound(
                  KillEffect.this.mc.player,
                  this.x,
                  burstY,
                  this.z,
                  SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST,
                  SoundCategory.AMBIENT,
                  2.0F,
                  0.8F + this.o4lGpWM * 0.2F
               );
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, burstY, this.z, SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.AMBIENT, 1.5F, 1.0F);

            for (int i = 0; i < 40; i++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double pitch = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * Math.PI;
               double speed = 0.25 + KillEffect.this.mc.world.random.nextDouble() * 0.35;
               double vx = Math.cos(angle) * Math.cos(pitch) * speed;
               double vy = Math.sin(pitch) * speed;
               double vz = Math.sin(angle) * Math.cos(pitch) * speed;
               switch ((i + this.o4lGpWM) % 6) {
                  case 0:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.FIREWORK, this.x, burstY, this.z, vx, vy, vz);
                     break;
                  case 1:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, this.x, burstY, this.z, vx * 0.7, vy * 0.7, vz * 0.7);
                     break;
                  case 2:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.CRIT, this.x, burstY, this.z, vx, vy, vz);
                     break;
                  case 3:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.ENCHANTED_HIT, this.x, burstY, this.z, vx, vy, vz);
                     break;
                  case 4:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.GLOW, this.x, burstY, this.z, vx * 0.5, vy * 0.5, vz * 0.5);
                     break;
                  case 5:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.FLASH, this.x, burstY, this.z, 0.0, 0.0, 0.0);
               }
            }
         }

         if (this.ticks > 10 && this.ticks < 20 || this.ticks > 20 && this.ticks < 30 || this.ticks > 30 && this.ticks < 40) {
            for (int i = 0; i < 8; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 2.0;
               double oy = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 2.0;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 2.0;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FIREWORK, this.x + ox, centerY + oy, this.z + oz, -ox * 0.02, -oy * 0.02, -oz * 0.02);
            }
         }

         if (this.ticks == 40) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, centerY, this.z, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.AMBIENT, 3.0F, 1.0F);

            for (int i = 0; i < 60; i++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double pitch = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * Math.PI;
               double speed = 0.4 + KillEffect.this.mc.world.random.nextDouble() * 0.4;
               double vx = Math.cos(angle) * Math.cos(pitch) * speed;
               double vy = Math.sin(pitch) * speed + 0.1;
               double vz = Math.sin(angle) * Math.cos(pitch) * speed;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FIREWORK, this.x, centerY, this.z, vx, vy, vz);
            }
         }
      }
   }

   private class FlameSpiralEffect extends KillEffect.AnimatedEffect {
      private final double o509w;

      FlameSpiralEffect(double x, double y, double z, double height) {
         super(x, y, z, 30);
         this.o509w = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 2.0F, 1.0F);
      }

      @Override
      void tick() {
         this.ticks++;
         double progress = (double)this.ticks / this.maxTicks;
         double currentHeight = progress * (this.o509w + 2.0);
         double radius = 0.8 - progress * 0.3;

         for (int spiral = 0; spiral < 2; spiral++) {
            double baseAngle = this.ticks * 0.5 + spiral * Math.PI;

            for (int i = 0; i < 3; i++) {
               double angle = baseAngle + i * 0.3;
               double px = this.x + Math.cos(angle) * radius;
               double pz = this.z + Math.sin(angle) * radius;
               double py = this.y + currentHeight - i * 0.2;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.02, 0.0);
               if (i == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0, 0.01, 0.0);
               }
            }
         }

         if (this.ticks % 3 == 0) {
            double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
            KillEffect.this.mc
               .world
               .addParticle(ParticleTypes.LAVA, this.x + Math.cos(angle) * 0.5, this.y + currentHeight, this.z + Math.sin(angle) * 0.5, 0.0, 0.0, 0.0);
         }
      }
   }

   private record MasInstance(String effect, Vec3d position, long startedAt, long duration, float size) {



      

      

      

      

      
   }

   private class MeteorEffect extends KillEffect.AnimatedEffect {
      private double qYS1e;
      private boolean gUH7nDp = false;

      MeteorEffect(double x, double y, double z) {
         super(x, y, z, 40);
         this.qYS1e = y + 20.0;
         KillEffect.this.mc
            .world
            .playSound(KillEffect.this.mc.player, x, y + 10.0, z, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 2.0F, 0.5F);
      }

      @Override
      void tick() {
         this.ticks++;
         if (!this.gUH7nDp) {
            this.qYS1e--;

            for (int i = 0; i < 8; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.5;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.5;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, this.x + ox, this.qYS1e + i * 0.3, this.z + oz, ox * 0.05, 0.1, oz * 0.05);
               if (i % 2 == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.LAVA, this.x + ox, this.qYS1e + i * 0.3, this.z + oz, 0.0, 0.0, 0.0);
               }
            }

            for (int ix = 0; ix < 3; ix++) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.LARGE_SMOKE, this.x, this.qYS1e, this.z, 0.0, 0.0, 0.0);
            }

            if (this.qYS1e <= this.y + 1.0) {
               this.gUH7nDp = true;
               KillEffect.this.mc
                  .world
                  .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 5.0F, 0.7F);

               for (int ix = 0; ix < 40; ix++) {
                  double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
                  double speed = 0.3 + KillEffect.this.mc.world.random.nextDouble() * 0.4;
                  double vx = Math.cos(angle) * speed;
                  double vy = 0.2 + KillEffect.this.mc.world.random.nextDouble() * 0.3;
                  double vz = Math.sin(angle) * speed;
                  KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, this.x, this.y + 0.5, this.z, vx, vy, vz);
                  if (ix % 2 == 0) {
                     KillEffect.this.mc.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + 0.5, this.z, vx * 0.5, vy * 0.5, vz * 0.5);
                  }
               }
            }
         } else if (this.ticks % 2 == 0) {
            for (int ixx = 0; ixx < 5; ixx++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 2.0;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 2.0;
               KillEffect.this.mc.world.addParticle(ParticleTypes.SMOKE, this.x + ox, this.y + 0.3, this.z + oz, 0.0, 0.05, 0.0);
            }
         }
      }
   }

   private class OrbitalStrikeEffect extends KillEffect.AnimatedEffect {
      private final double wvj9A;
      private boolean qeal8z = false;

      OrbitalStrikeEffect(double x, double y, double z, double height) {
         super(x, y, z, 60);
         this.wvj9A = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 3.0F, 0.5F);
      }

      @Override
      void tick() {
         this.ticks++;
         if (this.ticks < 20) {
            double radius = 1.5;

            for (int i = 0; i < 16; i++) {
               double angle = (Math.PI / 8) * i + this.ticks * 0.1;
               double px = this.x + Math.cos(angle) * radius;
               double pz = this.z + Math.sin(angle) * radius;
               KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, px, this.y + 0.1, pz, 0.0, 0.02, 0.0);
            }

            for (int i = 0; i < 8; i++) {
               double angle = (Math.PI / 4) * i - this.ticks * 0.15;
               double px = this.x + Math.cos(angle) * 0.7;
               double pz = this.z + Math.sin(angle) * 0.7;
               KillEffect.this.mc.world.addParticle(ParticleTypes.ELECTRIC_SPARK, px, this.y + 0.1, pz, 0.0, 0.01, 0.0);
            }
         }

         if (this.ticks >= 15 && this.ticks < 25) {
            double beamProgress = (this.ticks - 15) / 10.0;
            double beamTop = this.y + 25.0 - beamProgress * 25.0;

            for (double py = beamTop; py < this.y + 25.0; py += 0.5) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.4;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.4;
               KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, this.x + ox, py, this.z + oz, 0.0, -0.5, 0.0);
               if (KillEffect.this.mc.world.random.nextDouble() < 0.3) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.ELECTRIC_SPARK, this.x + ox, py, this.z + oz, ox * 0.1, 0.0, oz * 0.1);
               }
            }
         }

         if (this.ticks == 25 && !this.qeal8z) {
            this.qeal8z = true;
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 5.0F, 0.7F);
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 4.0F, 1.2F);

            for (int i = 0; i < 30; i++) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.FLASH, this.x, this.y + this.wvj9A / 2.0, this.z, 0.0, 0.0, 0.0);
            }

            for (int i = 0; i < 60; i++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double pitch = (KillEffect.this.mc.world.random.nextDouble() - 0.3) * Math.PI * 0.5;
               double speed = 0.4 + KillEffect.this.mc.world.random.nextDouble() * 0.5;
               double vx = Math.cos(angle) * Math.cos(pitch) * speed;
               double vy = Math.sin(pitch) * speed + 0.1;
               double vz = Math.sin(angle) * Math.cos(pitch) * speed;
               KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, this.x, this.y + 0.5, this.z, vx, vy, vz);
               if (i % 2 == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.ELECTRIC_SPARK, this.x, this.y + 0.5, this.z, vx * 1.5, vy * 1.5, vz * 1.5);
               }
            }
         }

         if (this.ticks > 25 && this.ticks < 45) {
            double waveRadius = (this.ticks - 25) * 0.5;
            int particles = 20 + (this.ticks - 25) * 2;

            for (int ix = 0; ix < particles; ix++) {
               double angle = (Math.PI * 2) / particles * ix;
               double px = this.x + Math.cos(angle) * waveRadius;
               double pz = this.z + Math.sin(angle) * waveRadius;
               KillEffect.this.mc.world.addParticle(ParticleTypes.CLOUD, px, this.y + 0.2, pz, Math.cos(angle) * 0.05, 0.02, Math.sin(angle) * 0.05);
            }

            if (this.ticks % 2 == 0) {
               for (int ix = 0; ix < 5; ix++) {
                  double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 1.5;
                  double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 1.5;
                  KillEffect.this.mc
                     .world
                     .addParticle(
                        ParticleTypes.ELECTRIC_SPARK, this.x + ox, this.y + KillEffect.this.mc.world.random.nextDouble() * 2.0, this.z + oz, 0.0, 0.05, 0.0
                     );
               }
            }
         }
      }
   }

   private class PhoenixRiseEffect extends KillEffect.AnimatedEffect {
      private final double uI2z;

      PhoenixRiseEffect(double x, double y, double z, double height) {
         super(x, y, z, 55);
         this.uI2z = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ENTITY_BLAZE_DEATH, SoundCategory.HOSTILE, 2.0F, 1.5F);
      }

      @Override
      void tick() {
         this.ticks++;
         double riseHeight = Math.min(this.ticks * 0.25, 8.0);
         double phoenixY = this.y + riseHeight;
         if (this.ticks < 45) {
            for (int i = 0; i < 8; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.4;
               double oy = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.6;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.4;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, this.x + ox, phoenixY + oy, this.z + oz, ox * 0.02, 0.1, oz * 0.02);
               if (i % 2 == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.x + ox, phoenixY + oy, this.z + oz, ox * 0.01, 0.08, oz * 0.01);
               }
            }

            double wingSpan = 1.0 + Math.sin(this.ticks * 0.2) * 0.3;

            for (int wing = -1; wing <= 1; wing += 2) {
               for (int ix = 0; ix < 5; ix++) {
                  double wingX = this.x + wing * (wingSpan * (0.3 + ix * 0.2));
                  double wingY = phoenixY - ix * 0.15 + Math.sin(this.ticks * 0.3 + ix) * 0.1;
                  double oy = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.2;
                  KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, wingX, wingY + oy, this.z, wing * 0.05, 0.02, 0.0);
                  if (ix < 3) {
                     KillEffect.this.mc.world.addParticle(ParticleTypes.SMALL_FLAME, wingX, wingY + oy, this.z, wing * 0.03, 0.01, 0.0);
                  }
               }
            }

            for (int ixx = 0; ixx < 4; ixx++) {
               double tailY = phoenixY - 0.5 - ixx * 0.3;
               if (tailY > this.y) {
                  double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3;
                  double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3;
                  KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, this.x + ox, tailY, this.z + oz, 0.0, -0.05, 0.0);
                  KillEffect.this.mc.world.addParticle(ParticleTypes.LAVA, this.x + ox, tailY, this.z + oz, 0.0, 0.0, 0.0);
               }
            }
         }

         if (this.ticks < 35) {
            double trailRadius = 0.8 + this.ticks * 0.03;

            for (int ixxx = 0; ixxx < 6; ixxx++) {
               double angle = (Math.PI / 3) * ixxx + this.ticks * 0.1;
               double px = this.x + Math.cos(angle) * trailRadius;
               double pz = this.z + Math.sin(angle) * trailRadius;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, px, this.y + 0.1, pz, 0.0, 0.05, 0.0);
            }
         }

         if (this.ticks == 45) {
            KillEffect.this.mc
               .world
               .playSound(
                  KillEffect.this.mc.player, this.x, phoenixY, this.z, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.AMBIENT, 2.0F, 1.2F
               );

            for (int ixxx = 0; ixxx < 50; ixxx++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double pitch = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * Math.PI;
               double speed = 0.3 + KillEffect.this.mc.world.random.nextDouble() * 0.4;
               double vx = Math.cos(angle) * Math.cos(pitch) * speed;
               double vy = Math.sin(pitch) * speed + 0.2;
               double vz = Math.sin(angle) * Math.cos(pitch) * speed;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, this.x, phoenixY, this.z, vx, vy, vz);
               if (ixxx % 3 == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.x, phoenixY, this.z, vx * 0.8, vy * 0.8, vz * 0.8);
               }
            }
         }

         if (this.ticks > 45 && this.ticks < 55) {
            for (int ixxxx = 0; ixxxx < 8; ixxxx++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 4.0;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 4.0;
               double startY = this.y + 8.0 + KillEffect.this.mc.world.random.nextDouble() * 2.0;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FALLING_LAVA, this.x + ox, startY, this.z + oz, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   private class PigBombEffect extends KillEffect.AnimatedEffect {
      private PigEntity p3ct5;
      private double i027Wrv;
      private boolean op6x8y = false;
      private static final double START_HEIGHT = 15.0;
      private static final double FALL_SPEED = 0.8;

      PigBombEffect(double x, double y, double z) {
         super(x, y, z, 60);
         this.i027Wrv = y + 15.0;
         this.b1uSgH();
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y + 15.0, z, SoundEvents.ENTITY_PIG_AMBIENT, SoundCategory.NEUTRAL, 3.0F, 1.5F);
      }

      private void b1uSgH() {
         this.p3ct5 = new PigEntity(EntityType.PIG, KillEffect.this.mc.world);
         this.p3ct5.refreshPositionAndAngles(this.x, this.i027Wrv, this.z, 0.0F, 0.0F);
         this.p3ct5.setNoGravity(true);
         this.p3ct5.setInvulnerable(true);
         KillEffect.this.mc.world.addEntity(this.p3ct5);
      }

      @Override
      void tick() {
         this.ticks++;
         if (!this.op6x8y && this.p3ct5 != null) {
            this.i027Wrv -= 0.8;
            this.p3ct5.refreshPositionAndAngles(this.x, this.i027Wrv, this.z, this.p3ct5.getYaw() + 15.0F, 0.0F);

            for (int i = 0; i < 6; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.6;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.6;
               double trailY = this.i027Wrv + 0.5 + i * 0.25;
               KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, this.x + ox, trailY, this.z + oz, ox * 0.02, 0.05, oz * 0.02);
               if (i % 2 == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.SMOKE, this.x + ox, trailY, this.z + oz, 0.0, 0.03, 0.0);
               }
            }

            if (this.ticks % 2 == 0) {
               for (int ix = 0; ix < 3; ix++) {
                  double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
                  double r = 0.4 + KillEffect.this.mc.world.random.nextDouble() * 0.3;
                  KillEffect.this.mc
                     .world
                     .addParticle(ParticleTypes.LAVA, this.x + Math.cos(angle) * r, this.i027Wrv + 0.3, this.z + Math.sin(angle) * r, 0.0, 0.0, 0.0);
               }
            }

            if (this.ticks % 5 == 0) {
               KillEffect.this.mc
                  .world
                  .playSound(
                     KillEffect.this.mc.player,
                     this.x,
                     this.i027Wrv,
                     this.z,
                     SoundEvents.ENTITY_PIG_HURT,
                     SoundCategory.NEUTRAL,
                     1.5F,
                     0.5F + this.ticks * 0.03F
                  );
            }

            if (this.i027Wrv <= this.y + 1.0) {
               this.op6x8y = true;
               this.p3ct5.discard();
               this.p3ct5 = null;
               this.zzah5ow();
            }
         }

         if (this.op6x8y && this.ticks < this.maxTicks && this.ticks % 2 == 0) {
            for (int ix = 0; ix < 4; ix++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 2.5;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 2.5;
               KillEffect.this.mc.world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.x + ox, this.y + 0.3, this.z + oz, 0.0, 0.08, 0.0);
            }
         }
      }

      private void zzah5ow() {
         KillEffect.this.mc
            .world
            .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 5.0F, 0.8F);
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_PIG_DEATH, SoundCategory.NEUTRAL, 3.0F, 0.5F);

         for (int i = 0; i < 50; i++) {
            double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
            double pitch = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * Math.PI;
            double speed = 0.4 + KillEffect.this.mc.world.random.nextDouble() * 0.5;
            double vx = Math.cos(angle) * Math.cos(pitch) * speed;
            double vy = Math.sin(pitch) * speed + 0.2;
            double vz = Math.sin(angle) * Math.cos(pitch) * speed;
            KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, this.x, this.y + 0.5, this.z, vx, vy, vz);
            if (i % 2 == 0) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + 0.5, this.z, vx * 0.3, vy * 0.3, vz * 0.3);
            }

            if (i % 4 == 0) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.LAVA, this.x, this.y + 0.5, this.z, vx * 0.5, vy * 0.5, vz * 0.5);
            }
         }

         for (int i = 0; i < 25; i++) {
            double anglex = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
            double speedx = 0.2 + KillEffect.this.mc.world.random.nextDouble() * 0.3;
            double vxx = Math.cos(anglex) * speedx;
            double vyx = 0.1 + KillEffect.this.mc.world.random.nextDouble() * 0.3;
            double vzx = Math.sin(anglex) * speedx;
            KillEffect.this.mc.world.addParticle(ParticleTypes.HEART, this.x, this.y + 0.5, this.z, vxx, vyx, vzx);
         }

         for (int ring = 0; ring < 3; ring++) {
            double radius = 1.0 + ring * 0.8;
            int particles = 16 + ring * 8;

            for (int i = 0; i < particles; i++) {
               double anglex = (Math.PI * 2) / particles * i;
               double px = this.x + Math.cos(anglex) * radius;
               double pz = this.z + Math.sin(anglex) * radius;
               KillEffect.this.mc.world.addParticle(ParticleTypes.CLOUD, px, this.y + 0.2, pz, Math.cos(anglex) * 0.1, 0.05, Math.sin(anglex) * 0.1);
            }
         }
      }

      @Override
      boolean isFinished() {
         if (this.p3ct5 != null && this.ticks >= this.maxTicks) {
            this.p3ct5.discard();
            this.p3ct5 = null;
         }

         return this.ticks >= this.maxTicks;
      }
   }

   private class RainbowBurstEffect extends KillEffect.AnimatedEffect {
      private final double vx0vD;

      RainbowBurstEffect(double x, double y, double z, double height) {
         super(x, y, z, 40);
         this.vx0vD = height;
         KillEffect.this.mc
            .world
            .playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, SoundCategory.AMBIENT, 2.0F, 1.5F);
      }

      @Override
      void tick() {
         this.ticks++;
         double centerY = this.y + this.vx0vD / 2.0;
         if (this.ticks == 1) {
            for (int i = 0; i < 60; i++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double pitch = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * Math.PI;
               double speed = 0.3 + KillEffect.this.mc.world.random.nextDouble() * 0.4;
               double vx = Math.cos(angle) * Math.cos(pitch) * speed;
               double vy = Math.sin(pitch) * speed + 0.1;
               double vz = Math.sin(angle) * Math.cos(pitch) * speed;
               switch (i % 7) {
                  case 0:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.FLAME, this.x, centerY, this.z, vx, vy, vz);
                     break;
                  case 1:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.x, centerY, this.z, vx, vy, vz);
                     break;
                  case 2:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, this.x, centerY, this.z, vx, vy, vz);
                     break;
                  case 3:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.GLOW, this.x, centerY, this.z, vx, vy, vz);
                     break;
                  case 4:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.ENCHANTED_HIT, this.x, centerY, this.z, vx, vy, vz);
                     break;
                  case 5:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.CRIT, this.x, centerY, this.z, vx, vy, vz);
                     break;
                  case 6:
                     KillEffect.this.mc.world.addParticle(ParticleTypes.FIREWORK, this.x, centerY, this.z, vx, vy, vz);
               }
            }
         }

         if (this.ticks < 30) {
            for (int ring = 0; ring < 3; ring++) {
               double radius = this.ticks * 0.15 + ring * 0.3;
               int particles = 12 + ring * 4;
               double ringY = centerY + (ring - 1) * 0.5;

               for (int i = 0; i < particles; i++) {
                  double angle = (Math.PI * 2) / particles * i + this.ticks * 0.1 * (ring % 2 == 0 ? 1 : -1);
                  double px = this.x + Math.cos(angle) * radius;
                  double pz = this.z + Math.sin(angle) * radius;
                  switch ((i + ring + this.ticks) % 5) {
                     case 0:
                        KillEffect.this.mc.world.addParticle(ParticleTypes.END_ROD, px, ringY, pz, 0.0, 0.02, 0.0);
                        break;
                     case 1:
                        KillEffect.this.mc.world.addParticle(ParticleTypes.GLOW, px, ringY, pz, 0.0, 0.02, 0.0);
                        break;
                     case 2:
                        KillEffect.this.mc.world.addParticle(ParticleTypes.FIREWORK, px, ringY, pz, 0.0, 0.02, 0.0);
                        break;
                     case 3:
                        KillEffect.this.mc.world.addParticle(ParticleTypes.ENCHANTED_HIT, px, ringY, pz, 0.0, 0.02, 0.0);
                        break;
                     case 4:
                        KillEffect.this.mc.world.addParticle(ParticleTypes.CRIT, px, ringY, pz, 0.0, 0.02, 0.0);
                  }
               }
            }
         }

         if (this.ticks % 10 == 0 && this.ticks < 30) {
            KillEffect.this.mc
               .world
               .playSound(
                  KillEffect.this.mc.player,
                  this.x,
                  this.y,
                  this.z,
                  SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE,
                  SoundCategory.AMBIENT,
                  1.0F,
                  1.0F + this.ticks * 0.02F
               );
         }
      }
   }

   private class ShatterEffect extends KillEffect.AnimatedEffect {
      private final double h0hTF;
      private final double[][] on0Hn3 = new double[20][6];

      ShatterEffect(double x, double y, double z, double height) {
         super(x, y, z, 40);
         this.h0hTF = height;

         for (int i = 0; i < this.on0Hn3.length; i++) {
            this.on0Hn3[i][0] = x + (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.6;
            this.on0Hn3[i][1] = y + KillEffect.this.mc.world.random.nextDouble() * height;
            this.on0Hn3[i][2] = z + (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.6;
            double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
            double speed = 0.15 + KillEffect.this.mc.world.random.nextDouble() * 0.2;
            this.on0Hn3[i][3] = Math.cos(angle) * speed;
            this.on0Hn3[i][4] = 0.1 + KillEffect.this.mc.world.random.nextDouble() * 0.15;
            this.on0Hn3[i][5] = Math.sin(angle) * speed;
         }

         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 2.0F, 0.8F);
      }

      @Override
      void tick() {
         this.ticks++;

         for (double[] shard : this.on0Hn3) {
            shard[0] += shard[3];
            shard[1] += shard[4];
            shard[2] += shard[5];
            shard[4] -= 0.02;
            KillEffect.this.mc.world.addParticle(ParticleTypes.CRIT, shard[0], shard[1], shard[2], 0.0, 0.0, 0.0);
            if (KillEffect.this.mc.world.random.nextDouble() < 0.3) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.ENCHANTED_HIT, shard[0], shard[1], shard[2], 0.0, 0.0, 0.0);
            }
         }

         if (this.ticks % 5 == 0 && this.ticks < 25) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, 1.2F);
         }
      }
   }

   private class ShockwaveEffect extends KillEffect.AnimatedEffect {
      ShockwaveEffect(double x, double y, double z) {
         super(x, y, z, 25);
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 2.0F, 1.5F);
      }

      @Override
      void tick() {
         this.ticks++;
         double radius = this.ticks * 0.4;
         int particles = 20 + this.ticks * 2;

         for (int i = 0; i < particles; i++) {
            double angle = (Math.PI * 2) / particles * i;
            double px = this.x + Math.cos(angle) * radius;
            double pz = this.z + Math.sin(angle) * radius;
            double vx = Math.cos(angle) * 0.1;
            double vz = Math.sin(angle) * 0.1;
            KillEffect.this.mc.world.addParticle(ParticleTypes.CLOUD, px, this.y + 0.1, pz, vx, 0.02, vz);
            if (i % 3 == 0) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.SWEEP_ATTACK, px, this.y + 0.5, pz, 0.0, 0.0, 0.0);
            }
         }

         if (this.ticks % 5 == 0) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 0.8F);
         }
      }
   }

   private class SoulAscendEffect extends KillEffect.AnimatedEffect {
      private final double xcP180;
      private double ae5Qr0x;

      SoulAscendEffect(double x, double y, double z, double height) {
         super(x, y, z, 55);
         this.xcP180 = height;
         this.ae5Qr0x = y + height / 2.0;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 2.0F, 0.6F);
      }

      @Override
      void tick() {
         this.ticks++;
         if (this.ticks < 15) {
            for (int i = 0; i < 5; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.8;
               double oy = KillEffect.this.mc.world.random.nextDouble() * this.xcP180;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.8;
               KillEffect.this.mc
                  .world
                  .addParticle(ParticleTypes.SOUL, this.x + ox, this.y + oy, this.z + oz, (this.x - (this.x + ox)) * 0.05, 0.1, (this.z - (this.z + oz)) * 0.05);
            }
         }

         if (this.ticks >= 15 && this.ticks < 50) {
            this.ae5Qr0x += 0.2;
            double wobble = Math.sin(this.ticks * 0.3) * 0.2;
            double soulX = this.x + wobble;
            double soulZ = this.z + Math.cos(this.ticks * 0.3) * 0.2;

            for (int i = 0; i < 4; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3;
               double oy = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.5;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3;
               KillEffect.this.mc.world.addParticle(ParticleTypes.SOUL, soulX + ox, this.ae5Qr0x + oy, soulZ + oz, 0.0, 0.05, 0.0);
            }

            if (this.ticks % 2 == 0) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.SCULK_SOUL, soulX, this.ae5Qr0x, soulZ, 0.0, 0.08, 0.0);
            }

            for (int i = 0; i < 2; i++) {
               double trailY = this.ae5Qr0x - 0.5 - i * 0.3;
               if (trailY > this.y) {
                  KillEffect.this.mc
                     .world
                     .addParticle(
                        ParticleTypes.SOUL,
                        soulX + (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.2,
                        trailY,
                        soulZ + (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.2,
                        0.0,
                        -0.02,
                        0.0
                     );
               }
            }
         }

         if (this.ticks == 50) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.ae5Qr0x, this.z, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1.5F, 2.0F);

            for (int ix = 0; ix < 20; ix++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double speed = 0.1 + KillEffect.this.mc.world.random.nextDouble() * 0.15;
               KillEffect.this.mc
                  .world
                  .addParticle(
                     ParticleTypes.SOUL,
                     this.x,
                     this.ae5Qr0x,
                     this.z,
                     Math.cos(angle) * speed,
                     0.2 + KillEffect.this.mc.world.random.nextDouble() * 0.1,
                     Math.sin(angle) * speed
                  );
            }
         }
      }
   }

   private class SoulEffect extends KillEffect.AnimatedEffect {
      private final double rijrN4T;

      SoulEffect(double x, double y, double z, double height) {
         super(x, y, z, 40);
         this.rijrN4T = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 2.0F, 0.8F);
      }

      @Override
      void tick() {
         this.ticks++;
         if (this.ticks < 25) {
            for (int i = 0; i < 3; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.8;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.8;
               double oy = KillEffect.this.mc.world.random.nextDouble() * this.rijrN4T;
               KillEffect.this.mc
                  .world
                  .addParticle(
                     ParticleTypes.SOUL,
                     this.x + ox,
                     this.y + oy,
                     this.z + oz,
                     (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.02,
                     0.08,
                     (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.02
                  );
            }
         }

         if (this.ticks % 2 == 0 && this.ticks < 30) {
            double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
            double r = KillEffect.this.mc.world.random.nextDouble() * 0.6;
            KillEffect.this.mc
               .world
               .addParticle(ParticleTypes.SCULK_SOUL, this.x + Math.cos(angle) * r, this.y + this.rijrN4T * 0.5, this.z + Math.sin(angle) * r, 0.0, 0.1, 0.0);
         }

         if (this.ticks == 25) {
            for (int i = 0; i < 10; i++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               KillEffect.this.mc
                  .world
                  .addParticle(ParticleTypes.SOUL, this.x, this.y + this.rijrN4T + 1.0, this.z, Math.cos(angle) * 0.1, 0.15, Math.sin(angle) * 0.1);
            }
         }
      }
   }

   private class ThunderClapEffect extends KillEffect.AnimatedEffect {
      private final double nttVkg;
      private boolean s5Rs = false;

      ThunderClapEffect(double x, double y, double z, double height) {
         super(x, y, z, 35);
         this.nttVkg = height;
      }

      @Override
      void tick() {
         this.ticks++;
         double centerY = this.y + this.nttVkg / 2.0;
         if (this.ticks < 10) {
            for (int i = 0; i < 8; i++) {
               double ox = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.5;
               double oy = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * this.nttVkg;
               double oz = (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.5;
               KillEffect.this.mc
                  .world
                  .addParticle(
                     ParticleTypes.ELECTRIC_SPARK, this.x + ox, centerY + oy, this.z + oz, (this.x - (this.x + ox)) * 0.1, 0.0, (this.z - (this.z + oz)) * 0.1
                  );
            }
         }

         if (this.ticks == 10 && !this.s5Rs) {
            this.s5Rs = true;
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 5.0F, 1.5F);
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.MASTER, 3.0F, 1.8F);

            for (int i = 0; i < 40; i++) {
               KillEffect.this.mc.world.addParticle(ParticleTypes.FLASH, this.x, centerY, this.z, 0.0, 0.0, 0.0);
            }

            for (int i = 0; i < 30; i++) {
               double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
               double speed = 0.5 + KillEffect.this.mc.world.random.nextDouble() * 0.3;
               KillEffect.this.mc
                  .world
                  .addParticle(
                     ParticleTypes.ELECTRIC_SPARK,
                     this.x,
                     centerY,
                     this.z,
                     Math.cos(angle) * speed,
                     (KillEffect.this.mc.world.random.nextDouble() - 0.5) * 0.3,
                     Math.sin(angle) * speed
                  );
            }
         }

         if (this.ticks > 10 && this.ticks < 30) {
            double waveRadius = (this.ticks - 10) * 0.6;
            int particles = 24 + (this.ticks - 10) * 2;

            for (int i = 0; i < particles; i++) {
               double angle = (Math.PI * 2) / particles * i;
               double px = this.x + Math.cos(angle) * waveRadius;
               double pz = this.z + Math.sin(angle) * waveRadius;
               KillEffect.this.mc.world.addParticle(ParticleTypes.CLOUD, px, centerY, pz, Math.cos(angle) * 0.08, 0.0, Math.sin(angle) * 0.08);
               if (i % 4 == 0) {
                  KillEffect.this.mc.world.addParticle(ParticleTypes.SONIC_BOOM, px, centerY, pz, 0.0, 0.0, 0.0);
               }
            }
         }
      }
   }

   private class TornadoEffect extends KillEffect.AnimatedEffect {
      private final double f9g6;

      TornadoEffect(double x, double y, double z, double height) {
         super(x, y, z, 45);
         this.f9g6 = height;
         KillEffect.this.mc.world.playSound(KillEffect.this.mc.player, x, y, z, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.HOSTILE, 2.0F, 0.5F);
      }

      @Override
      void tick() {
         this.ticks++;
         double tornadoHeight = Math.min(this.ticks * 0.3, 6.0);

         for (int layer = 0; layer < 10; layer++) {
            double layerY = this.y + tornadoHeight * layer / 10.0;
            if (!(layerY > this.y + tornadoHeight)) {
               double layerProgress = layer / 10.0;
               double radius = 0.3 + layerProgress * 1.2;
               double rotationSpeed = 0.4 - layerProgress * 0.2;

               for (int i = 0; i < 4; i++) {
                  double angle = this.ticks * rotationSpeed + (Math.PI / 2) * i + layer * 0.3;
                  double px = this.x + Math.cos(angle) * radius;
                  double pz = this.z + Math.sin(angle) * radius;
                  double vx = Math.cos(angle + (Math.PI / 2)) * 0.1;
                  double vz = Math.sin(angle + (Math.PI / 2)) * 0.1;
                  if (layer % 2 == 0) {
                     KillEffect.this.mc.world.addParticle(ParticleTypes.CLOUD, px, layerY, pz, vx, 0.05, vz);
                  } else {
                     KillEffect.this.mc.world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, layerY, pz, vx, 0.03, vz);
                  }
               }
            }
         }

         if (this.ticks % 3 == 0) {
            double angle = KillEffect.this.mc.world.random.nextDouble() * Math.PI * 2.0;
            KillEffect.this.mc
               .world
               .addParticle(
                  ParticleTypes.CRIT,
                  this.x + Math.cos(angle) * 1.5,
                  this.y + 0.2,
                  this.z + Math.sin(angle) * 1.5,
                  (this.x - (this.x + Math.cos(angle) * 1.5)) * 0.1,
                  0.2,
                  (this.z - (this.z + Math.sin(angle) * 1.5)) * 0.1
               );
         }

         if (this.ticks % 15 == 0) {
            KillEffect.this.mc
               .world
               .playSound(KillEffect.this.mc.player, this.x, this.y, this.z, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.HOSTILE, 1.5F, 0.6F);
         }
      }
   }
}
