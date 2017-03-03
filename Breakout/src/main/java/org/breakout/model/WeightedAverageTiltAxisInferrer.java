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

import java.util.Collection;

import org.andork.math3d.Vecmath;

public class WeightedAverageTiltAxisInferrer implements TiltAxisInferrer {

	@Override
	public float[] inferTiltAxis(Collection<? extends CalcShot> shots) {
		if (shots.isEmpty()) {
			return new float[] { 0f, -1f, 0f };
		}

		double xyAngle = 0.0;
		double zyAngle = 0.0;

		double totalXyWeight = 0.0;
		double totalZyWeight = 0.0;

		for (CalcShot shot : shots) {
			double x = shot.toStation.position[0] - shot.fromStation.position[0];
			double y = shot.toStation.position[1] - shot.fromStation.position[1];
			double z = shot.toStation.position[2] - shot.fromStation.position[2];

			double dxy = Math.sqrt(x * x + y * y);
			double dzy = Math.sqrt(z * z + y * y);

			xyAngle += dxy * Math.atan2(Math.signum(x) * y, Math.abs(x));
			totalXyWeight += dxy;
			zyAngle += dzy * Math.atan2(Math.signum(z) * y, Math.abs(z));
			totalZyWeight += dzy;
		}

		xyAngle /= totalXyWeight;
		zyAngle /= totalZyWeight;

		double[] xyNormal = { Math.cos(xyAngle), Math.sin(xyAngle), 0.0 };
		double[] zyProjection = { 0.0, -Math.cos(zyAngle), Math.sin(zyAngle) };

		double dot = Vecmath.dot3(xyNormal, zyProjection);

		float[] result = new float[] {
				(float) (zyProjection[0] - dot * xyNormal[0]),
				(float) (zyProjection[1] - dot * xyNormal[1]),
				(float) (zyProjection[2] - dot * xyNormal[2])
		};

		Vecmath.normalize3(result);

		return result;
	}
}
