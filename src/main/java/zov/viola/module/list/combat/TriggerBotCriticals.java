package zov.viola.module.list.combat;

final class TriggerBotCriticals {
   private TriggerBotCriticals() {
   }

   static boolean shouldAttack(boolean onlyCriticals, boolean smartCriticals, boolean jumpPressed, boolean criticalPhysicallyPossible, boolean criticalNow) {
      if (!onlyCriticals || !criticalPhysicallyPossible) {
         return true;
      } else {
         return smartCriticals && !jumpPressed ? true : criticalNow;
      }
   }
}
