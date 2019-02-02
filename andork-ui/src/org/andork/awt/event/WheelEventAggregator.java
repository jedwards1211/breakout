package org.andork.awt.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

public class WheelEventAggregator extends MouseAdapter {
	boolean queued = false;
	double queuedRotation = 0.0;
	Callback callback;

	public WheelEventAggregator(Callback callback) {
		this.callback = callback;
	}
	
	public static interface Callback {
		public void mouseWheelMoved(double rotation);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double rotation = e.getPreciseWheelRotation();
		if ((rotation >= 0) != (queuedRotation >= 0)) {
			queuedRotation = 0;
		}
		queuedRotation += rotation;
		
		if (!queued) {
			queued = true;
			SwingUtilities.invokeLater(() -> {
				double totalRotation = queuedRotation;
				queuedRotation = 0f;
				queued = false;
				callback.mouseWheelMoved(totalRotation);
			});
		}
	}
}
