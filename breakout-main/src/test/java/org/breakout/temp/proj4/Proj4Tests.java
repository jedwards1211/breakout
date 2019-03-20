package org.breakout.temp.proj4;

import org.breakout.proj4.Proj4Utils;
import org.breakout.proj4.WebMercatorProjection;
import org.junit.Test;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.datum.Datum;
import org.osgeo.proj4j.datum.Ellipsoid;

public class Proj4Tests {
	@Test
	public void testParser() {
//		CRSFactory factory = new CRSFactory();
//		CoordinateReferenceSystem merc = factory.createFromParameters(null, "+proj=merc +lat_ts=56.5 +ellps=GRS80");
//		CoordinateReferenceSystem utm32 = factory.createFromParameters(null, "+proj=utm +zone=32");
//		CoordinateReferenceSystem utm14 = factory.createFromParameters(null, "+proj=utm +zone=14");
//		CoordinateReferenceSystem utm15 = factory.createFromParameters(null, "+proj=utm +zone=15");
//		CoordinateTransform xform;
//		ProjCoordinate from;
//		ProjCoordinate to = new ProjCoordinate();
//		xform = new BasicCoordinateTransform(utm14, utm14.createGeographic());
//		from = new ProjCoordinate(600285.004, 3334617.996);
//		xform.transform(from, to);
//		System.out.println(to);
//	
//		xform = new BasicCoordinateTransform(merc, utm32);
//		from = new ProjCoordinate(3399483.80, 752085.60, 0.0);
//		xform.transform(from, to);
//		System.out.println(to);
//		
//		CoordinateReferenceSystem totesGebirge = factory.createFromParameters(null, "+proj=tmerc +lat_0=0 +lon_0=13d20 +k=1 +x_0=0 +y_0=-5200000 +ellps=bessel +towgs84=577.326,90.129,463.919,5.137,1.474,5.297,2.4232");
//		xform = new BasicCoordinateTransform(totesGebirge.createGeographic(), totesGebirge);
//		from = new ProjCoordinate(14.068727, 47.716934);
//		xform.transform(from, to);
//		System.out.println(to);
		System.out.println(
			Proj4Utils.convertToGeographic(
				new ProjCoordinate(731433.68,1995104.72,422.78),
				"+proj=utm +zone=14 +ellps=WGS84"
			)
		);
		System.out.println(
			Proj4Utils.convert(
				new ProjCoordinate(35,28,0),
				"+proj=latlong +datum=WGS84 +ellps=WGS84",
				"+proj=aeqd +datum=WGS84 +ellps=WGS84 +lon_0=34 +lat_0=27"
			)
		);
		System.out.println(
			Proj4Utils.convertToGeocentric(
				new ProjCoordinate(-96.81386583126447, 18.032105013826563, 422.78),
				"+proj=latlong +datum=WGS84 +ellps=WGS84"
			)
		);
		System.out.println(
			Proj4Utils.convertToGeocentric(
				new ProjCoordinate(731433.68,1995104.72,422.78),
				"+proj=utm +zone=14 +ellps=WGS84"
			)
		);
		WebMercatorProjection webmerc = new WebMercatorProjection(null, null);
		System.out.println(
			Proj4Utils.convert(
				new ProjCoordinate(-90,-85, 0),
				"+proj=latlong +datum=WGS84 +ellps=WGS84",
				new CoordinateReferenceSystem(null, new String[0], Datum.WGS84, webmerc)
			)
		);
	}
}
