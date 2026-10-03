package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import zov.viola.event.list.EventKeyInput;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;
import zov.viola.util.chat.ChatUtil;
import zov.viola.util.packet.NetworkUtils;
import zov.viola.util.player.other.InventoryUtil;

@ModuleInformation(
   moduleName = "Elytra Helper",
   moduleDesc = "Помощник для элитр",
   moduleCategory = ModuleCategory.PLAYER
)
public class ElytraHelper extends Module {
   private final ModeSetting fFcfq = new ModeSetting("Мод", "Vanilla", "Vanilla", "Grim", "Polar");
   private final BindSetting t9pO3c = new BindSetting(
      "Кнопка свапа", -1
   );
   private final BindSetting n98AWsq = new BindSetting(
      "Кнопка феерверка", -1
   );
   private final ModeSetting z27c8 = new ModeSetting(
      "Мод пуска феера",
      "Обычный",
      "Обычный",
      "Легитный"
   );
   private final BooleanSetting lyrS = new BooleanSetting(
      "Автовзлёт", true
   );
   private boolean vnRkD3D;
   private boolean e0IrkB7;
   private int qkuYpc = -1;

   @Subscribe
   private void onKey(EventKeyInput e2) {
      if (e2.getAction() != 0) {
         if (e2.getKey() == this.t9pO3c.getValue()) {
            this.e0IrkB7 = true;
         }

         if (e2.getKey() == this.n98AWsq.getValue() && this.mc.player.isGliding()) {
            this.vnRkD3D = true;
         }
      }
   }

   @Subscribe
   private void onPlayerUpdate(EventPlayerUpdate e2) {
      if (this.e0IrkB7) {
         this.e0IrkB7 = false;
         this.swap(this.fFcfq, this.mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA);
      }
   }

   @Subscribe
   private void onTick(EventTick e2) {
      if (this.mc.player != null) {
         if (this.lyrS.getValue()) {
            ItemStack chest = this.mc.player.getEquippedStack(EquipmentSlot.CHEST);
            if (chest.getItem() == Items.ELYTRA
               && !this.mc.player.isInLava()
               && !this.mc.player.isTouchingWater()
               && this.mc.player.isOnGround()
               && !this.mc.player.hasVehicle()
               && !this.mc.player.isGliding()
               && !this.mc.player.isSpectator()
               && !this.mc.options.jumpKey.isPressed()) {
               this.mc.player.jump();
            }

            if (chest.getItem() == Items.ELYTRA
               && !this.mc.player.isInLava()
               && !this.mc.player.isTouchingWater()
               && !this.mc.player.isOnGround()
               && !this.mc.player.hasVehicle()
               && !this.mc.player.isGliding()
               && !this.mc.player.isSpectator()) {
               NetworkUtils.sendSilentPacket(new ClientCommandC2SPacket(this.mc.player, Mode.START_FALL_FLYING));
               this.mc.player.startGliding();
            }
         }

         if (this.vnRkD3D) {
            String var4 = this.z27c8.getValue();
            switch (var4) {
               case "Обычный":
                  InventoryUtil.swapAndUseHvH(Items.FIREWORK_ROCKET);
                  break;
               case "Легитный":
                  InventoryUtil.swapAndUseLegit(Items.FIREWORK_ROCKET);
            }

            this.vnRkD3D = false;
         }
      }
   }

   public void swap(ModeSetting mode, boolean chestplate) {
      String var3 = mode.getValue();
      switch (var3) {
         case "Vanilla":
            this.mmrxJM(chestplate);
            break;
         case "Grim":
            this.mnFPC7(chestplate);
            break;
         case "Polar":
            this.hr31g(chestplate);
      }
   }

   private void mmrxJM(boolean chestplate) {
      if (chestplate) {
         this.fjY38tY();
      } else {
         this.weUK3fq();
      }
   }

   private void weUK3fq() {
      int slot = InventoryUtil.findBestElytraSlot();
      if (slot == -1) {
         ChatUtil.send("Элитра" + Formatting.GRAY + " не найдена в инвентаре");
      } else {
         if (slot >= 0 && slot <= 8) {
            this.qkuYpc = slot;
            this.mc.interactionManager.clickSlot(0, 6, slot, SlotActionType.SWAP, this.mc.player);
         } else if (slot >= 9 && slot <= 35) {
            this.qkuYpc = 8;
            this.mc.interactionManager.clickSlot(0, slot, 8, SlotActionType.SWAP, this.mc.player);
            this.mc.interactionManager.clickSlot(0, 6, 8, SlotActionType.SWAP, this.mc.player);
            this.mc.interactionManager.clickSlot(0, slot, 8, SlotActionType.SWAP, this.mc.player);
         }
      }
   }

   private void fjY38tY() {
      int chestplateSlot = InventoryUtil.findBestChestplateSlot();
      if (chestplateSlot == -1) {
         ChatUtil.send("Нагрудник" + Formatting.GRAY + " не найден в инвентаре");
      } else {
         if (this.qkuYpc >= 0 && this.qkuYpc <= 8) {
            if (chestplateSlot == this.qkuYpc) {
               this.mc.interactionManager.clickSlot(0, 6, this.qkuYpc, SlotActionType.SWAP, this.mc.player);
            } else {
               this.mc.interactionManager.clickSlot(0, chestplateSlot, this.qkuYpc, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, 6, this.qkuYpc, SlotActionType.SWAP, this.mc.player);
            }
         } else if (chestplateSlot >= 0 && chestplateSlot <= 8) {
            this.mc.interactionManager.clickSlot(0, 6, chestplateSlot, SlotActionType.SWAP, this.mc.player);
         } else if (chestplateSlot >= 9 && chestplateSlot <= 35) {
            this.mc.interactionManager.clickSlot(0, chestplateSlot, 8, SlotActionType.SWAP, this.mc.player);
            this.mc.interactionManager.clickSlot(0, 6, 8, SlotActionType.SWAP, this.mc.player);
            this.mc.interactionManager.clickSlot(0, chestplateSlot, 8, SlotActionType.SWAP, this.mc.player);
         }
      }
   }

   private void mnFPC7(boolean chestplate) {
      if (chestplate) {
         this.xyGv9();
      } else {
         this.kyUE8();
      }
   }

   private void kyUE8() {
      int slot = InventoryUtil.findBestElytraSlot();
      if (slot == -1) {
         ChatUtil.send("Элитра" + Formatting.GRAY + " не найдена в инвентаре");
      } else {
         if (slot >= 0 && slot <= 8) {
            this.qkuYpc = slot;
            InventoryUtil.swapWithBypassGrim(() -> this.mc.interactionManager.clickSlot(0, 6, slot, SlotActionType.SWAP, this.mc.player));
         } else if (slot >= 9 && slot <= 35) {
            this.qkuYpc = 8;
            InventoryUtil.swapWithBypassGrim(() -> {
               this.mc.interactionManager.clickSlot(0, slot, 8, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, 6, 8, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, slot, 8, SlotActionType.SWAP, this.mc.player);
            });
         }
      }
   }

   private void xyGv9() {
      int chestplateSlot = InventoryUtil.findBestChestplateSlot();
      if (chestplateSlot == -1) {
         ChatUtil.send("Нагрудник" + Formatting.GRAY + " не найден в инвентаре");
      } else {
         if (this.qkuYpc >= 0 && this.qkuYpc <= 8) {
            if (chestplateSlot == this.qkuYpc) {
               InventoryUtil.swapWithBypassGrim(() -> this.mc.interactionManager.clickSlot(0, 6, this.qkuYpc, SlotActionType.SWAP, this.mc.player));
            } else {
               InventoryUtil.swapWithBypassGrim(() -> {
                  this.mc.interactionManager.clickSlot(0, chestplateSlot, this.qkuYpc, SlotActionType.SWAP, this.mc.player);
                  this.mc.interactionManager.clickSlot(0, 6, this.qkuYpc, SlotActionType.SWAP, this.mc.player);
               });
            }
         } else if (chestplateSlot >= 0 && chestplateSlot <= 8) {
            InventoryUtil.swapWithBypassGrim(() -> this.mc.interactionManager.clickSlot(0, 6, chestplateSlot, SlotActionType.SWAP, this.mc.player));
         } else if (chestplateSlot >= 9 && chestplateSlot <= 35) {
            InventoryUtil.swapWithBypassGrim(() -> {
               this.mc.interactionManager.clickSlot(0, chestplateSlot, 8, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, 6, 8, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, chestplateSlot, 8, SlotActionType.SWAP, this.mc.player);
            });
         }
      }
   }

   private void hr31g(boolean chestplate) {
      if (chestplate) {
         this.kjDKPA();
      } else {
         this.kD7S();
      }
   }

   private void kD7S() {
      int slot = InventoryUtil.findBestElytraSlot();
      if (slot == -1) {
         ChatUtil.send("Элитра" + Formatting.GRAY + " не найдена в инвентаре");
      } else {
         if (slot >= 0 && slot <= 8) {
            this.qkuYpc = slot;
            InventoryUtil.swapWithBypassPolar(() -> this.mc.interactionManager.clickSlot(0, 6, slot, SlotActionType.SWAP, this.mc.player));
         } else if (slot >= 9 && slot <= 35) {
            this.qkuYpc = 8;
            InventoryUtil.swapWithBypassPolar(() -> {
               this.mc.interactionManager.clickSlot(0, slot, 8, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, 6, 8, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, slot, 8, SlotActionType.SWAP, this.mc.player);
            });
         }
      }
   }

   private void kjDKPA() {
      int chestplateSlot = InventoryUtil.findBestChestplateSlot();
      if (chestplateSlot == -1) {
         ChatUtil.send("Нагрудник" + Formatting.GRAY + " не найден в инвентаре");
      } else {
         if (this.qkuYpc >= 0 && this.qkuYpc <= 8) {
            if (chestplateSlot == this.qkuYpc) {
               InventoryUtil.swapWithBypassPolar(() -> this.mc.interactionManager.clickSlot(0, 6, this.qkuYpc, SlotActionType.SWAP, this.mc.player));
            } else {
               InventoryUtil.swapWithBypassPolar(() -> {
                  this.mc.interactionManager.clickSlot(0, chestplateSlot, this.qkuYpc, SlotActionType.SWAP, this.mc.player);
                  this.mc.interactionManager.clickSlot(0, 6, this.qkuYpc, SlotActionType.SWAP, this.mc.player);
               });
            }
         } else if (chestplateSlot >= 0 && chestplateSlot <= 8) {
            InventoryUtil.swapWithBypassPolar(() -> this.mc.interactionManager.clickSlot(0, 6, chestplateSlot, SlotActionType.SWAP, this.mc.player));
         } else if (chestplateSlot >= 9 && chestplateSlot <= 35) {
            InventoryUtil.swapWithBypassPolar(() -> {
               this.mc.interactionManager.clickSlot(0, chestplateSlot, 8, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, 6, 8, SlotActionType.SWAP, this.mc.player);
               this.mc.interactionManager.clickSlot(0, chestplateSlot, 8, SlotActionType.SWAP, this.mc.player);
            });
         }
      }
   }
}
