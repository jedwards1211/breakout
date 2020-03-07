# version 330

uniform sampler2D u_texture;
uniform int u_count;
uniform float u_coeff[11];
in vec2 v_texcoord[11];
out vec4 color_out;

void main() {
	vec4 color = vec4(0, 0, 0, 0);
	for (int i = 0; i < u_count; i++) {
		vec4 sample = texture(u_texture, v_texcoord[i]) * u_coeff[i];
		color = max(
			color,
			sample
		);
	}
	color_out = color;
}
