package zov.viola.ui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import zov.viola.module.list.combat.AutoSwap;
import zov.viola.obf.D;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

public class SwapRadialScreen extends Screen {
   private static final int RING_R = 54;
   private static final int SLOT_R = 21;
   private static final int CELL = 22;
   private final AutoSwap pa4n;
   private final int[] bKjtp = new int[5];
   private final int[] eDtf84 = new int[5];
   private double rzj9;
   private double zzwvl2h;
   private int gaZqM17 = -1;
   private final List<ItemStack> al57u = new ArrayList<>();
   private final int gFVjmW;

   public SwapRadialScreen(AutoSwap mod, int openKey) {
      super(Text.of("Auto Swap"));
      this.pa4n = mod;
      this.gFVjmW = openKey;
   }

   @Override
   protected void init() {
   }

   private void lC20() {
      int n = this.pa4n.getSwapCount();
      int cx = this.width / 2;
      int cy = this.height / 2 + 8;

      for (int i = 0; i < n; i++) {
         double ang = Math.toRadians(-90.0 + i * (360.0 / n));
         this.bKjtp[i] = cx + (int)Math.round(Math.cos(ang) * 54.0);
         this.eDtf84[i] = cy + (int)Math.round(Math.sin(ang) * 54.0);
      }
   }

   private int jec52pP(double mx, double my) {
      int n = this.pa4n.getSwapCount();

      for (int i = 0; i < n; i++) {
         if (Math.hypot(mx - this.bKjtp[i], my - this.eDtf84[i]) <= 28.0) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.rzj9 = mouseX;
      this.zzwvl2h = mouseY;
      context.fill(0, 0, this.width, this.height, 1426063360);
      if (this.gaZqM17 >= 0) {
         this.p8RsA(context, mouseX, mouseY);
      } else {
         this.gdSf(context, mouseX, mouseY);
      }

      super.render(context, mouseX, mouseY, delta);
   }

   private void gdSf(DrawContext context, int mouseX, int mouseY) {
      this.lC20();
      int accent = ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), 255);
      int cx = this.width / 2;
      int cy = this.height / 2 + 8;
      DrawUtil.drawCircle(cx, cy, 84.0F, 620756991);
      DrawUtil.drawRingArc(cx, cy, 76.0F, 1.5F, 0.0F, 360.0F, ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), 120));
      int hovered = this.jec52pP(mouseX, mouseY);
      int n = this.pa4n.getSwapCount();

      for (int i = 0; i < n; i++) {
         ItemStack slotStack = this.pa4n.getSlot(i);
         boolean hov = i == hovered;
         DrawUtil.drawCircle(this.bKjtp[i], this.eDtf84[i], 21.0F, hov ? ColorProvider.rgba(60, 60, 70, 235.0F) : ColorProvider.rgba(25, 25, 28, 225.0F));
         DrawUtil.drawRingArc(
            this.bKjtp[i], this.eDtf84[i], 19.0F, 1.5F, 0.0F, 360.0F, hov ? accent : ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), 90)
         );
         if (!slotStack.isEmpty()) {
            context.drawItem(slotStack, this.bKjtp[i] - 8, this.eDtf84[i] - 8);
            if (hov) {
               String del = "ПКМ \u2014 убрать";
               float dw = Fonts.SFMEDIUM.get().getWidth(del, 6.5F);
               DrawUtil.drawText(Fonts.SFMEDIUM.get(), del, this.bKjtp[i] - dw / 2.0F, this.eDtf84[i] + 21 + 3.0F, ColorProvider.setAlpha(-36752, 220), 6.5F);
            }
         } else {
            float pw = Fonts.SFBOLD.get().getWidth("+", 12.0F);
            DrawUtil.drawText(
               Fonts.SFBOLD.get(),
               "+",
               this.bKjtp[i] - pw / 2.0F,
               this.eDtf84[i] - 6.0F,
               ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), 220),
               12.0F
            );
         }
      }

      String hint = hovered >= 0 && !this.pa4n.getSlot(hovered).isEmpty()
         ? D.k(
            new int[]{1129, 1261, 1045, 1073, 1078, 1261, 1042, 82, 1101, 1172, 1050, 1088, 1103, 1255, 1129, 82, 8291, 143, 1131, 1088, 1095, 1168},
            new int[]{119, 175, 42, 114}
         )
         : D.k(
            new int[]{36, 154, 118, 247, 8347, 145, 1279, 1180, 1214, 1265, 1277, 1173, 1219, 145, 1266, 1175, 1210, 1157, 1265, 1250, 1229},
            new int[]{143, 177, 205, 215}
         );
      float hw = Fonts.SFMEDIUM.get().getWidth(hint, 7.5F);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), hint, cx - hw / 2.0F, cy + 54 + 42.0F, ColorProvider.setAlpha(-1, 200), 7.5F);
   }

   private void ee4sQ() {
      this.al57u.clear();
      if (this.client != null && this.client.player != null) {
         for (int i = 0; i <= 35; i++) {
            ItemStack st = this.client.player.getInventory().getStack(i);
            if (!st.isEmpty()) {
               this.al57u.add(st);
            }
         }
      }
   }

   private void p8RsA(DrawContext context, int mouseX, int mouseY) {
      this.ee4sQ();
      int cols = 9;
      int rows = (this.al57u.size() + cols - 1) / cols;
      int gridW = cols * 22;
      int startX = this.width / 2 - gridW / 2;
      int startY = Math.max(30, this.height / 2 - rows * 22 / 2);
      String title = D.k(
         new int[]{1138, 1090, 1054, 1241, 1056, 1073, 15, 1235, 1056, 1084, 1051, 1232, 1109, 1099, 15, 1240, 1115, 1094, 15, 1197, 1115, 1079, 1133, 1244},
         new int[]{96, 9, 47, 236}
      );
      float tw = Fonts.SFBOLD.get().getWidth(title, 8.0F);
      DrawUtil.drawText(Fonts.SFBOLD.get(), title, this.width / 2.0F - tw / 2.0F, startY - 14.0F, ColorProvider.setAlpha(-1, 230), 8.0F);

      for (int i = 0; i < this.al57u.size(); i++) {
         int col = i % cols;
         int row = i / cols;
         int x = startX + col * 22;
         int y = startY + row * 22;
         boolean hov = mouseX >= x && mouseX < x + 22 && mouseY >= y && mouseY < y + 22;
         DrawUtil.drawRound(x, y, 20.0F, 20.0F, 4.0F, hov ? ColorProvider.rgba(70, 70, 82, 240.0F) : ColorProvider.rgba(25, 25, 28, 225.0F));
         if (hov) {
            DrawUtil.drawRound(x, y, 20.0F, 20.0F, 4.0F, ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), 90));
         }

         context.drawItem(this.al57u.get(i), x + 3, y + 3);
      }

      String hint = D.k(
         new int[]{
            1131,
            1238,
            1218,
            107,
            8292,
            236,
            1251,
            1147,
            1095,
            1265,
            1262,
            1036,
            1096,
            1166,
            1170,
            107,
            80,
            236,
            8444,
            107,
            80,
            236,
            1217,
            1105,
            1132,
            236,
            241,
            107,
            53,
            159,
            157,
            107,
            8292,
            236,
            1251,
            1147,
            1095,
            1276,
            1258
         },
         new int[]{112, 204, 222, 75}
      );
      float hw = Fonts.SFMEDIUM.get().getWidth(hint, 7.0F);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), hint, this.width / 2.0F - hw / 2.0F, startY + rows * 22 + 10.0F, ColorProvider.setAlpha(-1, 180), 7.0F);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.gaZqM17 >= 0) {
         this.ee4sQ();
         int cols = 9;
         int rows = (this.al57u.size() + cols - 1) / cols;
         int gridW = cols * 22;
         int startX = this.width / 2 - gridW / 2;
         int startY = Math.max(30, this.height / 2 - rows * 22 / 2);

         for (int i = 0; i < this.al57u.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * 22;
            int y = startY + row * 22;
            if (mouseX >= x && mouseX < x + 22 && mouseY >= y && mouseY < y + 22) {
               if (button == 0) {
                  this.pa4n.setSlot(this.gaZqM17, this.al57u.get(i).copy());
                  this.gaZqM17 = -1;
                  return true;
               }

               if (button == 1) {
                  this.gaZqM17 = -1;
                  return true;
               }
            }
         }

         if (button == 1) {
            this.gaZqM17 = -1;
         }

         return true;
      } else {
         this.lC20();
         int idx = this.jec52pP(mouseX, mouseY);
         if (idx >= 0) {
            if (button == 0 && this.pa4n.getSlot(idx).isEmpty()) {
               this.gaZqM17 = idx;
               return true;
            }

            if (button == 1 && !this.pa4n.getSlot(idx).isEmpty()) {
               this.pa4n.clearSlot(idx);
               return true;
            }
         }

         return true;
      }
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.gaZqM17 < 0) {
         this.vksxLB2();
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         if (this.gaZqM17 >= 0) {
            this.gaZqM17 = -1;
         } else {
            this.sc2b();
         }

         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   @Override
   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      if (keyCode == this.gFVjmW && this.gaZqM17 < 0) {
         this.vksxLB2();
         return true;
      } else {
         return super.keyReleased(keyCode, scanCode, modifiers);
      }
   }

   private void vksxLB2() {
      this.lC20();
      int idx = this.jec52pP(this.rzj9, this.zzwvl2h);
      if (idx >= 0 && !this.pa4n.getSlot(idx).isEmpty()) {
         this.pa4n.choose(this.pa4n.getSlot(idx));
      }

      this.sc2b();
   }

   private void sc2b() {
      this.client.setScreen(null);
   }

   @Override
   public boolean shouldPause() {
      return false;
   }
}
