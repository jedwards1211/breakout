package org.andork.bind.ui;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class ComponentBackgroundBinder extends Binder<Color> implements PropertyChangeListener {
	Binder<Color>	upstream;
	Component		component;
	boolean			updating;

	public ComponentBackgroundBinder(Component component) {
		super();
		this.component = component;
		if (component != null) {
			component.addPropertyChangeListener("background", this);
		}
	}

	public static ComponentBackgroundBinder bind(Component component, Binder<Color> upstream) {
		return new ComponentBackgroundBinder(component).bind(upstream);
	}

	public ComponentBackgroundBinder bind(Binder<Color> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind(this.upstream, this);
			}
			this.upstream = upstream;
			if (upstream != null) {
				bind(this.upstream, this);
			}
			update(false);
		}
		return this;
	}

	public void unbind() {
		bind(null);
	}

	@Override
	public Color get() {
		return component == null ? null : component.getBackground();
	}

	@Override
	public void set(Color newValue) {
		if (component != null && newValue != null) {
			component.setBackground(newValue);
		}
	}

	public void update(boolean force) {
		updating = true;
		try {
			Color newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!updating && upstream != null && evt.getSource() == component && "background".equals(evt.getPropertyName())) {
			upstream.set(get());
		}
	}
}
