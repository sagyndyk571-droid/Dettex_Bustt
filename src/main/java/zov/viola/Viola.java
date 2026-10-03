package zov.viola;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.File;
import lombok.Generated;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import zov.viola.event.list.EventKeyInput;
import zov.viola.module.Module;
import zov.viola.module.ModuleStorage;
import zov.viola.module.list.player.Panic;
import zov.viola.module.list.render.Optimization;
import zov.viola.ui.ThemeEditor;
import zov.viola.util.alt.AltManager;
import zov.viola.util.commands.CommandDispatcher;
import zov.viola.util.commands.manager.CommandRepository;
import zov.viola.util.config.ConfigManager;
import zov.viola.util.discord.DiscordRpcManager;
import zov.viola.util.draggable.DragManager;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.license.LicenseManager;
import zov.viola.util.macro.MacroRepository;
import zov.viola.util.math.TPSGetter;
import zov.viola.util.player.combat.IdealHitUtils;
import zov.viola.util.player.other.ServerManager;
import zov.viola.util.rotation.ComponentManager;
import zov.viola.util.script.ScriptManager;
import zov.viola.util.staff.StaffManager;

public class Viola implements ModInitializer {
   private static Viola daAe56I;
   private final EventBus hC8neT2;
   private final ModuleStorage cuW7g;
   private final ComponentManager d63o4s;
   private final DragManager ri2V6hd;
   private final CommandRepository mO8dt;
   private final MacroRepository e8vTNrM;
   private final ConfigManager kPi31;
   private final CommandDispatcher rPHz9;
   private final StaffManager nRRh4WD;
   private final ServerManager gr2JlGC;
   private final TPSGetter i6KboNe;
   private final IdealHitUtils kEi5fs;
   private final ScriptManager rA23sJw;
   private final DiscordRpcManager snczxHs;
   private boolean vs5419;

   public Viola() {
      daAe56I = this;
      this.hC8neT2 = new EventBus();
      this.hC8neT2.register(this);
      this.cuW7g = new ModuleStorage();
      this.d63o4s = new ComponentManager();
      this.ri2V6hd = new DragManager();
      this.e8vTNrM = new MacroRepository();
      this.kPi31 = new ConfigManager();
      this.nRRh4WD = new StaffManager();
      this.nRRh4WD.load();
      this.mO8dt = new CommandRepository();
      this.rPHz9 = new CommandDispatcher();
      this.gr2JlGC = new ServerManager();
      this.i6KboNe = new TPSGetter();
      this.kEi5fs = new IdealHitUtils();
      this.rA23sJw = new ScriptManager();
      this.snczxHs = new DiscordRpcManager();
      this.hC8neT2.register(this.snczxHs);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         this.getDiscordRpcManager().stop();
         ConfigManager.save("autocfg");
         this.getDragManager().saveDraggables();
         this.getMacroRepository().save();
         FriendRepository.save();
         this.nRRh4WD.save();
      }));
      File dir = LicenseManager.dataDir().resolve("configs").toFile();

      try {
         LicenseManager.dataDir().toFile().mkdirs();
      } catch (Exception var3) {
      }

      if (!dir.exists()) {
         dir.mkdirs();
      }
   }

   public static Viola getInstance() {
      return daAe56I == null ? new Viola() : daAe56I;
   }

   public void onInitialize() {
      this.getDiscordRpcManager().start();
      this.getModuleStorage().injectRegisterModules();
      this.d63o4s.init();
      this.ri2V6hd.load();
      this.e8vTNrM.load();
      FriendRepository.load();
      ConfigManager.load("autocfg");
      ThemeEditor.applyStartupTheme();
      AltManager.load();
      AltManager.applyLastNick();
      if (Optimization.isWeakHardware()) {
         this.getModuleStorage().get(Optimization.class).enableDefault();
      }
   }

   @Subscribe
   private void onModuleKeyPressed(EventKeyInput event) {
      for (Module module : this.getModuleStorage().getModules()) {
         if (event.getAction() == 1
            && MinecraftClient.getInstance().currentScreen == null
            && module.getKey() == event.getKey()
            && (!this.vs5419 || module instanceof Panic)) {
            module.toggle();
         }
      }
   }

   @Generated
   public EventBus getEventBus() {
      return this.hC8neT2;
   }

   @Generated
   public ModuleStorage getModuleStorage() {
      return this.cuW7g;
   }

   @Generated
   public ComponentManager getComponentManager() {
      return this.d63o4s;
   }

   @Generated
   public DragManager getDragManager() {
      return this.ri2V6hd;
   }

   @Generated
   public CommandRepository getCommandRepository() {
      return this.mO8dt;
   }

   @Generated
   public MacroRepository getMacroRepository() {
      return this.e8vTNrM;
   }

   @Generated
   public ConfigManager getConfigManager() {
      return this.kPi31;
   }

   @Generated
   public CommandDispatcher getCommandDispatcher() {
      return this.rPHz9;
   }

   @Generated
   public StaffManager getStaffManager() {
      return this.nRRh4WD;
   }

   @Generated
   public ServerManager getServerManager() {
      return this.gr2JlGC;
   }

   @Generated
   public TPSGetter getTpsGetter() {
      return this.i6KboNe;
   }

   @Generated
   public IdealHitUtils getIdealHitUtils() {
      return this.kEi5fs;
   }

   @Generated
   public ScriptManager getScriptManager() {
      return this.rA23sJw;
   }

   @Generated
   public DiscordRpcManager getDiscordRpcManager() {
      return this.snczxHs;
   }

   @Generated
   public boolean isPanicMode() {
      return this.vs5419;
   }

   @Generated
   public void setPanicMode(boolean panicMode) {
      this.vs5419 = panicMode;
   }
}
