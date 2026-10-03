package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Well Helper",
   moduleDesc = "Помощник для Well",
   moduleCategory = ModuleCategory.MISC
)
public class WellHelper extends Module {
   private final BooleanSetting wfe0p = new BooleanSetting(
      "Invsee элитры", true
   );
   private final List<String> qYIbzhf = new ArrayList<>();
   private String gcZuWfo = null;
   private boolean ftjtK = false;
   private boolean fS84i = false;
   private int w9upr2 = 0;
   private int i3Vvi = 0;
   private static final Pattern PLAYER_PATTERN = Pattern.compile(
      D.k(
         new int[]{173, 5, 138, 1, 255, 31, 198, 118, 181, 115, 210, 115, 216, 37, 216, 0, 180, 104, 150, 5, 217, 45, 192, 112, 225, 117},
         new int[]{133, 94, 235, 44}
      )
   );

   @Subscribe
   private void onPacket(EventPacket e) {
      if (this.mc.player != null && this.mc.world != null) {
         if (e.getType() == EventPacket.Type.RECEIVE) {
            if (e.getPacket() instanceof GameMessageS2CPacket p && this.wfe0p.getValue()) {
               String text = p.content().getString();
               if (text.toLowerCase()
                  .contains("игроки рядом")) {
                  this.qYIbzhf.clear();
                  this.gcZuWfo = null;
                  this.ftjtK = false;
                  this.fS84i = true;
                  this.i3Vvi = 6;
                  return;
               }

               if (this.fS84i) {
                  Matcher matcher = PLAYER_PATTERN.matcher(text);
                  if (matcher.find()) {
                     String playerName = matcher.group(1);
                     if (!playerName.equalsIgnoreCase(this.mc.player.getName().getString()) && !this.qYIbzhf.contains(playerName)) {
                        this.qYIbzhf.add(playerName);
                     }

                     this.i3Vvi = 6;
                  }
               }
            }
         }
      }
   }

   @Subscribe
   private void onTick(EventTick e) {
      if (this.mc.player != null && this.mc.world != null && this.wfe0p.getValue()) {
         if (this.fS84i) {
            this.i3Vvi--;
            if (this.i3Vvi <= 0) {
               this.fS84i = false;
               if (!this.qYIbzhf.isEmpty()) {
                  this.w9upr2 = 10;
               }
            }
         } else if (this.w9upr2 > 0) {
            this.w9upr2--;
            if (this.w9upr2 == 0 && !this.qYIbzhf.isEmpty() && !this.ftjtK) {
               this.yWR7O();
            }
         } else {
            if (this.ftjtK && this.gcZuWfo != null && this.mc.currentScreen instanceof GenericContainerScreen screen) {
               boolean hasElytra = false;
               int containerSlots = screen.getScreenHandler().getRows() * 9;

               for (int i = 0; i < containerSlots; i++) {
                  Slot slot = screen.getScreenHandler().slots.get(i);
                  ItemStack stack = slot.getStack();
                  if (stack.getItem() == Items.ELYTRA) {
                     hasElytra = true;
                     break;
                  }
               }

               if (hasElytra) {
                  this.logDirect(
                     new Text[]{
                        Text.literal(this.gcZuWfo)
                           .formatted(Formatting.GREEN)
                           .append(
                              Text.literal(
                                    D.k(
                                       new int[]{253, 1203, 1200, 1257, 1256, 1225, 172, 1265, 1222, 1171, 1198, 1276, 1270, 170},
                                       new int[]{221, 139, 140, 220}
                                    )
                                 )
                                 .formatted(Formatting.LIGHT_PURPLE)
                           )
                     }
                  );
               }

               this.mc.player.closeHandledScreen();
               this.gcZuWfo = null;
               this.ftjtK = false;
               if (!this.qYIbzhf.isEmpty()) {
                  this.w9upr2 = 6;
               }
            }
         }
      }
   }

   private void yWR7O() {
      if (!this.qYIbzhf.isEmpty() && this.mc.getNetworkHandler() != null) {
         this.gcZuWfo = this.qYIbzhf.remove(0);
         this.ftjtK = true;
         this.mc.getNetworkHandler().sendChatCommand("invsee " + this.gcZuWfo);
      }
   }
}
