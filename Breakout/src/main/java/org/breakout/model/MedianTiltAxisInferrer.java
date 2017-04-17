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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.andork.math3d.Vecmath;
import org.breakout.model.calc.CalcShot;

public class MedianTiltAxisInferrer implements TiltAxisInferrer {

	@Override
	public float[] inferTiltAxis(Collection<? extends CalcShot> shots) {
		if (shots.isEmpty()) {
			return new float[] { 0f, -1f, 0f };
		}

		List<Double> xyAngles = new ArrayList<>();
		List<Double> zyAngles = new ArrayList<>();

		for (CalcShot shot : shots) {
			double x = shot.toStation.position[0] - shot.fromStation.position[0];
			double y = shot.toStation.position[1] - shot.fromStation.position[1];
			double z = shot.toStation.position[2] - shot.fromStation.position[2];

			xyAngles.add(Math.atan2(Math.signum(x) * y, Math.abs(x)));
			zyAngles.add(Math.atan2(Math.signum(z) * y, Math.abs(z)));
		}

		Collections.sort(xyAngles);
		Collections.sort(zyAngles);

		double xyAngle = xyAngles.get(xyAngles.size() / 2);
		double zyAngle = zyAngles.get(zyAngles.size() / 2);

		if (xyAngles.size() % 2 == 0) {
			xyAngle = (xyAngles.get(xyAngles.size() / 2 - 1) + xyAngle) * 0.5;
		}
		if (zyAngles.size() % 2 == 0) {
			zyAngle = (zyAngles.get(zyAngles.size() / 2 - 1) + zyAngle) * 0.5;
		}

		double[] xyNormal = { Math.cos(xyAngle), Math.sin(xyAngle), 0.0 };
		double[] zyProjection = { 0.0, -Math.cos(zyAngle), Math.sin(zyAngle) };

		double dot = Vecmath.dot3(xyNormal, zyProjection);

		return new float[] {
				(float) (zyProjection[0] - dot * xyNormal[0]),
				(float) (zyProjection[1] - dot * xyNormal[1]),
				(float) (zyProjection[2] - dot * xyNormal[2])
		};
	}
}
