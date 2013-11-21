package org.andork.ui;

import javax.swing.SwingUtilities;

public class CheckEDT {
	public static void checkEDT() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("Must be called from EDT");
		}
	}
}
