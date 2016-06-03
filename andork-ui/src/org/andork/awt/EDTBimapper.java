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
package org.andork.awt;

import org.andork.func.Bimapper;
import org.andork.swing.FromEDT;

public class EDTBimapper<I, O> extends EDTMapper<I, O> implements Bimapper<I, O> {
	public static <I, O> EDTBimapper<I, O> newInstance(Bimapper<I, O> wrapped) {
		return new EDTBimapper<I, O>(wrapped);
	}

	private Bimapper<I, O> wrapped;

	public EDTBimapper(Bimapper<I, O> wrapped) {
		super(wrapped);
		this.wrapped = wrapped;
	}

	@Override
	public I unmap(final O out) {
		return new FromEDT<I>() {
			@Override
			public I run() throws Throwable {
				return wrapped.unmap(out);
			}
		}.result();
	}
}
