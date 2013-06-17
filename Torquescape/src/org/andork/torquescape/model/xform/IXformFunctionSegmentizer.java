package org.andork.torquescape.model.xform;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.J3DTempsPool;
import org.andork.math3d.curve.SegmentedCurve3f;

public class IXformFunctionSegmentizer
{
	public static void getSegments( IXformFunction function , J3DTempsPool pool , List<Float> inParams , List<Point3f> outPoints , List<Vector3f> outTangents , List<Vector3f> outXNormals , List<Vector3f> outYNormals )
	{
		Transform3D xform = pool.getTransform3D( );
		
		for( float param : inParams )
		{
			function.eval( param , pool , xform );
			
			Point3f point = new Point3f( );
			xform.transform( point );
			outPoints.add( point );
			
			Vector3f tangent = new Vector3f( 0 , 0 , 1 );
			xform.transform( tangent );
			outTangents.add( tangent );
			
			Vector3f normalX = new Vector3f( 1 , 0 , 0 );
			xform.transform( normalX );
			outXNormals.add( normalX );
			
			Vector3f normalY = new Vector3f( 0 , 1 , 0 );
			xform.transform( normalY );
			outYNormals.add( normalY );
		}
		
		pool.release( xform );
		
		for( int i = 0 ; i < inParams.size( ) ; i++ )
		{
			Vector3f tangent = outTangents.get( i );
			tangent.sub( outPoints.get( Math.min( outPoints.size( ) - 1 , i + 1 ) ) , outPoints.get( Math.max( 0 , i - 1 ) ) );
			tangent.normalize( );
			
			Vector3f normalX = outXNormals.get( i );
			Vector3f normalY = outYNormals.get( i );
			normalY.cross( tangent , outXNormals.get( i ) );
			if( normalY.x == 0 && normalY.y == 0 && normalY.z == 0 )
			{
				if( i > 0 )
				{
					normalY.set( outYNormals.get( i - 1 ) );
				}
			}
			else
			{
				normalY.normalize( );
			}
			
			normalX.cross( normalY , tangent );
		}
	}
	
	public static SegmentedCurve3f createSegmentedCurve3f( IXformFunction function , J3DTempsPool pool , List<Float> params )
	{
		List<Point3f> points = new ArrayList<Point3f>( );
		List<Vector3f> tangents = new ArrayList<Vector3f>( );
		List<Vector3f> xNormals = new ArrayList<Vector3f>( );
		List<Vector3f> yNormals = new ArrayList<Vector3f>( );
		
		getSegments( function , pool , params , points , tangents , xNormals , yNormals );
		
		return new SegmentedCurve3f( params , points , tangents , xNormals , yNormals );
	}
}
