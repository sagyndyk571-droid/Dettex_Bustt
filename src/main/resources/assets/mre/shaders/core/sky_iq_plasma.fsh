#version 150
uniform float iTime;
uniform vec3 uColor;
uniform int uShowStars;
in vec3 vPos;
in vec4 FragColor;
out vec4 OutColor;
#define TAU 6.28318530718
#define PI 3.14159265
#define MAX_ITER 5
float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}
vec3 hueShift(vec3 c, float h) {
    vec3 k = vec3(0.57735, 0.57735, 0.57735);
    float c2 = cos(h);
    float s2 = sin(h);
    return c * c2 + cross(k, c) * s2 + k * dot(k, c) * (1.0 - c2);
}
void main() {
    vec2 ndc = vPos.xy;
    vec4 clip = vec4(ndc, 1.0, 1.0);
    vec4 view = inverse(ProjMat) * clip;
    vec3 dirView = normalize(view.xyz / max(abs(view.w), 1e-5));
    vec3 ray = normalize((inverse(ModelViewMat) * vec4(dirView, 0.0)).xyz);
    ray = normalize(vec3(ray.x, max(abs(ray.y), 1e-4), ray.z));
    vec2 uv = vec2(atan(ray.x, ray.z) / TAU, asin(clamp(ray.y, -1.0, 1.0)) / PI);
    vec2 p = uv * vec2(16.0, 10.0);
    float a = 0.0;
    for (int n = 0; n < MAX_ITER; n++) {
        float t = iTime * (0.22 + 0.03 * float(n));
        vec2 q = p + vec2(sin(float(n) * 1.7 + t) * 1.4, cos(float(n) * 1.3 + t) * 1.4);
        a += sin(q.x * 1.0 + t) * cos(q.y * 1.1 - t * 0.7);
        p = p * 1.55 + t * 0.05;
    }
    a /= float(MAX_ITER);       
    a = a * 0.5 + 0.5;          
    vec3 colA = uColor;
    vec3 colB = hueShift(uColor, 1.9 + sin(iTime * 0.35) * 0.35);
    vec3 colC = vec3(1.0, 0.93, 0.82);
    vec3 col = mix(colA, colB, clamp(a * a, 0.0, 1.0));
    col = mix(col, colC, pow(a, 2.5) * 0.75);
    float h = clamp(ray.y * 2.4 + 0.18, 0.0, 1.0);
    col *= 0.15 + 1.65 * h;
    if (uShowStars == 1) {
        vec2 g = floor(uv * vec2(220.0, 160.0));
        float star = hash21(g);
        star = step(0.9955, star);
        float s = smoothstep(0.06, 0.5, h) * star;
        s *= 0.8 + 0.2 * sin(iTime * 3.0 + g.x * 13.7 + g.y * 7.3);
        col += vec3(s * 0.9, s * 0.95, s) * 1.4 * step(0.5, h);
    }
    col = col / (col + vec3(0.30));
    col = pow(col, vec3(0.85));
    OutColor = vec4(col, 1.0);
}
