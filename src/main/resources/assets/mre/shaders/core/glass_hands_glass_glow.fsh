#version 150
uniform sampler2D Sampler0;  
uniform sampler2D Sampler1;  
uniform vec3 glowColor1;     
uniform vec3 glowColor2;     
uniform float exposure;      
in vec2 TexCoord;
out vec4 OutColor;
void main() {
    vec2 uv = TexCoord;
    float bloomIntensity = texture(Sampler0, uv).a;
    float originalMask = texture(Sampler1, uv).a;
    float glowMask = bloomIntensity * (1.0 - originalMask);
    if (glowMask < 0.01) discard;
    vec3 glowColor = mix(glowColor1, glowColor2, uv.y);
    vec3 finalGlow = glowColor * glowMask * exposure;
    OutColor = vec4(finalGlow, glowMask);
}
