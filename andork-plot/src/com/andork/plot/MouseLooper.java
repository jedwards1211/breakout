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

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON2_DOWN_MASK;
import static java.awt.event.InputEvent.BUTTON3_DOWN_MASK;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class MouseLooper extends MouseAdapter {
	private static Robot createRobot() {
		try {
			return new Robot();
		} catch (AWTException ex) {
			return null;
		}
	}

	Robot robot;
	MouseEvent pressEvent;
	Point lastDragPoint;
	int xOffset;
	int yOffset;
	Point fakeLocation = null;

	Point fakeLocationOnScreen = null;

	List<MouseAdapter> adapters = new ArrayList<MouseAdapter>();

	public MouseLooper() {
		this(createRobot());
	}

	public MouseLooper(Robot robot) {
		this.robot = robot;
	}

	public void addMouseAdapter(MouseAdapter a) {
		if (!adapters.contains(a)) {
			adapters.add(a);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		MouseEvent re = repositionEvent(e);

		for (MouseAdapter adapter : adapters) {
			adapter.mouseClicked(re);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (pressEvent == null) {
			return;
		}

		if (robot != null) {
			Component c = e.getComponent();

			Rectangle bounds;
			if (c instanceof JComponent) {
				bounds = ((JComponent) c).getVisibleRect();
			} else {
				bounds = SwingUtilities.getLocalBounds(c);
			}
			Point p = new Point();
			SwingUtilities.convertPointToScreen(p, c);
			bounds.setLocation(p);

			Rectangle screenBounds = c.getGraphicsConfiguration().getBounds();
			screenBounds.x++;
			screenBounds.y++;
			screenBounds.width -= 2;
			screenBounds.height -= 2;

			bounds = bounds.intersection(screenBounds);
			bounds.x -= p.x;
			bounds.y -= p.y;

			if (lastDragPoint != null) {
				xOffset += e.getX() - lastDragPoint.x;
				yOffset += e.getY() - lastDragPoint.y;
			}

			if (!bounds.contains(e.getPoint())) {
				int newX = (e.getX() - bounds.x) % bounds.width;
				if (newX < 0) {
					newX += bounds.width;
				}
				newX += bounds.x;

				int newY = (e.getY() - bounds.y) % bounds.height;
				if (newY < 0) {
					newY += bounds.height;
				}
				newY += bounds.y;

				Point newLoc = new Point();
				SwingUtilities.convertPointToScreen(newLoc, c);

				lastDragPoint = null;

				robot.mouseMove(newLoc.x + newX, newLoc.y + newY);
			} else {
				lastDragPoint = e.getPoint();
			}

			if (fakeLocation == null) {
				fakeLocation = new Point();
				fakeLocationOnScreen = new Point();
			}

			fakeLocation.x = pressEvent.getX() + xOffset;
			fakeLocation.y = pressEvent.getY() + yOffset;
			fakeLocationOnScreen.x = pressEvent.getXOnScreen() + xOffset;
			fakeLocationOnScreen.y = pressEvent.getYOnScreen() + yOffset;
		}

		MouseEvent re = repositionEvent(e);

		for (MouseAdapter adapter : adapters) {
			adapter.mouseDragged(re);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		MouseEvent re = repositionEvent(e);

		for (MouseAdapter adapter : adapters) {
			adapter.mouseEntered(re);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		MouseEvent re = repositionEvent(e);

		for (MouseAdapter adapter : adapters) {
			adapter.mouseExited(re);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		MouseEvent re = repositionEvent(e);

		for (MouseAdapter adapter : adapters) {
			adapter.mouseMoved(re);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (pressEvent == null) {
			pressEvent = e;
			lastDragPoint = null;
			xOffset = 0;
			yOffset = 0;
		}

		MouseEvent re = repositionEvent(e);

		for (MouseAdapter adapter : adapters) {
			adapter.mousePressed(re);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		MouseEvent re = repositionEvent(e);

		for (MouseAdapter adapter : adapters) {
			adapter.mouseReleased(re);
		}

		if ((e.getModifiersEx() & (BUTTON1_DOWN_MASK | BUTTON2_DOWN_MASK | BUTTON3_DOWN_MASK)) == 0) {
			pressEvent = null;
			lastDragPoint = null;
			fakeLocation = null;
			fakeLocationOnScreen = null;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		MouseWheelEvent re = repositionEvent(e);

		for (MouseAdapter adapter : adapters) {
			adapter.mouseWheelMoved(re);
		}
	}

	public void removeMouseAdapter(MouseAdapter a) {
		adapters.remove(a);
	}

	private MouseEvent repositionEvent(MouseEvent e) {
		if (fakeLocation == null) {
			return e;
		}
		return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
				fakeLocation.x, fakeLocation.y, fakeLocationOnScreen.x, fakeLocationOnScreen.y,
				e.getClickCount(), e.isPopupTrigger(), e.getButton());
	}

	private MouseWheelEvent repositionEvent(MouseWheelEvent e) {
		if (fakeLocation == null) {
			return e;
		}
		MouseWheelEvent mwe = e;
		return new MouseWheelEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
				fakeLocation.x, fakeLocation.y, fakeLocationOnScreen.x, fakeLocationOnScreen.y,
				e.getClickCount(), e.isPopupTrigger(),
				mwe.getScrollType(), mwe.getScrollAmount(), mwe.getWheelRotation());
	}
}
