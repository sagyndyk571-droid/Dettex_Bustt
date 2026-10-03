package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.entity.Entity;
import zov.viola.event.Event;

public class EventAttack extends Event {
   private final Entity blLty4u;

   @Generated
   public Entity getEntity() {
      return this.blLty4u;
   }

   @Generated
   public EventAttack(Entity entity) {
      this.blLty4u = entity;
   }
}
