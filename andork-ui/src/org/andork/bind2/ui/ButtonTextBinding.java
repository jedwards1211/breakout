package org.andork.bind2.ui;

import javax.swing.AbstractButton;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class ButtonTextBinding implements Binding {
	public final Link<String>	textLink	= new Link<String>(this);
	public final AbstractButton	button;

	public ButtonTextBinding(AbstractButton button) {
		super();
		this.button = button;
	}

	public void update(boolean force) {
		button.setText(textLink.get());
	}
}
