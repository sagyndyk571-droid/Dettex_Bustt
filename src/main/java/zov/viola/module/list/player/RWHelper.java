package zov.viola.module.list.player;

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
   moduleName = "RW Helper",
   moduleDesc = "Помощник для ReallyWorld сервера",
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
                     1128,
                     1092,
                     1178,
                     1204,
                     88,
                     1126,
                     1254,
                     1207,
                     1101,
                     1083,
                     248,
                     55,
                     88,
                     1131,
                     1171,
                     172,
                     1093,
                     1100,
                     248,
                     1200,
                     1094,
                     1103,
                     1261,
                     1230,
                     1101,
                     89,
                     1258,
                     1211,
                     1091,
                     1100,
                     1178,
                     1209,
                     1082,
                     1077,
                     249
                  },
                  new int[]{120, 121, 216, 140}
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
