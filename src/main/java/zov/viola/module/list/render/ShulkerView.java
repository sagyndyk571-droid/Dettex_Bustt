package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import zov.viola.event.list.EventHUD;
import zov.viola.event.list.EventHandledScreen;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.render.math.ProjectionUtil;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@ModuleInformation(
   moduleName = "ShulkerView",
   moduleDesc = "Показывает содержимое шалкеров и контейнеров",
   moduleCategory = ModuleCategory.RENDER
)
public class ShulkerView extends Module {
   private final BooleanSetting jcvfkx = new BooleanSetting(
      "В инвентаре", true
   );
   private final BooleanSetting vx50l = new BooleanSetting("На земле", true);
   private final SliderSetting xd5p = new SliderSetting(
      "Масштаб", 0.75, 0.4, 1.5, 0.05
   );
   private static final int CELL = 18;
   private static final int COLS = 9;
   private static final float PAD = 3.0F;

   @Subscribe
   private void onHandledScreen(EventHandledScreen e) {
      if (this.jcvfkx.getValue()) {
         Slot slot = e.getSlotHover();
         if (slot != null && slot.hasStack()) {
            List<ItemStack> contents = this.qZdBf33(slot.getStack());
            if (!contents.isEmpty()) {
               float sc = this.xd5p.getFloatValue();
               float w = 168.0F * sc;
               float h = (this.noCH(contents.size()) * 18 + 6.0F) * sc;
               float x = e.getMouseX() + 8;
               float y = e.getMouseY() + 8;
               if (x + w > this.mc.getWindow().getScaledWidth()) {
                  x = e.getMouseX() - w - 8.0F;
               }

               if (y + h > this.mc.getWindow().getScaledHeight()) {
                  y = e.getMouseY() - h - 8.0F;
               }

               this.d83hw(e.getDrawContext(), contents, x, y, sc);
            }
         }
      }
   }

   @Subscribe
   private void onHud(EventHUD e) {
      if (this.vx50l.getValue() && this.mc.world != null) {
         for (Entity entity : this.mc.world.getEntities()) {
            if (entity instanceof ItemEntity item) {
               List<ItemStack> contents = this.qZdBf33(item.getStack());
               if (!contents.isEmpty()) {
                  Vector2f pos = ProjectionUtil.project(entity.getPos().add(0.0, entity.getHeight() + 0.3, 0.0));
                  if (pos.getX() != Float.MAX_VALUE) {
                     float sc = this.xd5p.getFloatValue() * 0.6F;
                     float w = 168.0F * sc;
                     this.d83hw(e.getDrawContext(), contents, pos.getX() - w / 2.0F, pos.getY(), sc);
                  }
               }
            }
         }
      }
   }

   private List<ItemStack> qZdBf33(ItemStack stack) {
      ContainerComponent component = stack.get(DataComponentTypes.CONTAINER);
      return component != null ? component.stream().toList() : List.of();
   }

   private int noCH(int count) {
      return Math.max(1, (int)Math.ceil(count / 9.0));
   }

   private void d83hw(DrawContext ctx, List<ItemStack> stacks, float x, float y, float sc) {
      float w = 168.0F * sc;
      float h = (this.noCH(stacks.size()) * 18 + 6.0F) * sc;
      DrawUtil.drawRound(x, y, w, h, 4.0F, ColorProvider.rgba(18, 18, 24, 225.0F));
      ctx.getMatrices().push();
      ctx.getMatrices().translate(x + 3.0F * sc, y + 3.0F * sc, 400.0F);
      ctx.getMatrices().scale(sc, sc, 1.0F);
      int i = 0;

      for (ItemStack stack : stacks) {
         int cx = i % 9 * 18;
         int cy = i / 9 * 18;
         ctx.drawItem(stack, cx, cy);
         ctx.drawStackOverlay(this.mc.textRenderer, stack, cx, cy);
         i++;
      }

      ctx.getMatrices().pop();
   }
}
