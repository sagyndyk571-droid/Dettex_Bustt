package zov.viola.util.player.other;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.mojang.text2speech.Narrator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.collection.DefaultedList;
import zov.viola.Viola;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.list.movement.Sprint;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.base.Instance;
import zov.viola.util.packet.NetworkUtils;

public class InventoryUtil implements IMinecraft {
   public static final InventoryUtil instance = new InventoryUtil();
   private static boolean h4UeY50;
   private static int kY4p;
   private static long cjp6;
   private static int bbfpO = -1;
   private static int o9LcrCa;
   private static final InventoryUtil.StagedTicker STAGED_TICKER = new InventoryUtil.StagedTicker();
   private static boolean k9ca;

   public static int searchItem(Item item) {
      for (int i = 0; i < mc.player.getInventory().getChangeCount(); i++) {
         if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
            return i;
         }
      }

      return -1;
   }

   public static int searchItem(Item item, int start, int end) {
      for (int i = start; i < end; i++) {
         if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
            return i;
         }
      }

      return -1;
   }

   public static int searchItem(List<Item> items) {
      for (int i = 0; i < mc.player.getInventory().getChangeCount(); i++) {
         for (Item item : items) {
            if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int searchItemHotbar(Item item) {
      for (int i = 0; i < 9; i++) {
         if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
            return i;
         }
      }

      return -1;
   }

   public static int searchItemHotbar(List<Item> items) {
      for (int i = 0; i < 9; i++) {
         for (Item item : items) {
            if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int searchItemStack(Predicate<ItemStack> predicate) {
      for (int i = 0; i < 45; i++) {
         ItemStack stack = mc.player.getInventory().getStack(i);
         if (!stack.isEmpty() && predicate.test(stack)) {
            return i;
         }
      }

      return -1;
   }

   public static int searchHotbarStack(Predicate<ItemStack> predicate) {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = mc.player.getInventory().getStack(i);
         if (!stack.isEmpty() && predicate.test(stack)) {
            return i;
         }
      }

      return -1;
   }

   public static void swapWithBypassGrim(Runnable runnable) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         try {
            if (Viola.getInstance().getServerManager().isServerSprinting()) {
               mc.player.setSprinting(false);
               mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
               if (!Instance.get(Sprint.class).isEnabled()) {
                  mc.options.sprintKey.setPressed(false);
               }
            }

            PlayerInput input = mc.player.input.playerInput;
            mc.getNetworkHandler()
               .sendPacket(
                  new PlayerInputC2SPacket(new PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), false, input.sneak()))
               );
            runnable.run();
            NetworkUtils.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
            mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   public static void swapWithBypassPolar(Runnable runnable) {
      swapWithBypassPolar(runnable, 100L);
   }

   public static void swapWithBypassPolar(Runnable runnable, long duration) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         try {
            SlownessManager.addTask(new SlownessManager.SlowTask(duration, () -> {
               if (mc.player.isUsingItem()) {
                  mc.interactionManager.stopUsingItem(mc.player);
               }

               runnable.run();
               NetworkUtils.sendSilentPacket(new CloseHandledScreenC2SPacket(0));
            }));
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }
   }

   public static void swapAndUseHvH(Item item) {
      if (!mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(item))) {
         int slot = searchItem(item, 9, 45);
         int slotHotbar = searchItem(item, 0, 9);
         int previousSlot = mc.player.getInventory().selectedSlot;
         if (mc.player.getMainHandStack().getItem() == item) {
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
         } else if (mc.player.getOffHandStack().getItem() == item) {
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
         } else {
            if (slotHotbar != -1) {
               mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slotHotbar));
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
            }

            if (slotHotbar == -1 && slot != -1) {
               int slotCorrectable = -1;

               for (int slotNone = 0; slotNone < 8; slotNone++) {
                  ItemStack stack = mc.player.getInventory().getStack(slotNone);
                  if (stack.isEmpty()) {
                     slotCorrectable = slotNone;
                  }

                  UseAction action = stack.getUseAction();
                  if (action == UseAction.NONE) {
                     slotCorrectable = slotNone;
                  }
               }

               if (slotCorrectable == -1) {
                  if (Viola.getInstance().getServerManager().isServerSprinting()) {
                     mc.player.setSprinting(false);
                     mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
                     if (!Instance.get(Sprint.class).isEnabled()) {
                        mc.options.sprintKey.setPressed(false);
                     }
                  }

                  PlayerInput input = mc.player.input.playerInput;
                  mc.getNetworkHandler()
                     .sendPacket(
                        new PlayerInputC2SPacket(
                           new PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), false, input.sneak())
                        )
                     );
                  mc.interactionManager.clickSlot(0, slot, 8, SlotActionType.SWAP, mc.player);
                  mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                  mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(8));
                  mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                  mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
                  mc.interactionManager.clickSlot(0, slot, 8, SlotActionType.SWAP, mc.player);
                  mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                  mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
               } else {
                  if (Viola.getInstance().getServerManager().isServerSprinting()) {
                     mc.player.setSprinting(false);
                     mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
                     if (!Instance.get(Sprint.class).isEnabled()) {
                        mc.options.sprintKey.setPressed(false);
                     }
                  }

                  PlayerInput input = mc.player.input.playerInput;
                  mc.getNetworkHandler()
                     .sendPacket(
                        new PlayerInputC2SPacket(
                           new PlayerInput(input.forward(), input.backward(), input.left(), input.right(), input.jump(), false, input.sneak())
                        )
                     );
                  mc.interactionManager.clickSlot(0, slot, slotCorrectable, SlotActionType.SWAP, mc.player);
                  mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                  mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slotCorrectable));
                  mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                  mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
                  mc.interactionManager.clickSlot(0, slot, slotCorrectable, SlotActionType.SWAP, mc.player);
                  mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                  mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
               }
            }
         }
      }
   }

   public static synchronized void swapAndUseHvHStaged(Item item) {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         if (!h4UeY50) {
            if (!mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(item))) {
               if (mc.player.getMainHandStack().getItem() == item) {
                  mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               } else if (mc.player.getOffHandStack().getItem() == item) {
                  mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
               } else {
                  int slotHotbar = searchItem(item, 0, 9);
                  if (slotHotbar != -1) {
                     mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slotHotbar));
                     mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                     mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                  } else {
                     int invSlot = searchItem(item, 9, 36);
                     if (invSlot != -1) {
                        bbfpO = invSlot;
                        o9LcrCa = mc.player.getInventory().selectedSlot;
                        kY4p = 0;
                        cjp6 = System.currentTimeMillis();
                        h4UeY50 = true;
                        if (!k9ca) {
                           Viola.getInstance().getEventBus().register(STAGED_TICKER);
                           k9ca = true;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void suxj() {
      if (h4UeY50) {
         if (mc.player != null && mc.getNetworkHandler() != null) {
            long now = System.currentTimeMillis();
            if (now >= cjp6) {
               try {
                  switch (kY4p) {
                     case 0:
                        mc.player.getInventory().selectedSlot = 8;
                        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(8));
                        mc.interactionManager.clickSlot(0, bbfpO, 8, SlotActionType.SWAP, mc.player);
                        mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                        kY4p = 1;
                        cjp6 = now + 500L;
                        break;
                     case 1:
                        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                        kY4p = 2;
                        cjp6 = now + 500L;
                        break;
                     case 2:
                        mc.interactionManager.clickSlot(0, bbfpO, 8, SlotActionType.SWAP, mc.player);
                        mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                        mc.player.getInventory().selectedSlot = o9LcrCa;
                        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(o9LcrCa));
                        mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
                        h4UeY50 = false;
                  }
               } catch (Throwable var5) {
                  h4UeY50 = false;

                  try {
                     mc.player.getInventory().selectedSlot = o9LcrCa;
                  } catch (Throwable var4) {
                  }
               }
            }
         } else {
            h4UeY50 = false;
         }
      }
   }

   public static void swapAndUseLegit(Item item) {
      if (mc.player != null && mc.world != null) {
         if (!mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(item))) {
            int slot = searchItem(item, 9, 45);
            int slotHotbar = searchItem(item, 0, 9);
            int previousSlot = mc.player.getInventory().selectedSlot;
            if (mc.player.getMainHandStack().getItem() == item) {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            } else if (mc.player.getOffHandStack().getItem() == item) {
               mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
            } else if (slotHotbar != -1) {
               mc.player.getInventory().selectedSlot = slotHotbar;
               mc.interactionManager.syncSelectedSlot();
               mc.interactionManager
                  .sendSequencedPacket(
                     mc.world, sequence -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch())
                  );
               mc.player.getInventory().selectedSlot = previousSlot;
               mc.interactionManager.syncSelectedSlot();
            } else {
               if (slotHotbar == -1 && slot != -1) {
                  int slotCorrectable = -1;

                  for (int slotNone = 0; slotNone < 9; slotNone++) {
                     ItemStack stack = mc.player.getInventory().getStack(slotNone);
                     if (stack.isEmpty()) {
                        slotCorrectable = slotNone;
                        break;
                     }

                     UseAction action = stack.getUseAction();
                     if (action == UseAction.NONE) {
                        slotCorrectable = slotNone;
                     }
                  }

                  if (slotCorrectable != -1) {
                     mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
                     mc.interactionManager.clickSlot(0, slot, slotCorrectable, SlotActionType.SWAP, mc.player);
                     mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                     mc.player.getInventory().selectedSlot = slotCorrectable;
                     mc.interactionManager.syncSelectedSlot();
                     mc.interactionManager
                        .sendSequencedPacket(
                           mc.world, sequence -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch())
                        );
                     mc.player.getInventory().selectedSlot = previousSlot;
                     mc.interactionManager.syncSelectedSlot();
                     mc.interactionManager.clickSlot(0, slot, slotCorrectable, SlotActionType.SWAP, mc.player);
                     mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
                     mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
                  }
               }
            }
         }
      }
   }

   public static void clickSlotNoSync(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player) {
      ScreenHandler screenHandler = player.currentScreenHandler;
      if (syncId != screenHandler.syncId) {
         Narrator.LOGGER
            .warn(
               D.k(
                  new int[]{
                     161,
                     74,
                     232,
                     193,
                     154,
                     68,
                     232,
                     201,
                     200,
                     78,
                     234,
                     199,
                     139,
                     70,
                     166,
                     199,
                     134,
                     13,
                     235,
                     199,
                     155,
                     64,
                     231,
                     218,
                     139,
                     69,
                     239,
                     192,
                     143,
                     13,
                     229,
                     193,
                     134,
                     89,
                     231,
                     199,
                     134,
                     72,
                     244,
                     128,
                     200,
                     110,
                     234,
                     199,
                     139,
                     70,
                     166,
                     199,
                     134,
                     13,
                     253,
                     211,
                     196,
                     13,
                     246,
                     194,
                     137,
                     84,
                     227,
                     220,
                     200,
                     69,
                     231,
                     221,
                     200,
                     86,
                     251,
                     128
                  },
                  new int[]{232, 45, 134, 174}
               ),
               syncId,
               screenHandler.syncId
            );
      } else {
         DefaultedList<Slot> defaultedList = screenHandler.slots;
         int i = defaultedList.size();
         List<ItemStack> list = Lists.newArrayListWithCapacity(i);

         for (Slot slot : defaultedList) {
            list.add(slot.getStack().copy());
         }

         screenHandler.onSlotClick(slotId, button, actionType, player);
         Int2ObjectMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap();

         for (int j = 0; j < i; j++) {
            ItemStack itemStack = list.get(j);
            ItemStack itemStack2 = defaultedList.get(j).getStack();
            if (!ItemStack.areEqual(itemStack, itemStack2)) {
               int2ObjectMap.put(j, itemStack2.copy());
            }
         }

         NetworkUtils.sendSilentPacket(
            new ClickSlotC2SPacket(syncId, screenHandler.getRevision(), slotId, button, actionType, screenHandler.getCursorStack().copy(), int2ObjectMap)
         );
      }
   }

   public static int findBestElytraSlot() {
      int bestSlot = -1;
      double bestScore = -1.0;
      RegistryEntry<Enchantment> protection = MinecraftClient.getInstance()
         .world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.PROTECTION.getValue())
         .orElseThrow();
      RegistryEntry<Enchantment> unbreaking = MinecraftClient.getInstance()
         .world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.UNBREAKING.getValue())
         .orElseThrow();
      RegistryEntry<Enchantment> mending = MinecraftClient.getInstance()
         .world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.MENDING.getValue())
         .orElseThrow();

      for (int slot = 0; slot < 36; slot++) {
         ItemStack stack = mc.player.getInventory().getStack(slot);
         if (stack.isOf(Items.ELYTRA)) {
            int protLevel = EnchantmentHelper.getLevel(protection, stack);
            int unbLevel = EnchantmentHelper.getLevel(unbreaking, stack);
            int mendLevel = EnchantmentHelper.getLevel(mending, stack);
            int maxDurability = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            double durabilityRatio = (double)(maxDurability - currentDamage) / maxDurability;
            double score = protLevel * 100 + unbLevel * 10 + (mendLevel > 0 ? 1 : 0) + durabilityRatio * 10.0;
            if (score > bestScore) {
               bestScore = score;
               bestSlot = slot;
            }
         }
      }

      return bestSlot;
   }

   public static int findBestChestplateSlot() {
      int bestSlot = -1;
      double bestScore = -1.0;
      Reference<Enchantment> protection = mc.world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.PROTECTION.getValue())
         .orElseThrow();
      Reference<Enchantment> unbreaking = mc.world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.UNBREAKING.getValue())
         .orElseThrow();
      Reference<Enchantment> mending = mc.world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.MENDING.getValue())
         .orElseThrow();

      for (int slot = 0; slot < 36; slot++) {
         ItemStack stack = mc.player.getInventory().getStack(slot);
         if (stack.getItem() instanceof ArmorItem armor) {
            int protLevel = EnchantmentHelper.getLevel(protection, stack);
            int unbLevel = EnchantmentHelper.getLevel(unbreaking, stack);
            int mendLevel = EnchantmentHelper.getLevel(mending, stack);
            int armorTypePriority = hPJoZD(armor);
            int maxDurability = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            double durabilityRatio = (double)(maxDurability - currentDamage) / maxDurability;
            double score = armorTypePriority * 10000 + protLevel * 100 + unbLevel * 10 + (mendLevel > 0 ? 1 : 0) + durabilityRatio * 10.0;
            if (score > bestScore) {
               bestScore = score;
               bestSlot = slot;
            }
         }
      }

      return bestSlot;
   }

   private static int rO5Ghi(Item item) {
      if (item == Items.NETHERITE_HELMET || item == Items.NETHERITE_CHESTPLATE || item == Items.NETHERITE_LEGGINGS || item == Items.NETHERITE_BOOTS) {
         return 6;
      } else if (item == Items.DIAMOND_HELMET || item == Items.DIAMOND_CHESTPLATE || item == Items.DIAMOND_LEGGINGS || item == Items.DIAMOND_BOOTS) {
         return 5;
      } else if (item == Items.IRON_HELMET || item == Items.IRON_CHESTPLATE || item == Items.IRON_LEGGINGS || item == Items.IRON_BOOTS) {
         return 4;
      } else if (item == Items.CHAINMAIL_HELMET || item == Items.CHAINMAIL_CHESTPLATE || item == Items.CHAINMAIL_LEGGINGS || item == Items.CHAINMAIL_BOOTS) {
         return 3;
      } else if (item == Items.GOLDEN_HELMET || item == Items.GOLDEN_CHESTPLATE || item == Items.GOLDEN_LEGGINGS || item == Items.GOLDEN_BOOTS) {
         return 2;
      } else {
         return item != Items.LEATHER_HELMET && item != Items.LEATHER_CHESTPLATE && item != Items.LEATHER_LEGGINGS && item != Items.LEATHER_BOOTS ? 0 : 1;
      }
   }

   private static int hPJoZD(Item item) {
      if (item == Items.NETHERITE_CHESTPLATE) {
         return 6;
      } else if (item == Items.DIAMOND_CHESTPLATE) {
         return 5;
      } else if (item == Items.IRON_CHESTPLATE) {
         return 4;
      } else if (item == Items.CHAINMAIL_CHESTPLATE) {
         return 3;
      } else if (item == Items.GOLDEN_CHESTPLATE) {
         return 2;
      } else {
         return item == Items.LEATHER_CHESTPLATE ? 1 : 0;
      }
   }

   public static int getBestArmorSlot(EquipmentSlot slot) {
      int bestSlot = -1;
      double bestScore = -1.0;

      for (int i = 0; i < 36; i++) {
         ItemStack stack = mc.player.getInventory().getStack(i);
         if (stack.getItem() instanceof ArmorItem && fcQ3a(stack) == slot) {
            double score = getArmorScore(stack);
            if (score > bestScore) {
               bestScore = score;
               bestSlot = i;
            }
         }
      }

      return bestSlot;
   }

   private static EquipmentSlot fcQ3a(ItemStack stack) {
      Item item = stack.getItem();
      if (item instanceof ArmorItem) {
         if (item == Items.NETHERITE_HELMET
            || item == Items.DIAMOND_HELMET
            || item == Items.IRON_HELMET
            || item == Items.GOLDEN_HELMET
            || item == Items.CHAINMAIL_HELMET
            || item == Items.LEATHER_HELMET) {
            return EquipmentSlot.HEAD;
         }

         if (item == Items.NETHERITE_CHESTPLATE
            || item == Items.DIAMOND_CHESTPLATE
            || item == Items.IRON_CHESTPLATE
            || item == Items.GOLDEN_CHESTPLATE
            || item == Items.CHAINMAIL_CHESTPLATE
            || item == Items.LEATHER_CHESTPLATE) {
            return EquipmentSlot.CHEST;
         }

         if (item == Items.NETHERITE_LEGGINGS
            || item == Items.DIAMOND_LEGGINGS
            || item == Items.IRON_LEGGINGS
            || item == Items.GOLDEN_LEGGINGS
            || item == Items.CHAINMAIL_LEGGINGS
            || item == Items.LEATHER_LEGGINGS) {
            return EquipmentSlot.LEGS;
         }

         if (item == Items.NETHERITE_BOOTS
            || item == Items.DIAMOND_BOOTS
            || item == Items.IRON_BOOTS
            || item == Items.GOLDEN_BOOTS
            || item == Items.CHAINMAIL_BOOTS
            || item == Items.LEATHER_BOOTS) {
            return EquipmentSlot.FEET;
         }
      }

      return null;
   }

   public static double getArmorScore(ItemStack stack) {
      double score = 0.0;
      score += rO5Ghi(stack.getItem());
      Reference<Enchantment> protection = mc.world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.PROTECTION.getValue())
         .orElseThrow();
      Reference<Enchantment> unbreaking = mc.world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.UNBREAKING.getValue())
         .orElseThrow();
      Reference<Enchantment> mending = mc.world
         .getRegistryManager()
         .getOptional(RegistryKeys.ENCHANTMENT)
         .get()
         .getEntry(Enchantments.MENDING.getValue())
         .orElseThrow();
      score += EnchantmentHelper.getLevel(protection, stack) * 0.75;
      score += EnchantmentHelper.getLevel(unbreaking, stack) * 0.3;
      score += EnchantmentHelper.getLevel(mending, stack) * 0.1;
      double durabilityFactor = 1.0;
      if (stack.isDamageable()) {
         int max = stack.getMaxDamage();
         int left = max - stack.getDamage();
         durabilityFactor = Math.max(0.05, (double)left / max);
      }

      return score * durabilityFactor;
   }

   @Generated
   public static InventoryUtil getInstance() {
      return instance;
   }

   private static class StagedTicker {
      @Subscribe
      private void onUpdate(EventPlayerUpdate ignored) {
         InventoryUtil.suxj();
      }
   }
}
