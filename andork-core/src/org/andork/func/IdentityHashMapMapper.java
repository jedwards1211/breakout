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
package org.andork.func;

import java.util.IdentityHashMap;

/**
 * A {@link Mapper} whose {@link #map(Object)} and {@link #unmap(Object)} methods will return
 * the same values for the same parameters, even if the wrapped {@link Mapper} returns different (but equal)
 * values for the same parameters.
 * 
 * @author andy.edwards
 */
public class IdentityHashMapMapper<I, O> implements Mapper<I, O> {
	private final Mapper<I, O>		wrapped;
	private final IdentityHashMap<I, O>	inToOut	= new IdentityHashMap<I, O>();

	public IdentityHashMapMapper(Mapper<I, O> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public O map(I in) {
		O result = inToOut.get(in);
		if (result == null) {
			inToOut.put(in, result = wrapped.map(in));
		}
		return result;
	}
}
