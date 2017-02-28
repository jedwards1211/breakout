package org.andork.unit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Length extends UnitType<Length> {
	public static final Length type;

	public static final Unit<Length> kilometers;
	public static final Unit<Length> meters;
	public static final Unit<Length> centimeters;
	public static final Unit<Length> miles;
	public static final Unit<Length> yards;
	public static final Unit<Length> feet;
	public static final Unit<Length> inches;

	private static final Map<Unit<Length>, Map<Unit<Length>, BigDecimal>> bigDecimalConversions = new HashMap<>();
	private static final Map<Unit<Length>, Map<Unit<Length>, Double>> doubleConversions = new HashMap<>();

	private static final Set<Unit<Length>> imperialUnits = new HashSet<>();
	private static final Set<Unit<Length>> metricUnits = new HashSet<>();

	static {
		type = new Length();
		type.addUnit(kilometers = new Unit<>(type, "km"));
		type.addUnit(meters = new Unit<>(type, "m"));
		type.addUnit(centimeters = new Unit<>(type, "cm"));
		type.addUnit(miles = new Unit<>(type, "mi"));
		type.addUnit(yards = new Unit<>(type, "yd"));
		type.addUnit(feet = new Unit<>(type, "ft"));
		type.addUnit(inches = new Unit<>(type, "in"));

		metricUnits.add(kilometers);
		metricUnits.add(meters);
		metricUnits.add(centimeters);

		imperialUnits.add(miles);
		imperialUnits.add(yards);
		imperialUnits.add(feet);
		imperialUnits.add(inches);

		for (Unit<Length> unit : type.units()) {
			Map<Unit<Length>, BigDecimal> bigDecimalConv = new HashMap<>();
			// conversion from unit to itself
			bigDecimalConv.put(unit, BigDecimal.ONE);

			bigDecimalConversions.put(unit, bigDecimalConv);
			doubleConversions.put(unit, new HashMap<>());
		}

		// set up conversions from meters to feet and all metric units

		Map<Unit<Length>, BigDecimal> meterConversions = bigDecimalConversions.get(meters);
		meterConversions.put(meters, BigDecimal.ONE);
		meterConversions
				.put(feet, BigDecimal.ONE.divide(new BigDecimal("0.3048"), 64, BigDecimal.ROUND_HALF_UP));
		meterConversions.put(kilometers, new BigDecimal("0.001"));
		meterConversions.put(centimeters, new BigDecimal(100));

		// set up conversions from other metric units to meters

		for (Unit<Length> unit : metricUnits) {
			if (unit == meters) {
				continue;
			}
			bigDecimalConversions.get(unit).put(meters,
					BigDecimal.ONE.divide(meterConversions.get(unit), 64, BigDecimal.ROUND_HALF_UP));
		}

		// set up conversions from feet to meters and all imperial units

		Map<Unit<Length>, BigDecimal> feetConversions = bigDecimalConversions.get(feet);
		feetConversions.put(feet, BigDecimal.ONE);
		feetConversions.put(meters, new BigDecimal("0.3048"));
		feetConversions.put(miles, BigDecimal.ONE.divide(new BigDecimal(5280), 64, BigDecimal.ROUND_HALF_UP));
		feetConversions.put(yards, BigDecimal.ONE.divide(new BigDecimal(3), 64, BigDecimal.ROUND_HALF_UP));
		feetConversions.put(inches, new BigDecimal(12));

		// set up conversions from other imperial units to feet

		for (Unit<Length> unit : imperialUnits) {
			if (unit == feet) {
				continue;
			}
			bigDecimalConversions.get(unit).put(feet,
					BigDecimal.ONE.divide(feetConversions.get(unit), 64, BigDecimal.ROUND_HALF_UP));
		}

		// now go through all combinations of metric and imperial units and
		// compute the conversions

		for (Unit<Length> metricUnit : metricUnits) {
			Map<Unit<Length>, BigDecimal> metricConversions = bigDecimalConversions.get(metricUnit);

			for (Unit<Length> imperialUnit : imperialUnits) {
				if (metricUnit == meters && imperialUnit == feet) {
					continue;
				}

				// metricUnit/imperialUnit = metricUnit/m * m/ft *
				// ft/imperialUnit

				metricConversions.put(imperialUnit,
						metricConversions.get(meters)
								.multiply(meterConversions.get(feet))
								.multiply(feetConversions.get(imperialUnit)));

				Map<Unit<Length>, BigDecimal> imperialConversions = bigDecimalConversions.get(imperialUnit);

				// imperialUnit/metricUnit = imperialUnit/ft * ft/m *
				// m/metricUnit

				imperialConversions.put(metricUnit,
						imperialConversions.get(feet)
								.multiply(feetConversions.get(meters))
								.multiply(meterConversions.get(metricUnit)));
			}
		}

		// create the double conversions map by rounding all the BigDecimals

		for (Unit<Length> unit : type.units()) {
			Map<Unit<Length>, BigDecimal> bigDecimalConv = bigDecimalConversions.get(unit);
			Map<Unit<Length>, Double> doubleConv = doubleConversions.get(unit);

			for (Map.Entry<Unit<Length>, BigDecimal> entry : bigDecimalConv.entrySet()) {
				doubleConv.put(entry.getKey(), entry.getValue().doubleValue());
			}
		}
	}

	private Length() {

	}

	@Override
	public double convert(double d, Unit<Length> from, Unit<Length> to) {
		return d * doubleConversions.get(from).get(to);
	}

}
