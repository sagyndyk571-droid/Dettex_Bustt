package zov.viola.util.render.builders.impl;

import zov.viola.util.render.builders.AbstractBuilder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.renderers.impl.BuiltGlow;

public final class GlowBuilder extends AbstractBuilder<BuiltGlow> {
   private SizeState zsy9f;
   private QuadRadiusState cgZ6ro;
   private QuadColorState kIv7w9f;
   private float yqpSw;
   private float rBUGT;
   private float lrgrOb6;
   private boolean aztqs;

   public GlowBuilder size(SizeState size) {
      this.zsy9f = size;
      return this;
   }

   public GlowBuilder radius(QuadRadiusState radius) {
      this.cgZ6ro = radius;
      return this;
   }

   public GlowBuilder color(QuadColorState color) {
      this.kIv7w9f = color;
      return this;
   }

   public GlowBuilder glowRadius(float glowRadius) {
      this.yqpSw = glowRadius;
      return this;
   }

   public GlowBuilder softness(float softness) {
      this.rBUGT = softness;
      return this;
   }

   public GlowBuilder intensity(float intensity) {
      this.lrgrOb6 = intensity;
      return this;
   }

   public GlowBuilder additive(boolean additive) {
      this.aztqs = additive;
      return this;
   }

   protected BuiltGlow _build() {
      return new BuiltGlow(this.zsy9f, this.cgZ6ro, this.kIv7w9f, this.yqpSw, this.rBUGT, this.lrgrOb6, this.aztqs);
   }

   @Override
   protected void reset() {
      this.zsy9f = SizeState.NONE;
      this.cgZ6ro = QuadRadiusState.NO_ROUND;
      this.kIv7w9f = QuadColorState.TRANSPARENT;
      this.yqpSw = 8.0F;
      this.rBUGT = 1.25F;
      this.lrgrOb6 = 1.0F;
      this.aztqs = true;
   }
}
