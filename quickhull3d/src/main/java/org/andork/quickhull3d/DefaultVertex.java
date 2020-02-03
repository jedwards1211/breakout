package org.andork.quickhull3d;

public class DefaultVertex implements Vertex {

	public DefaultVertex(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double x;
	public double y;
	public double z;

	@Override
	public double x() {
		return x;
	}

	@Override
	public double y() {
		return y;
	}

	@Override
	public double z() {
		return z;
	}

}
