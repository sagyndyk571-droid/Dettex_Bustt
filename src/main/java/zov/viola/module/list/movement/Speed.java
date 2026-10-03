package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.event.list.MoveInputEvent;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.player.move.MoveUtil;

@ModuleInformation(
   moduleName = "Speed",
   moduleDesc = "Увеличение скорости передвижения",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class Speed extends Module {
   private final ModeSetting dlew = new ModeSetting("Mode", "RW", "RW", "Collision", "MetaHVH", "Grim", "HolyWorld");
   private final SliderSetting lx4hc = new SliderSetting(
         "Скорость", 1.5, 1.0, 5.0, 0.1F
      )
      .setVisible(() -> this.dlew.is("MetaHVH"));
   private final SliderSetting gJTn = new SliderSetting(
         "Скорость (Grim)",
         0.05F,
         0.01F,
         0.15F,
         0.005F
      )
      .setVisible(() -> this.dlew.is("Grim"));
   private int gbqqmr;
   private int sppw;
   final Map<Entity, Vec3d> previousPositions = new HashMap<>();

   @Subscribe
   public void onPlayerUpdate(EventPlayerUpdate event) {
      if (this.dlew.is("Collision")) {
         this.lbjf();
      } else if (this.dlew.is("RW")) {
         this.ho51();
      } else if (this.dlew.is("MetaHVH")) {
         this.aynJ();
      } else if (this.dlew.is("Grim")) {
         this.kY38();
      } else if (this.dlew.is("HolyWorld")) {
         this.svv3qI();
      }
   }

   private void aynJ() {
      if (MoveUtil.isMoving()) {
         double speed = this.lx4hc.getFloatValue() / 3.0;
         double[] dir = MoveUtil.calculateDirection(speed);
         Vec3d vel = this.mc.player.getVelocity();
         this.mc.player.setVelocity(dir[0], vel.y, dir[1]);
      }
   }

   private void kY38() {
      if (MoveUtil.isMoving()) {
         int collisions = this.tfMIY(0.5);
         if (collisions > 0) {
            double boost = this.gJTn.getFloatValue() * collisions;
            double[] dir = MoveUtil.calculateDirection(boost);
            Vec3d vel = this.mc.player.getVelocity();
            this.mc.player.setVelocity(dir[0], vel.y, dir[1]);
         }
      }
   }

   private void svv3qI() {
      if (MoveUtil.isMoving()) {
         int collisions = this.tfMIY(0.35);
         if (collisions > 0) {
            double boost = 0.0205 * collisions;
            double[] dir = MoveUtil.calculateDirection(boost);
            Vec3d vel = this.mc.player.getVelocity();
            this.mc.player.setVelocity(dir[0], vel.y, dir[1]);
         }
      }
   }

   private int tfMIY(double inflate) {
      int collisions = 0;
      Box searchBox = this.mc.player.getBoundingBox().expand(2.0);

      for (Entity entity : this.mc.world.getOtherEntities(this.mc.player, searchBox)) {
         if (entity != this.mc.player && !(entity instanceof ArmorStandEntity)) {
            boolean valid = entity instanceof LivingEntity || entity.getType().toString().toLowerCase(Locale.ROOT).contains("boat");
            if (valid) {
               Box entityBox = entity.getBoundingBox().expand(inflate, 0.0, inflate);
               if (this.mc.player.getBoundingBox().intersects(entityBox)) {
                  collisions++;
               }
            }
         }
      }

      return collisions;
   }

   private void ho51() {
      if (!this.j6Rq0z()) {
         this.x0iii8(true);
      } else {
         if (this.gbqqmr > 3) {
            double boost = 0.03;
            if (this.gbqqmr % 2 == 0) {
               this.mc.player.addVelocity(0.0, 0.03, 0.0);
               boost = this.mc.player.isOnGround() ? 0.085 : 0.03;
            }

            double yaw = Math.toRadians(MoveUtil.getDirection());
            double x2 = -Math.sin(yaw);
            double z2 = Math.cos(yaw);
            if (this.mc.player.input.movementForward == -1.0F) {
               x2 = 0.0;
               z2 = 0.0;
            }

            this.mc.player.addVelocity(x2 * boost, 0.0, z2 * boost);
         }

         this.gbqqmr++;
         if (this.gbqqmr % 2 == 0) {
            this.mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(this.mc.player, Mode.START_FALL_FLYING));
         }
      }
   }

   private void lbjf() {
      if (MoveUtil.isMoving()) {
         double scanRadius = 1.25;
         Box searchBox = this.mc.player.getBoundingBox().expand(scanRadius);

         for (Entity entity2 : this.mc.world.getOtherEntities(this.mc.player, searchBox, entity -> entity instanceof PlayerEntity)) {
            if (entity2 instanceof PlayerEntity) {
               double distanceX = Math.abs(this.mc.player.getX() - entity2.getX());
               double distanceZ = Math.abs(this.mc.player.getZ() - entity2.getZ());
               if (!(distanceX > 2.1) && !(distanceZ > 1.3)) {
                  double entitySpeed = this.getEntitySpeed(entity2);
                  if (entitySpeed < 5.0) {
                     double boostAmount = 0.02;
                     Box collisionBox = this.mc.player.getBoundingBox().expand(0.1);
                     List<Entity> collisionEntities = this.mc.world.getOtherEntities(this.mc.player, collisionBox, e2 -> e2 instanceof PlayerEntity);
                     if (!collisionEntities.isEmpty()) {
                        double[] motion = this.z8hi(boostAmount);
                        this.mc.player.addVelocity(motion[0], 0.0, motion[1]);
                     }
                  } else {
                     double boostAmount = 0.032;
                     Box checkBox = this.mc.player.getBoundingBox().expand(1.25);
                     List<Entity> potentialCollisions = this.mc.world.getOtherEntities(this.mc.player, checkBox, e2 -> e2 instanceof PlayerEntity);
                     int collisions = 0;

                     for (Entity collisionEntity : potentialCollisions) {
                        double distToCollision = this.mc.player.distanceTo(collisionEntity);
                        if (distToCollision <= 1.25) {
                           collisions++;
                        }
                     }

                     if (collisions > 0) {
                        double[] motion = this.z8hi(boostAmount);
                        this.mc.player.addVelocity(motion[0], 0.0, motion[1]);
                     }
                  }
                  break;
               }
            }
         }
      }
   }

   @Subscribe
   public void onMoveInput(MoveInputEvent event) {
      if (this.dlew.is("RW") && this.mc.player != null && MoveUtil.isMoving()) {
         this.sppw = this.mc.player.verticalCollision ? ++this.sppw : 0;
         if (this.sppw >= 1) {
            this.mc.player.jump();
         }
      } else {
         this.sppw = 0;
      }
   }

   @Subscribe
   public void onPacket(EventPacket event) {
      if (this.dlew.is("RW") && this.mc.player != null && event.getType() == EventPacket.Type.RECEIVE) {
         if (event.getPacket() instanceof PlayerPositionLookS2CPacket && this.gbqqmr % 2 == 1) {
            this.gbqqmr++;
         }
      }
   }

   public double getEntitySpeed(Entity entity) {
      Vec3d currentPos = entity.getPos();
      Vec3d previousPos = this.previousPositions.getOrDefault(entity, currentPos);
      double dx = currentPos.x - previousPos.x;
      double dz = currentPos.z - previousPos.z;
      double speed = Math.sqrt(dx * dx + dz * dz) * 20.0;
      this.previousPositions.put(entity, currentPos);
      return speed;
   }

   private double[] z8hi(double speed) {
      float forward = this.mc.player.input.movementForward;
      float strafe = this.mc.player.input.movementSideways;
      float yaw = this.mc.player.getYaw();
      if (forward != 0.0F) {
         if (strafe > 0.0F) {
            yaw += forward > 0.0F ? -45.0F : 45.0F;
         } else if (strafe < 0.0F) {
            yaw += forward > 0.0F ? 45.0F : -45.0F;
         }

         strafe = 0.0F;
         if (forward > 0.0F) {
            forward = 1.0F;
         } else if (forward < 0.0F) {
            forward = -1.0F;
         }
      }

      double sin = Math.sin(Math.toRadians(yaw + 90.0F));
      double cos = Math.cos(Math.toRadians(yaw + 90.0F));
      double posX = forward * speed * cos + strafe * speed * sin;
      double posZ = forward * speed * sin - strafe * speed * cos;
      return new double[]{posX, posZ};
   }

   private boolean j6Rq0z() {
      return this.mc.player != null
         && this.mc.world != null
         && this.mc.player.networkHandler != null
         && MoveUtil.isMoving()
         && !this.mc.player.hasVehicle()
         && !this.mc.player.getAbilities().flying;
   }

   private void x0iii8(boolean resetTimer) {
      this.gbqqmr = 0;
      this.sppw = 0;
   }

   @Override
   public void onEnable() {
      this.x0iii8(true);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.x0iii8(true);
      super.onDisable();
   }
}
