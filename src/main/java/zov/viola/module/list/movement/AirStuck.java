package zov.viola.module.list.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.player.ElytraHelper;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;

@ModuleInformation(
   moduleName = "Air Stuck",
   moduleDesc = "Зависание в воздухе",
   moduleCategory = ModuleCategory.MOVEMENT
)
public class AirStuck extends Module {
   private final ModeSetting pjTwa = new ModeSetting("Мод", "Vanilla", "Vanilla", "Grim", "Polar");
   private final BooleanSetting tzxfh = new BooleanSetting(
      "Свап на нагрудник", true
   );
   private final BooleanSetting fm6p3k = new BooleanSetting(
         "Вернуть при выкл", true
      )
      .setVisible(this.tzxfh::getValue);
   private final BooleanSetting byKi = new BooleanSetting(
      "Проверка на падение",
      true
   );
   public final BooleanSetting useKillAuraDistance = new BooleanSetting(
      "Своя дистанция", false
   );
   public final SliderSetting killAuraDistance = new SliderSetting(
         "Дистанция", 6.0, 2.0, 8.0, 0.1
      )
      .setVisible(this.useKillAuraDistance::getValue);
   private Vec3d k2azDZo = Vec3d.ZERO;
   private boolean qwvqw;
   private boolean qxkv = false;
   private boolean pSiHG5 = false;
   private int bSPPVfb = 0;
   private int u62q1 = 0;

   @Subscribe
   private void onPacket(EventPacket e2) {
      if (this.mc.player != null && !this.qxkv && this.pSiHG5) {
         if (e2.getPacket() instanceof PlayerMoveC2SPacket) {
            if (this.u62q1 > 0) {
               return;
            }

            e2.cancelEvent();
         }
      }
   }

   @Subscribe
   private void onTick(EventTick e2) {
      if (this.mc.player != null && !this.qxkv) {
         if (!this.pSiHG5) {
            boolean airborne = !this.mc.player.isOnGround() && this.mc.player.fallDistance > 0.0F;
            if (airborne) {
               this.pSiHG5 = true;
               this.k2azDZo = this.mc.player.getVelocity();
               this.mc.player.setNoGravity(true);
            } else if (--this.bSPPVfb <= 0) {
               this.setEnabled(false);
            }
         } else if (this.u62q1 > 0) {
            this.u62q1--;
         } else {
            this.mc.player.setVelocity(0.0, 0.0, 0.0);
            this.mc.player.setNoGravity(true);
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.mc.player != null && this.mc.world != null) {
         this.qxkv = false;
         this.pSiHG5 = false;
         this.bSPPVfb = this.byKi.getValue() ? 60 : 15;
         this.k2azDZo = this.mc.player.getVelocity();
         boolean wearingElytra = this.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
         if (wearingElytra && this.tzxfh.getValue()) {
            this.qwvqw = true;
            Instance.get(ElytraHelper.class).swap(this.pjTwa, true);
         } else {
            this.qwvqw = false;
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.mc.player != null) {
         if (this.k2azDZo != null && !this.qxkv) {
            this.mc.player.setVelocity(this.k2azDZo);
         }

         this.mc.player.setNoGravity(false);
         if (this.qwvqw) {
            boolean wearingChestPlate = this.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ArmorItem;
            if (wearingChestPlate && this.fm6p3k.getValue()) {
               Instance.get(ElytraHelper.class).swap(this.pjTwa, false);
            }

            this.qwvqw = false;
         }

         this.qxkv = false;
         this.k2azDZo = Vec3d.ZERO;
      }
   }

   public void forceDisable() {
      if (this.isEnabled()) {
         this.qxkv = true;
         this.setEnabled(false);
      }
   }

   public void pauseForAttack() {
      this.u62q1 = 1;
   }
}
