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

import static org.andork.math3d.Vecmath.cross;
import static org.andork.math3d.Vecmath.dot3;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.setf;

import java.util.Arrays;

public class FittingFrustum {
	public static void main(String[] args) {
		FittingFrustum frustum = new FittingFrustum();

		frustum.init(new float[] { -1, 0, 0 },
				new float[] { -1, 0, -1 },
				new float[] { -1, 0, 1 },
				new float[] { -1, 1, 0 },
				new float[] { -1, -1, 0 });

		frustum.addPoint(0, 0, 1);
		frustum.addPoint(1, 0, 5);

		float[] origin = new float[3];
		frustum.calculateOrigin(origin);

		System.out.println(Arrays.toString(origin));
	}

	/**
	 * Performs partial gaussian elimination on the m by n matrix A. Instead of
	 * exchanging rows, row_perms is used to mark the positions of the rows in
	 * the reduced matrix. Row <code>i</code> of the reduced matrix is row
	 * <code>row_perms[ i ]</code> of A.
	 *
	 * @param maxNumToReduce
	 *            only the topmost {@code maxNumToReduce} rows will be fully
	 *            reduced
	 */
	static void reduce(float[][] A, int maxNumToReduce, int[] row_perms, int[] pivot_cols) {
		int m = A.length;
		int n = A.length == 0 ? 0 : A[0].length;

		if (row_perms.length != m) {
			throw new IllegalArgumentException("row_perms.length must equal A.length");
		}

		for (int h = 0; h < row_perms.length; h++) {
			row_perms[h] = h;
		}

		for (int k = 0; k < n - 1; k++) {
			pivot_cols[k] = k;
		}

		int i = 0;

		while (i < maxNumToReduce && i < m) {
			int pivot_row = 0;
			int pivot_col = 0;
			float pivot = Float.NaN;

			// find the next pivot row and column

			for (int h = i; h < maxNumToReduce; h++) {
				for (int k = i; k < n - 1; k++) {
					if (Float.isNaN(pivot) || Math.abs(A[row_perms[h]][pivot_cols[k]]) > Math.abs(pivot)) {
						pivot = A[row_perms[h]][pivot_cols[k]];
						pivot_row = h;
						pivot_col = k;
					}
				}
			}

			if (Float.isNaN(pivot) || pivot == 0) {
				break;
			}

			int temp = row_perms[i];
			row_perms[i] = row_perms[pivot_row];
			row_perms[pivot_row] = temp;

			temp = pivot_cols[i];
			pivot_cols[i] = pivot_cols[pivot_col];
			pivot_cols[pivot_col] = temp;

			// divide pivot row by the pivot value
			for (int j = 0; j < n; j++) {
				A[row_perms[i]][j] /= pivot;
			}

			// subtract pivot row from the other rows
			for (int h = 0; h < m; h++) {
				if (h == i) {
					continue;
				}

				float multiplier = A[row_perms[h]][pivot_cols[i]];

				for (int j = 0; j < n; j++) {
					A[row_perms[h]][j] -= multiplier * A[row_perms[i]][j];
				}
			}
			i++;
		}
	}

	final float[] direction = new float[3];
	final float[] left = new float[4];
	final float[] right = new float[4];
	final float[] top = new float[4];

	final float[] bottom = new float[4];
	final float[] horizontal = new float[4];

	final float[] vertical = new float[4];
	final float[] horizontal2 = new float[4];

	final float[] vertical2 = new float[4];
	final float[][] matrix1 = new float[][] { left, right, horizontal };
	final float[][] matrix2 = new float[][] { top, bottom, vertical };
	final float[][] matrix3 = new float[][] { left, right, vertical2 };

	final float[][] matrix4 = new float[][] { top, bottom, horizontal2 };
	final float[] p0 = new float[3];

	final float[] p1 = new float[3];
	final int[] row_perms = new int[3];

	final int[] pivot_cols = new int[3];

	final float EPS = 1e-4f;

	public void addPoint(float x, float y, float z) {
		float dl = x * left[0] + y * left[1] + z * left[2];
		if (Float.isFinite(dl) && (Float.isNaN(left[3]) || dl > left[3])) {
			left[3] = dl;
		}

		float dr = x * right[0] + y * right[1] + z * right[2];
		if (Float.isFinite(dr) && (Float.isNaN(right[3]) || dr > right[3])) {
			right[3] = dr;
		}

		float dt = x * top[0] + y * top[1] + z * top[2];
		if (Float.isFinite(dt) && (Float.isNaN(top[3]) || dt > top[3])) {
			top[3] = dt;
		}

		float db = x * bottom[0] + y * bottom[1] + z * bottom[2];
		if (Float.isFinite(db) && (Float.isNaN(bottom[3]) || db > bottom[3])) {
			bottom[3] = db;
		}
	}

	public void addPoint(float[] coord) {
		addPoint(coord[0], coord[1], coord[2]);
	}

	public void calculateOrigin(float[] out) {
		reduce(matrix1, 2, row_perms, pivot_cols);
		// checkZeros( horizontal );
		horizontal2[3] = -horizontal[3];

		reduce(matrix2, 2, row_perms, pivot_cols);
		// checkZeros( vertical );
		vertical2[3] = -vertical[3];

		reduce(matrix3, 3, row_perms, pivot_cols);
		for (int i = 0; i < 3; i++) {
			p0[pivot_cols[i]] = matrix3[row_perms[i]][3];
		}

		reduce(matrix4, 3, row_perms, pivot_cols);
		for (int i = 0; i < 3; i++) {
			p1[pivot_cols[i]] = matrix4[row_perms[i]][3];
		}

		setf(out, dot3(p0, direction) < dot3(p1, direction) ? p0 : p1);
	}

	void checkZeros(float[] row) {
		for (int i = 0; i < 3; i++) {
			float f = row[i];
			if (Float.isNaN(f) || Float.isInfinite(f) || Math.abs(f) > EPS) {
				throw new RuntimeException("Malformed input or floating-point error: " + Arrays.toString(row));
			}
		}
	}

	public void furtherInit() {
		normalize3(left);
		normalize3(right);
		normalize3(top);
		normalize3(bottom);

		cross(right, left, vertical);
		cross(bottom, top, horizontal);
		normalize3(horizontal);
		normalize3(vertical);

		cross(vertical, left, left);
		cross(right, vertical, right);
		cross(horizontal, top, top);
		cross(bottom, horizontal, bottom);

		left[3] = right[3] = top[3] = bottom[3] = Float.NaN;
		horizontal[3] = vertical[3] = 0f;

		setf(horizontal2, horizontal);
		setf(vertical2, vertical);
	}

	public void init(float[] direction, float[] left, float[] right, float[] top, float[] bottom) {
		setf(this.direction, direction);
		setf(this.left, left);
		setf(this.right, right);
		setf(this.top, top);
		setf(this.bottom, bottom);

		furtherInit();
	}

	public void init(PickXform xform, float viewRatio) {
		xform.xform(.5f, .5f, 1f, 1f, horizontal, 0, direction, 0);

		xform.xform((1f - viewRatio) * .5f, .5f, 1f, 1f, horizontal, 0, left, 0);
		xform.xform((1f + viewRatio) * .5f, .5f, 1f, 1f, horizontal, 0, right, 0);
		xform.xform(.5f, (1f - viewRatio) * .5f, 1f, 1f, horizontal, 0, top, 0);
		xform.xform(.5f, (1f + viewRatio) * .5f, 1f, 1f, horizontal, 0, bottom, 0);

		furtherInit();
	}
}
