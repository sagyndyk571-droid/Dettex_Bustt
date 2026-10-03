package zov.viola.util.server;

import lombok.Generated;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import zov.viola.Viola;
import zov.viola.module.list.misc.ScoreboardHealth;
import zov.viola.obf.D;
import zov.viola.util.IMinecraft;

public final class Server implements IMinecraft {
   public static int getPing(PlayerEntity entity) {
      PlayerListEntry list = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
      return list != null ? list.getLatency() : 0;
   }

   public static float getHealth(LivingEntity entity, boolean gapple) {
      if (Viola.getInstance().getModuleStorage().get(ScoreboardHealth.class).isEnabled()) {
         if (entity instanceof PlayerEntity player) {
            float fallback = entity.getHealth() + (gapple ? entity.getAbsorptionAmount() : 0.0F);
            ScoreboardObjective objective = player.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
            if (objective == null) {
               return fallback;
            } else {
               ReadableScoreboardScore score = player.getScoreboard().getScore(player, objective);
               MutableText text = ReadableScoreboardScore.getFormattedScore(score, objective.getNumberFormatOr(StyledNumberFormat.EMPTY));

               float boardHp;
               try {
                  boardHp = Float.parseFloat(text.getString().replaceAll("\\D", ""));
               } catch (NullPointerException | NumberFormatException var9) {
                  return fallback;
               }

               return boardHp <= 0.0F && fallback > 0.0F ? fallback : boardHp;
            }
         } else {
            return entity.getHealth() + (gapple ? entity.getAbsorptionAmount() : 0.0F);
         }
      } else {
         return entity.getHealth() + (gapple ? entity.getAbsorptionAmount() : 0.0F);
      }
   }

   @Generated
   private Server() {
      throw new UnsupportedOperationException(
         D.k(
            new int[]{
               123,
               106,
               213,
               0,
               15,
               107,
               207,
               83,
               78,
               34,
               201,
               7,
               70,
               110,
               213,
               7,
               86,
               34,
               223,
               31,
               78,
               113,
               207,
               83,
               78,
               108,
               216,
               83,
               76,
               99,
               210,
               29,
               64,
               118,
               156,
               17,
               74,
               34,
               213,
               29,
               92,
               118,
               221,
               29,
               91,
               107,
               221,
               7,
               74,
               102
            },
            new int[]{47, 2, 188, 115}
         )
      );
   }
}
