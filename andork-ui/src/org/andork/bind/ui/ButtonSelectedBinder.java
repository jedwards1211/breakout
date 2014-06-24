package org.andork.bind.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractButton;

import org.andork.bind.Binder;
import org.andork.util.Java7;

public class ButtonSelectedBinder extends Binder<Boolean> implements ItemListener {
	Binder<Boolean>	upstream;
	AbstractButton	button;
	boolean			updating;

	public ButtonSelectedBinder(AbstractButton button) {
		super();
		this.button = button;
		if (button != null) {
			button.addItemListener(this);
		}
	}

	public static ButtonSelectedBinder bind(AbstractButton button, Binder<Boolean> upstream) {
		return new ButtonSelectedBinder(button).bind(upstream);
	}

	public ButtonSelectedBinder bind(Binder<Boolean> upstream) {
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
		return button == null ? null : button.isSelected();
	}

	@Override
	public void set(Boolean newValue) {
		if (button != null && newValue != null) {
			button.setSelected(newValue);
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

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (!updating && upstream != null && e.getSource() == button) {
			upstream.set(get());
		}
	}
}
