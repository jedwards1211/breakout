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
package org.breakout.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andork.func.Bimapper;
import org.andork.func.FloatArray2ListBimapper;
import org.andork.math3d.Clip3f;

public class Clip3fBimapper implements Bimapper<Clip3f, Object> {
	public static final Clip3fBimapper instance = new Clip3fBimapper();

	private Clip3fBimapper() {

	}

	@Override
	public Map<String, Object> map(Clip3f in) {
		if (in == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<>();
		result.put("axis", FloatArray2ListBimapper.instance.map(in.axis()));
		result.put("near", in.near());
		result.put("far", in.far());
		return result;
	}

	@Override
	public Clip3f unmap(Object out) {
		if (out == null || !(out instanceof Map)) {
			return null;
		}
		Map<?, ?> m = (Map<?, ?>) out;
		float[] axis = FloatArray2ListBimapper.instance.unmap((List<Float>) m.get("axis"));
		float near = Float.parseFloat(String.valueOf(m.get("near")));
		float far = Float.parseFloat(String.valueOf(m.get("far")));
		return new Clip3f(axis, near, far);
	}
}
