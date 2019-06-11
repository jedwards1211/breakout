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

import org.andork.func.TriFunction;
import org.andork.util.Java7;

public class TriFunctionBinder<T, U, V, R> extends Binder<R> {
	private Binder<T> upstreamT;
	private Binder<? extends U> upstreamU;
	private Binder<? extends V> upstreamV;
	private TriFunction<T, U, V, R> fn;
	private TriFunction<R, U, V, T> inverseFn;

	private R value;

	public TriFunctionBinder(TriFunction<T, U, V, R> fn, TriFunction<R, U, V, T> inverseFn) {
		this.fn = fn;
		this.inverseFn = inverseFn;
	}

	public TriFunctionBinder<T, U, V, R> bind(Binder<T> upstreamT, Binder<? extends U> upstreamU, Binder<? extends V> upstreamV) {
		boolean needsUpdate = this.upstreamT != upstreamT || this.upstreamU != upstreamU || this.upstreamV != upstreamV;
		if (this.upstreamT != upstreamT) {
			if (this.upstreamT != null) {
				unbind0(this.upstreamT, this);
			}
			this.upstreamT = upstreamT;
			if (this.upstreamT != null) {
				bind0(this.upstreamT, this);
			}
		}
		if (this.upstreamU != upstreamU) {
			if (this.upstreamU != null) {
				unbind0(this.upstreamU, this);
			}
			this.upstreamU = upstreamU;
			if (this.upstreamU != null) {
				bind0(this.upstreamU, this);
			}		
		}
		if (this.upstreamV != upstreamV) {
			if (this.upstreamV != null) {
				unbind0(this.upstreamV, this);
			}
			this.upstreamV = upstreamV;
			if (this.upstreamV != null) {
				bind0(this.upstreamV, this);
			}		
		}
		if (needsUpdate) {
			update(false);
		}
		return this;
	}

	@Override
	public R get() {
		return value;
	}

	@Override
	public void set(R newValue) {
		if (Java7.Objects.equals(this.value, newValue)) return;
		this.value = newValue;
		this.upstreamT.set(inverseFn.apply(newValue, upstreamU.get(), upstreamV.get()));
	}

	public void unbind() {
		bind(null, null, null);
	}

	@Override
	public void update(boolean force) {
		T t = upstreamT == null ? null : upstreamT.get();
		U u = upstreamU == null ? null : upstreamU.get();
		V v = upstreamV == null ? null : upstreamV.get();
		R newValue = fn.apply(t, u, v);
		if (force || !Java7.Objects.equals(value, newValue)) {
			value = newValue;
			updateDownstream(force);
		}
	}
}
