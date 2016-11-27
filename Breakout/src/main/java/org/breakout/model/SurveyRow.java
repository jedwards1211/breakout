/**
 * Generated from {@code SurveyRow.record.js} by java-record-generator on 11/27/2016, 5:26:47 PM.
 * {@link https://github.com/jedwards1211/java-record-generator#readme}
 */
 
package org.breakout.model;

import java.util.function.Consumer;
import java.util.Objects;

/**
 *
 */
public class SurveyRow {
	
	public static class MutableSurveyRow implements Cloneable {
		private int modCount = 0; 
		
		/**
		 * name of cave from station is in, if different from trip.
		 */
		private String overrideFromCave;
	
		/**
		 * from station name.
		 */
		private String fromStation;
	
		/**
		 * name of cave of to station is in, if different to trip.
		 */
		private String overrideToCave;
	
		/**
		 * to station name.
		 */
		private String toStation;
	
		/**
		 * distance between from and to station.
		 */
		private String distance;
	
		/**
		 * azimuth toward to station at from station.
		 */
		private String frontAzimuth;
	
		/**
		 * azimuth toward from station at to station.
		 */
		private String backAzimuth;
	
		/**
		 * inclination toward to station at from station.
		 */
		private String frontInclination;
	
		/**
		 * inclination toward from station at to station.
		 */
		private String backInclination;
	
		/**
		 * distance between from station and left wall.
		 */
		private String left;
	
		/**
		 * distance between from station and right wall.
		 */
		private String right;
	
		/**
		 * distance between from station and ceiling.
		 */
		private String up;
	
		/**
		 * distance between from station and floor.
		 */
		private String down;
	
		/**
		 * distance north relative to coordinate origin.
		 */
		private String northing;
	
		/**
		 * distance east relative to coordinate origin.
		 */
		private String easting;
	
		/**
		 * distance east relative to coordinate origin.
		 */
		private String elevation;
	
		/**
		 * any user comment.
		 */
		private String comment;
	
		/**
		 * trip this row belongs to.
		 */
		private SurveyTrip trip;
	
		
		/**
		 * @return name of cave from station is in, if different from trip.
		 */
		public String getOverrideFromCave() {
			return overrideFromCave;
		}
		
		/**
		 * @return from station name.
		 */
		public String getFromStation() {
			return fromStation;
		}
		
		/**
		 * @return name of cave of to station is in, if different to trip.
		 */
		public String getOverrideToCave() {
			return overrideToCave;
		}
		
		/**
		 * @return to station name.
		 */
		public String getToStation() {
			return toStation;
		}
		
		/**
		 * @return distance between from and to station.
		 */
		public String getDistance() {
			return distance;
		}
		
		/**
		 * @return azimuth toward to station at from station.
		 */
		public String getFrontAzimuth() {
			return frontAzimuth;
		}
		
		/**
		 * @return azimuth toward from station at to station.
		 */
		public String getBackAzimuth() {
			return backAzimuth;
		}
		
		/**
		 * @return inclination toward to station at from station.
		 */
		public String getFrontInclination() {
			return frontInclination;
		}
		
		/**
		 * @return inclination toward from station at to station.
		 */
		public String getBackInclination() {
			return backInclination;
		}
		
		/**
		 * @return distance between from station and left wall.
		 */
		public String getLeft() {
			return left;
		}
		
		/**
		 * @return distance between from station and right wall.
		 */
		public String getRight() {
			return right;
		}
		
		/**
		 * @return distance between from station and ceiling.
		 */
		public String getUp() {
			return up;
		}
		
		/**
		 * @return distance between from station and floor.
		 */
		public String getDown() {
			return down;
		}
		
		/**
		 * @return distance north relative to coordinate origin.
		 */
		public String getNorthing() {
			return northing;
		}
		
		/**
		 * @return distance east relative to coordinate origin.
		 */
		public String getEasting() {
			return easting;
		}
		
		/**
		 * @return distance east relative to coordinate origin.
		 */
		public String getElevation() {
			return elevation;
		}
		
		/**
		 * @return any user comment.
		 */
		public String getComment() {
			return comment;
		}
		
		/**
		 * @return trip this row belongs to.
		 */
		public SurveyTrip getTrip() {
			return trip;
		}
		
		
		/**
		 * Sets name of cave from station is in, if different from trip.
		 *
		 * @param overrideFromCave - the new value for name of cave from station is in, if different from trip
		 * 
		 * @return this {@code MutableSurveyRow}.
		 */
		public MutableSurveyRow setOverrideFromCave(String overrideFromCave) {
			if (Objects.equals(this.overrideFromCave, overrideFromCave)) return this;
			modCount++;
			this.overrideFromCave = overrideFromCave;
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
			if (Objects.equals(this.fromStation, fromStation)) return this;
			modCount++;
			this.fromStation = fromStation;
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
			if (Objects.equals(this.overrideToCave, overrideToCave)) return this;
			modCount++;
			this.overrideToCave = overrideToCave;
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
			if (Objects.equals(this.toStation, toStation)) return this;
			modCount++;
			this.toStation = toStation;
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
			if (Objects.equals(this.distance, distance)) return this;
			modCount++;
			this.distance = distance;
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
			if (Objects.equals(this.frontAzimuth, frontAzimuth)) return this;
			modCount++;
			this.frontAzimuth = frontAzimuth;
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
			if (Objects.equals(this.backAzimuth, backAzimuth)) return this;
			modCount++;
			this.backAzimuth = backAzimuth;
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
			if (Objects.equals(this.frontInclination, frontInclination)) return this;
			modCount++;
			this.frontInclination = frontInclination;
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
			if (Objects.equals(this.backInclination, backInclination)) return this;
			modCount++;
			this.backInclination = backInclination;
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
			if (Objects.equals(this.left, left)) return this;
			modCount++;
			this.left = left;
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
			if (Objects.equals(this.right, right)) return this;
			modCount++;
			this.right = right;
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
			if (Objects.equals(this.up, up)) return this;
			modCount++;
			this.up = up;
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
			if (Objects.equals(this.down, down)) return this;
			modCount++;
			this.down = down;
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
			if (Objects.equals(this.northing, northing)) return this;
			modCount++;
			this.northing = northing;
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
			if (Objects.equals(this.easting, easting)) return this;
			modCount++;
			this.easting = easting;
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
			if (Objects.equals(this.elevation, elevation)) return this;
			modCount++;
			this.elevation = elevation;
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
			if (Objects.equals(this.comment, comment)) return this;
			modCount++;
			this.comment = comment;
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
			if (Objects.equals(this.trip, trip)) return this;
			modCount++;
			this.trip = trip;
			return this;
		}
		
		
		@Override
		public MutableSurveyRow clone() {
			try {
				return (MutableSurveyRow) super.clone(); 
			} catch (Exception e) {
				// should not happen
				throw new RuntimeException(e);
			} 
		}
	}
	
	private final MutableSurveyRow data;
	
	private SurveyRow(MutableSurveyRow data) {
		this.data = data;
	}
	
	public SurveyRow() {
		this(new MutableSurveyRow());
	}
	
	public boolean equals(Object o) {
		return o == this;
	}
	
	/**
	 * @param initializer a {@link Consumer} that initializes a {@code SurveyRow}.
	 *
	 * @return a new {@code SurveyRow} with values initialized by {@code initializer}.
	 */
	public static SurveyRow create(Consumer<MutableSurveyRow> initializer) {
		MutableSurveyRow data = new MutableSurveyRow(); 
		initializer.accept(data);
		return new SurveyRow(data);
	}
	
	/**
	 * @param mutator a {@link Consumer} that applies mutations to this {@code SurveyRow}.
	 *
	 * @return a copy of this {@code SurveyRow} with the given mutations applied.
	 */
	public SurveyRow withMutations(Consumer<MutableSurveyRow> mutator) {
		MutableSurveyRow newData = data.clone(); 
		mutator.accept(newData);
		return newData.modCount == data.modCount ? this : new SurveyRow(newData);
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
	
	
	
}
