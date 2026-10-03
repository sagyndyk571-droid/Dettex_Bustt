package zov.viola.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import zov.viola.Viola;
import zov.viola.module.list.misc.NameProtect;
import zov.viola.util.replace.ReplaceUtil;

@Mixin({Team.class})
public class TeamMixin {
   @ModifyArg(
      method = {"decorateName(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/text/MutableText;append(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;",
         ordinal = 0
      ),
      index = 0
   )
   private Text modify(Text original) {
      if (MinecraftClient.getInstance().player == null) {
         return original;
      } else {
         String me = MinecraftClient.getInstance().player.getNameForScoreboard();
         String replaced = Viola.getInstance().getModuleStorage().get(NameProtect.class).getCustomName(me);
         return me.equals(replaced) ? original : ReplaceUtil.replaceLiteral(original, me, replaced);
      }
   }
}
