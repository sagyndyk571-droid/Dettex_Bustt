#version 150

uniform sampler2D Sampler0;
uniform float u_Strength;

in vec4 FragColor;
in vec2 TexCoord;

out vec4 OutColor;

void main() {
    vec4 color = texture(Sampler0, TexCoord);
    OutColor = vec4(color.rgb, color.a * u_Strength);
}
