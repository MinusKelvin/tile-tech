#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 tex;
layout(location = 2) in float wavy;

uniform mat4 proj;
uniform float time;

out vec3 texcoord;

void main() {
	vec3 pos = position + wavy * vec3(sin(time+position.z*2.0),cos(time+position.x*2.0), sin(time+position.y*2.0)) / 10.0;
	gl_Position = proj * vec4(pos, 1.0);
	texcoord = tex;
}