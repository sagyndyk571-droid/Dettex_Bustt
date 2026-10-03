package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.event.list.EventPopTotem;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.math.StopWatch;
import zov.viola.util.player.other.InventoryUtil;
import zov.viola.util.text.ValueUnit;

@ModuleInformation(
   moduleName = "Auto Totem",
   moduleDesc = "Автоматически берет тотем в руку",
   moduleCategory = ModuleCategory.COMBAT
)
public class AutoTotem extends Module {
   private final SliderSetting r2zSFC9 = new SliderSetting(
      "Здоровье",
      ValueUnit.abbreviation("ХП"),
      4.0,
      1.0,
      20.0,
      0.1F
   );
   private final SliderSetting rbrN = new SliderSetting(
      "Здоровье на элитре",
      ValueUnit.abbreviation("ХП"),
      11.0,
      1.0,
      20.0,
      0.1F
   );
   private final BooleanSetting fY8b947 = new BooleanSetting(
      D.k(
         new int[]{1169, 1148, 1150, 1265, 1267, 1148, 1037, 1155, 145, 1137, 1151, 239, 1163, 1036, 1143, 1166, 1267, 1148, 1140, 1156},
         new int[]{177, 76, 79, 207}
      ),
      false
   );
   private final BooleanSetting vVc2 = new BooleanSetting(
      "Замедление", false
   );
   private final SliderSetting ocLwk = new SliderSetting(
         D.k(
            new int[]{1060, 1095, 1174, 1043, 1029, 1095, 1250, 1132, 1038, 1085, 1260, 1053, 16, 1099, 1182, 1133, 1029, 1096, 1173, 1124, 1037, 1092, 1249},
            new int[]{48, 124, 174, 81}
         ),
         100.0,
         0.0,
         160.0,
         1.0
      )
      .setVisible(this.vVc2::getValue);
   private int vsi2 = -1;
   private float b2Bze;
   private final StopWatch p5svaw = new StopWatch();

   @Subscribe
   private void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null) {
         if (this.b2Bze > 0.0F) {
            this.b2Bze--;
         }

         this.f8dV0t9();
      }
   }

   @Subscribe
   private void onPopTotem(EventPopTotem e) {
      if (this.mc.player != null && e.getPlayer() == this.mc.player) {
         this.b2Bze = 5.0F;
      }
   }

   private boolean qcH05n() {
      if (!this.mc.player.isCreative() && !this.mc.player.isSpectator()) {
         boolean crystalsCheck = false;

         for (Entity entity : this.mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity
               && this.mc.player.getY() >= entity.getY()
               && this.mc.player.getEyePos().distanceTo(entity.getBoundingBox().getCenter()) <= 8.0) {
               crystalsCheck = true;
            }
         }

         return this.mc.player.getHealth() + this.mc.player.getAbsorptionAmount() <= this.r2zSFC9.getValue()
            || this.mc.player.getHealth() + this.mc.player.getAbsorptionAmount() <= this.rbrN.getValue()
               && this.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA
            || this.fY8b947.getValue() && crystalsCheck;
      } else {
         return false;
      }
   }

   private void f8dV0t9() {
      ItemStack offHand = this.mc.player.getOffHandStack();
      int totemSlot = InventoryUtil.searchItem(Items.TOTEM_OF_UNDYING);
      if (this.qcH05n() && this.p5svaw.isReached(300L) && totemSlot != -1 && offHand.getItem() != Items.TOTEM_OF_UNDYING) {
         if (this.vsi2 == -1 && !offHand.isEmpty()) {
            this.vsi2 = totemSlot;
         }

         if (totemSlot >= 0 && totemSlot <= 8) {
            this.qPD63r7(() -> this.mc.interactionManager.clickSlot(0, 45, totemSlot, SlotActionType.SWAP, this.mc.player));
         } else if (totemSlot >= 8 && totemSlot <= 45) {
            this.qPD63r7(() -> this.mc.interactionManager.clickSlot(0, totemSlot, 40, SlotActionType.SWAP, this.mc.player));
         }

         this.p5svaw.reset();
      }

      if (!this.qcH05n() && this.vsi2 != -1 && this.b2Bze == 0.0F) {
         int swapBackSlot = this.vsi2;
         if (swapBackSlot >= 0 && swapBackSlot <= 8) {
            this.qPD63r7(() -> this.mc.interactionManager.clickSlot(0, 45, swapBackSlot, SlotActionType.SWAP, this.mc.player));
         } else if (swapBackSlot >= 8 && swapBackSlot <= 45) {
            this.qPD63r7(() -> this.mc.interactionManager.clickSlot(0, swapBackSlot, 40, SlotActionType.SWAP, this.mc.player));
         }

         this.vsi2 = -1;
      }
   }

   private void qPD63r7(Runnable action) {
      if (this.vVc2.getValue()) {
         InventoryUtil.swapWithBypassPolar(action, (long)this.ocLwk.getValue());
      } else {
         InventoryUtil.swapWithBypassGrim(action);
      }
   }
}
