package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import org.lwjgl.glfw.GLFW;
import zov.viola.event.list.LookEvent;
import zov.viola.event.list.RotationEvent;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BindSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Third Person",
   moduleDesc = "Вращение камеры без поворота тела",
   moduleCategory = ModuleCategory.RENDER
)
public class FreeLook extends Module {
   public final BindSetting key = new BindSetting("Клавиша", 342);
   private boolean goOYW = false;
   private float ch7SG = 0.0F;
   private float h3Xt0 = 0.0F;
   private float kgxokP = 0.0F;
   private float mdb86 = 0.0F;

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetCamera();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.goOYW = false;
   }

   private void resetCamera() {
      if (this.mc.player != null) {
         this.ch7SG = this.mc.player.getYaw();
         this.h3Xt0 = this.mc.player.getPitch();
         this.kgxokP = this.mc.player.getYaw();
         this.mdb86 = this.mc.player.getPitch();
      }
   }

   private boolean qkad8Cc() {
      long handle = MinecraftClient.getInstance().getWindow().getHandle();
      int keyCode = this.key.getValue();
      return GLFW.glfwGetKey(handle, keyCode) == 1;
   }

   @Subscribe
   public void onLook(LookEvent event) {
      if (this.mc.player != null) {
         boolean wasActive = this.goOYW;
         this.goOYW = this.qkad8Cc();
         if (this.goOYW) {
            this.mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            if (!wasActive) {
               this.kgxokP = this.mc.player.getYaw();
               this.mdb86 = this.mc.player.getPitch();
               this.ch7SG = this.kgxokP;
               this.h3Xt0 = this.mdb86;
            }

            this.ch7SG = this.ch7SG + (float)event.getYaw() * 0.15F;
            this.h3Xt0 = this.h3Xt0 + (float)event.getPitch() * 0.15F;
            this.h3Xt0 = Math.max(-90.0F, Math.min(90.0F, this.h3Xt0));
            event.setCancelled(true);
         } else if (wasActive) {
            this.mc.options.setPerspective(Perspective.FIRST_PERSON);
            this.resetCamera();
         }
      }
   }

   @Subscribe
   public void onRotation(RotationEvent event) {
      if (this.mc.player != null) {
         if (this.goOYW) {
            event.setYaw(this.ch7SG);
            event.setPitch(this.h3Xt0);
         }
      }
   }

   public boolean isActive() {
      return this.goOYW && this.isEnabled();
   }

   public float getCameraYaw() {
      return this.ch7SG;
   }

   public float getCameraPitch() {
      return this.h3Xt0;
   }
}
