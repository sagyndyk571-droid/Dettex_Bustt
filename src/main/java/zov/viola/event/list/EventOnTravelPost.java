package zov.viola.event.list;

import net.minecraft.util.math.Vec3d;
import zov.viola.event.Event;

public class EventOnTravelPost extends Event {
   private Vec3d h1gh;

   public Vec3d getOldVelocity() {
      return this.h1gh;
   }

   public void setOldVelocity(Vec3d oldVelocity) {
      this.h1gh = oldVelocity;
   }

   public EventOnTravelPost(Vec3d oldVelocity) {
      this.h1gh = oldVelocity;
   }
}
