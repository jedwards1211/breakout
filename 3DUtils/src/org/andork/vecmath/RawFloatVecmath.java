package org.andork.vecmath;

public class RawFloatVecmath
{
	private static final float	FEPS	= 1.110223024E-8f;
	
	public static float distance3( float[ ] a , float[ ] b )
	{
		float dx = a[ 0 ] - b[ 0 ];
		float dy = a[ 1 ] - b[ 1 ];
		float dz = a[ 2 ] - b[ 2 ];
		
		return ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public static float distance3( float[ ] a , int ai , float[ ] b , int bi )
	{
		float dx = a[ ai ] - b[ bi ];
		float dy = a[ ai + 1 ] - b[ bi + 1 ];
		float dz = a[ ai + 2 ] - b[ bi + 2 ];
		
		return ( float ) Math.sqrt( dx * dx + dy * dy + dz * dz );
	}
	
	public static float dot3( float[ ] a , float[ ] b )
	{
		return a[ 0 ] * b[ 0 ] + a[ 1 ] * b[ 1 ] + a[ 2 ] * b[ 2 ];
	}
	
	public static float dot3( float[ ] a , int ai , float[ ] b , int bi )
	{
		return a[ ai + 0 ] * b[ bi + 0 ] + a[ ai + 1 ] * b[ bi + 1 ] + a[ ai + 2 ] * b[ bi + 2 ];
	}
	
	public static void cross( float[ ] a , float[ ] b , float[ ] out )
	{
		if( out != a && out != b )
		{
			out[ 0 ] = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
			out[ 1 ] = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
			out[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
		}
		else
		{
			float x = a[ 1 ] * b[ 2 ] - a[ 2 ] * b[ 1 ];
			float y = a[ 2 ] * b[ 0 ] - a[ 0 ] * b[ 2 ];
			out[ 2 ] = a[ 0 ] * b[ 1 ] - a[ 1 ] * b[ 0 ];
			out[ 1 ] = y;
			out[ 0 ] = x;
		}
	}
	
	public static void cross( float[ ] a , int ai , float[ ] b , int bi , float[ ] out , int outi )
	{
		if( out != a && out != b )
		{
			out[ outi + 0 ] = a[ ai + 1 ] * b[ bi + 2 ] - a[ ai + 2 ] * b[ bi + 1 ];
			out[ outi + 1 ] = a[ ai + 2 ] * b[ bi + 0 ] - a[ ai + 0 ] * b[ bi + 2 ];
			out[ outi + 2 ] = a[ ai + 0 ] * b[ bi + 1 ] - a[ ai + 1 ] * b[ bi + 0 ];
		}
		else
		{
			float x = a[ ai + 1 ] * b[ bi + 2 ] - a[ ai + 2 ] * b[ bi + 1 ];
			float y = a[ ai + 2 ] * b[ bi + 0 ] - a[ ai + 0 ] * b[ bi + 2 ];
			out[ outi + 2 ] = a[ ai + 0 ] * b[ bi + 1 ] - a[ ai + 1 ] * b[ bi + 0 ];
			out[ outi + 1 ] = y;
			out[ outi + 0 ] = x;
		}
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p )
	{
		float x = m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ];
		float y = m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ];
		p[ 2 ] = m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ];
		p[ 1 ] = y;
		p[ 0 ] = x;
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , int vi )
	{
		float x = m[ 0 ] * p[ vi ] + m[ 1 ] * p[ vi + 1 ] + m[ 2 ] * p[ vi + 2 ] + m[ 3 ];
		float y = m[ 4 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 6 ] * p[ vi + 2 ] + m[ 7 ];
		p[ vi + 2 ] = m[ 8 ] * p[ vi ] + m[ 9 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 11 ];
		p[ vi + 1 ] = y;
		p[ vi ] = x;
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , float[ ] out )
	{
		if( p != out )
		{
			out[ 0 ] = m[ 0 ] * p[ 0 ] + m[ 1 ] * p[ 1 ] + m[ 2 ] * p[ 2 ] + m[ 3 ];
			out[ 1 ] = m[ 4 ] * p[ 0 ] + m[ 5 ] * p[ 1 ] + m[ 6 ] * p[ 2 ] + m[ 7 ];
			out[ 2 ] = m[ 8 ] * p[ 0 ] + m[ 9 ] * p[ 1 ] + m[ 10 ] * p[ 2 ] + m[ 11 ];
		}
		else
		{
			mpmulAffine( m , p );
		}
	}
	
	public static void mpmulAffine( float[ ] m , float[ ] p , int vi , float[ ] out , int outi )
	{
		if( p != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * p[ vi ] + m[ 1 ] * p[ vi + 1 ] + m[ 2 ] * p[ vi + 2 ] + m[ 3 ];
			out[ outi + 1 ] = m[ 4 ] * p[ vi ] + m[ 5 ] * p[ vi + 1 ] + m[ 6 ] * p[ vi + 2 ] + m[ 7 ];
			out[ outi + 2 ] = m[ 8 ] * p[ vi ] + m[ 9 ] * p[ vi + 1 ] + m[ 10 ] * p[ vi + 2 ] + m[ 11 ];
		}
		else
		{
			mpmulAffine( m , p , vi );
		}
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v )
	{
		float x = m[ 0 ] * v[ 0 ] + m[ 1 ] * v[ 1 ] + m[ 2 ] * v[ 2 ];
		float y = m[ 4 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 6 ] * v[ 2 ];
		v[ 2 ] = m[ 8 ] * v[ 0 ] + m[ 9 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		v[ 1 ] = y;
		v[ 0 ] = x;
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , int vi )
	{
		float x = m[ 0 ] * v[ vi ] + m[ 1 ] * v[ vi + 1 ] + m[ 2 ] * v[ vi + 2 ];
		float y = m[ 4 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 6 ] * v[ vi + 2 ];
		v[ vi + 2 ] = m[ 8 ] * v[ vi ] + m[ 9 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
		v[ vi + 1 ] = y;
		v[ vi ] = x;
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , float[ ] out )
	{
		if( v != out )
		{
			out[ 0 ] = m[ 0 ] * v[ 0 ] + m[ 1 ] * v[ 1 ] + m[ 2 ] * v[ 2 ];
			out[ 1 ] = m[ 4 ] * v[ 0 ] + m[ 5 ] * v[ 1 ] + m[ 6 ] * v[ 2 ];
			out[ 2 ] = m[ 8 ] * v[ 0 ] + m[ 9 ] * v[ 1 ] + m[ 10 ] * v[ 2 ];
		}
		else
		{
			mvmulAffine( m , v );
		}
	}
	
	public static void mvmulAffine( float[ ] m , float[ ] v , int vi , float[ ] out , int outi )
	{
		if( v != out || vi != outi )
		{
			out[ outi ] = m[ 0 ] * v[ vi ] + m[ 1 ] * v[ vi + 1 ] + m[ 2 ] * v[ vi + 2 ];
			out[ outi + 1 ] = m[ 4 ] * v[ vi ] + m[ 5 ] * v[ vi + 1 ] + m[ 6 ] * v[ vi + 2 ];
			out[ outi + 2 ] = m[ 8 ] * v[ vi ] + m[ 9 ] * v[ vi + 1 ] + m[ 10 ] * v[ vi + 2 ];
		}
		else
		{
			mvmulAffine( m , v , vi );
		}
	}
	
	public static void mmul( float[ ] ma , float[ ] mb , float[ ] out )
	{
		if( out == ma || out == mb )
		{
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ] + ma[ 3 ] * mb[ 12 ];
			float m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ] + ma[ 3 ] * mb[ 13 ];
			float m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ] + ma[ 3 ] * mb[ 14 ];
			float m03 = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ] * mb[ 15 ];
			
			float m10 = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ] + ma[ 7 ] * mb[ 12 ];
			float m11 = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ] + ma[ 7 ] * mb[ 13 ];
			float m12 = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ] + ma[ 7 ] * mb[ 14 ];
			float m13 = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ] * mb[ 15 ];
			
			float m20 = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ] + ma[ 11 ] * mb[ 12 ];
			float m21 = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ] + ma[ 11 ] * mb[ 13 ];
			float m22 = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ] + ma[ 11 ] * mb[ 14 ];
			float m23 = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ] * mb[ 15 ];
			
			float m30 = ma[ 12 ] * mb[ 0 ] + ma[ 13 ] * mb[ 4 ] + ma[ 14 ] * mb[ 8 ] + ma[ 15 ] * mb[ 12 ];
			float m31 = ma[ 12 ] * mb[ 1 ] + ma[ 13 ] * mb[ 5 ] + ma[ 14 ] * mb[ 9 ] + ma[ 15 ] * mb[ 13 ];
			float m32 = ma[ 12 ] * mb[ 2 ] + ma[ 13 ] * mb[ 6 ] + ma[ 14 ] * mb[ 10 ] + ma[ 15 ] * mb[ 14 ];
			float m33 = ma[ 12 ] * mb[ 3 ] + ma[ 13 ] * mb[ 7 ] + ma[ 14 ] * mb[ 11 ] + ma[ 15 ] * mb[ 15 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 3 ] = m03;
			out[ 4 ] = m10;
			out[ 5 ] = m11;
			out[ 6 ] = m12;
			out[ 7 ] = m13;
			out[ 8 ] = m20;
			out[ 9 ] = m21;
			out[ 10 ] = m22;
			out[ 11 ] = m23;
			out[ 12 ] = m30;
			out[ 13 ] = m31;
			out[ 14 ] = m32;
			out[ 15 ] = m33;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ] + ma[ 3 ] * mb[ 12 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ] + ma[ 3 ] * mb[ 13 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ] + ma[ 3 ] * mb[ 14 ];
			out[ 3 ] = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ] * mb[ 15 ];
			
			out[ 4 ] = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ] + ma[ 7 ] * mb[ 12 ];
			out[ 5 ] = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ] + ma[ 7 ] * mb[ 13 ];
			out[ 6 ] = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ] + ma[ 7 ] * mb[ 14 ];
			out[ 7 ] = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ] * mb[ 15 ];
			
			out[ 8 ] = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ] + ma[ 11 ] * mb[ 12 ];
			out[ 9 ] = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ] + ma[ 11 ] * mb[ 13 ];
			out[ 10 ] = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ] + ma[ 11 ] * mb[ 14 ];
			out[ 11 ] = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ] * mb[ 15 ];
			
			out[ 12 ] = ma[ 12 ] * mb[ 0 ] + ma[ 13 ] * mb[ 4 ] + ma[ 14 ] * mb[ 8 ] + ma[ 15 ] * mb[ 12 ];
			out[ 13 ] = ma[ 12 ] * mb[ 1 ] + ma[ 13 ] * mb[ 5 ] + ma[ 14 ] * mb[ 9 ] + ma[ 15 ] * mb[ 13 ];
			out[ 14 ] = ma[ 12 ] * mb[ 2 ] + ma[ 13 ] * mb[ 6 ] + ma[ 14 ] * mb[ 10 ] + ma[ 15 ] * mb[ 14 ];
			out[ 15 ] = ma[ 12 ] * mb[ 3 ] + ma[ 13 ] * mb[ 7 ] + ma[ 14 ] * mb[ 11 ] + ma[ 15 ] * mb[ 15 ];
		}
	}
	
	public static void mmulAffine( float[ ] ma , float[ ] mb , float[ ] out )
	{
		if( out == ma || out == mb )
		{
			float m00 = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			float m01 = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			float m02 = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			float m03 = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ];
			
			float m10 = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			float m11 = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			float m12 = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			float m13 = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ];
			
			float m20 = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			float m21 = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			float m22 = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
			float m23 = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ];
			
			out[ 0 ] = m00;
			out[ 1 ] = m01;
			out[ 2 ] = m02;
			out[ 3 ] = m03;
			out[ 4 ] = m10;
			out[ 5 ] = m11;
			out[ 6 ] = m12;
			out[ 7 ] = m13;
			out[ 8 ] = m20;
			out[ 9 ] = m21;
			out[ 10 ] = m22;
			out[ 11 ] = m23;
		}
		else
		{
			out[ 0 ] = ma[ 0 ] * mb[ 0 ] + ma[ 1 ] * mb[ 4 ] + ma[ 2 ] * mb[ 8 ];
			out[ 1 ] = ma[ 0 ] * mb[ 1 ] + ma[ 1 ] * mb[ 5 ] + ma[ 2 ] * mb[ 9 ];
			out[ 2 ] = ma[ 0 ] * mb[ 2 ] + ma[ 1 ] * mb[ 6 ] + ma[ 2 ] * mb[ 10 ];
			out[ 3 ] = ma[ 0 ] * mb[ 3 ] + ma[ 1 ] * mb[ 7 ] + ma[ 2 ] * mb[ 11 ] + ma[ 3 ];
			
			out[ 4 ] = ma[ 4 ] * mb[ 0 ] + ma[ 5 ] * mb[ 4 ] + ma[ 6 ] * mb[ 8 ];
			out[ 5 ] = ma[ 4 ] * mb[ 1 ] + ma[ 5 ] * mb[ 5 ] + ma[ 6 ] * mb[ 9 ];
			out[ 6 ] = ma[ 4 ] * mb[ 2 ] + ma[ 5 ] * mb[ 6 ] + ma[ 6 ] * mb[ 10 ];
			out[ 7 ] = ma[ 4 ] * mb[ 3 ] + ma[ 5 ] * mb[ 7 ] + ma[ 6 ] * mb[ 11 ] + ma[ 7 ];
			
			out[ 8 ] = ma[ 8 ] * mb[ 0 ] + ma[ 9 ] * mb[ 4 ] + ma[ 10 ] * mb[ 8 ];
			out[ 9 ] = ma[ 8 ] * mb[ 1 ] + ma[ 9 ] * mb[ 5 ] + ma[ 10 ] * mb[ 9 ];
			out[ 10 ] = ma[ 8 ] * mb[ 2 ] + ma[ 9 ] * mb[ 6 ] + ma[ 10 ] * mb[ 10 ];
			out[ 11 ] = ma[ 8 ] * mb[ 3 ] + ma[ 9 ] * mb[ 7 ] + ma[ 10 ] * mb[ 11 ] + ma[ 11 ];
		}
	}
	
	public static void setIdentity( float[ ] m )
	{
		m[ 0 ] = 1;
		m[ 1 ] = 0;
		m[ 2 ] = 0;
		m[ 3 ] = 0;
		
		m[ 4 ] = 0;
		m[ 5 ] = 1;
		m[ 6 ] = 0;
		m[ 7 ] = 0;
		
		m[ 8 ] = 0;
		m[ 9 ] = 0;
		m[ 10 ] = 1;
		m[ 11 ] = 0;
		
		m[ 12 ] = 0;
		m[ 13 ] = 0;
		m[ 14 ] = 0;
		m[ 15 ] = 1;
	}
	
	public static void setIdentityAffine( float[ ] m )
	{
		m[ 0 ] = 1;
		m[ 1 ] = 0;
		m[ 2 ] = 0;
		m[ 3 ] = 0;
		
		m[ 4 ] = 0;
		m[ 5 ] = 1;
		m[ 6 ] = 0;
		m[ 7 ] = 0;
		
		m[ 8 ] = 0;
		m[ 9 ] = 0;
		m[ 10 ] = 1;
		m[ 11 ] = 0;
	}
	
	public static void setTranslation( float[ ] m , float[ ] v )
	{
		m[ 3 ] = v[ 0 ];
		m[ 7 ] = v[ 1 ];
		m[ 11 ] = v[ 2 ];
	}
	
	public static void setTranslation( float[ ] m , float[ ] v , int vi )
	{
		m[ 3 ] = v[ vi ];
		m[ 7 ] = v[ vi + 1 ];
		m[ 11 ] = v[ vi + 2 ];
	}
	
	public static void getTranslation( float[ ] m , float[ ] v )
	{
		v[ 0 ] = m[ 3 ];
		v[ 1 ] = m[ 7 ];
		v[ 2 ] = m[ 11 ];
	}
	
	public static void getTranslation( float[ ] m , float[ ] v , int vi )
	{
		v[ vi + 0 ] = m[ 3 ];
		v[ vi + 1 ] = m[ 7 ];
		v[ vi + 2 ] = m[ 11 ];
	}
	
	public static void setScale( float[ ] m , float[ ] v )
	{
		m[ 0 ] = v[ 0 ];
		m[ 5 ] = v[ 1 ];
		m[ 10 ] = v[ 2 ];
	}
	
	public static void setScale( float[ ] m , float[ ] v , int vi )
	{
		m[ 0 ] = v[ vi ];
		m[ 5 ] = v[ vi + 1 ];
		m[ 10 ] = v[ vi + 2 ];
	}
	
	public static void getScale( float[ ] m , float[ ] v )
	{
		v[ 0 ] = m[ 0 ];
		v[ 1 ] = m[ 5 ];
		v[ 2 ] = m[ 10 ];
	}
	
	public static void getScale( float[ ] m , float[ ] v , int vi )
	{
		v[ vi + 0 ] = m[ 0 ];
		v[ vi + 1 ] = m[ 5 ];
		v[ vi + 2 ] = m[ 10 ];
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the x axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *            the angle to rotate about the X axis in radians
	 */
	public static void rotX( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = 1f;
		mat[ 1 ] = 0f;
		mat[ 2 ] = 0f;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = 0f;
		mat[ 5 ] = cosAngle;
		mat[ 6 ] = -sinAngle;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = 0f;
		mat[ 9 ] = sinAngle;
		mat[ 10 ] = cosAngle;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the y axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *            the angle to rotate about the Y axis in radians
	 */
	public static void rotY( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 1 ] = 0f;
		mat[ 2 ] = sinAngle;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = 0f;
		mat[ 5 ] = 1f;
		mat[ 6 ] = 0f;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = -sinAngle;
		mat[ 9 ] = 0f;
		mat[ 10 ] = cosAngle;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	/**
	 * Sets the value of this transform to a counter clockwise rotation about the z axis. All of the non-rotational components are set as if this were an
	 * identity matrix.
	 * 
	 * @param angle
	 *            the angle to rotate about the Z axis in radians
	 */
	public static void rotZ( float[ ] mat , float angle )
	{
		float sinAngle = ( float ) Math.sin( angle );
		float cosAngle = ( float ) Math.cos( angle );
		
		mat[ 0 ] = cosAngle;
		mat[ 1 ] = -sinAngle;
		mat[ 2 ] = 0f;
		mat[ 3 ] = 0f;
		
		mat[ 4 ] = sinAngle;
		mat[ 5 ] = cosAngle;
		mat[ 6 ] = 0f;
		mat[ 7 ] = 0f;
		
		mat[ 8 ] = 0f;
		mat[ 9 ] = 0f;
		mat[ 10 ] = 1f;
		mat[ 11 ] = 0f;
		
		mat[ 12 ] = 0f;
		mat[ 13 ] = 0f;
		mat[ 14 ] = 0f;
		mat[ 15 ] = 1f;
	}
	
	public static void transpose( float[ ] m , float[ ] out )
	{
		if( out != m )
		{
			out[ 0 ] = m[ 0 ];
			out[ 1 ] = m[ 4 ];
			out[ 2 ] = m[ 8 ];
			out[ 3 ] = m[ 12 ];
			
			out[ 4 ] = m[ 1 ];
			out[ 5 ] = m[ 5 ];
			out[ 6 ] = m[ 9 ];
			out[ 7 ] = m[ 13 ];
			
			out[ 8 ] = m[ 2 ];
			out[ 9 ] = m[ 6 ];
			out[ 10 ] = m[ 10 ];
			out[ 11 ] = m[ 14 ];
			
			out[ 12 ] = m[ 3 ];
			out[ 13 ] = m[ 7 ];
			out[ 14 ] = m[ 11 ];
			out[ 15 ] = m[ 15 ];
		}
		else
		{
			float t = m[ 1 ];
			m[ 1 ] = m[ 4 ];
			m[ 4 ] = t;
			
			t = m[ 2 ];
			m[ 2 ] = m[ 8 ];
			m[ 8 ] = t;
			
			t = m[ 3 ];
			m[ 3 ] = m[ 12 ];
			m[ 12 ] = t;
			
			t = m[ 6 ];
			m[ 6 ] = m[ 9 ];
			m[ 9 ] = t;
			
			t = m[ 7 ];
			m[ 7 ] = m[ 13 ];
			m[ 13 ] = t;
			
			t = m[ 11 ];
			m[ 11 ] = m[ 14 ];
			m[ 14 ] = t;
		}
	}
	
	public static void mcopy( float[ ] msrc , float[ ] mdest )
	{
		System.arraycopy( msrc , 0 , mdest , 0 , 16 );
	}
	
	public static void mcopyAffine( float[ ] msrc , float[ ] mdest )
	{
		System.arraycopy( msrc , 0 , mdest , 0 , 12 );
	}
	
	public static float detAffine( float[ ] m )
	{
		return m[ 0 ] * ( m[ 5 ] * m[ 10 ] - m[ 6 ] * m[ 9 ] ) -
				m[ 1 ] * ( m[ 4 ] * m[ 10 ] - m[ 6 ] * m[ 8 ] ) +
				m[ 2 ] * ( m[ 4 ] * m[ 9 ] - m[ 5 ] * m[ 8 ] );
	}
	
	public static void invAffine( float[ ] m , float[ ] out )
	{
		float determinant = detAffine( m );
		
		if( determinant == 0.0 )
			throw new IllegalArgumentException( "Singular matrix" );
		
		float s = ( m[ 0 ] * m[ 0 ] + m[ 1 ] * m[ 1 ] +
				m[ 2 ] * m[ 2 ] + m[ 3 ] * m[ 3 ] ) *
				( m[ 4 ] * m[ 4 ] + m[ 5 ] * m[ 5 ] +
						m[ 6 ] * m[ 6 ] + m[ 7 ] * m[ 7 ] ) *
				( m[ 8 ] * m[ 8 ] + m[ 9 ] * m[ 9 ] +
						m[ 10 ] * m[ 10 ] + m[ 11 ] * m[ 11 ] );
		
		if( ( determinant * determinant ) < ( FEPS * s ) )
		{
			invertGeneral( m , out );
			return;
		}
		s = 1f / determinant;
		float tmp0 = ( m[ 5 ] * m[ 10 ] - m[ 9 ] * m[ 6 ] ) * s;
		float tmp1 = -( m[ 1 ] * m[ 10 ] - m[ 9 ] * m[ 2 ] ) * s;
		float tmp2 = ( m[ 1 ] * m[ 6 ] - m[ 5 ] * m[ 2 ] ) * s;
		float tmp4 = -( m[ 4 ] * m[ 10 ] - m[ 8 ] * m[ 6 ] ) * s;
		float tmp5 = ( m[ 0 ] * m[ 10 ] - m[ 8 ] * m[ 2 ] ) * s;
		float tmp6 = -( m[ 0 ] * m[ 6 ] - m[ 4 ] * m[ 2 ] ) * s;
		float tmp8 = ( m[ 4 ] * m[ 9 ] - m[ 8 ] * m[ 5 ] ) * s;
		float tmp9 = -( m[ 0 ] * m[ 9 ] - m[ 8 ] * m[ 1 ] ) * s;
		float tmp10 = ( m[ 0 ] * m[ 5 ] - m[ 4 ] * m[ 1 ] ) * s;
		float tmp3 = -( m[ 3 ] * tmp0 + m[ 7 ] * tmp1 + m[ 11 ] * tmp2 );
		float tmp7 = -( m[ 3 ] * tmp4 + m[ 7 ] * tmp5 + m[ 11 ] * tmp6 );
		out[ 11 ] = -( m[ 3 ] * tmp8 + m[ 7 ] * tmp9 + m[ 11 ] * tmp10 );
		
		out[ 0 ] = tmp0;
		out[ 1 ] = tmp1;
		out[ 2 ] = tmp2;
		out[ 3 ] = tmp3;
		out[ 4 ] = tmp4;
		out[ 5 ] = tmp5;
		out[ 6 ] = tmp6;
		out[ 7 ] = tmp7;
		out[ 8 ] = tmp8;
		out[ 9 ] = tmp9;
		out[ 10 ] = tmp10;
		out[ 12 ] = out[ 13 ] = out[ 14 ] = 0f;
		out[ 15 ] = 1f;
	}
	
	/**
	 * General invert routine. Inverts t1 and places the result in "this". Note that this routine handles both the "this" version and the non-"this" version.
	 * 
	 * Also note that since this routine is slow anyway, we won't worry about allocating a little bit of garbage.
	 */
	public static void invertGeneral( float[ ] mat , float[ ] out )
	{
		float tmp[] = new float[ 16 ];
		int row_perm[] = new int[ 4 ];
		
		// Use LU decomposition and backsubstitution code specifically
		// for floating-point 4x4 matrices.
		
		// Copy source matrix to tmp
		System.arraycopy( mat , 0 , tmp , 0 , tmp.length );
		
		// Calculate LU decomposition: Is the matrix singular?
		if( !luDecomposition( tmp , row_perm ) )
		{
			// Matrix has no inverse
			throw new IllegalArgumentException( "Singular Matrix" );
		}
		
		// Perform back substitution on the identity matrix
		// luDecomposition will set rot[] & scales[] for use
		// in luBacksubstituation
		out[ 0 ] = 1f;
		out[ 1 ] = 0f;
		out[ 2 ] = 0f;
		out[ 3 ] = 0f;
		out[ 4 ] = 0f;
		out[ 5 ] = 1f;
		out[ 6 ] = 0f;
		out[ 7 ] = 0f;
		out[ 8 ] = 0f;
		out[ 9 ] = 0f;
		out[ 10 ] = 1f;
		out[ 11 ] = 0f;
		out[ 12 ] = 0f;
		out[ 13 ] = 0f;
		out[ 14 ] = 0f;
		out[ 15 ] = 1f;
		luBacksubstitution( tmp , row_perm , out );
	}
	
	/**
	 * Given a 4x4 array "matrix0", this function replaces it with the LU decomposition of a row-wise permutation of itself. The input parameters are "matrix0"
	 * and "dimen". The array "matrix0" is also an output parameter. The vector "row_perm[4]" is an output parameter that contains the row permutations
	 * resulting from partial pivoting. The output parameter "even_row_xchg" is 1 when the number of row exchanges is even, or -1 otherwise. Assumes data type
	 * is always float.
	 * 
	 * This function is similar to luDecomposition, except that it is tuned specifically for 4x4 matrices.
	 * 
	 * @return true if the matrix is nonsingular, or false otherwise.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 40-45.
	//
	static boolean luDecomposition( float[ ] matrix0 ,
			int[ ] row_perm )
	{
		
		// Can't re-use this temporary since the method is static.
		float row_scale[] = new float[ 4 ];
		
		// Determine implicit scaling information by looping over rows
		{
			int i, j;
			int ptr, rs;
			float big, temp;
			
			ptr = 0;
			rs = 0;
			
			// For each row ...
			i = 4;
			while( i-- != 0 )
			{
				big = 0f;
				
				// For each column, find the largest element in the row
				j = 4;
				while( j-- != 0 )
				{
					temp = matrix0[ ptr++ ];
					temp = Math.abs( temp );
					if( temp > big )
					{
						big = temp;
					}
				}
				
				// Is the matrix singular?
				if( big == 0f )
				{
					return false;
				}
				row_scale[ rs++ ] = 1f / big;
			}
		}
		
		{
			int j;
			int mtx;
			
			mtx = 0;
			
			// For all columns, execute Crout's method
			for( j = 0 ; j < 4 ; j++ )
			{
				int i, imax, k;
				int target, p1, p2;
				float sum, big, temp;
				
				// Determine elements of upper diagonal matrix U
				for( i = 0 ; i < j ; i++ )
				{
					target = mtx + ( 4 * i ) + j;
					sum = matrix0[ target ];
					k = i;
					p1 = mtx + ( 4 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 4;
					}
					matrix0[ target ] = sum;
				}
				
				// Search for largest pivot element and calculate
				// intermediate elements of lower diagonal matrix L.
				big = 0f;
				imax = -1;
				for( i = j ; i < 4 ; i++ )
				{
					target = mtx + ( 4 * i ) + j;
					sum = matrix0[ target ];
					k = j;
					p1 = mtx + ( 4 * i );
					p2 = mtx + j;
					while( k-- != 0 )
					{
						sum -= matrix0[ p1 ] * matrix0[ p2 ];
						p1++ ;
						p2 += 4;
					}
					matrix0[ target ] = sum;
					
					// Is this the best pivot so far?
					if( ( temp = row_scale[ i ] * Math.abs( sum ) ) >= big )
					{
						big = temp;
						imax = i;
					}
				}
				
				if( imax < 0 )
				{
					return false;
				}
				
				// Is a row exchange necessary?
				if( j != imax )
				{
					// Yes: exchange rows
					k = 4;
					p1 = mtx + ( 4 * imax );
					p2 = mtx + ( 4 * j );
					while( k-- != 0 )
					{
						temp = matrix0[ p1 ];
						matrix0[ p1++ ] = matrix0[ p2 ];
						matrix0[ p2++ ] = temp;
					}
					
					// Record change in scale factor
					row_scale[ imax ] = row_scale[ j ];
				}
				
				// Record row permutation
				row_perm[ j ] = imax;
				
				// Is the matrix singular
				if( matrix0[ ( mtx + ( 4 * j ) + j ) ] == 0f )
				{
					return false;
				}
				
				// Divide elements of lower diagonal matrix L by pivot
				if( j != ( 4 - 1 ) )
				{
					temp = 1f / ( matrix0[ ( mtx + ( 4 * j ) + j ) ] );
					target = mtx + ( 4 * ( j + 1 ) ) + j;
					i = 3 - j;
					while( i-- != 0 )
					{
						matrix0[ target ] *= temp;
						target += 4;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Solves a set of linear equations. The input parameters "matrix1", and "row_perm" come from luDecompostionD4x4 and do not change here. The parameter
	 * "matrix2" is a set of column vectors assembled into a 4x4 matrix of floating-point values. The procedure takes each column of "matrix2" in turn and
	 * treats it as the right-hand side of the matrix equation Ax = LUx = b. The solution vector replaces the original column of the matrix.
	 * 
	 * If "matrix2" is the identity matrix, the procedure replaces its contents with the inverse of the matrix from which "matrix1" was originally derived.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 44-45.
	//
	static void luBacksubstitution( float[ ] matrix1 ,
			int[ ] row_perm ,
			float[ ] matrix2 )
	{
		
		int i, ii, ip, j, k;
		int rp;
		int cv, rv;
		
		// rp = row_perm;
		rp = 0;
		
		// For each column vector of matrix2 ...
		for( k = 0 ; k < 4 ; k++ )
		{
			// cv = &(matrix2[0][k]);
			cv = k;
			ii = -1;
			
			// Forward substitution
			for( i = 0 ; i < 4 ; i++ )
			{
				float sum;
				
				ip = row_perm[ rp + i ];
				sum = matrix2[ cv + 4 * ip ];
				matrix2[ cv + 4 * ip ] = matrix2[ cv + 4 * i ];
				if( ii >= 0 )
				{
					// rv = &(matrix1[i][0]);
					rv = i * 4;
					for( j = ii ; j <= i - 1 ; j++ )
					{
						sum -= matrix1[ rv + j ] * matrix2[ cv + 4 * j ];
					}
				}
				else if( sum != 0f )
				{
					ii = i;
				}
				matrix2[ cv + 4 * i ] = sum;
			}
			
			// Backsubstitution
			// rv = &(matrix1[3][0]);
			rv = 3 * 4;
			matrix2[ cv + 4 * 3 ] /= matrix1[ rv + 3 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 2 ] = ( matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 2 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 1 ] = ( matrix2[ cv + 4 * 1 ] -
					matrix1[ rv + 2 ] * matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 1 ];
			
			rv -= 4;
			matrix2[ cv + 4 * 0 ] = ( matrix2[ cv + 4 * 0 ] -
					matrix1[ rv + 1 ] * matrix2[ cv + 4 * 1 ] -
					matrix1[ rv + 2 ] * matrix2[ cv + 4 * 2 ] -
					matrix1[ rv + 3 ] * matrix2[ cv + 4 * 3 ] ) / matrix1[ rv + 0 ];
		}
	}
}