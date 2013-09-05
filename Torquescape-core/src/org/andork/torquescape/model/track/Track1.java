package org.andork.torquescape.model.track;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.math.curve.SegmentedCurve3f;
import org.andork.torquescape.model.meshing.FixedMeshingFn;
import org.andork.torquescape.model.param.ConstantParamFn;
import org.andork.torquescape.model.param.CosParamFn;
import org.andork.torquescape.model.param.LinearParamFn;
import org.andork.torquescape.model.section.FixedSectionFn;
import org.andork.torquescape.model.section.ISectionFn;
import org.andork.torquescape.model.section.SectionUtils;
import org.andork.torquescape.model.section.XformedSectionFn;
import org.andork.torquescape.model.xform.Bloater;
import org.andork.torquescape.model.xform.CompoundXformFn;
import org.andork.torquescape.model.xform.CurveXformFn;
import org.andork.torquescape.model.xform.Ellipse;
import org.andork.torquescape.model.xform.Helicizer;
import org.andork.torquescape.model.xform.IXformFn;
import org.andork.torquescape.model.xform.IXformFnSegmentizer;

public class Track1 extends Track
{
	public Track1( )
	{
		init( );
	}
	
	private void init( )
	{
		IXformFn xformFunction = new Ellipse( new Point3f( ) , new Vector3f( 0 , 0 , 1 ) , new Vector3f( 50 , 0 , 0 ) , new Vector3f( 0 , 40 , 0 ) );
		Helicizer helicizer = new Helicizer( new LinearParamFn( 0 , ( float ) Math.PI * 2 , 5 , 20 ) , new LinearParamFn( 0 , 1 , 3 , 0 ) );
		Bloater bloater = new Bloater( new CosParamFn( 0 , ( float ) Math.PI / 8 , .5f , 1 ) );
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
		
		ISectionFn section = new FixedSectionFn( SectionUtils.generateSharpPolygon( 3 , 5 ) );
		section = new XformedSectionFn( section , xformFunction );
		
		char[ ] meshing = { 1 , 2 , 7 , 8 , 7 , 2 , 3 , 4 , 9 , 10 , 9 , 4 , 5 , 0 , 11 , 6 , 11 , 0 };
		
		FixedMeshingFn meshingFn = new FixedMeshingFn( meshing );
		
		setSectionFn( section );
		setMeshingFn( meshingFn );
	}
}
