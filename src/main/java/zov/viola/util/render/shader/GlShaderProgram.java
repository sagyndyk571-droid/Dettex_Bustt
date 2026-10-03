package zov.viola.util.render.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public final class GlShaderProgram {
   private final int hhe24eu;

   public GlShaderProgram(String vertexSource, String fragmentSource) {
      int vertexShader = aer6(35633, vertexSource);
      int fragmentShader = aer6(35632, fragmentSource);
      this.hhe24eu = GL20.glCreateProgram();
      GL20.glAttachShader(this.hhe24eu, vertexShader);
      GL20.glAttachShader(this.hhe24eu, fragmentShader);
      GL20.glLinkProgram(this.hhe24eu);
      MemoryStack stack = MemoryStack.stackPush();

      try {
         IntBuffer status = stack.mallocInt(1);
         GL20.glGetProgramiv(this.hhe24eu, 35714, status);
         if (status.get(0) == 0) {
            String log = GL20.glGetProgramInfoLog(this.hhe24eu);
            GL20.glDeleteShader(vertexShader);
            GL20.glDeleteShader(fragmentShader);
            GL20.glDeleteProgram(this.hhe24eu);
            throw new IllegalStateException("Program link failed: " + log);
         }
      } catch (Throwable var9) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (stack != null) {
         stack.close();
      }

      GL20.glDetachShader(this.hhe24eu, vertexShader);
      GL20.glDetachShader(this.hhe24eu, fragmentShader);
      GL20.glDeleteShader(vertexShader);
      GL20.glDeleteShader(fragmentShader);
   }

   public static GlShaderProgram resolve(String vertexPath, String fragmentPath) {
      return new GlShaderProgram(jt9P(vertexPath), jt9P(fragmentPath));
   }

   private static int aer6(int type, String source) {
      int shader = GL20.glCreateShader(type);
      GL20.glShaderSource(shader, source);
      GL20.glCompileShader(shader);
      MemoryStack stack = MemoryStack.stackPush();

      try {
         IntBuffer status = stack.mallocInt(1);
         GL20.glGetShaderiv(shader, 35713, status);
         if (status.get(0) == 0) {
            String log = GL20.glGetShaderInfoLog(shader);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException("Shader compile failed: " + log);
         }
      } catch (Throwable var7) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (stack != null) {
         stack.close();
      }

      return shader;
   }

   private static String jt9P(String path) {
      String clean = path.startsWith("/") ? path.substring(1) : path;
      ClassLoader loader = GlShaderProgram.class.getClassLoader();

      try {
         String var13;
         try (InputStream input = loader.getResourceAsStream(clean)) {
            if (input == null) {
               throw new IllegalStateException("Resource not found: " + path);
            }

            StringBuilder builder = new StringBuilder();

            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
               while ((line = reader.readLine()) != null) {
                  builder.append(line).append('\n');
               }
            }

            var13 = builder.toString();
         }

         return var13;
      } catch (IOException var12) {
         throw new RuntimeException("Failed to read resource: " + path, var12);
      }
   }

   public void use() {
      GL20.glUseProgram(this.hhe24eu);
   }

   public int getUniformLocation(String name) {
      return GL20.glGetUniformLocation(this.hhe24eu, name);
   }

   public void delete() {
      GL20.glDeleteProgram(this.hhe24eu);
   }
}
