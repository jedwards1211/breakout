package org.andork.torquescape.model2.track;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.math3d.curve.SegmentedCurve3f;
import org.andork.torquescape.model.param.ConstantParamFunction;
import org.andork.torquescape.model.param.CosParamFunction;
import org.andork.torquescape.model.param.LinearParamFunction;
import org.andork.torquescape.model.xform.Bloater;
import org.andork.torquescape.model.xform.CompoundXformFunction;
import org.andork.torquescape.model.xform.CurveXformFunction;
import org.andork.torquescape.model.xform.Ellipse;
import org.andork.torquescape.model.xform.Helicizer;
import org.andork.torquescape.model.xform.IXformFunction;
import org.andork.torquescape.model.xform.IXformFunctionSegmentizer;
import org.andork.torquescape.model2.meshing.FixedMeshingFunction;
import org.andork.torquescape.model2.section.FixedSectionFunction;
import org.andork.torquescape.model2.section.SectionUtils;

public class Track1 extends Track
{
	public Track1( )
	{
		init( );
	}
	
	private void init( )
	{
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
		
		FixedSectionFunction section = new FixedSectionFunction( SectionUtils.generateSharpPolygon( 3 , 5 ) );
		
		char[ ] meshing = { 1 , 2 , 7 , 8 , 7 , 2 , 3 , 4 , 9 , 10 , 9 , 4 , 5 , 0 , 11 , 6 , 11 , 0 };
		
		FixedMeshingFunction meshingFunction = new FixedMeshingFunction( meshing );
		
		setXformFunction( xformFunction );
		setSectionFunction( section );
		setMeshingFunction( meshingFunction );
	}
}
