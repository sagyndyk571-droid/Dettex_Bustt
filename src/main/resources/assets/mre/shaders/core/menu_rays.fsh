#version 150
uniform vec2  Resolution;
uniform float Time;
uniform float Alpha;
uniform vec3  RayColor;
in  vec2 TexCoord;
out vec4 OutColor;
const int   RAY_COUNT = 7;
const float RAY_WIDTHS[7] = float[]( 0.38, 0.28, 0.28, 0.22, 0.22, 0.18, 0.18);
const float RAY_BRIGHT[7] = float[]( 0.96, 0.66, 0.66, 0.42, 0.42, 0.24, 0.24);
const float RAY_BASE[7]   = float[]( 0.00,  0.34, -0.34,  0.68, -0.68,  1.05, -1.05);
const float SWAY_AMP[7]   = float[]( 0.10,  0.14,  0.14,  0.12,  0.12,  0.09,  0.09);
const float SWAY_SPEED[7] = float[]( 0.38,  0.51,  0.44,  0.62,  0.55,  0.70,  0.65);
const float SWAY_PHASE[7] = float[]( 0.00,  1.20,  2.40,  0.80,  3.60,  2.00,  4.80);
void main() {
    vec2  uv       = TexCoord / Resolution;
    float dy       = uv.y + 0.08;
    float dx       = uv.x - 0.5;
    float pixAngle = atan(dx, dy);
    float breathe  = 0.78 + 0.22 * sin(Time * 0.65);
    float totalLight = 0.0;
    for (int i = 0; i < RAY_COUNT; i++) {
        float sway  = SWAY_AMP[i] * sin(Time * SWAY_SPEED[i] + SWAY_PHASE[i]);
        float angle = RAY_BASE[i] + sway;
        float diff  = pixAngle - angle;
        float cone  = smoothstep(RAY_WIDTHS[i], 0.0, abs(diff));
        float core  = smoothstep(RAY_WIDTHS[i] * 0.5, 0.0, abs(diff)) * 0.3;
        float t     = clamp(dy / 0.62, 0.0, 1.0);
        float depth = 1.0 - t * t * (3.0 - 2.0 * t); 
        float topFade = smoothstep(0.0, 0.10, dy);
        float flicker = 0.88 + 0.12 * sin(Time * (1.5 + float(i) * 0.45) + float(i) * 2.3);
        totalLight += (cone + core) * depth * topFade * RAY_BRIGHT[i] * breathe * flicker;
    }
    totalLight = totalLight / (totalLight + 0.5) * 1.5;
    totalLight = clamp(totalLight, 0.0, 1.0);
    vec3 color = RayColor * totalLight;
    float a    = totalLight * Alpha;
    if (a < 0.002) discard;
    OutColor = vec4(color, a);
}
