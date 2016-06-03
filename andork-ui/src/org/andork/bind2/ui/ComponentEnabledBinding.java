package org.andork.bind2.ui;

import java.awt.Component;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class ComponentEnabledBinding implements Binding {
	public final Link<Boolean> enabledLink = new Link<Boolean>(this);
	public final Component target;

	public ComponentEnabledBinding(Component target) {
		this.target = target;
	}

	@Override
	public void update(boolean force) {
		if (enabledLink.get() != null) {
			target.setEnabled(enabledLink.get());
		}
	}
}
