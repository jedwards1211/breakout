package org.andork.torquescape.model.track;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.bspline.FloatArrayBSpline;
import org.andork.math.curve.BSplines;
import org.andork.math.curve.SegmentedCurve3f;
import org.andork.torquescape.model.coord.FixedCoordFn;
import org.andork.torquescape.model.coord.ICoordFn;
import org.andork.torquescape.model.coord.CoordUtils;
import org.andork.torquescape.model.coord.XformedCoordFn;
import org.andork.torquescape.model.index.FixedIndexFn;
import org.andork.torquescape.model.param.BSplineParamFn;
import org.andork.torquescape.model.param.ConstantParamFn;
import org.andork.torquescape.model.param.CosParamFn;
import org.andork.torquescape.model.param.LinearParamFn;
import org.andork.torquescape.model.xform.Bloater;
import org.andork.torquescape.model.xform.CompoundXformFn;
import org.andork.torquescape.model.xform.CurveXformFn;
import org.andork.torquescape.model.xform.Ellipse;
import org.andork.torquescape.model.xform.Helicizer;
import org.andork.torquescape.model.xform.IXformFn;
import org.andork.torquescape.model.xform.IXformFnSegmentizer;
import org.andork.util.Reparam;

public class Track1b extends Track
{
	public Track1b( )
	{
		init( );
	}
	
	private void init( )
	{
		IXformFn xformFunction = new Ellipse( new Point3f( ) , new Vector3f( 0 , 0 , 1 ) , new Vector3f( 50 , 0 , 0 ) , new Vector3f( 0 , 40 , 0 ) );
		Helicizer helicizer = new Helicizer( new LinearParamFn( 0 , ( float ) Math.PI * 2 , 5 , 20 ) , new LinearParamFn( 0 , 1 , 3 , 0 ) );
		
		FloatArrayBSpline bspline = new FloatArrayBSpline( );
		bspline.degree = 3;
		bspline.dimension = 1;
		bspline.points = new float[ ] { .5f , .6f , .4f , 1f , .4f , 10f , 1f , .9f , 1.2f , 1.8f , 10f , 1.2f };
		bspline.knots = BSplines.createUniformKnots( 3 , bspline.points.length );
		Reparam.linear( bspline.knots , 0 , 1 , 0 , ( float ) Math.PI * 10 , bspline.knots );
		
		Bloater bloater = new Bloater( new BSplineParamFn( bspline ) );
		IXformFn twister = new Helicizer( new ConstantParamFn( 0 ) , new LinearParamFn( 0 , 1 , 0 , 3 ) );
		// curve = new CompoundXformFunction( curve , twister , bloater );
		// xformFunction = new CompoundXformFunction( xformFunction , helicizer , twister , bloater );
		xformFunction = new CompoundXformFn( xformFunction , helicizer );
		
		List<Float> params = new ArrayList<Float>( );
		for( float f = 0 ; f < Math.PI * 16 + 1 ; f += Math.PI / 180 )
		{
			params.add( f );
		}
		
		SegmentedCurve3f segmentedCurve = IXformFnSegmentizer.createSegmentedCurve3f( xformFunction , params );
		xformFunction = new CurveXformFn( segmentedCurve );
		xformFunction = new CompoundXformFn( xformFunction , twister , bloater );
		
		ICoordFn section = new FixedCoordFn( CoordUtils.generateSharpPolygon( 3 , 5 ) );
		section = new XformedCoordFn( section , xformFunction );
		
		char[ ] meshing = { 1 , 2 , 7 , 8 , 7 , 2 , 3 , 4 , 9 , 10 , 9 , 4 , 5 , 0 , 11 , 6 , 11 , 0 };
		
		FixedIndexFn meshingFn = new FixedIndexFn( meshing );
		
		setCoordFn( section );
		setIndexFn( meshingFn );
	}
}
