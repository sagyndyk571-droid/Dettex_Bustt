package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;
import zov.viola.event.Event;

public class EventWorldRender extends Event {
   private final MatrixStack ifIQ;
   private final float oYnyp;

   @Generated
   public MatrixStack getMatrixStack() {
      return this.ifIQ;
   }

   @Generated
   public float getTickDelta() {
      return this.oYnyp;
   }

   @Generated
   public EventWorldRender(MatrixStack matrixStack, float tickDelta) {
      this.ifIQ = matrixStack;
      this.oYnyp = tickDelta;
   }
}
