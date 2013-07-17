package org.andork.torquescape.launcher;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.math3d.curve.SegmentedCurve3f;
import org.andork.torquescape.model.Triangle;
import org.andork.torquescape.model.gen.DefaultTrackSegmentGenerator;
import org.andork.torquescape.model.param.ConstantParamFunction;
import org.andork.torquescape.model.param.CosParamFunction;
import org.andork.torquescape.model.param.LinearParamFunction;
import org.andork.torquescape.model.section.PolygonSectionFunction;
import org.andork.torquescape.model.xform.Bloater;
import org.andork.torquescape.model.xform.CompoundXformFunction;
import org.andork.torquescape.model.xform.CurveXformFunction;
import org.andork.torquescape.model.xform.Ellipse;
import org.andork.torquescape.model.xform.Helicizer;
import org.andork.torquescape.model.xform.IXformFunction;
import org.andork.torquescape.model.xform.IXformFunctionSegmentizer;

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
		
		J3DTempsPool pool = new J3DTempsPool( );
		
		SegmentedCurve3f segmentedCurve = IXformFunctionSegmentizer.createSegmentedCurve3f( xformFunction , pool , params );
		xformFunction = new CurveXformFunction( segmentedCurve );
		xformFunction = new CompoundXformFunction( xformFunction , twister , bloater );
		
		PolygonSectionFunction section = new PolygonSectionFunction( 3 , 5 );
		
		setXformFunction( xformFunction );
		setSectionFunction( section );
	}
	
	public static void main( String[ ] args )
	{
		DefaultTrackSegmentGenerator generator = new DefaultTrackSegmentGenerator( );
		List<List<Triangle>> outTriangles = new ArrayList<List<Triangle>>( );
		
		Track track = new Track1( );
		
		J3DTempsPool pool = new J3DTempsPool( );
		
		generator.generate( track.xformFunction , track.sectionFunction , 0 , ( float ) Math.PI * 4 , ( float ) Math.PI / 180 , pool , outTriangles );
		
		TorquescapeLauncher.start(outTriangles);
	}
}
