package zov.viola.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector4f;
import zov.viola.module.settings.impl.Theme;
import zov.viola.module.settings.impl.ThemeManager;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.cursor.CursorManager;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.math.Scissor;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class ThemeEditor implements IMinecraft {
   private static final String[] SLOTS = new String[]{
      "Тема клиента",
      "Цвет иконок",
      "Неактивный текст",
      "Активный текст",
      "Фон Interface",
      "Фон ClickGui"
   };
   private static final float W = 124.0F;
   private static final float TAB = 18.0F;
   private static final float ROW_H = 15.0F;
   private static final float PAD = 5.0F;
   private static final float BTN_H = 14.0F;
   private static final float BTN_GAP = 4.0F;
   private static final int[] DEFAULTS = new int[]{-12816651, -12816651, -8485216, -1513240, -16777216, -16777216};
   private static final float SV_SIZE = 58.0F;
   private static final float HUE_W = 7.0F;
   private static final float HUE_GAP = 4.0F;
   private static final float PICKER_PAD = 5.0F;
   private static final float PICKER_W = 69.0F;
   private static final File DIR = new File("viola");
   private static final File FILE = new File(DIR, "theme.json");
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final Animation rBv4Izw = new Animation(Easing.QUINTIC_OUT, 380L);
   private final Animation uhuz = new Animation(Easing.QUINTIC_OUT, 240L);
   private final Animation y4q2Aay = new Animation(Easing.QUINTIC_OUT, 340L);
   private boolean r40j2;
   private int gvwEh = -1;
   private final float[] bfOHe = new float[3];
   private boolean rmwrk;
   private boolean eul439;
   private float fwxmWm7;
   private float efTf3;
   private final int[] ajfubW = new int[SLOTS.length];
   private float vG4j;
   private float blW0;
   private float ab85;

   public void resetAppear() {
      this.y4q2Aay.reset(0.0F);
   }

   public ThemeEditor() {
      Theme t = ThemeManager.getInstance().getCurrentTheme();
      this.ajfubW[0] = t.colorClient;
      this.ajfubW[1] = t.colorIcons;
      this.ajfubW[2] = t.colorInactiveText;
      this.ajfubW[3] = t.colorText;
      this.ajfubW[4] = t.colorInterfaceBg;
      this.ajfubW[5] = t.colorClickGui;
      this.load();
      this.tuewhZh();
   }

   private void tuewhZh() {
      Theme t = ThemeManager.getInstance().getCurrentTheme();
      t.setAccent(this.ajfubW[0]);
      t.colorIcons = this.ajfubW[1];
      t.colorInactiveText = this.ajfubW[2];
      t.colorText = this.ajfubW[3];
      t.colorInterfaceBg = this.ajfubW[4];
      t.colorClickGui = this.ajfubW[5];
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      int sw = mc.getWindow().getScaledWidth();
      int sh = mc.getWindow().getScaledHeight();
      this.rBv4Izw.run(this.r40j2);
      float ep = this.rBv4Izw.getValue();
      float contentH = 10.0F + SLOTS.length * 15.0F + 4.0F + 14.0F;
      this.ab85 = 18.0F + contentH * ep;
      this.vG4j = sw - 124.0F - 4.0F;
      this.blW0 = sh - this.ab85 - 4.0F;
      this.y4q2Aay.run(true);
      float appear = this.y4q2Aay.getValue();
      float appearShift = (1.0F - appear) * (this.ab85 + 14.0F);
      this.blW0 += appearShift;
      Vector4f topRound = new Vector4f(8.0F, 8.0F, 0.0F, 0.0F);
      DrawUtil.drawRoundBlur(this.vG4j, this.blW0, 124.0F, this.ab85, topRound, ColorProvider.rgba(200, 200, 200, 255.0F), 14.0F);
      DrawUtil.drawRound(
         this.vG4j - 0.5F, this.blW0 - 0.5F, 125.0F, this.ab85 + 1.0F, new Vector4f(8.5F, 8.5F, 0.0F, 0.0F), ColorProvider.rgba(48, 66, 122, 70.0F)
      );
      DrawUtil.drawRound(this.vG4j, this.blW0, 124.0F, this.ab85, topRound, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), 130));
      DrawUtil.drawRound(this.vG4j, this.blW0, 124.0F, 18.0F, topRound, ColorProvider.setAlpha(ColorProvider.getColorHeaderBg(), 110));
      float titleSize = 8.5F;
      float titleW = Fonts.SFREGULAR.get().getWidth("Theme", titleSize);
      float arrowW = 7.0F;
      float groupGap = 5.0F;
      float groupTotal = titleW + groupGap + arrowW;
      float groupX = this.vG4j + (124.0F - groupTotal) / 2.0F;
      DrawUtil.drawText(Fonts.SFREGULAR.get(), "Theme", groupX, this.blW0 + (18.0F - titleSize) / 2.0F, ColorProvider.rgba(255, 255, 255, 255.0F), titleSize);
      this.hdc91(groupX + titleW + groupGap + arrowW / 2.0F, this.blW0 + 9.0F, !this.r40j2, ColorProvider.setAlpha(ColorProvider.getColorIcons(), 255));
      boolean tabHover = HoverUtil.isHovered(mouseX, mouseY, this.vG4j, this.blW0, 124.0, 18.0);
      if (tabHover) {
         CursorManager.requestHand();
      }

      if (ep > 0.01F) {
         Scissor.push();
         Scissor.setFromComponentCoordinates((double)this.vG4j, (double)(this.blW0 + 18.0F), 124.0, (double)(this.ab85 - 18.0F));
         float rowY = this.blW0 + 18.0F + 5.0F;

         for (int i = 0; i < SLOTS.length; i++) {
            this.v433v(i, this.vG4j + 4.0F, rowY, 116.0F, mouseX, mouseY, ep);
            rowY += 15.0F;
         }

         float btnX = this.vG4j + 4.0F;
         float btnY = rowY + 4.0F;
         float btnW = 116.0F;
         boolean btnHover = HoverUtil.isHovered(mouseX, mouseY, btnX, btnY, btnW, 14.0);
         if (btnHover && ep > 0.9F) {
            CursorManager.requestHand();
         }

         int btnBg = btnHover ? ColorProvider.rgba(60, 78, 140, (float)((int)(110.0F * ep))) : ColorProvider.rgba(48, 66, 122, (float)((int)(70.0F * ep)));
         DrawUtil.drawRound(btnX, btnY, btnW, 14.0F, 3.0F, btnBg);
         String label = "Сбросить";
         float lw = Fonts.SFREGULAR.get().getWidth(label, 7.0F);
         DrawUtil.drawText(
            Fonts.SFREGULAR.get(), label, btnX + (btnW - lw) / 2.0F, btnY + 3.5F, ColorProvider.rgba(230, 234, 245, (float)((int)(255.0F * ep))), 7.0F
         );
         Scissor.unset();
         Scissor.pop();
      }

      if (this.rmwrk) {
         this.bfOHe[1] = ajmf((mouseX - this.fwxmWm7) / 58.0F);
         this.bfOHe[2] = 1.0F - ajmf((mouseY - this.efTf3) / 58.0F);
         this.u1pG();
      } else if (this.eul439) {
         this.bfOHe[0] = ajmf((mouseY - this.efTf3) / 58.0F);
         this.u1pG();
      }

      this.uhuz.run(this.gvwEh >= 0 && this.r40j2);
      if (this.uhuz.getValue() > 0.01F) {
         this.u2DQUV(mouseX, mouseY, this.uhuz.getValue());
      }
   }

   private void v433v(int slot, float rx, float ry, float rw, int mouseX, int mouseY, float ep) {
      boolean hov = HoverUtil.isHovered(mouseX, mouseY, rx, ry, rw, 15.0);
      boolean active = this.gvwEh == slot;
      if ((hov || active) && ep > 0.9F) {
         CursorManager.requestHand();
      }

      if (hov || active) {
         DrawUtil.drawRound(rx, ry, rw, 13.5F, 3.0F, ColorProvider.rgba(60, 78, 140, (float)((int)(55.0F * ep))));
      }

      int textColor = active ? ColorProvider.getColorText() : ColorProvider.rgba(210, 214, 230, 255.0F);
      DrawUtil.drawText(Fonts.SFREGULAR.get(), SLOTS[slot], rx + 4.0F, ry + 4.0F, ColorProvider.setAlpha(textColor, (int)(255.0F * ep)), 7.0F);
      float sw = 11.0F;
      float sx = rx + rw - sw - 3.0F;
      float sy = ry + (15.0F - sw) / 2.0F - 0.75F;
      DrawUtil.drawRound(sx - 0.75F, sy - 0.75F, sw + 1.5F, sw + 1.5F, 3.0F, ColorProvider.rgba(255, 255, 255, (float)((int)(60.0F * ep))));
      DrawUtil.drawRound(sx, sy, sw, sw, 2.5F, ColorProvider.setAlpha(this.ajfubW[slot], (int)(255.0F * ep)));
   }

   private void u2DQUV(int mouseX, int mouseY, float anim) {
      int a = (int)(255.0F * anim);
      float px = this.fwxmWm7;
      float py = this.efTf3;
      DrawUtil.drawRoundBlur(px - 5.0F, py - 5.0F, 79.0F, 68.0F, 5.0F, ColorProvider.rgba(200, 200, 200, (float)((int)(255.0F * anim))), 12.0F);
      DrawUtil.drawRound(px - 5.0F, py - 5.0F, 79.0F, 68.0F, 5.0F, ColorProvider.setAlpha(ColorProvider.getColorClickGui(), (int)(140.0F * anim)));
      int cHue = ColorProvider.setAlpha(Color.HSBtoRGB(this.bfOHe[0], 1.0F, 1.0F), a);
      int white = ColorProvider.rgba(255, 255, 255, (float)a);
      int clearWhite = ColorProvider.rgba(255, 255, 255, 0.0F);
      int black = ColorProvider.rgba(0, 0, 0, (float)a);
      int clearBlack = ColorProvider.rgba(0, 0, 0, 0.0F);
      DrawUtil.drawRound(px, py, 58.0F, 58.0F, 2.0F, cHue);
      DrawUtil.drawRound(px, py, 58.0F, 58.0F, 2.0F, white, white, clearWhite, clearWhite);
      DrawUtil.drawRound(px, py, 58.0F, 58.0F, 2.0F, clearBlack, black, black, clearBlack);
      float scx = px + this.bfOHe[1] * 58.0F;
      float scy = py + (1.0F - this.bfOHe[2]) * 58.0F;
      DrawUtil.drawRound(scx - 2.5F, scy - 2.5F, 5.0F, 5.0F, 2.5F, ColorProvider.rgba(0, 0, 0, (float)((int)(180.0F * anim))));
      DrawUtil.drawRound(scx - 1.75F, scy - 1.75F, 3.5F, 3.5F, 1.75F, white);
      float hueX = px + 58.0F + 4.0F;

      for (float i = 0.0F; i <= 58.0F; i += 0.5F) {
         DrawUtil.drawRound(hueX, py + i, 7.0F, 1.0F, 0.0F, ColorProvider.setAlpha(Color.HSBtoRGB(i / 58.0F, 1.0F, 1.0F), a));
      }

      float hcy = py + this.bfOHe[0] * 58.0F;
      DrawUtil.drawRound(hueX - 1.5F, hcy - 2.0F, 10.0F, 4.0F, 2.0F, ColorProvider.rgba(0, 0, 0, (float)((int)(180.0F * anim))));
      DrawUtil.drawRound(hueX - 0.5F, hcy - 1.0F, 8.0F, 2.0F, 1.0F, white);
   }

   private void hdc91(float cx, float cy, boolean up, int color) {
      float w = 7.0F;
      float h = 4.0F;
      int rows = 8;
      float top = cy - h / 2.0F;

      for (int i = 0; i < rows; i++) {
         float frac = (float)i / (rows - 1);
         float rowW = up ? w * frac : w * (1.0F - frac);
         float ry = top + frac * h;
         DrawUtil.drawRound(cx - rowW / 2.0F, ry, rowW, h / rows + 0.5F, 0.0F, color);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (HoverUtil.isHovered(mouseX, mouseY, this.vG4j, this.blW0, 124.0, 18.0) && button == 0) {
         this.r40j2 = !this.r40j2;
         if (!this.r40j2) {
            this.gvwEh = -1;
         }

         return true;
      } else if (this.rBv4Izw.getValue() < 0.5F) {
         return HoverUtil.isHovered(mouseX, mouseY, this.vG4j, this.blW0, 124.0, this.ab85);
      } else {
         if (this.gvwEh >= 0 && this.uhuz.getValue() > 0.5F) {
            if (HoverUtil.isHovered(mouseX, mouseY, this.fwxmWm7, this.efTf3, 58.0, 58.0) && button == 0) {
               this.rmwrk = true;
               this.bfOHe[1] = ajmf((float)(mouseX - this.fwxmWm7) / 58.0F);
               this.bfOHe[2] = 1.0F - ajmf((float)(mouseY - this.efTf3) / 58.0F);
               this.u1pG();
               return true;
            }

            if (HoverUtil.isHovered(mouseX, mouseY, this.fwxmWm7 + 58.0F + 4.0F - 1.0F, this.efTf3, 9.0, 58.0) && button == 0) {
               this.eul439 = true;
               this.bfOHe[0] = ajmf((float)(mouseY - this.efTf3) / 58.0F);
               this.u1pG();
               return true;
            }

            boolean insidePicker = HoverUtil.isHovered(mouseX, mouseY, this.fwxmWm7 - 5.0F, this.efTf3 - 5.0F, 79.0, 68.0);
            if (insidePicker) {
               return true;
            }
         }

         float btnY = this.blW0 + 18.0F + 5.0F + SLOTS.length * 15.0F + 4.0F;
         if (HoverUtil.isHovered(mouseX, mouseY, this.vG4j + 4.0F, btnY, 116.0, 14.0) && button == 0) {
            this.nm5oE1();
            return true;
         } else {
            float rowY = this.blW0 + 18.0F + 5.0F;

            for (int i = 0; i < SLOTS.length; i++) {
               if (HoverUtil.isHovered(mouseX, mouseY, this.vG4j + 4.0F, rowY, 116.0, 15.0) && button == 0) {
                  this.gvwEh = this.gvwEh == i ? -1 : i;
                  if (this.gvwEh >= 0) {
                     this.r6L7(i);
                     this.fwxmWm7 = this.vG4j - 69.0F - 5.0F - 4.0F;
                     this.efTf3 = rowY + 7.5F - 29.0F;
                     float sh = mc.getWindow().getScaledHeight();
                     this.efTf3 = Math.max(7.0F, Math.min(this.efTf3, sh - 58.0F - 5.0F - 2.0F));
                  }

                  return true;
               }

               rowY += 15.0F;
            }

            return HoverUtil.isHovered(mouseX, mouseY, this.vG4j, this.blW0, 124.0, this.ab85);
         }
      }
   }

   public void mouseReleased(double mouseX, double mouseY, int button) {
      if (this.rmwrk || this.eul439) {
         this.save();
      }

      this.rmwrk = false;
      this.eul439 = false;
   }

   private void nm5oE1() {
      System.arraycopy(DEFAULTS, 0, this.ajfubW, 0, this.ajfubW.length);
      this.gvwEh = -1;
      this.tuewhZh();
      this.save();
   }

   public static void applyStartupTheme() {
      int[] c = (int[])DEFAULTS.clone();
      if (FILE.exists()) {
         try {
            JsonObject json = JsonParser.parseString(Files.readString(FILE.toPath())).getAsJsonObject();
            if (json.has("colors")) {
               JsonArray arr = json.getAsJsonArray("colors");

               for (int i = 0; i < Math.min(arr.size(), c.length); i++) {
                  c[i] = arr.get(i).getAsInt();
               }
            }
         } catch (Exception var4) {
         }
      }

      Theme t = ThemeManager.getInstance().getCurrentTheme();
      t.setAccent(c[0]);
      t.colorIcons = c[1];
      t.colorInactiveText = c[2];
      t.colorText = c[3];
      t.colorInterfaceBg = c[4];
      t.colorClickGui = c[5];
   }

   private void u1pG() {
      if (this.gvwEh >= 0) {
         this.ajfubW[this.gvwEh] = Color.HSBtoRGB(this.bfOHe[0], this.bfOHe[1], this.bfOHe[2]) | 0xFF000000;
         this.tuewhZh();
      }
   }

   private void r6L7(int slot) {
      Color c = new Color(this.ajfubW[slot], true);
      Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), this.bfOHe);
   }

   private static float ajmf(double v) {
      return (float)Math.max(0.0, Math.min(1.0, v));
   }

   public void save() {
      try {
         if (!DIR.exists()) {
            DIR.mkdirs();
         }

         JsonObject json = new JsonObject();
         JsonArray arr = new JsonArray();

         for (int c : this.ajfubW) {
            arr.add(c);
         }

         json.add("colors", arr);
         Files.writeString(FILE.toPath(), GSON.toJson(json));
      } catch (IOException var7) {
         var7.printStackTrace();
      }
   }

   public void load() {
      if (FILE.exists()) {
         try {
            JsonObject json = JsonParser.parseString(Files.readString(FILE.toPath())).getAsJsonObject();
            if (json.has("colors")) {
               JsonArray arr = json.getAsJsonArray("colors");

               for (int i = 0; i < Math.min(arr.size(), this.ajfubW.length); i++) {
                  this.ajfubW[i] = arr.get(i).getAsInt();
               }
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }
   }
}
