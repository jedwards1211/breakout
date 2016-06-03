package org.andork.bind2.ui;

import javax.swing.JSpinner;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class JSpinnerValueBinding implements Binding {
	public final Link<Object> valueLink = new Link<Object>(this);
	public final JSpinner spinner;

	public JSpinnerValueBinding(JSpinner spinner) {
		super();
		this.spinner = spinner;
	}

	@Override
	public void update(boolean force) {
		spinner.setValue(valueLink.get());
	}
}
