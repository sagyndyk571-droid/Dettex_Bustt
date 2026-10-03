package zov.viola.util.base;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import lombok.Generated;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.obf.D;
import zov.viola.util.rotation.Component;

public final class Instance {
   private static final ConcurrentMap<Class<? extends Module>, Module> instances = new ConcurrentHashMap<>();
   private static final ConcurrentMap<Class<? extends Component>, Component> componentInstances = new ConcurrentHashMap<>();

   public static <T extends Module> T get(Class<T> clazz) {
      return clazz.cast(instances.computeIfAbsent(clazz, instance -> Viola.getInstance().getModuleStorage().get((Class<? extends Module>)instance)));
   }

   public static <T extends Component> T getComponent(Class<T> clazz) {
      return clazz.cast(
         componentInstances.computeIfAbsent(clazz, instance -> Viola.getInstance().getComponentManager().get((Class<? extends Component>)instance))
      );
   }

   public static <T extends Module> Supplier<T> getSupplier(Class<T> clazz) {
      return () -> clazz.cast(instances.computeIfAbsent(clazz, instance -> Viola.getInstance().getModuleStorage().get((Class<? extends Module>)instance)));
   }

   public static <T extends Module> T get(String module) {
      return Viola.getInstance().getModuleStorage().get(module);
   }

   public static List<Module> get(ModuleCategory category) {
      return Viola.getInstance().getModuleStorage().get(category);
   }

   @Generated
   private Instance() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               218,
               184,
               66,
               58,
               174,
               185,
               88,
               105,
               239,
               240,
               94,
               61,
               231,
               188,
               66,
               61,
               247,
               240,
               72,
               37,
               239,
               163,
               88,
               105,
               239,
               190,
               79,
               105,
               237,
               177,
               69,
               39,
               225,
               164,
               11,
               43,
               235,
               240,
               66,
               39,
               253,
               164,
               74,
               39,
               250,
               185,
               74,
               61,
               235,
               180
            },
            new int[]{142, 208, 43, 73}
         )
      );
   }
}
