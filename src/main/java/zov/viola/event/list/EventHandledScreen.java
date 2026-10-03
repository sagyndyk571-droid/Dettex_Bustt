package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;
import zov.viola.event.Event;

public class EventHandledScreen extends Event {
   private final Slot uczc;
   private final DrawContext inia;
   private final int kchqOUB;
   private final int lrqDex;

   @Generated
   public Slot getSlotHover() {
      return this.uczc;
   }

   @Generated
   public DrawContext getDrawContext() {
      return this.inia;
   }

   @Generated
   public int getMouseX() {
      return this.kchqOUB;
   }

   @Generated
   public int getMouseY() {
      return this.lrqDex;
   }

   @Generated
   public EventHandledScreen(Slot slotHover, DrawContext drawContext, int mouseX, int mouseY) {
      this.uczc = slotHover;
      this.inia = drawContext;
      this.kchqOUB = mouseX;
      this.lrqDex = mouseY;
   }
}
