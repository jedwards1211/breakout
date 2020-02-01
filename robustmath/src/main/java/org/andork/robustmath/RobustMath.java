package org.andork.robustmath;

import java.util.ArrayList;

/**
 * Copped from:
 * {@link https://github.com/mikolalysenko/robust-sum/blob/master/robust-sum.js}
 */

public class RobustMath {
	private RobustMath() {
	}

	public static double[] add(double a, double b, double[] result) {
		double x = a + b;
		double bv = x - a;
		double av = x - bv;
		double br = b - bv;
		double ar = a - av;
		if (result != null) {
			result[0] = ar + br;
			result[1] = x;
			return result;
		}
		return new double[] { ar + br, x };
	}

	public static double[] add(double a, double b) {
		double[] result = new double[2];
		return add(a, b, result);
	}

	public static double[] add(double[] e, double[] f) {
		int ne = e.length;
		int nf = f.length;
		if (ne == 1 && nf == 1) {
			double a = e[0];
			double b = f[0];
			double x = a + b;
			double bv = x - a;
			double av = x - bv;
			double br = b - bv;
			double ar = a - av;
			double y = ar + br;
			if (y != 0) {
				return new double[] { y, x };
			}
			return new double[] { x };
		}
		int n = ne + nf;
		ArrayList<Double> g = new ArrayList<>(n);
		int eptr = 0;
		int fptr = 0;
		double ei = e[eptr];
		double ea = Math.abs(ei);
		double fi = f[fptr];
		double fa = Math.abs(fi);
		double a, b;
		if (ea < fa) {
			b = ei;
			eptr += 1;
			if (eptr < ne) {
				ei = e[eptr];
				ea = Math.abs(ei);
			}
		} else {
			b = fi;
			fptr += 1;
			if (fptr < nf) {
				fi = f[fptr];
				fa = Math.abs(fi);
			}
		}
		if (eptr < ne && ea < fa || fptr >= nf) {
			a = ei;
			eptr += 1;
			if (eptr < ne) {
				ei = e[eptr];
				ea = Math.abs(ei);
			}
		} else {
			a = fi;
			fptr += 1;
			if (fptr < nf) {
				fi = f[fptr];
				fa = Math.abs(fi);
			}
		}
		double x = a + b;
		double bv = x - a;
		double y = b - bv;
		double q0 = y;
		double q1 = x;
		double _x, _bv, _av, _br, _ar;
		while (eptr < ne && fptr < nf) {
			if (ea < fa) {
				a = ei;
				eptr += 1;
				if (eptr < ne) {
					ei = e[eptr];
					ea = Math.abs(ei);
				}
			} else {
				a = fi;
				fptr += 1;
				if (fptr < nf) {
					fi = f[fptr];
					fa = Math.abs(fi);
				}
			}
			b = q0;
			x = a + b;
			bv = x - a;
			y = b - bv;
			if (y != 0) {
				g.add(y);
			}
			_x = q1 + x;
			_bv = _x - q1;
			_av = _x - _bv;
			_br = x - _bv;
			_ar = q1 - _av;
			q0 = _ar + _br;
			q1 = _x;
		}
		while (eptr < ne) {
			a = ei;
			b = q0;
			x = a + b;
			bv = x - a;
			y = b - bv;
			if (y != 0) {
				g.add(y);
			}
			_x = q1 + x;
			_bv = _x - q1;
			_av = _x - _bv;
			_br = x - _bv;
			_ar = q1 - _av;
			q0 = _ar + _br;
			q1 = _x;
			eptr += 1;
			if (eptr < ne) {
				ei = e[eptr];
			}
		}
		while (fptr < nf) {
			a = fi;
			b = q0;
			x = a + b;
			bv = x - a;
			y = b - bv;
			if (y != 0) {
				g.add(y);
			}
			;
			_x = q1 + x;
			_bv = _x - q1;
			_av = _x - _bv;
			_br = x - _bv;
			_ar = q1 - _av;
			q0 = _ar + _br;
			q1 = _x;
			fptr += 1;
			if (fptr < nf) {
				fi = f[fptr];
			}
		}
		if (q0 != 0) {
			g.add(q0);
		}
		if (q1 != 0) {
			g.add(q1);
		}
		if (g.isEmpty()) {
			return new double[] { 0.0 };
		}
		return toArray(g);
	}

	public static double[] toArray(ArrayList<Double> g) {
		double[] result = new double[g.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = g.get(i);
		}
		return result;
	}

	public static double[] subtract(double a, double b) {
		double x = a + b;
		double bv = x - a;
		double av = x - bv;
		double br = b - bv;
		double ar = a - av;
		double y = ar + br;
		if (y != 0) {
			return new double[] { y, x };
		}
		return new double[] { x };
	}

	public static double[] subtract(double[] e, double[] f) {
		int ne = e.length;
		int nf = f.length;
		if (ne == 1 && nf == 1) {
			return subtract(e[0], -f[0]);
		}
		int n = ne + nf;
		ArrayList<Double> g = new ArrayList<>(n);
		int eptr = 0;
		int fptr = 0;
		double ei = e[eptr];
		double ea = Math.abs(ei);
		double fi = -f[fptr];
		double fa = Math.abs(fi);
		double a, b;
		if (ea < fa) {
			b = ei;
			eptr += 1;
			if (eptr < ne) {
				ei = e[eptr];
				ea = Math.abs(ei);
			}
		} else {
			b = fi;
			fptr += 1;
			if (fptr < nf) {
				fi = -f[fptr];
				fa = Math.abs(fi);
			}
		}
		if (eptr < ne && ea < fa || fptr >= nf) {
			a = ei;
			eptr += 1;
			if (eptr < ne) {
				ei = e[eptr];
				ea = Math.abs(ei);
			}
		} else {
			a = fi;
			fptr += 1;
			if (fptr < nf) {
				fi = -f[fptr];
				fa = Math.abs(fi);
			}
		}
		double x = a + b;
		double bv = x - a;
		double y = b - bv;
		double q0 = y;
		double q1 = x;
		double _x, _bv, _av, _br, _ar;
		while (eptr < ne && fptr < nf) {
			if (ea < fa) {
				a = ei;
				eptr += 1;
				if (eptr < ne) {
					ei = e[eptr];
					ea = Math.abs(ei);
				}
			} else {
				a = fi;
				fptr += 1;
				if (fptr < nf) {
					fi = -f[fptr];
					fa = Math.abs(fi);
				}
			}
			b = q0;
			x = a + b;
			bv = x - a;
			y = b - bv;
			if (y != 0) {
				g.add(y);
			}
			_x = q1 + x;
			_bv = _x - q1;
			_av = _x - _bv;
			_br = x - _bv;
			_ar = q1 - _av;
			q0 = _ar + _br;
			q1 = _x;
		}
		while (eptr < ne) {
			a = ei;
			b = q0;
			x = a + b;
			bv = x - a;
			y = b - bv;
			if (y != 0) {
				g.add(y);
			}
			_x = q1 + x;
			_bv = _x - q1;
			_av = _x - _bv;
			_br = x - _bv;
			_ar = q1 - _av;
			q0 = _ar + _br;
			q1 = _x;
			eptr += 1;
			if (eptr < ne) {
				ei = e[eptr];
			}
		}
		while (fptr < nf) {
			a = fi;
			b = q0;
			x = a + b;
			bv = x - a;
			y = b - bv;
			if (y != 0) {
				g.add(y);
			}
			;
			_x = q1 + x;
			_bv = _x - q1;
			_av = _x - _bv;
			_br = x - _bv;
			_ar = q1 - _av;
			q0 = _ar + _br;
			q1 = _x;
			fptr += 1;
			if (fptr < nf) {
				fi = -f[fptr];
			}
		}
		if (q0 != 0) {
			g.add(q0);
		}
		if (q1 != 0) {
			g.add(q1);
		}
		if (g.isEmpty()) {
			return new double[] { 0.0 };
		}
		return toArray(g);
	}

	private static double SPLITTER = Math.pow(2, 27) + 1.0;

	public static double[] multiply(double a, double b, double[] result) {
		double x = a * b;

		double c = SPLITTER * a;
		double abig = c - a;
		double ahi = c - abig;
		double alo = a - ahi;

		double d = SPLITTER * b;
		double bbig = d - b;
		double bhi = d - bbig;
		double blo = b - bhi;

		double err1 = x - ahi * bhi;
		double err2 = err1 - alo * bhi;
		double err3 = err2 - ahi * blo;

		double y = alo * blo - err3;

		if (result != null) {
			result[0] = y;
			result[1] = x;
			return result;
		}

		return new double[] { y, x };
	}

	public static double[] multiply(double a, double b) {
		double[] result = new double[2];
		return multiply(a, b, result);
	}

	public static double[] multiply(double[] e, double scale) {
		int n = e.length;
		if (n == 1) {
			double[] ts = multiply(e[0], scale);
			if (ts[0] != 0) {
				return ts;
			}
			return new double[] { ts[1] };
		}
		ArrayList<Double> g = new ArrayList<>(2 * n);
		double[] q = new double[] { 0.1, 0.1 };
		double[] t = new double[] { 0.1, 0.1 };
		multiply(e[0], scale, q);
		if (q[0] != 0) {
			g.add(q[0]);
		}
		for (int i = 1; i < n; ++i) {
			multiply(e[i], scale, t);
			double pq = q[1];
			add(pq, t[0], q);
			if (q[0] != 0) {
				g.add(q[0]);
			}
			double a = t[1];
			double b = q[1];
			double x = a + b;
			double bv = x - a;
			double y = b - bv;
			q[1] = x;
			if (y != 0) {
				g.add(y);
			}
		}
		if (q[1] != 0) {
			g.add(q[1]);
		}
		if (g.isEmpty()) {
			return new double[] { 0.0 };
		}
		return toArray(g);
	}
};
