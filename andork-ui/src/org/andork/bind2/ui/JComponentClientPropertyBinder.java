package org.andork.bind2.ui;

import java.awt.Component;

import javax.swing.JComponent;

public class JComponentClientPropertyBinder extends ComponentPropertyBinder {
	@Override
	public void bind(Component component, String property) {
		if (!(component instanceof JComponent)) {
			throw new IllegalArgumentException("component must be a JComponent");
		}
		super.bind(component, property);
	}

	@Override
	public Object get() {
		JComponent comp = getComponent();
		String property = getProperty();
		return comp != null && property != null ? comp.getClientProperty(property) : null;
	}

	@Override
	public JComponent getComponent() {
		return (JComponent) super.getComponent();
	}
}
