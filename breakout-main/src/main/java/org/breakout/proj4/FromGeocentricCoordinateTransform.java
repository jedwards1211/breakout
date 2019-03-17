package org.breakout.proj4;

import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.Proj4jException;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.GeocentricConverter;

public class FromGeocentricCoordinateTransform implements CoordinateTransform {
	GeocentricConverter converter;
	CoordinateTransform fromGeodetic;
	CoordinateReferenceSystem geodeticCrs;
	CoordinateReferenceSystem targetCrs;

	public FromGeocentricCoordinateTransform(CoordinateReferenceSystem targetCrs) {
		this.targetCrs = targetCrs;
		geodeticCrs = targetCrs.createGeographic();
		fromGeodetic = new BasicCoordinateTransform(geodeticCrs, targetCrs);
		converter = new GeocentricConverter(targetCrs.getProjection().getEllipsoid());
	}

	@Override
	public CoordinateReferenceSystem getSourceCRS() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CoordinateReferenceSystem getTargetCRS() {
		return targetCrs;
	}

	@Override
	public ProjCoordinate transform(ProjCoordinate src, ProjCoordinate tgt) throws Proj4jException {
		tgt.setValue(src);
		converter.convertGeocentricToGeodetic(tgt);
		tgt.x = Math.toDegrees(tgt.x);
		tgt.y = Math.toDegrees(tgt.y);
		fromGeodetic.transform(tgt, tgt);
		return tgt;
	}
}
