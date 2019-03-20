#version 330

// lighting
in float v_lightAmount;

// satellite imagery
in vec2 v_texcoord;
uniform sampler2D u_satelliteImagery;

// clip
uniform vec2 u_clipNearFar;
in float v_clipPosition;

uniform float u_alpha;

out vec4 color_out;

void main() {
	if (v_clipPosition < u_clipNearFar.x || v_clipPosition > u_clipNearFar.y) {
		discard;
	}
	vec4 color = texture(u_satelliteImagery, v_texcoord);
	color_out = vec4(color.xyz * v_lightAmount, color.w * u_alpha);
}
