package org.andork.jogl.awt;

import org.andork.jogl.JoglDrawContext;
import org.andork.jogl.JoglManagedResource;
import org.andork.jogl.JoglResourceManager;

import com.jogamp.opengl.GL2ES2;

public abstract class JoglTextProgram extends JoglManagedResource {
	public JoglTextProgram(JoglResourceManager manager) {
		super(manager);
	}

	public abstract void draw(JoglText text, JoglDrawContext context, GL2ES2 gl, float[] m, float[] n);
}