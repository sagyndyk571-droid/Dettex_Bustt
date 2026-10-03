package zov.viola.module;

public enum ModuleCategory {
   COMBAT,
   MOVEMENT,
   RENDER,
   PLAYER,
   MISC;

   public String getReadableName() {
      String n = this.name().toLowerCase();
      return Character.toUpperCase(n.charAt(0)) + n.substring(1);
   }

   public String getTextureName() {
      return this.name().toLowerCase();
   }
}
