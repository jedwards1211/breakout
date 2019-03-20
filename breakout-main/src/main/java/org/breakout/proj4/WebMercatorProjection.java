package org.breakout.proj4;

import java.util.HashMap;
import java.util.Map;

import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.Ellipsoid;
import org.osgeo.proj4j.proj.Projection;
import org.osgeo.proj4j.util.ProjectionMath;

public class WebMercatorProjection extends Projection {
	public final Integer tileSize;
	public final Double zoom;
	
	private final Map<Integer, Parameters> cache = new HashMap<>();
	
	private static class Parameters {
		double[] Bc;
		double[] Cc;
		double[] dc;
		double[] Ac;

		public Parameters(int tileSize) {
			int zoomLevels = 30;
			Bc = new double[zoomLevels];
			Cc = new double[zoomLevels];
			dc = new double[zoomLevels];
			Ac = new double[zoomLevels];
			for (int d = 0; d < zoomLevels; d++) {
				Bc[d] = tileSize / (2 * Math.PI);
				Cc[d] = tileSize / (2 * Math.PI);
				dc[d] = tileSize / 2.0;
				Ac[d] = tileSize;
				tileSize *= 2;
			}
		}
	}
	
	double Bc;
	double Cc;
	double d;
	double Ac;

	public final static Ellipsoid ELLIPSOID = new Ellipsoid("WEB_MERCATOR", 6378137.0, 6378137.0,
			0.0, "WebMercator");

	public WebMercatorProjection(Integer tileSize, Double zoom) {
		minLatitude = -85 * DTR;
		maxLatitude = 85 * DTR;
		this.tileSize = tileSize;
		this.zoom = zoom;
		if (tileSize != null && zoom != null) {
			Parameters params = cache.get(tileSize);
			if (zoom % 1.0 != 0) {
				d = tileSize / 2;
				Bc = (tileSize / (2 * Math.PI));
				Cc = (tileSize / (2 * Math.PI));
				Ac = tileSize;
			} else {
				if (params == null) {
					cache.put(tileSize, params = new Parameters(tileSize));
				}
				int izoom = zoom.intValue();
				Bc = params.Bc[izoom];
				Cc = params.Cc[izoom];
				d = params.dc[izoom];
				Ac = params.Ac[izoom];
			}
		} else {
			d = ELLIPSOID.equatorRadius / 2;
			Ac = ELLIPSOID.equatorRadius;
			Bc = ELLIPSOID.equatorRadius / (2 * Math.PI);
			Cc = ELLIPSOID.equatorRadius / (2 * Math.PI);
		}
		this.setEllipsoid(ELLIPSOID);
		this.initialize();
	}
	
	@Override
	public void initialize() {
		super.initialize();
		totalScale = 1;
	}

	@Override
	protected ProjCoordinate project(double lam, double phi, ProjCoordinate dst) {
	    double f = Math.min(Math.max(Math.sin(phi), -0.9999), 0.9999);
	    dst.x = d + lam * Bc;
	    dst.y = d + 0.5 * Math.log((1 + f) / (1 - f)) * -Cc;
	    if (dst.x > Ac) dst.x = Ac;
	    if (dst.y > Ac) dst.y = Ac;
	    return dst;
	}

	@Override
	protected ProjCoordinate projectInverse(double x, double y, ProjCoordinate dst) {
	    double g = (y - d) / -Cc;
	    dst.x = (x - d) / Bc;
	    dst.y = 2 * Math.atan(Math.exp(g)) - 0.5 * Math.PI;
	    return dst;
	}

	@Override
	public boolean hasInverse() {
		return true;
	}

	@Override
	public boolean isRectilinear() {
		return true;
	}
	
	@Override
	public String toString() {
		return "Web Mercator";
	}
}
