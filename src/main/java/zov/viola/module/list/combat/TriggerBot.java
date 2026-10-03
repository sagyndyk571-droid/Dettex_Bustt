package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Trigger Bot",
   moduleDesc = "Автоматическая атака при наведении",
   moduleCategory = ModuleCategory.COMBAT
)
public class TriggerBot extends Module {
   public final BooleanSetting pauseEating = new BooleanSetting(
      "Остановка при еде", true
   );
   public final BooleanSetting onlyCriticals = new BooleanSetting(
      "Только криты", true
   );
   public final BooleanSetting spaceOnly = new BooleanSetting(
      "Умные криты", false
   );
   private int xq7ZMp;

   @Subscribe
   public void onEvent(EventTick e2) {
      if (this.mc.player != null) {
         if (!this.mc.player.isUsingItem() || !this.pauseEating.getValue()) {
            if (this.xq7ZMp > 0) {
               this.xq7ZMp--;
            } else if (this.dea1cs()) {
               Entity ent = this.mc.targetedEntity;
               if (ent != null) {
                  this.mc.interactionManager.attackEntity(this.mc.player, ent);
                  this.mc.player.swingHand(Hand.MAIN_HAND);
                  this.xq7ZMp = 10;
               }
            }
         }
      }
   }

   @Override
   public void onDisable() {
      this.xq7ZMp = 0;
      super.onDisable();
   }

   private boolean dea1cs() {
      boolean reasonForSkipCrit = !this.onlyCriticals.getValue()
         || this.mc.player.getAbilities().flying
         || this.mc.player.hasStatusEffect(StatusEffects.LEVITATION)
         || this.mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
         || this.mc.world.getBlockState(this.mc.player.getBlockPos()).getBlock() == Blocks.LADDER;
      float f2 = this.mc.player.getAttackCooldownProgress(0.5F);
      float f3 = this.mc.player.isOnGround() ? 1.0F : 0.9F;
      if (f2 < f3) {
         return false;
      } else {
         boolean mergeWithSpeed = this.mc.player.isOnGround();
         if (!this.mc.options.jumpKey.isPressed() && mergeWithSpeed && this.spaceOnly.getValue()) {
            return true;
         } else if (this.mc.player.isInLava()) {
            return true;
         } else {
            return reasonForSkipCrit ? true : !this.mc.player.isOnGround() && this.mc.player.fallDistance > 0.0F;
         }
      }
   }
}
