package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.math.StopWatch;

@ModuleInformation(
   moduleName = "MaceTarget",
   moduleDesc = "Автоматическая атака булавой",
   moduleCategory = ModuleCategory.COMBAT
)
public class MaceTarget extends Module {
   private final SliderSetting ni020 = new SliderSetting(
      "Дистанция", 3.0, 2.0, 3.0, 0.5
   );
   private final SliderSetting acjhlW = new SliderSetting(
      "Мин. высота падения",
      3.0,
      0.0,
      20.0,
      0.5
   );
   private final BooleanSetting rAFpuEe = new BooleanSetting(
      "Свап на булаву", true
   );
   private final BooleanSetting t2sd1s = new BooleanSetting(
      D.k(
         new int[]{1143, 1210, 1214, 1082, 1135, 1210, 165, 1097, 1045, 1212, 165, 1097, 1125, 1200, 1200, 1099, 1133, 1212, 165, 1092, 1128, 1212, 1202},
         new int[]{85, 132, 133, 118}
      ),
      true
   );
   private final StopWatch ci4j0 = new StopWatch();
   private int nXLuG0p = -1;

   @Override
   public void onDisable() {
      this.cZhoduQ();
      super.onDisable();
   }

   @Subscribe
   public void onTick(EventTick event) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.mc.player.isOnGround()) {
            this.cZhoduQ();
         } else if (this.t2sd1s.getValue() && this.mc.player.getVelocity().y >= 0.0) {
            this.cZhoduQ();
         } else if (this.mc.player.fallDistance < this.acjhlW.getValue()) {
            this.cZhoduQ();
         } else {
            LivingEntity target = this.qs0waaf();
            if (target == null) {
               this.cZhoduQ();
            } else {
               if (this.rAFpuEe.getValue()) {
                  int maceSlot = this.q25aEmx();
                  if (maceSlot == -1) {
                     return;
                  }

                  if (this.mc.player.getInventory().selectedSlot != maceSlot) {
                     this.nXLuG0p = this.mc.player.getInventory().selectedSlot;
                     this.mc.player.getInventory().selectedSlot = maceSlot;
                     this.mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(maceSlot));
                  }
               } else if (this.mc.player.getMainHandStack().getItem() != Items.MACE) {
                  return;
               }

               if (this.ci4j0.isReached(450L)) {
                  this.mc.interactionManager.attackEntity(this.mc.player, target);
                  this.mc.player.swingHand(Hand.MAIN_HAND);
                  this.cZhoduQ();
                  this.ci4j0.reset();
               }
            }
         }
      }
   }

   private LivingEntity qs0waaf() {
      LivingEntity best = null;
      double bestDist = Double.MAX_VALUE;

      for (Entity entity : this.mc.world.getEntities()) {
         double dist;
         PlayerEntity p2;
         if (entity instanceof LivingEntity living
            && entity != this.mc.player
            && entity.isAlive()
            && !(entity instanceof ArmorStandEntity)
            && (!(entity instanceof PlayerEntity) || FriendRepository.shouldAttack(p2 = (PlayerEntity)entity))
            && !((dist = this.mc.player.distanceTo(entity)) > this.ni020.getValue())
            && dist < bestDist) {
            bestDist = dist;
            best = living;
         }
      }

      return best;
   }

   private int q25aEmx() {
      for (int i2 = 0; i2 < 9; i2++) {
         if (this.mc.player.getInventory().getStack(i2).getItem() == Items.MACE) {
            return i2;
         }
      }

      return -1;
   }

   private void cZhoduQ() {
      if (this.nXLuG0p != -1 && this.mc.player != null) {
         this.mc.player.getInventory().selectedSlot = this.nXLuG0p;
         this.mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.nXLuG0p));
         this.nXLuG0p = -1;
      }
   }
}
