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
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.menu.MenuBackgroundRenderer;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ClientPalette;
import zov.viola.util.render.providers.ColorProvider;
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
   @Unique
   private static final Identifier LOGO_GLOW_ID = Identifier.of("mre", "images/glow.png");

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
         this.renderPanoramaBackground(context, delta);
         float[] L = dtLayout(screenW, screenH);
         float leftCx = L[1];
         float logoY = L[2];
         float logoW = L[3];
         float logoH = L[4];
         float titleY = L[5];
         float titleSize = L[6];
         float btnX = L[7];
         float btnY = L[8];
         float btnW = L[9];
         float btnH = L[10];
         float halfW = L[11];
         float gap = 4.0F;
         float btnText = 8.5F;
         int white = -1;
         int textMain = -986379;
         int textMuted = -9735552;
         int cardBg = -16448249;
         int cardBgHover = -15856110;
         float tAnim = (float)(System.currentTimeMillis() % 60000L) / 1000.0F;
         float breathe = (float)(Math.sin(tAnim * 2.2F) * 0.5F + 0.5F);
         int brandCol = ColorProvider.interpolateColor(ClientPalette.pickTwo(ColorProvider.getColorVisualModules()), -1, 0.15F + breathe * 0.35F);
         renderLogo(context, leftCx, logoY, logoW, logoH);
         float brandW1 = Fonts.SFBOLD.get().getWidth("Dettex", titleSize);
         float brandW2 = Fonts.SFBOLD.get().getWidth("Client 1.21.11", titleSize);
         float titleX = leftCx - (brandW1 + 2.0F + brandW2) / 2.0F;
         DrawUtil.drawText(Fonts.SFBOLD.get(), "Dettex", titleX, titleY, brandCol, titleSize);
         DrawUtil.drawText(Fonts.SFBOLD.get(), "Client 1.21.11", titleX + brandW1 + 2.0F, titleY, white, titleSize);
         String[] labels = new String[]{
            "Одиночная игра",
            "Сетевая игра",
            "Аккаунты"
         };

         for (int i = 0; i < labels.length; i++) {
            float by = btnY + i * (btnH + gap);
            boolean hov = mouseX >= btnX && mouseX < btnX + btnW && mouseY >= by && mouseY < by + btnH;
            int bg = hov ? cardBgHover : cardBg;
            DrawUtil.drawRound(btnX, by, btnW, btnH, 4.0F, bg);
            float ty = by + (btnH - btnText) / 2.0F;
            DrawUtil.drawText(Fonts.SFMEDIUM.get(), labels[i], btnX + 12.0F, ty, hov ? white : textMain, btnText);
            if (hov) {
               DrawUtil.drawText(Fonts.SFSEMIBOLD.get(), ">", btnX + btnW - 18.0F, ty, white, btnText);
            }
         }

         float bottomY = btnY + labels.length * (btnH + gap);
         float tyRow = bottomY + (btnH - btnText) / 2.0F;
         boolean setHov = mouseX >= btnX && mouseX < btnX + halfW && mouseY >= bottomY && mouseY < bottomY + btnH;
         DrawUtil.drawRound(btnX, bottomY, halfW, btnH, 4.0F, setHov ? cardBgHover : cardBg);
         float setTW = Fonts.SFMEDIUM.get().getWidth("Настройки", btnText);
         DrawUtil.drawText(
            Fonts.SFMEDIUM.get(),
            "Настройки",
            btnX + (halfW - setTW) / 2.0F,
            tyRow,
            setHov ? white : textMain,
            btnText
         );
         float quitX = btnX + halfW + gap;
         boolean quitHov = mouseX >= quitX && mouseX < quitX + halfW && mouseY >= bottomY && mouseY < bottomY + btnH;
         DrawUtil.drawRound(quitX, bottomY, halfW, btnH, 4.0F, quitHov ? cardBgHover : cardBg);
         float quitTW = Fonts.SFMEDIUM.get().getWidth("Выход", btnText);
         DrawUtil.drawText(
            Fonts.SFMEDIUM.get(),
            "Выход",
            quitX + (halfW - quitTW) / 2.0F,
            tyRow,
            quitHov ? white : textMain,
            btnText
         );
         float footerY = screenH - 12.0F;
          String footer = "DettexClient 2026-2027";
         float footerSize = 6.0F;
         float footerW = Fonts.SFREGULAR.get().getWidth(footer, footerSize);
         DrawUtil.drawText(Fonts.SFREGULAR.get(), footer, screenW / 2.0F - footerW / 2.0F, footerY, ColorProvider.setAlpha(textMuted, 120), footerSize);
         float wbW = 110.0F;
         float wbH = 18.0F;
         float wbX = 12.0F;
         float wbY = screenH - 12.0F - wbH;
         boolean wbHov = mouseX >= wbX && mouseX < wbX + wbW && mouseY >= wbY && mouseY < wbY + wbH;
         DrawUtil.drawRound(wbX, wbY, wbW, wbH, 4.0F, wbHov ? cardBgHover : cardBg);
         String wbText = "Обои: " + MenuBackgroundRenderer.currentName();
         DrawUtil.drawText(Fonts.SFMEDIUM.get(), wbText, wbX + 8.0F, wbY + (wbH - 7.0F) / 2.0F, wbHov ? white : textMain, 7.0F);
      }
   }

   @Unique
   private static float[] dtLayout(int screenW, int screenH) {
      float gap = 4.0F;
      float btnW = 170.0F;
      float btnH = 26.0F;
      float totalH = btnH * 4.0F + gap * 3.0F;
      float rightMargin = Math.max(24.0F, screenW * 0.1F);
      float bX = screenW - rightMargin - btnW;
      float logoW = 240.0F;
      float logoH = logoW * 640.0F / 1672.0F;
      float leftCx = bX * 0.4F;
      boolean wide = screenW >= 560.0F && leftCx - logoW / 2.0F >= 12.0F;
      if (!wide) {
         logoW = 200.0F;
         logoH = logoW * 640.0F / 1672.0F;
         btnW = 180.0F;
         leftCx = screenW / 2.0F;
         bX = screenW / 2.0F - btnW / 2.0F;
         float titleSize = 18.0F;
         float blockH = logoH + 12.0F + titleSize + 14.0F + totalH + 8.0F;
         float top = Math.max(10.0F, (screenH - blockH) / 2.0F);
         float logoY = top;
         float titleY = logoY + logoH + 12.0F;
         float nbY = titleY + titleSize + 14.0F;
         return new float[]{0.0F, leftCx, logoY, logoW, logoH, titleY, titleSize, bX, nbY, btnW, btnH, (btnW - gap) / 2.0F};
      }
      float titleSize = 20.0F;
      float blockH = logoH + 12.0F + titleSize;
      float logoY = Math.max(12.0F, (screenH - blockH) / 2.0F);
      float titleY = logoY + logoH + 12.0F;
      float bY = Math.max(12.0F, (screenH - totalH) / 2.0F);
      return new float[]{1.0F, leftCx, logoY, logoW, logoH, titleY, titleSize, bX, bY, btnW, btnH, (btnW - gap) / 2.0F};
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
         if (hit(mouseX, mouseY, 12.0F, screenH - 30.0F, 110.0F, 18.0F)) {
            MenuBackgroundRenderer.next();
            cir.setReturnValue(true);
            return;
         }
         float[] L = dtLayout(screenW, screenH);
         float btnX = L[7];
         float btnY = L[8];
         float btnW = L[9];
         float btnH = L[10];
         float halfW = L[11];
         float gap = 4.0F;
         float rowH = btnH + gap;
         TitleScreen self = (TitleScreen)(Object)this;
         if (hit(mouseX, mouseY, btnX, btnY, btnW, btnH)) {
            mc.setScreen(new SelectWorldScreen(self));
            cir.setReturnValue(true);
         } else if (hit(mouseX, mouseY, btnX, btnY + rowH, btnW, btnH)) {
            mc.setScreen(new MultiplayerScreen(self));
            cir.setReturnValue(true);
         } else if (hit(mouseX, mouseY, btnX, btnY + rowH * 2.0F, btnW, btnH)) {
            mc.setScreen(new AltScreen(self));
            cir.setReturnValue(true);
         } else if (hit(mouseX, mouseY, btnX, btnY + rowH * 3.0F, halfW, btnH)) {
            mc.setScreen(new OptionsScreen(self, mc.options));
            cir.setReturnValue(true);
         } else {
            float quitX = btnX + halfW + gap;
            if (hit(mouseX, mouseY, quitX, btnY + rowH * 3.0F, halfW, btnH)) {
               mc.scheduleStop();
               cir.setReturnValue(true);
            }
         }
      }
   }

   @Unique
   private static void renderLogo(DrawContext context, float centerX, float y, float width, float height) {
      float logoX = centerX - width / 2.0F;
      AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(LOGO_ID);
      if (tex == null) {
         return;
      }
      int texId = tex.getGlId();
      if (texId <= 0) {
         return;
      }
      RenderSystem.bindTexture(texId);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      float t = (float)(System.currentTimeMillis() % 60000L) / 1000.0F;
      int accent = ClientPalette.pickTwo(ColorProvider.getColorVisualModules());
      float pulse = (float)(Math.sin(t * 2.2F) * 0.5F + 0.5F);
      AbstractTexture glow = MinecraftClient.getInstance().getTextureManager().getTexture(LOGO_GLOW_ID);
      if (glow != null) {
         int glowId = glow.getGlId();
         if (glowId > 0) {
            float gs = width * 1.25F;
            Builder.texture()
               .size(new SizeState(gs, gs))
               .radius(new QuadRadiusState(0.0F))
               .color(new QuadColorState(ColorProvider.setAlpha(accent, (int)(35.0F + pulse * 55.0F))))
               .smoothness(1.0F)
               .texture(0.0F, 0.0F, 1.0F, 1.0F, glowId)
               .build()
               .render(context.getMatrices().peek().getPositionMatrix(), centerX - gs / 2.0F, y + height / 2.0F - gs / 2.0F, 0.0F);
         }
      }
      dtDrawLogoTex(context, logoX, y, width, height, texId, -1);
      float bandW = width * 0.32F;
      float phase = (t * 0.22F) % 1.5F - 0.25F;
      float shineX = logoX + phase * width;
      int shineCol = ColorProvider.interpolateColor(accent, -1, 0.7F);
      Scissor.push();
      Scissor.scissor(MinecraftClient.getInstance().getWindow(), shineX, y, bandW, height);
      dtDrawLogoTex(context, logoX, y, width, height, texId, shineCol);
      Scissor.unset();
      Scissor.pop();
      float coreW = width * 0.1F;
      float coreX = shineX + (bandW - coreW) / 2.0F;
      Scissor.push();
      Scissor.scissor(MinecraftClient.getInstance().getWindow(), coreX, y, coreW, height);
      dtDrawLogoTex(context, logoX, y, width, height, texId, -1);
      Scissor.unset();
      Scissor.pop();
   }

   @Unique
   private static void dtDrawLogoTex(DrawContext context, float x, float y, float w, float h, int texId, int color) {
      Builder.texture()
         .size(new SizeState(w, h))
         .radius(new QuadRadiusState(0.0F))
         .color(new QuadColorState(color))
         .smoothness(1.0F)
         .texture(0.0F, 0.0F, 1.0F, 1.0F, texId)
         .build()
         .render(context.getMatrices().peek().getPositionMatrix(), x, y, 0.0F);
   }

   @Unique
   private static boolean hit(double mx, double my, float x, float y, float w, float h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }
}
