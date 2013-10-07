package org.andork.vecmath;

public class MixedArrayVecmath
{
	public static void set( double[ ] a , float ... b )
	{
		for( int i = 0 ; i < b.length ; i++ )
		{
			a[ i ] = b[ i ];
		}
	}
	
	public static void set( float[ ] a , double ... b )
	{
		for( int i = 0 ; i < b.length ; i++ )
		{
			a[ i ] = ( float ) b[ i ];
		}
	}
	
	public static void setRow4( float[ ] m , int rowIndex , double[ ] v )
	{
		rowIndex *= 4;
		m[ rowIndex ] = (float)(float)v[ 0 ];
		m[ rowIndex + 1 ] = (float)v[ 1 ];
		m[ rowIndex + 2 ] = (float)v[ 2 ];
		m[ rowIndex + 3 ] = (float)v[ 3 ];
	}
	
	public static void setRow4( float[ ] m , int rowIndex , double[ ] v , int vi )
	{
		rowIndex *= 4;
		m[ rowIndex ] = (float)v[ vi + 0 ];
		m[ rowIndex + 1 ] = (float)v[ vi + 1 ];
		m[ rowIndex + 2 ] = (float)v[ vi + 2 ];
		m[ rowIndex + 3 ] = (float)v[ vi + 3 ];
	}
	
	public static void setRow4( float[ ] m , int rowIndex , double a , double b , double c , double d )
	{
		rowIndex *= 4;
		m[ rowIndex ] = (float) a;
		m[ rowIndex + 1 ] = (float) b;
		m[ rowIndex + 2 ] = (float) c;
		m[ rowIndex + 3 ] = (float) d;
	}
	
	public static void setColumn3( float[ ] m , int colIndex , double a , double b , double c )
	{
		m[ colIndex ] = (float) a;
		m[ colIndex + 4 ] = (float) b;
		m[ colIndex + 8 ] = (float) c;
	}
	
	public static void setColumn3( float[ ] m , int colIndex , double[ ] v )
	{
		m[ colIndex ] = (float)v[ 0 ];
		m[ colIndex + 4 ] = (float)v[ 1 ];
		m[ colIndex + 8 ] = (float)v[ 2 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , double[ ] v )
	{
		m[ colIndex ] = (float)v[ 0 ];
		m[ colIndex + 4 ] = (float)v[ 1 ];
		m[ colIndex + 8 ] = (float)v[ 2 ];
		m[ colIndex + 12 ] = (float)v[ 3 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , double[ ] v , int vi )
	{
		m[ colIndex ] = (float)v[ vi + 0 ];
		m[ colIndex + 4 ] = (float)v[ vi + 1 ];
		m[ colIndex + 8 ] = (float)v[ vi + 2 ];
		m[ colIndex + 12 ] = ( float ) v[ vi + 3 ];
	}
	
	public static void setColumn4( float[ ] m , int colIndex , double a , double b , double c , double d )
	{
		m[ colIndex ] = (float) a;
		m[ colIndex + 4 ] = (float) b;
		m[ colIndex + 8 ] = (float) c;
		m[ colIndex + 12 ] = (float) d;
	}
}
