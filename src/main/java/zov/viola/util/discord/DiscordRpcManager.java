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
   private static final long APP_ID = 1555932169057210398L;
   private static final String BUILD = "1.0.2";
   private long iE2oXyw;
   private boolean dzjXdy;
   private String uzhVkHB;

   public void start() {
      this.iE2oXyw = System.currentTimeMillis();
      this.uzhVkHB = null;
      DiscordIPC.setOnError((code, message) -> this.logDirect("Discord RPC: ошибка " + code + " - " + message));
       this.dzjXdy = DiscordIPC.start(
          1555932169057210398L,
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
             obj.addProperty("name", "DettexClient");
            JsonObject tg = new JsonObject();
            tg.addProperty("label", "Telegram");
             tg.addProperty(
                "url",
                "https://t.me/Dettex_clientt"
             );
            JsonArray buttons = new JsonArray();
            buttons.add(tg);
            obj.add("buttons", buttons);
            return obj;
         }
      };
       presence.setDetails("DettexClient 1.21.11");
       presence.setState(this.xvyvc());
       presence.setStart(this.iE2oXyw);
       presence.setLargeImage("dettex", "DettexClient");
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
