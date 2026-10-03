#version 150
uniform sampler2D Sampler0;
in vec2 TexCoord;
out vec4 OutColor;
void main() {
    float a = texture(Sampler0, TexCoord).a;
    if (a < 0.01) discard;
    OutColor = vec4(0.0, 0.0, 0.0, a);
}
