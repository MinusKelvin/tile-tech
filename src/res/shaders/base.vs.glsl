#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 tex;
layout(location = 2) in vec3 norm;
layout(location = 3) in vec2 quadI;
layout(location = 4) in vec4 aoFactors;

uniform mat4 proj;
uniform mat4 sproj;

out vec3 texcoord;
out vec3 normal;
out vec2 quad;
flat out vec4 ao;
out vec3 shadowpos;

void main() {
	gl_Position = proj * vec4(position, 1.0);
	texcoord = tex;
	normal = norm;
	ao = aoFactors;
	quad = quadI;
	shadowpos = (sproj * vec4(position, 1.0)).xyz;
}