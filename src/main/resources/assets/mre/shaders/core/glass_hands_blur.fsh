#version 150
uniform sampler2D Sampler0;     
uniform sampler2D Sampler1;     
uniform vec3 tintColor;         
uniform float tintStrength;     
in vec2 TexCoord;
out vec4 OutColor;
void main() {
    vec2 uv = TexCoord;
    float mask = texture(Sampler1, uv).a;
    if (mask < 0.01) discard;
    vec3 src = texture(Sampler0, uv).rgb;
    vec3 final = mix(src, src * tintColor * 1.5, tintStrength);
    OutColor = vec4(final, mask);
}
