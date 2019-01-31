#version 330

// lighting
uniform float u_maxCenterlineDistance;

// distance coloration
in float v_dist;

// color
uniform vec4 u_color;
out vec4 color;

// clipping
in vec3 v_pos;
uniform vec3 u_clipAxis;
uniform float u_clipNear;
uniform float u_clipFar;

void main() {
  float clip_pos = dot(u_clipAxis, v_pos);
  if (clip_pos < u_clipNear || clip_pos > u_clipFar) discard;

  if (v_dist > u_maxCenterlineDistance) discard;
  color = u_color;
}
