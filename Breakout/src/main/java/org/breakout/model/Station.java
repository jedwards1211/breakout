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
import java.util.Comparator;
import java.util.List;

import org.andork.collect.CollectionUtils;
import org.andork.math.misc.AngleUtils;
import org.andork.math3d.LineLineIntersection2d;
import org.andork.math3d.Vecmath;

public class Station {
	public static boolean isVertical(Shot o1, Station station) {
		boolean o1vertical = Math.abs(o1.inc) == Math.toRadians(90.0) ||
				o1.crossSectionAt(station).type == CrossSectionType.NSEW;
		return o1vertical;
	}

	public Station calcedFrom;
	public String name;

	public final List<Shot> shots = new ArrayList<>();

	public final double[] position = { Double.NaN, Double.NaN, Double.NaN };

	public void calcSplayPoints(LineLineIntersection2d llx) {
		shots.sort(new Comparator<Shot>() {
			@Override
			public int compare(Shot o1, Shot o2) {
				boolean o1vertical = isVertical(o1, Station.this);
				boolean o2vertical = isVertical(o2, Station.this);

				if (o1vertical && o2vertical) {
					return 0;
				}
				if (o1vertical != o2vertical) {
					return o1vertical ? 1 : -1;
				}

				return Double.compare(o1.azimuthAt(Station.this), o2.azimuthAt(Station.this));
			}
		});

		int verticalIndex = CollectionUtils.indexOf(shots, shot -> Station.isVertical(shot, this));
		if (verticalIndex < 0) {
			verticalIndex = shots.size();
		}

		double maxUp = 0.0;
		double maxDown = 0.0;
		double maxNorth = 0.0;
		double maxSouth = 0.0;
		double maxEast = 0.0;
		double maxWest = 0.0;

		boolean anyLrud = false;
		boolean anyNsew = false;

		for (Shot shot : shots) {
			shot.setSplayPointsAt(this, new float[shot.crossSectionAt(this).dist.length][]);
			shot.setSplayNormalsAt(this, new float[shot.crossSectionAt(this).dist.length][]);

			CrossSection sect = shot.crossSectionAt(this);
			if (sect.type == CrossSectionType.LRUD) {
				anyLrud = true;
				maxUp = Math.max(maxUp, sect.dist[2]);
				maxDown = Math.max(maxDown, sect.dist[3]);
			} else if (sect.type == CrossSectionType.NSEW) {
				anyNsew = true;
				maxNorth = Math.max(maxNorth, sect.dist[0]);
				maxSouth = Math.max(maxSouth, sect.dist[1]);
				maxEast = Math.max(maxEast, sect.dist[2]);
				maxWest = Math.max(maxWest, sect.dist[3]);
			}
		}

		float[] up = null;
		float[] down = null;
		float[] north = null;
		float[] south = null;
		float[] east = null;
		float[] west = null;
		float[] upNorm = null;
		float[] downNorm = null;
		float[] northNorm = null;
		float[] southNorm = null;
		float[] eastNorm = null;
		float[] westNorm = null;

		if (anyLrud) {
			up = new float[] {
					(float) position[0],
					(float) (position[1] + maxUp),
					(float) position[2]
			};
			upNorm = new float[] { 0, 1, 0 };

			down = new float[] {
					(float) position[0],
					(float) (position[1] - maxDown),
					(float) position[2]
			};
			downNorm = new float[] { 0, -1, 0 };
		}
		if (anyNsew) {
			north = new float[] {
					(float) position[0],
					(float) position[1],
					(float) (position[2] - maxNorth)
			};
			northNorm = new float[] { 0, 0, -1 };
			south = new float[] {
					(float) position[0],
					(float) position[1],
					(float) (position[2] + maxSouth)
			};
			southNorm = new float[] { 0, 0, 1 };
			east = new float[] {
					(float) (position[0] + maxEast),
					(float) position[1],
					(float) position[2]
			};
			eastNorm = new float[] { 1, 0, 0 };
			west = new float[] {
					(float) (position[0] - maxWest),
					(float) position[1],
					(float) position[2]
			};
			westNorm = new float[] { -1, 0, 0 };
		}

		for (int i1 = 0; i1 < verticalIndex; i1++) {
			int i2 = (i1 + 1) % verticalIndex;

			Shot shot1 = shots.get(i1);
			Shot shot2 = shots.get(i2);

			CrossSection sect1 = shot1.crossSectionAt(this);

			maxUp = Math.max(maxUp, sect1.dist[2]);
			maxDown = Math.max(maxDown, sect1.dist[3]);

			double left1 = shot1.leftAt(this);
			double right2 = shot2.rightAt(this);

			float[][] splayPoints1 = shot1.splayPointsAt(this);
			splayPoints1[2] = up;
			splayPoints1[3] = down;
			float[][] splayNorms1 = shot1.splayNormalsAt(this);
			splayNorms1[2] = upNorm;
			splayNorms1[3] = downNorm;

			float[] leftSplayPoint1 = new float[3];
			float[] rightSplayPoint2 = leftSplayPoint1;
			float[] leftSplayNorm1 = new float[3];
			float[] rightSplayNorm2 = leftSplayNorm1;

			double shot1azm = shot1.azimuthAt(this);
			double shot2azm = shot2.azimuthAt(this);

			double angle = AngleUtils.clockwiseRotation(shot1azm, shot2azm);

			if (verticalIndex == 1) {
				double xv1 = Math.sin(shot1azm);
				double zv1 = -Math.cos(shot1azm);

				leftSplayPoint1[0] = (float) (position[0] - zv1 * left1);
				leftSplayPoint1[1] = (float) position[1];
				leftSplayPoint1[2] = (float) (position[2] + xv1 * left1);
				leftSplayNorm1[0] = (float) -zv1;
				leftSplayNorm1[1] = 0;
				leftSplayNorm1[2] = (float) xv1;

				rightSplayPoint2 = new float[3];
				rightSplayNorm2 = new float[3];

				rightSplayPoint2[0] = (float) (position[0] + zv1 * right2);
				rightSplayPoint2[1] = (float) position[1];
				rightSplayPoint2[2] = (float) (position[2] - xv1 * right2);
				rightSplayNorm2[0] = (float) zv1;
				rightSplayNorm2[1] = 0;
				rightSplayNorm2[2] = (float) -xv1;
			} else if (verticalIndex == 2) {
				double bisectorAzm = shot1azm + angle * 0.5;

				double xv = Math.sin(bisectorAzm);
				double zv = -Math.cos(bisectorAzm);

				leftSplayPoint1[0] = (float) (position[0] + xv * left1);
				leftSplayPoint1[1] = (float) position[1];
				leftSplayPoint1[2] = (float) (position[2] + zv * left1);
				leftSplayNorm1[0] = (float) xv;
				leftSplayNorm1[1] = 0;
				leftSplayNorm1[2] = (float) zv;
			} else {
				double xv1 = Math.sin(shot1azm);
				double zv1 = -Math.cos(shot1azm);

				double xv2 = Math.sin(shot2azm);
				double zv2 = -Math.cos(shot2azm);

				double offset1;
				double offset2;

				if (angle < Math.PI * 0.5) {
					offset2 = (left1 + right2 * Math.cos(angle)) / Math.sin(angle);
					offset1 = right2 * Math.sin(angle) + offset2 * Math.cos(angle);
				} else if (angle < Math.PI) {
					offset2 = (left1 - right2 * Math.cos(Math.PI - angle)) / Math.sin(Math.PI - angle);
					offset1 = right2 * Math.sin(Math.PI - angle) + offset2 * Math.cos(Math.PI - angle);
				} else {
					offset2 = -(left1 + right2 * Math.cos(2 * Math.PI - angle)) / Math.sin(2 * Math.PI - angle);
					offset1 = -right2 * Math.sin(2 * Math.PI - angle) - offset2 * Math.cos(2 * Math.PI - angle);
				}

				if (Math.abs(offset2) > shot2.dist || Math.abs(offset1) > shot1.dist) {
					leftSplayPoint1[0] = (float) (position[0] - zv1 * left1);
					leftSplayPoint1[1] = (float) position[1];
					leftSplayPoint1[2] = (float) (position[2] + xv1 * left1);
					leftSplayNorm1[0] = (float) -zv1;
					leftSplayNorm1[1] = 0;
					leftSplayNorm1[2] = (float) xv1;

					rightSplayPoint2 = new float[3];
					rightSplayNorm2 = new float[3];

					rightSplayPoint2[0] = (float) (position[0] + zv2 * right2);
					rightSplayPoint2[1] = (float) position[1];
					rightSplayPoint2[2] = (float) (position[2] - xv2 * right2);
					rightSplayNorm2[0] = (float) zv2;
					rightSplayNorm2[1] = 0;
					rightSplayNorm2[2] = (float) -xv2;
				} else {
					leftSplayPoint1[0] = (float) (position[0] + offset2 * xv2 + right2 * zv2);
					leftSplayPoint1[1] = (float) position[1];
					leftSplayPoint1[2] = (float) (position[2] + offset2 * zv2 - right2 * xv2);

					leftSplayNorm1[0] = (float) (leftSplayPoint1[0] - position[0]);
					leftSplayNorm1[1] = 0;
					leftSplayNorm1[2] = (float) (leftSplayPoint1[2] - position[2]);
					Vecmath.normalize3(leftSplayNorm1);
				}
			}

			shot1.setLeftSplayPointAt(this, leftSplayPoint1);
			shot1.setLeftSplayNormalAt(this, leftSplayNorm1);
			shot2.setRightSplayPointAt(this, rightSplayPoint2);
			shot2.setRightSplayNormalAt(this, rightSplayNorm2);
		}

		for (int i = verticalIndex; i < shots.size(); i++) {
			Shot shot = shots.get(i);
			CrossSection sect = shot.crossSectionAt(this);
			float[][] splays = shot.splayPointsAt(this);
			float[][] splayNorms = shot.splayNormalsAt(this);

			if (sect.type == CrossSectionType.NSEW) {
				splays[0] = north;
				splays[1] = south;
				splays[2] = east;
				splays[3] = west;
				splayNorms[0] = northNorm;
				splayNorms[1] = southNorm;
				splayNorms[2] = eastNorm;
				splayNorms[3] = westNorm;
			} else {
				double xv = Math.sin(shot.azm);
				double zv = -Math.cos(shot.azm);

				splays[0] = new float[] {
						(float) (position[0] + sect.dist[0] * zv),
						(float) position[1],
						(float) (position[2] - sect.dist[0] * xv)
				};
				splayNorms[0] = new float[] { (float) zv, 0, (float) -xv };

				splays[1] = new float[] {
						(float) (position[0] - sect.dist[1] * zv),
						(float) position[1],
						(float) (position[2] + sect.dist[1] * xv)
				};
				splayNorms[1] = new float[] { (float) -zv, 0, (float) xv };

				splays[2] = up;
				splayNorms[2] = upNorm;
				splays[3] = down;
				splayNorms[3] = downNorm;
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
