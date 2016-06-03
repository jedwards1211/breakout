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
package org.andork.ui.test;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.MouseEvent.BUTTON1;
import static java.awt.event.MouseEvent.BUTTON2;
import static java.awt.event.MouseEvent.BUTTON3;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

public interface Ghost {
	public abstract class InputBuilder {
		Component comp;
		Point mouseLocation;

		int modifiers;

		public InputBuilder(Component comp) {
			super();
			this.comp = comp;
			mouseLocation = new Point(comp.getWidth() / 2, comp.getHeight() / 2);
		}

		public InputBuilder alt() {
			modifiers |= ALT_DOWN_MASK;
			return this;
		}

		public InputBuilder at(Point mouseLocation) {
			if (mouseLocation == null) {
				throw new IllegalArgumentException("mouseLocation must be non-null");
			}
			this.mouseLocation = mouseLocation;
			return this;
		}

		public InputBuilder backspace() {
			return type(VK_BACK_SPACE);
		}

		public abstract InputBuilder click(int button, int clickCount);

		public InputBuilder ctrl() {
			modifiers |= CTRL_DOWN_MASK;
			return this;
		}

		public InputBuilder delete() {
			return type(VK_DELETE);
		}

		public InputBuilder enter() {
			return type(VK_ENTER);
		}

		public InputBuilder leftClick() {
			return click(BUTTON1, 1);
		}

		public InputBuilder middleClick() {
			return click(BUTTON2, 1);
		}

		public abstract InputBuilder press(int keyCode);

		public abstract InputBuilder release(int keyCode);

		public InputBuilder rightClick() {
			return click(BUTTON3, 1);
		}

		public InputBuilder shift() {
			modifiers |= SHIFT_DOWN_MASK;
			return this;
		}

		public abstract InputBuilder type(int keyCode);
	}

	public InputBuilder on(Component c);

	public void scroll(final Component c, Rectangle toBounds);

	public void type(final Component c, String text);
}
