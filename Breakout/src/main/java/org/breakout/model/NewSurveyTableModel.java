package org.breakout.model;

import org.andork.swing.table.ListTableColumn;

public class NewSurveyTableModel {
	public static class Columns {
		public static ListTableColumn<Row, String> fromCave = new ListTableColumn<Row, String>()
				.headerValue("From Cave")
				.getter(row -> row.getFromCave())
				.setter((row, value) -> row.setFromCave(value));
		public static ListTableColumn<Row, String> fromStation = new ListTableColumn<Row, String>()
				.headerValue("From Station")
				.getter(row -> row.getFromStation())
				.setter((row, value) -> row.setFromStation(value));
		public static ListTableColumn<Row, String> toCave = new ListTableColumn<Row, String>()
				.headerValue("To Cave")
				.getter(row -> row.getToCave())
				.setter((row, value) -> row.setToCave(value));
		public static ListTableColumn<Row, String> toStation = new ListTableColumn<Row, String>()
				.headerValue("To Station")
				.getter(row -> row.getToStation())
				.setter((row, value) -> row.setToStation(value));
		public static ListTableColumn<Row, String> distance = new ListTableColumn<Row, String>()
				.headerValue("Distance")
				.getter(row -> row.getDistance())
				.setter((row, value) -> row.setDistance(value));
		public static ListTableColumn<Row, String> frontAzimuth = new ListTableColumn<Row, String>()
				.headerValue("FrontAzimuth")
				.getter(row -> row.getFrontAzimuth())
				.setter((row, value) -> row.setFrontAzimuth(value));
		public static ListTableColumn<Row, String> backAzimuth = new ListTableColumn<Row, String>()
				.headerValue("BackAzimuth")
				.getter(row -> row.getBackAzimuth())
				.setter((row, value) -> row.setBackAzimuth(value));
		public static ListTableColumn<Row, String> frontInclination = new ListTableColumn<Row, String>()
				.headerValue("FrontInclination")
				.getter(row -> row.getFrontInclination())
				.setter((row, value) -> row.setFrontInclination(value));
		public static ListTableColumn<Row, String> backInclination = new ListTableColumn<Row, String>()
				.headerValue("BackInclination")
				.getter(row -> row.getBackInclination())
				.setter((row, value) -> row.setBackInclination(value));
		public static ListTableColumn<Row, String> left = new ListTableColumn<Row, String>()
				.headerValue("Left")
				.getter(row -> row.getLeft())
				.setter((row, value) -> row.setLeft(value));
		public static ListTableColumn<Row, String> right = new ListTableColumn<Row, String>()
				.headerValue("Right")
				.getter(row -> row.getRight())
				.setter((row, value) -> row.setRight(value));
		public static ListTableColumn<Row, String> up = new ListTableColumn<Row, String>()
				.headerValue("Up")
				.getter(row -> row.getUp())
				.setter((row, value) -> row.setUp(value));
		public static ListTableColumn<Row, String> down = new ListTableColumn<Row, String>()
				.headerValue("Down")
				.getter(row -> row.getDown())
				.setter((row, value) -> row.setDown(value));
		public static ListTableColumn<Row, String> northing = new ListTableColumn<Row, String>()
				.headerValue("Northing")
				.getter(row -> row.getNorthing())
				.setter((row, value) -> row.setNorthing(value));
		public static ListTableColumn<Row, String> easting = new ListTableColumn<Row, String>()
				.headerValue("Easting")
				.getter(row -> row.getEasting())
				.setter((row, value) -> row.setEasting(value));
		public static ListTableColumn<Row, String> elevation = new ListTableColumn<Row, String>()
				.headerValue("Elevation")
				.getter(row -> row.getElevation())
				.setter((row, value) -> row.setElevation(value));
	}

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
