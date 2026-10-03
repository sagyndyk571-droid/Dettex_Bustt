package zov.viola.util.render.math;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Rectangle;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class Scissor {
   private static Scissor.State jOy1iP = new Scissor.State();
   private static final List<Scissor.State> stateStack = Lists.newArrayList();
   private static float dTiM = 1.0F;
   private static float euWzskT = 0.0F;
   private static float g9rwtBu = 0.0F;

   public static void setGuiTransform(float scale, float centerX, float centerY) {
      dTiM = scale;
      euWzskT = centerX;
      g9rwtBu = centerY;
   }

   public static void resetGuiTransform() {
      dTiM = 1.0F;
      euWzskT = 0.0F;
      g9rwtBu = 0.0F;
   }

   public static void push() {
      stateStack.add(jOy1iP.clone());
   }

   public static void pop() {
      jOy1iP = stateStack.remove(stateStack.size() - 1);
      if (jOy1iP.enabled) {
         RenderSystem.enableScissor(jOy1iP.x, jOy1iP.y, jOy1iP.width, jOy1iP.height);
      }
   }

   public static void unset() {
      RenderSystem.disableScissor();
      jOy1iP.enabled = false;
   }

   public static void setFromComponentCoordinates(int x, int y, int width, int height) {
      setFromComponentCoordinates((double)x, (double)y, (double)width, (double)height);
   }

   public static void setFromComponentCoordinates(double x, double y, double width, double height) {
      Window window = MinecraftClient.getInstance().getWindow();
      int scaleFactor = (int)window.getScaleFactor();
      if (dTiM != 1.0F) {
         x = euWzskT + (x - euWzskT) * dTiM;
         y = g9rwtBu + (y - g9rwtBu) * dTiM;
         width *= dTiM;
         height *= dTiM;
      }

      int screenX = (int)(x * scaleFactor);
      int screenY = (int)(y * scaleFactor);
      int screenWidth = (int)(width * scaleFactor);
      int screenHeight = (int)(height * scaleFactor);
      screenY = window.getHeight() - screenY - screenHeight;
      set(screenX, screenY, screenWidth, screenHeight);
   }

   public static void setFromComponentCoordinates(double x, double y, double width, double height, float scale) {
      Window window = MinecraftClient.getInstance().getWindow();
      float halfRest = (1.0F - scale) / 2.0F;
      double testX = x + width * halfRest;
      double testY = y + height * halfRest;
      double testW = width * scale;
      double testH = height * scale;
      testX = testX * scale + (window.getScaledWidth() - testW) * halfRest;
      int scaleFactor = (int)window.getScaleFactor();
      int screenX = (int)(testX * scaleFactor);
      int screenY = (int)(testY * scaleFactor);
      int screenWidth = (int)(testW * scaleFactor);
      int screenHeight = (int)(testH * scaleFactor);
      screenY = window.getHeight() - screenY - screenHeight;
      set(screenX, screenY, screenWidth, screenHeight);
   }

   public static void scissor(Window window, double x, double y, double width, double height) {
      if (x + width != x && y + height != y && !(x < 0.0) && !(y + height < 0.0)) {
         double scaleFactor = window.getScaleFactor();
         int sx = (int)Math.round(x * scaleFactor);
         int sy = (int)Math.round((window.getScaledHeight() - (y + height)) * scaleFactor);
         int sw = (int)Math.round(width * scaleFactor);
         int sh = (int)Math.round(height * scaleFactor);
         RenderSystem.enableScissor(sx, sy, sw, sh);
      }
   }

   public static void set(int x, int y, int width, int height) {
      Window window = MinecraftClient.getInstance().getWindow();
      Rectangle screen = new Rectangle(0, 0, window.getWidth(), window.getHeight());
      Rectangle current = jOy1iP.enabled ? new Rectangle(jOy1iP.x, jOy1iP.y, jOy1iP.width, jOy1iP.height) : screen;
      Rectangle target = new Rectangle(x + jOy1iP.transX, y + jOy1iP.transY, width, height);
      Rectangle result = current.intersection(target).intersection(screen);
      if (result.width < 0) {
         result.width = 0;
      }

      if (result.height < 0) {
         result.height = 0;
      }

      jOy1iP.enabled = true;
      jOy1iP.x = result.x;
      jOy1iP.y = result.y;
      jOy1iP.width = result.width;
      jOy1iP.height = result.height;
      RenderSystem.enableScissor(result.x, result.y, result.width, result.height);
   }

   public static void translate(int x, int y) {
      jOy1iP.transX = x;
      jOy1iP.transY = y;
   }

   public static void translateFromComponentCoordinates(int x, int y) {
      Window window = MinecraftClient.getInstance().getWindow();
      int scaleFactor = (int)window.getScaleFactor();
      int screenX = x * scaleFactor;
      int screenY = y * scaleFactor;
      screenY = window.getScaledHeight() * scaleFactor - screenY;
      translate(screenX, screenY);
   }

   private static class State implements Cloneable {
      public boolean enabled;
      public int transX;
      public int transY;
      public int x;
      public int y;
      public int width;
      public int height;

      public Scissor.State clone() {
         try {
            return (Scissor.State)super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new AssertionError(var2);
         }
      }
   }
}
