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

public class OutCastingBimapper<I, O, OC> implements Bimapper<I, OC> {
	Bimapper<I, O>	wrapped;

	private OutCastingBimapper(Bimapper<I, O> bimapper) {
		this.wrapped = bimapper;
	}

	public static <I, O, OC> OutCastingBimapper<I, O, OC> cast(Bimapper<I, O> bimapper, Class<? super OC> outClass) {
		return new OutCastingBimapper<I, O, OC>(bimapper);
	}

	@Override
	public OC map(I in) {
		return (OC) wrapped.map(in);
	}

	@Override
	public I unmap(OC out) {
		return wrapped.unmap((O) out);
	}

}
