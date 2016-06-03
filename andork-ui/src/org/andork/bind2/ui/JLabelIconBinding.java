package org.andork.bind2.ui;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class JLabelIconBinding implements Binding {
	public final Link<Icon> iconLink = new Link<Icon>(this);
	public final JLabel label;

	public JLabelIconBinding(JLabel label) {
		super();
		this.label = label;
	}

	@Override
	public void update(boolean force) {
		label.setIcon(iconLink.get());
	}
}
