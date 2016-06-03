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

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.ComboPopup;

/**
 * "Nabs" something (generally a component) found from an AWT event. This is a
 * brute force process using an {@link AWTEventListener}, hence the term "nab".
 * II invented it to find {@link ComboPopup}s for automated testing, since I
 * wasn't aware of any other way.
 *
 * @author andy.edwards
 *
 * @param <C>
 *            the type of object to be nabbed
 */
public abstract class AWTNabber<C> {
	private class Listener implements AWTEventListener {
		@Override
		public void eventDispatched(AWTEvent event) {
			final C nabbed = nab(event);
			if (nabbed != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setNabbed(nabbed);
						Toolkit.getDefaultToolkit().removeAWTEventListener(Listener.this);
					}
				});
			}
		}
	}

	private final Object lock = new Object();
	private final Listener listener = new Listener();
	private boolean listening = false;
	private C nabbed;

	private long eventMask = AWTEvent.FOCUS_EVENT_MASK | AWTEvent.PAINT_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK;

	/**
	 * Gets the nabbed object, blocking if necessary until something is nabbed.
	 *
	 * @return the nabbed object.
	 * @throws InterruptedException
	 *             if the calling thread was interrupted while waiting for
	 *             something to be nabbed.
	 */
	public C getNabbed() throws InterruptedException {
		C result;
		synchronized (lock) {
			if (!listening) {
				throw new IllegalStateException("Not currently listening");
			}
			while (this.nabbed == null) {
				lock.wait();
			}

			result = this.nabbed;
			listening = false;
		}

		return result;
	}

	/**
	 * Nabs something found via an {@link AWTEvent}, for example the list of a
	 * {@code ComboPopup}.
	 *
	 * @param event
	 *            the event to nab from.
	 * @return the nabbed object, or {@code null} if nothing was nabbed from the
	 *         given event.
	 */
	protected abstract C nab(AWTEvent event);

	private void setNabbed(C result) {
		synchronized (lock) {
			this.nabbed = result;
			lock.notifyAll();
		}
	}

	/**
	 * Starts listening to {@link AWTEvent}s and looking for a result with
	 * {@link #nab(AWTEvent)}. Once {@code nab()} returns a non-null value, it
	 * will be saved and the {@link AWTEventListener} will be removed.
	 */
	public void startNabbing() {
		synchronized (lock) {
			if (listening) {
				throw new IllegalStateException("Already listening");
			}
			listening = true;
			nabbed = null;
		}

		Toolkit.getDefaultToolkit().addAWTEventListener(listener, eventMask);
	}
}
