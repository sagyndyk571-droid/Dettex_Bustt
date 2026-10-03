package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import zov.viola.event.Event;

public class EventHUD extends Event {
   private final DrawContext lO8r;
   private final RenderTickCounter ryvxZh;

   @Generated
   public DrawContext getDrawContext() {
      return this.lO8r;
   }

   @Generated
   public RenderTickCounter getRenderTickCounter() {
      return this.ryvxZh;
   }

   @Generated
   public EventHUD(DrawContext drawContext, RenderTickCounter renderTickCounter) {
      this.lO8r = drawContext;
      this.ryvxZh = renderTickCounter;
   }
}
