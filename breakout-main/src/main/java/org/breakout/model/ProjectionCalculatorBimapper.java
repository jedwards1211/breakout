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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.andork.func.Bimapper;
import org.andork.jogl.AutoClipOrthoProjection;
import org.andork.jogl.PerspectiveProjection;
import org.andork.jogl.Projection;
import org.andork.math3d.Vecmath;
import org.andork.util.ArrayUtils;
import org.andork.util.StringUtils;

public class ProjectionCalculatorBimapper implements Bimapper<Projection, Object> {
	public static final ProjectionCalculatorBimapper instance = new ProjectionCalculatorBimapper();

	private static float getFloat(Map<Object, Object> map, Object key) {
		return ((Number) map.get(key)).floatValue();
	}

	private ProjectionCalculatorBimapper() {
	}

	public Map<Object, Object> map(AutoClipOrthoProjection in) {
		Map<Object, Object> result = new LinkedHashMap<>();
		result.put("type", "ortho");
		result.put("hSpan", in.hSpan);
		result.put("vSpan", in.vSpan);
		result.put("center", ArrayUtils.toArrayList(in.center));
		result.put("radius", in.radius);
		if (in.useNearClipPoint) {
			result.put("nearClipPoint", ArrayUtils.toArrayList(in.nearClipPoint));
		}
		if (in.useFarClipPoint) {
			result.put("farClipPoint", ArrayUtils.toArrayList(in.farClipPoint));
		}
		return result;
	}

	public Map<Object, Object> map(PerspectiveProjection in) {
		Map<Object, Object> result = new LinkedHashMap<>();
		result.put("type", "perspective");
		result.put("fovAngle", in.fovAngle);
		result.put("zNear", in.zNear);
		result.put("zFar", in.zFar);
		return result;
	}

	@Override
	public Object map(Projection in) {
		if (in == null) {
			return null;
		}

		if (in instanceof PerspectiveProjection) {
			return map((PerspectiveProjection) in);
		} else if (in instanceof AutoClipOrthoProjection) {
			return map((AutoClipOrthoProjection) in);
		}
		throw new IllegalArgumentException("Unsupported type: " + in.getClass());
	}

	@Override
	public Projection unmap(Object out) {
		if (out == null) {
			return null;
		}

		if (out instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) out;
			String type = StringUtils.toStringOrNull(map.get("type"));
			if ("perspective".equals(type)) {
				return unmapPerspective(map);
			} else if ("ortho".equals(type)) {
				return unmapOrtho(map);
			}
			throw new IllegalArgumentException("unsupported type: " + type);
		}
		throw new IllegalArgumentException("Invalid type: " + out.getClass());
	}

	public AutoClipOrthoProjection unmapOrtho(Map<Object, Object> map) {
		AutoClipOrthoProjection result = new AutoClipOrthoProjection();
		result.hSpan = getFloat(map, "hSpan");
		result.vSpan = getFloat(map, "vSpan");
		Vecmath.setf(result.center, ArrayUtils.toFloatArray2((List<Number>) map.get("center")));
		result.radius = getFloat(map, "radius");
		result.useNearClipPoint = map.containsKey("nearClipPoint");
		if (result.useNearClipPoint) {
			Vecmath.setf(result.nearClipPoint, ArrayUtils.toFloatArray2((List<Number>) map.get("nearClipPoint")));
		}
		result.useFarClipPoint = map.containsKey("farClipPoint");
		if (result.useFarClipPoint) {
			Vecmath.setf(result.farClipPoint, ArrayUtils.toFloatArray2((List<Number>) map.get("farClipPoint")));
		}
		return result;
	}

	public PerspectiveProjection unmapPerspective(Map<Object, Object> map) {
		return new PerspectiveProjection(
				getFloat(map, "fovAngle"),
				getFloat(map, "zNear"),
				getFloat(map, "zFar"));
	}
}
