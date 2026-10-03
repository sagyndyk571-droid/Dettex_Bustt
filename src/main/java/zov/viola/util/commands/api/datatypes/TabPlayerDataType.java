package zov.viola.util.commands.api.datatypes;

import java.util.Collection;
import java.util.stream.Stream;
import net.minecraft.scoreboard.Team;
import zov.viola.obf.D;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;

public enum TabPlayerDataType implements IDatatypeFor<Team> {
   INSTANCE;

   @Override
   public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
      return new TabCompleteHelper()
         .append(
            this.getTeam()
               .stream()
               .map(Team::getPlayerList)
               .map(Object::toString)
               .map(s -> s.replaceAll("[\\[\\]]", ""))
         )
         .filterPrefix(ctx.getConsumer().getString())
         .sortAlphabetically()
         .stream();
   }

   public Team get(IDatatypeContext datatypeContext) throws CommandException {
      String username = datatypeContext.getConsumer().getString();
      return this.getTeam().stream().filter(s -> s.getName().equalsIgnoreCase(username)).findFirst().orElse(null);
   }

   public Collection<Team> getTeam() {
      return mc.world.getScoreboard().getTeams();
   }
}
