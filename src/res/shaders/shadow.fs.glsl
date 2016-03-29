#version 330 core

in vec3 texcoord;

uniform sampler2DArray tex;

out vec4 color;

void main() {
	color = texture(tex, texcoord);
	if (color.a < 0.5)
		discard;
}