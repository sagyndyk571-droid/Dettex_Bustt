package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.event.list.EventWorldRender;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "XRay",
   moduleDesc = "Поиск ценных блоков: сканирование чанков и слежение за пакетами мира",
   moduleCategory = ModuleCategory.RENDER
)
public class XRay extends Module {
   private static XRay yqab4;
   public final ModeSetting mode = new ModeSetting(
      "Режим",
      "Обновление",
      "Обновление",
      "Постоянный"
   );
   public final BooleanSetting debris = new BooleanSetting(
      "Древние обломки", true
   );
   public final BooleanSetting diamond = new BooleanSetting("Алмаз", true);
   public final BooleanSetting gold = new BooleanSetting("Золото", true);
   public final BooleanSetting lapis = new BooleanSetting("Лазурит", true);
   public final BooleanSetting notifications = new BooleanSetting(
      D.k(
         new int[]{1214, 1175, 1102, 1239, 1187, 1177, 1088, 1238, 1184, 1181, 1076, 195, 1187, 1172, 91, 1245, 1196, 1182, 1093, 1247, 1191, 1173, 1086},
         new int[]{157, 165, 123, 227}
      ),
      true
   );
   private final Map<BlockPos, Block> xrkc = new ConcurrentHashMap<>();
   private final AtomicLong vsKM = new AtomicLong();
   private final Map<Long, Long> i0cs5b = new ConcurrentHashMap<>();
   private final Map<Long, Long> mOWRCz = new ConcurrentHashMap<>();
   private int iNtq7hU = 0;
   private static final int RESCAN_INTERVAL = 20;
   private int wKwwRod = 0;
   private String uAe1mo = "";

   public static XRay getInstance() {
      return yqab4;
   }

   public XRay() {
      yqab4 = this;
   }

   public boolean isUpdateMode() {
      return this.mode.is("Обновление");
   }

   public boolean isDebrisEnabled() {
      return this.debris.getValue();
   }

   public void forceUpdate() {
      if (!this.mode.is("Обновление")) {
         this.mode.setValue("Обновление");
      }

      if (!this.debris.getValue()) {
         this.debris.setValue(true);
      }
   }

   public void setState(boolean updateMode, boolean debrisEnabled) {
      if (this.mode.is("Обновление") != updateMode) {
         this.mode
            .setValue(
               updateMode
                  ? "Обновление"
                  : "Постоянный"
            );
      }

      if (this.debris.getValue() != debrisEnabled) {
         this.debris.setValue(debrisEnabled);
      }
   }

   public long getLastPacketIdSeq() {
      return this.vsKM.get();
   }

   public long getLastPacketId(BlockPos pos, int range) {
      if (pos == null) {
         return 0L;
      } else {
         int r = Math.max(0, range);
         long result = 0L;

         for (int cx = pos.getX() - r >> 4; cx <= pos.getX() + r >> 4; cx++) {
            for (int cz = pos.getZ() - r >> 4; cz <= pos.getZ() + r >> 4; cz++) {
               result = Math.max(result, this.i0cs5b.getOrDefault(ChunkPos.toLong(cx, cz), 0L));
            }
         }

         return result;
      }
   }

   public long getLastTime(BlockPos pos, int range) {
      if (pos == null) {
         return 0L;
      } else {
         int r = Math.max(0, range);
         long result = 0L;

         for (int cx = pos.getX() - r >> 4; cx <= pos.getX() + r >> 4; cx++) {
            for (int cz = pos.getZ() - r >> 4; cz <= pos.getZ() + r >> 4; cz++) {
               result = Math.max(result, this.mOWRCz.getOrDefault(ChunkPos.toLong(cx, cz), 0L));
            }
         }

         return result;
      }
   }

   public Set<BlockPos> getDebrisPositions() {
      Set<BlockPos> result = new HashSet<>();
      this.xrkc.forEach((pos, block) -> {
         if (block == Blocks.ANCIENT_DEBRIS) {
            result.add(pos.toImmutable());
         }
      });
      return Set.copyOf(result);
   }

   @Override
   public void onEnable() {
      this.zC6N();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.zC6N();
      super.onDisable();
   }

   @Subscribe
   public void onTick(EventTick event) {
      if (this.mc.player != null) {
         if (!this.mode.getValue().equals(this.uAe1mo)) {
            this.uAe1mo = this.mode.getValue();
            this.zC6N();
         }

         if (this.iNtq7hU > 0 && this.notifications.getValue()) {
            this.mc.player.sendMessage(Text.literal("Найдено древних обломков: " + this.iNtq7hU).formatted(Formatting.RED), false);
            this.iNtq7hU = 0;
         }

         if (this.mode.is("Постоянный") && --this.wKwwRod <= 0) {
            this.wKwwRod = 20;
            this.qxpC6DG();
         }
      }
   }

   @Subscribe
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE) {
         if (this.mc.world != null && this.mc.player != null) {
            if (event.getPacket() instanceof ChunkDeltaUpdateS2CPacket delta) {
               long packetId = this.vsKM.incrementAndGet();
               long time = System.currentTimeMillis();
               delta.visitUpdates((pos, state) -> {
                  BlockPos immutable = pos.toImmutable();
                  this.lxer(immutable, state);
                  this.zvqu5(immutable, packetId, time);
               });
            } else if (event.getPacket() instanceof BlockUpdateS2CPacket update) {
               BlockPos pos = update.getPos().toImmutable();
               long packetId = this.vsKM.incrementAndGet();
               long time = System.currentTimeMillis();
               this.lxer(pos, update.getState());
               this.zvqu5(pos, packetId, time);
            }
         }
      }
   }

   @Subscribe
   public void onWorldRender(EventWorldRender event) {
      if (!this.xrkc.isEmpty()) {
         MatrixStack matrices = event.getMatrixStack();
         Matrix4f matrix = matrices.peek().getPositionMatrix();
         Vec3d cam = this.mc.gameRenderer.getCamera().getPos();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.disableDepthTest();
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder fill = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);

         for (Entry<BlockPos, Block> entry : this.xrkc.entrySet()) {
            Block block = entry.getValue();
            if (this.gTwkRV(block)) {
               XRay.ColorUtil color = this.gJ79x3(block);
               BlockPos pos = entry.getKey();
               double minX = pos.getX() - cam.x;
               double minY = pos.getY() - cam.y;
               double minZ = pos.getZ() - cam.z;
               this.uGHfqJK(fill, matrix, minX, minY, minZ, minX + 1.0, minY + 1.0, minZ + 1.0, color.t5KZ0, color.v9s5, color.tDm8, 0.39215687F);
            }
         }

         BufferRenderer.drawWithGlobalProgram(fill.end());
         RenderSystem.lineWidth(2.0F);
         BufferBuilder edges = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

         for (Entry<BlockPos, Block> entryx : this.xrkc.entrySet()) {
            Block block = entryx.getValue();
            if (this.gTwkRV(block)) {
               XRay.ColorUtil color = this.gJ79x3(block);
               BlockPos pos = entryx.getKey();
               double minX = pos.getX() - cam.x;
               double minY = pos.getY() - cam.y;
               double minZ = pos.getZ() - cam.z;
               this.tOdZ1p(edges, matrix, minX, minY, minZ, minX + 1.0, minY + 1.0, minZ + 1.0, color.t5KZ0, color.v9s5, color.tDm8, 1.0F);
            }
         }

         BufferRenderer.drawWithGlobalProgram(edges.end());
         RenderSystem.enableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         RenderSystem.lineWidth(1.0F);
      }
   }

   private void zC6N() {
      this.xrkc.clear();
      this.i0cs5b.clear();
      this.mOWRCz.clear();
      this.iNtq7hU = 0;
      this.wKwwRod = 0;
      this.uAe1mo = this.mode.getValue();
   }

   private void zvqu5(BlockPos pos, long packetId, long time) {
      long key = ChunkPos.toLong(pos.getX() >> 4, pos.getZ() >> 4);
      this.i0cs5b.merge(key, packetId, Math::max);
      this.mOWRCz.merge(key, time, Math::max);
   }

   private void lxer(BlockPos pos, BlockState state) {
      Block block = state.getBlock();
      if (this.gTwkRV(block)) {
         if (!this.xrkc.containsKey(pos)) {
            this.xrkc.put(pos, block);
            if (block == Blocks.ANCIENT_DEBRIS) {
               this.iNtq7hU++;
            }
         } else {
            this.xrkc.put(pos, block);
         }
      } else {
         this.xrkc.remove(pos);
      }
   }

   private void qxpC6DG() {
      if (this.mc.world != null && this.mc.player != null) {
         int distance = this.mc.options.getViewDistance().getValue();
         ChunkPos center = this.mc.player.getChunkPos();
         this.xrkc.clear();

         for (int cx = center.x - distance; cx <= center.x + distance; cx++) {
            for (int cz = center.z - distance; cz <= center.z + distance; cz++) {
               if (this.mc.world.isChunkLoaded(cx, cz)) {
                  WorldChunk chunk = this.mc.world.getChunk(cx, cz);
                  if (chunk != null) {
                     this.il09(chunk);
                  }
               }
            }
         }
      }
   }

   private void il09(WorldChunk chunk) {
      ChunkSection[] sections = chunk.getSectionArray();
      int bottom = chunk.getBottomSectionCoord();
      int startX = chunk.getPos().getStartX();
      int startZ = chunk.getPos().getStartZ();

      for (int i = 0; i < sections.length; i++) {
         ChunkSection section = sections[i];
         if (section != null && !section.isEmpty()) {
            int yBase = bottom + i << 4;

            for (int y = 0; y < 16; y++) {
               for (int z = 0; z < 16; z++) {
                  for (int x = 0; x < 16; x++) {
                     Block block = section.getBlockState(x, y, z).getBlock();
                     if (this.gTwkRV(block)) {
                        this.xrkc.put(new BlockPos(startX + x, yBase + y, startZ + z), block);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean gTwkRV(Block block) {
      if (block == Blocks.ANCIENT_DEBRIS && this.debris.getValue()) {
         return true;
      } else if ((block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) && this.diamond.getValue()) {
         return true;
      } else {
         return (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) && this.gold.getValue()
            ? true
            : (block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE) && this.lapis.getValue();
      }
   }

   private XRay.ColorUtil gJ79x3(Block block) {
      if (block == Blocks.ANCIENT_DEBRIS) {
         return XRay.ColorUtil.kw7Utu3(255, 131, 54);
      } else if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) {
         return XRay.ColorUtil.kw7Utu3(121, 54, 255);
      } else if (block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.NETHER_GOLD_ORE) {
         return XRay.ColorUtil.kw7Utu3(255, 215, 0);
      } else {
         return block != Blocks.LAPIS_ORE && block != Blocks.DEEPSLATE_LAPIS_ORE ? XRay.ColorUtil.kw7Utu3(255, 255, 255) : XRay.ColorUtil.kw7Utu3(0, 71, 179);
      }
   }

   private void kzx79D(
      BufferBuilder buffer,
      Matrix4f matrix,
      double x1,
      double y1,
      double z1,
      double x2,
      double y2,
      double z2,
      double x3,
      double y3,
      double z3,
      double x4,
      double y4,
      double z4,
      float r,
      float g,
      float b,
      float a
   ) {
      buffer.vertex(matrix, (float)x1, (float)y1, (float)z1).color(r, g, b, a);
      buffer.vertex(matrix, (float)x2, (float)y2, (float)z2).color(r, g, b, a);
      buffer.vertex(matrix, (float)x3, (float)y3, (float)z3).color(r, g, b, a);
      buffer.vertex(matrix, (float)x4, (float)y4, (float)z4).color(r, g, b, a);
   }

   private void uGHfqJK(
      BufferBuilder buffer, Matrix4f matrix, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a
   ) {
      this.kzx79D(buffer, matrix, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
      this.kzx79D(buffer, matrix, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
      this.kzx79D(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, r, g, b, a);
      this.kzx79D(buffer, matrix, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
      this.kzx79D(buffer, matrix, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);
      this.kzx79D(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, r, g, b, a);
   }

   private void tOdZ1p(
      BufferBuilder buffer, Matrix4f matrix, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a
   ) {
      this.wf8240Z(buffer, matrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, minX, minY, minZ, minX, minY, maxZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, minX, minY, maxZ, maxX, minY, maxZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, minX, maxY, minZ, minX, maxY, maxZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, minX, maxY, maxZ, maxX, maxY, maxZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
      this.wf8240Z(buffer, matrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
   }

   private void wf8240Z(
      BufferBuilder buffer, Matrix4f matrix, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a
   ) {
      buffer.vertex(matrix, (float)x1, (float)y1, (float)z1).color(r, g, b, a);
      buffer.vertex(matrix, (float)x2, (float)y2, (float)z2).color(r, g, b, a);
   }

   private static class ColorUtil {
      private final float t5KZ0;
      private final float v9s5;
      private final float tDm8;

      private ColorUtil(float r, float g, float b) {
         this.t5KZ0 = r;
         this.v9s5 = g;
         this.tDm8 = b;
      }

      private static XRay.ColorUtil kw7Utu3(int r, int g, int b) {
         return new XRay.ColorUtil(r / 255.0F, g / 255.0F, b / 255.0F);
      }
   }
}
