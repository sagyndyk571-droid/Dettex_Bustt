package zov.viola.util.player.other;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;

public final class SlownessManager implements IMinecraft {
   private static final KeyBinding[] keybindsList = new KeyBinding[]{
      mc.options.forwardKey, mc.options.leftKey, mc.options.rightKey, mc.options.backKey, mc.options.jumpKey, mc.options.sprintKey, mc.options.sneakKey
   };
   private static final List<SlownessManager.SlowTask> tasks = new ArrayList<>();
   private static final List<SlownessManager.TimeTask> timeTasks = new ArrayList<>();
   private static SlownessManager.SlowTask o95j59 = null;

   public static void addTask(SlownessManager.SlowTask task) {
      for (SlownessManager.SlowTask existing : tasks) {
         if (existing.runnable != null && task.runnable != null && existing.runnable.equals(task.runnable)) {
            return;
         }
      }

      o95j59 = task;
      tasks.add(task);
   }

   public static void addTimeTask(SlownessManager.TimeTask task) {
      timeTasks.add(task);
   }

   public static void updateSlowTasks() {
      long now = System.currentTimeMillis();
      if (o95j59 != null) {
         if (now - o95j59.time < o95j59.duration) {
            for (KeyBinding key : new KeyBinding[]{
               mc.options.forwardKey, mc.options.leftKey, mc.options.rightKey, mc.options.backKey, mc.options.jumpKey, mc.options.sprintKey
            }) {
               key.setPressed(false);
            }
         } else {
            for (KeyBinding key : keybindsList) {
               if (mc.currentScreen == null) {
                  key.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), key.getDefaultKey().getCode()));
               }
            }
         }
      }

      for (SlownessManager.SlowTask task : new ArrayList<>(tasks)) {
         if (now - task.time > task.duration - task.reflection && task.runnable != null) {
            task.runnable.run();
            task.runnable = null;
         }
      }

      Iterator<SlownessManager.SlowTask> iterator = tasks.iterator();

      while (iterator.hasNext()) {
         SlownessManager.SlowTask taskx = iterator.next();
         if (now - taskx.time > taskx.duration || !taskx.condition) {
            iterator.remove();
         }
      }
   }

   public static void updateTimeTasks(boolean ticksUpdate) {
      long now = System.currentTimeMillis();

      for (SlownessManager.TimeTask task : new ArrayList<>(timeTasks)) {
         if (ticksUpdate) {
            task.ticksExisted++;
         }

         if (task.mode == SlownessManager.TimeTask.Mode.TICKS ? task.ticksExisted >= task.time : now - task.time > task.duration && task.runnable != null) {
            task.runnable.run();
            task.runnable = null;
         }
      }

      Iterator<SlownessManager.TimeTask> iterator = timeTasks.iterator();

      while (iterator.hasNext()) {
         SlownessManager.TimeTask task = iterator.next();
         if ((task.mode == SlownessManager.TimeTask.Mode.TICKS ? task.ticksExisted >= task.time : now - task.time > task.duration) || !task.condition) {
            iterator.remove();
         }
      }
   }

   public static boolean slowTasksIsEmpty() {
      return tasks.isEmpty();
   }

   public static boolean timeTasksIsEmpty() {
      return timeTasks.isEmpty();
   }

   @Generated
   private SlownessManager() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               138,
               228,
               110,
               63,
               254,
               229,
               116,
               108,
               191,
               172,
               114,
               56,
               183,
               224,
               110,
               56,
               167,
               172,
               100,
               32,
               191,
               255,
               116,
               108,
               191,
               226,
               99,
               108,
               189,
               237,
               105,
               34,
               177,
               248,
               39,
               46,
               187,
               172,
               110,
               34,
               173,
               248,
               102,
               34,
               170,
               229,
               102,
               56,
               187,
               232
            },
            new int[]{222, 140, 7, 76}
         )
      );
   }

   public static class SlowTask {
      public long duration;
      public long time;
      public long reflection;
      public Runnable runnable;
      public boolean condition;

      public SlowTask(long duration, long reflection, Runnable runnable) {
         this.duration = duration;
         this.reflection = reflection;
         this.runnable = runnable;
         this.time = System.currentTimeMillis();
         this.condition = true;
      }

      public SlowTask(long duration, long reflection, Runnable runnable, boolean condition) {
         this.duration = duration;
         this.reflection = reflection;
         this.runnable = runnable;
         this.time = System.currentTimeMillis();
         this.condition = condition;
      }

      public SlowTask(long duration, Runnable runnable) {
         this.duration = duration;
         this.reflection = 20L;
         this.runnable = runnable;
         this.time = System.currentTimeMillis();
         this.condition = true;
      }

      public SlowTask(long duration, Runnable runnable, boolean condition) {
         this.duration = duration;
         this.reflection = 20L;
         this.runnable = runnable;
         this.time = System.currentTimeMillis();
         this.condition = condition;
      }
   }

   public static class TimeTask {
      public long duration;
      public long time;
      public long ticksExisted;
      public Runnable runnable;
      public boolean condition;
      public SlownessManager.TimeTask.Mode mode;

      public TimeTask(long duration, Runnable runnable, boolean condition) {
         this.mode = SlownessManager.TimeTask.Mode.TIME;
         this.duration = duration;
         this.runnable = runnable;
         this.time = System.currentTimeMillis();
         this.condition = condition;
      }

      public TimeTask(SlownessManager.TimeTask.Mode mode, int ticks, Runnable runnable, boolean condition) {
         this.mode = mode;
         this.runnable = runnable;
         this.time = ticks;
         this.condition = condition;
      }

      public static enum Mode {
         TIME,
         TICKS;
      }
   }
}
