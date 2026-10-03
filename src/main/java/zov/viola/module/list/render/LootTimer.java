package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Vector4f;
import zov.viola.event.list.EventHUD;
import zov.viola.mixin.PlayerListHudAccessor;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.math.ProjectionUtil;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@ModuleInformation(
   moduleName = "LootTimer",
   moduleDesc = "Неймтаги таймеров над сундуками (Варден/Медь)",
   moduleCategory = ModuleCategory.RENDER
)
public class LootTimer extends Module {
   public final SliderSetting size = new SliderSetting("Размер", 9.0, 5.0, 16.0, 0.5);
   public final SliderSetting range = new SliderSetting(
      "Дистанция", 128.0, 16.0, 512.0, 1.0
   );
   public final BooleanSetting glowSoon = new BooleanSetting(
      D.k(
         new int[]{1252, 1107, 1247, 1268, 1219, 1112, 1238, 149, 1217, 1107, 1239, 149, 194, 46, 207, 132, 222, 1071, 1239, 1160},
         new int[]{254, 19, 239, 181}
      ),
      true
   );
   private static final long STALE_MS = 600000L;
   private final Map<BlockPos, LootTimer.Timer> bA2Vor = new HashMap<>();
   private World ia2j;

   @Subscribe
   private void onRender(EventHUD e) {
      if (this.mc.world != null && this.mc.player != null) {
         if (!this.s20zl9O()) {
            if (this.mc.world != this.ia2j) {
               this.bA2Vor.clear();
               this.ia2j = this.mc.world;
            }

            float tickDelta = e.getRenderTickCounter().getTickDelta(true);
            double rangeSq = this.range.getValue() * this.range.getValue();
            MsdfFont font = Fonts.SFBOLD.get();
            float fontSize = this.size.getFloatValue();
            long now = System.currentTimeMillis();

            for (Entity entity : this.mc.world.getEntities()) {
               if (h7f8d2(entity) && entity.isInvisible()) {
                  BlockPos container = this.gsElr9(entity);
                  if (container != null) {
                     double dx = entity.getX() - this.mc.player.getX();
                     double dy = entity.getY() - this.mc.player.getY();
                     double dz = entity.getZ() - this.mc.player.getZ();
                     if (!(dx * dx + dy * dy + dz * dz > rangeSq)) {
                        String text = this.aq5dVm(entity);
                        if (text != null && !text.isEmpty()) {
                           int seconds = u5KNQ(text);
                           if (seconds >= 0) {
                              double x = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
                              double y = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY())
                                 + (entity instanceof TextDisplayEntity ? 0.05 : 0.35);
                              double z = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
                              this.bA2Vor.put(container, new LootTimer.Timer(x, y, z, now + seconds * 1000L));
                           }
                        }
                     }
                  }
               }
            }

            this.bA2Vor.entrySet().removeIf(entryx -> now > ((LootTimer.Timer)entryx.getValue()).deadline + 600000L);

            for (Entry<BlockPos, LootTimer.Timer> entry : this.bA2Vor.entrySet()) {
               LootTimer.Timer timer = entry.getValue();
               double dx = timer.x - this.mc.player.getX();
               double dy = timer.y - this.mc.player.getY();
               double dz = timer.z - this.mc.player.getZ();
               if (!(dx * dx + dy * dy + dz * dz > rangeSq)) {
                  long remain = Math.max(0L, (timer.deadline - now) / 1000L);
                  String text = f6dJ4e(remain);
                  Vector2f pos = ProjectionUtil.project(timer.x, timer.y, timer.z);
                  if (pos.getX() != Float.MAX_VALUE && pos.getY() != Float.MAX_VALUE) {
                     float sw = this.mc.getWindow().getScaledWidth();
                     float sh = this.mc.getWindow().getScaledHeight();
                     if (!(pos.getX() < -60.0F) && !(pos.getX() > sw + 60.0F) && !(pos.getY() < -60.0F) && !(pos.getY() > sh + 60.0F)) {
                        int textColor = this.glowSoon.getValue() && remain <= 60L ? -43691 : ColorProvider.getAccent();
                        float boxWidth = Math.max(
                           font.getWidth("00:00:00", fontSize),
                           font.getWidth(text, fontSize)
                        );
                        float totalWidth = boxWidth + 4.0F;
                        float tagHeight = fontSize + 5.0F;
                        float bgX = pos.getX() - totalWidth / 2.0F;
                        float bgY = pos.getY() - tagHeight / 2.0F;
                        float textX = bgX + 2.0F + (boxWidth - font.getWidth(text, fontSize)) / 2.0F;
                        float textY = bgY + (tagHeight - fontSize) / 2.0F - 0.25F;
                        Vector4f radii = new Vector4f(4.0F, 4.0F, 4.0F, 4.0F);
                        DrawUtil.drawRoundBlur(bgX, bgY, totalWidth - 1.0F, tagHeight, radii, ColorProvider.rgba(200, 200, 200, 255.0F), 12.0F);
                        DrawUtil.drawRound(bgX, bgY, totalWidth - 1.0F, tagHeight, radii, ColorProvider.setAlpha(ColorProvider.getColorWindowBg(), 200));
                        DrawUtil.drawText(font, text, textX, textY, textColor, fontSize);
                     }
                  }
               }
            }
         }
      }
   }

   private static String f6dJ4e(long sec) {
      return sec >= 3600L
         ? String.format(
            "%d:%02d:%02d",
            sec / 3600L,
            sec % 3600L / 60L,
            sec % 60L
         )
         : String.format("%02d:%02d", sec / 60L, sec % 60L);
   }

   private boolean s20zl9O() {
      try {
         if (this.mc.inGameHud == null) {
            return false;
         } else {
            PlayerListHud hud = this.mc.inGameHud.getPlayerListHud();
            return hud == null ? false : ((PlayerListHudAccessor)hud).vio_isVisible();
         }
      } catch (Throwable var2) {
         return false;
      }
   }

   private static boolean h7f8d2(Entity entity) {
      return entity instanceof ArmorStandEntity ? true : entity instanceof TextDisplayEntity;
   }

   private String aq5dVm(Entity entity) {
      String raw = null;
      if (entity instanceof TextDisplayEntity display) {
         raw = display.getData().text().getString();
      } else if (entity.getCustomName() != null) {
         raw = entity.getCustomName().getString();
      }

      if (raw == null) {
         return null;
      } else {
         String clean = e1nv(raw).trim();
         return !isTimerText(clean) ? null : clean;
      }
   }

   public static boolean isTimerText(String s) {
      if (s != null && !s.isEmpty()) {
         if (!s.matches("\\d+([:.][0-9]+)*")) {
            return false;
         } else {
            String digits = s.replaceAll("[^\\d]", "");
            return !digits.isEmpty();
         }
      } else {
         return false;
      }
   }

   private static int u5KNQ(String s) {
      try {
         String[] parts = s.split("[:.]");
         int total = 0;

         for (String part : parts) {
            total = total * 60 + Integer.parseInt(part.trim());
         }

         return total;
      } catch (NumberFormatException var7) {
         return -1;
      }
   }

   private static String e1nv(String s) {
      StringBuilder sb = new StringBuilder(s.length());

      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if (c != 167 && c != '&') {
            sb.append(c);
         } else {
            i++;
         }
      }

      return sb.toString();
   }

   private BlockPos gsElr9(Entity entity) {
      BlockPos base = entity.getBlockPos();

      for (int dy = 2; dy >= -3; dy--) {
         BlockPos p = base.up(dy);
         if (this.caSk6(p)) {
            return p;
         }
      }

      for (int dx = -1; dx <= 1; dx++) {
         for (int dz = -1; dz <= 1; dz++) {
            if (dx != 0 || dz != 0) {
               BlockPos p = base.add(dx, -1, dz);
               if (this.caSk6(p)) {
                  return p;
               }
            }
         }
      }

      return null;
   }

   private boolean caSk6(BlockPos pos) {
      Block block = this.mc.world.getBlockState(pos).getBlock();
      return block instanceof ChestBlock || block instanceof BarrelBlock;
   }

   private static final class Timer {
      final double x;
      final double y;
      final double z;
      final long deadline;

      Timer(double x, double y, double z, long deadline) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.deadline = deadline;
      }
   }
}
