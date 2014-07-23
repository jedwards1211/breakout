/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.swing.table;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.andork.swing.CellRendererWithButtons;
import org.andork.swing.CellRendererWithContentArea;

/**
 * A slightly modified {@link BasicTableUI} that paints the cell renderer under
 * the cell editor, and if the cell renderer is a
 * {@link CellRendererWithContentArea}, sets the bounds of the cell editor to
 * the renderer's {@link CellRendererWithContentArea#getContentArea() content
 * area}. Used with {@link TableCellRendererWithButtons}.
 * 
 * @author andy.edwards
 * 
 */
public class CellEditorBoundsOverridingTableUI extends BasicTableUI {
	/**
	 * Paint a representation of the <code>table</code> instance that was set in
	 * installUI().
	 */
	public void paint(Graphics g, JComponent c) {
		Rectangle clip = g.getClipBounds();

		Rectangle bounds = table.getBounds();
		// account for the fact that the graphics has already been translated
		// into the table's bounds
		bounds.x = bounds.y = 0;

		if (table.getRowCount() <= 0 || table.getColumnCount() <= 0 ||
				// this check prevents us from painting the entire table
				// when the clip doesn't intersect our bounds at all
				!bounds.intersects(clip)) {

			return;
		}

		Point upperLeft = clip.getLocation();
		Point lowerRight = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);
		int rMin = table.rowAtPoint(upperLeft);
		int rMax = table.rowAtPoint(lowerRight);
		// This should never happen (as long as our bounds intersect the clip,
		// which is why we bail above if that is the case).
		if (rMin == -1) {
			rMin = 0;
		}
		// If the table does not have enough rows to fill the view we'll get -1.
		// (We could also get -1 if our bounds don't intersect the clip,
		// which is why we bail above if that is the case).
		// Replace this with the index of the last row.
		if (rMax == -1) {
			rMax = table.getRowCount() - 1;
		}

		boolean ltr = table.getComponentOrientation().isLeftToRight();
		int cMin = table.columnAtPoint(ltr ? upperLeft : lowerRight);
		int cMax = table.columnAtPoint(ltr ? lowerRight : upperLeft);
		// This should never happen.
		if (cMin == -1) {
			cMin = 0;
		}
		// If the table does not have enough columns to fill the view we'll get
		// -1.
		// Replace this with the index of the last column.
		if (cMax == -1) {
			cMax = table.getColumnCount() - 1;
		}

		// Paint the grid.
		paintGrid(g, rMin, rMax, cMin, cMax);

		// Paint the cells.
		paintCells(g, rMin, rMax, cMin, cMax);
	}

	/*
	 * Paints the grid lines within <I>aRect</I>, using the grid color set with
	 * <I>setGridColor</I>. Paints vertical lines if
	 * <code>getShowVerticalLines()</code> returns true and paints horizontal
	 * lines if <code>getShowHorizontalLines()</code> returns true.
	 */
	private void paintGrid(Graphics g, int rMin, int rMax, int cMin, int cMax) {
		g.setColor(table.getGridColor());

		Rectangle minCell = table.getCellRect(rMin, cMin, true);
		Rectangle maxCell = table.getCellRect(rMax, cMax, true);
		Rectangle damagedArea = minCell.union(maxCell);

		if (table.getShowHorizontalLines()) {
			int tableWidth = damagedArea.x + damagedArea.width;
			int y = damagedArea.y;
			for (int row = rMin; row <= rMax; row++) {
				y += table.getRowHeight(row);
				g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
			}
		}
		if (table.getShowVerticalLines()) {
			TableColumnModel cm = table.getColumnModel();
			int tableHeight = damagedArea.y + damagedArea.height;
			int x;
			if (table.getComponentOrientation().isLeftToRight()) {
				x = damagedArea.x;
				for (int column = cMin; column <= cMax; column++) {
					int w = cm.getColumn(column).getWidth();
					x += w;
					g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
				}
			} else {
				x = damagedArea.x + damagedArea.width;
				for (int column = cMin; column < cMax; column++) {
					int w = cm.getColumn(column).getWidth();
					x -= w;
					g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
				}
				x -= cm.getColumn(cMax).getWidth();
				g.drawLine(x, 0, x, tableHeight - 1);
			}
		}
	}

	private int viewIndexForColumn(TableColumn aColumn) {
		TableColumnModel cm = table.getColumnModel();
		for (int column = 0; column < cm.getColumnCount(); column++) {
			if (cm.getColumn(column) == aColumn) {
				return column;
			}
		}
		return -1;
	}

	private void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax) {
		JTableHeader header = table.getTableHeader();
		TableColumn draggedColumn = (header == null) ? null : header.getDraggedColumn();

		TableColumnModel cm = table.getColumnModel();
		int columnMargin = cm.getColumnMargin();

		Rectangle cellRect;
		TableColumn aColumn;
		int columnWidth;
		if (table.getComponentOrientation().isLeftToRight()) {
			for (int row = rMin; row <= rMax; row++) {
				cellRect = table.getCellRect(row, cMin, false);
				for (int column = cMin; column <= cMax; column++) {
					aColumn = cm.getColumn(column);
					columnWidth = aColumn.getWidth();
					cellRect.width = columnWidth - columnMargin;
					if (aColumn != draggedColumn) {
						paintCell(g, cellRect, row, column);
					}
					cellRect.x += columnWidth;
				}
			}
		} else {
			for (int row = rMin; row <= rMax; row++) {
				cellRect = table.getCellRect(row, cMin, false);
				aColumn = cm.getColumn(cMin);
				if (aColumn != draggedColumn) {
					columnWidth = aColumn.getWidth();
					cellRect.width = columnWidth - columnMargin;
					paintCell(g, cellRect, row, cMin);
				}
				for (int column = cMin + 1; column <= cMax; column++) {
					aColumn = cm.getColumn(column);
					columnWidth = aColumn.getWidth();
					cellRect.width = columnWidth - columnMargin;
					cellRect.x -= columnWidth;
					if (aColumn != draggedColumn) {
						paintCell(g, cellRect, row, column);
					}
				}
			}
		}

		// Paint the dragged column if we are dragging.
		if (draggedColumn != null) {
			paintDraggedArea(g, rMin, rMax, draggedColumn, header.getDraggedDistance());
		}

		// Remove any renderers that may be left in the rendererPane.
		rendererPane.removeAll();
	}

	private void paintDraggedArea(Graphics g, int rMin, int rMax, TableColumn draggedColumn, int distance) {
		int draggedColumnIndex = viewIndexForColumn(draggedColumn);

		Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true);
		Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true);

		Rectangle vacatedColumnRect = minCell.union(maxCell);

		// Paint a gray well in place of the moving column.
		g.setColor(table.getParent().getBackground());
		g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
				vacatedColumnRect.width, vacatedColumnRect.height);

		// Move to the where the cell has been dragged.
		vacatedColumnRect.x += distance;

		// Fill the background.
		g.setColor(table.getBackground());
		g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
				vacatedColumnRect.width, vacatedColumnRect.height);

		// Paint the vertical grid lines if necessary.
		if (table.getShowVerticalLines()) {
			g.setColor(table.getGridColor());
			int x1 = vacatedColumnRect.x;
			int y1 = vacatedColumnRect.y;
			int x2 = x1 + vacatedColumnRect.width - 1;
			int y2 = y1 + vacatedColumnRect.height - 1;
			// Left
			g.drawLine(x1 - 1, y1, x1 - 1, y2);
			// Right
			g.drawLine(x2, y1, x2, y2);
		}

		for (int row = rMin; row <= rMax; row++) {
			// Render the cell value
			Rectangle r = table.getCellRect(row, draggedColumnIndex, false);
			r.x += distance;
			paintCell(g, r, row, draggedColumnIndex);

			// Paint the (lower) horizontal grid line if necessary.
			if (table.getShowHorizontalLines()) {
				g.setColor(table.getGridColor());
				Rectangle rcr = table.getCellRect(row, draggedColumnIndex, true);
				rcr.x += distance;
				int x1 = rcr.x;
				int y1 = rcr.y;
				int x2 = x1 + rcr.width - 1;
				int y2 = y1 + rcr.height - 1;
				g.drawLine(x1, y2, x2, y2);
			}
		}
	}

	private void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
		TableCellRenderer renderer = table.getCellRenderer(row, column);
		Component rendComp = table.prepareRenderer(renderer, row, column);
		rendererPane.paintComponent(g, rendComp, table, cellRect.x, cellRect.y,
				cellRect.width, cellRect.height, true);
		if (table.isEditing() && table.getEditingRow() == row &&
				table.getEditingColumn() == column) {
			Component editorComp = table.getEditorComponent();
			if (rendComp instanceof CellRendererWithButtons) {
				Rectangle bounds = new Rectangle(((CellRendererWithButtons) rendComp).getContentArea());
				bounds.x += cellRect.x;
				bounds.y += cellRect.y;
				editorComp.setBounds(bounds);
			} else {
				editorComp.setBounds(cellRect);
			}
			editorComp.validate();
		}
	}
}
