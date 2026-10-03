package zov.viola.module.settings;

import java.util.function.Supplier;
import lombok.Generated;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;

public class BooleanSetting extends Setting {
   private final Animation lmU8e = new Animation(Easing.QUINTIC_OUT, 400L);
   private final Animation s14k3b = new Animation(Easing.QUINTIC_OUT, 400L);
   private boolean nSyej;
   private boolean r5Tg;
   private int m8dpj = -1;
   private String nOkhTxC = "";

   public BooleanSetting(String name, boolean defaultValue) {
      super(name);
      this.r5Tg = defaultValue;
   }

   public boolean getValue() {
      return this.r5Tg;
   }

   @Override
   public String getValueAsString() {
      return Boolean.toString(this.r5Tg);
   }

   @Override
   public void setValueFromString(String value) {
      this.r5Tg = Boolean.parseBoolean(value);
   }

   public void toggle() {
      this.r5Tg = !this.r5Tg;
   }

   public BooleanSetting setVisible(Supplier<Boolean> visible) {
      this.visible = visible;
      return this;
   }

   public String getSearchQuery() {
      return this.nOkhTxC;
   }

   public void setSearchQuery(String query) {
      this.nOkhTxC = query == null ? "" : query;
   }

   public boolean isValue() {
      return this.r5Tg;
   }

   public int getColor() {
      return -1;
   }

   @Generated
   public Animation getAnimation() {
      return this.lmU8e;
   }

   @Generated
   public Animation getClickAnimation() {
      return this.s14k3b;
   }

   @Generated
   public boolean isClicked() {
      return this.nSyej;
   }

   @Generated
   public int getKey() {
      return this.m8dpj;
   }

   @Generated
   public String getBlockSearchQuery() {
      return this.nOkhTxC;
   }

   @Generated
   public void setClicked(boolean clicked) {
      this.nSyej = clicked;
   }

   @Generated
   public void setValue(boolean value) {
      this.r5Tg = value;
   }

   @Generated
   public void setKey(int key) {
      this.m8dpj = key;
   }

   @Generated
   public void setBlockSearchQuery(String blockSearchQuery) {
      this.nOkhTxC = blockSearchQuery;
   }
}
