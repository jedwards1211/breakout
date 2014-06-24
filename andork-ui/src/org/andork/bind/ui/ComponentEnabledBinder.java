package org.andork.bind.ui;

import java.awt.Component;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class ComponentEnabledBinder extends Binder<Boolean> {
	Binder<Boolean>	upstream;
	Component		component;
	boolean			updating;

	public ComponentEnabledBinder(Component component) {
		super();
		this.component = component;
	}

	public static ComponentEnabledBinder bind(Component component, Binder<Boolean> upstream) {
		return new ComponentEnabledBinder(component).bind(upstream);
	}

	public ComponentEnabledBinder bind(Binder<Boolean> upstream) {
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
	public Boolean get() {
		return component == null ? null : component.isEnabled();
	}

	@Override
	public void set(Boolean newValue) {
		if (component != null && newValue != null) {
			component.setEnabled(newValue);
		}
	}

	public void update(boolean force) {
		updating = true;
		try {
			Boolean newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}
}
