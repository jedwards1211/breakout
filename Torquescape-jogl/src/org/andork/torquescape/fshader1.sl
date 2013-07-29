//fragment shader
precision mediump float;
varying vec4 v_fcolor;
void main(void)
{
  gl_FragColor = v_fcolor;
}