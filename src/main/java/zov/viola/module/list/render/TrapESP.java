package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventHUD;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.util.render.math.ProjectionUtil;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;

@ModuleInformation(
   moduleName = "TrapESP",
   moduleDesc = "Таймеры трапок и пластов",
   moduleCategory = ModuleCategory.RENDER
)
public class TrapESP extends Module {
   private static final Set<String> TRAP_BLOCKS = Set.of(
      "obsidian",
      "crying_obsidian",
      "piston",
      "sticky_piston",
      "anvil",
      "chipped_anvil",
      "damaged_anvil",
      "tnt",
      "respawn_anchor"
   );
   private static final long PLACE_TIME = 30000L;
   private static final long PISTON_TIME = 15000L;
   private static final long ANVIL_TIME = 30000L;

   public final SliderSetting radius = new SliderSetting(
      "Радиус", 48.0, 16.0, 96.0, 1.0
   );
   public final SliderSetting scale = new SliderSetting(
      "Размер", 1.0, 0.5, 2.0, 0.05
   );
   public final BooleanSetting sounds = new BooleanSetting(
      "Звуки", true
   );

   private final Map<BlockPos, Long> traps = new ConcurrentHashMap<>();

   private double radius() {
      return this.radius.getValue();
   }

   private boolean inRadius(double x, double z) {
      if (this.mc.player == null) {
         return false;
      }
      double dx = x - this.mc.player.getX();
      double dz = z - this.mc.player.getZ();
      double r = this.radius();
      return dx * dx + dz * dz <= r * r;
   }

   private void addTrap(BlockPos pos, long time) {
      if (this.mc.player != null && this.inRadius(pos.getX() + 0.5, pos.getZ() + 0.5)) {
         BlockPos key = pos.toImmutable();
         long now = System.currentTimeMillis();
         this.traps.merge(key, now + time, Math::max);
      }
   }

   private void onBlockState(BlockPos pos, BlockState state) {
      if (state.isAir()) {
         this.traps.remove(pos);
         return;
      }
      String path = Registries.BLOCK.getId(state.getBlock()).getPath();
      if (TRAP_BLOCKS.contains(path) || path.endsWith("_pressure_plate")) {
         this.addTrap(pos, PLACE_TIME);
      }
   }

   @Subscribe
   private void onPacket(EventPacket e) {
      if (e.getType() == EventPacket.Type.RECEIVE && this.mc.player != null && this.mc.world != null) {
         if (e.getPacket() instanceof BlockUpdateS2CPacket p) {
            this.onBlockState(p.getPos(), p.getState());
         } else if (e.getPacket() instanceof ChunkDeltaUpdateS2CPacket p) {
            p.visitUpdates(this::onBlockState);
         } else if (e.getPacket() instanceof PlaySoundS2CPacket p && this.sounds.getValue()) {
            String path = p.getSound().value().id().getPath();
            float pitch = p.getPitch();
            if (path.equals("block.piston.extend") && Math.abs(pitch - 0.5F) < 0.06F) {
               if (this.inRadius(p.getX(), p.getZ())) {
                  this.addTrap(new BlockPos((int) Math.floor(p.getX()), (int) Math.floor(p.getY()), (int) Math.floor(p.getZ())), PISTON_TIME);
               }
            } else if (path.equals("block.anvil.place")) {
               if (this.inRadius(p.getX(), p.getZ())) {
                  this.addTrap(new BlockPos((int) Math.floor(p.getX()), (int) Math.floor(p.getY()), (int) Math.floor(p.getZ())), ANVIL_TIME);
               }
            }
         }
      }
   }

   @Subscribe
   private void onTick(EventTick e) {
      if (this.mc.world == null) {
         this.traps.clear();
         return;
      }
      long now = System.currentTimeMillis();
      this.traps.entrySet().removeIf(entry -> entry.getValue() <= now || this.mc.world.getBlockState(entry.getKey()).isAir());
   }

   @Subscribe
   private void onHud(EventHUD e) {
      if (this.mc.player == null || this.mc.world == null || this.traps.isEmpty()) {
         return;
      }
      float s = (float) this.scale.getValue();
      MsdfFont font = Fonts.SFMEDIUM.get();
      float textSize = 8.0F * s;
      long now = System.currentTimeMillis();
      Vec3d self = this.mc.player.getEyePos();
      for (Map.Entry<BlockPos, Long> entry : this.traps.entrySet()) {
         long left = entry.getValue() - now;
         if (left <= 0) {
            continue;
         }
         BlockPos pos = entry.getKey();
         Vector2f sc = ProjectionUtil.project(pos.getX() + 0.5, pos.getY() + 1.4, pos.getZ() + 0.5);
         if (sc.getX() == Float.MAX_VALUE) {
            continue;
         }
         int sec = (int) Math.ceil(left / 1000.0);
         String text = sec + "c";
         float tw = font.getWidth(text, textSize);
         float w = Math.max(30.0F * s, tw + 10.0F * s);
         float h = 13.0F * s;
         float x = sc.getX() - w / 2.0F;
         float y = sc.getY() - h / 2.0F;
         int col = sec > 15 ? ColorProvider.rgba(80, 220, 120, 255.0F) : (sec > 5 ? ColorProvider.rgba(255, 210, 80, 255.0F) : ColorProvider.rgba(255, 90, 90, 255.0F));
         DrawUtil.drawRound(x, y, w, h, 3.0F, ColorProvider.rgba(10, 10, 14, 170.0F));
         DrawUtil.drawText(font, text, x + (w - tw) / 2.0F, y + (h - textSize) / 2.0F, col, textSize);
         double dist = Math.sqrt(pos.getSquaredDistance(self));
         if (dist < 64.0) {
            String d = (int) dist + "м";
            float dw = font.getWidth(d, 6.0F * s);
            DrawUtil.drawText(font, d, x + (w - dw) / 2.0F, y + h + 1.0F, ColorProvider.rgba(200, 200, 200, 200.0F), 6.0F * s);
         }
      }
   }

   @Override
   public void onEnable() {
      this.traps.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.traps.clear();
      super.onDisable();
   }
}
