/**
 * Generated from {@code SurveyRow.record.js} by java-record-generator on 8/25/2019, 1:37:49 AM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */

package org.breakout.model.raw;

import java.util.List;
import static org.andork.util.JavaScript.or;
import org.andork.model.Property;
import com.github.krukow.clj_ds.PersistentVector;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Objects;
import com.github.krukow.clj_lang.PersistentHashMap;
import org.andork.model.DefaultProperty;
import java.util.function.BiConsumer;

/**
 *
 */
public final class SurveyRow {
	
	/**
	 * Key for name of cave from station is in, if different from trip.
	 */
	public static final String overrideFromCave = "overrideFromCave";
	
	/**
	 * Key for from station name.
	 */
	public static final String fromStation = "fromStation";
	
	/**
	 * Key for name of cave of to station is in, if different to trip.
	 */
	public static final String overrideToCave = "overrideToCave";
	
	/**
	 * Key for to station name.
	 */
	public static final String toStation = "toStation";
	
	/**
	 * Key for distance between from and to station.
	 */
	public static final String distance = "distance";
	
	/**
	 * Key for azimuth toward to station at from station.
	 */
	public static final String frontAzimuth = "frontAzimuth";
	
	/**
	 * Key for azimuth toward from station at to station.
	 */
	public static final String backAzimuth = "backAzimuth";
	
	/**
	 * Key for inclination toward to station at from station.
	 */
	public static final String frontInclination = "frontInclination";
	
	/**
	 * Key for inclination toward from station at to station.
	 */
	public static final String backInclination = "backInclination";
	
	/**
	 * Key for distance between from station and left wall.
	 */
	public static final String left = "left";
	
	/**
	 * Key for distance between from station and right wall.
	 */
	public static final String right = "right";
	
	/**
	 * Key for distance between from station and ceiling.
	 */
	public static final String up = "up";
	
	/**
	 * Key for distance between from station and floor.
	 */
	public static final String down = "down";
	
	/**
	 * Key for distance north relative to coordinate origin.
	 */
	public static final String northing = "northing";
	
	/**
	 * Key for from station's latitude.
	 */
	public static final String latitude = "latitude";
	
	/**
	 * Key for from station's longitude.
	 */
	public static final String longitude = "longitude";
	
	/**
	 * Key for from station's distance east relative to coordinate origin.
	 */
	public static final String easting = "easting";
	
	/**
	 * Key for from station's distance east relative to coordinate origin.
	 */
	public static final String elevation = "elevation";
	
	/**
	 * Key for any user comment.
	 */
	public static final String comment = "comment";
	
	/**
	 * Key for attached files (if one they can't be associated with the entire trip).
	 */
	public static final String overrideAttachedFiles = "overrideAttachedFiles";
	
	/**
	 * Key for trip this row belongs to.
	 */
	public static final String trip = "trip";
	
	/**
	 * Key for whether to exclude this shot from the total cave length.
	 */
	public static final String excludeDistance = "excludeDistance";
	
	/**
	 * Key for whether to exclude this shot from plotting.
	 */
	public static final String excludeFromPlotting = "excludeFromPlotting";
	
	
	static final PersistentHashMap<String, Object> initialData = PersistentHashMap.emptyMap();
	
	
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
			r -> r.get(SurveyRow.overrideFromCave),
			(m, v) -> m.set(SurveyRow.overrideFromCave, v)
		);
		

		/**
		 * from station name
		 */
		public static final DefaultProperty<SurveyRow, String> fromStation = create(
			"fromStation", String.class,
			r -> r.get(SurveyRow.fromStation),
			(m, v) -> m.set(SurveyRow.fromStation, v)
		);
		

		/**
		 * name of cave of to station is in, if different to trip
		 */
		public static final DefaultProperty<SurveyRow, String> overrideToCave = create(
			"overrideToCave", String.class,
			r -> r.get(SurveyRow.overrideToCave),
			(m, v) -> m.set(SurveyRow.overrideToCave, v)
		);
		

		/**
		 * to station name
		 */
		public static final DefaultProperty<SurveyRow, String> toStation = create(
			"toStation", String.class,
			r -> r.get(SurveyRow.toStation),
			(m, v) -> m.set(SurveyRow.toStation, v)
		);
		

		/**
		 * distance between from and to station
		 */
		public static final DefaultProperty<SurveyRow, String> distance = create(
			"distance", String.class,
			r -> r.get(SurveyRow.distance),
			(m, v) -> m.set(SurveyRow.distance, v)
		);
		

		/**
		 * azimuth toward to station at from station
		 */
		public static final DefaultProperty<SurveyRow, String> frontAzimuth = create(
			"frontAzimuth", String.class,
			r -> r.get(SurveyRow.frontAzimuth),
			(m, v) -> m.set(SurveyRow.frontAzimuth, v)
		);
		

		/**
		 * azimuth toward from station at to station
		 */
		public static final DefaultProperty<SurveyRow, String> backAzimuth = create(
			"backAzimuth", String.class,
			r -> r.get(SurveyRow.backAzimuth),
			(m, v) -> m.set(SurveyRow.backAzimuth, v)
		);
		

		/**
		 * inclination toward to station at from station
		 */
		public static final DefaultProperty<SurveyRow, String> frontInclination = create(
			"frontInclination", String.class,
			r -> r.get(SurveyRow.frontInclination),
			(m, v) -> m.set(SurveyRow.frontInclination, v)
		);
		

		/**
		 * inclination toward from station at to station
		 */
		public static final DefaultProperty<SurveyRow, String> backInclination = create(
			"backInclination", String.class,
			r -> r.get(SurveyRow.backInclination),
			(m, v) -> m.set(SurveyRow.backInclination, v)
		);
		

		/**
		 * distance between from station and left wall
		 */
		public static final DefaultProperty<SurveyRow, String> left = create(
			"left", String.class,
			r -> r.get(SurveyRow.left),
			(m, v) -> m.set(SurveyRow.left, v)
		);
		

		/**
		 * distance between from station and right wall
		 */
		public static final DefaultProperty<SurveyRow, String> right = create(
			"right", String.class,
			r -> r.get(SurveyRow.right),
			(m, v) -> m.set(SurveyRow.right, v)
		);
		

		/**
		 * distance between from station and ceiling
		 */
		public static final DefaultProperty<SurveyRow, String> up = create(
			"up", String.class,
			r -> r.get(SurveyRow.up),
			(m, v) -> m.set(SurveyRow.up, v)
		);
		

		/**
		 * distance between from station and floor
		 */
		public static final DefaultProperty<SurveyRow, String> down = create(
			"down", String.class,
			r -> r.get(SurveyRow.down),
			(m, v) -> m.set(SurveyRow.down, v)
		);
		

		/**
		 * distance north relative to coordinate origin
		 */
		public static final DefaultProperty<SurveyRow, String> northing = create(
			"northing", String.class,
			r -> r.get(SurveyRow.northing),
			(m, v) -> m.set(SurveyRow.northing, v)
		);
		

		/**
		 * from station's latitude
		 */
		public static final DefaultProperty<SurveyRow, String> latitude = create(
			"latitude", String.class,
			r -> r.get(SurveyRow.latitude),
			(m, v) -> m.set(SurveyRow.latitude, v)
		);
		

		/**
		 * from station's longitude
		 */
		public static final DefaultProperty<SurveyRow, String> longitude = create(
			"longitude", String.class,
			r -> r.get(SurveyRow.longitude),
			(m, v) -> m.set(SurveyRow.longitude, v)
		);
		

		/**
		 * from station's distance east relative to coordinate origin
		 */
		public static final DefaultProperty<SurveyRow, String> easting = create(
			"easting", String.class,
			r -> r.get(SurveyRow.easting),
			(m, v) -> m.set(SurveyRow.easting, v)
		);
		

		/**
		 * from station's distance east relative to coordinate origin
		 */
		public static final DefaultProperty<SurveyRow, String> elevation = create(
			"elevation", String.class,
			r -> r.get(SurveyRow.elevation),
			(m, v) -> m.set(SurveyRow.elevation, v)
		);
		

		/**
		 * any user comment
		 */
		public static final DefaultProperty<SurveyRow, String> comment = create(
			"comment", String.class,
			r -> r.get(SurveyRow.comment),
			(m, v) -> m.set(SurveyRow.comment, v)
		);
		

		/**
		 * attached files (if one they can't be associated with the entire trip)
		 */
		public static final DefaultProperty<SurveyRow, PersistentVector<String>> overrideAttachedFiles = create(
			"overrideAttachedFiles", PersistentVector.class,
			r -> r.get(SurveyRow.overrideAttachedFiles),
			(m, v) -> m.set(SurveyRow.overrideAttachedFiles, v)
		);
		

		/**
		 * trip this row belongs to
		 */
		public static final DefaultProperty<SurveyRow, SurveyTrip> trip = create(
			"trip", SurveyTrip.class,
			r -> r.get(SurveyRow.trip),
			(m, v) -> m.set(SurveyRow.trip, v)
		);
		

		/**
		 * whether to exclude this shot from the total cave length
		 */
		public static final DefaultProperty<SurveyRow, Boolean> excludeDistance = create(
			"excludeDistance", Boolean.class,
			r -> r.get(SurveyRow.excludeDistance),
			(m, v) -> m.set(SurveyRow.excludeDistance, v)
		);
		

		/**
		 * whether to exclude this shot from plotting
		 */
		public static final DefaultProperty<SurveyRow, Boolean> excludeFromPlotting = create(
			"excludeFromPlotting", Boolean.class,
			r -> r.get(SurveyRow.excludeFromPlotting),
			(m, v) -> m.set(SurveyRow.excludeFromPlotting, v)
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
		public static DefaultProperty<SurveyRow, PersistentVector<String>> attachedFiles = create(
			"attachedFiles", PersistentVector.class,
			r -> r.getAttachedFiles(),
			(r, attachedFiles) -> r.setOverrideAttachedFiles(attachedFiles)
		);
	
	}
	
	

	private final PersistentHashMap<String, Object> data;

	SurveyRow(PersistentHashMap<String, Object> data) {
		this.data = data;
	}

	public SurveyRow() {
		this(initialData);
	}

	public MutableSurveyRow toMutable() {
		return new MutableSurveyRow(data);
	}

	public SurveyRow withMutations(Consumer<MutableSurveyRow> mutator) {
		MutableSurveyRow mutable = toMutable();
		mutator.accept(mutable);
		return mutable.dataEquals(data) ? this : mutable.toImmutable();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) data.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T defaultValue) {
		T result = (T) data.get(key);
		return result != null ? result : defaultValue;
	}

	public SurveyRow set(String key, Object newValue) {
		return withMutations(m -> m.set(key, newValue));
	}

	public <T> SurveyRow update(String key, Function<? super T, ? extends T> updater) {
		return set(key, updater.apply(get(key)));
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
		if (obj instanceof SurveyRow) {
			return ((SurveyRow) obj).data.equals(data);
		}
		if (obj instanceof MutableSurveyRow) {
			return ((MutableSurveyRow) obj).persist().equals(data);
		}
		return false;
	}

	
	/**
	 * @return name of cave from station is in, if different from trip.
	 */
	public String getOverrideFromCave() {
		return get(overrideFromCave);
	}
	
	/**
	 * @return from station name.
	 */
	public String getFromStation() {
		return get(fromStation);
	}
	
	/**
	 * @return name of cave of to station is in, if different to trip.
	 */
	public String getOverrideToCave() {
		return get(overrideToCave);
	}
	
	/**
	 * @return to station name.
	 */
	public String getToStation() {
		return get(toStation);
	}
	
	/**
	 * @return distance between from and to station.
	 */
	public String getDistance() {
		return get(distance);
	}
	
	/**
	 * @return azimuth toward to station at from station.
	 */
	public String getFrontAzimuth() {
		return get(frontAzimuth);
	}
	
	/**
	 * @return azimuth toward from station at to station.
	 */
	public String getBackAzimuth() {
		return get(backAzimuth);
	}
	
	/**
	 * @return inclination toward to station at from station.
	 */
	public String getFrontInclination() {
		return get(frontInclination);
	}
	
	/**
	 * @return inclination toward from station at to station.
	 */
	public String getBackInclination() {
		return get(backInclination);
	}
	
	/**
	 * @return distance between from station and left wall.
	 */
	public String getLeft() {
		return get(left);
	}
	
	/**
	 * @return distance between from station and right wall.
	 */
	public String getRight() {
		return get(right);
	}
	
	/**
	 * @return distance between from station and ceiling.
	 */
	public String getUp() {
		return get(up);
	}
	
	/**
	 * @return distance between from station and floor.
	 */
	public String getDown() {
		return get(down);
	}
	
	/**
	 * @return distance north relative to coordinate origin.
	 */
	public String getNorthing() {
		return get(northing);
	}
	
	/**
	 * @return from station's latitude.
	 */
	public String getLatitude() {
		return get(latitude);
	}
	
	/**
	 * @return from station's longitude.
	 */
	public String getLongitude() {
		return get(longitude);
	}
	
	/**
	 * @return from station's distance east relative to coordinate origin.
	 */
	public String getEasting() {
		return get(easting);
	}
	
	/**
	 * @return from station's distance east relative to coordinate origin.
	 */
	public String getElevation() {
		return get(elevation);
	}
	
	/**
	 * @return any user comment.
	 */
	public String getComment() {
		return get(comment);
	}
	
	/**
	 * @return attached files (if one they can't be associated with the entire trip).
	 */
	public PersistentVector<String> getOverrideAttachedFiles() {
		return get(overrideAttachedFiles);
	}
	
	/**
	 * @return trip this row belongs to.
	 */
	public SurveyTrip getTrip() {
		return get(trip);
	}
	
	/**
	 * @return whether to exclude this shot from the total cave length.
	 */
	public Boolean isExcludeDistance() {
		return get(excludeDistance, false);
	}
	
	/**
	 * @return whether to exclude this shot from plotting.
	 */
	public Boolean isExcludeFromPlotting() {
		return get(excludeFromPlotting, false);
	}
	
	
	/**
	 * Sets name of cave from station is in, if different from trip.
	 *
	 * @param overrideFromCave - the new value for name of cave from station is in, if different from trip
	 *
	 * @return this {@code SurveyRow} if {@code overrideFromCave} is unchanged, or a copy with the new {@code overrideFromCave}.
	 */
	public SurveyRow setOverrideFromCave(String overrideFromCave) {
		return set(SurveyRow.overrideFromCave, overrideFromCave);
	}
	
	/**
	 * Sets from station name.
	 *
	 * @param fromStation - the new value for from station name
	 *
	 * @return this {@code SurveyRow} if {@code fromStation} is unchanged, or a copy with the new {@code fromStation}.
	 */
	public SurveyRow setFromStation(String fromStation) {
		return set(SurveyRow.fromStation, fromStation);
	}
	
	/**
	 * Sets name of cave of to station is in, if different to trip.
	 *
	 * @param overrideToCave - the new value for name of cave of to station is in, if different to trip
	 *
	 * @return this {@code SurveyRow} if {@code overrideToCave} is unchanged, or a copy with the new {@code overrideToCave}.
	 */
	public SurveyRow setOverrideToCave(String overrideToCave) {
		return set(SurveyRow.overrideToCave, overrideToCave);
	}
	
	/**
	 * Sets to station name.
	 *
	 * @param toStation - the new value for to station name
	 *
	 * @return this {@code SurveyRow} if {@code toStation} is unchanged, or a copy with the new {@code toStation}.
	 */
	public SurveyRow setToStation(String toStation) {
		return set(SurveyRow.toStation, toStation);
	}
	
	/**
	 * Sets distance between from and to station.
	 *
	 * @param distance - the new value for distance between from and to station
	 *
	 * @return this {@code SurveyRow} if {@code distance} is unchanged, or a copy with the new {@code distance}.
	 */
	public SurveyRow setDistance(String distance) {
		return set(SurveyRow.distance, distance);
	}
	
	/**
	 * Sets azimuth toward to station at from station.
	 *
	 * @param frontAzimuth - the new value for azimuth toward to station at from station
	 *
	 * @return this {@code SurveyRow} if {@code frontAzimuth} is unchanged, or a copy with the new {@code frontAzimuth}.
	 */
	public SurveyRow setFrontAzimuth(String frontAzimuth) {
		return set(SurveyRow.frontAzimuth, frontAzimuth);
	}
	
	/**
	 * Sets azimuth toward from station at to station.
	 *
	 * @param backAzimuth - the new value for azimuth toward from station at to station
	 *
	 * @return this {@code SurveyRow} if {@code backAzimuth} is unchanged, or a copy with the new {@code backAzimuth}.
	 */
	public SurveyRow setBackAzimuth(String backAzimuth) {
		return set(SurveyRow.backAzimuth, backAzimuth);
	}
	
	/**
	 * Sets inclination toward to station at from station.
	 *
	 * @param frontInclination - the new value for inclination toward to station at from station
	 *
	 * @return this {@code SurveyRow} if {@code frontInclination} is unchanged, or a copy with the new {@code frontInclination}.
	 */
	public SurveyRow setFrontInclination(String frontInclination) {
		return set(SurveyRow.frontInclination, frontInclination);
	}
	
	/**
	 * Sets inclination toward from station at to station.
	 *
	 * @param backInclination - the new value for inclination toward from station at to station
	 *
	 * @return this {@code SurveyRow} if {@code backInclination} is unchanged, or a copy with the new {@code backInclination}.
	 */
	public SurveyRow setBackInclination(String backInclination) {
		return set(SurveyRow.backInclination, backInclination);
	}
	
	/**
	 * Sets distance between from station and left wall.
	 *
	 * @param left - the new value for distance between from station and left wall
	 *
	 * @return this {@code SurveyRow} if {@code left} is unchanged, or a copy with the new {@code left}.
	 */
	public SurveyRow setLeft(String left) {
		return set(SurveyRow.left, left);
	}
	
	/**
	 * Sets distance between from station and right wall.
	 *
	 * @param right - the new value for distance between from station and right wall
	 *
	 * @return this {@code SurveyRow} if {@code right} is unchanged, or a copy with the new {@code right}.
	 */
	public SurveyRow setRight(String right) {
		return set(SurveyRow.right, right);
	}
	
	/**
	 * Sets distance between from station and ceiling.
	 *
	 * @param up - the new value for distance between from station and ceiling
	 *
	 * @return this {@code SurveyRow} if {@code up} is unchanged, or a copy with the new {@code up}.
	 */
	public SurveyRow setUp(String up) {
		return set(SurveyRow.up, up);
	}
	
	/**
	 * Sets distance between from station and floor.
	 *
	 * @param down - the new value for distance between from station and floor
	 *
	 * @return this {@code SurveyRow} if {@code down} is unchanged, or a copy with the new {@code down}.
	 */
	public SurveyRow setDown(String down) {
		return set(SurveyRow.down, down);
	}
	
	/**
	 * Sets distance north relative to coordinate origin.
	 *
	 * @param northing - the new value for distance north relative to coordinate origin
	 *
	 * @return this {@code SurveyRow} if {@code northing} is unchanged, or a copy with the new {@code northing}.
	 */
	public SurveyRow setNorthing(String northing) {
		return set(SurveyRow.northing, northing);
	}
	
	/**
	 * Sets from station's latitude.
	 *
	 * @param latitude - the new value for from station's latitude
	 *
	 * @return this {@code SurveyRow} if {@code latitude} is unchanged, or a copy with the new {@code latitude}.
	 */
	public SurveyRow setLatitude(String latitude) {
		return set(SurveyRow.latitude, latitude);
	}
	
	/**
	 * Sets from station's longitude.
	 *
	 * @param longitude - the new value for from station's longitude
	 *
	 * @return this {@code SurveyRow} if {@code longitude} is unchanged, or a copy with the new {@code longitude}.
	 */
	public SurveyRow setLongitude(String longitude) {
		return set(SurveyRow.longitude, longitude);
	}
	
	/**
	 * Sets from station's distance east relative to coordinate origin.
	 *
	 * @param easting - the new value for from station's distance east relative to coordinate origin
	 *
	 * @return this {@code SurveyRow} if {@code easting} is unchanged, or a copy with the new {@code easting}.
	 */
	public SurveyRow setEasting(String easting) {
		return set(SurveyRow.easting, easting);
	}
	
	/**
	 * Sets from station's distance east relative to coordinate origin.
	 *
	 * @param elevation - the new value for from station's distance east relative to coordinate origin
	 *
	 * @return this {@code SurveyRow} if {@code elevation} is unchanged, or a copy with the new {@code elevation}.
	 */
	public SurveyRow setElevation(String elevation) {
		return set(SurveyRow.elevation, elevation);
	}
	
	/**
	 * Sets any user comment.
	 *
	 * @param comment - the new value for any user comment
	 *
	 * @return this {@code SurveyRow} if {@code comment} is unchanged, or a copy with the new {@code comment}.
	 */
	public SurveyRow setComment(String comment) {
		return set(SurveyRow.comment, comment);
	}
	
	/**
	 * Sets attached files (if one they can't be associated with the entire trip).
	 *
	 * @param overrideAttachedFiles - the new value for attached files (if one they can't be associated with the entire trip)
	 *
	 * @return this {@code SurveyRow} if {@code overrideAttachedFiles} is unchanged, or a copy with the new {@code overrideAttachedFiles}.
	 */
	public SurveyRow setOverrideAttachedFiles(PersistentVector<String> overrideAttachedFiles) {
		return set(SurveyRow.overrideAttachedFiles, overrideAttachedFiles);
	}
	
	/**
	 * Sets trip this row belongs to.
	 *
	 * @param trip - the new value for trip this row belongs to
	 *
	 * @return this {@code SurveyRow} if {@code trip} is unchanged, or a copy with the new {@code trip}.
	 */
	public SurveyRow setTrip(SurveyTrip trip) {
		return set(SurveyRow.trip, trip);
	}
	
	/**
	 * Sets whether to exclude this shot from the total cave length.
	 *
	 * @param excludeDistance - the new value for whether to exclude this shot from the total cave length
	 *
	 * @return this {@code SurveyRow} if {@code excludeDistance} is unchanged, or a copy with the new {@code excludeDistance}.
	 */
	public SurveyRow setExcludeDistance(Boolean excludeDistance) {
		return set(SurveyRow.excludeDistance, excludeDistance);
	}
	
	/**
	 * Sets whether to exclude this shot from plotting.
	 *
	 * @param excludeFromPlotting - the new value for whether to exclude this shot from plotting
	 *
	 * @return this {@code SurveyRow} if {@code excludeFromPlotting} is unchanged, or a copy with the new {@code excludeFromPlotting}.
	 */
	public SurveyRow setExcludeFromPlotting(Boolean excludeFromPlotting) {
		return set(SurveyRow.excludeFromPlotting, excludeFromPlotting);
	}
	
	
	/**
	 * Updates name of cave from station is in, if different from trip.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideFromCave} and returns the new value for {@code overrideFromCave}.
	 *
	 * @return this {@code SurveyRow} if {@code overrideFromCave} is unchanged, or a copy with the updated {@code overrideFromCave}.
	 */
	public SurveyRow updateOverrideFromCave(Function<String, String> updater) {
		return update(overrideFromCave, updater);
	}
	
	/**
	 * Updates from station name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code fromStation} and returns the new value for {@code fromStation}.
	 *
	 * @return this {@code SurveyRow} if {@code fromStation} is unchanged, or a copy with the updated {@code fromStation}.
	 */
	public SurveyRow updateFromStation(Function<String, String> updater) {
		return update(fromStation, updater);
	}
	
	/**
	 * Updates name of cave of to station is in, if different to trip.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideToCave} and returns the new value for {@code overrideToCave}.
	 *
	 * @return this {@code SurveyRow} if {@code overrideToCave} is unchanged, or a copy with the updated {@code overrideToCave}.
	 */
	public SurveyRow updateOverrideToCave(Function<String, String> updater) {
		return update(overrideToCave, updater);
	}
	
	/**
	 * Updates to station name.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code toStation} and returns the new value for {@code toStation}.
	 *
	 * @return this {@code SurveyRow} if {@code toStation} is unchanged, or a copy with the updated {@code toStation}.
	 */
	public SurveyRow updateToStation(Function<String, String> updater) {
		return update(toStation, updater);
	}
	
	/**
	 * Updates distance between from and to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code distance} and returns the new value for {@code distance}.
	 *
	 * @return this {@code SurveyRow} if {@code distance} is unchanged, or a copy with the updated {@code distance}.
	 */
	public SurveyRow updateDistance(Function<String, String> updater) {
		return update(distance, updater);
	}
	
	/**
	 * Updates azimuth toward to station at from station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontAzimuth} and returns the new value for {@code frontAzimuth}.
	 *
	 * @return this {@code SurveyRow} if {@code frontAzimuth} is unchanged, or a copy with the updated {@code frontAzimuth}.
	 */
	public SurveyRow updateFrontAzimuth(Function<String, String> updater) {
		return update(frontAzimuth, updater);
	}
	
	/**
	 * Updates azimuth toward from station at to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backAzimuth} and returns the new value for {@code backAzimuth}.
	 *
	 * @return this {@code SurveyRow} if {@code backAzimuth} is unchanged, or a copy with the updated {@code backAzimuth}.
	 */
	public SurveyRow updateBackAzimuth(Function<String, String> updater) {
		return update(backAzimuth, updater);
	}
	
	/**
	 * Updates inclination toward to station at from station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code frontInclination} and returns the new value for {@code frontInclination}.
	 *
	 * @return this {@code SurveyRow} if {@code frontInclination} is unchanged, or a copy with the updated {@code frontInclination}.
	 */
	public SurveyRow updateFrontInclination(Function<String, String> updater) {
		return update(frontInclination, updater);
	}
	
	/**
	 * Updates inclination toward from station at to station.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code backInclination} and returns the new value for {@code backInclination}.
	 *
	 * @return this {@code SurveyRow} if {@code backInclination} is unchanged, or a copy with the updated {@code backInclination}.
	 */
	public SurveyRow updateBackInclination(Function<String, String> updater) {
		return update(backInclination, updater);
	}
	
	/**
	 * Updates distance between from station and left wall.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code left} and returns the new value for {@code left}.
	 *
	 * @return this {@code SurveyRow} if {@code left} is unchanged, or a copy with the updated {@code left}.
	 */
	public SurveyRow updateLeft(Function<String, String> updater) {
		return update(left, updater);
	}
	
	/**
	 * Updates distance between from station and right wall.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code right} and returns the new value for {@code right}.
	 *
	 * @return this {@code SurveyRow} if {@code right} is unchanged, or a copy with the updated {@code right}.
	 */
	public SurveyRow updateRight(Function<String, String> updater) {
		return update(right, updater);
	}
	
	/**
	 * Updates distance between from station and ceiling.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code up} and returns the new value for {@code up}.
	 *
	 * @return this {@code SurveyRow} if {@code up} is unchanged, or a copy with the updated {@code up}.
	 */
	public SurveyRow updateUp(Function<String, String> updater) {
		return update(up, updater);
	}
	
	/**
	 * Updates distance between from station and floor.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code down} and returns the new value for {@code down}.
	 *
	 * @return this {@code SurveyRow} if {@code down} is unchanged, or a copy with the updated {@code down}.
	 */
	public SurveyRow updateDown(Function<String, String> updater) {
		return update(down, updater);
	}
	
	/**
	 * Updates distance north relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code northing} and returns the new value for {@code northing}.
	 *
	 * @return this {@code SurveyRow} if {@code northing} is unchanged, or a copy with the updated {@code northing}.
	 */
	public SurveyRow updateNorthing(Function<String, String> updater) {
		return update(northing, updater);
	}
	
	/**
	 * Updates from station's latitude.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code latitude} and returns the new value for {@code latitude}.
	 *
	 * @return this {@code SurveyRow} if {@code latitude} is unchanged, or a copy with the updated {@code latitude}.
	 */
	public SurveyRow updateLatitude(Function<String, String> updater) {
		return update(latitude, updater);
	}
	
	/**
	 * Updates from station's longitude.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code longitude} and returns the new value for {@code longitude}.
	 *
	 * @return this {@code SurveyRow} if {@code longitude} is unchanged, or a copy with the updated {@code longitude}.
	 */
	public SurveyRow updateLongitude(Function<String, String> updater) {
		return update(longitude, updater);
	}
	
	/**
	 * Updates from station's distance east relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code easting} and returns the new value for {@code easting}.
	 *
	 * @return this {@code SurveyRow} if {@code easting} is unchanged, or a copy with the updated {@code easting}.
	 */
	public SurveyRow updateEasting(Function<String, String> updater) {
		return update(easting, updater);
	}
	
	/**
	 * Updates from station's distance east relative to coordinate origin.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code elevation} and returns the new value for {@code elevation}.
	 *
	 * @return this {@code SurveyRow} if {@code elevation} is unchanged, or a copy with the updated {@code elevation}.
	 */
	public SurveyRow updateElevation(Function<String, String> updater) {
		return update(elevation, updater);
	}
	
	/**
	 * Updates any user comment.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code comment} and returns the new value for {@code comment}.
	 *
	 * @return this {@code SurveyRow} if {@code comment} is unchanged, or a copy with the updated {@code comment}.
	 */
	public SurveyRow updateComment(Function<String, String> updater) {
		return update(comment, updater);
	}
	
	/**
	 * Updates attached files (if one they can't be associated with the entire trip).
	 *
	 * @param updater - {@code Function} that takes the current value of {@code overrideAttachedFiles} and returns the new value for {@code overrideAttachedFiles}.
	 *
	 * @return this {@code SurveyRow} if {@code overrideAttachedFiles} is unchanged, or a copy with the updated {@code overrideAttachedFiles}.
	 */
	public SurveyRow updateOverrideAttachedFiles(Function<PersistentVector<String>, PersistentVector<String>> updater) {
		return update(overrideAttachedFiles, updater);
	}
	
	/**
	 * Updates trip this row belongs to.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code trip} and returns the new value for {@code trip}.
	 *
	 * @return this {@code SurveyRow} if {@code trip} is unchanged, or a copy with the updated {@code trip}.
	 */
	public SurveyRow updateTrip(Function<SurveyTrip, SurveyTrip> updater) {
		return update(trip, updater);
	}
	
	/**
	 * Updates whether to exclude this shot from the total cave length.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code excludeDistance} and returns the new value for {@code excludeDistance}.
	 *
	 * @return this {@code SurveyRow} if {@code excludeDistance} is unchanged, or a copy with the updated {@code excludeDistance}.
	 */
	public SurveyRow updateExcludeDistance(Function<Boolean, Boolean> updater) {
		return update(excludeDistance, updater);
	}
	
	/**
	 * Updates whether to exclude this shot from plotting.
	 *
	 * @param updater - {@code Function} that takes the current value of {@code excludeFromPlotting} and returns the new value for {@code excludeFromPlotting}.
	 *
	 * @return this {@code SurveyRow} if {@code excludeFromPlotting} is unchanged, or a copy with the updated {@code excludeFromPlotting}.
	 */
	public SurveyRow updateExcludeFromPlotting(Function<Boolean, Boolean> updater) {
		return update(excludeFromPlotting, updater);
	}
	
	
	public String getFromCave() {
		return or(getOverrideFromCave(), getTrip() == null ? null : getTrip().getCave());
	}

	public String getToCave() {
		return or(getOverrideToCave(), getTrip() == null ? null : getTrip().getCave());
	}
	
	public PersistentVector<String> getAttachedFiles() {
		return or(getOverrideAttachedFiles(), getTrip() == null ? null : getTrip().getAttachedFiles());
	}
	
}
