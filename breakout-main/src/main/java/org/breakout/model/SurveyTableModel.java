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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.andork.swing.list.RealListModel;
import org.andork.swing.table.AnnotatingTableRowSorter.AbstractTableModelCopier;
import org.andork.swing.table.ListTableModel;
import org.andork.util.StringUtils;
import org.breakout.model.raw.SurveyRow;
import org.breakout.model.raw.SurveyTrip;

public class SurveyTableModel extends ListTableModel<SurveyRow> {
	public static class SurveyTableModelCopier extends AbstractTableModelCopier<SurveyTableModel> {
		@Override
		public void copyRow(SurveyTableModel src, int row, SurveyTableModel dest) {
			dest.setRow(row, src.getRow(row));
		}

		@Override
		public SurveyTableModel createEmptyCopy(SurveyTableModel model) {
			return new SurveyTableModel();
		}
	}

	public static class Columns {
		public static final Column<SurveyRow, String> fromCave = column(SurveyRow.Properties.fromCave);
		public static final Column<SurveyRow, String> fromStation = column(SurveyRow.Properties.fromStation);
		public static final Column<SurveyRow, String> toCave = column(SurveyRow.Properties.toCave);
		public static final Column<SurveyRow, String> toStation = column(SurveyRow.Properties.toStation);
		public static final Column<SurveyRow, String> distance = column(SurveyRow.Properties.distance);
		public static final Column<SurveyRow, String> frontAzimuth = column(SurveyRow.Properties.frontAzimuth);
		public static final Column<SurveyRow, String> frontInclination = column(SurveyRow.Properties.frontInclination);
		public static final Column<SurveyRow, String> backAzimuth = column(SurveyRow.Properties.backAzimuth);
		public static final Column<SurveyRow, String> backInclination = column(SurveyRow.Properties.backInclination);
		public static final Column<SurveyRow, String> left = column(SurveyRow.Properties.left);
		public static final Column<SurveyRow, String> right = column(SurveyRow.Properties.right);
		public static final Column<SurveyRow, String> up = column(SurveyRow.Properties.up);
		public static final Column<SurveyRow, String> down = column(SurveyRow.Properties.down);
		public static final Column<SurveyRow, String> northing = column(SurveyRow.Properties.northing);
		public static final Column<SurveyRow, String> easting = column(SurveyRow.Properties.easting);
		public static final Column<SurveyRow, String> elevation = column(SurveyRow.Properties.elevation);
		public static final Column<SurveyRow, String> comment = column(SurveyRow.Properties.comment);
		public static final Column<SurveyRow, String> tripName = column(SurveyRow.Properties.tripName);
		public static final Column<SurveyRow, String> surveyors = new ColumnBuilder<SurveyRow, String>()
				.columnName("Surveyors")
				.columnClass(String.class)
				.getter(r -> r.getTrip() == null || r.getTrip().getSurveyors() == null
						? null
						: StringUtils.join("; ", r.getTrip().getSurveyors()))
				.setter((row, surveyors) -> {
					return row.withMutations(r -> {
						List<String> parsed = Arrays.asList(surveyors.split("\\s*;\\s*"));
						r.updateTrip(t -> (t == null ? new SurveyTrip() : t).setSurveyors(parsed));
					});
				})
				.create();
		public static final Column<SurveyRow, String> date = column(SurveyRow.Properties.date);
		public static final Column<SurveyRow, String> surveyNotes = column(SurveyRow.Properties.surveyNotes);
		public static final Column<SurveyRow, String> units = new ColumnBuilder<SurveyRow, String>()
				.columnClass(String.class)
				.columnName("Units")
				.getter(r -> {
					SurveyTrip trip = r.getTrip();
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

		public static final List<Column<SurveyRow, ?>> list = Arrays.asList(
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

	private final List<SurveyRow> rows;

	public SurveyTableModel() {
		super(Columns.list, new ArrayList<SurveyRow>());
		rows = ((RealListModel<SurveyRow>) getListModel()).getList();
		fixEndRows();
	}

	public SurveyTableModel(List<SurveyRow> rows) {
		super(Columns.list, rows);
		this.rows = rows;
		fixEndRows();
	}

	public void clear() {
		rows.clear();
	}

	@Override
	public SurveyTableModel clone() {
		return new SurveyTableModel(new ArrayList<>(rows));
	}

	public void copyRowsFrom(SurveyTableModel src, int srcStart, int srcEnd, int myStart) {
		int origRowCount = rows.size();
		int myEnd = myStart + srcEnd - srcStart;
		for (int i = srcStart; i <= srcEnd; i++) {
			SurveyRow srcRow = src.rows.get(i);
			int destI = i + myStart - srcStart;
			while (destI >= rows.size()) {
				rows.add(null);
			}
			rows.set(destI, srcRow);
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
		int startOfEmptyRows = rows.size();
		while (startOfEmptyRows > 0 && isEmpty(startOfEmptyRows - 1)) {
			startOfEmptyRows--;
		}
		if (startOfEmptyRows <= rows.size()) {
			rows.subList(startOfEmptyRows, rows.size()).clear();
		}
	}

	public SurveyRow getRow(int index) {
		return rows.get(index);
	}

	public List<SurveyRow> getRows() {
		return ListTableModel.getList(this);
	}

	private boolean isEmpty(int SurveyRow) {
		for (int column = 0; column < getColumnCount(); column++) {
			Object value = getValueAt(SurveyRow, column);
			if (value != null && !"".equals(value)) {
				return false;
			}
		}
		return true;
	}

	public void setRow(int index, SurveyRow row) {
		if (index == rows.size()) {
			rows.add(row);
		} else {
			rows.set(index, row);
		}
		if (index == rows.size() - 1) {
			fixEndRows();
		}
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		Object prevValue = getValueAt(row, column);
		super.setValueAt(aValue, row, column);
		if (prevValue == null || "".equals(prevValue) != (aValue == null || "".equals(aValue))) {
			fixEndRows();
		}
	}
}
