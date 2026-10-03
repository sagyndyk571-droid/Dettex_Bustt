package zov.viola.module.list.render;

import net.minecraft.client.option.GraphicsMode;
import net.minecraft.particle.ParticlesMode;
import zov.viola.Viola;
import zov.viola.module.Module;
import zov.viola.module.ModuleCategory;
import zov.viola.module.ModuleInformation;
import zov.viola.module.settings.BooleanSetting;
import zov.viola.module.settings.SliderSetting;
import zov.viola.obf.D;

@ModuleInformation(
   moduleName = "Optimization",
   moduleDesc = "Максимальный FPS под ваше железо, всё настраивается",
   moduleCategory = ModuleCategory.RENDER
)
public class Optimization extends Module {
   public final BooleanSetting cullEntities = new BooleanSetting(
      D.k(
         new int[]{1268, 1152, 1173, 1232, 1197, 1271, 1257, 1245, 1247, 226, 1173, 1190, 1187, 1279, 1258, 1188, 1192, 1271, 1261},
         new int[]{234, 194, 212, 229}
      ),
      true
   );
   public final SliderSetting entityDist = new SliderSetting(
      D.k(
         new int[]{1188, 1205, 1245, 1157, 1152, 1200, 1242, 1279, 1279, 173, 1245, 1156, 1273, 1200, 1186, 1158, 1266, 1208, 1189},
         new int[]{176, 141, 156, 199}
      ),
      40.0,
      10.0,
      100.0,
      1.0
   );
   public final BooleanSetting cullBlockEntities = new BooleanSetting(
      D.k(
         new int[]{1048, 1217, 1232, 1076, 1089, 1206, 1196, 1081, 1075, 163, 1184, 1082, 1080, 1209, 188, 1100, 1083, 1217, 1185, 1080, 1092, 1211},
         new int[]{6, 131, 145, 1}
      ),
      true
   );
   public final SliderSetting blockEntityDist = new SliderSetting(
      D.k(
         new int[]{1075, 1255, 1206, 1259, 1047, 1250, 1201, 1169, 1128, 255, 1222, 1170, 1049, 1253, 218, 1252, 1050, 1181, 1223, 1168, 1125, 1255},
         new int[]{39, 223, 247, 169}
      ),
      32.0,
      10.0,
      80.0,
      1.0
   );
   public final BooleanSetting particles = new BooleanSetting("Частицы", true);
   public final SliderSetting particlePercentS = new SliderSetting(
      "Частицы %", 30.0, 5.0, 100.0, 1.0
   );
   public final BooleanSetting noBlockBreak = new BooleanSetting(
      "Блёск при ломании", true
   );
   public final BooleanSetting noShadows = new BooleanSetting(
      "Тени сущностей", true
   );
   public final BooleanSetting noWeather = new BooleanSetting("Погода", true);
   public final BooleanSetting noClouds = new BooleanSetting("Облака", true);
   public final BooleanSetting mcSettings = new BooleanSetting(
      "Настройки MC", true
   );
   public final SliderSetting renderDist = new SliderSetting(
      D.k(
         new int[]{1026, 1052, 1095, 1034, 1062, 1049, 1088, 1136, 1113, 4, 1081, 1032, 1064, 1124, 1086, 1033, 1064, 1046, 1084, 1136},
         new int[]{22, 36, 6, 72}
      ),
      4.0,
      2.0,
      16.0,
      1.0
   );
   private boolean sst6;
   private Integer vRviJw;
   private GraphicsMode ltk16XU;
   private ParticlesMode qZqlQ;
   private Boolean qapb7s;
   private Boolean eDoS1i;
   private boolean m0swlBr;

   @Override
   public void onEnable() {
      super.onEnable();
      if (isVeryWeakHardware()) {
         this.rWqmoFf();
      }

      if (!this.sst6 && this.mc.options != null && this.mcSettings.getValue()) {
         this.vRviJw = this.mc.options.getViewDistance().getValue();
         this.ltk16XU = this.mc.options.getGraphicsMode().getValue();
         this.qZqlQ = this.mc.options.getParticles().getValue();
         this.qapb7s = this.mc.options.getAo().getValue();
         this.eDoS1i = this.mc.options.getEntityShadows().getValue();
         this.mc.options.getViewDistance().setValue((int)Math.round(this.renderDist.getValue()));
         this.mc.options.getGraphicsMode().setValue(GraphicsMode.FAST);
         this.mc.options.getParticles().setValue(ParticlesMode.MINIMAL);
         this.mc.options.getAo().setValue(false);
         this.mc.options.getEntityShadows().setValue(false);
         this.mc.options.write();
         this.sst6 = true;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.sst6) {
         if (this.vRviJw != null) {
            this.mc.options.getViewDistance().setValue(this.vRviJw);
         }

         if (this.ltk16XU != null) {
            this.mc.options.getGraphicsMode().setValue(this.ltk16XU);
         }

         if (this.qZqlQ != null) {
            this.mc.options.getParticles().setValue(this.qZqlQ);
         }

         if (this.qapb7s != null) {
            this.mc.options.getAo().setValue(this.qapb7s);
         }

         if (this.eDoS1i != null) {
            this.mc.options.getEntityShadows().setValue(this.eDoS1i);
         }

         this.mc.options.write();
         this.sst6 = false;
      }
   }

   private void rWqmoFf() {
      if (!this.m0swlBr) {
         this.m0swlBr = true;
         this.entityDist.setValue(24.0);
         this.blockEntityDist.setValue(20.0);
         this.particlePercentS.setValue(10.0);
         this.renderDist.setValue(2.0);
      }
   }

   public static Optimization getInstance() {
      try {
         return Viola.getInstance().getModuleStorage().get(Optimization.class);
      } catch (Exception var1) {
         return null;
      }
   }

   private static Optimization e2t5q7h() {
      Optimization m = getInstance();
      return m != null && m.isEnabled() ? m : null;
   }

   public static boolean isNoShadows() {
      Optimization m = e2t5q7h();
      return m != null && m.noShadows.getValue();
   }

   public static boolean isNoWeather() {
      Optimization m = e2t5q7h();
      return m != null && m.noWeather.getValue();
   }

   public static boolean isNoClouds() {
      Optimization m = e2t5q7h();
      return m != null && m.noClouds.getValue();
   }

   public static boolean isNoBlockBreak() {
      Optimization m = e2t5q7h();
      return m != null && m.noBlockBreak.getValue();
   }

   public static int particlePercent() {
      Optimization m = e2t5q7h();
      return m != null && m.particles.getValue() ? (int)Math.round(m.particlePercentS.getValue()) : 100;
   }

   public static double entityMaxDistance() {
      Optimization m = e2t5q7h();
      return m != null && m.cullEntities.getValue() ? m.entityDist.getValue() : 0.0;
   }

   public static double blockEntityMaxDistance() {
      Optimization m = e2t5q7h();
      return m != null && m.cullBlockEntities.getValue() ? m.blockEntityDist.getValue() : 0.0;
   }

   public static boolean isWeakHardware() {
      int cores = Runtime.getRuntime().availableProcessors();
      long mem = Runtime.getRuntime().maxMemory();
      return cores <= 4 || mem <= 8589934592L;
   }

   public static boolean isVeryWeakHardware() {
      int cores = Runtime.getRuntime().availableProcessors();
      long mem = Runtime.getRuntime().maxMemory();
      return cores <= 3 || mem <= 4294967296L;
   }
}
