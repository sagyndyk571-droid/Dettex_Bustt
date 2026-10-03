package zov.viola.util.script;

import com.google.common.eventbus.Subscribe;
import java.util.LinkedList;
import java.util.Queue;
import zov.viola.Viola;
import zov.viola.event.list.EventPlayerSync;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.event.list.EventTick;

public class ScriptManager {
   private final Queue<ScriptManager.ScriptTask> qkPkg = new LinkedList<>();

   public ScriptManager() {
      Viola.getInstance().getEventBus().register(this);
   }

   public void tick(Object event) {
      ScriptManager.ScriptTask currentTask = this.qkPkg.peek();
      if (currentTask != null) {
         boolean finished = currentTask.tryTick(event);
         if (finished) {
            this.qkPkg.poll();
         }
      }
   }

   @Subscribe
   public void updatePlayerTick(EventPlayerUpdate tickUpdate) {
      this.tick(tickUpdate);
   }

   @Subscribe
   public void updateTick(EventTick tickUpdate) {
      this.tick(tickUpdate);
   }

   @Subscribe
   public void onSync(EventPlayerSync tickUpdate) {
      this.tick(tickUpdate);
   }

   public void addTask(ScriptManager.ScriptTask task) {
      this.qkPkg.add(task);
   }

   public boolean isFinished() {
      return this.qkPkg.isEmpty();
   }

   public static class ScriptTask {
      private final Queue<ScriptManager.ScriptTask.Step<?>> yP4C3A = new LinkedList<>();
      private int r8X5w = 0;
      private int ugd1qPN = 400;

      public ScriptManager.ScriptTask withMaxIdleTicks(int maxIdleTicks) {
         this.ugd1qPN = Math.max(1, maxIdleTicks);
         return this;
      }

      public <E> ScriptManager.ScriptTask schedule(Class<E> eventClass, ScriptManager.ScriptTask.StepTask<E> action) {
         this.yP4C3A.add(new ScriptManager.ScriptTask.Step<>(eventClass, action));
         return this;
      }

      public boolean tryTick(Object event) {
         ScriptManager.ScriptTask.Step<?> nextStep = this.yP4C3A.peek();
         if (nextStep == null) {
            return true;
         } else {
            boolean progressed = false;
            if (nextStep.l5ygLX.isInstance(event)) {
               boolean stepDone = nextStep.execute(event);
               if (stepDone) {
                  this.yP4C3A.poll();
                  progressed = true;
               }
            }

            if (progressed) {
               this.r8X5w = 0;
               return this.yP4C3A.isEmpty();
            } else {
               this.r8X5w++;
               if (this.r8X5w > this.ugd1qPN) {
                  this.yP4C3A.clear();
                  return true;
               } else {
                  return false;
               }
            }
         }
      }

      public boolean isCompleted() {
         return this.yP4C3A.isEmpty();
      }

      private static class Step<E> {
         private final Class<E> l5ygLX;
         private final ScriptManager.ScriptTask.StepTask<E> lzOj8;

         Step(Class<E> eventClass, ScriptManager.ScriptTask.StepTask<E> action) {
            this.l5ygLX = eventClass;
            this.lzOj8 = action;
         }

         boolean execute(Object event) {
            return this.lzOj8.accept(this.l5ygLX.cast(event));
         }
      }

      @FunctionalInterface
      public interface StepTask<E> {
         boolean accept(E var1);
      }
   }
}
