#version 330 core

in vec3 texcoord;
in vec4 color;

uniform sampler2DArray tex;

out vec4 col;

void main() {
	col = color * texture(tex,  texcoord);
}