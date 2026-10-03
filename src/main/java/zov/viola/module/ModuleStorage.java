package zov.viola.module;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import zov.viola.Viola;
import zov.viola.event.EventGameUpdate;
import zov.viola.event.list.EventHUD;
import zov.viola.event.list.EventKeyInput;
import zov.viola.event.list.EventTick;
import zov.viola.module.list.combat.AntiBot;
import zov.viola.module.list.combat.AutoArmor;
import zov.viola.module.list.combat.AutoMace;
import zov.viola.module.list.combat.AutoPotion;
import zov.viola.module.list.combat.AutoSwap;
import zov.viola.module.list.combat.AutoTotem;
import zov.viola.module.list.combat.AutoTrap;
import zov.viola.module.list.combat.CrystalSpammer;
import zov.viola.module.list.combat.FixHP;
import zov.viola.module.list.combat.GoldEat;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.list.combat.NoEntityTrace;
import zov.viola.module.list.combat.NoFriendDamage;
import zov.viola.module.list.combat.TargetPearl;
import zov.viola.module.list.combat.TriggerBot;
import zov.viola.module.list.combat.Velocity;
import zov.viola.module.list.misc.AutoDuel;
import zov.viola.module.list.misc.ChestStealer;
import zov.viola.module.list.misc.CrystalOptimizer;
import zov.viola.module.list.misc.NameProtect;
import zov.viola.module.list.misc.ScoreboardHealth;
import zov.viola.module.list.misc.ServerJoiner;
import zov.viola.module.list.misc.SwiftBreak;
import zov.viola.module.list.misc.UseTracker;
import zov.viola.module.list.misc.Voice;
import zov.viola.module.list.movement.AirStuck;
import zov.viola.module.list.movement.DogFly;
import zov.viola.module.list.movement.DragonFly;
import zov.viola.module.list.movement.ElytraBooster;
import zov.viola.module.list.movement.ElytraFlight;
import zov.viola.module.list.movement.ElytraMotion;
import zov.viola.module.list.movement.ElytraTarget;
import zov.viola.module.list.movement.GrimGlide;
import zov.viola.module.list.movement.GuiMove;
import zov.viola.module.list.movement.NoJumpDelay;
import zov.viola.module.list.movement.NoSlow;
import zov.viola.module.list.movement.NoWeb;
import zov.viola.module.list.movement.Speed;
import zov.viola.module.list.movement.Sprint;
import zov.viola.module.list.movement.Strafe;
import zov.viola.module.list.movement.Timer;
import zov.viola.module.list.player.AutoEat;
import zov.viola.module.list.player.AutoLeave;
import zov.viola.module.list.player.AutoRebirth;
import zov.viola.module.list.player.AutoTool;
import zov.viola.module.list.player.AutoTpaccept;
import zov.viola.module.list.player.ClickPearl;
import zov.viola.module.list.player.CmdFix;
import zov.viola.module.list.player.DeathCoords;
import zov.viola.module.list.player.ElytraHelper;
import zov.viola.module.list.player.FakePlayer;
import zov.viola.module.list.player.FastExp;
import zov.viola.module.list.player.FreeCamera;
import zov.viola.module.list.player.InvisDrink;
import zov.viola.module.list.player.LockSlot;
import zov.viola.module.list.player.NoPush;
import zov.viola.module.list.player.Panic;
import zov.viola.module.list.player.RPSpoofer;
import zov.viola.module.list.player.ServerHelper;
import zov.viola.module.list.player.TapeMouse;
import zov.viola.module.list.player.TeleportBack;
import zov.viola.module.list.player.TeleportExploit;
import zov.viola.module.list.render.Ambience;
import zov.viola.module.list.render.Arrows;
import zov.viola.module.list.render.AtmoDawnFog;
import zov.viola.module.list.render.BlockOverlay;
import zov.viola.module.list.render.Cape;
import zov.viola.module.list.render.Chams;
import zov.viola.module.list.render.ClickGui;
import zov.viola.module.list.render.ClientSounds;
import zov.viola.module.list.render.Cosmetics;
import zov.viola.module.list.render.FireFly;
import zov.viola.module.list.render.FreeLook;
import zov.viola.module.list.render.FtHelper;
import zov.viola.module.list.render.FullBright;
import zov.viola.module.list.render.GlassBody;
import zov.viola.module.list.render.GlassHands;
import zov.viola.module.list.render.GlowEsp;
import zov.viola.module.list.render.HitBubbles;
import zov.viola.module.list.render.HitIndicator;
import zov.viola.module.list.render.ItemReplacer;
import zov.viola.module.list.render.KillEffect;
import zov.viola.module.list.render.LineGlyphs;
import zov.viola.module.list.render.LootTimer;
import zov.viola.module.list.render.MotionBlur;
import zov.viola.module.list.render.NameTags;
import zov.viola.module.list.render.NoRender;
import zov.viola.module.list.render.Optimization;
import zov.viola.module.list.render.Particles;
import zov.viola.module.list.render.Predictions;
import zov.viola.module.list.render.SeeInvisible;
import zov.viola.module.list.render.ShaderScreens;
import zov.viola.module.list.render.ShulkerView;
import zov.viola.module.list.render.SoulESP;
import zov.viola.module.list.render.SwingAnimations;
import zov.viola.module.list.render.TargetESP;
import zov.viola.module.list.render.TargetESP2;
import zov.viola.module.list.render.Tracers;
import zov.viola.module.list.render.Trails;
import zov.viola.module.list.render.TrapESP;
import zov.viola.module.list.render.UseIndicator;
import zov.viola.module.list.render.ViewModel;
import zov.viola.module.list.render.Wings;
import zov.viola.module.list.render.XRay;
import zov.viola.module.list.render.hud.Interface;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.Setting;
import zov.viola.module.settings.ThemeSetting;
import zov.viola.util.IMinecraft;
import zov.viola.util.player.other.SlownessManager;
import zov.viola.util.rotation.FreeLookComponent;
import zov.viola.util.rotation.Rotation;
import zov.viola.util.rotation.RotationComponent;

public class ModuleStorage implements IMinecraft {
   private final List<Module> kbjnH = new ArrayList<>();
   private KillAura xMz8EM;
   private FreeLook cwDfo;
   private float wanir;
   private float ztjl;

   public void injectRegisterModules() {
      this.kbjnH
         .addAll(
            List.of(
               new FullBright(),
               new ClickGui(),
               new Sprint(),
               new TriggerBot(),
               new NoRender(),
               new KillAura(),
               new NameTags(),
               new TargetESP(),
               new TargetESP2(),
               new NoPush(),
               new SoulESP(),
               new Optimization(),
               new NoJumpDelay(),
               new TeleportBack(),
               new ElytraHelper(),
               new AutoTotem(),
               new ClickPearl(),
               new UseTracker(),
               new ClientSounds(),
               new NoFriendDamage(),
               new ElytraBooster(),
               new FreeCamera(),
               new SwingAnimations(),
               new Predictions(),
               new DogFly(),
               new AutoTpaccept(),
               new RPSpoofer(),
               new FireFly(),
               new AntiBot(),
               new DeathCoords(),
               new CrystalOptimizer(),
               new GuiMove(),
               new Tracers(),
               new ElytraMotion(),
               new Velocity(),
               new ElytraFlight(),
               new ViewModel(),
               new GlassHands(),
               new ItemReplacer(),
               new KillEffect(),
               new AutoArmor(),
               new Speed(),
               new AutoTool(),
               new TapeMouse(),
               new Ambience(),
               new BlockOverlay(),
               new FreeLook(),
               new Chams(),
               new GlowEsp(),
                new Trails(),
                new TrapESP(),
               new FastExp(),
               new NameProtect(),
               new CrystalSpammer(),
               new GlassBody(),
               new AirStuck(),
               new AutoSwap(),
               new NoSlow(),
               new NoWeb(),
               new FakePlayer(),
               new Interface(),
               new AutoEat(),
               new AutoLeave(),
               new Voice(),
               new Wings(),
               new ScoreboardHealth(),
               new AutoPotion(),
               new Cape(),
               new AutoDuel(),
               new DragonFly(),
               new Strafe(),
               new ChestStealer(),
               new SeeInvisible(),
               new ShulkerView(),
               new ServerJoiner(),
               new UseIndicator(),
               new ServerHelper(),
               new Cosmetics(),
               new Particles(),
               new Arrows(),
               new TeleportExploit(),
               new AutoTrap(),
               new TargetPearl(),
               new LineGlyphs(),
               new HitIndicator(),
               new GrimGlide(),
               new Timer(),
               new AutoMace(),
               new NoEntityTrace(),
               new LockSlot(),
               new ElytraTarget(),
               new HitBubbles(),
               new MotionBlur(),
               new FtHelper(),
               new LootTimer(),
               new FixHP(),
               new AutoRebirth(),
               new SwiftBreak(),
               new CmdFix(),
               new GoldEat(),
               new InvisDrink(),
               new AtmoDawnFog(),
               new XRay(),
               new ShaderScreens(),
               new Panic()
            )
         );
      Viola.getInstance().getEventBus().register(this);
      this.xMz8EM = this.get(KillAura.class);
      this.cwDfo = this.get(FreeLook.class);
   }

   public <T extends Module> T get(String name) {
      return this.kbjnH.stream().filter(module -> module.getName().equalsIgnoreCase(name)).map(module -> (T)module).findFirst().orElse(null);
   }

   public <T extends Module> T get(Class<T> clazz) {
      return this.kbjnH.stream().filter(module -> clazz.isAssignableFrom(module.getClass())).map(clazz::cast).findFirst().orElse(null);
   }

   public List<Module> get(ModuleCategory category) {
      return this.kbjnH.stream().filter(module -> module.getCategory() == category).collect(Collectors.toList());
   }

   @Subscribe
   private void onGameUpdate(EventGameUpdate e) {
      if (mc.player != null) {
         if (!SlownessManager.slowTasksIsEmpty()) {
            SlownessManager.updateSlowTasks();
         }

         if (!SlownessManager.timeTasksIsEmpty()) {
            SlownessManager.updateTimeTasks(false);
         }

         KillAura aura = this.xMz8EM;
         if (!aura.isEnabled() || aura.getTarget() == null) {
            if (mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT) {
               aura.lastYaw = mc.gameRenderer.getCamera().getYaw() - 180.0F;
               aura.lastPitch = -mc.gameRenderer.getCamera().getPitch();
            } else {
               aura.lastYaw = mc.gameRenderer.getCamera().getYaw();
               aura.lastPitch = mc.gameRenderer.getCamera().getPitch();
            }

            if (aura.rotation.is("Vanilla") || this.cwDfo.isActive()) {
               return;
            }

            this.nSQ42l1();
         }
      }
   }

   @Subscribe
   private void onRender(EventHUD ignored) {
      for (Module module : this.getModules()) {
         module.getAnimation().run(module.isEnabled());

         for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting b) {
               b.getAnimation().run(b.getValue());
            }

            if (setting instanceof ThemeSetting t) {
               t.getValue().animation.run(1.0F);
            }
         }
      }
   }

   @Subscribe
   private void onKey(EventKeyInput e) {
      if (!Viola.getInstance().isPanicMode()) {
         if (e.getAction() != 0) {
            for (Module module : this.getModules()) {
               module.getSettings()
                  .stream()
                  .filter(setting -> setting instanceof BooleanSetting b && e.getKey() == b.getKey())
                  .forEach(setting -> ((BooleanSetting)setting).toggle());
            }
         }
      }
   }

   @Subscribe
   private void onUpdate(EventTick ignored) {
      if (!SlownessManager.timeTasksIsEmpty()) {
         SlownessManager.updateTimeTasks(true);
      }

      if (mc.player != null) {
      }
   }

   private void nSQ42l1() {
      if (mc.player.isGliding()) {
         this.wanir += 0.06F;
      } else {
         this.wanir += 0.006F;
      }

      this.get(KillAura.class).speedAcceleration = 0.0F;
      float hitYaw = mc.player.getYaw();
      float hitPitch = mc.player.getPitch();
      if (mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT) {
         hitYaw = mc.player.getYaw() - 180.0F;
         hitPitch = -mc.player.getPitch();
      }

      float freeYaw = FreeLookComponent.getFreeYaw();
      float freePitch = FreeLookComponent.getFreePitch();
      float deltaYaw = MathHelper.wrapDegrees(hitYaw - freeYaw);
      float deltaPitch = hitPitch - freePitch;
      if (Math.abs(deltaYaw) < 0.5F && Math.abs(deltaPitch) < 0.5F) {
         RotationComponent.getInstance().stopRotation();
      } else {
         float smooth = Math.min(Math.max(this.wanir, 0.0F), 1.0F);
         float newYaw = freeYaw + deltaYaw * smooth;
         float newPitch = freePitch + deltaPitch * smooth;
         FreeLookComponent.setFreeYaw(newYaw);
         FreeLookComponent.setFreePitch(newPitch);
         RotationComponent.update(new Rotation(hitYaw, hitPitch), 360.0F, 360.0F, 360.0F, 360.0F, 0, 2, false);
      }
   }

   @Generated
   public List<Module> getModules() {
      return this.kbjnH;
   }

   @Generated
   public KillAura getCachedKillAura() {
      return this.xMz8EM;
   }

   @Generated
   public FreeLook getCachedFreeLook() {
      return this.cwDfo;
   }

   @Generated
   public float getSpeedAcceleration() {
      return this.wanir;
   }

   @Generated
   public float getRandomness() {
      return this.ztjl;
   }

   @Generated
   public void setSpeedAcceleration(float speedAcceleration) {
      this.wanir = speedAcceleration;
   }

   @Generated
   public void setRandomness(float randomness) {
      this.ztjl = randomness;
   }
}
