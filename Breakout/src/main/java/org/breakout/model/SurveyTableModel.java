/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.breakout.model;

import static org.andork.util.JavaScript.or;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import org.andork.swing.list.RealListModel;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.ListTableModel;

public class SurveyTableModel extends ListTableModel<SurveyTableModel.Row> {
	public static class DataCloner {
		private final IdentityHashMap<Row, Row> rows = new IdentityHashMap<>();
		private final IdentityHashMap<Trip, Trip> trips = new IdentityHashMap<>();

		public Row clone(Row row) {
			if (row == null) {
				return null;
			}
			Row result = rows.get(row);
			if (result == null) {
				result = new Row();
				result.setOverrideFromCave(row.getOverrideFromCave());
				result.setFromStation(row.getFromStation());
				result.setOverrideToCave(row.getOverrideToCave());
				result.setToStation(row.getToStation());
				result.setDistance(row.getDistance());
				result.setFrontAzimuth(row.getFrontAzimuth());
				result.setFrontInclination(row.getFrontInclination());
				result.setBackAzimuth(row.getBackAzimuth());
				result.setBackInclination(row.getBackInclination());
				result.setLeft(row.getLeft());
				result.setRight(row.getRight());
				result.setUp(row.getUp());
				result.setDown(row.getDown());
				result.setNorthing(row.getNorthing());
				result.setEasting(row.getEasting());
				result.setElevation(row.getElevation());
				result.setComment(row.getComment());
				result.setTrip(clone(row.getTrip()));
			}
			return result;
		}

		public Trip clone(Trip trip) {
			if (trip == null) {
				return null;
			}
			Trip result = trips.get(trip);
			if (result == null) {
				result = new Trip();
				result.setCave(trip.getCave());
				result.setDate(trip.getDate());
				result.setName(trip.getName());
				result.setSurveyNotes(trip.getSurveyNotes());
				result.setSurveyors(new ArrayList<>(trip.getSurveyors()));
				trips.put(trip, result);
			}
			return result;
		}
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
		private String overrideFromCave;
		private String fromStation;
		private String overrideToCave;
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
		private String comment;
		private Trip trip;

		public Trip ensureTrip() {
			if (trip == null) {
				trip = new Trip();
			}
			return trip;
		}

		public String getBackAzimuth() {
			return backAzimuth;
		}

		public String getBackInclination() {
			return backInclination;
		}

		public String getComment() {
			return comment;
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
			return or(overrideFromCave, trip == null ? null : trip.getCave());
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

		public String getOverrideFromCave() {
			return overrideFromCave;
		}

		public String getOverrideToCave() {
			return overrideToCave;
		}

		public String getRight() {
			return right;
		}

		public String getToCave() {
			return or(overrideToCave, trip == null ? null : trip.getCave());
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

		public void setComment(String comment) {
			this.comment = comment;
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

		public void setOverrideFromCave(String fromCave) {
			overrideFromCave = fromCave;
		}

		public void setOverrideToCave(String toCave) {
			overrideToCave = toCave;
		}

		public void setRight(String right) {
			this.right = right;
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

	public static class SurveyTableModelCopier extends AbstractTableModelCopier<SurveyTableModel> {
		public SurveyTableModel copy(SurveyTableModel src) {
			SurveyTableModel dest = createEmptyCopy(src);
			for (int row = 0; row < src.getRowCount(); row++) {
				copyRow(src, row, dest);
			}
			return dest;
		}

		@Override
		public SurveyTableModel createEmptyCopy(SurveyTableModel model) {
			return new SurveyTableModel();
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
		private List<String> surveyors;
		private boolean backsightsAreCorrected;

		public boolean areBacksightsCorrected() {
			return backsightsAreCorrected;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Trip other = (Trip) obj;
			if (cave == null) {
				if (other.cave != null) {
					return false;
				}
			} else if (!cave.equals(other.cave)) {
				return false;
			}
			if (date == null) {
				if (other.date != null) {
					return false;
				}
			} else if (!date.equals(other.date)) {
				return false;
			}
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (surveyNotes == null) {
				if (other.surveyNotes != null) {
					return false;
				}
			} else if (!surveyNotes.equals(other.surveyNotes)) {
				return false;
			}
			if (surveyors == null) {
				if (other.surveyors != null) {
					return false;
				}
			} else if (!surveyors.equals(other.surveyors)) {
				return false;
			}
			return true;
		}

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

		public List<String> getSurveyors() {
			return surveyors;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (cave == null ? 0 : cave.hashCode());
			result = prime * result + (date == null ? 0 : date.hashCode());
			result = prime * result + (name == null ? 0 : name.hashCode());
			result = prime * result + (surveyNotes == null ? 0 : surveyNotes.hashCode());
			result = prime * result + (surveyors == null ? 0 : surveyors.hashCode());
			return result;
		}

		public void setBacksightsCorrected(boolean corrected) {
			backsightsAreCorrected = corrected;
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

		public void setSurveyors(List<String> surveyors) {
			this.surveyors = surveyors;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -2919165714804950483L;

	private final List<Row> rows;

	public SurveyTableModel() {
		super(new ArrayList<Row>());
		rows = ((RealListModel<Row>) getListModel()).getList();
		fixEndRows();
	}

	public SurveyTableModel(List<Row> rows) {
		super(rows);
		this.rows = rows;
		fixEndRows();
	}

	public void clear() {
		rows.clear();
	}

	public void copyRowsFrom(SurveyTableModel src, int srcStart, int srcEnd, int myStart) {
		DataCloner cloner = new DataCloner();

		int origRowCount = rows.size();
		int myEnd = myStart + srcEnd - srcStart;
		for (int i = srcStart; i <= srcEnd; i++) {
			Row srcRow = src.rows.get(i);
			int destI = i + myStart - srcStart;
			while (destI >= rows.size()) {
				rows.add(null);
			}
			rows.set(destI, cloner.clone(srcRow));
		}
		int updateEnd = Math.min(origRowCount - 1, myEnd);
		if (updateEnd >= myStart) {
			fireTableRowsUpdated(myStart, updateEnd);
		}
		if (myEnd >= origRowCount) {
			fireTableRowsInserted(origRowCount, myEnd);
		}
		fixEndRows();
	}

	private void fixEndRows() {
		int startOfEmptyRows = getRowCount();
		while (startOfEmptyRows > 0 && isEmpty(startOfEmptyRows - 1)) {
			startOfEmptyRows--;
		}

		if (startOfEmptyRows == getRowCount()) {
			rows.add(new Row());
		} else if (startOfEmptyRows <= getRowCount() - 2) {
			rows.subList(startOfEmptyRows, getRowCount() - 1).clear();
		}
	}

	public Row getRow(int index) {
		return rows.get(index);
	}

	public List<Row> getRows() {
		return ListTableModel.getList(this);
	}

	private boolean isEmpty(int row) {
		for (int column = 0; column < getColumnCount(); column++) {
			Object value = getValueAt(row, column);
			if (value != null && !"".equals(value)) {
				return false;
			}
		}
		return true;
	}

	public void setRow(int index, Row row) {
		while (index >= getRowCount()) {
			rows.add(new Row());
		}
		rows.set(index, row);
		if (index >= getRowCount() - 2) {
			fixEndRows();
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		while (row >= getRowCount()) {
			rows.add(new Row());
		}
		Object prevValue = getValueAt(row, column);
		super.setValueAt(aValue, row, column);
		if (prevValue == null || "".equals(prevValue) != (aValue == null || "".equals(aValue))) {
			fixEndRows();
		}
	}
}
