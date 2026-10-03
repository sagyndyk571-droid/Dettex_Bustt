package zov.viola.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerListHud.class})
public interface PlayerListHudAccessor {
   @Accessor("visible")
   boolean vio_isVisible();
}
