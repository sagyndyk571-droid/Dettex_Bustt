#version 150
uniform sampler2D Sampler0;     
uniform sampler2D Sampler1;     
uniform vec3 color;
uniform vec3 color2;
uniform vec2 texelSize;
uniform float trailIntensity;
uniform float glowSize;
uniform float time;
uniform float rainbowEnabled;
uniform float rainbowSpeed;
in vec2 TexCoord;
out vec4 OutColor;
float sampleMask(vec2 uv) {
    return texture(Sampler0, uv).a;
}
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}
void main() {
    vec2 uv = TexCoord;
    float center = sampleMask(uv);
    float maxNeighbor = 0.0;
    float step = texelSize.x * glowSize * 3.0;
    maxNeighbor = max(maxNeighbor, sampleMask(uv + vec2(step, 0.0)));
    maxNeighbor = max(maxNeighbor, sampleMask(uv - vec2(step, 0.0)));
    maxNeighbor = max(maxNeighbor, sampleMask(uv + vec2(0.0, step)));
    maxNeighbor = max(maxNeighbor, sampleMask(uv - vec2(0.0, step)));
    maxNeighbor = max(maxNeighbor, sampleMask(uv + vec2(step, step)));
    maxNeighbor = max(maxNeighbor, sampleMask(uv - vec2(step, step)));
    maxNeighbor = max(maxNeighbor, sampleMask(uv + vec2(step, -step)));
    maxNeighbor = max(maxNeighbor, sampleMask(uv - vec2(step, -step)));
    float outlineMask = clamp(maxNeighbor - center, 0.0, 1.0);
    float bloom = texture(Sampler1, uv).a;
    float wave = sin(time * 3.0 + uv.x * 10.0 + uv.y * 10.0) * 0.5 + 0.5;
    float animatedGlow = outlineMask * (0.7 + wave * 0.3);
    vec3 grad;
    if (rainbowEnabled > 0.5) {
        float hue = mod(uv.y + time * rainbowSpeed * 0.1, 1.0) * 0.83;
        grad = hsv2rgb(vec3(hue, 0.9, 1.0));
    } else {
        grad = mix(color, color2, uv.y + wave * 0.2);
    }
    float glowAlpha = bloom * trailIntensity * 2.0;
    float outlineAlpha = animatedGlow * trailIntensity;
    float fillAlpha = center * 0.6;
    float totalAlpha = clamp(glowAlpha + outlineAlpha + fillAlpha, 0.0, 1.0);
    vec3 finalColor = grad * (1.0 + glowAlpha * 2.0);
    if (totalAlpha <= 0.001) discard;
    OutColor = vec4(finalColor, totalAlpha);
}
