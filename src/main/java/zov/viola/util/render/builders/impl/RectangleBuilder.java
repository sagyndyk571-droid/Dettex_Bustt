package zov.viola.util.render.builders.impl;

import zov.viola.util.render.builders.AbstractBuilder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.renderers.impl.BuiltRectangle;

public final class RectangleBuilder extends AbstractBuilder<BuiltRectangle> {
   private SizeState mU3a;
   private QuadRadiusState jtv7;
   private QuadColorState cmNo6X;
   private float q44mk6;

   public RectangleBuilder size(SizeState size) {
      this.mU3a = size;
      return this;
   }

   public RectangleBuilder radius(QuadRadiusState radius) {
      this.jtv7 = radius;
      return this;
   }

   public RectangleBuilder color(QuadColorState color) {
      this.cmNo6X = color;
      return this;
   }

   public RectangleBuilder smoothness(float smoothness) {
      this.q44mk6 = smoothness;
      return this;
   }

   protected BuiltRectangle _build() {
      return new BuiltRectangle(this.mU3a, this.jtv7, this.cmNo6X, this.q44mk6);
   }

   @Override
   protected void reset() {
      this.mU3a = SizeState.NONE;
      this.jtv7 = QuadRadiusState.NO_ROUND;
      this.cmNo6X = QuadColorState.TRANSPARENT;
      this.q44mk6 = 1.0F;
   }
}
