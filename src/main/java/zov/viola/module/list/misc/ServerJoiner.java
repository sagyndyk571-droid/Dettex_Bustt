package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.player.other.InventoryUtil;

@ModuleInformation(
   moduleName = "ServerJoiner",
   moduleDesc = "Автовход на сервер через компас/GUI",
   moduleCategory = ModuleCategory.MISC
)
public class ServerJoiner extends Module {
   private final ModeSetting t4l5Ys = new ModeSetting(
      "Сервер",
      "РиллиВорлд",
      "РиллиВорлд",
      "СпукиТайм дуэли"
   );
   private final SliderSetting b62fQ = new SliderSetting(
         "Номер грифа", 1.0, 1.0, 54.0, 1.0
      )
      .setVisible(() -> this.t4l5Ys.is("РиллиВорлд"));
   private final ModeSetting s216b = new ModeSetting(
         "Название компаса",
         "выберите режим",
         "выберите режим",
         "дуэли",
         "мясорубка",
         "квадромясо"
      )
      .setVisible(
         () -> this.t4l5Ys
            .is("СпукиТайм дуэли")
      );
   private long iewk;

   @Subscribe
   private void onTick(EventTick ignored) {
      if (this.isEnabled()) {
         if (this.mc.getWindow() != null && GLFW.glfwGetKey(this.mc.getWindow().getHandle(), 260) == 1) {
            this.setEnabled(false);
         } else if (this.mc.player != null && this.mc.world != null) {
            if (this.mc.currentScreen == null) {
               if (this.t4l5Ys
                  .is("СпукиТайм дуэли")) {
                  if (System.currentTimeMillis() - this.iewk > 250L) {
                     this.y4846();
                  }
               } else if (this.mc.player.age < 5) {
                  this.y4846();
               }
            } else {
               if (this.mc.currentScreen instanceof GenericContainerScreen container) {
                  this.gtfpGq3(container);
               }
            }
         }
      }
   }

   private void gtfpGq3(GenericContainerScreen container) {
      GenericContainerScreenHandler handler = container.getScreenHandler();
      String title = container.getTitle().getString().toLowerCase();

      for (int i = 0; i < handler.slots.size(); i++) {
         if (handler.slots.get(i).hasStack()) {
            String slotName = handler.slots.get(i).getStack().getName().getString().toLowerCase();
            if (this.t4l5Ys.is("РиллиВорлд")) {
               if (slotName.contains(
                     D.k(
                        new int[]{1148, 1061, 1216, 1185, 1146, 1061, 1209, 1247, 1137, 1104, 216, 1239, 1028, 1107, 1216, 1239, 1151, 1112, 1216, 1232},
                        new int[]{79, 101, 248, 229}
                     )
                  )
                  && this.o0fe46()) {
                  this.hy5J8(handler.syncId, i, SlotActionType.PICKUP);
               }

               int number = this.b62fQ.getIntValue();
               if (slotName.contains("гриф #" + number) && this.o0fe46()) {
                  this.hy5J8(handler.syncId, i, SlotActionType.PICKUP);
               }
            } else if (this.t4l5Ys
                  .is("СпукиТайм дуэли")
               && title.contains("выберите режим")
               && this.o0fe46()) {
               this.hy5J8(handler.syncId, 14, SlotActionType.QUICK_MOVE);
            }
         }
      }
   }

   private void hy5J8(int syncId, int slot, SlotActionType action) {
      if (this.mc.interactionManager != null && this.mc.player != null) {
         this.mc.interactionManager.clickSlot(syncId, slot, 0, action, this.mc.player);
         this.iewk = System.currentTimeMillis();
      }
   }

   private void y4846() {
      if (this.mc.player != null && this.mc.world != null && this.mc.getNetworkHandler() != null && this.mc.interactionManager != null) {
         int slot = InventoryUtil.searchItemHotbar(Items.COMPASS);
         if (slot != -1) {
            this.mc.player.getInventory().selectedSlot = slot;
            this.mc.interactionManager.syncSelectedSlot();
            this.mc.interactionManager.interactItem(this.mc.player, Hand.MAIN_HAND);
            this.iewk = System.currentTimeMillis();
         }
      }
   }

   @Subscribe
   private void onPacket(EventPacket e) {
      if (this.isEnabled() && e.getType() == EventPacket.Type.RECEIVE) {
         if (e.getPacket() instanceof DisconnectS2CPacket packet) {
            String message = packet.reason().getString().toLowerCase();
            boolean retry = message.contains(
                  D.k(
                     new int[]{1060, 1044, 1224, 1043, 1104, 1121, 168, 1054, 1104, 1121, 1213, 1054, 1115, 1050, 1205, 1044, 1112},
                     new int[]{101, 33, 136, 33}
                  )
               )
               || message.contains("подождите")
               || message.contains(
                  "вы уже подключены"
               )
               || message.contains(
                  "вы были кикнуты"
               )
               || message.contains(
                  D.k(
                     new int[]{1065, 1182, 1045, 1128, 1104, 1182, 1047, 4, 1063, 1182, 1132, 1050, 1058, 128, 1046, 1047, 1112, 1182, 1044, 1050, 1066},
                     new int[]{24, 160, 46, 36}
                  )
               )
               || message.contains(
                  "сервер заполнен"
               );
            if (retry) {
               this.y4846();
            }
         }
      }
   }

   private boolean o0fe46() {
      return System.currentTimeMillis() - this.iewk > 50L;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.iewk = 0L;
   }
}
