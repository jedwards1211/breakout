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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;

/**
 * A {@link Ghost} wrapper that automatically delays between events (introduces
 * the same auto-delay behavior as in {@link Robot} to the {@link Ghost}
 * paradigm).
 *
 * @author andy.edwards
 */
public class SlowGhost implements Ghost {
	private static final int MAX_DELAY = 60000;
	Ghost wrapped;
	private boolean isAutoWaitForIdle = false;
	private int autoDelay = 0;

	public SlowGhost(Ghost wrapped, boolean isAutoWaitForIdle, int autoDelay) {
		super();
		this.wrapped = wrapped;
		this.isAutoWaitForIdle = isAutoWaitForIdle;
		this.autoDelay = autoDelay;
	}

	/*
	 * Called after an event is generated
	 */
	private void afterEvent() {
		autoWaitForIdle();
		autoDelay();
	}

	/*
	 * Automatically sleeps for the specified interval after event generated.
	 */
	private void autoDelay() {
		delay(autoDelay);
	}

	/*
	 * Calls waitForIdle after every event if so desired.
	 */
	private void autoWaitForIdle() {
		if (isAutoWaitForIdle) {
			AutomatedTesting.flushEDT();
		}
	}

	private void checkDelayArgument(int ms) {
		if (ms < 0 || ms > MAX_DELAY) {
			throw new IllegalArgumentException("Delay must be to 0 to 60,000ms");
		}
	}

	/**
	 * Sleeps for the specified time. To catch any
	 * <code>InterruptedException</code>s that occur,
	 * <code>Thread.sleep()</code> may be used instead.
	 *
	 * @param ms
	 *            time to sleep in milliseconds
	 * @throws IllegalArgumentException
	 *             if <code>ms</code> is not between 0 and 60,000 milliseconds
	 *             inclusive
	 * @see java.lang.Thread#sleep
	 */
	public void delay(int ms) {
		checkDelayArgument(ms);
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ite) {
			ite.printStackTrace();
		}
	}

	/**
	 * Returns the number of milliseconds this SlowGhost sleeps after generating
	 * an event.
	 */
	public synchronized int getAutoDelay() {
		return autoDelay;
	}

	/**
	 * Returns whether this SlowGhost automatically invokes
	 * <code>waitForIdle</code> after generating an event.
	 *
	 * @return Whether <code>waitForIdle</code> is automatically called
	 */
	public synchronized boolean isAutoWaitForIdle() {
		return isAutoWaitForIdle;
	}

	@Override
	public InputBuilder on(Component c) {
		return new InputBuilder(c) {
			InputBuilder wrapped = SlowGhost.this.wrapped.on(comp);

			@Override
			public InputBuilder alt() {
				wrapped.alt();
				return this;
			}

			@Override
			public InputBuilder at(Point mouseLocation) {
				wrapped.at(mouseLocation);
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder backspace() {
				wrapped.backspace();
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder click(int button, int clickCount) {
				wrapped.click(button, clickCount);
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder ctrl() {
				wrapped.ctrl();
				return this;
			}

			@Override
			public InputBuilder delete() {
				wrapped.delete();
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder enter() {
				wrapped.enter();
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder leftClick() {
				wrapped.leftClick();
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder middleClick() {
				wrapped.middleClick();
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder press(int keyCode) {
				wrapped.press(keyCode);
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder release(int keyCode) {
				wrapped.release(keyCode);
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder rightClick() {
				wrapped.rightClick();
				afterEvent();
				return this;
			}

			@Override
			public InputBuilder shift() {
				wrapped.shift();
				return this;
			}

			@Override
			public InputBuilder type(int keyCode) {
				wrapped.type(keyCode);
				afterEvent();
				return this;
			}
		};
	}

	@Override
	public synchronized void scroll(Component c, Rectangle toBounds) {
		wrapped.scroll(c, toBounds);
		afterEvent();
	}

	/**
	 * Sets the number of milliseconds this SlowGhost sleeps after generating an
	 * event.
	 *
	 * @throws IllegalArgumentException
	 *             If <code>ms</code> is not between 0 and 60,000 milliseconds
	 *             inclusive
	 */
	public synchronized void setAutoDelay(int ms) {
		checkDelayArgument(ms);
		autoDelay = ms;
	}

	/**
	 * Sets whether this SlowGhost automatically invokes
	 * <code>waitForIdle</code> after generating an event.
	 *
	 * @param isOn
	 *            Whether <code>waitForIdle</code> is automatically invoked
	 */
	public synchronized void setAutoWaitForIdle(boolean isOn) {
		isAutoWaitForIdle = isOn;
	}

	@Override
	public synchronized void type(Component c, String text) {
		wrapped.type(c, text);
		afterEvent();
	}
}
