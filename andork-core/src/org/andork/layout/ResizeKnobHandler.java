package org.andork.layout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResizeKnobHandler extends MouseAdapter {
	Component	target;

	Side		side;
	MouseEvent	pressEvent;
	Rectangle	pressBounds;

	public ResizeKnobHandler(Component target, Side side) {
		super();
		this.target = target;
		this.side = side;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			pressEvent = e;
			pressBounds = target.getBounds();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			pressEvent = null;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (pressEvent == null) {
			return;
		}

		Dimension newSize = pressBounds.getSize();

		Axis axis = side.axis();
		axis.setSize(newSize, axis.size(newSize) +
				(axis.get(e.getLocationOnScreen()) - axis.get(pressEvent.getLocationOnScreen())) /
				side.direction());

		Dimension minSize = target.getMinimumSize();
		Dimension maxSize = target.getMaximumSize();

		newSize.width = Math.max(minSize.width, Math.min(maxSize.width, newSize.width));
		newSize.height = Math.max(minSize.height, Math.min(maxSize.height, newSize.height));

		target.setPreferredSize(newSize);
		if (target.getParent() != null) {
			target.getParent().invalidate();
			target.getParent().validate();
		}
	}
}
