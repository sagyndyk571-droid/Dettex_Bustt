package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.entity.Entity;
import zov.viola.event.Event;

public class EventEntitySpawn extends Event {
   private final Entity zcqnk;

   @Generated
   public Entity getEntity() {
      return this.zcqnk;
   }

   @Generated
   public EventEntitySpawn(Entity entity) {
      this.zcqnk = entity;
   }
}
