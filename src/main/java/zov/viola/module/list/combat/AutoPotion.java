package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import lombok.Generated;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import zov.viola.event.list.EventPlayerSync;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.obf.D;
import zov.viola.util.math.StopWatch;

@ModuleInformation(
   moduleName = "Auto Potion",
   moduleDesc = "Автоматически кидает выбранные бафы",
   moduleCategory = ModuleCategory.COMBAT
)
public class AutoPotion extends Module {
   private final ModeListSetting cWz0px = new ModeListSetting(
      "Кидать",
      new BooleanSetting("Зелье силы", true),
      new BooleanSetting("Зелье скорости", true),
      new BooleanSetting(
         D.k(
            new int[]{1178, 1152, 1166, 1103, 1208, 149, 1163, 1072, 1200, 1152, 1268, 1089, 1203, 1164, 1167, 1085, 1228, 1271, 1165},
            new int[]{141, 181, 181, 3}
         ),
         true
      )
   );
   private final BooleanSetting vwYx2 = new BooleanSetting(
      D.k(
         new int[]{
            1177,
            1161,
            1027,
            1047,
            1221,
            1157,
            1033,
            1134,
            1223,
            226,
            1030,
            1042,
            1226,
            1273,
            1036,
            12,
            1203,
            1155,
            1030,
            1042,
            1200,
            1166,
            1038,
            1042,
            1209,
            1266,
            1028,
            1044,
            1220
         },
         new int[]{139, 194, 57, 44}
      ),
      false
   );
   private final StopWatch fr17we = new StopWatch();
   private boolean o5Zfozw = false;
   private static AutoPotion agk0ojl;

   @Subscribe
   public void onSync(EventPlayerSync e) {
      if (this.mc.player != null) {
         this.o5Zfozw = this.bvJ9Q();
      }
   }

   @Subscribe
   public void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.o5Zfozw) {
            if (this.fr17we.isReached(500L)) {
               for (AutoPotion.PotionType type : AutoPotion.PotionType.values()) {
                  if (type.isSettingEnabled() && !this.mc.player.hasStatusEffect(type.getEffect())) {
                     int slot = this.ayi0(type.getEffect());
                     if (slot != -1) {
                        this.cTZZKIp(slot);
                        this.fr17we.reset();
                        if (this.vwYx2.getValue()) {
                           this.setEnabled(false);
                        }

                        return;
                     }
                  }
               }
            }
         }
      }
   }

   private void cTZZKIp(int slot) {
      int previousSlot = this.mc.player.getInventory().selectedSlot;
      if (slot < 9) {
         this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
         this.z0h4Y3();
         this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
      } else {
         this.mc.interactionManager.clickSlot(0, slot, previousSlot, SlotActionType.SWAP, this.mc.player);
         this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
         this.z0h4Y3();
         this.mc.interactionManager.clickSlot(0, slot, previousSlot, SlotActionType.SWAP, this.mc.player);
      }
   }

   private void z0h4Y3() {
      AutoPotionThrowPlan.ServerRotation rotation = AutoPotionThrowPlan.forYaw(this.mc.player.getYaw());
      this.mc
         .getNetworkHandler()
         .sendPacket(new LookAndOnGround(rotation.yaw(), rotation.pitch(), this.mc.player.isOnGround(), this.mc.player.horizontalCollision));
      this.mc
         .interactionManager
         .sendSequencedPacket(this.mc.world, sequence -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, rotation.yaw(), rotation.pitch()));
   }

   private int ayi0(RegistryEntry<StatusEffect> effect) {
      for (int i = 0; i < 45; i++) {
         ItemStack stack = this.mc.player.getInventory().getStack(i);
         if (stack.getItem() == Items.SPLASH_POTION) {
            PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (contents != null) {
               for (StatusEffectInstance effectInstance : contents.getEffects()) {
                  if (effectInstance.getEffectType().equals(effect)) {
                     return i;
                  }
               }
            }
         }
      }

      return -1;
   }

   private boolean bvJ9Q() {
      if (this.mc.player != null && this.mc.world != null) {
         boolean onGround = this.mc.player.isOnGround()
            || this.mc.world.getBlockState(BlockPos.ofFloored(this.mc.player.getX(), this.mc.player.getY() - 0.3, this.mc.player.getZ())).isSolid();
         if (!onGround) {
            return false;
         } else if (this.mc.player.isClimbing()) {
            return false;
         } else if (this.mc.player.hasVehicle()) {
            return false;
         } else if (this.mc.player.getAbilities().flying) {
            return false;
         } else if (!this.mc.player.isTouchingWater() && !this.mc.player.isInLava()) {
            for (AutoPotion.PotionType type : AutoPotion.PotionType.values()) {
               if (type.isSettingEnabled() && !this.mc.player.hasStatusEffect(type.getEffect()) && this.ayi0(type.getEffect()) != -1) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onDisable() {
      this.o5Zfozw = false;
      super.onDisable();
   }

   public AutoPotion() {
      agk0ojl = this;
   }

   public static AutoPotion getStaticInstance() {
      return agk0ojl;
   }

   private static enum PotionType {
      STRENGTH(StatusEffects.STRENGTH, "Зелье силы"),
      SPEED(StatusEffects.SPEED, "Зелье скорости"),
      FIRE_RESISTANCE(
         StatusEffects.FIRE_RESISTANCE,
         D.k(
            new int[]{1171, 1063, 1084, 1166, 1201, 50, 1081, 1265, 1209, 1063, 1094, 1152, 1210, 1067, 1085, 1276, 1221, 1104, 1087},
            new int[]{132, 18, 7, 194}
         )
      );

      private final RegistryEntry<StatusEffect> qzpg5a;
      private final String yAodB4;

      public boolean isSettingEnabled() {
         return AutoPotion.getStaticInstance().cWz0px.isEnabled(this.yAodB4);
      }

      @Generated
      public RegistryEntry<StatusEffect> getEffect() {
         return this.qzpg5a;
      }

      @Generated
      public String getSettingName() {
         return this.yAodB4;
      }

      @Generated
      private PotionType(final RegistryEntry<StatusEffect> effect, final String settingName) {
         this.qzpg5a = effect;
         this.yAodB4 = settingName;
      }
   }
}
