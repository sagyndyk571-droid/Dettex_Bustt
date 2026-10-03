package zov.viola.module.list.render.hud;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import zov.viola.Viola;
import zov.viola.event.list.EventHUD;
import zov.viola.event.list.EventPacket;
import zov.viola.event.list.EventPopTotem;
import zov.viola.event.list.EventTick;
import zov.viola.mixin.BossBarHudAccessor;
import zov.viola.mixin.InGameHudAccessor;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.combat.KillAura;
import zov.viola.module.list.misc.NameProtect;
import zov.viola.module.list.player.ServerHelper;
import zov.viola.module.list.render.NameTags;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.Setting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.module.settings.impl.ThemeManager;
import zov.viola.obf.D;
import zov.viola.ui.ClickGuiBackgroundRenderer;
import zov.viola.util.base.Instance;
import zov.viola.util.draggable.DragManager;
import zov.viola.util.draggable.Draggable;
import zov.viola.util.keyboard.KeyStorage;
import zov.viola.util.math.Counter;
import zov.viola.util.render.builders.Builder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.helper.HoverUtil;
import zov.viola.util.render.math.Animation;
import zov.viola.util.render.math.Easing;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.providers.ClientPalette;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;
import zov.viola.util.render.renderers.IRenderer;
import zov.viola.util.render.renderers.impl.BuiltTexture;
import zov.viola.util.render.timer.TimerTextAnimator;
import zov.viola.util.replace.ReplaceUtil;
import zov.viola.util.server.Server;
import zov.viola.util.staff.StaffManager;

@ModuleInformation(
   moduleName = "Interface",
   moduleDesc = "Настройка элементов HUD на экране",
   moduleCategory = ModuleCategory.RENDER
)
public class Interface extends Module {
   private static final Identifier TARGET_HUD_GLOW_TEXTURE = Identifier.of("mre", "images/glow.png");
   private static final Identifier WATERMARK_TEXTURE = Identifier.of("mre", "images/watermark.png");
   private final ModeListSetting jBvFMv = new ModeListSetting(
      "Элементы",
      new BooleanSetting("Ватермарка", true),
      new BooleanSetting("Логотип", true),
      new BooleanSetting("Инфо", true),
      new BooleanSetting(
         "Привязанные модули",
         true
      ),
      new BooleanSetting(
         D.k(
            new int[]{1207, 1263, 1150, 1092, 1173, 1256, 1143, 1097, 135, 1257, 1026, 1096, 1170, 1173, 1036, 1086, 1177, 1173, 1143},
            new int[]{167, 213, 60, 124}
         ),
         true
      ),
      new BooleanSetting("Бафы", true),
      new BooleanSetting("КулДауны", true),
      new BooleanSetting("ServerHelper", true),
      new BooleanSetting("Нотификации", true),
      new BooleanSetting(
         "Активный таргет", true
      ),
      new BooleanSetting(
         "Кастомный хотбар", false
      ),
      new BooleanSetting("Броня", true),
      new BooleanSetting("Полоса тотемов", false),
      new BooleanSetting("Список модулей", true),
      new BooleanSetting("Блюр фона", true)
   );
   private final SliderSetting nNUUBXA = new SliderSetting(
      "Интенсивность фона",
      0.5,
      0.05F,
      1.0,
      0.01F
   );
   private final SliderSetting iKkus = new SliderSetting(
      D.k(
         new int[]{
            1231, 1137, 1260, 1163, 1258, 1037, 1174, 1164, 1258, 1138, 1263, 1276, 1179, 108, 1177, 1166, 1252, 1138, 1173, 1152, 1253, 1142, 1168, 1164
         },
         new int[]{215, 76, 174, 190}
      ),
      0.5,
      0.05F,
      1.0,
      0.01F
   );
   private final SliderSetting o8lAyf = new SliderSetting(
      D.k(
         new int[]{1238, 1230, 1162, 1277, 1267, 1202, 1264, 1274, 1267, 1229, 1161, 1162, 1154, 211, 1157, 1267, 1275, 1231, 1277, 1269, 1164, 1229, 1274},
         new int[]{206, 243, 200, 200}
      ),
      0.2F,
      0.05F,
      1.0,
      0.01F
   );
   private final ModeSetting b72q45 = new ModeSetting(
      "Фон шейдер",
      "Обычный",
      "Обычный",
      "Шейдер",
      "Liquid Glass"
   );
   private final ModeSetting feack = new ModeSetting(
         "Шейдер фона",
         "Aurora",
         "Aurora",
         "Energy",
         "Nebula",
         "Cosmic Veil",
         "Deep Space",
         "Matrix",
         "Void",
         "Plasma"
      )
      .setVisible(() -> this.b72q45.is("Шейдер"));
   private final SliderSetting s3bfxgy = new SliderSetting(
         D.k(
            new int[]{1277, 1042, 1218, 1123, 1186, 1122, 1211, 1129, 1244, 1043, 1214, 1048, 194, 1050, 1225, 1133, 1238, 1127, 1212, 1124},
            new int[]{226, 82, 252, 84}
         ),
         0.65F,
         0.1F,
         1.0,
         0.01F
      )
      .setVisible(() -> this.b72q45.is("Шейдер"));
   private final SliderSetting evYwr1 = new SliderSetting(
         "Сила блюра", 22.0, 5.0, 45.0, 1.0
      )
      .setVisible(() -> this.b72q45.is("Liquid Glass"));
   private final SliderSetting mCB3y = new SliderSetting(
         D.k(
            new int[]{1194, 1135, 1137, 1118, 1269, 1055, 1032, 1108, 1163, 1134, 1037, 1061, 149, 1134, 1037, 1116, 1167, 1044, 1151},
            new int[]{181, 47, 79, 105}
         ),
         0.45,
         0.1,
         1.0,
         0.01
      )
      .setVisible(() -> this.b72q45.is("Liquid Glass"));
   private final SliderSetting xgFpWWZ = new SliderSetting(
      D.k(
         new int[]{1043, 1190, 1160, 1248, 1087, 184, 1261, 1217, 44, 1190, 1271, 1248, 1086, 1197, 1153, 1259, 1073, 1184, 1159}, new int[]{12, 152, 200, 222}
      ),
      8.0,
      1.0,
      20.0,
      0.5
   );
   private final Draggable vI37 = DragManager.installDrag(this, "Watermark", 4.0F, 4.0F);
   private final Draggable chgvv8 = DragManager.installDrag(this, "Logo", 4.0F, 28.0F);
   private final Draggable qzcKzlZ = DragManager.installDrag(this, "Info", 4.0F, 24.0F);
   private final Draggable za7swXa = DragManager.installDrag(this, "HotKeys", 100.0F, 50.0F);
   private final Draggable a6JkFr8 = DragManager.installDrag(this, "StaffList", 200.0F, 50.0F);
   private final Draggable or0d = DragManager.installDrag(this, "Potions", 300.0F, 50.0F);
   private final Draggable hGdh5qk = DragManager.installDrag(this, "CoolDowns", 300.0F, 130.0F);
   private final Draggable k9fd = DragManager.installDrag(this, "ServerHelper", 300.0F, 200.0F);
   private final Draggable y3p1 = DragManager.installDrag(this, "CustomHotbar", 150.0F, 220.0F);
   private final Draggable tMa0 = DragManager.installDrag(this, "ArmourBar", 150.0F, 245.0F);
   private final Draggable dzLPe = DragManager.installDrag(this, "TotemBar", 150.0F, 270.0F);
   private final Draggable i1oqHrD = DragManager.installDrag(this, "ModuleList", 320.0F, 320.0F);
   private final Draggable wUY90 = DragManager.installDrag(this, "TargetHUD", 130.0F, 130.0F);
   public final NotificationsElement notifications = new NotificationsElement();
   private final Animation ukd6 = new Animation(Easing.EXPO_OUT, 1200L);
   private final Animation z5wC45 = new Animation(Easing.EXPO_OUT, 1200L);
   private float xnN0vpO = 0.0F;
   private long c7e6hv = System.currentTimeMillis();
   private float x9nT = -1.0F;
   private final Animation cw47 = new Animation(Easing.EXPO_OUT, 200L);
   private final Animation d2b4IIw = new Animation(Easing.EXPO_OUT, 300L);
   private final List<Interface.BindEntry> zrGzWkO = new ArrayList<>();
   private final List<Interface.Staff> imz8r = new ArrayList<>();
   private final Pattern s74dnbF = Pattern.compile("^\\w{3,16}$");
   private final Pattern b41dCrw = Pattern.compile(
      D.k(
         new int[]{
            128,
            92,
            144,
            42316,
            210,
            42309,
            196,
            42322,
            210,
            42323,
            196,
            42330,
            210,
            42337,
            196,
            42344,
            210,
            42,
            218,
            22,
            193,
            18,
            228,
            25,
            210,
            42,
            218,
            26,
            202,
            27,
            228,
            25,
            210,
            42,
            218,
            19,
            203,
            26,
            200,
            39,
            204,
            10,
            228,
            25,
            217,
            24,
            221,
            39,
            204,
            10,
            1156,
            1093,
            1178,
            1091,
            1272,
            7,
            1259,
            1091,
            1155,
            1092,
            210,
            1097,
            1158,
            1095,
            1168,
            1087,
            196,
            1099,
            1178,
            1098,
            1152,
            1094,
            210,
            1092,
            1155,
            1099,
            1178,
            1091,
            1155,
            7,
            1168,
            1076,
            1272,
            1091,
            1256,
            10,
            228,
            25,
            218,
            23,
            222,
            39,
            204,
            10,
            228,
            25,
            205,
            3,
            202,
            26,
            218,
            42,
            218,
            7,
            1172,
            1077,
            1272,
            1099,
            1260,
            1096,
            1272,
            7,
            242,
            20,
            220,
            30,
            216,
            42,
            218,
            7,
            1262,
            1094,
            1167,
            1083,
            1182,
            1095,
            196,
            39,
            204,
            5,
            205,
            11,
            222,
            42,
            218,
            7,
            1263,
            1094,
            1159,
            1092,
            1168,
            1078,
            1274,
            7,
            242,
            20,
            193,
            15,
            242,
            20,
            196,
            39,
            245,
            15,
            204,
            39,
            243,
            10,
            1270,
            1081,
            1261,
            1095,
            196,
            1082,
            1260,
            1094,
            1166,
            1102,
            1262,
            10,
            1273,
            1093,
            1260,
            1078,
            1275,
            1103,
            1171,
            1102,
            1154,
            82,
            128,
            92
         },
         new int[]{174, 118, 184, 123}
      )
   );
   private static final float HB_PAD = 3.0F;
   private static final float HB_CELL = 20.0F;
   private static final float HB_ICON = 16.0F;
   private static final float HB_OFFHAND_GAP = 4.0F;
   private final Animation u0hqCA = new Animation(Easing.EXPO_OUT, 200L);
   private final Animation tPlp4sF = new Animation(Easing.EXPO_OUT, 200L);
   private final SliderSetting wE7oAIB = new SliderSetting(
         "Размер бафов", 1.0, 0.5, 2.0, 0.05
      )
      .setVisible(() -> false);
   private final SliderSetting bceo13 = new SliderSetting(
         "Прозрачность бафов",
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final SliderSetting eb1jv = new SliderSetting(
         "Размер кейбиндов",
         1.0,
         0.5,
         2.0,
         0.05
      )
      .setVisible(() -> false);
   private final SliderSetting v6Ei8 = new SliderSetting(
         D.k(
            new int[]{1114, 1237, 1101, 1161, 1029, 1189, 1076, 1155, 1147, 1236, 1073, 1266, 101, 1199, 1094, 1159, 1140, 1197, 1102, 1162, 1147, 1191},
            new int[]{69, 149, 115, 190}
         ),
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final SliderSetting dkl1 = new SliderSetting(
         "Размер стафф", 1.0, 0.5, 2.0, 0.05
      )
      .setVisible(() -> false);
   private final SliderSetting bd4Lx1 = new SliderSetting(
         "Прозрачность стафф",
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final SliderSetting kL8npIE = new SliderSetting(
         "Размер кд", 1.0, 0.5, 2.0, 0.05
      )
      .setVisible(() -> false);
   private final SliderSetting y0t3x = new SliderSetting(
         "Прозрачность кд",
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final Interface.HudPopup s07r = new Interface.HudPopup("Potions", this.or0d, this.wE7oAIB, this.bceo13, null);
   private final Interface.HudPopup i6tb = new Interface.HudPopup("CoolDowns", this.hGdh5qk, this.kL8npIE, this.y0t3x, null);
   private final SliderSetting gu9hGqu = new SliderSetting(
         "Размер ServerHelper",
         1.0,
         0.5,
         2.0,
         0.05
      )
      .setVisible(() -> false);
   private final SliderSetting lDjOjC = new SliderSetting(
         D.k(
            new int[]{1216, 1033, 1099, 1144, 1183, 1145, 1074, 1138, 1249, 1032, 1079, 1027, 255, 26, 16, 61, 169, 44, 7, 7, 186, 37, 5, 42, 173},
            new int[]{223, 73, 117, 79}
         ),
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final Interface.HudPopup t9e3F = new Interface.HudPopup("ServerHelper", this.k9fd, this.gu9hGqu, this.lDjOjC, null);
   private final SliderSetting cDpKE96 = new SliderSetting(
         "Размер вм", 1.05, 0.5, 2.0, 0.05
      )
      .setVisible(() -> false);
   private final SliderSetting bR856 = new SliderSetting(
         "Прозрачность вм",
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final Interface.HudPopup eaunw = new Interface.HudPopup("Watermark", this.vI37, this.cDpKE96, this.bR856, null);
   private final SliderSetting ghqvgi9 = new SliderSetting(
         "Размер лого", 1.0, 0.5, 2.0, 0.05
      )
      .setVisible(() -> false);
   private final SliderSetting mk9EU = new SliderSetting(
         "Прозрачность лого",
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final Interface.HudPopup gzTbtan = new Interface.HudPopup("Logo", this.chgvv8, this.ghqvgi9, this.mk9EU, null);
   private final SliderSetting yegg = new SliderSetting(
         "Размер инфо", 1.05, 0.5, 2.0, 0.05
      )
      .setVisible(() -> false);
   private final SliderSetting n9qn8g0 = new SliderSetting(
         "Прозрачность инфо",
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final Interface.HudPopup v1iE = new Interface.HudPopup("Info", this.qzcKzlZ, this.yegg, this.n9qn8g0, null);
   private final SliderSetting ciwihhL = new SliderSetting(
         "Размер хотбар", 1.0, 0.5, 2.0, 0.05
      )
      .setVisible(() -> false);
   private final SliderSetting yuP9hM = new SliderSetting(
         D.k(
            new int[]{1207, 1167, 1255, 1277, 1256, 1279, 1182, 1271, 1174, 1166, 1179, 1158, 136, 1162, 1255, 1160, 1177, 1279, 1177},
            new int[]{168, 207, 217, 202}
         ),
         180.0,
         0.0,
         255.0,
         1.0
      )
      .setVisible(() -> false);
   private final BooleanSetting zcb63A7 = new BooleanSetting(
         D.k(
            new int[]{1147, 1155, 1219, 1143, 1107, 1270, 1227, 1143, 1062, 1265, 217, 1149, 1114, 1158, 1217, 1024, 1105, 1276, 1211, 1141, 1114},
            new int[]{100, 189, 249, 71}
         ),
         true
      )
      .setVisible(() -> false);
   private final Interface.HudPopup l96dNP = new Interface.HudPopup("CustomHotbar", this.y3p1, this.ciwihhL, this.yuP9hM, null);
   private final SliderSetting zaa4l;
   private final SliderSetting cFxl;
   private final BooleanSetting tA76dFX;
   private final Interface.HudPopup nb22ru;
   private final SliderSetting jD138lb;
   private final SliderSetting f9jr8bx;
   private final Interface.HudPopup sexFA0;
   private final Draggable wvBd;
   private final SliderSetting uF9W;
   private final SliderSetting cSgGT5;
   private final BooleanSetting c2W8w;
   private final BooleanSetting q3Md;
   private final Interface.HudPopup l2rzcE;
   private final Interface.HudPopup nwWANcB;
   private final Interface.HudPopup z436cbx;
   private final SliderSetting iIFr;
   private final SliderSetting ix0h;
   private final Interface.HudPopup k7gBcr8;
   private final BooleanSetting xtEQ;
   private final BooleanSetting eF15;
   private final Animation nquybb;
   private final List<Module> eyZ0id;
   private final SliderSetting mwxu0;
   private final SliderSetting gTwpN0y;
   private final BooleanSetting mqh3;
   private final BooleanSetting rkOWd;
   private final Interface.HudPopup azttZ;
   private float vlw9m7b;
   private float ylsIQh;
   private long cpytB;
   private LivingEntity ur2BeWr;
   private long gGv534P;
   private Entity loqITZz;
   private final Animation obkH;
   private final Animation faiI;
   private final Animation aFNHi;
   private final Animation nMjsSpC;
   private static final float POPUP_HEADER_H = 15.0F;
   private static final float POPUP_TOGGLE_H = 14.0F;
   private static final float POPUP_SLIDER_H = 19.0F;
   private static final float POPUP_PAD = 4.0F;
   private static final float POPUP_TRACK_INSET = 6.0F;
   private float rkGm7L;
   private float wCd108;
   private float r7kZiw;
   private final List<Interface.HeadParticle> dm31z;
   private final List<Interface.PotionItem> v42Pbh1;
   private RegistryEntry<StatusEffect> fAqm;
   private long yB14jv;
   private static List<RegistryEntry<StatusEffect>> eA8s;
   private static final Random RANDOM = new Random();
   private final List<Interface.CooldownItem> vzKx;
   private static final Item[] COOLDOWN_EXAMPLE_ITEMS = new Item[]{
      Items.ENDER_PEARL, Items.ENDER_EYE, Items.CHORUS_FRUIT, Items.MACE, Items.ENCHANTED_GOLDEN_APPLE, Items.TRIDENT, Items.SHIELD, Items.GOAT_HORN
   };
   private int i965v4;
   private long r1b1;
   private final Animation rnT632K;
   private final Animation cMzz;

   public Interface() {
      this.l96dNP
         .extraRows
         .add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE,
               D.k(
                  new int[]{1243, 1082, 1191, 1190, 1267, 1103, 1199, 1190, 1158, 1096, 189, 1196, 1274, 1087, 1189, 1233, 1265, 1093, 1247, 1188, 1274},
                  new int[]{196, 4, 157, 150}
               ),
               this.zcb63A7,
               null
            )
         );
      this.zaa4l = new SliderSetting(
            "Размер броня", 1.0, 0.5, 2.0, 0.05
         )
         .setVisible(() -> false);
      this.cFxl = new SliderSetting(
            D.k(
               new int[]{1217, 1165, 1118, 1134, 1182, 1277, 1063, 1124, 1248, 1164, 1058, 1045, 254, 1276, 1056, 1127, 1251, 1154},
               new int[]{222, 205, 96, 89}
            ),
            180.0,
            0.0,
            255.0,
            1.0
         )
         .setVisible(() -> false);
      this.tA76dFX = new BooleanSetting(
            "Полоса прочности", true
         )
         .setVisible(() -> false);
      this.nb22ru = new Interface.HudPopup("ArmourBar", this.tMa0, this.zaa4l, this.cFxl, null);
      this.nb22ru
         .extraRows
         .add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE,
               "Полоса прочности",
               this.tA76dFX,
               null
            )
         );
      this.jD138lb = new SliderSetting(
            "Размер тотем-бар",
            1.0,
            0.5,
            2.0,
            0.05
         )
         .setVisible(() -> false);
      this.f9jr8bx = new SliderSetting(
            D.k(
               new int[]{1123, 1112, 1062, 1124, 1084, 1064, 1119, 1134, 1090, 1113, 1114, 1055, 92, 1114, 1062, 1041, 1097, 1060, 53, 1122, 1100, 1112},
               new int[]{124, 24, 24, 83}
            ),
            180.0,
            0.0,
            255.0,
            1.0
         )
         .setVisible(() -> false);
      this.sexFA0 = new Interface.HudPopup("TotemBar", this.dzLPe, this.jD138lb, this.f9jr8bx, null);
      this.wvBd = DragManager.installDrag(this, "Notifications", 250.0F, 250.0F);
      this.uF9W = new SliderSetting(
            "Размер notif", 1.0, 0.5, 2.0, 0.05
         )
         .setVisible(() -> false);
      this.cSgGT5 = new SliderSetting(
            "Прозрачность notif",
            180.0,
            0.0,
            255.0,
            1.0
         )
         .setVisible(() -> false);
      this.c2W8w = new BooleanSetting(
            "Состояния модулей", true
         )
         .setVisible(() -> false);
      this.q3Md = new BooleanSetting("Снос тотема", true)
         .setVisible(() -> false);
      this.l2rzcE = new Interface.HudPopup("Notifications", this.wvBd, this.uF9W, this.cSgGT5, null);
      this.l2rzcE
         .extraRows
         .add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE,
               "Состояния модулей",
               this.c2W8w,
               null
            )
         );
      this.l2rzcE
         .extraRows
         .add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE,
               "Снос тотема",
               this.q3Md,
               null
            )
         );
      this.nwWANcB = new Interface.HudPopup("Keybinds", this.za7swXa, this.eb1jv, this.v6Ei8, null);
      this.z436cbx = new Interface.HudPopup("StaffList", this.a6JkFr8, this.dkl1, this.bd4Lx1, null);
      this.iIFr = new SliderSetting(
            "Размер списка", 1.0, 0.5, 2.0, 0.05
         )
         .setVisible(() -> false);
      this.ix0h = new SliderSetting(
            D.k(
               new int[]{1067, 1145, 1248, 1036, 1140, 1033, 1177, 1030, 1034, 1144, 1180, 1143, 20, 1144, 1249, 1027, 1141, 1027, 1262},
               new int[]{52, 57, 222, 59}
            ),
            180.0,
            0.0,
            255.0,
            1.0
         )
         .setVisible(() -> false);
      this.k7gBcr8 = new Interface.HudPopup("ModuleList", this.i1oqHrD, this.iIFr, this.ix0h, null);
      this.xtEQ = new BooleanSetting("Суффиксы", true).setVisible(() -> false);
      this.eF15 = new BooleanSetting(
            "Прозрачный фон", false
         )
         .setVisible(() -> false);
      this.nquybb = new Animation(Easing.EXPO_OUT, 200L);
      this.eyZ0id = new ArrayList<>();
      this.k7gBcr8
         .extraRows
         .add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE, "Суффиксы", this.xtEQ, null
            )
         );
      this.k7gBcr8
         .extraRows
         .add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE,
               "Прозрачный фон",
               this.eF15,
               null
            )
         );
      this.mwxu0 = new SliderSetting(
         "Размер тхуд", 1.0, 0.5, 2.0, 0.05
      );
      this.gTwpN0y = new SliderSetting(
         "Прозрачность тхуд",
         180.0,
         0.0,
         255.0,
         1.0
      );
      this.mqh3 = new BooleanSetting(
            "При наведении", true
         )
         .setVisible(() -> false);
      this.rkOWd = new BooleanSetting(
            D.k(
               new int[]{1028, 1175, 1279, 1190, 1068, 1250, 1271, 1190, 1113, 1253, 229, 1193, 1115, 1180, 1265, 1194, 1070, 1259, 1166},
               new int[]{27, 169, 197, 150}
            ),
            true
         )
         .setVisible(() -> false);
      this.azttZ = new Interface.HudPopup("TargetHUD", this.wUY90, this.mwxu0, this.gTwpN0y, null);
      this.azttZ
         .extraRows
         .add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE,
               "При наведении",
               this.mqh3,
               null
            )
         );
      this.azttZ
         .extraRows
         .add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE,
               D.k(
                  new int[]{1107, 1235, 1140, 1266, 1147, 1190, 1148, 1266, 1038, 1185, 110, 1277, 1036, 1240, 1146, 1278, 1145, 1199, 1029},
                  new int[]{76, 237, 78, 194}
               ),
               this.rkOWd,
               null
            )
         );
      this.cpytB = 0L;
      this.ur2BeWr = null;
      this.gGv534P = 0L;
      this.obkH = new Animation(Easing.EXPO_OUT, 300L);
      this.faiI = new Animation(Easing.EXPO_OUT, 300L);
      this.aFNHi = new Animation(Easing.EXPO_OUT, 600L);
      this.nMjsSpC = new Animation(Easing.EXPO_OUT, 300L);
      this.rkGm7L = 1.0F;
      this.wCd108 = 1.0F;
      this.r7kZiw = -1.0F;
      this.dm31z = new ArrayList<>();
      this.v42Pbh1 = new CopyOnWriteArrayList<>();
      this.yB14jv = 0L;
      this.vzKx = new CopyOnWriteArrayList<>();
      this.i965v4 = 0;
      this.r1b1 = 0L;
      this.rnT632K = new Animation(Easing.EXPO_OUT, 200L);
      this.cMzz = new Animation(Easing.EXPO_OUT, 200L);
   }

   public String getHudShaderMode() {
      return this.feack.getValue();
   }

   public float getHudShaderOpacity() {
      return this.s3bfxgy.getFloatValue();
   }

   public float getBackgroundIntensity() {
      return this.nNUUBXA.getFloatValue();
   }

   public void drawHeaderBackground(float x, float y, float w, float h, float radius, int alpha) {
      if (this.b72q45.is("Шейдер")) {
         this.jgyvr6o(x, y, w, h, radius, alpha);
      } else if (this.b72q45.is("Liquid Glass")) {
         this.apkZ3(x, y, w, h, radius, alpha);
      } else {
         float intensity = this.iKkus.getFloatValue();
         this.j4995qd(x, y, w, h, radius, (int)(alpha * intensity), true);
      }
   }

   private void hLv4ha(float x, float y, float w, float h, float radius, int alpha) {
      if (this.b72q45.is("Шейдер")) {
         this.jgyvr6o(x, y, w, h, radius, alpha);
      } else if (this.b72q45.is("Liquid Glass")) {
         this.apkZ3(x, y, w, h, radius, alpha);
      } else {
         float intensity = this.o8lAyf.getFloatValue();
         this.j4995qd(x, y, w, h, radius, (int)(alpha * intensity), false);
      }
   }

   public void drawBackground(float x, float y, float w, float h, float radius, int alpha) {
      if (this.b72q45.is("Шейдер")) {
         this.jgyvr6o(x, y, w, h, radius, alpha);
      } else if (this.b72q45.is("Liquid Glass")) {
         this.apkZ3(x, y, w, h, radius, alpha);
      } else {
         int intensity = (int)(alpha * this.nNUUBXA.getFloatValue());
         this.j4995qd(x, y, w, h, radius, intensity, true);
      }
   }

   public void drawBackground(float x, float y, float w, float h, float tl, float tr, float bl, float br, int alpha) {
      Vector4f radii = new Vector4f(tl, tr, bl, br);
      if (this.b72q45.is("Шейдер")) {
         this.jgyvr6o(x, y, w, h, tl, alpha);
      } else if (this.b72q45.is("Liquid Glass")) {
         this.mtYnbnm(x, y, w, h, radii, alpha);
      } else {
         int intensity = (int)(alpha * this.nNUUBXA.getFloatValue());
         int bg = ColorProvider.setAlpha(ColorProvider.getColorWindowBg(), intensity);
         DrawUtil.drawRound(x, y, w, h, radii, bg);
         DrawUtil.drawRound(x, y, w, h, radii, ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), (int)(intensity * 0.05F)));
      }
   }

   @Subscribe
   public void onEventHUD(EventHUD e) {
      if (this.mc.player != null && !this.mc.options.hudHidden && !this.mc.getDebugHud().shouldShowDebugHud()) {
         if (this.jBvFMv.isEnabled("Нотификации")) {
            this.notifications.render(e.getDrawContext());
            this.nn6d(e.getDrawContext());
         }

         if (this.jBvFMv
            .isEnabled("Активный таргет")) {
            this.fz30Tm(e.getDrawContext());
         }

         if (this.jBvFMv.isEnabled("Ватермарка")) {
            this.bx4cip(e.getDrawContext());
         }

         if (this.jBvFMv.isEnabled("Логотип")) {
            this.ep1H8(e.getDrawContext());
         }

         if (this.jBvFMv.isEnabled("Инфо")) {
            this.wFqc(e.getDrawContext());
         }

         if (this.jBvFMv
            .isEnabled(
               D.k(
                  new int[]{1218, 1257, 1103, 1109, 1170, 1182, 1095, 1114, 1248, 1250, 1090, 71, 1249, 1175, 1091, 1060, 1254, 1169},
                  new int[]{221, 169, 119, 103}
               )
            )) {
            this.vpidz(e.getDrawContext());
         }

         if (this.jBvFMv
            .isEnabled("Список модулей")) {
            this.x9EhC(e.getDrawContext());
         }

         if (this.jBvFMv
            .isEnabled(
               D.k(
                  new int[]{1245, 1165, 1113, 1086, 1279, 1162, 1104, 1075, 237, 1163, 1061, 1074, 1272, 1271, 1067, 1092, 1267, 1271, 1104},
                  new int[]{205, 183, 27, 6}
               )
            )) {
            this.x7c7RgY(e.getDrawContext());
         }

         if (this.jBvFMv.isEnabled("Бафы")) {
            this.x5ne9(e.getDrawContext());
         }

         if (this.jBvFMv.isEnabled("КулДауны")) {
            this.g5Ym5(e.getDrawContext());
         }

         if (this.jBvFMv.isEnabled("ServerHelper")) {
            this.ymQF3pn(e.getDrawContext());
         }

         if (this.jBvFMv
            .isEnabled(
               "Кастомный хотбар"
            )) {
            this.qVzq4G4(e.getDrawContext());
         }

         if (this.jBvFMv.isEnabled("Броня")) {
            this.z5DwAe(e.getDrawContext());
         }

         if (this.jBvFMv
            .isEnabled("Полоса тотемов")) {
            this.fPeAO0l(e.getDrawContext());
         }
      }
   }

   @Subscribe
   private void onUpdate(EventTick e) {
      if (this.mc.player != null && this.mc.world != null) {
         if (this.jBvFMv
            .isEnabled(
               D.k(
                  new int[]{1275, 1199, 1064, 1151, 1241, 1192, 1057, 1138, 203, 1193, 1108, 1139, 1246, 1237, 1114, 1029, 1237, 1237, 1057},
                  new int[]{235, 149, 106, 71}
               )
            )) {
            this.update();
         }

         if (this.jBvFMv.isEnabled("Бафы")) {
            this.ems5();
         }

         if (this.jBvFMv.isEnabled("КулДауны")) {
            this.d6Vfg();
         }
      }
   }

   @Subscribe
   private void onPopTotem(EventPopTotem e) {
      if (this.isTotemNotifEnabled()) {
         PlayerEntity player = e.getPlayer();
         String name = player.getName().getString();
         boolean enchanted = !player.getOffHandStack().getEnchantments().isEmpty();
         Text tagText = NameTags.processName(player);
         this.notifications.postTotem(tagText, enchanted);
      }
   }

   @Subscribe
   private void onCooldownPacket(EventPacket e) {
      if (this.mc.player != null && e.getType() == EventPacket.Type.RECEIVE) {
         if (e.getPacket() instanceof CooldownUpdateS2CPacket c) {
            Item item = Registries.ITEM.get(c.cooldownGroup());
            if (item == null || item == Items.AIR) {
               return;
            }

            for (Interface.CooldownItem ci : this.vzKx) {
               if (ci.item == item) {
                  ci.active = false;
               }
            }

            if (c.cooldown() != 0) {
               long durMs = c.cooldown() * 50L;
               this.vzKx.add(new Interface.CooldownItem(item, System.currentTimeMillis() + durMs, durMs));
            }
         } else if (e.getPacket() instanceof PlayerRespawnS2CPacket) {
            this.vzKx.clear();
         }
      }
   }

   private void vpidz(DrawContext context) {
      if (this.mc.player != null) {
         if (!(this.mc.currentScreen instanceof ChatScreen)) {
            this.nwWANcB.open = false;
            this.nwWANcB.draggingSlider = null;
         }

         this.eemFuA(this.nwWANcB, context);
         this.lj0r6b(context);
         this.sao7CKx(this.nwWANcB, context);
         this.z1TvB0(this.nwWANcB, context);
      }
   }

   private void lj0r6b(DrawContext context) {
      if (this.mc.player != null) {
         float posX = this.za7swXa.getX();
         float posY = this.za7swXa.getY();
         float headerHeight = 14.0F;
         float itemHeight = 9.5F;
         float minWidth = 52.0F;
         float padX = 5.0F;
         float padY = 2.0F;
         List<Interface.BindEntry> entries = this.zrGzWkO;
         entries.clear();

         for (Module module : Viola.getInstance().getModuleStorage().getModules()) {
            if (module.getKey() != -1 && module.getAnimation().getValue() > 0.001) {
               entries.add(
                  new Interface.BindEntry(module.getName(), KeyStorage.getKey(module.getKey()), (double)module.getAnimation().getValue(), module.getCategory())
               );
            }

            for (Setting setting : module.getSettings()) {
               if (setting instanceof BooleanSetting bs && bs.getKey() != -1 && bs.getValue()) {
                  entries.add(new Interface.BindEntry(bs.getName(), KeyStorage.getKey(bs.getKey()), 1.0, module.getCategory()));
               }
            }
         }

         boolean isFound = !entries.isEmpty();
         if (!isFound && !(this.mc.currentScreen instanceof ChatScreen)) {
            this.cw47.run(0.0F);
         } else {
            this.cw47.run(1.0F);
         }

         float globalAlpha = this.cw47.getValue();
         if (!(globalAlpha <= 0.05F)) {
            int headerAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * globalAlpha));
            boolean showExample = this.mc.currentScreen instanceof ChatScreen && !isFound;
            float maxLabelBoxW = minWidth;
            float maxBindBoxW = 0.0F;

            for (Interface.BindEntry entry : entries) {
               float animVal = (float)Math.min(1.0, Math.max(0.0, entry.animValue()));
               if (!(animVal <= 0.001F)) {
                  float lw = 9.0F + Fonts.SFMEDIUM.get().getWidth(entry.label(), 7.0F) + 7.0F;
                  if (lw > maxLabelBoxW) {
                     maxLabelBoxW = lw;
                  }

                  float bw = Fonts.SFMEDIUM.get().getWidth(entry.bind(), 6.75F) + 6.0F;
                  if (bw > maxBindBoxW) {
                     maxBindBoxW = bw;
                  }
               }
            }

            if (showExample) {
               float lwx = 9.0F + Fonts.SFMEDIUM.get().getWidth("Example", 7.0F) + 7.0F;
               if (lwx > maxLabelBoxW) {
                  maxLabelBoxW = lwx;
               }

               float bw = Fonts.SFMEDIUM.get().getWidth("K", 6.75F) + 6.0F;
               if (bw > maxBindBoxW) {
                  maxBindBoxW = bw;
               }
            }

            float contentHeight = 0.0F;
            if (showExample) {
               contentHeight = itemHeight;
            } else {
               for (Interface.BindEntry entryx : entries) {
                  float av = (float)Math.min(1.0, Math.max(0.0, entryx.animValue()));
                  if (!(av <= 0.001F)) {
                     contentHeight += itemHeight * av;
                  }
               }
            }

            float rawWidth = maxLabelBoxW + maxBindBoxW + padX * 2.0F;
            this.nwWANcB.panelWidth.run(rawWidth);
            float totalRowWidth = this.nwWANcB.panelWidth.getValue();
            if (totalRowWidth < 20.0F) {
               totalRowWidth = rawWidth;
            }

            float totalHeight = headerHeight + contentHeight + padY * 2.0F;
            this.q4cfy(this.nwWANcB, posX, posY, totalRowWidth, totalHeight, 6.0F, globalAlpha);
            this.qm7dp(Fonts.SFMEDIUM.get(), "Keybinds", posX + padX + 4.0F, posY + padY + 2.5F, headerAlpha, 8.0F);
            DrawUtil.drawText(
               Fonts.ALPHADLC.get(),
               "g",
               posX + totalRowWidth - padX - 11.0F,
               posY + padY + 3.0F,
               ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), headerAlpha),
               9.0F
            );
            float curY = posY + headerHeight + padY;

            for (Interface.BindEntry entryxx : entries) {
               float animVal = (float)Math.min(1.0, Math.max(0.0, entryxx.animValue()));
               if (!(animVal <= 0.001F)) {
                  int itemAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * animVal * globalAlpha));
                  if (itemAlpha >= 5) {
                     float rowHeight = itemHeight * animVal;
                     context.getMatrices().push();
                     context.getMatrices().translate(posX + totalRowWidth / 2.0F, curY + rowHeight / 2.0F, 0.0F);
                     context.getMatrices().scale(animVal, animVal, animVal);
                     context.getMatrices().translate(-(posX + totalRowWidth / 2.0F), -(curY + rowHeight / 2.0F), 0.0F);

                     String catIcon = switch (entryxx.category()) {
                        case COMBAT -> "a";
                        case MOVEMENT -> "b";
                        case RENDER -> "c";
                        case PLAYER -> "d";
                        case MISC -> "e";
                     };
                     DrawUtil.drawText(
                        Fonts.ICONS_MINCED.get(),
                        catIcon,
                        posX + padX + 1.0F,
                        curY + 1.5F,
                        ColorProvider.setAlpha(ColorProvider.getColorClient(), itemAlpha),
                        7.0F
                     );
                     DrawUtil.drawText(
                        Fonts.SFMEDIUM.get(), entryxx.label(), posX + padX + 9.0F, curY + 1.35F, ColorProvider.rgba(255, 255, 255, (float)itemAlpha), 7.0F
                     );
                     this.i0qk0(entryxx.bind(), posX + totalRowWidth - padX - maxBindBoxW, curY, maxBindBoxW, itemHeight, itemAlpha);
                     context.getMatrices().pop();
                     curY += rowHeight;
                  }
               }
            }

            if (showExample) {
               DrawUtil.drawText(
                  Fonts.ICONS_MINCED.get(), "a", posX + padX + 1.0F, curY + 1.5F, ColorProvider.setAlpha(ColorProvider.getColorClient(), headerAlpha), 7.0F
               );
               DrawUtil.drawText(Fonts.SFMEDIUM.get(), "Example", posX + padX + 9.0F, curY + 1.35F, ColorProvider.rgba(255, 255, 255, (float)headerAlpha), 7.0F);
               this.i0qk0("K", posX + totalRowWidth - padX - maxBindBoxW, curY, maxBindBoxW, itemHeight, headerAlpha);
            }

            this.za7swXa.setWidth(totalRowWidth);
            this.za7swXa.setHeight(totalHeight);
         }
      }
   }

   private void x9EhC(DrawContext context) {
      if (this.mc.player != null) {
         float posX = this.i1oqHrD.getX();
         float posY = this.i1oqHrD.getY();
         float headerHeight = 14.0F;
         float itemHeight = 12.0F;
         List<Module> all = Viola.getInstance().getModuleStorage().getModules();
         List<Module> enabled = this.eyZ0id;
         enabled.clear();

         for (Module m : all) {
            float av = m.getAnimation().getValue();
            if (m.isEnabled() && av > 0.05F) {
               enabled.add(m);
            }
         }

         enabled.sort(Comparator.comparingDouble(mx -> -Fonts.SFMEDIUM.get().getWidth(mx.getName(), 7.0F)));
         boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
         boolean showExample = chatOpen && enabled.isEmpty();
         if (!chatOpen && enabled.isEmpty()) {
            this.nquybb.run(0.0F);
         } else {
            this.nquybb.run(1.0F);
         }

         float globalAlpha = this.nquybb.getValue();
         if (!(globalAlpha <= 0.05F)) {
            int headerAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * globalAlpha));
            float maxW = 30.0F;

            for (Module mx : enabled) {
               float w = 14.0F + Fonts.SFMEDIUM.get().getWidth(mx.getName(), 7.0F);
               String suffix = this.xtEQ.getValue() ? this.rFhvC(mx) : "";
               if (!suffix.isEmpty()) {
                  w += 4.0F + Fonts.SFMEDIUM.get().getWidth(suffix, 6.75F);
               }

               if (w > maxW) {
                  maxW = w;
               }
            }

            if (showExample) {
               float wx = 14.0F + Fonts.SFMEDIUM.get().getWidth("Example", 7.0F);
               if (wx > maxW) {
                  maxW = wx;
               }
            }

            float contentHeight = showExample ? itemHeight : enabled.size() * itemHeight;
            float totalHeight = headerHeight + contentHeight + 2.0F;
            float totalRowWidth = maxW;
            this.q4cfy(this.k7gBcr8, posX, posY, maxW, totalHeight, 6.0F, globalAlpha);
            this.qm7dp(
               Fonts.SFMEDIUM.get(),
               "Список модулей",
               posX + 6.0F,
               posY + 2.5F,
               headerAlpha,
               7.0F
            );
            DrawUtil.drawText(
               Fonts.ALPHADLC.get(), "j", posX + maxW - 14.0F, posY + 3.0F, ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), headerAlpha), 9.0F
            );
            float curY = posY + headerHeight;
            int index = 0;

            for (Module mx : enabled) {
               float av = (float)Math.min(1.0, Math.max(0.0, (double)mx.getAnimation().getValue()));
               float rowHeight = itemHeight * av;
               if (!(rowHeight <= 0.1F)) {
                  int itemAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * av * globalAlpha));
                  if (itemAlpha >= 5) {
                     if (!this.eF15.getValue()) {
                        DrawUtil.drawRound(
                           posX + 3.0F,
                           curY,
                           totalRowWidth - 6.0F,
                           rowHeight,
                           2.0F,
                           ColorProvider.setAlpha(ColorProvider.getColorWindowBg(), (int)(itemAlpha * 0.55F))
                        );
                     }

                     int accent = ColorProvider.interpolateColor(
                        ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), itemAlpha),
                        ColorProvider.setAlpha(ColorProvider.getColorMain(), itemAlpha),
                        Math.min(1.0F, index * 0.08F)
                     );
                     DrawUtil.drawRound(posX + 5.5F, curY + 3.0F, 1.5F, rowHeight - 6.0F, 0.75F, accent);
                     DrawUtil.drawText(Fonts.SFMEDIUM.get(), mx.getName(), posX + 10.0F, curY + 1.5F, accent, 7.0F);
                     String suffixx = this.xtEQ.getValue() ? this.rFhvC(mx) : "";
                     if (!suffixx.isEmpty()) {
                        float sx = posX + 10.0F + Fonts.SFMEDIUM.get().getWidth(mx.getName(), 7.0F) + 4.0F;
                        DrawUtil.drawText(
                           Fonts.SFMEDIUM.get(), suffixx, sx, curY + 1.5F, ColorProvider.setAlpha(ColorProvider.getColorText(), itemAlpha), 6.75F
                        );
                     }

                     curY += rowHeight;
                     index++;
                  }
               }
            }

            if (showExample) {
               int accent = ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), headerAlpha);
               DrawUtil.drawRound(posX + 5.5F, curY + 3.0F, 1.5F, itemHeight - 6.0F, 0.75F, accent);
               DrawUtil.drawText(Fonts.SFMEDIUM.get(), "Example", posX + 10.0F, curY + 1.5F, accent, 7.0F);
            }

            this.i1oqHrD.setWidth(totalRowWidth);
            this.i1oqHrD.setHeight(totalHeight);
         }
      }
   }

   private String rFhvC(Module m) {
      for (Setting s : m.getSettings()) {
         if (s instanceof ModeSetting ms && ms.visible.get()) {
            return ms.getValue();
         }
      }

      return "";
   }

   private void i0qk0(String bind, float colX, float rowY, float colW, float itemHeight, int itemAlpha) {
      float bindW = Fonts.SFMEDIUM.get().getWidth(bind, 6.75F);
      float textX = colX + (colW - bindW) / 2.0F + 0.75F;
      float textY = rowY + itemHeight / 2.0F - 4.0F;
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), bind, textX, textY, ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), itemAlpha), 6.75F);
   }

   private float sgfZdd() {
      return 186.0F;
   }

   private float lfox5() {
      return 26.0F;
   }

   private float ysZyq() {
      return 26.0F;
   }

   private float rCik() {
      return this.mc.getWindow().getScaledHeight() - 4.0F;
   }

   private float fLL2Gh7() {
      float size = this.l96dNP.size.getFloatValue();
      return this.mc.getWindow().getScaledWidth() / 2.0F + this.sgfZdd() * size / 2.0F;
   }

   private int p3D4h(float ratio) {
      ratio = MathHelper.clamp(ratio, 0.0F, 1.0F);
      int rgb = Color.HSBtoRGB(ratio * 0.33F, 0.85F, 0.95F);
      return ColorProvider.rgba(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, 255.0F);
   }

   private void x5yS0(String text, float x, float y, float size) {
      int outline = ColorProvider.rgba(0, 0, 0, 200.0F);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), text, x - 0.6F, y, outline, size);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), text, x + 0.6F, y, outline, size);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), text, x, y - 0.6F, outline, size);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), text, x, y + 0.6F, outline, size);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), text, x, y, -1, size);
   }

   private void qm7dp(MsdfFont font, String text, float x, float y, int alpha, float size) {
      DrawUtil.drawText(font, text, x, y, ColorProvider.setAlpha(ColorProvider.getColorText(), alpha), size);
   }

   private void qVzq4G4(DrawContext context) {
      if (this.mc.player != null) {
         float size = this.l96dNP.size.getFloatValue();
         float offW = this.lfox5();
         float mainW = this.sgfZdd();
         float height = this.ysZyq();
         float totalWidth = offW + 4.0F + mainW;
         float screenW = this.mc.getWindow().getScaledWidth();
         float ox = screenW / 2.0F - (offW + 4.0F + mainW / 2.0F) * size;
         float oy = this.rCik() - height * size;
         this.y3p1.setX(ox);
         this.y3p1.setY(oy);
         this.y3p1.setWidth(totalWidth);
         this.y3p1.setHeight(height);
         if (!(this.mc.currentScreen instanceof ChatScreen)) {
            this.l96dNP.open = false;
            this.l96dNP.draggingSlider = null;
         }

         this.eemFuA(this.l96dNP, context);
         this.dCnrUp(context, ox, oy);
         this.sao7CKx(this.l96dNP, context);
         this.z1TvB0(this.l96dNP, context);
      }
   }

   private void dCnrUp(DrawContext context, float ox, float oy) {
      float pad = 3.0F;
      float cell = 20.0F;
      float iconSize = 16.0F;
      float offW = this.lfox5();
      float mainW = this.sgfZdd();
      float height = this.ysZyq();
      int selected = this.mc.player.getInventory().selectedSlot;
      int divColor = ColorProvider.setAlpha(ColorProvider.getColorClient(), 45);
      this.q4cfy(this.l96dNP, ox, oy, offW, height, 6.0F, 1.0F);
      this.rnL1tsb(context, this.mc.player.getOffHandStack(), ox + pad, oy + pad, cell, iconSize, false);
      float mainX = ox + offW + 4.0F;
      this.q4cfy(this.l96dNP, mainX, oy, mainW, height, 6.0F, 1.0F);
      String lvl = String.valueOf(this.mc.player.experienceLevel);
      float lvlW = Fonts.SFMEDIUM.get().getWidth(lvl, 8.0F);
      int xpColor = ColorProvider.setAlpha(ColorProvider.getColorClient(), 255);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), lvl, mainX + mainW / 2.0F - lvlW / 2.0F, oy - 9.0F, xpColor, 8.0F);
      float cellY = oy + pad;

      for (int i = 0; i < 9; i++) {
         float cellX = mainX + pad + i * cell;
         if (i > 0) {
            DrawUtil.drawRound(cellX - 0.25F, cellY + 3.0F, 0.5F, cell - 6.0F, 0.25F, divColor);
         }

         this.rnL1tsb(context, this.mc.player.getInventory().getStack(i), cellX, cellY, cell, iconSize, i == selected);
      }
   }

   private void rnL1tsb(DrawContext context, ItemStack stack, float cellX, float cellY, float cell, float iconSize, boolean selected) {
      if (selected) {
         DrawUtil.drawRound(cellX + 0.5F, cellY, cell - 1.0F, cell, 2.5F, ColorProvider.setAlpha(ColorProvider.getColorClient(), 120));
      }

      if (!stack.isEmpty()) {
         float iconX = cellX + (cell - iconSize) / 2.0F;
         float iconY = cellY + (cell - iconSize) / 2.0F;
         this.n29w5(context, stack, iconX, iconY, iconSize, 255);
         if (this.zcb63A7.getValue() && stack.getCount() > 1) {
            String cnt = String.valueOf(stack.getCount());
            float cw = Fonts.SFMEDIUM.get().getWidth(cnt, 7.5F);
            this.x5yS0(cnt, cellX + cell - cw - 1.5F, cellY + cell - 8.5F, 7.5F);
         }
      }
   }

   private void z5DwAe(DrawContext context) {
      if (this.mc.player != null) {
         float ox = this.tMa0.getX();
         float oy = this.tMa0.getY();
         float cell = 18.0F;
         float iconSize = 15.2F;
         int equipped = 0;

         for (ItemStack s : this.mc.player.getArmorItems()) {
            if (!s.isEmpty()) {
               equipped++;
            }
         }

         boolean showDur = this.tA76dFX.getValue();
         float width = equipped * cell;
         float height = iconSize + 2.0F + (showDur ? 4.0F : 0.0F);
         this.tMa0.setWidth(width);
         this.tMa0.setHeight(height);
         if (!(this.mc.currentScreen instanceof ChatScreen)) {
            this.nb22ru.open = false;
            this.nb22ru.draggingSlider = null;
         }

         this.eemFuA(this.nb22ru, context);
         this.cj83l(context, ox, oy, width, height, cell, iconSize);
         this.sao7CKx(this.nb22ru, context);
         this.z1TvB0(this.nb22ru, context);
      }
   }

   private void cj83l(DrawContext context, float posX, float posY, float width, float height, float cell, float iconSize) {
      boolean showDur = this.tA76dFX.getValue();
      int i = 0;

      for (ItemStack stack : this.mc.player.getArmorItems()) {
         if (!stack.isEmpty()) {
            float x = posX + i * cell + 1.0F;
            this.n29w5(context, stack, x, posY + 1.0F, iconSize, 255);
            if (showDur) {
               int maxDur = stack.getMaxDamage();
               if (maxDur > 0) {
                  int dur = maxDur - stack.getDamage();
                  float frac = MathHelper.clamp((float)dur / maxDur, 0.0F, 1.0F);
                  float barY = posY + 1.0F + iconSize + 1.5F;
                  DrawUtil.drawRound(x, barY, iconSize, 2.0F, 1.0F, 1711276032);
                  if (frac > 0.001F) {
                     DrawUtil.drawRound(x, barY, iconSize * frac, 2.0F, 1.0F, this.p3D4h(frac));
                  }
               }
            }

            i++;
         }
      }
   }

   private void fPeAO0l(DrawContext context) {
      if (this.mc.player != null) {
         if (!(this.mc.currentScreen instanceof ChatScreen)) {
            this.sexFA0.open = false;
            this.sexFA0.draggingSlider = null;
         }

         this.eemFuA(this.sexFA0, context);
         this.imvnV(context);
         this.sao7CKx(this.sexFA0, context);
         this.z1TvB0(this.sexFA0, context);
      }
   }

   private void imvnV(DrawContext context) {
      float posX = this.dzLPe.getX();
      float posY = this.dzLPe.getY();
      int totemCount = 0;

      for (int i = 0; i < this.mc.player.getInventory().size(); i++) {
         ItemStack s = this.mc.player.getInventory().getStack(i);
         if (s.getItem() == Items.TOTEM_OF_UNDYING) {
            totemCount += s.getCount();
         }
      }

      String text = "x" + totemCount;
      float iconSize = 16.0F;
      float textSize = 7.0F;
      float gap = 2.0F;
      float textW = Fonts.SFMEDIUM.get().getWidth(text, textSize);
      float width = iconSize + gap + textW;
      this.n29w5(context, new ItemStack(Items.TOTEM_OF_UNDYING), posX, posY + (iconSize - iconSize) / 2.0F, iconSize, 255);
      DrawUtil.drawText(
         Fonts.SFMEDIUM.get(),
         text,
         posX + iconSize + gap,
         posY + (iconSize - textSize) / 2.0F - 0.5F,
         ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), 255),
         textSize
      );
      this.dzLPe.setWidth(width);
      this.dzLPe.setHeight(iconSize);
   }

   private void x7c7RgY(DrawContext context) {
      if (this.mc.player != null) {
         if (!(this.mc.currentScreen instanceof ChatScreen)) {
            this.z436cbx.open = false;
            this.z436cbx.draggingSlider = null;
         }

         this.eemFuA(this.z436cbx, context);
         this.iEb9b(context);
         this.sao7CKx(this.z436cbx, context);
         this.z1TvB0(this.z436cbx, context);
      }
   }

   private void iEb9b(DrawContext context) {
      long nowMs = System.currentTimeMillis();
      if (nowMs - this.gGv534P >= 500L) {
         this.gGv534P = nowMs;
         this.update();
      }

      float posX = this.a6JkFr8.getX();
      float posY = this.a6JkFr8.getY();
      float headerHeight = 14.0F;
      float itemHeight = 11.5F;
      float minWidth = 52.0F;
      float padX = 5.0F;
      float padY = 2.0F;
      float statusBoxW = Fonts.SFMEDIUM.get().getWidth("Vanish", 7.0F);

      for (Interface.Staff staff : this.imz8r) {
         staff.animation.run(staff.isOnServer ? 1.0F : 0.0F);
      }

      boolean isFound = false;

      for (Interface.Staff staff : this.imz8r) {
         if (staff.animation.getValue() > 0.001F) {
            isFound = true;
         }
      }

      if (!isFound && !(this.mc.currentScreen instanceof ChatScreen)) {
         this.u0hqCA.run(0.0F);
      } else {
         this.u0hqCA.run(1.0F);
      }

      float globalAlpha = this.u0hqCA.getValue();
      if (!(globalAlpha <= 0.05F)) {
         int headerAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * globalAlpha));
         boolean showExample = this.mc.currentScreen instanceof ChatScreen && !isFound;
         float maxNameBoxW = minWidth;

         for (Interface.Staff staffx : this.imz8r) {
            if (staffx.animation.getValue() > 0.001F) {
               float nw = 11.0F + Fonts.SFMEDIUM.get().getWidth(staffx.prefix, 7.0F) + 7.0F;
               if (nw > maxNameBoxW) {
                  maxNameBoxW = nw;
               }
            }
         }

         if (showExample) {
            float nw = 11.0F + Fonts.SFMEDIUM.get().getWidth("Example", 7.0F) + 7.0F;
            if (nw > maxNameBoxW) {
               maxNameBoxW = nw;
            }
         }

         float contentHeight = 0.0F;
         if (showExample) {
            contentHeight = itemHeight;
         } else {
            for (Interface.Staff staffxx : this.imz8r) {
               float av = staffxx.animation.getValue();
               if (!(av <= 0.001F)) {
                  contentHeight += itemHeight * av;
               }
            }
         }

         float rawWidth = maxNameBoxW + statusBoxW + padX * 2.0F;
         this.z436cbx.panelWidth.run(rawWidth);
         float totalRowWidth = this.z436cbx.panelWidth.getValue();
         if (totalRowWidth < 20.0F) {
            totalRowWidth = rawWidth;
         }

         float totalHeight = headerHeight + contentHeight + padY * 2.0F;
         this.q4cfy(this.z436cbx, posX, posY, totalRowWidth, totalHeight, 6.0F, globalAlpha);
         float statusCX = posX + totalRowWidth - padX - statusBoxW / 2.0F;
         float headerIconW = Fonts.ALPHADLC.get().getWidth("w", 9.0F);
         this.qm7dp(
            Fonts.SFMEDIUM.get(),
            "Staff online",
            posX + padX + 4.0F,
            posY + padY + 2.5F,
            headerAlpha,
            8.0F
         );
         DrawUtil.drawText(
            Fonts.ALPHADLC.get(),
            "w",
            posX + totalRowWidth - padX - headerIconW + 0.75F,
            posY + padY + 3.0F,
            ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), headerAlpha),
            9.0F
         );
         float curY = posY + headerHeight + padY;

         for (Interface.Staff staffxxx : this.imz8r) {
            float animVal = staffxxx.animation.getValue();
            if (!(animVal <= 0.001F)) {
               int itemAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * animVal * globalAlpha));
               if (itemAlpha >= 5) {
                  float rowHeight = itemHeight * animVal;
                  context.getMatrices().push();
                  context.getMatrices().translate(posX + totalRowWidth / 2.0F, curY + rowHeight / 2.0F, 0.0F);
                  context.getMatrices().scale(animVal, animVal, animVal);
                  context.getMatrices().translate(-(posX + totalRowWidth / 2.0F), -(curY + rowHeight / 2.0F), 0.0F);
                  PlayerListEntry playerEntry = this.mc.getNetworkHandler().getPlayerListEntry(staffxxx.name);
                  Identifier skinTexture;
                  if (playerEntry != null) {
                     skinTexture = playerEntry.getSkinTextures().texture();
                  } else {
                     skinTexture = DefaultSkinHelper.getTexture();
                  }

                  int textureId = this.mc.getTextureManager().getTexture(skinTexture).getGlId();
                  float headSize = 8.0F;
                  Builder.texture()
                     .size(new SizeState(headSize, headSize))
                     .radius(new QuadRadiusState(2.0F))
                     .color(new QuadColorState(ColorProvider.setAlpha(-1, itemAlpha)))
                     .texture(0.125F, 0.125F, 0.125F, 0.125F, textureId)
                     .smoothness(1.0F)
                     .build()
                     .render(context.getMatrices().peek().getPositionMatrix(), posX + padX + 1.0F, curY + (itemHeight - headSize) / 2.0F);
                  DrawUtil.drawText(Fonts.SFMEDIUM.get(), staffxxx.prefix, posX + padX + 10.0F, curY + (itemHeight - 7.0F) / 2.0F - 0.35F, 7.0F, itemAlpha);
                  String statusText = staffxxx.status == Interface.Status.VANISHED ? "Vanish" : "Active";
                  int statusColor = staffxxx.status == Interface.Status.VANISHED
                     ? ColorProvider.rgba(255, 60, 60, (float)itemAlpha)
                     : ColorProvider.rgba(60, 255, 60, (float)itemAlpha);
                  float statusW = Fonts.SFMEDIUM.get().getWidth(statusText, 7.0F);
                  DrawUtil.drawText(
                     Fonts.SFMEDIUM.get(),
                     statusText,
                     posX + totalRowWidth - padX - statusW + 0.75F,
                     curY + (itemHeight - 7.0F) / 2.0F - 0.35F,
                     statusColor,
                     7.0F
                  );
                  context.getMatrices().pop();
                  curY += rowHeight;
               }
            }
         }

         if (showExample) {
            int textureId = this.mc.getTextureManager().getTexture(DefaultSkinHelper.getTexture()).getGlId();
            float headSize = 8.0F;
            Builder.texture()
               .size(new SizeState(headSize, headSize))
               .radius(new QuadRadiusState(2.0F))
               .color(new QuadColorState(ColorProvider.setAlpha(-1, headerAlpha)))
               .texture(0.125F, 0.125F, 0.125F, 0.125F, textureId)
               .smoothness(1.0F)
               .build()
               .render(context.getMatrices().peek().getPositionMatrix(), posX + padX + 1.0F, curY + (itemHeight - headSize) / 2.0F);
            DrawUtil.drawText(
               Fonts.SFMEDIUM.get(),
               "Example",
               posX + padX + 10.0F,
               curY + (itemHeight - 7.0F) / 2.0F - 0.35F,
               ColorProvider.rgba(255, 255, 255, (float)headerAlpha),
               7.0F
            );
            float statusW = Fonts.SFMEDIUM.get().getWidth("Active", 7.0F);
            DrawUtil.drawText(
               Fonts.SFMEDIUM.get(),
               "Active",
               posX + totalRowWidth - padX - statusW + 0.75F,
               curY + (itemHeight - 7.0F) / 2.0F - 0.35F,
               ColorProvider.rgba(60, 255, 60, (float)headerAlpha),
               7.0F
            );
         }

         this.a6JkFr8.setWidth(totalRowWidth);
         this.a6JkFr8.setHeight(totalHeight);
      }
   }

   public boolean isModuleStateNotifEnabled() {
      return this.jBvFMv.isEnabled("Нотификации")
         && this.c2W8w.getValue();
   }

   public boolean isTotemNotifEnabled() {
      return this.jBvFMv.isEnabled("Нотификации")
         && this.q3Md.getValue();
   }

   private List<Interface.PopupRow> hw1bm7p(Interface.HudPopup p) {
      List<Interface.PopupRow> rows = new ArrayList<>();
      rows.add(new Interface.PopupRow(Interface.PopupKind.SLIDER, "Размер", null, p.size));
      rows.add(
         new Interface.PopupRow(
            Interface.PopupKind.SLIDER,
            "Прозрачность",
            null,
            p.alpha
         )
      );
      if (p.ring != null) {
         rows.add(
            new Interface.PopupRow(
               Interface.PopupKind.TOGGLE,
               "Кольцо таймер",
               p.ring,
               null
            )
         );
      }

      rows.addAll(p.extraRows);
      return rows;
   }

   private double zletaz() {
      return this.mc.mouse.getX() / this.mc.getWindow().getScaleFactor();
   }

   private double iM3p2ou() {
      return this.mc.mouse.getY() / this.mc.getWindow().getScaleFactor();
   }

   private void x5ne9(DrawContext context) {
      if (this.mc.player != null) {
         boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
         if (!chatOpen) {
            this.s07r.open = false;
            this.s07r.draggingSlider = null;
         }

         this.eemFuA(this.s07r, context);
         this.kwr0(context);
         this.sao7CKx(this.s07r, context);
         this.z1TvB0(this.s07r, context);
      }
   }

   private void eemFuA(Interface.HudPopup p, DrawContext context) {
      float size = p.size.getFloatValue();
      float angle = p.drag.getWobbleAngle();
      boolean needScale = Math.abs(size - 1.0F) > 0.001F;
      boolean needRot = Math.abs(angle) > 0.01F;
      p.transformed = needScale || needRot;
      if (p.transformed) {
         float ox = p.drag.getX();
         float oy = p.drag.getY();
         float cx = ox + p.drag.getWidth() / 2.0F;
         float cy = oy + p.drag.getHeight() / 2.0F;
         if (needScale) {
            IRenderer.DEFAULT_MATRIX.identity().translate(ox, oy, 0.0F).scale(size, size, 1.0F).translate(-ox, -oy, 0.0F);
         }

         context.getMatrices().push();
         if (needScale) {
            context.getMatrices().translate(ox, oy, 0.0F);
            context.getMatrices().scale(size, size, 1.0F);
            context.getMatrices().translate(-ox, -oy, 0.0F);
         }

         if (needRot) {
            context.getMatrices().translate(cx, cy, 0.0F);
            context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
            context.getMatrices().translate(-cx, -cy, 0.0F);
         }
      }
   }

   private void sao7CKx(Interface.HudPopup p, DrawContext context) {
      if (p.transformed) {
         context.getMatrices().pop();
         IRenderer.DEFAULT_MATRIX.identity();
         float size = p.size.getFloatValue();
         if (Math.abs(size - 1.0F) > 0.001F) {
            p.drag.setWidth(p.drag.getWidth() * size);
            p.drag.setHeight(p.drag.getHeight() * size);
         }
      }
   }

   private void z1TvB0(Interface.HudPopup p, DrawContext context) {
      p.anim.run(p.open ? 1.0F : 0.0F);
      if (p.anim.getValue() > 0.01F) {
         this.rqUa(p, context);
      }
   }

   private void j4995qd(float x, float y, float w, float h, float radius, int alpha, boolean animated) {
      alpha = Math.min(255, Math.max(0, alpha));
      if (alpha > 0) {
         int bg = ColorProvider.getColorWindowBg();
         int accent = ColorProvider.getColorVisualModules();
         DrawUtil.drawRound(x - 2.0F, y - 2.0F, w + 4.0F, h + 4.0F, radius + 1.5F, ColorProvider.setAlpha(accent, alpha * 0.07));
         DrawUtil.drawRound(x - 0.75F, y - 0.75F, w + 1.5F, h + 1.5F, radius + 0.75F, ColorProvider.setAlpha(accent, alpha * 0.38));
         DrawUtil.drawRound(
            x, y, w, h, radius, ColorProvider.setAlpha(bg, alpha), ColorProvider.setAlpha(ColorProvider.interpolateColor(bg, accent, 0.13F), alpha)
         );
         DrawUtil.drawRound(x + Math.min(radius, 6.0F), y + 1.0F, Math.max(4.0F, w - 18.0F), 1.5F, 0.75F, ColorProvider.setAlpha(-1, alpha * 0.08));
         if (animated && w > 48.0F) {
            float t = (float)(System.currentTimeMillis() % 2600L) / 2600.0F;
            float sx = x + 4.0F + t * (w - 40.0F);
            float sa = (float)Math.sin(t * Math.PI);
            DrawUtil.drawRound(sx, y + 0.4F, 32.0F, 1.1F, 0.55F, ColorProvider.setAlpha(-1, alpha * (0.05 + 0.14 * sa)));
         }
      }
   }

   public void drawElementBackground(float x, float y, float w, float h, float radius, int alpha) {
      alpha = Math.min(255, Math.max(0, alpha));
      if (this.b72q45.is("Шейдер")) {
         this.jgyvr6o(x, y, w, h, radius, alpha);
      } else if (this.b72q45.is("Liquid Glass")) {
         this.apkZ3(x, y, w, h, radius, alpha);
      } else {
         this.j4995qd(x, y, w, h, radius, alpha, true);
      }
   }

   private void q4cfy(Interface.HudPopup p, float x, float y, float w, float h, float radius, float alphaFactor) {
      float clampFactor = MathHelper.clamp(alphaFactor, 0.0F, 1.0F);
      int alpha = (int)(p.alpha.getIntValue() * clampFactor);
      this.drawElementBackground(x, y, w, h, radius, alpha);
   }

   private void jgyvr6o(float x, float y, float w, float h, float radius, int alpha) {
      int accent = ThemeManager.getInstance().getCurrentTheme().getColorFirst();
      ClickGuiBackgroundRenderer.render(this.feack.getValue(), accent, this.s3bfxgy.getFloatValue(), x, y, w + 0.5F, h + 0.25F, radius);
      int overlay = (int)(alpha * 0.55F);
      overlay = Math.min(255, Math.max(0, overlay));
      DrawUtil.drawRound(x, y, w, h, radius, ColorProvider.setAlpha(ColorProvider.getColorWindowBg(), overlay));
   }

   private void apkZ3(float x, float y, float w, float h, float radius, int alpha) {
      this.mtYnbnm(x, y, w, h, new Vector4f(radius, radius, radius, radius), alpha);
   }

   private void mtYnbnm(float x, float y, float w, float h, Vector4f radii, int alpha) {
      alpha = Math.min(255, Math.max(0, alpha));
      if (alpha > 0) {
         int accent = ColorProvider.getColorVisualModules();
         DrawUtil.drawRoundBlur(
            x, y, w, h, radii, ColorProvider.setAlpha(ColorProvider.getColorWindowBg(), (int)(alpha * this.mCB3y.getFloatValue())), this.evYwr1.getFloatValue()
         );
         DrawUtil.drawRound(x, y, w, h, radii, ColorProvider.setAlpha(accent, (int)(alpha * 0.08)));
         DrawUtil.drawRound(x + Math.min(radii.x, 6.0F), y + 1.0F, Math.max(4.0F, w - 18.0F), 1.5F, 0.75F, ColorProvider.setAlpha(-1, (int)(alpha * 0.1)));
      }
   }

   private void kwr0(DrawContext context) {
      if (this.mc.player != null) {
         float posX = this.or0d.getX();
         float posY = this.or0d.getY();
         float headerHeight = 14.0F;
         float itemHeight = 12.0F;
         float minWidth = 52.0F;
         float padX = 5.0F;
         float padY = 2.0F;
         boolean isFound = false;

         for (Interface.PotionItem item : this.v42Pbh1) {
            item.animation.run(item.active ? 1.0F : 0.0F);
            item.rowAnim.run(item.active ? 1.0F : 0.0F);
            if (item.animation.getValue() > 0.001F) {
               isFound = true;
            }
         }

         if (!isFound && !(this.mc.currentScreen instanceof ChatScreen)) {
            this.tPlp4sF.run(0.0F);
         } else {
            this.tPlp4sF.run(1.0F);
         }

         float globalAlpha = this.tPlp4sF.getValue();
         if (!(globalAlpha <= 0.05F)) {
            int headerAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * globalAlpha));
            boolean showExample = this.mc.currentScreen instanceof ChatScreen && !isFound;
            if (showExample) {
               long currentTime = System.currentTimeMillis();
               if (currentTime - this.yB14jv > 2000L) {
                  if (eA8s == null) {
                     eA8s = new ArrayList<>();
                     Registries.STATUS_EFFECT.streamEntries().forEach(eA8s::add);
                  }

                  if (!eA8s.isEmpty()) {
                     this.fAqm = eA8s.get(RANDOM.nextInt(eA8s.size()));
                     this.yB14jv = currentTime;
                  }
               }
            }

            float maxNameBoxW = minWidth;
            float maxTimeBoxW = 0.0F;
            float contentHeight = 0.0F;

            for (Interface.PotionItem itemx : this.v42Pbh1) {
               float animVal = itemx.animation.getValue();
               if (animVal > 0.001F) {
                  contentHeight += itemHeight * itemx.rowAnim.getValue();
                  String lvlStr = itemx.amplifier >= 1 ? "LVL " + (itemx.amplifier + 1) : "";
                  float nameW = Fonts.SFMEDIUM.get().getWidth(itemx.name, 7.0F);
                  float lvlW = lvlStr.isEmpty() ? 0.0F : Fonts.SFMEDIUM.get().getWidth(lvlStr, 6.0F);
                  float nw = nameW + lvlW + 8.0F + 10.0F + 10.0F;
                  if (nw > maxNameBoxW) {
                     maxNameBoxW = nw;
                  }

                  int seconds = itemx.durationTicks / 20;
                  int minutes = seconds / 60;
                  int sec = seconds % 60;
                  String timeStr = String.format("%d:%02d", minutes, sec);
                  float tw = Fonts.SFMEDIUM.get().getWidth(timeStr, 6.75F) + 10.0F;
                  if (tw > maxTimeBoxW) {
                     maxTimeBoxW = tw;
                  }
               }
            }

            if (showExample) {
               float exNameW = Fonts.SFMEDIUM.get().getWidth("Example", 7.0F);
               if (exNameW + 8.0F + 10.0F + 10.0F > maxNameBoxW) {
                  maxNameBoxW = exNameW + 8.0F + 10.0F + 10.0F;
               }

               float exTimeW = Fonts.SFMEDIUM.get().getWidth("**:**", 6.75F) + 10.0F;
               if (exTimeW > maxTimeBoxW) {
                  maxTimeBoxW = exTimeW;
               }

               contentHeight = itemHeight;
            }

            float rawWidth = maxNameBoxW + maxTimeBoxW + padX * 2.0F;
            this.s07r.panelWidth.run(rawWidth);
            float totalRowWidth = this.s07r.panelWidth.getValue();
            if (totalRowWidth < 20.0F) {
               totalRowWidth = rawWidth;
            }

            float totalHeight = headerHeight + contentHeight + padY * 2.0F;
            this.q4cfy(this.s07r, posX, posY, totalRowWidth, totalHeight, 6.0F, globalAlpha);
            this.qm7dp(Fonts.SFMEDIUM.get(), "Potions", posX + padX + 4.0F, posY + padY + 2.5F, headerAlpha, 8.0F);
            DrawUtil.drawText(
               Fonts.ALPHADLC.get(),
               "f",
               posX + totalRowWidth - padX - 11.0F,
               posY + padY + 3.0F,
               ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), headerAlpha),
               9.0F
            );
            float curY = posY + headerHeight + padY;

            for (Interface.PotionItem itemxx : this.v42Pbh1) {
               float animVal = itemxx.animation.getValue();
               if (!(animVal <= 0.001F)) {
                  float rowAnimVal = itemxx.rowAnim.getValue();
                  if (!(rowAnimVal <= 0.001F)) {
                     int seconds = itemxx.durationTicks / 20;
                     int minutes = seconds / 60;
                     int sec = seconds % 60;
                     String timeStr = String.format("%d:%02d", minutes, sec);
                     String nameStr = itemxx.name;
                     boolean isHarmful = !itemxx.effect.value().isBeneficial();
                     String effectId = Registries.STATUS_EFFECT.getId(itemxx.effect.value()).getPath();
                     boolean isNightVision = effectId.equals("night_vision");
                     int textAlpha = 255;
                     if (itemxx.durationTicks <= 200 && itemxx.durationTicks > 0 && !isNightVision) {
                        double output = 0.5 + 0.5 * Math.cos((Math.PI * 2) * (System.currentTimeMillis() % 700L) / 700.0);
                        textAlpha = (int)(100.0 + 155.0 * output);
                     } else if (itemxx.durationTicks == 0) {
                        textAlpha = 0;
                     }

                     int itemAlpha = (int)Math.min(255.0F, Math.max(0.0F, textAlpha * animVal * globalAlpha));
                     if (itemAlpha >= 5) {
                        float nameWx = Fonts.SFMEDIUM.get().getWidth(nameStr, 7.0F);
                        String lvlStrx = itemxx.amplifier >= 1 ? "LVL " + (itemxx.amplifier + 1) : "";
                        if (lvlStrx.isEmpty()) {
                           float var10000 = 0.0F;
                        } else {
                           Fonts.SFMEDIUM.get().getWidth(lvlStrx, 6.0F);
                        }

                        float timeW = Fonts.SFMEDIUM.get().getWidth(timeStr, 6.75F);
                        float timeBoxW = timeW + 10.0F;
                        float rowHeight = itemHeight * rowAnimVal;
                        context.getMatrices().push();
                        context.getMatrices().translate(posX + totalRowWidth / 2.0F, curY + rowHeight / 2.0F, 0.0F);
                        context.getMatrices().scale(rowAnimVal, rowAnimVal, rowAnimVal);
                        context.getMatrices().translate(-(posX + totalRowWidth / 2.0F), -(curY + rowHeight / 2.0F), 0.0F);
                        float potionMid = kRb60o2(curY + 2.5F, 7.0F);
                        Sprite sprite = this.mc.getStatusEffectSpriteManager().getSprite(itemxx.effect);
                        if (sprite != null) {
                           float iconSize = 8.0F;
                           float iconX = posX + padX + 2.0F;
                           float iconY = potionMid - iconSize / 2.0F;
                           int color = itemAlpha << 24 | 16777215;
                           RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, itemAlpha / 255.0F);
                           context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, (int)iconX, (int)iconY, (int)iconSize, (int)iconSize, color);
                           RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                        }

                        float dotX = posX + padX + 11.0F;
                        float dotY = potionMid - 1.5F;
                        DrawUtil.drawRound(dotX, dotY, 3.0F, 3.0F, 1.5F, ColorProvider.setAlpha(ColorProvider.getColorClient(), itemAlpha));
                        int nameColor = isHarmful ? ColorProvider.rgba(255, 80, 80, (float)itemAlpha) : ColorProvider.rgba(255, 255, 255, (float)itemAlpha);
                        float textX = posX + padX + 18.0F;
                        DrawUtil.drawText(Fonts.SFMEDIUM.get(), nameStr, textX, curY + 2.5F, nameColor, 7.0F);
                        if (!lvlStrx.isEmpty()) {
                           float lvlX = textX + nameWx + 6.0F;
                           DrawUtil.drawText(
                              Fonts.SFMEDIUM.get(),
                              lvlStrx,
                              lvlX,
                              curY + 3.5F,
                              ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), (int)(itemAlpha * 0.7F)),
                              6.0F
                           );
                        }

                        float timerX = posX + totalRowWidth - padX - timeBoxW;
                        String timerKey = "potions_duration_" + effectId + "_" + itemxx.amplifier;
                        TimerTextAnimator.draw(
                           Fonts.SFMEDIUM.get(),
                           timerKey,
                           timeStr,
                           timerX + (timeBoxW - timeW) / 2.0F + 1.0F,
                           curY + 2.5F,
                           ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), itemAlpha),
                           6.75F
                        );
                        context.getMatrices().pop();
                        curY += rowHeight;
                     }
                  }
               }
            }

            if (showExample && this.fAqm != null) {
               String nameStrx = "Example";
               String timeStrx = "**:**";
               float nameWxx = Fonts.SFMEDIUM.get().getWidth(nameStrx, 7.0F);
               float timeWx = Fonts.SFMEDIUM.get().getWidth(timeStrx, 6.75F);
               float timeBoxWx = timeWx + 10.0F;
               float exMid = kRb60o2(curY + 2.5F, 7.0F);
               Sprite spritex = this.mc.getStatusEffectSpriteManager().getSprite(this.fAqm);
               if (spritex != null) {
                  float iconSize = 8.0F;
                  float iconX = posX + padX + 2.0F;
                  float iconY = exMid - iconSize / 2.0F;
                  int color = headerAlpha << 24 | 16777215;
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, headerAlpha / 255.0F);
                  context.drawSpriteStretched(RenderLayer::getGuiTextured, spritex, (int)iconX, (int)iconY, (int)iconSize, (int)iconSize, color);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               }

               float dotX = posX + padX + 11.0F;
               float dotY = exMid - 1.5F;
               DrawUtil.drawRound(dotX, dotY, 3.0F, 3.0F, 1.5F, ColorProvider.setAlpha(ColorProvider.getColorClient(), headerAlpha));
               float textX = posX + padX + 18.0F;
               DrawUtil.drawText(Fonts.SFMEDIUM.get(), nameStrx, textX, curY + 2.5F, ColorProvider.rgba(255, 255, 255, (float)headerAlpha), 7.0F);
               float timerX = posX + totalRowWidth - padX - timeBoxWx;
               DrawUtil.drawText(
                  Fonts.SFMEDIUM.get(),
                  timeStrx,
                  timerX + (timeBoxWx - timeWx) / 2.0F + 1.0F,
                  curY + 2.5F,
                  ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), headerAlpha),
                  6.75F
               );
            }

            this.or0d.setWidth(totalRowWidth);
            this.or0d.setHeight(totalHeight);
         }
      }
   }

   private void rqUa(Interface.HudPopup p, DrawContext context) {
      List<Interface.PopupRow> rows = this.hw1bm7p(p);
      float w = 96.0F;

      for (Interface.PopupRow r : rows) {
         float lw = Fonts.SFMEDIUM.get().getWidth(r.label, 6.75F);
         float need = lw + 12.0F + (r.kind == Interface.PopupKind.TOGGLE ? 22.0F : 34.0F);
         if (need > w) {
            w = need;
         }
      }

      float totalH = 19.0F;

      for (Interface.PopupRow rx : rows) {
         totalH += rx.kind == Interface.PopupKind.TOGGLE ? 14.0F : 19.0F;
      }

      float ex = p.drag.getX();
      float ey = p.drag.getY();
      float ew = p.drag.getWidth();
      float screenW = this.mc.getWindow().getScaledWidth();
      float screenH = this.mc.getWindow().getScaledHeight();
      float x = p.title.equals("CustomHotbar") ? ex - w - 4.0F : ex + ew + 4.0F;
      x = MathHelper.clamp(x, 2.0F, Math.max(2.0F, screenW - w - 2.0F));
      float y = MathHelper.clamp(ey, 2.0F, Math.max(2.0F, screenH - totalH - 2.0F));
      p.px = x;
      p.py = y;
      p.pw = w;
      p.ph = totalH;
      float anim = p.anim.getValue();
      int a = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * anim));
      this.drawBackground(x, y, w, totalH, 4.0F, a);
      this.qm7dp(
         Fonts.SFMEDIUM.get(), "Настройки", x + 6.0F, y + 4.5F, a, 7.5F
      );
      DrawUtil.drawRound(x + 5.0F, y + 15.0F - 1.5F, w - 10.0F, 0.5F, 0.0F, ColorProvider.setAlpha(ColorProvider.getColorClient(), a));
      float cy = y + 15.0F;

      for (Interface.PopupRow rx : rows) {
         rx.x = x;
         rx.y = cy;
         rx.w = w;
         rx.h = rx.kind == Interface.PopupKind.TOGGLE ? 14.0F : 19.0F;
         if (rx.kind == Interface.PopupKind.TOGGLE) {
            this.tYykR(rx.label, rx.bool, rx.x, rx.y, rx.w, rx.h, a);
         } else {
            this.gvBL9G(rx.label, rx.slider, rx.x, rx.y, rx.w, rx.h, a);
         }

         cy += rx.h;
      }

      p.rendered.clear();
      p.rendered.addAll(rows);
      if (p.draggingSlider != null) {
         double val = (this.zletaz() - p.trackX) / p.trackW * (p.draggingSlider.getMax() - p.draggingSlider.getMin()) + p.draggingSlider.getMin();
         double stepped = Math.round(val / p.draggingSlider.getStep()) * p.draggingSlider.getStep();
         p.draggingSlider.setValue(stepped);
      }
   }

   private void tYykR(String label, BooleanSetting setting, float x, float y, float w, float h, int a) {
      setting.getAnimation().run(setting.getValue());
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), label, x + 6.0F, y + h / 2.0F - 3.25F, ColorProvider.rgba(255, 255, 255, (float)a), 6.75F);
      float toggleW = 15.0F;
      float toggleH = 8.0F;
      float toggleX = x + w - toggleW - 6.0F;
      float toggleY = y + (h - toggleH) / 2.0F;
      float tAnim = setting.getAnimation().getValue();
      int inactive = ColorProvider.setAlpha(ColorProvider.getColorInactiveIndicator(), a);
      int active = ColorProvider.setAlpha(ColorProvider.getColorIndicator(), a);
      int bg = ColorProvider.interpolateColor(inactive, active, tAnim);
      DrawUtil.drawRound(toggleX, toggleY, toggleW, toggleH, toggleH / 2.0F, bg);
      float knob = toggleH - 1.0F;
      float knobMinX = toggleX + 0.5F;
      float knobMaxX = toggleX + toggleW - knob - 0.5F;
      float knobX = knobMinX + (knobMaxX - knobMinX) * tAnim;
      DrawUtil.drawCircle(knobX + knob / 2.0F, toggleY + 0.5F + knob / 2.0F, knob / 2.0F, ColorProvider.setAlpha(ColorProvider.getColorSliderCircle(), a));
   }

   private String qmnzxc(SliderSetting s) {
      return s.getStep() < 1.0
         ? String.format(Locale.US, "%.2f", s.getValue())
         : String.valueOf(s.getIntValue());
   }

   private void gvBL9G(String label, SliderSetting setting, float x, float y, float w, float h, int a) {
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), label, x + 6.0F, y + 2.5F, ColorProvider.rgba(255, 255, 255, (float)a), 6.5F);
      String valStr = this.qmnzxc(setting);
      float valW = Fonts.SFMEDIUM.get().getWidth(valStr, 6.5F);
      DrawUtil.drawText(Fonts.SFMEDIUM.get(), valStr, x + w - 6.0F - valW, y + 2.5F, ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), a), 6.5F);
      float trackX = x + 6.0F;
      float trackW = w - 12.0F;
      float trackY = y + h - 5.5F;
      DrawUtil.drawRound(trackX, trackY, trackW, 3.0F, 1.0F, ColorProvider.setAlpha(ColorProvider.getColorSliderWindow(), a));
      float fill = (float)(trackW * (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin()));
      fill = MathHelper.clamp(fill, 0.0F, trackW);
      DrawUtil.drawRound(trackX, trackY, fill, 3.0F, 1.0F, ColorProvider.setAlpha(ColorProvider.getColorSlider(), a));
      float circleX = trackX + fill;
      DrawUtil.drawRound(circleX - 2.5F, trackY - 1.0F, 5.0F, 5.0F, 1.75F, ColorProvider.setAlpha(ColorProvider.getColorSliderCircle(), a));
   }

   public boolean isPotionsActive() {
      return this.isEnabled() && this.jBvFMv.isEnabled("Бафы");
   }

   private int ryuY6u() {
      try {
         InGameHudAccessor hud = (InGameHudAccessor)this.mc.inGameHud;
         return ((BossBarHudAccessor)hud.vio_getBossBarHud()).vio_getBossBars().size();
      } catch (Exception var2) {
         return 0;
      }
   }

   public boolean isCustomHotbarActive() {
      return this.isEnabled()
         && this.jBvFMv
            .isEnabled(
               "Кастомный хотбар"
            );
   }

   public boolean handlePotionsClick(double mouseX, double mouseY, int button) {
      if (this.isEnabled() && this.mc.currentScreen instanceof ChatScreen) {
         if (this.jBvFMv.isEnabled("Бафы") && this.ojUBZ7O(this.s07r, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv.isEnabled("КулДауны")
            && this.ojUBZ7O(this.i6tb, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv.isEnabled("ServerHelper") && this.ojUBZ7O(this.t9e3F, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv
               .isEnabled(
                  D.k(
                     new int[]{1109, 1122, 1269, 1220, 1029, 1045, 1277, 1227, 1143, 1129, 1272, 214, 1142, 1052, 1273, 1205, 1137, 1050},
                     new int[]{74, 34, 205, 246}
                  )
               )
            && this.ojUBZ7O(this.nwWANcB, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv
               .isEnabled(
                  D.k(
                     new int[]{1136, 1136, 1031, 1081, 1106, 1143, 1038, 1076, 64, 1142, 1147, 1077, 1109, 1034, 1141, 1091, 1118, 1034, 1038},
                     new int[]{96, 74, 69, 1}
                  )
               )
            && this.ojUBZ7O(this.z436cbx, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv.isEnabled("Ватермарка")
            && this.ojUBZ7O(this.eaunw, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv.isEnabled("Логотип")
            && this.ojUBZ7O(this.gzTbtan, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv.isEnabled("Инфо")
            && this.ojUBZ7O(this.v1iE, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv.isEnabled("Нотификации")
            && this.ojUBZ7O(this.l2rzcE, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv
               .isEnabled("Активный таргет")
            && this.ojUBZ7O(this.azttZ, mouseX, mouseY, button)) {
            return true;
         } else if (this.jBvFMv
               .isEnabled(
                  "Кастомный хотбар"
               )
            && this.ojUBZ7O(this.l96dNP, mouseX, mouseY, button)) {
            return true;
         } else {
            return this.jBvFMv.isEnabled("Броня")
                  && this.ojUBZ7O(this.nb22ru, mouseX, mouseY, button)
               ? true
               : this.jBvFMv
                     .isEnabled("Полоса тотемов")
                  && this.ojUBZ7O(this.sexFA0, mouseX, mouseY, button);
         }
      } else {
         return false;
      }
   }

   private boolean ojUBZ7O(Interface.HudPopup p, double mouseX, double mouseY, int button) {
      if (p.open && p.anim.getValue() > 0.5F) {
         if (button == 0) {
            for (Interface.PopupRow r : p.rendered) {
               if (HoverUtil.isHovered(mouseX, mouseY, r.x, r.y, r.w, r.h)) {
                  if (r.kind == Interface.PopupKind.TOGGLE) {
                     r.bool.toggle();
                  } else {
                     p.draggingSlider = r.slider;
                     p.trackX = r.x + 6.0F;
                     p.trackW = r.w - 12.0F;
                     double val = (mouseX - p.trackX) / p.trackW * (r.slider.getMax() - r.slider.getMin()) + r.slider.getMin();
                     double stepped = Math.round(val / r.slider.getStep()) * r.slider.getStep();
                     r.slider.setValue(stepped);
                  }

                  return true;
               }
            }
         }

         if (HoverUtil.isHovered(mouseX, mouseY, p.px, p.py, p.pw, p.ph)) {
            return true;
         }
      }

      if (button == 1 && p.drag.isHovering()) {
         p.open = !p.open;
         return true;
      } else {
         if (p.open && !p.drag.isHovering()) {
            p.open = false;
         }

         return false;
      }
   }

   public void handlePotionsRelease(int button) {
      if (button == 0) {
         this.s07r.draggingSlider = null;
         this.i6tb.draggingSlider = null;
         this.t9e3F.draggingSlider = null;
         this.nwWANcB.draggingSlider = null;
         this.z436cbx.draggingSlider = null;
         this.eaunw.draggingSlider = null;
         this.gzTbtan.draggingSlider = null;
         this.v1iE.draggingSlider = null;
         this.l2rzcE.draggingSlider = null;
         this.azttZ.draggingSlider = null;
         this.l96dNP.draggingSlider = null;
         this.nb22ru.draggingSlider = null;
         this.sexFA0.draggingSlider = null;
      }
   }

   private void fz30Tm(DrawContext context) {
      if (this.mc.player != null) {
         boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
         if (!chatOpen) {
            this.azttZ.open = false;
            this.azttZ.draggingSlider = null;
         }

         this.eemFuA(this.azttZ, context);
         this.jk49(context);
         this.sao7CKx(this.azttZ, context);
         this.z1TvB0(this.azttZ, context);
      }
   }

   private void jk49(DrawContext context) {
      boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
      KillAura killAura = Instance.get(KillAura.class);
      LivingEntity target = null;
      if (chatOpen) {
         target = this.mc.player;
      } else if (killAura.isEnabled() && killAura.getTarget() != null && killAura.getTarget().isAlive()) {
         target = killAura.getTarget();
      } else if (this.mqh3.getValue()) {
         if (this.mc.targetedEntity instanceof LivingEntity living && living.isAlive()) {
            target = living;
         } else if (this.mc.player != null && this.mc.world != null) {
            Entity cam = (Entity)(this.mc.cameraEntity != null ? this.mc.cameraEntity : this.mc.player);
            long nowMs = System.currentTimeMillis();
            if (nowMs - this.cpytB >= 120L) {
               this.cpytB = nowMs;
               double range = 8.0;
               Vec3d cameraVec = cam.getCameraPosVec(1.0F);
               Vec3d rotationVec = cam.getRotationVec(1.0F);
               Vec3d end = cameraVec.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
               Box box = cam.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);
               EntityHitResult hit = ProjectileUtil.raycast(
                  cam, cameraVec, end, box, ent -> !ent.isSpectator() && ent != this.mc.player && ent instanceof LivingEntity le && le.isAlive(), range * range
               );
               this.ur2BeWr = hit != null && hit.getEntity() instanceof LivingEntity le && le.isAlive() ? le : null;
            }

            target = this.ur2BeWr;
         }
      }

      if (target != null) {
         this.loqITZz = target;
         this.obkH.run(1.0F);
         this.faiI.run(1.0F);
      } else {
         this.obkH.run(0.0F);
         this.faiI.run(0.0F);
      }

      if (!(this.obkH.getValue() <= 0.05F) && this.loqITZz != null && this.loqITZz instanceof LivingEntity livingEntity) {
         AbstractClientPlayerEntity playerEntity = this.loqITZz instanceof AbstractClientPlayerEntity ? (AbstractClientPlayerEntity)this.loqITZz : null;
         float anim = this.obkH.getValue();
         int alphaInt = (int)(255.0F * anim);
         float width = 108.0F;
         float height = 34.0F;
         float x = this.wUY90.getX();
         float y = this.wUY90.getY();
         this.vlw9m7b = x;
         this.ylsIQh = y;
         this.q4cfy(this.azttZ, x, y, width, height, 4.0F, anim);
         float headSize = 24.0F;
         float headX = x + 4.0F;
         float headY = y + (height - headSize) / 2.0F;
         float hurtPercent = livingEntity.hurtTime / 10.0F;
         int headColor = ColorProvider.rgba(255, (int)(255.0F * (1.0F - hurtPercent)), (int)(255.0F * (1.0F - hurtPercent)), (float)alphaInt);

         try {
            Identifier faceTex = this.e6qA(livingEntity, playerEntity);
            if (faceTex != null) {
               AbstractTexture tex = this.mc.getTextureManager().getTexture(faceTex);
               tex.setFilter(false, false);
               int texId = tex.getGlId();
               if (texId > 0) {
                  BuiltTexture headTexture = Builder.texture()
                     .size(new SizeState(headSize, headSize))
                     .radius(new QuadRadiusState(6.0F))
                     .color(new QuadColorState(headColor))
                     .texture(0.125F, 0.125F, 0.125F, 0.125F, texId)
                     .smoothness(1.0F)
                     .build();
                  headTexture.render(context.getMatrices().peek().getPositionMatrix(), headX, headY);
               }
            }
         } catch (Exception var64) {
         }

         float currentHp = Server.getHealth(livingEntity, false);
         if (Float.isNaN(currentHp) || currentHp < 0.0F) {
            currentHp = 0.0F;
         }

         float absorption = livingEntity.getAbsorptionAmount();
         if (Float.isNaN(absorption) || absorption < 0.0F) {
            absorption = 0.0F;
         }

         float maxHealth = livingEntity.getMaxHealth();
         float total = maxHealth + absorption;
         if (total <= 0.0F) {
            total = 1.0F;
         }

         float healthFrac = MathHelper.clamp(currentHp / total, 0.0F, 1.0F);
         float absFrac = MathHelper.clamp(absorption / total, 0.0F, 1.0F);
         this.aFNHi.run(healthFrac);
         this.nMjsSpC.run(absFrac);
         float animHealth = this.aFNHi.getValue();
         float animAbs = this.nMjsSpC.getValue();
         float ringCX = headX + headSize / 2.0F;
         float ringCY = headY + headSize / 2.0F;
         float ringSize = headSize + 5.0F;
         float cornerRadius = 6.0F + (ringSize - headSize) / 2.0F;
         float ringThickness = 0.7F;
         int hpColor = ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), alphaInt);
         int goldColor = ColorProvider.rgba(255, 215, 0, (float)alphaInt);
         int ringBackColor = ColorProvider.rgba(20, 20, 20, (float)((int)(160.0F * anim)));
         DrawUtil.drawBorderArc(ringCX, ringCY, ringSize, cornerRadius, ringThickness, -90.0F, 270.0F, ringBackColor);
         float hpDeg = 360.0F * animHealth;
         if (hpDeg > 0.5F) {
            DrawUtil.drawBorderArc(ringCX, ringCY, ringSize, cornerRadius, ringThickness, -90.0F, -90.0F + hpDeg, hpColor);
         }

         float absDeg = 360.0F * animAbs;
         if (absDeg > 0.5F) {
            DrawUtil.drawBorderArc(ringCX, ringCY, ringSize, cornerRadius, ringThickness, -90.0F + hpDeg, -90.0F + hpDeg + absDeg, goldColor);
         }

         float textX = headX + headSize + 5.0F;
         float rightEdge = x + width - 5.0F;
         NameProtect nameProtect = Instance.get(NameProtect.class);
         String name = nameProtect.isEnabled() ? nameProtect.getCustomName(livingEntity.getName().getString()) : livingEntity.getName().getString();
         String shownName = this.rC74(name, 8.5F, rightEdge - textX);
         DrawUtil.drawText(Fonts.SFPRO_MEDIUM.get(), shownName, textX, y + 3.0F, ColorProvider.rgba(255, 255, 255, (float)alphaInt), 8.5F);
         float myHp = this.mc.player != null ? Server.getHealth(this.mc.player, false) : 0.0F;
         float myAbs = this.mc.player != null ? this.mc.player.getAbsorptionAmount() : 0.0F;
         if (Float.isNaN(myHp) || myHp < 0.0F) {
            myHp = 0.0F;
         }

         if (Float.isNaN(myAbs) || myAbs < 0.0F) {
            myAbs = 0.0F;
         }

         float shownHp = currentHp + absorption;
         boolean win = myHp + myAbs >= shownHp;
         String hpNum = String.format(Locale.US, "%.1f HP", shownHp);
         String verdict = win ? "WIN" : "LOSE";
         int verdictColor = win ? ColorProvider.rgba(70, 220, 90, (float)alphaInt) : ColorProvider.rgba(240, 70, 60, (float)alphaInt);
         float hpNumW = Fonts.SFPRO_MEDIUM.get().getWidth(hpNum, 7.0F);
         float verdictW = Fonts.SFPRO_MEDIUM.get().getWidth(verdict, 7.0F);
         float yText = y + height - 12.0F;
         float barWidth = rightEdge - textX;
         float verdictX = textX + barWidth - verdictW;
         DrawUtil.drawText(Fonts.SFPRO_MEDIUM.get(), hpNum, textX, yText, ColorProvider.rgba(255, 255, 255, (float)alphaInt), 7.0F);
         DrawUtil.drawText(Fonts.SFPRO_MEDIUM.get(), verdict, verdictX, yText, verdictColor, 7.0F);
         this.wUY90.setWidth(width);
         this.wUY90.setHeight(height);
         float armorAlpha = this.faiI.getValue();
         if (this.rkOWd.getValue() && armorAlpha > 0.05F) {
            List<ItemStack> allItems = new ArrayList<>();
            List<ItemStack> armorList = new ArrayList<>();

            for (ItemStack stack : livingEntity.getArmorItems()) {
               armorList.add(stack);
            }

            Collections.reverse(armorList);
            allItems.addAll(armorList);
            allItems.add(livingEntity.getMainHandStack());
            allItems.add(livingEntity.getOffHandStack());
            allItems.removeIf(ItemStack::isEmpty);
            float cell = 13.0F;
            float itemScale = 0.6875F;
            float itemsW = cell * allItems.size();
            float itemsX = x + (width - itemsW) / 2.0F;
            float itemsY = y - cell - 4.0F;
            context.getMatrices().push();
            context.getMatrices().translate(0.0F, 0.0F, 100.0F);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            this.ggr65(context, textRenderer, allItems, itemsX, itemsY, cell, itemScale);
            context.getMatrices().pop();
         }
      }
   }

   private void khg8(
      DrawContext context,
      LivingEntity livingEntity,
      AbstractClientPlayerEntity playerEntity,
      float faceX,
      float faceY,
      float faceSize,
      float radius,
      int alphaInt
   ) {
      try {
         Identifier faceTex = this.e6qA(livingEntity, playerEntity);
         if (faceTex != null) {
            AbstractTexture tex = this.mc.getTextureManager().getTexture(faceTex);
            tex.setFilter(false, false);
            int texId = tex.getGlId();
            if (texId > 0) {
               float hurt = Math.min(1.0F, livingEntity.hurtTime / 10.0F);
               int headColor = ColorProvider.rgba(255, (int)(255.0F * (1.0F - hurt)), (int)(255.0F * (1.0F - hurt)), (float)alphaInt);
               BuiltTexture skinTexture = Builder.texture()
                  .size(new SizeState(faceSize, faceSize))
                  .radius(new QuadRadiusState(radius))
                  .color(new QuadColorState(headColor))
                  .texture(0.125F, 0.125F, 0.125F, 0.125F, texId)
                  .smoothness(1.0F)
                  .build();
               skinTexture.render(context.getMatrices().peek().getPositionMatrix(), faceX, faceY);
            }
         }
      } catch (Exception var15) {
      }
   }

   private int l67zUz9(float frac, int alpha) {
      frac = MathHelper.clamp(frac, 0.0F, 1.0F);
      float red;
      float green;
      if (frac > 0.5F) {
         red = 1.0F - (frac - 0.5F) * 2.0F;
         green = 1.0F;
      } else {
         red = 1.0F;
         green = frac * 2.0F;
      }

      return ColorProvider.rgba((int)(red * 190.0F), (int)(green * 190.0F), 25, (float)alpha);
   }

   private String rC74(String text, float size, float maxWidth) {
      MsdfFont font = Fonts.SFPRO_MEDIUM.get();
      if (text != null && !(font.getWidth(text, size) <= maxWidth)) {
         String ellipsis = "..";
         String result = text;

         while (result.length() > 0 && font.getWidth(result + ellipsis, size) > maxWidth) {
            result = result.substring(0, result.length() - 1);
         }

         return result + ellipsis;
      } else {
         return text;
      }
   }

   private Identifier e6qA(LivingEntity entity, AbstractClientPlayerEntity player) {
      try {
         if (player != null) {
            return player.getSkinTextures().texture();
         }

         if (this.mc.getEntityRenderDispatcher().getRenderer(entity) instanceof LivingEntityRenderer renderer) {
            float tickDelta = this.mc.getRenderTickCounter().getTickDelta(false);
            LivingEntityRenderState state = (LivingEntityRenderState)renderer.getAndUpdateRenderState(entity, tickDelta);
            if (state != null) {
               return renderer.getTexture(state);
            }
         }
      } catch (Exception var7) {
      }

      return null;
   }

   public void update() {
      for (Interface.Staff staff : this.imz8r) {
         staff.isOnServer = false;
      }

      Iterator var28 = this.mc.getNetworkHandler().getPlayerList().iterator();

      while (true) {
         String name;
         boolean vanish;
         boolean isGM3;
         PlayerListEntry playerListEntry;
         while (true) {
            if (!var28.hasNext()) {
               this.imz8r.removeIf(staffx -> !staffx.isOnServer && staffx.animation.getValue() == 0.0F);
               return;
            }

            playerListEntry = (PlayerListEntry)var28.next();
            name = playerListEntry.getProfile().getName().replaceAll("[\\[\\]]", "");
            PlayerListEntry info = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(name);
            vanish = info == null;
            isGM3 = info != null && info.getGameMode() == GameMode.SPECTATOR;
            boolean matchesPrefix = this.b41dCrw
               .matcher(playerListEntry.getDisplayName() != null ? playerListEntry.getDisplayName().getString().toLowerCase(Locale.ROOT) : "")
               .matches();
            boolean isValidName = this.s74dnbF.matcher(name).matches();
            boolean notSelf = !name.equals(MinecraftClient.getInstance().player.getName().getString());
            if (isValidName && notSelf && matchesPrefix || isValidName && notSelf && vanish || StaffManager.isStaff(name)) {
               if (!StaffManager.isStaff(name)) {
                  break;
               }

               String[] names = new String[]{
                  "auction",
                  "exp_smith",
                  "shop_balls",
                  "shop_grief",
                  "free",
                  "shop_kits",
                  "siege",
                  "rwplus",
                  "bossfight",
                  "guide",
                  "shop_smith",
                  "shop_spawners",
                  "colliseum",
                  "battlepass",
                  "buyer",
                  "huckster",
                  "buff_brewer",
                  "killer",
                  "shop_mage"
               };
               boolean contains = false;
               if (MinecraftClient.getInstance().getCurrentServerEntry() != null
                  && MinecraftClient.getInstance().getCurrentServerEntry().address != null
                  && (
                     MinecraftClient.getInstance().getCurrentServerEntry().address.contains("mc.rwdonat.pw")
                        || MinecraftClient.getInstance().getCurrentServerEntry().address.contains("mc.cakeworld.pw")
                  )) {
                  for (int i = 0; i < Arrays.stream(names).count(); i++) {
                     if (name.contains(names[i])) {
                        contains = true;
                        break;
                     }
                  }
               }

               if (!contains) {
                  break;
               }
            }
         }

         String targetName = name;
         Optional<Interface.Staff> existingStaff = this.imz8r.stream().filter(s -> s.name.equals(targetName)).findFirst();
         Interface.Status status = vanish ? Interface.Status.VANISHED : (isGM3 ? Interface.Status.VANISHED : Interface.Status.NONE);
         if (existingStaff.isPresent()) {
            Interface.Staff s = existingStaff.get();
            s.isOnServer = true;
            s.status = status;
         } else {
            String[] namesx = new String[]{
               "auction",
               "exp_smith",
               "shop_balls",
               "shop_grief",
               "free",
               "shop_kits",
               "siege",
               "rwplus",
               "bossfight",
               "guide",
               "shop_smith",
               "shop_spawners",
               "colliseum",
               "battlepass",
               "buyer",
               "huckster",
               "buff_brewer",
               "killer",
               "shop_mage"
            };
            boolean containsx = false;
            if (MinecraftClient.getInstance().getCurrentServerEntry() != null
               && MinecraftClient.getInstance().getCurrentServerEntry().address != null
               && (
                  MinecraftClient.getInstance().getCurrentServerEntry().address.contains("mc.rwdonat.pw")
                     || MinecraftClient.getInstance().getCurrentServerEntry().address.contains("mc.cakeworld.pw")
               )) {
               for (int ix = 0; ix < Arrays.stream(namesx).count(); ix++) {
                  if (name.contains(namesx[ix])) {
                     containsx = true;
                  }
               }
            }

            if (!containsx) {
               Text originalPrefix = playerListEntry.getDisplayName();
               Text prefix = originalPrefix;
               if (originalPrefix != null) {
                  prefix = ReplaceUtil.replaceSymbols(originalPrefix);
                  String fullString = prefix.getString();
                  int nickIndex = fullString.indexOf(name);
                  if (nickIndex != -1) {
                     int endIndex = nickIndex + name.length();
                     if (endIndex < fullString.length()) {
                        MutableText newText = Text.empty();
                        int currentLength = 0;
                        MutableText baseCopy = prefix.copy();
                        baseCopy.getSiblings().clear();
                        String mainContent = baseCopy.getString();
                        if (!mainContent.isEmpty() && currentLength < endIndex) {
                           int takeLength = Math.min(mainContent.length(), endIndex - currentLength);
                           newText.append(Text.literal(mainContent.substring(0, takeLength)).setStyle(prefix.getStyle()));
                           currentLength += takeLength;
                        }

                        for (Text sibling : prefix.getSiblings()) {
                           if (currentLength >= endIndex) {
                              break;
                           }

                           MutableText siblingCopy = sibling.copy();
                           siblingCopy.getSiblings().clear();
                           String siblingContent = siblingCopy.getString();
                           int takeLength = Math.min(siblingContent.length(), endIndex - currentLength);
                           if (takeLength > 0) {
                              newText.append(Text.literal(siblingContent.substring(0, takeLength)).setStyle(sibling.getStyle()));
                              currentLength += takeLength;
                           }
                        }

                        prefix = newText;
                     }
                  }
               }

               Interface.Staff staff = new Interface.Staff(
                  prefix == null ? Text.of(playerListEntry.getProfile().getName()) : prefix, name, vanish || isGM3, status
               );
               staff.isOnServer = true;
               this.imz8r.add(staff);
            }
         }
      }
   }

   public int getPing(PlayerEntity entity) {
      PlayerListEntry list = this.mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
      return list != null ? list.getLatency() : 0;
   }

   private void ep1H8(DrawContext context) {
      if (this.mc.player != null) {
         boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
         if (!chatOpen) {
            this.gzTbtan.open = false;
            this.gzTbtan.draggingSlider = null;
         }

         this.eemFuA(this.gzTbtan, context);
         this.sEU6(context);
         this.sao7CKx(this.gzTbtan, context);
         this.z1TvB0(this.gzTbtan, context);
      }
   }

   private void sEU6(DrawContext context) {
      float size = 64.0F;
      float x = this.chgvv8.getX();
      float y = this.chgvv8.getY();
      float pad = 3.0F;
      float width = pad * 2.0F + size;
      float height = pad * 2.0F + size;
      this.chgvv8.setWidth(width);
      this.chgvv8.setHeight(height);
      this.q4cfy(this.gzTbtan, x, y, width, height, 8.0F, 1.0F);
      DrawUtil.drawCircle(x + pad + size / 2.0F, y + pad + size / 2.0F, size * 0.4F, ColorProvider.setAlpha(ColorProvider.getColorClient(), 50));
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      context.drawTexture(
         RenderLayer::getGuiTextured, WATERMARK_TEXTURE, (int)(x + pad), (int)(y + pad), 0.0F, 0.0F, (int)size, (int)size, (int)size, (int)size
      );
   }

   private void bx4cip(DrawContext context) {
      if (this.mc.player != null) {
         if (!this.mc.options.playerListKey.isPressed()) {
            boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
            if (!chatOpen) {
               this.eaunw.open = false;
               this.eaunw.draggingSlider = null;
            }

            this.eemFuA(this.eaunw, context);
            this.iqRHm(context);
            this.sao7CKx(this.eaunw, context);
            this.z1TvB0(this.eaunw, context);
         }
      }
   }

   private void wFqc(DrawContext context) {
      if (this.mc.player != null) {
         if (!this.mc.options.playerListKey.isPressed()) {
            boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
            if (!chatOpen) {
               this.v1iE.open = false;
               this.v1iE.draggingSlider = null;
            }

            this.eemFuA(this.v1iE, context);
            this.l9cSxQj(context);
            this.sao7CKx(this.v1iE, context);
            this.z1TvB0(this.v1iE, context);
         }
      }
   }

   private void iqRHm(DrawContext context) {
      if (this.mc.player != null) {
         Counter.updateFPS();
         String nick = Instance.get(NameProtect.class).getCustomName();
         String fpsText = Counter.getCurrentFPS() + " FPS";
         String serverText = this.mc.getCurrentServerEntry() != null ? this.mc.getCurrentServerEntry().address : "local";
         MsdfFont textFont = Fonts.SFMEDIUM.get();
         MsdfFont boldFont = Fonts.SFBOLD.get();
         MsdfFont iconsFont = Fonts.ICONS_NURIK.get();
         MsdfFont alphaFont = Fonts.ALPHADLC.get();
         float textSize = 7.0F;
         float iconSize = 7.0F;
         float iconGap = 1.5F;
         float entryGap = 6.0F;
         float skinSize = 12.0F;
         float padX = 9.0F;
         float height = 17.0F;
         int white = ColorProvider.setAlpha(ColorProvider.getColorText(), 255);
         int accent = ClientPalette.pickTwo(ColorProvider.getColorVisualModules());
         float brandW = boldFont.getWidth("Viola", textSize) + 1.0F + textFont.getWidth("Client", textSize);
         float row1W = brandW
            + entryGap
            + skinSize
            + iconGap
            + textFont.getWidth(nick, textSize)
            + entryGap
            + alphaFont.getWidth("n", iconSize)
            + iconGap
            + textFont.getWidth(fpsText, textSize)
            + entryGap
            + iconsFont.getWidth("Q", iconSize)
            + iconGap
            + textFont.getWidth(serverText, textSize);
         float boxWidth = row1W + padX * 2.0F;
         int screenW = context.getScaledWindowWidth();
         float x = (screenW - boxWidth) / 2.0F;
         int bossBars = this.ryuY6u();
         float y = 20.0F + bossBars * 13.0F;
         float radius = 8.0F;
         this.q4cfy(this.eaunw, x, y, boxWidth, height, radius, 1.0F);
         float textY = y + (height - textSize) / 2.0F;
         float iconY = y + (height - iconSize) / 2.0F + 0.75F;
         float skinY = y + (height - skinSize) / 2.0F;
         float cx = x + padX;
         DrawUtil.drawText(boldFont, "Viola", cx, textY, accent, textSize);
         cx += boldFont.getWidth("Viola", textSize) + 1.0F;
         DrawUtil.drawText(textFont, "Client", cx, textY, white, textSize);
         cx += textFont.getWidth("Client", textSize) + entryGap;
         this.q9dE(context, cx, skinY, skinSize);
         cx += skinSize + iconGap;
         DrawUtil.drawText(textFont, nick, cx, textY, white, textSize);
         cx += textFont.getWidth(nick, textSize) + entryGap;
         DrawUtil.drawText(alphaFont, "n", cx, iconY, accent, iconSize);
         cx += alphaFont.getWidth("n", iconSize) + iconGap;
         DrawUtil.drawText(textFont, fpsText, cx, textY, white, textSize);
         cx += textFont.getWidth(fpsText, textSize) + entryGap;
         DrawUtil.drawText(iconsFont, "Q", cx, iconY, accent, iconSize);
         cx += iconsFont.getWidth("Q", iconSize) + iconGap;
         DrawUtil.drawText(textFont, serverText, cx, textY, white, textSize);
         this.vI37.setX(x);
         this.vI37.setY(y);
         this.vI37.setWidth(boxWidth);
         this.vI37.setHeight(height);
      }
   }

   private void q9dE(DrawContext context, float px, float py, float size) {
      try {
         AbstractClientPlayerEntity self = this.mc.player instanceof AbstractClientPlayerEntity ? this.mc.player : null;
         Identifier faceTex = this.nQ8z9(this.mc.player, self);
         if (faceTex != null) {
            AbstractTexture tex = this.mc.getTextureManager().getTexture(faceTex);
            tex.setFilter(false, false);
            int texId = tex.getGlId();
            if (texId > 0) {
               BuiltTexture skinTexture = Builder.texture()
                  .size(new SizeState(size, size))
                  .radius(new QuadRadiusState(size / 2.0F))
                  .color(new QuadColorState(-1))
                  .texture(0.125F, 0.125F, 0.125F, 0.125F, texId)
                  .smoothness(1.0F)
                  .build();
               skinTexture.render(context.getMatrices().peek().getPositionMatrix(), px, py);
            }
         }
      } catch (Exception var10) {
      }
   }

   private void l9cSxQj(DrawContext context) {
      MsdfFont msdf = Fonts.SFMEDIUM.get();
      MsdfFont icons = Fonts.ICONS_NURIK.get();
      float height = 17.0F;
      float padX = 9.0F;
      float wmX = this.vI37.getX();
      float wmY = this.vI37.getY();
      float wmW = this.vI37.getWidth();
      float wmH = this.vI37.getHeight();
      if (wmW <= 0.0F || wmH <= 0.0F) {
         int scrW = context.getScaledWindowWidth();
         float fallbackW = 200.0F;
         wmX = (scrW - fallbackW) / 2.0F;
         wmY = 24.0F;
         wmW = fallbackW;
         wmH = 17.0F;
      }

      float infoGap = 4.0F;
      int whiteColor = -1;
      int goldIcon = ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), 255);
      int accent = ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), 255);
      int dotColor = ColorProvider.setAlpha(ColorProvider.getColorClient(), 90);
      String coordsIcon = "F";
      String tpsIcon = "$";
      String bpsIcon = "@";
      String xPart = "x" + (int)this.mc.player.getX();
      String yPart = "y" + (int)this.mc.player.getY();
      String zPart = "z" + (int)this.mc.player.getZ();
      double dX = this.mc.player.getX() - this.mc.player.prevX;
      double dZ = this.mc.player.getZ() - this.mc.player.prevZ;
      String bpsValue = String.format(Locale.US, "%.1f Bps", Math.hypot(dX, dZ) * 20.0);
      String tpsValue = String.format(
         Locale.US, "%.1f Tps", Viola.getInstance().getTpsGetter().getTPS()
      );
      float iconGap = 1.5F;
      float sepGap = 3.5F;
      float dotSize = 3.0F;
      float sep = sepGap + dotSize + sepGap;
      float contentW = icons.getWidth(coordsIcon, 7.0F)
         + iconGap
         + msdf.getWidth(xPart, 7.0F)
         + sep
         + msdf.getWidth(yPart, 7.0F)
         + sep
         + msdf.getWidth(zPart, 7.0F)
         + sep
         + icons.getWidth(tpsIcon, 7.0F)
         + iconGap
         + msdf.getWidth(tpsValue, 7.0F)
         + sep
         + icons.getWidth(bpsIcon, 7.0F)
         + iconGap
         + msdf.getWidth(bpsValue, 7.0F);
      float boxWidth = contentW + padX * 2.0F;
      float x = wmX + wmW / 2.0F - boxWidth / 2.0F;
      float y = wmY + wmH + infoGap;
      float radius = 8.0F;
      this.q4cfy(this.v1iE, x, y, boxWidth, height, radius, 1.0F);
      float textY = y + (height - 7.0F) / 2.0F;
      float dotY = y + (height - dotSize) / 2.0F;
      float cx = x + padX;
      DrawUtil.drawText(icons, coordsIcon, cx, textY + 0.75F, goldIcon, 7.0F);
      cx += icons.getWidth(coordsIcon, 7.0F) + iconGap;
      DrawUtil.drawText(msdf, xPart, cx, textY, whiteColor, 7.0F);
      cx += msdf.getWidth(xPart, 7.0F) + sepGap;
      DrawUtil.drawRound(cx, dotY, dotSize, dotSize, dotSize / 2.0F, dotColor);
      cx += dotSize + sepGap;
      DrawUtil.drawText(msdf, yPart, cx, textY, whiteColor, 7.0F);
      cx += msdf.getWidth(yPart, 7.0F) + sepGap;
      DrawUtil.drawRound(cx, dotY, dotSize, dotSize, dotSize / 2.0F, dotColor);
      cx += dotSize + sepGap;
      DrawUtil.drawText(msdf, zPart, cx, textY, whiteColor, 7.0F);
      cx += msdf.getWidth(zPart, 7.0F) + sep;
      DrawUtil.drawText(icons, tpsIcon, cx, textY + 0.75F, goldIcon, 7.0F);
      cx += icons.getWidth(tpsIcon, 7.0F) + iconGap;
      DrawUtil.drawText(msdf, tpsValue, cx, textY, whiteColor, 7.0F);
      cx += msdf.getWidth(tpsValue, 7.0F) + sep;
      DrawUtil.drawText(icons, bpsIcon, cx, textY + 0.75F, goldIcon, 7.0F);
      cx += icons.getWidth(bpsIcon, 7.0F) + iconGap;
      DrawUtil.drawText(msdf, bpsValue, cx, textY, whiteColor, 7.0F);
      this.qzcKzlZ.setX(x);
      this.qzcKzlZ.setY(y);
      this.qzcKzlZ.setWidth(boxWidth);
      this.qzcKzlZ.setHeight(height);
   }

   private int hQDcLV(int start, int end, float speed, float offset) {
      long t = System.currentTimeMillis();
      double ph = t * (speed / 1000.0) + offset;
      float p = (float)(Math.sin(ph) * 0.5 + 0.5);
      int sr = start >> 16 & 0xFF;
      int sg = start >> 8 & 0xFF;
      int sb = start & 0xFF;
      int er = end >> 16 & 0xFF;
      int eg = end >> 8 & 0xFF;
      int eb = end & 0xFF;
      int r = (int)(sr * (1.0F - p) + er * p);
      int g = (int)(sg * (1.0F - p) + eg * p);
      int b = (int)(sb * (1.0F - p) + eb * p);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private Identifier nQ8z9(LivingEntity entity, AbstractClientPlayerEntity player) {
      try {
         if (player != null) {
            return player.getSkinTextures().texture();
         }

         if (this.mc.getEntityRenderDispatcher().getRenderer(entity) instanceof LivingEntityRenderer renderer) {
            float tickDelta = this.mc.getRenderTickCounter().getTickDelta(false);
            LivingEntityRenderState state = (LivingEntityRenderState)renderer.getAndUpdateRenderState(entity, tickDelta);
            if (state != null) {
               return renderer.getTexture(state);
            }
         }
      } catch (Exception var7) {
      }

      return null;
   }

   public void drawEntity(float x, float y, float scale, float yawAngle, float pitchAngle, LivingEntity entity) {
      MatrixStack matrices = new MatrixStack();
      matrices.push();
      matrices.translate((double)x, (double)y, 50.0);
      matrices.scale(-scale, scale, scale);
      matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
      matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yawAngle));
      matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitchAngle));
      float bodyYaw = entity.bodyYaw;
      float prevBodyYaw = entity.prevBodyYaw;
      float headYaw = entity.headYaw;
      float prevHeadYaw = entity.prevHeadYaw;
      float yaw = entity.getYaw();
      float prevYaw = entity.prevYaw;
      float pitch = entity.getPitch();
      float prevPitch = entity.prevPitch;
      entity.bodyYaw = 0.0F;
      entity.prevBodyYaw = 0.0F;
      entity.headYaw = 0.0F;
      entity.prevHeadYaw = 0.0F;
      entity.setYaw(0.0F);
      entity.prevYaw = 0.0F;
      entity.setPitch(0.0F);
      entity.prevPitch = 0.0F;
      DiffuseLighting.disableGuiDepthLighting();
      Immediate immediate = this.mc.getBufferBuilders().getEntityVertexConsumers();
      float tickDelta = this.mc.getRenderTickCounter().getTickDelta(true);
      this.mc.getEntityRenderDispatcher().render(entity, 0.0, 0.0, 0.0, tickDelta, matrices, immediate, 15728880);
      immediate.draw();
      DiffuseLighting.enableGuiDepthLighting();
      entity.bodyYaw = bodyYaw;
      entity.prevBodyYaw = prevBodyYaw;
      entity.headYaw = headYaw;
      entity.prevHeadYaw = prevHeadYaw;
      entity.setYaw(yaw);
      entity.prevYaw = prevYaw;
      entity.setPitch(pitch);
      entity.prevPitch = prevPitch;
      matrices.pop();
   }

   private Color bOxa(Color a, Color b, float t) {
      return new Color(
         (int)(a.getRed() + t * (b.getRed() - a.getRed())),
         (int)(a.getGreen() + t * (b.getGreen() - a.getGreen())),
         (int)(a.getBlue() + t * (b.getBlue() - a.getBlue()))
      );
   }

   private void ems5() {
      Map<String, StatusEffectInstance> currentEffects = this.mc
         .player
         .getStatusEffects()
         .stream()
         .collect(
            Collectors.toMap(e -> Text.translatable(e.getTranslationKey()).getString() + ":" + e.getAmplifier(), e -> (StatusEffectInstance)e, (e1, e2) -> e1)
         );
      this.v42Pbh1.forEach(item -> {
         String key = item.name + ":" + item.amplifier;
         StatusEffectInstance effect = currentEffects.get(key);
         if (effect != null) {
            item.durationTicks = effect.getDuration();
            if (item.durationTicks > item.maxDurationTicks) {
               item.maxDurationTicks = item.durationTicks;
            }

            if (!item.active) {
               item.animation.setValue(1.0F);
            }

            item.active = true;
            currentEffects.remove(key);
         } else {
            item.active = false;
         }
      });
      boolean added = !currentEffects.isEmpty();
      currentEffects.forEach(
         (key, effect) -> this.v42Pbh1
            .add(
               new Interface.PotionItem(
                  Text.translatable(effect.getTranslationKey()).getString(), effect.getAmplifier(), effect.getDuration(), effect.getEffectType()
               )
            )
      );
      boolean removed = this.v42Pbh1.removeIf(item -> !item.active && item.animation.getValue() == 0.0F);
      if (added || removed) {
         this.v42Pbh1.sort(Comparator.comparing(pi -> pi.name));
      }
   }

   private void d6Vfg() {
      if (this.mc.player != null) {
         for (Interface.CooldownItem ci : this.vzKx) {
            boolean cooling = this.mc.player.getItemCooldownManager().isCoolingDown(ci.item.getDefaultStack());
            if (!cooling || System.currentTimeMillis() >= ci.endTimeMs) {
               ci.active = false;
            }
         }

         this.vzKx.removeIf(cix -> !cix.active && cix.animation.getValue() == 0.0F);
         if (this.vzKx.isEmpty() && this.mc.currentScreen instanceof ChatScreen) {
            long now = System.currentTimeMillis();
            if (now - this.r1b1 >= 1500L) {
               this.i965v4 = (this.i965v4 + 1) % COOLDOWN_EXAMPLE_ITEMS.length;
               this.r1b1 = now;
            }
         }
      }
   }

   private void g5Ym5(DrawContext context) {
      if (this.mc.player != null) {
         boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
         if (!chatOpen) {
            this.i6tb.open = false;
            this.i6tb.draggingSlider = null;
         }

         this.eemFuA(this.i6tb, context);
         this.gGizv97(context);
         this.sao7CKx(this.i6tb, context);
         this.z1TvB0(this.i6tb, context);
      }
   }

   private void n29w5(DrawContext context, ItemStack stack, float x, float y, float size, int alpha) {
      float scale = size / 16.0F;
      context.getMatrices().push();
      context.getMatrices().translate(x, y, 0.0F);
      context.getMatrices().scale(scale, scale, 1.0F);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha / 255.0F);
      context.drawItem(stack, 0, 0);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.mc.player != null && !stack.isEmpty()) {
         float cd = this.mc.player.getItemCooldownManager().getCooldownProgress(stack, 1.0F);
         if (cd > 0.0F) {
            context.fill(0, 0, 16, Math.round(16.0F * cd), -1946157056);
         }
      }

      context.getMatrices().pop();
   }

   private void gGizv97(DrawContext context) {
      if (this.mc.player != null) {
         float posX = this.hGdh5qk.getX();
         float posY = this.hGdh5qk.getY();
         float headerHeight = 14.0F;
         float itemHeight = 12.0F;
         float minWidth = 56.0F;
         float padX = 5.0F;
         float padY = 2.0F;
         boolean isFound = false;

         for (Interface.CooldownItem item : this.vzKx) {
            item.animation.run(item.active ? 1.0F : 0.0F);
            item.rowAnim.run(item.active ? 1.0F : 0.0F);
            if (item.animation.getValue() > 0.001F) {
               isFound = true;
            }
         }

         boolean showExample = this.mc.currentScreen instanceof ChatScreen && !isFound;
         if (!isFound && !(this.mc.currentScreen instanceof ChatScreen)) {
            this.rnT632K.run(0.0F);
         } else {
            this.rnT632K.run(1.0F);
         }

         float globalAlpha = this.rnT632K.getValue();
         if (!(globalAlpha <= 0.05F)) {
            int headerAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * globalAlpha));
            float maxNameBoxW = minWidth;
            float maxTimeBoxW = 0.0F;
            float contentHeight = 0.0F;

            for (Interface.CooldownItem itemx : this.vzKx) {
               float animVal = itemx.animation.getValue();
               if (animVal > 0.001F) {
                  contentHeight += itemHeight * itemx.rowAnim.getValue();
                  String name = itemx.item.getName().getString();
                  float nameW = Fonts.SFMEDIUM.get().getWidth(name, 7.0F);
                  float nw = nameW + 8.0F + 12.0F + 10.0F;
                  if (nw > maxNameBoxW) {
                     maxNameBoxW = nw;
                  }

                  int sec = itemx.remainingSeconds();
                  String timeStr = String.format("%d:%02d", sec / 60, sec % 60);
                  float tw = Fonts.SFMEDIUM.get().getWidth(timeStr, 6.75F) + 10.0F;
                  if (tw > maxTimeBoxW) {
                     maxTimeBoxW = tw;
                  }
               }
            }

            if (showExample) {
               float exNameW = Fonts.SFMEDIUM.get().getWidth("Example", 7.0F);
               if (exNameW + 8.0F + 12.0F + 10.0F > maxNameBoxW) {
                  maxNameBoxW = exNameW + 8.0F + 12.0F + 10.0F;
               }

               float exTimeW = Fonts.SFMEDIUM.get().getWidth("**:**", 6.75F) + 10.0F;
               if (exTimeW > maxTimeBoxW) {
                  maxTimeBoxW = exTimeW;
               }

               contentHeight = itemHeight;
            }

            float rawWidth = maxNameBoxW + maxTimeBoxW + padX * 2.0F;
            this.i6tb.panelWidth.run(rawWidth);
            float totalRowWidth = this.i6tb.panelWidth.getValue();
            if (totalRowWidth < 20.0F) {
               totalRowWidth = rawWidth;
            }

            float totalHeight = headerHeight + contentHeight + padY * 2.0F;
            this.q4cfy(this.i6tb, posX, posY, totalRowWidth, totalHeight, 6.0F, globalAlpha);
            this.qm7dp(Fonts.SFMEDIUM.get(), "Cooldowns", posX + padX + 4.0F, posY + padY + 2.5F, headerAlpha, 8.0F);
            DrawUtil.drawText(
               Fonts.ALPHADLC.get(),
               "h",
               posX + totalRowWidth - padX - 11.0F,
               posY + padY + 3.0F,
               ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), headerAlpha),
               9.0F
            );
            float curY = posY + headerHeight + padY;

            for (Interface.CooldownItem itemxx : this.vzKx) {
               float animVal = itemxx.animation.getValue();
               if (!(animVal <= 0.001F)) {
                  float rowAnimVal = itemxx.rowAnim.getValue();
                  if (!(rowAnimVal <= 0.001F)) {
                     int sec = itemxx.remainingSeconds();
                     String timeStr = String.format("%d:%02d", sec / 60, sec % 60);
                     String nameStr = itemxx.item.getName().getString();
                     int itemAlpha = (int)Math.min(255.0F, Math.max(0.0F, 255.0F * animVal * globalAlpha));
                     if (itemAlpha >= 5) {
                        float timeW = Fonts.SFMEDIUM.get().getWidth(timeStr, 6.75F);
                        float timeBoxW = timeW + 10.0F;
                        float rowHeight = itemHeight * rowAnimVal;
                        context.getMatrices().push();
                        context.getMatrices().translate(posX + totalRowWidth / 2.0F, curY + rowHeight / 2.0F, 0.0F);
                        context.getMatrices().scale(rowAnimVal, rowAnimVal, rowAnimVal);
                        context.getMatrices().translate(-(posX + totalRowWidth / 2.0F), -(curY + rowHeight / 2.0F), 0.0F);
                        float cdMid = kRb60o2(curY + 2.75F, 7.0F);
                        this.n29w5(context, itemxx.item.getDefaultStack(), posX + padX + 1.0F, cdMid - 5.0F, 10.0F, itemAlpha);
                        float dotX = posX + padX + 13.0F;
                        float dotY = cdMid - 1.5F;
                        DrawUtil.drawRound(dotX, dotY, 3.0F, 3.0F, 1.5F, ColorProvider.setAlpha(ColorProvider.getColorClient(), itemAlpha));
                        float textX = posX + padX + 20.0F;
                        DrawUtil.drawText(Fonts.SFMEDIUM.get(), nameStr, textX, curY + 2.75F, ColorProvider.rgba(255, 255, 255, (float)itemAlpha), 7.0F);
                        float timerX = posX + totalRowWidth - padX - timeBoxW;
                        String timerKey = "cooldowns_duration_" + itemxx.item.getTranslationKey();
                        TimerTextAnimator.draw(
                           Fonts.SFMEDIUM.get(),
                           timerKey,
                           timeStr,
                           timerX + (timeBoxW - timeW) / 2.0F + 1.0F,
                           curY + 3.5F,
                           ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), itemAlpha),
                           6.75F
                        );
                        context.getMatrices().pop();
                        curY += rowHeight;
                     }
                  }
               }
            }

            if (showExample) {
               String nameStr = "Example";
               String timeStr = "**:**";
               ItemStack stack = COOLDOWN_EXAMPLE_ITEMS[this.i965v4].getDefaultStack();
               float timeW = Fonts.SFMEDIUM.get().getWidth(timeStr, 6.75F);
               float timeBoxW = timeW + 10.0F;
               float cdExMid = kRb60o2(curY + 3.5F, 7.0F);
               this.n29w5(context, stack, posX + padX + 1.0F, cdExMid - 5.0F, 10.0F, headerAlpha);
               float dotX = posX + padX + 13.0F;
               float dotY = cdExMid - 1.5F;
               DrawUtil.drawRound(dotX, dotY, 3.0F, 3.0F, 1.5F, ColorProvider.setAlpha(ColorProvider.getColorClient(), headerAlpha));
               float textX = posX + padX + 20.0F;
               DrawUtil.drawText(Fonts.SFMEDIUM.get(), nameStr, textX, curY + 3.5F, ColorProvider.rgba(255, 255, 255, (float)headerAlpha), 7.0F);
               float timerX = posX + totalRowWidth - padX - timeBoxW;
               DrawUtil.drawText(
                  Fonts.SFMEDIUM.get(),
                  timeStr,
                  timerX + (timeBoxW - timeW) / 2.0F + 1.0F,
                  curY + 3.5F,
                  ColorProvider.setAlpha(ColorProvider.getColorVisualModules(), headerAlpha),
                  6.75F
               );
            }

            this.hGdh5qk.setWidth(totalRowWidth);
            this.hGdh5qk.setHeight(totalHeight);
         }
      }
   }

   private static float kRb60o2(float drawY, float size) {
      return drawY + size * 0.59F;
   }

   private void bX26N(float cx, float topY, float tipY, float w, int color) {
      float h = tipY - topY;
      if (h <= 0.0F) {
         h = 4.0F;
      }

      int steps = Math.max(4, (int)(h * 2.0F));

      for (int i = 0; i <= steps; i++) {
         float t = (float)i / steps;
         float rowW = w * (1.0F - t);
         float ry = topY + h * t;
         DrawUtil.drawRound(cx - rowW / 2.0F, ry, rowW, h / steps + 0.6F, 0.0F, color);
      }
   }

   private void ggr65(DrawContext context, TextRenderer textRenderer, List<ItemStack> items, float startX, float startY, float cell, float itemScale) {
      float drawn = 16.0F * itemScale;
      float inset = (cell - drawn) / 2.0F;

      for (int i = 0; i < items.size(); i++) {
         ItemStack stack = items.get(i);
         if (stack != null && !stack.isEmpty()) {
            float cx = startX + i * cell + inset;
            float cy = startY + inset;
            context.getMatrices().push();
            context.getMatrices().translate(cx, cy, 0.0F);
            context.getMatrices().scale(itemScale, itemScale, 1.0F);
            context.drawItem(stack, 0, 0);
            context.drawStackOverlay(textRenderer, stack, 0, 0);
            context.getMatrices().pop();
         }
      }
   }

   public float getNotificationsX() {
      return this.wvBd.getX();
   }

   public float getNotificationsY() {
      return this.wvBd.getY();
   }

   private float micWjX2() {
      float toggleW = 15.0F;
      float pad = 6.0F;
      float gap = 6.0F;
      float textW = Fonts.SFMEDIUM
         .get()
         .getWidth(
            D.k(
               new int[]{1226, 1143, 1219, 1086, 1248, 1143, 219, 1089, 1255, 1026, 1231, 1084, 1257, 1036, 1230, 1087, 1261, 1144}, new int[]{213, 55, 251, 2}
            ),
            7.0F
         );
      return pad + textW + gap + toggleW + pad;
   }

   public float getNotificationsCenterX() {
      return this.wvBd.getX() + this.micWjX2() / 2.0F;
   }

   public float getNotificationsScale() {
      return this.l2rzcE.size.getFloatValue();
   }

   public void drawNotifBackground(DrawContext context, float x, float y, float w, float h, float radius, float alphaFactor) {
      this.q4cfy(this.l2rzcE, x, y, w, h, radius, alphaFactor);
   }

   public void drawNewToggle(float x, float y, float toggleW, float toggleH, boolean on, float anim, int alpha) {
      int inactive = ColorProvider.setAlpha(ColorProvider.getColorInactiveIndicator(), alpha);
      int active = ColorProvider.setAlpha(ColorProvider.getColorIndicator(), alpha);
      int bg = ColorProvider.interpolateColor(inactive, active, anim);
      DrawUtil.drawRound(x, y, toggleW, toggleH, toggleH / 2.0F, bg);
      float knob = toggleH - 1.0F;
      float knobMinX = x + 0.5F;
      float knobMaxX = x + toggleW - knob - 0.5F;
      float knobX = knobMinX + (knobMaxX - knobMinX) * anim;
      DrawUtil.drawCircle(knobX + knob / 2.0F, y + 0.5F + knob / 2.0F, knob / 2.0F, ColorProvider.setAlpha(ColorProvider.getColorSliderCircle(), alpha));
   }

   private void nn6d(DrawContext context) {
      if (this.mc.player != null) {
         boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
         if (!chatOpen) {
            this.l2rzcE.open = false;
            this.l2rzcE.draggingSlider = null;
         } else {
            this.eemFuA(this.l2rzcE, context);
            float posX = this.wvBd.getX();
            float posY = this.wvBd.getY();
            String text = D.k(
               new int[]{1180, 1233, 1273, 1228, 1206, 1233, 225, 1203, 1201, 1188, 1269, 1230, 1215, 1194, 1268, 1229, 1211, 1246},
               new int[]{131, 145, 193, 240}
            );
            float toggleW = 15.0F;
            float toggleH = 8.0F;
            float pad = 6.0F;
            float gap = 6.0F;
            float textW = Fonts.SFMEDIUM.get().getWidth(text, 7.0F);
            float width = pad + textW + gap + toggleW + pad;
            float height = 15.0F;
            this.q4cfy(this.l2rzcE, posX, posY, width, height, 6.0F, 1.0F);
            boolean exampleOn = System.currentTimeMillis() / 3000L % 2L == 0L;
            this.cMzz.run(exampleOn ? 1.0F : 0.0F);
            float textX = posX + pad;
            DrawUtil.drawText(Fonts.SFMEDIUM.get(), text, textX, posY + height / 2.0F - 4.1299996F, ColorProvider.rgba(255, 255, 255, 255.0F), 7.0F);
            float tX = textX + textW + gap;
            float tY = posY + (height - toggleH) / 2.0F;
            this.drawNewToggle(tX, tY, toggleW, toggleH, exampleOn, this.cMzz.getValue(), 255);
            long phaseMs = System.currentTimeMillis() % 3000L;
            float rem = Math.max(0.0F, Math.min(1.0F, 1.0F - (float)phaseMs / 1600.0F));
            int barColor = exampleOn ? ColorProvider.rgba(0, 255, 100, 255.0F) : ColorProvider.rgba(255, 80, 80, 255.0F);
            float barH = 2.0F;
            float barY = posY + height - barH - 1.0F;
            float barPad = 6.0F;
            float barW = width - barPad * 2.0F;
            DrawUtil.drawRound(posX + barPad, barY, barW, barH, barH / 2.0F, ColorProvider.setAlpha(ColorProvider.getColorInactiveText(), 90));
            if (rem > 0.01F) {
               DrawUtil.drawRound(posX + barPad, barY, barW * rem, barH, barH / 2.0F, barColor);
            }

            this.wvBd.setWidth(width);
            this.wvBd.setHeight(height);
            this.sao7CKx(this.l2rzcE, context);
            this.z1TvB0(this.l2rzcE, context);
         }
      }
   }

   private void ymQF3pn(DrawContext context) {
      if (this.mc.player != null) {
         boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
         if (!chatOpen) {
            this.t9e3F.open = false;
            this.t9e3F.draggingSlider = null;
         }

         this.eemFuA(this.t9e3F, context);
         this.zyESfV(context);
         this.sao7CKx(this.t9e3F, context);
         this.z1TvB0(this.t9e3F, context);
      }
   }

   private void zyESfV(DrawContext context) {
      if (this.mc.player != null) {
         ServerHelper serverHelper = Viola.getInstance().getModuleStorage().get(ServerHelper.class);
         if (serverHelper != null) {
            boolean chatOpen = this.mc.currentScreen instanceof ChatScreen;
            boolean showExample = chatOpen && !serverHelper.isEnabled();
            float posX = this.k9fd.getX();
            float posY = this.k9fd.getY();
            float itemSize = 32.0F;
            float gap = 4.0F;
            float padding = 2.0F;
            List<Interface.ServerHelperItem> items = new ArrayList<>();
            if (showExample) {
               items.add(
                  new Interface.ServerHelperItem(
                     Items.FIREWORK_STAR, "G", "АнтиПолет"
                  )
               );
               items.add(new Interface.ServerHelperItem(Items.SPLASH_POTION, "H", "Гринч"));
               items.add(new Interface.ServerHelperItem(Items.PLAYER_HEAD, "J", "Shift"));
               items.add(
                  new Interface.ServerHelperItem(Items.HEART_OF_THE_SEA, "K", "Трапка")
               );
            } else if (serverHelper.isEnabled() && !serverHelper.isFunTime()) {
               int antiFlyKey = serverHelper.getAntiFlyKey();
               if (antiFlyKey != -1 && this.tyzl7(Items.FIREWORK_STAR)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.FIREWORK_STAR,
                        this.w4CBfW(antiFlyKey),
                        "АнтиПолет"
                     )
                  );
               }

               int grinchKey = serverHelper.getGrinchPotionKey();
               if (grinchKey != -1 && this.tyzl7(Items.SPLASH_POTION)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.SPLASH_POTION, this.w4CBfW(grinchKey), "Гринч"
                     )
                  );
               }

               int autoShiftKey = serverHelper.getAutoShiftKey();
               if (autoShiftKey != -1 && this.tyzl7(Items.PLAYER_HEAD)) {
                  items.add(new Interface.ServerHelperItem(Items.PLAYER_HEAD, this.w4CBfW(autoShiftKey), "Shift"));
               }

               int horrorKey = serverHelper.getNewYearHorrorKey();
               if (horrorKey != -1 && this.tyzl7(Items.SPLASH_POTION)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.SPLASH_POTION, this.w4CBfW(horrorKey), "Ужас"
                     )
                  );
               }

               int essenceKey = serverHelper.getDarkEssenceKey();
               if (essenceKey != -1 && this.tyzl7(Items.SPLASH_POTION)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.SPLASH_POTION,
                        this.w4CBfW(essenceKey),
                        "Эссенция"
                     )
                  );
               }

               int snowballKey = serverHelper.getSnowballKey();
               if (snowballKey != -1 && this.tyzl7(Items.SPLASH_POTION)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.SPLASH_POTION, this.w4CBfW(snowballKey), "Снежок"
                     )
                  );
               }

               int trapKey = serverHelper.getTrapKey();
               if (trapKey != -1 && this.tyzl7(Items.HEART_OF_THE_SEA)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.HEART_OF_THE_SEA, this.w4CBfW(trapKey), "Трапка"
                     )
                  );
               }
            } else if (serverHelper.isEnabled() && serverHelper.isFunTime()) {
               int deoritKey = serverHelper.getFtDeoritKey();
               if (deoritKey != -1 && this.tyzl7(Items.ENDER_EYE)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.ENDER_EYE, this.w4CBfW(deoritKey), "Дезорит"
                     )
                  );
               }

               int ftTrapKey = serverHelper.getFtTrapKey();
               if (ftTrapKey != -1 && this.tyzl7(Items.NETHERITE_SCRAP)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.NETHERITE_SCRAP, this.w4CBfW(ftTrapKey), "Трапка"
                     )
                  );
               }

               int freezeKey = serverHelper.getFtFreezeKey();
               if (freezeKey != -1 && this.tyzl7(Items.SNOWBALL)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.SNOWBALL,
                        this.w4CBfW(freezeKey),
                        "Заморозка"
                     )
                  );
               }

               int dustKey = serverHelper.getFtDustKey();
               if (dustKey != -1 && this.tyzl7(Items.SUGAR)) {
                  items.add(
                     new Interface.ServerHelperItem(Items.SUGAR, this.w4CBfW(dustKey), "Пыль")
                  );
               }

               int plateKey = serverHelper.getFtPlateKey();
               if (plateKey != -1 && this.tyzl7(Items.DRIED_KELP)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.DRIED_KELP, this.w4CBfW(plateKey), "Пласт"
                     )
                  );
               }

               int windKey = serverHelper.getFtWindKey();
               if (windKey != -1 && this.tyzl7(Items.WIND_CHARGE)) {
                  items.add(
                     new Interface.ServerHelperItem(
                        Items.WIND_CHARGE, this.w4CBfW(windKey), "Ветер"
                     )
                  );
               }
            }

            if (!items.isEmpty()) {
               int itemsPerRow = items.size();
               float boxSize = itemSize + padding * 2.0F;
               float totalWidth = boxSize * itemsPerRow + gap * (itemsPerRow - 1);
               this.t9e3F.px = posX;
               this.t9e3F.py = posY;
               this.t9e3F.pw = totalWidth;
               this.t9e3F.ph = boxSize;
               int alpha = this.t9e3F.alpha.getIntValue();
               float radius = 8.0F;
               float curX = posX;

               for (Interface.ServerHelperItem item : items) {
                  this.drawElementBackground(curX, posY, boxSize, boxSize, radius, alpha);
                  ItemStack stack = item.item.getDefaultStack();
                  float iconSize = 18.0F;
                  float iconX = curX + (boxSize - iconSize) / 2.0F;
                  float iconY = posY + padding + 2.0F;
                  context.getMatrices().push();
                  context.getMatrices().translate(iconX, iconY, 0.0F);
                  float iconScale = iconSize / 16.0F;
                  context.getMatrices().scale(iconScale, iconScale, 1.0F);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  context.drawItem(stack, 0, 0);
                  context.getMatrices().pop();
                  float keyW = Fonts.SFMEDIUM.get().getWidth(item.keyBind, 8.5F);
                  float keyX = curX + (boxSize - keyW) / 2.0F;
                  float keyY = posY + boxSize - padding - 10.0F;
                  DrawUtil.drawText(Fonts.SFMEDIUM.get(), item.keyBind, keyX, keyY, ColorProvider.setAlpha(ColorProvider.getColorClient(), 255), 8.5F);
                  curX += boxSize + gap;
               }

               this.k9fd.setWidth(totalWidth);
               this.k9fd.setHeight(boxSize);
            }
         }
      }
   }

   private String w4CBfW(int keyCode) {
      if (keyCode == -1) {
         return "None";
      } else if (keyCode == -100) {
         return "LMB";
      } else if (keyCode == -99) {
         return "RMB";
      } else if (keyCode == -98) {
         return "MMB";
      } else {
         String keyName = GLFW.glfwGetKeyName(keyCode, 0);
         if (keyName != null) {
            return this.oy779uT(keyName.toUpperCase());
         } else {
            String key = KeyStorage.getKey(keyCode);
            return this.oy779uT(key.toUpperCase());
         }
      }
   }

   private String oy779uT(String text) {
      if (text != null && !text.isEmpty()) {
         Map<Character, Character> russianToEnglish = new HashMap<>();
         russianToEnglish.put('Й', 'Q');
         russianToEnglish.put('й', 'q');
         russianToEnglish.put('Ц', 'W');
         russianToEnglish.put('ц', 'w');
         russianToEnglish.put('У', 'E');
         russianToEnglish.put('у', 'e');
         russianToEnglish.put('К', 'R');
         russianToEnglish.put('к', 'r');
         russianToEnglish.put('Е', 'T');
         russianToEnglish.put('е', 't');
         russianToEnglish.put('Н', 'Y');
         russianToEnglish.put('н', 'y');
         russianToEnglish.put('Г', 'U');
         russianToEnglish.put('г', 'u');
         russianToEnglish.put('Ш', 'I');
         russianToEnglish.put('ш', 'i');
         russianToEnglish.put('Щ', 'O');
         russianToEnglish.put('щ', 'o');
         russianToEnglish.put('З', 'P');
         russianToEnglish.put('з', 'p');
         russianToEnglish.put('Х', '[');
         russianToEnglish.put('х', '[');
         russianToEnglish.put('Ъ', ']');
         russianToEnglish.put('ъ', ']');
         russianToEnglish.put('Ф', 'A');
         russianToEnglish.put('ф', 'a');
         russianToEnglish.put('Ы', 'S');
         russianToEnglish.put('ы', 's');
         russianToEnglish.put('В', 'D');
         russianToEnglish.put('в', 'd');
         russianToEnglish.put('А', 'F');
         russianToEnglish.put('а', 'f');
         russianToEnglish.put('П', 'G');
         russianToEnglish.put('п', 'g');
         russianToEnglish.put('Р', 'H');
         russianToEnglish.put('р', 'h');
         russianToEnglish.put('О', 'J');
         russianToEnglish.put('о', 'j');
         russianToEnglish.put('Л', 'K');
         russianToEnglish.put('л', 'k');
         russianToEnglish.put('Д', 'L');
         russianToEnglish.put('д', 'l');
         russianToEnglish.put('Ж', ';');
         russianToEnglish.put('ж', ';');
         russianToEnglish.put('Э', '\'');
         russianToEnglish.put('э', '\'');
         russianToEnglish.put('Я', 'Z');
         russianToEnglish.put('я', 'z');
         russianToEnglish.put('Ч', 'X');
         russianToEnglish.put('ч', 'x');
         russianToEnglish.put('С', 'C');
         russianToEnglish.put('с', 'c');
         russianToEnglish.put('М', 'V');
         russianToEnglish.put('м', 'v');
         russianToEnglish.put('И', 'B');
         russianToEnglish.put('и', 'b');
         russianToEnglish.put('Т', 'N');
         russianToEnglish.put('т', 'n');
         russianToEnglish.put('Ь', 'M');
         russianToEnglish.put('ь', 'm');
         russianToEnglish.put('Б', ',');
         russianToEnglish.put('б', ',');
         russianToEnglish.put('Ю', '.');
         russianToEnglish.put('ю', '.');
         StringBuilder result = new StringBuilder();

         for (char c : text.toCharArray()) {
            result.append(russianToEnglish.getOrDefault(c, c));
         }

         return result.toString();
      } else {
         return text;
      }
   }

   private boolean tyzl7(Item item) {
      if (this.mc.player == null) {
         return false;
      } else {
         for (ItemStack stack : this.mc.player.getInventory().main) {
            if (!stack.isEmpty() && stack.getItem() == item) {
               return true;
            }
         }

         for (ItemStack stackx : this.mc.player.getInventory().armor) {
            if (!stackx.isEmpty() && stackx.getItem() == item) {
               return true;
            }
         }

         ItemStack offhand = this.mc.player.getOffHandStack();
         return !offhand.isEmpty() && offhand.getItem() == item;
      }
   }

   private record BindEntry(String label, String bind, double animValue, ModuleCategory category) {


      

      

      

      

      
   }

   private static class CooldownItem {
      final Item item;
      long endTimeMs;
      long maxDurationMs;
      boolean active = true;
      final Animation animation = new Animation(Easing.EXPO_OUT, 233L);
      final Animation rowAnim = new Animation(Easing.DECELERATE, 150L);

      CooldownItem(Item item, long endTimeMs, long maxDurationMs) {
         this.item = item;
         this.endTimeMs = endTimeMs;
         this.maxDurationMs = maxDurationMs;
      }

      int remainingSeconds() {
         long rem = this.endTimeMs - System.currentTimeMillis();
         return (int)Math.max(0.0, Math.ceil(rem / 1000.0));
      }
   }

   private static class HeadParticle {
      float x;
      float y;
      float vx;
      float vy;
      float size;
      long spawnTime;
      int color;

      HeadParticle(float startX, float startY, int color) {
         this.x = startX;
         this.y = startY;
         double angle = Math.random() * Math.PI * 2.0;
         double speed = Math.random() * 0.4 + 0.1;
         this.vx = (float)(Math.cos(angle) * speed);
         this.vy = (float)(Math.sin(angle) * speed);
         this.size = (float)(Math.random() * 8.0 + 2.0);
         this.spawnTime = System.currentTimeMillis();
         this.color = color;
      }

      void update() {
         this.x = this.x + this.vx;
         this.y = this.y + this.vy;
      }

      float getAlpha() {
         long elapsed = System.currentTimeMillis() - this.spawnTime;
         return elapsed >= 2000L ? 0.0F : 1.0F - (float)elapsed / 2000.0F;
      }
   }

   private static final class HudPopup {
      final String title;
      final Draggable drag;
      final SliderSetting size;
      final SliderSetting alpha;
      final BooleanSetting ring;
      boolean open = false;
      boolean transformed = false;
      final Animation anim = new Animation(Easing.EXPO_OUT, 250L);
      final Animation panelWidth = new Animation(Easing.EXPO_OUT, 220L);
      final List<Interface.PopupRow> rendered = new ArrayList<>();
      final List<Interface.PopupRow> extraRows = new ArrayList<>();
      float px;
      float py;
      float pw;
      float ph;
      SliderSetting draggingSlider = null;
      float trackX;
      float trackW;

      HudPopup(String title, Draggable drag, SliderSetting size, SliderSetting alpha, BooleanSetting ring) {
         this.title = title;
         this.drag = drag;
         this.size = size;
         this.alpha = alpha;
         this.ring = ring;
      }
   }

   private static enum PopupKind {
      TOGGLE,
      SLIDER;
   }

   private static final class PopupRow {
      final Interface.PopupKind kind;
      final String label;
      final BooleanSetting bool;
      final SliderSetting slider;
      float x;
      float y;
      float w;
      float h;

      PopupRow(Interface.PopupKind kind, String label, BooleanSetting bool, SliderSetting slider) {
         this.kind = kind;
         this.label = label;
         this.bool = bool;
         this.slider = slider;
      }
   }

   private static class PotionItem {
      String name;
      int amplifier;
      int durationTicks;
      int maxDurationTicks;
      boolean active;
      RegistryEntry<StatusEffect> effect;
      Animation animation = new Animation(Easing.EXPO_OUT, 233L);
      Animation rowAnim = new Animation(Easing.DECELERATE, 150L);

      PotionItem(String name, int amplifier, int durationTicks, RegistryEntry<StatusEffect> effect) {
         this.name = name;
         this.amplifier = amplifier;
         this.durationTicks = durationTicks;
         this.maxDurationTicks = durationTicks;
         this.effect = effect;
         this.active = true;
      }
   }

   private static class ServerHelperItem {
      final Item item;
      final String keyBind;
      final String name;

      ServerHelperItem(Item item, String keyBind, String name) {
         this.item = item;
         this.keyBind = keyBind;
         this.name = name;
      }
   }

   public static class Staff {
      Text prefix;
      public String name;
      boolean isSpec;
      Interface.Status status;
      boolean isOnServer;
      Animation animation;
      long mills;

      public Staff(Text prefix, String name, boolean isSpec, Interface.Status status) {
         this.prefix = prefix;
         this.name = name;
         this.isSpec = isSpec;
         this.status = status;
         this.animation = new Animation(Easing.EXPO_OUT, 233L);
         this.mills = System.currentTimeMillis();
      }
   }

   public static enum Status {
      NONE("", -1),
      VANISHED("SPEC", ColorProvider.rgba(229, 0, 63, 255.0F));

      public final String string;
      public final int color;

      private Status(String string, int color) {
         this.string = string;
         this.color = color;
      }
   }
}
