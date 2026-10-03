package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.entity.player.PlayerEntity;
import zov.viola.event.Event;

public class EventPopTotem extends Event {
   private final PlayerEntity ynK3;

   @Generated
   public PlayerEntity getPlayer() {
      return this.ynK3;
   }

   @Generated
   public EventPopTotem(PlayerEntity player) {
      this.ynK3 = player;
   }
}
