package zov.viola.ui.component;

import lombok.Generated;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;

public abstract class Component implements IComponent {
   public float x;
   public float y;
   public float width;
   public float height;
   private final Animation njhOpd = new Animation(Easing.BACK_OUT, 550L);
   private final Animation av8N0v = new Animation(Easing.CUBIC_OUT, 280L);
   private final Animation akU0m = new Animation(Easing.CUBIC_OUT, 280L);

   public boolean isVisible() {
      return true;
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
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public Animation getAlphaAnim() {
      return this.njhOpd;
   }

   @Generated
   public Animation getAlphaAnimSetting() {
      return this.av8N0v;
   }

   @Generated
   public Animation getAlphaAnimBack() {
      return this.akU0m;
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
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }
}
