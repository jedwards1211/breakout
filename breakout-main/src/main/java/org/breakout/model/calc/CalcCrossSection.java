package org.breakout.model.calc;

import java.util.Arrays;

import org.andork.math.misc.Angles;
import org.breakout.model.CrossSectionType;

public class CalcCrossSection implements Cloneable {
	public CrossSectionType type = CrossSectionType.LRUD;
	public double facingAzimuth = Double.NaN;
	public double[] measurements;

	public CalcCrossSection() {
	}

	public CalcCrossSection(CrossSectionType type, double... measurements) {
		this.type = type;
		this.measurements = measurements;
	}

	public CalcCrossSection(CrossSectionType type, double[] measurements, double facingAzimuth) {
		this(type, measurements);
		this.facingAzimuth = facingAzimuth;
	}

	@Override
	public CalcCrossSection clone() {
		CalcCrossSection result = new CalcCrossSection();
		result.type = type;
		result.facingAzimuth = facingAzimuth;
		result.measurements = Arrays.copyOf(measurements, measurements.length);
		return result;
	}

	public CalcCrossSection rotateLRUDs180Degrees() {
		if (type != CrossSectionType.LRUD) {
			return clone();
		}
		CalcCrossSection rotated = new CalcCrossSection();
		rotated.type = CrossSectionType.LRUD;
		rotated.facingAzimuth = Angles.opposite(facingAzimuth);
		rotated.measurements = new double[] { measurements[1], measurements[0], measurements[2], measurements[3] };
		return rotated;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CalcCrossSection other = (CalcCrossSection) obj;
		if (Double.doubleToLongBits(facingAzimuth) != Double.doubleToLongBits(other.facingAzimuth)) {
			return false;
		}
		if (!Arrays.equals(measurements, other.measurements)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public void clampMin(double min) {
		for (int i = 0; i < measurements.length; i++) {
			if (Double.isNaN(measurements[i]) || min > measurements[i]) {
				measurements[i] = min;
			}
		}
	}

	public boolean isPoint() {
		for (int i = 0; i < measurements.length; i++) {
			if (measurements[i] > 0)
				return false;
		}
		return true;
	}
}
