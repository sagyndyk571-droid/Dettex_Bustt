package zov.viola.util.neuro.rotation;

import java.util.Arrays;
import lombok.Generated;

public class TrainingSample {
   private float[] input;
   private float[] output;

   @Generated
   public float[] getInput() {
      return this.input;
   }

   @Generated
   public float[] getOutput() {
      return this.output;
   }

   @Generated
   public void setInput(float[] input) {
      this.input = input;
   }

   @Generated
   public void setOutput(float[] output) {
      this.output = output;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TrainingSample other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         return !Arrays.equals(this.getInput(), other.getInput()) ? false : Arrays.equals(this.getOutput(), other.getOutput());
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof TrainingSample;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + Arrays.hashCode(this.getInput());
      return result * 59 + Arrays.hashCode(this.getOutput());
   }

   @Generated
   @Override
   public String toString() {
      return "TrainingSample(input=" + Arrays.toString(this.getInput()) + ", output=" + Arrays.toString(this.getOutput()) + ")";
   }

   @Generated
   public TrainingSample() {
   }

   @Generated
   public TrainingSample(float[] input, float[] output) {
      this.input = input;
      this.output = output;
   }
}
