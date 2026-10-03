package zov.viola.util.commands.api.datatypes;

import java.util.List;
import java.util.stream.Stream;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.commands.api.helpers.TabCompleteHelper;
import zov.viola.util.friend.Friend;
import zov.viola.util.friend.FriendRepository;

public enum FriendDataType implements IDatatypeFor<Friend> {
   INSTANCE;

   @Override
   public Stream<String> tabComplete(IDatatypeContext datatypeContext) throws CommandException {
      Stream<String> friends = this.gR2Y().stream().map(Friend::name);
      String context = datatypeContext.getConsumer().getString();
      return new TabCompleteHelper().append(friends).filterPrefix(context).sortAlphabetically().stream();
   }

   public Friend get(IDatatypeContext datatypeContext) throws CommandException {
      String username = datatypeContext.getConsumer().getString();
      return this.gR2Y().stream().filter(s -> s.name().equalsIgnoreCase(username)).findFirst().orElse(null);
   }

   private List<? extends Friend> gR2Y() {
      return FriendRepository.getFriends();
   }
}
