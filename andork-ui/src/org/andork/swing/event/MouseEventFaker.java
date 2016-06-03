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
package org.andork.swing.event;

import static java.awt.event.MouseEvent.MOUSE_CLICKED;
import static java.awt.event.MouseEvent.MOUSE_DRAGGED;
import static java.awt.event.MouseEvent.MOUSE_ENTERED;
import static java.awt.event.MouseEvent.MOUSE_EXITED;
import static java.awt.event.MouseEvent.MOUSE_MOVED;
import static java.awt.event.MouseEvent.MOUSE_PRESSED;
import static java.awt.event.MouseEvent.MOUSE_RELEASED;
import static java.awt.event.MouseEvent.MOUSE_WHEEL;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseEventFaker {
	public static void dispatch(MouseEvent e, MouseAdapter adapter) {
		switch (e.getID()) {
		case MOUSE_PRESSED:
			adapter.mousePressed(e);
			return;
		case MOUSE_RELEASED:
			adapter.mouseReleased(e);
			return;
		case MOUSE_CLICKED:
			adapter.mouseClicked(e);
			return;
		case MOUSE_MOVED:
			adapter.mouseMoved(e);
			return;
		case MOUSE_DRAGGED:
			adapter.mouseDragged(e);
			return;
		case MOUSE_ENTERED:
			adapter.mouseEntered(e);
			return;
		case MOUSE_EXITED:
			adapter.mouseExited(e);
			return;
		case MOUSE_WHEEL:
			adapter.mouseWheelMoved((MouseWheelEvent) e);
			return;
		}
	}
}
