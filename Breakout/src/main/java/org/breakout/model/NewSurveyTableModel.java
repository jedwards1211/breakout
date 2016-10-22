package org.breakout.model;

public class NewSurveyTableModel {
	/**
	 * Data for a row in the survey table, which represents a shot.<br>
	 * <br>
	 * LRUDs are associated with the {@link #getFromStation()} station and the
	 * {@link #getToStation()} station of the previous shot, if it's the same.
	 * <br>
	 * <br>
	 * {@link #getNorthing()}, {@link #getEasting()}, and
	 * {@link #getElevation()} are associated with the {@link #getFromStation()}
	 * station.
	 */
	public static class Row {
		private String fromCave;
		private String fromStation;
		private String toCave;
		private String toStation;
		private String distance;
		private String frontAzimuth;
		private String frontInclination;
		private String backAzimuth;
		private String backInclination;
		private String left;
		private String right;
		private String up;
		private String down;
		private String northing;
		private String easting;
		private String elevation;
		private Trip trip;

		public String getBackAzimuth() {
			return backAzimuth;
		}

		public String getBackInclination() {
			return backInclination;
		}

		public String getDistance() {
			return distance;
		}

		public String getDown() {
			return down;
		}

		public String getEasting() {
			return easting;
		}

		public String getElevation() {
			return elevation;
		}

		public String getFromCave() {
			return fromCave;
		}

		public String getFromStation() {
			return fromStation;
		}

		public String getFrontAzimuth() {
			return frontAzimuth;
		}

		public String getFrontInclination() {
			return frontInclination;
		}

		public String getLeft() {
			return left;
		}

		public String getNorthing() {
			return northing;
		}

		public String getRight() {
			return right;
		}

		public String getToCave() {
			return toCave;
		}

		public String getToStation() {
			return toStation;
		}

		public Trip getTrip() {
			return trip;
		}

		public String getUp() {
			return up;
		}

		public void setBackAzimuth(String backAzimuth) {
			this.backAzimuth = backAzimuth;
		}

		public void setBackInclination(String backInclination) {
			this.backInclination = backInclination;
		}

		public void setDistance(String distance) {
			this.distance = distance;
		}

		public void setDown(String down) {
			this.down = down;
		}

		public void setEasting(String easting) {
			this.easting = easting;
		}

		public void setElevation(String elevation) {
			this.elevation = elevation;
		}

		public void setFromCave(String fromCave) {
			this.fromCave = fromCave;
		}

		public void setFromStation(String from) {
			fromStation = from;
		}

		public void setFrontAzimuth(String frontAzimuth) {
			this.frontAzimuth = frontAzimuth;
		}

		public void setFrontInclination(String frontInclination) {
			this.frontInclination = frontInclination;
		}

		public void setLeft(String left) {
			this.left = left;
		}

		public void setNorthing(String northing) {
			this.northing = northing;
		}

		public void setRight(String right) {
			this.right = right;
		}

		public void setToCave(String toCave) {
			this.toCave = toCave;
		}

		public void setToStation(String to) {
			toStation = to;
		}

		public void setTrip(Trip trip) {
			this.trip = trip;
		}

		public void setUp(String up) {
			this.up = up;
		}
	}

	/**
	 * Data common to a group of {@link Row}s (shots) all taken in one survey
	 * trip.
	 */
	public static class Trip {
		private String cave;
		private String name;
		private String date;
		private String surveyNotes;
		private String surveyors;

		public String getCave() {
			return cave;
		}

		public String getDate() {
			return date;
		}

		public String getName() {
			return name;
		}

		public String getSurveyNotes() {
			return surveyNotes;
		}

		public String getSurveyors() {
			return surveyors;
		}

		public void setCave(String cave) {
			this.cave = cave;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setSurveyNotes(String surveyNotes) {
			this.surveyNotes = surveyNotes;
		}

		public void setSurveyors(String surveyors) {
			this.surveyors = surveyors;
		}
	}
}
