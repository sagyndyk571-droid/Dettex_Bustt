package zov.viola.event.list;

import lombok.Generated;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import zov.viola.event.Event;

public class EventRightClickBlock extends Event {
   private final Hand wvt0;
   private final BlockHitResult zgVm9;

   @Generated
   public Hand getHand() {
      return this.wvt0;
   }

   @Generated
   public BlockHitResult getHitResult() {
      return this.zgVm9;
   }

   @Generated
   public EventRightClickBlock(Hand hand, BlockHitResult hitResult) {
      this.wvt0 = hand;
      this.zgVm9 = hitResult;
   }
}
