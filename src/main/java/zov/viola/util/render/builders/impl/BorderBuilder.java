package zov.viola.util.render.builders.impl;

import zov.viola.util.render.builders.AbstractBuilder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.renderers.impl.BuiltBorder;

public final class BorderBuilder extends AbstractBuilder<BuiltBorder> {
   private SizeState a9bela;
   private QuadRadiusState bcjNo03;
   private QuadColorState dbeixk;
   private float aau7;
   private float dFZt96;
   private float wz7buj;

   public BorderBuilder size(SizeState size) {
      this.a9bela = size;
      return this;
   }

   public BorderBuilder radius(QuadRadiusState radius) {
      this.bcjNo03 = radius;
      return this;
   }

   public BorderBuilder color(QuadColorState color) {
      this.dbeixk = color;
      return this;
   }

   public BorderBuilder thickness(float thickness) {
      this.aau7 = thickness;
      return this;
   }

   public BorderBuilder smoothness(float internalSmoothness, float externalSmoothness) {
      this.dFZt96 = internalSmoothness;
      this.wz7buj = externalSmoothness;
      return this;
   }

   protected BuiltBorder _build() {
      return new BuiltBorder(this.a9bela, this.bcjNo03, this.dbeixk, this.aau7, this.dFZt96, this.wz7buj);
   }

   @Override
   protected void reset() {
      this.a9bela = SizeState.NONE;
      this.bcjNo03 = QuadRadiusState.NO_ROUND;
      this.dbeixk = QuadColorState.TRANSPARENT;
      this.aau7 = 0.0F;
      this.dFZt96 = 1.0F;
      this.wz7buj = 1.0F;
   }
}
