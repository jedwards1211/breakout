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
package org.andork.awt.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseAdapterWrapper extends MouseAdapter {
	private MouseAdapter wrapped;

	public MouseAdapterWrapper() {

	}

	private MouseAdapterWrapper(MouseAdapter wrapped) {
		super();
		this.wrapped = wrapped;
	}

	public MouseAdapter getWrapped() {
		return wrapped;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (wrapped != null) {
			wrapped.mouseClicked(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (wrapped != null) {
			wrapped.mouseDragged(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (wrapped != null) {
			wrapped.mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (wrapped != null) {
			wrapped.mouseExited(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (wrapped != null) {
			wrapped.mouseMoved(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (wrapped != null) {
			wrapped.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (wrapped != null) {
			wrapped.mouseReleased(e);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (wrapped != null) {
			wrapped.mouseWheelMoved(e);
		}
	}

	public void setWrapped(MouseAdapter wrapped) {
		this.wrapped = wrapped;
	}

}
