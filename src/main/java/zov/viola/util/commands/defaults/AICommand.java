package zov.viola.util.commands.defaults;

import java.util.List;
import java.util.stream.Stream;
import zov.viola.Viola;
import zov.viola.obf.D;
import zov.viola.util.chat.ChatUtil;
import zov.viola.util.commands.api.Command;
import zov.viola.util.commands.api.argument.IArgConsumer;
import zov.viola.util.commands.api.exception.CommandException;
import zov.viola.util.neuro.rotation.AIRotationManager;
import zov.viola.util.neuro.rotation.AIRotationRecorder;

public class AICommand extends Command {
   private static AIRotationRecorder me908 = null;

   public AICommand() {
      super("ai");
   }

   @Override
   public void execute(String label, IArgConsumer args) throws CommandException {
      if (!args.hasAny()) {
         this.t9Va();
      } else {
         String subcommand = args.getString().toLowerCase();
         switch (subcommand) {
            case "start":
               if (AIRotationRecorder.isRecording()) {
                  ChatUtil.send(
                     D.k(
                        new int[]{124, 250, 1181, 1192, 1252, 1185, 1227, 1236, 251, 1242, 1212, 1197, 251, 1185, 1214, 1197, 1177, 184},
                        new int[]{219, 153, 138, 152}
                     )
                  );
                  return;
               }

               if (me908 == null) {
                  me908 = new AIRotationRecorder();
                  Viola.getInstance().getEventBus().register(me908);
               }

               AIRotationRecorder.startRecording();
               ChatUtil.send(
                  "\u00a7aЗапись начата!"
               );
               ChatUtil.send(
                  D.k(
                     new int[]{
                        59,
                        108,
                        1100,
                        1217,
                        1196,
                        1121,
                        1055,
                        1210,
                        1246,
                        1134,
                        124,
                        1221,
                        1193,
                        1120,
                        1040,
                        175,
                        188,
                        1129,
                        1132,
                        1227,
                        1188,
                        123,
                        1128,
                        1201,
                        1188,
                        1133,
                        1129,
                        1214,
                        1188,
                        1044,
                        124,
                        1202,
                        1247,
                        1135,
                        1055,
                        1217,
                        188,
                        1132,
                        1132,
                        1212,
                        1188,
                        1050,
                        1132,
                        1214,
                        1239
                     },
                     new int[]{156, 91, 92, 131}
                  )
               );
               ChatUtil.send(
                  D.k(
                     new int[]{
                        95,
                        235,
                        1220,
                        1144,
                        1223,
                        1250,
                        1255,
                        1141,
                        1231,
                        1183,
                        1253,
                        1147,
                        1229,
                        252,
                        123,
                        95,
                        214,
                        189,
                        181,
                        25,
                        139,
                        168,
                        179,
                        73,
                        216,
                        123,
                        235,
                        1037,
                        1219,
                        1171,
                        252,
                        1031,
                        1209,
                        1182,
                        1260,
                        1028,
                        1222,
                        1262,
                        1254,
                        1025
                     },
                     new int[]{248, 220, 220, 57}
                  )
               );
               break;
            case "stop":
               if (!AIRotationRecorder.isRecording()) {
                  ChatUtil.send(
                     D.k(
                        new int[]{58, 203, 1032, 1194, 1186, 1168, 1118, 1238, 189, 1173, 1066, 186, 1189, 1180, 1066, 1240, 188}, new int[]{157, 168, 31, 154}
                     )
                  );
                  return;
               }

               int samples = AIRotationRecorder.stopRecording();
               ChatUtil.send(
                  D.k(
                     new int[]{171, 230, 1043, 1254, 1075, 1215, 1093, 1178, 44, 1209, 1093, 1172, 1084, 1210, 1082, 1252, 1079, 1202, 1081, 1254, 45},
                     new int[]{12, 135, 4, 214}
                  )
               );
               ChatUtil.send("§7Записано сэмплов: §f" + samples);
               ChatUtil.send(
                  D.k(
                     new int[]{
                        173,
                        82,
                        1029,
                        1168,
                        1077,
                        1115,
                        1062,
                        1181,
                        1085,
                        1062,
                        1060,
                        1171,
                        1087,
                        69,
                        186,
                        183,
                        36,
                        4,
                        116,
                        241,
                        121,
                        4,
                        107,
                        180,
                        42,
                        89,
                        115,
                        176,
                        103,
                        0,
                        35,
                        241,
                        173,
                        82,
                        1065,
                        1258,
                        1093,
                        69,
                        1116,
                        1263,
                        1103,
                        1061,
                        1069,
                        1260,
                        1087,
                        1112,
                        1061,
                        1182
                     },
                     new int[]{10, 101, 29, 209}
                  )
               );
               break;
            case "save":
               if (!args.hasAny()) {
                  ChatUtil.send(
                     D.k(
                        new int[]{
                           106,
                           212,
                           1087,
                           1110,
                           1266,
                           1161,
                           1052,
                           1115,
                           1274,
                           1161,
                           1045,
                           1063,
                           1264,
                           1167,
                           1042,
                           45,
                           237,
                           16,
                           65,
                           57,
                           172,
                           222,
                           7,
                           100,
                           172,
                           193,
                           66,
                           55,
                           241,
                           217,
                           70,
                           122,
                           168,
                           137
                        },
                        new int[]{205, 183, 39, 23}
                     )
                  );
                  return;
               }

               String name = args.getString();
               AIRotationManager.saveDataset(name);
               break;
            case "load":
               if (!args.hasAny()) {
                  ChatUtil.send(
                     D.k(
                        new int[]{
                           241,
                           179,
                           1217,
                           1215,
                           1129,
                           1262,
                           1250,
                           1202,
                           1121,
                           1262,
                           1259,
                           1230,
                           1131,
                           1256,
                           1260,
                           196,
                           118,
                           119,
                           191,
                           208,
                           55,
                           185,
                           249,
                           146,
                           57,
                           177,
                           189,
                           222,
                           106,
                           189,
                           182,
                           154,
                           51,
                           188,
                           183,
                           159,
                           59,
                           181,
                           231
                        },
                        new int[]{86, 208, 217, 254}
                     )
                  );
                  return;
               }

               String modelName = args.getString();
               AIRotationManager.loadModel(modelName);
               break;
            case "train":
               if (!args.has(2)) {
                  ChatUtil.send(
                     D.k(
                        new int[]{
                           65,
                           61,
                           1171,
                           1150,
                           1241,
                           1120,
                           1200,
                           1139,
                           1233,
                           1120,
                           1209,
                           1039,
                           1243,
                           1126,
                           1214,
                           5,
                           198,
                           249,
                           237,
                           17,
                           135,
                           55,
                           171,
                           75,
                           148,
                           63,
                           226,
                           81,
                           198,
                           98,
                           239,
                           94,
                           146,
                           63,
                           248,
                           90,
                           146,
                           96,
                           171,
                           3,
                           139,
                           49,
                           239,
                           90,
                           138,
                           48,
                           234,
                           82,
                           131,
                           96
                        },
                        new int[]{230, 94, 139, 63}
                     )
                  );
                  return;
               }

               String trainDatasetName = args.getString();
               String trainModelName = args.getString();
               ChatUtil.send(
                  D.k(
                     new int[]{233, 24, 1273, 1043, 1033, 1047, 1241, 1043, 1024, 15, 1242, 1042, 1037, 1128, 1233, 1054, 1142, 1050, 202, 13, 96},
                     new int[]{78, 47, 228, 35}
                  )
               );
               new Thread(() -> AIRotationManager.trainModel(trainDatasetName, trainModelName)).start();
               break;
            case "list":
               AIRotationManager.listFiles();
               break;
            case "dir":
               AIRotationManager.openDirectory();
               break;
            default:
               ChatUtil.send("§cНеизвестная подкоманда: §f" + subcommand);
               this.t9Va();
         }
      }
   }

   private void t9Va() {
      ChatUtil.send(
         D.k(
            new int[]{
               70,
               239,
               254,
               182,
               220,
               183,
               100,
               250,
               160,
               195,
               121,
               136,
               142,
               254,
               56,
               174,
               136,
               229,
               55,
               250,
               162,
               229,
               52,
               183,
               128,
               228,
               61,
               169,
               193,
               183,
               100,
               231
            },
            new int[]{225, 138, 89, 218}
         )
      );
      ChatUtil.send(
         D.k(
            new int[]{
               186,
               152,
               13,
               185,
               116,
               222,
               80,
               172,
               124,
               140,
               87,
               248,
               186,
               201,
               14,
               248,
               1024,
               1230,
               1124,
               1256,
               1119,
               1202,
               3,
               1263,
               1069,
               1217,
               1051,
               1177,
               1105,
               222,
               1047,
               1258,
               1061,
               1224,
               1046,
               1253,
               1061,
               1223
            },
            new int[]{29, 254, 35, 216}
         )
      );
      ChatUtil.send(
         D.k(
            new int[]{
               217,
               212,
               81,
               35,
               23,
               146,
               12,
               54,
               17,
               194,
               95,
               229,
               73,
               159,
               95,
               1116,
               1087,
               1264,
               1103,
               1151,
               1088,
               1152,
               1095,
               1024,
               1074,
               146,
               1096,
               1138,
               1089,
               1162,
               1086,
               1038
            },
            new int[]{126, 178, 127, 66}
         )
      );
      ChatUtil.send(
         D.k(
            new int[]{
               35,
               66,
               135,
               82,
               237,
               4,
               218,
               82,
               242,
               65,
               137,
               15,
               234,
               69,
               196,
               86,
               186,
               4,
               14,
               4,
               169,
               4,
               1160,
               1037,
               1217,
               1124,
               1177,
               1038,
               1212,
               1126,
               1253,
               19,
               1200,
               1044,
               1259,
               1027,
               1221,
               1041,
               1259
            },
            new int[]{132, 36, 169, 51}
         )
      );
      ChatUtil.send(
         D.k(
            new int[]{
               232,
               127,
               247,
               243,
               38,
               57,
               173,
               224,
               46,
               112,
               183,
               178,
               115,
               125,
               184,
               230,
               46,
               106,
               188,
               230,
               113,
               57,
               229,
               255,
               32,
               125,
               188,
               254,
               113,
               57,
               126,
               165,
               98,
               57,
               1223,
               1187,
               1036,
               1118,
               1249,
               1232,
               1027,
               57,
               1253,
               1196,
               1147,
               1068,
               1250,
               1246
            },
            new int[]{79, 25, 217, 146}
         )
      );
      ChatUtil.send(
         D.k(
            new int[]{
               15,
               103,
               105,
               208,
               193,
               33,
               43,
               222,
               201,
               101,
               103,
               141,
               197,
               110,
               35,
               212,
               196,
               63,
               103,
               22,
               159,
               44,
               103,
               1190,
               1176,
               1074,
               1031,
               1266,
               1183,
               1081,
               1029,
               1277,
               136,
               1085,
               1145,
               1157,
               1181,
               1082,
               1035
            },
            new int[]{168, 1, 71, 177}
         )
      );
      ChatUtil.send(
         D.k(
            new int[]{
               126,
               180,
               143,
               230,
               176,
               242,
               205,
               238,
               170,
               166,
               129,
               32,
               238,
               255,
               129,
               1190,
               1254,
               1258,
               1248,
               1209,
               1251,
               242,
               1253,
               1207,
               1248,
               1257,
               1183,
               1205
            },
            new int[]{217, 210, 161, 135}
         )
      );
      ChatUtil.send(
         D.k(
            new int[]{219, 78, 150, 82, 21, 8, 220, 90, 14, 8, 31, 4, 81, 8, 1190, 1137, 1094, 1128, 1267, 1137, 1072, 8, 1159, 1027, 1091, 1042, 1275},
            new int[]{124, 40, 184, 51}
         )
      );
   }

   @Override
   public String getShortDesc() {
      return D.k(
         new int[]{1104, 1222, 1166, 1081, 1089, 1218, 1275, 1076, 1099, 1228, 238, 72, 58, 217, 1166, 1079, 1073, 1225, 1160, 1073, 1084, 1221, 1270},
         new int[]{115, 249, 206, 9}
      );
   }

   @Override
   public List<String> getLongDesc() {
      return List.of(
         D.k(
            new int[]{
               1032,
               1094,
               1217,
               1245,
               1071,
               1100,
               1229,
               205,
               1062,
               1091,
               1202,
               205,
               1061,
               1096,
               1218,
               1237,
               1107,
               1088,
               209,
               205,
               1068,
               1097,
               1214,
               1194,
               1063,
               1093,
               1221,
               1186,
               50,
               1088,
               221,
               1237,
               1107,
               1095,
               1219,
               1238,
               1118,
               1103,
               1219,
               1247,
               1058,
               1093,
               1221,
               1186,
               50,
               57,
               180,
               205,
               1070,
               1094,
               1225,
               1240,
               1065,
               1101,
               1220,
               205,
               1106,
               1094,
               1215,
               1245,
               1108,
               1088,
               1220
            },
            new int[]{18, 120, 253, 237}
         ),
         "",
         "Использование:",
         D.k(
            new int[]{79, 41, 205, 132, 18, 60, 197, 214, 21, 104, 137, 132, 1116, 1144, 1251, 1172, 1059, 1028, 132, 1171, 1105, 1143, 1180, 1253, 1069},
            new int[]{97, 72, 164, 164}
         ),
         D.k(
            new int[]{
               180,
               217,
               74,
               216,
               233,
               204,
               76,
               136,
               186,
               149,
               3,
               1222,
               1243,
               1274,
               1043,
               1221,
               1188,
               1162,
               1051,
               1210,
               1238,
               152,
               1044,
               1224,
               1189,
               1152,
               1122,
               1204
            },
            new int[]{154, 184, 35, 248}
         ),
         D.k(
            new int[]{
               85,
               74,
               130,
               202,
               8,
               74,
               157,
               143,
               91,
               23,
               133,
               139,
               22,
               78,
               213,
               202,
               86,
               11,
               1194,
               1236,
               1086,
               1131,
               1243,
               1239,
               1091,
               1129,
               1191,
               202,
               1103,
               1051,
               1193,
               1242,
               1082,
               1054,
               1193
            },
            new int[]{123, 43, 235, 234}
         ),
         D.k(
            new int[]{
               64,
               198,
               118,
               168,
               26,
               213,
               126,
               225,
               0,
               135,
               35,
               236,
               15,
               211,
               126,
               251,
               11,
               211,
               33,
               168,
               82,
               202,
               112,
               236,
               11,
               203,
               33,
               168,
               67,
               135,
               1057,
               1209,
               1069,
               1248,
               1063,
               1226,
               1058,
               135,
               1059,
               1206,
               1114,
               1170,
               1060,
               1220
            },
            new int[]{110, 167, 31, 136}
         ),
         D.k(
            new int[]{
               219,
               214,
               252,
               237,
               153,
               216,
               244,
               169,
               213,
               139,
               248,
               162,
               145,
               210,
               249,
               243,
               213,
               154,
               181,
               1274,
               1221,
               1156,
               1237,
               1166,
               1218,
               1167,
               1239,
               1153,
               213,
               1163,
               1195,
               1273,
               1216,
               1164,
               1241
            },
            new int[]{245, 183, 149, 205}
         ),
         D.k(
            new int[]{103, 78, 117, 8, 37, 70, 111, 92, 105, 2, 60, 1129, 1142, 1047, 1117, 1046, 1139, 15, 1112, 1048, 1136, 1044, 1058, 1050},
            new int[]{73, 47, 28, 40}
         ),
         D.k(
            new int[]{79, 1, 199, 26, 5, 9, 220, 26, 76, 64, 1168, 1144, 1115, 1056, 1253, 1144, 1069, 64, 1169, 1034, 1118, 1114, 1261},
            new int[]{97, 96, 174, 58}
         )
      );
   }

   @Override
   public Stream<String> tabComplete(String label, IArgConsumer args) {
      return args.hasExactlyOne() ? Stream.of("start", "stop", "save", "load", "train", "list", "dir") : Stream.empty();
   }
}
