package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import zov.viola.Viola;
import zov.viola.event.list.EventHUD;
import zov.viola.mixin.PlayerListHudAccessor;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.list.misc.NameProtect;
import zov.viola.module.list.render.hud.Interface;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeListSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.ui.ClickGuiBackgroundRenderer;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.render.builders.Builder;
import zov.viola.util.render.builders.states.QuadColorState;
import zov.viola.util.render.builders.states.QuadRadiusState;
import zov.viola.util.render.builders.states.SizeState;
import zov.viola.util.render.math.ProjectionUtil;
import zov.viola.util.render.msdf.Fonts;
import zov.viola.util.render.msdf.MsdfFont;
import zov.viola.util.render.providers.ColorProvider;
import zov.viola.util.render.renderers.DrawUtil;
import zov.viola.util.render.renderers.IRenderer;
import zov.viola.util.replace.ReplaceUtil;
import zov.viola.util.server.Server;

@ModuleInformation(
   moduleName = "NameTags",
   moduleDesc = "Теги над игроками",
   moduleCategory = ModuleCategory.RENDER
)
public class NameTags extends Module {
   private final ModeListSetting sNpXd = new ModeListSetting(
      "Типы",
      new BooleanSetting("Игроки", true),
      new BooleanSetting("Предметы", true)
   );
   private final SliderSetting tnuY5 = new SliderSetting("Размер", 7.5, 5.0, 15.0, 0.5);
   private final BooleanSetting yDjjsh = new BooleanSetting("Блюр", true);
   private final SliderSetting t7dy6Z = new SliderSetting(
      "Прозрачность", 100.0, 0.0, 100.0, 1.0
   );
   private final SliderSetting fqqcGv = new SliderSetting(
      "Дистанция", 64.0, 10.0, 200.0, 1.0
   );
   private final ModeSetting qEpAg = new ModeSetting(
      "Фон шейдер",
      "Обычный",
      "Обычный",
      "Шейдер"
   );
   private final ModeSetting rbOx48 = new ModeSetting(
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
      .setVisible(() -> this.qEpAg.is("Шейдер"));
   private final SliderSetting wh4XSe = new SliderSetting(
         D.k(
            new int[]{1265, 1168, 1096, 1039, 1198, 1248, 1073, 1029, 1232, 1169, 1076, 1140, 206, 1176, 1091, 1025, 1242, 1253, 1078, 1032},
            new int[]{238, 208, 118, 56}
         ),
         0.65F,
         0.1F,
         1.0,
         0.01F
      )
      .setVisible(() -> this.qEpAg.is("Шейдер"));
   private final Map<UUID, Text> lnqZUi = new ConcurrentHashMap<>();
   private int juV9 = 0;
   private final List<ItemStack> nfxBCBX = new ArrayList<>();

   public static Text normalizeSmallCaps(Text text) {
      String[] from = new String[]{
         "ᴀ",
         "ʙ",
         "ᴄ",
         "ᴅ",
         "ᴇ",
         "ꜰ",
         "ɢ",
         "ʜ",
         "ɪ",
         "ᴊ",
         "ᴋ",
         "ʟ",
         "ᴍ",
         "ɴ",
         "ᴏ",
         "ᴘ",
         "ǫ",
         "ʀ",
         "ꜱ",
         "ᴛ",
         "ᴜ",
         "ᴠ",
         "ᴡ",
         "x",
         "ʏ",
         "ᴢ",
         "◆",
         "\u2503 "
      };
      String[] to = new String[]{
         "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "●", ""
      };

      for (int i = 0; i < from.length; i++) {
         text = ReplaceUtil.replace(text, from[i], to[i]);
      }

      return text;
   }

   public static Text processName(PlayerEntity entity) {
      Text name = entity.getDisplayName();
      MinecraftClient mc = MinecraftClient.getInstance();
      NameProtect nameProtect = Viola.getInstance().getModuleStorage().get(NameProtect.class);
      if (nameProtect != null && nameProtect.isEnabled() && mc.player != null) {
         String scoreName = entity.getNameForScoreboard();
         String myName = mc.player.getNameForScoreboard();
         String customName = nameProtect.getCustomName();
         if (scoreName.equals(myName) || entity.getUuid().equals(mc.player.getUuid())) {
            name = ReplaceUtil.replace(name, myName, customName);
         } else if (nameProtect.hideFriends.getValue() && FriendRepository.isFriend(scoreName)) {
            name = ReplaceUtil.replace(name, scoreName, customName);
         }
      }

      String s = name.getString();
      if (s.contains("ꔀ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔀ", Formatting.GRAY, "ИГРОК");
      }

      if (s.contains("ꔄ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔄ", Formatting.BLUE, "HERO");
      }

      if (s.contains("ꔈ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔈ", Formatting.GOLD, "TITAN");
      }

      if (s.contains("ꔒ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔒ", Formatting.GREEN, "AVENGER");
      }

      if (s.contains("ꔖ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔖ", Formatting.AQUA, "OVERLORD");
      }

      if (s.contains("ꔠ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔠ", Formatting.GOLD, "MAGISTER");
      }

      if (s.contains("ꔤ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔤ", Formatting.RED, "IMPERATOR");
      }

      if (s.contains("ꔲ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔲ", Formatting.DARK_PURPLE, "BULL");
      }

      if (s.contains("ꕓ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕓ", Formatting.DARK_GRAY, "GHOST");
      }

      if (s.contains("ꔨ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔨ", Formatting.LIGHT_PURPLE, "DRAGON");
      }

      if (s.contains("ꔂ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔂ", Formatting.GOLD, "GOD");
      }

      if (s.contains("ꔸ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔸ", Formatting.GOLD, "GOD");
      }

      if (s.contains("ꔦ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔦ", Formatting.BLUE, "D.ML.ADMIN");
      }

      if (s.contains("ꕀ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕀ", Formatting.DARK_GREEN, "HYDRA");
      }

      if (s.contains("ꕖ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕖ", Formatting.GRAY, "BUNNY");
      }

      if (s.contains("ꕒ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕒ", Formatting.WHITE, "RABBIT");
      }

      if (s.contains("ꕈ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕈ", Formatting.GREEN, "COBRA");
      }

      if (s.contains("ꔶ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔶ", Formatting.GOLD, "TIGER");
      }

      if (s.contains("ꕠ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕠ", Formatting.YELLOW, "D.HELPER");
      }

      if (s.contains("ꔉ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔉ", Formatting.YELLOW, "HELPER");
      }

      if (s.contains("ꔆ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔆ", Formatting.GRAY, "D.MODER");
      }

      if (s.contains("ꕄ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕄ", Formatting.DARK_RED, "VAMPIRE");
      }

      if (s.contains("ꔰ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔰ", Formatting.GRAY, "D.ML.ADMIN");
      }

      if (s.contains("ꔐ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔐ", Formatting.DARK_BLUE, "D.GL.MODER");
      }

      if (s.contains("ꔔ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔔ", Formatting.GRAY, "D.GL.MODER");
      }

      if (s.contains("ꔢ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔢ", Formatting.GRAY, "D.ST.MODER");
      }

      if (s.contains("ꕡ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕡ", Formatting.GOLD, "ST.HELPER");
      }

      if (s.contains("ꕅ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕅ", Formatting.DARK_PURPLE, "MEDIA+");
      }

      if (s.contains("ꔓ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔓ", Formatting.BLUE, "ML.MODER");
      }

      if (s.contains("ꔗ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔗ", Formatting.BLUE, "MODER");
      }

      if (s.contains("ꔡ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔡ", Formatting.DARK_PURPLE, "MODER+");
      }

      if (s.contains("ꔥ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔥ", Formatting.BLUE, "ST.MODER");
      }

      if (s.contains("ꔩ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔩ", Formatting.DARK_PURPLE, "GL.MODER");
      }

      if (s.contains("ꕗ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕗ", Formatting.DARK_RED, "D.ADMIN");
      }

      if (s.contains("ꔘ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔘ", Formatting.BLUE, "D.ST.MODER");
      }

      if (s.contains("ꔳ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔳ", Formatting.AQUA, "ML.ADMIN");
      }

      if (s.contains("ꔷ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔷ", Formatting.RED, "ADMIN");
      }

      if (s.contains("ꔁ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔁ", Formatting.DARK_PURPLE, "MEDIA");
      }

      if (s.contains("ꔅ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꔅ", Formatting.RED, "YT");
      }

      if (s.contains("ꕉ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕉ", Formatting.GOLD, "PEGAS");
      }

      if (s.contains("ꕆ")) {
         name = ReplaceUtil.replaceWithFormatted(name, "ꕆ", Formatting.GOLD, "PEGAS");
      }

      return normalizeSmallCaps(name);
   }

   private Text g9n730s(PlayerEntity entity) {
      return this.lnqZUi.computeIfAbsent(entity.getUuid(), uuid -> processName(entity));
   }

   @Subscribe
   private void onRender(EventHUD e) {
      if (this.juV9++ > 100) {
         this.lnqZUi.clear();
         this.juV9 = 0;
      }

      if (this.mc.world != null && this.mc.player != null) {
         if (!this.h15Ld()) {
            MsdfFont font = Fonts.SFBOLD.get();
            float tickDelta = e.getRenderTickCounter().getTickDelta(true);
            if (this.sNpXd.isEnabled("Игроки")) {
               this.b89S4h0(font, tickDelta, e);
            }

            if (this.sNpXd.isEnabled("Предметы")) {
               this.h7Ac0e(font, tickDelta, e);
            }
         }
      }
   }

   private boolean h15Ld() {
      try {
         if (this.mc.inGameHud == null) {
            return false;
         } else {
            PlayerListHud hud = this.mc.inGameHud.getPlayerListHud();
            return hud == null ? false : ((PlayerListHudAccessor)hud).vio_isVisible();
         }
      } catch (Throwable var2) {
         return false;
      }
   }

   private void b89S4h0(MsdfFont font, float tickDelta, EventHUD e) {
      List<AbstractClientPlayerEntity> worldPlayers = this.mc.world.getPlayers();
      Interface iface = Viola.getInstance().getModuleStorage().get(Interface.class);
      if (iface != null) {
         for (PlayerEntity entity : worldPlayers) {
            if (entity != this.mc.player || this.mc.getEntityRenderDispatcher().camera.isThirdPerson()) {
               double ddx = entity.getX() - this.mc.player.getX();
               double ddy = entity.getY() - this.mc.player.getY();
               double ddz = entity.getZ() - this.mc.player.getZ();
               double rangeSq = this.fqqcGv.getValue() * this.fqqcGv.getValue();
               if (!(ddx * ddx + ddy * ddy + ddz * ddz > rangeSq)) {
                  double x = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
                  double y = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY()) + entity.getHeight() + 0.5;
                  double z = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
                  Vector2f pos = ProjectionUtil.project(x, y, z);
                  if (pos.getX() != Float.MAX_VALUE && pos.getY() != Float.MAX_VALUE) {
                     float sw = this.mc.getWindow().getScaledWidth();
                     float sh = this.mc.getWindow().getScaledHeight();
                     if (!(pos.getX() < -60.0F) && !(pos.getX() > sw + 60.0F) && !(pos.getY() < -60.0F) && !(pos.getY() > sh + 60.0F)) {
                        float screenYOffset = 5.7F;
                        float posY = pos.getY() - 5.7F;
                        Text baseName = this.g9n730s(entity);
                        float currentHp = Server.getHealth(entity, false);
                        int hpColor = 16733525;
                        MutableText name = baseName.copy();
                        String playerName = entity.getNameForScoreboard();
                        boolean isFriend = FriendRepository.isFriend(playerName);
                        ItemStack offHandStack = entity.getOffHandStack();
                        if (offHandStack.getItem() == Items.PLAYER_HEAD && offHandStack.getName().getString().length() <= 18) {
                           name.append(
                                 Text.literal(" - ").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
                              )
                              .append(offHandStack.getName())
                              .append(Text.literal("").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                        }

                        name.append(Text.literal(" - ").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
                           .append(
                              Text.literal(String.format("%.1f", currentHp))
                                 .setStyle(Style.EMPTY.withColor(hpColor))
                           )
                           .append(Text.literal("HP").setStyle(Style.EMPTY.withColor(hpColor)));
                        float fontSize = (float)this.tnuY5.getValue();
                        float textWidth = font.getWidth(name.getString(), fontSize);
                        float paddingX = 3.0F;
                        float headIconSize = 9.5F;
                        float headIconPadding = 1.0F;
                        float totalWidth = textWidth + paddingX * 2.0F + headIconSize + headIconPadding;
                        float tagHeight = 12.5F;
                        float tagY = posY - 2.0F;
                        float centerX = pos.getX();
                        float bgX = centerX - totalWidth / 2.0F;
                        Vector4f radii = new Vector4f(4.0F, 4.0F, 4.0F, 4.0F);
                        if (isFriend) {
                           if (this.qEpAg.is("Шейдер")) {
                              this.nRwb(bgX, tagY, totalWidth, tagHeight + 1.0F, radii.x, (float)(this.t7dy6Z.getValue() * 2.55F), true);
                           } else {
                              if (this.yDjjsh.getValue()) {
                                 DrawUtil.drawRoundBlur(bgX, tagY, totalWidth, tagHeight + 1.0F, radii, -10633362, 12.0F);
                              }

                              DrawUtil.drawRound(
                                 bgX, tagY, totalWidth, tagHeight + 1.0F, radii, ColorProvider.setAlpha(1243507514, (int)(this.t7dy6Z.getValue() * 2.55))
                              );
                           }
                        } else if (this.qEpAg.is("Шейдер")) {
                           this.nRwb(bgX, tagY, totalWidth, tagHeight + 1.0F, radii.x, (float)(this.t7dy6Z.getValue() * 2.55F), false);
                        } else {
                           if (this.yDjjsh.getValue()) {
                              DrawUtil.drawRoundBlur(bgX, tagY, totalWidth, tagHeight + 1.0F, radii, ColorProvider.rgba(200, 200, 200, 255.0F), 12.0F);
                           }

                           DrawUtil.drawRound(
                              bgX,
                              tagY,
                              totalWidth,
                              tagHeight + 1.0F,
                              radii,
                              ColorProvider.setAlpha(ColorProvider.rgba(25, 25, 25, 25.0F), (int)(this.t7dy6Z.getValue() * 2.55))
                           );
                        }

                        float headSize = 9.5F;
                        float headX = bgX + 2.8F;
                        float headY = tagY + (tagHeight - headSize) / 2.0F;
                        if (entity instanceof AbstractClientPlayerEntity clientPlayer) {
                           AbstractTexture skinTex = this.mc.getTextureManager().getTexture(clientPlayer.getSkinTextures().texture());
                           int texId = skinTex.getGlId();
                           Builder.texture()
                              .size(new SizeState(headSize, headSize))
                              .radius(new QuadRadiusState(1.0F))
                              .color(new QuadColorState(-1))
                              .texture(0.125F, 0.125F, 0.125F, 0.125F, texId)
                              .build()
                              .render(IRenderer.DEFAULT_MATRIX, headX, headY, 0.0F);
                        }

                        if (isFriend) {
                           DrawUtil.drawText(font, name.getString(), bgX + paddingX + 1.0F + headIconSize + headIconPadding, posY - 0.25F, -8586240, fontSize);
                        } else {
                           DrawUtil.drawText(font, name, bgX + paddingX + 1.0F + headIconSize + headIconPadding, posY - 0.25F, fontSize, 220);
                        }

                        this.nfxBCBX.clear();
                        this.nfxBCBX.add(entity.getEquippedStack(EquipmentSlot.HEAD));
                        this.nfxBCBX.add(entity.getEquippedStack(EquipmentSlot.CHEST));
                        this.nfxBCBX.add(entity.getEquippedStack(EquipmentSlot.LEGS));
                        this.nfxBCBX.add(entity.getEquippedStack(EquipmentSlot.FEET));
                        this.nfxBCBX.add(entity.getMainHandStack());
                        this.nfxBCBX.add(entity.getOffHandStack());
                        this.nfxBCBX.removeIf(ItemStack::isEmpty);
                        if (!this.nfxBCBX.isEmpty()) {
                           float iconSize = 16.0F;
                           float spacing = 0.0F;
                           float itemsTotalWidth = this.nfxBCBX.size() * iconSize + (this.nfxBCBX.size() - 1) * spacing;
                           float startX = pos.getX() - itemsTotalWidth / 2.0F + 13.5F;
                           float iconY = posY - 10.0F;
                           MatrixStack matrices = e.getDrawContext().getMatrices();

                           for (int i = 0; i < this.nfxBCBX.size(); i++) {
                              ItemStack stack = this.nfxBCBX.get(i);
                              float x2 = startX + i * (iconSize + spacing - 2.0F);
                              float scale = 0.7F;
                              float half = -18.0F;
                              matrices.push();
                              matrices.translate(x2 + half, iconY + half, 0.0F);
                              matrices.scale(scale, scale, 1.0F);
                              e.getDrawContext().drawItem(stack, (int)(-half), (int)(-half));
                              e.getDrawContext().drawStackOverlay(this.mc.textRenderer, stack, (int)(-half), (int)(-half));
                              matrices.pop();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void h7Ac0e(MsdfFont font, float tickDelta, EventHUD e) {
      MsdfFont sfBold = Fonts.SFBOLD.get();
      Interface iface = Viola.getInstance().getModuleStorage().get(Interface.class);
      if (iface != null) {
         for (Entity entity : this.mc.world.getEntities()) {
            if (entity instanceof ItemEntity itemEntity && !itemEntity.isInvisible() && !itemEntity.hasNoGravity()) {
               double ddx = entity.getX() - this.mc.player.getX();
               double ddy = entity.getY() - this.mc.player.getY();
               double ddz = entity.getZ() - this.mc.player.getZ();
               double rangeSq = this.fqqcGv.getValue() * this.fqqcGv.getValue();
               if (!(ddx * ddx + ddy * ddy + ddz * ddz > rangeSq)) {
                  double x = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
                  double y = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY()) + entity.getHeight() + 0.5;
                  double z = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
                  Vector2f pos = ProjectionUtil.project(x, y, z);
                  if (pos.getX() != Float.MAX_VALUE && pos.getY() != Float.MAX_VALUE) {
                     float sw = this.mc.getWindow().getScaledWidth();
                     float sh = this.mc.getWindow().getScaledHeight();
                     if (!(pos.getX() < -60.0F) && !(pos.getX() > sw + 60.0F) && !(pos.getY() < -60.0F) && !(pos.getY() > sh + 60.0F)) {
                        ItemStack stack = itemEntity.getStack();
                        if (!stack.isEmpty()) {
                           int rarityOrdinal = stack.getRarity().ordinal();

                           Formatting rarityColor = switch (rarityOrdinal) {
                              case 0 -> Formatting.WHITE;
                              case 1 -> Formatting.YELLOW;
                              case 2 -> Formatting.AQUA;
                              case 3 -> Formatting.LIGHT_PURPLE;
                              default -> Formatting.WHITE;
                           };
                           String itemName = stack.getName().getString();
                           if (itemName.length() > 20) {
                              itemName = itemName.substring(0, 20);
                           }

                           Text nameText = Text.literal(itemName).setStyle(Style.EMPTY.withColor(rarityColor));
                           if (!stack.getName().getSiblings().isEmpty()) {
                              String siblingName = stack.getName().getString();
                              if (siblingName.length() > 20) {
                                 siblingName = siblingName.substring(0, 20);
                              }

                              nameText = Text.literal(siblingName).setStyle(stack.getName().getStyle());
                           }

                           Text countComponent = stack.getCount() > 1
                              ? Text.literal(" - ")
                                 .setStyle(Style.EMPTY.withColor(Formatting.GRAY))
                                 .append(Text.literal(stack.getCount() + "x").setStyle(Style.EMPTY.withColor(Formatting.RED)))
                              : Text.empty();
                           Text textComponent = nameText.copy().append(countComponent);
                           Text normalized = normalizeSmallCaps(textComponent);
                           float textWidth = sfBold.getWidth(normalized.getString(), (float)this.tnuY5.getValue());
                           float totalWidth = textWidth + 4.0F;
                           float bgX = pos.getX() - totalWidth / 2.0F;
                           Vector4f radii = new Vector4f(3.0F, 3.0F, 3.0F, 3.0F);
                           if (this.qEpAg.is("Шейдер")) {
                              this.nRwb(bgX, pos.getY() - 2.0F, totalWidth - 1.0F, 11.0F, radii.x, (float)(this.t7dy6Z.getValue() * 2.55F), false);
                           } else {
                              if (this.yDjjsh.getValue()) {
                                 DrawUtil.drawRoundBlur(
                                    bgX, pos.getY() - 2.0F, totalWidth - 1.0F, 11.0F, radii, ColorProvider.rgba(200, 200, 200, 255.0F), 12.0F
                                 );
                              }

                              DrawUtil.drawRound(
                                 bgX,
                                 pos.getY() - 2.0F,
                                 totalWidth - 1.0F,
                                 11.0F,
                                 radii,
                                 ColorProvider.setAlpha(ColorProvider.rgba(25, 25, 25, 25.0F), (int)(this.t7dy6Z.getValue() * 2.55))
                              );
                           }

                           DrawUtil.drawText(sfBold, normalized, bgX + 2.0F, pos.getY() - 2.0F, (float)this.tnuY5.getValue(), 255);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void nRwb(float x, float y, float w, float h, float radius, float alphaF, boolean friend) {
      int accent = friend ? -13710223 : ColorProvider.getAccent();
      int a = (int)MathHelper.clamp(alphaF, 0.0F, 255.0F);
      ClickGuiBackgroundRenderer.render(this.rbOx48.getValue(), accent, this.wh4XSe.getFloatValue(), x, y, w + 0.5F, h + 0.25F, radius);
      int overlay = (int)(a * 0.55F);
      overlay = Math.min(255, Math.max(0, overlay));
      DrawUtil.drawRound(x, y, w, h, radius, ColorProvider.setAlpha(ColorProvider.getColorWindowBg(), overlay));
   }
}
