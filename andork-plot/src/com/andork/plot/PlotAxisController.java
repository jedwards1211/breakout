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
package com.andork.plot;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.andork.plot.PlotAxis.Orientation;

public class PlotAxisController {
	private class MouseHandler extends MouseAdapter implements MouseWheelListener {
		MouseEvent pressEvent = null;
		MouseEvent lastEvent = null;

		@Override
		public void mouseDragged(MouseEvent e) {
			if (lastEvent == null) {
				return;
			}

			boolean horiz = view.getOrientation() == Orientation.HORIZONTAL;

			LinearAxisConversion axisConversion = view.getAxisConversion();

			int dx = e.getX() - lastEvent.getX();
			int dy = e.getY() - lastEvent.getY();

			double oldMouseDomain = axisConversion.invert(horiz ? lastEvent.getX() : lastEvent.getY());
			double oldStart = axisConversion.invert(0);
			double oldEnd = axisConversion.invert(view.getViewSpan());

			double newMouseDomain = axisConversion.invert(horiz ? e.getX() : e.getY());
			double newStart = oldStart;
			double newEnd = oldEnd;

			if (pressEvent.getButton() == MouseEvent.BUTTON1 || !enableZoom) {
				double zoom = enableZoom ? Math.pow(dragZoomSpeed, horiz ? dy : dx) : 1.0;
				newStart = oldMouseDomain + (oldStart - newMouseDomain) * zoom;
				newEnd = oldMouseDomain + (oldEnd - newMouseDomain) * zoom;
			} else if (pressEvent.getButton() == MouseEvent.BUTTON3) {
				if (horiz ? pressEvent.getX() > view.getWidth() / 2 : pressEvent.getY() > view.getHeight() / 2) {
					if (newMouseDomain != oldStart) {
						newEnd = newStart
								+ (newEnd - newStart) * (oldMouseDomain - oldStart) / (newMouseDomain - oldStart);
					}
				} else {
					if (newMouseDomain != oldEnd) {
						newStart = newEnd + (newStart - newEnd) * (oldMouseDomain - oldEnd) / (newMouseDomain - oldEnd);
					}
				}
			}

			if (newStart != newEnd) {
				setAxisRange(newStart, newEnd);
			}

			lastEvent = e;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (pressEvent != null) {
				return;
			}
			if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
				pressEvent = lastEvent = e;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (pressEvent != null && e.getButton() == pressEvent.getButton()) {
				lastEvent = null;
				pressEvent = null;
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			doZoom(e);
		}
	}

	private final PlotAxis view;
	private final MouseHandler mouseHandler = new MouseHandler();
	private final MouseLooper mouseLooper = new MouseLooper();

	private double dragZoomSpeed = 1.01;
	private double wheelZoomSpeed = 1.01;

	private boolean enableZoom = true;

	public PlotAxisController(PlotAxis view) {
		this.view = view;
		view.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		view.addMouseListener(mouseLooper);
		view.addMouseMotionListener(mouseLooper);
		view.addMouseWheelListener(mouseLooper);
		mouseLooper.addMouseAdapter(mouseHandler);
	}

	public void doZoom(MouseWheelEvent e) {
		LinearAxisConversion axisConversion = view.getAxisConversion();

		double oldStart = axisConversion.invert(0);
		double oldEnd = axisConversion.invert(view.getViewSpan());

		double mousePosition = axisConversion
				.invert(view.getOrientation() == Orientation.HORIZONTAL ? e.getX() : e.getY());

		double zoom = enableZoom ? Math.pow(wheelZoomSpeed, e.getUnitsToScroll()) : 1.0;

		double newStart = mousePosition + (oldStart - mousePosition) * zoom;
		double newEnd = mousePosition + (oldEnd - mousePosition) * zoom;

		setAxisRange(newStart, newEnd);
	}

	public double getDragZoomSpeed() {
		return dragZoomSpeed;
	}

	public MouseAdapter getMouseHandler() {
		return mouseHandler;
	}

	public PlotAxis getView() {
		return view;
	}

	public double getWheelZoomSpeed() {
		return wheelZoomSpeed;
	}

	public boolean isEnableZoom() {
		return enableZoom;
	}

	public void removeMouseWheelListener() {
		view.removeMouseWheelListener(mouseLooper);
	}

	protected void setAxisRange(double start, double end) {
		view.getAxisConversion().set(start, 0, end, view.getViewSpan());
		// fire an event
		view.setAxisConversion(view.getAxisConversion());
		view.repaint();
		for (Component plot : view.getPlots()) {
			plot.repaint();
		}
	}

	public void setDragZoomSpeed(double dragZoomSpeed) {
		this.dragZoomSpeed = dragZoomSpeed;
	}

	public void setEnableZoom(boolean enableZoom) {
		this.enableZoom = enableZoom;
	}

	public void setWheelZoomSpeed(double wheelZoomSpeed) {
		this.wheelZoomSpeed = wheelZoomSpeed;
	}
}
