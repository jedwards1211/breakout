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

import org.andork.func.Bimapper;

public class BimapperBinder<I, O> extends Binder<O> {
	public static <I, O> BimapperBinder<I, O> bind(Bimapper<I, O> bimapper, Binder<I> inBinder) {
		return new BimapperBinder<I, O>(bimapper).bind(inBinder);
	}

	public static <I, O> BimapperBinder<I, O> newInstance(Bimapper<I, O> bimapper) {
		return new BimapperBinder<I, O>(bimapper);
	}

	Binder<I> inBinder;

	Bimapper<I, O> bimapper;

	O out;

	public BimapperBinder(Bimapper<I, O> bimapper) {
		super();
		this.bimapper = bimapper;
	}

	public BimapperBinder<I, O> bind(Binder<I> inBinder) {
		if (this.inBinder != inBinder) {
			if (this.inBinder != null) {
				unbind0(inBinder, this);
			}
			this.inBinder = inBinder;
			if (inBinder != null) {
				bind0(inBinder, this);
			}
			update(false);
		}
		return this;
	}

	@Override
	public O get() {
		return out;
	}

	@Override
	public void set(O newValue) {
		if (inBinder != null && bimapper != null) {
			inBinder.set(bimapper.unmap(newValue));
		}
	}

	public void unbind() {
		bind(null);
	}

	@Override
	public void update(boolean force) {
		I in = inBinder == null ? null : inBinder.get();
		O newOut = in == null || bimapper == null ? null : bimapper.map(in);

		if (force || out != newOut) {
			out = newOut;
			updateDownstream(false);
		}
	}
}
