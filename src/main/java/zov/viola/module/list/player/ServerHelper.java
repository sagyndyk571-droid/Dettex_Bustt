package zov.viola.module.list.player;

import com.google.common.eventbus.Subscribe;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import zov.viola.event.list.EventKeyInput;
import zov.viola.event.list.EventTick;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.movement.Sprint;
import zov.viola.module.settings.BindSetting;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;

@ModuleInformation(
   moduleName = "ServerHelper",
   moduleDesc = "Помощник для сервера",
   moduleCategory = ModuleCategory.PLAYER
)
public class ServerHelper extends Module {
   private final ModeSetting h2p35vd = new ModeSetting(
      "Сервер", "ReallyWorld", "ReallyWorld", "FunTime"
   );
   private final BindSetting jdqS = new BindSetting("Анти Полет", 71)
      .setVisible(() -> this.h2p35vd.is("ReallyWorld"));
   private final BindSetting v5vEO3 = new BindSetting(
         "Зелье Гринча", -1
      )
      .setVisible(() -> this.h2p35vd.is("ReallyWorld"));
   private final BindSetting uq9vt = new BindSetting(
         D.k(
            new int[]{166, 157, 25, 211, 180, 128, 4, 218, 147, 200, 69, 1172, 1247, 1196, 1071, 156, 1235, 1235, 1058, 156, 1199, 1240, 1069, 1164, 206},
            new int[]{231, 232, 109, 188}
         ),
         -1
      )
      .setVisible(() -> this.h2p35vd.is("ReallyWorld"));
   private final BindSetting rgJQ8lb = new BindSetting(
         "Новогодний ужас", -1
      )
      .setVisible(() -> this.h2p35vd.is("ReallyWorld"));
   private final BindSetting u1t58f = new BindSetting(
         D.k(
            new int[]{1278, 1080, 1107, 1220, 1262, 1087, 1066, 1214, 243, 1091, 1106, 1231, 1263, 1100, 1114, 1228, 1259, 1091, 1058},
            new int[]{211, 121, 18, 241}
         ),
         -1
      )
      .setVisible(() -> this.h2p35vd.is("ReallyWorld"));
   private final BindSetting lay9sj6 = new BindSetting("Снежок", -1)
      .setVisible(() -> this.h2p35vd.is("ReallyWorld"));
   private final BindSetting bTvh2S = new BindSetting("Трапка", -1)
      .setVisible(() -> this.h2p35vd.is("ReallyWorld"));
   private final BooleanSetting vO661mZ = new BooleanSetting(
         "Auto /fix all", false
      )
      .setVisible(() -> this.h2p35vd.is("ReallyWorld"));
   private final BindSetting k4G8 = new BindSetting(
         "Дезоритация", -1
      )
      .setVisible(() -> this.h2p35vd.is("FunTime"));
   private final BindSetting n3i2p5 = new BindSetting("Трапка", -1)
      .setVisible(() -> this.h2p35vd.is("FunTime"));
   private final BindSetting c1tbjq = new BindSetting(
         "Снежок заморозки", -1
      )
      .setVisible(() -> this.h2p35vd.is("FunTime"));
   private final BindSetting tlkvY = new BindSetting("Явная пыль", -1)
      .setVisible(() -> this.h2p35vd.is("FunTime"));
   private final BindSetting xdPti = new BindSetting("Пласт", -1)
      .setVisible(() -> this.h2p35vd.is("FunTime"));
   private final BindSetting gxBC = new BindSetting(
         "Порыв ветра", -1
      )
      .setVisible(() -> this.h2p35vd.is("FunTime"));
   private final SliderSetting h6s800X = new SliderSetting(
         D.k(
            new int[]{1238, 1241, 1146, 1103, 1153, 1247, 1140, 1098, 225, 1237, 1147, 1100, 1269, 1194, 110, 1076, 1270, 1239, 1138, 90, 233, 1237, 1039, 83},
            new int[]{193, 233, 78, 122}
         ),
         150.0,
         50.0,
         1000.0,
         50.0
      )
      .setVisible(() -> this.h2p35vd.is("FunTime"));
   private boolean u4lt2;
   private boolean fDnU;
   private boolean m0Ik;
   private boolean v8jG7;
   private boolean rRh2;
   private boolean lSBAze7;
   private boolean i2oY;
   private boolean aeo7tA9;
   private boolean a9rjx3;
   private boolean ixnb1a;
   private boolean eLS0;
   private boolean eo3fnpO;
   private boolean iBuK;
   private long i2404;
   private int jwMpje = -1;
   private float lUEO7F3;
   private boolean qrtd;
   private int hCFSm;
   private boolean ewG7CS;
   private int moPexQ;
   private boolean nmW079;
   private boolean ia3q8;
   private int mztPp;

   @Override
   public void onDisable() {
      super.onDisable();
      Sprint.clearSprintBlock();
      this.ewG7CS = false;
      this.moPexQ = 0;
      this.mztPp = 0;
      if (this.mc.options != null) {
         this.mc.options.sneakKey.setPressed(false);
      }

      if (this.nmW079 && this.mc.player != null && !this.ia3q8) {
         this.mc.options.sprintKey.setPressed(true);
      }

      this.nmW079 = false;
   }

   public int getAntiFlyKey() {
      return this.jdqS.getValue();
   }

   public int getGrinchPotionKey() {
      return this.v5vEO3.getValue();
   }

   public int getAutoShiftKey() {
      return this.uq9vt.getValue();
   }

   public int getNewYearHorrorKey() {
      return this.rgJQ8lb.getValue();
   }

   public int getDarkEssenceKey() {
      return this.u1t58f.getValue();
   }

   public int getSnowballKey() {
      return this.lay9sj6.getValue();
   }

   public int getTrapKey() {
      return this.bTvh2S.getValue();
   }

   public boolean isFunTime() {
      return this.h2p35vd.is("FunTime");
   }

   public int getFtDeoritKey() {
      return this.k4G8.getValue();
   }

   public int getFtTrapKey() {
      return this.n3i2p5.getValue();
   }

   public int getFtFreezeKey() {
      return this.c1tbjq.getValue();
   }

   public int getFtDustKey() {
      return this.tlkvY.getValue();
   }

   public int getFtPlateKey() {
      return this.xdPti.getValue();
   }

   public int getFtWindKey() {
      return this.gxBC.getValue();
   }

   private boolean msp9xvt(ItemStack stack) {
      if (!stack.isOf(Items.SPLASH_POTION)) {
         return false;
      } else {
         String name = stack.getName().getString().toLowerCase();
         return name.contains("гринч");
      }
   }

   private boolean cp8yfj(ItemStack stack) {
      if (!stack.isOf(Items.SPLASH_POTION)) {
         return false;
      } else {
         String name = stack.getName().getString().toLowerCase();
         return name.contains("новогодний ужас");
      }
   }

   private boolean kOumO3K(ItemStack stack) {
      if (!stack.isOf(Items.SPLASH_POTION)) {
         return false;
      } else {
         String name = stack.getName().getString().toLowerCase();
         return name.contains(
            D.k(
               new int[]{1258, 1052, 1095, 1267, 1178, 1051, 1086, 1161, 135, 1127, 1094, 1272, 1179, 1128, 1102, 1275, 1183, 1127, 1078},
               new int[]{167, 93, 6, 198}
            )
         );
      }
   }

   private boolean m58f6(ItemStack stack) {
      if (!stack.isOf(Items.SPLASH_POTION)) {
         return false;
      } else {
         String name = stack.getName().getString().toLowerCase();
         return name.contains("снежок");
      }
   }

   private boolean ikVu02(ItemStack stack) {
      return stack.isOf(Items.FIREWORK_STAR);
   }

   private boolean weP5V(ItemStack stack) {
      return stack.isOf(Items.HEART_OF_THE_SEA);
   }

   private boolean cckdu(ItemStack stack) {
      if (!stack.isOf(Items.SPLASH_POTION)) {
         return false;
      } else {
         String name = stack.getName().getString().toLowerCase();
         return name.contains("ловуш")
            || name.contains("ловушк");
      }
   }

   private int vHjD2p(Predicate<ItemStack> predicate, int from, int to) {
      for (int i2 = from; i2 < to; i2++) {
         if (predicate.test(this.mc.player.getInventory().getStack(i2))) {
            return i2;
         }
      }

      return -1;
   }

   private int si2r() {
      return this.vHjD2p(this::weP5V, 0, 9);
   }

   private int yDBV() {
      return this.vHjD2p(this::weP5V, 9, 45);
   }

   @Subscribe
   private void onKey(EventKeyInput e2) {
      if (this.mc.currentScreen == null) {
         if (e2.getAction() == 1) {
            if (e2.getKey() == this.jdqS.getValue()) {
               this.u4lt2 = true;
            }

            if (e2.getKey() == this.v5vEO3.getValue()) {
               this.fDnU = true;
            }

            if (e2.getKey() == this.uq9vt.getValue()) {
               this.m0Ik = true;
            }

            if (e2.getKey() == this.rgJQ8lb.getValue()) {
               this.v8jG7 = true;
            }

            if (e2.getKey() == this.u1t58f.getValue()) {
               this.rRh2 = true;
            }

            if (e2.getKey() == this.lay9sj6.getValue()) {
               this.lSBAze7 = true;
            }

            if (e2.getKey() == this.bTvh2S.getValue()) {
               this.i2oY = true;
            }

            if (e2.getKey() == this.k4G8.getValue()) {
               this.aeo7tA9 = true;
            }

            if (e2.getKey() == this.n3i2p5.getValue()) {
               this.a9rjx3 = true;
            }

            if (e2.getKey() == this.c1tbjq.getValue()) {
               this.ixnb1a = true;
            }

            if (e2.getKey() == this.tlkvY.getValue()) {
               this.eLS0 = true;
            }

            if (e2.getKey() == this.xdPti.getValue()) {
               this.eo3fnpO = true;
            }

            if (e2.getKey() == this.gxBC.getValue()) {
               this.iBuK = true;
            }
         }
      }
   }

   @Subscribe
   private void onTick(EventTick e2) {
      if (this.u4lt2) {
         this.u4lt2 = false;
         this.y1oAf(this::ikVu02);
      }

      if (this.fDnU) {
         this.fDnU = false;
         this.y1oAf(this::msp9xvt);
      }

      if (this.m0Ik) {
         this.m0Ik = false;
         this.rr98();
      }

      if (this.v8jG7) {
         this.v8jG7 = false;
         this.y1oAf(this::cp8yfj);
      }

      if (this.rRh2) {
         this.rRh2 = false;
         this.y1oAf(this::kOumO3K);
      }

      if (this.lSBAze7) {
         this.lSBAze7 = false;
         this.y1oAf(this::m58f6);
      }

      if (this.i2oY) {
         this.i2oY = false;
         this.vs6ywrj();
      }

      boolean ftReady = System.currentTimeMillis() - this.i2404 >= this.h6s800X.getValue();
      if (ftReady && this.aeo7tA9) {
         this.aeo7tA9 = false;
         this.qzWJI(Items.ENDER_EYE);
      }

      if (ftReady && this.a9rjx3) {
         this.a9rjx3 = false;
         this.qzWJI(Items.NETHERITE_SCRAP);
      }

      if (ftReady && this.ixnb1a) {
         this.ixnb1a = false;
         this.qzWJI(Items.SNOWBALL);
      }

      if (ftReady && this.eLS0) {
         this.eLS0 = false;
         this.qzWJI(Items.SUGAR);
      }

      if (ftReady && this.eo3fnpO) {
         this.eo3fnpO = false;
         this.qzWJI(Items.DRIED_KELP);
      }

      if (ftReady && this.iBuK) {
         this.iBuK = false;
         this.acs5i2();
      }

      if (this.jwMpje >= 0 && this.mc.player != null) {
         this.jwMpje++;
         boolean airborne = !this.mc.player.isOnGround();
         if (airborne && this.jwMpje >= 1 || this.jwMpje > 10) {
            this.bvU18Ne();
            this.jwMpje = -1;
         }
      }

      if (this.ewG7CS) {
         if (this.moPexQ > 0) {
            this.moPexQ--;
            if (this.mc.player != null) {
               this.mc.player.setSprinting(false);
               if (!this.ia3q8 && this.mc.options != null) {
                  this.mc.options.sprintKey.setPressed(false);
               }
            }
         } else {
            this.ewG7CS = false;
            if (this.nmW079 && this.mc.player != null) {
               this.mc.player.setSprinting(true);
               if (!this.ia3q8) {
                  this.mc.options.sprintKey.setPressed(true);
               }
            }
         }
      }

      if (this.mztPp > 0) {
         this.mztPp--;
         if (this.mztPp == 0 && this.mc.options != null) {
            this.mc.options.sneakKey.setPressed(false);
         }
      }

      this.gg52mY();
   }

   private void acs5i2() {
      if (this.mc.player != null) {
         this.lUEO7F3 = this.mc.player.getPitch();
         this.mc.player.setVelocity(this.mc.player.getVelocity().x, 0.5, this.mc.player.getVelocity().z);
         this.jwMpje = 0;
      }
   }

   private void bvU18Ne() {
      if (this.mc.player != null) {
         this.mc.player.setPitch(90.0F);
         this.qzWJI(Items.WIND_CHARGE);
         this.mc.player.setPitch(this.lUEO7F3);
      }
   }

   private void gg52mY() {
      if (this.h2p35vd.is("ReallyWorld") && this.vO661mZ.getValue()) {
         boolean inPvp = this.eAidEba();
         if (this.qrtd && !inPvp && this.mc.player.age >= this.hCFSm) {
            this.mc.player.networkHandler.sendChatCommand("fix all");
            this.hCFSm = this.mc.player.age + 40;
         }

         this.qrtd = inPvp;
      } else {
         this.qrtd = this.eAidEba();
      }
   }

   private boolean eAidEba() {
      return this.mc.player != null && this.mc.world != null
         ? this.mc.world.getPlayers().stream().filter(p -> p != this.mc.player).anyMatch(p -> p.squaredDistanceTo(this.mc.player) < 100.0)
         : false;
   }

   private void rr98() {
      if (this.mc.options != null) {
         this.mc.options.sneakKey.setPressed(true);
         this.mztPp = 3;
      }
   }

   private void vkb59YW(Runnable action) {
      if (this.mc.player != null && this.mc.getNetworkHandler() != null) {
         boolean wasSprinting = this.mc.player.isSprinting();
         boolean sprintModuleOn = Instance.get(Sprint.class).isEnabled();
         if (wasSprinting) {
            this.mc.player.setSprinting(false);
            if (!sprintModuleOn) {
               this.mc.options.sprintKey.setPressed(false);
            }

            if (sprintModuleOn) {
               Sprint.blockSprint(40);
            }
         }

         action.run();
         if (wasSprinting) {
            this.ewG7CS = true;
            this.moPexQ = 40;
            this.nmW079 = true;
            this.ia3q8 = sprintModuleOn;
         }
      } else {
         action.run();
      }
   }

   private void vs6ywrj() {
      this.vkb59YW(
         () -> {
            if (this.mc.player != null && this.mc.getNetworkHandler() != null) {
               if (this.weP5V(this.mc.player.getMainHandStack())) {
                  this.mc
                     .interactionManager
                     .sendSequencedPacket(
                        this.mc.world, seq -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, seq, this.mc.player.getYaw(), this.mc.player.getPitch())
                     );
               } else if (this.weP5V(this.mc.player.getOffHandStack())) {
                  this.mc
                     .interactionManager
                     .sendSequencedPacket(
                        this.mc.world, seq -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, seq, this.mc.player.getYaw(), this.mc.player.getPitch())
                     );
               } else {
                  int slot = this.si2r();
                  if (slot != -1) {
                     this.bbfe(slot);
                  } else {
                     slot = this.yDBV();
                     if (slot != -1) {
                        this.pmch(slot);
                     }
                  }
               }
            }
         }
      );
   }

   private int em42d7R() {
      return this.vHjD2p(this::cckdu, 0, 9);
   }

   private int hs4j() {
      return this.vHjD2p(this::cckdu, 9, 45);
   }

   private void y1oAf(Predicate<ItemStack> predicate) {
      this.vkb59YW(() -> {
         if (this.mc.player != null && this.mc.getNetworkHandler() != null) {
            int hotbarSlot = this.vHjD2p(predicate, 0, 9);
            if (hotbarSlot != -1) {
               this.bbfe(hotbarSlot);
            } else {
               int invSlot = this.vHjD2p(predicate, 9, 45);
               if (invSlot != -1) {
                  this.pmch(invSlot);
               }
            }
         }
      });
   }

   private void qzWJI(Item item) {
      this.vkb59YW(
         () -> {
            if (this.mc.player != null && this.mc.getNetworkHandler() != null) {
               if (!this.mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(item))) {
                  if (this.mc.player.getMainHandStack().getItem() == item) {
                     this.mc
                        .interactionManager
                        .sendSequencedPacket(
                           this.mc.world, seq -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, seq, this.mc.player.getYaw(), this.mc.player.getPitch())
                        );
                  } else if (this.mc.player.getOffHandStack().getItem() == item) {
                     this.mc
                        .interactionManager
                        .sendSequencedPacket(
                           this.mc.world, seq -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, seq, this.mc.player.getYaw(), this.mc.player.getPitch())
                        );
                  } else {
                     int hotbarSlot = this.vHjD2p(s -> s.isOf(item), 0, 9);
                     if (hotbarSlot != -1) {
                        this.bbfe(hotbarSlot);
                        this.i2404 = System.currentTimeMillis();
                     } else {
                        int invSlot = this.vHjD2p(s -> s.isOf(item), 9, 45);
                        if (invSlot != -1) {
                           this.pmch(invSlot);
                           this.i2404 = System.currentTimeMillis();
                        }
                     }
                  }
               }
            }
         }
      );
   }

   private void bbfe(int hotbarSlot) {
      int prev = this.mc.player.getInventory().selectedSlot;
      this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(hotbarSlot));
      this.mc
         .interactionManager
         .sendSequencedPacket(this.mc.world, seq -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, seq, this.mc.player.getYaw(), this.mc.player.getPitch()));
      this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(prev));
   }

   private void pmch(int invSlot) {
      int prev = this.mc.player.getInventory().selectedSlot;
      this.mc.interactionManager.clickSlot(0, invSlot, 8, SlotActionType.SWAP, this.mc.player);
      this.mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
      this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(8));
      this.mc
         .interactionManager
         .sendSequencedPacket(this.mc.world, seq -> new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, seq, this.mc.player.getYaw(), this.mc.player.getPitch()));
      this.mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(prev));
      this.mc.interactionManager.clickSlot(0, invSlot, 8, SlotActionType.SWAP, this.mc.player);
      this.mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(0));
   }
}
