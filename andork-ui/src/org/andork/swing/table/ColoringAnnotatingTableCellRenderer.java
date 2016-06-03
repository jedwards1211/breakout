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
package org.andork.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class ColoringAnnotatingTableCellRenderer extends DefaultTableCellRenderer
		implements AnnotatingTableCellRenderer {
	/**
	 *
	 */
	private static final long serialVersionUID = -2569593925739779647L;
	Function<Object, Color> colorer;

	public ColoringAnnotatingTableCellRenderer() {

	}

	public ColoringAnnotatingTableCellRenderer(Map<?, Color> annotationColors) {
		this();
		setAnnotationColors(annotationColors);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, Object annotation, boolean isSelected,
			boolean hasFocus, int row, int column) {
		Component renderer = getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (!isSelected) {
			renderer.setBackground(table.getBackground());
			if (annotation != null && colorer != null) {
				Color bg = colorer.apply(annotation);
				if (bg != null) {
					renderer.setBackground(bg);
				}
			}
		}
		return renderer;
	}

	public void setAnnotationColors(Map<?, Color> annotationColors) {
		colorer = annotationColors == null ? null : o -> annotationColors.get(o);
	}

	public void setColorer(Function<Object, Color> colorer) {
		this.colorer = colorer;
	}

}
