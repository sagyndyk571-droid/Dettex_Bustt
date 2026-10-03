package by.x.j2c;

import net.minecraft.util.Identifier;

public class UserProfile {
   public Identifier getAvatarId() {
      return Identifier.of("viola", "textures/gui/avatar.png");
   }

   public String getReadableName() {
      return "Player";
   }
}
