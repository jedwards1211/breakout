package org.andork.bind2.ui;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andork.bind2.Binder;

public class JSpinnerValueBinder extends Binder<Object> implements ChangeListener {
	private JSpinner spinner;

	public JSpinnerValueBinder() {
	}

	public void bind(JSpinner spinner) {
		if (this.spinner != spinner) {
			if (this.spinner != null) {
				this.spinner.removeChangeListener(this);
			}
			this.spinner = spinner;
			if (spinner != null) {
				spinner.addChangeListener(this);
			}
		}
	}

	@Override
	public Object get() {
		return spinner.getValue();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		updateBindings(false);
	}
}
