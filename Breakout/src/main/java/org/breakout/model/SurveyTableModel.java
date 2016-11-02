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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.collect.CollectionUtils;
import org.andork.math.misc.AngleUtils;
import org.andork.math3d.Vecmath;
import org.andork.swing.async.Subtask;
import org.andork.swing.list.RealListModel;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.ListTableModel;

@SuppressWarnings("serial")
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
				result.setFromCave(row.getFromCave());
				result.setFromStation(row.getFromStation());
				result.setToCave(row.getToCave());
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

	private static float coalesceNaNOrInf(float a, float b) {
		return Float.isNaN(a) || Float.isInfinite(a) ? b : a;
	}

	private static Station getStation(Map<String, Station> stations, String name) {
		Station station = stations.get(name);
		if (station == null) {
			station = new Station();
			station.name = name;
			stations.put(name, station);
		}
		return station;
	}

	private static double parse(Object o) {
		if (o == null) {
			return Double.NaN;
		}
		try {
			return Double.valueOf(o.toString());
		} catch (Exception ex) {
			return Double.NaN;
		}
	}

	private static float parseFloat(Object o) {
		if (o == null) {
			return Float.NaN;
		}
		try {
			return Float.valueOf(o.toString());
		} catch (Exception ex) {
			return Float.NaN;
		}
	}

	private static void updateCrossSections(Station station) {
		if (station.shots.size() == 2) {
			Iterator<Shot> shotIter = station.shots.iterator();
			Shot shot1 = shotIter.next();
			Shot shot2 = shotIter.next();
			CrossSection sect1 = shot1.crossSectionAt(station);
			CrossSection sect2 = shot2.crossSectionAt(station);

			boolean opposite = station == shot1.from == (station == shot2.from);

			for (int i = 0; i < Math.min(sect1.dist.length, sect2.dist.length); i++) {
				int oi = i > 1 ? i : opposite ? 1 - i : i;

				if (Double.isNaN(sect1.dist[i])) {
					sect1.dist[i] = sect2.dist[oi];
				}
				if (Double.isNaN(sect2.dist[i])) {
					sect2.dist[i] = sect1.dist[oi];
				}
			}
		}

		int populatedCount = CollectionUtils.moveToFront(station.shots, shot -> {
			CrossSection section = shot.crossSectionAt(station);
			return section.type == CrossSectionType.LRUD && !Double.isNaN(section.dist[0])
					&& !Double.isNaN(section.dist[1]);
		});

		for (int i = populatedCount; i < station.shots.size(); i++) {
			Shot shot = station.shots.get(i);
			CrossSection section = shot.crossSectionAt(station);
			if (section.type == CrossSectionType.LRUD) {
				double leftAzm = shot.azm - Math.PI * 0.5;
				double rightAzm = shot.azm + Math.PI * 0.5;

				boolean populateLeft = Double.isNaN(section.dist[0]);
				boolean populateRight = Double.isNaN(section.dist[0]);

				for (int i2 = 0; i2 < populatedCount; i2++) {
					Shot populated = station.shots.get(i2);
					CrossSection popCrossSection = populated.crossSectionAt(station);

					double popLeftAzm = populated.azm - Math.PI * 0.5;
					double popRightAzm = populated.azm + Math.PI * 0.5;

					if (populateLeft) {
						double candidateLeft;
						candidateLeft = popCrossSection.dist[0] * Math.cos(AngleUtils.angle(leftAzm, popLeftAzm));
						section.dist[0] = (float) Vecmath.nmax(section.dist[0], candidateLeft);
						candidateLeft = popCrossSection.dist[1] * Math.cos(AngleUtils.angle(leftAzm, popRightAzm));
						section.dist[0] = (float) Vecmath.nmax(section.dist[0], candidateLeft);
					}

					if (populateRight) {
						double candidateRight;
						candidateRight = popCrossSection.dist[0] * Math.cos(AngleUtils.angle(rightAzm, popLeftAzm));
						section.dist[1] = (float) Vecmath.nmax(section.dist[1], candidateRight);
						candidateRight = popCrossSection.dist[1] * Math.cos(AngleUtils.angle(rightAzm, popRightAzm));
						section.dist[1] = (float) Vecmath.nmax(section.dist[1], candidateRight);
					}
				}
			}
		}

		for (Shot shot : station.shots) {
			CrossSection sect1 = shot.crossSectionAt(station);
			CrossSection sect2 = shot.crossSectionAt(shot.otherStation(station));

			for (int i = 0; i < Math.min(sect1.dist.length, sect2.dist.length); i++) {
				if (Double.isNaN(sect1.dist[i])) {
					sect1.dist[i] = sect2.dist[i];
				}
			}
		}
	}

	private final List<Row> rows;

	public SurveyTableModel() {
		super(new ArrayList<Row>());
		rows = ((RealListModel<Row>) getListModel()).getList();
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

	public List<Shot> createShots(Subtask subtask) {
		if (subtask != null) {
			subtask.setTotal(getRowCount());
		}

		Map<String, Station> stations = new LinkedHashMap<String, Station>();
		Map<String, Shot> shots = new LinkedHashMap<String, Shot>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		List<Shot> shotList = new ArrayList<Shot>();

		for (int i = 0; i < getRowCount(); i++) {
			Row row = getRow(i);

			Shot shot = null;

			try {
				String fromName = row.getFromStation();
				String toName = row.getToStation();
				double dist = parse(row.getDistance());
				double fsAzm = Math.toRadians(parse(row.getFrontAzimuth()));
				double bsAzm = Math.toRadians(parse(row.getBackAzimuth()));
				double fsInc = Math.toRadians(parse(row.getFrontInclination()));
				double bsInc = Math.toRadians(parse(row.getBackInclination()));

				CrossSectionType xSectionType = CrossSectionType.LRUD;
				ShotSide xSectionSide = ShotSide.AT_FROM;

				float left = parseFloat(row.getLeft());
				float right = parseFloat(row.getRight());
				float up = parseFloat(row.getUp());
				float down = parseFloat(row.getDown());

				ShotSide positionSide = ShotSide.AT_FROM;

				if (fromName == null || toName == null) {
					continue;
				}

				shot = shots.get(Shot.getName(fromName, toName));
				if (shot == null) {
					shot = shots.get(Shot.getName(toName, fromName));
					if (shot != null) {
						shot = new Shot();
						String s = fromName;
						fromName = toName;
						toName = s;

						double d = fsAzm;
						fsAzm = bsAzm;
						bsAzm = d;

						d = fsInc;
						fsInc = bsInc;
						bsInc = d;

						xSectionSide = xSectionSide.opposite();
						positionSide = positionSide.opposite();
					} else {
						if (Double.isNaN(dist) || Double.isNaN(fsInc) && Double.isNaN(bsInc)) {
							continue;
						}
					}
				}

				double north = parse(row.getNorthing());
				double east = parse(row.getEasting());
				double elev = parse(row.getElevation());

				Station from = getStation(stations, fromName);
				Station to = getStation(stations, toName);

				Vecmath.setdNoNaNOrInf(positionSide == ShotSide.AT_FROM ? from.position : to.position, east, elev,
						-north);

				shot = new Shot();
				shot.from = from;
				shot.to = to;
				shot.dist = dist;
				shot.inc = Shot.averageInc(fsInc, bsInc);
				shot.azm = Shot.averageAzm(shot.inc, fsAzm, bsAzm);
				shot.desc = row.getTrip() == null ? null : row.getTrip().getName();

				try {
					shot.date = row.getTrip() == null ? null : dateFormat.parse(row.getTrip().getDate());
				} catch (Exception ex) {

				}

				CrossSection xSection = xSectionSide == ShotSide.AT_FROM ? shot.fromXsection : shot.toXsection;
				xSection.type = xSectionType;
				xSection.dist[0] = coalesceNaNOrInf(left, xSection.dist[0]);
				xSection.dist[1] = coalesceNaNOrInf(right, xSection.dist[1]);
				xSection.dist[2] = coalesceNaNOrInf(up, xSection.dist[2]);
				xSection.dist[3] = coalesceNaNOrInf(down, xSection.dist[3]);

				if (subtask != null) {
					if (subtask.isCanceling()) {
						return null;
					}
					subtask.setCompleted(i);
				}
			} catch (Exception ex) {
				shot = null;
			} finally {
				if (shot != null) {
					shots.put(shotName(shot), shot);
				}
				// DO add null shots to shotList
				shotList.add(shot);
			}
		}

		for (Shot shot : shots.values()) {
			shot.from.shots.add(shot);
			shot.to.shots.add(shot);
		}

		for (Station station : stations.values()) {
			updateCrossSections(station);
		}

		int number = 0;
		for (Shot shot : shotList) {
			if (shot != null) {
				shot.number = number++;
			}
		}

		return shotList;
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

	protected String shotName(Shot shot) {
		return shot.from.name + " - " + shot.to.name;
	}
}
