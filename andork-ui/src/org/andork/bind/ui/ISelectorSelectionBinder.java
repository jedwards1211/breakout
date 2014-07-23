/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.bind.ui;

import org.andork.bind.Binder;
import org.andork.swing.selector.ISelector;
import org.andork.swing.selector.ISelectorListener;
import org.andork.util.Java7;

public class ISelectorSelectionBinder<T> extends Binder<T> implements ISelectorListener<T> {
	Binder<T>		upstream;
	ISelector<T>	selector;

	public ISelectorSelectionBinder(ISelector<T> selector) {
		this.selector = selector;
		if (this.selector != null) {
			this.selector.addSelectorListener(this);
		}
	}

	public static <T> ISelectorSelectionBinder<T> bind(ISelector<T> selector, Binder<T> upstream) {
		return new ISelectorSelectionBinder<T>(selector).bind(upstream);
	}

	public ISelectorSelectionBinder<T> bind(Binder<T> upstream) {
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

	boolean	updating;

	@Override
	public T get() {
		return selector == null ? null : selector.getSelection();
	}

	@Override
	public void set(T newValue) {
		if (selector != null) {
			selector.setSelection(newValue);
		}
	}

	@Override
	public void update(boolean force) {
		updating = true;
		try {
			T newValue = upstream == null ? null : upstream.get();
			if (force || !Java7.Objects.equals(get(), newValue)) {
				set(newValue);
			}
		} finally {
			updating = false;
		}
	}

	@Override
	public void selectionChanged(ISelector<T> selector, T oldSelection, T newSelection) {
		if (!updating && upstream != null && selector == this.selector) {
			upstream.set(get());
		}
	}
}
