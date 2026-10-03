package zov.viola.util.render.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.texture.AbstractTexture;
import org.lwjgl.opengl.GL30;

public class MipmapUtil {
   private static final Set<Integer> GENERATED = new HashSet<>();

   public static void ensure(AbstractTexture tex) {
      int glId = tex.getGlId();
      if (glId > 0) {
         if (!GENERATED.contains(glId)) {
            RenderSystem.bindTextureForSetup(glId);
            GL30.glGenerateMipmap(3553);
            GENERATED.add(glId);
         }
      }
   }
}
