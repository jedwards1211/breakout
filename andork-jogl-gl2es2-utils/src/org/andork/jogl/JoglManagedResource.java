package org.andork.jogl;

import com.jogamp.opengl.GL2ES2;

public abstract class JoglManagedResource implements JoglResource {
	int referenceCount = 0;

	@Override
	public void dispose(GL2ES2 gl) {
		if (referenceCount == 0) {
			throw new IllegalStateException("expected referenceCount to be > 0");
		}
		if (--referenceCount == 0) {
			doDispose(gl);
		}
	}

	@Override
	public boolean init(GL2ES2 gl) {
		if (referenceCount++ == 0) {
			return doInit(gl);
		}
		return true;
	}
	
	protected abstract boolean doInit(GL2ES2 gl);

	protected abstract void doDispose(GL2ES2 gl);
}
