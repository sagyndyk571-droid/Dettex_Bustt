package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventEntitySpawn;
import zov.viola.event.list.EventHUD;
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
   moduleName = "UseIndicator",
   moduleDesc = "Иконка используемого предмета и вспышки фейерверков",
   moduleCategory = ModuleCategory.RENDER
)
public class UseIndicator extends Module {
   private static final long FIREWORK_LIFETIME_MS = 1000L;
   private static final int MAX_FIREWORKS = 64;
   private final BooleanSetting aWxj524 = new BooleanSetting(
      "У игроков", true
   );
   private final BooleanSetting dvt6s1W = new BooleanSetting(
      "Фейерверки", true
   );
   private final SliderSetting eg31 = new SliderSetting(
      "Масштаб", 1.0, 0.3, 2.0, 0.05
   );
   private final List<UseIndicator.Spawn> z5S1e = new ArrayList<>();

   @Subscribe
   private void onEntitySpawn(EventEntitySpawn e) {
      if (this.dvt6s1W.getValue()) {
         if (e.getEntity() instanceof FireworkRocketEntity rocket) {
            ItemStack var7 = new ItemStack(Items.FIREWORK_ROCKET);
            synchronized (this.z5S1e) {
               this.z5S1e.add(new UseIndicator.Spawn(rocket.getPos(), var7, System.currentTimeMillis() + 1000L));

               while (this.z5S1e.size() > 64) {
                  this.z5S1e.remove(0);
               }
            }
         }
      }
   }

   @Subscribe
   private void onHud(EventHUD e) {
      if (this.mc.world != null && this.mc.player != null) {
         DrawContext ctx = e.getDrawContext();
         float tickDelta = this.mc.getRenderTickCounter().getTickDelta(true);
         float sc = this.eg31.getFloatValue();
         if (this.aWxj524.getValue()) {
            for (PlayerEntity player : this.mc.world.getPlayers()) {
               if (this.dWvImE(player)) {
                  ItemStack stack = this.xltb6I(player);
                  if (!stack.isEmpty()) {
                     Vec3d pos = player.getLerpedPos(tickDelta).add(0.0, player.getHeight() / 2.0, 0.0);
                     Vector2f screen = ProjectionUtil.project(pos, tickDelta);
                     if (screen.getX() != Float.MAX_VALUE) {
                        this.wi8Lh(ctx, stack, screen.getX(), screen.getY(), sc);
                     }
                  }
               }
            }
         }

         if (this.dvt6s1W.getValue()) {
            long now = System.currentTimeMillis();
            synchronized (this.z5S1e) {
               Iterator<UseIndicator.Spawn> it = this.z5S1e.iterator();

               while (it.hasNext()) {
                  UseIndicator.Spawn spawn = it.next();
                  if (now >= spawn.expireAt()) {
                     it.remove();
                  } else {
                     Vector2f screen = ProjectionUtil.project(spawn.pos(), tickDelta);
                     if (screen.getX() != Float.MAX_VALUE) {
                        this.wi8Lh(ctx, spawn.stack(), screen.getX(), screen.getY(), sc);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean dWvImE(LivingEntity entity) {
      if (!entity.isAlive() || !entity.isUsingItem()) {
         return false;
      } else if (entity == this.mc.player && this.mc.options.getPerspective().isFirstPerson()) {
         return false;
      } else {
         return entity.isInvisible() && entity != this.mc.player ? false : !this.xltb6I(entity).isEmpty();
      }
   }

   private ItemStack xltb6I(LivingEntity entity) {
      if (!entity.isUsingItem()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack active = entity.getActiveItem();
         if (this.w3v4idn(active)) {
            return active;
         } else {
            Hand hand = entity.getActiveHand();
            if (hand != null) {
               ItemStack handStack = entity.getStackInHand(hand);
               if (this.w3v4idn(handStack)) {
                  return handStack;
               }
            }

            return ItemStack.EMPTY;
         }
      }
   }

   private boolean w3v4idn(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         UseAction action = stack.getUseAction();
         return action == UseAction.EAT || action == UseAction.DRINK;
      } else {
         return false;
      }
   }

   private void wi8Lh(DrawContext ctx, ItemStack stack, float x, float y, float sc) {
      float radius = 9.0F * sc;
      DrawUtil.drawRound(x - radius, y - radius, radius * 2.0F, radius * 2.0F, radius, ColorProvider.rgba(16, 16, 20, 205.0F));
      float itemSize = 16.0F * sc;
      ctx.getMatrices().push();
      ctx.getMatrices().translate(x - itemSize / 2.0F, y - itemSize / 2.0F, 300.0F);
      ctx.getMatrices().scale(sc, sc, 1.0F);
      ctx.drawItem(stack, 0, 0);
      ctx.getMatrices().pop();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      synchronized (this.z5S1e) {
         this.z5S1e.clear();
      }
   }

   private record Spawn(Vec3d pos, ItemStack stack, long expireAt) {


      

      

      
   }
}
