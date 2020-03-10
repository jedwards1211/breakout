package org.andork.bind.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class ButtonsSelectedBinder<T> extends Binder<T> implements ItemListener {
	final Map<AbstractButton, T> buttonToValue = new HashMap<>();

	ButtonGroup buttonGroup = new ButtonGroup();
	Binder<T> upstream;
	boolean updating;

	public ButtonsSelectedBinder<T> put(AbstractButton button, T value) {
		this.buttonToValue.put(button, value);
		button.addItemListener(this);
		buttonGroup.add(button);
		return this;
	}

	public ButtonsSelectedBinder<T> bind(Binder<T> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind0(this.upstream, this);
			}
			this.upstream = upstream;
			if (upstream != null) {
				bind0(this.upstream, this);
			}
			update(false);
		}
		return this;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (!updating && upstream != null) {
			upstream.set(get());
		}
	}

	@Override
	public T get() {
		for (Map.Entry<AbstractButton, T> entry : buttonToValue.entrySet()) {
			if (entry.getKey().isSelected())
				return entry.getValue();
		}
		return null;
	}

	@Override
	public void set(T newValue) {
		for (Map.Entry<AbstractButton, T> entry : buttonToValue.entrySet()) {
			entry.getKey().setSelected(Java7.Objects.equals(newValue, entry.getValue()));
		}
	}

	@Override
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

}
