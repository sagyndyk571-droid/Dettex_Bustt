package zov.viola.util.render;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zov.viola.obf.D;

public final class GifBackground {
   private static final Logger LOGGER = LoggerFactory.getLogger("ViolaGif");
   private static GifBackground y1z03c;
   private final NativeImageBackedTexture[] cZDgCL2;
   private final int[] cOru;
   private final int tt5W9U;
   private final float mze3aP;
   private final float w3SpJQ0;
   private final long bY0wFz;

   private GifBackground(List<BufferedImage> frames, List<Integer> delayMs) {
      int count = frames.size();
      this.cZDgCL2 = new NativeImageBackedTexture[count];
      this.cOru = new int[count];
      BufferedImage first = frames.get(0);
      this.mze3aP = first.getWidth();
      this.w3SpJQ0 = first.getHeight();
      int sum = 0;

      for (int i = 0; i < count; i++) {
         int d = delayMs.get(i);
         if (d <= 0) {
            d = 100;
         }

         this.cOru[i] = d;
         sum += d;
      }

      this.tt5W9U = sum;
      this.bY0wFz = System.currentTimeMillis();

      for (int i = 0; i < count; i++) {
         NativeImage img = mG2sswJ(frames.get(i));
         NativeImageBackedTexture tex = new NativeImageBackedTexture(img);
         tex.upload();
         this.cZDgCL2[i] = tex;
      }
   }

   public static GifBackground getInstance() {
      if (y1z03c == null) {
         MinecraftClient mc = MinecraftClient.getInstance();
         Identifier id = Identifier.of("mre", "textures/gui/title/menu_bg.gif");

         try (InputStream stream = mc.getResourceManager().getResourceOrThrow(id).getInputStream()) {
            y1z03c = gm80Y(stream);
         } catch (Exception var7) {
            LOGGER.error(
               D.k(
                  new int[]{
                     237, 88, 5, 86, 206, 93, 76, 78, 196, 25, 0, 85, 202, 93, 76, 91, 197, 80, 1, 91, 223, 92, 8, 26, 201, 88, 15, 81, 204, 75, 3, 79, 197, 93
                  },
                  new int[]{171, 57, 108, 58}
               ),
               var7
            );
         }
      }

      return y1z03c;
   }

   public static GifBackground getCached() {
      return y1z03c;
   }

   public static void invalidate() {
      y1z03c = null;
   }

   private static GifBackground gm80Y(InputStream stream) throws Exception {
      List<BufferedImage> frames = new ArrayList<>();
      List<Integer> delays = new ArrayList<>();
      Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
      if (!readers.hasNext()) {
         throw new IllegalStateException(
            D.k(
               new int[]{205, 204, 180, 154, 202, 229, 180, 175, 230, 194, 240, 184, 241, 131, 245, 171, 226, 202, 248, 188, 225, 207, 241},
               new int[]{131, 163, 148, 221}
            )
         );
      } else {
         ImageReader reader = readers.next();

         try (ImageInputStream iis = ImageIO.createImageInputStream(stream)) {
            reader.setInput(iis, false, true);
            int count = reader.getNumImages(true);
            BufferedImage canvas = null;

            for (int i = 0; i < count; i++) {
               BufferedImage frame = reader.read(i);
               if (canvas == null) {
                  canvas = new BufferedImage(frame.getWidth(), frame.getHeight(), 2);
               }

               canvas.getGraphics().drawImage(frame, 0, 0, null);
               frames.add(fF90(canvas));
               delays.add(hsl04(reader, i));
            }
         } finally {
            reader.dispose();
         }

         return new GifBackground(frames, delays);
      }
   }

   private static BufferedImage fF90(BufferedImage image) {
      BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), 2);
      copy.getGraphics().drawImage(image, 0, 0, null);
      return copy;
   }

   private static int hsl04(ImageReader reader, int index) {
      try {
         IIOMetadata meta = reader.getImageMetadata(index);
         if (meta == null) {
            return 100;
         }

         IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree("javax_imageio_gif_image_1.0");

         for (int i = 0; i < root.getLength(); i++) {
            if (root.item(i) instanceof IIOMetadataNode node && "GraphicControlExtension".equals(node.getNodeName())) {
               String delay = node.getAttribute("delayTime");
               if (delay != null && !delay.isEmpty()) {
                  return Math.max(10, Integer.parseInt(delay) * 10);
               }
            }
         }
      } catch (Exception var7) {
      }

      return 100;
   }

   private static NativeImage mG2sswJ(BufferedImage image) {
      int w = image.getWidth();
      int h = image.getHeight();
      NativeImage nativeImage = new NativeImage(Format.RGBA, w, h, false);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            nativeImage.setColorArgb(x, y, image.getRGB(x, y));
         }
      }

      return nativeImage;
   }

   public int getCurrentFrame() {
      if (this.cZDgCL2.length != 0 && this.tt5W9U > 0) {
         long elapsed = (System.currentTimeMillis() - this.bY0wFz) % this.tt5W9U;
         int acc = 0;

         for (int i = 0; i < this.cOru.length; i++) {
            acc += this.cOru[i];
            if (elapsed < acc) {
               return i;
            }
         }

         return this.cZDgCL2.length - 1;
      } else {
         return 0;
      }
   }

   public int getTextureId(int frame) {
      return this.cZDgCL2[frame].getGlId();
   }

   public int getFrameCount() {
      return this.cZDgCL2.length;
   }

   public float getWidth() {
      return this.mze3aP;
   }

   public float getHeight() {
      return this.w3SpJQ0;
   }
}
