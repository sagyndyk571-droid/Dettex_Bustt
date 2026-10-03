package zov.viola.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.util.render.menu.MenuBackgroundRenderer;

@Mixin({Screen.class})
public class MenuScreenMixin {
   @Inject(
      method = {"renderPanoramaBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vio_renderCustomMenuBackground(DrawContext context, float delta, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.world == null) {
         ci.cancel();
         MenuBackgroundRenderer.render(context);
      }
   }
}
