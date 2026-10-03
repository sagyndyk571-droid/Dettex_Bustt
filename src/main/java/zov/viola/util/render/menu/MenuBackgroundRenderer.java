package zov.viola.util.render.menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import zov.viola.util.render.builders.Builder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;

public class MenuBackgroundRenderer {
   private static final Identifier[] BG_IDS = new Identifier[]{
      Identifier.of("mre", "textures/gui/title/menu_photo.png"),
      Identifier.of("mre", "textures/gui/title/menu_bg.png"),
      Identifier.of("mre", "textures/gui/title/menu_bg_blue.png"),
      Identifier.of("mre", "textures/gui/title/menu_bg_black.png")
   };
   private static final String[] BG_NAMES = new String[]{
      "Фото", "Стандарт", "Синий", "Черный"
   };
   private static int index = 0;
   private static final float IMG_RATIO = 1.5F;

   public static void next() {
      index = (index + 1) % BG_IDS.length;
   }

   public static String currentName() {
      return BG_NAMES[index];
   }

   public static void render(DrawContext context) {
      int screenW = context.getScaledWindowWidth();
      int screenH = context.getScaledWindowHeight();
      if (screenW > 0 && screenH > 0) {
         try {
            AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(BG_IDS[index]);
            if (tex == null) {
               return;
            }

            int texId = tex.getGlId();
            if (texId <= 0) {
               return;
            }

            tex.setFilter(true, true);
            float screenRatio = (float)screenW / screenH;
            float drawW;
            float drawH;
            float offsetX;
            float offsetY;
            if (screenRatio > 1.5F) {
               drawW = screenW;
               drawH = screenW / 1.5F;
               offsetX = 0.0F;
               offsetY = (screenH - drawH) / 2.0F;
            } else {
               drawH = screenH;
               drawW = screenH * 1.5F;
               offsetX = (screenW - drawW) / 2.0F;
               offsetY = 0.0F;
            }

            Builder.texture()
               .size(new SizeState(drawW, drawH))
               .radius(new QuadRadiusState(0.0F))
               .color(new QuadColorState(-1))
               .texture(0.0F, 0.0F, 1.0F, 1.0F, texId)
               .smoothness(1.0F)
               .build()
               .render(context.getMatrices().peek().getPositionMatrix(), offsetX, offsetY, 0.0F);
         } catch (Exception var10) {
         }
      }
   }
}
