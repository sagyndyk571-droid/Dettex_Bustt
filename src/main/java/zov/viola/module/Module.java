package zov.viola.module;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import zov.viola.Viola;
import zov.viola.module.list.player.Panic;
import zov.viola.module.list.render.ClientSounds;
import zov.viola.module.list.render.hud.Interface;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.Setting;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;
import zov.viola.util.QuickLogger;
import zov.viola.util.base.Instance;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;

public class Module implements IMinecraft, QuickLogger {
   private final String neiLP;
   private final String sv4K1;
   private final ModuleCategory mP83;
   private int dzdNZfj;
   private boolean tAJzvFv;
   private boolean lnoSB;
   private final Animation tzaj = new Animation(Easing.BACK_OUT, 450L);
   public final MinecraftClient mc = MinecraftClient.getInstance();
   private final List<Setting> gs4a7w = new ArrayList<>();
   private boolean y3g4xi = false;

   public Module() {
      ModuleInformation information = this.getClass().getAnnotation(ModuleInformation.class);
      this.neiLP = information.moduleName();
      this.sv4K1 = information.moduleDesc();
      this.mP83 = information.moduleCategory();
      this.dzdNZfj = information.moduleKeybind();
   }

   public List<Setting> getSettings() {
      if (!this.y3g4xi) {
         for (Field field : this.ipkni()) {
            try {
               field.setAccessible(true);
               if (field.get(this) instanceof Setting setting) {
                  this.gs4a7w.add(setting);
               }
            } catch (IllegalAccessException var5) {
               var5.printStackTrace();
            }
         }

         this.y3g4xi = true;
      }

      return this.gs4a7w;
   }

   private List<Field> ipkni() {
      List<Field> fields = new ArrayList<>();

      for (Class<?> cls = this.getClass(); cls != null && cls != Object.class; cls = cls.getSuperclass()) {
         fields.addAll(Arrays.asList(cls.getDeclaredFields()));
      }

      return fields;
   }

   public void setEnabled(boolean enabled) {
      if (this.tAJzvFv != enabled) {
         if (enabled && Viola.getInstance().isPanicMode() && !(this instanceof Panic)) {
            return;
         }

         this.tAJzvFv = enabled;
         if (enabled) {
            this.onEnable();
         } else {
            this.onDisable();
         }

         Interface iface = Instance.get(Interface.class);
         if (!Viola.getInstance().isPanicMode() && iface != null && iface.isModuleStateNotifEnabled() && !this.k3X06() && !this.isHidden()) {
            iface.notifications.post(this.neiLP, enabled);
         }
      }

      if (!this.k3X06()) {
         if (!Viola.getInstance().isPanicMode()) {
            ClientSounds soundsModule = Instance.get(ClientSounds.class);
            if (soundsModule != null && (soundsModule.isEnabled() || this instanceof ClientSounds)) {
               ClientSounds.play(this.isEnabled());
            }
         }
      }
   }

   protected ModeSetting modeCreate() {
      return new ModeSetting("Мод", "Vanilla", "Vanilla", "Grim", "Polar");
   }

   public void onEnable() {
      Viola.getInstance().getEventBus().register(this);
   }

   public void onDisable() {
      Viola.getInstance().getEventBus().unregister(this);
   }

   public boolean isState() {
      return this.tAJzvFv;
   }

   public void switchState() {
      this.toggle();
   }

   public List<Setting> settings() {
      return this.getSettings();
   }

   public String getReadableName() {
      return this.neiLP;
   }

   public String getVisibleName() {
      return this.neiLP;
   }

   public void reset() {
   }

   public void toggle() {
      this.setEnabled(!this.isEnabled());
   }

   public void enableDefault() {
      if (!this.tAJzvFv) {
         this.tAJzvFv = true;
         this.onEnable();
      }
   }

   private boolean k3X06() {
      return this.getClass().getSimpleName().equals("ClickGui");
   }

   @Generated
   public String getName() {
      return this.neiLP;
   }

   @Generated
   public String getDesc() {
      return this.sv4K1;
   }

   @Generated
   public ModuleCategory getCategory() {
      return this.mP83;
   }

   @Generated
   public int getKey() {
      return this.dzdNZfj;
   }

   @Generated
   public boolean isEnabled() {
      return this.tAJzvFv;
   }

   @Generated
   public boolean isHidden() {
      return this.lnoSB;
   }

   @Generated
   public Animation getAnimation() {
      return this.tzaj;
   }

   @Generated
   public MinecraftClient getMc() {
      return this.mc;
   }

   @Generated
   public boolean isSettingsCached() {
      return this.y3g4xi;
   }

   @Generated
   public void setKey(int key) {
      this.dzdNZfj = key;
   }

   @Generated
   public void setHidden(boolean hidden) {
      this.lnoSB = hidden;
   }

   @Generated
   public void setSettingsCached(boolean settingsCached) {
      this.y3g4xi = settingsCached;
   }
}
