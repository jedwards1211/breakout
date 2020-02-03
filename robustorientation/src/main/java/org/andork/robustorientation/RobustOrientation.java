package org.andork.robustorientation;

import static org.andork.robustmath.RobustMath.add;
import static org.andork.robustmath.RobustMath.multiply;
import static org.andork.robustmath.RobustMath.subtract;

public class RobustOrientation {
	public static void main(String[] args) {
		double[] a = { 0, 0.1, 0 };
		double[] b = { 1, 0, 0 };
		double[] c = { 0, 0, 1 };
		double[] d = { 0.5, 1, 0.5 };
		double[] e = { 10, 1, 0.5 };

		System.out.println(orientation4(a, b, c, d));
		System.out.println(orientation4(a, b, c, e));
	}

	private RobustOrientation() {

	}

	private static double orientation4Exact(
		double ax,
		double ay,
		double az,
		double bx,
		double by,
		double bz,
		double cx,
		double cy,
		double cz,
		double dx,
		double dy,
		double dz) {
		double[] p =
			add(
				add(
					multiply(add(multiply(cy, dx), multiply(-dy, cx)), bz),
					add(
						multiply(add(multiply(by, dx), multiply(-dy, bx)), -cz),
						multiply(add(multiply(by, cx), multiply(-cy, bx)), dz))),
				add(
					multiply(add(multiply(by, dx), multiply(-dy, bx)), az),
					add(
						multiply(add(multiply(ay, dx), multiply(-dy, ax)), -bz),
						multiply(add(multiply(ay, bx), multiply(-by, ax)), dz)))), n =
							add(
								add(
									multiply(add(multiply(cy, dx), multiply(-dy, cx)), az),
									add(
										multiply(add(multiply(ay, dx), multiply(-dy, ax)), -cz),
										multiply(add(multiply(ay, cx), multiply(-cy, ax)), dz))),
								add(
									multiply(add(multiply(by, cx), multiply(-cy, bx)), az),
									add(
										multiply(add(multiply(ay, cx), multiply(-cy, ax)), -bz),
										multiply(add(multiply(ay, bx), multiply(-by, ax)), cz)))), x = subtract(p, n);
		return x[x.length - 1];
	}

	private static double orientation4Exact(double[] a, double[] b, double[] c, double[] d) {
		double[] p =
			add(
				add(
					multiply(add(multiply(c[1], d[0]), multiply(-d[1], c[0])), b[2]),
					add(
						multiply(add(multiply(b[1], d[0]), multiply(-d[1], b[0])), -c[2]),
						multiply(add(multiply(b[1], c[0]), multiply(-c[1], b[0])), d[2]))),
				add(
					multiply(add(multiply(b[1], d[0]), multiply(-d[1], b[0])), a[2]),
					add(
						multiply(add(multiply(a[1], d[0]), multiply(-d[1], a[0])), -b[2]),
						multiply(add(multiply(a[1], b[0]), multiply(-b[1], a[0])), d[2])))), n =
							add(
								add(
									multiply(add(multiply(c[1], d[0]), multiply(-d[1], c[0])), a[2]),
									add(
										multiply(add(multiply(a[1], d[0]), multiply(-d[1], a[0])), -c[2]),
										multiply(add(multiply(a[1], c[0]), multiply(-c[1], a[0])), d[2]))),
								add(
									multiply(add(multiply(b[1], c[0]), multiply(-c[1], b[0])), a[2]),
									add(
										multiply(add(multiply(a[1], c[0]), multiply(-c[1], a[0])), -b[2]),
										multiply(add(multiply(a[1], b[0]), multiply(-b[1], a[0])), c[2])))), x =
											subtract(p, n);
		return x[x.length - 1];
	}

	public static double orientation4(
		double ax,
		double ay,
		double az,
		double bx,
		double by,
		double bz,
		double cx,
		double cy,
		double cz,
		double dx,
		double dy,
		double dz) {
		double adx = ax - dx;
		double bdx = bx - dx;
		double cdx = cx - dx;
		double ady = ay - dy;
		double bdy = by - dy;
		double cdy = cy - dy;
		double adz = az - dz;
		double bdz = bz - dz;
		double cdz = cz - dz;
		double bdxcdy = bdx * cdy;
		double cdxbdy = cdx * bdy;
		double cdxady = cdx * ady;
		double adxcdy = adx * cdy;
		double adxbdy = adx * bdy;
		double bdxady = bdx * ady;
		double det = adz * (bdxcdy - cdxbdy) + bdz * (cdxady - adxcdy) + cdz * (adxbdy - bdxady);
		double permanent =
			(Math.abs(bdxcdy) + Math.abs(cdxbdy)) * Math.abs(adz)
				+ (Math.abs(cdxady) + Math.abs(adxcdy)) * Math.abs(bdz)
				+ (Math.abs(adxbdy) + Math.abs(bdxady)) * Math.abs(cdz);
		double tol = ERRBOUND4 * permanent;
		if (det > tol || -det > tol) {
			return det;
		}
		return orientation4Exact(ax, ay, az, bx, by, bz, cx, cy, cz, dx, dy, dz);
	}

	public static double orientation4(double[] a, double[] b, double[] c, double[] d) {
		double adx = a[0] - d[0];
		double bdx = b[0] - d[0];
		double cdx = c[0] - d[0];
		double ady = a[1] - d[1];
		double bdy = b[1] - d[1];
		double cdy = c[1] - d[1];
		double adz = a[2] - d[2];
		double bdz = b[2] - d[2];
		double cdz = c[2] - d[2];
		double bdxcdy = bdx * cdy;
		double cdxbdy = cdx * bdy;
		double cdxady = cdx * ady;
		double adxcdy = adx * cdy;
		double adxbdy = adx * bdy;
		double bdxady = bdx * ady;
		double det = adz * (bdxcdy - cdxbdy) + bdz * (cdxady - adxcdy) + cdz * (adxbdy - bdxady);
		double permanent =
			(Math.abs(bdxcdy) + Math.abs(cdxbdy)) * Math.abs(adz)
				+ (Math.abs(cdxady) + Math.abs(adxcdy)) * Math.abs(bdz)
				+ (Math.abs(adxbdy) + Math.abs(bdxady)) * Math.abs(cdz);
		double tol = ERRBOUND4 * permanent;
		if (det > tol || -det > tol) {
			return det;
		}
		return orientation4Exact(a, b, c, d);
	}

	private static double EPSILON = 1.1102230246251565e-16;
	private static double ERRBOUND4 = (7.0 + 56.0 * EPSILON) * EPSILON;
}
