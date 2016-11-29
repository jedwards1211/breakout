/**
 * Generated from {@code SurveyTrip.record.js} by java-record-generator on 11/28/2016, 11:18:30 PM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */
 
package org.breakout.model;

import java.util.List;
import org.andork.unit.Unit;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import static org.andork.util.JavaScript.or;
import java.util.function.Consumer;
import java.util.Objects;
import org.andork.model.DefaultProperty;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public class SurveyTrip {
	
	public static class Properties {
		public static <V> DefaultProperty<SurveyTrip, V> create(
				String name, Class<? super V> valueClass,
				Function<? super SurveyTrip, ? extends V> getter, 
				BiConsumer<MutableSurveyTrip, ? super V> setter) {
			return new DefaultProperty<SurveyTrip, V>(
				name, valueClass, getter, (m, v) -> setter.accept((MutableSurveyTrip) m, v)
			);
		}
		
		
		/**
		 * cave name
		 */
		public static final DefaultProperty<SurveyTrip, String> cave = create(
			"cave", String.class,
			r -> r.getCave(),
			(m, v) -> m.setCave(v)
		);
		

		/**
		 * trip name
		 */
		public static final DefaultProperty<SurveyTrip, String> name = create(
			"name", String.class,
			r -> r.getName(),
			(m, v) -> m.setName(v)
		);
		

		/**
		 * trip date
		 */
		public static final DefaultProperty<SurveyTrip, String> date = create(
			"date", String.class,
			r -> r.getDate(),
			(m, v) -> m.setDate(v)
		);
		

		/**
		 * survey notes file path
		 */
		public static final DefaultProperty<SurveyTrip, String> surveyNotes = create(
			"surveyNotes", String.class,
			r -> r.getSurveyNotes(),
			(m, v) -> m.setSurveyNotes(v)
		);
		

		/**
		 * surveyor names
		 */
		public static final DefaultProperty<SurveyTrip, List<String>> surveyors = create(
			"surveyors", List.class,
			r -> r.getSurveyors(),
			(m, v) -> m.setSurveyors(v)
		);
		

		/**
		 * default length unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Length>> distanceUnit = create(
			"distanceUnit", Unit.class,
			r -> r.getDistanceUnit(),
			(m, v) -> m.setDistanceUnit(v)
		);
		

		/**
		 * default angle unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> angleUnit = create(
			"angleUnit", Unit.class,
			r -> r.getAngleUnit(),
			(m, v) -> m.setAngleUnit(v)
		);
		

		/**
		 * default frontsight azimuth unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideFrontAzimuthUnit = create(
			"overrideFrontAzimuthUnit", Unit.class,
			r -> r.getOverrideFrontAzimuthUnit(),
			(m, v) -> m.setOverrideFrontAzimuthUnit(v)
		);
		

		/**
		 * default backsight azimuth unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideBackAzimuthUnit = create(
			"overrideBackAzimuthUnit", Unit.class,
			r -> r.getOverrideBackAzimuthUnit(),
			(m, v) -> m.setOverrideBackAzimuthUnit(v)
		);
		

		/**
		 * default frontsight inclination unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideFrontInclinationUnit = create(
			"overrideFrontInclinationUnit", Unit.class,
			r -> r.getOverrideFrontInclinationUnit(),
			(m, v) -> m.setOverrideFrontInclinationUnit(v)
		);
		

		/**
		 * default backsight inclination unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideBackInclinationUnit = create(
			"overrideBackInclinationUnit", Unit.class,
			r -> r.getOverrideBackInclinationUnit(),
			(m, v) -> m.setOverrideBackInclinationUnit(v)
		);
		

		/**
		 * whether backsight azimuths are corrected
		 */
		public static final DefaultProperty<SurveyTrip, Boolean> backAzimuthsCorrected = create(
			"backAzimuthsCorrected", Boolean.class,
			r -> r.areBackAzimuthsCorrected(),
			(m, v) -> m.setBackAzimuthsCorrected(v)
		);
		

		/**
		 * whether backsight inclinations are corrected
		 */
		public static final DefaultProperty<SurveyTrip, Boolean> backInclinationsCorrected = create(
			"backInclinationsCorrected", Boolean.class,
			r -> r.areBackInclinationsCorrected(),
			(m, v) -> m.setBackInclinationsCorrected(v)
		);
		

		/**
		 * magnetic declination
		 */
		public static final DefaultProperty<SurveyTrip, String> declination = create(
			"declination", String.class,
			r -> r.getDeclination(),
			(m, v) -> m.setDeclination(v)
		);
		

		/**
		 * correction for shot distances
		 */
		public static final DefaultProperty<SurveyTrip, String> distanceCorrection = create(
			"distanceCorrection", String.class,
			r -> r.getDistanceCorrection(),
			(m, v) -> m.setDistanceCorrection(v)
		);
		

		/**
		 * correction for frontsight azimuths
		 */
		public static final DefaultProperty<SurveyTrip, String> frontAzimuthCorrection = create(
			"frontAzimuthCorrection", String.class,
			r -> r.getFrontAzimuthCorrection(),
			(m, v) -> m.setFrontAzimuthCorrection(v)
		);
		

		/**
		 * correction for frontsight inclinations
		 */
		public static final DefaultProperty<SurveyTrip, String> frontInclinationCorrection = create(
			"frontInclinationCorrection", String.class,
			r -> r.getFrontInclinationCorrection(),
			(m, v) -> m.setFrontInclinationCorrection(v)
		);
		

		/**
		 * correction for backsight azimuths
		 */
		public static final DefaultProperty<SurveyTrip, String> backAzimuthCorrection = create(
			"backAzimuthCorrection", String.class,
			r -> r.getBackAzimuthCorrection(),
			(m, v) -> m.setBackAzimuthCorrection(v)
		);
		

		/**
		 * correction for backsight inclinations
		 */
		public static final DefaultProperty<SurveyTrip, String> backInclinationCorrection = create(
			"backInclinationCorrection", String.class,
			r -> r.getBackInclinationCorrection(),
			(m, v) -> m.setBackInclinationCorrection(v)
		);
		
	}
	
	static class SurveyTripData implements Cloneable {
		static final SurveyTripData initial = new SurveyTripData();
		
		
		/**
		 * cave name.
		 */
		String cave;
	
		/**
		 * trip name.
		 */
		String name;
	
		/**
		 * trip date.
		 */
		String date;
	
		/**
		 * survey notes file path.
		 */
		String surveyNotes;
	
		/**
		 * surveyor names.
		 */
		List<String> surveyors;
	
		/**
		 * default length unit.
		 */
		Unit<Length> distanceUnit = Length.meters;
	
		/**
		 * default angle unit.
		 */
		Unit<Angle> angleUnit = Angle.degrees;
	
		/**
		 * default frontsight azimuth unit.
		 */
		Unit<Angle> overrideFrontAzimuthUnit;
	
		/**
		 * default backsight azimuth unit.
		 */
		Unit<Angle> overrideBackAzimuthUnit;
	
		/**
		 * default frontsight inclination unit.
		 */
		Unit<Angle> overrideFrontInclinationUnit;
	
		/**
		 * default backsight inclination unit.
		 */
		Unit<Angle> overrideBackInclinationUnit;
	
		/**
		 * whether backsight azimuths are corrected.
		 */
		boolean backAzimuthsCorrected;
	
		/**
		 * whether backsight inclinations are corrected.
		 */
		boolean backInclinationsCorrected;
	
		/**
		 * magnetic declination.
		 */
		String declination;
	
		/**
		 * correction for shot distances.
		 */
		String distanceCorrection;
	
		/**
		 * correction for frontsight azimuths.
		 */
		String frontAzimuthCorrection;
	
		/**
		 * correction for frontsight inclinations.
		 */
		String frontInclinationCorrection;
	
		/**
		 * correction for backsight azimuths.
		 */
		String backAzimuthCorrection;
	
		/**
		 * correction for backsight inclinations.
		 */
		String backInclinationCorrection;
	
		@Override
		public SurveyTripData clone() {
			try {
				return (SurveyTripData) super.clone(); 
			} catch (Exception e) {
				// should not happen
				throw new RuntimeException(e);
			} 
		}
		
		
		@Override
		public int hashCode() {
			int prime = 31;
			int result = 0;
			result = prime * result + Objects.hashCode(cave);
			result = prime * result + Objects.hashCode(name);
			result = prime * result + Objects.hashCode(date);
			result = prime * result + Objects.hashCode(surveyNotes);
			result = prime * result + Objects.hashCode(surveyors);
			result = prime * result + Objects.hashCode(distanceUnit);
			result = prime * result + Objects.hashCode(angleUnit);
			result = prime * result + Objects.hashCode(overrideFrontAzimuthUnit);
			result = prime * result + Objects.hashCode(overrideBackAzimuthUnit);
			result = prime * result + Objects.hashCode(overrideFrontInclinationUnit);
			result = prime * result + Objects.hashCode(overrideBackInclinationUnit);
			result = prime * result + (backAzimuthsCorrected ? 1231 : 1237);
			result = prime * result + (backInclinationsCorrected ? 1231 : 1237);
			result = prime * result + Objects.hashCode(declination);
			result = prime * result + Objects.hashCode(distanceCorrection);
			result = prime * result + Objects.hashCode(frontAzimuthCorrection);
			result = prime * result + Objects.hashCode(frontInclinationCorrection);
			result = prime * result + Objects.hashCode(backAzimuthCorrection);
			result = prime * result + Objects.hashCode(backInclinationCorrection);
			return result;
		}
	
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SurveyTripData other = (SurveyTripData) obj;
			if (!Objects.equals(cave, other.cave)) return false;
			if (!Objects.equals(name, other.name)) return false;
			if (!Objects.equals(date, other.date)) return false;
			if (!Objects.equals(surveyNotes, other.surveyNotes)) return false;
			if (!Objects.equals(surveyors, other.surveyors)) return false;
			if (!Objects.equals(distanceUnit, other.distanceUnit)) return false;
			if (!Objects.equals(angleUnit, other.angleUnit)) return false;
			if (!Objects.equals(overrideFrontAzimuthUnit, other.overrideFrontAzimuthUnit)) return false;
			if (!Objects.equals(overrideBackAzimuthUnit, other.overrideBackAzimuthUnit)) return false;
			if (!Objects.equals(overrideFrontInclinationUnit, other.overrideFrontInclinationUnit)) return false;
			if (!Objects.equals(overrideBackInclinationUnit, other.overrideBackInclinationUnit)) return false;
			if (backAzimuthsCorrected != other.backAzimuthsCorrected) return false;
			if (backInclinationsCorrected != other.backInclinationsCorrected) return false;
			if (!Objects.equals(declination, other.declination)) return false;
			if (!Objects.equals(distanceCorrection, other.distanceCorrection)) return false;
			if (!Objects.equals(frontAzimuthCorrection, other.frontAzimuthCorrection)) return false;
			if (!Objects.equals(frontInclinationCorrection, other.frontInclinationCorrection)) return false;
			if (!Objects.equals(backAzimuthCorrection, other.backAzimuthCorrection)) return false;
			if (!Objects.equals(backInclinationCorrection, other.backInclinationCorrection)) return false;
			return true;
		}
	
	}
	
	public static class MutableSurveyTrip extends SurveyTrip {
		private volatile boolean frozen = true;
		
		MutableSurveyTrip(SurveyTripData data) {
			super(data);
		}
		
		MutableSurveyTrip() {
			this(SurveyTripData.initial);
		}
		
		public void detach() {
			if (frozen) {
				data = data.clone();
				frozen = false;
			}
		}
		
		public SurveyTrip create() {
			frozen = true;
			return new SurveyTrip(data);
		} 
		
		public MutableSurveyTrip withMutations(Consumer<MutableSurveyTrip> mutator) {
			mutator.accept(this);
			return this;
		}
		
		
		/**
		 * Sets cave name.
		 *
		 * @param cave - the new value for cave name
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setCave(String cave) {
			if (data.cave == cave) return this;
			detach();
			data.cave = cave;
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
			if (data.name == name) return this;
			detach();
			data.name = name;
			return this;
		}
		
		/**
		 * Sets trip date.
		 *
		 * @param date - the new value for trip date
		 * 
		 * @return this {@code MutableSurveyTrip}.
		 */
		public MutableSurveyTrip setDate(String date) {
			if (data.date == date) return this;
			detach();
			data.date = date;
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
			if (data.surveyNotes == surveyNotes) return this;
			detach();
			data.surveyNotes = surveyNotes;
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
			if (data.surveyors == surveyors) return this;
			detach();
			data.surveyors = surveyors;
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
			if (data.distanceUnit == distanceUnit) return this;
			detach();
			data.distanceUnit = distanceUnit;
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
			if (data.angleUnit == angleUnit) return this;
			detach();
			data.angleUnit = angleUnit;
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
			if (data.overrideFrontAzimuthUnit == overrideFrontAzimuthUnit) return this;
			detach();
			data.overrideFrontAzimuthUnit = overrideFrontAzimuthUnit;
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
			if (data.overrideBackAzimuthUnit == overrideBackAzimuthUnit) return this;
			detach();
			data.overrideBackAzimuthUnit = overrideBackAzimuthUnit;
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
			if (data.overrideFrontInclinationUnit == overrideFrontInclinationUnit) return this;
			detach();
			data.overrideFrontInclinationUnit = overrideFrontInclinationUnit;
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
			if (data.overrideBackInclinationUnit == overrideBackInclinationUnit) return this;
			detach();
			data.overrideBackInclinationUnit = overrideBackInclinationUnit;
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
			if (data.backAzimuthsCorrected == backAzimuthsCorrected) return this;
			detach();
			data.backAzimuthsCorrected = backAzimuthsCorrected;
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
			if (data.backInclinationsCorrected == backInclinationsCorrected) return this;
			detach();
			data.backInclinationsCorrected = backInclinationsCorrected;
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
			if (data.declination == declination) return this;
			detach();
			data.declination = declination;
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
			if (data.distanceCorrection == distanceCorrection) return this;
			detach();
			data.distanceCorrection = distanceCorrection;
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
			if (data.frontAzimuthCorrection == frontAzimuthCorrection) return this;
			detach();
			data.frontAzimuthCorrection = frontAzimuthCorrection;
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
			if (data.frontInclinationCorrection == frontInclinationCorrection) return this;
			detach();
			data.frontInclinationCorrection = frontInclinationCorrection;
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
			if (data.backAzimuthCorrection == backAzimuthCorrection) return this;
			detach();
			data.backAzimuthCorrection = backAzimuthCorrection;
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
			if (data.backInclinationCorrection == backInclinationCorrection) return this;
			detach();
			data.backInclinationCorrection = backInclinationCorrection;
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
			return setCave(updater.apply(data.cave));
		}
		
		/**
		 * Updates trip name.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code name} and returns the new value for {@code name}.
		 * 
		 * @return this {@code SurveyTrip} if {@code name} is unchanged, or a copy with the updated {@code name}.
		 */
		public MutableSurveyTrip updateName(Function<String, String> updater) {
			return setName(updater.apply(data.name));
		}
		
		/**
		 * Updates trip date.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code date} and returns the new value for {@code date}.
		 * 
		 * @return this {@code SurveyTrip} if {@code date} is unchanged, or a copy with the updated {@code date}.
		 */
		public MutableSurveyTrip updateDate(Function<String, String> updater) {
			return setDate(updater.apply(data.date));
		}
		
		/**
		 * Updates survey notes file path.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code surveyNotes} and returns the new value for {@code surveyNotes}.
		 * 
		 * @return this {@code SurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the updated {@code surveyNotes}.
		 */
		public MutableSurveyTrip updateSurveyNotes(Function<String, String> updater) {
			return setSurveyNotes(updater.apply(data.surveyNotes));
		}
		
		/**
		 * Updates surveyor names.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code surveyors} and returns the new value for {@code surveyors}.
		 * 
		 * @return this {@code SurveyTrip} if {@code surveyors} is unchanged, or a copy with the updated {@code surveyors}.
		 */
		public MutableSurveyTrip updateSurveyors(Function<List<String>, List<String>> updater) {
			return setSurveyors(updater.apply(data.surveyors));
		}
		
		/**
		 * Updates default length unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code distanceUnit} and returns the new value for {@code distanceUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the updated {@code distanceUnit}.
		 */
		public MutableSurveyTrip updateDistanceUnit(Function<Unit<Length>, Unit<Length>> updater) {
			return setDistanceUnit(updater.apply(data.distanceUnit));
		}
		
		/**
		 * Updates default angle unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code angleUnit} and returns the new value for {@code angleUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code angleUnit} is unchanged, or a copy with the updated {@code angleUnit}.
		 */
		public MutableSurveyTrip updateAngleUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setAngleUnit(updater.apply(data.angleUnit));
		}
		
		/**
		 * Updates default frontsight azimuth unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code overrideFrontAzimuthUnit} and returns the new value for {@code overrideFrontAzimuthUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the updated {@code overrideFrontAzimuthUnit}.
		 */
		public MutableSurveyTrip updateOverrideFrontAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setOverrideFrontAzimuthUnit(updater.apply(data.overrideFrontAzimuthUnit));
		}
		
		/**
		 * Updates default backsight azimuth unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code overrideBackAzimuthUnit} and returns the new value for {@code overrideBackAzimuthUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the updated {@code overrideBackAzimuthUnit}.
		 */
		public MutableSurveyTrip updateOverrideBackAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setOverrideBackAzimuthUnit(updater.apply(data.overrideBackAzimuthUnit));
		}
		
		/**
		 * Updates default frontsight inclination unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code overrideFrontInclinationUnit} and returns the new value for {@code overrideFrontInclinationUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the updated {@code overrideFrontInclinationUnit}.
		 */
		public MutableSurveyTrip updateOverrideFrontInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setOverrideFrontInclinationUnit(updater.apply(data.overrideFrontInclinationUnit));
		}
		
		/**
		 * Updates default backsight inclination unit.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code overrideBackInclinationUnit} and returns the new value for {@code overrideBackInclinationUnit}.
		 * 
		 * @return this {@code SurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the updated {@code overrideBackInclinationUnit}.
		 */
		public MutableSurveyTrip updateOverrideBackInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
			return setOverrideBackInclinationUnit(updater.apply(data.overrideBackInclinationUnit));
		}
		
		/**
		 * Updates whether backsight azimuths are corrected.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code backAzimuthsCorrected} and returns the new value for {@code backAzimuthsCorrected}.
		 * 
		 * @return this {@code SurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the updated {@code backAzimuthsCorrected}.
		 */
		public MutableSurveyTrip updateBackAzimuthsCorrected(Function<Boolean, Boolean> updater) {
			return setBackAzimuthsCorrected(updater.apply(data.backAzimuthsCorrected));
		}
		
		/**
		 * Updates whether backsight inclinations are corrected.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code backInclinationsCorrected} and returns the new value for {@code backInclinationsCorrected}.
		 * 
		 * @return this {@code SurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the updated {@code backInclinationsCorrected}.
		 */
		public MutableSurveyTrip updateBackInclinationsCorrected(Function<Boolean, Boolean> updater) {
			return setBackInclinationsCorrected(updater.apply(data.backInclinationsCorrected));
		}
		
		/**
		 * Updates magnetic declination.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code declination} and returns the new value for {@code declination}.
		 * 
		 * @return this {@code SurveyTrip} if {@code declination} is unchanged, or a copy with the updated {@code declination}.
		 */
		public MutableSurveyTrip updateDeclination(Function<String, String> updater) {
			return setDeclination(updater.apply(data.declination));
		}
		
		/**
		 * Updates correction for shot distances.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code distanceCorrection} and returns the new value for {@code distanceCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the updated {@code distanceCorrection}.
		 */
		public MutableSurveyTrip updateDistanceCorrection(Function<String, String> updater) {
			return setDistanceCorrection(updater.apply(data.distanceCorrection));
		}
		
		/**
		 * Updates correction for frontsight azimuths.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code frontAzimuthCorrection} and returns the new value for {@code frontAzimuthCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the updated {@code frontAzimuthCorrection}.
		 */
		public MutableSurveyTrip updateFrontAzimuthCorrection(Function<String, String> updater) {
			return setFrontAzimuthCorrection(updater.apply(data.frontAzimuthCorrection));
		}
		
		/**
		 * Updates correction for frontsight inclinations.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code frontInclinationCorrection} and returns the new value for {@code frontInclinationCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the updated {@code frontInclinationCorrection}.
		 */
		public MutableSurveyTrip updateFrontInclinationCorrection(Function<String, String> updater) {
			return setFrontInclinationCorrection(updater.apply(data.frontInclinationCorrection));
		}
		
		/**
		 * Updates correction for backsight azimuths.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code backAzimuthCorrection} and returns the new value for {@code backAzimuthCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the updated {@code backAzimuthCorrection}.
		 */
		public MutableSurveyTrip updateBackAzimuthCorrection(Function<String, String> updater) {
			return setBackAzimuthCorrection(updater.apply(data.backAzimuthCorrection));
		}
		
		/**
		 * Updates correction for backsight inclinations.
		 *
		 * @param updater - {@code Function} that takes the current value of {@code backInclinationCorrection} and returns the new value for {@code backInclinationCorrection}.
		 * 
		 * @return this {@code SurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the updated {@code backInclinationCorrection}.
		 */
		public MutableSurveyTrip updateBackInclinationCorrection(Function<String, String> updater) {
			return setBackInclinationCorrection(updater.apply(data.backInclinationCorrection));
		}
		
		
	}
	
	volatile SurveyTripData data;
	
	SurveyTrip(SurveyTripData data) {
		this.data = data;
	}
	
	public SurveyTrip() {
		this(SurveyTripData.initial);
	}
	
	/**
	 * @param initializer a {@link Consumer} that initializes a {@code SurveyTrip}.
	 *
	 * @return a new {@code SurveyTrip} with values initialized by {@code initializer}.
	 */
	public static MutableSurveyTrip builder() {
		return new MutableSurveyTrip(); 
	}
	
	/**
	 * @param mutator a {@link Consumer} that applies mutations to this {@code SurveyTrip}.
	 *
	 * @return a copy of this {@code SurveyTrip} with the given mutations applied.
	 */
	public SurveyTrip withMutations(Consumer<MutableSurveyTrip> mutator) {
		MutableSurveyTrip mutable = new MutableSurveyTrip(data);
		mutator.accept(mutable);
		return data == mutable.data ? this : new SurveyTrip(mutable.data);
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
	public String getDate() {
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
		if (data.cave == cave) return this;
		return new MutableSurveyTrip(data).setCave(cave).create();
	}
	
	/**
	 * Sets trip name.
	 *
	 * @param name - the new value for trip name
	 * 
	 * @return this {@code SurveyTrip} if {@code name} is unchanged, or a copy with the new {@code name}.
	 */
	public SurveyTrip setName(String name) {
		if (data.name == name) return this;
		return new MutableSurveyTrip(data).setName(name).create();
	}
	
	/**
	 * Sets trip date.
	 *
	 * @param date - the new value for trip date
	 * 
	 * @return this {@code SurveyTrip} if {@code date} is unchanged, or a copy with the new {@code date}.
	 */
	public SurveyTrip setDate(String date) {
		if (data.date == date) return this;
		return new MutableSurveyTrip(data).setDate(date).create();
	}
	
	/**
	 * Sets survey notes file path.
	 *
	 * @param surveyNotes - the new value for survey notes file path
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the new {@code surveyNotes}.
	 */
	public SurveyTrip setSurveyNotes(String surveyNotes) {
		if (data.surveyNotes == surveyNotes) return this;
		return new MutableSurveyTrip(data).setSurveyNotes(surveyNotes).create();
	}
	
	/**
	 * Sets surveyor names.
	 *
	 * @param surveyors - the new value for surveyor names
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyors} is unchanged, or a copy with the new {@code surveyors}.
	 */
	public SurveyTrip setSurveyors(List<String> surveyors) {
		if (data.surveyors == surveyors) return this;
		return new MutableSurveyTrip(data).setSurveyors(surveyors).create();
	}
	
	/**
	 * Sets default length unit.
	 *
	 * @param distanceUnit - the new value for default length unit
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the new {@code distanceUnit}.
	 */
	public SurveyTrip setDistanceUnit(Unit<Length> distanceUnit) {
		if (data.distanceUnit == distanceUnit) return this;
		return new MutableSurveyTrip(data).setDistanceUnit(distanceUnit).create();
	}
	
	/**
	 * Sets default angle unit.
	 *
	 * @param angleUnit - the new value for default angle unit
	 * 
	 * @return this {@code SurveyTrip} if {@code angleUnit} is unchanged, or a copy with the new {@code angleUnit}.
	 */
	public SurveyTrip setAngleUnit(Unit<Angle> angleUnit) {
		if (data.angleUnit == angleUnit) return this;
		return new MutableSurveyTrip(data).setAngleUnit(angleUnit).create();
	}
	
	/**
	 * Sets default frontsight azimuth unit.
	 *
	 * @param overrideFrontAzimuthUnit - the new value for default frontsight azimuth unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the new {@code overrideFrontAzimuthUnit}.
	 */
	public SurveyTrip setOverrideFrontAzimuthUnit(Unit<Angle> overrideFrontAzimuthUnit) {
		if (data.overrideFrontAzimuthUnit == overrideFrontAzimuthUnit) return this;
		return new MutableSurveyTrip(data).setOverrideFrontAzimuthUnit(overrideFrontAzimuthUnit).create();
	}
	
	/**
	 * Sets default backsight azimuth unit.
	 *
	 * @param overrideBackAzimuthUnit - the new value for default backsight azimuth unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the new {@code overrideBackAzimuthUnit}.
	 */
	public SurveyTrip setOverrideBackAzimuthUnit(Unit<Angle> overrideBackAzimuthUnit) {
		if (data.overrideBackAzimuthUnit == overrideBackAzimuthUnit) return this;
		return new MutableSurveyTrip(data).setOverrideBackAzimuthUnit(overrideBackAzimuthUnit).create();
	}
	
	/**
	 * Sets default frontsight inclination unit.
	 *
	 * @param overrideFrontInclinationUnit - the new value for default frontsight inclination unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the new {@code overrideFrontInclinationUnit}.
	 */
	public SurveyTrip setOverrideFrontInclinationUnit(Unit<Angle> overrideFrontInclinationUnit) {
		if (data.overrideFrontInclinationUnit == overrideFrontInclinationUnit) return this;
		return new MutableSurveyTrip(data).setOverrideFrontInclinationUnit(overrideFrontInclinationUnit).create();
	}
	
	/**
	 * Sets default backsight inclination unit.
	 *
	 * @param overrideBackInclinationUnit - the new value for default backsight inclination unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the new {@code overrideBackInclinationUnit}.
	 */
	public SurveyTrip setOverrideBackInclinationUnit(Unit<Angle> overrideBackInclinationUnit) {
		if (data.overrideBackInclinationUnit == overrideBackInclinationUnit) return this;
		return new MutableSurveyTrip(data).setOverrideBackInclinationUnit(overrideBackInclinationUnit).create();
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
		return new MutableSurveyTrip(data).setBackAzimuthsCorrected(backAzimuthsCorrected).create();
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
		return new MutableSurveyTrip(data).setBackInclinationsCorrected(backInclinationsCorrected).create();
	}
	
	/**
	 * Sets magnetic declination.
	 *
	 * @param declination - the new value for magnetic declination
	 * 
	 * @return this {@code SurveyTrip} if {@code declination} is unchanged, or a copy with the new {@code declination}.
	 */
	public SurveyTrip setDeclination(String declination) {
		if (data.declination == declination) return this;
		return new MutableSurveyTrip(data).setDeclination(declination).create();
	}
	
	/**
	 * Sets correction for shot distances.
	 *
	 * @param distanceCorrection - the new value for correction for shot distances
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the new {@code distanceCorrection}.
	 */
	public SurveyTrip setDistanceCorrection(String distanceCorrection) {
		if (data.distanceCorrection == distanceCorrection) return this;
		return new MutableSurveyTrip(data).setDistanceCorrection(distanceCorrection).create();
	}
	
	/**
	 * Sets correction for frontsight azimuths.
	 *
	 * @param frontAzimuthCorrection - the new value for correction for frontsight azimuths
	 * 
	 * @return this {@code SurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the new {@code frontAzimuthCorrection}.
	 */
	public SurveyTrip setFrontAzimuthCorrection(String frontAzimuthCorrection) {
		if (data.frontAzimuthCorrection == frontAzimuthCorrection) return this;
		return new MutableSurveyTrip(data).setFrontAzimuthCorrection(frontAzimuthCorrection).create();
	}
	
	/**
	 * Sets correction for frontsight inclinations.
	 *
	 * @param frontInclinationCorrection - the new value for correction for frontsight inclinations
	 * 
	 * @return this {@code SurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the new {@code frontInclinationCorrection}.
	 */
	public SurveyTrip setFrontInclinationCorrection(String frontInclinationCorrection) {
		if (data.frontInclinationCorrection == frontInclinationCorrection) return this;
		return new MutableSurveyTrip(data).setFrontInclinationCorrection(frontInclinationCorrection).create();
	}
	
	/**
	 * Sets correction for backsight azimuths.
	 *
	 * @param backAzimuthCorrection - the new value for correction for backsight azimuths
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the new {@code backAzimuthCorrection}.
	 */
	public SurveyTrip setBackAzimuthCorrection(String backAzimuthCorrection) {
		if (data.backAzimuthCorrection == backAzimuthCorrection) return this;
		return new MutableSurveyTrip(data).setBackAzimuthCorrection(backAzimuthCorrection).create();
	}
	
	/**
	 * Sets correction for backsight inclinations.
	 *
	 * @param backInclinationCorrection - the new value for correction for backsight inclinations
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the new {@code backInclinationCorrection}.
	 */
	public SurveyTrip setBackInclinationCorrection(String backInclinationCorrection) {
		if (data.backInclinationCorrection == backInclinationCorrection) return this;
		return new MutableSurveyTrip(data).setBackInclinationCorrection(backInclinationCorrection).create();
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
	public SurveyTrip updateDate(Function<String, String> updater) {
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
	
	@Override
	public int hashCode() {
		return data.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof SurveyTrip)) return false;
		return data.equals(((SurveyTrip) obj).data);
	}
	
	public Unit<Angle> getFrontAzimuthUnit() {
		return or(data.overrideFrontAzimuthUnit, data.angleUnit);
	}

	public Unit<Angle> getBackAzimuthUnit() {
		return or(data.overrideBackAzimuthUnit, data.angleUnit);
	}

	public Unit<Angle> getFrontInclinationUnit() {
		return or(data.overrideFrontInclinationUnit, data.angleUnit);
	}

	public Unit<Angle> getBackInclinationUnit() {
		return or(data.overrideBackInclinationUnit, data.angleUnit);
	}
	
}
