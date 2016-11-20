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

import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.ListTableColumn;
import org.andork.swing.table.ListTableModel;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.Row;

public class SurveyTable extends AnnotatingJTable {
	public static class Columns {
		private static <V> ListTableColumn<Row, V> column(ListTableModel.Column<Row, V> modelColumn) {
			return new ListTableColumn<>(modelColumn);
		}

		public static final ListTableColumn<Row, String> fromCave = column(SurveyTableModel.Columns.fromCave)
				.headerValue("From Cave");
		public static final ListTableColumn<Row, String> fromStation = column(SurveyTableModel.Columns.fromStation)
				.headerValue("From Station");
		public static final ListTableColumn<Row, String> toCave = column(SurveyTableModel.Columns.toCave)
				.headerValue("To Cave");
		public static final ListTableColumn<Row, String> toStation = column(SurveyTableModel.Columns.toStation)
				.headerValue("To Station");
		public static final ListTableColumn<Row, String> distance = column(SurveyTableModel.Columns.distance)
				.headerValue("Distance")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> frontAzimuth = column(SurveyTableModel.Columns.frontAzimuth)
				.headerValue("FS Azimuth")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> frontInclination = column(
				SurveyTableModel.Columns.frontInclination)
						.headerValue("FS Inclination")
						.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> backAzimuth = column(SurveyTableModel.Columns.backAzimuth)
				.headerValue("BS Azimuth")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> backInclination = column(
				SurveyTableModel.Columns.backInclination)
						.headerValue("BS Inclination")
						.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> left = column(SurveyTableModel.Columns.left)
				.headerValue("Left")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> right = column(SurveyTableModel.Columns.right)
				.headerValue("Right")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> up = column(SurveyTableModel.Columns.up)
				.headerValue("Up")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> down = column(SurveyTableModel.Columns.down)
				.headerValue("Down")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> northing = column(SurveyTableModel.Columns.northing)
				.headerValue("Northing")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> easting = column(SurveyTableModel.Columns.easting)
				.headerValue("Easting")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> elevation = column(SurveyTableModel.Columns.elevation)
				.headerValue("Elevation")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<Row, String> comment = column(SurveyTableModel.Columns.comment)
				.headerValue("Comment");
		public static final ListTableColumn<Row, String> tripName = column(SurveyTableModel.Columns.tripName)
				.headerValue("Trip Name");
		public static final ListTableColumn<Row, String> surveyors = column(SurveyTableModel.Columns.surveyors)
				.headerValue("Surveyors");
		public static final ListTableColumn<Row, String> date = column(SurveyTableModel.Columns.date)
				.headerValue("Date");
		public static final ListTableColumn<Row, String> surveyNotes = column(SurveyTableModel.Columns.surveyNotes)
				.headerValue("Survey Notes");
	}

	private static final long serialVersionUID = -3257512752381778654L;

	private static final NumberFormat numberFormat = NumberFormat.getInstance();

	static {
		numberFormat.setGroupingUsed(false);
		numberFormat.setMinimumFractionDigits(1);
		numberFormat.setMaximumFractionDigits(1);
	}

	private static final RawNumberCellRenderer numberCellRenderer = new RawNumberCellRenderer(numberFormat);

	private List<SurveyTableListener> listeners = new ArrayList<>();

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
		SurveyTableModel m = getModel();
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
				addColumn(Columns.comment);
				addColumn(Columns.northing);
				addColumn(Columns.easting);
				addColumn(Columns.elevation);
			} else {
				addColumn(Columns.tripName);
				addColumn(Columns.surveyors);
				addColumn(Columns.date);
				addColumn(Columns.surveyNotes);
			}

			ListTableColumn.updateModelIndices(this);
		}
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
