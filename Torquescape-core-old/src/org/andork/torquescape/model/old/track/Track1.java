package org.andork.torquescape.model.old.track;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.math3d.curve.SegmentedCurve3f;
import org.andork.torquescape.model.old.Triangle;
import org.andork.torquescape.model.old.gen.DefaultTrackSegmentGenerator;
import org.andork.torquescape.model.old.param.ConstantParamFunction;
import org.andork.torquescape.model.old.param.CosParamFunction;
import org.andork.torquescape.model.old.param.LinearParamFunction;
import org.andork.torquescape.model.old.section.PolygonSectionFunction;
import org.andork.torquescape.model.old.xform.Bloater;
import org.andork.torquescape.model.old.xform.CompoundXformFunction;
import org.andork.torquescape.model.old.xform.CurveXformFunction;
import org.andork.torquescape.model.old.xform.Ellipse;
import org.andork.torquescape.model.old.xform.Helicizer;
import org.andork.torquescape.model.old.xform.IXformFunction;
import org.andork.torquescape.model.old.xform.IXformFunctionSegmentizer;

public class Track1 extends Track
{
	public Track1() {
		init();
	}
	
	private void init() {
		IXformFunction xformFunction = new Ellipse( new Point3f( ) , new Vector3f( 0 , 0 , 1 ) , new Vector3f( 50 , 0 , 0 ) , new Vector3f( 0 , 40 , 0 ) );
		Helicizer helicizer = new Helicizer( new LinearParamFunction( 0 , ( float ) Math.PI * 2 , 5 , 20 ) , new LinearParamFunction( 0 , 1 , 3 , 0 ) );
		Bloater bloater = new Bloater( new CosParamFunction( 0 , ( float ) Math.PI / 8 , .5f , 1 ) );
		IXformFunction twister = new Helicizer( new ConstantParamFunction( 0 ) , new LinearParamFunction( 0 , 1 , 0 , 3 ) );
		// curve = new CompoundXformFunction( curve , twister , bloater );
		// xformFunction = new CompoundXformFunction( xformFunction , helicizer , twister , bloater );
		xformFunction = new CompoundXformFunction( xformFunction , helicizer );
		
		List<Float> params = new ArrayList<Float>( );
		for( float f = 0 ; f < Math.PI * 16 + 1 ; f += Math.PI / 180 )
		{
			params.add( f );
		}
		
		SegmentedCurve3f segmentedCurve = IXformFunctionSegmentizer.createSegmentedCurve3f( xformFunction , params );
		xformFunction = new CurveXformFunction( segmentedCurve );
		xformFunction = new CompoundXformFunction( xformFunction , twister , bloater );
		
		PolygonSectionFunction section = new PolygonSectionFunction( 3 , 5 );
		
		setXformFunction( xformFunction );
		setSectionFunction( section );
	}
}