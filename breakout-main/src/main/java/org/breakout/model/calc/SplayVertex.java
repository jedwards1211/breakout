package org.breakout.model.calc;

import org.andork.quickhull3d.Vertex;

class SplayVertex implements Vertex {
	public double originalX;
	public double originalY;
	public double originalZ;
	public double normalizedX;
	public double normalizedY;
	public double normalizedZ;

	@Override
	public double x() {
		return normalizedX;
	}

	@Override
	public double y() {
		return normalizedY;
	}

	@Override
	public double z() {
		return normalizedZ;
	}

	public boolean originalEquals(SplayVertex other) {
		return originalX == other.originalX && originalY == other.originalY && originalZ == other.originalZ;
	}
}
