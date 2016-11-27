/**
 * Generated from {@code SurveyTrip.record.js} by java-record-generator on 11/27/2016, 5:26:47 PM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */
 
package org.breakout.model;

import java.util.List;
import java.util.Date;
import org.andork.unit.Unit;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import java.util.function.Consumer;
import java.util.Objects;
import org.andork.model.DefaultProperty;
import java.util.function.BiConsumer;

/**
 *
 */
public class SurveyTrip {
	
	public static class SurveyTripProperty<V> extends DefaultProperty<SurveyTrip, V> {
		private final BiConsumer<MutableSurveyTrip, ? super V> setter;

		public SurveyTripProperty(String name, Class<? super V> valueClass,
				Function<? super SurveyTrip, ? extends V> getter, BiConsumer<MutableSurveyTrip, ? super V> setter) {
			super(name, valueClass, getter);
			this.setter = setter;
		}

		public MutableSurveyTrip set(MutableSurveyTrip trip, V newValue) {
			setter.accept(trip, newValue);
			return trip;
		}
	}
		
	public static class Properties {
		
		/**
		 * cave name
		 */
		public static final SurveyTripProperty<String> cave = new SurveyTripProperty<String>(
			"cave",
			String.class,
			r -> r.getCave(),
			(m, v) -> m.setCave(v)
		);
		

		/**
		 * trip name
		 */
		public static final SurveyTripProperty<String> name = new SurveyTripProperty<String>(
			"name",
			String.class,
			r -> r.getName(),
			(m, v) -> m.setName(v)
		);
		

		/**
		 * trip date
		 */
		public static final SurveyTripProperty<Date> date = new SurveyTripProperty<Date>(
			"date",
			Date.class,
			r -> r.getDate(),
			(m, v) -> m.setDate(v)
		);
		

		/**
		 * survey notes file path
		 */
		public static final SurveyTripProperty<String> surveyNotes = new SurveyTripProperty<String>(
			"surveyNotes",
			String.class,
			r -> r.getSurveyNotes(),
			(m, v) -> m.setSurveyNotes(v)
		);
		

		/**
		 * surveyor names
		 */
		public static final SurveyTripProperty<List<String>> surveyors = new SurveyTripProperty<List<String>>(
			"surveyors",
			List.class,
			r -> r.getSurveyors(),
			(m, v) -> m.setSurveyors(v)
		);
		

		/**
		 * default length unit
		 */
		public static final SurveyTripProperty<Unit<Length>> distanceUnit = new SurveyTripProperty<Unit<Length>>(
			"distanceUnit",
			Unit.class,
			r -> r.getDistanceUnit(),
			(m, v) -> m.setDistanceUnit(v)
		);
		

		/**
		 * default angle unit
		 */
		public static final SurveyTripProperty<Unit<Angle>> angleUnit = new SurveyTripProperty<Unit<Angle>>(
			"angleUnit",
			Unit.class,
			r -> r.getAngleUnit(),
			(m, v) -> m.setAngleUnit(v)
		);
		

		/**
		 * default frontsight azimuth unit
		 */
		public static final SurveyTripProperty<Unit<Angle>> overrideFrontAzimuthUnit = new SurveyTripProperty<Unit<Angle>>(
			"overrideFrontAzimuthUnit",
			Unit.class,
			r -> r.getOverrideFrontAzimuthUnit(),
			(m, v) -> m.setOverrideFrontAzimuthUnit(v)
		);
		

		/**
		 * default backsight azimuth unit
		 */
		public static final SurveyTripProperty<Unit<Angle>> overrideBackAzimuthUnit = new SurveyTripProperty<Unit<Angle>>(
			"overrideBackAzimuthUnit",
			Unit.class,
			r -> r.getOverrideBackAzimuthUnit(),
			(m, v) -> m.setOverrideBackAzimuthUnit(v)
		);
		

		/**
		 * default frontsight inclination unit
		 */
		public static final SurveyTripProperty<Unit<Angle>> overrideFrontInclinationUnit = new SurveyTripProperty<Unit<Angle>>(
			"overrideFrontInclinationUnit",
			Unit.class,
			r -> r.getOverrideFrontInclinationUnit(),
			(m, v) -> m.setOverrideFrontInclinationUnit(v)
		);
		

		/**
		 * default backsight inclination unit
		 */
		public static final SurveyTripProperty<Unit<Angle>> overrideBackInclinationUnit = new SurveyTripProperty<Unit<Angle>>(
			"overrideBackInclinationUnit",
			Unit.class,
			r -> r.getOverrideBackInclinationUnit(),
			(m, v) -> m.setOverrideBackInclinationUnit(v)
		);
		

		/**
		 * whether backsight azimuths are corrected
		 */
		public static final SurveyTripProperty<Boolean> backAzimuthsCorrected = new SurveyTripProperty<Boolean>(
			"backAzimuthsCorrected",
			Boolean.class,
			r -> r.areBackAzimuthsCorrected(),
			(m, v) -> m.setBackAzimuthsCorrected(v)
		);
		

		/**
		 * whether backsight inclinations are corrected
		 */
		public static final SurveyTripProperty<Boolean> backInclinationsCorrected = new SurveyTripProperty<Boolean>(
			"backInclinationsCorrected",
			Boolean.class,
			r -> r.areBackInclinationsCorrected(),
			(m, v) -> m.setBackInclinationsCorrected(v)
		);
		

		/**
		 * magnetic declination
		 */
		public static final SurveyTripProperty<String> declination = new SurveyTripProperty<String>(
			"declination",
			String.class,
			r -> r.getDeclination(),
			(m, v) -> m.setDeclination(v)
		);
		

		/**
		 * correction for shot distances
		 */
		public static final SurveyTripProperty<String> distanceCorrection = new SurveyTripProperty<String>(
			"distanceCorrection",
			String.class,
			r -> r.getDistanceCorrection(),
			(m, v) -> m.setDistanceCorrection(v)
		);
		

		/**
		 * correction for frontsight azimuths
		 */
		public static final SurveyTripProperty<String> frontAzimuthCorrection = new SurveyTripProperty<String>(
			"frontAzimuthCorrection",
			String.class,
			r -> r.getFrontAzimuthCorrection(),
			(m, v) -> m.setFrontAzimuthCorrection(v)
		);
		

		/**
		 * correction for frontsight inclinations
		 */
		public static final SurveyTripProperty<String> frontInclinationCorrection = new SurveyTripProperty<String>(
			"frontInclinationCorrection",
			String.class,
			r -> r.getFrontInclinationCorrection(),
			(m, v) -> m.setFrontInclinationCorrection(v)
		);
		

		/**
		 * correction for backsight azimuths
		 */
		public static final SurveyTripProperty<String> backAzimuthCorrection = new SurveyTripProperty<String>(
			"backAzimuthCorrection",
			String.class,
			r -> r.getBackAzimuthCorrection(),
			(m, v) -> m.setBackAzimuthCorrection(v)
		);
		

		/**
		 * correction for backsight inclinations
		 */
		public static final SurveyTripProperty<String> backInclinationCorrection = new SurveyTripProperty<String>(
			"backInclinationCorrection",
			String.class,
			r -> r.getBackInclinationCorrection(),
			(m, v) -> m.setBackInclinationCorrection(v)
		);
		
	}
	
	public static class MutableSurveyTrip implements Cloneable {
		private int modCount = 0; 
		
		/**
		 * cave name.
		 */
		private String cave;
	
		/**
		 * trip name.
		 */
		private String name;
	
		/**
		 * trip date.
		 */
		private Date date;
	
		/**
		 * survey notes file path.
		 */
		private String surveyNotes;
	
		/**
		 * surveyor names.
		 */
		private List<String> surveyors;
	
		/**
		 * default length unit.
		 */
		private Unit<Length> distanceUnit = Length.meters;
	
		/**
		 * default angle unit.
		 */
		private Unit<Angle> angleUnit = Angle.degrees;
	
		/**
		 * default frontsight azimuth unit.
		 */
		private Unit<Angle> overrideFrontAzimuthUnit;
	
		/**
		 * default backsight azimuth unit.
		 */
		private Unit<Angle> overrideBackAzimuthUnit;
	
		/**
		 * default frontsight inclination unit.
		 */
		private Unit<Angle> overrideFrontInclinationUnit;
	
		/**
		 * default backsight inclination unit.
		 */
		private Unit<Angle> overrideBackInclinationUnit;
	
		/**
		 * whether backsight azimuths are corrected.
		 */
		private boolean backAzimuthsCorrected;
	
		/**
		 * whether backsight inclinations are corrected.
		 */
		private boolean backInclinationsCorrected;
	
		/**
		 * magnetic declination.
		 */
		private String declination;
	
		/**
		 * correction for shot distances.
		 */
		private String distanceCorrection;
	
		/**
		 * correction for frontsight azimuths.
		 */
		private String frontAzimuthCorrection;
	
		/**
		 * correction for frontsight inclinations.
		 */
		private String frontInclinationCorrection;
	
		/**
		 * correction for backsight azimuths.
		 */
		private String backAzimuthCorrection;
	
		/**
		 * correction for backsight inclinations.
		 */
		private String backInclinationCorrection;
	
		
		/**
		 * @return cave name.
		 */
		public String getCave() {
			return cave;
		}
		
		/**
		 * @return trip name.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * @return trip date.
		 */
		public Date getDate() {
			return date;
		}
		
		/**
		 * @return survey notes file path.
		 */
		public String getSurveyNotes() {
			return surveyNotes;
		}
		
		/**
		 * @return surveyor names.
		 */
		public List<String> getSurveyors() {
			return surveyors;
		}
		
		/**
		 * @return default length unit.
		 */
		public Unit<Length> getDistanceUnit() {
			return distanceUnit;
		}
		
		/**
		 * @return default angle unit.
		 */
		public Unit<Angle> getAngleUnit() {
			return angleUnit;
		}
		
		/**
		 * @return default frontsight azimuth unit.
		 */
		public Unit<Angle> getOverrideFrontAzimuthUnit() {
			return overrideFrontAzimuthUnit;
		}
		
		/**
		 * @return default backsight azimuth unit.
		 */
		public Unit<Angle> getOverrideBackAzimuthUnit() {
			return overrideBackAzimuthUnit;
		}
		
		/**
		 * @return default frontsight inclination unit.
		 */
		public Unit<Angle> getOverrideFrontInclinationUnit() {
			return overrideFrontInclinationUnit;
		}
		
		/**
		 * @return default backsight inclination unit.
		 */
		public Unit<Angle> getOverrideBackInclinationUnit() {
			return overrideBackInclinationUnit;
		}
		
		/**
		 * @return whether backsight azimuths are corrected.
		 */
		public boolean areBackAzimuthsCorrected() {
			return backAzimuthsCorrected;
		}
		
		/**
		 * @return whether backsight inclinations are corrected.
		 */
		public boolean areBackInclinationsCorrected() {
			return backInclinationsCorrected;
		}
		
		/**
		 * @return magnetic declination.
		 */
		public String getDeclination() {
			return declination;
		}
		
		/**
		 * @return correction for shot distances.
		 */
		public String getDistanceCorrection() {
			return distanceCorrection;
		}
		
		/**
		 * @return correction for frontsight azimuths.
		 */
		public String getFrontAzimuthCorrection() {
			return frontAzimuthCorrection;
		}
		
		/**
		 * @return correction for frontsight inclinations.
		 */
		public String getFrontInclinationCorrection() {
			return frontInclinationCorrection;
		}
		
		/**
		 * @return correction for backsight azimuths.
		 */
		public String getBackAzimuthCorrection() {
			return backAzimuthCorrection;
		}
		
		/**
		 * @return correction for backsight inclinations.
		 */
		public String getBackInclinationCorrection() {
			return backInclinationCorrection;
		}
		
		
		/**
		 * Sets cave name.
		 *
		 * @param cave - the new value for cave name
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setCave(String cave) {
			if (Objects.equals(this.cave, cave)) return this;
			modCount++;
			this.cave = cave;
			return this;
		}
		
		/**
		 * Sets trip name.
		 *
		 * @param name - the new value for trip name
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setName(String name) {
			if (Objects.equals(this.name, name)) return this;
			modCount++;
			this.name = name;
			return this;
		}
		
		/**
		 * Sets trip date.
		 *
		 * @param date - the new value for trip date
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setDate(Date date) {
			if (Objects.equals(this.date, date)) return this;
			modCount++;
			this.date = date;
			return this;
		}
		
		/**
		 * Sets survey notes file path.
		 *
		 * @param surveyNotes - the new value for survey notes file path
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setSurveyNotes(String surveyNotes) {
			if (Objects.equals(this.surveyNotes, surveyNotes)) return this;
			modCount++;
			this.surveyNotes = surveyNotes;
			return this;
		}
		
		/**
		 * Sets surveyor names.
		 *
		 * @param surveyors - the new value for surveyor names
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setSurveyors(List<String> surveyors) {
			if (Objects.equals(this.surveyors, surveyors)) return this;
			modCount++;
			this.surveyors = surveyors;
			return this;
		}
		
		/**
		 * Sets default length unit.
		 *
		 * @param distanceUnit - the new value for default length unit
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setDistanceUnit(Unit<Length> distanceUnit) {
			if (Objects.equals(this.distanceUnit, distanceUnit)) return this;
			modCount++;
			this.distanceUnit = distanceUnit;
			return this;
		}
		
		/**
		 * Sets default angle unit.
		 *
		 * @param angleUnit - the new value for default angle unit
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setAngleUnit(Unit<Angle> angleUnit) {
			if (Objects.equals(this.angleUnit, angleUnit)) return this;
			modCount++;
			this.angleUnit = angleUnit;
			return this;
		}
		
		/**
		 * Sets default frontsight azimuth unit.
		 *
		 * @param overrideFrontAzimuthUnit - the new value for default frontsight azimuth unit
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setOverrideFrontAzimuthUnit(Unit<Angle> overrideFrontAzimuthUnit) {
			if (Objects.equals(this.overrideFrontAzimuthUnit, overrideFrontAzimuthUnit)) return this;
			modCount++;
			this.overrideFrontAzimuthUnit = overrideFrontAzimuthUnit;
			return this;
		}
		
		/**
		 * Sets default backsight azimuth unit.
		 *
		 * @param overrideBackAzimuthUnit - the new value for default backsight azimuth unit
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setOverrideBackAzimuthUnit(Unit<Angle> overrideBackAzimuthUnit) {
			if (Objects.equals(this.overrideBackAzimuthUnit, overrideBackAzimuthUnit)) return this;
			modCount++;
			this.overrideBackAzimuthUnit = overrideBackAzimuthUnit;
			return this;
		}
		
		/**
		 * Sets default frontsight inclination unit.
		 *
		 * @param overrideFrontInclinationUnit - the new value for default frontsight inclination unit
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setOverrideFrontInclinationUnit(Unit<Angle> overrideFrontInclinationUnit) {
			if (Objects.equals(this.overrideFrontInclinationUnit, overrideFrontInclinationUnit)) return this;
			modCount++;
			this.overrideFrontInclinationUnit = overrideFrontInclinationUnit;
			return this;
		}
		
		/**
		 * Sets default backsight inclination unit.
		 *
		 * @param overrideBackInclinationUnit - the new value for default backsight inclination unit
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setOverrideBackInclinationUnit(Unit<Angle> overrideBackInclinationUnit) {
			if (Objects.equals(this.overrideBackInclinationUnit, overrideBackInclinationUnit)) return this;
			modCount++;
			this.overrideBackInclinationUnit = overrideBackInclinationUnit;
			return this;
		}
		
		/**
		 * Sets whether backsight azimuths are corrected.
		 *
		 * @param backAzimuthsCorrected - the new value for whether backsight azimuths are corrected
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setBackAzimuthsCorrected(boolean backAzimuthsCorrected) {
			if (this.backAzimuthsCorrected == backAzimuthsCorrected) return this;
			modCount++;
			this.backAzimuthsCorrected = backAzimuthsCorrected;
			return this;
		}
		
		/**
		 * Sets whether backsight inclinations are corrected.
		 *
		 * @param backInclinationsCorrected - the new value for whether backsight inclinations are corrected
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setBackInclinationsCorrected(boolean backInclinationsCorrected) {
			if (this.backInclinationsCorrected == backInclinationsCorrected) return this;
			modCount++;
			this.backInclinationsCorrected = backInclinationsCorrected;
			return this;
		}
		
		/**
		 * Sets magnetic declination.
		 *
		 * @param declination - the new value for magnetic declination
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setDeclination(String declination) {
			if (Objects.equals(this.declination, declination)) return this;
			modCount++;
			this.declination = declination;
			return this;
		}
		
		/**
		 * Sets correction for shot distances.
		 *
		 * @param distanceCorrection - the new value for correction for shot distances
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setDistanceCorrection(String distanceCorrection) {
			if (Objects.equals(this.distanceCorrection, distanceCorrection)) return this;
			modCount++;
			this.distanceCorrection = distanceCorrection;
			return this;
		}
		
		/**
		 * Sets correction for frontsight azimuths.
		 *
		 * @param frontAzimuthCorrection - the new value for correction for frontsight azimuths
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setFrontAzimuthCorrection(String frontAzimuthCorrection) {
			if (Objects.equals(this.frontAzimuthCorrection, frontAzimuthCorrection)) return this;
			modCount++;
			this.frontAzimuthCorrection = frontAzimuthCorrection;
			return this;
		}
		
		/**
		 * Sets correction for frontsight inclinations.
		 *
		 * @param frontInclinationCorrection - the new value for correction for frontsight inclinations
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setFrontInclinationCorrection(String frontInclinationCorrection) {
			if (Objects.equals(this.frontInclinationCorrection, frontInclinationCorrection)) return this;
			modCount++;
			this.frontInclinationCorrection = frontInclinationCorrection;
			return this;
		}
		
		/**
		 * Sets correction for backsight azimuths.
		 *
		 * @param backAzimuthCorrection - the new value for correction for backsight azimuths
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setBackAzimuthCorrection(String backAzimuthCorrection) {
			if (Objects.equals(this.backAzimuthCorrection, backAzimuthCorrection)) return this;
			modCount++;
			this.backAzimuthCorrection = backAzimuthCorrection;
			return this;
		}
		
		/**
		 * Sets correction for backsight inclinations.
		 *
		 * @param backInclinationCorrection - the new value for correction for backsight inclinations
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setBackInclinationCorrection(String backInclinationCorrection) {
			if (Objects.equals(this.backInclinationCorrection, backInclinationCorrection)) return this;
			modCount++;
			this.backInclinationCorrection = backInclinationCorrection;
			return this;
		}
		
		
		/**
		 * Updates cave name.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code cave} and returns the new value for {@code cave}.
		 * 
		 * @return this {@code SurveyTrip} if {@code cave} is unchanged, or a copy with the updated {@code cave}.
		 */
		public MutableSurveyTrip updateCave(Function<String, String> updater) {
			return setCave(updater.apply(cave));
		}
		
		/**
		 * Updates trip name.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code name} and returns the new value for {@code name}.
		 * 
		 * @return this {@code SurveyTrip} if {@code name} is unchanged, or a copy with the updated {@code name}.
		 */
		public MutableSurveyTrip updateName(Function<String, String> updater) {
			return setName(updater.apply(name));
		}
		
		/**
		 * Updates trip date.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code date} and returns the new value for {@code date}.
		 * 
		 * @return this {@code SurveyTrip} if {@code date} is unchanged, or a copy with the updated {@code date}.
		 */
		public MutableSurveyTrip updateDate(Function<Date, Date> updater) {
			return setDate(updater.apply(date));
		}
		
		/**
		 * Updates survey notes file path.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code surveyNotes} and returns the new value for {@code surveyNotes}.
		 * 
		 * @return this {@code SurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the updated {@code surveyNotes}.
		 */
		public MutableSurveyTrip updateSurveyNotes(Function<String, String> updater) {
			return setSurveyNotes(updater.apply(surveyNotes));
		}
		
		/**
		 * Updates surveyor names.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code surveyors} and returns the new value for {@code surveyors}.
		 * 
		 * @return this {@code SurveyTrip} if {@code surveyors} is unchanged, or a copy with the updated {@code surveyors}.
		 */
		public MutableSurveyTrip updateSurveyors(Function<List<String>, List<String>> updater) {
			return setSurveyors(updater.apply(surveyors));
		}
		
		/**
		 * Updates default length unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code distanceUnit} and returns the new value for {@code distanceUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the updated {@code distanceUnit}.
		 */
		public MutableSurveyTrip updateDistanceUnit(Function<Unit<Length>, Unit<Length>> updater) {
			return setDistanceUnit(updater.apply(distanceUnit));
		}
		
		/**
		 * Updates default angle unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code angleUnit} and returns the new value for {@code angleUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code angleUnit} is unchanged, or a copy with the updated {@code angleUnit}.
		 */
		public MutableSurveyTrip updateAngleUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setAngleUnit(updater.apply(angleUnit));
		}
		
		/**
		 * Updates default frontsight azimuth unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code overrideFrontAzimuthUnit} and returns the new value for {@code overrideFrontAzimuthUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the updated {@code overrideFrontAzimuthUnit}.
		 */
		public MutableSurveyTrip updateOverrideFrontAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setOverrideFrontAzimuthUnit(updater.apply(overrideFrontAzimuthUnit));
		}
		
		/**
		 * Updates default backsight azimuth unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code overrideBackAzimuthUnit} and returns the new value for {@code overrideBackAzimuthUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the updated {@code overrideBackAzimuthUnit}.
		 */
		public MutableSurveyTrip updateOverrideBackAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setOverrideBackAzimuthUnit(updater.apply(overrideBackAzimuthUnit));
		}
		
		/**
		 * Updates default frontsight inclination unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code overrideFrontInclinationUnit} and returns the new value for {@code overrideFrontInclinationUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the updated {@code overrideFrontInclinationUnit}.
		 */
		public MutableSurveyTrip updateOverrideFrontInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setOverrideFrontInclinationUnit(updater.apply(overrideFrontInclinationUnit));
		}
		
		/**
		 * Updates default backsight inclination unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code overrideBackInclinationUnit} and returns the new value for {@code overrideBackInclinationUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the updated {@code overrideBackInclinationUnit}.
		 */
		public MutableSurveyTrip updateOverrideBackInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setOverrideBackInclinationUnit(updater.apply(overrideBackInclinationUnit));
		}
		
		/**
		 * Updates whether backsight azimuths are corrected.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code backAzimuthsCorrected} and returns the new value for {@code backAzimuthsCorrected}.
		 * 
		 * @return this {@code SurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the updated {@code backAzimuthsCorrected}.
		 */
		public MutableSurveyTrip updateBackAzimuthsCorrected(Function<Boolean, Boolean> updater) {
			return setBackAzimuthsCorrected(updater.apply(backAzimuthsCorrected));
		}
		
		/**
		 * Updates whether backsight inclinations are corrected.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code backInclinationsCorrected} and returns the new value for {@code backInclinationsCorrected}.
		 * 
		 * @return this {@code SurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the updated {@code backInclinationsCorrected}.
		 */
		public MutableSurveyTrip updateBackInclinationsCorrected(Function<Boolean, Boolean> updater) {
			return setBackInclinationsCorrected(updater.apply(backInclinationsCorrected));
		}
		
		/**
		 * Updates magnetic declination.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code declination} and returns the new value for {@code declination}.
		 * 
		 * @return this {@code SurveyTrip} if {@code declination} is unchanged, or a copy with the updated {@code declination}.
		 */
		public MutableSurveyTrip updateDeclination(Function<String, String> updater) {
			return setDeclination(updater.apply(declination));
		}
		
		/**
		 * Updates correction for shot distances.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code distanceCorrection} and returns the new value for {@code distanceCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the updated {@code distanceCorrection}.
		 */
		public MutableSurveyTrip updateDistanceCorrection(Function<String, String> updater) {
			return setDistanceCorrection(updater.apply(distanceCorrection));
		}
		
		/**
		 * Updates correction for frontsight azimuths.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code frontAzimuthCorrection} and returns the new value for {@code frontAzimuthCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the updated {@code frontAzimuthCorrection}.
		 */
		public MutableSurveyTrip updateFrontAzimuthCorrection(Function<String, String> updater) {
			return setFrontAzimuthCorrection(updater.apply(frontAzimuthCorrection));
		}
		
		/**
		 * Updates correction for frontsight inclinations.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code frontInclinationCorrection} and returns the new value for {@code frontInclinationCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the updated {@code frontInclinationCorrection}.
		 */
		public MutableSurveyTrip updateFrontInclinationCorrection(Function<String, String> updater) {
			return setFrontInclinationCorrection(updater.apply(frontInclinationCorrection));
		}
		
		/**
		 * Updates correction for backsight azimuths.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code backAzimuthCorrection} and returns the new value for {@code backAzimuthCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the updated {@code backAzimuthCorrection}.
		 */
		public MutableSurveyTrip updateBackAzimuthCorrection(Function<String, String> updater) {
			return setBackAzimuthCorrection(updater.apply(backAzimuthCorrection));
		}
		
		/**
		 * Updates correction for backsight inclinations.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code backInclinationCorrection} and returns the new value for {@code backInclinationCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the updated {@code backInclinationCorrection}.
		 */
		public MutableSurveyTrip updateBackInclinationCorrection(Function<String, String> updater) {
			return setBackInclinationCorrection(updater.apply(backInclinationCorrection));
		}
		
		@Override
		public MutableSurveyTrip clone() {
			try {
				return (MutableSurveyTrip) super.clone(); 
			} catch (Exception e) {
				// should not happen
				throw new RuntimeException(e);
			} 
		}
	}
	
	private final MutableSurveyTrip data;
	
	private SurveyTrip(MutableSurveyTrip data) {
		this.data = data;
	}
	
	public SurveyTrip() {
		this(new MutableSurveyTrip());
	}
	
	public boolean equals(Object o) {
		return o == this;
	}
	
	/**
	 * @param initializer a {@link Consumer} that initializes a {@code SurveyTrip}.
	 *
	 * @return a new {@code SurveyTrip} with values initialized by {@code initializer}.
	 */
	public static SurveyTrip create(Consumer<MutableSurveyTrip> initializer) {
		MutableSurveyTrip data = new MutableSurveyTrip(); 
		initializer.accept(data);
		return new SurveyTrip(data);
	}
	
	/**
	 * @param mutator a {@link Consumer} that applies mutations to this {@code SurveyTrip}.
	 *
	 * @return a copy of this {@code SurveyTrip} with the given mutations applied.
	 */
	public SurveyTrip withMutations(Consumer<MutableSurveyTrip> mutator) {
		MutableSurveyTrip newData = data.clone(); 
		mutator.accept(newData);
		return newData.modCount == data.modCount ? this : new SurveyTrip(newData);
	}
	
	
	/**
	 * @return cave name.
	 */
	public String getCave() {
		return data.cave;
	}
	
	/**
	 * @return trip name.
	 */
	public String getName() {
		return data.name;
	}
	
	/**
	 * @return trip date.
	 */
	public Date getDate() {
		return data.date;
	}
	
	/**
	 * @return survey notes file path.
	 */
	public String getSurveyNotes() {
		return data.surveyNotes;
	}
	
	/**
	 * @return surveyor names.
	 */
	public List<String> getSurveyors() {
		return data.surveyors;
	}
	
	/**
	 * @return default length unit.
	 */
	public Unit<Length> getDistanceUnit() {
		return data.distanceUnit;
	}
	
	/**
	 * @return default angle unit.
	 */
	public Unit<Angle> getAngleUnit() {
		return data.angleUnit;
	}
	
	/**
	 * @return default frontsight azimuth unit.
	 */
	public Unit<Angle> getOverrideFrontAzimuthUnit() {
		return data.overrideFrontAzimuthUnit;
	}
	
	/**
	 * @return default backsight azimuth unit.
	 */
	public Unit<Angle> getOverrideBackAzimuthUnit() {
		return data.overrideBackAzimuthUnit;
	}
	
	/**
	 * @return default frontsight inclination unit.
	 */
	public Unit<Angle> getOverrideFrontInclinationUnit() {
		return data.overrideFrontInclinationUnit;
	}
	
	/**
	 * @return default backsight inclination unit.
	 */
	public Unit<Angle> getOverrideBackInclinationUnit() {
		return data.overrideBackInclinationUnit;
	}
	
	/**
	 * @return whether backsight azimuths are corrected.
	 */
	public boolean areBackAzimuthsCorrected() {
		return data.backAzimuthsCorrected;
	}
	
	/**
	 * @return whether backsight inclinations are corrected.
	 */
	public boolean areBackInclinationsCorrected() {
		return data.backInclinationsCorrected;
	}
	
	/**
	 * @return magnetic declination.
	 */
	public String getDeclination() {
		return data.declination;
	}
	
	/**
	 * @return correction for shot distances.
	 */
	public String getDistanceCorrection() {
		return data.distanceCorrection;
	}
	
	/**
	 * @return correction for frontsight azimuths.
	 */
	public String getFrontAzimuthCorrection() {
		return data.frontAzimuthCorrection;
	}
	
	/**
	 * @return correction for frontsight inclinations.
	 */
	public String getFrontInclinationCorrection() {
		return data.frontInclinationCorrection;
	}
	
	/**
	 * @return correction for backsight azimuths.
	 */
	public String getBackAzimuthCorrection() {
		return data.backAzimuthCorrection;
	}
	
	/**
	 * @return correction for backsight inclinations.
	 */
	public String getBackInclinationCorrection() {
		return data.backInclinationCorrection;
	}
	
	
	/**
	 * Sets cave name.
	 *
	 * @param cave - the new value for cave name
	 * 
	 * @return this {@code SurveyTrip} if {@code cave} is unchanged, or a copy with the new {@code cave}.
	 */
	public SurveyTrip setCave(String cave) {
		if (Objects.equals(data.cave, cave)) return this;
		return new SurveyTrip(data.clone().setCave(cave));
	}
	
	/**
	 * Sets trip name.
	 *
	 * @param name - the new value for trip name
	 * 
	 * @return this {@code SurveyTrip} if {@code name} is unchanged, or a copy with the new {@code name}.
	 */
	public SurveyTrip setName(String name) {
		if (Objects.equals(data.name, name)) return this;
		return new SurveyTrip(data.clone().setName(name));
	}
	
	/**
	 * Sets trip date.
	 *
	 * @param date - the new value for trip date
	 * 
	 * @return this {@code SurveyTrip} if {@code date} is unchanged, or a copy with the new {@code date}.
	 */
	public SurveyTrip setDate(Date date) {
		if (Objects.equals(data.date, date)) return this;
		return new SurveyTrip(data.clone().setDate(date));
	}
	
	/**
	 * Sets survey notes file path.
	 *
	 * @param surveyNotes - the new value for survey notes file path
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the new {@code surveyNotes}.
	 */
	public SurveyTrip setSurveyNotes(String surveyNotes) {
		if (Objects.equals(data.surveyNotes, surveyNotes)) return this;
		return new SurveyTrip(data.clone().setSurveyNotes(surveyNotes));
	}
	
	/**
	 * Sets surveyor names.
	 *
	 * @param surveyors - the new value for surveyor names
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyors} is unchanged, or a copy with the new {@code surveyors}.
	 */
	public SurveyTrip setSurveyors(List<String> surveyors) {
		if (Objects.equals(data.surveyors, surveyors)) return this;
		return new SurveyTrip(data.clone().setSurveyors(surveyors));
	}
	
	/**
	 * Sets default length unit.
	 *
	 * @param distanceUnit - the new value for default length unit
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the new {@code distanceUnit}.
	 */
	public SurveyTrip setDistanceUnit(Unit<Length> distanceUnit) {
		if (Objects.equals(data.distanceUnit, distanceUnit)) return this;
		return new SurveyTrip(data.clone().setDistanceUnit(distanceUnit));
	}
	
	/**
	 * Sets default angle unit.
	 *
	 * @param angleUnit - the new value for default angle unit
	 * 
	 * @return this {@code SurveyTrip} if {@code angleUnit} is unchanged, or a copy with the new {@code angleUnit}.
	 */
	public SurveyTrip setAngleUnit(Unit<Angle> angleUnit) {
		if (Objects.equals(data.angleUnit, angleUnit)) return this;
		return new SurveyTrip(data.clone().setAngleUnit(angleUnit));
	}
	
	/**
	 * Sets default frontsight azimuth unit.
	 *
	 * @param overrideFrontAzimuthUnit - the new value for default frontsight azimuth unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the new {@code overrideFrontAzimuthUnit}.
	 */
	public SurveyTrip setOverrideFrontAzimuthUnit(Unit<Angle> overrideFrontAzimuthUnit) {
		if (Objects.equals(data.overrideFrontAzimuthUnit, overrideFrontAzimuthUnit)) return this;
		return new SurveyTrip(data.clone().setOverrideFrontAzimuthUnit(overrideFrontAzimuthUnit));
	}
	
	/**
	 * Sets default backsight azimuth unit.
	 *
	 * @param overrideBackAzimuthUnit - the new value for default backsight azimuth unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the new {@code overrideBackAzimuthUnit}.
	 */
	public SurveyTrip setOverrideBackAzimuthUnit(Unit<Angle> overrideBackAzimuthUnit) {
		if (Objects.equals(data.overrideBackAzimuthUnit, overrideBackAzimuthUnit)) return this;
		return new SurveyTrip(data.clone().setOverrideBackAzimuthUnit(overrideBackAzimuthUnit));
	}
	
	/**
	 * Sets default frontsight inclination unit.
	 *
	 * @param overrideFrontInclinationUnit - the new value for default frontsight inclination unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the new {@code overrideFrontInclinationUnit}.
	 */
	public SurveyTrip setOverrideFrontInclinationUnit(Unit<Angle> overrideFrontInclinationUnit) {
		if (Objects.equals(data.overrideFrontInclinationUnit, overrideFrontInclinationUnit)) return this;
		return new SurveyTrip(data.clone().setOverrideFrontInclinationUnit(overrideFrontInclinationUnit));
	}
	
	/**
	 * Sets default backsight inclination unit.
	 *
	 * @param overrideBackInclinationUnit - the new value for default backsight inclination unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the new {@code overrideBackInclinationUnit}.
	 */
	public SurveyTrip setOverrideBackInclinationUnit(Unit<Angle> overrideBackInclinationUnit) {
		if (Objects.equals(data.overrideBackInclinationUnit, overrideBackInclinationUnit)) return this;
		return new SurveyTrip(data.clone().setOverrideBackInclinationUnit(overrideBackInclinationUnit));
	}
	
	/**
	 * Sets whether backsight azimuths are corrected.
	 *
	 * @param backAzimuthsCorrected - the new value for whether backsight azimuths are corrected
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the new {@code backAzimuthsCorrected}.
	 */
	public SurveyTrip setBackAzimuthsCorrected(boolean backAzimuthsCorrected) {
		if (data.backAzimuthsCorrected == backAzimuthsCorrected) return this;
		return new SurveyTrip(data.clone().setBackAzimuthsCorrected(backAzimuthsCorrected));
	}
	
	/**
	 * Sets whether backsight inclinations are corrected.
	 *
	 * @param backInclinationsCorrected - the new value for whether backsight inclinations are corrected
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the new {@code backInclinationsCorrected}.
	 */
	public SurveyTrip setBackInclinationsCorrected(boolean backInclinationsCorrected) {
		if (data.backInclinationsCorrected == backInclinationsCorrected) return this;
		return new SurveyTrip(data.clone().setBackInclinationsCorrected(backInclinationsCorrected));
	}
	
	/**
	 * Sets magnetic declination.
	 *
	 * @param declination - the new value for magnetic declination
	 * 
	 * @return this {@code SurveyTrip} if {@code declination} is unchanged, or a copy with the new {@code declination}.
	 */
	public SurveyTrip setDeclination(String declination) {
		if (Objects.equals(data.declination, declination)) return this;
		return new SurveyTrip(data.clone().setDeclination(declination));
	}
	
	/**
	 * Sets correction for shot distances.
	 *
	 * @param distanceCorrection - the new value for correction for shot distances
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the new {@code distanceCorrection}.
	 */
	public SurveyTrip setDistanceCorrection(String distanceCorrection) {
		if (Objects.equals(data.distanceCorrection, distanceCorrection)) return this;
		return new SurveyTrip(data.clone().setDistanceCorrection(distanceCorrection));
	}
	
	/**
	 * Sets correction for frontsight azimuths.
	 *
	 * @param frontAzimuthCorrection - the new value for correction for frontsight azimuths
	 * 
	 * @return this {@code SurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the new {@code frontAzimuthCorrection}.
	 */
	public SurveyTrip setFrontAzimuthCorrection(String frontAzimuthCorrection) {
		if (Objects.equals(data.frontAzimuthCorrection, frontAzimuthCorrection)) return this;
		return new SurveyTrip(data.clone().setFrontAzimuthCorrection(frontAzimuthCorrection));
	}
	
	/**
	 * Sets correction for frontsight inclinations.
	 *
	 * @param frontInclinationCorrection - the new value for correction for frontsight inclinations
	 * 
	 * @return this {@code SurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the new {@code frontInclinationCorrection}.
	 */
	public SurveyTrip setFrontInclinationCorrection(String frontInclinationCorrection) {
		if (Objects.equals(data.frontInclinationCorrection, frontInclinationCorrection)) return this;
		return new SurveyTrip(data.clone().setFrontInclinationCorrection(frontInclinationCorrection));
	}
	
	/**
	 * Sets correction for backsight azimuths.
	 *
	 * @param backAzimuthCorrection - the new value for correction for backsight azimuths
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the new {@code backAzimuthCorrection}.
	 */
	public SurveyTrip setBackAzimuthCorrection(String backAzimuthCorrection) {
		if (Objects.equals(data.backAzimuthCorrection, backAzimuthCorrection)) return this;
		return new SurveyTrip(data.clone().setBackAzimuthCorrection(backAzimuthCorrection));
	}
	
	/**
	 * Sets correction for backsight inclinations.
	 *
	 * @param backInclinationCorrection - the new value for correction for backsight inclinations
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the new {@code backInclinationCorrection}.
	 */
	public SurveyTrip setBackInclinationCorrection(String backInclinationCorrection) {
		if (Objects.equals(data.backInclinationCorrection, backInclinationCorrection)) return this;
		return new SurveyTrip(data.clone().setBackInclinationCorrection(backInclinationCorrection));
	}
	
	
	/**
	 * Updates cave name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code cave} and returns the new value for {@code cave}.
	 * 
	 * @return this {@code SurveyTrip} if {@code cave} is unchanged, or a copy with the updated {@code cave}.
	 */
	public SurveyTrip updateCave(Function<String, String> updater) {
		return setCave(updater.apply(data.cave));
	}
	
	/**
	 * Updates trip name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code name} and returns the new value for {@code name}.
	 * 
	 * @return this {@code SurveyTrip} if {@code name} is unchanged, or a copy with the updated {@code name}.
	 */
	public SurveyTrip updateName(Function<String, String> updater) {
		return setName(updater.apply(data.name));
	}
	
	/**
	 * Updates trip date.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code date} and returns the new value for {@code date}.
	 * 
	 * @return this {@code SurveyTrip} if {@code date} is unchanged, or a copy with the updated {@code date}.
	 */
	public SurveyTrip updateDate(Function<Date, Date> updater) {
		return setDate(updater.apply(data.date));
	}
	
	/**
	 * Updates survey notes file path.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code surveyNotes} and returns the new value for {@code surveyNotes}.
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the updated {@code surveyNotes}.
	 */
	public SurveyTrip updateSurveyNotes(Function<String, String> updater) {
		return setSurveyNotes(updater.apply(data.surveyNotes));
	}
	
	/**
	 * Updates surveyor names.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code surveyors} and returns the new value for {@code surveyors}.
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyors} is unchanged, or a copy with the updated {@code surveyors}.
	 */
	public SurveyTrip updateSurveyors(Function<List<String>, List<String>> updater) {
		return setSurveyors(updater.apply(data.surveyors));
	}
	
	/**
	 * Updates default length unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distanceUnit} and returns the new value for {@code distanceUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the updated {@code distanceUnit}.
	 */
	public SurveyTrip updateDistanceUnit(Function<Unit<Length>, Unit<Length>> updater) {
		return setDistanceUnit(updater.apply(data.distanceUnit));
	}
	
	/**
	 * Updates default angle unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code angleUnit} and returns the new value for {@code angleUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code angleUnit} is unchanged, or a copy with the updated {@code angleUnit}.
	 */
	public SurveyTrip updateAngleUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setAngleUnit(updater.apply(data.angleUnit));
	}
	
	/**
	 * Updates default frontsight azimuth unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFrontAzimuthUnit} and returns the new value for {@code overrideFrontAzimuthUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the updated {@code overrideFrontAzimuthUnit}.
	 */
	public SurveyTrip updateOverrideFrontAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setOverrideFrontAzimuthUnit(updater.apply(data.overrideFrontAzimuthUnit));
	}
	
	/**
	 * Updates default backsight azimuth unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideBackAzimuthUnit} and returns the new value for {@code overrideBackAzimuthUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the updated {@code overrideBackAzimuthUnit}.
	 */
	public SurveyTrip updateOverrideBackAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setOverrideBackAzimuthUnit(updater.apply(data.overrideBackAzimuthUnit));
	}
	
	/**
	 * Updates default frontsight inclination unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFrontInclinationUnit} and returns the new value for {@code overrideFrontInclinationUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the updated {@code overrideFrontInclinationUnit}.
	 */
	public SurveyTrip updateOverrideFrontInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setOverrideFrontInclinationUnit(updater.apply(data.overrideFrontInclinationUnit));
	}
	
	/**
	 * Updates default backsight inclination unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideBackInclinationUnit} and returns the new value for {@code overrideBackInclinationUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the updated {@code overrideBackInclinationUnit}.
	 */
	public SurveyTrip updateOverrideBackInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setOverrideBackInclinationUnit(updater.apply(data.overrideBackInclinationUnit));
	}
	
	/**
	 * Updates whether backsight azimuths are corrected.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuthsCorrected} and returns the new value for {@code backAzimuthsCorrected}.
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the updated {@code backAzimuthsCorrected}.
	 */
	public SurveyTrip updateBackAzimuthsCorrected(Function<Boolean, Boolean> updater) {
		return setBackAzimuthsCorrected(updater.apply(data.backAzimuthsCorrected));
	}
	
	/**
	 * Updates whether backsight inclinations are corrected.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclinationsCorrected} and returns the new value for {@code backInclinationsCorrected}.
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the updated {@code backInclinationsCorrected}.
	 */
	public SurveyTrip updateBackInclinationsCorrected(Function<Boolean, Boolean> updater) {
		return setBackInclinationsCorrected(updater.apply(data.backInclinationsCorrected));
	}
	
	/**
	 * Updates magnetic declination.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code declination} and returns the new value for {@code declination}.
	 * 
	 * @return this {@code SurveyTrip} if {@code declination} is unchanged, or a copy with the updated {@code declination}.
	 */
	public SurveyTrip updateDeclination(Function<String, String> updater) {
		return setDeclination(updater.apply(data.declination));
	}
	
	/**
	 * Updates correction for shot distances.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distanceCorrection} and returns the new value for {@code distanceCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the updated {@code distanceCorrection}.
	 */
	public SurveyTrip updateDistanceCorrection(Function<String, String> updater) {
		return setDistanceCorrection(updater.apply(data.distanceCorrection));
	}
	
	/**
	 * Updates correction for frontsight azimuths.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontAzimuthCorrection} and returns the new value for {@code frontAzimuthCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the updated {@code frontAzimuthCorrection}.
	 */
	public SurveyTrip updateFrontAzimuthCorrection(Function<String, String> updater) {
		return setFrontAzimuthCorrection(updater.apply(data.frontAzimuthCorrection));
	}
	
	/**
	 * Updates correction for frontsight inclinations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontInclinationCorrection} and returns the new value for {@code frontInclinationCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the updated {@code frontInclinationCorrection}.
	 */
	public SurveyTrip updateFrontInclinationCorrection(Function<String, String> updater) {
		return setFrontInclinationCorrection(updater.apply(data.frontInclinationCorrection));
	}
	
	/**
	 * Updates correction for backsight azimuths.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuthCorrection} and returns the new value for {@code backAzimuthCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the updated {@code backAzimuthCorrection}.
	 */
	public SurveyTrip updateBackAzimuthCorrection(Function<String, String> updater) {
		return setBackAzimuthCorrection(updater.apply(data.backAzimuthCorrection));
	}
	
	/**
	 * Updates correction for backsight inclinations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclinationCorrection} and returns the new value for {@code backInclinationCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the updated {@code backInclinationCorrection}.
	 */
	public SurveyTrip updateBackInclinationCorrection(Function<String, String> updater) {
		return setBackInclinationCorrection(updater.apply(data.backInclinationCorrection));
	}
	
}
