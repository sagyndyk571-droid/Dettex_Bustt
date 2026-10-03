package zov.viola.util.aim;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class TargetSelector {
   public static boolean isLivingEntity(LivingEntity livingEntity) {
      for (ItemStack itemstack : livingEntity.getArmorItems()) {
         if (!itemstack.isEmpty()) {
            return true;
         }
      }

      return false;
   }
}
