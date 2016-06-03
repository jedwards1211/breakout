package org.andork.redux.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import org.andork.redux.Action;
import org.andork.redux.Dispatcher;
import org.andork.redux.Store;
import org.andork.swing.FromEDT;

public abstract class Connector<S> extends Container implements Dispatcher {
	private static final long serialVersionUID = 3101848419113180498L;

	private Store<S> store;
	private Runnable unsubscribe;

	public Connector(Component comp) {
		setLayout(new BorderLayout());
		if (comp != null) {
			add(comp, BorderLayout.CENTER);
		}
		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if ((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
					onParentChanged();
				}
			}
		});
	}

	@Override
	public Object dispatch(Action action) {
		return FromEDT.fromEDT(() -> {
			if (store != null) {
				return store.dispatch(action);
			}
			throw new IllegalStateException("not connected to a store");
		});
	}

	protected void onParentChanged() {
		Store<S> newStore = Provider.<S> getStore(this);
		if (newStore != store) {
			if (unsubscribe != null) {
				unsubscribe.run();
				unsubscribe = null;
			}
			store = newStore;
			if (store != null) {
				unsubscribe = store.subscribe(() -> {
					update(store.getState());
				});
				update(store.getState());
			}
		}
	}

	public abstract void update(S state);
}
