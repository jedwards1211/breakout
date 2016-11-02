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
package org.breakout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.ListTableColumn;
import org.andork.util.StringUtils;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.Row;

public class SurveyTable extends AnnotatingJTable {
	public static class Columns {
		static final NumberFormat numberFormat = NumberFormat.getInstance();
		static final RawNumberCellRenderer numberCellRenderer = new RawNumberCellRenderer(numberFormat);

		static {
			numberFormat.setGroupingUsed(false);
			numberFormat.setMinimumFractionDigits(1);
			numberFormat.setMaximumFractionDigits(1);
		}

		public static ListTableColumn<Row, String> fromCave = new ListTableColumn<Row, String>()
				.headerValue("From Cave")
				.getter(row -> row.getFromCave())
				.setter((row, value) -> {
					row.setFromCave(value);
				});
		public static ListTableColumn<Row, String> fromStation = new ListTableColumn<Row, String>()
				.headerValue("From Station")
				.getter(row -> row.getFromStation())
				.setter((row, value) -> {
					row.setFromStation(value);
				});
		public static ListTableColumn<Row, String> toCave = new ListTableColumn<Row, String>()
				.headerValue("To Cave")
				.getter(row -> row.getToCave())
				.setter((row, value) -> {
					row.setToCave(value);
				});
		public static ListTableColumn<Row, String> toStation = new ListTableColumn<Row, String>()
				.headerValue("To Station")
				.getter(row -> row.getToStation())
				.setter((row, value) -> {
					row.setToStation(value);
				});
		public static ListTableColumn<Row, String> distance = new ListTableColumn<Row, String>()
				.headerValue("Distance")
				.renderer(numberCellRenderer)
				.getter(row -> row.getDistance())
				.setter((row, value) -> {
					row.setDistance(value);
				});
		public static ListTableColumn<Row, String> frontAzimuth = new ListTableColumn<Row, String>()
				.headerValue("Front Azimuth")
				.renderer(numberCellRenderer)
				.getter(row -> row.getFrontAzimuth())
				.setter((row, value) -> {
					row.setFrontAzimuth(value);
				});
		public static ListTableColumn<Row, String> backAzimuth = new ListTableColumn<Row, String>()
				.headerValue("Back Azimuth")
				.renderer(numberCellRenderer)
				.getter(row -> row.getBackAzimuth())
				.setter((row, value) -> {
					row.setBackAzimuth(value);
				});
		public static ListTableColumn<Row, String> frontInclination = new ListTableColumn<Row, String>()
				.headerValue("Front Inclination")
				.renderer(numberCellRenderer)
				.getter(row -> row.getFrontInclination())
				.setter((row, value) -> {
					row.setFrontInclination(value);
				});
		public static ListTableColumn<Row, String> backInclination = new ListTableColumn<Row, String>()
				.headerValue("Back Inclination")
				.renderer(numberCellRenderer)
				.getter(row -> row.getBackInclination())
				.setter((row, value) -> {
					row.setBackInclination(value);
				});
		public static ListTableColumn<Row, String> left = new ListTableColumn<Row, String>()
				.headerValue("Left")
				.renderer(numberCellRenderer)
				.getter(row -> row.getLeft())
				.setter((row, value) -> {
					row.setLeft(value);
				});
		public static ListTableColumn<Row, String> right = new ListTableColumn<Row, String>()
				.headerValue("Right")
				.renderer(numberCellRenderer)
				.getter(row -> row.getRight())
				.setter((row, value) -> {
					row.setRight(value);
				});
		public static ListTableColumn<Row, String> up = new ListTableColumn<Row, String>()
				.headerValue("Up")
				.renderer(numberCellRenderer)
				.getter(row -> row.getUp())
				.setter((row, value) -> {
					row.setUp(value);
				});
		public static ListTableColumn<Row, String> down = new ListTableColumn<Row, String>()
				.headerValue("Down")
				.renderer(numberCellRenderer)
				.getter(row -> row.getDown())
				.setter((row, value) -> {
					row.setDown(value);
				});
		public static ListTableColumn<Row, String> northing = new ListTableColumn<Row, String>()
				.headerValue("Northing")
				.renderer(numberCellRenderer)
				.getter(row -> row.getNorthing())
				.setter((row, value) -> {
					row.setNorthing(value);
				});
		public static ListTableColumn<Row, String> easting = new ListTableColumn<Row, String>()
				.headerValue("Easting")
				.renderer(numberCellRenderer)
				.getter(row -> row.getEasting())
				.setter((row, value) -> {
					row.setEasting(value);
				});
		public static ListTableColumn<Row, String> elevation = new ListTableColumn<Row, String>()
				.headerValue("Elevation")
				.renderer(numberCellRenderer)
				.getter(row -> row.getElevation())
				.setter((row, value) -> {
					row.setElevation(value);
				});
		public static ListTableColumn<Row, String> comment = new ListTableColumn<Row, String>()
				.headerValue("Comment")
				.getter(row -> row.getComment())
				.setter((row, value) -> {
					row.setComment(value);
				});
		public static ListTableColumn<Row, String> surveyNotes = new ListTableColumn<Row, String>()
				.headerValue("Scanned Notes")
				.getter(row -> row.getTrip() == null ? null : row.getTrip().getSurveyNotes())
				.setter((row, value) -> {
					row.ensureTrip().setSurveyNotes(value);
				});
		public static ListTableColumn<Row, String> date = new ListTableColumn<Row, String>()
				.headerValue("Date")
				.renderer(numberCellRenderer)
				.getter(row -> row.getTrip() == null ? null : row.getTrip().getDate())
				.setter((row, value) -> {
					row.ensureTrip().setDate(value);
				});
		public static ListTableColumn<Row, String> tripName = new ListTableColumn<Row, String>()
				.headerValue("Trip")
				.getter(row -> row.getTrip() == null ? null : row.getTrip().getName())
				.setter((row, value) -> {
					row.ensureTrip().setName(value);
				});
		public static ListTableColumn<Row, String> surveyors = new ListTableColumn<Row, String>()
				.headerValue("Surveyors")
				.getter(row -> {
					if (row.getTrip() == null || row.getTrip().getSurveyors() == null) {
						return null;
					}
					return StringUtils.join("; ", row.getTrip().getSurveyors());
				});
	}

	private static final long serialVersionUID = -3257512752381778654L;

	private List<SurveyTableListener> listeners = new ArrayList<>();

	private final NumberFormat numberFormat = NumberFormat.getInstance();

	private boolean showData;

	public SurveyTable() {
		super(new SurveyTableModel());

		numberFormat.setGroupingUsed(false);
		numberFormat.setMinimumFractionDigits(1);
		numberFormat.setMaximumFractionDigits(1);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!e.isControlDown()) {
					return;
				}

				int columnIndex = columnAtPoint(e.getPoint());
				int row = rowAtPoint(e.getPoint());

				@SuppressWarnings("unchecked")
				ListTableColumn<Row, ?> column = (ListTableColumn<Row, ?>) getColumnModel().getColumn(columnIndex);

				if (column == Columns.surveyNotes) {
					Object o = getValueAt(row, columnIndex);
					if (o != null) {
						listeners.forEach(listener -> listener.surveyNotesClicked(o.toString(), row));

					}
				}
			}
		});
	}

	public void addSurveyTableListener(SurveyTableListener listener) {
		listeners.add(listener);
	}

	@Override
	public void createDefaultColumnsFromModel() {
		TableModel m = getModel();
		if (m != null) {
			// Remove any current columns
			TableColumnModel cm = getColumnModel();
			while (cm.getColumnCount() > 0) {
				cm.removeColumn(cm.getColumn(0));
			}

			addColumn(Columns.fromCave);
			addColumn(Columns.fromStation);
			addColumn(Columns.toCave);
			addColumn(Columns.toStation);

			if (showData) {
				addColumn(Columns.distance);
				addColumn(Columns.frontAzimuth);
				addColumn(Columns.frontInclination);
				addColumn(Columns.backAzimuth);
				addColumn(Columns.backInclination);
				addColumn(Columns.left);
				addColumn(Columns.right);
				addColumn(Columns.up);
				addColumn(Columns.down);
				addColumn(Columns.northing);
				addColumn(Columns.easting);
				addColumn(Columns.elevation);
				addColumn(Columns.surveyNotes);
			} else {
				addColumn(Columns.tripName);
				addColumn(Columns.date);
				addColumn(Columns.surveyors);
				addColumn(Columns.comment);
			}
		}
		// int[] widths = showData
		// ? null
		// : new int[] { 50, 50, 300, 70, 200, 200 };
		// for (int i = 0; i < attrs.length; i++) {
		// TableColumnExt column = new TableColumnExt(attrs[i].getIndex());
		// if (widths != null) {
		// column.setPreferredWidth(widths[i]);
		// }
		// }
	}

	@Override
	public SurveyTableModel getModel() {
		return (SurveyTableModel) super.getModel();
	}

	public void removeSurveyTableListener(SurveyTableListener listener) {
		listeners.remove(listener);
	}

	public void setShowData(boolean showData) {
		if (this.showData != showData) {
			this.showData = showData;
			createDefaultColumnsFromModel();
		}
	}
}
