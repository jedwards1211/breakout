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
import java.util.Map;
import java.util.Objects;

import org.andork.func.Bimapper;
import org.andork.jogl.OrthoProjection;
import org.andork.jogl.PerspectiveProjection;
import org.andork.jogl.Projection;
import org.andork.util.ArrayUtils;

public class ProjectionCalculatorBimapper implements Bimapper<Projection, Object> {
	public static final ProjectionCalculatorBimapper instance = new ProjectionCalculatorBimapper();

	private static float getFloat(Map<Object, Object> map, Object key) {
		return ((Number) map.get(key)).floatValue();
	}

	private ProjectionCalculatorBimapper() {
	}

	public Map<Object, Object> map(OrthoProjection in) {
		Map<Object, Object> result = new LinkedHashMap<>();
		result.put("type", "ortho");
		result.put("hSpan", in.hSpan);
		result.put("vSpan", in.vSpan);
		result.put("zNear", in.zNear);
		result.put("zFar", in.zFar);
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
		} else if (in instanceof OrthoProjection) {
			return map((OrthoProjection) in);
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
			String type = Objects.toString(map.get("type"), null);
			if ("perspective".equals(type)) {
				return unmapPerspective(map);
			} else if ("ortho".equals(type)) {
				return unmapOrtho(map);
			}
			throw new IllegalArgumentException("unsupported type: " + type);
		}
		throw new IllegalArgumentException("Invalid type: " + out.getClass());
	}

	public OrthoProjection unmapOrtho(Map<Object, Object> map) {
		// previous versions using AutoClipOrthoProjection didn't write these fields
		Number zNear = (Number) map.get("zNear");
		Number zFar = (Number) map.get("zFar");
		return new OrthoProjection(
			getFloat(map, "hSpan"),
			getFloat(map, "vSpan"),
			zNear != null ? zNear.floatValue() : 1,
			zFar != null ? zFar.floatValue() : 1e7f
		);
	}

	public PerspectiveProjection unmapPerspective(Map<Object, Object> map) {
		return new PerspectiveProjection(
				getFloat(map, "fovAngle"),
				getFloat(map, "zNear"),
				getFloat(map, "zFar"));
	}
}
