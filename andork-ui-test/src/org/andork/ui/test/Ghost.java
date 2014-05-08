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
	public void type(final Component c, String text);

	public void scroll(final Component c, Rectangle toBounds);

	public InputBuilder on(Component c);

	public abstract class InputBuilder {
		Component	comp;
		Point		mouseLocation;

		int			modifiers;

		public InputBuilder(Component comp) {
			super();
			this.comp = comp;
			mouseLocation = new Point(comp.getWidth() / 2, comp.getHeight() / 2);
		}

		public InputBuilder ctrl() {
			modifiers |= CTRL_DOWN_MASK;
			return this;
		}

		public InputBuilder shift() {
			modifiers |= SHIFT_DOWN_MASK;
			return this;
		}

		public InputBuilder alt() {
			modifiers |= ALT_DOWN_MASK;
			return this;
		}

		public InputBuilder enter() {
			return type(VK_ENTER);
		}

		public InputBuilder backspace() {
			return type(VK_BACK_SPACE);
		}

		public InputBuilder delete() {
			return type(VK_DELETE);
		}

		public abstract InputBuilder press(int keyCode);

		public abstract InputBuilder type(int keyCode);

		public abstract InputBuilder release(int keyCode);

		public InputBuilder at(Point mouseLocation) {
			if (mouseLocation == null) {
				throw new IllegalArgumentException("mouseLocation must be non-null");
			}
			this.mouseLocation = mouseLocation;
			return this;
		}

		public InputBuilder leftClick() {
			return click(BUTTON1, 1);
		}

		public InputBuilder middleClick() {
			return click(BUTTON2, 1);
		}

		public InputBuilder rightClick() {
			return click(BUTTON3, 1);
		}

		public abstract InputBuilder click(int button, int clickCount);
	}
}
