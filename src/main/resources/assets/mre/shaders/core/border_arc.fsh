#version 150
#moj_import <mre:common.glsl>
in vec2 FragCoord; 
in vec4 FragColor;
uniform vec2 Size; 
uniform vec4 Radius; 
uniform float Thickness; 
uniform vec2 Smoothness; 
uniform float StartAngle; 
uniform float EndAngle;   
out vec4 OutColor;
const float TAU = 6.28318530718;
void main() {
    vec2 center = Size * 0.5;
    float dist = rdist(center - (FragCoord.xy * Size), center - 1.0, Radius);
    float alpha = smoothstep(1.0 - Thickness - Smoothness.x - Smoothness.y,
        1.0 - Thickness - Smoothness.y, dist); 
    alpha *= 1.0 - smoothstep(1.0 - Smoothness.y, 1.0, dist); 
    float sweep = EndAngle - StartAngle;
    if (sweep < TAU - 0.01) {
        vec2 p = FragCoord * Size - center;
        float ang = atan(p.y, p.x);
        float rel = mod(ang - StartAngle, TAU);
        if (rel < 0.0) rel += TAU;
        float aaAng = min(2.0 / max(length(p), 1.0), sweep * 0.5);
        float startEdge = smoothstep(0.0, aaAng, rel);
        float endEdge = 1.0 - smoothstep(sweep - aaAng, sweep, rel);
        alpha *= startEdge * endEdge;
    }
    vec4 color = vec4(FragColor.rgb, FragColor.a * alpha);
    if (color.a == 0.0) { 
        discard;
    }
    OutColor = color;
}
