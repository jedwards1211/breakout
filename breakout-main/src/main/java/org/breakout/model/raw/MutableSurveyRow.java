/**
 * Generated from {@code SurveyRow.record.js} by java-record-generator on 8/25/2019, 1:37:49 AM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */

package org.breakout.model.raw;

import com.github.krukow.clj_ds.PersistentVector;
import com.github.krukow.clj_ds.TransientMap;
import com.github.krukow.clj_lang.PersistentHashMap;
import java.util.Objects;
import java.util.function.Function;

/**
 * The mutable version of {@link SurveyRow}.
 */
public final class MutableSurveyRow {
	private volatile PersistentHashMap<String, Object> persisted;
	private final TransientMap<String, Object> data;

	@SuppressWarnings("unchecked")
	MutableSurveyRow(PersistentHashMap<String, Object> data) {
		persisted = data;
		this.data = persisted.asTransient();
	}

	public MutableSurveyRow() {
		this(SurveyRow.initialData);
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

	public SurveyRow toImmutable() {
		return new SurveyRow(persist());
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

	public MutableSurveyRow set(String key, Object value) {
		if (persisted != null && equals(value, persisted.get(key))) {
			return this;
		}
		persisted = null;
		data.plus(key, value);
		return this;
	}

	public <T> MutableSurveyRow update(String key, Function<? super T, ? extends T> updater) {
		@SuppressWarnings("unchecked")
		T oldValue = (T) persist().get(key);
		T newValue = updater.apply(oldValue);
		if (equals(oldValue, newValue)) {
			return this;
		}
		data.plus(key, newValue);
		return this;
	}

	public MutableSurveyRow delete(String key) {
		if (persisted != null && !persisted.containsKey(key)) {
			return this;
		}
		persisted = null;
		data.minus(key);
		return this;
	}

	
	
	/**
	 * @return name of cave from station is in, if different from trip.
	 */
	public String getOverrideFromCave() {
		return get(SurveyRow.overrideFromCave);
	}
	
	/**
	 * @return from station name.
	 */
	public String getFromStation() {
		return get(SurveyRow.fromStation);
	}
	
	/**
	 * @return name of cave of to station is in, if different to trip.
	 */
	public String getOverrideToCave() {
		return get(SurveyRow.overrideToCave);
	}
	
	/**
	 * @return to station name.
	 */
	public String getToStation() {
		return get(SurveyRow.toStation);
	}
	
	/**
	 * @return distance between from and to station.
	 */
	public String getDistance() {
		return get(SurveyRow.distance);
	}
	
	/**
	 * @return azimuth toward to station at from station.
	 */
	public String getFrontAzimuth() {
		return get(SurveyRow.frontAzimuth);
	}
	
	/**
	 * @return azimuth toward from station at to station.
	 */
	public String getBackAzimuth() {
		return get(SurveyRow.backAzimuth);
	}
	
	/**
	 * @return inclination toward to station at from station.
	 */
	public String getFrontInclination() {
		return get(SurveyRow.frontInclination);
	}
	
	/**
	 * @return inclination toward from station at to station.
	 */
	public String getBackInclination() {
		return get(SurveyRow.backInclination);
	}
	
	/**
	 * @return distance between from station and left wall.
	 */
	public String getLeft() {
		return get(SurveyRow.left);
	}
	
	/**
	 * @return distance between from station and right wall.
	 */
	public String getRight() {
		return get(SurveyRow.right);
	}
	
	/**
	 * @return distance between from station and ceiling.
	 */
	public String getUp() {
		return get(SurveyRow.up);
	}
	
	/**
	 * @return distance between from station and floor.
	 */
	public String getDown() {
		return get(SurveyRow.down);
	}
	
	/**
	 * @return distance north relative to coordinate origin.
	 */
	public String getNorthing() {
		return get(SurveyRow.northing);
	}
	
	/**
	 * @return from station's latitude.
	 */
	public String getLatitude() {
		return get(SurveyRow.latitude);
	}
	
	/**
	 * @return from station's longitude.
	 */
	public String getLongitude() {
		return get(SurveyRow.longitude);
	}
	
	/**
	 * @return from station's distance east relative to coordinate origin.
	 */
	public String getEasting() {
		return get(SurveyRow.easting);
	}
	
	/**
	 * @return from station's distance east relative to coordinate origin.
	 */
	public String getElevation() {
		return get(SurveyRow.elevation);
	}
	
	/**
	 * @return any user comment.
	 */
	public String getComment() {
		return get(SurveyRow.comment);
	}
	
	/**
	 * @return attached files (if one they can't be associated with the entire trip).
	 */
	public PersistentVector<String> getOverrideAttachedFiles() {
		return get(SurveyRow.overrideAttachedFiles);
	}
	
	/**
	 * @return trip this row belongs to.
	 */
	public SurveyTrip getTrip() {
		return get(SurveyRow.trip);
	}
	
	/**
	 * @return whether to exclude this shot from the total cave length.
	 */
	public Boolean isExcludeDistance() {
		return get(SurveyRow.excludeDistance, false);
	}
	
	/**
	 * @return whether to exclude this shot from plotting.
	 */
	public Boolean isExcludeFromPlotting() {
		return get(SurveyRow.excludeFromPlotting, false);
	}
	
	
	/**
	 * Sets name of cave from station is in, if different from trip.
	 *
	 * @param overrideFromCave - the new value for name of cave from station is in, if different from trip
	 *
	 * @return this {@code SurveyRow} if {@code overrideFromCave} is unchanged, or a copy with the new {@code overrideFromCave}.
	 */
	public MutableSurveyRow setOverrideFromCave(String overrideFromCave) {
		return set(SurveyRow.overrideFromCave, overrideFromCave);
	}
	
	/**
	 * Sets from station name.
	 *
	 * @param fromStation - the new value for from station name
	 *
	 * @return this {@code SurveyRow} if {@code fromStation} is unchanged, or a copy with the new {@code fromStation}.
	 */
	public MutableSurveyRow setFromStation(String fromStation) {
		return set(SurveyRow.fromStation, fromStation);
	}
	
	/**
	 * Sets name of cave of to station is in, if different to trip.
	 *
	 * @param overrideToCave - the new value for name of cave of to station is in, if different to trip
	 *
	 * @return this {@code SurveyRow} if {@code overrideToCave} is unchanged, or a copy with the new {@code overrideToCave}.
	 */
	public MutableSurveyRow setOverrideToCave(String overrideToCave) {
		return set(SurveyRow.overrideToCave, overrideToCave);
	}
	
	/**
	 * Sets to station name.
	 *
	 * @param toStation - the new value for to station name
	 *
	 * @return this {@code SurveyRow} if {@code toStation} is unchanged, or a copy with the new {@code toStation}.
	 */
	public MutableSurveyRow setToStation(String toStation) {
		return set(SurveyRow.toStation, toStation);
	}
	
	/**
	 * Sets distance between from and to station.
	 *
	 * @param distance - the new value for distance between from and to station
	 *
	 * @return this {@code SurveyRow} if {@code distance} is unchanged, or a copy with the new {@code distance}.
	 */
	public MutableSurveyRow setDistance(String distance) {
		return set(SurveyRow.distance, distance);
	}
	
	/**
	 * Sets azimuth toward to station at from station.
	 *
	 * @param frontAzimuth - the new value for azimuth toward to station at from station
	 *
	 * @return this {@code SurveyRow} if {@code frontAzimuth} is unchanged, or a copy with the new {@code frontAzimuth}.
	 */
	public MutableSurveyRow setFrontAzimuth(String frontAzimuth) {
		return set(SurveyRow.frontAzimuth, frontAzimuth);
	}
	
	/**
	 * Sets azimuth toward from station at to station.
	 *
	 * @param backAzimuth - the new value for azimuth toward from station at to station
	 *
	 * @return this {@code SurveyRow} if {@code backAzimuth} is unchanged, or a copy with the new {@code backAzimuth}.
	 */
	public MutableSurveyRow setBackAzimuth(String backAzimuth) {
		return set(SurveyRow.backAzimuth, backAzimuth);
	}
	
	/**
	 * Sets inclination toward to station at from station.
	 *
	 * @param frontInclination - the new value for inclination toward to station at from station
	 *
	 * @return this {@code SurveyRow} if {@code frontInclination} is unchanged, or a copy with the new {@code frontInclination}.
	 */
	public MutableSurveyRow setFrontInclination(String frontInclination) {
		return set(SurveyRow.frontInclination, frontInclination);
	}
	
	/**
	 * Sets inclination toward from station at to station.
	 *
	 * @param backInclination - the new value for inclination toward from station at to station
	 *
	 * @return this {@code SurveyRow} if {@code backInclination} is unchanged, or a copy with the new {@code backInclination}.
	 */
	public MutableSurveyRow setBackInclination(String backInclination) {
		return set(SurveyRow.backInclination, backInclination);
	}
	
	/**
	 * Sets distance between from station and left wall.
	 *
	 * @param left - the new value for distance between from station and left wall
	 *
	 * @return this {@code SurveyRow} if {@code left} is unchanged, or a copy with the new {@code left}.
	 */
	public MutableSurveyRow setLeft(String left) {
		return set(SurveyRow.left, left);
	}
	
	/**
	 * Sets distance between from station and right wall.
	 *
	 * @param right - the new value for distance between from station and right wall
	 *
	 * @return this {@code SurveyRow} if {@code right} is unchanged, or a copy with the new {@code right}.
	 */
	public MutableSurveyRow setRight(String right) {
		return set(SurveyRow.right, right);
	}
	
	/**
	 * Sets distance between from station and ceiling.
	 *
	 * @param up - the new value for distance between from station and ceiling
	 *
	 * @return this {@code SurveyRow} if {@code up} is unchanged, or a copy with the new {@code up}.
	 */
	public MutableSurveyRow setUp(String up) {
		return set(SurveyRow.up, up);
	}
	
	/**
	 * Sets distance between from station and floor.
	 *
	 * @param down - the new value for distance between from station and floor
	 *
	 * @return this {@code SurveyRow} if {@code down} is unchanged, or a copy with the new {@code down}.
	 */
	public MutableSurveyRow setDown(String down) {
		return set(SurveyRow.down, down);
	}
	
	/**
	 * Sets distance north relative to coordinate origin.
	 *
	 * @param northing - the new value for distance north relative to coordinate origin
	 *
	 * @return this {@code SurveyRow} if {@code northing} is unchanged, or a copy with the new {@code northing}.
	 */
	public MutableSurveyRow setNorthing(String northing) {
		return set(SurveyRow.northing, northing);
	}
	
	/**
	 * Sets from station's latitude.
	 *
	 * @param latitude - the new value for from station's latitude
	 *
	 * @return this {@code SurveyRow} if {@code latitude} is unchanged, or a copy with the new {@code latitude}.
	 */
	public MutableSurveyRow setLatitude(String latitude) {
		return set(SurveyRow.latitude, latitude);
	}
	
	/**
	 * Sets from station's longitude.
	 *
	 * @param longitude - the new value for from station's longitude
	 *
	 * @return this {@code SurveyRow} if {@code longitude} is unchanged, or a copy with the new {@code longitude}.
	 */
	public MutableSurveyRow setLongitude(String longitude) {
		return set(SurveyRow.longitude, longitude);
	}
	
	/**
	 * Sets from station's distance east relative to coordinate origin.
	 *
	 * @param easting - the new value for from station's distance east relative to coordinate origin
	 *
	 * @return this {@code SurveyRow} if {@code easting} is unchanged, or a copy with the new {@code easting}.
	 */
	public MutableSurveyRow setEasting(String easting) {
		return set(SurveyRow.easting, easting);
	}
	
	/**
	 * Sets from station's distance east relative to coordinate origin.
	 *
	 * @param elevation - the new value for from station's distance east relative to coordinate origin
	 *
	 * @return this {@code SurveyRow} if {@code elevation} is unchanged, or a copy with the new {@code elevation}.
	 */
	public MutableSurveyRow setElevation(String elevation) {
		return set(SurveyRow.elevation, elevation);
	}
	
	/**
	 * Sets any user comment.
	 *
	 * @param comment - the new value for any user comment
	 *
	 * @return this {@code SurveyRow} if {@code comment} is unchanged, or a copy with the new {@code comment}.
	 */
	public MutableSurveyRow setComment(String comment) {
		return set(SurveyRow.comment, comment);
	}
	
	/**
	 * Sets attached files (if one they can't be associated with the entire trip).
	 *
	 * @param overrideAttachedFiles - the new value for attached files (if one they can't be associated with the entire trip)
	 *
	 * @return this {@code SurveyRow} if {@code overrideAttachedFiles} is unchanged, or a copy with the new {@code overrideAttachedFiles}.
	 */
	public MutableSurveyRow setOverrideAttachedFiles(PersistentVector<String> overrideAttachedFiles) {
		return set(SurveyRow.overrideAttachedFiles, overrideAttachedFiles);
	}
	
	/**
	 * Sets trip this row belongs to.
	 *
	 * @param trip - the new value for trip this row belongs to
	 *
	 * @return this {@code SurveyRow} if {@code trip} is unchanged, or a copy with the new {@code trip}.
	 */
	public MutableSurveyRow setTrip(SurveyTrip trip) {
		return set(SurveyRow.trip, trip);
	}
	
	/**
	 * Sets whether to exclude this shot from the total cave length.
	 *
	 * @param excludeDistance - the new value for whether to exclude this shot from the total cave length
	 *
	 * @return this {@code SurveyRow} if {@code excludeDistance} is unchanged, or a copy with the new {@code excludeDistance}.
	 */
	public MutableSurveyRow setExcludeDistance(Boolean excludeDistance) {
		return set(SurveyRow.excludeDistance, excludeDistance);
	}
	
	/**
	 * Sets whether to exclude this shot from plotting.
	 *
	 * @param excludeFromPlotting - the new value for whether to exclude this shot from plotting
	 *
	 * @return this {@code SurveyRow} if {@code excludeFromPlotting} is unchanged, or a copy with the new {@code excludeFromPlotting}.
	 */
	public MutableSurveyRow setExcludeFromPlotting(Boolean excludeFromPlotting) {
		return set(SurveyRow.excludeFromPlotting, excludeFromPlotting);
	}
	
	
	/**
	 * Updates name of cave from station is in, if different from trip.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFromCave} and returns the new value for {@code overrideFromCave}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code overrideFromCave} is unchanged, or a copy with the updated {@code overrideFromCave}.
	 */
	public MutableSurveyRow updateOverrideFromCave(Function<String, String> updater) {
		return update(SurveyRow.overrideFromCave, updater);
	}
	
	/**
	 * Updates from station name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code fromStation} and returns the new value for {@code fromStation}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code fromStation} is unchanged, or a copy with the updated {@code fromStation}.
	 */
	public MutableSurveyRow updateFromStation(Function<String, String> updater) {
		return update(SurveyRow.fromStation, updater);
	}
	
	/**
	 * Updates name of cave of to station is in, if different to trip.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideToCave} and returns the new value for {@code overrideToCave}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code overrideToCave} is unchanged, or a copy with the updated {@code overrideToCave}.
	 */
	public MutableSurveyRow updateOverrideToCave(Function<String, String> updater) {
		return update(SurveyRow.overrideToCave, updater);
	}
	
	/**
	 * Updates to station name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code toStation} and returns the new value for {@code toStation}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code toStation} is unchanged, or a copy with the updated {@code toStation}.
	 */
	public MutableSurveyRow updateToStation(Function<String, String> updater) {
		return update(SurveyRow.toStation, updater);
	}
	
	/**
	 * Updates distance between from and to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distance} and returns the new value for {@code distance}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code distance} is unchanged, or a copy with the updated {@code distance}.
	 */
	public MutableSurveyRow updateDistance(Function<String, String> updater) {
		return update(SurveyRow.distance, updater);
	}
	
	/**
	 * Updates azimuth toward to station at from station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontAzimuth} and returns the new value for {@code frontAzimuth}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code frontAzimuth} is unchanged, or a copy with the updated {@code frontAzimuth}.
	 */
	public MutableSurveyRow updateFrontAzimuth(Function<String, String> updater) {
		return update(SurveyRow.frontAzimuth, updater);
	}
	
	/**
	 * Updates azimuth toward from station at to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuth} and returns the new value for {@code backAzimuth}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code backAzimuth} is unchanged, or a copy with the updated {@code backAzimuth}.
	 */
	public MutableSurveyRow updateBackAzimuth(Function<String, String> updater) {
		return update(SurveyRow.backAzimuth, updater);
	}
	
	/**
	 * Updates inclination toward to station at from station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontInclination} and returns the new value for {@code frontInclination}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code frontInclination} is unchanged, or a copy with the updated {@code frontInclination}.
	 */
	public MutableSurveyRow updateFrontInclination(Function<String, String> updater) {
		return update(SurveyRow.frontInclination, updater);
	}
	
	/**
	 * Updates inclination toward from station at to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclination} and returns the new value for {@code backInclination}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code backInclination} is unchanged, or a copy with the updated {@code backInclination}.
	 */
	public MutableSurveyRow updateBackInclination(Function<String, String> updater) {
		return update(SurveyRow.backInclination, updater);
	}
	
	/**
	 * Updates distance between from station and left wall.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code left} and returns the new value for {@code left}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code left} is unchanged, or a copy with the updated {@code left}.
	 */
	public MutableSurveyRow updateLeft(Function<String, String> updater) {
		return update(SurveyRow.left, updater);
	}
	
	/**
	 * Updates distance between from station and right wall.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code right} and returns the new value for {@code right}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code right} is unchanged, or a copy with the updated {@code right}.
	 */
	public MutableSurveyRow updateRight(Function<String, String> updater) {
		return update(SurveyRow.right, updater);
	}
	
	/**
	 * Updates distance between from station and ceiling.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code up} and returns the new value for {@code up}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code up} is unchanged, or a copy with the updated {@code up}.
	 */
	public MutableSurveyRow updateUp(Function<String, String> updater) {
		return update(SurveyRow.up, updater);
	}
	
	/**
	 * Updates distance between from station and floor.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code down} and returns the new value for {@code down}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code down} is unchanged, or a copy with the updated {@code down}.
	 */
	public MutableSurveyRow updateDown(Function<String, String> updater) {
		return update(SurveyRow.down, updater);
	}
	
	/**
	 * Updates distance north relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code northing} and returns the new value for {@code northing}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code northing} is unchanged, or a copy with the updated {@code northing}.
	 */
	public MutableSurveyRow updateNorthing(Function<String, String> updater) {
		return update(SurveyRow.northing, updater);
	}
	
	/**
	 * Updates from station's latitude.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code latitude} and returns the new value for {@code latitude}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code latitude} is unchanged, or a copy with the updated {@code latitude}.
	 */
	public MutableSurveyRow updateLatitude(Function<String, String> updater) {
		return update(SurveyRow.latitude, updater);
	}
	
	/**
	 * Updates from station's longitude.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code longitude} and returns the new value for {@code longitude}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code longitude} is unchanged, or a copy with the updated {@code longitude}.
	 */
	public MutableSurveyRow updateLongitude(Function<String, String> updater) {
		return update(SurveyRow.longitude, updater);
	}
	
	/**
	 * Updates from station's distance east relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code easting} and returns the new value for {@code easting}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code easting} is unchanged, or a copy with the updated {@code easting}.
	 */
	public MutableSurveyRow updateEasting(Function<String, String> updater) {
		return update(SurveyRow.easting, updater);
	}
	
	/**
	 * Updates from station's distance east relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code elevation} and returns the new value for {@code elevation}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code elevation} is unchanged, or a copy with the updated {@code elevation}.
	 */
	public MutableSurveyRow updateElevation(Function<String, String> updater) {
		return update(SurveyRow.elevation, updater);
	}
	
	/**
	 * Updates any user comment.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code comment} and returns the new value for {@code comment}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code comment} is unchanged, or a copy with the updated {@code comment}.
	 */
	public MutableSurveyRow updateComment(Function<String, String> updater) {
		return update(SurveyRow.comment, updater);
	}
	
	/**
	 * Updates attached files (if one they can't be associated with the entire trip).
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideAttachedFiles} and returns the new value for {@code overrideAttachedFiles}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code overrideAttachedFiles} is unchanged, or a copy with the updated {@code overrideAttachedFiles}.
	 */
	public MutableSurveyRow updateOverrideAttachedFiles(Function<PersistentVector<String>, PersistentVector<String>> updater) {
		return update(SurveyRow.overrideAttachedFiles, updater);
	}
	
	/**
	 * Updates trip this row belongs to.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code trip} and returns the new value for {@code trip}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code trip} is unchanged, or a copy with the updated {@code trip}.
	 */
	public MutableSurveyRow updateTrip(Function<SurveyTrip, SurveyTrip> updater) {
		return update(SurveyRow.trip, updater);
	}
	
	/**
	 * Updates whether to exclude this shot from the total cave length.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code excludeDistance} and returns the new value for {@code excludeDistance}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code excludeDistance} is unchanged, or a copy with the updated {@code excludeDistance}.
	 */
	public MutableSurveyRow updateExcludeDistance(Function<Boolean, Boolean> updater) {
		return update(SurveyRow.excludeDistance, updater);
	}
	
	/**
	 * Updates whether to exclude this shot from plotting.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code excludeFromPlotting} and returns the new value for {@code excludeFromPlotting}.
	 *
	 * @return this {@code MutableSurveyRow} if {@code excludeFromPlotting} is unchanged, or a copy with the updated {@code excludeFromPlotting}.
	 */
	public MutableSurveyRow updateExcludeFromPlotting(Function<Boolean, Boolean> updater) {
		return update(SurveyRow.excludeFromPlotting, updater);
	}
	
	
	public MutableSurveyRow ensureTrip() {
		if (getTrip() == null) setTrip(new SurveyTrip());
		return this;
	}
	
}
