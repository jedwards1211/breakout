package org.andork.unit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

public class Angle extends UnitType<Angle> {
	public static class AngleUnit extends Unit<Angle> {
		private final DoubleUnaryOperator toRadians;

		private final DoubleUnaryOperator fromRadians;

		public AngleUnit(Angle type, String id, DoubleUnaryOperator toRadians, DoubleUnaryOperator fromRadians,
				double range) {
			super(type, id);
			this.toRadians = toRadians;
			this.fromRadians = fromRadians;
			this.range = new UnitizedDouble<>(range, this);
		}

		public final UnitizedDouble<Angle> range;
	}

	public static final Angle type;
	public static final AngleUnit degrees;
	public static final AngleUnit radians;
	public static final AngleUnit gradians;
	public static final AngleUnit percentGrade;

	public static final AngleUnit milsNATO;

	private static final Map<Unit<Angle>, Map<Unit<Angle>, Double>> doubleConversions = new HashMap<>();

	static {
		type = new Angle();
		type.addUnit(degrees = new AngleUnit(type, "deg",
				angle -> Math.PI * angle / 180.0,
				angle -> angle * 180.0 / Math.PI, 360.0));
		type.addUnit(radians = new AngleUnit(type, "rad",
				DoubleUnaryOperator.identity(),
				DoubleUnaryOperator.identity(), Math.PI * 2.0));
		type.addUnit(gradians = new AngleUnit(type, "grad",
				angle -> Math.PI * angle / 200.0,
				angle -> angle * 200.0 / Math.PI, 4000));
		type.addUnit(percentGrade = new AngleUnit(type, "% grade",
				angle -> Math.atan(angle / 100.0),
				angle -> Math.tan(angle) * 100, Double.POSITIVE_INFINITY));
		type.addUnit(milsNATO = new AngleUnit(type, "mil",
				angle -> Math.PI * angle / 3200.0,
				angle -> 3200.0 * angle / Math.PI, 6400));

		Map<Unit<Angle>, Double> degreeConversions = new HashMap<>();
		degreeConversions.put(radians, Math.PI / 180.0);
		degreeConversions.put(gradians, 400.0 / 360.0);
		doubleConversions.put(degrees, degreeConversions);

		Map<Unit<Angle>, Double> radianConversions = new HashMap<>();
		radianConversions.put(degrees, 180.0 / Math.PI);
		radianConversions.put(gradians, 200.0 / Math.PI);
		doubleConversions.put(radians, radianConversions);

		Map<Unit<Angle>, Double> gradianConversions = new HashMap<>();
		gradianConversions.put(degrees, 360.0 / 400.0);
		gradianConversions.put(radians, Math.PI / 200.0);
		doubleConversions.put(gradians, gradianConversions);

		/**
		 * Add identity conversions
		 */
		for (Unit<Angle> unit : type.units()) {
			Map<Unit<Angle>, Double> conv = doubleConversions.get(unit);
			if (conv != null) {
				conv.put(unit, 1.0);
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(type.convert(360.0, degrees, radians));
		System.out.println(type.convert(Math.PI * 2, radians, degrees));
		System.out.println(type.convert(400.0, gradians, degrees));
		System.out.println(type.convert(400.0, gradians, radians));
	}

	private Angle() {

	}

	@Override
	public double convert(double d, Unit<Angle> from, Unit<Angle> to) {
		Map<Unit<Angle>, Double> subMap = doubleConversions.get(from);
		if (subMap != null) {
			Double ratio = subMap.get(to);
			if (ratio != null) {
				return d * ratio;
			}
		}
		return ((AngleUnit) to).fromRadians.applyAsDouble(((AngleUnit) from).toRadians.applyAsDouble(d));
	}

	public static double sin(UnitizedNumber<Angle> angle) {
		return Math.sin(angle.doubleValue(radians));
	}

	public static double cos(UnitizedNumber<Angle> angle) {
		return Math.cos(angle.doubleValue(radians));
	}

	public static double tan(UnitizedNumber<Angle> angle) {
		return Math.tan(angle.doubleValue(radians));
	}

	public static UnitizedDouble<Angle> atan2(UnitizedNumber<Length> y, UnitizedNumber<Length> x) {
		return new UnitizedDouble<>(
				Math.atan2(y.doubleValue(y.unit), x.doubleValue(y.unit)), Angle.radians);
	}

	public static UnitizedDouble<Angle> normalize(UnitizedDouble<Angle> a) {
		UnitizedDouble<Angle> range = ((AngleUnit) a.unit).range;
		return a.isNegative() ? a.mod(range).add(range) : a.mod(range);
	}

	public static UnitizedDouble<Angle> opposite(UnitizedDouble<Angle> a) {
		UnitizedDouble<Angle> range = ((AngleUnit) a.unit).range;
		return normalize(a.add(range.mul(0.5)));
	}
}
