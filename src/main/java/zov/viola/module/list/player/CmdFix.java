package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import zov.viola.event.list.ChatEvent;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Cmd Fix",
   moduleDesc = "Исправляет команды, набранные в русской раскладке",
   moduleCategory = ModuleCategory.PLAYER
)
public class CmdFix extends Module {
   @Subscribe
   private void onChat(ChatEvent e) {
      if (this.mc.player != null && this.mc.getNetworkHandler() != null) {
         String message = e.getMessage();
         if (message != null && !message.isEmpty()) {
            String full = message.startsWith("/") ? message : "/" + message;
            String lower = full.toLowerCase();
            String replacement = null;
            if (lower.startsWith("/рщьу")) {
               if (lower.equals("/рщьу")
                  || lower.startsWith("/рщьу ")) {
                  replacement = "/home" + full.substring(5);
               }
            } else if (lower.startsWith("/ызфцт")) {
               if (lower.equals("/ызфцт")
                  || lower.startsWith("/ызфцт ")) {
                  replacement = "/spawn" + full.substring(6);
               }
            } else if (lower.startsWith("/фп")) {
               if (lower.equals("/фп")
                  || lower.startsWith("/фп ")) {
                  replacement = "/ah" + full.substring(3);
               }
            } else if (lower.startsWith("/кез")) {
               if (lower.equals("/кез")
                  || lower.startsWith("/кез ")) {
                  replacement = "/rtp" + full.substring(4);
               }
            } else if (lower.equals("/сдфт")) {
               replacement = "/clan";
            }

            if (replacement != null) {
               e.setCancelled(true);
               if (replacement.startsWith("/")) {
                  this.mc.getNetworkHandler().sendChatCommand(replacement.substring(1));
               } else {
                  this.mc.getNetworkHandler().sendChatMessage(replacement);
               }
            }
         }
      }
   }
}
