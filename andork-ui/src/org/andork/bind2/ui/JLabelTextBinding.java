package org.andork.bind2.ui;

import javax.swing.JLabel;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class JLabelTextBinding implements Binding {
	public final Link<String> textLink = new Link<String>(this);
	public final JLabel label;

	public JLabelTextBinding(JLabel label) {
		super();
		this.label = label;
	}

	@Override
	public void update(boolean force) {
		label.setText(textLink.get());
	}
}
