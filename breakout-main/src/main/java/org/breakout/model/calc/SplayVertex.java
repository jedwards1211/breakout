package org.breakout.model.calc;

import org.andork.quickhull3d.Vertex;

class SplayVertex implements Vertex {
	public double originalX;
	public double originalY;
	public double originalZ;
	public double normalizedX;
	public double normalizedY;
	public double normalizedZ;

	public SplayVertex(double[] position) {
		originalX = position[0];
		originalY = position[1];
		originalZ = position[2];
	}

	public SplayVertex(CalcStation splayStation, CalcStation surveyStation) {
		originalX = splayStation.position[0];
		originalY = splayStation.position[1];
		originalZ = splayStation.position[2];
		normalizedX = splayStation.position[0] - surveyStation.position[0];
		normalizedY = splayStation.position[1] - surveyStation.position[1];
		normalizedZ = splayStation.position[2] - surveyStation.position[2];
		double length = Math.sqrt(normalizedX * normalizedX + normalizedY * normalizedY + normalizedZ * normalizedZ);
		if (length != 0) {
			normalizedX /= length;
			normalizedY /= length;
			normalizedZ /= length;
		}
	}

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
