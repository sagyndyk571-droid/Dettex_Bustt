package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventPlayerSyncEnd;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.math.RotationUtil;
import zov.viola.util.math.StopWatch;
import zov.viola.util.player.other.InventoryUtil;
import zov.viola.util.rotation.Rotation;
import zov.viola.util.rotation.RotationComponent;

@ModuleInformation(
   moduleName = "AutoTrap",
   moduleDesc = "Ставит двойную паутину в ноги и рост цели",
   moduleCategory = ModuleCategory.COMBAT
)
public class AutoTrap extends Module {
   private final SliderSetting wgzpr9 = new SliderSetting("Радиус", 3.2F, 2.0, 4.5, 0.1F);
   private final SliderSetting diXL8q = new SliderSetting(
      "Задержка (тики)", 7.0, 4.0, 12.0, 1.0
   );
   private final BooleanSetting vj40c1 = new BooleanSetting(
      "Пауза в GUI", true
   );
   private final BooleanSetting iLo422T = new BooleanSetting("Предикт", true);
   private final SliderSetting fui0 = new SliderSetting(
         "Предикт тиков", 3.0, 1.0, 10.0, 1.0
      )
      .setVisible(this.iLo422T::getValue);
   private final StopWatch xl6waA8 = new StopWatch();

   @Subscribe
   private void onUpdate(EventPlayerUpdate ignored) {
      if (this.mc.player != null && this.mc.world != null) {
         if (!this.vj40c1.getValue() || this.mc.currentScreen == null) {
            if (this.xl6waA8.isReached((long)(this.diXL8q.getValue() * 50.0))) {
               PlayerEntity target = this.qHaU8n5();
               if (target != null) {
                  BlockPos placePos = this.nEoqh1a(target);
                  if (placePos != null) {
                     BlockHitResult hit = this.aMV3OW(placePos);
                     if (hit != null) {
                        Vec2f rot = RotationUtil.calculate(this.mc.player.getEyePos(), hit.getPos());
                        RotationComponent.update(new Rotation(rot), 65.0F, 65.0F, 180.0F, 2, 6);
                     }
                  }
               }
            }
         }
      }
   }

   @Subscribe
   private void onSyncEnd(EventPlayerSyncEnd ignored) {
      if (this.mc.player != null && this.mc.world != null) {
         if (!this.vj40c1.getValue() || this.mc.currentScreen == null) {
            if (this.xl6waA8.isReached((long)(this.diXL8q.getValue() * 50.0))) {
               PlayerEntity target = this.qHaU8n5();
               if (target != null) {
                  int slot = InventoryUtil.searchItemHotbar(Items.COBWEB);
                  if (slot != -1) {
                     BlockPos trapPos = this.nEoqh1a(target);
                     if (trapPos != null) {
                        BlockPos headPos = trapPos.up();
                        boolean placedAny = false;
                        if (this.mc.world.getBlockState(trapPos).isAir()) {
                           BlockHitResult hit = this.aMV3OW(trapPos);
                           if (hit != null && this.jigb(hit, slot)) {
                              placedAny = true;
                           }
                        }

                        if (this.mc.world.getBlockState(headPos).isAir()) {
                           BlockHitResult hit = this.aMV3OW(headPos);
                           if (hit != null && this.jigb(hit, slot)) {
                              placedAny = true;
                           }
                        }

                        if (placedAny) {
                           this.xl6waA8.reset();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean jigb(BlockHitResult hit, int slot) {
      int previous = this.mc.player.getInventory().selectedSlot;
      this.mc.player.getInventory().selectedSlot = slot;
      this.mc.interactionManager.syncSelectedSlot();
      ActionResult result = this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, hit);
      if (result instanceof Success) {
         this.mc.player.swingHand(Hand.MAIN_HAND);
      }

      this.mc.player.getInventory().selectedSlot = previous;
      this.mc.interactionManager.syncSelectedSlot();
      return result instanceof Success;
   }

   private PlayerEntity qHaU8n5() {
      PlayerEntity best = null;
      double bestDistance = Double.MAX_VALUE;

      for (PlayerEntity player : this.mc.world.getPlayers()) {
         if (player != this.mc.player && player.isAlive() && !FriendRepository.isFriend(player.getNameForScoreboard())) {
            double distance = this.mc.player.distanceTo(player);
            if (!(distance > this.wgzpr9.getValue()) && distance < bestDistance) {
               bestDistance = distance;
               best = player;
            }
         }
      }

      return best;
   }

   private BlockPos nEoqh1a(PlayerEntity target) {
      if (!this.iLo422T.getValue()) {
         return target.getBlockPos();
      } else {
         Vec3d velocity = new Vec3d(target.getX() - target.prevX, target.getY() - target.prevY, target.getZ() - target.prevZ);
         if (velocity.lengthSquared() < 0.001) {
            return target.getBlockPos();
         } else {
            Vec3d predictedPos = target.getPos().add(velocity.multiply(this.fui0.getValue()));
            return BlockPos.ofFloored(predictedPos);
         }
      }
   }

   private BlockHitResult aMV3OW(BlockPos placePos) {
      if (!this.mc.world.getBlockState(placePos).isAir()) {
         return null;
      } else {
         for (Direction dir : Direction.values()) {
            BlockPos support = placePos.offset(dir);
            BlockState state = this.mc.world.getBlockState(support);
            if (!state.isAir() && !state.isOf(Blocks.COBWEB)) {
               Direction face = dir.getOpposite();
               Vec3d center = new Vec3d(
                  support.getX() + 0.5 + face.getOffsetX() * 0.5,
                  support.getY() + 0.5 + face.getOffsetY() * 0.5,
                  support.getZ() + 0.5 + face.getOffsetZ() * 0.5
               );
               return new BlockHitResult(center, face, support, false);
            }
         }

         return null;
      }
   }
}
