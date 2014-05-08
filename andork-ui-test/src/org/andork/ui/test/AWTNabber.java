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
	private final Object	lock		= new Object();
	private final Listener	listener	= new Listener();
	private boolean			listening	= false;
	private C				nabbed;
	private long			eventMask	= AWTEvent.FOCUS_EVENT_MASK | AWTEvent.PAINT_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK;

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

	private void setNabbed(C result) {
		synchronized (lock) {
			this.nabbed = result;
			lock.notifyAll();
		}
	}

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

	private class Listener implements AWTEventListener {
		public void eventDispatched(AWTEvent event) {
			final C nabbed = nab(event);
			if (nabbed != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setNabbed(nabbed);
						Toolkit.getDefaultToolkit().removeAWTEventListener(Listener.this);
					}
				});
			}
		}
	}
}
