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
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.andork.func.Lodash;
import org.andork.model.DefaultProperty;
import org.andork.swing.list.RealListModel;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.ListTableModel;
import org.andork.unit.Angle;
import org.andork.unit.Length;
import org.andork.unit.Unit;
import org.andork.util.StringUtils;

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
				result.setDistanceUnit(trip.getDistanceUnit());
				result.setDistanceCorrection(trip.getDistanceCorrection());
				result.setAngleUnit(trip.getAngleUnit());
				result.setDeclination(trip.getDeclination());
				result.setOverrideFrontAzimuthUnit(trip.getOverrideFrontAzimuthUnit());
				result.setOverrideBackAzimuthUnit(trip.getOverrideBackAzimuthUnit());
				result.setOverrideFrontInclinationUnit(trip.getOverrideFrontInclinationUnit());
				result.setOverrideBackInclinationUnit(trip.getOverrideBackInclinationUnit());
				result.setBackAzimuthsCorrected(trip.areBackAzimuthsCorrected());
				result.setBackInclinationsCorrected(trip.areBackInclinationsCorrected());
				result.setFrontAzimuthCorrection(trip.getFrontAzimuthCorrection());
				result.setBackAzimuthCorrection(trip.getBackAzimuthCorrection());
				result.setFrontInclinationCorrection(trip.getFrontInclinationCorrection());
				result.setBackInclinationCorrection(trip.getBackInclinationCorrection());
				if (trip.getSurveyors() != null) {
					result.setSurveyors(new ArrayList<>(trip.getSurveyors()));
				}
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
		public static class Properties {
			private static <V> DefaultProperty<Row, V> property(String name, Class<V> valueClass,
					Function<? super Row, ? extends V> getter,
					BiConsumer<? super Row, V> setter) {
				return new DefaultProperty<Row, V>(name, valueClass, getter, setter);
			}

			private static <V> DefaultProperty<Row, V> tripProperty(String name, Class<V> valueClass,
					Function<Trip, ? extends V> getter,
					BiConsumer<Trip, V> setter) {
				return new DefaultProperty<Row, V>(name, valueClass,
						r -> r.getTrip() == null ? null : getter.apply(r.getTrip()),
						(r, v) -> setter.accept(r.ensureTrip(), v));
			}

			public static DefaultProperty<Row, String> fromCave = property(
					"fromCave", String.class,
					r -> r.getFromCave(),
					(r, v) -> {
						if (r.getTrip() != null && v == r.getTrip().getCave()) {
							r.setOverrideFromCave(null);
						} else {
							r.setOverrideFromCave(v);
						}
					});
			public static DefaultProperty<Row, String> overrideFromCave = property(
					"overrideFromCave", String.class,
					r -> r.getOverrideFromCave(),
					(r, v) -> r.setOverrideFromCave(v));
			public static DefaultProperty<Row, String> fromStation = property(
					"fromStation", String.class,
					r -> r.getFromStation(),
					(r, v) -> r.setFromStation(v));
			public static DefaultProperty<Row, String> toCave = property(
					"toCave", String.class,
					r -> r.getToCave(),
					(r, v) -> {
						if (r.getTrip() != null && v == r.getTrip().getCave()) {
							r.setOverrideToCave(null);
						} else {
							r.setOverrideToCave(v);
						}
					});
			public static DefaultProperty<Row, String> overrideToCave = property(
					"overrideToCave", String.class,
					r -> r.getOverrideToCave(),
					(r, v) -> r.setOverrideToCave(v));
			public static DefaultProperty<Row, String> toStation = property(
					"toStation", String.class,
					r -> r.getToStation(),
					(r, v) -> r.setToStation(v));
			public static DefaultProperty<Row, String> distance = property(
					"distance", String.class,
					r -> r.getDistance(),
					(r, v) -> r.setDistance(v));
			public static DefaultProperty<Row, String> frontAzimuth = property(
					"frontAzimuth", String.class,
					r -> r.getFrontAzimuth(),
					(r, v) -> r.setFrontAzimuth(v));
			public static DefaultProperty<Row, String> frontInclination = property(
					"frontInclination", String.class,
					r -> r.getFrontInclination(),
					(r, v) -> r.setFrontInclination(v));
			public static DefaultProperty<Row, String> backAzimuth = property(
					"backAzimuth", String.class,
					r -> r.getBackAzimuth(),
					(r, v) -> r.setBackAzimuth(v));
			public static DefaultProperty<Row, String> backInclination = property(
					"backInclination", String.class,
					r -> r.getBackInclination(),
					(r, v) -> r.setBackInclination(v));
			public static DefaultProperty<Row, String> left = property(
					"left", String.class,
					r -> r.getLeft(),
					(r, v) -> r.setLeft(v));
			public static DefaultProperty<Row, String> right = property(
					"right", String.class,
					r -> r.getRight(),
					(r, v) -> r.setRight(v));
			public static DefaultProperty<Row, String> up = property(
					"up", String.class,
					r -> r.getUp(),
					(r, v) -> r.setUp(v));
			public static DefaultProperty<Row, String> down = property(
					"down", String.class,
					r -> r.getDown(),
					(r, v) -> r.setDown(v));
			public static DefaultProperty<Row, String> northing = property(
					"northing", String.class,
					r -> r.getNorthing(),
					(r, v) -> r.setNorthing(v));
			public static DefaultProperty<Row, String> easting = property(
					"easting", String.class,
					r -> r.getEasting(),
					(r, v) -> r.setEasting(v));
			public static DefaultProperty<Row, String> elevation = property(
					"elevation", String.class,
					r -> r.getElevation(),
					(r, v) -> r.setElevation(v));
			public static DefaultProperty<Row, String> comment = property(
					"comment", String.class,
					r -> r.getComment(),
					(r, v) -> r.setComment(v));
			public static DefaultProperty<Row, Trip> trip = property(
					"trip", Trip.class,
					r -> r.getTrip(),
					(r, v) -> r.setTrip(v));
			public static DefaultProperty<Row, String> tripName = tripProperty(
					"tripName", String.class,
					t -> t.getName(),
					(t, v) -> t.setName(v));
			public static DefaultProperty<Row, String> surveyors = tripProperty(
					"surveyors", String.class,
					t -> t.getSurveyors() == null ? null : StringUtils.join("; ", t.getSurveyors()),
					(t, v) -> t.setSurveyors(v == null ? null : Arrays.asList(v.split(";"))));
			public static DefaultProperty<Row, String> date = tripProperty(
					"date", String.class,
					t -> t.getDate(),
					(t, v) -> t.setDate(v));
			public static DefaultProperty<Row, String> surveyNotes = tripProperty(
					"surveyNotes", String.class,
					t -> t.getSurveyNotes(),
					(t, v) -> t.setSurveyNotes(v));
		}

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

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Row [overrideFromCave=").append(overrideFromCave).append(", fromStation=")
					.append(fromStation).append(", overrideToCave=").append(overrideToCave).append(", toStation=")
					.append(toStation).append(", distance=").append(distance).append(", frontAzimuth=")
					.append(frontAzimuth).append(", frontInclination=").append(frontInclination)
					.append(", backAzimuth=").append(backAzimuth).append(", backInclination=").append(backInclination)
					.append(", left=").append(left).append(", right=").append(right).append(", up=").append(up)
					.append(", down=").append(down).append(", northing=").append(northing).append(", easting=")
					.append(easting).append(", elevation=").append(elevation).append(", comment=").append(comment)
					.append(", trip=").append(trip).append("]");
			return builder.toString();
		}
	}

	public static class SurveyTableModelCopier extends AbstractTableModelCopier<SurveyTableModel> {
		public SurveyTableModel copy(SurveyTableModel src) {
			return src.clone();
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
		private Unit<Length> distanceUnit = Length.meters;
		private Unit<Angle> angleUnit = Angle.degrees;
		private Unit<Angle> overrideFrontAzimuthUnit;
		private Unit<Angle> overrideBackAzimuthUnit;
		private Unit<Angle> overrideFrontInclinationUnit;
		private Unit<Angle> overrideBackInclinationUnit;
		private boolean backAzimuthsCorrected;
		private boolean backInclinationsCorrected;
		private String declination;
		private String distanceCorrection;
		private String frontAzimuthCorrection;
		private String frontInclinationCorrection;
		private String backAzimuthCorrection;
		private String backInclinationCorrection;

		public boolean areBackAzimuthsCorrected() {
			return backAzimuthsCorrected;
		}

		public boolean areBackInclinationsCorrected() {
			return backInclinationsCorrected;
		}

		public Unit<Angle> getAngleUnit() {
			return angleUnit;
		}

		public String getBackAzimuthCorrection() {
			return backAzimuthCorrection;
		}

		public Unit<Angle> getBackAzimuthUnit() {
			return overrideBackAzimuthUnit == null ? angleUnit : overrideBackAzimuthUnit;
		}

		public String getBackInclinationCorrection() {
			return backInclinationCorrection;
		}

		public Unit<Angle> getBackInclinationUnit() {
			return overrideBackInclinationUnit == null ? angleUnit : overrideBackInclinationUnit;
		}

		public String getCave() {
			return cave;
		}

		public String getDate() {
			return date;
		}

		public String getDeclination() {
			return declination;
		}

		public String getDistanceCorrection() {
			return distanceCorrection;
		}

		public Unit<Length> getDistanceUnit() {
			return distanceUnit;
		}

		public String getFrontAzimuthCorrection() {
			return frontAzimuthCorrection;
		}

		public Unit<Angle> getFrontAzimuthUnit() {
			return overrideFrontAzimuthUnit == null ? angleUnit : overrideFrontAzimuthUnit;
		}

		public String getFrontInclinationCorrection() {
			return frontInclinationCorrection;
		}

		public Unit<Angle> getFrontInclinationUnit() {
			return overrideFrontInclinationUnit == null ? angleUnit : overrideFrontInclinationUnit;
		}

		public String getName() {
			return name;
		}

		public Unit<Angle> getOverrideBackAzimuthUnit() {
			return overrideBackAzimuthUnit;
		}

		public Unit<Angle> getOverrideBackInclinationUnit() {
			return overrideBackInclinationUnit;
		}

		public Unit<Angle> getOverrideFrontAzimuthUnit() {
			return overrideFrontAzimuthUnit;
		}

		public Unit<Angle> getOverrideFrontInclinationUnit() {
			return overrideFrontInclinationUnit;
		}

		public String getSurveyNotes() {
			return surveyNotes;
		}

		public List<String> getSurveyors() {
			return surveyors;
		}

		public void setAngleUnit(Unit<Angle> angleUnit) {
			this.angleUnit = angleUnit;
		}

		public void setBackAzimuthCorrection(String backAzimuthCorrection) {
			this.backAzimuthCorrection = backAzimuthCorrection;
		}

		public void setBackAzimuthsCorrected(boolean backAzimuthsCorrected) {
			this.backAzimuthsCorrected = backAzimuthsCorrected;
		}

		public void setBackInclinationCorrection(String backInclinationCorrection) {
			this.backInclinationCorrection = backInclinationCorrection;
		}

		public void setBackInclinationsCorrected(boolean backInclinationsCorrected) {
			this.backInclinationsCorrected = backInclinationsCorrected;
		}

		public void setCave(String cave) {
			this.cave = cave;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public void setDeclination(String declination) {
			this.declination = declination;
		}

		public void setDistanceCorrection(String distanceCorrection) {
			this.distanceCorrection = distanceCorrection;
		}

		public void setDistanceUnit(Unit<Length> distanceUnit) {
			this.distanceUnit = distanceUnit;
		}

		public void setFrontAzimuthCorrection(String frontAzimuthCorrection) {
			this.frontAzimuthCorrection = frontAzimuthCorrection;
		}

		public void setFrontInclinationCorrection(String frontInclinationCorrection) {
			this.frontInclinationCorrection = frontInclinationCorrection;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setOverrideBackAzimuthUnit(Unit<Angle> backAzimuthUnit) {
			overrideBackAzimuthUnit = backAzimuthUnit;
		}

		public void setOverrideBackInclinationUnit(Unit<Angle> backInclinationUnit) {
			overrideBackInclinationUnit = backInclinationUnit;
		}

		public void setOverrideFrontAzimuthUnit(Unit<Angle> frontAzimuthUnit) {
			overrideFrontAzimuthUnit = frontAzimuthUnit;
		}

		public void setOverrideFrontInclinationUnit(Unit<Angle> frontInclinationUnit) {
			overrideFrontInclinationUnit = frontInclinationUnit;
		}

		public void setSurveyNotes(String surveyNotes) {
			this.surveyNotes = surveyNotes;
		}

		public void setSurveyors(List<String> surveyors) {
			this.surveyors = surveyors;
		}
	}

	public static class Columns {
		public static final Column<Row, String> fromCave = column(Row.Properties.fromCave);
		public static final Column<Row, String> fromStation = column(Row.Properties.fromStation);
		public static final Column<Row, String> toCave = column(Row.Properties.toCave);
		public static final Column<Row, String> toStation = column(Row.Properties.toStation);
		public static final Column<Row, String> distance = column(Row.Properties.distance);
		public static final Column<Row, String> frontAzimuth = column(Row.Properties.frontAzimuth);
		public static final Column<Row, String> frontInclination = column(Row.Properties.frontInclination);
		public static final Column<Row, String> backAzimuth = column(Row.Properties.backAzimuth);
		public static final Column<Row, String> backInclination = column(Row.Properties.backInclination);
		public static final Column<Row, String> left = column(Row.Properties.left);
		public static final Column<Row, String> right = column(Row.Properties.right);
		public static final Column<Row, String> up = column(Row.Properties.up);
		public static final Column<Row, String> down = column(Row.Properties.down);
		public static final Column<Row, String> northing = column(Row.Properties.northing);
		public static final Column<Row, String> easting = column(Row.Properties.easting);
		public static final Column<Row, String> elevation = column(Row.Properties.elevation);
		public static final Column<Row, String> comment = column(Row.Properties.comment);
		public static final Column<Row, String> tripName = column(Row.Properties.tripName);
		public static final Column<Row, String> surveyors = column(Row.Properties.surveyors);
		public static final Column<Row, String> date = column(Row.Properties.date);
		public static final Column<Row, String> surveyNotes = column(Row.Properties.surveyNotes);
		public static final Column<Row, String> units = new ColumnBuilder<Row, String>()
				.columnClass(String.class)
				.columnName("Units")
				.getter(r -> {
					Trip trip = r.getTrip();
					if (trip == null) {
						return null;
					}
					String dist = trip.getDistanceUnit().toString();
					String azm = trip.getFrontAzimuthUnit().toString() + "/" + trip.getBackAzimuthUnit();
					String azmCorrected = trip.areBackAzimuthsCorrected() ? "C" : "U";
					String inc = trip.getFrontInclinationUnit().toString() + "/" + trip.getBackInclinationUnit();
					String incCorrected = trip.areBackInclinationsCorrected() ? "C" : "U";
					return String.format("dist=%1s azm=%2s %3s inc=%4s %5s", dist, azm, azmCorrected, inc,
							incCorrected);
				})
				.create();

		public static final List<Column<Row, ?>> list = Arrays.asList(
				fromCave,
				fromStation,
				toCave,
				toStation,
				distance,
				frontAzimuth,
				frontInclination,
				backAzimuth,
				backInclination,
				left,
				right,
				up,
				down,
				northing,
				easting,
				elevation,
				comment,
				tripName,
				surveyors,
				date,
				surveyNotes,
				units);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -2919165714804950483L;

	private final List<Row> rows;

	public SurveyTableModel() {
		super(Columns.list, new ArrayList<Row>());
		rows = ((RealListModel<Row>) getListModel()).getList();
		fixEndRows();
	}

	public SurveyTableModel(List<Row> rows) {
		super(Columns.list, rows);
		this.rows = rows;
		fixEndRows();
	}

	public void clear() {
		rows.clear();
	}

	@Override
	public SurveyTableModel clone() {
		DataCloner cloner = new DataCloner();
		return new SurveyTableModel(Lodash.map(rows, cloner::clone));
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
