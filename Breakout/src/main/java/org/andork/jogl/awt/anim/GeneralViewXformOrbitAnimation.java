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
package org.andork.jogl.awt.anim;

import static org.andork.math3d.Vecmath.cross;
import static org.andork.math3d.Vecmath.distance3;
import static org.andork.math3d.Vecmath.dot3;
import static org.andork.math3d.Vecmath.getColumn3;
import static org.andork.math3d.Vecmath.interp3;
import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.negate3;
import static org.andork.math3d.Vecmath.newMat4f;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.partDist;
import static org.andork.math3d.Vecmath.rotation;
import static org.andork.math3d.Vecmath.scaleAdd3;
import static org.andork.math3d.Vecmath.setd;
import static org.andork.math3d.Vecmath.setf;
import static org.andork.math3d.Vecmath.subDot3;

import org.andork.awt.anim.Animation;
import org.andork.jogl.JoglViewSettings;
import org.andork.math3d.LineLineIntersection2d;
import org.andork.math3d.LinePlaneIntersection3d;

import com.jogamp.opengl.GLAutoDrawable;

public class GeneralViewXformOrbitAnimation implements Animation {
	final float[] startXform = newMat4f();
	final float[] endXform = newMat4f();

	JoglViewSettings viewSettings;
	GLAutoDrawable drawable;

	long elapsedTime;
	long totalTime;
	long period;

	final double[] startLocation = new double[3];
	final double[] startRight = new double[3];
	final double[] startForward = new double[3];
	final double[] startUp = new double[3];
	final double[] startHorizontal = new double[3];

	final double[] endLocation = new double[3];
	final double[] endRight = new double[3];
	final double[] endForward = new double[3];
	final double[] endUp = new double[3];
	final double[] endHorizontal = new double[3];

	final double[] alignedStartLocation = new double[3];
	final double[] alignedStartForward = new double[3];

	final double[] alignedEndLocation = new double[3];
	final double[] alignedEndForward = new double[3];

	final double[] startOrigin = new double[3];
	final double[] alignedStartTiltOrigin = new double[3];
	double startPan;
	double startTilt;
	double startOffset;

	final double[] endOrigin = new double[3];
	final double[] alignedEndTiltOrigin = new double[3];
	double endPan;
	double endTilt;
	double endOffset;

	boolean linear;

	double totalPan;
	double totalTilt;

	final double[] origin = new double[3];
	final double[] alignedTiltOrigin = new double[3];
	final double[] alignedLocation = new double[3];
	final double[] alignedForward = new double[3];
	final double[] horizontal = new double[3];

	final float[] viewXform = newMat4f();

	final LineLineIntersection2d llx = new LineLineIntersection2d();
	final LinePlaneIntersection3d lpx = new LinePlaneIntersection3d();

	public GeneralViewXformOrbitAnimation(GLAutoDrawable drawable, JoglViewSettings viewSettings, long totalTime,
			long period) {
		this.viewSettings = viewSettings;
		this.drawable = drawable;
		this.totalTime = totalTime;
		this.period = period;
	}

	@Override
	public long animate(long animTime) {
		elapsedTime += animTime;

		calcViewXform(Math.min(1f, (double) elapsedTime / totalTime), viewXform);

		viewSettings.setViewXform(viewXform);

		drawable.display();

		return Math.min(Math.max(0, totalTime - elapsedTime), period);
	}

	public void calcViewXform(double progress, float[] outXform) {
		if (linear) {
			interp3(startLocation, endLocation, progress, outXform, 12);
			interp3(startRight, endRight, progress, outXform, 0);
			interp3(startForward, endForward, progress, outXform, 8);
			negate3(outXform, 8);
		} else {
			double pan = startPan + totalPan * progress;
			double tilt = startTilt + totalTilt * progress;
			double offset = startOffset * (1 - progress) + endOffset * progress;

			interp3(alignedStartTiltOrigin, alignedEndTiltOrigin, progress, alignedTiltOrigin);
			alignedForward[0] = Math.cos(tilt);
			alignedForward[1] = Math.sin(tilt);
			scaleAdd3(offset, alignedForward, alignedTiltOrigin, alignedLocation);

			horizontal[0] = Math.cos(pan);
			horizontal[2] = Math.sin(pan);

			interp3(startOrigin, endOrigin, progress, origin);

			// new location
			outXform[12] = (float) (origin[0] + horizontal[0] * alignedLocation[0]);
			outXform[13] = (float) alignedLocation[1];
			outXform[14] = (float) (origin[2] + horizontal[2] * alignedLocation[0]);

			// new backward
			outXform[8] = (float) (horizontal[0] * -alignedForward[0]);
			outXform[9] = (float) -alignedForward[1];
			outXform[10] = (float) (horizontal[2] * -alignedForward[0]);

			// new right
			outXform[0] = (float) -horizontal[2];
			outXform[1] = 0;
			outXform[2] = (float) horizontal[0];
		}

		// new up = backward X right
		cross(outXform, 8, outXform, 0, outXform, 4);

		invAffine(outXform);
	}

	private void restOfSetUp() {
		if (Math.abs(dot3(startRight, endRight) - 1f) < 1e-6f
				|| Math.abs(dot3(startForward, endForward) - 1f) < 1e-6f) {
			linear = true;
			return;
		}

		linear = false;

		cross(0, 1, 0, startRight, startHorizontal);
		cross(0, 1, 0, endRight, endHorizontal);

		startPan = Math.atan2(startHorizontal[2], startHorizontal[0]);
		endPan = Math.atan2(endHorizontal[2], endHorizontal[0]);

		totalPan = rotation(startPan, endPan);

		llx.setUp(startLocation, startHorizontal, endLocation, endHorizontal, 0, 2);

		double minRotation = 1e-6;

		if (llx.findIntersection() && Math.abs(totalPan) > minRotation && Math.PI - Math.abs(totalPan) > minRotation) {
			alignedStartLocation[0] = -llx.t0;
			alignedEndLocation[0] = -llx.t1;

			startOrigin[0] = endOrigin[0] = llx.x[0];
			startOrigin[2] = endOrigin[2] = llx.x[1]; // note that llx.x[1] is
														// the z coordinate
														// llx.x[2] would be out
														// of bounds
		} else if (Math.abs(totalPan) > Math.PI / 2) {
			lpx.lineFromRay(startLocation, startForward);
			lpx.planeFromUV(endLocation, endForward, endRight);
			lpx.findIntersection();
			if (dot3(startForward, endForward) > -0.95 || lpx.isPointIntersection()) {
				alignedStartLocation[0] = -lpx.t * startForward[0];
				alignedEndLocation[0] = -lpx.u * endForward[0];

				startOrigin[0] = endOrigin[0] = lpx.result[0];
				startOrigin[2] = endOrigin[2] = lpx.result[2];
			} else {
				alignedStartLocation[0] = alignedEndLocation[0] = partDist(startLocation, endLocation, 0, 2) * 0.5f;
				if (subDot3(endLocation, startLocation, startHorizontal) > 0) {
					alignedStartLocation[0] = alignedEndLocation[0] = -alignedStartLocation[0];
				}

				startOrigin[0] = endOrigin[0] = (startLocation[0] + endLocation[0]) * 0.5f;
				startOrigin[2] = endOrigin[2] = (startLocation[2] + endLocation[2]) * 0.5f;
			}
		} else {
			alignedStartLocation[0] = 0;
			alignedEndLocation[0] = subDot3(endLocation, startLocation, startHorizontal);

			startOrigin[0] = startLocation[0];
			startOrigin[2] = startLocation[2];

			double lateral = subDot3(endLocation, startLocation, startRight);

			endOrigin[0] = startOrigin[0] + lateral * startRight[0];
			endOrigin[2] = startOrigin[2] + lateral * startRight[2];
		}

		alignedStartLocation[1] = startLocation[1];
		alignedEndLocation[1] = endLocation[1];

		alignedStartForward[0] = dot3(startForward, startHorizontal);
		alignedEndForward[0] = dot3(endForward, endHorizontal);

		alignedStartForward[1] = startForward[1];
		alignedEndForward[1] = endForward[1];

		startTilt = Math.atan2(alignedStartForward[1], alignedStartForward[0]);
		endTilt = Math.atan2(alignedEndForward[1], alignedEndForward[0]);

		totalTilt = rotation(startTilt, endTilt);

		llx.setUp(alignedStartLocation, alignedStartForward, alignedEndLocation, alignedEndForward, 0, 1);

		if (llx.findIntersection() && Math.abs(totalTilt) > minRotation
				&& Math.PI - Math.abs(totalTilt) > minRotation) {
			alignedStartTiltOrigin[0] = alignedEndTiltOrigin[0] = llx.x[0];
			alignedStartTiltOrigin[1] = alignedEndTiltOrigin[1] = llx.x[1];

			startOffset = -llx.t0;
			endOffset = -llx.t1;
		} else if (Math.abs(totalTilt) > Math.PI / 2) {
			interp3(alignedStartLocation, alignedEndLocation, 0.5f, alignedStartTiltOrigin);
			setd(alignedEndTiltOrigin, alignedStartTiltOrigin);

			startOffset = endOffset = distance3(alignedStartLocation, alignedStartTiltOrigin);
		} else {
			setd(alignedStartTiltOrigin, alignedStartLocation);
			setd(alignedEndTiltOrigin, alignedEndLocation);

			startOffset = endOffset = 0;
		}
	}

	public GeneralViewXformOrbitAnimation setUp(float[] startViewXform, float[] endViewXform) {
		invAffine(startViewXform, startXform);
		invAffine(endViewXform, endXform);

		getColumn3(startXform, 3, startLocation);
		getColumn3(endXform, 3, endLocation);

		mvmulAffine(startXform, 0, 0, -1, startForward);
		mvmulAffine(endXform, 0, 0, -1, endForward);
		normalize3(startForward);
		normalize3(endForward);

		mvmulAffine(startXform, 1, 0, 0, startRight);
		mvmulAffine(endXform, 1, 0, 0, endRight);
		normalize3(startRight);
		normalize3(endRight);

		mvmulAffine(startXform, 0, 1, 0, startUp);
		mvmulAffine(endXform, 0, 1, 0, endUp);
		normalize3(startUp);
		normalize3(endUp);

		restOfSetUp();
		return this;
	}

	public GeneralViewXformOrbitAnimation setUp(float[] origin, float[] startViewXform, float[] endForward,
			float[] endRight) {
		invAffine(startViewXform, startXform);

		getColumn3(startXform, 3, startLocation);
		mvmulAffine(startXform, 0, 0, -1, startForward);
		mvmulAffine(startXform, 1, 0, 0, startRight);
		mvmulAffine(startXform, 0, 1, 0, startUp);

		normalize3(startForward);
		normalize3(startRight);
		normalize3(startUp);

		double forwardAmt = subDot3(startLocation, origin, startForward);
		double rightAmt = subDot3(startLocation, origin, startRight);
		double upAmt = subDot3(startLocation, origin, startUp);

		normalize3(endForward, this.endForward);
		normalize3(endRight, this.endRight);
		cross(endRight, endForward, endUp);

		scaleAdd3(forwardAmt, this.endForward, origin, endLocation);
		scaleAdd3(rightAmt, this.endRight, endLocation, endLocation);
		scaleAdd3(upAmt, endUp, endLocation, endLocation);

		restOfSetUp();
		return this;
	}

	public GeneralViewXformOrbitAnimation setUpWithEndLocation(float[] startViewXform, float[] endLocation,
			float[] endForward, float[] endRight) {
		invAffine(startViewXform, startXform);

		getColumn3(startXform, 3, startLocation);
		mvmulAffine(startXform, 0, 0, -1, startForward);
		mvmulAffine(startXform, 1, 0, 0, startRight);
		mvmulAffine(startXform, 0, 1, 0, startUp);

		normalize3(startForward);
		normalize3(startRight);
		normalize3(startUp);

		normalize3(endForward, this.endForward);
		normalize3(endRight, this.endRight);
		cross(endRight, endForward, endUp);

		setf(this.endLocation, endLocation);

		restOfSetUp();
		return this;
	}
}
