package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import java.util.List;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.util.math.RotationUtil;
import zov.viola.util.player.other.InventoryUtil;
import zov.viola.util.player.other.SlownessManager;
import zov.viola.util.player.other.WorldUtils;
import zov.viola.util.rotation.Rotation;
import zov.viola.util.rotation.RotationComponent;

@ModuleInformation(
   moduleName = "High Jump",
   moduleDesc = "Увеличивает высоту прыжка",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class HighJump extends Module {
   private boolean tMhsmE;
   private boolean oio58K9;
   private int sZQR;
   private BlockPos q6ywf;

   @Subscribe
   public void onUpdate(EventTick ignored) {
      if (this.mc.player != null) {
         int slot = InventoryUtil.searchItem(
            List.of(Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.BLUE_SHULKER_BOX)
         );
         if (this.oio58K9) {
            this.na4C8();
         } else if (slot != -1) {
            BlockPos nearestBlock = WorldUtils.findNearestPlaceableBlock();
            if (nearestBlock != null) {
               RotationComponent.update(new Rotation(RotationUtil.calculate(nearestBlock.toCenterPos())), 360.0F, 360.0F, 360.0F, 360.0F, 0, 10, false);
               SlownessManager.addTask(
                  new SlownessManager.SlowTask(
                     0L,
                     () -> {
                        if (!this.tMhsmE) {
                           if (this.mc.crosshairTarget instanceof BlockHitResult hitResult) {
                              this.q6ywf = nearestBlock;
                              if (slot >= 0 && slot <= 8) {
                                 this.sZQR = slot;
                                 this.mc.player.getInventory().selectedSlot = slot;
                              } else {
                                 InventoryUtil.swapWithBypassGrim(() -> {
                                    this.sZQR = slot;
                                    this.mc
                                       .interactionManager
                                       .clickSlot(0, slot, this.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, this.mc.player);
                                 });
                              }

                              this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, hitResult);
                              if (this.sZQR >= 0 && this.sZQR <= 8) {
                                 this.mc.player.getInventory().selectedSlot = this.sZQR;
                              } else {
                                 InventoryUtil.swapWithBypassGrim(
                                    () -> this.mc
                                       .interactionManager
                                       .clickSlot(0, this.sZQR, this.mc.player.getInventory().selectedSlot, SlotActionType.SWAP, this.mc.player)
                                 );
                              }

                              this.tMhsmE = true;
                           }

                           if (this.oio58K9) {
                              return;
                           }

                           RotationComponent.update(
                              new Rotation(RotationUtil.calculate(this.q6ywf.toCenterPos())), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1424821, false
                           );
                           SlownessManager.addTask(new SlownessManager.SlowTask(50L, () -> {
                              if (this.q6ywf != null) {
                                 if (this.mc.crosshairTarget instanceof BlockHitResult hitResultx) {
                                    this.mc.interactionManager.interactBlock(this.mc.player, Hand.MAIN_HAND, hitResultx);
                                 }

                                 this.oio58K9 = true;
                              }
                           }));
                        }
                     }
                  )
               );
            }
         }
      }
   }

   private void na4C8() {
      for (BlockPos pos : BlockPos.iterate(this.mc.player.getBlockPos().add(-6, -6, -6), this.mc.player.getBlockPos().add(6, 6, 6))) {
         if (this.mc.world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity shulker) {
            double dx = this.mc.player.getX() - (pos.getX() + 0.5);
            double dz = this.mc.player.getZ() - (pos.getZ() + 0.5);
            double dy = this.mc.player.getY() - (pos.getY() + 0.5);
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            if (horizontalDistance > 1.5) {
               this.setEnabled(false);
            } else if (!(Math.abs(dy) > (this.mc.player.getVelocity().y > 1.0 ? 30.0 : 2.0)) && this.mc.player.isOnGround()) {
               float progress = shulker.getAnimationProgress(1.0F);
               if (progress > 0.0F && progress <= 1.0F) {
                  Vec3d vel = this.mc.player.getVelocity();
                  this.mc.player.setVelocity(vel.x, 2.33F, vel.z);
                  this.mc.player.velocityDirty = true;
                  this.setEnabled(false);
                  break;
               }
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.oio58K9 = false;
      this.tMhsmE = false;
   }
}
