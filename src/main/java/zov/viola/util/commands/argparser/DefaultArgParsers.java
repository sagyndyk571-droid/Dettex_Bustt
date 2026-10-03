package zov.viola.util.commands.argparser;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import zov.viola.obf.D;
import zov.viola.util.commands.api.argparser.IArgParser;
import zov.viola.util.commands.api.argument.ICommandArgument;

public class DefaultArgParsers {
   public static final List<IArgParser<?>> ALL = Arrays.asList(
      DefaultArgParsers.IntArgumentParser.INSTANCE,
      DefaultArgParsers.LongArgumentParser.INSTANCE,
      DefaultArgParsers.FloatArgumentParser.INSTANCE,
      DefaultArgParsers.DoubleArgumentParser.INSTANCE,
      DefaultArgParsers.BooleanArgumentParser.INSTANCE
   );

   public static class BooleanArgumentParser implements IArgParser.Stateless<Boolean> {
      public static final DefaultArgParsers.BooleanArgumentParser INSTANCE = new DefaultArgParsers.BooleanArgumentParser();
      public static final List<String> TRUTHY_VALUES = Arrays.asList("1", "true", "yes", "t", "y", "on", "enable");
      public static final List<String> FALSY_VALUES = Arrays.asList("0", "false", "no", "f", "n", "off", "disable");

      @Override
      public Class<Boolean> getTarget() {
         return Boolean.class;
      }

      public Boolean parseArg(ICommandArgument arg) throws RuntimeException {
         String value = arg.getValue();
         if (TRUTHY_VALUES.contains(value.toLowerCase(Locale.US))) {
            return true;
         } else if (FALSY_VALUES.contains(value.toLowerCase(Locale.US))) {
            return false;
         } else {
            throw new IllegalArgumentException(
               "invalid boolean"
            );
         }
      }
   }

   public static enum DoubleArgumentParser implements IArgParser.Stateless<Double> {
      INSTANCE;

      @Override
      public Class<Double> getTarget() {
         return Double.class;
      }

      public Double parseArg(ICommandArgument arg) throws RuntimeException {
         String value = arg.getValue();
         if (!value.matches(
            D.k(
               new int[]{
                  90,
                  109,
                  52,
                  235,
                  41,
                  24,
                  80,
                  232,
                  59,
                  127,
                  51,
                  164,
                  47,
                  109,
                  80,
                  250,
                  88,
                  107,
                  51,
                  164,
                  46,
                  108,
                  80,
                  188,
                  88,
                  107,
                  51,
                  164,
                  47,
                  108,
                  19,
                  233,
                  32
               },
               new int[]{4, 69, 111, 192}
            )
         )) {
            throw new IllegalArgumentException(
               D.k(
                  new int[]{18, 69, 96, 124, 17, 64, 41, 116, 27, 81, 107, 124, 17, 4, 111, 127, 6, 73, 104, 100, 84, 71, 97, 117, 23, 79},
                  new int[]{116, 36, 9, 16}
               )
            );
         } else {
            return Double.parseDouble(value);
         }
      }
   }

   public static enum FloatArgumentParser implements IArgParser.Stateless<Float> {
      INSTANCE;

      @Override
      public Class<Float> getTarget() {
         return Float.class;
      }

      public Float parseArg(ICommandArgument arg) throws RuntimeException {
         String value = arg.getValue();
         if (!value.matches(
            D.k(
               new int[]{
                  122, 90, 85, 249, 9, 47, 49, 250, 27, 72, 82, 182, 15, 90, 49, 232, 120, 92, 82, 182, 14, 91, 49, 174, 120, 92, 82, 182, 15, 91, 114, 251, 0
               },
               new int[]{36, 114, 14, 210}
            )
         )) {
            throw new IllegalArgumentException(
               D.k(
                  new int[]{138, 125, 238, 248, 137, 120, 167, 242, 128, 115, 230, 224, 204, 122, 232, 230, 129, 125, 243, 180, 143, 116, 226, 247, 135},
                  new int[]{236, 28, 135, 148}
               )
            );
         } else {
            return Float.parseFloat(value);
         }
      }
   }

   public static enum IntArgumentParser implements IArgParser.Stateless<Integer> {
      INSTANCE;

      @Override
      public Class<Integer> getTarget() {
         return Integer.class;
      }

      public Integer parseArg(ICommandArgument arg) throws RuntimeException {
         return Integer.parseInt(arg.getValue());
      }
   }

   public static enum LongArgumentParser implements IArgParser.Stateless<Long> {
      INSTANCE;

      @Override
      public Class<Long> getTarget() {
         return Long.class;
      }

      public Long parseArg(ICommandArgument arg) throws RuntimeException {
         return Long.parseLong(arg.getValue());
      }
   }
}
