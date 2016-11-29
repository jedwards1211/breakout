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

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;

import org.andork.swing.table.AnnotatingJTable;
import org.andork.swing.table.ListTableColumn;
import org.andork.swing.table.ListTableModel;
import org.breakout.model.SurveyRow;
import org.breakout.model.SurveyTableModel;

public class SurveyTable extends AnnotatingJTable {
	public static class Columns {
		private static <V> ListTableColumn<SurveyRow, V> column(ListTableModel.Column<SurveyRow, V> modelColumn) {
			return new ListTableColumn<>(modelColumn);
		}

		public static final ListTableColumn<SurveyRow, String> fromCave = column(SurveyTableModel.Columns.fromCave)
				.headerValue("From Cave");
		public static final ListTableColumn<SurveyRow, String> fromStation = column(
				SurveyTableModel.Columns.fromStation)
						.headerValue("From Station");
		public static final ListTableColumn<SurveyRow, String> toCave = column(SurveyTableModel.Columns.toCave)
				.headerValue("To Cave");
		public static final ListTableColumn<SurveyRow, String> toStation = column(SurveyTableModel.Columns.toStation)
				.headerValue("To Station");
		public static final ListTableColumn<SurveyRow, String> distance = column(SurveyTableModel.Columns.distance)
				.headerValue("Distance")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> frontAzimuth = column(
				SurveyTableModel.Columns.frontAzimuth)
						.headerValue("FS Azimuth")
						.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> frontInclination = column(
				SurveyTableModel.Columns.frontInclination)
						.headerValue("FS Inclination")
						.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> backAzimuth = column(
				SurveyTableModel.Columns.backAzimuth)
						.headerValue("BS Azimuth")
						.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> backInclination = column(
				SurveyTableModel.Columns.backInclination)
						.headerValue("BS Inclination")
						.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> left = column(SurveyTableModel.Columns.left)
				.headerValue("Left")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> right = column(SurveyTableModel.Columns.right)
				.headerValue("Right")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> up = column(SurveyTableModel.Columns.up)
				.headerValue("Up")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> down = column(SurveyTableModel.Columns.down)
				.headerValue("Down")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> northing = column(SurveyTableModel.Columns.northing)
				.headerValue("Northing")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> easting = column(SurveyTableModel.Columns.easting)
				.headerValue("Easting")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> elevation = column(SurveyTableModel.Columns.elevation)
				.headerValue("Elevation")
				.renderer(numberCellRenderer);
		public static final ListTableColumn<SurveyRow, String> comment = column(SurveyTableModel.Columns.comment)
				.headerValue("Comment");
		public static final ListTableColumn<SurveyRow, String> tripName = column(SurveyTableModel.Columns.tripName)
				.headerValue("SurveyTrip Name");
		public static final ListTableColumn<SurveyRow, String> surveyors = column(SurveyTableModel.Columns.surveyors)
				.headerValue("Surveyors");
		public static final ListTableColumn<SurveyRow, String> date = column(SurveyTableModel.Columns.date)
				.headerValue("Date");
		public static final ListTableColumn<SurveyRow, String> surveyNotes = column(
				SurveyTableModel.Columns.surveyNotes)
						.headerValue("Survey Notes");
		public static final ListTableColumn<SurveyRow, String> units = column(SurveyTableModel.Columns.units)
				.headerValue("Units");
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

		setDefaultEditor(String.class, new DefaultCellEditor(new JTextField()));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!e.isControlDown()) {
					return;
				}

				int columnIndex = columnAtPoint(e.getPoint());
				int Surveyrow = rowAtPoint(e.getPoint());

				@SuppressWarnings("unchecked")
				ListTableColumn<SurveyRow, ?> column = (ListTableColumn<SurveyRow, ?>) getColumnModel()
						.getColumn(columnIndex);

				if (column == Columns.surveyNotes) {
					Object o = getValueAt(Surveyrow, columnIndex);
					if (o != null) {
						listeners.forEach(listener -> listener.surveyNotesClicked(o.toString(), Surveyrow));

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
				addColumn(Columns.units);
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
