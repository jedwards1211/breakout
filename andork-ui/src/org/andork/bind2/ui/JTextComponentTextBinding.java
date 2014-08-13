package org.andork.bind2.ui;

import javax.swing.text.JTextComponent;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class JTextComponentTextBinding implements Binding {
	public final Link<String>	textLink	= new Link<String>(this);
	public final JTextComponent	label;

	public JTextComponentTextBinding(JTextComponent label) {
		super();
		this.label = label;
	}

	public void update(boolean force) {
		label.setText(textLink.get());
	}
}
