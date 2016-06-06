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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.q.QSpec.Attribute;
import org.andork.swing.table.AnnotatingJTable;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.Row;
import org.jdesktop.swingx.table.TableColumnExt;

@SuppressWarnings("serial")
public class SurveyTable extends AnnotatingJTable {
	private static final long serialVersionUID = -3257512752381778654L;
	private List<SurveyTableListener> listeners = new ArrayList<>();

	private final NumberFormat numberFormat = NumberFormat.getInstance();

	private final TableCellRenderer numberRenderer = new DefaultTableCellRenderer() {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value instanceof String) {
				try {
					value = numberFormat.format(numberFormat.parse(value.toString()));
				} catch (ParseException e) {
				}
			}
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			return label;
		}
	};

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

				int column = columnAtPoint(e.getPoint());
				int row = rowAtPoint(e.getPoint());

				int modelColumn = convertColumnIndexToModel(column);

				if (modelColumn == SurveyTableModel.Row.scannedNotes.getIndex()) {
					Object o = getValueAt(row, column);
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

			Attribute<?>[] attrs = showData
					? new Attribute<?>[] { Row.from, Row.to, Row.distance, Row.fsAzm, Row.fsInc, Row.bsAzm,
							Row.bsInc, Row.left, Row.right, Row.up, Row.down, Row.north, Row.east, Row.elev,
							Row.scannedNotes }
					: new Attribute<?>[] { Row.from, Row.to, Row.desc, Row.date, Row.surveyors, Row.comment };
			String[] names = showData
					? new String[] { "From", "To", "Distance", "Front Azimuth", "Front Inclination", "Back Azimuth",
							"Back Inclination", "Left", "Right", "Up", "Down", "Northing", "Easting", "Elevation",
							"Scanned Notes" }
					: new String[] { "From", "To", "Description", "Date", "Surveyors", "Comment" };
			boolean[] useNumberFormat = showData
					? new boolean[] { false, false, true, true, true, true, true, true, true, true, true, true, true,
							true, false }
					: new boolean[] { false, false, false, false, false, false };
			int[] widths = showData
					? null
					: new int[] { 50, 50, 300, 70, 200, 200 };
			for (int i = 0; i < attrs.length; i++) {
				TableColumnExt column = new TableColumnExt(attrs[i].getIndex());
				column.setIdentifier(names[i]);
				column.setHeaderValue(names[i]);
				if (widths != null) {
					column.setPreferredWidth(widths[i]);
				}
				if (useNumberFormat[i]) {
					column.setCellRenderer(numberRenderer);
				}
				addColumn(column);
			}
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
