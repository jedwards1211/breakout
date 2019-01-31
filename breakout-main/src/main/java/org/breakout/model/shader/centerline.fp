#version 330

// lighting
uniform float u_maxCenterlineDistance;

// distance coloration
in float v_dist;

// color
uniform vec4 u_color;
out vec4 color;

void main() {
  if (v_dist > u_maxCenterlineDistance) discard;
  color = u_color;
}
