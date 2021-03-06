#version 330

// lighting
uniform float u_maxCenterlineDistance;

// distance coloration
in float v_dist;

// time travel
uniform float u_maxDate;
in float v_date;

// color
uniform vec4 u_color;
out vec4 color;

// clipping
in float v_clipPosition;
uniform float u_clipNear;
uniform float u_clipFar;

void main() {
  if (v_date > u_maxDate) discard;
  if (v_clipPosition < u_clipNear || v_clipPosition > u_clipFar) discard;
  if (v_dist > u_maxCenterlineDistance) discard;
  color = u_color;
}
