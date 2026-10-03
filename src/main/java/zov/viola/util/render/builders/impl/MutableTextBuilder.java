package zov.viola.util.render.builders.impl;

import java.awt.Color;
import net.minecraft.text.Text;
import zov.viola.util.render.builders.AbstractBuilder;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.renderers.impl.BuiltMutableText;

public final class MutableTextBuilder extends AbstractBuilder<BuiltMutableText> {
   private MsdfFont wbjq3E;
   private Text ge8p;
   private float z904z;
   private float ojI6;
   private int luwapS;
   private float rZUe;
   private float bav4Y;
   private int y9x3D;
   private float gFci1G7;
   private int e45bv;

   public MutableTextBuilder font(MsdfFont font) {
      this.wbjq3E = font;
      return this;
   }

   public MutableTextBuilder text(Text text) {
      this.ge8p = text;
      return this;
   }

   public MutableTextBuilder size(float size) {
      this.z904z = size;
      return this;
   }

   public MutableTextBuilder thickness(float thickness) {
      this.ojI6 = thickness;
      return this;
   }

   public MutableTextBuilder color(int color) {
      this.luwapS = color;
      return this;
   }

   public MutableTextBuilder smoothness(float smoothness) {
      this.rZUe = smoothness;
      return this;
   }

   public MutableTextBuilder spacing(float spacing) {
      this.bav4Y = spacing;
      return this;
   }

   public MutableTextBuilder outline(Color color, float thickness) {
      return this.outline(color.getRGB(), thickness);
   }

   public MutableTextBuilder outline(int color, float thickness) {
      this.y9x3D = color;
      this.gFci1G7 = thickness;
      return this;
   }

   public MutableTextBuilder alpha(int alpha) {
      this.e45bv = alpha;
      return this;
   }

   protected BuiltMutableText _build() {
      return new BuiltMutableText(this.wbjq3E, this.ge8p, this.z904z, this.ojI6, this.luwapS, this.rZUe, this.bav4Y, this.y9x3D, this.gFci1G7, this.e45bv);
   }

   @Override
   protected void reset() {
      this.wbjq3E = null;
      this.ge8p = null;
      this.z904z = 0.0F;
      this.ojI6 = 0.05F;
      this.luwapS = -1;
      this.rZUe = 0.5F;
      this.bav4Y = 0.0F;
      this.y9x3D = 0;
      this.gFci1G7 = 0.0F;
   }
}
