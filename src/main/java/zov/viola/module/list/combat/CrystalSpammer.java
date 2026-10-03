package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventKeyInput;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.network.NetworkUtils;

@ModuleInformation(
   moduleName = "Crystal Spammer",
   moduleDesc = "Тихая автоустановка/взрыв кристалла по кнопке",
   moduleCategory = ModuleCategory.COMBAT
)
public class CrystalSpammer extends Module {
   private final BindSetting f6r0z = new BindSetting(
      "Клавиша спама", -1
   );
   private final BooleanSetting hIvhw0l = new BooleanSetting(
      "Ломать кристалл", true
   );
   private final SliderSetting iooz = new SliderSetting(
      "Радиус врага", 6.0, 2.0, 10.0, 0.5
   );
   private final SliderSetting uLnm5b = new SliderSetting(
      "Радиус кристалла",
      4.5,
      2.0,
      6.0,
      0.5
   );
   private final SliderSetting rekf5 = new SliderSetting(
      "Задержка установки",
      4.0,
      1.0,
      10.0,
      1.0
   );
   private final SliderSetting wyB4OSh = new SliderSetting(
      "Задержка взрыва", 2.0, 0.0, 8.0, 1.0
   );
   private boolean ptT2W;
   private int n1frd3t;
   private int yycrNEm;

   @Subscribe
   private void onUpdate(EventTick e) {
      if (this.ptT2W && this.mc.player != null && this.mc.world != null) {
         if (this.mc.currentScreen != null) {
            this.ptT2W = false;
         } else {
            PlayerEntity target = this.yhk03Uh();
            if (target != null) {
               EndCrystalEntity crystal = this.pT0O1e(target);
               if (crystal != null && this.hIvhw0l.getValue()) {
                  if (this.yycrNEm >= this.wyB4OSh.getValue()) {
                     this.silentLook(crystal.getBoundingBox().getCenter());
                     if (this.mc.player.distanceTo(crystal) <= this.uLnm5b.getValue() + 1.0) {
                        this.mc.interactionManager.attackEntity(this.mc.player, crystal);
                        this.mc.player.swingHand(Hand.MAIN_HAND);
                     }

                     this.yycrNEm = 0;
                  } else {
                     this.yycrNEm++;
                  }
               } else {
                  BlockPos pos = this.dPmo4rb(target);
                  if (pos != null) {
                     if (this.n1frd3t >= this.rekf5.getValue()) {
                        if (this.t7jtkhg()) {
                           this.silentLook(pos.toCenterPos().add(0.0, 1.0, 0.0));
                           this.nPRdcR(pos);
                           this.n1frd3t = 0;
                        }
                     } else {
                        this.n1frd3t++;
                     }
                  }
               }
            }
         }
      }
   }

   @Subscribe
   private void onKey(EventKeyInput e) {
      if (e.getKey() == this.f6r0z.getValue()) {
         this.ptT2W = e.getAction() == 1 || e.getAction() == 2;
      }
   }

   private void silentLook(Vec3d vec) {
      if (this.mc.player != null) {
         Vec3d eye = this.mc.player.getEyePos();
         double dx = vec.x - eye.x;
         double dy = vec.y - eye.y;
         double dz = vec.z - eye.z;
         double distXZ = Math.sqrt(dx * dx + dz * dz);
         float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
         float pitch = (float)(-Math.toDegrees(Math.atan2(dy, distXZ)));
         NetworkUtils.sendPacket(
            new LookAndOnGround(yaw, MathHelper.clamp(pitch, -90.0F, 90.0F), this.mc.player.isOnGround(), this.mc.player.horizontalCollision)
         );
      }
   }

   private PlayerEntity yhk03Uh() {
      if (this.mc.world != null && this.mc.player != null) {
         PlayerEntity closest = null;
         double closestDist = Double.MAX_VALUE;

         for (PlayerEntity player : this.mc.world.getPlayers()) {
            if (player != this.mc.player
               && !player.isDead()
               && !(player.getHealth() <= 0.0F)
               && !FriendRepository.getFriends().contains(player.getName().getString())) {
               double dist = this.mc.player.distanceTo(player);
               if (!(dist > this.iooz.getValue()) && dist < closestDist) {
                  closestDist = dist;
                  closest = player;
               }
            }
         }

         return closest;
      } else {
         return null;
      }
   }

   private EndCrystalEntity pT0O1e(PlayerEntity target) {
      if (this.mc.world != null && target != null) {
         EndCrystalEntity bestCrystal = null;
         double bestScore = Double.MAX_VALUE;

         for (Entity entity : this.mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity crystal && crystal.isAlive() && !(this.mc.player.distanceTo(crystal) > this.uLnm5b.getValue())) {
               double distToTarget = target.getPos().distanceTo(crystal.getPos());
               if (!(distToTarget > 8.0) && distToTarget < bestScore) {
                  bestScore = distToTarget;
                  bestCrystal = crystal;
               }
            }
         }

         return bestCrystal;
      } else {
         return null;
      }
   }

   private BlockPos dPmo4rb(PlayerEntity target) {
      if (this.mc.world != null && this.mc.player != null && target != null) {
         BlockPos playerPos = this.mc.player.getBlockPos();
         BlockPos bestPos = null;
         double bestScore = Double.MAX_VALUE;
         int r = (int)Math.ceil(this.uLnm5b.getValue());

         for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
               for (int z = -r; z <= r; z++) {
                  BlockPos pos = playerPos.add(x, y, z);
                  if (this.uAR08(pos)
                     && !(this.mc.player.getPos().distanceTo(pos.toCenterPos()) > this.uLnm5b.getValue())
                     && !(target.getY() < pos.getY() - 0.2)) {
                     double distToTarget = target.getPos().distanceTo(pos.toCenterPos());
                     if (!(distToTarget > 8.0) && distToTarget < bestScore) {
                        bestScore = distToTarget;
                        bestPos = pos;
                     }
                  }
               }
            }
         }

         return bestPos;
      } else {
         return null;
      }
   }

   private boolean uAR08(BlockPos pos) {
      if (this.mc.world == null) {
         return false;
      } else if (!this.mc.world.getBlockState(pos).isOf(Blocks.OBSIDIAN) && !this.mc.world.getBlockState(pos).isOf(Blocks.BEDROCK)) {
         return false;
      } else if (!this.mc.world.getBlockState(pos.up()).isAir()) {
         return false;
      } else if (!this.mc.world.getBlockState(pos.up(2)).isAir()) {
         return false;
      } else {
         Box box = new Box(pos.up()).expand(0.0, 1.0, 0.0);

         for (Entity entity : this.mc.world.getOtherEntities(null, box)) {
            if (!(entity instanceof EndCrystalEntity)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean t7jtkhg() {
      if (this.mc.player == null) {
         return false;
      } else if (this.mc.player.getMainHandStack().getItem() instanceof EndCrystalItem) {
         return true;
      } else {
         for (int i = 0; i < 9; i++) {
            if (this.mc.player.getInventory().getStack(i).getItem() instanceof EndCrystalItem) {
               this.mc.player.getInventory().selectedSlot = i;
               if (this.mc.interactionManager != null) {
                  this.mc.interactionManager.syncSelectedSlot();
               }

               return true;
            }
         }

         return false;
      }
   }

   private void nPRdcR(BlockPos pos) {
      if (this.mc.player != null && this.mc.interactionManager != null) {
         Hand hand = Hand.MAIN_HAND;
         if (this.mc.player.getMainHandStack().isEmpty()) {
            hand = Hand.OFF_HAND;
         }

         Vec3d hitVec = pos.toCenterPos().add(0.0, 1.0, 0.0);
         this.mc.interactionManager.interactBlock(this.mc.player, hand, new BlockHitResult(hitVec, Direction.UP, pos, false));
         this.mc.player.swingHand(hand);
      }
   }
}
