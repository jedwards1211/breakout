package org.andork.vecmath;

import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Point4d;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class VecmathUtils
{
	public static void checkReal( Tuple3d p )
	{
		if( Double.isNaN( p.x ) || Double.isNaN( p.y ) || Double.isNaN( p.z ) )
		{
			throw new IllegalArgumentException( "tuple has NaN values: " + p );
		}
		if( Double.isInfinite( p.x ) || Double.isInfinite( p.y ) || Double.isInfinite( p.z ) )
		{
			throw new IllegalArgumentException( "tuple has Infinite values: " + p );
		}
	}
	
	public static void checkNonzero( Tuple3d p )
	{
		if( p.x == 0 && p.y == 0 && p.z == 0 )
		{
			throw new IllegalArgumentException( "tuple is zero" );
		}
	}
	
	public static void checkReal( Tuple3f p )
	{
		if( Double.isNaN( p.x ) || Double.isNaN( p.y ) || Double.isNaN( p.z ) )
		{
			throw new IllegalArgumentException( "tuple has NaN values: " + p );
		}
		if( Double.isInfinite( p.x ) || Double.isInfinite( p.y ) || Double.isInfinite( p.z ) )
		{
			throw new IllegalArgumentException( "tuple has Infinite values: " + p );
		}
	}
	
	public static void checkNonzero( Tuple3f p )
	{
		if( p.x == 0 && p.y == 0 && p.z == 0 )
		{
			throw new IllegalArgumentException( "tuple is zero" );
		}
	}
	
	public static final Vector3f	UNIT_XF		= new Vector3f( 1 , 0 , 0 );
	public static final Vector3f	UNIT_YF		= new Vector3f( 0 , 1 , 0 );
	public static final Vector3f	UNIT_ZF		= new Vector3f( 0 , 0 , 1 );
	public static final Vector3f	UNIT_NEG_XF	= new Vector3f( -1 , 0 , 0 );
	public static final Vector3f	UNIT_NEG_YF	= new Vector3f( 0 , -1 , 0 );
	public static final Vector3f	UNIT_NEG_ZF	= new Vector3f( 0 , 0 , -1 );
	public static final Point3f		ZEROF		= new Point3f( 0 , 0 , 0 );
	public static final Vector3d	UNIT_XD		= new Vector3d( 1 , 0 , 0 );
	public static final Vector3d	UNIT_YD		= new Vector3d( 0 , 1 , 0 );
	public static final Vector3d	UNIT_ZD		= new Vector3d( 0 , 0 , 1 );
	public static final Point3d		ZEROD		= new Point3d( 0 , 0 , 0 );
	
	public static boolean approxZero( final Tuple3f tuple , float tolerance )
	{
		return Math.abs( tuple.x ) < tolerance && Math.abs( tuple.y ) < tolerance && Math.abs( tuple.z ) < tolerance;
	}
	
	public static <T extends Tuple2f> float[ ] toFloatArray2( T[ ] tuples , float[ ] buffer )
	{
		if( buffer == null )
		{
			buffer = new float[ tuples.length * 2 ];
		}
		int d = 0;
		for( final T tuple : tuples )
		{
			buffer[ d++ ] = tuple.x;
			buffer[ d++ ] = tuple.y;
		}
		return buffer;
	}
	
	public static <T extends Tuple3f> float[ ] toFloatArray3( T[ ] tuples , float[ ] buffer )
	{
		if( buffer == null )
		{
			buffer = new float[ tuples.length * 3 ];
		}
		int d = 0;
		for( final T tuple : tuples )
		{
			buffer[ d++ ] = tuple.x;
			buffer[ d++ ] = tuple.y;
			buffer[ d++ ] = tuple.z;
		}
		return buffer;
	}
	
	public static <T extends Tuple4f> float[ ] toFloatArray4( T[ ] tuples , float[ ] buffer )
	{
		if( buffer == null )
		{
			buffer = new float[ tuples.length * 4 ];
		}
		int d = 0;
		for( final T tuple : tuples )
		{
			buffer[ d++ ] = tuple.x;
			buffer[ d++ ] = tuple.y;
			buffer[ d++ ] = tuple.z;
			buffer[ d++ ] = tuple.w;
		}
		return buffer;
	}
	
	public static ArrayList<Point3f> toPoint3fArrayList( float[ ] points )
	{
		if( ( points.length % 3 ) != 0 )
		{
			throw new IllegalArgumentException( "points.length must be divisible by 3" );
		}
		ArrayList<Point3f> result = new ArrayList<Point3f>( );
		for( int k = 0 ; k < points.length ; k += 3 )
		{
			result.add( new Point3f( points[ k ] , points[ k + 1 ] , points[ k + 2 ] ) );
		}
		return result;
	}
	
	public static ArrayList<Vector3f> toVector3fArrayList( float[ ] points )
	{
		if( ( points.length % 3 ) != 0 )
		{
			throw new IllegalArgumentException( "points.length must be divisible by 3" );
		}
		ArrayList<Vector3f> result = new ArrayList<Vector3f>( );
		for( int k = 0 ; k < points.length ; k += 3 )
		{
			result.add( new Vector3f( points[ k ] , points[ k + 1 ] , points[ k + 2 ] ) );
		}
		return result;
	}
	
	public static Point2f[ ] allocPoint2fArray( int size )
	{
		final Point2f[ ] result = new Point2f[ size ];
		for( int i = 0 ; i < size ; i++ )
		{
			result[ i ] = new Point2f( );
		}
		return result;
	}
	
	public static Vector2f[ ] allocVector2fArray( int size )
	{
		final Vector2f[ ] result = new Vector2f[ size ];
		for( int i = 0 ; i < size ; i++ )
		{
			result[ i ] = new Vector2f( );
		}
		return result;
	}
	
	public static Point3f[ ] allocPoint3fArray( int size )
	{
		final Point3f[ ] result = new Point3f[ size ];
		for( int i = 0 ; i < size ; i++ )
		{
			result[ i ] = new Point3f( );
		}
		return result;
	}
	
	public static Point4f[ ] allocPoint4fArray( int size )
	{
		final Point4f[ ] result = new Point4f[ size ];
		for( int i = 0 ; i < size ; i++ )
		{
			result[ i ] = new Point4f( );
		}
		return result;
	}
	
	public static Point3d[ ] allocPoint3dArray( int size )
	{
		final Point3d[ ] result = new Point3d[ size ];
		for( int i = 0 ; i < size ; i++ )
		{
			result[ i ] = new Point3d( );
		}
		return result;
	}
	
	public static Point2d[ ] allocPoint2dArray( int size )
	{
		final Point2d[ ] result = new Point2d[ size ];
		for( int i = 0 ; i < size ; i++ )
		{
			result[ i ] = new Point2d( );
		}
		return result;
	}
	
	public static Vector3f[ ] allocVector3fArray( int size )
	{
		final Vector3f[ ] result = new Vector3f[ size ];
		for( int i = 0 ; i < size ; i++ )
		{
			result[ i ] = new Vector3f( );
		}
		return result;
	}
	
	public static Point4d toPoint4d( Point3d p , double w )
	{
		return new Point4d( p.x , p.y , p.z , w );
	}
	
	public static Point3d toPoint3d( Point4d p )
	{
		return new Point3d( p.x , p.y , p.z );
	}
	
	public static Point3f toPoint3f( Point4d p )
	{
		return new Point3f( ( float ) p.x , ( float ) p.y , ( float ) p.z );
	}
	
	public static <T extends Tuple2f> float[ ] toFloatArray2( Collection<T> tuples , float[ ] buffer )
	{
		if( buffer == null )
		{
			buffer = new float[ tuples.size( ) * 2 ];
		}
		int d = 0;
		for( final T tuple : tuples )
		{
			buffer[ d++ ] = tuple.x;
			buffer[ d++ ] = tuple.y;
		}
		return buffer;
	}
	
	public static <T extends Tuple3f> float[ ] toFloatArray3( Collection<T> tuples , float[ ] buffer )
	{
		if( buffer == null )
		{
			buffer = new float[ tuples.size( ) * 3 ];
		}
		int d = 0;
		for( final T tuple : tuples )
		{
			buffer[ d++ ] = tuple.x;
			buffer[ d++ ] = tuple.y;
			buffer[ d++ ] = tuple.z;
		}
		return buffer;
	}
	
	public static <T extends Tuple4f> float[ ] toFloatArray4( Collection<T> tuples , float[ ] buffer )
	{
		if( buffer == null )
		{
			buffer = new float[ tuples.size( ) * 4 ];
		}
		int d = 0;
		for( final T tuple : tuples )
		{
			buffer[ d++ ] = tuple.x;
			buffer[ d++ ] = tuple.y;
			buffer[ d++ ] = tuple.z;
			buffer[ d++ ] = tuple.w;
		}
		return buffer;
	}
	
	public static void toArray( Matrix4f m , float[ ] out )
	{
		out[ 0 ] = m.m00;
		out[ 1 ] = m.m01;
		out[ 2 ] = m.m02;
		out[ 3 ] = m.m03;
		out[ 4 ] = m.m10;
		out[ 5 ] = m.m11;
		out[ 6 ] = m.m12;
		out[ 7 ] = m.m13;
		out[ 8 ] = m.m20;
		out[ 9 ] = m.m21;
		out[ 10 ] = m.m22;
		out[ 11 ] = m.m23;
		out[ 12 ] = m.m30;
		out[ 13 ] = m.m31;
		out[ 14 ] = m.m32;
		out[ 15 ] = m.m33;
	}
}
