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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CompoundMapper<I, O> implements Mapper<I, O>
{
	Mapper	m0;
	Mapper	m1;

	protected CompoundMapper(Mapper m0, Mapper m1)
	{
		this.m0 = m0;
		this.m1 = m1;
	}

	public static <I, M, O> CompoundMapper<I, O> compose(Mapper<I, M> m0, Mapper<M, O> m1)
	{
		return new CompoundMapper<I, O>(m0, m1);
	}

	@Override
	public O map(I in)
	{
		return (O) m1.map(m0.map(in));
	}

}
