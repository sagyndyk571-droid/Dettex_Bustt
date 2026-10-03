package zov.viola.util.player.simulate;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import zov.viola.util.IMinecraft;
import zov.viola.util.player.move.MoveUtil;

public class SimulatedPlayer implements IMinecraft {
   public final PlayerEntity player;
   public final SimulatedPlayer.SimulatedPlayerInput input;
   public Vec3d pos;
   public Vec3d velocity;
   public Box boundingBox;
   public float yaw;
   public float pitch;
   public boolean sprinting;
   public float fallDistance;
   public int jumpingCooldown;
   public boolean isJumping;
   public boolean isFallFlying;
   public boolean onGround;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean touchingWater;
   public boolean isSwimming;
   public boolean submergedInWater;
   private final Object2DoubleMap<TagKey<Fluid>> gott5gu;
   private final HashSet<TagKey<Fluid>> smvaa7;
   private int exIlsR = 0;
   private boolean tPnEY = false;
   private static final double STEP_HEIGHT = 0.5;

   public SimulatedPlayer(
      PlayerEntity player,
      SimulatedPlayer.SimulatedPlayerInput input,
      Vec3d pos,
      Vec3d velocity,
      Box boundingBox,
      float yaw,
      float pitch,
      boolean sprinting,
      float fallDistance,
      int jumpingCooldown,
      boolean isJumping,
      boolean isFallFlying,
      boolean onGround,
      boolean horizontalCollision,
      boolean verticalCollision,
      boolean touchingWater,
      boolean isSwimming,
      boolean submergedInWater,
      Object2DoubleMap<TagKey<Fluid>> fluidHeight,
      HashSet<TagKey<Fluid>> submergedFluidTag
   ) {
      this.player = player;
      this.input = input;
      this.pos = pos;
      this.velocity = velocity;
      this.boundingBox = boundingBox;
      this.yaw = yaw;
      this.pitch = pitch;
      this.sprinting = sprinting;
      this.fallDistance = fallDistance;
      this.jumpingCooldown = jumpingCooldown;
      this.isJumping = isJumping;
      this.isFallFlying = isFallFlying;
      this.onGround = onGround;
      this.horizontalCollision = horizontalCollision;
      this.verticalCollision = verticalCollision;
      this.touchingWater = touchingWater;
      this.isSwimming = isSwimming;
      this.submergedInWater = submergedInWater;
      this.gott5gu = fluidHeight;
      this.smvaa7 = submergedFluidTag;
   }

   public static SimulatedPlayer simulateLocalPlayer(int ticks) {
      SimulatedPlayer simulatedPlayer = fromClientPlayer(SimulatedPlayer.SimulatedPlayerInput.fromClientPlayer(mc.player.input.playerInput));

      for (int i = 0; i < ticks; i++) {
         simulatedPlayer.tick();
      }

      return simulatedPlayer;
   }

   public static SimulatedPlayer simulateOtherPlayer(PlayerEntity player, int ticks) {
      SimulatedPlayer simulatedPlayer = fromOtherPlayer(player, SimulatedPlayer.SimulatedPlayerInput.guessInput(player));

      for (int i = 0; i < ticks; i++) {
         simulatedPlayer.tick();
      }

      return simulatedPlayer;
   }

   public static SimulatedPlayer fromClientPlayer(SimulatedPlayer.SimulatedPlayerInput input) {
      ClientPlayerEntity player = mc.player;
      return new SimulatedPlayer(
         player,
         input,
         player.getPos(),
         player.getVelocity(),
         player.getBoundingBox(),
         player.getYaw(),
         player.getPitch(),
         player.isSprinting(),
         player.fallDistance,
         player.jumpingCooldown,
         player.jumping,
         player.isGliding(),
         player.isOnGround(),
         player.horizontalCollision,
         player.verticalCollision,
         player.isTouchingWater(),
         player.isSwimming(),
         player.isSubmergedInWater(),
         new Object2DoubleArrayMap(player.fluidHeight),
         new HashSet<>(player.submergedFluidTag)
      );
   }

   public static SimulatedPlayer fromOtherPlayer(PlayerEntity player, SimulatedPlayer.SimulatedPlayerInput input) {
      return new SimulatedPlayer(
         player,
         input,
         player.getPos(),
         player.getPos().subtract(new Vec3d(player.prevX, player.prevY, player.prevZ)),
         player.getBoundingBox(),
         player.getYaw(),
         player.getPitch(),
         player.isSprinting(),
         player.fallDistance,
         player.jumpingCooldown,
         player.jumping,
         player.isGliding(),
         player.isOnGround(),
         player.horizontalCollision,
         player.verticalCollision,
         player.isTouchingWater(),
         player.isSwimming(),
         player.isSubmergedInWater(),
         new Object2DoubleArrayMap(player.fluidHeight),
         new HashSet<>(player.submergedFluidTag)
      );
   }

   public Vec3d pos() {
      return this.player.getPos();
   }

   public void tick() {
      this.exIlsR++;
      this.tPnEY = false;
      if (!(this.pos.y <= -70.0)) {
         this.input.update();
         this.jAGXq9();
         this.vht1Bv7();
         this.ps9n();
         if (this.jumpingCooldown > 0) {
            this.jumpingCooldown--;
         }

         this.isJumping = this.input.playerInput.jump();
         double newX = this.velocity.x;
         double newY = this.velocity.y;
         double newZ = this.velocity.z;
         if (Math.abs(this.velocity.x) < 0.003) {
            newX = 0.0;
         }

         if (Math.abs(this.velocity.y) < 0.003) {
            newY = 0.0;
         }

         if (Math.abs(this.velocity.z) < 0.003) {
            newZ = 0.0;
         }

         if (this.onGround) {
            this.isFallFlying = false;
         }

         this.velocity = new Vec3d(newX, newY, newZ);
         if (this.isJumping) {
            double fluidLevel = this.isInLava() ? this.acl3je(FluidTags.LAVA) : this.acl3je(FluidTags.WATER);
            boolean inWater = this.q2385iq() && fluidLevel > 0.0;
            double swimHeight = this.vs6xm72();
            if (!inWater || this.onGround && !(fluidLevel > swimHeight)) {
               if (!this.isInLava() || this.onGround && !(fluidLevel > swimHeight)) {
                  if ((this.onGround || inWater && fluidLevel <= swimHeight) && this.jumpingCooldown == 0) {
                     this.jump();
                     this.jumpingCooldown = 10;
                  }
               } else {
                  this.bv4vq1r(FluidTags.LAVA);
               }
            } else {
               this.bv4vq1r(FluidTags.WATER);
            }
         }

         float sidewaysSpeed = this.input.movementSideways * 0.98F;
         float forwardSpeed = this.input.movementForward * 0.98F;
         float upwardsSpeed = 0.0F;
         if (this.hasStatusEffect(StatusEffects.SLOW_FALLING) || this.hasStatusEffect(StatusEffects.LEVITATION)) {
            this.v6kF();
         }

         this.u22YKso(new Vec3d(sidewaysSpeed, upwardsSpeed, forwardSpeed));
      }
   }

   private void u22YKso(Vec3d movementInput) {
      if (this.isSwimming && !this.player.hasVehicle()) {
         double g = this.getRotationVector().y;
         double h = g < -0.2 ? 0.085 : 0.06;
         BlockPos posAbove = new BlockPos(MathHelper.floor(this.pos.x), MathHelper.floor(this.pos.y + 1.0 - 0.1), MathHelper.floor(this.pos.z));
         if (g <= 0.0 || this.input.playerInput.jump() || !this.player.getWorld().getBlockState(posAbove).getFluidState().isEmpty()) {
            this.velocity = this.velocity.add(0.0, (g - this.velocity.y) * h, 0.0);
         }
      }

      double beforeTravelVelocityY = this.velocity.y;
      double d = 0.08;
      boolean falling = this.velocity.y <= 0.0;
      if (this.velocity.y <= 0.0 && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
         d = 0.01;
         this.v6kF();
      }

      if (this.q2385iq() && this.player.shouldSwimInFluids()) {
         double e = this.pos.y;
         float f = this.o0ly() ? 0.9F : 0.8F;
         float g = 0.02F;
         float h = (float)this.getAttributeValue(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
         if (!this.onGround) {
            h *= 0.5F;
         }

         if (h > 0.0F) {
            f += (0.54600006F - f) * h / 3.0F;
            g += (this.d7Whobw() - g) * h / 3.0F;
         }

         if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
            f = 0.96F;
         }

         this.ciP9u8(g, movementInput);
         this.fAY8(this.velocity);
         Vec3d tempVel = this.velocity;
         if (this.horizontalCollision && this.isClimbing()) {
            tempVel = new Vec3d(tempVel.x, 0.2, tempVel.z);
         }

         this.velocity = tempVel.multiply(f, 0.8, f);
         Vec3d vec3d2 = this.player.applyFluidMovingSpeed(d, falling, this.velocity);
         this.velocity = vec3d2;
         if (this.horizontalCollision && this.ot6gCPg(vec3d2.x, vec3d2.y + 0.6 - this.pos.y + e, vec3d2.z)) {
            this.velocity = new Vec3d(vec3d2.x, 0.3, vec3d2.z);
         }
      } else if (this.isInLava() && this.player.shouldSwimInFluids()) {
         double ex = this.pos.y;
         this.ciP9u8(0.02F, movementInput);
         this.fAY8(this.velocity);
         if (this.acl3je(FluidTags.LAVA) <= this.vs6xm72()) {
            this.velocity = this.velocity.multiply(0.5, 0.8, 0.5);
            this.velocity = this.player.applyFluidMovingSpeed(d, falling, this.velocity);
         } else {
            this.velocity = this.velocity.multiply(0.5);
         }

         if (!this.player.hasNoGravity()) {
            this.velocity = this.velocity.add(0.0, -d / 4.0, 0.0);
         }

         if (this.horizontalCollision && this.ot6gCPg(this.velocity.x, this.velocity.y + 0.6 - this.pos.y + ex, this.velocity.z)) {
            this.velocity = new Vec3d(this.velocity.x, 0.3, this.velocity.z);
         }
      } else if (this.isFallFlying) {
         Vec3d exx = this.velocity;
         if (exx.y > -0.5) {
            this.fallDistance = 1.0F;
         }

         Vec3d vec3d3 = this.getRotationVector(this.pitch + (this.pitch - this.player.prevPitch), this.yaw + (this.yaw - this.player.prevYaw));
         float fx = this.pitch * (float) (Math.PI / 180.0);
         double gx = Math.sqrt(vec3d3.x * vec3d3.x + vec3d3.z * vec3d3.z);
         double horizontalSpeed = this.velocity.horizontalLength();
         double i = vec3d3.length();
         float j = MathHelper.cos(fx);
         j = (float)(j * (j * Math.min(1.0, i / 0.4)));
         exx = this.velocity.add(0.0, d * (-1.0 + j * 0.75), 0.0);
         if (exx.y < 0.0 && gx > 0.0) {
            double k = exx.y * -0.1 * j;
            exx = exx.add(vec3d3.x * k / gx, k, vec3d3.z * k / gx);
         }

         if (fx < 0.0F && gx > 0.0) {
            double k = horizontalSpeed * -MathHelper.sin(fx) * 0.04;
            exx = exx.add(-vec3d3.x * k / gx, k * 3.2, -vec3d3.z * k / gx);
         }

         if (gx > 0.0) {
            exx = exx.add((vec3d3.x / gx * horizontalSpeed - exx.x) * 0.1, 0.0, (vec3d3.z / gx * horizontalSpeed - exx.z) * 0.1);
         }

         this.velocity = exx.multiply(0.99, 0.98, 0.99);
         this.fAY8(this.velocity);
      } else {
         BlockPos blockPos = this.wbzi();
         float p = this.player.getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
         float fxx = this.onGround ? p * 0.91F : 0.91F;
         Vec3d vec3d6 = this.jO68XjC(movementInput, p);
         double q = vec3d6.y;
         if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
            StatusEffectInstance levitation = this.k7H8(StatusEffects.LEVITATION);
            if (levitation != null) {
               q += (0.05 * (levitation.getAmplifier() + 1) - vec3d6.y) * 0.2;
            }
         } else if (this.player.getWorld().isClient() && !this.player.getWorld().isChunkLoaded(blockPos)) {
            q = this.pos.y > this.player.getWorld().getBottomY() ? -0.1 : 0.0;
         } else if (!this.player.hasNoGravity()) {
            q -= d;
         }

         if (this.player.hasNoDrag()) {
            this.velocity = new Vec3d(vec3d6.x, q, vec3d6.z);
         } else {
            this.velocity = new Vec3d(vec3d6.x * fxx, q * 0.98F, vec3d6.z * fxx);
         }
      }

      if (this.player.getAbilities().flying && !this.player.hasVehicle()) {
         this.velocity = new Vec3d(this.velocity.x, beforeTravelVelocityY * 0.6, this.velocity.z);
         this.v6kF();
      }
   }

   private Vec3d jO68XjC(Vec3d movementInput, float slipperiness) {
      this.ciP9u8(this.wPqqAW8(slipperiness), movementInput);
      this.velocity = this.nags(this.velocity);
      this.fAY8(this.velocity);
      Vec3d result = this.velocity;
      BlockPos posBlock = this.posToBlockPos(this.pos);
      BlockState state = this.getState(posBlock);
      if ((this.horizontalCollision || this.isJumping)
         && (this.isClimbing() || state != null && state.isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(this.player))) {
         result = new Vec3d(result.x, 0.2, result.z);
      }

      return result;
   }

   private void ciP9u8(float speed, Vec3d movementInput) {
      Vec3d vec = Entity.movementInputToVelocity(movementInput, speed, this.yaw);
      this.velocity = this.velocity.add(vec);
   }

   private float wPqqAW8(float slipperiness) {
      return this.onGround ? this.d7Whobw() * (0.21600002F / (slipperiness * slipperiness * slipperiness)) : this.bwXZLJ();
   }

   private float bwXZLJ() {
      float speed = 0.02F;
      return this.input.playerInput.sprint() ? speed + 0.006F : speed;
   }

   private float d7Whobw() {
      return 0.1F;
   }

   private void fAY8(Vec3d movement) {
      Vec3d modifiedMovement = this.dGb8Fk(movement);
      Vec3d adjustedMovement = this.ff98(modifiedMovement);
      if (adjustedMovement.lengthSquared() > 1.0E-7) {
         this.pos = this.pos.add(adjustedMovement);
         this.boundingBox = this.player.dimensions.getBoxAt(this.pos);
      }

      boolean xCollision = !MathHelper.approximatelyEquals(movement.x, adjustedMovement.x);
      boolean zCollision = !MathHelper.approximatelyEquals(movement.z, adjustedMovement.z);
      this.horizontalCollision = xCollision || zCollision;
      this.verticalCollision = movement.y != adjustedMovement.y;
      this.onGround = this.verticalCollision && movement.y < 0.0;
      if (!this.q2385iq()) {
         this.jAGXq9();
      }

      if (this.onGround) {
         this.v6kF();
      } else if (movement.y < 0.0) {
         this.fallDistance = this.fallDistance - (float)movement.y;
      }

      Vec3d currentVel = this.velocity;
      if (this.horizontalCollision || this.verticalCollision) {
         this.velocity = new Vec3d(xCollision ? 0.0 : currentVel.x, this.onGround ? 0.0 : currentVel.y, zCollision ? 0.0 : currentVel.z);
      }
   }

   private Vec3d ff98(Vec3d movement) {
      Box box = new Box(-0.3, 0.0, -0.3, 0.3, 1.8, 0.3).offset(this.pos);
      List<VoxelShape> collisionShapes = Collections.emptyList();
      Vec3d adjusted;
      if (movement.lengthSquared() == 0.0) {
         adjusted = movement;
      } else {
         adjusted = Entity.adjustMovementForCollisions(this.player, movement, box, this.player.getWorld(), collisionShapes);
      }

      boolean xCollide = movement.x != adjusted.x;
      boolean yCollide = movement.y != adjusted.y;
      boolean zCollide = movement.z != adjusted.z;
      boolean stepPossible = this.onGround || yCollide && movement.y < 0.0;
      if (this.player.getStepHeight() > 0.0F && stepPossible && (xCollide || zCollide)) {
         Vec3d stepAdjust = Entity.adjustMovementForCollisions(
            this.player, new Vec3d(movement.x, this.player.getStepHeight(), movement.z), box, this.player.getWorld(), collisionShapes
         );
         Vec3d stepOffset = Entity.adjustMovementForCollisions(
            this.player, new Vec3d(0.0, this.player.getStepHeight(), 0.0), box.stretch(movement.x, 0.0, movement.z), this.player.getWorld(), collisionShapes
         );
         Vec3d combined = Entity.adjustMovementForCollisions(
               this.player, new Vec3d(movement.x, 0.0, movement.z), box.offset(stepOffset), this.player.getWorld(), collisionShapes
            )
            .add(stepOffset);
         if (stepOffset.y < this.player.getStepHeight() && combined.horizontalLengthSquared() > stepAdjust.horizontalLengthSquared()) {
            stepAdjust = combined;
         }

         if (stepAdjust.horizontalLengthSquared() > adjusted.horizontalLengthSquared()) {
            return stepAdjust.add(
               Entity.adjustMovementForCollisions(
                  this.player, new Vec3d(0.0, -stepAdjust.y + movement.y, 0.0), box.offset(stepAdjust), this.player.getWorld(), collisionShapes
               )
            );
         }
      }

      return adjusted;
   }

   private void v6kF() {
      this.fallDistance = 0.0F;
   }

   public void jump() {
      this.velocity = this.velocity.add(0.0, this.h7sm() - this.velocity.y, 0.0);
      if (this.o0ly()) {
         float rad = (float)Math.toRadians(this.yaw);
         this.velocity = this.velocity.add(-MathHelper.sin(rad) * 0.2, 0.0, MathHelper.cos(rad) * 0.2);
      }
   }

   private Vec3d nags(Vec3d motion) {
      if (!this.isClimbing()) {
         return motion;
      } else {
         this.v6kF();
         double clampedX = MathHelper.clamp(motion.x, -0.15F, 0.15F);
         double clampedZ = MathHelper.clamp(motion.z, -0.15F, 0.15F);
         double clampedY = Math.max(motion.y, -0.15F);
         if (clampedY < 0.0 && !this.getState(this.posToBlockPos(this.pos)).isOf(Blocks.SCAFFOLDING) && this.player.isHoldingOntoLadder()) {
            clampedY = 0.0;
         }

         return new Vec3d(clampedX, clampedY, clampedZ);
      }
   }

   public boolean isClimbing() {
      BlockPos posBlock = this.posToBlockPos(this.pos);
      BlockState state = this.getState(posBlock);
      return state.isIn(BlockTags.CLIMBABLE) ? true : state.getBlock() instanceof TrapdoorBlock && this.czxu(posBlock, state);
   }

   private boolean czxu(BlockPos pos, BlockState state) {
      if (!state.get(TrapdoorBlock.OPEN)) {
         return false;
      } else {
         BlockState below = this.player.getWorld().getBlockState(pos.down());
         return below.isOf(Blocks.LADDER) && below.get(LadderBlock.FACING).equals(state.get(TrapdoorBlock.FACING));
      }
   }

   private Vec3d dGb8Fk(Vec3d movement) {
      if (movement.y <= 0.0 && this.ak1X()) {
         double dx = movement.x;
         double dz = movement.z;

         double step;
         for (step = 0.05; dx != 0.0 && this.player.getWorld().isSpaceEmpty(this.player, this.boundingBox.offset(dx, -0.5, 0.0)); dx += dx > 0.0 ? -step : step) {
            if (dx < step && dx >= -step) {
               dx = 0.0;
               break;
            }
         }

         while (dz != 0.0 && this.player.getWorld().isSpaceEmpty(this.player, this.boundingBox.offset(0.0, -0.5, dz))) {
            if (dz < step && dz >= -step) {
               dz = 0.0;
               break;
            }

            dz += dz > 0.0 ? -step : step;
         }

         while (dx != 0.0 && dz != 0.0 && this.player.getWorld().isSpaceEmpty(this.player, this.boundingBox.offset(dx, -0.5, dz))) {
            dx = dx < step && dx >= -step ? 0.0 : (dx > 0.0 ? dx - step : dx + step);
            if (dz < step && dz >= -step) {
               dz = 0.0;
               break;
            }

            dz += dz > 0.0 ? -step : step;
         }

         if (movement.x != dx || movement.z != dz) {
            this.tPnEY = true;
         }

         if (this.shouldClipAtLedge()) {
            movement = new Vec3d(dx, movement.y, dz);
         }
      }

      return movement;
   }

   protected boolean shouldClipAtLedge() {
      return this.input.playerInput.sneak() || this.input.forceSafeWalk;
   }

   private boolean ak1X() {
      return this.onGround
         || this.fallDistance < 0.5 && !this.player.getWorld().isSpaceEmpty(this.player, this.boundingBox.offset(0.0, this.fallDistance - 0.5, 0.0));
   }

   private boolean o0ly() {
      return this.sprinting;
   }

   private float h7sm() {
      return 0.42F * this.giaGf0() + this.p5rL7lr();
   }

   private float p5rL7lr() {
      if (this.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
         StatusEffectInstance boost = this.k7H8(StatusEffects.JUMP_BOOST);
         return 0.1F * (boost.getAmplifier() + 1);
      } else {
         return 0.0F;
      }
   }

   private float giaGf0() {
      float multiplier1 = 0.0F;
      Block block = this.getState(this.posToBlockPos(this.pos)).getBlock();
      if (block != null) {
         multiplier1 = block.getJumpVelocityMultiplier();
      }

      float multiplier2 = 0.0F;
      Block block2 = this.getState(this.wbzi()).getBlock();
      if (block2 != null) {
         multiplier2 = block2.getJumpVelocityMultiplier();
      }

      return multiplier1 == 1.0F ? multiplier2 : multiplier1;
   }

   private boolean ot6gCPg(double offsetX, double offsetY, double offsetZ) {
      return this.f28Ek(this.boundingBox.offset(offsetX, offsetY, offsetZ));
   }

   private boolean f28Ek(Box box) {
      return this.player.getWorld().isSpaceEmpty(this.player, box) && !this.player.getWorld().containsFluid(box);
   }

   private void bv4vq1r(TagKey<Fluid> fluidTag) {
      this.velocity = this.velocity.add(0.0, 0.04F, 0.0);
   }

   private BlockPos wbzi() {
      return BlockPos.ofFloored(this.pos.x, this.boundingBox.minY - 0.5000001, this.pos.z);
   }

   private double vs6xm72() {
      return this.player.getStandingEyeHeight() < 0.4 ? 0.0 : 0.4;
   }

   private boolean q2385iq() {
      return this.touchingWater;
   }

   public boolean isInLava() {
      return this.gott5gu.getDouble(FluidTags.LAVA) > 0.0;
   }

   private void jAGXq9() {
      if (this.player.getVehicle() instanceof BoatEntity) {
         BoatEntity boat = (BoatEntity)this.player.getVehicle();
         if (!boat.isSubmergedInWater()) {
            this.touchingWater = false;
            return;
         }
      }

      if (this.y7tZ(FluidTags.WATER, 0.014)) {
         this.v6kF();
         this.touchingWater = true;
      } else {
         this.touchingWater = false;
      }
   }

   private void ps9n() {
      if (this.isSwimming) {
         this.isSwimming = this.o0ly() && this.q2385iq() && !this.player.hasVehicle();
      } else {
         this.isSwimming = this.o0ly()
            && this.isSubmergedInWater()
            && !this.player.hasVehicle()
            && this.player.getWorld().getFluidState(this.posToBlockPos(this.pos)).isIn(FluidTags.WATER);
      }
   }

   private void vht1Bv7() {
      this.submergedInWater = this.smvaa7.contains(FluidTags.WATER);
      this.smvaa7.clear();
      double eyeLevel = this.hV4UTRH() - 0.11111111F;
      if (!(
         this.player.getVehicle() instanceof BoatEntity boat
            && !boat.isSubmergedInWater()
            && boat.getBoundingBox().maxY >= eyeLevel
            && boat.getBoundingBox().minY <= eyeLevel
      )) {
         BlockPos posEye = BlockPos.ofFloored(this.pos.x, eyeLevel, this.pos.z);
         FluidState fluidState = this.player.getWorld().getFluidState(posEye);
         double height = posEye.getY() + fluidState.getHeight(this.player.getWorld(), posEye);
         if (height > eyeLevel) {
            this.smvaa7.addAll(fluidState.streamTags().toList());
         }
      }
   }

   private double hV4UTRH() {
      return this.pos.y + this.player.getStandingEyeHeight();
   }

   public boolean isSubmergedInWater() {
      return this.submergedInWater && this.q2385iq();
   }

   private double acl3je(TagKey<Fluid> tag) {
      return this.gott5gu.getDouble(tag);
   }

   private boolean y7tZ(TagKey<Fluid> tag, double speed) {
      if (this.i78erFb()) {
         return false;
      } else {
         Box box = this.boundingBox.contract(0.001);
         int i = MathHelper.floor(box.minX);
         int j = MathHelper.ceil(box.maxX);
         int k = MathHelper.floor(box.minY);
         int l = MathHelper.ceil(box.maxY);
         int m = MathHelper.floor(box.minZ);
         int n = MathHelper.ceil(box.maxZ);
         double d = 0.0;
         boolean pushedByFluids = true;
         boolean foundFluid = false;
         Vec3d fluidVelocity = Vec3d.ZERO;
         int count = 0;
         Mutable mutable = new Mutable();

         for (int p = i; p < j; p++) {
            for (int q = k; q < l; q++) {
               for (int r = m; r < n; r++) {
                  mutable.set(p, q, r);
                  FluidState fluidState = this.player.getWorld().getFluidState(mutable);
                  if (fluidState.isIn(tag)) {
                     double e = q + fluidState.getHeight(this.player.getWorld(), mutable);
                     if (e >= box.minY) {
                        foundFluid = true;
                        d = Math.max(e - box.minY, d);
                        if (pushedByFluids) {
                           Vec3d vel = fluidState.getVelocity(this.player.getWorld(), mutable);
                           if (d < 0.4) {
                              vel = vel.multiply(d);
                           }

                           fluidVelocity = fluidVelocity.add(vel);
                           count++;
                        }
                     }
                  }
               }
            }
         }

         if (fluidVelocity.length() > 0.0) {
            if (count > 0) {
               fluidVelocity = fluidVelocity.multiply(1.0 / count);
            }

            fluidVelocity = fluidVelocity.multiply(speed);
            if (Math.abs(this.velocity.x) < 0.003 && Math.abs(this.velocity.z) < 0.003 && fluidVelocity.length() < 0.0045) {
               fluidVelocity = fluidVelocity.normalize().multiply(0.0045);
            }

            this.velocity = this.velocity.add(fluidVelocity);
         }

         this.gott5gu.put(tag, d);
         return foundFluid;
      }
   }

   private boolean i78erFb() {
      Box box = this.boundingBox.expand(1.0);
      int i = MathHelper.floor(box.minX);
      int j = MathHelper.ceil(box.maxX);
      int k = MathHelper.floor(box.minZ);
      int l = MathHelper.ceil(box.maxZ);
      return !this.player.getWorld().isRegionLoaded(i, k, j, l);
   }

   private Vec3d getRotationVector() {
      return this.getRotationVector(this.pitch, this.yaw);
   }

   private Vec3d getRotationVector(float pitch, float yaw) {
      float f = (float)(pitch * Math.PI / 180.0);
      float g = (float)(-yaw * Math.PI / 180.0);
      float h = MathHelper.cos(g);
      float i = MathHelper.sin(g);
      float j = MathHelper.cos(f);
      float k = MathHelper.sin(f);
      return new Vec3d(i * j, -k, h * j);
   }

   public boolean hasStatusEffect(RegistryEntry<StatusEffect> effect) {
      StatusEffectInstance instance = this.player.getStatusEffect(effect);
      return instance != null && instance.getDuration() >= this.exIlsR;
   }

   private StatusEffectInstance k7H8(RegistryEntry<StatusEffect> effect) {
      StatusEffectInstance instance = this.player.getStatusEffect(effect);
      return instance != null && instance.getDuration() >= this.exIlsR ? instance : null;
   }

   public double getAttributeValue(RegistryEntry<EntityAttribute> attribute) {
      return this.player.getAttributes().getValue(attribute);
   }

   public SimulatedPlayer clone() {
      return new SimulatedPlayer(
         this.player,
         this.input,
         this.pos,
         this.velocity,
         this.boundingBox,
         this.yaw,
         this.pitch,
         this.sprinting,
         this.fallDistance,
         this.jumpingCooldown,
         this.isJumping,
         this.isFallFlying,
         this.onGround,
         this.horizontalCollision,
         this.verticalCollision,
         this.touchingWater,
         this.isSwimming,
         this.submergedInWater,
         new Object2DoubleArrayMap(this.gott5gu),
         new HashSet<>(this.smvaa7)
      );
   }

   public BlockPos posToBlockPos(Vec3d pos) {
      return new BlockPos(MathHelper.floor(pos.x), MathHelper.floor(pos.y), MathHelper.floor(pos.z));
   }

   public BlockState getState(BlockPos pos) {
      return this.player.getWorld().getBlockState(pos);
   }

   public static class SimulatedPlayerInput extends Input {
      public boolean forceSafeWalk = false;
      public float movementForward;
      public float movementSideways;
      public PlayerInput playerInput;
      public static final double MAX_WALKING_SPEED = 0.121;

      public SimulatedPlayerInput(PlayerInput input) {
         this.playerInput = input;
      }

      public void update() {
         if (this.playerInput.forward() != this.playerInput.backward()) {
            this.movementForward = this.playerInput.forward() ? 1.0F : -1.0F;
         } else {
            this.movementForward = 0.0F;
         }

         if (this.playerInput.left() == this.playerInput.right()) {
            this.movementSideways = 0.0F;
         } else {
            this.movementSideways = this.playerInput.left() ? 1.0F : -1.0F;
         }

         if (this.playerInput.sneak()) {
            this.movementSideways *= 0.3F;
            this.movementForward *= 0.3F;
         }
      }

      @Override
      public String toString() {
         return "SimulatedPlayerInput(forwards={"
            + this.playerInput.forward()
            + "}, backwards={"
            + this.playerInput.backward()
            + "}, left={"
            + this.playerInput.left()
            + "}, right={"
            + this.playerInput.right()
            + "}, jumping={"
            + this.playerInput.jump()
            + "}, sprinting="
            + this.playerInput.sprint()
            + ", slowDown="
            + this.playerInput.sneak()
            + ")";
      }

      public static SimulatedPlayer.SimulatedPlayerInput fromClientPlayer(PlayerInput input) {
         return new SimulatedPlayer.SimulatedPlayerInput(input);
      }

      public static SimulatedPlayer.SimulatedPlayerInput guessInput(PlayerEntity entity) {
         Vec3d velocity = entity.getPos().subtract(new Vec3d(entity.prevX, entity.prevY, entity.prevZ));
         double horizontalVelocity = velocity.horizontalLengthSquared();
         PlayerInput input = new PlayerInput(false, false, false, false, !entity.isOnGround(), entity.isSneaking(), horizontalVelocity >= 0.014641);
         if (horizontalVelocity > 0.0025000000000000005) {
            double velocityAngle = MoveUtil.getDegreesRelativeToView(velocity, entity.getYaw());
            double wrappedAngle = MathHelper.wrapDegrees(velocityAngle);
            input = MoveUtil.getDirectionalInputForDegrees(input, wrappedAngle);
         }

         return new SimulatedPlayer.SimulatedPlayerInput(input);
      }
   }
}
