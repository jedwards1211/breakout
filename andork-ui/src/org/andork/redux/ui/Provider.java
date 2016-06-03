package org.andork.redux.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import org.andork.redux.Store;

public class Provider<S> extends Container {
	private static final long serialVersionUID = -6849202639075735732L;

	public static <S> Store<S> getStore(Component comp) {
		Container parent = comp.getParent();
		while (parent != null) {
			if (parent instanceof Provider) {
				@SuppressWarnings("unchecked")
				Provider<S> provider = (Provider<S>) parent;
				return provider.getStore();
			}
			parent = parent.getParent();
		}
		return null;
	}

	private final Store<S> store;

	public Provider(Store<S> store, Component comp) {
		setLayout(new BorderLayout());
		this.store = store;
		if (comp != null) {
			add(comp, BorderLayout.CENTER);
		}
	}

	public Store<S> getStore() {
		return store;
	}
}
