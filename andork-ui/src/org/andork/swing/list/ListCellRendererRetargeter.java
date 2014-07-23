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
package org.andork.swing.list;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import org.andork.awt.event.MouseRetargeter;

public class ListCellRendererRetargeter extends MouseRetargeter {
	protected MouseEvent	prevEvent;
	protected boolean		allowButtonChange;
	protected int			rolloverIndex;
	protected int			pressIndex;

	public boolean isAllowButtonChange() {
		return allowButtonChange;
	}

	public int getRolloverIndex() {
		return rolloverIndex;
	}

	public int getPressIndex() {
		return pressIndex;
	}

	private int findRolloverIndex(JList list, Point p) {
		for (int i = 0; i < list.getModel().getSize(); i++) {
			Rectangle bounds = list.getCellBounds(i, i);
			if (bounds.y + bounds.height > p.y) {
				return i;
			}
		}
		return -1;
	}

	@Override
	protected Component getDeepestComponent(MouseEvent e) {
		JList list = (JList) e.getComponent();
		int index = findRolloverIndex(list, e.getPoint());
		if (index >= 0) {
			Rectangle bounds = list.getCellBounds(index, index);
			int x = e.getX() - bounds.x;
			int y = e.getY() - bounds.y;
			ListCellRenderer cellRenderer = list.getCellRenderer();
			Object value = list.getModel().getElementAt(index);
			boolean isSelected = list.getSelectionModel().isSelectedIndex(index);
			boolean cellHasFocus = list.hasFocus() && isSelected;
			Component rendComp = cellRenderer.getListCellRendererComponent(
					list, value, index, isSelected, cellHasFocus);
			if (rendComp != null) {
				rendComp.setBounds(list.getCellBounds(index, index));
				rendComp.doLayout();
				Component deepest = SwingUtilities.getDeepestComponentAt(rendComp, x, y);
				return deepest != rendComp ? deepest : null;
			}
		}
		return null;
	}

	@Override
	protected Point convertPoint(Component origComp, Point origPoint, Component newTarget) {
		JList list = (JList) origComp;
		int index = findRolloverIndex(list, origPoint);
		if (index >= 0) {
			Rectangle bounds = list.getCellBounds(index, index);
			Point newPoint = new Point(origPoint.x - bounds.x, origPoint.y - bounds.y);
			ListCellRenderer cellRenderer = list.getCellRenderer();
			Object value = list.getModel().getElementAt(index);
			boolean isSelected = list.getSelectionModel().isSelectedIndex(index);
			boolean cellHasFocus = list.hasFocus() && isSelected;
			Component rendComp = cellRenderer.getListCellRendererComponent(
					list, value, index, isSelected, cellHasFocus);
			if (rendComp != null) {
				rendComp.setBounds(list.getCellBounds(index, index));
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
		JList list = (JList) e.getComponent();
		int index = list.locationToIndex(e.getPoint());
		repaintCell(list, index);
	}

	protected void repaintCell(JList list, int index) {
		if (index >= 0) {
			list.repaint(list.getCellBounds(index, index));
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
		JList list = (JList) e.getComponent();
		rolloverIndex = list.locationToIndex(e.getPoint());
		repaintFor(prevEvent);
		super.mouseEntered(e);
		repaintFor(e);
		prevEvent = e;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		rolloverIndex = -1;
		repaintFor(prevEvent);
		super.mouseExited(e);
		prevEvent = null;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		JList list = (JList) e.getComponent();
		rolloverIndex = list.locationToIndex(e.getPoint());
		repaintFor(prevEvent);
		super.mouseMoved(e);
		repaintFor(e);
		prevEvent = e;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		JList list = (JList) e.getComponent();
		rolloverIndex = list.locationToIndex(e.getPoint());
		repaintFor(prevEvent);
		super.mouseDragged(e);
		repaintFor(e);
		prevEvent = e;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JList list = (JList) e.getComponent();
		pressIndex = rolloverIndex = list.locationToIndex(e.getPoint());
		repaintFor(prevEvent);
		super.mousePressed(e);
		repaintFor(e);
		prevEvent = e;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		JList list = (JList) e.getComponent();
		rolloverIndex = list.locationToIndex(e.getPoint());
		repaintFor(prevEvent);
		repaintCell(list, pressIndex);
		super.mouseReleased(e);
		repaintFor(e);
		prevEvent = e;
	}
}
