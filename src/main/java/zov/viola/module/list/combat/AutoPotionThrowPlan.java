package zov.viola.module.list.combat;

final class AutoPotionThrowPlan {
   private static final float DOWNWARD_PITCH = 90.0F;

   private AutoPotionThrowPlan() {
   }

   static AutoPotionThrowPlan.ServerRotation forYaw(float yaw) {
      return new AutoPotionThrowPlan.ServerRotation(yaw, 90.0F);
   }

   record ServerRotation(float yaw, float pitch) {
   }
}
