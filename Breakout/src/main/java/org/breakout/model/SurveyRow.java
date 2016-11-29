/**
 * Generated from {@code SurveyRow.record.js} by java-record-generator on 11/29/2016, 1:16:50 AM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */
 
package org.breakout.model;

import java.util.List;
import static org.andork.util.JavaScript.or;
import org.andork.model.Property;
import java.util.function.Consumer;
import java.util.Objects;
import org.andork.model.DefaultProperty;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 *
 */
public final class SurveyRow {
	
	public static final class Properties {
		public static <V> DefaultProperty<SurveyRow, V> create(
				String name, Class<? super V> valueClass,
				Function<? super SurveyRow, ? extends V> getter, 
				BiConsumer<MutableSurveyRow, ? super V> setter) {
			return new DefaultProperty<SurveyRow, V>(
				name, valueClass, getter, (m, v) -> {
					return m.withMutations(m2 -> setter.accept(m2, v));
				}
			);
		}
		
		
		/**
		 * name of cave from station is in, if different from trip
		 */
		public static final DefaultProperty<SurveyRow, String> overrideFromCave = create(
			"overrideFromCave", String.class,
			r -> r.getOverrideFromCave(),
			(m, v) -> m.setOverrideFromCave(v)
		);
		

		/**
		 * from station name
		 */
		public static final DefaultProperty<SurveyRow, String> fromStation = create(
			"fromStation", String.class,
			r -> r.getFromStation(),
			(m, v) -> m.setFromStation(v)
		);
		

		/**
		 * name of cave of to station is in, if different to trip
		 */
		public static final DefaultProperty<SurveyRow, String> overrideToCave = create(
			"overrideToCave", String.class,
			r -> r.getOverrideToCave(),
			(m, v) -> m.setOverrideToCave(v)
		);
		

		/**
		 * to station name
		 */
		public static final DefaultProperty<SurveyRow, String> toStation = create(
			"toStation", String.class,
			r -> r.getToStation(),
			(m, v) -> m.setToStation(v)
		);
		

		/**
		 * distance between from and to station
		 */
		public static final DefaultProperty<SurveyRow, String> distance = create(
			"distance", String.class,
			r -> r.getDistance(),
			(m, v) -> m.setDistance(v)
		);
		

		/**
		 * azimuth toward to station at from station
		 */
		public static final DefaultProperty<SurveyRow, String> frontAzimuth = create(
			"frontAzimuth", String.class,
			r -> r.getFrontAzimuth(),
			(m, v) -> m.setFrontAzimuth(v)
		);
		

		/**
		 * azimuth toward from station at to station
		 */
		public static final DefaultProperty<SurveyRow, String> backAzimuth = create(
			"backAzimuth", String.class,
			r -> r.getBackAzimuth(),
			(m, v) -> m.setBackAzimuth(v)
		);
		

		/**
		 * inclination toward to station at from station
		 */
		public static final DefaultProperty<SurveyRow, String> frontInclination = create(
			"frontInclination", String.class,
			r -> r.getFrontInclination(),
			(m, v) -> m.setFrontInclination(v)
		);
		

		/**
		 * inclination toward from station at to station
		 */
		public static final DefaultProperty<SurveyRow, String> backInclination = create(
			"backInclination", String.class,
			r -> r.getBackInclination(),
			(m, v) -> m.setBackInclination(v)
		);
		

		/**
		 * distance between from station and left wall
		 */
		public static final DefaultProperty<SurveyRow, String> left = create(
			"left", String.class,
			r -> r.getLeft(),
			(m, v) -> m.setLeft(v)
		);
		

		/**
		 * distance between from station and right wall
		 */
		public static final DefaultProperty<SurveyRow, String> right = create(
			"right", String.class,
			r -> r.getRight(),
			(m, v) -> m.setRight(v)
		);
		

		/**
		 * distance between from station and ceiling
		 */
		public static final DefaultProperty<SurveyRow, String> up = create(
			"up", String.class,
			r -> r.getUp(),
			(m, v) -> m.setUp(v)
		);
		

		/**
		 * distance between from station and floor
		 */
		public static final DefaultProperty<SurveyRow, String> down = create(
			"down", String.class,
			r -> r.getDown(),
			(m, v) -> m.setDown(v)
		);
		

		/**
		 * distance north relative to coordinate origin
		 */
		public static final DefaultProperty<SurveyRow, String> northing = create(
			"northing", String.class,
			r -> r.getNorthing(),
			(m, v) -> m.setNorthing(v)
		);
		

		/**
		 * distance east relative to coordinate origin
		 */
		public static final DefaultProperty<SurveyRow, String> easting = create(
			"easting", String.class,
			r -> r.getEasting(),
			(m, v) -> m.setEasting(v)
		);
		

		/**
		 * distance east relative to coordinate origin
		 */
		public static final DefaultProperty<SurveyRow, String> elevation = create(
			"elevation", String.class,
			r -> r.getElevation(),
			(m, v) -> m.setElevation(v)
		);
		

		/**
		 * any user comment
		 */
		public static final DefaultProperty<SurveyRow, String> comment = create(
			"comment", String.class,
			r -> r.getComment(),
			(m, v) -> m.setComment(v)
		);
		

		/**
		 * trip this row belongs to
		 */
		public static final DefaultProperty<SurveyRow, SurveyTrip> trip = create(
			"trip", SurveyTrip.class,
			r -> r.getTrip(),
			(m, v) -> m.setTrip(v)
		);
		

		public static <V> DefaultProperty<SurveyRow, V> createTripProperty(
				String name, Class<? super V> valueClass,
				Property<SurveyTrip, V> tripProperty) {
			return new DefaultProperty<SurveyRow, V>(name, valueClass,
				r -> r.getTrip() == null ? null : tripProperty.get(r.getTrip()),
				(row, v) -> {
					return row.withMutations(r -> r.updateTrip(trip -> {
						return tripProperty.set(trip == null ? new SurveyTrip() : trip, v);
					}));
				}
			);
		}

		public static DefaultProperty<SurveyRow, String> fromCave = create(
			"fromCave", String.class,
			r -> r.getFromCave(),
			(r, fromCave) -> r.setOverrideFromCave(fromCave)
		);
		public static DefaultProperty<SurveyRow, String> toCave = create(
			"toCave", String.class,
			r -> r.getToCave(),
			(r, toCave) -> r.setOverrideToCave(toCave)
		);
		public static DefaultProperty<SurveyRow, String> tripName = createTripProperty(
			"tripName", String.class, SurveyTrip.Properties.name);
		public static DefaultProperty<SurveyRow, String> date = createTripProperty(
			"date", String.class, SurveyTrip.Properties.date);
		public static DefaultProperty<SurveyRow, List<String>> surveyors = createTripProperty(
			"surveyors", List.class, SurveyTrip.Properties.surveyors);
		public static DefaultProperty<SurveyRow, String> surveyNotes = createTripProperty(
			"surveyNotes", String.class, SurveyTrip.Properties.surveyNotes);
	
	}
	
	static final class Data implements Cloneable {
		static final Data initial = new Data();
		
		
		/**
		 * name of cave from station is in, if different from trip.
		 */
		String overrideFromCave;
	
		/**
		 * from station name.
		 */
		String fromStation;
	
		/**
		 * name of cave of to station is in, if different to trip.
		 */
		String overrideToCave;
	
		/**
		 * to station name.
		 */
		String toStation;
	
		/**
		 * distance between from and to station.
		 */
		String distance;
	
		/**
		 * azimuth toward to station at from station.
		 */
		String frontAzimuth;
	
		/**
		 * azimuth toward from station at to station.
		 */
		String backAzimuth;
	
		/**
		 * inclination toward to station at from station.
		 */
		String frontInclination;
	
		/**
		 * inclination toward from station at to station.
		 */
		String backInclination;
	
		/**
		 * distance between from station and left wall.
		 */
		String left;
	
		/**
		 * distance between from station and right wall.
		 */
		String right;
	
		/**
		 * distance between from station and ceiling.
		 */
		String up;
	
		/**
		 * distance between from station and floor.
		 */
		String down;
	
		/**
		 * distance north relative to coordinate origin.
		 */
		String northing;
	
		/**
		 * distance east relative to coordinate origin.
		 */
		String easting;
	
		/**
		 * distance east relative to coordinate origin.
		 */
		String elevation;
	
		/**
		 * any user comment.
		 */
		String comment;
	
		/**
		 * trip this row belongs to.
		 */
		SurveyTrip trip;
	
		@Override
		public Data clone() {
			try {
				return (Data) super.clone(); 
			} catch (Exception e) {
				// should not happen
				throw new RuntimeException(e);
			} 
		}
		
		
		@Override
		public int hashCode() {
			int prime = 31;
			int result = 0;
			result = prime * result + Objects.hashCode(overrideFromCave);
			result = prime * result + Objects.hashCode(fromStation);
			result = prime * result + Objects.hashCode(overrideToCave);
			result = prime * result + Objects.hashCode(toStation);
			result = prime * result + Objects.hashCode(distance);
			result = prime * result + Objects.hashCode(frontAzimuth);
			result = prime * result + Objects.hashCode(backAzimuth);
			result = prime * result + Objects.hashCode(frontInclination);
			result = prime * result + Objects.hashCode(backInclination);
			result = prime * result + Objects.hashCode(left);
			result = prime * result + Objects.hashCode(right);
			result = prime * result + Objects.hashCode(up);
			result = prime * result + Objects.hashCode(down);
			result = prime * result + Objects.hashCode(northing);
			result = prime * result + Objects.hashCode(easting);
			result = prime * result + Objects.hashCode(elevation);
			result = prime * result + Objects.hashCode(comment);
			result = prime * result + Objects.hashCode(trip);
			return result;
		}
	
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Data other = (Data) obj;
			if (!Objects.equals(overrideFromCave, other.overrideFromCave)) return false;
			if (!Objects.equals(fromStation, other.fromStation)) return false;
			if (!Objects.equals(overrideToCave, other.overrideToCave)) return false;
			if (!Objects.equals(toStation, other.toStation)) return false;
			if (!Objects.equals(distance, other.distance)) return false;
			if (!Objects.equals(frontAzimuth, other.frontAzimuth)) return false;
			if (!Objects.equals(backAzimuth, other.backAzimuth)) return false;
			if (!Objects.equals(frontInclination, other.frontInclination)) return false;
			if (!Objects.equals(backInclination, other.backInclination)) return false;
			if (!Objects.equals(left, other.left)) return false;
			if (!Objects.equals(right, other.right)) return false;
			if (!Objects.equals(up, other.up)) return false;
			if (!Objects.equals(down, other.down)) return false;
			if (!Objects.equals(northing, other.northing)) return false;
			if (!Objects.equals(easting, other.easting)) return false;
			if (!Objects.equals(elevation, other.elevation)) return false;
			if (!Objects.equals(comment, other.comment)) return false;
			if (!Objects.equals(trip, other.trip)) return false;
			return true;
		}
	
	}
 
	private volatile Data data;
	
	SurveyRow(Data data) {
		this.data = data;
	}
	
	public SurveyRow() {
		this(Data.initial);
	}
	
	/**
	 * @param mutator a {@link Consumer} that applies mutations to this {@code SurveyRow}.
	 *
	 * @return a copy of this {@code SurveyRow} with the given mutations applied.
	 */
	public SurveyRow withMutations(Consumer<MutableSurveyRow> mutator) {
		MutableSurveyRow mutable = toMutable();
		mutator.accept(mutable);
		return mutable.dataIs(data) ? this : mutable.toImmutable();
	}
	
	/**
	 * @return a mutable copy of this {@code SurveyRow}.
	 */
	public MutableSurveyRow toMutable() {
		return new MutableSurveyRow(data);
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
	 * @return this {@code SurveyRow} if {@code overrideFromCave} is unchanged, or a copy with the new {@code overrideFromCave}.
	 */
	public SurveyRow setOverrideFromCave(String overrideFromCave) {
		if (data.overrideFromCave == overrideFromCave) return this;
		return toMutable().setOverrideFromCave(overrideFromCave).toImmutable();
	}
	
	/**
	 * Sets from station name.
	 *
	 * @param fromStation - the new value for from station name
	 * 
	 * @return this {@code SurveyRow} if {@code fromStation} is unchanged, or a copy with the new {@code fromStation}.
	 */
	public SurveyRow setFromStation(String fromStation) {
		if (data.fromStation == fromStation) return this;
		return toMutable().setFromStation(fromStation).toImmutable();
	}
	
	/**
	 * Sets name of cave of to station is in, if different to trip.
	 *
	 * @param overrideToCave - the new value for name of cave of to station is in, if different to trip
	 * 
	 * @return this {@code SurveyRow} if {@code overrideToCave} is unchanged, or a copy with the new {@code overrideToCave}.
	 */
	public SurveyRow setOverrideToCave(String overrideToCave) {
		if (data.overrideToCave == overrideToCave) return this;
		return toMutable().setOverrideToCave(overrideToCave).toImmutable();
	}
	
	/**
	 * Sets to station name.
	 *
	 * @param toStation - the new value for to station name
	 * 
	 * @return this {@code SurveyRow} if {@code toStation} is unchanged, or a copy with the new {@code toStation}.
	 */
	public SurveyRow setToStation(String toStation) {
		if (data.toStation == toStation) return this;
		return toMutable().setToStation(toStation).toImmutable();
	}
	
	/**
	 * Sets distance between from and to station.
	 *
	 * @param distance - the new value for distance between from and to station
	 * 
	 * @return this {@code SurveyRow} if {@code distance} is unchanged, or a copy with the new {@code distance}.
	 */
	public SurveyRow setDistance(String distance) {
		if (data.distance == distance) return this;
		return toMutable().setDistance(distance).toImmutable();
	}
	
	/**
	 * Sets azimuth toward to station at from station.
	 *
	 * @param frontAzimuth - the new value for azimuth toward to station at from station
	 * 
	 * @return this {@code SurveyRow} if {@code frontAzimuth} is unchanged, or a copy with the new {@code frontAzimuth}.
	 */
	public SurveyRow setFrontAzimuth(String frontAzimuth) {
		if (data.frontAzimuth == frontAzimuth) return this;
		return toMutable().setFrontAzimuth(frontAzimuth).toImmutable();
	}
	
	/**
	 * Sets azimuth toward from station at to station.
	 *
	 * @param backAzimuth - the new value for azimuth toward from station at to station
	 * 
	 * @return this {@code SurveyRow} if {@code backAzimuth} is unchanged, or a copy with the new {@code backAzimuth}.
	 */
	public SurveyRow setBackAzimuth(String backAzimuth) {
		if (data.backAzimuth == backAzimuth) return this;
		return toMutable().setBackAzimuth(backAzimuth).toImmutable();
	}
	
	/**
	 * Sets inclination toward to station at from station.
	 *
	 * @param frontInclination - the new value for inclination toward to station at from station
	 * 
	 * @return this {@code SurveyRow} if {@code frontInclination} is unchanged, or a copy with the new {@code frontInclination}.
	 */
	public SurveyRow setFrontInclination(String frontInclination) {
		if (data.frontInclination == frontInclination) return this;
		return toMutable().setFrontInclination(frontInclination).toImmutable();
	}
	
	/**
	 * Sets inclination toward from station at to station.
	 *
	 * @param backInclination - the new value for inclination toward from station at to station
	 * 
	 * @return this {@code SurveyRow} if {@code backInclination} is unchanged, or a copy with the new {@code backInclination}.
	 */
	public SurveyRow setBackInclination(String backInclination) {
		if (data.backInclination == backInclination) return this;
		return toMutable().setBackInclination(backInclination).toImmutable();
	}
	
	/**
	 * Sets distance between from station and left wall.
	 *
	 * @param left - the new value for distance between from station and left wall
	 * 
	 * @return this {@code SurveyRow} if {@code left} is unchanged, or a copy with the new {@code left}.
	 */
	public SurveyRow setLeft(String left) {
		if (data.left == left) return this;
		return toMutable().setLeft(left).toImmutable();
	}
	
	/**
	 * Sets distance between from station and right wall.
	 *
	 * @param right - the new value for distance between from station and right wall
	 * 
	 * @return this {@code SurveyRow} if {@code right} is unchanged, or a copy with the new {@code right}.
	 */
	public SurveyRow setRight(String right) {
		if (data.right == right) return this;
		return toMutable().setRight(right).toImmutable();
	}
	
	/**
	 * Sets distance between from station and ceiling.
	 *
	 * @param up - the new value for distance between from station and ceiling
	 * 
	 * @return this {@code SurveyRow} if {@code up} is unchanged, or a copy with the new {@code up}.
	 */
	public SurveyRow setUp(String up) {
		if (data.up == up) return this;
		return toMutable().setUp(up).toImmutable();
	}
	
	/**
	 * Sets distance between from station and floor.
	 *
	 * @param down - the new value for distance between from station and floor
	 * 
	 * @return this {@code SurveyRow} if {@code down} is unchanged, or a copy with the new {@code down}.
	 */
	public SurveyRow setDown(String down) {
		if (data.down == down) return this;
		return toMutable().setDown(down).toImmutable();
	}
	
	/**
	 * Sets distance north relative to coordinate origin.
	 *
	 * @param northing - the new value for distance north relative to coordinate origin
	 * 
	 * @return this {@code SurveyRow} if {@code northing} is unchanged, or a copy with the new {@code northing}.
	 */
	public SurveyRow setNorthing(String northing) {
		if (data.northing == northing) return this;
		return toMutable().setNorthing(northing).toImmutable();
	}
	
	/**
	 * Sets distance east relative to coordinate origin.
	 *
	 * @param easting - the new value for distance east relative to coordinate origin
	 * 
	 * @return this {@code SurveyRow} if {@code easting} is unchanged, or a copy with the new {@code easting}.
	 */
	public SurveyRow setEasting(String easting) {
		if (data.easting == easting) return this;
		return toMutable().setEasting(easting).toImmutable();
	}
	
	/**
	 * Sets distance east relative to coordinate origin.
	 *
	 * @param elevation - the new value for distance east relative to coordinate origin
	 * 
	 * @return this {@code SurveyRow} if {@code elevation} is unchanged, or a copy with the new {@code elevation}.
	 */
	public SurveyRow setElevation(String elevation) {
		if (data.elevation == elevation) return this;
		return toMutable().setElevation(elevation).toImmutable();
	}
	
	/**
	 * Sets any user comment.
	 *
	 * @param comment - the new value for any user comment
	 * 
	 * @return this {@code SurveyRow} if {@code comment} is unchanged, or a copy with the new {@code comment}.
	 */
	public SurveyRow setComment(String comment) {
		if (data.comment == comment) return this;
		return toMutable().setComment(comment).toImmutable();
	}
	
	/**
	 * Sets trip this row belongs to.
	 *
	 * @param trip - the new value for trip this row belongs to
	 * 
	 * @return this {@code SurveyRow} if {@code trip} is unchanged, or a copy with the new {@code trip}.
	 */
	public SurveyRow setTrip(SurveyTrip trip) {
		if (data.trip == trip) return this;
		return toMutable().setTrip(trip).toImmutable();
	}
	
	
	/**
	 * Updates name of cave from station is in, if different from trip.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFromCave} and returns the new value for {@code overrideFromCave}.
	 * 
	 * @return this {@code SurveyRow} if {@code overrideFromCave} is unchanged, or a copy with the updated {@code overrideFromCave}.
	 */
	public SurveyRow updateOverrideFromCave(Function<String, String> updater) {
		return setOverrideFromCave(updater.apply(data.overrideFromCave));
	}
	
	/**
	 * Updates from station name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code fromStation} and returns the new value for {@code fromStation}.
	 * 
	 * @return this {@code SurveyRow} if {@code fromStation} is unchanged, or a copy with the updated {@code fromStation}.
	 */
	public SurveyRow updateFromStation(Function<String, String> updater) {
		return setFromStation(updater.apply(data.fromStation));
	}
	
	/**
	 * Updates name of cave of to station is in, if different to trip.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideToCave} and returns the new value for {@code overrideToCave}.
	 * 
	 * @return this {@code SurveyRow} if {@code overrideToCave} is unchanged, or a copy with the updated {@code overrideToCave}.
	 */
	public SurveyRow updateOverrideToCave(Function<String, String> updater) {
		return setOverrideToCave(updater.apply(data.overrideToCave));
	}
	
	/**
	 * Updates to station name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code toStation} and returns the new value for {@code toStation}.
	 * 
	 * @return this {@code SurveyRow} if {@code toStation} is unchanged, or a copy with the updated {@code toStation}.
	 */
	public SurveyRow updateToStation(Function<String, String> updater) {
		return setToStation(updater.apply(data.toStation));
	}
	
	/**
	 * Updates distance between from and to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distance} and returns the new value for {@code distance}.
	 * 
	 * @return this {@code SurveyRow} if {@code distance} is unchanged, or a copy with the updated {@code distance}.
	 */
	public SurveyRow updateDistance(Function<String, String> updater) {
		return setDistance(updater.apply(data.distance));
	}
	
	/**
	 * Updates azimuth toward to station at from station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontAzimuth} and returns the new value for {@code frontAzimuth}.
	 * 
	 * @return this {@code SurveyRow} if {@code frontAzimuth} is unchanged, or a copy with the updated {@code frontAzimuth}.
	 */
	public SurveyRow updateFrontAzimuth(Function<String, String> updater) {
		return setFrontAzimuth(updater.apply(data.frontAzimuth));
	}
	
	/**
	 * Updates azimuth toward from station at to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuth} and returns the new value for {@code backAzimuth}.
	 * 
	 * @return this {@code SurveyRow} if {@code backAzimuth} is unchanged, or a copy with the updated {@code backAzimuth}.
	 */
	public SurveyRow updateBackAzimuth(Function<String, String> updater) {
		return setBackAzimuth(updater.apply(data.backAzimuth));
	}
	
	/**
	 * Updates inclination toward to station at from station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontInclination} and returns the new value for {@code frontInclination}.
	 * 
	 * @return this {@code SurveyRow} if {@code frontInclination} is unchanged, or a copy with the updated {@code frontInclination}.
	 */
	public SurveyRow updateFrontInclination(Function<String, String> updater) {
		return setFrontInclination(updater.apply(data.frontInclination));
	}
	
	/**
	 * Updates inclination toward from station at to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclination} and returns the new value for {@code backInclination}.
	 * 
	 * @return this {@code SurveyRow} if {@code backInclination} is unchanged, or a copy with the updated {@code backInclination}.
	 */
	public SurveyRow updateBackInclination(Function<String, String> updater) {
		return setBackInclination(updater.apply(data.backInclination));
	}
	
	/**
	 * Updates distance between from station and left wall.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code left} and returns the new value for {@code left}.
	 * 
	 * @return this {@code SurveyRow} if {@code left} is unchanged, or a copy with the updated {@code left}.
	 */
	public SurveyRow updateLeft(Function<String, String> updater) {
		return setLeft(updater.apply(data.left));
	}
	
	/**
	 * Updates distance between from station and right wall.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code right} and returns the new value for {@code right}.
	 * 
	 * @return this {@code SurveyRow} if {@code right} is unchanged, or a copy with the updated {@code right}.
	 */
	public SurveyRow updateRight(Function<String, String> updater) {
		return setRight(updater.apply(data.right));
	}
	
	/**
	 * Updates distance between from station and ceiling.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code up} and returns the new value for {@code up}.
	 * 
	 * @return this {@code SurveyRow} if {@code up} is unchanged, or a copy with the updated {@code up}.
	 */
	public SurveyRow updateUp(Function<String, String> updater) {
		return setUp(updater.apply(data.up));
	}
	
	/**
	 * Updates distance between from station and floor.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code down} and returns the new value for {@code down}.
	 * 
	 * @return this {@code SurveyRow} if {@code down} is unchanged, or a copy with the updated {@code down}.
	 */
	public SurveyRow updateDown(Function<String, String> updater) {
		return setDown(updater.apply(data.down));
	}
	
	/**
	 * Updates distance north relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code northing} and returns the new value for {@code northing}.
	 * 
	 * @return this {@code SurveyRow} if {@code northing} is unchanged, or a copy with the updated {@code northing}.
	 */
	public SurveyRow updateNorthing(Function<String, String> updater) {
		return setNorthing(updater.apply(data.northing));
	}
	
	/**
	 * Updates distance east relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code easting} and returns the new value for {@code easting}.
	 * 
	 * @return this {@code SurveyRow} if {@code easting} is unchanged, or a copy with the updated {@code easting}.
	 */
	public SurveyRow updateEasting(Function<String, String> updater) {
		return setEasting(updater.apply(data.easting));
	}
	
	/**
	 * Updates distance east relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code elevation} and returns the new value for {@code elevation}.
	 * 
	 * @return this {@code SurveyRow} if {@code elevation} is unchanged, or a copy with the updated {@code elevation}.
	 */
	public SurveyRow updateElevation(Function<String, String> updater) {
		return setElevation(updater.apply(data.elevation));
	}
	
	/**
	 * Updates any user comment.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code comment} and returns the new value for {@code comment}.
	 * 
	 * @return this {@code SurveyRow} if {@code comment} is unchanged, or a copy with the updated {@code comment}.
	 */
	public SurveyRow updateComment(Function<String, String> updater) {
		return setComment(updater.apply(data.comment));
	}
	
	/**
	 * Updates trip this row belongs to.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code trip} and returns the new value for {@code trip}.
	 * 
	 * @return this {@code SurveyRow} if {@code trip} is unchanged, or a copy with the updated {@code trip}.
	 */
	public SurveyRow updateTrip(Function<SurveyTrip, SurveyTrip> updater) {
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
	
	
	public String getFromCave() {
		return or(data.overrideFromCave, data.trip == null ? null : data.trip.getCave());
	}

	public String getToCave() {
		return or(data.overrideToCave, data.trip == null ? null : data.trip.getCave());
	}
	
}
