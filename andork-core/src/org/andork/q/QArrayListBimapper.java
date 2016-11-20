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
package org.andork.q;

import java.util.List;

import org.andork.collect.CollectionUtils;
import org.andork.func.Bimapper;
import org.andork.func.Lodash;

public class QArrayListBimapper<I, O> implements Bimapper<QArrayList<I>, List<O>> {
	public static <I, O> QArrayListBimapper<I, O> newInstance(Bimapper<I, O> elemBimapper) {
		return new QArrayListBimapper<I, O>(elemBimapper);
	}

	Bimapper<I, O> elemBimapper;

	private QArrayListBimapper(Bimapper<I, O> elemBimapper) {
		super();
		this.elemBimapper = elemBimapper;
	}

	@Override
	public List<O> map(QArrayList<I> in) {
		return in == null ? null : CollectionUtils.toArrayList(Lodash.map(in, e -> elemBimapper.map(e)));
	}

	@Override
	public QArrayList<I> unmap(List<O> out) {
		if (out == null) {
			return null;
		}
		QArrayList<I> result = QArrayList.newInstance();
		for (O o : out) {
			result.add(elemBimapper.unmap(o));
		}
		return result;
	}
}
