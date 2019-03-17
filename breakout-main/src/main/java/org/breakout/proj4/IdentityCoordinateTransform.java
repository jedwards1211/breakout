package org.breakout.proj4;

import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.Proj4jException;
import org.osgeo.proj4j.ProjCoordinate;

public class IdentityCoordinateTransform implements CoordinateTransform {
	private IdentityCoordinateTransform() {
	}
	
	public static IdentityCoordinateTransform INSTANCE = new IdentityCoordinateTransform();

	@Override
	public CoordinateReferenceSystem getSourceCRS() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CoordinateReferenceSystem getTargetCRS() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProjCoordinate transform(ProjCoordinate src, ProjCoordinate tgt) throws Proj4jException {
		tgt.setValue(src);
		return tgt;
	}

}
