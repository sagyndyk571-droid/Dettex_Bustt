package zov.viola.util.math;

import com.google.common.eventbus.Subscribe;
import lombok.Generated;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import zov.viola.Viola;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.util.IMinecraft;

public class PingGetter implements IMinecraft {
   private final StopWatch qngt4vu = new StopWatch();
   private boolean oCY7qb;
   private int nJQgsV;

   public PingGetter() {
      Viola.getInstance().getEventBus().register(this);
   }

   @Subscribe
   private void onUpdate(EventTick e) {
      this.nJQgsV = (int)this.qngt4vu.getTime();
      if (this.qngt4vu.getTime() > 1000L) {
         this.oCY7qb = true;
      }
   }

   @Subscribe
   private void onPacket(EventPacket e) {
      if (e.getPacket() instanceof CommonPingS2CPacket) {
         this.qngt4vu.reset();
      }
   }

   @Generated
   public StopWatch getStopWatch() {
      return this.qngt4vu;
   }

   @Generated
   public boolean isLagged() {
      return this.oCY7qb;
   }

   @Generated
   public int getPing() {
      return this.nJQgsV;
   }
}
