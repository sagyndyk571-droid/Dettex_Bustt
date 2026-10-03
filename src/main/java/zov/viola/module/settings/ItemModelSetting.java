package zov.viola.module.settings;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ItemModelSetting extends ModeSetting {
   private final Map<String, ItemStack> n4rPnaL = new LinkedHashMap<>();

   public ItemModelSetting(String name, String defaultValue, String... models) {
      super(name, defaultValue, models);

      for (String model : models) {
         ItemStack preview = new ItemStack(Items.STICK);
         preview.set(DataComponentTypes.ITEM_MODEL, this.getModelId(model));
         this.n4rPnaL.put(model, preview);
      }
   }

   public Identifier getModelId(String model) {
      return Identifier.of("mre", "item_replacer/sword/" + model);
   }

   public ItemStack getPreviewStack(String model) {
      return this.n4rPnaL.get(model);
   }
}
