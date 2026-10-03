#version 150
uniform float iTime;
uniform vec3 uColor;
uniform int uShowStars;
in vec3 vPos;
in vec4 FragColor;
out vec4 OutColor;
float hash21(vec2 n) { return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453); }
float hash31(vec3 n) { return fract(sin(dot(n, vec3(12.9898, 4.1414, 5.28934))) * 43758.5453); }
float tri(float x) { return abs(fract(x) - 0.5); }
float triNoise2d(vec2 p, float spd) {
    float z = 1.8, z2 = 2.5, rz = 0.0;
    float c = cos(p.x * 0.06), s = sin(p.x * 0.06);
    p = vec2(p.x * c - p.y * s, p.x * s + p.y * c);
    vec2 bp = p;
    float t = iTime * spd;
    for (int i = 0; i < 3; i++) {
        vec2 dg = vec2(tri(bp.x * 1.85) + tri(bp.y * 1.85), tri(bp.y * 1.85 + tri(bp.x * 1.85))) * 0.75;
        float ct = cos(t), st = sin(t);
        dg = vec2(dg.x * ct - dg.y * st, dg.x * st + dg.y * ct);
        p -= dg / z2;
        bp *= 1.3;
        z2 *= 0.45;
        z *= 0.42;
        p *= 1.21 + (rz - 1.0) * 0.02;
        rz += tri(p.x + tri(p.y)) * z;
        p = vec2(-p.y * 0.95534 + p.x * 0.29552, p.x * 0.95534 + p.y * 0.29552);
    }
    return clamp(1.0 / pow(rz * 29.0 + 0.0001, 1.3), 0.0, 0.55);
}
float skinNoise(float a, float h) {
    float w1 = triNoise2d(vec2(a * 2.0 + 5.0, h * 2.2 + iTime * 0.05), 0.3);
    float w2 = triNoise2d(vec2(a * 3.1 - 3.0, h * 1.6 - iTime * 0.07), 0.3);
    float w3 = triNoise2d(vec2(a * 1.3 + 9.0, h * 3.4 + iTime * 0.03), 0.3);
    return clamp(w1 * 0.55 + w2 * 0.3 + w3 * 0.25, 0.0, 1.0);
}
vec4 aurora(vec3 ro, vec3 rd) {
    vec4 col = vec4(0.0);
    vec4 avgCol = vec4(0.0);
    float rdY = rd.y * 2.0 + 0.4;
    if (rdY <= 0.01) return col;
    float breath = 0.10 + 0.06 * skinNoise(rd.z, rd.x);
    for (int i = 0; i < 10; i++) {
        float fi = float(i);
        float pt = (0.8 + pow(fi, 1.4) * 0.004 - ro.y) / rdY;
        pt -= 0.006 * hash21(gl_FragCoord.xy) * smoothstep(0.0, 8.0, fi);
        vec3 bpos = ro + pt * rd;
        float rzt = triNoise2d(bpos.zx, 0.15);
        vec3 base = vec3(0.18, 1.00, 0.58);
        vec3 teal = vec3(0.05, 0.85, 0.95);
        vec3 ribbon = mix(base, teal, 1.0 - exp(-rzt * 9.0));
        vec3 theme = vec3(uColor.r, uColor.g, uColor.b);
        float themeMax = max(max(theme.r, theme.g), theme.b);
        theme = themeMax > 0.001 ? theme * (1.1 / themeMax) : vec3(1.0);
        ribbon = mix(ribbon, theme, 0.16);
        vec4 col2 = vec4(ribbon * rzt, rzt);
        avgCol = mix(avgCol, col2, 0.5);
        col += avgCol * exp2(-fi * 0.1 - 1.5) * smoothstep(0.0, 3.0, fi);
    }
    col *= clamp(rd.y * 15.0 + 0.4, 0.0, 1.0);
    col.rgb *= 1.0 + 0.7 * smoothstep(breath, breath + 0.12, rd.y * 2.0 + 0.4);
    return col * 3.2;
}
vec3 stars(vec3 p) {
    if (uShowStars == 0) return vec3(0.0);
    vec3 c = vec3(0.0);
    float res = 400.0;
    for (int i = 0; i < 2; i++) {
        vec3 q = fract(p * res) - 0.5;
        vec3 id = floor(p * res);
        float rn = hash31(id);
        float c2 = 1.0 - smoothstep(0.0, 0.6, length(q));
        c2 *= step(rn, 0.003 + float(i) * 0.001);
        c += c2 * (mix(vec3(1.0, 0.49, 0.1), vec3(0.75, 0.9, 1.0), hash31(id + 100.0)) * 0.1 + 0.9);
        res *= 1.5;
    }
    return c * c * 0.8;
}
vec3 bg(vec3 rd) {
    float sd = dot(normalize(vec3(-0.5, -0.6, 0.9)), rd) * 0.5 + 0.5;
    sd = pow(sd, 5.0);
    return mix(vec3(0.04, 0.09, 0.18), vec3(0.09, 0.04, 0.2), sd) * 0.63;
}
void main() {
    vec3 rd = normalize(vPos);
    vec3 ro = vec3(0.0, 0.0, -6.7);
    vec3 rdSky = vec3(rd.x, abs(rd.y), rd.z);
    vec3 col = bg(rdSky);
    vec4 aur = aurora(ro, rdSky);
    col += stars(rdSky);
    col = col * (1.0 - aur.a) + aur.rgb;
    OutColor = vec4(col, 1.0);
}
