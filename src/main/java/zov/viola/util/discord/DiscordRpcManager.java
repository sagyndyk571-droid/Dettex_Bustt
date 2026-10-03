package zov.viola.util.discord;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.QuickLogger;

public class DiscordRpcManager implements IMinecraft, QuickLogger {
   private static final long APP_ID = 1538942852925628567L;
   private static final String BUILD = "1.0.2";
   private long iE2oXyw;
   private boolean dzjXdy;
   private String uzhVkHB;

   public void start() {
      this.iE2oXyw = System.currentTimeMillis();
      this.uzhVkHB = null;
      DiscordIPC.setOnError((code, message) -> this.logDirect("Discord RPC: ошибка " + code + " - " + message));
      this.dzjXdy = DiscordIPC.start(
         1538942852925628567L,
         () -> this.logDirect(
            D.k(
               new int[]{193, 147, 4, 61, 234, 136, 19, 126, 215, 170, 52, 126, 1210, 1220, 1091, 1124, 1214, 1204, 1072, 1131, 1208},
               new int[]{133, 250, 119, 94}
            )
         )
      );
      this.jciZJ();
   }

   public void stop() {
      if (this.dzjXdy) {
         DiscordIPC.stop();
         this.dzjXdy = false;
      }

      this.uzhVkHB = null;
   }

   @Subscribe
   private void onUpdate(EventPlayerUpdate event) {
      if (this.dzjXdy && DiscordIPC.isConnected()) {
         String state = this.xvyvc();
         if (!state.equals(this.uzhVkHB)) {
            this.uzhVkHB = state;
            this.jciZJ();
         }
      }
   }

   private void jciZJ() {
      RichPresence presence = new RichPresence() {
         @Override
         public JsonObject toJson() {
            JsonObject obj = super.toJson();
            JsonObject tg = new JsonObject();
            tg.addProperty("label", "Telegram");
            tg.addProperty(
               "url",
               D.k(
                  new int[]{192, 51, 183, 227, 219, 125, 236, 188, 220, 105, 174, 246, 135, 17, 170, 252, 196, 38, 128, 255, 193, 34, 173, 231},
                  new int[]{168, 71, 195, 147}
               )
            );
            JsonArray buttons = new JsonArray();
            buttons.add(tg);
            obj.add("buttons", buttons);
            return obj;
         }
      };
      presence.setDetails(
         D.k(
            new int[]{113, 250, 165, 234, 70, 190, 251, 168, 21, 162, 228, 178, 7, 239, 234, 196, 82, 250, 166, 226, 29, 179, 251, 168, 23, 189, 248},
            new int[]{39, 147, 202, 134}
         )
      );
      presence.setState(this.xvyvc());
      presence.setStart(this.iE2oXyw);
      presence.setLargeImage("viola", "Viola Client");
      DiscordIPC.setActivity(presence);
   }

   private String xvyvc() {
      if (mc.getCurrentServerEntry() != null) {
         return mc.getCurrentServerEntry().address;
      } else {
         return mc.getServer() != null
            ? "Одиночная игра"
            : "В меню";
      }
   }
}
