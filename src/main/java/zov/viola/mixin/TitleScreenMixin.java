package zov.viola.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import zov.viola.obf.D;
import zov.viola.ui.AltScreen;
import zov.viola.util.render.builders.Builder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.menu.MenuBackgroundRenderer;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@Mixin({TitleScreen.class})
public class TitleScreenMixin {
   @Unique
   private static final float BTN_W = 200.0F;
   @Unique
   private static final float BTN_H = 28.0F;
   @Unique
   private static final float BTN_GAP = 4.0F;
   @Unique
   private static final Identifier LOGO_ID = Identifier.of("mre", "textures/gui/title/logo.png");

   @Shadow
   private void renderPanoramaBackground(DrawContext context, float delta) {
   }

   @Inject(
      method = {"init"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vio_init(CallbackInfo ci) {
      ci.cancel();
   }

   @Inject(
      method = {"renderPanoramaBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vio_renderCustomBackground(DrawContext context, float delta, CallbackInfo ci) {
      ci.cancel();
      MenuBackgroundRenderer.render(context);
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vio_render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      ci.cancel();
      int screenW = context.getScaledWindowWidth();
      int screenH = context.getScaledWindowHeight();
      if (screenW > 0 && screenH > 0) {
         TitleScreen self = (TitleScreen)(Object)this;
         this.renderPanoramaBackground(context, delta);
         int white = -1;
         int textMain = -986379;
         int textDim = -6248268;
         int textMuted = -9735552;
         int cardBg = -16448249;
         int cardBgHover = -15856110;
         float logoW = 300.0F;
         float logoH = logoW * 640.0F / 1672.0F;
         float gapLogo = 14.0F;
         float titleSize = 22.0F;
         float gapTitle = 20.0F;
         float blockH = logoH + gapLogo + titleSize + gapTitle + 112.0F + 12.0F;
         float blockY = Math.max(32.0F, (screenH - blockH) / 2.0F);
         String title = "Viola Client 1.21.4 | SRC BY NEXXY";
         float titleW = Fonts.SFBOLD.get().getWidth(title, titleSize);
         renderLogo(context, screenW / 2.0F, blockY, logoW, logoH);
         float logoTitleY = blockY + logoH + gapLogo;
         DrawUtil.drawText(Fonts.SFBOLD.get(), title, screenW / 2.0F - titleW / 2.0F, logoTitleY, white, titleSize);
         String[] labels = new String[]{
            "Одиночная игра",
            "Сетевая игра",
            "Аккаунты"
         };
         String[] icons = new String[]{"a", "b", "c"};
         float btnX = screenW / 2.0F - 100.0F;
         float btnY = logoTitleY + titleSize + gapTitle;

         for (int i = 0; i < labels.length; i++) {
            float by = btnY + i * 32.0F;
            boolean hov = mouseX >= btnX && mouseX < btnX + 200.0F && mouseY >= by && mouseY < by + 28.0F;
            int bg = hov ? cardBgHover : cardBg;
            DrawUtil.drawRound(btnX, by, 200.0F, 28.0F, 3.0F, bg);
            DrawUtil.drawText(Fonts.ICONS.get(), icons[i], btnX + 12.0F, by + 14.0F - 5.0F, hov ? white : textDim, 9.0F);
            DrawUtil.drawText(Fonts.SFMEDIUM.get(), labels[i], btnX + 26.0F, by + 14.0F - 4.0F, hov ? white : textMain, 9.0F);
            if (hov) {
               DrawUtil.drawText(Fonts.SFSEMIBOLD.get(), ">", btnX + 200.0F - 18.0F, by + 14.0F - 4.0F, white, 9.0F);
            }
         }

         int rowIdx = labels.length;
         float bottomY = btnY + rowIdx * 32.0F;
         float halfW = 98.0F;
         boolean setHov = mouseX >= btnX && mouseX < btnX + halfW && mouseY >= bottomY && mouseY < bottomY + 28.0F;
         DrawUtil.drawRound(btnX, bottomY, halfW, 28.0F, 3.0F, setHov ? cardBgHover : cardBg);
         DrawUtil.drawText(Fonts.ICONS.get(), "d", btnX + 12.0F, bottomY + 14.0F - 5.0F, setHov ? white : textDim, 9.0F);
         float setTW = Fonts.SFMEDIUM.get().getWidth("Настройки", 9.0F);
         DrawUtil.drawText(
            Fonts.SFMEDIUM.get(),
            "Настройки",
            btnX + (halfW - setTW) / 2.0F,
            bottomY + 14.0F - 4.0F,
            setHov ? white : textMain,
            9.0F
         );
         float quitX = btnX + halfW + 4.0F;
         boolean quitHov = mouseX >= quitX && mouseX < quitX + halfW && mouseY >= bottomY && mouseY < bottomY + 28.0F;
         DrawUtil.drawRound(quitX, bottomY, halfW, 28.0F, 3.0F, quitHov ? cardBgHover : cardBg);
         float quitTW = Fonts.SFMEDIUM.get().getWidth("Выход", 9.0F);
         DrawUtil.drawText(
            Fonts.SFMEDIUM.get(),
            "Выход",
            quitX + (halfW - quitTW) / 2.0F,
            bottomY + 14.0F - 4.0F,
            quitHov ? white : textMain,
            9.0F
         );
         float footerY = screenH - 12.0F;
         String footer = "ViolaClient 2026-2027";
         float footerSize = 6.0F;
         float footerW = Fonts.SFREGULAR.get().getWidth(footer, footerSize);
         DrawUtil.drawText(Fonts.SFREGULAR.get(), footer, screenW / 2.0F - footerW / 2.0F, footerY, ColorProvider.setAlpha(textMuted, 120), footerSize);
      }
   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void vio_mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      if (button == 0) {
         MinecraftClient mc = MinecraftClient.getInstance();
         int screenW = mc.getWindow().getScaledWidth();
         int screenH = mc.getWindow().getScaledHeight();
         float titleSize = 22.0F;
         float gapTitle = 20.0F;
         float logoW = 300.0F;
         float logoH = logoW * 640.0F / 1672.0F;
         float gapLogo = 14.0F;
         float blockH = logoH + gapLogo + titleSize + gapTitle + 112.0F + 12.0F;
         float blockY = Math.max(32.0F, (screenH - blockH) / 2.0F);
         float btnX = screenW / 2.0F - 100.0F;
         float btnY = blockY + logoH + gapLogo + titleSize + gapTitle;
         float halfW = 98.0F;
         TitleScreen self = (TitleScreen)(Object)this;
         if (hit(mouseX, mouseY, btnX, btnY, 200.0F, 28.0F)) {
            mc.setScreen(new SelectWorldScreen(self));
            cir.setReturnValue(true);
         } else if (hit(mouseX, mouseY, btnX, btnY + 28.0F + 4.0F, 200.0F, 28.0F)) {
            mc.setScreen(new MultiplayerScreen(self));
            cir.setReturnValue(true);
         } else if (hit(mouseX, mouseY, btnX, btnY + 64.0F, 200.0F, 28.0F)) {
            mc.setScreen(new AltScreen(self));
            cir.setReturnValue(true);
         } else if (hit(mouseX, mouseY, btnX, btnY + 96.0F, halfW, 28.0F)) {
            mc.setScreen(new OptionsScreen(self, mc.options));
            cir.setReturnValue(true);
         } else {
            float quitX = btnX + halfW + 4.0F;
            if (hit(mouseX, mouseY, quitX, btnY + 96.0F, halfW, 28.0F)) {
               mc.scheduleStop();
               cir.setReturnValue(true);
            }
         }
      }
   }

   @Unique
   private static void renderLogo(DrawContext context, float centerX, float y, float width, float height) {
      AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(LOGO_ID);
      if (tex != null) {
         int texId = tex.getGlId();
         if (texId > 0) {
            RenderSystem.bindTexture(texId);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            Builder.texture()
               .size(new SizeState(width, height))
               .radius(new QuadRadiusState(0.0F))
               .color(new QuadColorState(-1))
               .smoothness(1.0F)
               .texture(0.0F, 0.0F, 1.0F, 1.0F, texId)
               .build()
               .render(context.getMatrices().peek().getPositionMatrix(), centerX - width / 2.0F, y, 0.0F);
         }
      }
   }

   @Unique
   private static boolean hit(double mx, double my, float x, float y, float w, float h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }
}
