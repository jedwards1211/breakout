package org.andork.bind2.ui;

import javax.swing.AbstractButton;
import javax.swing.Icon;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class ButtonIconBinding implements Binding {
	public final Link<Icon> iconLink = new Link<Icon>(this);
	public final AbstractButton button;

	public ButtonIconBinding(AbstractButton button) {
		super();
		this.button = button;
	}

	@Override
	public void update(boolean force) {
		button.setIcon(iconLink.get());
	}
}
