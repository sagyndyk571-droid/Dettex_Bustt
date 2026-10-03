package zov.viola.event.list;

import net.minecraft.entity.Entity;
import zov.viola.event.Event;

public class EventEntityHitBox extends Event {
   private final Entity doR307y;
   private float adn8uQs;

   public Entity getEntity() {
      return this.doR307y;
   }

   public float getSize() {
      return this.adn8uQs;
   }

   public EventEntityHitBox(Entity entity, float size) {
      this.doR307y = entity;
      this.adn8uQs = size;
   }

   public void setSize(float size) {
      this.adn8uQs = size;
   }
}
