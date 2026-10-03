package zov.viola.util.render.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import zov.viola.obf.D;

public final class CosmeticPack {
   private static final MinecraftClient MC = MinecraftClient.getInstance();
   private static final Map<String, CosmeticPack> CACHE = new HashMap<>();
   public final CosmeticPack.CosmeticDef def;
   public float cfgX;
   public float cfgY;
   public float cfgZ;
   public float cfgScale;
   public String attachment;
   public final List<CosmeticPack.Bone> bones = new ArrayList<>();
   public final Map<String, CosmeticPack.Bone> boneIndex = new HashMap<>();
   public final CosmeticPack.Animation anim = new CosmeticPack.Animation();
   public int texture = -1;
   public float texW = 64.0F;
   public float texH = 64.0F;

   private CosmeticPack(CosmeticPack.CosmeticDef def) {
      this.def = def;
      String base = "cosmetics/pulse/" + def.category() + "/" + def.id();
      this.jg4Mms(Identifier.of("wexvisuals", base + "/config.json"));
      this.q9mk(Identifier.of("wexvisuals", base + "/geometry.json"));
      this.dceIi(Identifier.of("wexvisuals", base + "/animation.json"));
      this.aseO(Identifier.of("wexvisuals", base + "/texture.png"));
   }

   public static CosmeticPack get(CosmeticPack.CosmeticDef def) {
      if (def == null) {
         return null;
      } else {
         String key = def.category() + "/" + def.id();
         if (!CACHE.containsKey(key)) {
            try {
               CosmeticPack pack = new CosmeticPack(def);
               CACHE.put(key, pack);
            } catch (Exception var3) {
               CACHE.put(key, null);
            }
         }

         return CACHE.get(key);
      }
   }

   private void jg4Mms(Identifier id) {
      JsonObject o = d3I3ob(id);
      if (o == null) {
         this.cfgX = 0.0F;
         this.cfgY = 0.0F;
         this.cfgZ = 0.0F;
         this.cfgScale = 1.0F;
         this.attachment = "body";
      } else {
         this.cfgX = txHGt(o, "x", 0.0F);
         this.cfgY = txHGt(o, "y", 0.0F);
         this.cfgZ = txHGt(o, "z", 0.0F);
         this.cfgScale = txHGt(o, "scale", 1.0F);
         this.attachment = o.has("attachment") ? o.get("attachment").getAsString() : "body";
      }
   }

   private void q9mk(Identifier id) {
      JsonObject o = d3I3ob(id);
      if (o != null) {
         JsonArray geos = o.getAsJsonArray(
            "minecraft:geometry"
         );
         if (geos != null && !geos.isEmpty()) {
            JsonObject geo = geos.get(0).getAsJsonObject();
            if (geo.has("description")) {
               JsonObject desc = geo.get("description").getAsJsonObject();
               this.texW = desc.has("texture_width") ? desc.get("texture_width").getAsFloat() : 64.0F;
               this.texH = desc.has("texture_height") ? desc.get("texture_height").getAsFloat() : 64.0F;
            }

            JsonArray array = geo.getAsJsonArray("bones");
            if (array != null) {
               for (JsonElement el : array) {
                  JsonObject b = el.getAsJsonObject();
                  CosmeticPack.Bone bone = CosmeticPack.Bone.parse(b);
                  this.bones.add(bone);
                  this.boneIndex.put(bone.name, bone);
               }
            }
         }
      }
   }

   private void dceIi(Identifier id) {
      JsonObject o = d3I3ob(id);
      if (o != null && o.has("animations")) {
         JsonObject anims = o.get("animations").getAsJsonObject();
         if (anims.size() != 0) {
            JsonObject a = ((JsonElement)((Entry)anims.entrySet().iterator().next()).getValue()).getAsJsonObject();
            this.anim.loop = a.has("loop") && a.get("loop").getAsBoolean();
            this.anim.length = a.has("animation_length") ? a.get("animation_length").getAsFloat() : 1.0F;
            if (a.has("bones")) {
               JsonObject bonesAnim = a.get("bones").getAsJsonObject();

               for (Entry<String, JsonElement> entry : bonesAnim.entrySet()) {
                  JsonObject boneAnim = entry.getValue().getAsJsonObject();

                  for (String channel : new String[]{"position", "rotation", "scale"}) {
                     if (boneAnim.has(channel)) {
                        JsonObject frames = boneAnim.get(channel).getAsJsonObject();
                        TreeMap<Float, float[]> keys = new TreeMap<>();

                        for (Entry<String, JsonElement> f : frames.entrySet()) {
                           float t = Float.parseFloat(f.getKey());
                           JsonArray v = f.getValue().getAsJsonArray();
                           float x = v.size() > 0 ? v.get(0).getAsFloat() : 0.0F;
                           float y = v.size() > 1 ? v.get(1).getAsFloat() : 0.0F;
                           float z = v.size() > 2 ? v.get(2).getAsFloat() : 0.0F;
                           keys.put(t, new float[]{x, y, z});
                        }

                        if (!keys.isEmpty()) {
                           this.anim.bones.computeIfAbsent(entry.getKey(), k -> new CosmeticPack.BoneAnim()).channels.put(channel, keys);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void aseO(Identifier id) {
      try {
         Optional<Resource> resource = MC.getResourceManager().getResource(id);
         if (resource.isEmpty()) {
            return;
         }

         try (InputStream in = resource.get().getInputStream()) {
            NativeImage image = NativeImage.read(in);
            NativeImageBackedTexture tex = new NativeImageBackedTexture(image);
            tex.upload();
            this.texture = tex.getGlId();
         }
      } catch (Exception var8) {
         this.texture = -1;
      }
   }

   public void render(MatrixStack stack, float time) {
      if (this.texture >= 0) {
         float t = this.anim.length > 0.0F ? time % this.anim.length : 0.0F;
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
         RenderSystem.setShaderTexture(0, this.texture);
         BufferBuilder buf = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         this.bG03(stack, buf, null, null, t);
         BufferRenderer.drawWithGlobalProgram(buf.end());
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
      }
   }

   private void bG03(MatrixStack stack, BufferBuilder buf, Matrix4f mat, String parent, float time) {
      for (CosmeticPack.Bone bone : this.bones) {
         if (parent == null ? bone.parent == null : bone.parent != null && bone.parent.equals(parent)) {
            this.wCtlTd7(stack, buf, mat, bone, time);
         }
      }
   }

   private void wCtlTd7(MatrixStack stack, BufferBuilder buf, Matrix4f mat, CosmeticPack.Bone bone, float time) {
      float[] pos = this.anim.get(bone.name, "position", time);
      float[] rot = this.anim.get(bone.name, "rotation", time);
      float[] scl = this.anim.get(bone.name, "scale", time);
      stack.push();
      float px = bone.pivotX;
      float py = bone.pivotY;
      float pz = bone.pivotZ;
      if (pos != null) {
         stack.translate(pos[0] * 0.0625F, pos[1] * 0.0625F, pos[2] * 0.0625F);
      }

      stack.translate(px * 0.0625F, py * 0.0625F, pz * 0.0625F);
      float sx = scl != null ? scl[0] : 1.0F;
      float sy = scl != null ? scl[1] : 1.0F;
      float sz = scl != null ? scl[2] : 1.0F;
      stack.scale(sx, sy, sz);
      float rx = rot != null ? rot[0] : bone.rotX;
      float ry = rot != null ? rot[1] : bone.rotY;
      float rz = rot != null ? rot[2] : bone.rotZ;
      if (rx != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rx));
      }

      if (ry != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ry));
      }

      if (rz != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rz));
      }

      stack.translate(-px * 0.0625F, -py * 0.0625F, -pz * 0.0625F);

      for (CosmeticPack.Cube cube : bone.cubes) {
         this.kEOovc(stack, buf, cube);
      }

      for (CosmeticPack.Mesh mesh : bone.meshes) {
         this.yoh08fK(stack, buf, mesh);
      }

      this.bG03(stack, buf, stack.peek().getPositionMatrix(), bone.name, time);
      stack.pop();
   }

   private void kEOovc(MatrixStack stack, BufferBuilder buf, CosmeticPack.Cube cube) {
      stack.push();
      float cpx = cube.pivotX;
      float cpy = cube.pivotY;
      float cpz = cube.pivotZ;
      stack.translate(cpx * 0.0625F, cpy * 0.0625F, cpz * 0.0625F);
      if (cube.rotX != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cube.rotX));
      }

      if (cube.rotY != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cube.rotY));
      }

      if (cube.rotZ != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(cube.rotZ));
      }

      stack.translate(-cpx * 0.0625F, -cpy * 0.0625F, -cpz * 0.0625F);
      float ox = (cube.originX - cube.inflate) * 0.0625F;
      float oy = (cube.originY - cube.inflate) * 0.0625F;
      float oz = (cube.originZ - cube.inflate) * 0.0625F;
      float dx = (cube.sizeX + cube.inflate * 2.0F) * 0.0625F;
      float dy = (cube.sizeY + cube.inflate * 2.0F) * 0.0625F;
      float dz = (cube.sizeZ + cube.inflate * 2.0F) * 0.0625F;
      Matrix4f m = stack.peek().getPositionMatrix();
      this.n4j2(buf, m, ky8w(ox + dx, oy + dy, oz, ox + dx, oy, oz, ox, oy, oz, ox, oy + dy, oz), cube.uv("east"));
      this.n4j2(buf, m, ky8w(ox, oy + dy, oz + dz, ox, oy, oz + dz, ox + dx, oy, oz + dz, ox + dx, oy + dy, oz + dz), cube.uv("west"));
      this.n4j2(buf, m, ky8w(ox, oy + dy, oz, ox, oy, oz, ox, oy, oz + dz, ox, oy + dy, oz + dz), cube.uv("south"));
      this.n4j2(buf, m, ky8w(ox + dx, oy + dy, oz + dz, ox + dx, oy, oz + dz, ox + dx, oy, oz, ox + dx, oy + dy, oz), cube.uv("north"));
      this.n4j2(buf, m, ky8w(ox, oy + dy, oz + dz, ox + dx, oy + dy, oz + dz, ox + dx, oy + dy, oz, ox, oy + dy, oz), cube.uv("up"));
      this.n4j2(buf, m, ky8w(ox, oy, oz, ox + dx, oy, oz, ox + dx, oy, oz + dz, ox, oy, oz + dz), cube.uv("down"));
      stack.pop();
   }

   private void yoh08fK(MatrixStack stack, BufferBuilder buf, CosmeticPack.Mesh mesh) {
      stack.push();
      stack.translate(mesh.pivotX * 0.0625F, mesh.pivotY * 0.0625F, mesh.pivotZ * 0.0625F);
      if (mesh.rotX != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mesh.rotX));
      }

      if (mesh.rotY != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(mesh.rotY));
      }

      if (mesh.rotZ != 0.0F) {
         stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(mesh.rotZ));
      }

      stack.translate(-mesh.pivotX * 0.0625F, -mesh.pivotY * 0.0625F, -mesh.pivotZ * 0.0625F);
      Matrix4f m = stack.peek().getPositionMatrix();

      for (CosmeticPack.MeshFace f : mesh.faces) {
         if (f.v().length >= 4 && f.uv().length >= 4) {
            float[][] v = new float[4][];

            for (int i = 0; i < 4; i++) {
               v[i] = f.v()[i];
            }

            float[][] uv = new float[4][];

            for (int i = 0; i < 4; i++) {
               uv[i] = new float[]{f.uv()[i][0] / this.texW, f.uv()[i][1] / this.texH};
            }

            for (int i = 0; i < 4; i++) {
               float[] p = v[i];
               buf.vertex(m, p[0], p[1], p[2]).texture(uv[i][0], uv[i][1]).color(255, 255, 255, 255);
            }
         }
      }

      stack.pop();
   }

   private void n4j2(BufferBuilder buf, Matrix4f m, float[] v, float[] u) {
      if (u == null) {
         u = new float[]{0.0F, 0.0F, 1.0F, 1.0F};
      }

      float u0 = u[0];
      float v0 = u[1];
      float u1 = u[2];
      float v1 = u[3];
      buf.vertex(m, v[0], v[1], v[2]).texture(u0, v0).color(255, 255, 255, 255);
      buf.vertex(m, v[3], v[4], v[5]).texture(u1, v0).color(255, 255, 255, 255);
      buf.vertex(m, v[6], v[7], v[8]).texture(u1, v1).color(255, 255, 255, 255);
      buf.vertex(m, v[9], v[10], v[11]).texture(u0, v1).color(255, 255, 255, 255);
   }

   private static float[] ky8w(float ax, float ay, float az, float bx, float by, float bz, float cx, float cy, float cz, float dx, float dy, float dz) {
      return new float[]{ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz};
   }

   private static float[] maldb(JsonObject o, String key, float[] def) {
      if (!o.has(key)) {
         return def;
      } else {
         JsonArray a = o.get(key).getAsJsonArray();
         return new float[]{
            a.size() > 0 ? a.get(0).getAsFloat() : def[0], a.size() > 1 ? a.get(1).getAsFloat() : def[1], a.size() > 2 ? a.get(2).getAsFloat() : def[2]
         };
      }
   }

   private static float txHGt(JsonObject o, String key, float def) {
      try {
         return o.has(key) ? o.get(key).getAsFloat() : def;
      } catch (Exception var4) {
         return def;
      }
   }

   private static JsonObject d3I3ob(Identifier id) {
      try {
         Optional<Resource> res = MC.getResourceManager().getResource(id);
         return res.isEmpty() ? null : JsonParser.parseReader(res.get().getReader()).getAsJsonObject();
      } catch (Exception var2) {
         return null;
      }
   }

   private static float bh5lCd(float a, float b, float f) {
      return a + (b - a) * f;
   }

   public static final class Animation {
      public boolean loop = true;
      public float length = 1.0F;
      public final Map<String, CosmeticPack.BoneAnim> bones = new HashMap<>();

      float[] get(String bone, String channel, float time) {
         CosmeticPack.BoneAnim ba = this.bones.get(bone);
         if (ba == null) {
            return null;
         } else {
            TreeMap<Float, float[]> keys = ba.channels.get(channel);
            if (keys != null && !keys.isEmpty()) {
               List<Entry<Float, float[]>> list = new ArrayList<>(keys.entrySet());
               float t = time % this.length;
               Entry<Float, float[]> first = list.get(0);
               Entry<Float, float[]> last = list.get(list.size() - 1);
               if (t <= first.getKey()) {
                  return first.getValue();
               } else if (t >= last.getKey()) {
                  return last.getValue();
               } else {
                  for (int i = 1; i < list.size(); i++) {
                     Entry<Float, float[]> cur = list.get(i);
                     if (t <= cur.getKey()) {
                        Entry<Float, float[]> prev = list.get(i - 1);
                        float span = cur.getKey() - prev.getKey();
                        float f = span <= 0.0F ? 0.0F : (t - prev.getKey()) / span;
                        float[] a = prev.getValue();
                        float[] b = cur.getValue();
                        return new float[]{CosmeticPack.bh5lCd(a[0], b[0], f), CosmeticPack.bh5lCd(a[1], b[1], f), CosmeticPack.bh5lCd(a[2], b[2], f)};
                     }
                  }

                  return last.getValue();
               }
            } else {
               return null;
            }
         }
      }
   }

   public static final class Bone {
      public final String name;
      public final String parent;
      public final float pivotX;
      public final float pivotY;
      public final float pivotZ;
      public final float rotX;
      public final float rotY;
      public final float rotZ;
      public final List<CosmeticPack.Cube> cubes = new ArrayList<>();
      public final List<CosmeticPack.Mesh> meshes = new ArrayList<>();

      Bone(String name, String parent, float px, float py, float pz, float rx, float ry, float rz) {
         this.name = name;
         this.parent = parent;
         this.pivotX = px;
         this.pivotY = py;
         this.pivotZ = pz;
         this.rotX = rx;
         this.rotY = ry;
         this.rotZ = rz;
      }

      static CosmeticPack.Bone parse(JsonObject o) {
         String name = o.has("name") ? o.get("name").getAsString() : "";
         String parent = o.has("parent") ? o.get("parent").getAsString() : null;
         float[] p = CosmeticPack.maldb(o, "pivot", new float[]{0.0F, 0.0F, 0.0F});
         float[] r = CosmeticPack.maldb(o, "rotation", new float[]{0.0F, 0.0F, 0.0F});
         CosmeticPack.Bone b = new CosmeticPack.Bone(name, parent, p[0], p[1], p[2], r[0], r[1], r[2]);
         if (o.has("cubes")) {
            for (JsonElement el : o.get("cubes").getAsJsonArray()) {
               b.cubes.add(CosmeticPack.Cube.parse(el.getAsJsonObject()));
            }
         }

         if (o.has("meshes")) {
            for (JsonElement el : o.get("meshes").getAsJsonArray()) {
               b.meshes.add(CosmeticPack.Mesh.parse(el.getAsJsonObject()));
            }
         }

         return b;
      }
   }

   public static final class BoneAnim {
      public final Map<String, TreeMap<Float, float[]>> channels = new HashMap<>();
   }

   public record CosmeticDef(String category, String id, String display) {


      

      

      
   }

   public static final class Cube {
      public final float originX;
      public final float originY;
      public final float originZ;
      public final float sizeX;
      public final float sizeY;
      public final float sizeZ;
      public final float pivotX;
      public final float pivotY;
      public final float pivotZ;
      public final float rotX;
      public final float rotY;
      public final float rotZ;
      public final float inflate;
      private final Map<String, float[]> m0pQ = new HashMap<>();

      Cube(float ox, float oy, float oz, float sx, float sy, float sz, float px, float py, float pz, float rx, float ry, float rz, float inflate) {
         this.originX = ox;
         this.originY = oy;
         this.originZ = oz;
         this.sizeX = sx;
         this.sizeY = sy;
         this.sizeZ = sz;
         this.pivotX = px;
         this.pivotY = py;
         this.pivotZ = pz;
         this.rotX = rx;
         this.rotY = ry;
         this.rotZ = rz;
         this.inflate = inflate;
      }

      float[] uv(String face) {
         float[] u = this.m0pQ.get(face);
         return u == null ? null : (float[])u.clone();
      }

      static CosmeticPack.Cube parse(JsonObject o) {
         float[] origin = CosmeticPack.maldb(o, "origin", new float[]{0.0F, 0.0F, 0.0F});
         float[] size = CosmeticPack.maldb(o, "size", new float[]{0.0F, 0.0F, 0.0F});
         float[] pivot = CosmeticPack.maldb(o, "pivot", new float[]{origin[0] + size[0] / 2.0F, origin[1] + size[1] / 2.0F, origin[2] + size[2] / 2.0F});
         float[] rot = CosmeticPack.maldb(o, "rotation", new float[]{0.0F, 0.0F, 0.0F});
         float inflate = o.has("inflate") ? o.get("inflate").getAsFloat() : 0.0F;
         CosmeticPack.Cube c = new CosmeticPack.Cube(
            origin[0], origin[1], origin[2], size[0], size[1], size[2], pivot[0], pivot[1], pivot[2], rot[0], rot[1], rot[2], inflate
         );
         if (o.has("uv")) {
            JsonElement uv = o.get("uv");
            if (uv.isJsonObject()) {
               for (String face : new String[]{"north", "south", "east", "west", "up", "down"}) {
                  if (uv.getAsJsonObject().has(face)) {
                     JsonObject fo = uv.getAsJsonObject().get(face).getAsJsonObject();
                     float u = fo.has("uv") ? fo.get("uv").getAsJsonArray().get(0).getAsFloat() : 0.0F;
                     float v = fo.has("uv") ? fo.get("uv").getAsJsonArray().get(1).getAsFloat() : 0.0F;
                     float w = fo.has("uv_size") ? fo.get("uv_size").getAsJsonArray().get(0).getAsFloat() : y8LTo(face, c, 0);
                     float h = fo.has("uv_size") ? fo.get("uv_size").getAsJsonArray().get(1).getAsFloat() : y8LTo(face, c, 1);
                     c.m0pQ.put(face, new float[]{u, v, u + w, v + h});
                  }
               }
            } else if (uv.isJsonArray()) {
               float u = uv.getAsJsonArray().get(0).getAsFloat();
               float v = uv.getAsJsonArray().get(1).getAsFloat();
               float w = c.sizeX;
               float h = c.sizeY;
               c.m0pQ.put("north", new float[]{u, v, u + w, v + h});
               c.m0pQ.put("south", new float[]{u, v, u + w, v + h});
            }
         }

         return c;
      }

      private static float y8LTo(String face, CosmeticPack.Cube c, int axis) {
         return switch (face) {
            case "north", "south" -> axis == 0 ? c.sizeX : c.sizeY;
            case "east", "west" -> axis == 0 ? c.sizeZ : c.sizeY;
            default -> axis == 0 ? c.sizeX : c.sizeZ;
         };
      }
   }

   public static final class Mesh {
      public final float pivotX;
      public final float pivotY;
      public final float pivotZ;
      public final float rotX;
      public final float rotY;
      public final float rotZ;
      public final float[][] vertices;
      public final CosmeticPack.MeshFace[] faces;

      Mesh(float px, float py, float pz, float rx, float ry, float rz, float[][] vertices, CosmeticPack.MeshFace[] faces) {
         this.pivotX = px;
         this.pivotY = py;
         this.pivotZ = pz;
         this.rotX = rx;
         this.rotY = ry;
         this.rotZ = rz;
         this.vertices = vertices;
         this.faces = faces;
      }

      static CosmeticPack.Mesh parse(JsonObject o) {
         float[] pivot = CosmeticPack.maldb(o, "pivot", new float[]{0.0F, 0.0F, 0.0F});
         float[] rot = CosmeticPack.maldb(o, "rotation", new float[]{0.0F, 0.0F, 0.0F});
         JsonArray vArr = o.getAsJsonArray("vertices");
         float[][] verts = new float[vArr.size()][];

         for (int i = 0; i < vArr.size(); i++) {
            JsonArray p = vArr.get(i).getAsJsonArray();
            verts[i] = new float[]{p.get(0).getAsFloat(), p.get(1).getAsFloat(), p.get(2).getAsFloat()};
         }

         JsonArray fArr = o.getAsJsonArray("faces");
         CosmeticPack.MeshFace[] faces = new CosmeticPack.MeshFace[fArr.size()];

         for (int i = 0; i < fArr.size(); i++) {
            JsonObject fo = fArr.get(i).getAsJsonObject();
            JsonArray idx = fo.getAsJsonArray("vertices");
            int[] indices = new int[idx.size()];

            for (int j = 0; j < idx.size(); j++) {
               indices[j] = idx.get(j).getAsInt();
            }

            float[][] fverts = new float[indices.length][];

            for (int j = 0; j < indices.length; j++) {
               fverts[j] = verts[indices[j]];
            }

            JsonArray uArr = fo.getAsJsonArray("uv");
            float[][] uvs = new float[uArr.size()][];

            for (int j = 0; j < uArr.size(); j++) {
               JsonArray u = uArr.get(j).getAsJsonArray();
               uvs[j] = new float[]{u.get(0).getAsFloat(), u.get(1).getAsFloat()};
            }

            faces[i] = new CosmeticPack.MeshFace(fverts, uvs);
         }

         return new CosmeticPack.Mesh(pivot[0], pivot[1], pivot[2], rot[0], rot[1], rot[2], verts, faces);
      }
   }

   public record MeshFace(float[][] v, float[][] uv) {

      

      

      
   }
}
