package org.breakout.proj4;

import org.osgeo.proj4j.datum.Ellipsoid;

public class WebMercatorEllipsoid extends Ellipsoid {
	public final static Ellipsoid WEB_MERCATOR = new Ellipsoid("WEB_MERCATOR", 6378137.0, 0.0,
			0.0, "WebMercator");

}
