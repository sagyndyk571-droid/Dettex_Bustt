package zov.viola.mixin;

import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zov.viola.event.list.EventHUD;
import zov.viola.module.list.render.ShaderScreens;
import zov.viola.module.list.render.hud.Interface;
import zov.viola.module.settings.impl.ThemeManager;
import zov.viola.ui.ClickGuiBackgroundRenderer;
import zov.viola.ui.CsPalette;
import zov.viola.util.base.Instance;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;
import zov.viola.util.render.renderers.impl.BuiltBlur;

@Mixin({InGameHud.class})
public class InGameHudMixin {
   @Shadow
   @Final
   private static Comparator<ScoreboardEntry> SCOREBOARD_ENTRY_COMPARATOR;

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void beginBlurFrame(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      BuiltBlur.beginFrame();
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      new EventHUD(context, tickCounter).post();
   }

   @Inject(
      method = {"renderStatusEffectOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hideVanillaStatusEffects(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      Interface iface = Instance.get(Interface.class);
      if (iface != null && iface.isPotionsActive()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderHotbar"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hideVanillaHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      Interface iface = Instance.get(Interface.class);
      if (iface != null && iface.isCustomHotbarActive()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderExperienceBar"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hideVanillaExperienceBar(DrawContext context, int x, CallbackInfo ci) {
      Interface iface = Instance.get(Interface.class);
      if (iface != null && iface.isCustomHotbarActive()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderExperienceLevel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hideVanillaExperienceLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      Interface iface = Instance.get(Interface.class);
      if (iface != null && iface.isCustomHotbarActive()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"},
      at = {@At("HEAD")}
   )
   private void drawShaderSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
      ShaderScreens mod = Instance.get(ShaderScreens.class);
      if (mod != null && mod.isEnabled() && mod.isSidebarShader()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.player != null && mc.world != null) {
            List<ScoreboardEntry> entries = objective.getScoreboard()
               .getScoreboardEntries(objective)
               .stream()
               .filter(entry -> !entry.hidden())
               .sorted(SCOREBOARD_ENTRY_COMPARATOR)
               .limit(15L)
               .toList();
            if (!entries.isEmpty()) {
               TextRenderer tr = mc.textRenderer;
               int titleW = tr.getWidth(objective.getDisplayName());
               int colonW = tr.getWidth(":");
               int sidebarWidth = titleW;

               for (ScoreboardEntry entry : entries) {
                  Text scoreText = entry.formatted(objective.getNumberFormatOr(StyledNumberFormat.RED));
                  int scoreW = tr.getWidth(scoreText);
                  int w = tr.getWidth(Team.decorateName(objective.getScoreboard().getScoreHolderTeam(entry.owner()), entry.name()))
                     + (scoreW > 0 ? colonW + scoreW : 0);
                  sidebarWidth = Math.max(sidebarWidth, w);
               }

               int count = entries.size();
               int startY = context.getScaledWindowHeight() / 2 + count * 9 / 3;
               int left = context.getScaledWindowWidth() - sidebarWidth - 3;
               int right = context.getScaledWindowWidth() - 1;
               int top = startY - count * 9;
               int pad = 5;
               float px = left - 2 - pad;
               float py = top - 10 - pad;
               float pw = right - (left - 2) + pad * 2.0F;
               float ph = count * 9 + 10 + pad * 2.0F;
               int accent = ThemeManager.getInstance().getCurrentTheme().getColorFirst();
               float radius = 8.0F;
               String mode = mod.getShaderMode();
               float opacity = mod.getShaderOpacity();
               ClickGuiBackgroundRenderer.render(mode, accent, opacity, px, py, pw + 0.5F, ph + 0.25F, radius);
               DrawUtil.drawRound(px, py, pw, ph, radius, ColorProvider.setAlpha(CsPalette.PANEL_BG, 23));
               DrawUtil.drawRound(px, py, pw, ph, radius, ColorProvider.setAlpha(accent, 8));
               DrawUtil.drawRound(px - 0.5F, py - 0.5F, pw + 1.0F, ph + 1.0F, radius + 0.5F, ColorProvider.setAlpha(CsPalette.BORDER, 80));
            }
         }
      }
   }

   @Redirect(
      method = {"renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
      )
   )
   private void transparentSidebarBg(DrawContext context, int x1, int y1, int x2, int y2, int color) {
      ShaderScreens mod = Instance.get(ShaderScreens.class);
      if (mod == null || !mod.isEnabled() || !mod.isSidebarShader()) {
         context.fill(x1, y1, x2, y2, color);
      }
   }
}
