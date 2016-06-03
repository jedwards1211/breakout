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
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.andork.q.QSpec.Attribute;
import org.andork.swing.table.AnnotatingJTable;
import org.breakout.model.SurveyTableModel;
import org.breakout.model.SurveyTableModel.Row;
import org.jdesktop.swingx.table.TableColumnExt;

@SuppressWarnings("serial")
public class SurveyTable extends AnnotatingJTable {
	/**
	 *
	 */
	private static final long serialVersionUID = -3257512752381778654L;
	private List<SurveyTableListener> listeners = new ArrayList<>();

	public SurveyTable() {
		super(new SurveyTableModel());
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

			Attribute<?>[] attrs = new Attribute<?>[] { Row.from, Row.to, Row.distance, Row.fsAzm, Row.fsInc, Row.bsAzm,
					Row.bsInc,
					Row.left, Row.right, Row.up, Row.down, Row.north, Row.east, Row.elev, Row.desc, Row.date,
					Row.surveyors, Row.comment, Row.scannedNotes };
			String[] names = new String[] { "From", "To", "Distance", "Front Azimuth", "Front Inclination",
					"Back Azimuth", "Back Inclination",
					"Left", "Right", "Up", "Down", "Northing", "Easting", "Elevation", "Description", "Date",
					"Surveyors", "Comment", "Scanned Notes" };

			for (int i = 0; i < attrs.length; i++) {
				TableColumnExt column = new TableColumnExt(attrs[i].getIndex());
				column.setIdentifier(names[i]);
				column.setHeaderValue(names[i]);
				addColumn(column);
			}
		}

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

	@Override
	public SurveyTableModel getModel() {
		return (SurveyTableModel) super.getModel();
	}

	public void removeSurveyTableListener(SurveyTableListener listener) {
		listeners.remove(listener);
	}

}
