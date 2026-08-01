package org.breakout.model.raw;

import static org.andork.util.JavaScript.or;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.andork.model.DefaultProperty;
import org.andork.model.Property;

public class SurveyRow {
	protected static class Data implements Cloneable {
		protected String overrideFromCave; // OverrideFromCave
		protected String fromStation; // FromStation
		protected String overrideToCave; // OverrideToCave
		protected String toStation; // ToStation
		protected String distance; // Distance
		protected String frontAzimuth; // FrontAzimuth
		protected String backAzimuth; // BackAzimuth
		protected String frontInclination; // FrontInclination
		protected String backInclination; // BackInclination
		protected String left; // Left
		protected String right; // Right
		protected String up; // Up
		protected String down; // Down
		protected String northing; // Northing
		protected String latitude; // Latitude
		protected String easting; // Easting
		protected String longitude; // Longitude
		protected String elevation; // Elevation
		protected String comment; // Comment
		protected List<String> overrideAttachedFiles; // OverrideAttachedFiles
		protected SurveyTrip trip; // Trip
		protected boolean excludeDistance; // ExcludeDistance
		protected boolean excludeFromPlotting; // ExcludeFromPlotting
	

		public void copy(Data other) {
			overrideFromCave = other.overrideFromCave;
			fromStation = other.fromStation;
			overrideToCave = other.overrideToCave;
			toStation = other.toStation;
			distance = other.distance;
			frontAzimuth = other.frontAzimuth;
			backAzimuth = other.backAzimuth;
			frontInclination = other.frontInclination;
			backInclination = other.backInclination;
			left = other.left;
			right = other.right;
			up = other.up;
			down = other.down;
			northing = other.northing;
			latitude = other.latitude;
			easting = other.easting;
			longitude = other.longitude;
			elevation = other.elevation;
			comment = other.comment;
			overrideAttachedFiles = other.overrideAttachedFiles;
			trip = other.trip;
			excludeDistance = other.excludeDistance;
			excludeFromPlotting = other.excludeFromPlotting;
		}

		@Override
		public Data clone() {
			Data clone = new Data();
			clone.copy(this);
			return clone;
		}

		@Override
		public int hashCode() {
			return Objects
				.hash(
					backAzimuth,
					backInclination,
					comment,
					distance,
					down,
					easting,
					elevation,
					excludeDistance,
					excludeFromPlotting,
					fromStation,
					frontAzimuth,
					frontInclination,
					latitude,
					left,
					longitude,
					northing,
					overrideAttachedFiles,
					overrideFromCave,
					overrideToCave,
					right,
					toStation,
					trip,
					up);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Data other = (Data) obj;
			return Objects.equals(backAzimuth, other.backAzimuth)
				&& Objects.equals(backInclination, other.backInclination)
				&& Objects.equals(comment, other.comment)
				&& Objects.equals(distance, other.distance)
				&& Objects.equals(down, other.down)
				&& Objects.equals(easting, other.easting)
				&& Objects.equals(elevation, other.elevation)
				&& excludeDistance == other.excludeDistance
				&& excludeFromPlotting == other.excludeFromPlotting
				&& Objects.equals(fromStation, other.fromStation)
				&& Objects.equals(frontAzimuth, other.frontAzimuth)
				&& Objects.equals(frontInclination, other.frontInclination)
				&& Objects.equals(latitude, other.latitude)
				&& Objects.equals(left, other.left)
				&& Objects.equals(longitude, other.longitude)
				&& Objects.equals(northing, other.northing)
				&& Objects.equals(overrideAttachedFiles, other.overrideAttachedFiles)
				&& Objects.equals(overrideFromCave, other.overrideFromCave)
				&& Objects.equals(overrideToCave, other.overrideToCave)
				&& Objects.equals(right, other.right)
				&& Objects.equals(toStation, other.toStation)
				&& Objects.equals(trip, other.trip)
				&& Objects.equals(up, other.up);
		}
	}

	protected Data data;

	public SurveyRow() {
		data = new Data();
	}

	public SurveyRow(SurveyRow other) {
		data = other.data;
	}

	@Override
	public SurveyRow clone() {
		return new SurveyRow(this);
	}

	public int hashCode() {
		return data.hashCode();
	}

	public boolean equals(Object obj) {
		return data.equals(obj);
	}

	protected SurveyRow toImmutable() {
		return this;
	}

	public Mutable toMutable() {
		return new Mutable(this);
	}

	public SurveyRow withMutations(Consumer<Mutable> mutator) {
		Mutable mutable = toMutable();
		mutator.accept(mutable);
		return mutable.toImmutable();
	}
	
	
	public String getOverrideFromCave() {
		return data.overrideFromCave;
	}


	public String getFromStation() {
		return data.fromStation;
	}


	public String getOverrideToCave() {
		return data.overrideToCave;
	}


	public String getToStation() {
		return data.toStation;
	}


	public String getDistance() {
		return data.distance;
	}


	public String getFrontAzimuth() {
		return data.frontAzimuth;
	}


	public String getBackAzimuth() {
		return data.backAzimuth;
	}


	public String getFrontInclination() {
		return data.frontInclination;
	}


	public String getBackInclination() {
		return data.backInclination;
	}


	public String getLeft() {
		return data.left;
	}


	public String getRight() {
		return data.right;
	}


	public String getUp() {
		return data.up;
	}


	public String getDown() {
		return data.down;
	}


	public String getNorthing() {
		return data.northing;
	}


	public String getLatitude() {
		return data.latitude;
	}


	public String getEasting() {
		return data.easting;
	}


	public String getLongitude() {
		return data.longitude;
	}


	public String getElevation() {
		return data.elevation;
	}


	public String getComment() {
		return data.comment;
	}


	public List<String> getOverrideAttachedFiles() {
		return data.overrideAttachedFiles;
	}


	public SurveyTrip getTrip() {
		return data.trip;
	}


	public boolean isExcludeDistance() {
		return data.excludeDistance;
	}


	public boolean isExcludeFromPlotting() {
		return data.excludeFromPlotting;
	}

	public String getFromCave() {
		return or(getOverrideFromCave(), getTrip() == null ? null : getTrip().getCave());
	}

	public String getToCave() {
		return or(getOverrideToCave(), getTrip() == null ? null : getTrip().getCave());
	}

	public List<String> getAttachedFiles() {
		return or(getOverrideAttachedFiles(), getTrip() == null ? null : getTrip().getAttachedFiles());
	}

	public SurveyRow setOverrideFromCave(String overrideFromCave) {
		return toMutable().setOverrideFromCave(overrideFromCave).toImmutable();
	}

	public SurveyRow setFromStation(String fromStation) {
		return toMutable().setFromStation(fromStation).toImmutable();
	}

	public SurveyRow setOverrideToCave(String overrideToCave) {
		return toMutable().setOverrideToCave(overrideToCave).toImmutable();
	}

	public SurveyRow setToStation(String toStation) {
		return toMutable().setToStation(toStation).toImmutable();
	}

	public SurveyRow setDistance(String distance) {
		return toMutable().setDistance(distance).toImmutable();
	}

	public SurveyRow setFrontAzimuth(String frontAzimuth) {
		return toMutable().setFrontAzimuth(frontAzimuth).toImmutable();
	}

	public SurveyRow setBackAzimuth(String backAzimuth) {
		return toMutable().setBackAzimuth(backAzimuth).toImmutable();
	}

	public SurveyRow setFrontInclination(String frontInclination) {
		return toMutable().setFrontInclination(frontInclination).toImmutable();
	}

	public SurveyRow setBackInclination(String backInclination) {
		return toMutable().setBackInclination(backInclination).toImmutable();
	}

	public SurveyRow setLeft(String left) {
		return toMutable().setLeft(left).toImmutable();
	}

	public SurveyRow setRight(String right) {
		return toMutable().setRight(right).toImmutable();
	}

	public SurveyRow setUp(String up) {
		return toMutable().setUp(up).toImmutable();
	}

	public SurveyRow setDown(String down) {
		return toMutable().setDown(down).toImmutable();
	}

	public SurveyRow setNorthing(String northing) {
		return toMutable().setNorthing(northing).toImmutable();
	}

	public SurveyRow setLatitude(String latitude) {
		return toMutable().setLatitude(latitude).toImmutable();
	}

	public SurveyRow setEasting(String easting) {
		return toMutable().setEasting(easting).toImmutable();
	}

	public SurveyRow setLongitude(String longitude) {
		return toMutable().setLongitude(longitude).toImmutable();
	}

	public SurveyRow setElevation(String elevation) {
		return toMutable().setElevation(elevation).toImmutable();
	}

	public SurveyRow setComment(String comment) {
		return toMutable().setComment(comment).toImmutable();
	}

	public SurveyRow setOverrideAttachedFiles(List<String> overrideAttachedFiles) {
		return toMutable().setOverrideAttachedFiles(overrideAttachedFiles).toImmutable();
	}

	public SurveyRow setTrip(SurveyTrip trip) {
		return toMutable().setTrip(trip).toImmutable();
	}

	public SurveyRow setExcludeDistance(boolean excludeDistance) {
		return toMutable().setExcludeDistance(excludeDistance).toImmutable();
	}

	public SurveyRow setExcludeFromPlotting(boolean excludeFromPlotting) {
		return toMutable().setExcludeFromPlotting(excludeFromPlotting).toImmutable();
	}

	public static class Mutable extends SurveyRow {
		private SurveyRow original;

		public Mutable() {
		}

		public Mutable(SurveyRow original) {
			super(original);
			this.original = original.toImmutable();
		}

		private void detach() {
			data = data.clone();
			original = null;
		}

		public Mutable clone() {
			return new Mutable(this);
		}

		public SurveyRow toImmutable() {
			if (original == null)
				original = new SurveyRow(this);
			return original;
		}

		public Mutable setOverrideFromCave(String overrideFromCave) {
			if (original != null && !Objects.equals(data.overrideFromCave, overrideFromCave)) {
				detach();
			}
			data.overrideFromCave = overrideFromCave;
			return this;
		}

		public Mutable setFromStation(String fromStation) {
			if (original != null && !Objects.equals(data.fromStation, fromStation)) {
				detach();
			}
			data.fromStation = fromStation;
			return this;
		}

		public Mutable setOverrideToCave(String overrideToCave) {
			if (original != null && !Objects.equals(data.overrideToCave, overrideToCave)) {
				detach();
			}
			data.overrideToCave = overrideToCave;
			return this;
		}

		public Mutable setToStation(String toStation) {
			if (original != null && !Objects.equals(data.toStation, toStation)) {
				detach();
			}
			data.toStation = toStation;
			return this;
		}

		public Mutable setDistance(String distance) {
			if (original != null && !Objects.equals(data.distance, distance)) {
				detach();
			}
			data.distance = distance;
			return this;
		}

		public Mutable setFrontAzimuth(String frontAzimuth) {
			if (original != null && !Objects.equals(data.frontAzimuth, frontAzimuth)) {
				detach();
			}
			data.frontAzimuth = frontAzimuth;
			return this;
		}

		public Mutable setBackAzimuth(String backAzimuth) {
			if (original != null && !Objects.equals(data.backAzimuth, backAzimuth)) {
				detach();
			}
			data.backAzimuth = backAzimuth;
			return this;
		}

		public Mutable setFrontInclination(String frontInclination) {
			if (original != null && !Objects.equals(data.frontInclination, frontInclination)) {
				detach();
			}
			data.frontInclination = frontInclination;
			return this;
		}

		public Mutable setBackInclination(String backInclination) {
			if (original != null && !Objects.equals(data.backInclination, backInclination)) {
				detach();
			}
			data.backInclination = backInclination;
			return this;
		}

		public Mutable setLeft(String left) {
			if (original != null && !Objects.equals(data.left, left)) {
				detach();
			}
			data.left = left;
			return this;
		}

		public Mutable setRight(String right) {
			if (original != null && !Objects.equals(data.right, right)) {
				detach();
			}
			data.right = right;
			return this;
		}

		public Mutable setUp(String up) {
			if (original != null && !Objects.equals(data.up, up)) {
				detach();
			}
			data.up = up;
			return this;
		}

		public Mutable setDown(String down) {
			if (original != null && !Objects.equals(data.down, down)) {
				detach();
			}
			data.down = down;
			return this;
		}

		public Mutable setNorthing(String northing) {
			if (original != null && !Objects.equals(data.northing, northing)) {
				detach();
			}
			data.northing = northing;
			return this;
		}

		public Mutable setLatitude(String latitude) {
			if (original != null && !Objects.equals(data.latitude, latitude)) {
				detach();
			}
			data.latitude = latitude;
			return this;
		}

		public Mutable setEasting(String easting) {
			if (original != null && !Objects.equals(data.easting, easting)) {
				detach();
			}
			data.easting = easting;
			return this;
		}

		public Mutable setLongitude(String longitude) {
			if (original != null && !Objects.equals(data.longitude, longitude)) {
				detach();
			}
			data.longitude = longitude;
			return this;
		}

		public Mutable setElevation(String elevation) {
			if (original != null && !Objects.equals(data.elevation, elevation)) {
				detach();
			}
			data.elevation = elevation;
			return this;
		}

		public Mutable setComment(String comment) {
			if (original != null && !Objects.equals(data.comment, comment)) {
				detach();
			}
			data.comment = comment;
			return this;
		}

		public Mutable setOverrideAttachedFiles(List<String> overrideAttachedFiles) {
			if (original != null && !Objects.equals(data.overrideAttachedFiles, overrideAttachedFiles)) {
				detach();
			}
			data.overrideAttachedFiles = overrideAttachedFiles;
			return this;
		}

		public Mutable setTrip(SurveyTrip trip) {
			if (original != null && !Objects.equals(data.trip, trip)) {
				detach();
			}
			data.trip = trip;
			return this;
		}

		public Mutable setExcludeDistance(boolean excludeDistance) {
			if (original != null && data.excludeDistance != excludeDistance) {
				detach();
			}
			data.excludeDistance = excludeDistance;
			return this;
		}

		public Mutable setExcludeFromPlotting(boolean excludeFromPlotting) {
			if (original != null && data.excludeFromPlotting != excludeFromPlotting) {
				detach();
			}
			data.excludeFromPlotting = excludeFromPlotting;
			return this;
		}
	}

	public static final class Properties {
		private static <V> DefaultProperty<SurveyRow, V> create(
			String name,
			Class<? super V> valueClass,
			Function<SurveyRow, ? extends V> getter,
			BiFunction<SurveyRow, V, SurveyRow> setter) {
			return new DefaultProperty<SurveyRow, V>(name, valueClass, getter, setter);
		}

		public static final DefaultProperty<SurveyRow, String> overrideFromCave =
			create("overrideFromCave", String.class, r -> r.getOverrideFromCave(), (m, v) -> m.setOverrideFromCave(v));

		public static final DefaultProperty<SurveyRow, String> fromStation =
			create("fromStation", String.class, r -> r.getFromStation(), (m, v) -> m.setFromStation(v));

		public static final DefaultProperty<SurveyRow, String> overrideToCave =
			create("overrideToCave", String.class, r -> r.getOverrideToCave(), (m, v) -> m.setOverrideToCave(v));

		public static final DefaultProperty<SurveyRow, String> toStation =
			create("toStation", String.class, r -> r.getToStation(), (m, v) -> m.setToStation(v));

		public static final DefaultProperty<SurveyRow, String> distance =
			create("distance", String.class, r -> r.getDistance(), (m, v) -> m.setDistance(v));

		public static final DefaultProperty<SurveyRow, String> frontAzimuth =
			create("frontAzimuth", String.class, r -> r.getFrontAzimuth(), (m, v) -> m.setFrontAzimuth(v));

		public static final DefaultProperty<SurveyRow, String> backAzimuth =
			create("backAzimuth", String.class, r -> r.getBackAzimuth(), (m, v) -> m.setBackAzimuth(v));

		public static final DefaultProperty<SurveyRow, String> frontInclination =
			create("frontInclination", String.class, r -> r.getFrontInclination(), (m, v) -> m.setFrontInclination(v));

		public static final DefaultProperty<SurveyRow, String> backInclination =
			create("backInclination", String.class, r -> r.getBackInclination(), (m, v) -> m.setBackInclination(v));

		public static final DefaultProperty<SurveyRow, String> left =
			create("left", String.class, r -> r.getLeft(), (m, v) -> m.setLeft(v));

		public static final DefaultProperty<SurveyRow, String> right =
			create("right", String.class, r -> r.getRight(), (m, v) -> m.setRight(v));

		public static final DefaultProperty<SurveyRow, String> up =
			create("up", String.class, r -> r.getUp(), (m, v) -> m.setUp(v));

		public static final DefaultProperty<SurveyRow, String> down =
			create("down", String.class, r -> r.getDown(), (m, v) -> m.setDown(v));

		public static final DefaultProperty<SurveyRow, String> northing =
			create("northing", String.class, r -> r.getNorthing(), (m, v) -> m.setNorthing(v));

		public static final DefaultProperty<SurveyRow, String> latitude =
			create("latitude", String.class, r -> r.getLatitude(), (m, v) -> m.setLatitude(v));

		public static final DefaultProperty<SurveyRow, String> easting =
			create("easting", String.class, r -> r.getEasting(), (m, v) -> m.setEasting(v));

		public static final DefaultProperty<SurveyRow, String> longitude =
			create("longitude", String.class, r -> r.getLongitude(), (m, v) -> m.setLongitude(v));

		public static final DefaultProperty<SurveyRow, String> elevation =
			create("elevation", String.class, r -> r.getElevation(), (m, v) -> m.setElevation(v));

		public static final DefaultProperty<SurveyRow, String> comment =
			create("comment", String.class, r -> r.getComment(), (m, v) -> m.setComment(v));

		public static final DefaultProperty<SurveyRow, List<String>> overrideAttachedFiles =
			create(
				"overrideAttachedFiles",
				List.class,
				r -> r.getOverrideAttachedFiles(),
				(m, v) -> m.setOverrideAttachedFiles(v));

		public static final DefaultProperty<SurveyRow, SurveyTrip> trip =
			create("trip", SurveyTrip.class, r -> r.getTrip(), (m, v) -> m.setTrip(v));

		public static final DefaultProperty<SurveyRow, Boolean> excludeDistance =
			create("excludeDistance", Boolean.class, r -> r.isExcludeDistance(), (m, v) -> m.setExcludeDistance(v));

		public static final DefaultProperty<SurveyRow, Boolean> excludeFromPlotting =
			create(
				"excludeFromPlotting",
				Boolean.class,
				r -> r.isExcludeFromPlotting(),
				(m, v) -> m.setExcludeFromPlotting(v));

		public static DefaultProperty<SurveyRow, String> fromCave =
			create("fromCave", String.class, r -> r.getFromCave(), (r, fromCave) -> r.setOverrideFromCave(fromCave));

		public static DefaultProperty<SurveyRow, String> toCave =
			create("toCave", String.class, r -> r.getToCave(), (r, toCave) -> r.setOverrideToCave(toCave));

		public static <V> DefaultProperty<SurveyRow, V>
			createTripProperty(String name, Class<? super V> valueClass, Property<SurveyTrip, V> tripProperty) {
			return new DefaultProperty<SurveyRow, V>(
				name,
				valueClass,
				r -> r.getTrip() == null ? null : tripProperty.get(r.getTrip()),
				(row, v) -> {
					return row.withMutations(r -> {
						SurveyTrip trip = r.getTrip();
						trip = tripProperty.set(trip == null ? new SurveyTrip() : trip, v);
						r.setTrip(trip);
					});
				});
		}

		public static DefaultProperty<SurveyRow, String> tripName =
			createTripProperty("tripName", String.class, SurveyTrip.Properties.name);

		public static DefaultProperty<SurveyRow, String> date =
			createTripProperty("date", String.class, SurveyTrip.Properties.date);

		public static DefaultProperty<SurveyRow, List<String>> surveyors =
			createTripProperty("surveyors", List.class, SurveyTrip.Properties.surveyors);

		public static DefaultProperty<SurveyRow, List<String>> attachedFiles =
			create(
				"attachedFiles",
				List.class,
				r -> r.getAttachedFiles(),
				(r, attachedFiles) -> r.setOverrideAttachedFiles(attachedFiles));

		public static DefaultProperty<SurveyRow, String> datum =
			createTripProperty("datum", String.class, SurveyTrip.Properties.datum);

		public static DefaultProperty<SurveyRow, String> ellipsoid =
			createTripProperty("ellipsoid", String.class, SurveyTrip.Properties.ellipsoid);

		public static DefaultProperty<SurveyRow, String> utmZone =
			createTripProperty("utmZone", String.class, SurveyTrip.Properties.utmZone);
	}
}
