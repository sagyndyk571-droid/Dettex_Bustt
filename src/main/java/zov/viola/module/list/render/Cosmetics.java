package zov.viola.module.list.render;

import com.google.common.eventbus.Subscribe;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import zov.viola.event.list.EventWorldRender;
import zov.viola.mixin.LivingEntityRendererAccessor;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.ModeSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;
import zov.viola.util.friend.FriendRepository;
import zov.viola.util.render.cosmetics.pulse.PulseCosmeticModel;

@ModuleInformation(
   moduleName = "Cosmetics",
   moduleDesc = "Косметика: крылья",
   moduleCategory = ModuleCategory.RENDER
)
public class Cosmetics extends Module {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   public final ModeSetting wings = new ModeSetting(
      "Крылья",
      "Выкл",
      "Выкл",
      "Dark Wings",
      "Ghoul Wings",
      "Wings",
      "Vulcano Wings",
      "Angel Wings",
      "Baby Dragon Wings",
      "Dragon Wings Blue",
      "Spider Wings",
      "Easter Wings",
      "Vane Wings",
      "Trident Wings",
      "Wota Wings",
      "Rocker Wings",
      "Bat Wings",
      "Bee Wings",
      "Shark Tail",
      "Vin Wings"
   );
   private static final Map<String, String> NAME_BY_ID = new HashMap<>();
   private static final Map<String, String> ID_BY_NAME = new HashMap<>();
   public final BooleanSetting showOnFriends;
   public final BooleanSetting animate;
   public final SliderSetting offsetX;
   public final SliderSetting offsetY;
   public final SliderSetting offsetZ;
   public final SliderSetting scale;
   private final Map<String, PulseCosmeticModel> uL41mJ;
   private final Set<String> psEhizr;

   public Cosmetics() {
      z47uwqR(
         this.wings,
         "123",
         "124",
         "136",
         "25",
         "61",
         "62",
         "63",
         "64",
         "65",
         "82",
         "86",
         "91",
         "99",
         "custom_bat_wings",
         "custom_bee_wings",
         "custom_shark_tail",
         "custom_vin_wings"
      );
      this.wings.addAlias("Pulse Wings", "Wings");
      this.wings
         .addAlias(
            "fluger Wings",
            "Vane Wings"
         );
      this.wings
         .addAlias(
            "bro9i Trident Wings",
            "Trident Wings"
         );
      this.wings
         .addAlias(
            "pulse Rocker Wings",
            "Rocker Wings"
         );
      this.wings
         .addAlias(
            "wota Wings",
            "Wota Wings"
         );
      this.showOnFriends = new BooleanSetting(
         D.k(
            new int[]{1040, 1069, 1255, 1092, 1080, 1112, 1263, 1092, 1101, 1119, 253, 1097, 1087, 51, 1257, 1076, 1100, 1060, 1169, 1083, 1098},
            new int[]{15, 19, 221, 116}
         ),
         false
      );
      this.animate = new BooleanSetting("Анимация", true);
      this.offsetX = new SliderSetting("Сдвиг X", 0.0, -3.0, 3.0, 0.1);
      this.offsetY = new SliderSetting("Сдвиг Y", 0.0, -3.0, 3.0, 0.1);
      this.offsetZ = new SliderSetting("Сдвиг Z", 0.0, -3.0, 3.0, 0.1);
      this.scale = new SliderSetting("Масштаб", 1.0, 0.25, 3.0, 0.05);
      this.uL41mJ = new HashMap<>();
      this.psEhizr = new HashSet<>();
   }

   private static void nXFk4wf(String id, String name) {
      NAME_BY_ID.put(id, name);
      ID_BY_NAME.put(name, id);
   }

   private static void z47uwqR(ModeSetting setting, String... ids) {
      for (String id : ids) {
         String name = NAME_BY_ID.get(id);
         if (name != null) {
            setting.addAlias(id, name);
         }
      }
   }

   @Subscribe
   public void onWorldRender(EventWorldRender event) {
      if (mc.world != null && mc.player != null) {
         PulseCosmeticModel self = this.tulc();
         if (self != null && !mc.options.getPerspective().isFirstPerson()) {
            this.wthmpe(event.getMatrixStack(), mc.player, event.getTickDelta(), self);
         }

         if (this.showOnFriends.getValue() && self != null) {
            for (PlayerEntity player : mc.world.getPlayers()) {
               if (player != mc.player && FriendRepository.isFriend(player.getNameForScoreboard())) {
                  this.wthmpe(event.getMatrixStack(), player, event.getTickDelta(), self);
               }
            }
         }
      }
   }

   private PulseCosmeticModel tulc() {
      return this.kn8S9("wings", this.wings);
   }

   private PulseCosmeticModel kn8S9(String category, ModeSetting setting) {
      String value = setting.getValue();
      if (value != null && !value.isEmpty() && !value.equals("Выкл")) {
         String id = ID_BY_NAME.getOrDefault(value, value);
         String key = id + "/" + category;
         if (this.psEhizr.contains(key)) {
            return null;
         } else if (this.uL41mJ.containsKey(key)) {
            return this.uL41mJ.get(key);
         } else {
            try {
               PulseCosmeticModel model = PulseCosmeticModel.load(category, id);
               this.uL41mJ.put(key, model);
               return model;
            } catch (Throwable var7) {
               this.psEhizr.add(key);
               System.out.println("[Cosmetics] Failed to load " + key + ": " + var7);
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private void wthmpe(MatrixStack matrices, PlayerEntity player, float tickDelta, PulseCosmeticModel cosmetic) {
      if (cosmetic != null) {
         if (player instanceof AbstractClientPlayerEntity clientPlayer) {
            if ((Object)mc.getEntityRenderDispatcher().getRenderer(player) instanceof PlayerEntityRenderer renderer) {
               PlayerEntityRenderState state = renderer.createRenderState();
               renderer.updateRenderState(clientPlayer, state, tickDelta);
               PlayerEntityModel model = renderer.getModel();
               model.setAngles(state);
               matrices.push();
               Vec3d cam = mc.gameRenderer.getCamera().getPos();
               Vec3d pos = player.getLerpedPos(tickDelta);
               matrices.translate(pos.x - cam.x, pos.y - cam.y, pos.z - cam.z);
               if (state.sleepingDirection != null) {
                  float off = state.standingEyeHeight - 0.1F;
                  matrices.translate(-state.sleepingDirection.getOffsetX() * off, 0.0F, -state.sleepingDirection.getOffsetZ() * off);
               }

               float baseScale = state.baseScale;
               matrices.scale(baseScale, baseScale, baseScale);
               LivingEntityRendererAccessor accessor = (LivingEntityRendererAccessor)renderer;
               accessor.viola$setupTransforms(state, matrices, state.bodyYaw, baseScale);
               accessor.viola$scale(state, matrices);
               matrices.scale(-1.0F, -1.0F, 1.0F);
               matrices.translate(0.0F, -1.501F, 0.0F);
               model.getRootPart().rotate(matrices);
               matrices.push();
               PulseCosmeticModel.Transform t = cosmetic.getTransform();
               PulseCosmeticModel.Attachment attachment = cosmetic.getAttachment();
               if (attachment == PulseCosmeticModel.Attachment.ROOT && state.isInSneakingPose) {
                  matrices.translate(0.0F, 0.2F, 0.05F);
               }

               if (attachment == PulseCosmeticModel.Attachment.HEAD) {
                  model.head.rotate(matrices);
               } else if (attachment == PulseCosmeticModel.Attachment.BODY) {
                  model.body.rotate(matrices);
               }

               matrices.translate(t.x() + this.offsetX.getFloatValue(), t.y() + this.offsetY.getFloatValue(), t.z() + this.offsetZ.getFloatValue());
               if (t.yaw() != 0.0F) {
                  matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(t.yaw()));
               }

               if (t.pitch() != 0.0F) {
                  matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(t.pitch()));
               }

               if (t.roll() != 0.0F) {
                  matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(t.roll()));
               }

               float effScale = t.scale() * this.scale.getFloatValue();
               if (effScale != 1.0F) {
                  matrices.scale(effScale, effScale, effScale);
               }

               if (this.animate.getValue()) {
                  cosmetic.setupAnim(state);
               } else {
                  cosmetic.resetPose();
               }

               Immediate vcp = mc.getBufferBuilders().getEntityVertexConsumers();
               int light = WorldRenderer.getLightmapCoordinates(mc.world, player.getBlockPos());
               cosmetic.getRoot()
                  .render(matrices, vcp.getBuffer(RenderLayer.getEntityTranslucent(cosmetic.getTexture())), light, OverlayTexture.DEFAULT_UV, -1);
               matrices.pop();
               matrices.pop();
               vcp.draw();
            }
         }
      }
   }

   static {
      nXFk4wf("123", "Dark Wings");
      nXFk4wf("124", "Ghoul Wings");
      nXFk4wf("136", "Wings");
      nXFk4wf("25", "Vulcano Wings");
      nXFk4wf("61", "Angel Wings");
      nXFk4wf("62", "Baby Dragon Wings");
      nXFk4wf("63", "Dragon Wings Blue");
      nXFk4wf("64", "Spider Wings");
      nXFk4wf("65", "Easter Wings");
      nXFk4wf("82", "Vane Wings");
      nXFk4wf("86", "Trident Wings");
      nXFk4wf("91", "Wota Wings");
      nXFk4wf("99", "Rocker Wings");
      nXFk4wf("custom_bat_wings", "Bat Wings");
      nXFk4wf("custom_bee_wings", "Bee Wings");
      nXFk4wf("custom_shark_tail", "Shark Tail");
      nXFk4wf("custom_vin_wings", "Vin Wings");
   }
}
