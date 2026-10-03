package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventEntitySpawn;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventPlayerUpdate;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.base.Instance;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.player.other.InventoryUtil;

@ModuleInformation(
   moduleName = "TargetPearl",
   moduleDesc = "Кидает перл за противником при его броске",
   moduleCategory = ModuleCategory.COMBAT
)
public class TargetPearl extends Module {
   private final BooleanSetting fdct = new BooleanSetting(
      D.k(
         new int[]{1185, 1056, 1152, 1068, 1209, 1056, 155, 1058, 1203, 1118, 1160, 1109, 1217, 62, 240, 9, 239, 114, 250, 21, 241, 127},
         new int[]{131, 30, 187, 96}
      ),
      false
   );
   private final SliderSetting vxS84Dm = new SliderSetting(
      "Макс. дистанция",
      40.0,
      10.0,
      120.0,
      5.0
   );
   private final SliderSetting muRbcl = new SliderSetting(
      "Мин. дистанция", 5.0, 1.0, 40.0, 1.0
   );
   private final SliderSetting amvDx9 = new SliderSetting(
      "Точность (блоки)",
      4.0,
      1.0,
      10.0,
      1.0
   );
   private final SliderSetting ixBrd8 = new SliderSetting(
      "Макс. питч вниз", 65.0, 20.0, 90.0, 5.0
   );
   private final SliderSetting wen4j = new SliderSetting(
      D.k(
         new int[]{
            1153, 1079, 1203, 1217, 1173, 1078, 1214, 1213, 128, 1077, 1220, 168, 1250, 1077, 1217, 1202, 1176, 43, 174, 1209, 1179, 1077, 1212, 1200, 137
         },
         new int[]{160, 11, 134, 136}
      ),
      2.0,
      0.0,
      8.0,
      1.0
   );
   private final BooleanSetting mMeC2N = new BooleanSetting(
      "Вернуть взгляд", true
   );
   private final BooleanSetting oglynJ2 = new BooleanSetting(
      "Отладка в чат", true
   );
   private static final int MAX_SIM_TICKS = 160;
   private static final int COLLISION_SUBSTEPS = 4;
   private static final double THROW_VELOCITY = 1.5;
   private static final long LANDING_TTL = 5000L;
   private static final long MIN_TRACK_AGE_MS = 120L;
   private static final long OWN_THROW_GUARD_MS = 500L;
   private static final double MATCH_RADIUS_SQ = 4.0;
   private static final double MAX_OBSERVED_SPEED_SQ = 36.0;
   private static final int HISTORY_LIMIT = 45;
   private static final Random RANDOM = new Random();
   private final Map<UUID, TargetPearl.TrackedData> i2tjqkX = new ConcurrentHashMap<>();
   private final Map<UUID, Deque<Vec3d>> f8x0Ir1 = new ConcurrentHashMap<>();
   private final Set<UUID> fJ1wzN7 = ConcurrentHashMap.newKeySet();
   private final Set<String> qL0z = ConcurrentHashMap.newKeySet();
   private volatile long hUmg;

   private void uulu(UUID pearlId, String stage, String message) {
      if (this.oglynJ2.getValue()) {
         if (this.qL0z.add(pearlId + ":" + stage)) {
            this.logDirect("TargetPearl [" + stage + "]: " + message, Formatting.GRAY);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.i2tjqkX.clear();
      this.f8x0Ir1.clear();
   }

   @Subscribe
   private void onEntitySpawn(EventEntitySpawn event) {
      if (this.mc.player != null && this.mc.world != null) {
         if (event.getEntity() instanceof EnderPearlEntity pearl) {
            long now = System.currentTimeMillis();
            if (now - this.hUmg >= 500L) {
               PlayerEntity thrower = this.yF48i(pearl);
               if (thrower != this.mc.player) {
                  if (!this.qa8o(thrower)) {
                     this.uulu(
                        pearl.getUuid(),
                        "no-thrower",
                        D.k(
                           new int[]{
                              1119,
                              1249,
                              78,
                              1068,
                              1110,
                              1252,
                              1109,
                              1105,
                              1059,
                              1176,
                              78,
                              1105,
                              1117,
                              1172,
                              1115,
                              1115,
                              1111,
                              1263,
                              1110,
                              1069,
                              1070,
                              244,
                              1119,
                              1071,
                              1116,
                              1173,
                              1118,
                              1117,
                              1066,
                              1249,
                              1117,
                              1105,
                              66,
                              252,
                              1070,
                              1119,
                              1110,
                              1260,
                              1069,
                              1070,
                              66,
                              230,
                              78,
                              1118,
                              1113,
                              1258,
                              1108,
                              1119,
                              75
                           },
                           new int[]{98, 212, 110, 111}
                        )
                     );
                  } else {
                     if (this.fdct.getValue()) {
                        KillAura aura = Instance.get(KillAura.class);
                        if (aura == null || aura.getTarget() != thrower) {
                           return;
                        }
                     }

                     this.fJ1wzN7.remove(pearl.getUuid());
                     this.i2tjqkX.put(pearl.getUuid(), new TargetPearl.TrackedData(pearl, now, thrower.getUuid()));
                     if (this.oglynJ2.getValue()) {
                        this.logDirect("TargetPearl: отслеживаю перл от " + thrower.getNameForScoreboard(), Formatting.AQUA);
                     }
                  }
               }
            }
         }
      }
   }

   @Subscribe
   private void onPacket(EventPacket event) {
      if (event.getType() == EventPacket.Type.SEND) {
         if (event.getPacket() instanceof PlayerInteractItemC2SPacket) {
            if (this.mc.player != null) {
               if (this.mc.player.getMainHandStack().isOf(Items.ENDER_PEARL) || this.mc.player.getOffHandStack().isOf(Items.ENDER_PEARL)) {
                  this.hUmg = System.currentTimeMillis();
               }
            }
         }
      }
   }

   @Subscribe
   private void onUpdate(EventPlayerUpdate ignored) {
      if (this.mc.player != null && this.mc.world != null && this.mc.interactionManager != null) {
         long now = System.currentTimeMillis();

         for (TargetPearl.TrackedData data : this.i2tjqkX.values()) {
            if (!data.pearl.isAlive()) {
               if (data.landing != null && !data.landed) {
                  data.landed = true;
                  this.uulu(
                     data.pearl.getUuid(),
                     "landed",
                     String.format(
                        D.k(
                           new int[]{
                              1158,
                              1204,
                              1109,
                              1123,
                              153,
                              1214,
                              1109,
                              1120,
                              1166,
                              1204,
                              1065,
                              1123,
                              1153,
                              1210,
                              1108,
                              1047,
                              153,
                              1203,
                              53,
                              125,
                              151,
                              177,
                              115,
                              119,
                              156,
                              175,
                              37,
                              62,
                              150,
                              164,
                              59,
                              104,
                              223
                           },
                           new int[]{185, 129, 21, 88}
                        ),
                        data.landing.x,
                        data.landing.y,
                        data.landing.z
                     )
                  );
               }
            } else {
               Vec3d velocity = this.bhdVO96(data);
               if (velocity != null) {
                  Vec3d landing = this.phvSQ0b(data.pearl.getPos(), velocity);
                  if (landing != null) {
                     if (data.landing != null && data.landing.squaredDistanceTo(landing) < 1.0) {
                        data.stableTicks++;
                     } else {
                        data.stableTicks = 0;
                     }

                     data.landing = landing;
                     data.landed = false;
                  }
               }
            }
         }

         this.i2tjqkX.values().removeIf(d -> {
            boolean expired = now - d.detectedAt > 5000L;
            if (expired) {
               this.qL0z.removeIf(s -> s.startsWith(d.pearl.getUuid().toString()));
            }

            return expired;
         });
         this.y1R7();
         if (!this.mc.player.isGliding()) {
            if (!this.mc.player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.ENDER_PEARL))) {
               boolean hotbarPearl = InventoryUtil.searchItemHotbar(Items.ENDER_PEARL) != -1;
               if (hotbarPearl || this.mc.player.getOffHandStack().isOf(Items.ENDER_PEARL)) {
                  TargetPearl.TrackedData latest = null;

                  for (TargetPearl.TrackedData d : this.i2tjqkX.values()) {
                     if (d.landing != null && (latest == null || d.detectedAt > latest.detectedAt)) {
                        latest = d;
                     }
                  }

                  if (latest != null) {
                     if (latest.landed || now - latest.detectedAt >= 120L && latest.stableTicks >= 5) {
                        if (!latest.landed && latest.throwerId != null) {
                           PlayerEntity thrower = this.mc.world.getPlayerByUuid(latest.throwerId);
                           if (thrower != null && thrower.squaredDistanceTo(latest.landing) < 9.0) {
                              this.uulu(
                                 latest.pearl.getUuid(),
                                 "near-thrower",
                                 String.format(
                                    D.k(
                                       new int[]{
                                          1068,
                                          1041,
                                          1150,
                                          1147,
                                          1118,
                                          15,
                                          28,
                                          111,
                                          94,
                                          73,
                                          22,
                                          100,
                                          64,
                                          31,
                                          95,
                                          110,
                                          75,
                                          1,
                                          9,
                                          39,
                                          78,
                                          1134,
                                          1026,
                                          1145,
                                          1062,
                                          1045,
                                          1031,
                                          1149,
                                          78,
                                          1054,
                                          1026,
                                          1145,
                                          1113,
                                          1045,
                                          1031,
                                          97,
                                          1108,
                                          15,
                                          1035,
                                          1025,
                                          1118,
                                          1052,
                                          1146,
                                          97,
                                          8314,
                                          15,
                                          1039,
                                          1141,
                                          1069
                                       },
                                       new int[]{110, 47, 57, 65}
                                    ),
                                    latest.landing.x,
                                    latest.landing.y,
                                    latest.landing.z
                                 )
                              );
                              return;
                           }
                        }

                        Vec3d landing = latest.landing;
                        double maxOffset = this.wen4j.getValue();
                        if (maxOffset > 0.0) {
                           double angle = RANDOM.nextDouble() * Math.PI * 2.0;
                           double dist = RANDOM.nextDouble() * maxOffset;
                           landing = landing.add(Math.cos(angle) * dist, 0.0, Math.sin(angle) * dist);
                        }

                        double distance = Math.sqrt(this.mc.player.squaredDistanceTo(landing));
                        if (!(distance < this.muRbcl.getValue()) && !(distance > this.vxS84Dm.getValue())) {
                           Vec3d playerVel = this.mc.player.getVelocity();
                           if (!(Math.hypot(playerVel.x, playerVel.z) > 0.22) && !(playerVel.y < -0.35)) {
                              float[] rot = this.findRotation(landing, latest.pearl.getUuid());
                              if (rot == null) {
                                 if (this.oglynJ2.getValue() && this.fJ1wzN7.add(latest.pearl.getUuid())) {
                                    this.logDirect(
                                       String.format(
                                          D.k(
                                             new int[]{
                                                189,
                                                121,
                                                191,
                                                189,
                                                140,
                                                108,
                                                157,
                                                191,
                                                136,
                                                106,
                                                161,
                                                224,
                                                201,
                                                1061,
                                                1272,
                                                1176,
                                                201,
                                                1114,
                                                1165,
                                                1258,
                                                1244,
                                                1058,
                                                1167,
                                                1252,
                                                1193,
                                                1056,
                                                1269,
                                                250,
                                                1245,
                                                1062,
                                                237,
                                                1176,
                                                1239,
                                                1119,
                                                1271,
                                                1250,
                                                201,
                                                48,
                                                232,
                                                244,
                                                217,
                                                126,
                                                225,
                                                250,
                                                204,
                                                54,
                                                253,
                                                188,
                                                197,
                                                56,
                                                232,
                                                244,
                                                217,
                                                126,
                                                228
                                             },
                                             new int[]{233, 24, 205, 218}
                                          ),
                                          landing.x,
                                          landing.y,
                                          landing.z
                                       ),
                                       Formatting.YELLOW
                                    );
                                 }
                              } else {
                                 this.hdceB(rot[0], rot[1], this.mMeC2N.getValue());
                                 this.i2tjqkX.clear();
                                 if (this.oglynJ2.getValue()) {
                                    this.logDirect(
                                       String.format(
                                          D.k(
                                             new int[]{
                                                1,
                                                83,
                                                160,
                                                14,
                                                48,
                                                70,
                                                130,
                                                12,
                                                52,
                                                64,
                                                190,
                                                83,
                                                117,
                                                1027,
                                                1170,
                                                1111,
                                                1044,
                                                1036,
                                                1256,
                                                73,
                                                1127,
                                                18,
                                                250,
                                                76,
                                                123,
                                                2,
                                                180,
                                                69,
                                                117,
                                                23,
                                                252,
                                                89,
                                                51,
                                                30,
                                                242,
                                                76,
                                                123,
                                                2,
                                                180,
                                                64,
                                                121,
                                                18,
                                                1254,
                                                1105,
                                                1044,
                                                1136,
                                                1250,
                                                1108,
                                                1043,
                                                1034,
                                                1181,
                                                73,
                                                112,
                                                28,
                                                227,
                                                15
                                             },
                                             new int[]{85, 50, 210, 105}
                                          ),
                                          landing.x,
                                          landing.y,
                                          landing.z,
                                          distance
                                       ),
                                       Formatting.GREEN
                                    );
                                 }
                              }
                           } else {
                              this.uulu(
                                 latest.pearl.getUuid(),
                                 "moving",
                                 D.k(
                                    new int[]{
                                       1230,
                                       1049,
                                       1275,
                                       1241,
                                       1205,
                                       1052,
                                       1279,
                                       177,
                                       1214,
                                       1129,
                                       1154,
                                       1235,
                                       1231,
                                       1052,
                                       1270,
                                       177,
                                       1211,
                                       1040,
                                       1275,
                                       1191,
                                       1210,
                                       1055,
                                       1275,
                                       1188,
                                       175,
                                       8246,
                                       227,
                                       1191,
                                       1211,
                                       1121,
                                       227,
                                       1232,
                                       1229,
                                       1042,
                                       1266,
                                       1193,
                                       1204,
                                       1134,
                                       1278,
                                       1199,
                                       1212,
                                       1052,
                                       227,
                                       1197,
                                       1201,
                                       1054,
                                       1270,
                                       1196,
                                       1229,
                                       1042
                                    },
                                    new int[]{143, 34, 195, 145}
                                 )
                              );
                           }
                        } else {
                           this.uulu(
                              latest.pearl.getUuid(),
                              "range",
                              String.format(
                                 D.k(
                                    new int[]{
                                       1075,
                                       1134,
                                       1097,
                                       1252,
                                       1079,
                                       1131,
                                       1102,
                                       1182,
                                       1096,
                                       118,
                                       45,
                                       136,
                                       54,
                                       48,
                                       40,
                                       1172,
                                       1082,
                                       1123,
                                       40,
                                       253,
                                       34,
                                       37,
                                       38,
                                       136,
                                       34,
                                       37,
                                       85,
                                       134,
                                       8211,
                                       118,
                                       1079,
                                       1254,
                                       1081,
                                       1129,
                                       1099,
                                       1255,
                                       1085,
                                       1126,
                                       1094
                                    },
                                    new int[]{7, 86, 8, 166}
                                 ),
                                 distance,
                                 this.muRbcl.getValue(),
                                 this.vxS84Dm.getValue()
                              )
                           );
                        }
                     } else {
                        this.uulu(
                           latest.pearl.getUuid(),
                           "wait",
                           D.k(
                              new int[]{
                                 1148,
                                 1275,
                                 1065,
                                 134,
                                 1035,
                                 1165,
                                 1114,
                                 1175,
                                 1138,
                                 1268,
                                 1106,
                                 1169,
                                 1146,
                                 1161,
                                 1106,
                                 1182,
                                 106,
                                 1165,
                                 1066,
                                 1174,
                                 1151,
                                 1269,
                                 1064,
                                 1176,
                                 1034,
                                 1271,
                                 1106
                              },
                              new int[]{74, 207, 106, 166}
                           )
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private Vec3d bhdVO96(TargetPearl.TrackedData data) {
      Vec3d pos = data.pearl.getPos();
      if (data.prevPos == null) {
         data.prevPos = pos;
         return null;
      } else {
         Vec3d observed = pos.subtract(data.prevPos);
         data.prevPos = pos;
         return !(observed.lengthSquared() < 1.0E-4) && !(observed.lengthSquared() > 36.0) ? observed : null;
      }
   }

   private PlayerEntity yF48i(EnderPearlEntity pearl) {
      PlayerEntity best = null;
      double bestDist = 4.0;

      for (PlayerEntity player : this.mc.world.getPlayers()) {
         Deque<Vec3d> history = this.f8x0Ir1.get(player.getUuid());
         if (history != null) {
            for (Vec3d eye : history) {
               double dist = eye.squaredDistanceTo(pearl.getPos());
               if (dist < bestDist) {
                  bestDist = dist;
                  best = player;
               }
            }
         }
      }

      return best;
   }

   private boolean qa8o(PlayerEntity entity) {
      return entity == null || entity == this.mc.player || !entity.isAlive() ? false : !FriendRepository.isFriend(entity.getNameForScoreboard());
   }

   private void y1R7() {
      for (PlayerEntity player : this.mc.world.getPlayers()) {
         Deque<Vec3d> history = this.f8x0Ir1.computeIfAbsent(player.getUuid(), k -> new ArrayDeque<>());
         history.addLast(player.getEyePos());

         while (history.size() > 45) {
            history.removeFirst();
         }
      }

      this.f8x0Ir1.keySet().removeIf(id -> this.mc.world.getPlayerByUuid(id) == null);
   }

   private float[] findRotation(Vec3d target, UUID pearlId) {
      Vec3d eye = this.mc.player.getEyePos();
      double dx = target.x - eye.x;
      double dz = target.z - eye.z;
      float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
      float bestPitch = this.findBestPitch(yaw, target, -30.0F, 80.0F, 1.0F);
      if (Float.isNaN(bestPitch)) {
         return null;
      } else {
         bestPitch = this.findBestPitch(
            yaw, target, MathHelper.clamp(bestPitch - 1.5F, -30.0F, 80.0F), MathHelper.clamp(bestPitch + 1.5F, -30.0F, 80.0F), 0.25F
         );
         if (Float.isNaN(bestPitch)) {
            return null;
         } else {
            Vec3d landing = this.udoj(yaw, bestPitch);
            if (landing == null) {
               return null;
            } else if (this.mc.player.squaredDistanceTo(landing) < this.muRbcl.getValue() * this.muRbcl.getValue()) {
               return null;
            } else if (bestPitch > this.ixBrd8.getValue()) {
               return null;
            } else {
               double tolerance = this.amvDx9.getValue();
               if (landing.squaredDistanceTo(target) > tolerance * tolerance) {
                  this.uulu(
                     pearlId,
                     "loose",
                     String.format(
                        D.k(
                           new int[]{
                              1272,
                              1083,
                              1064,
                              1081,
                              1156,
                              1092,
                              1069,
                              1096,
                              154,
                              1080,
                              1111,
                              1074,
                              1167,
                              37,
                              1115,
                              1082,
                              1157,
                              1094,
                              1070,
                              1086,
                              1162,
                              37,
                              71,
                              33,
                              148,
                              52,
                              9,
                              36,
                              1163,
                              1086,
                              70,
                              36,
                              8366,
                              37,
                              1109,
                              1084,
                              1166,
                              1077,
                              1057,
                              36,
                              1157,
                              1083,
                              79,
                              1087,
                              1273,
                              1090,
                              1063,
                              1073,
                              1158,
                              1094,
                              79,
                              1083,
                              1154,
                              1095,
                              1064,
                              1095
                           },
                           new int[]{186, 5, 111, 4}
                        ),
                        Math.sqrt(landing.squaredDistanceTo(target))
                     )
                  );
               }

               return new float[]{yaw, MathHelper.clamp(bestPitch, -90.0F, 90.0F)};
            }
         }
      }
   }

   private float findBestPitch(float yaw, Vec3d target, float from, float to, float step) {
      float bestPitch = Float.NaN;
      double bestError = Double.MAX_VALUE;
      float pitch = from;

      while (pitch <= to) {
         Vec3d landing = this.udoj(yaw, pitch);
         if (landing != null) {
            double error = landing.squaredDistanceTo(target);
            if (error < bestError) {
               bestError = error;
               bestPitch = pitch;
            }
         }

         pitch += step;
      }

      return bestPitch;
   }

   private Vec3d udoj(float yaw, float pitch) {
      double yr = Math.toRadians(yaw);
      double pr = Math.toRadians(pitch);
      double x = this.mc.player.getX();
      double y = this.mc.player.getY() + this.mc.player.getEyeHeight(this.mc.player.getPose()) - 0.1;
      double z = this.mc.player.getZ();
      double vx = -Math.sin(yr) * Math.cos(pr) * 1.5;
      double vy = -Math.sin(pr) * 1.5;
      double vz = Math.cos(yr) * Math.cos(pr) * 1.5;
      Vec3d playerVel = this.mc.player.getVelocity();
      vx += playerVel.x;
      vz += playerVel.z;
      if (!this.mc.player.isOnGround()) {
         vy += playerVel.y;
      }

      return this.tefu7I(x, y, z, vx, vy, vz);
   }

   private Vec3d phvSQ0b(Vec3d start, Vec3d velocity) {
      return this.tefu7I(start.x, start.y, start.z, velocity.x, velocity.y, velocity.z);
   }

   private Vec3d tefu7I(double x, double y, double z, double vx, double vy, double vz) {
      double bottom = this.mc.world.getBottomY();

      for (int i = 0; i < 160; i++) {
         double sx = vx / 4.0;
         double sy = vy / 4.0;
         double sz = vz / 4.0;

         for (int s = 0; s < 4; s++) {
            x += sx;
            y += sy;
            z += sz;
            if (y <= bottom) {
               return new Vec3d(x, y, z);
            }

            BlockPos bp = BlockPos.ofFloored(x, y, z);
            if (!this.mc.world.getBlockState(bp).getCollisionShape(this.mc.world, bp).isEmpty()) {
               return new Vec3d(x, y, z);
            }
         }

         boolean inWater = this.mc.world.getBlockState(BlockPos.ofFloored(x, y, z)).getFluidState().isIn(FluidTags.WATER);
         double drag = inWater ? 0.8 : 0.99;
         vx *= drag;
         vy = vy * drag - (inWater ? 0.02 : 0.03);
         vz *= drag;
      }

      return null;
   }

   private void hdceB(float yaw, float pitch, boolean restoreView) {
      int slot = InventoryUtil.searchItemHotbar(Items.ENDER_PEARL);
      boolean offhand = slot == -1 && this.mc.player.getOffHandStack().isOf(Items.ENDER_PEARL);
      if (slot != -1 || offhand) {
         int previous = this.mc.player.getInventory().selectedSlot;
         float prevYaw = this.mc.player.getYaw();
         float prevPitch = this.mc.player.getPitch();
         if (!offhand && slot != previous) {
            this.mc.player.getInventory().selectedSlot = slot;
            this.mc.interactionManager.syncSelectedSlot();
         }

         this.mc.player.setYaw(yaw);
         this.mc.player.setPitch(pitch);
         Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;
         this.mc.interactionManager.interactItem(this.mc.player, hand);
         this.mc.player.swingHand(hand);
         if (!offhand && slot != previous) {
            this.mc.player.getInventory().selectedSlot = previous;
            this.mc.interactionManager.syncSelectedSlot();
         }

         if (restoreView) {
            this.mc.player.setYaw(prevYaw);
            this.mc.player.setPitch(prevPitch);
         }
      }
   }

   private static final class TrackedData {
      final EnderPearlEntity pearl;
      final long detectedAt;
      final UUID throwerId;
      Vec3d landing;
      Vec3d prevPos;
      boolean landed;
      int stableTicks;

      TrackedData(EnderPearlEntity pearl, long detectedAt, UUID throwerId) {
         this.pearl = pearl;
         this.detectedAt = detectedAt;
         this.throwerId = throwerId;
      }
   }
}
