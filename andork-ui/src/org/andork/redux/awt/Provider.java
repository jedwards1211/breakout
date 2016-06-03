package org.andork.redux.awt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import org.andork.redux.Store;

public class Provider extends Container {
	/**
	 *
	 */
	private static final long serialVersionUID = -6849202639075735732L;

	private final Store store;

	public Provider(Store store, Component comp) {
		setLayout(new BorderLayout());
		this.store = store;
		if (comp != null) {
			add(comp, BorderLayout.CENTER);
		}
	}

	public Store getStore() {
		return store;
	}
}
