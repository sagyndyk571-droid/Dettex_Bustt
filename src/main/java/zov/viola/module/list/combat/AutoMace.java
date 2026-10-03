package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.math.StopWatch;
import zov.viola.util.rotation.Rotation;
import zov.viola.util.rotation.RotationComponent;

@ModuleInformation(
   moduleName = "AutoMace",
   moduleDesc = "Автоматически бьёт булавой при падении",
   moduleCategory = ModuleCategory.COMBAT
)
public class AutoMace extends Module {
   private final SliderSetting vWmvIi0 = new SliderSetting(
      "Дистанция", 3.0, 2.0, 3.0, 0.5
   );
   private final SliderSetting abi9ntL = new SliderSetting(
      D.k(
         new int[]{1176, 1039, 1192, 204, 164, 1029, 1246, 1187, 1210, 1141, 1189, 194, 1211, 1031, 1185, 1239, 1209, 1039, 1242}, new int[]{132, 55, 149, 226}
      ),
      3.0,
      0.0,
      20.0,
      0.5
   );
   private final BooleanSetting o5IU66 = new BooleanSetting(
      D.k(
         new int[]{1138, 1247, 1098, 1061, 1130, 1247, 81, 1110, 1040, 1241, 81, 1110, 1120, 1237, 1092, 1108, 1128, 1241, 81, 1115, 1133, 1241, 1094},
         new int[]{80, 225, 113, 105}
      ),
      true
   );
   private final BooleanSetting k9pks = new BooleanSetting(
      "Свап на булаву", true
   );
   private final BooleanSetting eFdnan = new BooleanSetting("SilentAim", true);
   private final ModeSetting fM5t = new ModeSetting(
      "Сортировка",
      "По дистанции",
      "По дистанции",
      "По полю-зрения",
      "По здоровью",
      "Умная"
   );
   private final StopWatch uFBWre = new StopWatch();
   private int mmFho7y = -1;

   @Override
   public void onDisable() {
      this.bCx615f();
      super.onDisable();
   }

   @Subscribe
   public void onTick(EventTick event) {
      if (this.mc.player == null || this.mc.world == null || this.mc.currentScreen != null) {
         this.bCx615f();
      } else if (this.mc.player.isOnGround()) {
         this.bCx615f();
      } else if (this.o5IU66.getValue() && this.mc.player.getVelocity().y >= 0.0) {
         this.bCx615f();
      } else if (this.mc.player.fallDistance < this.abi9ntL.getValue()) {
         this.bCx615f();
      } else {
         LivingEntity target = this.rLHQU6();
         if (target == null) {
            this.bCx615f();
         } else {
            if (this.k9pks.getValue()) {
               int maceSlot = this.gc7z();
               if (maceSlot == -1) {
                  return;
               }

               if (this.mc.player.getInventory().selectedSlot != maceSlot) {
                  this.mmFho7y = this.mc.player.getInventory().selectedSlot;
                  this.mc.player.getInventory().selectedSlot = maceSlot;
                  this.mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(maceSlot));
               }
            } else if (this.mc.player.getMainHandStack().getItem() != Items.MACE) {
               return;
            }

            if (this.uFBWre.isReached(450L)) {
               if (this.mc.player.isUsingItem()) {
                  this.mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
               }

               if (this.eFdnan.getValue()) {
                  RotationComponent.update(Rotation.from(this.mc.player, target), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
               }

               this.mc.interactionManager.attackEntity(this.mc.player, target);
               this.mc.player.swingHand(Hand.MAIN_HAND);
               this.bCx615f();
               this.uFBWre.reset();
            }
         }
      }
   }

   private LivingEntity rLHQU6() {
      LivingEntity best = null;
      double bestScore = Double.MAX_VALUE;
      if (this.fM5t.is("По полю-зрения")) {
         bestScore = -1.0;
      }

      Vec3d eyePos = this.mc.player.getEyePos();
      Vec3d lookVec = this.mc.player.getRotationVec(1.0F);

      for (Entity entity : this.mc.world.getEntities()) {
         double distance;
         if (entity instanceof LivingEntity
            && entity != this.mc.player
            && entity.isAlive()
            && !(entity instanceof ArmorStandEntity)
            && (!(entity instanceof PlayerEntity) || FriendRepository.shouldAttack((PlayerEntity)entity))
            && !((distance = this.mc.player.distanceTo(entity)) > this.vWmvIi0.getValue())) {
            LivingEntity living = (LivingEntity)entity;
            if (this.fM5t.is("По дистанции")) {
               if (distance < bestScore) {
                  bestScore = distance;
                  best = living;
               }
            } else if (this.fM5t
               .is("По полю-зрения")) {
               Vec3d direction = living.getBoundingBox().getCenter().subtract(eyePos).normalize();
               double dot = lookVec.dotProduct(direction);
               if (dot > bestScore) {
                  bestScore = dot;
                  best = living;
               }
            } else if (this.fM5t.is("По здоровью")) {
               double health = living.getHealth();
               if (health < bestScore) {
                  bestScore = health;
                  best = living;
               }
            } else {
               Vec3d dir = living.getBoundingBox().getCenter().subtract(eyePos).normalize();
               double ignoreAngle = 1.0 - Math.max(0.0, lookVec.dotProduct(dir));
               double normalizedDist = distance / this.vWmvIi0.getValue();
               double score = ignoreAngle * 0.5 + normalizedDist * 0.5;
               if (living instanceof PlayerEntity) {
                  ItemStack armor = ((PlayerEntity)living).getInventory().getArmorStack(2);
                  if (armor.isOf(Items.ELYTRA)) {
                     score -= 100.0;
                  }
               }

               if (score < bestScore) {
                  bestScore = score;
                  best = living;
               }
            }
         }
      }

      return best;
   }

   private int gc7z() {
      for (int i2 = 0; i2 < 9; i2++) {
         if (this.mc.player.getInventory().getStack(i2).getItem() == Items.MACE) {
            return i2;
         }
      }

      return -1;
   }

   private void bCx615f() {
      if (this.mmFho7y != -1 && this.mc.player != null) {
         this.mc.player.getInventory().selectedSlot = this.mmFho7y;
         this.mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.mmFho7y));
         this.mmFho7y = -1;
      }
   }
}
