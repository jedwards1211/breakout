package org.andork.math3d;

import java.util.Arrays;

public final class Clip3f implements Cloneable {
	private final float[] axis;
	private final float near;
	private final float far;

	public Clip3f(float[] axis, float near, float far) {
		for (int i = 0; i < axis.length; i++) {
			if (!Float.isFinite(axis[i])) throw new IllegalArgumentException("axis must be finite");
		}
		if (!Float.isFinite(near)) throw new IllegalArgumentException("near must be finite");
		if (!Float.isFinite(far)) throw new IllegalArgumentException("far must be finite");
		this.axis = Arrays.copyOf(axis, 3);
		this.near = Math.min(near, far);
		this.far = Math.max(near, far);
	}
	
	@Override
	public Clip3f clone() {
		return new Clip3f(axis, near, far);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Clip3f other = (Clip3f) obj;
		return Arrays.equals(axis, other.axis) && far == other.far && near == other.near;
	}

	/**
	 * @param point a point to test
	 * @return {@code true} iff {@code point} is within the clipping bounds (not clipped out)
	 */
	public boolean contains(float[] point) {
		float dot = Vecmath.dot3(axis, point);
		return dot >= near && dot <= far;
	}
	
	/**
	 * Linearly interpolates between two clips
	 * @param a the first clip
	 * @param b the second clip
	 * @param f the interpolation amount. 0 = a, 1 = b, 0.5 = halfway between
	 * @return an interpolated {@code Clip3f}
	 */
	public static Clip3f lerp(Clip3f a, Clip3f b, float f) {
		float[] axis = new float[3];
		Vecmath.interpRot3(a.axis, b.axis, f, axis);
		float rf = 1 - f;
		return new Clip3f(axis, a.near * rf + b.near * f, a.far * rf + b.far * f);
	}
	
	public Clip3f flip() {
		return new Clip3f(new float[] {-axis[0], -axis[1], -axis[2]}, -near, -far);
	}

	public float[] axis() {
		return Arrays.copyOf(axis, 3);
	}
	
	public float near() {
		return near;
	}
	
	public float far() {
		return far;
	}
	
	public Clip3f setAxis(float[] axis) {
		if (Arrays.equals(this.axis, axis)) return this;
		return new Clip3f(axis, near, far);
	}

	public Clip3f setNear(float near) {
		return (near == this.near) ? this : new Clip3f(axis, near, far) ;
	}

	public Clip3f setFar(float far) {
		return (far == this.far) ? this : new Clip3f(axis, near, far) ;
	}
	
	public void getNearFarOfMbr(float[] mbr, float[] out) {
		float nearx = mbr[axis[0] < 0 ? 3 : 0];
		float neary = mbr[axis[1] < 0 ? 4 : 1];
		float nearz = mbr[axis[2] < 0 ? 5 : 2];
		float farx = mbr[axis[0] < 0 ? 0 : 3];
		float fary = mbr[axis[1] < 0 ? 1 : 4];
		float farz = mbr[axis[2] < 0 ? 2 : 5];
		out[0] = axis[0] * nearx + axis[1] * neary + axis[2] * nearz;
		out[1] = axis[0] * farx + axis[1] * fary + axis[2] * farz;
	}
}
