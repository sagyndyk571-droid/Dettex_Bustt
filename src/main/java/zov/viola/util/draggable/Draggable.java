package zov.viola.util.draggable;

import com.google.gson.annotations.Expose;
import lombok.Generated;
import net.minecraft.client.gui.screen.ChatScreen;
import zov.viola.module.Module;
import zov.viola.util.IMinecraft;
import zov.viola.util.render.math.MathUtil;

public class Draggable implements IMinecraft {
   @Expose
   private float x;
   @Expose
   private float y;
   @Expose
   public float initialXVal;
   @Expose
   public float initialYVal;
   private float startX;
   private float startY;
   private boolean dragging;
   private float width;
   private float height;
   private float wobbleAngle;
   private float wobbleVel;
   private float prevX;
   private boolean wobbleInit;
   @Expose
   private final String name;
   private final Module module;

   public Draggable(Module module, String name, float initialXVal, float initialYVal) {
      this.module = module;
      this.name = name;
      this.x = this.cjy6Fgg(initialXVal);
      this.y = this.cjy6Fgg(initialYVal);
      this.initialXVal = initialXVal;
      this.initialYVal = initialYVal;
   }

   public final void onDraw() {
      if (!(mc.currentScreen instanceof ChatScreen)) {
         this.daoh57();
      } else {
         if (this.dragging) {
            int mouseX = this.normaliseX();
            int mouseY = this.normaliseY();
            this.x = this.cjy6Fgg(mouseX - this.startX);
            this.y = this.cjy6Fgg(mouseY - this.startY);
            int screenWidth = (int)MathUtil.calc(mc.getWindow().getScaledWidth());
            int screenHeight = (int)MathUtil.calc(mc.getWindow().getScaledHeight());
            this.x = Math.max(0.0F, Math.min(this.x, screenWidth - this.width));
            this.y = Math.max(0.0F, Math.min(this.y, screenHeight - this.height));
         }

         this.daoh57();
      }
   }

   private void daoh57() {
      this.prevX = this.x;
      this.wobbleAngle = 0.0F;
      this.wobbleVel = 0.0F;
   }

   public final void onClick(int button) {
      if (button == 0) {
         this.dragging = true;
         this.startX = (int)(this.normaliseX() - this.x);
         this.startY = (int)(this.normaliseY() - this.y);
      }
   }

   public boolean isHovering() {
      return this.normaliseX() > Math.min(this.x, this.x + this.width)
         && this.normaliseX() < Math.max(this.x, this.x + this.width)
         && this.normaliseY() > Math.min(this.y, this.y + this.height)
         && this.normaliseY() < Math.max(this.y, this.y + this.height);
   }

   public int normaliseX() {
      return (int)(mc.mouse.getX() / mc.getWindow().getScaleFactor());
   }

   public int normaliseY() {
      return (int)(mc.mouse.getY() / mc.getWindow().getScaleFactor());
   }

   public final void onRelease(int button) {
      if (button == 0) {
         this.dragging = false;
      }
   }

   private float cjy6Fgg(float value) {
      return Math.round(value * 2.0F) / 2.0F;
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getInitialXVal() {
      return this.initialXVal;
   }

   @Generated
   public float getInitialYVal() {
      return this.initialYVal;
   }

   @Generated
   public float getStartX() {
      return this.startX;
   }

   @Generated
   public float getStartY() {
      return this.startY;
   }

   @Generated
   public boolean isDragging() {
      return this.dragging;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public float getWobbleAngle() {
      return this.wobbleAngle;
   }

   @Generated
   public float getWobbleVel() {
      return this.wobbleVel;
   }

   @Generated
   public float getPrevX() {
      return this.prevX;
   }

   @Generated
   public boolean isWobbleInit() {
      return this.wobbleInit;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public void setX(float x) {
      this.x = x;
   }

   @Generated
   public void setY(float y) {
      this.y = y;
   }

   @Generated
   public void setInitialXVal(float initialXVal) {
      this.initialXVal = initialXVal;
   }

   @Generated
   public void setInitialYVal(float initialYVal) {
      this.initialYVal = initialYVal;
   }

   @Generated
   public void setStartX(float startX) {
      this.startX = startX;
   }

   @Generated
   public void setStartY(float startY) {
      this.startY = startY;
   }

   @Generated
   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   @Generated
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }

   @Generated
   public void setWobbleAngle(float wobbleAngle) {
      this.wobbleAngle = wobbleAngle;
   }

   @Generated
   public void setWobbleVel(float wobbleVel) {
      this.wobbleVel = wobbleVel;
   }

   @Generated
   public void setPrevX(float prevX) {
      this.prevX = prevX;
   }

   @Generated
   public void setWobbleInit(boolean wobbleInit) {
      this.wobbleInit = wobbleInit;
   }
}
