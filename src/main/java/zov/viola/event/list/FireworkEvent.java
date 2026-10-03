package zov.viola.event.list;

import net.minecraft.entity.LivingEntity;
import zov.viola.event.Event;

public class FireworkEvent extends Event {
   private final LivingEntity w8I19i;
   private float lzjp;

   public FireworkEvent(LivingEntity boostedEntity, float speed) {
      this.w8I19i = boostedEntity;
      this.lzjp = speed;
   }

   public LivingEntity getBoostedEntity() {
      return this.w8I19i;
   }

   public float getSpeed() {
      return this.lzjp;
   }

   public void setSpeed(float speed) {
      this.lzjp = speed;
   }
}
