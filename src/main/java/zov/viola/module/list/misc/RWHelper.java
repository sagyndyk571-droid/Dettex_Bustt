package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.obf.D;
import zov.viola.util.packet.NetworkUtils;
import zov.viola.util.player.other.InventoryUtil;

@ModuleInformation(
   moduleName = "RWHelper",
   moduleDesc = "Помощник для ReallyWorld",
   moduleCategory = ModuleCategory.MISC
)
public class RWHelper extends Module {
   public final BooleanSetting antipolet = new BooleanSetting(
      "Анти-полет обход", false
   );
   boolean need;
   public boolean fireworkUse;

   @Subscribe
   private void onPacket(EventPacket e) {
      if (e.getPacket() instanceof GameMessageS2CPacket p
         && p.content()
            .getString()
            .contains(
               D.k(
                  new int[]{
                     1192,
                     1116,
                     1242,
                     1207,
                     152,
                     1150,
                     1190,
                     1204,
                     1165,
                     1059,
                     184,
                     52,
                     152,
                     1139,
                     1235,
                     175,
                     1157,
                     1108,
                     184,
                     1203,
                     1158,
                     1111,
                     1197,
                     1229,
                     1165,
                     65,
                     1194,
                     1208,
                     1155,
                     1108,
                     1242,
                     1210,
                     1274,
                     1069,
                     185
                  },
                  new int[]{184, 97, 152, 143}
               )
            )) {
         this.need = true;
      }
   }

   @Subscribe
   private void onPlayerUpdate(EventPlayerUpdate e) {
      if (this.antipolet.getValue()) {
         if (this.need) {
            if (!this.mc.player.isOnGround() && this.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
               NetworkUtils.sendPacket(new ClientCommandC2SPacket(this.mc.player, Mode.START_FALL_FLYING));
               this.mc.player.startGliding();
               if (this.fireworkUse) {
                  InventoryUtil.swapAndUseHvH(Items.FIREWORK_ROCKET);
                  this.fireworkUse = false;
               }
            } else {
               this.need = false;
            }
         }
      }
   }
}
