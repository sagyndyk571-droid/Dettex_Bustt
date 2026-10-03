package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import zov.viola.event.list.EventPacket;
import zov.viola.mixin.SlotAccessor;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.obf.D;
import zov.viola.util.chat.ChatUtil;

@ModuleInformation(
   moduleName = "LockSlot",
   moduleDesc = "Блокировка выброса из слотов",
   moduleCategory = ModuleCategory.PLAYER
)
public class LockSlot extends Module {
   private final ModeListSetting m9lvq3 = new ModeListSetting(
      "Слоты",
      new BooleanSetting("1", false),
      new BooleanSetting("2", false),
      new BooleanSetting("3", false),
      new BooleanSetting("4", false),
      new BooleanSetting("5", false),
      new BooleanSetting("6", false),
      new BooleanSetting("7", false),
      new BooleanSetting("8", false),
      new BooleanSetting("9", false)
   );

   @Subscribe
   private void onPacket(EventPacket e2) {
      if (this.mc.player != null && e2.getType() == EventPacket.Type.SEND) {
         if (!(this.mc.currentScreen instanceof HandledScreen)) {
            if (e2.getPacket() instanceof PlayerActionC2SPacket packet3) {
               if (packet3.getAction() == Action.DROP_ITEM || packet3.getAction() == Action.DROP_ALL_ITEMS) {
                  if (this.isCurrentSlotLockedForDrop()) {
                     e2.cancelEvent();
                     this.cFpZ(this.mc.player.getInventory().selectedSlot);
                  }
               }
            } else {
               Packet<?> var6 = e2.getPacket();
               int hotbarSlot;
               ClickSlotC2SPacket packet;
               if (var6 instanceof ClickSlotC2SPacket
                  && (packet = (ClickSlotC2SPacket)var6).getActionType() == SlotActionType.THROW
                  && (hotbarSlot = this.jcrjI1(packet.getSlot())) >= 0
                  && this.vofqSZM(hotbarSlot)) {
                  e2.cancelEvent();
                  this.cFpZ(hotbarSlot);
               }
            }
         }
      }
   }

   public boolean isCurrentSlotLockedForDrop() {
      if (!this.isEnabled() || this.mc.player == null || this.mc.player.getMainHandStack().isEmpty()) {
         return false;
      } else {
         return this.mc.currentScreen instanceof HandledScreen ? false : this.vofqSZM(this.mc.player.getInventory().selectedSlot);
      }
   }

   private boolean vofqSZM(int slot) {
      return slot >= 0 && slot < this.m9lvq3.getSettings().size() ? this.m9lvq3.getSettings().get(slot).getValue() : false;
   }

   private int jcrjI1(int slotId) {
      if (this.mc.player != null && slotId >= 0 && slotId < this.mc.player.currentScreenHandler.slots.size()) {
         Slot slot = this.mc.player.currentScreenHandler.getSlot(slotId);
         SlotAccessor accessor = (SlotAccessor)slot;
         int inventoryIndex = accessor.getIndex();
         return accessor.getInventory() == this.mc.player.getInventory() && inventoryIndex >= 0 && inventoryIndex <= 9 ? inventoryIndex : -1;
      } else {
         return -1;
      }
   }

   private void cFpZ(int slot) {
      ChatUtil.send("Выброс предмета из слота " + (slot + 1) + " заблокирован");
   }
}
