package org.andork.torquescape.model.old.xform;

import static org.andork.vecmath.FloatArrayVecmath.mpmulAffine;
import static org.andork.vecmath.FloatArrayVecmath.mvmulAffine;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.andork.math.curve.SegmentedCurve3f;

public class IXformFunctionSegmentizer
{
	public static void getSegments( IXformFunction function , List<Float> inParams , List<Point3f> outPoints , List<Vector3f> outTangents , List<Vector3f> outXNormals , List<Vector3f> outYNormals )
	{
		float[ ] xform = new float[ 16 ];
		
		for( float param : inParams )
		{
			function.eval( param , xform );
			
			float[ ] point = new float[ 3 ];
			mpmulAffine( xform , point );
			outPoints.add( new Point3f( point ) );
			
			float[ ] tangent = { 0 , 0 , 1 };
			mvmulAffine( xform , tangent );
			outTangents.add( new Vector3f( tangent ) );
			
			float[ ] normalX = { 1 , 0 , 0 };
			mvmulAffine( xform , normalX );
			outXNormals.add( new Vector3f( normalX ) );
			
			float[ ] normalY = { 0 , 1 , 0 };
			mvmulAffine( xform , normalY );
			outYNormals.add( new Vector3f( normalY ) );
		}
		
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
	
	public static SegmentedCurve3f createSegmentedCurve3f( IXformFunction function , List<Float> params )
	{
		List<Point3f> points = new ArrayList<Point3f>( );
		List<Vector3f> tangents = new ArrayList<Vector3f>( );
		List<Vector3f> xNormals = new ArrayList<Vector3f>( );
		List<Vector3f> yNormals = new ArrayList<Vector3f>( );
		
		getSegments( function , params , points , tangents , xNormals , yNormals );
		
		return new SegmentedCurve3f( params , points , tangents , xNormals , yNormals );
	}
}
