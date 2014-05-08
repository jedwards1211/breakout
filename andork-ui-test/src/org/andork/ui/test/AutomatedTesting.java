package org.andork.ui.test;

import org.andork.swing.DoSwing;


public class AutomatedTesting {
	/**
	 * Waits for all events previously added to the AWT event queue to be
	 * processed (excluding events that are blocked by modal dialogs).
	 */
	public static void flushEDT() {
		new DoSwing() {
			public void run() {

			}
		};
	}
}
