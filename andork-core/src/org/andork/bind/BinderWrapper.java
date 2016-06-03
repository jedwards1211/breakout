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
package org.andork.bind;

import org.andork.util.Java7;

public class BinderWrapper<T> extends Binder<T> {
	private Binder<T> wrapped;
	private T value;

	public BinderWrapper() {

	}

	public BinderWrapper(Binder<T> wrapped) {
		bind(wrapped);
	}

	public BinderWrapper<T> bind(Binder<T> wrapped) {
		if (this.wrapped != wrapped) {
			if (this.wrapped != null) {
				unbind0(this.wrapped, this);
			}
			this.wrapped = wrapped;
			if (wrapped != null) {
				bind0(this.wrapped, this);
			}

			update(false);
		}
		return this;
	}

	@Override
	public T get() {
		return value;
	}

	protected void onValueChanged(T newValue) {
	}

	@Override
	public void set(T newValue) {
		if (wrapped != null) {
			wrapped.set(newValue);
		}
	}

	public void unbind() {
		bind(null);
	}

	@Override
	public void update(boolean force) {
		T newValue = wrapped == null ? null : wrapped.get();
		if (force || !Java7.Objects.equals(value, newValue)) {
			value = newValue;
			onValueChanged(newValue);
			updateDownstream(force);
		}
	}
}
