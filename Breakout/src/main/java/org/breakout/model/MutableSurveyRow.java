/**
 * Generated from {@code SurveyRow.record.js} by java-record-generator on 11/29/2016, 1:16:50 AM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */
 
package org.breakout.model;

import org.breakout.model.SurveyRow.Data;
import java.util.function.Function;

/**
 * The mutable version of {@link SurveyRow}.
 */
public final class MutableSurveyRow {
	private volatile boolean frozen = true;
	private volatile Data data;
	
	MutableSurveyRow(Data data) {
		this.data = data;
	}
	
	public MutableSurveyRow() {
		this(Data.initial);
	}
 
	public void detach() {
		if (frozen) {
			data = data.clone();
			frozen = false;
		}
	}
	
	/**
	 * @return an immutable copy of this {@code MutableSurveyRow}.
	 */
	public SurveyRow toImmutable() {
		frozen = true;
		return new SurveyRow(data);
	} 
	
	
	/**
	 * @return name of cave from station is in, if different from trip.
	 */
	public String getOverrideFromCave() {
		return data.overrideFromCave;
	}
	
	/**
	 * @return from station name.
	 */
	public String getFromStation() {
		return data.fromStation;
	}
	
	/**
	 * @return name of cave of to station is in, if different to trip.
	 */
	public String getOverrideToCave() {
		return data.overrideToCave;
	}
	
	/**
	 * @return to station name.
	 */
	public String getToStation() {
		return data.toStation;
	}
	
	/**
	 * @return distance between from and to station.
	 */
	public String getDistance() {
		return data.distance;
	}
	
	/**
	 * @return azimuth toward to station at from station.
	 */
	public String getFrontAzimuth() {
		return data.frontAzimuth;
	}
	
	/**
	 * @return azimuth toward from station at to station.
	 */
	public String getBackAzimuth() {
		return data.backAzimuth;
	}
	
	/**
	 * @return inclination toward to station at from station.
	 */
	public String getFrontInclination() {
		return data.frontInclination;
	}
	
	/**
	 * @return inclination toward from station at to station.
	 */
	public String getBackInclination() {
		return data.backInclination;
	}
	
	/**
	 * @return distance between from station and left wall.
	 */
	public String getLeft() {
		return data.left;
	}
	
	/**
	 * @return distance between from station and right wall.
	 */
	public String getRight() {
		return data.right;
	}
	
	/**
	 * @return distance between from station and ceiling.
	 */
	public String getUp() {
		return data.up;
	}
	
	/**
	 * @return distance between from station and floor.
	 */
	public String getDown() {
		return data.down;
	}
	
	/**
	 * @return distance north relative to coordinate origin.
	 */
	public String getNorthing() {
		return data.northing;
	}
	
	/**
	 * @return distance east relative to coordinate origin.
	 */
	public String getEasting() {
		return data.easting;
	}
	
	/**
	 * @return distance east relative to coordinate origin.
	 */
	public String getElevation() {
		return data.elevation;
	}
	
	/**
	 * @return any user comment.
	 */
	public String getComment() {
		return data.comment;
	}
	
	/**
	 * @return trip this row belongs to.
	 */
	public SurveyTrip getTrip() {
		return data.trip;
	}
	
	
	/**
	 * Sets name of cave from station is in, if different from trip.
	 *
	 * @param overrideFromCave - the new value for name of cave from station is in, if different from trip
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setOverrideFromCave(String overrideFromCave) {
		if (data.overrideFromCave == overrideFromCave) return this;
		detach();
		data.overrideFromCave = overrideFromCave;
		return this;
	}
	
	/**
	 * Sets from station name.
	 *
	 * @param fromStation - the new value for from station name
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setFromStation(String fromStation) {
		if (data.fromStation == fromStation) return this;
		detach();
		data.fromStation = fromStation;
		return this;
	}
	
	/**
	 * Sets name of cave of to station is in, if different to trip.
	 *
	 * @param overrideToCave - the new value for name of cave of to station is in, if different to trip
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setOverrideToCave(String overrideToCave) {
		if (data.overrideToCave == overrideToCave) return this;
		detach();
		data.overrideToCave = overrideToCave;
		return this;
	}
	
	/**
	 * Sets to station name.
	 *
	 * @param toStation - the new value for to station name
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setToStation(String toStation) {
		if (data.toStation == toStation) return this;
		detach();
		data.toStation = toStation;
		return this;
	}
	
	/**
	 * Sets distance between from and to station.
	 *
	 * @param distance - the new value for distance between from and to station
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setDistance(String distance) {
		if (data.distance == distance) return this;
		detach();
		data.distance = distance;
		return this;
	}
	
	/**
	 * Sets azimuth toward to station at from station.
	 *
	 * @param frontAzimuth - the new value for azimuth toward to station at from station
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setFrontAzimuth(String frontAzimuth) {
		if (data.frontAzimuth == frontAzimuth) return this;
		detach();
		data.frontAzimuth = frontAzimuth;
		return this;
	}
	
	/**
	 * Sets azimuth toward from station at to station.
	 *
	 * @param backAzimuth - the new value for azimuth toward from station at to station
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setBackAzimuth(String backAzimuth) {
		if (data.backAzimuth == backAzimuth) return this;
		detach();
		data.backAzimuth = backAzimuth;
		return this;
	}
	
	/**
	 * Sets inclination toward to station at from station.
	 *
	 * @param frontInclination - the new value for inclination toward to station at from station
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setFrontInclination(String frontInclination) {
		if (data.frontInclination == frontInclination) return this;
		detach();
		data.frontInclination = frontInclination;
		return this;
	}
	
	/**
	 * Sets inclination toward from station at to station.
	 *
	 * @param backInclination - the new value for inclination toward from station at to station
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setBackInclination(String backInclination) {
		if (data.backInclination == backInclination) return this;
		detach();
		data.backInclination = backInclination;
		return this;
	}
	
	/**
	 * Sets distance between from station and left wall.
	 *
	 * @param left - the new value for distance between from station and left wall
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setLeft(String left) {
		if (data.left == left) return this;
		detach();
		data.left = left;
		return this;
	}
	
	/**
	 * Sets distance between from station and right wall.
	 *
	 * @param right - the new value for distance between from station and right wall
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setRight(String right) {
		if (data.right == right) return this;
		detach();
		data.right = right;
		return this;
	}
	
	/**
	 * Sets distance between from station and ceiling.
	 *
	 * @param up - the new value for distance between from station and ceiling
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setUp(String up) {
		if (data.up == up) return this;
		detach();
		data.up = up;
		return this;
	}
	
	/**
	 * Sets distance between from station and floor.
	 *
	 * @param down - the new value for distance between from station and floor
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setDown(String down) {
		if (data.down == down) return this;
		detach();
		data.down = down;
		return this;
	}
	
	/**
	 * Sets distance north relative to coordinate origin.
	 *
	 * @param northing - the new value for distance north relative to coordinate origin
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setNorthing(String northing) {
		if (data.northing == northing) return this;
		detach();
		data.northing = northing;
		return this;
	}
	
	/**
	 * Sets distance east relative to coordinate origin.
	 *
	 * @param easting - the new value for distance east relative to coordinate origin
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setEasting(String easting) {
		if (data.easting == easting) return this;
		detach();
		data.easting = easting;
		return this;
	}
	
	/**
	 * Sets distance east relative to coordinate origin.
	 *
	 * @param elevation - the new value for distance east relative to coordinate origin
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setElevation(String elevation) {
		if (data.elevation == elevation) return this;
		detach();
		data.elevation = elevation;
		return this;
	}
	
	/**
	 * Sets any user comment.
	 *
	 * @param comment - the new value for any user comment
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setComment(String comment) {
		if (data.comment == comment) return this;
		detach();
		data.comment = comment;
		return this;
	}
	
	/**
	 * Sets trip this row belongs to.
	 *
	 * @param trip - the new value for trip this row belongs to
	 * 
	 * @return this {@code MutableSurveyRow}.
	 */
	public MutableSurveyRow setTrip(SurveyTrip trip) {
		if (data.trip == trip) return this;
		detach();
		data.trip = trip;
		return this;
	}
	
	
	/**
	 * Updates name of cave from station is in, if different from trip.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFromCave} and returns the new value for {@code overrideFromCave}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code overrideFromCave} is unchanged, or a copy with the updated {@code overrideFromCave}.
	 */
	public MutableSurveyRow updateOverrideFromCave(Function<String, String> updater) {
		return setOverrideFromCave(updater.apply(data.overrideFromCave));
	}
	
	/**
	 * Updates from station name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code fromStation} and returns the new value for {@code fromStation}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code fromStation} is unchanged, or a copy with the updated {@code fromStation}.
	 */
	public MutableSurveyRow updateFromStation(Function<String, String> updater) {
		return setFromStation(updater.apply(data.fromStation));
	}
	
	/**
	 * Updates name of cave of to station is in, if different to trip.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideToCave} and returns the new value for {@code overrideToCave}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code overrideToCave} is unchanged, or a copy with the updated {@code overrideToCave}.
	 */
	public MutableSurveyRow updateOverrideToCave(Function<String, String> updater) {
		return setOverrideToCave(updater.apply(data.overrideToCave));
	}
	
	/**
	 * Updates to station name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code toStation} and returns the new value for {@code toStation}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code toStation} is unchanged, or a copy with the updated {@code toStation}.
	 */
	public MutableSurveyRow updateToStation(Function<String, String> updater) {
		return setToStation(updater.apply(data.toStation));
	}
	
	/**
	 * Updates distance between from and to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distance} and returns the new value for {@code distance}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code distance} is unchanged, or a copy with the updated {@code distance}.
	 */
	public MutableSurveyRow updateDistance(Function<String, String> updater) {
		return setDistance(updater.apply(data.distance));
	}
	
	/**
	 * Updates azimuth toward to station at from station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontAzimuth} and returns the new value for {@code frontAzimuth}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code frontAzimuth} is unchanged, or a copy with the updated {@code frontAzimuth}.
	 */
	public MutableSurveyRow updateFrontAzimuth(Function<String, String> updater) {
		return setFrontAzimuth(updater.apply(data.frontAzimuth));
	}
	
	/**
	 * Updates azimuth toward from station at to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuth} and returns the new value for {@code backAzimuth}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code backAzimuth} is unchanged, or a copy with the updated {@code backAzimuth}.
	 */
	public MutableSurveyRow updateBackAzimuth(Function<String, String> updater) {
		return setBackAzimuth(updater.apply(data.backAzimuth));
	}
	
	/**
	 * Updates inclination toward to station at from station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontInclination} and returns the new value for {@code frontInclination}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code frontInclination} is unchanged, or a copy with the updated {@code frontInclination}.
	 */
	public MutableSurveyRow updateFrontInclination(Function<String, String> updater) {
		return setFrontInclination(updater.apply(data.frontInclination));
	}
	
	/**
	 * Updates inclination toward from station at to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclination} and returns the new value for {@code backInclination}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code backInclination} is unchanged, or a copy with the updated {@code backInclination}.
	 */
	public MutableSurveyRow updateBackInclination(Function<String, String> updater) {
		return setBackInclination(updater.apply(data.backInclination));
	}
	
	/**
	 * Updates distance between from station and left wall.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code left} and returns the new value for {@code left}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code left} is unchanged, or a copy with the updated {@code left}.
	 */
	public MutableSurveyRow updateLeft(Function<String, String> updater) {
		return setLeft(updater.apply(data.left));
	}
	
	/**
	 * Updates distance between from station and right wall.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code right} and returns the new value for {@code right}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code right} is unchanged, or a copy with the updated {@code right}.
	 */
	public MutableSurveyRow updateRight(Function<String, String> updater) {
		return setRight(updater.apply(data.right));
	}
	
	/**
	 * Updates distance between from station and ceiling.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code up} and returns the new value for {@code up}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code up} is unchanged, or a copy with the updated {@code up}.
	 */
	public MutableSurveyRow updateUp(Function<String, String> updater) {
		return setUp(updater.apply(data.up));
	}
	
	/**
	 * Updates distance between from station and floor.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code down} and returns the new value for {@code down}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code down} is unchanged, or a copy with the updated {@code down}.
	 */
	public MutableSurveyRow updateDown(Function<String, String> updater) {
		return setDown(updater.apply(data.down));
	}
	
	/**
	 * Updates distance north relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code northing} and returns the new value for {@code northing}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code northing} is unchanged, or a copy with the updated {@code northing}.
	 */
	public MutableSurveyRow updateNorthing(Function<String, String> updater) {
		return setNorthing(updater.apply(data.northing));
	}
	
	/**
	 * Updates distance east relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code easting} and returns the new value for {@code easting}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code easting} is unchanged, or a copy with the updated {@code easting}.
	 */
	public MutableSurveyRow updateEasting(Function<String, String> updater) {
		return setEasting(updater.apply(data.easting));
	}
	
	/**
	 * Updates distance east relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code elevation} and returns the new value for {@code elevation}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code elevation} is unchanged, or a copy with the updated {@code elevation}.
	 */
	public MutableSurveyRow updateElevation(Function<String, String> updater) {
		return setElevation(updater.apply(data.elevation));
	}
	
	/**
	 * Updates any user comment.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code comment} and returns the new value for {@code comment}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code comment} is unchanged, or a copy with the updated {@code comment}.
	 */
	public MutableSurveyRow updateComment(Function<String, String> updater) {
		return setComment(updater.apply(data.comment));
	}
	
	/**
	 * Updates trip this row belongs to.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code trip} and returns the new value for {@code trip}.
	 * 
	 * @return this {@code MutableSurveyRow} if {@code trip} is unchanged, or a copy with the updated {@code trip}.
	 */
	public MutableSurveyRow updateTrip(Function<SurveyTrip, SurveyTrip> updater) {
		return setTrip(updater.apply(data.trip));
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
		if (obj instanceof SurveyRow) return ((SurveyRow) obj).dataEquals(data);
		if (obj instanceof MutableSurveyRow) return ((MutableSurveyRow) obj).dataEquals(data);
		return false;
	}
	
	
	public MutableSurveyRow ensureTrip() {
		if (data.trip == null) setTrip(new SurveyTrip());
		return this;
	}
	
}
