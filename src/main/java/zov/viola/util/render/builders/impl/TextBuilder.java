package zov.viola.util.render.builders.impl;

import java.awt.Color;
import zov.viola.util.render.builders.AbstractBuilder;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.renderers.impl.BuiltText;

public final class TextBuilder extends AbstractBuilder<BuiltText> {
   private MsdfFont p7Epm1;
   private String n8hU;
   private float epM2;
   private float ksaq4X;
   private int iiXT;
   private float hlltd;
   private float iabi;
   private int qhggtw0;
   private float kA7oB;

   public TextBuilder font(MsdfFont font) {
      this.p7Epm1 = font;
      return this;
   }

   public TextBuilder text(String text) {
      this.n8hU = text;
      return this;
   }

   public TextBuilder size(float size) {
      this.epM2 = size;
      return this;
   }

   public TextBuilder thickness(float thickness) {
      this.ksaq4X = thickness;
      return this;
   }

   public TextBuilder color(int color) {
      this.iiXT = color;
      return this;
   }

   public TextBuilder color(Color color) {
      return this.color(color.getRGB());
   }

   public TextBuilder smoothness(float smoothness) {
      this.hlltd = smoothness;
      return this;
   }

   public TextBuilder spacing(float spacing) {
      this.iabi = spacing;
      return this;
   }

   public TextBuilder outline(Color color, float thickness) {
      return this.outline(color.getRGB(), thickness);
   }

   public TextBuilder outline(int color, float thickness) {
      this.qhggtw0 = color;
      this.kA7oB = thickness;
      return this;
   }

   protected BuiltText _build() {
      return new BuiltText(this.p7Epm1, this.n8hU, this.epM2, this.ksaq4X, this.iiXT, this.hlltd, this.iabi, this.qhggtw0, this.kA7oB);
   }

   @Override
   protected void reset() {
      this.p7Epm1 = null;
      this.n8hU = null;
      this.epM2 = 0.0F;
      this.ksaq4X = 0.05F;
      this.iiXT = -1;
      this.hlltd = 0.5F;
      this.iabi = 0.0F;
      this.qhggtw0 = 0;
      this.kA7oB = 0.0F;
   }
}
