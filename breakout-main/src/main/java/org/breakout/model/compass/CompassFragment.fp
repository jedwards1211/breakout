#version 330

in vec3 v_position;
in vec3 v_normal;
uniform float u_ambient;

out vec4 color;

void main() {
	float diffuse = dot(v_normal, vec3(0.0, 0.0, 1.0));
	float light = u_ambient + diffuse * (1.0 - u_ambient);
	
	vec3 base_color = mix(vec3(0.9, 0.9, 0.9), vec3(1, 0, 0), clamp(-v_position.z * 2, 0, 1));
	
	color = vec4(base_color * light, 1.0);
}
