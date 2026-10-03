package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.event.list.MoveInputEvent;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;
import zov.viola.util.math.StopWatch;
import zov.viola.util.packet.NetworkUtils;
import zov.viola.util.player.other.InventoryUtil;
import zov.viola.util.render.math.GCDFixer;
import zov.viola.util.rotation.Rotation;
import zov.viola.util.rotation.RotationComponent;

@ModuleInformation(
   moduleName = "ElytraFlight",
   moduleDesc = "Быстрое и четкое передвижение на элитрах",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class ElytraFlight extends Module {
   private final SliderSetting vh6s = new SliderSetting(
      "Задержка феера",
      750.0,
      100.0,
      2000.0,
      50.0
   );
   private final StopWatch oJfkmS = new StopWatch();
   private Vec2f gnge = new Vec2f(0.0F, 0.0F);
   private float tr9lX8 = -2.1474836E9F;
   private float ih7S0 = -2.1474836E9F;
   private boolean e0Pb4 = false;

   @Subscribe
   private void onStrafe(MoveInputEvent e) {
      if (this.mc.player != null && this.mc.player.isGliding()) {
         if (this.e0Pb4) {
            if (this.tr9lX8 != -2.1474836E9F && this.ih7S0 != -2.1474836E9F) {
               RotationComponent.fixMovement(e, MathHelper.wrapDegrees(this.mc.gameRenderer.getCamera().getYaw()), this.tr9lX8);
            }
         }
      }
   }

   @Subscribe
   private void onUpdate(EventPlayerUpdate e) {
      if (this.mc.player != null) {
         if (!this.mc.player.isGliding() && this.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            if (this.mc.player.isOnGround()) {
               this.mc.player.jump();
            } else {
               NetworkUtils.sendPacket(new ClientCommandC2SPacket(this.mc.player, Mode.START_FALL_FLYING));
               this.mc.player.startGliding();
               InventoryUtil.swapAndUseHvH(Items.FIREWORK_ROCKET);
               this.oJfkmS.reset();
            }
         } else if (this.mc.player.isGliding()) {
            KillAura killAura = Instance.get(KillAura.class);
            boolean killauraActive = killAura != null && killAura.isEnabled() && killAura.getTarget() != null;
            if (this.oJfkmS.isReached((long)this.vh6s.getValue())) {
               InventoryUtil.swapAndUseHvH(Items.FIREWORK_ROCKET);
               this.oJfkmS.reset();
            }

            boolean forward = this.mc.options.forwardKey.isPressed();
            boolean back = this.mc.options.backKey.isPressed();
            boolean left = this.mc.options.leftKey.isPressed();
            boolean right = this.mc.options.rightKey.isPressed();
            boolean jump = this.mc.options.jumpKey.isPressed();
            boolean sneak = this.mc.options.sneakKey.isPressed();
            boolean isMoving = forward || back || left || right;
            float cameraYaw = this.mc.gameRenderer.getCamera().getYaw();
            float pitch = 0.0F;
            float yaw;
            if (isMoving) {
               float yawOffset = 0.0F;
               if (forward) {
                  if (left) {
                     yawOffset = -45.0F;
                  } else if (right) {
                     yawOffset = 45.0F;
                  }
               } else if (back) {
                  yawOffset = 180.0F;
                  if (left) {
                     yawOffset = -135.0F;
                  } else if (right) {
                     yawOffset = 135.0F;
                  }
               } else if (left) {
                  yawOffset = -90.0F;
               } else if (right) {
                  yawOffset = 90.0F;
               }

               yaw = cameraYaw + yawOffset;
            } else {
               yaw = cameraYaw;
            }

            if (jump) {
               pitch = isMoving ? -25.0F : -90.0F;
            } else if (sneak) {
               pitch = isMoving ? 25.0F : 90.0F;
            }

            boolean hasAnyInput = isMoving || jump || sneak;
            this.e0Pb4 = hasAnyInput;
            if (!killauraActive && isMoving) {
               float motion = pitch == -25.0F || pitch == 25.0F ? 0.5F : (pitch != 90.0F && pitch != -90.0F ? 1.0F : 0.25F);
               double rad = Math.toRadians(yaw + 90.0F);
               double motionX = Math.cos(rad) * motion;
               double motionZ = Math.sin(rad) * motion;
               double vy = 0.0;
               if (pitch == -25.0F) {
                  vy = 0.5;
               } else if (pitch == -90.0F) {
                  vy = 1.0;
               } else if (pitch == 25.0F) {
                  vy = -0.5;
               } else if (pitch == 90.0F) {
                  vy = -1.0;
               }

               this.mc.player.setVelocity(motionX, vy, motionZ);
            } else if (!killauraActive && !isMoving && (jump || sneak)) {
               double vy = jump ? 1.0 : -1.0;
               this.mc.player.setVelocity(0.0, vy, 0.0);
            } else if (!killauraActive && !hasAnyInput) {
               this.mc.player.setVelocity(0.0, 0.0, 0.0);
            }

            if (hasAnyInput && !killauraActive) {
               yaw -= (yaw - this.gnge.x) % GCDFixer.getGCDValue();
               pitch -= (pitch - this.gnge.y) % GCDFixer.getGCDValue();
               this.gnge = new Vec2f(yaw, MathHelper.clamp(pitch, -90.0F, 90.0F));
               this.tr9lX8 = this.gnge.x;
               this.ih7S0 = this.gnge.y;
               RotationComponent.update(new Rotation(this.gnge.x, this.gnge.y), 360.0F, 360.0F, 360.0F, 360.0F, 0, 3, false);
               this.mc.player.setYaw(this.gnge.x);
               this.mc.player.setPitch(this.gnge.y);
               this.mc.player.headYaw = this.gnge.x;
               this.mc.player.bodyYaw = this.gnge.x;
            }
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.oJfkmS.reset();
      this.gnge = new Vec2f(this.mc.player != null ? this.mc.player.getYaw() : 0.0F, 0.0F);
      this.tr9lX8 = -2.1474836E9F;
      this.ih7S0 = -2.1474836E9F;
      this.e0Pb4 = false;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.tr9lX8 = -2.1474836E9F;
      this.ih7S0 = -2.1474836E9F;
      this.e0Pb4 = false;
   }
}
