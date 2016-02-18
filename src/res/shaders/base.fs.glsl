#version 330 core
#extension GL_ARB_texture_gather : enable

in vec3 texcoord;
in vec3 normal;
in vec2 quad;
flat in vec4 ao;

uniform sampler2DArray tex;
uniform vec4 sundir;

out vec4 color;

void main() {
	color = texture(tex, texcoord);
	if (color.a < 0.5)
		discard;
	color.rgb *= min(1.0, 0.5 + max(0.0, dot(sundir.xyz, normalize(normal.xyz)))) * sundir.w;
	color.rgb *= mix(mix(ao.x, ao.z, quad.x), mix(ao.y, ao.w, quad.x), quad.y);
}