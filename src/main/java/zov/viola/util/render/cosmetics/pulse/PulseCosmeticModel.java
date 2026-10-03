package zov.viola.util.render.cosmetics.pulse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.model.ModelPart.Quad;
import net.minecraft.client.model.ModelPart.Vertex;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.joml.Vector4f;
import zov.viola.obf.D;

public final class PulseCosmeticModel {
   private static final MinecraftClient MC = MinecraftClient.getInstance();
   private static final List<Entry<String, Direction>> FACE_DIRECTIONS = List.of(
      Map.entry("down", Direction.DOWN),
      Map.entry("up", Direction.UP),
      Map.entry("west", Direction.WEST),
      Map.entry("north", Direction.NORTH),
      Map.entry("east", Direction.EAST),
      Map.entry("south", Direction.SOUTH)
   );
   private static final float[] LOCAL_ORIGIN = new float[]{0.0F, 0.0F, 0.0F};
   private static final float[] PLAYER_ORIGIN = new float[]{0.0F, 24.0F, 0.0F};
   private static final float[] ZERO_ROTATION = new float[]{0.0F, 0.0F, 0.0F};
   private static final float HEAD_TOP_Y = -0.5F;
   private static final float HAT_SINK_PIXELS = 2.0F;
   private static final float PET_CLEARANCE_X = 0.375F;
   private static final float PET_SINK_PIXELS = 1.5F;
   private static final int ANIMATION_SAMPLES = 12;
   private static final List<String> RESTING_TOKENS = List.of("idle", "main", "anim");
   private static final float BODY_CENTER_Y = 0.375F;
   private static final float BODY_FRONT_Z = -0.125F;
   private final ModelPart fJ39;
   private final PulseCosmeticModel.Transform kect8Z;
   private final PulseCosmeticModel.Attachment t5b5qC;
   private final Map<String, ModelPart> uQC1;
   private final List<PulseCosmeticModel.Animation> z8lvKs;
   private final Identifier jmt6x;

   private PulseCosmeticModel(
      ModelPart root,
      PulseCosmeticModel.Transform transform,
      PulseCosmeticModel.Attachment attachment,
      Map<String, ModelPart> bones,
      List<PulseCosmeticModel.Animation> animations,
      Identifier texture
   ) {
      this.fJ39 = root;
      this.kect8Z = transform;
      this.t5b5qC = attachment;
      this.uQC1 = bones;
      this.z8lvKs = animations;
      this.jmt6x = texture;
   }

   public static PulseCosmeticModel load(String category, String id) {
      String root = "cosmetics/pulse/" + category + "/" + id;
      JsonObject geometryDocument = m3IIg(Identifier.of("wexvisuals", root + "/geometry.json"));
      JsonObject config = m3IIg(Identifier.of("wexvisuals", root + "/config.json"));
      JsonObject animationDocument = o5w4i(Identifier.of("wexvisuals", root + "/animation.json"));
      JsonObject geometry = geometryDocument.getAsJsonArray(
            "minecraft:geometry"
         )
         .get(0)
         .getAsJsonObject();
      JsonObject description = geometry.getAsJsonObject("description");
      float textureWidth = description.get("texture_width").getAsFloat();
      float textureHeight = description.get("texture_height").getAsFloat();
      JsonArray bonesJson = geometry.getAsJsonArray("bones");
      boolean playerOrigin = config.get("player_origin") != null && config.get("player_origin").getAsBoolean();
      PulseCosmeticModel.Geometry baked = hH9u(bonesJson, textureWidth, textureHeight, playerOrigin ? PLAYER_ORIGIN : c1n3(category));
      float configuredScale = mNDFsk(config, "scale", 1.0F);
      PulseCosmeticModel.Attachment attachment = vt5Ml(category, config);
      List<PulseCosmeticModel.Animation> animations = aGmaskg(animationDocument);
      float[] placement = m8ad(category, baked, animations, configuredScale, config);
      Identifier textureId = Identifier.of("wexvisuals", root + "/texture.png");
      lB8Npif(textureId);
      return new PulseCosmeticModel(
         baked.getRoot(),
         new PulseCosmeticModel.Transform(
            placement[0], placement[1], placement[2], configuredScale, mNDFsk(config, "yaw", 0.0F), mNDFsk(config, "pitch", 0.0F), mNDFsk(config, "roll", 0.0F)
         ),
         attachment,
         baked.getBones(),
         animations,
         textureId
      );
   }

   private static void lB8Npif(Identifier id) {
      try {
         Optional<Resource> resource = MC.getResourceManager().getResource(id);
         if (resource.isPresent()) {
            try (InputStream in = resource.get().getInputStream()) {
               NativeImage image = NativeImage.read(in);
               MC.getTextureManager().registerTexture(id, new NativeImageBackedTexture(image));
            }
         }
      } catch (Exception var7) {
      }
   }

   public ModelPart getRoot() {
      return this.fJ39;
   }

   public PulseCosmeticModel.Transform getTransform() {
      return this.kect8Z;
   }

   public PulseCosmeticModel.Attachment getAttachment() {
      return this.t5b5qC;
   }

   public Identifier getTexture() {
      return this.jmt6x;
   }

   public void setupAnim(PlayerEntityRenderState state) {
      this.resetPose();
      PulseCosmeticModel.Animation animation = this.n6boj(state);
      if (animation != null) {
         float time = animation.yt14r > 0.0F ? state.age / 20.0F % animation.yt14r : 0.0F;
         v0f6(this.uQC1, animation, time);
      }
   }

   public void resetPose() {
      this.fJ39.traverse().forEach(ModelPart::resetTransform);
   }

   private static float[] c1n3(String category) {
      return "pet".equals(category) ? PLAYER_ORIGIN : LOCAL_ORIGIN;
   }

   private static float[] m8ad(
      String category, PulseCosmeticModel.Geometry baked, List<PulseCosmeticModel.Animation> animations, float scale, JsonObject config
   ) {
      float[] resting = new float[]{mNDFsk(config, "x", 0.0F), -mNDFsk(config, "y", 0.0F), mNDFsk(config, "z", 0.0F)};
      if ("wings".equals(category)) {
         return new float[]{resting[0], resting[1] + 0.25F, resting[2]};
      } else {
         PulseCosmeticModel.Bounds restingExtents = hhpJy(baked.getRoot(), scale);
         if (kloeO9(config)) {
            return zfA00gc(category, resting, restingExtents, config);
         } else {
            PulseCosmeticModel.Bounds bounds = qEbf(baked, animations, scale);
            if (bounds == null) {
               return resting;
            } else {
               float bottom = restingExtents != null ? restingExtents.getMaxY() : bounds.getMaxY();
               switch (category) {
                  case "hat":
                     return new float[]{-bounds.getCenterX(), -0.5F + mNDFsk(config, "sink", 2.0F) / 16.0F - bottom, -bounds.getCenterZ()};
                  case "pet":
                     float petX = bounds.getCenterX() < 0.0F ? -0.375F - bounds.getMaxX() : 0.375F - bounds.getMinX();
                     return new float[]{petX, 0.09375F - bottom, -bounds.getCenterZ()};
                  case "bodywear":
                     return new float[]{-bounds.getCenterX(), 0.375F - bounds.getCenterY(), -0.125F - bounds.getMinZ()};
                  default:
                     return resting;
               }
            }
         }
      }
   }

   private static float[] zfA00gc(String category, float[] configured, PulseCosmeticModel.Bounds resting, JsonObject config) {
      if ("hat".equals(category) && resting != null) {
         float rest = -0.5F + mNDFsk(config, "sink", 2.0F) / 16.0F;
         return resting.getMaxY() + configured[1] >= rest ? configured : new float[]{configured[0], rest - resting.getMaxY(), configured[2]};
      } else {
         return configured;
      }
   }

   private static boolean kloeO9(JsonObject config) {
      if (config.has("attachment")) {
         JsonElement playerOrigin = config.get("player_origin");
         if (playerOrigin == null || !playerOrigin.getAsBoolean()) {
            return true;
         }
      }

      return false;
   }

   private static PulseCosmeticModel.Bounds qEbf(PulseCosmeticModel.Geometry baked, List<PulseCosmeticModel.Animation> animations, float scale) {
      PulseCosmeticModel.Bounds bounds = hhpJy(baked.getRoot(), scale);
      PulseCosmeticModel.Animation resting = oo1xQcq(animations);
      if (resting != null) {
         for (int step = 0; step < 13; step++) {
            baked.getRoot().resetTransform();
            v0f6(baked.getBones(), resting, resting.getLength() * step / 12.0F);
            bounds = lrra(bounds, hhpJy(baked.getRoot(), scale));
         }

         baked.getRoot().resetTransform();
      }

      return bounds;
   }

   private static PulseCosmeticModel.Animation oo1xQcq(List<PulseCosmeticModel.Animation> animations) {
      List<PulseCosmeticModel.Animation> usable = new ArrayList<>();

      for (PulseCosmeticModel.Animation it : animations) {
         if (!it.getName().toLowerCase(Locale.ROOT).contains("gui")) {
            usable.add(it);
         }
      }

      if (usable.isEmpty()) {
         return null;
      } else {
         for (PulseCosmeticModel.Animation animation : usable) {
            String name = animation.getName().toLowerCase(Locale.ROOT);
            boolean resting = false;

            for (String token : RESTING_TOKENS) {
               if (name.contains(token)) {
                  resting = true;
                  break;
               }
            }

            if (resting && !name.contains("sneak") && !name.contains("water")) {
               return animation;
            }
         }

         return usable.get(0);
      }
   }

   private static PulseCosmeticModel.Bounds hhpJy(ModelPart root, float scale) {
      float[] minX = new float[]{Float.POSITIVE_INFINITY};
      float[] minY = new float[]{Float.POSITIVE_INFINITY};
      float[] minZ = new float[]{Float.POSITIVE_INFINITY};
      float[] maxX = new float[]{Float.NEGATIVE_INFINITY};
      float[] maxY = new float[]{Float.NEGATIVE_INFINITY};
      float[] maxZ = new float[]{Float.NEGATIVE_INFINITY};
      root.forEachCuboid(new MatrixStack(), (entry, path, index, cuboid) -> {
         for (Quad quad : cuboid.sides) {
            for (Vertex vertex : quad.vertices()) {
               Vector3f pos = vertex.pos();
               Vector4f out = entry.getPositionMatrix().transform(new Vector4f(pos.x(), pos.y(), pos.z(), 1.0F));
               minX[0] = Math.min(minX[0], out.x());
               minY[0] = Math.min(minY[0], out.y());
               minZ[0] = Math.min(minZ[0], out.z());
               maxX[0] = Math.max(maxX[0], out.x());
               maxY[0] = Math.max(maxY[0], out.y());
               maxZ[0] = Math.max(maxZ[0], out.z());
            }
         }
      });
      return Math.abs(minX[0]) <= Float.MAX_VALUE && Math.abs(maxX[0]) <= Float.MAX_VALUE
         ? new PulseCosmeticModel.Bounds(minX[0] * scale, minY[0] * scale, minZ[0] * scale, maxX[0] * scale, maxY[0] * scale, maxZ[0] * scale)
         : null;
   }

   private static PulseCosmeticModel.Bounds lrra(PulseCosmeticModel.Bounds a, PulseCosmeticModel.Bounds b) {
      if (a == null) {
         return b;
      } else {
         return b == null
            ? a
            : new PulseCosmeticModel.Bounds(
               Math.min(a.getMinX(), b.getMinX()),
               Math.min(a.getMinY(), b.getMinY()),
               Math.min(a.getMinZ(), b.getMinZ()),
               Math.max(a.getMaxX(), b.getMaxX()),
               Math.max(a.getMaxY(), b.getMaxY()),
               Math.max(a.getMaxZ(), b.getMaxZ())
            );
      }
   }

   private static void v0f6(Map<String, ModelPart> bones, PulseCosmeticModel.Animation animation, float time) {
      Iterator var3 = animation.getTracks().entrySet().iterator();

      while (true) {
         PulseCosmeticModel.BoneTrack track;
         ModelPart part;
         do {
            if (!var3.hasNext()) {
               return;
            }

            Entry<String, PulseCosmeticModel.BoneTrack> entry = (Entry<String, PulseCosmeticModel.BoneTrack>)var3.next();
            String boneName = entry.getKey();
            track = entry.getValue();
            part = bones.get(boneName);
            if (part != null) {
               break;
            }

            for (Entry<String, ModelPart> it : bones.entrySet()) {
               if (it.getKey().equalsIgnoreCase(boneName)) {
                  part = it.getValue();
                  break;
               }
            }
         } while (part == null);

         if (track.getRotation() != null) {
            float[] rotation = track.getRotation().sample(time);
            if (rotation != null) {
               part.pitch = part.pitch + kgDdC7(rotation[0]);
               part.yaw = part.yaw + kgDdC7(rotation[1]);
               part.roll = part.roll + kgDdC7(rotation[2]);
            }
         }

         if (track.getPosition() != null) {
            float[] position = track.getPosition().sample(time);
            if (position != null) {
               part.pivotX = part.pivotX + position[0];
               part.pivotY = part.pivotY - position[1];
               part.pivotZ = part.pivotZ + position[2];
            }
         }

         if (track.getScale() != null) {
            float[] scale = track.getScale().sample(time);
            if (scale != null) {
               part.xScale = part.xScale * scale[0];
               part.yScale = part.yScale * scale[1];
               part.zScale = part.zScale * scale[2];
            }
         }
      }
   }

   private PulseCosmeticModel.Animation n6boj(PlayerEntityRenderState state) {
      if (this.z8lvKs.isEmpty()) {
         return null;
      } else {
         List<PulseCosmeticModel.Animation> usable = new ArrayList<>();

         for (PulseCosmeticModel.Animation it : this.z8lvKs) {
            if (!it.getName().toLowerCase(Locale.ROOT).contains("gui")) {
               usable.add(it);
            }
         }

         if (usable.isEmpty()) {
            return this.z8lvKs.get(0);
         } else {
            List<String> movementTokens;
            if (state.isGliding) {
               movementTokens = List.of("elytra", "flying");
            } else if (state.isSwimming) {
               movementTokens = List.of("swimming", "swimmings", "water_deep", "water");
            } else if (state.limbFrequency > 0.04F) {
               movementTokens = List.of("moving", "walking", "walk", "run");
            } else {
               movementTokens = List.of("idle", "main", "anim", "animation");
            }

            List<PulseCosmeticModel.Animation> candidates = new ArrayList<>();

            for (PulseCosmeticModel.Animation animation : usable) {
               String name = animation.getName().toLowerCase(Locale.ROOT);
               boolean matched = false;

               for (String token : movementTokens) {
                  if (name.contains(token)) {
                     matched = true;
                     break;
                  }
               }

               if (matched) {
                  candidates.add(animation);
               }
            }

            if (candidates.isEmpty()) {
               candidates = usable;
            }

            PulseCosmeticModel.Animation best = null;
            int bestScore = Integer.MIN_VALUE;

            for (PulseCosmeticModel.Animation candidate : candidates) {
               int score = dsWI5(state, candidate, movementTokens);
               if (best == null || score > bestScore) {
                  best = candidate;
                  bestScore = score;
               }
            }

            return best;
         }
      }
   }

   private static int dsWI5(PlayerEntityRenderState state, PulseCosmeticModel.Animation animation, List<String> movementTokens) {
      String name = animation.getName().toLowerCase(Locale.ROOT);
      int score = 0;
      if (state.sneaking && name.contains("sneak")) {
         score += 4;
      }

      if (!state.sneaking && !name.contains("sneak")) {
         score += 2;
      }

      if (state.touchingWater && name.contains("water")) {
         score += 3;
      }

      if (!state.touchingWater && !name.contains("water")) {
         score++;
      }

      boolean matched = false;

      for (String token : movementTokens) {
         if (name.contains(token)) {
            matched = true;
            break;
         }
      }

      if (matched) {
         score += 8;
      }

      return score;
   }

   private static PulseCosmeticModel.Attachment vt5Ml(String category, JsonObject config) {
      switch (category) {
         case "hat":
            return PulseCosmeticModel.Attachment.HEAD;
         case "pet":
            return PulseCosmeticModel.Attachment.ROOT;
         case "bodywear":
            return PulseCosmeticModel.Attachment.BODY;
         default:
            JsonElement attachmentEl = config.get("attachment");
            if (attachmentEl != null) {
               String attachment = attachmentEl.getAsString().toLowerCase(Locale.ROOT);
               switch (attachment) {
                  case "body":
                     return PulseCosmeticModel.Attachment.BODY;
                  case "head":
                     return PulseCosmeticModel.Attachment.HEAD;
                  case "root":
                     return PulseCosmeticModel.Attachment.ROOT;
               }
            }

            JsonElement posEl = config.get("pos");
            return xf0od(category, posEl != null ? posEl.getAsInt() : 1);
      }
   }

   private static PulseCosmeticModel.Attachment xf0od(String category, int position) {
      if (position == 2 || "hat".equals(category)) {
         return PulseCosmeticModel.Attachment.HEAD;
      } else {
         return !"wings".equals(category) && !"bodywear".equals(category) ? PulseCosmeticModel.Attachment.ROOT : PulseCosmeticModel.Attachment.BODY;
      }
   }

   private static JsonObject m3IIg(Identifier id) {
      Optional<Resource> resource = MC.getResourceManager().getResource(id);
      if (resource.isEmpty()) {
         throw new IllegalStateException("Missing PulseVisuals cosmetic resource: " + id);
      } else {
         try {
            JsonObject var3;
            try (InputStream in = resource.get().getInputStream()) {
               var3 = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            }

            return var3;
         } catch (IOException var7) {
            throw new IllegalStateException(var7);
         }
      }
   }

   private static JsonObject o5w4i(Identifier id) {
      Optional<Resource> resource = MC.getResourceManager().getResource(id);
      if (resource.isEmpty()) {
         return null;
      } else {
         try {
            JsonObject var3;
            try (InputStream in = resource.get().getInputStream()) {
               var3 = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            }

            return var3;
         } catch (IOException var7) {
            return null;
         }
      }
   }

   private static PulseCosmeticModel.Geometry hH9u(JsonArray bones, float textureWidth, float textureHeight, float[] referencePivot) {
      List<JsonObject> boneObjects = new ArrayList<>();

      for (JsonElement el : bones) {
         boneObjects.add(el.getAsJsonObject());
      }

      Map<String, List<JsonObject>> childrenByParent = new LinkedHashMap<>();

      for (JsonObject bone : boneObjects) {
         JsonElement parent = bone.get("parent");
         String key = parent != null ? parent.getAsString() : null;
         childrenByParent.computeIfAbsent(key, k -> new ArrayList<>()).add(bone);
      }

      Map<String, ModelPart> bakedBones = new LinkedHashMap<>();
      Map<String, ModelPart> rootChildren = new LinkedHashMap<>();
      List<JsonObject> rootBones = childrenByParent.get(null);
      if (rootBones != null) {
         for (JsonObject bone : rootBones) {
            rootChildren.put(bone.get("name").getAsString(), sopHxV6(childrenByParent, textureWidth, textureHeight, bakedBones, bone, referencePivot));
         }
      }

      ModelPart root = new ModelPart(Collections.emptyList(), rootChildren);
      root = stjJG(root, ModelTransform.NONE);
      return new PulseCosmeticModel.Geometry(root, bakedBones);
   }

   private static ModelPart sopHxV6(
      Map<String, List<JsonObject>> childrenByParent,
      float textureWidth,
      float textureHeight,
      Map<String, ModelPart> bakedBones,
      JsonObject bone,
      float[] parentPivot
   ) {
      String name = bone.get("name").getAsString();
      float[] pivot = c8j4gjl(bone, "pivot", 3);
      Map<String, ModelPart> children = new LinkedHashMap<>();
      JsonArray cubes = bone.getAsJsonArray("cubes");
      if (cubes != null) {
         for (int i = 0; i < cubes.size(); i++) {
            children.put("cube_" + i, oXf3vg(cubes.get(i).getAsJsonObject(), pivot, textureWidth, textureHeight));
         }
      }

      JsonArray meshes = bone.getAsJsonArray("meshes");
      if (meshes != null) {
         for (int i = 0; i < meshes.size(); i++) {
            children.put("mesh_" + i, dv2p(meshes.get(i).getAsJsonObject(), pivot, textureWidth, textureHeight));
         }
      }

      List<JsonObject> childBones = childrenByParent.get(name);
      if (childBones != null) {
         for (JsonObject child : childBones) {
            children.put(child.get("name").getAsString(), sopHxV6(childrenByParent, textureWidth, textureHeight, bakedBones, child, pivot));
         }
      }

      float[] rotation = c8j4gjl(bone, "rotation", 3);
      ModelPart part = new ModelPart(Collections.emptyList(), children);
      part = stjJG(
         part,
         ModelTransform.of(
            pivot[0] - parentPivot[0], parentPivot[1] - pivot[1], pivot[2] - parentPivot[2], kgDdC7(rotation[0]), kgDdC7(rotation[1]), kgDdC7(rotation[2])
         )
      );
      bakedBones.put(name, part);
      return part;
   }

   private static ModelPart oXf3vg(JsonObject cubeJson, float[] bonePivot, float textureWidth, float textureHeight) {
      float[] origin = c8j4gjl(cubeJson, "origin", 3);
      float[] size = c8j4gjl(cubeJson, "size", 3);
      float[] pivot = cubeJson.has("pivot") ? c8j4gjl(cubeJson, "pivot", 3) : bonePivot;
      float[] rotation = cubeJson.has("rotation") ? c8j4gjl(cubeJson, "rotation", 3) : ZERO_ROTATION;
      float inflate = mNDFsk(cubeJson, "inflate", 0.0F);
      JsonElement mirrorEl = cubeJson.get("mirror");
      boolean mirror = mirrorEl != null && mirrorEl.getAsBoolean();
      float localX = origin[0] - pivot[0];
      float localY = pivot[1] - origin[1] - size[1];
      float localZ = origin[2] - pivot[2];
      JsonElement uv = cubeJson.get("uv");
      Cuboid cube;
      if (uv != null && uv.isJsonObject()) {
         cube = y3Vgv(uv.getAsJsonObject(), localX, localY, localZ, size, inflate, mirror, textureWidth, textureHeight);
      } else {
         JsonArray uvArr = null;
         if (uv != null) {
            uvArr = uv.isJsonArray() ? uv.getAsJsonArray() : null;
         }

         float[] offset = uvArr != null ? r9pd(uvArr, 2) : new float[]{0.0F, 0.0F};
         cube = new Cuboid(
            (int)offset[0],
            (int)offset[1],
            localX,
            localY,
            localZ,
            size[0],
            size[1],
            size[2],
            inflate,
            inflate,
            inflate,
            mirror,
            textureWidth,
            textureHeight,
            EnumSet.allOf(Direction.class)
         );
      }

      ModelPart part = new ModelPart(Collections.singletonList(cube), Collections.emptyMap());
      return stjJG(
         part,
         ModelTransform.of(
            pivot[0] - bonePivot[0], bonePivot[1] - pivot[1], pivot[2] - bonePivot[2], kgDdC7(rotation[0]), kgDdC7(rotation[1]), kgDdC7(rotation[2])
         )
      );
   }

   private static ModelPart dv2p(JsonObject meshJson, float[] bonePivot, float textureWidth, float textureHeight) {
      float[] pivot = c8j4gjl(meshJson, "pivot", 3);
      float[] rotation = c8j4gjl(meshJson, "rotation", 3);
      JsonArray verticesArr = meshJson.getAsJsonArray("vertices");
      List<float[]> sourceVertices = new ArrayList<>();

      for (JsonElement el : verticesArr) {
         sourceVertices.add(r9pd(el.getAsJsonArray(), 3));
      }

      JsonArray facesArr = meshJson.getAsJsonArray("faces");
      List<Cuboid> cubes = new ArrayList<>();

      for (JsonElement element : facesArr) {
         JsonObject face = element.getAsJsonObject();
         JsonArray indicesArr = face.getAsJsonArray("vertices");
         List<Integer> indices = new ArrayList<>();

         for (JsonElement idxEl : indicesArr) {
            indices.add(idxEl.getAsInt());
         }

         JsonArray uvArray = face.getAsJsonArray("uv");
         int faceCount = indices.size();
         if (faceCount >= 3 && faceCount < 5 && uvArray.size() == faceCount) {
            Vertex[] vertices = new Vertex[faceCount];

            for (int i = 0; i < faceCount; i++) {
               float[] source = sourceVertices.get(indices.get(i));
               JsonArray uv = uvArray.get(i).getAsJsonArray();
               vertices[i] = new Vertex(
                  source[0] - pivot[0],
                  pivot[1] - source[1],
                  source[2] - pivot[2],
                  uv.get(0).getAsFloat() / textureWidth,
                  uv.get(1).getAsFloat() / textureHeight
               );
            }

            Vector3f first = new Vector3f(
               vertices[1].pos().x() - vertices[0].pos().x(), vertices[1].pos().y() - vertices[0].pos().y(), vertices[1].pos().z() - vertices[0].pos().z()
            );
            Vector3f second = new Vector3f(
               vertices[2].pos().x() - vertices[0].pos().x(), vertices[2].pos().y() - vertices[0].pos().y(), vertices[2].pos().z() - vertices[0].pos().z()
            );
            Vector3f normal = first.cross(second).normalize();
            Cuboid cube = new Cuboid(
               0, 0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, false, textureWidth, textureHeight, EnumSet.of(Direction.NORTH)
            );
            cube.sides[0] = new Quad(vertices, normal);
            cubes.add(cube);
         }
      }

      ModelPart part = new ModelPart(cubes, Collections.emptyMap());
      return stjJG(
         part,
         ModelTransform.of(
            pivot[0] - bonePivot[0], bonePivot[1] - pivot[1], pivot[2] - bonePivot[2], kgDdC7(rotation[0]), kgDdC7(rotation[1]), kgDdC7(rotation[2])
         )
      );
   }

   private static Cuboid y3Vgv(JsonObject uv, float x, float y, float z, float[] size, float inflate, boolean mirror, float textureWidth, float textureHeight) {
      List<Entry<String, Direction>> faces = new ArrayList<>();

      for (Entry<String, Direction> face : FACE_DIRECTIONS) {
         if (uv.has(face.getKey())) {
            faces.add(face);
         }
      }

      EnumSet<Direction> directions = EnumSet.noneOf(Direction.class);

      for (Entry<String, Direction> facex : faces) {
         directions.add(facex.getValue());
      }

      Cuboid cube = new Cuboid(0, 0, x, y, z, size[0], size[1], size[2], inflate, inflate, inflate, mirror, textureWidth, textureHeight, directions);

      for (int i = 0; i < faces.size(); i++) {
         Entry<String, Direction> facex = faces.get(i);
         JsonObject faceObj = uv.getAsJsonObject(facex.getKey());
         float[] start = r9pd(faceObj.getAsJsonArray("uv"), 2);
         float[] extent = r9pd(faceObj.getAsJsonArray("uv_size"), 2);
         Quad polygon = new Quad(
            cube.sides[i].vertices(), start[0], start[1], start[0] + extent[0], start[1] + extent[1], textureWidth, textureHeight, mirror, facex.getValue()
         );
         JsonElement rotationEl = faceObj.get("uv_rotation");
         int base = rotationEl != null ? rotationEl.getAsInt() / 90 : 0;
         int quarterTurns = normalizeQuarterTurns(base);
         if (quarterTurns != 0) {
            Vertex[] src = polygon.vertices();
            Vertex[] remapped = new Vertex[src.length];

            for (int j = 0; j < src.length; j++) {
               Vertex uvSource = src[(j + quarterTurns) % src.length];
               remapped[j] = src[j].remap(uvSource.u(), uvSource.v());
            }

            polygon = new Quad(remapped, polygon.direction());
         }

         cube.sides[i] = polygon;
      }

      return cube;
   }

   private static int normalizeQuarterTurns(int turns) {
      int remapped = 4;
      int v = turns % remapped;
      return v + (remapped & ((v ^ remapped) & (v | -v)) >> 31);
   }

   private static List<PulseCosmeticModel.Animation> aGmaskg(JsonObject root) {
      if (root != null && root.has("animations")) {
         JsonObject animations = root.getAsJsonObject("animations");
         List<PulseCosmeticModel.Animation> result = new ArrayList<>();

         for (Entry<String, JsonElement> entry : animations.entrySet()) {
            String name = entry.getKey();
            JsonObject json = entry.getValue().getAsJsonObject();
            Map<String, PulseCosmeticModel.BoneTrack> tracks = new LinkedHashMap<>();
            JsonObject bones = json.getAsJsonObject("bones");
            if (bones != null) {
               for (Entry<String, JsonElement> bone : bones.entrySet()) {
                  JsonObject boneObj = bone.getValue().getAsJsonObject();
                  tracks.put(
                     bone.getKey(),
                     new PulseCosmeticModel.BoneTrack(d7K2iT(boneObj.get("rotation")), d7K2iT(boneObj.get("position")), d7K2iT(boneObj.get("scale")))
                  );
               }
            }

            JsonElement lengthEl = json.get("animation_length");
            float explicitLength = lengthEl != null ? lengthEl.getAsFloat() : 0.0F;
            float inferredLength = 0.0F;

            for (PulseCosmeticModel.BoneTrack track : tracks.values()) {
               if (track.getRotation() != null) {
                  inferredLength = Math.max(inferredLength, track.getRotation().lastTime());
               }

               if (track.getPosition() != null) {
                  inferredLength = Math.max(inferredLength, track.getPosition().lastTime());
               }

               if (track.getScale() != null) {
                  inferredLength = Math.max(inferredLength, track.getScale().lastTime());
               }
            }

            result.add(new PulseCosmeticModel.Animation(name, Math.max(explicitLength, Math.max(inferredLength, 0.05F)), tracks));
         }

         return result;
      } else {
         return Collections.emptyList();
      }
   }

   private static PulseCosmeticModel.Channel d7K2iT(JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         if (!element.isJsonArray() && !element.isJsonPrimitive()) {
            List<PulseCosmeticModel.Keyframe> keyframes = new ArrayList<>();

            for (Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
               Float time = r8x2(entry.getKey());
               if (time != null) {
                  JsonElement value = entry.getValue();
                  if (!value.isJsonObject()) {
                     float[] vector = bFe302(value, ZERO_ROTATION);
                     keyframes.add(new PulseCosmeticModel.Keyframe(time, vector, vector, "linear", null));
                  } else {
                     JsonObject json = value.getAsJsonObject();
                     float[] fallback;
                     if (json.get("vector") != null) {
                        fallback = bFe302(json.get("vector"), ZERO_ROTATION);
                     } else if (json.get("post") != null) {
                        fallback = bFe302(json.get("post"), ZERO_ROTATION);
                     } else if (json.get("pre") != null) {
                        fallback = bFe302(json.get("pre"), ZERO_ROTATION);
                     } else {
                        fallback = ZERO_ROTATION;
                     }

                     float[] pre = json.get("pre") != null ? bFe302(json.get("pre"), fallback) : Arrays.copyOf(fallback, fallback.length);
                     float[] post = json.get("post") != null ? bFe302(json.get("post"), fallback) : Arrays.copyOf(fallback, fallback.length);
                     String interpolation = json.get("lerp_mode") != null ? json.get("lerp_mode").getAsString() : "linear";
                     String easing = json.get("easing") != null ? json.get("easing").getAsString() : null;
                     keyframes.add(new PulseCosmeticModel.Keyframe(time, pre, post, interpolation, easing));
                  }
               }
            }

            if (keyframes.isEmpty()) {
               return null;
            } else {
               keyframes.sort((a, b) -> Float.compare(a.mSGkG, b.mSGkG));
               return new PulseCosmeticModel.Channel(keyframes);
            }
         } else {
            float[] vector = bFe302(element, new float[]{0.0F, 0.0F, 0.0F});
            return new PulseCosmeticModel.Channel(List.of(new PulseCosmeticModel.Keyframe(0.0F, vector, vector, "linear", null)));
         }
      } else {
         return null;
      }
   }

   private static float[] bFe302(JsonElement element, float[] fallback) {
      if (!element.isJsonArray()) {
         if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            float v = element.getAsFloat();
            return new float[]{v, v, v};
         } else {
            return Arrays.copyOf(fallback, fallback.length);
         }
      } else {
         JsonArray values = element.getAsJsonArray();
         float[] out = new float[3];
         switch (values.size()) {
            case 0:
               return Arrays.copyOf(fallback, fallback.length);
            case 1:
               for (int i = 0; i < 3; i++) {
                  out[i] = values.get(0).getAsFloat();
               }

               return out;
            default:
               for (int i = 0; i < 3; i++) {
                  out[i] = values.get(Math.min(i, values.size() - 1)).getAsFloat();
               }

               return out;
         }
      }
   }

   private static Float r8x2(String key) {
      try {
         return Float.parseFloat(key);
      } catch (NumberFormatException var2) {
         return null;
      }
   }

   private static float i0Fwuo(float p0, float p1, float p2, float p3, float t) {
      float t2 = t * t;
      float t3 = t2 * t;
      return 0.5F * (2.0F * p1 + (-p0 + p2) * t + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * t2 + (-p0 + 3.0F * p1 - 3.0F * p2 + p3) * t3);
   }

   private static ModelPart stjJG(ModelPart part, ModelTransform pose) {
      part.setDefaultTransform(pose);
      part.resetTransform();
      return part;
   }

   private static float[] c8j4gjl(JsonObject object, String name, int expectedSize) {
      float[] out = new float[expectedSize];
      JsonElement element = object.has(name) ? object.get(name) : null;
      if (element == null || element.isJsonNull()) {
         return out;
      } else if (element.isJsonPrimitive()) {
         float v = element.getAsFloat();
         Arrays.fill(out, v);
         return out;
      } else {
         JsonArray array = element.isJsonArray() ? element.getAsJsonArray() : null;
         if (array != null) {
            int size = Math.min(array.size(), expectedSize);

            for (int i = 0; i < size; i++) {
               out[i] = array.get(i).getAsFloat();
            }
         }

         return out;
      }
   }

   private static float[] r9pd(JsonArray array, int expectedSize) {
      if (array == null) {
         return new float[expectedSize];
      } else {
         float[] out = new float[expectedSize];
         int size = Math.min(array.size(), expectedSize);

         for (int i = 0; i < size; i++) {
            out[i] = array.get(i).getAsFloat();
         }

         return out;
      }
   }

   private static float mNDFsk(JsonObject object, String name, float fallback) {
      JsonElement element = object.get(name);
      return element != null && !element.isJsonNull() ? element.getAsFloat() : fallback;
   }

   private static float kgDdC7(float value) {
      return (float)Math.toRadians(value);
   }

   public static final class Animation {
      private final String jH1t;
      private final float yt14r;
      private final Map<String, PulseCosmeticModel.BoneTrack> bzy6y;

      Animation(String name, float length, Map<String, PulseCosmeticModel.BoneTrack> tracks) {
         this.jH1t = name;
         this.yt14r = length;
         this.bzy6y = tracks;
      }

      public String getName() {
         return this.jH1t;
      }

      public float getLength() {
         return this.yt14r;
      }

      public Map<String, PulseCosmeticModel.BoneTrack> getTracks() {
         return this.bzy6y;
      }
   }

   public static enum Attachment {
      ROOT,
      HEAD,
      BODY;
   }

   public static final class BoneTrack {
      private final PulseCosmeticModel.Channel sxaM;
      private final PulseCosmeticModel.Channel pR7a;
      private final PulseCosmeticModel.Channel d90a;

      BoneTrack(PulseCosmeticModel.Channel rotation, PulseCosmeticModel.Channel position, PulseCosmeticModel.Channel scale) {
         this.sxaM = rotation;
         this.pR7a = position;
         this.d90a = scale;
      }

      public PulseCosmeticModel.Channel getRotation() {
         return this.sxaM;
      }

      public PulseCosmeticModel.Channel getPosition() {
         return this.pR7a;
      }

      public PulseCosmeticModel.Channel getScale() {
         return this.d90a;
      }
   }

   public static final class Bounds {
      private final float ejqf;
      private final float y3X0kwg;
      private final float fK2ma7D;
      private final float diwd58q;
      private final float euIzdR;
      private final float widtv64;

      Bounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
         this.ejqf = minX;
         this.y3X0kwg = minY;
         this.fK2ma7D = minZ;
         this.diwd58q = maxX;
         this.euIzdR = maxY;
         this.widtv64 = maxZ;
      }

      public float getMinX() {
         return this.ejqf;
      }

      public float getMinY() {
         return this.y3X0kwg;
      }

      public float getMinZ() {
         return this.fK2ma7D;
      }

      public float getMaxX() {
         return this.diwd58q;
      }

      public float getMaxY() {
         return this.euIzdR;
      }

      public float getMaxZ() {
         return this.widtv64;
      }

      public float getCenterX() {
         return (this.ejqf + this.diwd58q) * 0.5F;
      }

      public float getCenterY() {
         return (this.y3X0kwg + this.euIzdR) * 0.5F;
      }

      public float getCenterZ() {
         return (this.fK2ma7D + this.widtv64) * 0.5F;
      }
   }

   public static final class Channel {
      private final List<PulseCosmeticModel.Keyframe> d73x;

      Channel(List<PulseCosmeticModel.Keyframe> keyframes) {
         this.d73x = keyframes;
      }

      public float lastTime() {
         PulseCosmeticModel.Keyframe last = this.d73x.isEmpty() ? null : this.d73x.get(this.d73x.size() - 1);
         return last != null ? last.mSGkG : 0.0F;
      }

      public float[] sample(float time) {
         int size = this.d73x.size();
         if (size != 1 && !(time <= this.d73x.get(0).mSGkG)) {
            int nextIndex = -1;

            for (int i = 0; i < size; i++) {
               if (this.d73x.get(i).mSGkG > time) {
                  nextIndex = i;
                  break;
               }
            }

            if (nextIndex < 0) {
               PulseCosmeticModel.Keyframe last = this.d73x.get(size - 1);
               return Arrays.copyOf(last.vt03e, last.vt03e.length);
            } else {
               PulseCosmeticModel.Keyframe previous = this.d73x.get(nextIndex - 1);
               PulseCosmeticModel.Keyframe next = this.d73x.get(nextIndex);
               float span = Math.max(next.mSGkG - previous.mSGkG, 1.0E-4F);
               float progress = (time - previous.mSGkG) / span;
               progress = Math.max(0.0F, Math.min(1.0F, progress));
               if ("easeInOutQuad".equals(previous.qJj18c)) {
                  if (progress < 0.5F) {
                     progress = 2.0F * progress * progress;
                  } else {
                     float eased = -2.0F * progress + 2.0F;
                     progress = 1.0F - eased * eased * 0.5F;
                  }
               }

               float[] result = new float[3];
               if ("catmullrom".equalsIgnoreCase(previous.gbxw)) {
                  float[] before = this.d73x.get(Math.max(nextIndex - 2, 0)).vt03e;
                  float[] after = this.d73x.get(Math.min(nextIndex + 1, size - 1)).vt03e;

                  for (int ix = 0; ix < 3; ix++) {
                     result[ix] = PulseCosmeticModel.i0Fwuo(before[ix], previous.vt03e[ix], next.vt03e[ix], after[ix], progress);
                  }
               } else {
                  for (int ix = 0; ix < 3; ix++) {
                     result[ix] = previous.vt03e[ix] + (next.vt03e[ix] - previous.vt03e[ix]) * progress;
                  }
               }

               return result;
            }
         } else {
            PulseCosmeticModel.Keyframe first = this.d73x.get(0);
            return Arrays.copyOf(first.vt03e, first.vt03e.length);
         }
      }
   }

   public static final class Geometry {
      private final ModelPart n84Pl;
      private final Map<String, ModelPart> h35gS;

      Geometry(ModelPart root, Map<String, ModelPart> bones) {
         this.n84Pl = root;
         this.h35gS = bones;
      }

      public ModelPart getRoot() {
         return this.n84Pl;
      }

      public Map<String, ModelPart> getBones() {
         return this.h35gS;
      }
   }

   public static final class Keyframe {
      private final float mSGkG;
      private final float[] rlSc;
      private final float[] vt03e;
      private final String gbxw;
      private final String qJj18c;

      Keyframe(float time, float[] pre, float[] post, String interpolation, String easing) {
         this.mSGkG = time;
         this.rlSc = pre;
         this.vt03e = post;
         this.gbxw = interpolation;
         this.qJj18c = easing;
      }

      public float getTime() {
         return this.mSGkG;
      }

      public float[] getPre() {
         return this.rlSc;
      }

      public float[] getPost() {
         return this.vt03e;
      }

      public String getInterpolation() {
         return this.gbxw;
      }

      public String getEasing() {
         return this.qJj18c;
      }
   }

   public record Transform(float x, float y, float z, float scale, float yaw, float pitch, float roll) {




      

      

      

      

      

      

      
   }
}
