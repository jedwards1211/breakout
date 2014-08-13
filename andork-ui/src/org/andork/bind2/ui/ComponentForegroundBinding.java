package org.andork.bind2.ui;

import java.awt.Color;
import java.awt.Component;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class ComponentForegroundBinding implements Binding {
	public final Link<Color>	foregroundLink	= new Link<Color>(this);
	public final Component		target;

	public ComponentForegroundBinding(Component target) {
		this.target = target;
	}

	public void update(boolean force) {
		if (foregroundLink.get() != null) {
			target.setForeground(foregroundLink.get());
		}
	}
}
