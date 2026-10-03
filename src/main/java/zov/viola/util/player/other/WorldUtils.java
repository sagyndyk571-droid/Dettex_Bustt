package zov.viola.util.player.other;

import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;

public final class WorldUtils implements IMinecraft {
   public static boolean isInWeb() {
      Box box = mc.player.getBoundingBox();
      int minX = MathHelper.floor(box.minX);
      int minY = MathHelper.floor(box.minY);
      int minZ = MathHelper.floor(box.minZ);
      int maxX = MathHelper.floor(box.maxX);
      int maxY = MathHelper.floor(box.maxY);
      int maxZ = MathHelper.floor(box.maxZ);

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               BlockPos pos = new BlockPos(x, y, z);
               BlockState state = mc.world.getBlockState(pos);
               if (state.isOf(Blocks.COBWEB)
                  && state.getOutlineShape(mc.world, pos).getBoundingBoxes().stream().anyMatch(shape -> shape.offset(pos).intersects(box))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean isBoxInBlock(Box box, Block block) {
      return isBox(box, pos -> mc.world.getBlockState(pos).getBlock().equals(block));
   }

   public static boolean isBoxInBlocks(Box box, List<Block> blocks) {
      return isBox(box, pos -> blocks.contains(mc.world.getBlockState(pos).getBlock()));
   }

   public static boolean isBox(Box box, Predicate<BlockPos> pos) {
      return BlockPos.stream(box).anyMatch(pos);
   }

   public static BlockPos findNearestPlaceableBlock() {
      Vec3d playerPos = mc.player.getPos();
      BlockPos feetBlock = mc.player.getBlockPos();
      int placementY = feetBlock.getY() - 1;
      BlockPos[] candidates = new BlockPos[]{
         new BlockPos(feetBlock.getX() - 1, placementY, feetBlock.getZ()),
         new BlockPos(feetBlock.getX() + 1, placementY, feetBlock.getZ()),
         new BlockPos(feetBlock.getX(), placementY, feetBlock.getZ() - 1),
         new BlockPos(feetBlock.getX(), placementY, feetBlock.getZ() + 1)
      };
      Box playerBox = mc.player.getBoundingBox();
      BlockPos nearest = null;
      double minDistance = Double.MAX_VALUE;

      for (BlockPos candidate : candidates) {
         Box blockBox = new Box(candidate);
         if (!playerBox.expand(0.15F).intersects(blockBox)) {
            Vec3d blockCenter = Vec3d.ofCenter(candidate.up());
            double distance = playerPos.distanceTo(blockCenter);
            if (distance < minDistance) {
               minDistance = distance;
               nearest = candidate;
            }
         }
      }

      return nearest;
   }

   @Generated
   private WorldUtils() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               4,
               248,
               102,
               194,
               112,
               249,
               124,
               145,
               49,
               176,
               122,
               197,
               57,
               252,
               102,
               197,
               41,
               176,
               108,
               221,
               49,
               227,
               124,
               145,
               49,
               254,
               107,
               145,
               51,
               241,
               97,
               223,
               63,
               228,
               47,
               211,
               53,
               176,
               102,
               223,
               35,
               228,
               110,
               223,
               36,
               249,
               110,
               197,
               53,
               244
            },
            new int[]{80, 144, 15, 177}
         )
      );
   }
}
