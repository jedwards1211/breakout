package org.andork.jogl;

import java.awt.Component;

import com.jogamp.opengl.GLAutoDrawable;

public class DevicePixelRatio
{
	private DevicePixelRatio( )
	{

	}

	public static float getDevicePixelRatio(GLAutoDrawable drawable) {
		if (!(drawable instanceof Component)) return 1;
		Component comp = (Component) drawable;
		return (float) drawable.getSurfaceWidth( ) / comp.getWidth( );
	}
}
