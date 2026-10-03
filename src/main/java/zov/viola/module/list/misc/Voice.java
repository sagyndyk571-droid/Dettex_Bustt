package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import zov.viola.event.list.EventHUD;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@ModuleInformation(
   moduleName = "Voice",
   moduleDesc = "Показывает кто говорит в Simple Voice Chat",
   moduleCategory = ModuleCategory.MISC
)
public class Voice extends Module {
   public final BooleanSetting whisperSeparate = new BooleanSetting(
      "Шёпот отдельно", true
   );
   public final BooleanSetting levelBar = new BooleanSetting(
      "Полоса звука", true
   );
   public final SliderSetting maxRows = new SliderSetting(
      "Макс. строк", 8.0, 3.0, 16.0, 1.0
   );
   public final SliderSetting offsetY = new SliderSetting(
      "Отступ сверху", 60.0, 10.0, 300.0, 5.0
   );
   private static final int COLOR_TALKING = ColorProvider.rgba(130, 220, 130, 255.0F);
   private static final int COLOR_WHISPER = ColorProvider.rgba(120, 200, 255, 255.0F);
   private static boolean b4wn7;
   private static boolean bXhtjLx;
   private static Method egac;
   private static Method lMrftzt;
   private static Method cKmxiI;
   private static Method wmee;
   private static Method zl77;

   @Subscribe
   public void onEventHUD(EventHUD e) {
      if (this.mc.player != null && this.mc.world != null && !this.mc.options.hudHidden && !this.mc.getDebugHud().shouldShowDebugHud()) {
         if (rjSpa1U()) {
            Object talkCache = this.z7l7i();
            List<Voice.Row> rows = this.lS7Sv2x(talkCache);
            if (!rows.isEmpty()) {
               float size = 9.0F;
               float rowH = 16.0F;
               float pad = 8.0F;
               float dotGap = 12.0F;
               float barW = this.levelBar.getValue() ? 36.0F : 0.0F;
               String header = "Голосовой чат";
               float w = Fonts.SFSEMIBOLD.get().getWidth(header, 9.5F);

               for (Voice.Row row : rows) {
                  w = Math.max(w, Fonts.SFMEDIUM.get().getWidth(row.name(), size));
               }

               w += pad * 2.0F + dotGap + (barW > 0.0F ? barW + 8.0F : 0.0F);
               float h = 20.0F + rows.size() * rowH + pad * 0.5F;
               float screenW = this.mc.getWindow().getScaledWidth();
               float x = screenW - w - 6.0F;
               float y = this.offsetY.getFloatValue();
               DrawUtil.drawRound(x - 0.8F, y - 0.8F, w + 1.6F, h + 1.6F, 6.0F, ColorProvider.rgba(46, 46, 52, 220.0F));
               DrawUtil.drawRound(x, y, w, h, 5.0F, ColorProvider.rgba(17, 17, 21, 215.0F));
               DrawUtil.drawText(Fonts.SFSEMIBOLD.get(), header, x + pad, y + 6.0F, ColorProvider.rgba(210, 214, 222, 235.0F), 9.5F);
               float rowY = y + 22.0F;

               for (Voice.Row row : rows) {
                  int dotColor = row.whispering() ? COLOR_WHISPER : COLOR_TALKING;
                  DrawUtil.drawCircle(x + pad + 3.0F, rowY + rowH * 0.5F, 2.6F, dotColor);
                  DrawUtil.drawText(
                     Fonts.SFMEDIUM.get(),
                     row.name(),
                     x + pad + dotGap,
                     rowY + rowH * 0.5F - 4.5F,
                     row.whispering() ? ColorProvider.rgba(200, 228, 250, 240.0F) : ColorProvider.rgba(232, 234, 240, 245.0F),
                     size
                  );
                  if (barW > 0.0F) {
                     float barX = x + w - pad - barW;
                     float barY = rowY + rowH * 0.5F - 2.5F;
                     DrawUtil.drawRound(barX, barY, barW, 5.0F, 2.5F, ColorProvider.rgba(255, 255, 255, 28.0F));
                     float fill = (float)Math.min(1.0, Math.max(0.0, row.level())) * barW;
                     if (fill > 0.5F) {
                        DrawUtil.drawRound(barX, barY, fill, 5.0F, 2.5F, tfQTqg(dotColor, 230));
                     }
                  }

                  rowY += rowH;
               }
            }
         }
      }
   }

   private List<Voice.Row> lS7Sv2x(Object talkCache) {
      List<Voice.Row> rows = new ArrayList<>();
      if (talkCache != null) {
         for (AbstractClientPlayerEntity player : this.mc.world.getPlayers()) {
            UUID uuid = player.getUuid();
            Boolean talking = n8rjSwz(cKmxiI, talkCache, uuid);
            Boolean whispering = this.whisperSeparate.getValue() ? n8rjSwz(wmee, talkCache, uuid) : false;
            if (talking != null && (talking || whispering != null && whispering)) {
               double level = siams(zl77, talkCache, uuid);
               boolean whisper = !talking && whispering;
               rows.add(new Voice.Row(this.fmeR6p2(player), whisper, level));
            }
         }
      }

      rows.sort(Comparator.comparing(r -> r.name().toLowerCase()));
      int limit = this.maxRows.getIntValue();
      if (rows.size() > limit) {
         rows = rows.subList(0, limit);
      }

      return rows;
   }

   private Object z7l7i() {
      try {
         Object client = egac.invoke(null);
         return client == null ? null : lMrftzt.invoke(client);
      } catch (Throwable var2) {
         return null;
      }
   }

   private String fmeR6p2(AbstractClientPlayerEntity player) {
      if (this.mc.getNetworkHandler() != null) {
         PlayerListEntry entry = this.mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
         if (entry != null && entry.getProfile() != null && entry.getProfile().getName() != null) {
            return entry.getProfile().getName();
         }
      }

      return player.getName().getString();
   }

   private static Boolean n8rjSwz(Method method, Object cache, UUID uuid) {
      try {
         return (Boolean)method.invoke(cache, uuid);
      } catch (Throwable var4) {
         return null;
      }
   }

   private static double siams(Method method, Object cache, UUID uuid) {
      try {
         return (Double)method.invoke(cache, uuid);
      } catch (Throwable var4) {
         return 0.0;
      }
   }

   private static synchronized boolean rjSpa1U() {
      if (b4wn7) {
         return bXhtjLx;
      } else {
         b4wn7 = true;

         try {
            Class<?> manager = Class.forName("de.maxhenkel.voicechat.voice.client.ClientManager");
            Class<?> voicechat = Class.forName("de.maxhenkel.voicechat.voice.client.ClientVoicechat");
            Class<?> talkCache = Class.forName("de.maxhenkel.voicechat.voice.client.TalkCache");
            egac = manager.getMethod("getClient");
            lMrftzt = voicechat.getMethod("getTalkCache");
            cKmxiI = talkCache.getMethod("isTalking", UUID.class);
            wmee = talkCache.getMethod("isWhispering", UUID.class);
            zl77 = talkCache.getMethod("getPlayerAudioLevel", UUID.class);
            bXhtjLx = true;
         } catch (Throwable var3) {
            bXhtjLx = false;
         }

         return bXhtjLx;
      }
   }

   private static int tfQTqg(int argb, int alpha) {
      return argb & 16777215 | alpha << 24;
   }

   private record Row(String name, boolean whispering, double level) {
   }
}
