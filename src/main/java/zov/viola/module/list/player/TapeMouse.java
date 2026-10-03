package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.util.hit.HitResult.Type;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.bot.BotSessionManager;

@ModuleInformation(
   moduleName = "Tape Mouse",
   moduleDesc = "Автоматические клики для фоновых ботов",
   moduleCategory = ModuleCategory.PLAYER
)
public class TapeMouse extends Module {
   private final SliderSetting v8w4L1 = new SliderSetting("CPS", 1.0, 0.0, 2.0, 0.05F);
   private final SliderSetting rdrw = new SliderSetting(
      "Доп. задержка", 0.0, 0.0, 1000.0, 10.0
   );
   private final BooleanSetting y29rb35 = new BooleanSetting(
      "Правая кнопка", false
   );
   private final BooleanSetting a3D9N = new BooleanSetting(
      "Только по entity", false
   );
   private final BooleanSetting aeUbp6 = new BooleanSetting(
      "Только боты", true
   );
   private long r54y0d;

   @Subscribe
   private void onTick(EventTick eventTick) {
      long cpsDelay = this.v8w4L1.getValue() > 0.0 ? (long)(1000.0 / this.v8w4L1.getValue()) : 1000L;
      long effectiveDelay = Math.max(cpsDelay, (long)this.rdrw.getValue());
      long now = System.currentTimeMillis();
      if (now - this.r54y0d >= effectiveDelay) {
         if (!this.aeUbp6.getValue()) {
            this.o8rmve5();
         }

         if (!BotSessionManager.getConnections().isEmpty()) {
            BotSessionManager.pulseBots(this.y29rb35.getValue());
         }

         this.r54y0d = now;
      }
   }

   private void o8rmve5() {
      if (this.mc.player != null && this.mc.currentScreen == null) {
         if (this.y29rb35.getValue()) {
            this.mc.doItemUse();
         } else if (!this.a3D9N.getValue() || this.mc.crosshairTarget != null && this.mc.crosshairTarget.getType() == Type.ENTITY) {
            this.mc.doAttack();
         }
      }
   }
}
