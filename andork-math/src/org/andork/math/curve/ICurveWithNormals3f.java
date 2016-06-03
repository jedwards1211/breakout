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
package org.andork.math.curve;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public interface ICurveWithNormals3f {

	public abstract Vector3f getBinormal(float param, Vector3f result);

	public abstract Vector3f getNormal(float param, Vector3f result);

	public abstract Point3f getPoint(float param, Point3f result);

	public abstract Vector3f getTangent(float param, Vector3f result);

}
