package org.breakout.proj4;

import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.GeocentricConverter;

public class Proj4Utils {

	private Proj4Utils() {
	}

	private static CRSFactory factory = new CRSFactory();
	
	public static ProjCoordinate convert(
		ProjCoordinate fromCoord,
		CoordinateReferenceSystem fromSys,
		ProjCoordinate toCoord,
		CoordinateReferenceSystem toSys
	) {
		CoordinateTransform xform = new BasicCoordinateTransform(fromSys, toSys);
		xform.transform(fromCoord, toCoord);
		return toCoord;
	}
	
	public static ProjCoordinate convert(
		ProjCoordinate coord,
		CoordinateReferenceSystem fromSys,
		CoordinateReferenceSystem toSys
	) {
		return convert(coord, fromSys, coord, toSys);
	}
	
	public static ProjCoordinate convertToGeographic(
		ProjCoordinate fromCoord,
		CoordinateReferenceSystem sys,
		ProjCoordinate toCoord
	) {
		return convert(fromCoord, sys, toCoord, sys.createGeographic());
	}
		
	public static ProjCoordinate convertToGeographic(
		ProjCoordinate coord,
		CoordinateReferenceSystem sys
	) {
		return convertToGeographic(coord, sys, coord);
	}

	public static ProjCoordinate convert(
		ProjCoordinate fromCoord,
		String fromSys,
		ProjCoordinate toCoord,
		String toSys
	) {
		return convert(
			fromCoord,
			factory.createFromParameters(null, fromSys),
			toCoord,
			factory.createFromParameters(null, toSys)
		);
	}
	
	public static ProjCoordinate convert(
		ProjCoordinate coord,
		String fromSys,
		String toSys
	) {
		return convert(coord, fromSys, coord, toSys);
	}
	
	public static ProjCoordinate convertToGeographic(
		ProjCoordinate fromCoord,
		String sys,
		ProjCoordinate toCoord
	) {
		return convertToGeographic(fromCoord, factory.createFromParameters(null, sys), toCoord);
	}
		
	public static ProjCoordinate convertToGeographic(
		ProjCoordinate coord,
		String sys
	) {
		return convertToGeographic(coord, factory.createFromParameters(null, sys));
	}
	
	public static ProjCoordinate convertToGeocentric(
		ProjCoordinate coord,
		CoordinateReferenceSystem fromSys
	) {
		convertToGeographic(coord, fromSys);
		coord.x = Math.toRadians(coord.x);
		coord.y = Math.toRadians(coord.y);
		GeocentricConverter converter = new GeocentricConverter(fromSys.getProjection().getEllipsoid());
		converter.convertGeodeticToGeocentric(coord);
		return coord;
	}

	public static ProjCoordinate convertToGeocentric(
		ProjCoordinate coord,
		String fromSys
	) {
		return convertToGeocentric(coord, factory.createFromParameters(null, fromSys));
	}
}
