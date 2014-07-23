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

import java.util.ArrayList;
import java.util.List;

public class FloatArray2ListBimapper implements Bimapper<float[], List<? extends Number>> {
	public static final FloatArray2ListBimapper	instance	= new FloatArray2ListBimapper();

	private FloatArray2ListBimapper() {

	}

	@Override
	public List<Float> map(float[] in) {
		List<Float> result = new ArrayList<Float>();
		for (float f : in) {
			result.add(f);
		}
		return result;
	}

	@Override
	public float[] unmap(List<? extends Number> out) {
		float[] result = new float[out.size()];
		int k = 0;
		for (Number f : out) {
			result[k++] = f.floatValue( );
		}
		return result;
	}

}
