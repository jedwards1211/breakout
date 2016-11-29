/**
 * Generated from {@code SurveyTrip.record.js} by java-record-generator on 11/29/2016, 1:16:50 AM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */
 
package org.breakout.model;

import java.util.List;
import org.andork.unit.Unit;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.breakout.model.SurveyTrip.Data;
import java.util.function.Function;

/**
 * The mutable version of {@link SurveyTrip}.
 */
public final class MutableSurveyTrip {
	private volatile boolean frozen = true;
	private volatile Data data;
	
	MutableSurveyTrip(Data data) {
		this.data = data;
	}
	
	public MutableSurveyTrip() {
		this(Data.initial);
	}
 
	public void detach() {
		if (frozen) {
			data = data.clone();
			frozen = false;
		}
	}
	
	/**
	 * @return an immutable copy of this {@code MutableSurveyTrip}.
	 */
	public SurveyTrip toImmutable() {
		frozen = true;
		return new SurveyTrip(data);
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
	 * @return this {@code MutableSurveyTrip} if {@code cave} is unchanged, or a copy with the updated {@code cave}.
	 */
	public MutableSurveyTrip updateCave(Function<String, String> updater) {
		return setCave(updater.apply(data.cave));
	}
	
	/**
	 * Updates trip name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code name} and returns the new value for {@code name}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code name} is unchanged, or a copy with the updated {@code name}.
	 */
	public MutableSurveyTrip updateName(Function<String, String> updater) {
		return setName(updater.apply(data.name));
	}
	
	/**
	 * Updates trip date.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code date} and returns the new value for {@code date}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code date} is unchanged, or a copy with the updated {@code date}.
	 */
	public MutableSurveyTrip updateDate(Function<String, String> updater) {
		return setDate(updater.apply(data.date));
	}
	
	/**
	 * Updates survey notes file path.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code surveyNotes} and returns the new value for {@code surveyNotes}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code surveyNotes} is unchanged, or a copy with the updated {@code surveyNotes}.
	 */
	public MutableSurveyTrip updateSurveyNotes(Function<String, String> updater) {
		return setSurveyNotes(updater.apply(data.surveyNotes));
	}
	
	/**
	 * Updates surveyor names.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code surveyors} and returns the new value for {@code surveyors}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code surveyors} is unchanged, or a copy with the updated {@code surveyors}.
	 */
	public MutableSurveyTrip updateSurveyors(Function<List<String>, List<String>> updater) {
		return setSurveyors(updater.apply(data.surveyors));
	}
	
	/**
	 * Updates default length unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distanceUnit} and returns the new value for {@code distanceUnit}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code distanceUnit} is unchanged, or a copy with the updated {@code distanceUnit}.
	 */
	public MutableSurveyTrip updateDistanceUnit(Function<Unit<Length>, Unit<Length>> updater) {
		return setDistanceUnit(updater.apply(data.distanceUnit));
	}
	
	/**
	 * Updates default angle unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code angleUnit} and returns the new value for {@code angleUnit}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code angleUnit} is unchanged, or a copy with the updated {@code angleUnit}.
	 */
	public MutableSurveyTrip updateAngleUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setAngleUnit(updater.apply(data.angleUnit));
	}
	
	/**
	 * Updates default frontsight azimuth unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFrontAzimuthUnit} and returns the new value for {@code overrideFrontAzimuthUnit}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code overrideFrontAzimuthUnit} is unchanged, or a copy with the updated {@code overrideFrontAzimuthUnit}.
	 */
	public MutableSurveyTrip updateOverrideFrontAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setOverrideFrontAzimuthUnit(updater.apply(data.overrideFrontAzimuthUnit));
	}
	
	/**
	 * Updates default backsight azimuth unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideBackAzimuthUnit} and returns the new value for {@code overrideBackAzimuthUnit}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code overrideBackAzimuthUnit} is unchanged, or a copy with the updated {@code overrideBackAzimuthUnit}.
	 */
	public MutableSurveyTrip updateOverrideBackAzimuthUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setOverrideBackAzimuthUnit(updater.apply(data.overrideBackAzimuthUnit));
	}
	
	/**
	 * Updates default frontsight inclination unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFrontInclinationUnit} and returns the new value for {@code overrideFrontInclinationUnit}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code overrideFrontInclinationUnit} is unchanged, or a copy with the updated {@code overrideFrontInclinationUnit}.
	 */
	public MutableSurveyTrip updateOverrideFrontInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setOverrideFrontInclinationUnit(updater.apply(data.overrideFrontInclinationUnit));
	}
	
	/**
	 * Updates default backsight inclination unit.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideBackInclinationUnit} and returns the new value for {@code overrideBackInclinationUnit}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code overrideBackInclinationUnit} is unchanged, or a copy with the updated {@code overrideBackInclinationUnit}.
	 */
	public MutableSurveyTrip updateOverrideBackInclinationUnit(Function<Unit<Angle>, Unit<Angle>> updater) {
		return setOverrideBackInclinationUnit(updater.apply(data.overrideBackInclinationUnit));
	}
	
	/**
	 * Updates whether backsight azimuths are corrected.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuthsCorrected} and returns the new value for {@code backAzimuthsCorrected}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code backAzimuthsCorrected} is unchanged, or a copy with the updated {@code backAzimuthsCorrected}.
	 */
	public MutableSurveyTrip updateBackAzimuthsCorrected(Function<Boolean, Boolean> updater) {
		return setBackAzimuthsCorrected(updater.apply(data.backAzimuthsCorrected));
	}
	
	/**
	 * Updates whether backsight inclinations are corrected.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclinationsCorrected} and returns the new value for {@code backInclinationsCorrected}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code backInclinationsCorrected} is unchanged, or a copy with the updated {@code backInclinationsCorrected}.
	 */
	public MutableSurveyTrip updateBackInclinationsCorrected(Function<Boolean, Boolean> updater) {
		return setBackInclinationsCorrected(updater.apply(data.backInclinationsCorrected));
	}
	
	/**
	 * Updates magnetic declination.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code declination} and returns the new value for {@code declination}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code declination} is unchanged, or a copy with the updated {@code declination}.
	 */
	public MutableSurveyTrip updateDeclination(Function<String, String> updater) {
		return setDeclination(updater.apply(data.declination));
	}
	
	/**
	 * Updates correction for shot distances.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distanceCorrection} and returns the new value for {@code distanceCorrection}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code distanceCorrection} is unchanged, or a copy with the updated {@code distanceCorrection}.
	 */
	public MutableSurveyTrip updateDistanceCorrection(Function<String, String> updater) {
		return setDistanceCorrection(updater.apply(data.distanceCorrection));
	}
	
	/**
	 * Updates correction for frontsight azimuths.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontAzimuthCorrection} and returns the new value for {@code frontAzimuthCorrection}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code frontAzimuthCorrection} is unchanged, or a copy with the updated {@code frontAzimuthCorrection}.
	 */
	public MutableSurveyTrip updateFrontAzimuthCorrection(Function<String, String> updater) {
		return setFrontAzimuthCorrection(updater.apply(data.frontAzimuthCorrection));
	}
	
	/**
	 * Updates correction for frontsight inclinations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontInclinationCorrection} and returns the new value for {@code frontInclinationCorrection}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code frontInclinationCorrection} is unchanged, or a copy with the updated {@code frontInclinationCorrection}.
	 */
	public MutableSurveyTrip updateFrontInclinationCorrection(Function<String, String> updater) {
		return setFrontInclinationCorrection(updater.apply(data.frontInclinationCorrection));
	}
	
	/**
	 * Updates correction for backsight azimuths.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuthCorrection} and returns the new value for {@code backAzimuthCorrection}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code backAzimuthCorrection} is unchanged, or a copy with the updated {@code backAzimuthCorrection}.
	 */
	public MutableSurveyTrip updateBackAzimuthCorrection(Function<String, String> updater) {
		return setBackAzimuthCorrection(updater.apply(data.backAzimuthCorrection));
	}
	
	/**
	 * Updates correction for backsight inclinations.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclinationCorrection} and returns the new value for {@code backInclinationCorrection}.
	 * 
	 * @return this {@code MutableSurveyTrip} if {@code backInclinationCorrection} is unchanged, or a copy with the updated {@code backInclinationCorrection}.
	 */
	public MutableSurveyTrip updateBackInclinationCorrection(Function<String, String> updater) {
		return setBackInclinationCorrection(updater.apply(data.backInclinationCorrection));
	}
	
	
	@Override
	public int hashCode() {
		return data.hashCode();
	}

	boolean dataIs(Data data) {
		return this.data == data;
	}

	boolean dataEquals(Data data) {
		return data.equals(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof SurveyTrip) return ((SurveyTrip) obj).dataEquals(data);
		if (obj instanceof MutableSurveyTrip) return ((MutableSurveyTrip) obj).dataEquals(data);
		return false;
	}
	
	
}
