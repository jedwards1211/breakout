package org.andork.bind2.ui;

import javax.swing.AbstractButton;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class ButtonSelectedBinding implements Binding {
	public final Link<Boolean> selectedLink = new Link<Boolean>(this);
	public final AbstractButton target;

	public ButtonSelectedBinding(AbstractButton target) {
		this.target = target;
	}

	@Override
	public void update(boolean force) {
		if (selectedLink.get() != null) {
			target.setSelected(selectedLink.get());
		}
	}
}
