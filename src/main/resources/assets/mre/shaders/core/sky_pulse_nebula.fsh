#version 150
uniform float iTime;
uniform vec3 uColor;
in vec3 vPos;
in vec4 FragColor;
out vec4 OutColor;
#define uTime iTime
#define uScale 5.0
#define uSpeed 1.0
#define uIntensity 0.02
const mat3 m3 = mat3(
     0.00,  0.80,  0.60,
    -0.80,  0.36, -0.48,
    -0.60, -0.48,  0.64
);
float hash(vec3 p) {
    p = fract(p * vec3(443.897, 441.423, 437.195));
    p += dot(p, p.yzx + 19.19);
    return fract((p.x + p.y) * p.z);
}
float noise(vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(hash(p + vec3(0, 0, 0)), hash(p + vec3(1, 0, 0)), f.x),
            mix(hash(p + vec3(0, 1, 0)), hash(p + vec3(1, 1, 0)), f.x), f.y),
        mix(mix(hash(p + vec3(0, 0, 1)), hash(p + vec3(1, 0, 1)), f.x),
            mix(hash(p + vec3(0, 1, 1)), hash(p + vec3(1, 1, 1)), f.x), f.y),
        f.z
    );
}
float fbm(vec3 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 4; ++i) {
        v += a * noise(p);
        p = m3 * p * 2.02;
        a *= 0.5;
    }
    return v;
}
void main() {
    vec3 rd = normalize(vPos);
    vec3 rdSky = vec3(rd.x, abs(rd.y), rd.z);
    vec3 p = rdSky * (uScale * 0.3 + 0.15);
    float t = uTime * uSpeed * 0.04;
    vec3 q = vec3(
        fbm(p + vec3(0.0, t, 0.0)),
        fbm(p + vec3(1.7, 0.4, t * 0.8)),
        fbm(p + vec3(2.4, t * 1.2, 0.5))
    );
    float density = fbm(p + q * 1.3);
    float innerCore = fbm(p * 1.6 + q * 2.0 - vec3(0.0, t * 0.5, 0.0));
    vec3 primaryColor = uColor;
    vec3 mysteryGlowColor = mix(vec3(uColor.b, uColor.r, uColor.g), vec3(0.2, 0.8, 1.0), 0.35);
    vec3 voidDark = vec3(0.001, 0.001, 0.004);
    float cloudShape = pow(clamp(density, 0.0, 1.0), 2.2);
    vec3 finalColor = mix(voidDark, primaryColor * 0.45, cloudShape);
    float corePower = pow(clamp(innerCore * density, 0.0, 1.0), 2.0);
    vec3 coreGlow = mysteryGlowColor * corePower * (1.8 + uIntensity * 16.0);
    finalColor += coreGlow;
    finalColor = mix(voidDark, finalColor, 1.0);
    OutColor = vec4(finalColor, 1.0);
}
