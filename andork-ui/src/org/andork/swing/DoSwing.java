package org.andork.swing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public abstract class DoSwing implements Runnable {
	public DoSwing() {
		doSwing(this);
	}

	public static void doSwing(Runnable r) {
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getCause());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
