package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Invis Drink",
   moduleDesc = "Автоматически пьёт зелье невидимости, когда эффект кончается",
   moduleCategory = ModuleCategory.PLAYER
)
public class InvisDrink extends Module {
   private final SliderSetting yR26 = new SliderSetting(
      "Обновлять за (сек.)",
      5.0,
      1.0,
      30.0,
      1.0
   );
   private boolean dSrg9I = false;
   private int zwR0OGK = -1;

   @Subscribe
   private void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         if (this.mc.currentScreen == null) {
            if (!this.dSrg9I) {
               int invisibleSeconds = 0;
               StatusEffectInstance current = this.mc.player.getStatusEffect(StatusEffects.INVISIBILITY);
               if (current != null) {
                  invisibleSeconds = Math.max(0, current.getDuration() / 20);
               }

               if (!(invisibleSeconds > this.yR26.getValue())) {
                  if (this.fjqGt(this.mc.player.getMainHandStack())) {
                     this.zwR0OGK = this.mc.player.getInventory().selectedSlot;
                  } else {
                     int slot = this.ek8wo49();
                     if (slot == -1) {
                        return;
                     }

                     this.zwR0OGK = this.mc.player.getInventory().selectedSlot;
                     this.mc.player.getInventory().selectedSlot = slot;
                  }

                  this.mc.options.useKey.setPressed(true);
                  this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
                  this.mc.player.swingHand(Hand.MAIN_HAND);
                  this.dSrg9I = true;
               }
            } else if (!this.mc.player.isUsingItem()) {
               this.t0s7r8();
            }
         }
      }
   }

   private int ek8wo49() {
      for (int i = 0; i < 9; i++) {
         if (this.fjqGt(this.mc.player.getInventory().getStack(i))) {
            return i;
         }
      }

      return -1;
   }

   private boolean fjqGt(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else if (stack.getItem() != Items.POTION && stack.getItem() != Items.SPLASH_POTION) {
         return false;
      } else {
         PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
         if (contents == null) {
            return false;
         } else {
            int count = 0;
            boolean onlyInvis = true;

            for (StatusEffectInstance effect : contents.getEffects()) {
               count++;
               if (!effect.getEffectType().equals(StatusEffects.INVISIBILITY)) {
                  onlyInvis = false;
               }
            }

            return count == 1 && onlyInvis;
         }
      }
   }

   private void t0s7r8() {
      if (this.mc.player != null) {
         if (this.mc.player.isUsingItem() && this.mc.interactionManager != null) {
            this.mc.interactionManager.stopUsingItem(this.mc.player);
         }

         if (this.zwR0OGK != -1) {
            this.mc.player.getInventory().selectedSlot = this.zwR0OGK;
         }
      }

      this.mc.options.useKey.setPressed(false);
      this.zwR0OGK = -1;
      this.dSrg9I = false;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.t0s7r8();
   }
}
