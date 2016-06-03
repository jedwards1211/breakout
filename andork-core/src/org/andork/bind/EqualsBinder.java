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

public class EqualsBinder<T> extends Binder<Boolean> {
	public static <T> EqualsBinder<T> bindEquals(T trueValue, Binder<T> upstream) {
		return new EqualsBinder<T>(trueValue).bind(upstream);
	}

	Binder<T> upstream;
	T trueValue;

	boolean value;

	public EqualsBinder(T trueValue) {
		this.trueValue = trueValue;
	}

	public EqualsBinder<T> bind(Binder<T> upstream) {
		if (this.upstream != upstream) {
			if (this.upstream != null) {
				unbind0(this.upstream, this);
			}
			this.upstream = upstream;
			if (upstream != null) {
				bind0(this.upstream, this);
			}

			update(false);
		}
		return this;
	}

	@Override
	public Boolean get() {
		return value;
	}

	@Override
	public void set(Boolean newValue) {
		if (upstream != null && Boolean.TRUE.equals(newValue)) {
			upstream.set(trueValue);
		}
	}

	@Override
	public void update(boolean force) {
		T upstreamValue = upstream == null ? null : upstream.get();
		boolean newValue = Java7.Objects.equals(upstreamValue, trueValue);
		if (force || newValue != value) {
			value = newValue;
			updateDownstream(force);
		}
	}
}
