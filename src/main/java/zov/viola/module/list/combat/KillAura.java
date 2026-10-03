package zov.viola.module.list.combat;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Last;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import zov.viola.Viola;
import zov.viola.event.EventGameUpdate;
import zov.viola.event.list.EventChangeSprint;
import zov.viola.event.list.EventTick;
import zov.viola.event.list.MoveInputEvent;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.ModuleSettingDefinitions;
import zov.viola.module.list.movement.AirStuck;
import zov.viola.module.list.movement.ElytraTarget;
import zov.viola.module.list.player.FreeCamera;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ColorSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.math.BestPoint;
import zov.viola.util.math.RotationUtil;
import zov.viola.util.math.StopWatch;
import zov.viola.util.neuro.rotation.AIRotationRecorder;
import zov.viola.util.player.combat.PredictUtils;
import zov.viola.util.player.combat.RaytraceUtil;
import zov.viola.util.player.simulate.SimulatedPlayer;
import zov.viola.util.render.math.GCDFixer;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.rotation.Rotation;
import zov.viola.util.rotation.RotationComponent;
import zov.viola.util.rotation.apex.ApexMovementFix;
import zov.viola.util.rotation.apex.ApexRotation;
import zov.viola.util.rotation.apex.ApexSprintReset;
import zov.viola.util.rotation.apex.ApexTargetCorrection;
import zov.viola.util.text.ValueUnit;

@ModuleInformation(
   moduleName = "KillAura",
   moduleDesc = "Автоматически атакует ближайших врагов",
   moduleCategory = ModuleCategory.COMBAT
)
public class KillAura extends Module {
   public final ModeSetting rotation = ModuleSettingDefinitions.killAuraRotation();
    private final ModeListSetting tXILr = new ModeListSetting(
      "Таргеты",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("Голые", true),
      new BooleanSetting("Инвиз", false),
      new BooleanSetting("НПС", false),
      new BooleanSetting("Животные", true),
      new BooleanSetting("Мобы", true),
      new BooleanSetting("Жители", false)
   );
   public final SliderSetting distance = new SliderSetting(
      "Дистанция",
      ValueUnit.countable(
         "блок",
         "блока",
         "блоков"
      ),
      3.0,
      2.0,
      6.0,
      0.1F
   );
   private final SliderSetting wv8f = new SliderSetting(
      "Пре дистанция",
      ValueUnit.countable(
         "блок",
         "блока",
         "блоков"
      ),
      1.5,
      0.0,
      3.0,
      0.1F
   );
   public final BooleanSetting raycastCheck = new BooleanSetting(
      D.k(
         new int[]{1116, 1172, 1092, 1039, 1142, 1172, 1088, 1037, 99, 1257, 1098, 29, 1150, 1252, 1096, 1032, 1143, 1249, 1095, 1029, 1142},
         new int[]{67, 212, 122, 61}
      ),
      true
   );
   public final BooleanSetting smartAim = new BooleanSetting(
      "Умное наведение", true
   );
    private final SliderSetting id3J = new SliderSetting(
         D.k(
            new int[]{1045, 1163, 146, 1230, 1144, 145, 1155, 1231, 1039, 1277, 1277, 1204, 1035, 1167, 1159, 1201, 1142, 1167, 1165},
            new int[]{52, 177, 191, 140}
         ),
         1.0,
         0.3F,
         3.0,
         0.1F
      )
      .setVisible(() -> this.rotation.is("Sloth"));
   private final SliderSetting in9Mk = new SliderSetting(
         "Частота отводки",
         1.0,
         0.2F,
         3.0,
         0.1F
      )
      .setVisible(() -> this.rotation.is("Sloth"));
   private final SliderSetting nxrlkF = new SliderSetting(
         "Сила отводки", 1.0, 0.3F, 2.5, 0.1F
      )
      .setVisible(() -> this.rotation.is("Sloth"));
   private final SliderSetting e1a6 = new SliderSetting(
         "Мин. скорость", 0.03F, 0.01F, 0.3F, 0.01F
      )
      .setVisible(() -> this.rotation.is("Legit"));
   private final SliderSetting i658 = new SliderSetting(
         "Макс. скорость", 0.6F, 0.1F, 3.0, 0.1F
      )
      .setVisible(() -> this.rotation.is("Legit"));
   private final SliderSetting n3UL10 = new SliderSetting(
         "Плавность", 0.05F, 0.01F, 0.2F, 0.005F
      )
      .setVisible(() -> this.rotation.is("Legit"));
    public final ModeSetting moveFix = new ModeSetting(
      "Коррекция движения",
      "Сфокусированная",
      "Нет",
      "Сфокусированная",
      "Таргетированная",
      "TargetCorrection"
   );
    public final BooleanSetting onlySpace = ModuleSettingDefinitions.killAuraOnlySpace();
    public final ModeSetting slowdownMode = ModuleSettingDefinitions.killAuraSprintReset();
    private ElytraTarget eT4bg;
    private final StopWatch rt911 = new StopWatch();
    private static final float RANDOM_STRENGTH = 0.75F;
   private static final StopWatch stopWatch = new StopWatch();
   private final StopWatch qHm7QCn = new StopWatch();
   private static final long SWORD_SWING_DELAY = 530L;
   private LivingEntity tii1;
   public static LivingEntity lastTarget;
   public int ticksToAttack;
   private int zqtFQ0;
   private boolean b72qK3r;
   public float speedAcceleration;
    public static long lastPhysicalMoveTime;
    public float lastYaw;
   public float lastPitch;
   private int nVt2ixx;
   private boolean gAk1m;
   private boolean tVN91;
   private LivingEntity cyX6k48;
   private float xdBSn;
   private boolean enJim99;
   private boolean y7pQz;
   private float hjgjei;
   private int k48Zk;
   private float d84Y = 1.0F;
   private int y3B60aO;
   private int nxVeb;
   private float xhlsar;
   private float s2vNH;
   private float xfteLF;
   private float t1AB;
   private final Random k5gTXE1 = new Random();
   private float pqeQ;
   private float plO390H;
   private float xiV6z6;
   private float x5uo6;
   private float yolsrD;
   private int qtl1;
   private long bLzhfd;
   private Vec3d oFBUk;
   private long uh9Fr8;
   private long wYvqh;
   private float dmO7ts;
   private float ool07;
   private LivingEntity e0se;
   private LivingEntity tTdH9;
   private float dYsCdha;
   private float uk0NTe;
   private float t40300;
   private float vKwu1;
   private double bO1JNQj;
   private double lekiy;
   private double cQUc;
   private float jRrg;
   private final float u8N3E = 1.8F;
   private int eiO57;
   private int vh2V;
   private float sYo6;
   private long fZwMqQi;
   private int xbzv9F;
   private boolean ow4m;
   private float mib0f;
   private float t0Qs;
   private float mdOcVt;
   private float uz2584;
   private float z347tK;
   private static final double[][] SLOTH_MULTIPOINTS = new double[][]{
      {0.0, 0.9, 0.0},
      {0.0, 0.86, 0.0},
      {0.16, 0.86, 0.0},
      {-0.16, 0.86, 0.0},
      {0.0, 0.86, 0.16},
      {0.0, 0.86, -0.16},
      {0.0, 0.78, 0.0},
      {0.15, 0.78, 0.0},
      {-0.15, 0.78, 0.0},
      {0.0, 0.7, 0.0},
      {0.25, 0.7, 0.0},
      {-0.25, 0.7, 0.0},
      {0.0, 0.62, 0.0},
      {0.0, 0.6, 0.22},
      {0.0, 0.6, -0.22},
      {0.0, 0.56, 0.0},
      {0.22, 0.52, 0.22},
      {-0.22, 0.52, 0.22},
      {0.22, 0.52, -0.22},
      {-0.22, 0.52, -0.22},
      {0.0, 0.45, 0.0},
      {0.0, 0.4, 0.2},
      {0.0, 0.34, 0.0},
      {0.3, 0.34, 0.0},
      {-0.3, 0.34, 0.0},
      {0.0, 0.25, 0.0},
      {0.0, 0.18, 0.0},
      {0.0, 0.1, 0.0},
      {0.3, 0.08, 0.0},
      {-0.3, 0.08, 0.0},
      {0.0, 0.02, 0.0}
   };
   private int l34P;
   private long vrfD;
   private Vec3d pm7kP = new Vec3d(0.0, 0.7, 0.0);
   private Vec3d m9o5xiw = new Vec3d(0.0, 0.7, 0.0);
   private long s4lm9Q;
   private long tU5G;
   private Vec3d tCzzmCe = Vec3d.ZERO;
   private float in0J5 = 0.35F;
   private int j5p8Dok;
   private int ilhZq8q = 4;
   private boolean yFhh;
   private long tZFee = 4000L;
   private int fYHB;
   private double zl5gB4;
   private double f0oomV;
   private int bpy6q;
   private int jv2X = 2;
   private int zNFg8;
   private boolean i0hP;
   private boolean qoHb4a4;
   private int skS8;
   private float ie0ap;
   private float op9x;
   private float k42Zm;
   private long a1ns77;
   private float gycB95 = -1.0F;
   private int uEcxv;
   private int mKytle;
   private int ryo7oc5;
   private int qt10;
   private int mopN3;
   private int nrq1YGg;
   private int gDuag;
   private int gyw7q;
    public ElytraTarget getElytraTarget() {
      if (this.eT4bg == null) {
         this.eT4bg = Viola.getInstance().getModuleStorage().get(ElytraTarget.class);
      }

      return this.eT4bg;
   }

    @Subscribe
   private void onGameUpdate(EventGameUpdate e) {
      if (this.mc.player != null && this.tii1 != null) {
         if (!this.e3lChG()) {
            Viola.getInstance().getModuleStorage().setRandomness(1.0F);
              if (!AIRotationRecorder.isRecording()) {
                boolean playerOnElytra = this.mc.player.isGliding();
               if (!playerOnElytra || this.eT4bg != null && this.eT4bg.isEnabled()) {
                  String var3 = this.rotation.getValue();
                  switch (var3) {
                     case "ReallyWorld":
                        this.updateVanillaRotation(this.tii1);
                        break;
                     case "Smooth":
                        this.updateSmoothRotation(this.tii1);
                        break;
                     case "Funtime":
                        this.updateFuntimeRotation(this.tii1);
                        break;
                     case "SPtest":
                        this.updateSpookyTimeRotation(this.tii1);
                        break;
                     case "AresMine":
                        this.updateAresMineRotation(this.tii1);
                        break;
                     case "Sloth":
                        this.updateSlothRotation(this.tii1);
                        break;
                      case "Legit":
                         this.updateLegitRotation(this.tii1);
                         break;
                      case "ApexTime":
                         this.updateApexTimeRotation(this.tii1);
                         break;
                   }
               }
            }
         }
      }
   }

    @Subscribe
    private void onChangeSprint(EventChangeSprint e) {
       if (!this.slowdownMode.is("Легитный")) {
          if (this.canStopSprinting()) {
             e.setSprinting(false);
          }
       }
    }

   @Subscribe
    private void onMoveInput(MoveInputEvent event) {
       if (this.mc.player != null && this.tii1 != null) {
          if (this.mc.player.isGliding()) {
             if (this.moveFix.is("Таргетированная") || this.moveFix.is("TargetCorrection")) {
                event.forward = 0.0F;
                event.strafe = 0.0F;
             }
             return;
          }
          if (event.forward == 0.0F && event.strafe == 0.0F) {
             return;
          }
          ApexMovementFix.StrafeInput in = new ApexMovementFix.StrafeInput() {
             public float getForward() {
                return event.forward;
             }

             public float getStrafe() {
                return event.strafe;
             }

             public void setForward(float v) {
                event.forward = v;
             }

             public void setStrafe(float v) {
                event.strafe = v;
             }
          };
          if (this.moveFix.is("Таргетированная")) {
             ApexTargetCorrection.correctFullTarget(this.mc.player, this.lastYaw, this.tii1, in);
          } else if (this.moveFix.is("TargetCorrection")) {
             if (this.mc.options.forwardKey.isPressed()) {
                ApexTargetCorrection.correctFocused(this.mc.player, this.lastYaw, this.tii1, in);
             }
          }
       }
    }

   @Subscribe
   private void onUpdate(EventTick ignored) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.ticksToAttack > 0) {
            this.ticksToAttack--;
         }

         this.gffav0();
         if (this.tii1 != null) {
             lastTarget = this.tii1;
             if (this.canStopSprinting() && !this.slowdownMode.is("Легитный")) {
                this.mc.player.setSprinting(false);
             }

             if (this.slowdownMode.is("Легитный") && this.tii1 != null) {
               boolean hitWindow = this.ticksToAttack <= 1 || Viola.getInstance().getIdealHitUtils().cooldownIsReached(false);
               if (!this.gAk1m && hitWindow && this.mc.player.isSprinting() && this.mc.player.input.movementForward > 0.0F && Math.random() < 0.6F) {
                  this.gAk1m = true;
                  this.nVt2ixx = 1 + (int)(Math.random() * 2.0);
                  this.mc.options.forwardKey.setPressed(false);
               } else if (this.gAk1m) {
                  this.nVt2ixx--;
                  if (this.nVt2ixx <= 0) {
                     this.gAk1m = false;
                     this.mc
                        .options
                        .forwardKey
                        .setPressed(
                           InputUtil.isKeyPressed(
                              this.mc.getWindow().getHandle(), InputUtil.fromTranslationKey(this.mc.options.forwardKey.getBoundKeyTranslationKey()).getCode()
                           )
                        );
                  }
               }
            }

            boolean wantJump = this.rotation.is("Funtime")
               && !this.onlySpace.getValue()
               && !this.mc.player.isGliding()
               && !this.mc.player.hasVehicle()
               && !Viola.getInstance().getIdealHitUtils().cooldownIsReached(false);
            if (wantJump && this.mc.player.isOnGround() && !this.mc.options.jumpKey.isPressed()) {
               this.mc.options.jumpKey.setPressed(true);
               this.tVN91 = true;
            } else if (this.tVN91 && (!wantJump || !this.mc.player.isOnGround())) {
               this.mc.options.jumpKey.setPressed(false);
               this.tVN91 = false;
            }

             if (this.canAttack()) {
                boolean sprintWasOn = this.mc.player.isSprinting();
                boolean sprintTap = this.rotation.is("Funtime") && sprintWasOn && Math.random() < 0.65F;
                if (sprintTap) {
                  this.mc.player.setSprinting(false);
               }

               AirStuck airStuck = Viola.getInstance().getModuleStorage().get(AirStuck.class);
               if (airStuck != null && airStuck.isEnabled()) {
                  airStuck.pauseForAttack();
               }

               if (this.rotation.is("Sloth") && this.mc.player != null) {
                  float gcd = this.zA58();
                  float lY = this.mdOcVt;
                  float lP = MathHelper.clamp(this.uz2584, -89.0F, 89.0F);
                  lY -= (lY - this.mib0f) % gcd;
                  lP -= (lP - this.t0Qs) % gcd;
                  this.mc.player.setYaw(lY);
                  this.mc.player.setPitch(lP);
                  this.mib0f = lY;
                  this.t0Qs = lP;
               }

                boolean apexSprintReset = this.slowdownMode.is("Перед ударом");
                if (apexSprintReset) {
                   ApexSprintReset.stopSprint(this.mc);
                }
                this.mc.interactionManager.attackEntity(this.mc.player, this.tii1);
                this.mc.player.swingHand(Hand.MAIN_HAND);
                if (apexSprintReset) {
                   ApexSprintReset.startSprint(this.mc);
                }
               this.qHm7QCn.reset();
               if (sprintTap) {
                  this.mc.player.setSprinting(true);
               }

               this.mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(this.mc.player.input.playerInput));
               this.ticksToAttack = this.rotation.is("Sloth")
                  ? 3 + (int)(Math.random() * 6.0)
                  : (this.rotation.is("Legit") ? 9 + (int)(Math.random() * 2.0) : 5 + (int)(Math.random() * 2.0));
               if (this.rotation.is("Funtime")) {
                  this.utSreJ7();
               }

               if (this.rotation.is("Sloth")) {
                  this.slOnAttack();
               }
            }
         } else {
            this.speedAcceleration = 0.0F;
         }
      }
   }

   private boolean s44kh8v(Entity entity) {
      if (!entity.isAlive()) {
         return false;
      } else if (Viola.getInstance().getModuleStorage().get(AntiBot.class).isBot(entity)) {
         return false;
      } else {
         PlayerEntity player = (PlayerEntity)(Viola.getInstance().getModuleStorage().get(FreeCamera.class).fakePlayer != null
            ? Viola.getInstance().getModuleStorage().get(FreeCamera.class).fakePlayer
            : this.mc.player);
         if (entity == Viola.getInstance().getModuleStorage().get(FreeCamera.class).fakePlayer) {
            return false;
         } else if (entity instanceof ClientPlayerEntity) {
            return false;
         } else if (entity instanceof ArmorStandEntity) {
            return false;
         } else if (entity.isInvisible() && !this.tXILr.isEnabled("Инвиз")) {
            return false;
         } else {
            if (entity instanceof PlayerEntity p) {
               if (!FriendRepository.shouldAttack(p)) {
                  return false;
               }

               if (!this.wd9hTDc(p)) {
                  return this.tXILr.isEnabled("НПС");
               }

               if (p.getArmor() != 0 && !this.tXILr.isEnabled("Игроки")) {
                  return false;
               }

               if (p.getArmor() == 0 && !this.tXILr.isEnabled("Голые")) {
                  return false;
               }
            } else if (entity instanceof MerchantEntity) {
               if (!this.tXILr.isEnabled("Жители")) {
                  return false;
               }
            } else if (entity instanceof HostileEntity || entity instanceof AmbientEntity) {
               if (!this.tXILr.isEnabled("Мобы")) {
                  return false;
               }
            } else if ((entity instanceof PassiveEntity || entity instanceof FishEntity)
               && !this.tXILr.isEnabled("Животные")) {
               return false;
            }

            return !(
               player.getEyePos().distanceTo(BestPoint.getNearestPoint(entity)) > (player.isGliding() ? 50.0 : this.distance.getValue() + this.wv8f.getValue())
            );
         }
      }
   }

   private boolean wd9hTDc(PlayerEntity player) {
      return player == this.mc.player ? true : this.mc.getNetworkHandler() != null && this.mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) != null;
   }

   public boolean canAttack() {
      if (this.tii1 == null) {
         return false;
      } else if (this.e3lChG()) {
         return false;
      } else {
         boolean slDbg = this.rotation.is("Sloth");
         if (slDbg) {
            this.uEcxv++;
            if (this.mc.player != null) {
               long now = System.currentTimeMillis();
               if (this.a1ns77 == 0L) {
                  this.a1ns77 = now;
               }

               if (this.tii1 != null) {
                  float h = this.tii1.getHealth();
                  if (this.gycB95 >= 0.0F && this.gycB95 - h > 0.01F) {
                     this.gyw7q++;
                  }

                  this.gycB95 = h;
               }

               if (now - this.a1ns77 >= 15000L) {
                  this.a1ns77 = now;
                  this.mc
                     .player
                     .sendMessage(
                        Text.literal(
                           String.format(
                              D.k(
                                 new int[]{
                                    241,
                                    178,
                                    129,
                                    93,
                                    222,
                                    137,
                                    169,
                                    112,
                                    237,
                                    188,
                                    205,
                                    84,
                                    195,
                                    147,
                                    136,
                                    86,
                                    151,
                                    196,
                                    137,
                                    18,
                                    206,
                                    140,
                                    138,
                                    15,
                                    143,
                                    133,
                                    205,
                                    78,
                                    138,
                                    130,
                                    137,
                                    15,
                                    143,
                                    133,
                                    205,
                                    70,
                                    222,
                                    128,
                                    208,
                                    23,
                                    206,
                                    193,
                                    159,
                                    83,
                                    211,
                                    220,
                                    200,
                                    86,
                                    138,
                                    133,
                                    132,
                                    65,
                                    222,
                                    220,
                                    200,
                                    86,
                                    138,
                                    130,
                                    159,
                                    91,
                                    222,
                                    179,
                                    136,
                                    83,
                                    201,
                                    137,
                                    208,
                                    23,
                                    206
                                 },
                                 new int[]{170, 225, 237, 50}
                              ),
                              this.gDuag,
                              this.gyw7q,
                              this.mKytle,
                              this.ryo7oc5,
                              this.qt10,
                              this.mopN3,
                              this.nrq1YGg
                           )
                        ),
                        false
                     );
                  this.uEcxv = this.mKytle = this.ryo7oc5 = this.qt10 = this.mopN3 = this.nrq1YGg = this.gDuag = this.gyw7q = 0;
               }
            }
         }

         if (this.rotation.is("Sloth") && !this.i0hP) {
            return false;
         } else {
            if (this.rotation.is("Funtime")) {
               if (Math.abs(MathHelper.wrapDegrees(this.xfteLF - this.lastYaw)) > 8.0F || Math.abs(this.t1AB - this.lastPitch) > 6.0F) {
                  return false;
               }

               PlayerEntity fPlayer = (PlayerEntity)(Viola.getInstance().getModuleStorage().get(FreeCamera.class).fakePlayer != null
                  ? Viola.getInstance().getModuleStorage().get(FreeCamera.class).fakePlayer
                  : this.mc.player);
               if (!RaytraceUtil.rayTrace(fPlayer.getRotationVector(), this.distance.getValue(), this.tii1.getBoundingBox().expand(0.2F, 0.2F, 0.2F))) {
                  return false;
               }
            }

             PlayerEntity player = (PlayerEntity)(Viola.getInstance().getModuleStorage().get(FreeCamera.class).fakePlayer != null
                ? Viola.getInstance().getModuleStorage().get(FreeCamera.class).fakePlayer
                : this.mc.player);
             boolean anyElytra = this.mc.player.isGliding() || this.tii1.isGliding();
             if (!Viola.getInstance().getIdealHitUtils().cooldownIsReached(false)) {
                if (this.rotation.is("Sloth")) {
                   this.mKytle++;
               }

               return false;
            } else if (this.ticksToAttack > 0) {
               if (this.rotation.is("Sloth")) {
                  this.ryo7oc5++;
               }

               return false;
            } else if (this.mc.player.getMainHandStack().isEmpty() && !this.qHm7QCn.isReached(530L)) {
               return false;
            } else if (anyElytra) {
               return this.eT4bg != null && this.eT4bg.isEnabled() ? this.eT4bg.canAttack(this.tii1) : false;
            } else if (!RaytraceUtil.rayTrace(player.getRotationVector(), this.distance.getValue(), this.tii1.getBoundingBox()) && this.raycastCheck.getValue()
               )
             {
               if (this.rotation.is("Sloth")) {
                  this.qt10++;
               }

               return false;
            } else {
               if (this.mc.world != null) {
                  Vec3d eyePos2 = player.getEyePos();
                  Vec3d lookDir = player.getRotationVector();
                  double range = this.distance.getValue();
                  BlockHitResult blockHit = RaytraceUtil.raycast(eyePos2, eyePos2.add(lookDir.multiply(range)), ShapeType.OUTLINE, player);
                  if (blockHit != null) {
                     double dBlock = blockHit.getPos().squaredDistanceTo(eyePos2);
                     Box bb = this.tii1.getBoundingBox();
                     double dx = Math.max(bb.minX - eyePos2.x, Math.max(0.0, eyePos2.x - bb.maxX));
                     double dy = Math.max(bb.minY - eyePos2.y, Math.max(0.0, eyePos2.y - bb.maxY));
                     double dz = Math.max(bb.minZ - eyePos2.z, Math.max(0.0, eyePos2.z - bb.maxZ));
                     double dTarget = dx * dx + dy * dy + dz * dz;
                     if (dBlock < dTarget + 0.01) {
                        return false;
                     }
                  }
               }

               if (player.getEyePos().distanceTo(BestPoint.getNearestPoint(this.tii1)) > this.distance.getValue() - 0.2F) {
                  if (this.rotation.is("Sloth")) {
                     this.mopN3++;
                  }

                  return false;
               } else {
                  if (this.rotation.is("Sloth")) {
                     this.nrq1YGg++;
                  }

                  boolean critOk = Viola.getInstance().getIdealHitUtils().canCritical();
                  if (this.rotation.is("Sloth") && critOk) {
                     this.gDuag++;
                  }

                  return critOk;
               }
            }
         }
      }
   }

   public boolean canStopSprinting() {
      if (this.tii1 == null) {
         return false;
      } else if (!Viola.getInstance().getIdealHitUtils().cooldownIsReached(true)) {
         return false;
      } else {
         return this.ticksToAttack > 1 ? false : SimulatedPlayer.simulateLocalPlayer(1).fallDistance != 0.0F;
      }
   }

   private boolean e3lChG() {
      if (this.mc.player == null) {
         return false;
      } else if (this.mc.player.isUsingItem()) {
         return true;
      } else {
         ItemStack main = this.mc.player.getMainHandStack();
         ItemStack off = this.mc.player.getOffHandStack();
         return this.cpm7(main) || this.cpm7(off);
      }
   }

   private boolean cpm7(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else if (stack.get(DataComponentTypes.FOOD) != null) {
         return true;
      } else {
         Item item = stack.getItem();
         return item == Items.ENDER_EYE
            || item == Items.NETHERITE_SCRAP
            || item == Items.SNOWBALL
            || item == Items.SUGAR
            || item == Items.DRIED_KELP
            || item == Items.WIND_CHARGE;
      }
   }

   private void gffav0() {
      LivingEntity best = null;
      double bestFovDot = -1.0;
      Vec3d eyePos = this.mc.player.getEyePos();
      Vec3d lookVec = this.mc.player.getRotationVec(1.0F);

      for (Entity entity : this.mc.world.getEntities()) {
         if (entity instanceof LivingEntity && this.s44kh8v(entity)) {
            double score;
            if (this.rotation.is("SPtest")) {
               score = -eyePos.distanceTo(BestPoint.getNearestPoint(entity));
            } else {
               Vec3d targetVec = BestPoint.getNearestPoint(entity).subtract(eyePos).normalize();
               score = lookVec.dotProduct(targetVec);
            }

            if (score > bestFovDot) {
               bestFovDot = score;
               best = (LivingEntity)entity;
            }
         }
      }

      if (this.tii1 == null || !this.s44kh8v(this.tii1)) {
         this.tii1 = best;
      } else if (best != null && best != this.tii1) {
         double curDist = eyePos.distanceTo(BestPoint.getNearestPoint(this.tii1));
         double bestDist = eyePos.distanceTo(BestPoint.getNearestPoint(best));
         boolean switchByDistance = this.rotation.is("SPtest") && curDist - bestDist > 1.0;
         boolean switchByPriority = !this.rotation.is("SPtest") && bestFovDot > 0.9 && curDist - bestDist > 2.0;
         if (switchByDistance || switchByPriority) {
            this.tii1 = best;
         }
      }
   }

   private void updateVanillaRotation(LivingEntity target) {
      if (target != null) {
         Vec3d targetPoint = this.eDxXn(target, BestPoint.getNearestPoint(target), this.distance.getValue());
 

         Rotation rotation = new Rotation(RotationUtil.calculate(targetPoint));
         float targetYaw = rotation.getYaw();
         float targetPitch = rotation.getPitch();
         float deltaYaw = MathHelper.wrapDegrees(targetYaw - this.lastYaw);
         float deltaPitch = targetPitch - this.lastPitch;
         if (Math.abs(deltaYaw) < 0.5F) {
            deltaYaw = 0.0F;
         }

         if (Math.abs(deltaPitch) < 0.5F) {
            deltaPitch = 0.0F;
         }

         float dist = (float)this.mc.player.getEyePos().distanceTo(targetPoint);
         float proximity = MathHelper.clamp((dist - 0.3F) / 4.0F, 0.0F, 1.0F);
         float yawSmooth = MathHelper.clamp(0.16F + Math.abs(deltaYaw) / 450.0F, 0.12F, 0.32F);
         float pitchSmooth = MathHelper.clamp(0.14F + Math.abs(deltaPitch) / 380.0F, 0.1F, 0.28F);
         float maxYawStep = MathHelper.clamp(2.5F + proximity * 18.0F, 2.5F, 22.0F);
         float maxPitchStep = MathHelper.clamp(1.8F + proximity * 11.0F, 1.8F, 13.0F);
         float yawStep = MathHelper.clamp(deltaYaw * yawSmooth, -maxYawStep, maxYawStep);
         float pitchStep = MathHelper.clamp(deltaPitch * pitchSmooth, -maxPitchStep, maxPitchStep);
         float newYaw = this.lastYaw + yawStep;
         float newPitch = this.lastPitch + pitchStep;
         float gcd = GCDFixer.getGCDValue();
         newYaw -= (newYaw - this.lastYaw) % gcd;
         newPitch -= (newPitch - this.lastPitch) % gcd;
         newPitch = MathHelper.clamp(newPitch, -90.0F, 90.0F);
         Rotation smoothRot = new Rotation(newYaw, newPitch);
         RotationComponent.update(smoothRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
         this.lastYaw = smoothRot.getYaw();
         this.lastPitch = smoothRot.getPitch();
      }
   }

   private void sKvK6() {
      this.tTdH9 = null;
      this.t40300 = this.vKwu1 = 0.0F;
      this.bO1JNQj = this.lekiy = this.cQUc = 0.0;
      this.jRrg = 0.0F;
      this.eiO57 = this.vh2V = 0;
      this.fZwMqQi = 0L;
      this.ow4m = false;
      this.xbzv9F = 0;
      this.l34P = 0;
      this.vrfD = 0L;
      this.pm7kP = new Vec3d(0.0, 0.7, 0.0);
      this.m9o5xiw = new Vec3d(0.0, 0.7, 0.0);
      this.s4lm9Q = 0L;
      this.tU5G = 0L;
      this.tCzzmCe = Vec3d.ZERO;
      this.in0J5 = 0.35F;
      this.j5p8Dok = 0;
      this.ilhZq8q = 4;
      this.yFhh = false;
      this.fYHB = 0;
      this.zl5gB4 = Math.random() - 0.5;
      this.f0oomV = Math.random() - 0.5;
      this.bpy6q = 0;
      this.jv2X = 2;
      this.zNFg8 = 0;
      this.i0hP = false;
      this.ie0ap = 0.0F;
      this.op9x = this.k42Zm = 0.0F;
      if (this.mc.player != null) {
         this.dYsCdha = this.mc.player.getYaw();
         this.uk0NTe = this.mc.player.getPitch();
         this.mib0f = this.dYsCdha;
         this.t0Qs = this.uk0NTe;
         this.mdOcVt = this.dYsCdha;
         this.uz2584 = this.uk0NTe;
      } else {
         this.dYsCdha = this.uk0NTe = 0.0F;
         this.mib0f = this.t0Qs = 0.0F;
         this.mdOcVt = this.uz2584 = 0.0F;
      }
   }

   private float zA58() {
      double s = this.mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
      return (float)(s * s * s * 1.2);
   }

   private void slPickAimPoint(LivingEntity e) {
      Box bb = e.getBoundingBox();
      double w = bb.maxX - bb.minX;
      double h = bb.maxY - bb.minY;
      double d = bb.maxZ - bb.minZ;
      this.bO1JNQj = (Math.random() - 0.5) * w * 0.12;
      this.lekiy = (Math.random() - 0.5) * h * 0.11;
      this.cQUc = (Math.random() - 0.5) * d * 0.12;
   }

   public void slOnAttack() {
      if (Math.random() < 0.78F) {
         this.eiO57 = 1;
      } else {
         this.eiO57 = 0;
      }

      this.vh2V = 0;
      this.ie0ap = 0.0F;
      this.bpy6q = 0;
      this.zNFg8 = 0;
      this.i0hP = false;
      this.skS8 = 0;
      this.jv2X = 1 + (int)(Math.random() * 4.0);
      this.sYo6 = this.uk0NTe;
   }

   private float xgi8e(LivingEntity e) {
      if (this.mc.player == null) {
         return 0.0F;
      } else {
         Vec3d eyes = this.mc.player.getEyePos();
         Vec3d mid = e.getBoundingBox().getCenter();
         Vec3d delta = mid.subtract(eyes);
         float needYaw = (float)Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F;
         float needPitch = (float)(-Math.toDegrees(Math.atan2(delta.y, delta.horizontalLength())));
         float dYaw = Math.abs(MathHelper.wrapDegrees(needYaw - this.mc.player.getYaw()));
         float dPitch = Math.abs(needPitch - this.mc.player.getPitch());
         return dYaw + dPitch;
      }
   }

   private int os57M(float angle) {
      if (angle > 130.0F) {
         return 0 + (int)(Math.random() * 10.0);
      } else if (angle > 70.0F) {
         return 0 + (int)(Math.random() * 10.0);
      } else {
         return angle > 30.0F ? 0 + (int)(Math.random() * 10.0) : 0 + (int)(Math.random() * 5.0);
      }
   }

   private boolean jMRkz() {
      return this.mc.player == null ? false : this.mc.options.forwardKey.isPressed();
   }

   private boolean xabwr(LivingEntity target) {
      if (this.mc.player != null && target != null) {
         Vec3d playerPos = this.mc.player.getPos();
         Vec3d targetPos = target.getPos();
         Vec3d playerVel = new Vec3d(
            this.mc.player.getX() - this.mc.player.prevX, this.mc.player.getY() - this.mc.player.prevY, this.mc.player.getZ() - this.mc.player.prevZ
         );
         Vec3d targetVel = new Vec3d(target.getX() - target.prevX, target.getY() - target.prevY, target.getZ() - target.prevZ);
         Vec3d toTarget = targetPos.subtract(playerPos).normalize();
         double playerSpeedToTarget = playerVel.dotProduct(toTarget);
         double targetSpeedToPlayer = targetVel.dotProduct(toTarget.multiply(-1.0));
         double relativeSpeed = playerSpeedToTarget + targetSpeedToPlayer;
         double distance = Math.sqrt(Math.pow(playerPos.x - targetPos.x, 2.0) + Math.pow(playerPos.z - targetPos.z, 2.0));
         return relativeSpeed > 0.05 && distance < 4.0;
      } else {
         return false;
      }
   }

   private float[] qlZri(float dist) {
      this.jRrg = this.jRrg + (0.042F + (float)(Math.random() * 0.018F));
      float scale = MathHelper.clamp(dist / 4.5F, 0.25F, 1.0F);
      float amp = 1.8F * scale;
      float n1 = (float)Math.sin(this.jRrg * 0.87) * 0.38F;
      float n2 = (float)Math.sin(this.jRrg * 1.43 + 0.75) * 0.28F;
      float n3 = (float)Math.cos(this.jRrg * 1.18 + 0.35) * 0.32F;
      float n4 = (float)Math.cos(this.jRrg * 1.76 + 1.42) * 0.23F;
      float yawNoise = (n1 + n2) * amp;
      float pitchNoise = (n3 + n4) * amp * 0.52F;
      yawNoise += ((float)Math.random() - 0.5F) * amp * 0.13F;
      pitchNoise += ((float)Math.random() - 0.5F) * amp * 0.09F;
      return new float[]{yawNoise, pitchNoise};
   }

   private float yK50jw8(float x) {
      x = MathHelper.clamp(x, 0.0F, 1.0F);
      return x * x * (3.0F - 2.0F * x);
   }

   private float vIs6J(float x) {
      x = MathHelper.clamp(x, 0.0F, 1.0F);
      return 1.0F - (1.0F - x) * (1.0F - x);
   }

   private float mord(float current, float target, float vel, float stiffness, float damping) {
      float diff = target - current;
      float acc = diff * stiffness - vel * damping;
      return vel + acc;
   }

   private float eFfie7v(float from, float to, float alpha) {
      alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
      float delta = MathHelper.wrapDegrees(to - from);
      return from + delta * alpha;
   }

    private Vec3d hOe5Gd(LivingEntity target, boolean bothGliding) {
       return target.getBoundingBox().getCenter();
    }

   private void updateSlothRotation(LivingEntity target) {
      if (this.mc.player != null && target != null) {
         boolean playerFlying = this.mc.player.isGliding();
         boolean targetFlying = target.isGliding();
         boolean bothGliding = playerFlying && targetFlying;
         if (this.tTdH9 != target) {
            this.tTdH9 = target;
            this.dYsCdha = this.mc.player.getYaw();
            this.uk0NTe = this.mc.player.getPitch();
            this.mib0f = this.dYsCdha;
            this.t0Qs = this.uk0NTe;
            this.mdOcVt = this.dYsCdha;
            this.uz2584 = this.uk0NTe;
            this.t40300 = this.vKwu1 = 0.0F;
            this.slPickAimPoint(target);
            this.l34P = 0;
            this.vrfD = 0L;
            this.pm7kP = new Vec3d(0.0, 0.7, 0.0);
            this.m9o5xiw = new Vec3d(0.0, 0.7, 0.0);
            this.s4lm9Q = 0L;
            this.tU5G = 0L;
            this.tCzzmCe = Vec3d.ZERO;
            this.eiO57 = this.vh2V = 0;
            this.jRrg = (float)(Math.random() * Math.PI * 2.0);
            float angleDiff = this.xgi8e(target);
            this.xbzv9F = this.os57M(angleDiff);
            this.fZwMqQi = System.currentTimeMillis();
            this.ow4m = false;
            this.bpy6q = 0;
            this.jv2X = 1 + (int)(Math.random() * 4.0);
            this.zNFg8 = 0;
            this.i0hP = false;
            this.ie0ap = 0.0F;
            this.op9x = this.k42Zm = 0.0F;
         }

         Vec3d eyePos = this.mc.player.getEyePos();
         Vec3d targetCenter = this.hOe5Gd(target, bothGliding);
         float distance = (float)eyePos.distanceTo(targetCenter);
         float gcd = this.zA58();
         if (!this.ow4m) {
            long elapsed = System.currentTimeMillis() - this.fZwMqQi;
            if (elapsed < this.xbzv9F) {
               float jitterY = ((float)Math.random() - 0.5F) * 0.22F;
               float jitterP = ((float)Math.random() - 0.5F) * 0.14F;
               float outY = this.mib0f + jitterY;
               float outP = MathHelper.clamp(this.t0Qs + jitterP, -89.0F, 89.0F);
               outY -= (outY - this.mib0f) % gcd;
               outP -= (outP - this.t0Qs) % gcd;
               this.mib0f = outY;
               this.t0Qs = outP;
               RotationComponent.update(new Rotation(outY, outP), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
               return;
            }

            this.ow4m = true;
         }

         float[] noise = this.qlZri(distance);
         boolean cooldownReady = Viola.getInstance().getIdealHitUtils().cooldownIsReached(false);
         if (!cooldownReady) {
            this.i0hP = false;
            this.skS8 = 0;
            this.zNFg8 = (int)(Math.random() * 4.0);
         } else if (!this.i0hP) {
            if (this.zNFg8 > 0) {
               this.zNFg8--;
            } else {
               this.i0hP = true;
            }
         } else if (++this.skS8 > 10) {
            this.i0hP = false;
            this.skS8 = 0;
         }

         this.qoHb4a4 = this.i0hP || this.zNFg8 > 0;
         long slNow = System.currentTimeMillis();
         Box slBox = target.getBoundingBox();
         double slW = slBox.maxX - slBox.minX;
         double slH = slBox.maxY - slBox.minY;
         double slD = slBox.maxZ - slBox.minZ;
         double driftSpan = 300.0 / this.id3J.getValue();
         if (slNow >= this.vrfD) {
            this.vrfD = slNow + (long)(driftSpan * 0.35 + Math.random() * driftSpan * 0.8);
            this.l34P = (int)(Math.random() * SLOTH_MULTIPOINTS.length);
            double[] p = SLOTH_MULTIPOINTS[this.l34P];
            this.m9o5xiw = new Vec3d(p[0] * slW, p[1] * slH, p[2] * slD);
         }

         this.pm7kP = this.pm7kP.lerp(this.m9o5xiw, 0.06F + (float)Math.random() * 0.04F);
         Vec3d aimPos = targetCenter.add(this.pm7kP);
         Vec3d direction = aimPos.subtract(eyePos);
         float wantYaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0);
         float wantPitch = (float)(-Math.toDegrees(Math.atan2(direction.y, direction.horizontalLength())));
         float errYaw = MathHelper.wrapDegrees(wantYaw - this.dYsCdha);
         float errPitch = wantPitch - this.uk0NTe;
         boolean movingForward = this.jMRkz();
         boolean overtaking = this.xabwr(target);
         if (!this.yFhh && slNow >= this.tZFee) {
            this.yFhh = true;
            this.tZFee = slNow + 8000L + (long)(Math.random() * 12000.0);
            this.fYHB = 2 + (int)(Math.random() * 4.0);
            this.zl5gB4 = Math.random() - 0.5;
            this.f0oomV = Math.random() - 0.5;
         }

         if (this.yFhh) {
            if (--this.fYHB <= 0) {
               this.yFhh = false;
            } else {
               this.dYsCdha = this.dYsCdha + MathHelper.clamp((float)this.zl5gB4 * 0.55F, -0.55F, 0.55F);
               this.uk0NTe = this.uk0NTe + MathHelper.clamp((float)this.f0oomV * 0.4F, -0.4F, 0.4F);
               this.uk0NTe = MathHelper.clamp(this.uk0NTe, -89.0F, 89.0F);
            }
         } else if (Math.abs(errYaw) < this.in0J5 && Math.abs(errPitch) < this.in0J5 * 0.85F) {
            if (this.j5p8Dok >= this.ilhZq8q) {
               this.j5p8Dok = 0;
               this.ilhZq8q = 2 + (int)(Math.random() * 7.0);
               this.in0J5 = 0.12F + (float)Math.random() * 0.5F;
            } else {
               this.j5p8Dok++;
            }

            this.t40300 *= 0.35F;
            this.vKwu1 *= 0.35F;
            this.op9x = this.t40300;
            this.k42Zm = this.vKwu1;
            float microY = ((float)Math.random() - 0.5F) * 0.09F;
            float microP = ((float)Math.random() - 0.5F) * 0.06F;
            if (Math.random() < 0.6F) {
               microY = 0.0F;
            }

            if (Math.random() < 0.6F) {
               microP = 0.0F;
            }

            this.dYsCdha += microY;
            this.uk0NTe += microP;
         } else {
            float mag = (float)Math.sqrt(errYaw * errYaw + errPitch * errPitch);
            float speedScale = MathHelper.clamp(0.45F + mag * 0.26F, 0.45F, 2.6F);
            if (bothGliding) {
               speedScale *= 0.72F;
            }

            if ((movingForward || overtaking) && !bothGliding) {
               speedScale *= 0.62F;
            }

            float stiffness = (0.05F + (float)Math.random() * 0.014F) * speedScale;
            float damping = 0.45F + (float)Math.random() * 0.3F;
            this.t40300 = this.mord(this.dYsCdha, this.dYsCdha + errYaw, this.t40300, stiffness, damping);
            this.vKwu1 = this.mord(this.uk0NTe, wantPitch, this.vKwu1, stiffness * 0.9F, damping);
            float maxVelYaw = Math.min(1.15F + mag * 0.22F, 2.2F) * (bothGliding ? 0.75F : 1.0F);
            float maxVelPitch = maxVelYaw * 0.82F;
            this.t40300 = MathHelper.clamp(this.t40300, -maxVelYaw, maxVelYaw);
            this.vKwu1 = MathHelper.clamp(this.vKwu1, -maxVelPitch, maxVelPitch);
            float maxAccel = 0.13F + mag * 0.02F + (float)Math.random() * 0.05F;
            float dVelYaw = this.t40300 - this.op9x;
            float dVelPitch = this.vKwu1 - this.k42Zm;
            if (Math.abs(dVelYaw) > maxAccel) {
               this.t40300 = this.op9x + Math.signum(dVelYaw) * maxAccel;
            }

            if (Math.abs(dVelPitch) > maxAccel) {
               this.vKwu1 = this.k42Zm + Math.signum(dVelPitch) * maxAccel;
            }

            this.op9x = this.t40300;
            this.k42Zm = this.vKwu1;
            this.dYsCdha = this.dYsCdha + this.t40300;
            this.uk0NTe = this.uk0NTe + this.vKwu1;
            if (Math.random() < 0.035) {
               this.dYsCdha = this.dYsCdha + (float)(Math.signum(this.t40300) * (0.06 + Math.random() * 0.2));
            }

            this.uk0NTe = MathHelper.clamp(this.uk0NTe, -89.0F, 89.0F);
         }

         if (this.eiO57 > 0) {
            this.vh2V++;
            int dipTicks = 5 + (int)(Math.random() * 4.0);
            int returnTicks = 10 + (int)(Math.random() * 8.0);
            float dipAmount = -1.2F - (float)(Math.random() * 1.6F);
            if (this.eiO57 == 1) {
               float tDip = MathHelper.clamp((float)this.vh2V / dipTicks, 0.0F, 1.0F);
               this.ie0ap = dipAmount * this.vIs6J(tDip);
               if (this.vh2V >= dipTicks) {
                  this.eiO57 = 2;
                  this.vh2V = 0;
               }
            } else {
               float tRet = MathHelper.clamp((float)this.vh2V / returnTicks, 0.0F, 1.0F);
               this.ie0ap = dipAmount * (1.0F - this.yK50jw8(tRet));
               if (this.vh2V >= returnTicks) {
                  this.eiO57 = 0;
                  this.vh2V = 0;
                  this.ie0ap = 0.0F;
               }
            }
         } else {
            this.ie0ap = 0.0F;
         }

         float smoothFactor = bothGliding ? 0.35F : 0.8F;
         this.mdOcVt = this.eFfie7v(this.mdOcVt, this.dYsCdha, smoothFactor);
         this.uz2584 = this.eFfie7v(this.uz2584, this.uk0NTe, smoothFactor * 0.95F);
         float yNoise = this.qoHb4a4 ? noise[0] * 0.25F : noise[0];
         float pNoise = this.qoHb4a4 ? noise[1] * 0.25F : noise[1];
         float recoilP = this.qoHb4a4 ? 0.0F : this.ie0ap;
         float outY = this.mdOcVt + yNoise;
         float outP = this.uz2584 + pNoise + recoilP;
         outP = MathHelper.clamp(outP, -89.0F, 89.0F);
         outY -= (outY - this.mib0f) % gcd;
         outP -= (outP - this.t0Qs) % gcd;
         this.mib0f = outY;
         this.t0Qs = outP;
         RotationComponent.update(new Rotation(outY, outP), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
      }
   }

   private void updateLonyJirRotation(LivingEntity target) {
      double time = System.nanoTime() * 1.0E-9;
      Rotation angle = new Rotation(RotationUtil.calculate(target.getBoundingBox().getCenter().add(0.0, (float)Math.abs(Math.sin(time * 19.0)) / 2.0F, 0.0)));

      if (!RaytraceUtil.rayTrace(this.mc.player.getRotationVector(), 999.0, target.getBoundingBox().expand(-0.2F))) {
         this.speedAcceleration = this.speedAcceleration + (float)Math.abs(Math.sin(time * 19.0)) / 666.0F;
      } else if (this.speedAcceleration >= 0.02F) {
         this.speedAcceleration -= 0.02F;
      }

      float deltaYaw = MathHelper.wrapDegrees(angle.getYaw() - this.lastYaw);
      float deltaPitch = angle.getPitch() - this.lastPitch;
      float smooth = Math.min(Math.max(this.speedAcceleration, 0.0F), 0.2F);
      float newYaw = this.lastYaw + deltaYaw * smooth;
      float newPitch = this.lastPitch + deltaPitch * (smooth / 3.0F);
      newYaw -= (newYaw - this.lastYaw) % GCDFixer.getGCDValue();
      newPitch -= (newPitch - this.lastPitch) % GCDFixer.getGCDValue();
      Rotation smoothRot = new Rotation(newYaw, newPitch);
      float deltaYaw2 = MathHelper.wrapDegrees(this.mc.gameRenderer.getCamera().getYaw() - this.lastYaw);
      float deltaPitch2 = this.mc.gameRenderer.getCamera().getPitch() - this.lastPitch;
      if (this.mc.options.getPerspective() == Perspective.THIRD_PERSON_FRONT) {
         deltaYaw2 = MathHelper.wrapDegrees(this.mc.gameRenderer.getCamera().getYaw() - 180.0F - this.lastYaw);
         deltaPitch2 = -this.mc.gameRenderer.getCamera().getPitch() - this.lastPitch;
      }

      if (this.mc.player.isGliding() && target.isGliding()) {
         RotationComponent.update(smoothRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
      }

      this.lastYaw = smoothRot.getYaw();
      this.lastPitch = smoothRot.getPitch();
   }

   private void updateFuntimeRotation(LivingEntity target) {
      if (target != null && this.mc.player != null) {
         if (this.mc.player.isBlocking()) {
            this.lastYaw = this.mc.player.getYaw();
            this.lastPitch = this.mc.player.getPitch();
            RotationComponent.update(new Rotation(this.lastYaw, this.lastPitch), 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
         } else {
            if (!this.y7pQz) {
               this.lastYaw = this.mc.player.getYaw();
               this.lastPitch = this.mc.player.getPitch();
               this.y7pQz = true;
            }

            if (this.cyX6k48 != target) {
               this.cyX6k48 = target;
               this.xdBSn = 0.0F;
               this.enJim99 = false;
               this.k48Zk = 0;
               this.d84Y = 1.0F;
            }

            this.k48Zk++;
            float wave1 = (float)Math.sin(this.k48Zk * 0.13F) * 0.08F;
            float wave2 = (float)Math.cos(this.k48Zk * 0.051F) * 0.047F;
            float random = (ThreadLocalRandom.current().nextFloat() - 0.51F) * 0.02F;
            this.d84Y = Math.min(1.0F, this.d84Y + 0.1F);
            this.hjgjei = (wave1 + wave2 + random) * this.d84Y;
            Vec3d point = this.eDxXn(target, BestPoint.getNearestPoint(target), this.distance.getValue());
            if (point == null) {
               point = target.getEyePos();
            }

            Rotation angle = new Rotation(RotationUtil.calculate(point));
            float targetYaw = angle.getYaw();
            float targetPitch = angle.getPitch();
            this.xfteLF = targetYaw;
            this.t1AB = targetPitch;
            float yawDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - this.lastYaw));
            boolean readyToAttack = this.mc.player.getAttackCooldownProgress(1.0F) > 0.9F && this.ticksToAttack <= 1;
            if (!this.enJim99) {
               float gain = 0.0032F;
               if (yawDiff > 60.0F) {
                  gain += 0.015F;
               } else if (yawDiff > 30.0F) {
                  gain += 0.007F;
               } else {
                  gain += 0.0028F;
               }

               if (readyToAttack) {
                  gain += 0.006F;
               }

               this.xdBSn = this.xdBSn + gain * (1.4F + this.hjgjei);
               if (this.xdBSn >= 0.18F) {
                  this.enJim99 = true;
               }
            } else {
               float loss = readyToAttack ? 0.024F : 0.005F;
               this.xdBSn = this.xdBSn - loss * (1.8F + this.hjgjei);
               if (this.xdBSn <= -0.04F) {
                  this.enJim99 = false;
               }
            }

            float smooth = MathHelper.clamp(this.xdBSn, 0.0F, this.mc.player.isGliding() ? 0.3F : 0.18F);
            if (readyToAttack) {
               smooth = Math.min(smooth + 0.04F, this.mc.player.isGliding() ? 0.35F : 0.23F);
            }

            smooth += this.hjgjei * 0.4F;
            if (this.k48Zk % 7 == 0) {
               smooth += 0.02F;
            }

            float yawLimit = this.mc.player.isGliding() ? 38.0F : (readyToAttack ? 24.0F : 17.0F);
            float pitchLimit = this.mc.player.isGliding() ? 10.0F : (readyToAttack ? 3.8F : 2.3F);
            float deltaYaw = MathHelper.clamp(MathHelper.wrapDegrees(targetYaw - this.lastYaw), -yawLimit, yawLimit);
            float deltaPitch = MathHelper.clamp(targetPitch - this.lastPitch, -pitchLimit, pitchLimit);
            float newYaw = this.lastYaw + deltaYaw * smooth * (0.83F + this.hjgjei * 0.22F);
            float newPitch = this.lastPitch + deltaPitch * smooth * 0.69F;
            float gcd = GCDFixer.getGCDValue();
            if (gcd > 0.0F) {
               newYaw = this.lastYaw + Math.round((newYaw - this.lastYaw) / gcd) * gcd;
               newPitch = this.lastPitch + Math.round((newPitch - this.lastPitch) / gcd) * gcd;
            }

            newPitch = MathHelper.clamp(newPitch, -89.0F, 89.0F);
            if (this.nxVeb > 0) {
               int total = 22;
               float progress = (float)(total - this.nxVeb) / total;
               float prevProgress = (float)(total - this.nxVeb - 1) / total;
               float currPos = (float)Math.sin(Math.PI * progress);
               float prevPos = (float)Math.sin(Math.PI * Math.max(0.0F, prevProgress));
               float delta = currPos - prevPos;
               newYaw = MathHelper.wrapDegrees(newYaw + this.xhlsar * delta);
               newPitch = MathHelper.clamp(newPitch + this.s2vNH * delta, -89.0F, 89.0F);
               this.nxVeb--;
            }

            this.lastYaw = newYaw;
            this.lastPitch = newPitch;
            Rotation rot = new Rotation(newYaw, newPitch);
            float rotSpeed = this.mc.player.isGliding() && target.isGliding() ? 360.0F : 45.0F;
            RotationComponent.update(rot, rotSpeed, rotSpeed, rotSpeed, rotSpeed, 0, 1, false);
         }
      }
   }

   private void utSreJ7() {
      this.y3B60aO = Math.min(this.y3B60aO + 1, 30);
      this.nxVeb = 22;
      float growth = 1.0F + Math.min(0.05F * this.y3B60aO, 0.5F);
      float randomness = 1.0F + this.y3B60aO / 30.0F * 0.6F;
      float totalYaw = (6.0F + ThreadLocalRandom.current().nextFloat() * 3.0F * randomness) * growth;
      float totalPitch = (2.2F + ThreadLocalRandom.current().nextFloat() * 1.6F * randomness) * growth;
      float yawDir = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
      float pitchDir = ThreadLocalRandom.current().nextInt(100) == 0 ? -1.0F : (ThreadLocalRandom.current().nextFloat() < 0.8F ? 1.0F : -1.0F);
      this.xhlsar = yawDir * totalYaw;
      this.s2vNH = pitchDir * totalPitch;
   }

   private void b6qI() {
      this.cyX6k48 = null;
      this.xdBSn = 0.0F;
      this.enJim99 = false;
      this.d84Y = Math.max(0.0F, this.d84Y - 0.08F);
      this.k48Zk = 0;
      this.y7pQz = this.mc.player != null;
      if (this.mc.player != null) {
         this.lastYaw = this.mc.player.getYaw();
         this.lastPitch = this.mc.player.getPitch();
      } else {
         this.lastYaw = 0.0F;
         this.lastPitch = 0.0F;
      }
   }

   private void updateSpookyTimeRotation(LivingEntity target) {
      if (target != null) {
          Vec3d targetPoint = target.getEyePos();

         Rotation rotation = new Rotation(RotationUtil.calculate(targetPoint));
         float targetYaw = rotation.getYaw();
         float targetPitch = MathHelper.clamp(rotation.getPitch(), -89.0F, 89.0F);
         float deltaYaw = MathHelper.wrapDegrees(targetYaw - this.lastYaw);
         float deltaPitch = MathHelper.clamp(targetPitch - this.lastPitch, -90.0F, 90.0F);
         float absDelta = (float)Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
         if (absDelta < 0.4F) {
            Rotation finalRot = new Rotation(targetYaw, targetPitch);
            RotationComponent.update(finalRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
            this.lastYaw = targetYaw;
            this.lastPitch = targetPitch;
         } else {
            float softness = MathHelper.clamp(absDelta / 30.0F, 0.18F, 1.0F);
            float humanFactor = 0.9F + (float)Math.random() * 0.2F;
            float maxYawStep = MathHelper.clamp(absDelta * softness, 1.0F, 8.0F) * humanFactor;
            float maxPitchStep = MathHelper.clamp(absDelta * softness * 0.7F, 0.8F, 5.5F) * humanFactor;
            float yawStep = MathHelper.clamp(deltaYaw * softness * 0.55F, -maxYawStep, maxYawStep);
            float pitchStep = MathHelper.clamp(deltaPitch * softness * 0.5F, -maxPitchStep, maxPitchStep);
            float newYaw = this.lastYaw + yawStep;
            float newPitch = this.lastPitch + pitchStep;
            float gcd = GCDFixer.getGCDValue();
            float qYaw = Math.round((newYaw - this.lastYaw) / gcd) * gcd;
            if (qYaw == 0.0F && deltaYaw != 0.0F) {
               qYaw = Math.signum(deltaYaw) * gcd;
            }

            float qPitch = Math.round((newPitch - this.lastPitch) / gcd) * gcd;
            if (qPitch == 0.0F && deltaPitch != 0.0F) {
               qPitch = Math.signum(deltaPitch) * gcd;
            }

            newYaw = this.lastYaw + qYaw;
            newPitch = MathHelper.clamp(this.lastPitch + qPitch, -90.0F, 90.0F);
            Rotation smoothRot = new Rotation(newYaw, newPitch);
            RotationComponent.update(smoothRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
            this.lastYaw = smoothRot.getYaw();
            this.lastPitch = smoothRot.getPitch();
         }
      }
   }

   private void updateAresMineRotation(LivingEntity target) {
      if (target != null) {
         long now = System.currentTimeMillis();
         if (target != this.e0se) {
            this.e0se = target;
            this.pqeQ = 0.0F;
            this.plO390H = 0.0F;
            this.xiV6z6 = 0.0F;
            this.x5uo6 = 0.0F;
            this.yolsrD = 0.0F;
            this.qtl1 = 0;
            this.bLzhfd = 0L;
            this.oFBUk = null;
            this.uh9Fr8 = now + 1500L + this.k5gTXE1.nextInt(2000);
            this.wYvqh = 0L;
         }

         if (now >= this.uh9Fr8 && this.wYvqh == 0L) {
            this.wYvqh = now + 45L + this.k5gTXE1.nextInt(50);
            this.dmO7ts = (this.k5gTXE1.nextFloat() - 0.5F) * 2.2F;
            this.ool07 = (this.k5gTXE1.nextFloat() - 0.5F) * 1.2F;
         }

         boolean disrupting = now < this.wYvqh;
         if (this.wYvqh != 0L && now >= this.wYvqh) {
            this.wYvqh = 0L;
            this.dmO7ts = 0.0F;
            this.ool07 = 0.0F;
            this.uh9Fr8 = now + 1800L + this.k5gTXE1.nextInt(2200);
         }

         Vec3d point = this.zeALXw(target, now);
         Rotation angle = new Rotation(RotationUtil.calculate(point));
         float rawYawDelta = MathHelper.wrapDegrees(angle.getYaw() - this.lastYaw);
         float rawPitchDelta = angle.getPitch() - this.lastPitch;
         float totalDelta = (float)Math.hypot(rawYawDelta, rawPitchDelta);
         this.x5uo6 = this.x5uo6 + ((float)(this.k5gTXE1.nextGaussian() * 0.08) - 0.25F * this.x5uo6);
         this.yolsrD = this.yolsrD + ((float)(this.k5gTXE1.nextGaussian() * 0.06) - 0.22F * this.yolsrD);
         this.xiV6z6 += 0.22F;
         float envelope = Math.min(totalDelta * 0.035F, 1.2F);
         float spiralYaw = (float)(Math.sin(this.xiV6z6) * envelope);
         float spiralPitch = (float)(Math.cos(this.xiV6z6 * 1.15F) * (envelope * 0.35F));
         float aimYaw = angle.getYaw() + spiralYaw + this.yolsrD + (disrupting ? this.dmO7ts : 0.0F);
         float aimPitch = MathHelper.clamp(angle.getPitch() + spiralPitch + this.x5uo6 + (disrupting ? this.ool07 : 0.0F), -89.0F, 90.0F);
         float deltaYaw = MathHelper.wrapDegrees(aimYaw - this.lastYaw);
         float deltaPitch = aimPitch - this.lastPitch;
         float targetYawStep = this.l5LQh4y(deltaYaw, totalDelta, true);
         float targetPitchStep = this.l5LQh4y(deltaPitch, totalDelta, false);
         float smoothFactor = totalDelta > 25.0F ? 0.38F : (totalDelta > 8.0F ? 0.3F : 0.24F);
         if (disrupting) {
            smoothFactor = 0.45F;
         }

         this.pqeQ = MathHelper.lerp(smoothFactor, this.pqeQ, targetYawStep);
         this.plO390H = MathHelper.lerp(smoothFactor, this.plO390H, targetPitchStep);
         float nextYaw = this.lastYaw + this.pqeQ;
         float nextPitch = this.lastPitch + this.plO390H;
         float gcd = GCDFixer.getGCDValue();
         float qYaw = Math.round(MathHelper.wrapDegrees(nextYaw - this.lastYaw) / gcd) * gcd;
         if (qYaw == 0.0F && deltaYaw != 0.0F) {
            qYaw = Math.signum(deltaYaw) * gcd;
         }

         float qPitch = Math.round((nextPitch - this.lastPitch) / gcd) * gcd;
         if (qPitch == 0.0F && deltaPitch != 0.0F) {
            qPitch = Math.signum(deltaPitch) * gcd;
         }

         Rotation rot = new Rotation(this.lastYaw + qYaw, MathHelper.clamp(this.lastPitch + qPitch, -90.0F, 90.0F));
         RotationComponent.update(rot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
         this.lastYaw = rot.getYaw();
         this.lastPitch = rot.getPitch();
      }
   }

   private float l5LQh4y(float delta, float totalDelta, boolean yaw) {
      float absDelta = Math.abs(delta);
      if (absDelta < 0.02F) {
         return delta;
      } else {
         float variance = 0.88F + this.k5gTXE1.nextFloat() * 0.28F;
         float force;
         if (totalDelta > 30.0F) {
            force = 19.0F + (float)Math.pow(absDelta, 0.72) * (yaw ? 0.48F : 0.36F) * variance;
            force = Math.min(force, yaw ? 34.0F : 24.0F);
         } else if (totalDelta > 8.0F) {
            force = (float)Math.pow(absDelta, 0.8) * (yaw ? 0.5F : 0.38F) * variance;
            force = Math.min(force, yaw ? 22.0F : 15.0F);
         } else {
            force = (float)Math.pow(absDelta, 0.84) * (yaw ? 0.42F : 0.32F) * variance;
            force = Math.min(force, 11.0F);
         }

         if (!yaw) {
            force *= delta > 0.0F ? 0.88F + this.k5gTXE1.nextFloat() * 0.14F : 1.06F + this.k5gTXE1.nextFloat() * 0.14F;
         }

         force = Math.max(force + (this.k5gTXE1.nextFloat() - 0.5F) * (yaw ? 0.7F : 0.4F), 0.03F);
         return Math.signum(delta) * force;
      }
   }

   private Vec3d zeALXw(LivingEntity target, long now) {
      Box box = target.getBoundingBox();
      double midX = (box.minX + box.maxX) * 0.5;
      double midZ = (box.minZ + box.maxZ) * 0.5;
      double height = box.maxY - box.minY;
      Vec3d vel = target.getVelocity();
      Vec3d[] points = new Vec3d[]{
         new Vec3d(midX, box.minY + height * 0.85, midZ),
         new Vec3d(midX, target.getY(), midZ),
         new Vec3d(midX, box.minY + height * 0.62, midZ),
         new Vec3d(midX, box.minY + height * 0.4, midZ),
         new Vec3d(midX + vel.x * 0.22, box.minY + height * 0.6 + vel.y * 0.14, midZ + vel.z * 0.22),
         BestPoint.getNearestPoint(target)
      };
      if (now >= this.bLzhfd) {
         this.qtl1 = this.k5gTXE1.nextInt(points.length);
         this.bLzhfd = now + 110L + this.k5gTXE1.nextInt(180);
      }

      Vec3d chosen = points[this.qtl1 % points.length];
      if (this.oFBUk == null) {
         this.oFBUk = chosen;
      } else {
         double blend = 0.32 + this.k5gTXE1.nextDouble() * 0.25;
         this.oFBUk = new Vec3d(
            this.oFBUk.x + (chosen.x - this.oFBUk.x) * blend,
            this.oFBUk.y + (chosen.y - this.oFBUk.y) * blend,
            this.oFBUk.z + (chosen.z - this.oFBUk.z) * blend
         );
      }

      return this.oFBUk;
   }

   private void updateSmoothRotation(LivingEntity target) {
      if (target != null) {
          Vec3d targetPoint = target.getEyePos();

         Rotation angle = new Rotation(RotationUtil.calculate(targetPoint));
         float targetYaw = angle.getYaw();
         float targetPitch = angle.getPitch();
         float deltaYaw = MathHelper.wrapDegrees(targetYaw - this.lastYaw);
         float deltaPitch = targetPitch - this.lastPitch;
         float speed = 1.0F;
         float newYaw = this.lastYaw + deltaYaw * speed;
         float newPitch = this.lastPitch + deltaPitch * speed;
         float gcd = GCDFixer.getGCDValue();
         newYaw -= (newYaw - this.lastYaw) % gcd;
         newPitch -= (newPitch - this.lastPitch) % gcd;
         newPitch = MathHelper.clamp(newPitch, -90.0F, 90.0F);
         Rotation smoothRot = new Rotation(newYaw, newPitch);
         RotationComponent.update(smoothRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
         this.lastYaw = smoothRot.getYaw();
         this.lastPitch = smoothRot.getPitch();
      }
   }

   private void updateLegitRotation(LivingEntity target) {
      if (this.mc.player != null && target != null) {
         float currentYaw = this.mc.player.getYaw();
         boolean isOnTarget = this.legitIsYawOnTarget(target, currentYaw);
         Vec3d targetPoint = this.eDxXn(target, BestPoint.getNearestPoint(target), this.distance.getValue());
 

         Vec2f targetRot = RotationUtil.calculate(targetPoint);
         float yawDelta = MathHelper.wrapDegrees(targetRot.x - currentYaw);
         float absDelta = Math.abs(yawDelta);
         float targetSpeed = 0.0F;
         if (!isOnTarget) {
            float min = this.e1a6.getFloatValue();
            float max = this.i658.getFloatValue();
            float factor = ThreadLocalRandom.current().nextFloat(min, Math.min(min * 1.6F + 0.001F, max));
            float randomMaxSpeed = ThreadLocalRandom.current().nextFloat(max * 0.25F, max * 0.5F);
            targetSpeed = MathHelper.clamp(absDelta * factor, 0.01F, Math.max(randomMaxSpeed, 0.02F));
         }

         this.z347tK = MathHelper.lerp(this.n3UL10.getFloatValue(), this.z347tK, targetSpeed);
         if (this.z347tK < 5.0E-4F) {
            this.z347tK = 0.0F;
         }

         float clampedDelta = MathHelper.clamp(yawDelta, -this.z347tK, this.z347tK);
         float targetPitch = this.mc.player.getPitch();
         RotationComponent.update(new Rotation(targetRot.x, targetPitch), this.z347tK, 0.0F, this.z347tK, 0.0F, 1, 1, true);
         this.lastYaw = currentYaw + clampedDelta;
         this.lastPitch = targetPitch;
      }
   }

    private final ApexRotation apexAim = new ApexRotation();

    private void updateApexTimeRotation(LivingEntity target) {
       if (target != null && this.mc.player != null) {
          float tickDelta = this.mc.getRenderTickCounter().getTickDelta(true);
          float chase = (float)this.wv8f.getValue();
          ApexRotation.Result r = this.apexAim.apexTick(this.mc.player, target, (float)this.distance.getValue(), chase);
          if (r == null) {
             return;
          }
          float yawDiff = MathHelper.wrapDegrees(r.targetYaw - this.lastYaw);
          float pitchDiff = r.targetPitch - this.lastPitch;
          float newYaw = this.lastYaw + MathHelper.clamp(yawDiff, -r.yawSpeed, r.yawSpeed);
          float newPitch = this.lastPitch + MathHelper.clamp(pitchDiff, -r.pitchSpeed, r.pitchSpeed);
          newPitch = MathHelper.clamp(newPitch, -89.0F, 89.0F);
          newYaw = this.lastYaw + this.b0la8(newYaw - this.lastYaw);
          newPitch = this.lastPitch + this.b0la8(newPitch - this.lastPitch);
          Rotation apex = new Rotation(newYaw, newPitch);
          RotationComponent.update(apex, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
          this.lastYaw = apex.getYaw();
          this.lastPitch = apex.getPitch();
       }
    }

    private boolean legitIsYawOnTarget(LivingEntity target, float currentYaw) {
      Vec3d eyePos = this.mc.player.getEyePos();
      Box box = target.getBoundingBox();
      double minDiff = Double.MAX_VALUE;
      double maxDiff = -Double.MAX_VALUE;

      for (double x : new double[]{box.minX, box.maxX}) {
         for (double z : new double[]{box.minZ, box.maxZ}) {
            float cornerYaw = (float)(Math.atan2(z - eyePos.z, x - eyePos.x) * (180.0 / Math.PI) - 90.0);
            float diff = MathHelper.wrapDegrees(cornerYaw - currentYaw);
            if (diff < minDiff) {
               minDiff = diff;
            }

            if (diff > maxDiff) {
               maxDiff = diff;
            }
         }
      }

      return minDiff <= 0.0 && maxDiff >= 0.0;
   }

   private void i30S(LivingEntity target) {
      if (target != null) {
          Vec3d point = this.eDxXn(target, BestPoint.getPoint2(target), 6.0);

         boolean isLooking = RaytraceUtil.rayTrace(this.mc.player.getRotationVector(), 6.0, target.getBoundingBox().expand(0.0, -1.0, 0.0));
         Rotation idealRotation = new Rotation(RotationUtil.calculate(point));
         float targetYaw = idealRotation.getYaw();
         float targetPitch = idealRotation.getPitch();
         float randomFactor = (float)Math.random();
         float deltaYaw = MathHelper.wrapDegrees(targetYaw - this.lastYaw);
         float deltaPitch = targetPitch - this.lastPitch;
         float distance = this.mc.player.distanceTo(target) / 30.0F;
         if (!isLooking && this.mc.player.getAttackCooldownProgress(1.0F) >= 0.7F) {
            distance += 0.02F;
            stopWatch.reset();
         }

         if (!isLooking) {
            distance += 0.005F;
            stopWatch.reset();
         } else {
            distance *= 0.15F + randomFactor * 0.2F;
         }

         float smooth = Math.min(Math.max(distance, 0.0F), 0.12F);
         float newYaw = this.lastYaw + deltaYaw * smooth;
         float newPitch = this.lastPitch + deltaPitch * 0.5F * smooth;
         float gcd = GCDFixer.getGCDValue();
         newYaw -= (newYaw - this.lastYaw) % gcd;
         newPitch -= (newPitch - this.lastPitch) % gcd;
         newPitch = MathHelper.clamp(newPitch, -90.0F, 90.0F);
         Rotation legitRot = new Rotation(newYaw, newPitch);
         RotationComponent.update(legitRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
         this.lastYaw = legitRot.getYaw();
         this.lastPitch = legitRot.getPitch();
      }
   }

   private void updateLonyGriefRotation(LivingEntity target) {
          Vec3d point = this.eDxXn(target, BestPoint.getPoint(target), 6.0);
      Rotation angle = new Rotation(RotationUtil.calculate(point));
      float targetYaw = angle.getYaw();
      float targetPitch = angle.getPitch();
      if (!this.b72qK3r) {
         float pon = this.mc.player.isGliding() ? 1.35F : 1.0F;
         this.speedAcceleration = this.speedAcceleration + (Math.abs(MathHelper.wrapDegrees(targetYaw - this.lastYaw)) > 40.0F ? 0.005F / pon : 0.0038F / pon);
         boolean isLooking = RaytraceUtil.rayTrace(this.mc.player.getRotationVector(), 6.0, target.getBoundingBox().expand(-0.2, -0.3, -0.2));
         if (this.speedAcceleration >= 0.16F / pon || isLooking) {
            this.b72qK3r = true;
         }
      } else {
         if (this.speedAcceleration >= -0.01F) {
            this.speedAcceleration = this.speedAcceleration - (Math.abs(MathHelper.wrapDegrees(targetYaw - this.lastYaw)) > 60.0F ? 0.06F : 0.01F);
         }

         if (this.speedAcceleration <= -0.01F) {
            this.b72qK3r = false;
         }
      }

      float deltaYaw = MathHelper.wrapDegrees(targetYaw - this.lastYaw);
      float deltaPitch = targetPitch - this.lastPitch;
      float smooth = Math.max(this.speedAcceleration, 0.0F);
      float newYaw = this.lastYaw + deltaYaw * Math.min(Math.max(smooth, 0.0F), 1.0F);
      float newPitch = this.lastPitch + deltaPitch * Math.min(Math.max(smooth / 2.0F, 0.0F), 1.0F);
      float gcdValue = GCDFixer.getGCDValue();
      newYaw -= (newYaw - this.lastYaw) % gcdValue;
      newPitch -= (newPitch - this.lastPitch) % gcdValue;
      Rotation smoothRot = new Rotation(newYaw, newPitch);
      RotationComponent.update(smoothRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
      this.lastYaw = smoothRot.getYaw();
      this.lastPitch = smoothRot.getPitch();
   }

   private void updateWellmineRotation(LivingEntity target) {
      Box box = target.getBoundingBox();
       Vec3d vector = this.eDxXn(target, BestPoint.getMultipoint(target, 6.0), 6.0);

      Vec2f angle = RotationUtil.calculate(vector);
      float targetYaw = angle.x;
      float targetPitch = angle.y;
      if (!this.b72qK3r) {
         if (this.speedAcceleration >= 1.0F) {
            this.speedAcceleration = 0.0F;
         } else if (this.mc.player.isGliding()) {
            float diff = Math.abs(MathHelper.wrapDegrees(angle.x - this.mc.player.getYaw()));
            this.speedAcceleration += diff > 40.0F ? 0.0025F : 0.005F;
         } else {
            this.speedAcceleration += 0.005F;
         }

          Vec3d offset = Vec3d.ZERO;

         if (this.speedAcceleration >= 0.18 || RaytraceUtil.rayTrace(this.mc.player.getRotationVector(), 6.0, box.offset(offset).expand(-0.5, -1.0, -0.5))) {
            this.b72qK3r = true;
         }
      } else {
         if (this.speedAcceleration >= -0.01F) {
            float diff = Math.abs(MathHelper.wrapDegrees(targetYaw - this.mc.player.getYaw()));
            this.speedAcceleration -= diff > 40.0F ? 0.04F : 0.01F;
         }

         if (this.speedAcceleration <= -0.01F) {
            this.b72qK3r = false;
         }
      }

      float randomYaw = (float)ThreadLocalRandom.current().nextDouble(-0.75, 0.75);
      float randomPitch = (float)ThreadLocalRandom.current().nextDouble(-0.75, 0.75);
      targetYaw += randomYaw;
      targetPitch += randomPitch;
      float smoothVal = Math.min(Math.max(this.speedAcceleration, -1.0F), 1.0F);
      float changeYaw = MathHelper.wrapDegrees(targetYaw - this.mc.player.getYaw()) * smoothVal;
      float changePitch = (targetPitch - this.mc.player.getPitch()) * (smoothVal / 2.0F);
      Rotation smoothRot = new Rotation(this.mc.player.getYaw() + changeYaw, MathHelper.clamp(this.mc.player.getPitch() + changePitch, -90.0F, 90.0F));
      RotationComponent.update(smoothRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
      this.lastYaw = smoothRot.getYaw();
      this.lastPitch = smoothRot.getPitch();
   }

   private Vec3d eDxXn(LivingEntity target, Vec3d point, double range) {
      return this.smartAim.getValue() && target != null ? BestPoint.getNearestVisiblePoint(target, point, range) : point;
   }

   private float b0la8(float deltaRotation) {
      float sensitivity = (float)(this.mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F);
      float multiplier = sensitivity * sensitivity * sensitivity * 8.0F * 0.15F;
      return Math.round(deltaRotation / multiplier) * multiplier;
   }

   private void updateAssistRotation(LivingEntity target) {
      if (target != null) {
         boolean elytraDuel = this.mc.player.isGliding();
         if (elytraDuel || System.currentTimeMillis() - lastPhysicalMoveTime <= 100L) {
            if (System.currentTimeMillis() - lastPhysicalMoveTime < 100L) {
               this.lastYaw = this.mc.player.getYaw();
               this.lastPitch = this.mc.player.getPitch();
            }

             Vec3d point = this.eDxXn(target, BestPoint.getPoint(target), 6.0);

            Vec3d eyePos = this.mc.player.getEyePos();
            double deltaX = point.x - eyePos.x;
            double deltaY = point.y - eyePos.y;
            double deltaZ = point.z - eyePos.z;
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float targetYaw = (float)(Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0);
            float targetPitch = (float)(-Math.toDegrees(Math.atan2(deltaY, distance)));
            float radius = 0.0F;
            Box box = target.getBoundingBox();
            boolean isAimed = RaytraceUtil.rayTrace(this.mc.player.getRotationVector(), 6.0, box.expand(radius, radius, radius));
            float speed;
            if (isAimed) {
               speed = 0.0625F;
            } else if (this.mc.player.isGliding()) {
               speed = 8.0F;
            } else if (this.mc.player.isOnGround()) {
               speed = 0.5F;
            } else {
               speed = 0.5F;
            }

            float cooldownMultiplier = this.mc.player.getAttackCooldownProgress(0.5F) > 0.85F ? 1.2F : 0.4F;
            speed *= cooldownMultiplier;
            if (!RaytraceUtil.rayTrace(this.mc.player.getRotationVector(), 6.0, box.expand(radius + 0.1F, radius + 0.1F, radius + 0.1F))) {
               this.speedAcceleration += 5.0E-4F * cooldownMultiplier;
            } else if (this.speedAcceleration >= -0.01F) {
               this.speedAcceleration -= 4.0E-4F;
            }

            float smooth = Math.max(this.speedAcceleration, 0.0F);
            speed += smooth + (float)((Math.random() - 0.5) * 0.02);
            float yawDelta = MathHelper.wrapDegrees(targetYaw - this.lastYaw);
            float pitchDelta = targetPitch - this.lastPitch;
            float clampedYawDelta = MathHelper.clamp(yawDelta, -speed, speed);
            float clampedPitchDelta = MathHelper.clamp(pitchDelta, -speed, speed);
            float newYaw = this.lastYaw + clampedYawDelta;
            float newPitch = MathHelper.clamp(this.lastPitch + clampedPitchDelta, -89.9F, 89.9F);
            float gcd = GCDFixer.getGCDValue();
            if (gcd > 0.0F) {
               newYaw = this.lastYaw + Math.round((newYaw - this.lastYaw) / gcd) * gcd;
               newPitch = this.lastPitch + Math.round((newPitch - this.lastPitch) / gcd) * gcd;
            }

            Rotation smoothRot = new Rotation(newYaw, newPitch);
            RotationComponent.update(smoothRot, 360.0F, 360.0F, 360.0F, 360.0F, 0, 1, false);
            this.lastYaw = smoothRot.getYaw();
            this.lastPitch = smoothRot.getPitch();
         }
      }
   }

    @Override
    public void onEnable() {
      this.tii1 = null;
      this.zqtFQ0 = 0;
      this.nVt2ixx = 0;
      this.gAk1m = false;
      this.lastYaw = this.mc.player != null ? this.mc.player.getYaw() : 0.0F;
      this.lastPitch = this.mc.player != null ? this.mc.player.getPitch() : 0.0F;
      Viola.getInstance().getModuleStorage().setSpeedAcceleration(0.0F);
      this.sKvK6();
      this.b6qI();
      this.z347tK = 0.0F;
      this.qHm7QCn.reset();
      if (this.eT4bg == null) {
         this.eT4bg = Viola.getInstance().getModuleStorage().get(ElytraTarget.class);
      }

       super.onEnable();
    }

   @Override
   public void onDisable() {
      this.tii1 = null;
      this.ticksToAttack = 0;
      this.nVt2ixx = 0;
      this.gAk1m = false;
      this.speedAcceleration = 0.0F;
       this.zqtFQ0 = 0;
       Viola.getInstance().getModuleStorage().setSpeedAcceleration(0.0F);
      Viola.getInstance().getModuleStorage().setRandomness(1.0F);
      this.sKvK6();
      this.b6qI();
      this.z347tK = 0.0F;
      super.onDisable();
   }

   @Generated
   public LivingEntity getTarget() {
      return this.tii1;
   }
}
