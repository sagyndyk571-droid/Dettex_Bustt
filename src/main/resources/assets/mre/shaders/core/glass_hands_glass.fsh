#version 150
uniform sampler2D Sampler0;  
uniform sampler2D Sampler1;  
uniform sampler2D Sampler2;  
uniform float mixFactor;     
in vec2 TexCoord;
out vec4 OutColor;
void main() {
    vec2 uv = TexCoord;
    float mask = texture(Sampler2, uv).a;
    if (mask < 0.01) discard;
    vec3 blurred = texture(Sampler0, vec2(uv.x, 1.0 - uv.y)).rgb;
    vec3 handsColor = texture(Sampler1, uv).rgb;
    vec3 finalColor = mix(blurred, handsColor, mixFactor);
    OutColor = vec4(finalColor, mask);
}
