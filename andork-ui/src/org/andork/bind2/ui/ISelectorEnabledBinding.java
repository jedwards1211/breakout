package org.andork.bind2.ui;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;
import org.andork.swing.selector.ISelector;

public class ISelectorEnabledBinding implements Binding {
	public final Link<Boolean> enabledLink = new Link<Boolean>(this);
	public final ISelector<?> target;

	public ISelectorEnabledBinding(ISelector<?> target) {
		this.target = target;
	}

	@Override
	public void update(boolean force) {
		if (enabledLink.get() != null) {
			target.setEnabled(enabledLink.get());
		}
	}
}
