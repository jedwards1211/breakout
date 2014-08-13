package org.andork.bind2.ui;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.andork.bind2.Binder;

public abstract class ComponentPropertyBinder extends Binder<Object> implements PropertyChangeListener {
	private Component	component;
	private String		property;

	public ComponentPropertyBinder() {
	}

	public void bind(Component component, String property) {
		if (this.component != component || this.property != property) {
			if (this.component != null) {
				if (this.property != null) {
					this.component.removePropertyChangeListener(this.property, this);
				}
				else {
					this.component.removePropertyChangeListener(this);
				}
			}
			this.component = component;
			this.property = property;
			if (component != null) {
				if (property != null) {
					component.addPropertyChangeListener(property, this);
				} else {
					component.addPropertyChangeListener(this);
				}
			}
		}
	}

	public Component getComponent() {
		return component;
	}

	public String getProperty() {
		return property;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		updateBindings(false);
	}
}
