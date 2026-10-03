package zov.viola.util.rotation;

import com.google.common.eventbus.Subscribe;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import zov.viola.event.list.LookEvent;
import zov.viola.event.list.RotationEvent;

public class FreeLookComponent extends Component {
   private static boolean zBQhgla;
   private static float cG79;
   private static float gesLb;

   @Subscribe
   public void onEvent(LookEvent event) {
      if (zBQhgla) {
         this.nFN8Xg(event.getYaw(), event.getPitch());
         event.cancelEvent();
      }
   }

   @Subscribe
   public void onEvent(RotationEvent event) {
      if (zBQhgla) {
         event.setYaw(cG79);
         event.setPitch(gesLb);
      } else {
         cG79 = event.getYaw();
         gesLb = event.getPitch();
      }
   }

   private void nFN8Xg(double targetYaw, double targetPitch) {
      gesLb = MathHelper.clamp((float)(gesLb + targetPitch * 0.15), -90.0F, 90.0F);
      cG79 = (float)(cG79 + targetYaw * 0.15);
   }

   @Generated
   public static boolean isActive() {
      return zBQhgla;
   }

   @Generated
   public static void setActive(boolean active) {
      zBQhgla = active;
   }

   @Generated
   public static float getFreeYaw() {
      return cG79;
   }

   @Generated
   public static float getFreePitch() {
      return gesLb;
   }

   @Generated
   public static void setFreeYaw(float freeYaw) {
      cG79 = freeYaw;
   }

   @Generated
   public static void setFreePitch(float freePitch) {
      gesLb = freePitch;
   }
}
