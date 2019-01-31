package org.breakout.model.shader;

import org.andork.jogl.shader.Uniform1fvLocation;
import org.andork.jogl.shader.Uniform3fvLocation;
import org.andork.math3d.Clip3f;

import com.jogamp.opengl.GL2ES2;

public class ClipLocations {
	public final Uniform3fvLocation clipAxis = new Uniform3fvLocation("u_clipAxis");
	public final Uniform1fvLocation clipNear = new Uniform1fvLocation("u_clipNear");
	public final Uniform1fvLocation clipFar = new Uniform1fvLocation("u_clipFar");
	
	public void put(GL2ES2 gl, Clip3f clip) {
		clipAxis.put(gl, clip.axis());
		clipNear.put(gl, clip.near());
		clipFar.put(gl, clip.far());
	}
}
