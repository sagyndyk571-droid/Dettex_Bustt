package zov.viola.module.list.render;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.ModuleSettingDefinitions;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Client Sounds",
   moduleDesc = "Создает звук при включении/выключении функции",
   moduleCategory = ModuleCategory.RENDER
)
public class ClientSounds extends Module {
   private static ClientSounds kgzo;
   private final ModeSetting ssmiSk = new ModeSetting(
      "Мод звука",
      "Первый",
      "Первый",
      "Второй",
      "Третий"
   );
   private final SliderSetting gxTntL = ModuleSettingDefinitions.clientSoundsVolume();
   private static final Identifier ENABLE_ID = Identifier.of("mre", "module_enable");
   private static final Identifier DISABLE_ID = Identifier.of("mre", "module_disable");
   private static final Identifier ENABLE_ID1 = Identifier.of("mre", "module_enable1");
   private static final Identifier DISABLE_ID1 = Identifier.of("mre", "module_disable1");
   private static final Identifier ENABLE_ID2 = Identifier.of("mre", "module_enable2");
   private static final Identifier DISABLE_ID2 = Identifier.of("mre", "module_disable2");
   private static final SoundEvent ENABLE_EVENT = SoundEvent.of(ENABLE_ID);
   private static final SoundEvent DISABLE_EVENT = SoundEvent.of(DISABLE_ID);
   private static final SoundEvent ENABLE_EVENT1 = SoundEvent.of(ENABLE_ID1);
   private static final SoundEvent DISABLE_EVENT1 = SoundEvent.of(DISABLE_ID1);
   private static final SoundEvent ENABLE_EVENT2 = SoundEvent.of(ENABLE_ID2);
   private static final SoundEvent DISABLE_EVENT2 = SoundEvent.of(DISABLE_ID2);
   private static SoundInstance fkakq;

   public ClientSounds() {
      kgzo = this;
   }

   public static void play(boolean enabled) {
      if (kgzo != null && kgzo.mc.getSoundManager() != null) {
         if (kgzo.isEnabled() || kgzo == Viola.getInstance().getModuleStorage().get(ClientSounds.class)) {
            if (fkakq != null) {
               kgzo.mc.getSoundManager().stop(fkakq);
            }

            String var2 = kgzo.ssmiSk.getValue();

            SoundEvent soundToPlay = switch (var2) {
               case "Второй" -> enabled ? ENABLE_EVENT1 : DISABLE_EVENT1;
               case "Третий" -> enabled ? ENABLE_EVENT2 : DISABLE_EVENT2;
               default -> enabled ? ENABLE_EVENT : DISABLE_EVENT;
            };
            fkakq = PositionedSoundInstance.master(soundToPlay, 1.0F, kgzo.gxTntL.getFloatValue() / 100.0F);
            kgzo.mc.getSoundManager().play(fkakq);
         }
      }
   }
}
