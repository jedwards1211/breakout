package org.andork.math.curve;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public class SegmentedCurve3f implements ICurveWithNormals3f
{
	public SegmentedCurve3f( List<Float> params , List<Point3f> points , List<Vector3f> tangents , List<Vector3f> xNormals , List<Vector3f> yNormals )
	{
		if( params.size( ) != points.size( ) )
		{
			throw new IllegalArgumentException( "params and points must be the same size" );
		}
		if( params.size( ) != tangents.size( ) )
		{
			throw new IllegalArgumentException( "params and tangents must be the same size" );
		}
		if( params.size( ) != xNormals.size( ) )
		{
			throw new IllegalArgumentException( "params and xNormals must be the same size" );
		}
		if( params.size( ) != yNormals.size( ) )
		{
			throw new IllegalArgumentException( "params and yNormals must be the same size" );
		}
		
		this.params = params;
		this.points = points;
		this.tangents = tangents;
		this.xNormals = xNormals;
		this.yNormals = yNormals;
	}
	
	private List<Float>		params;
	private List<Point3f>	points;
	private List<Vector3f>	tangents;
	private List<Vector3f>	xNormals;
	private List<Vector3f>	yNormals;
	
	private int findInsertionIndex( float param )
	{
		return Collections.binarySearch( params , param );
	}
	
	private void checkInsertionIndex( int index )
	{
		if( index == 0 || index == params.size( ) )
		{
			throw new IllegalArgumentException( "param is out of range" );
		}
	}
	
	private Tuple3f get( float param , List<? extends Tuple3f> list , Tuple3f result )
	{
		int index = findInsertionIndex( param );
		if( index < 0 )
		{
			index = -( index + 1 );
			checkInsertionIndex( index );
			float lower = params.get( index - 1 );
			float upper = params.get( index );
			float f = ( param - lower ) / ( upper - lower );
			result.interpolate( list.get( index - 1 ) , list.get( index ) , f );
		}
		else
		{
			result.set( list.get( index ) );
		}
		return result;
	}
	
	@Override
	public Vector3f getNormal( float param , Vector3f result )
	{
		get( param , yNormals , result );
		result.normalize( );
		return result;
	}
	
	@Override
	public Vector3f getBinormal( float param , Vector3f result )
	{
		get( param , xNormals , result );
		result.normalize( );
		return result;
	}
	
	@Override
	public Vector3f getTangent( float param , Vector3f result )
	{
		get( param , tangents , result );
		result.normalize( );
		return result;
	}
	
	@Override
	public Point3f getPoint( float param , Point3f result )
	{
		return ( Point3f ) get( param , points , result );
	}
}