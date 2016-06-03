package org.andork.bind2.ui;

import javax.swing.JComponent;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class JComponentToolTipBinding implements Binding {
	public final Link<String> textLink = new Link<String>(this);
	public final JComponent component;

	public JComponentToolTipBinding(JComponent component) {
		super();
		this.component = component;
	}

	@Override
	public void update(boolean force) {
		component.setToolTipText(textLink.get());
	}
}
