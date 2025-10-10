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
package org.andork.func;

import java.util.function.Function;

public interface Bimapper<I, O> extends Mapper<I, O> {

	public I unmap(O out);

	public default Bimapper<O, I> invert() {
		Bimapper<I, O> self = this;
		return new Bimapper<O, I>() {
			@Override
			public I map(O in) {
				return self.unmap(in);
			}

			@Override
			public O unmap(I out) {
				return self.map(out);
			}
		};
	}

	public static <I, O> Bimapper<I, O> from(Function<I, O> map, Function<O, I> unmap) {
		return new Bimapper<I, O>() {
			@Override
			public O map(I in) {
				return map.apply(in);
			}

			@Override
			public I unmap(O out) {
				return unmap.apply(out);
			}
		};
	}

	public static <A, B, C> Bimapper<A, C> compose(Bimapper<A, B> m0, Bimapper<B, C> m1) {
		return compose(new Bimapper[] { m0, m1 });
	}

	public static <A, B, C, D> Bimapper<A, D> compose(Bimapper<A, B> m0, Bimapper<B, C> m1, Bimapper<C, D> m2) {
		return compose(new Bimapper[] { m0, m1, m2 });
	}

	public static <I, O> Bimapper<I, O> compose(@SuppressWarnings("rawtypes") Bimapper... mappers) {
		return new Bimapper<I, O>() {
			@SuppressWarnings("unchecked")
			@Override
			public O map(I in) {
				Object o = in;
				for (int i = 0; i < mappers.length; i++) {
					o = mappers[i].map(o);
				}
				return (O) o;
			}

			@SuppressWarnings("unchecked")
			@Override
			public I unmap(O out) {
				Object o = out;
				for (int i = mappers.length - 1; i >= 0; i--) {
					o = mappers[i].unmap(o);
				}
				return (I) o;
			}
		};
	}
}
