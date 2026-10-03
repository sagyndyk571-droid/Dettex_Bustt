package zov.viola.util.text;

import java.util.List;
import lombok.Generated;
import zov.viola.obf.D;

public class BetterText {
   private List<String> iw7Mage;
   public final StringBuilder output = new StringBuilder();
   private int d8aaoo;
   private int pOwa81a = 0;
   private int cgSc = 0;
   private boolean tD6m = true;
   private long v888m = System.currentTimeMillis();

   public BetterText(List<String> texts, int delay) {
      if (texts != null && !texts.isEmpty()) {
         this.iw7Mage = texts;
         this.d8aaoo = delay;
      } else {
         throw new IllegalArgumentException(
            D.k(
               new int[]{
                  139,
                  148,
                  153,
                  178,
                  255,
                  157,
                  136,
                  181,
                  171,
                  209,
                  130,
                  167,
                  177,
                  159,
                  142,
                  178,
                  255,
                  147,
                  132,
                  230,
                  177,
                  132,
                  141,
                  170,
                  255,
                  158,
                  147,
                  230,
                  186,
                  156,
                  145,
                  178,
                  166
               },
               new int[]{223, 241, 225, 198}
            )
         );
      }
   }

   public void update() {
      long currentTime = System.currentTimeMillis();
      if (currentTime - this.v888m >= 100L) {
         this.v888m = currentTime;
         String currentText = this.iw7Mage.get(this.pOwa81a);
         if (currentText == null || currentText.isEmpty()) {
            return;
         }

         if (this.tD6m) {
            if (this.cgSc < currentText.length()) {
               this.output.append(currentText.charAt(this.cgSc));
               this.cgSc++;
            } else {
               this.tD6m = false;
               this.v888m = currentTime + this.d8aaoo;
            }
         } else if (this.cgSc > 0) {
            this.output.deleteCharAt(this.cgSc - 1);
            this.cgSc--;
         } else {
            this.tD6m = true;
            this.pOwa81a = (this.pOwa81a + 1) % this.iw7Mage.size();
         }
      }
   }

   @Generated
   public List<String> getTexts() {
      return this.iw7Mage;
   }

   @Generated
   public StringBuilder getOutput() {
      return this.output;
   }

   @Generated
   public int getDelay() {
      return this.d8aaoo;
   }

   @Generated
   public int getTextIndex() {
      return this.pOwa81a;
   }

   @Generated
   public int getCharIndex() {
      return this.cgSc;
   }

   @Generated
   public boolean isForward() {
      return this.tD6m;
   }

   @Generated
   public long getLastUpdateTime() {
      return this.v888m;
   }

   @Generated
   public void setTexts(List<String> texts) {
      this.iw7Mage = texts;
   }

   @Generated
   public void setDelay(int delay) {
      this.d8aaoo = delay;
   }

   @Generated
   public void setTextIndex(int textIndex) {
      this.pOwa81a = textIndex;
   }

   @Generated
   public void setCharIndex(int charIndex) {
      this.cgSc = charIndex;
   }

   @Generated
   public void setForward(boolean forward) {
      this.tD6m = forward;
   }

   @Generated
   public void setLastUpdateTime(long lastUpdateTime) {
      this.v888m = lastUpdateTime;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BetterText other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getDelay() != other.getDelay()) {
         return false;
      } else if (this.getTextIndex() != other.getTextIndex()) {
         return false;
      } else if (this.getCharIndex() != other.getCharIndex()) {
         return false;
      } else if (this.isForward() != other.isForward()) {
         return false;
      } else if (this.getLastUpdateTime() != other.getLastUpdateTime()) {
         return false;
      } else {
         Object this$texts = this.getTexts();
         Object other$texts = other.getTexts();
         if (this$texts == null ? other$texts == null : this$texts.equals(other$texts)) {
            Object this$output = this.getOutput();
            Object other$output = other.getOutput();
            return this$output == null ? other$output == null : this$output.equals(other$output);
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof BetterText;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getDelay();
      result = result * 59 + this.getTextIndex();
      result = result * 59 + this.getCharIndex();
      result = result * 59 + (this.isForward() ? 79 : 97);
      long $lastUpdateTime = this.getLastUpdateTime();
      result = result * 59 + (int)($lastUpdateTime >>> 32 ^ $lastUpdateTime);
      Object $texts = this.getTexts();
      result = result * 59 + ($texts == null ? 43 : $texts.hashCode());
      Object $output = this.getOutput();
      return result * 59 + ($output == null ? 43 : $output.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "BetterText(texts="
         + this.getTexts()
         + ", output="
         + this.getOutput()
         + ", delay="
         + this.getDelay()
         + ", textIndex="
         + this.getTextIndex()
         + ", charIndex="
         + this.getCharIndex()
         + ", forward="
         + this.isForward()
         + ", lastUpdateTime="
         + this.getLastUpdateTime()
         + ")";
   }
}
