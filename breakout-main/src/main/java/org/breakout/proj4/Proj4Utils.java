package org.breakout.proj4;

import org.andork.unit.Angle;
import org.andork.unit.UnitizedDouble;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.GeocentricConverter;
import org.osgeo.proj4j.proj.TransverseMercatorProjection;
import org.osgeo.proj4j.util.ProjectionMath;

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
		CoordinateReferenceSystem toSys
	) {
		return convert(
			fromCoord,
			factory.createFromParameters(null, fromSys),
			toSys
		);
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
	

	private static int getRowFromNearestParallelDegrees(double latitude) {
		int degrees = (int)ProjectionMath.normalizeLatitude(latitude);
		if (degrees < -80 || degrees > 84)
			return 0;
		if (degrees > 80)
			return 24;
		return (degrees + 80) / 8 + 3;
	}
	
	private static int getZoneFromNearestMeridianDegrees(double longitude) {
		int zone = (int)Math.floor((ProjectionMath.normalizeLongitude(longitude) + 180.0) * 30.0 / 180.0) + 1;
		if (zone < 1)
			zone = 1;
		else if (zone > 60)
			zone = 60;
		return zone;
	}
	
	public static int getUtmZone(UnitizedDouble<Angle> longitude, UnitizedDouble<Angle> latitude) {
		if (longitude.unit == Angle.degrees) {
			return getUtmZoneDegrees(
				longitude.doubleValue(Angle.degrees),
				latitude.doubleValue(Angle.degrees));
		}
		return getUtmZoneRadians(
			longitude.doubleValue(Angle.radians),
			latitude.doubleValue(Angle.radians));
	}
	
	public static int getUtmZoneDegrees(double longitude, double latitude) {
		int zone = getZoneFromNearestMeridianDegrees(longitude);
		int row = getRowFromNearestParallelDegrees(latitude);
		if (row == 22 && zone == 31 && longitude > 3) {
			return 32;
		}
		if (row == 24) {
			if (zone < 31 || zone > 37) {
				return zone;
			}
			if (latitude < 9) {
				return 31;
			}
			if (latitude < 21) {
				return 33;
			}
			if (latitude < 33) {
				return 35;
			}
			return 37;
		}
		return zone;
	}
	
	public static int getUtmZoneRadians(double longitude, double latitude) {
		int zone = TransverseMercatorProjection.getZoneFromNearestMeridian(longitude);
		int row = TransverseMercatorProjection.getRowFromNearestParallel(latitude);
		if (row == 22 && zone == 31 && longitude > Math.toRadians(3)) {
			return 32;
		}
		if (row == 24) {
			if (zone < 31 || zone > 37) {
				return zone;
			}
			if (latitude < Math.toRadians(9)) {
				return 31;
			}
			if (latitude < Math.toRadians(21)) {
				return 33;
			}
			if (latitude < Math.toRadians(33)) {
				return 35;
			}
			return 37;
		}
		return zone;
	}
}
