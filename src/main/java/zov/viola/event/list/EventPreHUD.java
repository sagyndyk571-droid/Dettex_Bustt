package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import zov.viola.event.Event;

public class EventPreHUD extends Event {
   private final DrawContext peYVYoy;
   private final RenderTickCounter liH1FK;

   @Generated
   public DrawContext getDrawContext() {
      return this.peYVYoy;
   }

   @Generated
   public RenderTickCounter getRenderTickCounter() {
      return this.liH1FK;
   }

   @Generated
   public EventPreHUD(DrawContext drawContext, RenderTickCounter renderTickCounter) {
      this.peYVYoy = drawContext;
      this.liH1FK = renderTickCounter;
   }
}
