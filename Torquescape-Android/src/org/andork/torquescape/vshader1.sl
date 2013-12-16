//vertex shader

attribute vec4 v_position;
attribute vec3 v_normal;
attribute vec4 v_color;
uniform vec4 light_loc;
varying float lightIntensity;
varying vec4 v_fcolor;

uniform mat4 m_mvp;

void main()
{
	//specify direction of light
	vec4 light_dir = light_loc - v_position;

	vec4 newNormal = vec4(v_normal,0.0) * m_mvp;
			
	//lightIntensity = max(0.0, dot(newNormal.xyz, light_dir.xyz));
	v_fcolor = v_color;
	// * lightIntensity;
	//lightIntensity = 1.0;

	//gl_Position must come LAST!
	gl_Position = v_position * m_mvp;
}