package org.breakout.proj4;

import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.Proj4jException;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.GeocentricConverter;

public class ToGeocentricCoordinateTransform implements CoordinateTransform {
	CoordinateReferenceSystem sourceCrs;
	CoordinateReferenceSystem geodeticCrs;
	CoordinateTransform toGeodetic;
	GeocentricConverter converter;

	public ToGeocentricCoordinateTransform(CoordinateReferenceSystem sourceCrs) {
		this.sourceCrs = sourceCrs;
		geodeticCrs = sourceCrs.createGeographic();
		toGeodetic = new BasicCoordinateTransform(sourceCrs, geodeticCrs);
		converter = new GeocentricConverter(sourceCrs.getProjection().getEllipsoid());
	}

	@Override
	public CoordinateReferenceSystem getSourceCRS() {
		return sourceCrs;
	}

	@Override
	public CoordinateReferenceSystem getTargetCRS() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProjCoordinate transform(ProjCoordinate src, ProjCoordinate tgt) throws Proj4jException {
		toGeodetic.transform(src, tgt);
		tgt.x = Math.toRadians(tgt.x);
		tgt.y = Math.toRadians(tgt.y);
		converter.convertGeodeticToGeocentric(tgt);
		return tgt;
	}
}
