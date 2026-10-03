package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.screen.slot.SlotActionType;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;
import zov.viola.util.math.StopWatch;
import zov.viola.util.player.other.InventoryUtil;

@ModuleInformation(
   moduleName = "Auto Armor",
   moduleDesc = "Автоматически экипирует броню",
   moduleCategory = ModuleCategory.COMBAT
)
public class AutoArmor extends Module {
   private final ModeSetting ha3Gr6D = new ModeSetting("Мод", "Vanilla", "Vanilla", "Grim");
   private final StopWatch mg6Cfj = new StopWatch();
   private final StopWatch o7x0u = new StopWatch();
   private final StopWatch sRZooY = new StopWatch();
   private final StopWatch v2Fi = new StopWatch();

   @Subscribe
   private void onUpdate(EventPlayerUpdate ignored) {
      if (this.mc.player != null) {
         if (this.mc.currentScreen == null || this.mc.currentScreen instanceof InventoryScreen) {
            if (this.mc.player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
               this.xayQ54H(EquipmentSlot.HEAD);
            }

            if (this.mc.player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
               this.xayQ54H(EquipmentSlot.CHEST);
            }

            if (this.mc.player.getEquippedStack(EquipmentSlot.LEGS).isEmpty()) {
               this.xayQ54H(EquipmentSlot.LEGS);
            }

            if (this.mc.player.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
               this.xayQ54H(EquipmentSlot.FEET);
            }
         }
      }
   }

   private void xayQ54H(EquipmentSlot equipmentSlot) {
      if ((equipmentSlot != EquipmentSlot.HEAD || this.mg6Cfj.isReached(50L))
         && (equipmentSlot != EquipmentSlot.CHEST || this.o7x0u.isReached(50L))
         && (equipmentSlot != EquipmentSlot.LEGS || this.sRZooY.isReached(50L))
         && (equipmentSlot != EquipmentSlot.FEET || this.v2Fi.isReached(50L))) {
         int slot = InventoryUtil.getBestArmorSlot(equipmentSlot);
         if (slot != -1) {
            if (slot < 9) {
               slot += 36;
            }

            int finalSlot = slot;
            String var4 = this.ha3Gr6D.getValue();
            switch (var4) {
               case "Vanilla":
                  this.mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.QUICK_MOVE, this.mc.player);
                  break;
               case "Grim":
                  InventoryUtil.swapWithBypassGrim(() -> this.mc.interactionManager.clickSlot(0, finalSlot, 0, SlotActionType.QUICK_MOVE, this.mc.player));
            }

            if (equipmentSlot == EquipmentSlot.HEAD) {
               this.mg6Cfj.reset();
            }

            if (equipmentSlot == EquipmentSlot.CHEST) {
               this.o7x0u.reset();
            }

            if (equipmentSlot == EquipmentSlot.LEGS) {
               this.sRZooY.reset();
            }

            if (equipmentSlot == EquipmentSlot.FEET) {
               this.v2Fi.reset();
            }
         }
      }
   }
}
