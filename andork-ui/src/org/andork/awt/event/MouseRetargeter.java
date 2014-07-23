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
package org.andork.awt.event;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * This is an advanced Swing hack. It sends fake mouse events to components
 * based upon real mouse input. It can thus be used to send fake mouse events to
 * components inside {@link ListCellRenderer}, {@link TreeCellRenderer}, and
 * {@link TableCellRenderer} components, and make them behave as if they were
 * actually part of the component hierarchy and receiving events naturally.
 * 
 * @author andy.edwards
 */
public abstract class MouseRetargeter extends MouseAdapter implements MouseMotionListener, MouseWheelListener {
	private Component			rolloverComponent;
	private Component			heldComponent;
	private MouseEvent			holdEvent;

	private MouseListener		unretargetedMouseListener;
	private MouseMotionListener	unretargetedMouseMotionListener;
	private MouseWheelListener	unretargetedMouseWheelListener;

	private static boolean		DEBUG		= false;

	public void setUnretargetedMouseListener(Object listener) {
		if (listener instanceof MouseListener) {
			unretargetedMouseListener = (MouseListener) listener;
		}
		if (listener instanceof MouseMotionListener) {
			unretargetedMouseMotionListener = (MouseMotionListener) listener;
		}
		if (listener instanceof MouseWheelListener) {
			unretargetedMouseWheelListener = (MouseWheelListener) listener;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!shouldRetarget(e)) {
			handleUnretargetedEvent(e);
			return;
		}
		Component deepest = getDeepestComponentBesidesSource(e);
		if (deepest != null) {
			retarget(e, deepest);
		} else {
			handleUnretargetedEvent(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!shouldRetarget(e)) {
			handleUnretargetedEvent(e);
			return;
		}
		if (holdEvent == null) {
			holdEvent = e;
			heldComponent = getDeepestComponentBesidesSource(e);
			if (heldComponent != null) {
				retarget(e, heldComponent);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!shouldRetarget(e)) {
			handleUnretargetedEvent(e);
			return;
		}
		if (holdEvent != null) {
			if (e.getButton() == holdEvent.getButton()) {
				holdEvent = null;
				if (heldComponent != null) {
					retarget(e, heldComponent);
				}
				heldComponent = null;
			}
		} else {
			handleUnretargetedEvent(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		handleMouseMovedOrDragged(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		handleMouseMovedOrDragged(e);
	}

	public void mouseDragged(MouseEvent e) {
		handleMouseMovedOrDragged(e);
	}

	public void mouseMoved(MouseEvent e) {
		handleMouseMovedOrDragged(e);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!shouldRetarget(e)) {
			handleUnretargetedEvent(e);
			return;
		}
		Component deepest = getDeepestComponentBesidesSource(e);
		if (heldComponent != null && heldComponent != deepest) {
			deepest = null;
		}
		if (deepest != null) {
			retarget(e, deepest);
		} else {
			handleUnretargetedEvent(e);
		}
	}

	/**
	 * @param e
	 *            a {@link MouseEvent} this {@code MouseRetargeter} received via
	 *            one of its listener methods.
	 * @return {@code true} if {@code e} should be retargeted to another
	 *         component.
	 */
	protected boolean shouldRetarget(MouseEvent e) {
		return true;
	}

	protected void handleUnretargetedEvent(MouseEvent e) {
		switch (e.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			if (unretargetedMouseListener != null)
				unretargetedMouseListener.mouseClicked(e);
			return;
		case MouseEvent.MOUSE_PRESSED:
			if (unretargetedMouseListener != null)
				unretargetedMouseListener.mousePressed(e);
			return;
		case MouseEvent.MOUSE_RELEASED:
			if (unretargetedMouseListener != null)
				unretargetedMouseListener.mouseReleased(e);
			return;
		case MouseEvent.MOUSE_ENTERED:
			if (unretargetedMouseListener != null)
				unretargetedMouseListener.mouseEntered(e);
			return;
		case MouseEvent.MOUSE_EXITED:
			if (unretargetedMouseListener != null)
				unretargetedMouseListener.mouseExited(e);
			return;
		case MouseEvent.MOUSE_MOVED:
			if (unretargetedMouseMotionListener != null)
				unretargetedMouseMotionListener.mouseMoved(e);
			return;
		case MouseEvent.MOUSE_DRAGGED:
			if (unretargetedMouseMotionListener != null)
				unretargetedMouseMotionListener.mouseDragged(e);
			return;
		case MouseEvent.MOUSE_WHEEL:
			if (unretargetedMouseWheelListener != null)
				unretargetedMouseWheelListener.mouseWheelMoved((MouseWheelEvent) e);
			return;
		}
	}
	
	private Component getDeepestComponentBesidesSource(MouseEvent e) {
		Component result = getDeepestComponent(e);
		if (result == e.getSource()) {
			result = null;
		}
		return result;
	}

	private void handleMouseMovedOrDragged(MouseEvent e) {
		if (!shouldRetarget(e)) {
			handleUnretargetedEvent(e);
			return;
		}

		Component deepest = getDeepestComponentBesidesSource(e);
		if (heldComponent != null && deepest != heldComponent) {
			deepest = null;
		}
		if (deepest != rolloverComponent) {
			if (rolloverComponent != null) {
				retarget(e, rolloverComponent, MouseEvent.MOUSE_EXITED);
			}

			rolloverComponent = deepest;

			if (rolloverComponent != null) {
				retarget(e, rolloverComponent, MouseEvent.MOUSE_ENTERED);
			}
		} else if (rolloverComponent != null) {
			retarget(e, rolloverComponent);
		}

		if (rolloverComponent == null) {
			handleUnretargetedEvent(e);
		}
	}

	/**
	 * Determines what component the given {@link MouseEvent} is over. For
	 * example, for a cell tracker with buttons inside of it, this would figure
	 * out the button that is painted under the event location. This way, even
	 * if the buttons are not in the component hierarchy and receiving events
	 * naturally (because cell renderers are used for painting and removed from
	 * the component hierarchy afterward), we can dispatch fake mouse events to
	 * the buttons so they behave as if they are part of the component
	 * hierarchy.
	 * 
	 * @param e
	 * @return
	 */
	protected abstract Component getDeepestComponent(MouseEvent e);

	/**
	 * Converts a point from the coordinate system of the {@link Component} this
	 * {@code MouseRetargeter} is listening to to the coordinate system of a
	 * {@link Component} the event will be retargeted to.
	 * 
	 * @param origComp
	 *            the {@link Component} that fired a {@link MouseEvent} to one
	 *            of the listener methods of this {@code MouseRetargeter}.
	 * @param origPoint
	 *            the original location of the {@code MouseEvent}.
	 * @param newTarget
	 *            the {@link Component} that the event will be retargeted to.
	 * @return a point representing the corresponding location in
	 *         {@code newTarget}'s coordinate system.
	 */
	protected abstract Point convertPoint(Component origComp, Point origPoint, Component newTarget);

	protected void retarget(MouseEvent e, Component target) {
		retarget(e, target, e.getID());
	}

	protected void retarget(MouseEvent e, Component target, int newID) {
		Point p = convertPoint(e.getComponent(), e.getPoint(), target);
		MouseEvent m = new MouseEvent(target, newID, e.getWhen(), e.getModifiers(),
				p.x, p.y, e.getClickCount(), e.isPopupTrigger());
		if (DEBUG) {
			System.out.println("Retargeting: " + e);
			System.out.println("         to: " + m);
			System.out.println();
		}
		target.dispatchEvent(m);
	}

	protected void retarget(MouseWheelEvent e, Component target) {
		Point p = convertPoint(e.getComponent(), e.getPoint(), target);
		MouseWheelEvent m = new MouseWheelEvent(target, e.getID(), e.getWhen(), e.getModifiers(),
				p.x, p.y, e.getClickCount(), e.isPopupTrigger(), e.getScrollType(),
				e.getScrollAmount(), e.getWheelRotation());
		if (DEBUG) {
			System.out.println("Retargeting: " + e);
			System.out.println("         to: " + m);
			System.out.println();
		}
		target.dispatchEvent(m);
	}
}
