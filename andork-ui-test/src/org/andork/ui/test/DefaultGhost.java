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
import static java.awt.event.KeyEvent.CHAR_UNDEFINED;
import static java.awt.event.KeyEvent.KEY_PRESSED;
import static java.awt.event.KeyEvent.KEY_RELEASED;
import static java.awt.event.KeyEvent.KEY_TYPED;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Like {@link Robot}, except that it works by posting fake events to the AWT
 * event queue rather than faking user action at the OS level.
 *
 * @author andy.edwards
 */
public class DefaultGhost implements Ghost {
	private static void scrollRectToVisible(Component c, Rectangle aRect) {
		if (c instanceof JComponent) {
			((JComponent) c).scrollRectToVisible(aRect);
		} else {
			Container parent;

			int dx = c.getX(), dy = c.getY();

			for (parent = c.getParent(); !(parent == null) &&
					!(parent instanceof JComponent) &&
					!(parent instanceof CellRendererPane); parent = parent.getParent()) {
				Rectangle bounds = parent.getBounds();

				dx += bounds.x;
				dy += bounds.y;
			}

			if (!(parent == null) && !(parent instanceof CellRendererPane)) {
				aRect.x += dx;
				aRect.y += dy;

				scrollRectToVisible(parent, aRect);
				aRect.x -= dx;
				aRect.y -= dy;
			}
		}
	}

	@Override
	public InputBuilder on(Component c) {
		return new InputBuilder(c) {
			@Override
			public InputBuilder click(int button, int clickCount) {
				EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();

				boolean ctrl = (modifiers & CTRL_DOWN_MASK) != 0;
				boolean alt = (modifiers & ALT_DOWN_MASK) != 0;
				boolean shift = (modifiers & SHIFT_DOWN_MASK) != 0;

				int curModifiers = 0;

				if (ctrl) {
					curModifiers |= CTRL_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_CONTROL, CHAR_UNDEFINED));
				}
				if (alt) {
					curModifiers |= ALT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_ALT, CHAR_UNDEFINED));
				}
				if (shift) {
					curModifiers |= SHIFT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_SHIFT, CHAR_UNDEFINED));
				}
				for (int i = 0; i < clickCount; i++) {
					queue.postEvent(new MouseEvent(comp, MouseEvent.MOUSE_PRESSED,
							System.currentTimeMillis(), modifiers, mouseLocation.x, mouseLocation.y,
							i, false, button));
					queue.postEvent(new MouseEvent(comp, MouseEvent.MOUSE_RELEASED,
							System.currentTimeMillis(), modifiers, mouseLocation.x, mouseLocation.y,
							i, false, button));
					queue.postEvent(new MouseEvent(comp, MouseEvent.MOUSE_CLICKED,
							System.currentTimeMillis(), modifiers, mouseLocation.x, mouseLocation.y,
							i + 1, false, button));

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (ctrl) {
					curModifiers &= ~CTRL_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_CONTROL, CHAR_UNDEFINED));
				}
				if (alt) {
					curModifiers &= ~ALT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_ALT, CHAR_UNDEFINED));
				}
				if (shift) {
					curModifiers &= ~SHIFT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_SHIFT, CHAR_UNDEFINED));
				}

				return this;
			}

			@Override
			public InputBuilder press(int keyCode) {
				EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();

				char keyChar = (char) keyCode;

				boolean ctrl = (modifiers & CTRL_DOWN_MASK) != 0;
				boolean alt = (modifiers & ALT_DOWN_MASK) != 0;
				boolean shift = (modifiers & SHIFT_DOWN_MASK) != 0;

				int curModifiers = 0;

				if (ctrl) {
					curModifiers |= CTRL_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_CONTROL, CHAR_UNDEFINED));
				}
				if (alt) {
					curModifiers |= ALT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_ALT, CHAR_UNDEFINED));
				}
				if (shift) {
					curModifiers |= SHIFT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_SHIFT, CHAR_UNDEFINED));
				}
				queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
						modifiers, Character.toUpperCase(keyChar), CHAR_UNDEFINED));

				return this;
			}

			@Override
			public InputBuilder release(int keyCode) {
				EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();

				char keyChar = (char) keyCode;

				boolean ctrl = (modifiers & CTRL_DOWN_MASK) != 0;
				boolean alt = (modifiers & ALT_DOWN_MASK) != 0;
				boolean shift = (modifiers & SHIFT_DOWN_MASK) != 0;

				int curModifiers = 0;

				if (ctrl) {
					curModifiers |= CTRL_DOWN_MASK;
				}
				if (alt) {
					curModifiers |= ALT_DOWN_MASK;
				}
				if (shift) {
					curModifiers |= SHIFT_DOWN_MASK;
				}

				queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
						modifiers, Character.toUpperCase(keyChar), CHAR_UNDEFINED));
				if (ctrl) {
					curModifiers &= ~CTRL_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_CONTROL, CHAR_UNDEFINED));
				}
				if (alt) {
					curModifiers &= ~ALT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_ALT, CHAR_UNDEFINED));
				}
				if (shift) {
					curModifiers &= ~SHIFT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_SHIFT, CHAR_UNDEFINED));
				}

				return this;
			}

			@Override
			public InputBuilder type(int keyCode) {
				EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();

				char keyChar = (char) keyCode;

				boolean ctrl = (modifiers & CTRL_DOWN_MASK) != 0;
				boolean alt = (modifiers & ALT_DOWN_MASK) != 0;
				boolean shift = (modifiers & SHIFT_DOWN_MASK) != 0;

				int curModifiers = 0;

				if (ctrl) {
					curModifiers |= CTRL_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_CONTROL, CHAR_UNDEFINED));
				}
				if (alt) {
					curModifiers |= ALT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_ALT, CHAR_UNDEFINED));
				}
				if (shift) {
					curModifiers |= SHIFT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_SHIFT, CHAR_UNDEFINED));
				}
				queue.postEvent(new KeyEvent(comp, KEY_PRESSED, System.currentTimeMillis(),
						modifiers, Character.toUpperCase(keyChar), CHAR_UNDEFINED));
				queue.postEvent(new KeyEvent(comp, KEY_TYPED, System.currentTimeMillis(),
						modifiers, KeyEvent.VK_UNDEFINED, keyChar));
				queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
						modifiers, Character.toUpperCase(keyChar), CHAR_UNDEFINED));
				if (ctrl) {
					curModifiers &= ~CTRL_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_CONTROL, CHAR_UNDEFINED));
				}
				if (alt) {
					curModifiers &= ~ALT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_ALT, CHAR_UNDEFINED));
				}
				if (shift) {
					curModifiers &= ~SHIFT_DOWN_MASK;
					queue.postEvent(new KeyEvent(comp, KEY_RELEASED, System.currentTimeMillis(),
							curModifiers, KeyEvent.VK_SHIFT, CHAR_UNDEFINED));
				}

				return this;
			}
		};
	}

	@Override
	public void scroll(final Component c, final Rectangle toBounds) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scrollRectToVisible(c, toBounds);
			}
		});
	}

	private void type(Component c, int modifiers, int keyCode) {
		EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();

		char keyChar = (char) keyCode;

		boolean ctrl = (modifiers & CTRL_DOWN_MASK) != 0;
		boolean alt = (modifiers & ALT_DOWN_MASK) != 0;
		boolean shift = (modifiers & SHIFT_DOWN_MASK) != 0;

		int curModifiers = 0;

		if (ctrl) {
			curModifiers |= CTRL_DOWN_MASK;
			queue.postEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(),
					curModifiers, KeyEvent.VK_CONTROL, CHAR_UNDEFINED));
		}
		if (alt) {
			curModifiers |= ALT_DOWN_MASK;
			queue.postEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(),
					curModifiers, KeyEvent.VK_ALT, CHAR_UNDEFINED));
		}
		if (shift) {
			curModifiers |= SHIFT_DOWN_MASK;
			queue.postEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(),
					curModifiers, KeyEvent.VK_SHIFT, CHAR_UNDEFINED));
		}
		queue.postEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(),
				modifiers, Character.toUpperCase(keyChar), CHAR_UNDEFINED));
		queue.postEvent(new KeyEvent(c, KEY_TYPED, System.currentTimeMillis(),
				modifiers, KeyEvent.VK_UNDEFINED, keyChar));
		queue.postEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(),
				modifiers, Character.toUpperCase(keyChar), CHAR_UNDEFINED));
		if (ctrl) {
			curModifiers &= ~CTRL_DOWN_MASK;
			queue.postEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(),
					curModifiers, KeyEvent.VK_CONTROL, CHAR_UNDEFINED));
		}
		if (alt) {
			curModifiers &= ~ALT_DOWN_MASK;
			queue.postEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(),
					curModifiers, KeyEvent.VK_ALT, CHAR_UNDEFINED));
		}
		if (shift) {
			curModifiers &= ~SHIFT_DOWN_MASK;
			queue.postEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(),
					curModifiers, KeyEvent.VK_SHIFT, CHAR_UNDEFINED));
		}
	}

	@Override
	public void type(Component c, String text) {
		for (int i = 0; i < text.length(); i++) {
			type(c, 0, text.charAt(i));
		}
	}
}
