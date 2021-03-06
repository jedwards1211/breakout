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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CompoundBimapper<I, O> extends CompoundMapper<I, O> implements Bimapper<I, O> {
	public static <I, M, O> CompoundBimapper<I, O> compose(Bimapper<I, M> m0, Bimapper<M, O> m1) {
		return new CompoundBimapper<I, O>(m0, m1);
	}

	protected CompoundBimapper(Bimapper m0, Bimapper m1) {
		super(m0, m1);
	}

	@Override
	public I unmap(O out) {
		return (I) ((Bimapper) m0).unmap(((Bimapper) m1).unmap(out));
	}
}
