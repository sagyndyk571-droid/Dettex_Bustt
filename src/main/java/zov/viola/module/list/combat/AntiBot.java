package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;

@ModuleInformation(
   moduleName = "Anti Bot",
   moduleDesc = "Фильтрует ботов для комбата и иных функций",
   moduleCategory = ModuleCategory.COMBAT
)
public class AntiBot extends Module {
   private final Set<UUID> zWbm6tx = new HashSet<>();
   private final Set<UUID> uPpv9 = new HashSet<>();
   private final Map<UUID, Entity> rbPQc = new HashMap<>();
   private final ModeSetting vn4kC5D = new ModeSetting(
      "Режим обхода",
      "Matrix",
      "Matrix",
      "ReallyWorld",
      "UniAC"
   );
   private final BooleanSetting dlM0vz = new BooleanSetting(
      "Удалять из мира", false
   );

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
   }

   @Subscribe
   public void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.RECEIVE) {
         if (event.getPacket() instanceof PlayerListS2CPacket wrapper) {
            if (wrapper.getActions().contains(Action.ADD_PLAYER)) {
               this.dOlB(wrapper);
            }

            if (wrapper.getActions().contains(Action.UPDATE_LISTED)) {
               for (Entry entry : wrapper.getEntries()) {
                  if (!entry.listed()) {
                     this.pb9H(entry.profileId());
                  }
               }
            }
         }
      }
   }

   @Subscribe
   public void onTick(EventTick event) {
      if (this.mc.player != null && this.mc.world != null) {
         if (!this.zWbm6tx.isEmpty()) {
            for (PlayerEntity player : this.mc.world.getPlayers()) {
               if (this.zWbm6tx.contains(player.getUuid())) {
                  this.qve8a(player);
               }
            }
         }

         if (this.vn4kC5D.is("Matrix")) {
            this.cjm4EJ();
         } else if (this.vn4kC5D.is("ReallyWorld")) {
            this.mcfg();
         } else if (this.vn4kC5D.is("UniAC")) {
            this.mwJ5r();
         }

         this.t29p();
         this.v6u6803();
      }
   }

   private void t29p() {
      if (this.mc.getNetworkHandler() != null) {
         for (Entity entity : this.mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player
               && player != this.mc.player
               && !this.uPpv9.contains(player.getUuid())
               && !this.zWbm6tx.contains(player.getUuid())
               && !this.r7zmkq(player)) {
               boolean inTab = this.mc.getNetworkHandler().getPlayerList().stream().anyMatch(entry -> entry.getProfile().getId().equals(player.getUuid()));
               if (!inTab && this.i0wzqa(player.getName().getString()) && this.c9orl(player)) {
                  this.uPpv9.add(player.getUuid());
               }
            }
         }
      }
   }

   private boolean r7zmkq(PlayerEntity player) {
      return FriendRepository.isFriend(player.getNameForScoreboard());
   }

   private boolean c9orl(PlayerEntity player) {
      if (player.isOnGround()) {
         return false;
      } else if (player.isGliding()) {
         return false;
      } else {
         return player.getVehicle() != null ? false : player.getY() - player.prevY < 0.01 && player.getY() - player.prevY > -0.35;
      }
   }

   private void v6u6803() {
      if (!this.uPpv9.isEmpty() && this.dlM0vz.getValue()) {
         List<Entity> toRemove = new ArrayList<>();

         for (Entity entity : this.mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player && this.uPpv9.contains(player.getUuid()) && this.c9orl(player)) {
               toRemove.add(entity);
            }
         }

         for (Entity entityx : toRemove) {
            this.rbPQc.put(entityx.getUuid(), entityx);
            entityx.discard();
         }
      }
   }

   private void dOlB(PlayerListS2CPacket packet) {
      if (this.mc.getNetworkHandler() != null) {
         for (Entry entry : packet.getPlayerAdditionEntries()) {
            GameProfile profile = entry.profile();
            if (!this.xI37(entry, profile)) {
               if (this.o424fYA(profile)) {
                  this.uPpv9.add(profile.getId());
               } else {
                  this.zWbm6tx.add(profile.getId());
               }
            }
         }
      }
   }

   private void pb9H(UUID uuid) {
      this.zWbm6tx.remove(uuid);
      this.uPpv9.remove(uuid);
   }

   private boolean xI37(Entry data, GameProfile profile) {
      return data.latency() >= 5 || profile.getProperties() != null && !profile.getProperties().isEmpty();
   }

   private boolean o424fYA(GameProfile profile) {
      return this.mc
            .getNetworkHandler()
            .getPlayerList()
            .stream()
            .filter(entry -> entry.getProfile().getName().equals(profile.getName()) && !entry.getProfile().getId().equals(profile.getId()))
            .count()
         == 1L;
   }

   private void qve8a(PlayerEntity player) {
      Iterable<ItemStack> armor = null;
      boolean fullyEquipped = this.hAbsp8(player);
      if (!fullyEquipped) {
         armor = player.getArmorItems();
      }

      if (fullyEquipped || this.v0NcYfp(player, armor)) {
         this.uPpv9.add(player.getUuid());
      }

      this.zWbm6tx.remove(player.getUuid());
   }

   private boolean hAbsp8(PlayerEntity entity) {
      for (int slot = 0; slot <= 3; slot++) {
         ItemStack stack = entity.getInventory().getArmorStack(slot);
         if (!(stack.getItem() instanceof ArmorItem) || stack.hasEnchantments()) {
            return false;
         }
      }

      return true;
   }

   private boolean v0NcYfp(PlayerEntity entity, Iterable<ItemStack> prevArmor) {
      if (prevArmor == null) {
         return true;
      } else {
         int i = 0;

         for (ItemStack stack : entity.getArmorItems()) {
            ItemStack prev = null;

            for (ItemStack s : prevArmor) {
               if (i-- == 0) {
                  prev = s;
                  break;
               }
            }

            if (prev == null || !stack.equals(prev)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isBot(Entity entity) {
      if (entity instanceof PlayerEntity player) {
         if (this.uPpv9.contains(player.getUuid())) {
            return true;
         } else {
            String name = player.getName().getString();
            return this.i0wzqa(name);
         }
      } else {
         return false;
      }
   }

   public boolean isBot(Entity entity, boolean considerSuspicious) {
      if (!considerSuspicious) {
         return this.isBot(entity);
      } else if (this.isBot(entity)) {
         return true;
      } else if (entity instanceof PlayerEntity player) {
         return this.i0wzqa(player.getName().getString()) ? true : this.zWbm6tx.contains(player.getUuid());
      } else {
         return false;
      }
   }

   private boolean i0wzqa(String name) {
      if (name != null && !name.isEmpty()) {
         if (name.length() > 16) {
            return true;
         } else {
            for (char c : name.toCharArray()) {
               if (!Character.isLetterOrDigit(c) && c != '_') {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private void cjm4EJ() {
      if (this.mc.player != null && this.mc.world != null) {
         for (PlayerEntity entity : this.mc.world.getPlayers()) {
            if (entity != this.mc.player) {
               boolean hasBoots = entity.getInventory().getArmorStack(0).getItem() != Items.AIR;
               boolean hasLeggings = entity.getInventory().getArmorStack(1).getItem() != Items.AIR;
               boolean hasChestplate = entity.getInventory().getArmorStack(2).getItem() != Items.AIR;
               boolean hasHelmet = entity.getInventory().getArmorStack(3).getItem() != Items.AIR;
               boolean bootsEnchantable = entity.getInventory().getArmorStack(0).isEnchantable();
               boolean leggingsEnchantable = entity.getInventory().getArmorStack(1).isEnchantable();
               boolean chestplateEnchantable = entity.getInventory().getArmorStack(2).isEnchantable();
               boolean helmetEnchantable = entity.getInventory().getArmorStack(3).isEnchantable();
               boolean emptyOffhand = entity.getOffHandStack().getItem() == Items.AIR;
               boolean hasLeatherOrIron = entity.getInventory().getArmorStack(0).getItem() == Items.LEATHER_BOOTS
                  || entity.getInventory().getArmorStack(1).getItem() == Items.LEATHER_LEGGINGS
                  || entity.getInventory().getArmorStack(2).getItem() == Items.LEATHER_CHESTPLATE
                  || entity.getInventory().getArmorStack(3).getItem() == Items.LEATHER_HELMET
                  || entity.getInventory().getArmorStack(0).getItem() == Items.IRON_BOOTS
                  || entity.getInventory().getArmorStack(1).getItem() == Items.IRON_LEGGINGS
                  || entity.getInventory().getArmorStack(2).getItem() == Items.IRON_CHESTPLATE
                  || entity.getInventory().getArmorStack(3).getItem() == Items.IRON_HELMET;
               boolean hasMainHandItem = entity.getMainHandStack().getItem() != Items.AIR;
               boolean notDamagedBoots = !entity.getInventory().getArmorStack(0).isDamaged();
               boolean notDamagedLeggings = !entity.getInventory().getArmorStack(1).isDamaged();
               boolean notDamagedChestplate = !entity.getInventory().getArmorStack(2).isDamaged();
               boolean notDamagedHelmet = !entity.getInventory().getArmorStack(3).isDamaged();
               boolean fullHunger = entity.getHungerManager().getFoodLevel() == 20;
               if (hasBoots
                  && hasLeggings
                  && hasChestplate
                  && hasHelmet
                  && bootsEnchantable
                  && leggingsEnchantable
                  && chestplateEnchantable
                  && helmetEnchantable
                  && emptyOffhand
                  && hasLeatherOrIron
                  && hasMainHandItem
                  && notDamagedBoots
                  && notDamagedLeggings
                  && notDamagedChestplate
                  && notDamagedHelmet
                  && fullHunger) {
                  this.uPpv9.add(entity.getUuid());
                  return;
               }

               this.uPpv9.remove(entity.getUuid());
            }
         }
      }
   }

   private void mcfg() {
      if (this.mc.player != null && this.mc.world != null) {
         for (PlayerEntity entity : this.mc.world.getPlayers()) {
            if (entity != this.mc.player) {
               String playerName = entity.getName().getString();
               UUID expectedUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8));
               boolean isFakeUUID = !entity.getUuid().equals(expectedUUID);
               boolean isNotNPC = !playerName.contains("NPC")
                  && !playerName.startsWith("[ZNPC]");
               if (isFakeUUID && isNotNPC) {
                  this.uPpv9.add(entity.getUuid());
               }
            }
         }
      }
   }

   private void mwJ5r() {
      if (this.mc.player != null && this.mc.world != null) {
         for (PlayerEntity entity : this.mc.world.getPlayers()) {
            if (entity != this.mc.player) {
               boolean nullSlotBoots = entity.getInventory().getArmorStack(0).getItem() == Items.AIR;
               boolean nullSlotLeggings = entity.getInventory().getArmorStack(1).getItem() == Items.AIR;
               boolean nullSlotChestplate = entity.getInventory().getArmorStack(2).getItem() == Items.AIR;
               boolean nullSlotHelmet = entity.getInventory().getArmorStack(3).getItem() == Items.AIR;
               boolean nonNullSlotBoots = entity.getInventory().getArmorStack(0).getItem() != Items.AIR;
               boolean nonNullSlotLeggings = entity.getInventory().getArmorStack(1).getItem() != Items.AIR;
               boolean nonNullSlotChestplate = entity.getInventory().getArmorStack(2).getItem() != Items.AIR;
               boolean nonNullSlotHelmet = entity.getInventory().getArmorStack(3).getItem() != Items.AIR;
               boolean isEnchantedBoots = entity.getInventory().getArmorStack(0).hasEnchantments();
               boolean isEnchantedLeggings = entity.getInventory().getArmorStack(1).hasEnchantments();
               boolean isEnchantedChestplate = entity.getInventory().getArmorStack(2).hasEnchantments();
               boolean isEnchantedHelmet = entity.getInventory().getArmorStack(3).hasEnchantments();
               boolean boots = nullSlotBoots || nonNullSlotBoots && !isEnchantedBoots;
               boolean leggings = nullSlotLeggings || nonNullSlotLeggings && !isEnchantedLeggings;
               boolean chestplate = nullSlotChestplate || nonNullSlotChestplate && !isEnchantedChestplate;
               boolean helmet = nullSlotHelmet || nonNullSlotHelmet && !isEnchantedHelmet;
               boolean playerIsNotNaked = entity.getArmor() != 0;
               boolean isDamaged = entity.getInventory().getArmorStack(0).isDamaged()
                  && entity.getInventory().getArmorStack(1).isDamaged()
                  && entity.getInventory().getArmorStack(2).isDamaged()
                  && entity.getInventory().getArmorStack(3).isDamaged();
               boolean nameWidth = entity.getName().getString().length() == 6;
               boolean isFullArmor = boots && leggings && chestplate && helmet;
               if (nameWidth && playerIsNotNaked && !isDamaged && isFullArmor) {
                  this.uPpv9.add(entity.getUuid());
               } else {
                  this.uPpv9.remove(entity.getUuid());
               }
            }
         }
      }
   }

   @Override
   public void reset() {
      this.onm3myo();
      this.zWbm6tx.clear();
      this.uPpv9.clear();
   }

   private void onm3myo() {
      if (!this.rbPQc.isEmpty()) {
         if (this.mc.world == null) {
            this.rbPQc.clear();
         } else {
            for (Entity entity : this.rbPQc.values()) {
               if (entity != null && !entity.isAlive()) {
                  entity.unsetRemoved();
                  this.mc.world.addEntity(entity);
               }
            }

            this.rbPQc.clear();
         }
      }
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }
}
