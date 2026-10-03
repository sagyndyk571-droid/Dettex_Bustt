package zov.viola.module.list.render.hud;

import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.base.Instance;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;
import zov.viola.util.render.renderers.IRenderer;

public class NotificationsElement implements IMinecraft {
   private static final CopyOnWriteArrayList<NotificationsElement.Notification> notifications = new CopyOnWriteArrayList<>();
   private static final float TEXT_GAP = 3.0F;

   public void post(String name, boolean enabled) {
      NotificationsElement.Notification n = new NotificationsElement.Notification(name, enabled);
      n.moduleName = name;
      n.statusText = enabled
         ? "Включен"
         : "Выключен";
      n.duration = 1600L;
      notifications.add(0, n);
   }

   public void postWarning(String text) {
      notifications.add(0, new NotificationsElement.Notification(text, true, true));
   }

   public void postTotem(Text tagText, boolean enchanted) {
      MutableText full = Text.literal("Игрок ")
         .setStyle(Style.EMPTY.withColor(16777215))
         .append(tagText)
         .append(
            Text.literal(" потерял Тотем")
               .setStyle(Style.EMPTY.withColor(16777215))
         );
      NotificationsElement.Notification n = new NotificationsElement.Notification(full.getString(), true, true);
      n.isTotem = true;
      n.enchanted = enchanted;
      n.totemTagText = full;
      notifications.add(0, n);
   }

   public void render(DrawContext context) {
      Interface iface = Instance.get(Interface.class);
      float baseX = iface != null ? iface.getNotificationsCenterX() : MinecraftClient.getInstance().getWindow().getScaledWidth() / 2.0F;
      float baseY = iface != null ? iface.getNotificationsY() : MinecraftClient.getInstance().getWindow().getScaledHeight() / 2.0F + 20.0F;
      float offset = 0.0F;
      float scale = iface != null ? iface.getNotificationsScale() : 1.0F;
      boolean scaled = Math.abs(scale - 1.0F) > 0.001F;
      if (scaled) {
         IRenderer.DEFAULT_MATRIX.identity().translate(baseX, baseY, 0.0F).scale(scale, scale, 1.0F).translate(-baseX, -baseY, 0.0F);
      }

      for (NotificationsElement.Notification n : notifications) {
         if (System.currentTimeMillis() - n.time > n.duration && n.anim.getValue() <= 0.01) {
            notifications.remove(n);
         } else {
            boolean expiring = System.currentTimeMillis() - n.time > n.duration;
            n.anim.run(expiring ? 0.0F : 1.0F);
            double animValue = n.anim.getValue();
            if (!(animValue <= 0.01)) {
               float clampedAlpha = (float)Math.max(0.0, Math.min(1.0, animValue));
               int alphaInt = (int)(255.0F * clampedAlpha);
               float height = 14.5F;
               float iconSize = n.isWarning ? 8.0F : 9.0F;
               String fullText;
               String iconCode;
               int iconColor;
               if (n.isWarning) {
                  fullText = n.customText;
                  iconCode = "G";
                  iconColor = ColorProvider.rgba(255, 255, 255, (float)alphaInt);
               } else {
                  fullText = n.name;
                  iconCode = n.enabled ? "J" : "K";
                  iconColor = ColorProvider.rgba(255, 255, 255, (float)alphaInt);
               }

               float toggleW = 15.0F;
               float toggleH = 8.0F;
               float width;
               if (n.isWarning) {
                  float textWidth = Fonts.SFMEDIUM.get().getWidth(n.customText, 7.0F);
                  float iconWidth = Fonts.ICONS_NURIK.get().getWidth(iconCode, iconSize);
                  width = iconWidth + textWidth + 22.0F;
               } else if (n.isTotem) {
                  float textWidth = Fonts.SFMEDIUM.get().getWidth(n.totemTagText.getString(), 7.0F);
                  width = 10.0F + textWidth + 16.0F;
               } else {
                  String prefix = "Модуль";
                  float prefixWidth = Fonts.SFMEDIUM.get().getWidth(prefix, 7.0F);
                  float moduleWidth = Fonts.SFMEDIUM.get().getWidth(n.moduleName, 7.0F);
                  float statusWidth = Fonts.SFMEDIUM.get().getWidth(n.statusText, 7.0F);
                  float totalTextWidth = prefixWidth + 3.0F + moduleWidth + 3.0F + statusWidth;
                  width = totalTextWidth + toggleW + 16.0F;
               }

               float x = baseX - width / 2.0F;
               float y = baseY + offset;
               float cx = x + width / 2.0F;
               context.getMatrices().push();
               context.getMatrices().translate(cx, y + height / 2.0F, 0.0F);
               context.getMatrices().scale((float)animValue, (float)animValue, 1.0F);
               context.getMatrices().translate(-cx, -(y + height / 2.0F), 0.0F);
               if (iface != null) {
                  iface.drawNotifBackground(context, x, y, width, height, 6.0F, clampedAlpha);
               }

               if (n.isWarning && !n.isTotem) {
                  float warningTextWidth = Fonts.SFMEDIUM.get().getWidth(fullText, 7.0F);
                  float warningIconWidth = Fonts.ICONS_NURIK.get().getWidth(iconCode, iconSize);
                  float warningTotalWidth = warningIconWidth + 5.0F + warningTextWidth;
                  float warningIconX = x + (width - warningTotalWidth) / 2.0F;
                  DrawUtil.drawText(Fonts.ICONS_NURIK.get(), iconCode, warningIconX, y + 4.0F, iconColor, iconSize);
               }

               float textY = y + (height - 7.0F) / 2.0F - 0.5F;
               if (n.isTotem && n.totemTagText != null) {
                  float textWidth_full = Fonts.SFMEDIUM.get().getWidth(n.totemTagText.getString(), 7.0F);
                  float totalWidth = textWidth_full + 4.0F + 10.0F;
                  float textX = x + (width - totalWidth) / 2.0F;
                  DrawUtil.drawText(Fonts.SFMEDIUM.get(), n.totemTagText, textX, textY, 7.0F, alphaInt);
                  context.getMatrices().push();
                  context.getMatrices().translate(textX + textWidth_full + 4.0F, y + 2.5F, 0.0F);
                  context.getMatrices().scale(0.6F, 0.6F, 1.0F);
                  context.drawItem(new ItemStack(Items.TOTEM_OF_UNDYING), 0, 0);
                  context.getMatrices().pop();
               } else if (n.isWarning) {
                  float warningTextWidth = Fonts.SFMEDIUM.get().getWidth(fullText, 7.0F);
                  float warningIconWidth = Fonts.ICONS_NURIK.get().getWidth(iconCode, iconSize);
                  float warningTotalWidth = warningIconWidth + 5.0F + warningTextWidth;
                  float warningTextX = x + (width - warningTotalWidth) / 2.0F + warningIconWidth + 5.0F;
                  DrawUtil.drawText(Fonts.SFMEDIUM.get(), fullText, warningTextX, textY, ColorProvider.rgba(255, 255, 255, (float)alphaInt), 7.0F);
               } else {
                  String prefix = "Модуль";
                  String moduleName = n.moduleName;
                  String status = n.statusText;
                  float prefixWidth = Fonts.SFMEDIUM.get().getWidth(prefix, 7.0F);
                  float moduleWidth = Fonts.SFMEDIUM.get().getWidth(moduleName, 7.0F);
                  float statusWidth = Fonts.SFMEDIUM.get().getWidth(status, 7.0F);
                  float totalTextWidth = prefixWidth + 3.0F + moduleWidth + 3.0F + statusWidth;
                  int statusColor = n.enabled ? ColorProvider.rgba(0, 255, 100, (float)alphaInt) : ColorProvider.rgba(255, 80, 80, (float)alphaInt);
                  float totalModuleWidth = totalTextWidth + 5.0F + toggleW;
                  float startX = x + (width - totalModuleWidth) / 2.0F;
                  DrawUtil.drawText(Fonts.SFMEDIUM.get(), prefix, startX, textY, ColorProvider.rgba(255, 255, 255, (float)alphaInt), 7.0F);
                  float currentX = startX + (prefixWidth + 3.0F);
                  DrawUtil.drawText(Fonts.SFMEDIUM.get(), moduleName, currentX, textY, ColorProvider.rgba(255, 255, 255, (float)alphaInt), 7.0F);
                  currentX += moduleWidth + 3.0F;
                  DrawUtil.drawText(Fonts.SFMEDIUM.get(), status, currentX, textY, statusColor, 7.0F);
                  float tX = startX + totalTextWidth + 5.0F;
                  float tY = y + (height - toggleH) / 2.0F;
                  float anim = n.enabled ? 1.0F : 0.0F;
                  if (iface != null) {
                     iface.drawNewToggle(tX, tY, toggleW, toggleH, n.enabled, anim, alphaInt);
                  }

                  int barColor = n.enabled ? ColorProvider.rgba(0, 255, 100, (float)alphaInt) : ColorProvider.rgba(255, 80, 80, (float)alphaInt);
                  float barH = 2.0F;
                  float barY = y + height - barH - 1.0F;
                  float barPad = 6.0F;
                  float barW = width - barPad * 2.0F;
                  float remaining = 1.0F - (float)(System.currentTimeMillis() - n.time) / (float)n.duration;
                  remaining = Math.max(0.0F, Math.min(1.0F, remaining));
                  DrawUtil.drawRound(
                     x + barPad, barY, barW, barH, barH / 2.0F, ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), (int)(alphaInt * 0.35F))
                  );
                  if (remaining > 0.01F) {
                     DrawUtil.drawRound(x + barPad, barY, barW * remaining, barH, barH / 2.0F, barColor);
                  }
               }

               context.getMatrices().pop();
               offset += (height + 3.0F) * clampedAlpha;
            }
         }
      }

      if (scaled) {
         IRenderer.DEFAULT_MATRIX.identity();
      }
   }

   private static class Notification {
      String name;
      boolean enabled;
      long time;
      long duration = 1000L;
      Animation anim = new Animation(Easing.BACK_OUT, 300L);
      boolean isWarning = false;
      String customText;
      boolean isTotem = false;
      boolean enchanted = false;
      Text totemTagText = null;
      String moduleName = "";
      String statusText = "";

      public Notification(String name, boolean enabled) {
         this.name = name;
         this.enabled = enabled;
         this.time = System.currentTimeMillis();
      }

      public Notification(String customText, boolean enabled, boolean isWarning) {
         this.customText = customText;
         this.enabled = enabled;
         this.isWarning = isWarning;
         this.time = System.currentTimeMillis();
         this.duration = 2000L;
      }
   }
}
