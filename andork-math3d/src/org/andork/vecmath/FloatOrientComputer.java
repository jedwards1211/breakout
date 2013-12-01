package org.andork.vecmath;

import static org.andork.vecmath.Vecmath.cross;
import static org.andork.vecmath.Vecmath.invertGeneral;
import static org.andork.vecmath.Vecmath.mmul;
import static org.andork.vecmath.Vecmath.newMat4f;
import static org.andork.vecmath.Vecmath.normalize3;
import static org.andork.vecmath.Vecmath.setColumn3;
import static org.andork.vecmath.Vecmath.setRow4;
import static org.andork.vecmath.Vecmath.setf;

/**
 * Provides temporary variables and methods for computing various transforms:
 * <ul>
 * <li>Shear</li>
 * <li>Orient</li>
 * <li>Local-to-local</li>
 * </ul>
 * TransformComputer3f is not synchronized!
 */
public class FloatOrientComputer
{
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					m		= newMat4f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					x1		= newMat4f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					x2		= newMat4f( );
	
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					p1		= new float[ 3 ];
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					p2		= new float[ 3 ];
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					v1		= new float[ 3 ];
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					v2		= new float[ 3 ];
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					v3		= new float[ 3 ];
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					v4		= new float[ 3 ];
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					v5		= new float[ 3 ];
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					v6		= new float[ 3 ];
	
	private static final float[ ]	ZEROF	= new float[ 3 ];
	
	/**
	 * Computes a shear from the public instance variables.
	 * 
	 * @return shear( p1 , v1 , v2 , v3 , p2 , v4 , v5 , v6 , result ).
	 * 
	 * @see #shear(float[], float[], float[], float[], float[], float[], float[], float[], float[])
	 */
	public float[ ] shear( float[ ] result )
	{
		return shear( p1 , v1 , v2 , v3 , p2 , v4 , v5 , v6 , result );
	}
	
	/**
	 * Transform from the origin and unit x, y, and z axes to a new origin and x, y, and z axes. If newX, newY, and newZ are not perpendicular, shearing will
	 * result. If newX, newY, and newZ are not unitary, scaling will result.
	 * 
	 * @param newOrigin
	 *            the new origin point.
	 * @param newX
	 *            the new x axis.
	 * @param newY
	 *            the new y axis.
	 * @param newZ
	 *            the new z axis.
	 * @param result
	 *            the float[] to set such that (ignoring floating-point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( new float[]{ 0, 0, 0 } )</code> equals <code>newOrigin</code>,</li>
	 *            <li> <code>result.transform( new float[]{ 1, 0, 0 } )</code> equals <code>newX</code>,</li>
	 *            <li> <code>result.transform( new float[]{ 0, 1, 0 } )</code> equals <code>newY</code>, and</li>
	 *            <li> <code>result.transform( new float[]{ 0, 0, 1 } )</code> equals <code>newZ</code>.</li>
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>newX</code>, <code>newY</code>, or <code>newZ</code> is zero
	 */
	public float[ ] shear( float[ ] newOrigin , float[ ] newX , float[ ] newY , float[ ] newZ , float[ ] result )
	{
		if( newX.equals( ZEROF ) || newY.equals( ZEROF ) || newZ.equals( ZEROF ) )
		{
			throw new IllegalArgumentException( "newX, newY, and newZ must be nonzero" );
		}
		
		setColumn3( result , 0 , newX );
		setColumn3( result , 1 , newY );
		setColumn3( result , 2 , newZ );
		setColumn3( result , 3 , newOrigin );
		setRow4( result , 3 , 0 , 0 , 0 , 1 );
		
		return result;
	}
	
	/**
	 * Shears from an old origin and x, y, and z axes to a new origin and x, y, and z axes.
	 * 
	 * @param oldOrigin
	 *            the old origin point.
	 * @param oldX
	 *            the old x axis.
	 * @param oldY
	 *            the old y axis.
	 * @param oldZ
	 *            the old z axis.
	 * @param newOrigin
	 *            the new origin point.
	 * @param newX
	 *            the new x axis.
	 * @param newY
	 *            the new y axis.
	 * @param newZ
	 *            the new z axis.
	 * @param result
	 *            the float[] to set such that (ignoring floating-point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( oldOrigin )</code> equals <code>newOrigin</code>,</li>
	 *            <li> <code>result.transform( oldX )</code> equals <code>newX</code>,</li>
	 *            <li> <code>result.transform( oldY )</code> equals <code>newY</code>, and</li>
	 *            <li> <code>result.transform( oldZ )</code> equals <code>newZ</code>.</li>
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>newX</code>, <code>newY</code>, or <code>newZ</code> is zero
	 */
	public float[ ] shear( float[ ] oldOrigin , float[ ] oldX , float[ ] oldY , float[ ] oldZ , float[ ] newOrigin , float[ ] newX , float[ ] newY , float[ ] newZ , float[ ] result )
	{
		if( oldX.equals( ZEROF ) || oldY.equals( ZEROF ) || oldZ.equals( ZEROF ) || newX.equals( ZEROF ) || newY.equals( ZEROF ) || newZ.equals( ZEROF ) )
		{
			throw new IllegalArgumentException( "oldX, oldY, oldZ, newX, newY, and newZ must be nonzero" );
		}
		
		shear( newOrigin , newX , newY , newZ , result );
		shear( oldOrigin , oldX , oldY , oldZ , x1 );
		invertGeneral( x1 );
		mmul( result , x1 , result );
		
		return result;
	}
	
	/**
	 * Creates a transform that orients an object from one coordinate reference frame to another.<br>
	 * <br>
	 * 
	 * @param oldAxis
	 *            the x axis of the old reference frame (whatever direction you want; it doesn't have to be (1, 0, 0))
	 * @param newX
	 *            the x axis of the new reference frame.
	 * @param result
	 *            the float[] to set such that (ignoring floating point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( oldX )</code> will equal <code>newX</code></li>
	 *            <li> <code>result.transform( n )</code> will equal <code>n</code></li> for any vector <code>n</code> perpendicular to <code>oldX</code> and
	 *            <code>newX</code>.
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>oldX</code> or <code>newX</code> is zero.
	 * 
	 * @see #orient(float[], float[], float[], float[], float[], float[], float[])
	 */
	public float[ ] orientInPlace( float oldAxisX , float oldAxisY , float oldAxisZ , float[ ] newAxis , float[ ] result )
	{
		setf( v1 , oldAxisX , oldAxisY , oldAxisZ );
		return orient( ZEROF , v1 , ZEROF , newAxis , result );
	}
	
	/**
	 * Creates a transform that orients an object from one coordinate reference frame to another.<br>
	 * <br>
	 * 
	 * @param oldAxis
	 *            the x axis of the old reference frame (whatever direction you want; it doesn't have to be (1, 0, 0))
	 * @param newX
	 *            the x axis of the new reference frame.
	 * @param result
	 *            the float[] to set such that (ignoring floating point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( oldX )</code> will equal <code>newX</code></li>
	 *            <li> <code>result.transform( n )</code> will equal <code>n</code></li> for any vector <code>n</code> perpendicular to <code>oldX</code> and
	 *            <code>newX</code>.
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>oldX</code> or <code>newX</code> is zero.
	 * 
	 * @see #orient(float[], float[], float[], float[], float[], float[], float[])
	 */
	public float[ ] orientInPlace( float[ ] oldAxis , float newAxisX , float newAxisY , float newAxisZ , float[ ] result )
	{
		setf( v4 , newAxisX , newAxisY , newAxisZ );
		return orient( ZEROF , oldAxis , ZEROF , v4 , result );
	}
	
	/**
	 * Creates a transform that orients an object from one coordinate reference frame to another.<br>
	 * <br>
	 * 
	 * @param oldX
	 *            the x axis of the old reference frame (whatever direction you want; it doesn't have to be (1, 0, 0))
	 * @param newX
	 *            the x axis of the new reference frame.
	 * @param result
	 *            the float[] to set such that (ignoring floating point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( oldX )</code> will equal <code>newX</code></li>
	 *            <li> <code>result.transform( n )</code> will equal <code>n</code></li> for any vector <code>n</code> perpendicular to <code>oldX</code> and
	 *            <code>newX</code>.
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>oldX</code> or <code>newX</code> is zero.
	 * 
	 * @see #orient(float[], float[], float[], float[], float[], float[], float[])
	 */
	public float[ ] orientInPlace( float[ ] oldX , float[ ] newX , float[ ] result )
	{
		return orient( ZEROF , oldX , ZEROF , newX , result );
	}
	
	/**
	 * Creates a transform that orients an object from one coordinate reference frame to another.<br>
	 * <br>
	 * 
	 * @param oldOrigin
	 *            the origin of the old reference frame.
	 * @param oldX
	 *            the x axis of the old reference frame (whatever direction you want; it doesn't have to be (1, 0, 0))
	 * @param newOrigin
	 *            the origin of the new reference frame.
	 * @param newX
	 *            the x axis of the new reference frame.
	 * @param result
	 *            the float[] to set such that (ignoring floating point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( oldOrigin )</code> will equal <code>newOrigin</code></li>
	 *            <li> <code>result.transform( oldX )</code> will equal <code>newX</code></li>
	 *            <li> <code>result.transform( n )</code> will equal <code>n</code></li> for any vector <code>n</code> perpendicular to <code>oldX</code> and
	 *            <code>newX</code>.
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>oldX</code> or <code>newX</code> is zero.
	 * 
	 * @see #orient(float[], float[], float[], float[], float[], float[], float[])
	 */
	public float[ ] orient( float[ ] oldOrigin , float[ ] oldX , float[ ] newOrigin , float[ ] newX , float[ ] result )
	{
		normalize3( oldX , v1 );
		normalize3( newX , v4 );
		
		cross( v1 , v4 , v2 );
		setf( v5 , v2 );
		
		if( v2[ 0 ] == 0 && v2[ 1 ] == 0 && v2[ 2 ] == 0 )
		{
			setRow4( result , 0 , 1 , 0 , 0 , newOrigin[ 0 ] - oldOrigin[ 0 ] );
			setRow4( result , 1 , 0 , 1 , 0 , newOrigin[ 1 ] - oldOrigin[ 1 ] );
			setRow4( result , 2 , 0 , 0 , 1 , newOrigin[ 2 ] - oldOrigin[ 2 ] );
			setRow4( result , 3 , 0 , 0 , 0 , 1 );
			return result;
		}
		else
		{
			cross( v1 , v2 , v3 );
			cross( v4 , v5 , v6 );
			return shear( oldOrigin , v1 , v2 , v3 , newOrigin , v4 , v5 , v6 , result );
		}
	}
	
	/**
	 * Creates a transform that orients and object from one coordinate reference frame to another without translation.
	 * 
	 * @see #orient(float[], float[], float[], float[], float[], float[], float[])
	 * @param oldX
	 *            the x axis of the old reference frame (whatever direction you want; it doesn't have to be (1, 0, 0))
	 * @param oldY
	 *            the y axis of the old reference frame (if not perpendicular to <code>oldX</code>, it will be replaced with a vector perpendicular to
	 *            <code>oldX</code> at the same angle around <code>oldX</code>).
	 * @param newX
	 *            the x axis of the new reference frame.
	 * @param newY
	 *            the y axis of the new reference frame (if not perpendicular to <code>newX</code>, it will be replaced with a vector perpendicular to
	 *            <code>newX</code> at the same angle around <code>newX</code>).
	 * @param result
	 *            the float[] to set such that (ignoring floating point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( oldX )</code> will equal <code>newX</code></li>
	 *            <li> <code>result.transform( oldY )</code> will equal <code>newY</code></li>
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if:
	 *             <ul>
	 *             <li> <code>oldX, oldY, newX,</code> or <code>newY</code> is zero,</li>
	 *             <li> <code>oldX</code> and <code>oldY</code> are parallel, or</li>
	 *             <li> <code>newX</code> and <code>newY</code> are parallel.</li>
	 *             </ul>
	 */
	public float[ ] orientInPlace( float[ ] oldX , float[ ] oldY , float[ ] newX , float[ ] newY , float[ ] result )
	{
		return orient( ZEROF , oldX , oldY , ZEROF , newX , newY , result );
	}
	
	/**
	 * Creates a transform that orients an object from one coordinate reference frame to another.<br>
	 * <br>
	 * 
	 * For example, let's say you want to pin a poster on a wall. you've unrolled the poster on the ground facing up and the top edge of the poster is facing
	 * north. The wall you want to put it on is facing east. The only problem is you have to use a mathematical transform to put it on the wall! What should the
	 * transform do? If you transform the center of the poster, it should move from the ground to the wall. If you transform the direction the poster is facing
	 * (up), it should turn east. If you transform the direction of the top edge of the poster, it should turn up. This method creates such a transform. In this
	 * example:
	 * <ul>
	 * <li><code>oldOrigin</code> is the center of the poster on the ground</li>
	 * <li><code>oldX</code> is the direction the poster is facing (up)</li>
	 * <li><code>oldY</code> is the direction the top edge of the poster is facing (north)</li>
	 * <li><code>newOrigin</code> is the point on the wall where you want the poster to be centered</li>
	 * <li><code>newX</code> is the direction the wall is facing (east)</li>
	 * <li><code>newY</code> is the direction you want the top edge of the poster to be facing when you put it on the wall (up)</li>
	 * </ul>
	 * 
	 * In other words, if you create an orient transform and apply it to an object, the part of the object at <code>oldOrigin</code> will now be at
	 * <code>newOrigin</code>, the part of the object facing in the <code>oldX</code> direction will now face in the <code>newX</code> direction, and the part
	 * of the object facing in the <code>oldY</code> direction will now face in the <code>newY</code> direction.
	 * 
	 * @param oldOrigin
	 *            the origin of the old reference frame.
	 * @param oldX
	 *            the x axis of the old reference frame (whatever direction you want; it doesn't have to be (1, 0, 0))
	 * @param oldY
	 *            the y axis of the old reference frame (if not perpendicular to <code>oldX</code>, it will be replaced with a vector perpendicular to
	 *            <code>oldX</code> at the same angle around <code>oldX</code>).
	 * @param newOrigin
	 *            the origin of the new reference frame.
	 * @param newX
	 *            the x axis of the new reference frame.
	 * @param newY
	 *            the y axis of the new reference frame (if not perpendicular to <code>newX</code>, it will be replaced with a vector perpendicular to
	 *            <code>newX</code> at the same angle around <code>newX</code>).
	 * @param result
	 *            the float[] to set such that (ignoring floating point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( oldOrigin )</code> will equal <code>newOrigin</code></li>
	 *            <li> <code>result.transform( oldX )</code> will equal <code>newX</code></li>
	 *            <li> <code>result.transform( oldY )</code> will equal <code>newY</code></li>
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if:
	 *             <ul>
	 *             <li> <code>oldX, oldY, newX,</code> or <code>newY</code> is zero,</li>
	 *             <li> <code>oldX</code> and <code>oldY</code> are parallel, or</li>
	 *             <li> <code>newX</code> and <code>newY</code> are parallel.</li>
	 *             </ul>
	 */
	public float[ ] orient( float[ ] oldOrigin , float[ ] oldX , float[ ] oldY , float[ ] newOrigin , float[ ] newX , float[ ] newY , float[ ] result )
	{
		if( oldX.equals( ZEROF ) || oldY.equals( ZEROF ) || newX.equals( ZEROF ) || newY.equals( ZEROF ) )
		{
			throw new IllegalArgumentException( "oldX, oldY, newX, and newY must be nonzero" );
		}
		
		normalize3( oldX , v1 );
		cross( oldX , oldY , v3 );
		if( v3[ 0 ] == 0 && v3[ 1 ] == 0 && v3[ 2 ] == 0 )
		{
			throw new IllegalArgumentException( "oldX and oldY must not be parallel" );
		}
		normalize3( v3 );
		cross( v3 , v1 , v2 );
		normalize3( newX , v4 );
		cross( newX , newY , v6 );
		
		if( v6[ 0 ] == 0 && v6[ 1 ] == 0 && v6[ 2 ] == 0 )
		{
			throw new IllegalArgumentException( "newX and newY must not be parallel" );
		}
		
		normalize3( v6 );
		cross( v6 , v4 , v5 );
		return shear( oldOrigin , v1 , v2 , v3 , newOrigin , v4 , v5 , v6 , result );
	}
	
	/**
	 * Creates a transform that orients an object from one coordinate reference frame to another.<br>
	 * <br>
	 * 
	 * For example, let's say you want to pin a poster on a wall. you've unrolled the poster on the ground facing up and the top edge of the poster is facing
	 * north. The wall you want to put it on is facing east. The only problem is you have to use a mathematical transform to put it on the wall! What should the
	 * transform do? If you transform the center of the poster, it should move from the ground to the wall. If you transform the direction the poster is facing
	 * (up), it should turn east. If you transform the direction of the top edge of the poster, it should turn up. This method creates such a transform. In this
	 * example:
	 * <ul>
	 * <li><code>oldOrigin</code> is the center of the poster on the ground</li>
	 * <li><code>oldX</code> is the direction the poster is facing (up)</li>
	 * <li><code>oldY</code> is the direction the top edge of the poster is facing (north)</li>
	 * <li><code>newOrigin</code> is the point on the wall where you want the poster to be centered</li>
	 * <li><code>newX</code> is the direction the wall is facing (east)</li>
	 * <li><code>newY</code> is the direction you want the top edge of the poster to be facing when you put it on the wall (up)</li>
	 * </ul>
	 * 
	 * In other words, if you create an orient transform and apply it to an object, the part of the object at <code>oldOrigin</code> will now be at
	 * <code>newOrigin</code>, the part of the object facing in the <code>oldX</code> direction will now face in the <code>newX</code> direction, and the part
	 * of the object facing in the <code>oldY</code> direction will now face in the <code>newY</code> direction.
	 * 
	 * @param oldOrigin
	 *            the origin of the old reference frame.
	 * @param oldX
	 *            the x axis of the old reference frame (whatever direction you want; it doesn't have to be (1, 0, 0))
	 * @param oldY
	 *            the y axis of the old reference frame (if not perpendicular to <code>oldX</code>, it will be replaced with a vector perpendicular to
	 *            <code>oldX</code> at the same angle around <code>oldX</code>).
	 * @param newOrigin
	 *            the origin of the new reference frame.
	 * @param newX
	 *            the x axis of the new reference frame.
	 * @param newY
	 *            the y axis of the new reference frame (if not perpendicular to <code>newX</code>, it will be replaced with a vector perpendicular to
	 *            <code>newX</code> at the same angle around <code>newX</code>).
	 * @param result
	 *            the float[] to set such that (ignoring floating point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( oldOrigin )</code> will equal <code>newOrigin</code></li>
	 *            <li> <code>result.transform( oldX )</code> will equal <code>newX</code></li>
	 *            <li> <code>result.transform( oldY )</code> will equal <code>newY</code></li>
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if:
	 *             <ul>
	 *             <li> <code>oldX, oldY, newX,</code> or <code>newY</code> is zero,</li>
	 *             <li> <code>oldX</code> and <code>oldY</code> are parallel, or</li>
	 *             <li> <code>newX</code> and <code>newY</code> are parallel.</li>
	 *             </ul>
	 */
	public float[ ] orient( float oldOriginX , float oldOriginY , float oldOriginZ ,
			float oldXx , float oldXy , float oldXz ,
			float oldYx , float oldYy , float oldYz ,
			float[ ] newOrigin , float[ ] newX , float[ ] newY , float[ ] result )
	{
		if( oldXx == 0 && oldXy == 0 && oldXz == 0 )
		{
			throw new IllegalArgumentException( "oldX must be non-zero" );
		}
		if( oldYx == 0 && oldYy == 0 && oldYz == 0 )
		{
			throw new IllegalArgumentException( "oldY must be non-zero" );
		}
		if( newX[ 0 ] == 0 && newX[ 1 ] == 0 && newX[ 2 ] == 0 )
		{
			throw new IllegalArgumentException( "newX must be non-zero" );
		}
		if( newY[ 0 ] == 0 && newY[ 1 ] == 0 && newY[ 2 ] == 0 )
		{
			throw new IllegalArgumentException( "newY must be non-zero" );
		}
		
		normalize3( oldXx , oldXy , oldXz , v1 );
		cross( oldXx , oldXy , oldXz , oldYx , oldYy , oldYz , v3 );
		if( v3[ 0 ] == 0 && v3[ 1 ] == 0 && v3[ 2 ] == 0 )
		{
			throw new IllegalArgumentException( "oldX and oldY must not be parallel" );
		}
		normalize3( v3 );
		cross( v3 , v1 , v2 );
		normalize3( newX , v4 );
		cross( newX , newY , v6 );
		
		if( v6[ 0 ] == 0 && v6[ 1 ] == 0 && v6[ 2 ] == 0 )
		{
			throw new IllegalArgumentException( "newX and newY must not be parallel" );
		}
		
		normalize3( v6 );
		cross( v6 , v4 , v5 );
		
		setf( p1 , oldOriginX , oldOriginY , oldOriginZ );
		return shear( p1 , v1 , v2 , v3 , newOrigin , v4 , v5 , v6 , result );
	}
}
