/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.bind.ui;

import java.awt.Container;

import org.andork.awt.layout.BetterCardLayout;
import org.andork.bind.Binder;
import org.andork.util.Java7;

public class BetterCardLayoutBinder extends Binder<Object> {
	public static BetterCardLayoutBinder bind(Container parent, BetterCardLayout layout, Binder<?> upstream) {
		return new BetterCardLayoutBinder(parent, layout).bind(upstream);
	}

	Binder<?> upstream;
	Container parent;

	BetterCardLayout layout;

	boolean updating;

	public BetterCardLayoutBinder(Container parent, BetterCardLayout layout) {
		this.parent = parent;
		this.layout = layout;
	}

	public BetterCardLayoutBinder bind(Binder<?> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind0(this.upstream, this);
			}
			this.upstream = upstream;
			if (this.upstream != null) {
				bind0(this.upstream, this);
			}

			update(false);
		}
		return this;
	}

	@Override
	public Object get() {
		return layout.getCurrentCardKey();
	}

	@Override
	public void set(Object newValue) {
		if (layout != null) {
			layout.show(parent, newValue);
		}
	}

	@Override
	public void update(boolean force) {
		updating = true;
		try {
			Object newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}
}
