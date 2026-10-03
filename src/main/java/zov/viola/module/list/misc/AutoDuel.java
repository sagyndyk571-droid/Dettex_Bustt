package zov.viola.module.list.misc;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.obf.D;
import zov.viola.util.math.StopWatch;

@ModuleInformation(
   moduleName = "Auto Duel",
   moduleDesc = "Автоматически рассылает /duel и выбирает кит",
   moduleCategory = ModuleCategory.MISC
)
public class AutoDuel extends Module {
   private final ModeListSetting h06WX = new ModeListSetting(
      "Разрешённые киты",
      new BooleanSetting("Щиты", false),
      new BooleanSetting("Шипы 3", false),
      new BooleanSetting("Лук", false),
      new BooleanSetting("Тотемы", false),
      new BooleanSetting("НоДебафф", false),
      new BooleanSetting("Шары", true),
      new BooleanSetting("Классик", false),
      new BooleanSetting("Читерский рай", false),
      new BooleanSetting(
         "Без эндер-жемчуга",
         false
      )
   );
   private static final Pattern NAME_PATTERN = Pattern.compile("^\\w{3,16}$");
   private final List<String> u082 = new ArrayList<>();
   private final StopWatch n1ba = new StopWatch();
   private final StopWatch i88Rhs = new StopWatch();
   private final StopWatch w48m9 = new StopWatch();
   private final StopWatch sC0k = new StopWatch();

   @Override
   public void onEnable() {
      super.onEnable();
      this.u082.clear();
   }

   @Subscribe
   public void onTick(EventTick e) {
      if (this.mc.player != null && this.mc.getNetworkHandler() != null) {
         List<String> players = this.ndlp3u();
         if (this.i88Rhs.isReached(800L * Math.max(players.size(), 1))) {
            this.u082.clear();
            this.i88Rhs.reset();
         }

         for (String player : players) {
            if (!this.u082.contains(player) && !player.equals(this.mc.player.getGameProfile().getName())) {
               if (this.n1ba.isReached(1000L)) {
                  this.mc.getNetworkHandler().sendChatCommand("duel " + player);
                  this.u082.add(player);
                  this.n1ba.reset();
               }
               break;
            }
         }

         if (this.mc.currentScreen instanceof GenericContainerScreen screen) {
            String title = screen.getTitle().getString();
            if (title.contains("Выбор набора")
               || title.contains("Kit selection")) {
               this.jqdx();
            } else if (title.contains(
                  D.k(
                     new int[]{1027, 1265, 1141, 1122, 1118, 1279, 1037, 1050, 1070, 225, 1035, 1054, 1067, 1269, 1036, 1053, 1060, 1265},
                     new int[]{30, 193, 52, 32}
                  )
               )
               || title.contains("Duel setup")) {
               this.l763H();
            }
         }
      }
   }

   @Subscribe
   public void onPacket(EventPacket e) {
      if (e.getPacket() instanceof GameMessageS2CPacket packet) {
         String text = packet.content().getString().toLowerCase();
         if ((
               text.contains("начало")
                     && text.contains("через")
                     && text.contains("секунд")
                  || text.contains(
                     D.k(
                        new int[]{
                           1098,
                           1025,
                           202,
                           1234,
                           1080,
                           1034,
                           1238,
                           1199,
                           88,
                           1024,
                           1236,
                           1237,
                           1100,
                           1031,
                           1239,
                           1242,
                           1096,
                           31,
                           1245,
                           1232,
                           1095,
                           1151,
                           1247,
                           1193,
                           1101,
                           1026,
                           1236,
                           192,
                           1088,
                           1150,
                           1237,
                           1246,
                           1091,
                           1139,
                           1245,
                           1246,
                           1098,
                           1039,
                           1192,
                           1196,
                           88,
                           1029,
                           1236,
                           1244,
                           1096,
                           1026,
                           1246,
                           1195
                        },
                        new int[]{120, 63, 234, 224}
                     )
                  )
                  || text.contains("duel") && text.contains("during") && text.contains("forbidden")
            )
            && this.isEnabled()) {
            this.setEnabled(false);
         }
      }
   }

   private List<String> ndlp3u() {
      return this.mc
         .getNetworkHandler()
         .getPlayerList()
         .stream()
         .map(entry -> entry.getProfile().getName())
         .filter(name -> NAME_PATTERN.matcher(name).matches())
         .collect(Collectors.toList());
   }

   private void jqdx() {
      List<Integer> slots = new ArrayList<>();
      List<BooleanSetting> settings = this.h06WX.getSettings();

      for (int i = 0; i < settings.size(); i++) {
         if (settings.get(i).getValue()) {
            slots.add(i);
         }
      }

      if (!slots.isEmpty()) {
         Collections.shuffle(slots);
         int slotId = slots.get(0);
         if (this.w48m9.isReached(90L)) {
            this.boFdrN7(slotId);
            this.w48m9.reset();
         }
      }
   }

   private void l763H() {
      if (this.sC0k.isReached(90L)) {
         this.boFdrN7(0);
         this.sC0k.reset();
      }
   }

   private void boFdrN7(int slotId) {
      if (this.mc.interactionManager != null && this.mc.player != null) {
         int syncId = this.mc.player.currentScreenHandler.syncId;
         this.mc.interactionManager.clickSlot(syncId, slotId, 0, SlotActionType.QUICK_MOVE, this.mc.player);
      }
   }
}
