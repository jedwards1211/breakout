package org.andork.jogl;

import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.newMat4f;

import java.util.Objects;

import org.andork.math3d.Vecmath;

public class JoglViewSettings {
	private Projection projection = new PerspectiveProjection((float) Math.PI / 2, 1f, 1e7f);

	private final float[] v = newMat4f();

	private final float[] vi = newMat4f();

	public void copy(JoglViewSettings other) {
		setProjection(other.projection);
		setViewXform(other.v);
	}

	public void getInvViewXform(float[] viOut) {
		System.arraycopy(vi, 0, viOut, 0, 16);
	}

	public Projection getProjection() {
		return projection;
	}

	public void getViewXform(float[] vOut) {
		System.arraycopy(v, 0, vOut, 0, 16);
	}

	public void setProjection(Projection projection) {
		Objects.requireNonNull(projection);
		this.projection = projection;
	}

	public void setViewXform(float[] v) {
		if (Vecmath.hasNaNsOrInfinites(v)) {
			throw new IllegalArgumentException("v must not have NaN or infinite values");
		}

		invAffine(v, vi);
		System.arraycopy(v, 0, this.v, 0, 16);
	}

	public void setInvViewXform(float[] vi) {
		if (Vecmath.hasNaNsOrInfinites(vi)) {
			throw new IllegalArgumentException("vi must not have NaN or infinite values");
		}
	
		invAffine(vi, v);
		System.arraycopy(vi, 0, this.vi, 0, 16);
	}
}
