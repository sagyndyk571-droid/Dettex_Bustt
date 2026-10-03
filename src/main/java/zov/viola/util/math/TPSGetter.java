package zov.viola.util.math;

import com.google.common.eventbus.Subscribe;
import java.util.LinkedList;
import java.util.Queue;
import lombok.Generated;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import zov.viola.Viola;
import zov.viola.event.list.EventPacket;

public class TPSGetter {
   private final Queue<Float> dLfjHT = new LinkedList<>();
   private float z6L9 = 20.0F;
   private float mcRs8 = 20.0F;
   private float fw1CZm = 0.0F;
   private long aT49Z;

   public TPSGetter() {
      Viola.getInstance().getEventBus().register(this);
   }

   @Subscribe
   private void onPacket(EventPacket e) {
      if (e.getPacket() != null && e.getPacket() instanceof WorldTimeUpdateS2CPacket) {
         this.dusiExp();
      }
   }

   private void dusiExp() {
      long delay = System.nanoTime() - this.aT49Z;
      float maxTPS = 20.0F;
      float rawTPS = maxTPS * (1.0E9F / (float)delay);
      float boundedTPS = MathHelper.clamp(rawTPS, 0.0F, maxTPS);
      this.z6L9 = (float)this.round(boundedTPS);
      this.fw1CZm = boundedTPS - maxTPS;
      this.aT49Z = System.nanoTime();
      this.m6qq();
   }

   private void m6qq() {
      if (this.dLfjHT.size() >= 10) {
         this.dLfjHT.poll();
      }

      this.dLfjHT.add(this.z6L9);
      float sum = 0.0F;

      for (float tps : this.dLfjHT) {
         sum += tps;
      }

      this.mcRs8 = (float)this.round(sum / this.dLfjHT.size());
   }

   public double round(double input) {
      return Math.round(input * 100.0) / 100.0;
   }

   @Generated
   public Queue<Float> getTpsHistory() {
      return this.dLfjHT;
   }

   @Generated
   public float getTPS() {
      return this.z6L9;
   }

   @Generated
   public float getAverageTPS() {
      return this.mcRs8;
   }

   @Generated
   public float getAdjustTicks() {
      return this.fw1CZm;
   }

   @Generated
   public long getTimestamp() {
      return this.aT49Z;
   }
}
