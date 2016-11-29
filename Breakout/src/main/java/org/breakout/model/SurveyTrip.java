/**
 * Generated from {@code SurveyTrip.record.js} by java-record-generator on 11/29/2016, 1:24:28 PM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */
 
package org.breakout.model;

import java.util.List;
import org.andork.unit.Unit;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import static org.andork.util.JavaScript.or;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Objects;
import com.github.krukow.clj_lang.PersistentHashMap;
import com.github.krukow.clj_ds.TransientMap;
import org.andork.model.DefaultProperty;
import java.util.function.BiConsumer;

/**
 *
 */
public final class SurveyTrip {
	
	/**
	 * Key for cave name.
	 */
	public static final String cave = "cave";
	
	/**
	 * Key for trip name.
	 */
	public static final String name = "name";
	
	/**
	 * Key for trip date.
	 */
	public static final String date = "date";
	
	/**
	 * Key for survey notes file path.
	 */
	public static final String surveyNotes = "surveyNotes";
	
	/**
	 * Key for surveyor names.
	 */
	public static final String surveyors = "surveyors";
	
	/**
	 * Key for default length unit.
	 */
	public static final String distanceUnit = "distanceUnit";
	
	/**
	 * Key for default angle unit.
	 */
	public static final String angleUnit = "angleUnit";
	
	/**
	 * Key for default frontsight azimuth unit.
	 */
	public static final String overrideFrontAzimuthUnit = "overrideFrontAzimuthUnit";
	
	/**
	 * Key for default backsight azimuth unit.
	 */
	public static final String overrideBackAzimuthUnit = "overrideBackAzimuthUnit";
	
	/**
	 * Key for default frontsight inclination unit.
	 */
	public static final String overrideFrontInclinationUnit = "overrideFrontInclinationUnit";
	
	/**
	 * Key for default backsight inclination unit.
	 */
	public static final String overrideBackInclinationUnit = "overrideBackInclinationUnit";
	
	/**
	 * Key for whether backsight azimuths are corrected.
	 */
	public static final String backAzimuthsCorrected = "backAzimuthsCorrected";
	
	/**
	 * Key for whether backsight inclinations are corrected.
	 */
	public static final String backInclinationsCorrected = "backInclinationsCorrected";
	
	/**
	 * Key for magnetic declination.
	 */
	public static final String declination = "declination";
	
	/**
	 * Key for correction for shot distances.
	 */
	public static final String distanceCorrection = "distanceCorrection";
	
	/**
	 * Key for correction for frontsight azimuths.
	 */
	public static final String frontAzimuthCorrection = "frontAzimuthCorrection";
	
	/**
	 * Key for correction for frontsight inclinations.
	 */
	public static final String frontInclinationCorrection = "frontInclinationCorrection";
	
	/**
	 * Key for correction for backsight azimuths.
	 */
	public static final String backAzimuthCorrection = "backAzimuthCorrection";
	
	/**
	 * Key for correction for backsight inclinations.
	 */
	public static final String backInclinationCorrection = "backInclinationCorrection";
	
	
	static final PersistentHashMap<String, Object> initialData;
	static {
		@SuppressWarnings("unchecked")
		TransientMap<String, Object> init = PersistentHashMap.emptyMap().asTransient();
		init.plus(distanceUnit, Length.meters);
		init.plus(angleUnit, Angle.degrees);
		initialData = (PersistentHashMap<String, Object>) init.persist();
	}
	
	
	public static final class Properties {
		public static <V> DefaultProperty<SurveyTrip, V> create(
				String name, Class<? super V> valueClass,
				Function<? super SurveyTrip, ? extends V> getter, 
				BiConsumer<MutableSurveyTrip, ? super V> setter) {
			return new DefaultProperty<SurveyTrip, V>(
				name, valueClass, getter, (m, v) -> {
					return m.withMutations(m2 -> setter.accept(m2, v));
				}
			);
		}
		
		
		/**
		 * cave name
		 */
		public static final DefaultProperty<SurveyTrip, String> cave = create(
			"cave", String.class,
			r -> r.get(SurveyTrip.cave),
			(m, v) -> m.set(SurveyTrip.cave, v)
		);
		

		/**
		 * trip name
		 */
		public static final DefaultProperty<SurveyTrip, String> name = create(
			"name", String.class,
			r -> r.get(SurveyTrip.name),
			(m, v) -> m.set(SurveyTrip.name, v)
		);
		

		/**
		 * trip date
		 */
		public static final DefaultProperty<SurveyTrip, String> date = create(
			"date", String.class,
			r -> r.get(SurveyTrip.date),
			(m, v) -> m.set(SurveyTrip.date, v)
		);
		

		/**
		 * survey notes file path
		 */
		public static final DefaultProperty<SurveyTrip, String> surveyNotes = create(
			"surveyNotes", String.class,
			r -> r.get(SurveyTrip.surveyNotes),
			(m, v) -> m.set(SurveyTrip.surveyNotes, v)
		);
		

		/**
		 * surveyor names
		 */
		public static final DefaultProperty<SurveyTrip, List<String>> surveyors = create(
			"surveyors", List.class,
			r -> r.get(SurveyTrip.surveyors),
			(m, v) -> m.set(SurveyTrip.surveyors, v)
		);
		

		/**
		 * default length unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Length>> distanceUnit = create(
			"distanceUnit", Unit.class,
			r -> r.get(SurveyTrip.distanceUnit),
			(m, v) -> m.set(SurveyTrip.distanceUnit, v)
		);
		

		/**
		 * default angle unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> angleUnit = create(
			"angleUnit", Unit.class,
			r -> r.get(SurveyTrip.angleUnit),
			(m, v) -> m.set(SurveyTrip.angleUnit, v)
		);
		

		/**
		 * default frontsight azimuth unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideFrontAzimuthUnit = create(
			"overrideFrontAzimuthUnit", Unit.class,
			r -> r.get(SurveyTrip.overrideFrontAzimuthUnit),
			(m, v) -> m.set(SurveyTrip.overrideFrontAzimuthUnit, v)
		);
		

		/**
		 * default backsight azimuth unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideBackAzimuthUnit = create(
			"overrideBackAzimuthUnit", Unit.class,
			r -> r.get(SurveyTrip.overrideBackAzimuthUnit),
			(m, v) -> m.set(SurveyTrip.overrideBackAzimuthUnit, v)
		);
		

		/**
		 * default frontsight inclination unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideFrontInclinationUnit = create(
			"overrideFrontInclinationUnit", Unit.class,
			r -> r.get(SurveyTrip.overrideFrontInclinationUnit),
			(m, v) -> m.set(SurveyTrip.overrideFrontInclinationUnit, v)
		);
		

		/**
		 * default backsight inclination unit
		 */
		public static final DefaultProperty<SurveyTrip, Unit<Angle>> overrideBackInclinationUnit = create(
			"overrideBackInclinationUnit", Unit.class,
			r -> r.get(SurveyTrip.overrideBackInclinationUnit),
			(m, v) -> m.set(SurveyTrip.overrideBackInclinationUnit, v)
		);
		

		/**
		 * whether backsight azimuths are corrected
		 */
		public static final DefaultProperty<SurveyTrip, Boolean> backAzimuthsCorrected = create(
			"backAzimuthsCorrected", Boolean.class,
			r -> r.get(SurveyTrip.backAzimuthsCorrected),
			(m, v) -> m.set(SurveyTrip.backAzimuthsCorrected, v)
		);
		

		/**
		 * whether backsight inclinations are corrected
		 */
		public static final DefaultProperty<SurveyTrip, Boolean> backInclinationsCorrected = create(
			"backInclinationsCorrected", Boolean.class,
			r -> r.get(SurveyTrip.backInclinationsCorrected),
			(m, v) -> m.set(SurveyTrip.backInclinationsCorrected, v)
		);
		

		/**
		 * magnetic declination
		 */
		public static final DefaultProperty<SurveyTrip, String> declination = create(
			"declination", String.class,
			r -> r.get(SurveyTrip.declination),
			(m, v) -> m.set(SurveyTrip.declination, v)
		);
		

		/**
		 * correction for shot distances
		 */
		public static final DefaultProperty<SurveyTrip, String> distanceCorrection = create(
			"distanceCorrection", String.class,
			r -> r.get(SurveyTrip.distanceCorrection),
			(m, v) -> m.set(SurveyTrip.distanceCorrection, v)
		);
		

		/**
		 * correction for frontsight azimuths
		 */
		public static final DefaultProperty<SurveyTrip, String> frontAzimuthCorrection = create(
			"frontAzimuthCorrection", String.class,
			r -> r.get(SurveyTrip.frontAzimuthCorrection),
			(m, v) -> m.set(SurveyTrip.frontAzimuthCorrection, v)
		);
		

		/**
		 * correction for frontsight inclinations
		 */
		public static final DefaultProperty<SurveyTrip, String> frontInclinationCorrection = create(
			"frontInclinationCorrection", String.class,
			r -> r.get(SurveyTrip.frontInclinationCorrection),
			(m, v) -> m.set(SurveyTrip.frontInclinationCorrection, v)
		);
		

		/**
		 * correction for backsight azimuths
		 */
		public static final DefaultProperty<SurveyTrip, String> backAzimuthCorrection = create(
			"backAzimuthCorrection", String.class,
			r -> r.get(SurveyTrip.backAzimuthCorrection),
			(m, v) -> m.set(SurveyTrip.backAzimuthCorrection, v)
		);
		

		/**
		 * correction for backsight inclinations
		 */
		public static final DefaultProperty<SurveyTrip, String> backInclinationCorrection = create(
			"backInclinationCorrection", String.class,
			r -> r.get(SurveyTrip.backInclinationCorrection),
			(m, v) -> m.set(SurveyTrip.backInclinationCorrection, v)
		);
		
	}
	 

	private final PersistentHashMap<String, Object> data;
	
	SurveyTrip(PersistentHashMap<String, Object> data) {
		this.data = data;
	}

	public SurveyTrip() {
		this(initialData);
	}

	public MutableSurveyTrip toMutable() {
		return new MutableSurveyTrip(data);
	}

	public SurveyTrip withMutations(Consumer<MutableSurveyTrip> mutator) {
		MutableSurveyTrip mutable = toMutable();
		mutator.accept(mutable);
		return mutable.dataEquals(data) ? this : mutable.toImmutable();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) data.get(key);
	}

	public SurveyTrip set(String key, Object newValue) {
		return withMutations(m -> m.set(key, newValue));
	}

	public <T> SurveyTrip update(String key, Function<? super T, ? extends T> updater) {
		@SuppressWarnings("unchecked")
		T oldValue = (T) data.get(key);
		T newValue = updater.apply(oldValue);
		if (Objects.equals(oldValue, newValue)) {
			return this;
		}
		return set(key, newValue);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof SurveyTrip) {
			return ((SurveyTrip) obj).data.equals(data);
		}
		if (obj instanceof MutableSurveyTrip) {
			return ((MutableSurveyTrip) obj).persist().equals(data);
		}
		return false;
	}
	
	
	/**
	 * @return cave name.
	 */
	public String getCave() {
		return get(cave);
	}
	
	/**
	 * @return trip name.
	 */
	public String getName() {
		return get(name);
	}
	
	/**
	 * @return trip date.
	 */
	public String getDate() {
		return get(date);
	}
	
	/**
	 * @return survey notes file path.
	 */
	public String getSurveyNotes() {
		return get(surveyNotes);
	}
	
	/**
	 * @return surveyor names.
	 */
	public List<String> getSurveyors() {
		return get(surveyors);
	}
	
	/**
	 * @return default length unit.
	 */
	public Unit<Length> getDistanceUnit() {
		return get(distanceUnit);
	}
	
	/**
	 * @return default angle unit.
	 */
	public Unit<Angle> getAngleUnit() {
		return get(angleUnit);
	}
	
	/**
	 * @return default frontsight azimuth unit.
	 */
	public Unit<Angle> getOverrideFrontAzimuthUnit() {
		return get(overrideFrontAzimuthUnit);
	}
	
	/**
	 * @return default backsight azimuth unit.
	 */
	public Unit<Angle> getOverrideBackAzimuthUnit() {
		return get(overrideBackAzimuthUnit);
	}
	
	/**
	 * @return default frontsight inclination unit.
	 */
	public Unit<Angle> getOverrideFrontInclinationUnit() {
		return get(overrideFrontInclinationUnit);
	}
	
	/**
	 * @return default backsight inclination unit.
	 */
	public Unit<Angle> getOverrideBackInclinationUnit() {
		return get(overrideBackInclinationUnit);
	}
	
	/**
	 * @return whether backsight azimuths are corrected.
	 */
	public boolean areBackAzimuthsCorrected() {
		return get(backAzimuthsCorrected);
	}
	
	/**
	 * @return whether backsight inclinations are corrected.
	 */
	public boolean areBackInclinationsCorrected() {
		return get(backInclinationsCorrected);
	}
	
	/**
	 * @return magnetic declination.
	 */
	public String getDeclination() {
		return get(declination);
	}
	
	/**
	 * @return correction for shot distances.
	 */
	public String getDistanceCorrection() {
		return get(distanceCorrection);
	}
	
	/**
	 * @return correction for frontsight azimuths.
	 */
	public String getFrontAzimuthCorrection() {
		return get(frontAzimuthCorrection);
	}
	
	/**
	 * @return correction for frontsight inclinations.
	 */
	public String getFrontInclinationCorrection() {
		return get(frontInclinationCorrection);
	}
	
	/**
	 * @return correction for backsight azimuths.
	 */
	public String getBackAzimuthCorrection() {
		return get(backAzimuthCorrection);
	}
	
	/**
	 * @return correction for backsight inclinations.
	 */
	public String getBackInclinationCorrection() {
		return get(backInclinationCorrection);
	}
	
	
	/**
	 * Sets cave name.
	 *
	 * @param cave - the new value for cave name
	 * 
	 * @return this {@code SurveyTrip} if {@code cave} is unchanged, or a copy with the new {@code cave}.
	 */
	public SurveyTrip setCave(String cave) {
		return set(SurveyTrip.cave, cave);
	}
	
	/**
	 * Sets trip name.
	 *
	 * @param name - the new value for trip name
	 * 
	 * @return this {@code SurveyTrip} if {@code name} is unchanged, or a copy with the new {@code name}.
	 */
	public SurveyTrip setName(String name) {
		return set(SurveyTrip.name, name);
	}
	
	/**
	 * Sets trip date.
	 *
	 * @param date - the new value for trip date
	 * 
	 * @return this {@code SurveyTrip} if {@code date} is unchanged, or a copy with the new {@code date}.
	 */
	public SurveyTrip setDate(String date) {
		return set(SurveyTrip.date, date);
	}
	
	/**
	 * Sets survey notes file path.
	 *
	 * @param surveyNotes - the new value for survey notes file path
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the new {@code surveyNotes}.
	 */
	public SurveyTrip setSurveyNotes(String surveyNotes) {
		return set(SurveyTrip.surveyNotes, surveyNotes);
	}
	
	/**
	 * Sets surveyor names.
	 *
	 * @param surveyors - the new value for surveyor names
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyors} is unchanged, or a copy with the new {@code surveyors}.
	 */
	public SurveyTrip setSurveyors(List<String> surveyors) {
		return set(SurveyTrip.surveyors, surveyors);
	}
	
	/**
	 * Sets default length unit.
	 *
	 * @param distanceUnit - the new value for default length unit
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the new {@code distanceUnit}.
	 */
	public SurveyTrip setDistanceUnit(Unit<Length> distanceUnit) {
		return set(SurveyTrip.distanceUnit, distanceUnit);
	}
	
	/**
	 * Sets default angle unit.
	 *
	 * @param angleUnit - the new value for default angle unit
	 * 
	 * @return this {@code SurveyTrip} if {@code angleUnit} is unchanged, or a copy with the new {@code angleUnit}.
	 */
	public SurveyTrip setAngleUnit(Unit<Angle> angleUnit) {
		return set(SurveyTrip.angleUnit, angleUnit);
	}
	
	/**
	 * Sets default frontsight azimuth unit.
	 *
	 * @param overrideFrontAzimuthUnit - the new value for default frontsight azimuth unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the new {@code overrideFrontAzimuthUnit}.
	 */
	public SurveyTrip setOverrideFrontAzimuthUnit(Unit<Angle> overrideFrontAzimuthUnit) {
		return set(SurveyTrip.overrideFrontAzimuthUnit, overrideFrontAzimuthUnit);
	}
	
	/**
	 * Sets default backsight azimuth unit.
	 *
	 * @param overrideBackAzimuthUnit - the new value for default backsight azimuth unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the new {@code overrideBackAzimuthUnit}.
	 */
	public SurveyTrip setOverrideBackAzimuthUnit(Unit<Angle> overrideBackAzimuthUnit) {
		return set(SurveyTrip.overrideBackAzimuthUnit, overrideBackAzimuthUnit);
	}
	
	/**
	 * Sets default frontsight inclination unit.
	 *
	 * @param overrideFrontInclinationUnit - the new value for default frontsight inclination unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the new {@code overrideFrontInclinationUnit}.
	 */
	public SurveyTrip setOverrideFrontInclinationUnit(Unit<Angle> overrideFrontInclinationUnit) {
		return set(SurveyTrip.overrideFrontInclinationUnit, overrideFrontInclinationUnit);
	}
	
	/**
	 * Sets default backsight inclination unit.
	 *
	 * @param overrideBackInclinationUnit - the new value for default backsight inclination unit
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the new {@code overrideBackInclinationUnit}.
	 */
	public SurveyTrip setOverrideBackInclinationUnit(Unit<Angle> overrideBackInclinationUnit) {
		return set(SurveyTrip.overrideBackInclinationUnit, overrideBackInclinationUnit);
	}
	
	/**
	 * Sets whether backsight azimuths are corrected.
	 *
	 * @param backAzimuthsCorrected - the new value for whether backsight azimuths are corrected
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the new {@code backAzimuthsCorrected}.
	 */
	public SurveyTrip setBackAzimuthsCorrected(boolean backAzimuthsCorrected) {
		return set(SurveyTrip.backAzimuthsCorrected, backAzimuthsCorrected);
	}
	
	/**
	 * Sets whether backsight inclinations are corrected.
	 *
	 * @param backInclinationsCorrected - the new value for whether backsight inclinations are corrected
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the new {@code backInclinationsCorrected}.
	 */
	public SurveyTrip setBackInclinationsCorrected(boolean backInclinationsCorrected) {
		return set(SurveyTrip.backInclinationsCorrected, backInclinationsCorrected);
	}
	
	/**
	 * Sets magnetic declination.
	 *
	 * @param declination - the new value for magnetic declination
	 * 
	 * @return this {@code SurveyTrip} if {@code declination} is unchanged, or a copy with the new {@code declination}.
	 */
	public SurveyTrip setDeclination(String declination) {
		return set(SurveyTrip.declination, declination);
	}
	
	/**
	 * Sets correction for shot distances.
	 *
	 * @param distanceCorrection - the new value for correction for shot distances
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the new {@code distanceCorrection}.
	 */
	public SurveyTrip setDistanceCorrection(String distanceCorrection) {
		return set(SurveyTrip.distanceCorrection, distanceCorrection);
	}
	
	/**
	 * Sets correction for frontsight azimuths.
	 *
	 * @param frontAzimuthCorrection - the new value for correction for frontsight azimuths
	 * 
	 * @return this {@code SurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the new {@code frontAzimuthCorrection}.
	 */
	public SurveyTrip setFrontAzimuthCorrection(String frontAzimuthCorrection) {
		return set(SurveyTrip.frontAzimuthCorrection, frontAzimuthCorrection);
	}
	
	/**
	 * Sets correction for frontsight inclinations.
	 *
	 * @param frontInclinationCorrection - the new value for correction for frontsight inclinations
	 * 
	 * @return this {@code SurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the new {@code frontInclinationCorrection}.
	 */
	public SurveyTrip setFrontInclinationCorrection(String frontInclinationCorrection) {
		return set(SurveyTrip.frontInclinationCorrection, frontInclinationCorrection);
	}
	
	/**
	 * Sets correction for backsight azimuths.
	 *
	 * @param backAzimuthCorrection - the new value for correction for backsight azimuths
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the new {@code backAzimuthCorrection}.
	 */
	public SurveyTrip setBackAzimuthCorrection(String backAzimuthCorrection) {
		return set(SurveyTrip.backAzimuthCorrection, backAzimuthCorrection);
	}
	
	/**
	 * Sets correction for backsight inclinations.
	 *
	 * @param backInclinationCorrection - the new value for correction for backsight inclinations
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the new {@code backInclinationCorrection}.
	 */
	public SurveyTrip setBackInclinationCorrection(String backInclinationCorrection) {
		return set(SurveyTrip.backInclinationCorrection, backInclinationCorrection);
	}
	
	
	/**
	 * Updates cave name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code cave} and returns the new value for {@code cave}.
	 * 
	 * @return this {@code SurveyTrip} if {@code cave} is unchanged, or a copy with the updated {@code cave}.
	 */
	public SurveyTrip updateCave(Function<String, String> updater) {
		return update(cave, updater);
	}
	
	/**
	 * Updates trip name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code name} and returns the new value for {@code name}.
	 * 
	 * @return this {@code SurveyTrip} if {@code name} is unchanged, or a copy with the updated {@code name}.
	 */
	public SurveyTrip updateName(Function<String, String> updater) {
		return update(name, updater);
	}
	
	/**
	 * Updates trip date.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code date} and returns the new value for {@code date}.
	 * 
	 * @return this {@code SurveyTrip} if {@code date} is unchanged, or a copy with the updated {@code date}.
	 */
	public SurveyTrip updateDate(Function<String, String> updater) {
		return update(date, updater);
	}
	
	/**
	 * Updates survey notes file path.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code surveyNotes} and returns the new value for {@code surveyNotes}.
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the updated {@code surveyNotes}.
	 */
	public SurveyTrip updateSurveyNotes(Function<String, String> updater) {
		return update(surveyNotes, updater);
	}
	
	/**
	 * Updates surveyor names.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code surveyors} and returns the new value for {@code surveyors}.
	 * 
	 * @return this {@code SurveyTrip} if {@code surveyors} is unchanged, or a copy with the updated {@code surveyors}.
	 */
	public SurveyTrip updateSurveyors(Function<List<String>, List<String>> updater) {
		return update(surveyors, updater);
	}
	
	/**
	 * Updates default length unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distanceUnit} and returns the new value for {@code distanceUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the updated {@code distanceUnit}.
	 */
	public SurveyTrip updateDistanceUnit(Function<Unit<Length>, Unit<Length>> updater) {
		return update(distanceUnit, updater);
	}
	
	/**
	 * Updates default angle unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code angleUnit} and returns the new value for {@code angleUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code angleUnit} is unchanged, or a copy with the updated {@code angleUnit}.
	 */
	public SurveyTrip updateAngleUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(angleUnit, updater);
	}
	
	/**
	 * Updates default frontsight azimuth unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFrontAzimuthUnit} and returns the new value for {@code overrideFrontAzimuthUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the updated {@code overrideFrontAzimuthUnit}.
	 */
	public SurveyTrip updateOverrideFrontAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(overrideFrontAzimuthUnit, updater);
	}
	
	/**
	 * Updates default backsight azimuth unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideBackAzimuthUnit} and returns the new value for {@code overrideBackAzimuthUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the updated {@code overrideBackAzimuthUnit}.
	 */
	public SurveyTrip updateOverrideBackAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(overrideBackAzimuthUnit, updater);
	}
	
	/**
	 * Updates default frontsight inclination unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFrontInclinationUnit} and returns the new value for {@code overrideFrontInclinationUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the updated {@code overrideFrontInclinationUnit}.
	 */
	public SurveyTrip updateOverrideFrontInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(overrideFrontInclinationUnit, updater);
	}
	
	/**
	 * Updates default backsight inclination unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideBackInclinationUnit} and returns the new value for {@code overrideBackInclinationUnit}.
	 * 
	 * @return this {@code SurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the updated {@code overrideBackInclinationUnit}.
	 */
	public SurveyTrip updateOverrideBackInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(overrideBackInclinationUnit, updater);
	}
	
	/**
	 * Updates whether backsight azimuths are corrected.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuthsCorrected} and returns the new value for {@code backAzimuthsCorrected}.
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the updated {@code backAzimuthsCorrected}.
	 */
	public SurveyTrip updateBackAzimuthsCorrected(Function<Boolean, Boolean> updater) {
		return update(backAzimuthsCorrected, updater);
	}
	
	/**
	 * Updates whether backsight inclinations are corrected.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclinationsCorrected} and returns the new value for {@code backInclinationsCorrected}.
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the updated {@code backInclinationsCorrected}.
	 */
	public SurveyTrip updateBackInclinationsCorrected(Function<Boolean, Boolean> updater) {
		return update(backInclinationsCorrected, updater);
	}
	
	/**
	 * Updates magnetic declination.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code declination} and returns the new value for {@code declination}.
	 * 
	 * @return this {@code SurveyTrip} if {@code declination} is unchanged, or a copy with the updated {@code declination}.
	 */
	public SurveyTrip updateDeclination(Function<String, String> updater) {
		return update(declination, updater);
	}
	
	/**
	 * Updates correction for shot distances.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distanceCorrection} and returns the new value for {@code distanceCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the updated {@code distanceCorrection}.
	 */
	public SurveyTrip updateDistanceCorrection(Function<String, String> updater) {
		return update(distanceCorrection, updater);
	}
	
	/**
	 * Updates correction for frontsight azimuths.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontAzimuthCorrection} and returns the new value for {@code frontAzimuthCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the updated {@code frontAzimuthCorrection}.
	 */
	public SurveyTrip updateFrontAzimuthCorrection(Function<String, String> updater) {
		return update(frontAzimuthCorrection, updater);
	}
	
	/**
	 * Updates correction for frontsight inclinations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontInclinationCorrection} and returns the new value for {@code frontInclinationCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the updated {@code frontInclinationCorrection}.
	 */
	public SurveyTrip updateFrontInclinationCorrection(Function<String, String> updater) {
		return update(frontInclinationCorrection, updater);
	}
	
	/**
	 * Updates correction for backsight azimuths.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuthCorrection} and returns the new value for {@code backAzimuthCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the updated {@code backAzimuthCorrection}.
	 */
	public SurveyTrip updateBackAzimuthCorrection(Function<String, String> updater) {
		return update(backAzimuthCorrection, updater);
	}
	
	/**
	 * Updates correction for backsight inclinations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclinationCorrection} and returns the new value for {@code backInclinationCorrection}.
	 * 
	 * @return this {@code SurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the updated {@code backInclinationCorrection}.
	 */
	public SurveyTrip updateBackInclinationCorrection(Function<String, String> updater) {
		return update(backInclinationCorrection, updater);
	}
	
	
	public Unit<Angle> getFrontAzimuthUnit() {
		return or(getOverrideFrontAzimuthUnit(), getAngleUnit());
	}

	public Unit<Angle> getBackAzimuthUnit() {
		return or(getOverrideBackAzimuthUnit(), getAngleUnit());
	}
	
	public Unit<Angle> getFrontInclinationUnit() {
		return or(getOverrideFrontInclinationUnit(), getAngleUnit());
	}

	public Unit<Angle> getBackInclinationUnit() {
		return or(getOverrideBackInclinationUnit(), getAngleUnit());
	}
	
}
