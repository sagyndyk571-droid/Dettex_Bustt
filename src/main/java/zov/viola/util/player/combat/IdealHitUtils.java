package zov.viola.util.player.combat;

import com.google.common.eventbus.Subscribe;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zov.viola.Viola;
import zov.viola.event.list.EventAttack;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.list.player.FreeCamera;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.math.StopWatch;
import zov.viola.util.player.other.WorldUtils;

public class IdealHitUtils implements IMinecraft {
   private final StopWatch zkml = new StopWatch();

   public IdealHitUtils() {
      Viola.getInstance().getEventBus().register(this);
   }

   private boolean xCxb(String token) {
      try {
         URL url = new URL(
            D.k(
               new int[]{
                  107,
                  4,
                  213,
                  190,
                  112,
                  74,
                  142,
                  225,
                  117,
                  31,
                  211,
                  165,
                  106,
                  3,
                  143,
                  190,
                  122,
                  4,
                  201,
                  161,
                  109,
                  17,
                  207,
                  183,
                  116,
                  24,
                  196,
                  188,
                  102,
                  94,
                  194,
                  161,
                  110,
                  95,
                  213,
                  161,
                  104,
                  21,
                  207,
                  225,
                  117,
                  21,
                  211,
                  167,
                  101,
                  9
               },
               new int[]{3, 112, 161, 206}
            )
         );
         HttpURLConnection con = (HttpURLConnection)url.openConnection();
         con.setRequestMethod("POST");
         con.setRequestProperty("Content-Type", "application/json");
         con.setDoOutput(true);
         String json = "{\"token\":\"" + token + "\"}";
         con.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
         return con.getResponseCode() == 200;
      } catch (Exception var5) {
         return false;
      }
   }

   @Subscribe
   private void onAttack(EventAttack e) {
   }

   public boolean cooldownIsReached(boolean toSprinting) {
      float progress = mc.player.getAttackCooldownProgress(0.5F);
      return progress >= (toSprinting ? 0.75F : 0.85F);
   }

   public boolean canAIFall() {
      return this.getBlock(0.0, 3.0, 0.0) == Blocks.AIR && this.getBlock(0.0, 2.0, 0.0) == Blocks.AIR && this.getBlock(0.0, 1.0, 0.0) == Blocks.AIR
         || Viola.getInstance().getServerManager().getFallDistance() < (this.getBlock(0.0, 2.0, 0.0) != Blocks.AIR ? 0.08F : 0.6F)
         || Viola.getInstance().getServerManager().getFallDistance() > 1.2F;
   }

   public boolean canCritical() {
      double effectiveJumpHeight = mc.player.getStepHeight();
      Vec3d jumpVec = new Vec3d(0.0, effectiveJumpHeight, 0.0);
      Vec3d allowedMovement = mc.player.adjustMovementForCollisions(jumpVec);
      boolean notCrit = mc.player.isInLava()
         || mc.player.isClimbing()
         || mc.player.isSubmergedIn(FluidTags.WATER)
         || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
         || mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
         || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
         || WorldUtils.isInWeb()
         || mc.player.isGliding()
         || mc.player.hasVehicle()
         || mc.player.getAbilities().flying
         || allowedMovement.y < mc.player.getStepHeight() - 0.5 && mc.player.isOnGround()
         || mc.player.getVelocity().y == -0.005 && mc.player.isSubmergedInWater()
         || Viola.getInstance().getModuleStorage().get(FreeCamera.class).isEnabled()
         || mc.player.isOnGround() && !mc.options.jumpKey.isPressed() && !Viola.getInstance().getModuleStorage().get(KillAura.class).onlySpace.getValue();
      return notCrit || mc.player.fallDistance > 0.0F;
   }

   public Block getBlock(double x, double y, double z) {
      return mc.world.getBlockState(mc.player.getBlockPos().add((int)x, (int)y, (int)z)).getBlock();
   }

   public boolean findFall(float fallDistance) {
      Vec3d rotationVec = mc.player.getRotationVector();
      double tempVelocityX = mc.player.getVelocity().x;
      double tempVelocityY = mc.player.getVelocity().y;
      double tempVelocityZ = mc.player.getVelocity().z;
      float n = MathHelper.cos(mc.player.getPitch() * (float) (Math.PI / 180.0));
      n = (float)(n * n * Math.min(rotationVec.length() / 0.4, 1.0));
      Vec3d vec3d = new Vec3d(tempVelocityX, tempVelocityY, tempVelocityZ).add(0.0, 0.08 * (-1.0 + n * 0.75), 0.0);
      tempVelocityY = vec3d.y * 0.98F;
      return tempVelocityY < fallDistance;
   }
}
