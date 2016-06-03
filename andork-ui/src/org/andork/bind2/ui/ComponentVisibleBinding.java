package org.andork.bind2.ui;

import java.awt.Component;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class ComponentVisibleBinding implements Binding {
	public final Link<Boolean> visibleLink = new Link<Boolean>(this);
	public final Component target;

	public ComponentVisibleBinding(Component target) {
		this.target = target;
	}

	@Override
	public void update(boolean force) {
		if (visibleLink.get() != null) {
			target.setVisible(visibleLink.get());
		}
	}
}
