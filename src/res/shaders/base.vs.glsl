#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 tex;
layout(location = 2) in vec3 norm;
layout(location = 3) in vec3 quadI;
layout(location = 4) in vec4 aoFactors;
//layout(location = 5) in vec4 colorv0;
//layout(location = 6) in vec4 colorv1;
//layout(location = 7) in vec4 colorv2;
//layout(location = 8) in vec4 colorv3;

uniform mat4 proj;
uniform mat4 sproj;
uniform float time;

out vec3 texcoord;
out vec3 normal;
out vec2 quad;
flat out vec4 ao;
out vec3 shadowpos;

void main() {
	vec3 pos = position + quadI.z * vec3(sin(time+position.z*2.0),cos(time+position.x*2.0), sin(time+position.y*2.0)) / 10.0;
	gl_Position = proj * vec4(pos, 1.0);
	texcoord = tex;
	normal = norm;
	ao = aoFactors;
	quad = quadI.xy;
	shadowpos = (sproj * vec4(pos, 1.0)).xyz;
}