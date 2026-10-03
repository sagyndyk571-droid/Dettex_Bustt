package zov.viola.util.render.builders.impl;

import zov.viola.util.render.builders.AbstractBuilder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.renderers.impl.BuiltBlur;

public final class BlurBuilder extends AbstractBuilder<BuiltBlur> {
   private SizeState zDvrtK7;
   private QuadRadiusState qlM4dI;
   private QuadColorState nQe5Zaa;
   private float t0xuwC;
   private float gMf7yS0;

   public BlurBuilder size(SizeState size) {
      this.zDvrtK7 = size;
      return this;
   }

   public BlurBuilder radius(QuadRadiusState radius) {
      this.qlM4dI = radius;
      return this;
   }

   public BlurBuilder color(QuadColorState color) {
      this.nQe5Zaa = color;
      return this;
   }

   public BlurBuilder smoothness(float smoothness) {
      this.t0xuwC = smoothness;
      return this;
   }

   public BlurBuilder blurRadius(float blurRadius) {
      this.gMf7yS0 = blurRadius;
      return this;
   }

   protected BuiltBlur _build() {
      return new BuiltBlur(this.zDvrtK7, this.qlM4dI, this.nQe5Zaa, this.t0xuwC, this.gMf7yS0);
   }

   @Override
   protected void reset() {
      this.zDvrtK7 = SizeState.NONE;
      this.qlM4dI = QuadRadiusState.NO_ROUND;
      this.nQe5Zaa = QuadColorState.WHITE;
      this.t0xuwC = 1.0F;
      this.gMf7yS0 = 0.0F;
   }
}
