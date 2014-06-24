package org.andork.bind.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSpinner;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class JSpinnerValueBinder<T> extends Binder<T> implements PropertyChangeListener {
	Class<T>	valueClass;
	Binder<T>	upstream;
	JSpinner	spinner;
	boolean		updating;

	public JSpinnerValueBinder(JSpinner spinner, Class<T> valueClass) {
		super();
		this.valueClass = valueClass;
		this.spinner = spinner;
		if (spinner != null) {
			spinner.addPropertyChangeListener("value", this);
		}
	}

	public static <T> JSpinnerValueBinder<T> bind(JSpinner spinner, Class<T> valueClass, Binder<T> upstream) {
		return new JSpinnerValueBinder<T>(spinner, valueClass).bind(upstream);
	}

	public JSpinnerValueBinder<T> bind(Binder<T> upstream) {
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
	public T get() {
		return spinner == null ? null : valueClass.cast(spinner.getValue());
	}

	@Override
	public void set(T newValue) {
		if (spinner != null && newValue != null) {
			spinner.setValue(newValue);
		}
	}

	public void update(boolean force) {
		updating = true;
		try {
			T newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!updating && upstream != null && evt.getSource() == spinner && "value".equals(evt.getPropertyName())) {
			upstream.set(get());
		}
	}
}
