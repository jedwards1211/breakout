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
package org.andork.math3d;

import java.util.Iterator;

public class Primitives {
	public static Iterable<float[]> ellipsoid(final float[] center, final float[] a1, final float[] a2,
			final float[] a3, final int latDivs, final int longDivs) {
		return new Iterable<float[]>() {
			@Override
			public Iterator<float[]> iterator() {
				return new Iterator<float[]>() {
					int latitude = 0;
					int longitude = 0;
					float[] point = new float[3];

					@Override
					public boolean hasNext() {
						return latitude <= latDivs;
					}

					@Override
					public float[] next() {
						double latAngle = Math.PI * latitude / latDivs;
						double longAngle = Math.PI * 2 * longitude / longDivs;

						double slat = Math.sin(latAngle);
						double clat = Math.cos(latAngle);
						double slong = Math.sin(longAngle);
						double clong = Math.cos(longAngle);

						point[0] = (float) (center[0] + clat * a1[0] + slat * (clong * a2[0] + slong * a3[0]));
						point[1] = (float) (center[1] + clat * a1[1] + slat * (clong * a2[1] + slong * a3[1]));
						point[2] = (float) (center[2] + clat * a1[2] + slat * (clong * a2[2] + slong * a3[2]));

						longitude++;
						if (latitude == 0 || latitude == latDivs || longitude == longDivs) {
							latitude++;
							longitude = 0;
						}

						return point;
					}

					@Override
					public void remove() {
						throw new IllegalArgumentException();
					}
				};
			}
		};
	}

	public static Iterable<int[]> ellipsoidIndices(final int latDivs, final int longDivs) {
		return new Iterable<int[]>() {
			@Override
			public Iterator<int[]> iterator() {
				return new Iterator<int[]>() {
					int[] indices = new int[3];

					boolean other;
					int latitude = 0;
					int longitude = 0;

					@Override
					public boolean hasNext() {
						return latitude < latDivs;
					}

					@Override
					public int[] next() {
						int nextLat = latitude + 1;
						int nextLong = (longitude + 1) % longDivs;
						int ringStart = latitude > 0 ? 1 + (latitude - 1) * longDivs : 0;
						int nextRingStart = ringStart + longDivs;

						if (latitude == 0) {
							indices[0] = 0;
							indices[1] = 1 + longitude;
							indices[2] = 1 + nextLong;
						} else if (latitude == latDivs - 1) {
							int lastIndex = 1 + (latDivs - 1) * longDivs;
							indices[0] = lastIndex;
							indices[1] = ringStart + nextLong;
							indices[2] = ringStart + longitude;
						} else {
							if (other) {
								indices[0] = nextRingStart + longitude;
								indices[1] = nextRingStart + nextLong;
								indices[2] = ringStart + longitude;
							} else {
								indices[0] = ringStart + nextLong;
								indices[1] = ringStart + longitude;
								indices[2] = nextRingStart + nextLong;
							}
							other = !other;
						}

						if (!other) {
							longitude++;
						}

						if (longitude == longDivs) {
							latitude++;
							longitude = 0;
							other = false;
						}

						return indices;
					}

					@Override
					public void remove() {
						// TODO Auto-generated method stub

					}
				};
			}
		};
	}
}
