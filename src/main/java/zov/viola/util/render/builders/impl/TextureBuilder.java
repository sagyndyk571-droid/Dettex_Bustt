package zov.viola.util.render.builders.impl;

import net.minecraft.client.texture.AbstractTexture;
import zov.viola.util.render.builders.AbstractBuilder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.renderers.impl.BuiltTexture;

public final class TextureBuilder extends AbstractBuilder<BuiltTexture> {
   private SizeState tyEEs;
   private QuadRadiusState w9Iox6L;
   private QuadColorState anrAg;
   private float vKo0Y5S;
   private float tyVxN;
   private float mN7zw;
   private float ey8p0jo;
   private float m3wkOt;
   private int ry9hq;

   public TextureBuilder size(SizeState size) {
      this.tyEEs = size;
      return this;
   }

   public TextureBuilder radius(QuadRadiusState radius) {
      this.w9Iox6L = radius;
      return this;
   }

   public TextureBuilder color(QuadColorState color) {
      this.anrAg = color;
      return this;
   }

   public TextureBuilder smoothness(float smoothness) {
      this.vKo0Y5S = smoothness;
      return this;
   }

   public TextureBuilder texture(float u, float v, float texWidth, float texHeight, AbstractTexture texture) {
      return this.texture(u, v, texWidth, texHeight, texture.getGlId());
   }

   public TextureBuilder texture(float u, float v, float texWidth, float texHeight, int textureId) {
      this.tyVxN = u;
      this.mN7zw = v;
      this.ey8p0jo = texWidth;
      this.m3wkOt = texHeight;
      this.ry9hq = textureId;
      return this;
   }

   protected BuiltTexture _build() {
      return new BuiltTexture(this.tyEEs, this.w9Iox6L, this.anrAg, this.vKo0Y5S, this.tyVxN, this.mN7zw, this.ey8p0jo, this.m3wkOt, this.ry9hq);
   }

   @Override
   protected void reset() {
      this.tyEEs = SizeState.NONE;
      this.w9Iox6L = QuadRadiusState.NO_ROUND;
      this.anrAg = QuadColorState.WHITE;
      this.vKo0Y5S = 1.0F;
      this.tyVxN = 0.0F;
      this.mN7zw = 0.0F;
      this.ey8p0jo = 0.0F;
      this.m3wkOt = 0.0F;
      this.ry9hq = 0;
   }
}
