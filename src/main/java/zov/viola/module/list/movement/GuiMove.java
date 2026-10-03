package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import zov.viola.event.list.EventCloseInv;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.event.list.MoveInputEvent;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;
import zov.viola.util.packet.NetworkUtils;
import zov.viola.util.player.move.MoveUtil;

@ModuleInformation(
   moduleName = "Gui Move",
   moduleDesc = "Взаимодействие с инвентарём при движении",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class GuiMove extends Module {
   private final ModeSetting pOll = new ModeSetting(
      "Обход",
      "Легит",
      "Vanilla",
      "Reallyworld",
      "Funtime",
      "Holyworld",
      "SPtest",
      "LonyGrief",
      "Легит"
   );
   private final List<Packet<?>> yu8lgp = new ArrayList<>();
   private int rw19UvA;
   private boolean txRZ;
   private boolean zU77pXL;

   @Override
   public void onDisable() {
      super.onDisable();
      this.g1rt();
      this.yu8lgp.clear();
      this.rw19UvA = 0;
      this.txRZ = false;
      this.zU77pXL = false;
   }

   private boolean d4n4Oos() {
      return this.pOll.is("Vanilla");
   }

   private boolean cf7sy9b() {
      return this.pOll.is("Легит");
   }

   private boolean c7H1hZ() {
      return !this.d4n4Oos() && !this.cf7sy9b();
   }

   private int z6ve() {
      String var1 = this.pOll.getValue();

      return switch (var1) {
         case "Reallyworld" -> 3;
         case "Funtime" -> 4;
         case "Holyworld" -> 5;
         case "SPtest" -> 4;
         case "LonyGrief" -> 2;
         default -> 0;
      };
   }

   @Subscribe
   private void onPacket(EventPacket e) {
      if (this.mc.player != null && !this.d4n4Oos()) {
         if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof ChatScreen)) {
            if (e.getType() == EventPacket.Type.RECEIVE && e.getPacket() instanceof CloseScreenS2CPacket && this.c7H1hZ() && !this.yu8lgp.isEmpty()) {
               e.cancelEvent();
            } else if (e.getType() == EventPacket.Type.SEND) {
               if (e.getPacket() instanceof ClickSlotC2SPacket click) {
                  if (this.mc.currentScreen instanceof InventoryScreen) {
                     if (MoveUtil.hasPlayerMovement()) {
                        if (this.cf7sy9b()) {
                           this.yu8lgp.add(click);
                           e.cancelEvent();
                        } else {
                           this.yu8lgp.add(click);
                           e.cancelEvent();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Subscribe
   private void onCloseInv(EventCloseInv e) {
      if (this.mc.player != null && !this.d4n4Oos() && !this.cf7sy9b()) {
         if (!this.yu8lgp.isEmpty()) {
            e.cancelEvent();
            this.txRZ = true;
            this.rw19UvA = this.c7H1hZ() ? this.z6ve() : 0;
         }
      }
   }

   @Subscribe
   private void onMoveInput(MoveInputEvent e) {
      if (this.rw19UvA > 0 || this.zU77pXL) {
         e.cancel();
      }
   }

   @Subscribe
   private void onUpdate(EventPlayerUpdate ignored) {
      if (this.mc.player != null && !this.d4n4Oos()) {
         if (this.cf7sy9b() && !this.yu8lgp.isEmpty() && !MoveUtil.hasPlayerMovement() && !(this.mc.currentScreen instanceof InventoryScreen)) {
            this.g1rt();
         }

         if (this.rw19UvA > 0) {
            this.rw19UvA--;
            if (this.rw19UvA == 0 && this.txRZ) {
               if (this.pOll.is("SPtest")) {
                  this.zU77pXL = true;
               } else {
                  this.g1rt();
                  NetworkUtils.sendSilentPacket(new CloseHandledScreenC2SPacket(this.mc.player.currentScreenHandler.syncId));
               }

               this.txRZ = false;
            }
         }

         if (this.zU77pXL) {
            if (!this.yu8lgp.isEmpty()) {
               NetworkUtils.sendSilentPacket(this.yu8lgp.remove(0));
            }

            if (this.yu8lgp.isEmpty()) {
               NetworkUtils.sendSilentPacket(new CloseHandledScreenC2SPacket(this.mc.player.currentScreenHandler.syncId));
               this.zU77pXL = false;
            }
         }

         if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof ChatScreen)) {
            for (KeyBinding key : new KeyBinding[]{
               this.mc.options.forwardKey, this.mc.options.backKey, this.mc.options.leftKey, this.mc.options.rightKey, this.mc.options.jumpKey
            }) {
               key.setPressed(InputUtil.isKeyPressed(this.mc.getWindow().getHandle(), InputUtil.fromTranslationKey(key.getBoundKeyTranslationKey()).getCode()));
            }
         }
      }
   }

   private void g1rt() {
      if (!this.yu8lgp.isEmpty()) {
         this.yu8lgp.forEach(NetworkUtils::sendSilentPacket);
         this.yu8lgp.clear();
      }
   }
}
