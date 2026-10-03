package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import zov.viola.Viola;
import zov.viola.event.list.EventKeyInput;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.movement.Sprint;
import zov.viola.module.settings.BindSetting;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;
import zov.viola.util.packet.NetworkUtils;

@ModuleInformation(
   moduleName = "Sunrise Helper",
   moduleDesc = "Помощник для Sunrise",
   moduleCategory = ModuleCategory.MISC
)
public class SunriseHelper extends Module {
   private final BindSetting qw8SoL = new BindSetting("Трапка", -1);
   private final BindSetting v27l9C = new BindSetting(
      "Волна огня", -1
   );
   private boolean pqTn;
   private boolean jPw6Npx;

   @Subscribe
   public void onKey(EventKeyInput e) {
      if (this.mc.currentScreen == null && e.getAction() != 0 && e.getAction() != 2) {
         if (e.getKey() == this.qw8SoL.getValue()) {
            this.pqTn = true;
         }

         if (e.getKey() == this.v27l9C.getValue()) {
            this.jPw6Npx = true;
         }
      }
   }

   @Subscribe
   public void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.pqTn) {
            this.kVJau("трапка");
            this.pqTn = false;
         }

         if (this.jPw6Npx) {
            this.kVJau("волна огня");
            this.jPw6Npx = false;
         }
      }
   }

   private void kVJau(String itemName) {
      int slotHotbar = this.xf66O4(itemName);
      int slotInv = this.yp9yCJQ(itemName);
      int previousSlot = this.mc.player.getInventory().selectedSlot;
      String mainHandName = this.mc.player.getMainHandStack().getName().getString().toLowerCase();
      if (mainHandName.contains(itemName)) {
         this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
      } else {
         String offHandName = this.mc.player.getOffHandStack().getName().getString().toLowerCase();
         if (offHandName.contains(itemName)) {
            this.mc.interactionManager.interactItem(this.mc.player, Hand.OFF_HAND);
         } else if (slotHotbar != -1) {
            this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slotHotbar));
            this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
            this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
         } else {
            if (slotInv != -1) {
               int slotCorrectable = -1;

               for (int slotNone = 0; slotNone < 8; slotNone++) {
                  ItemStack stack = this.mc.player.getInventory().getStack(slotNone);
                  if (stack.isEmpty()) {
                     slotCorrectable = slotNone;
                  }

                  if (stack.getUseAction() == UseAction.NONE) {
                     slotCorrectable = slotNone;
                  }
               }

               this.mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
               if (Viola.getInstance().getServerManager().isServerSprinting()) {
                  this.mc.player.setSprinting(false);
                  this.mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(this.mc.player, Mode.STOP_SPRINTING));
                  if (!Instance.get(Sprint.class).isEnabled()) {
                     this.mc.options.sprintKey.setPressed(false);
                  }
               }

               if (slotCorrectable == -1) {
                  this.mc.interactionManager.clickSlot(0, slotInv, 8, SlotActionType.SWAP, this.mc.player);
                  NetworkUtils.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
                  this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(8));
                  this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
                  this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
               } else {
                  this.mc.interactionManager.clickSlot(0, slotInv, slotCorrectable, SlotActionType.SWAP, this.mc.player);
                  NetworkUtils.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
                  this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slotCorrectable));
                  this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
                  this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
                  this.mc.interactionManager.clickSlot(0, slotInv, slotCorrectable, SlotActionType.SWAP, this.mc.player);
                  NetworkUtils.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
               }

               this.mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(this.mc.player.input.playerInput));
            }
         }
      }
   }

   private int xf66O4(String itemName) {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = this.mc.player.getInventory().getStack(i);
         if (!stack.isEmpty()) {
            String name = stack.getName().getString().toLowerCase();
            if (name.contains(itemName)) {
               return i;
            }
         }
      }

      return -1;
   }

   private int yp9yCJQ(String itemName) {
      for (int i = 9; i < 36; i++) {
         ItemStack stack = this.mc.player.getInventory().getStack(i);
         if (!stack.isEmpty()) {
            String name = stack.getName().getString().toLowerCase();
            if (name.contains(itemName)) {
               return i;
            }
         }
      }

      return -1;
   }
}
