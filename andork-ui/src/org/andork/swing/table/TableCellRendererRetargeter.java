package org.andork.swing.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import org.andork.awt.event.MouseRetargeter;

public class TableCellRendererRetargeter extends MouseRetargeter {
	protected MouseEvent			prevEvent;
	protected boolean				allowButtonChange;
	protected int					rolloverRow;
	protected int					rolloverColumn;
	protected int					pressRow;
	protected int					pressColumn;

	public boolean isAllowButtonChange() {
		return allowButtonChange;
	}

	public int getRolloverRow() {
		return rolloverRow;
	}

	public int getRolloverColumn() {
		return rolloverColumn;
	}

	public int getPressRow() {
		return pressRow;
	}

	public int getPressColumn() {
		return pressColumn;
	}

	@Override
	protected Component getDeepestComponent(MouseEvent e) {
		JTable table = (JTable) e.getComponent();
		int row = table.rowAtPoint(e.getPoint());
		int column = table.columnAtPoint(e.getPoint());
		if (row >= 0 && column >= 0) {
			Rectangle bounds = table.getCellRect(row, column, false);
			int x = e.getX() - bounds.x;
			int y = e.getY() - bounds.y;
			TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
			Object value = table.getValueAt(row, column);
			boolean isSelected = table.isCellSelected(row, column);
			boolean cellHasFocus = table.hasFocus() && isSelected;
			Component rendComp = cellRenderer.getTableCellRendererComponent(
					table, value, isSelected, cellHasFocus, row, column);
			if (rendComp != null) {
				rendComp.setBounds(bounds);
				rendComp.doLayout();
				Component deepest = SwingUtilities.getDeepestComponentAt(rendComp, x, y);
				return deepest != rendComp ? deepest : null;
			}
		}
		return null;
	}

	@Override
	protected Point convertPoint(Component origComp, Point origPoint, Component newTarget) {
		JTable table = (JTable) origComp;
		int row = table.rowAtPoint(origPoint);
		int column = table.columnAtPoint(origPoint);
		if (row >= 0 && column >= 0) {
			Rectangle bounds = table.getCellRect(row, column, false);
			int x = origPoint.x - bounds.x;
			int y = origPoint.y - bounds.y;
			Point newPoint = new Point(x, y);
			TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
			Object value = table.getValueAt(row, column);
			boolean isSelected = table.isCellSelected(row, column);
			boolean cellHasFocus = table.hasFocus() && isSelected;
			Component rendComp = cellRenderer.getTableCellRendererComponent(
					table, value, isSelected, cellHasFocus, row, column);
			if (rendComp != null) {
				rendComp.setBounds(bounds);
				rendComp.doLayout();
				return SwingUtilities.convertPoint(rendComp, newPoint, newTarget);
			}
		}
		return new Point(0, 0);
	}

	protected void repaintFor(MouseEvent e) {
		if (e == null) {
			return;
		}
		JTable table = (JTable) e.getComponent();
		int row = table.rowAtPoint(e.getPoint());
		int column = table.columnAtPoint(e.getPoint());
		repaintCell(table, row, column);
	}

	protected void repaintCell(JTable table, int row, int column) {
		if (row >= 0 && column >= 0) {
			table.repaint(table.getCellRect(row, column, false));
		}
	}

	@Override
	protected void retarget(MouseEvent e, Component target, int newID) {
		allowButtonChange = true;
		try {
			super.retarget(e, target, newID);
		} finally {
			allowButtonChange = false;
		}
	}

	@Override
	protected void retarget(MouseWheelEvent e, Component target) {
		allowButtonChange = true;
		try {
			super.retarget(e, target);
		} finally {
			allowButtonChange = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JTable table = (JTable) e.getComponent();
		rolloverRow = table.rowAtPoint(e.getPoint());
		rolloverColumn = table.columnAtPoint(e.getPoint());
		repaintFor(prevEvent);
		super.mouseEntered(e);
		repaintFor(e);
		prevEvent = e;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		rolloverRow = -1;
		rolloverColumn = -1;
		repaintFor(prevEvent);
		super.mouseExited(e);
		prevEvent = null;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		JTable table = (JTable) e.getComponent();
		rolloverRow = table.rowAtPoint(e.getPoint());
		rolloverColumn = table.columnAtPoint(e.getPoint());
		repaintFor(prevEvent);
		super.mouseMoved(e);
		repaintFor(e);
		prevEvent = e;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		JTable table = (JTable) e.getComponent();
		rolloverRow = table.rowAtPoint(e.getPoint());
		rolloverColumn = table.columnAtPoint(e.getPoint());
		repaintFor(prevEvent);
		super.mouseDragged(e);
		repaintFor(e);
		prevEvent = e;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JTable table = (JTable) e.getComponent();
		pressRow = rolloverRow = table.rowAtPoint(e.getPoint());
		pressColumn = rolloverColumn = table.columnAtPoint(e.getPoint());
		repaintFor(prevEvent);
		super.mousePressed(e);
		repaintFor(e);
		prevEvent = e;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		JTable table = (JTable) e.getComponent();
		rolloverRow = table.rowAtPoint(e.getPoint());
		rolloverColumn = table.columnAtPoint(e.getPoint());
		repaintFor(prevEvent);
		repaintCell(table, pressRow, pressColumn);
		super.mouseReleased(e);
		repaintFor(e);
		prevEvent = e;
	}
}
