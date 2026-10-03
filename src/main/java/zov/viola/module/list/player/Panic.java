package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import zov.viola.Viola;
import zov.viola.event.list.ChatEvent;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Panic",
   moduleDesc = "Маскировка под ваниллу + Sodium",
   moduleCategory = ModuleCategory.MISC
)
public class Panic extends Module {
   private final List<Module> ypvtcp = new ArrayList<>();
   private final SecureRandom w5L6o5 = new SecureRandom();
   private int lvfD = -1;
   private MessageSignatureData z0cV9t;
   private long yvGE = -1L;
   private boolean l2rn = false;

   @Override
   public void onEnable() {
      super.onEnable();
      Viola.getInstance().setPanicMode(true);
      this.ypvtcp.clear();

      for (Module module : Viola.getInstance().getModuleStorage().getModules()) {
         if (module != this && module.isEnabled()) {
            this.ypvtcp.add(module);
         }
      }

      for (Module modulex : this.ypvtcp) {
         modulex.setEnabled(false);
      }

      this.lvfD = 100 + this.w5L6o5.nextInt(900);
      this.z0cV9t = null;
      this.yvGE = -1L;
      this.l2rn = true;
      if (this.mc.currentScreen != null) {
         this.mc.setScreen(null);
      }

      if (this.mc.getWindow() != null) {
         this.mc.updateWindowTitle();
      }

      System.out.println("[Sodium] Verification code: " + this.lvfD);
      this.zmueft();
   }

   @Override
   public void onDisable() {
      if (Viola.getInstance().isPanicMode()) {
         this.restore();
      }

      super.onDisable();
   }

   @Subscribe
   public void onTick(EventTick event) {
      if (Viola.getInstance().isPanicMode()) {
         if (this.yvGE > 0L && System.currentTimeMillis() - this.yvGE >= 5000L) {
            this.yvGE = -1L;
            this.eehU();
         }

         if (this.l2rn && this.mc.player != null && this.mc.inGameHud != null) {
            this.l2rn = false;
            this.ku4ZBAq();
            this.yvGE = System.currentTimeMillis();
         }
      }
   }

   @Subscribe
   public void onChat(ChatEvent event) {
      if (Viola.getInstance().isPanicMode() && this.lvfD > 0 && event.getMessage() != null) {
         if (event.getMessage().equals(String.valueOf(this.lvfD))) {
            event.cancelEvent();
            this.restore();
         }
      }
   }

   public void restore() {
      if (Viola.getInstance().isPanicMode()) {
         this.eehU();
         this.yvGE = -1L;
         this.lvfD = -1;
         this.l2rn = false;
         Viola.getInstance().setPanicMode(false);

         for (Module module : this.ypvtcp) {
            if (!module.isEnabled() && module != this) {
               module.setEnabled(true);
            }
         }

         this.ypvtcp.clear();
         if (this.isEnabled()) {
            this.setEnabled(false);
         }
      }
   }

   private void ku4ZBAq() {
      if (this.mc.inGameHud != null) {
         ChatHud chatHud = this.mc.inGameHud.getChatHud();
         byte[] bytes = new byte[256];
         this.w5L6o5.nextBytes(bytes);
         this.z0cV9t = new MessageSignatureData(bytes);
         chatHud.addMessage(Text.of("ViolaClient: код чтобы вернуть чит:" + this.lvfD), this.z0cV9t, MessageIndicator.system());
      }
   }

   private void eehU() {
      if (this.z0cV9t != null && this.mc.inGameHud != null) {
         ChatHud chatHud = this.mc.inGameHud.getChatHud();
         chatHud.removeMessage(this.z0cV9t);
         this.z0cV9t = null;
      }
   }

   private void zmueft() {
      System.out
         .println(
            D.k(
               new int[]{
                  105,
                  72,
                  207,
                  245,
                  91,
                  110,
                  205,
                  204,
                  18,
                  82,
                  206,
                  248,
                  70,
                  114,
                  193,
                  253,
                  91,
                  97,
                  201,
                  255,
                  85,
                  59,
                  243,
                  254,
                  86,
                  114,
                  213,
                  252,
                  18,
                  42,
                  142,
                  163,
                  3,
                  53,
                  148
               },
               new int[]{50, 27, 160, 145}
            )
         );
      System.out
         .println(
            D.k(
               new int[]{133, 84, 87, 31, 183, 114, 85, 38, 254, 69, 89, 24, 181, 98, 86, 31, 228, 39, 110, 14, 178, 108, 89, 21, 254, 53, 22, 75},
               new int[]{222, 7, 56, 123}
            )
         );
      System.out
         .println(
            D.k(
               new int[]{
                  41,
                  116,
                  192,
                  180,
                  27,
                  82,
                  194,
                  141,
                  82,
                  97,
                  206,
                  178,
                  0,
                  78,
                  204,
                  240,
                  0,
                  66,
                  193,
                  180,
                  23,
                  85,
                  202,
                  162,
                  82,
                  87,
                  198,
                  160,
                  23,
                  75,
                  198,
                  190,
                  23,
                  7,
                  220,
                  164,
                  19,
                  69,
                  195,
                  181
               },
               new int[]{114, 39, 175, 208}
            )
         );
      System.out
         .println(
            D.k(
               new int[]{
                  185,
                  52,
                  201,
                  80,
                  139,
                  18,
                  203,
                  105,
                  194,
                  38,
                  202,
                  88,
                  194,
                  17,
                  199,
                  90,
                  139,
                  11,
                  202,
                  85,
                  194,
                  21,
                  195,
                  90,
                  134,
                  2,
                  212,
                  81,
                  144,
                  71,
                  203,
                  91,
                  134,
                  18,
                  202,
                  81,
                  145,
                  71,
                  212,
                  81,
                  145,
                  19,
                  201,
                  70,
                  135,
                  3
               },
               new int[]{226, 103, 166, 52}
            )
         );
   }
}
