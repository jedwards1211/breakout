package org.andork.swing.async;

import java.awt.Component;

import org.andork.awt.layout.DrawerHolder;

public abstract class DrawerPinningTask<R> extends SelfReportingTask<R> {
	private DrawerHolder drawerHolder;

	public DrawerPinningTask(Component dialogParent, DrawerHolder drawerHolder) {
		super(dialogParent);
		this.drawerHolder = drawerHolder;
	}

	@Override
	protected final R work() throws Exception {
		try {
			drawerHolder.hold(this);
			return super.work();
		} finally {
			drawerHolder.release(this);
		}

	}
}
