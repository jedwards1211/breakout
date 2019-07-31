/**
 * Generated from {@code SurveyTrip.record.js} by java-record-generator on 7/28/2019, 3:35:00 PM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */

package org.breakout.model.raw;

import java.util.List;
import org.andork.unit.Unit;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import com.github.krukow.clj_ds.TransientMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import java.util.Objects;
import java.util.function.Function;

/**
 * The mutable version of {@link SurveyTrip}.
 */
public final class MutableSurveyTrip {
	private volatile PersistentHashMap<String, Object> persisted;
	private final TransientMap<String, Object> data;

	@SuppressWarnings("unchecked")
	MutableSurveyTrip(PersistentHashMap<String, Object> data) {
		persisted = data;
		this.data = persisted.asTransient();
	}

	public MutableSurveyTrip() {
		this(SurveyTrip.initialData);
	}

	boolean dataEquals(PersistentHashMap<String, Object> prevData) {
		return persisted == prevData;
	}

	PersistentHashMap<String, Object> persist() {
		if (persisted == null) {
			persisted = (PersistentHashMap<String, Object>) data.persist();
		}
		return persisted;
	}

	public SurveyTrip toImmutable() {
		return new SurveyTrip(persist());
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) persist().get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue) {
		T result = (T) persist().get(key);
		return result != null ? result : defaultValue;
	}

	private static boolean equals(Object a, Object b) {
		if (a instanceof Number || b instanceof Number ||
			a instanceof String || b instanceof String) {
			return Objects.equals(a, b);
		}
		return a == b;
	}

	public MutableSurveyTrip set(String key, Object value) {
		if (persisted != null && equals(value, persisted.get(key))) {
			return this;
		}
		persisted = null;
		data.plus(key, value);
		return this;
	}

	public <T> MutableSurveyTrip update(String key, Function<? super T, ? extends T> updater) {
		@SuppressWarnings("unchecked")
		T oldValue = (T) persist().get(key);
		T newValue = updater.apply(oldValue);
		if (equals(oldValue, newValue)) {
			return this;
		}
		data.plus(key, newValue);
		return this;
	}

	public MutableSurveyTrip delete(String key) {
		if (persisted != null && !persisted.containsKey(key)) {
			return this;
		}
		persisted = null;
		data.minus(key);
		return this;
	}

	
	
	/**
	 * @return cave name.
	 */
	public String getCave() {
		return get(SurveyTrip.cave);
	}
	
	/**
	 * @return trip name.
	 */
	public String getName() {
		return get(SurveyTrip.name);
	}
	
	/**
	 * @return trip date.
	 */
	public String getDate() {
		return get(SurveyTrip.date);
	}
	
	/**
	 * @return survey notes file path.
	 */
	public String getSurveyNotes() {
		return get(SurveyTrip.surveyNotes);
	}
	
	/**
	 * @return surveyor names.
	 */
	public List<String> getSurveyors() {
		return get(SurveyTrip.surveyors);
	}
	
	/**
	 * @return default length unit.
	 */
	public Unit<Length> getDistanceUnit() {
		return get(SurveyTrip.distanceUnit);
	}
	
	/**
	 * @return default angle unit.
	 */
	public Unit<Angle> getAngleUnit() {
		return get(SurveyTrip.angleUnit);
	}
	
	/**
	 * @return default frontsight azimuth unit.
	 */
	public Unit<Angle> getOverrideFrontAzimuthUnit() {
		return get(SurveyTrip.overrideFrontAzimuthUnit);
	}
	
	/**
	 * @return default backsight azimuth unit.
	 */
	public Unit<Angle> getOverrideBackAzimuthUnit() {
		return get(SurveyTrip.overrideBackAzimuthUnit);
	}
	
	/**
	 * @return default frontsight inclination unit.
	 */
	public Unit<Angle> getOverrideFrontInclinationUnit() {
		return get(SurveyTrip.overrideFrontInclinationUnit);
	}
	
	/**
	 * @return default backsight inclination unit.
	 */
	public Unit<Angle> getOverrideBackInclinationUnit() {
		return get(SurveyTrip.overrideBackInclinationUnit);
	}
	
	/**
	 * @return whether backsight azimuths are corrected.
	 */
	public boolean areBackAzimuthsCorrected() {
		return get(SurveyTrip.backAzimuthsCorrected, false);
	}
	
	/**
	 * @return whether backsight inclinations are corrected.
	 */
	public boolean areBackInclinationsCorrected() {
		return get(SurveyTrip.backInclinationsCorrected, false);
	}
	
	/**
	 * @return magnetic declination.
	 */
	public String getDeclination() {
		return get(SurveyTrip.declination);
	}
	
	/**
	 * @return correction for shot distances.
	 */
	public String getDistanceCorrection() {
		return get(SurveyTrip.distanceCorrection);
	}
	
	/**
	 * @return correction for frontsight azimuths.
	 */
	public String getFrontAzimuthCorrection() {
		return get(SurveyTrip.frontAzimuthCorrection);
	}
	
	/**
	 * @return correction for frontsight inclinations.
	 */
	public String getFrontInclinationCorrection() {
		return get(SurveyTrip.frontInclinationCorrection);
	}
	
	/**
	 * @return correction for backsight azimuths.
	 */
	public String getBackAzimuthCorrection() {
		return get(SurveyTrip.backAzimuthCorrection);
	}
	
	/**
	 * @return correction for backsight inclinations.
	 */
	public String getBackInclinationCorrection() {
		return get(SurveyTrip.backInclinationCorrection);
	}
	
	/**
	 * @return the geodetic datum for fixed station locations.
	 */
	public String getDatum() {
		return get(SurveyTrip.datum);
	}
	
	/**
	 * @return the reference ellipsoid for fixed station locations.
	 */
	public String getEllipsoid() {
		return get(SurveyTrip.ellipsoid);
	}
	
	/**
	 * @return the UTM zone for fixed station locations.
	 */
	public String getUtmZone() {
		return get(SurveyTrip.utmZone);
	}
	
	
	/**
	 * Sets cave name.
	 *
	 * @param cave - the new value for cave name
	 *
	 * @return this {@code SurveyTrip} if {@code cave} is unchanged, or a copy with the new {@code cave}.
	 */
	public MutableSurveyTrip setCave(String cave) {
		return set(SurveyTrip.cave, cave);
	}
	
	/**
	 * Sets trip name.
	 *
	 * @param name - the new value for trip name
	 *
	 * @return this {@code SurveyTrip} if {@code name} is unchanged, or a copy with the new {@code name}.
	 */
	public MutableSurveyTrip setName(String name) {
		return set(SurveyTrip.name, name);
	}
	
	/**
	 * Sets trip date.
	 *
	 * @param date - the new value for trip date
	 *
	 * @return this {@code SurveyTrip} if {@code date} is unchanged, or a copy with the new {@code date}.
	 */
	public MutableSurveyTrip setDate(String date) {
		return set(SurveyTrip.date, date);
	}
	
	/**
	 * Sets survey notes file path.
	 *
	 * @param surveyNotes - the new value for survey notes file path
	 *
	 * @return this {@code SurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the new {@code surveyNotes}.
	 */
	public MutableSurveyTrip setSurveyNotes(String surveyNotes) {
		return set(SurveyTrip.surveyNotes, surveyNotes);
	}
	
	/**
	 * Sets surveyor names.
	 *
	 * @param surveyors - the new value for surveyor names
	 *
	 * @return this {@code SurveyTrip} if {@code surveyors} is unchanged, or a copy with the new {@code surveyors}.
	 */
	public MutableSurveyTrip setSurveyors(List<String> surveyors) {
		return set(SurveyTrip.surveyors, surveyors);
	}
	
	/**
	 * Sets default length unit.
	 *
	 * @param distanceUnit - the new value for default length unit
	 *
	 * @return this {@code SurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the new {@code distanceUnit}.
	 */
	public MutableSurveyTrip setDistanceUnit(Unit<Length> distanceUnit) {
		return set(SurveyTrip.distanceUnit, distanceUnit);
	}
	
	/**
	 * Sets default angle unit.
	 *
	 * @param angleUnit - the new value for default angle unit
	 *
	 * @return this {@code SurveyTrip} if {@code angleUnit} is unchanged, or a copy with the new {@code angleUnit}.
	 */
	public MutableSurveyTrip setAngleUnit(Unit<Angle> angleUnit) {
		return set(SurveyTrip.angleUnit, angleUnit);
	}
	
	/**
	 * Sets default frontsight azimuth unit.
	 *
	 * @param overrideFrontAzimuthUnit - the new value for default frontsight azimuth unit
	 *
	 * @return this {@code SurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the new {@code overrideFrontAzimuthUnit}.
	 */
	public MutableSurveyTrip setOverrideFrontAzimuthUnit(Unit<Angle> overrideFrontAzimuthUnit) {
		return set(SurveyTrip.overrideFrontAzimuthUnit, overrideFrontAzimuthUnit);
	}
	
	/**
	 * Sets default backsight azimuth unit.
	 *
	 * @param overrideBackAzimuthUnit - the new value for default backsight azimuth unit
	 *
	 * @return this {@code SurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the new {@code overrideBackAzimuthUnit}.
	 */
	public MutableSurveyTrip setOverrideBackAzimuthUnit(Unit<Angle> overrideBackAzimuthUnit) {
		return set(SurveyTrip.overrideBackAzimuthUnit, overrideBackAzimuthUnit);
	}
	
	/**
	 * Sets default frontsight inclination unit.
	 *
	 * @param overrideFrontInclinationUnit - the new value for default frontsight inclination unit
	 *
	 * @return this {@code SurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the new {@code overrideFrontInclinationUnit}.
	 */
	public MutableSurveyTrip setOverrideFrontInclinationUnit(Unit<Angle> overrideFrontInclinationUnit) {
		return set(SurveyTrip.overrideFrontInclinationUnit, overrideFrontInclinationUnit);
	}
	
	/**
	 * Sets default backsight inclination unit.
	 *
	 * @param overrideBackInclinationUnit - the new value for default backsight inclination unit
	 *
	 * @return this {@code SurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the new {@code overrideBackInclinationUnit}.
	 */
	public MutableSurveyTrip setOverrideBackInclinationUnit(Unit<Angle> overrideBackInclinationUnit) {
		return set(SurveyTrip.overrideBackInclinationUnit, overrideBackInclinationUnit);
	}
	
	/**
	 * Sets whether backsight azimuths are corrected.
	 *
	 * @param backAzimuthsCorrected - the new value for whether backsight azimuths are corrected
	 *
	 * @return this {@code SurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the new {@code backAzimuthsCorrected}.
	 */
	public MutableSurveyTrip setBackAzimuthsCorrected(boolean backAzimuthsCorrected) {
		return set(SurveyTrip.backAzimuthsCorrected, backAzimuthsCorrected);
	}
	
	/**
	 * Sets whether backsight inclinations are corrected.
	 *
	 * @param backInclinationsCorrected - the new value for whether backsight inclinations are corrected
	 *
	 * @return this {@code SurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the new {@code backInclinationsCorrected}.
	 */
	public MutableSurveyTrip setBackInclinationsCorrected(boolean backInclinationsCorrected) {
		return set(SurveyTrip.backInclinationsCorrected, backInclinationsCorrected);
	}
	
	/**
	 * Sets magnetic declination.
	 *
	 * @param declination - the new value for magnetic declination
	 *
	 * @return this {@code SurveyTrip} if {@code declination} is unchanged, or a copy with the new {@code declination}.
	 */
	public MutableSurveyTrip setDeclination(String declination) {
		return set(SurveyTrip.declination, declination);
	}
	
	/**
	 * Sets correction for shot distances.
	 *
	 * @param distanceCorrection - the new value for correction for shot distances
	 *
	 * @return this {@code SurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the new {@code distanceCorrection}.
	 */
	public MutableSurveyTrip setDistanceCorrection(String distanceCorrection) {
		return set(SurveyTrip.distanceCorrection, distanceCorrection);
	}
	
	/**
	 * Sets correction for frontsight azimuths.
	 *
	 * @param frontAzimuthCorrection - the new value for correction for frontsight azimuths
	 *
	 * @return this {@code SurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the new {@code frontAzimuthCorrection}.
	 */
	public MutableSurveyTrip setFrontAzimuthCorrection(String frontAzimuthCorrection) {
		return set(SurveyTrip.frontAzimuthCorrection, frontAzimuthCorrection);
	}
	
	/**
	 * Sets correction for frontsight inclinations.
	 *
	 * @param frontInclinationCorrection - the new value for correction for frontsight inclinations
	 *
	 * @return this {@code SurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the new {@code frontInclinationCorrection}.
	 */
	public MutableSurveyTrip setFrontInclinationCorrection(String frontInclinationCorrection) {
		return set(SurveyTrip.frontInclinationCorrection, frontInclinationCorrection);
	}
	
	/**
	 * Sets correction for backsight azimuths.
	 *
	 * @param backAzimuthCorrection - the new value for correction for backsight azimuths
	 *
	 * @return this {@code SurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the new {@code backAzimuthCorrection}.
	 */
	public MutableSurveyTrip setBackAzimuthCorrection(String backAzimuthCorrection) {
		return set(SurveyTrip.backAzimuthCorrection, backAzimuthCorrection);
	}
	
	/**
	 * Sets correction for backsight inclinations.
	 *
	 * @param backInclinationCorrection - the new value for correction for backsight inclinations
	 *
	 * @return this {@code SurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the new {@code backInclinationCorrection}.
	 */
	public MutableSurveyTrip setBackInclinationCorrection(String backInclinationCorrection) {
		return set(SurveyTrip.backInclinationCorrection, backInclinationCorrection);
	}
	
	/**
	 * Sets the geodetic datum for fixed station locations.
	 *
	 * @param datum - the new value for the geodetic datum for fixed station locations
	 *
	 * @return this {@code SurveyTrip} if {@code datum} is unchanged, or a copy with the new {@code datum}.
	 */
	public MutableSurveyTrip setDatum(String datum) {
		return set(SurveyTrip.datum, datum);
	}
	
	/**
	 * Sets the reference ellipsoid for fixed station locations.
	 *
	 * @param ellipsoid - the new value for the reference ellipsoid for fixed station locations
	 *
	 * @return this {@code SurveyTrip} if {@code ellipsoid} is unchanged, or a copy with the new {@code ellipsoid}.
	 */
	public MutableSurveyTrip setEllipsoid(String ellipsoid) {
		return set(SurveyTrip.ellipsoid, ellipsoid);
	}
	
	/**
	 * Sets the UTM zone for fixed station locations.
	 *
	 * @param utmZone - the new value for the UTM zone for fixed station locations
	 *
	 * @return this {@code SurveyTrip} if {@code utmZone} is unchanged, or a copy with the new {@code utmZone}.
	 */
	public MutableSurveyTrip setUtmZone(String utmZone) {
		return set(SurveyTrip.utmZone, utmZone);
	}
	
	
	/**
	 * Updates cave name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code cave} and returns the new value for {@code cave}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code cave} is unchanged, or a copy with the updated {@code cave}.
	 */
	public MutableSurveyTrip updateCave(Function<String, String> updater) {
		return update(SurveyTrip.cave, updater);
	}
	
	/**
	 * Updates trip name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code name} and returns the new value for {@code name}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code name} is unchanged, or a copy with the updated {@code name}.
	 */
	public MutableSurveyTrip updateName(Function<String, String> updater) {
		return update(SurveyTrip.name, updater);
	}
	
	/**
	 * Updates trip date.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code date} and returns the new value for {@code date}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code date} is unchanged, or a copy with the updated {@code date}.
	 */
	public MutableSurveyTrip updateDate(Function<String, String> updater) {
		return update(SurveyTrip.date, updater);
	}
	
	/**
	 * Updates survey notes file path.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code surveyNotes} and returns the new value for {@code surveyNotes}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the updated {@code surveyNotes}.
	 */
	public MutableSurveyTrip updateSurveyNotes(Function<String, String> updater) {
		return update(SurveyTrip.surveyNotes, updater);
	}
	
	/**
	 * Updates surveyor names.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code surveyors} and returns the new value for {@code surveyors}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code surveyors} is unchanged, or a copy with the updated {@code surveyors}.
	 */
	public MutableSurveyTrip updateSurveyors(Function<List<String>, List<String>> updater) {
		return update(SurveyTrip.surveyors, updater);
	}
	
	/**
	 * Updates default length unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distanceUnit} and returns the new value for {@code distanceUnit}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the updated {@code distanceUnit}.
	 */
	public MutableSurveyTrip updateDistanceUnit(Function<Unit<Length>, Unit<Length>> updater) {
		return update(SurveyTrip.distanceUnit, updater);
	}
	
	/**
	 * Updates default angle unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code angleUnit} and returns the new value for {@code angleUnit}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code angleUnit} is unchanged, or a copy with the updated {@code angleUnit}.
	 */
	public MutableSurveyTrip updateAngleUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(SurveyTrip.angleUnit, updater);
	}
	
	/**
	 * Updates default frontsight azimuth unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFrontAzimuthUnit} and returns the new value for {@code overrideFrontAzimuthUnit}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the updated {@code overrideFrontAzimuthUnit}.
	 */
	public MutableSurveyTrip updateOverrideFrontAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(SurveyTrip.overrideFrontAzimuthUnit, updater);
	}
	
	/**
	 * Updates default backsight azimuth unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideBackAzimuthUnit} and returns the new value for {@code overrideBackAzimuthUnit}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the updated {@code overrideBackAzimuthUnit}.
	 */
	public MutableSurveyTrip updateOverrideBackAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(SurveyTrip.overrideBackAzimuthUnit, updater);
	}
	
	/**
	 * Updates default frontsight inclination unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFrontInclinationUnit} and returns the new value for {@code overrideFrontInclinationUnit}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the updated {@code overrideFrontInclinationUnit}.
	 */
	public MutableSurveyTrip updateOverrideFrontInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(SurveyTrip.overrideFrontInclinationUnit, updater);
	}
	
	/**
	 * Updates default backsight inclination unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideBackInclinationUnit} and returns the new value for {@code overrideBackInclinationUnit}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the updated {@code overrideBackInclinationUnit}.
	 */
	public MutableSurveyTrip updateOverrideBackInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return update(SurveyTrip.overrideBackInclinationUnit, updater);
	}
	
	/**
	 * Updates whether backsight azimuths are corrected.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuthsCorrected} and returns the new value for {@code backAzimuthsCorrected}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the updated {@code backAzimuthsCorrected}.
	 */
	public MutableSurveyTrip updateBackAzimuthsCorrected(Function<Boolean, Boolean> updater) {
		return update(SurveyTrip.backAzimuthsCorrected, updater);
	}
	
	/**
	 * Updates whether backsight inclinations are corrected.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclinationsCorrected} and returns the new value for {@code backInclinationsCorrected}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the updated {@code backInclinationsCorrected}.
	 */
	public MutableSurveyTrip updateBackInclinationsCorrected(Function<Boolean, Boolean> updater) {
		return update(SurveyTrip.backInclinationsCorrected, updater);
	}
	
	/**
	 * Updates magnetic declination.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code declination} and returns the new value for {@code declination}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code declination} is unchanged, or a copy with the updated {@code declination}.
	 */
	public MutableSurveyTrip updateDeclination(Function<String, String> updater) {
		return update(SurveyTrip.declination, updater);
	}
	
	/**
	 * Updates correction for shot distances.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distanceCorrection} and returns the new value for {@code distanceCorrection}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the updated {@code distanceCorrection}.
	 */
	public MutableSurveyTrip updateDistanceCorrection(Function<String, String> updater) {
		return update(SurveyTrip.distanceCorrection, updater);
	}
	
	/**
	 * Updates correction for frontsight azimuths.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontAzimuthCorrection} and returns the new value for {@code frontAzimuthCorrection}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the updated {@code frontAzimuthCorrection}.
	 */
	public MutableSurveyTrip updateFrontAzimuthCorrection(Function<String, String> updater) {
		return update(SurveyTrip.frontAzimuthCorrection, updater);
	}
	
	/**
	 * Updates correction for frontsight inclinations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontInclinationCorrection} and returns the new value for {@code frontInclinationCorrection}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the updated {@code frontInclinationCorrection}.
	 */
	public MutableSurveyTrip updateFrontInclinationCorrection(Function<String, String> updater) {
		return update(SurveyTrip.frontInclinationCorrection, updater);
	}
	
	/**
	 * Updates correction for backsight azimuths.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuthCorrection} and returns the new value for {@code backAzimuthCorrection}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the updated {@code backAzimuthCorrection}.
	 */
	public MutableSurveyTrip updateBackAzimuthCorrection(Function<String, String> updater) {
		return update(SurveyTrip.backAzimuthCorrection, updater);
	}
	
	/**
	 * Updates correction for backsight inclinations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclinationCorrection} and returns the new value for {@code backInclinationCorrection}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the updated {@code backInclinationCorrection}.
	 */
	public MutableSurveyTrip updateBackInclinationCorrection(Function<String, String> updater) {
		return update(SurveyTrip.backInclinationCorrection, updater);
	}
	
	/**
	 * Updates the geodetic datum for fixed station locations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code datum} and returns the new value for {@code datum}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code datum} is unchanged, or a copy with the updated {@code datum}.
	 */
	public MutableSurveyTrip updateDatum(Function<String, String> updater) {
		return update(SurveyTrip.datum, updater);
	}
	
	/**
	 * Updates the reference ellipsoid for fixed station locations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code ellipsoid} and returns the new value for {@code ellipsoid}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code ellipsoid} is unchanged, or a copy with the updated {@code ellipsoid}.
	 */
	public MutableSurveyTrip updateEllipsoid(Function<String, String> updater) {
		return update(SurveyTrip.ellipsoid, updater);
	}
	
	/**
	 * Updates the UTM zone for fixed station locations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code utmZone} and returns the new value for {@code utmZone}.
	 *
	 * @return this {@code MutableSurveyTrip} if {@code utmZone} is unchanged, or a copy with the updated {@code utmZone}.
	 */
	public MutableSurveyTrip updateUtmZone(Function<String, String> updater) {
		return update(SurveyTrip.utmZone, updater);
	}
	
	
}
