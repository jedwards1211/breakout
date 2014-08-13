package org.andork.bind2.ui;

import java.awt.CardLayout;
import java.awt.Container;

import org.andork.bind2.Binding;
import org.andork.bind2.Link;

public class CardLayoutBinding implements Binding {
	public final Link<String>	nameLink	= new Link<String>(this);
	final CardLayout			layout;
	final Container				target;

	public CardLayoutBinding(CardLayout layout, Container target) {
		super();
		this.layout = layout;
		this.target = target;
	}

	public void update(boolean force) {
		layout.show(target, nameLink.get());
	}
}
