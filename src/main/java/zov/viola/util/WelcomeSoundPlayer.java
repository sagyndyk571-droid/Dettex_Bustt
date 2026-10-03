package zov.viola.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class WelcomeSoundPlayer {
   private static final Identifier WELCOME_ID = Identifier.of("mre", "welcome");
   private static final SoundEvent WELCOME_EVENT = SoundEvent.of(WELCOME_ID);
   private static boolean iw3Z = false;

   private WelcomeSoundPlayer() {
   }

   public static void playOnce() {
      if (!iw3Z) {
         iw3Z = true;
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.getSoundManager() != null) {
            mc.getSoundManager().play(PositionedSoundInstance.master(WELCOME_EVENT, 1.0F, 1.0F));
         }
      }
   }
}
