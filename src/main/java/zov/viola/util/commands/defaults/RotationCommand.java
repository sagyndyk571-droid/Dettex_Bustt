package zov.viola.util.commands.defaults;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import zov.viola.obf.D;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.rotation.Rotation;
import zov.viola.util.rotation.RotationComponent;

public class RotationCommand extends Command {
   public RotationCommand() {
      super("r");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      args.requireMin(2);
      String input = args.getString();
      if (args.hasExactlyOne()) {
         String arg = args.getString();
         if (input.contains("yaw")) {
            RotationComponent.getInstance().stopRotation();
            RotationComponent.update(
               new Rotation(Float.parseFloat(arg), MinecraftClient.getInstance().player.getPitch()), 360.0F, 360.0F, 360.0F, 360.0F, 0, 999999, true
            );
         }

         if (input.contains("pitch")) {
            RotationComponent.getInstance().stopRotation();
            RotationComponent.update(
               new Rotation(MinecraftClient.getInstance().player.getYaw(), Float.parseFloat(arg)), 360.0F, 360.0F, 360.0F, 360.0F, 0, 999999, true
            );
         }
      }
   }

   @Override
   public String getShortDesc() {
      return "Ставит ротацию";
   }

   @Override
   public List<String> getLongDesc() {
      return List.of("Test");
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) {
      return args.hasExactlyOne() ? Stream.of("yaw", "pitch") : Stream.empty();
   }
}
