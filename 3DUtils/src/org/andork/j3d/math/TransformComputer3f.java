
package org.andork.j3d.math;

import java.util.Arrays;

import javax.media.j3d.BadTransformException;
import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Provides temporary variables and methods for computing various transforms:
 * <ul>
 * <li>Shear</li>
 * <li>Orient</li>
 * <li>Local-to-local</li>
 * </ul>
 * TransformComputer3f is not synchronized!
 */
public class TransformComputer3f
{
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public float[ ]					m		= new float[ 16 ];
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Transform3D				x1		= new Transform3D( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Transform3D				x2		= new Transform3D( );
	
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Point3f					p1		= new Point3f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Point3f					p2		= new Point3f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Vector3f					v1		= new Vector3f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Vector3f					v2		= new Vector3f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Vector3f					v3		= new Vector3f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Vector3f					v4		= new Vector3f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Vector3f					v5		= new Vector3f( );
	/**
	 * This is used as a temporary in instance methods, but it is public so that you can use it instead of wasting memory by allocating more temporaries.
	 */
	public Vector3f					v6		= new Vector3f( );
	
	private static final Point3f	ZEROF	= new Point3f( );
	
	/**
	 * Computes a shear from the public instance variables.
	 * 
	 * @return shear( p1 , v1 , v2 , v3 , p2 , v4 , v5 , v6 , result ).
	 * 
	 * @see #shear(Point3d, Vector3d, Vector3d, Vector3d, Point3d, Vector3d, Vector3d, Vector3d, Transform3D)
	 */
	public Transform3D shear( Transform3D result )
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
	 *            the Transform3D to set such that (ignoring floating-point inaccuracy):
	 *            <ul>
	 *            <li> <code>result.transform( new Point3d( 0, 0, 0 ) )</code> equals <code>newOrigin</code>,</li>
	 *            <li> <code>result.transform( new Vector3d( 1, 0, 0 ) )</code> equals <code>newX</code>,</li>
	 *            <li> <code>result.transform( new Vector3d( 0, 1, 0 ) )</code> equals <code>newY</code>, and</li>
	 *            <li> <code>result.transform( new Vector3d( 0, 0, 1 ) )</code> equals <code>newZ</code>.</li>
	 *            </ul>
	 * @return <code>result</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>newX</code>, <code>newY</code>, or <code>newZ</code> is zero
	 */
	public Transform3D shear( Point3f newOrigin , Vector3f newX , Vector3f newY , Vector3f newZ , Transform3D result )
	{
		if( newX.equals( ZEROF ) || newY.equals( ZEROF ) || newZ.equals( ZEROF ) )
		{
			throw new IllegalArgumentException( "newX, newY, and newZ must be nonzero" );
		}
		
		m[ 0 ] = newX.x;
		m[ 1 ] = newY.x;
		m[ 2 ] = newZ.x;
		m[ 3 ] = newOrigin.x;
		m[ 4 ] = newX.y;
		m[ 5 ] = newY.y;
		m[ 6 ] = newZ.y;
		m[ 7 ] = newOrigin.y;
		m[ 8 ] = newX.z;
		m[ 9 ] = newY.z;
		m[ 10 ] = newZ.z;
		m[ 11 ] = newOrigin.z;
		m[ 12 ] = 0;
		m[ 13 ] = 0;
		m[ 14 ] = 0;
		m[ 15 ] = 1;
		result.set( m );
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
	 *            the Transform3D to set such that (ignoring floating-point inaccuracy):
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
	public Transform3D shear( Point3f oldOrigin , Vector3f oldX , Vector3f oldY , Vector3f oldZ , Point3f newOrigin , Vector3f newX , Vector3f newY , Vector3f newZ , Transform3D result )
	{
		if( oldX.equals( ZEROF ) || oldY.equals( ZEROF ) || oldZ.equals( ZEROF ) || newX.equals( ZEROF ) || newY.equals( ZEROF ) || newZ.equals( ZEROF ) )
		{
			throw new IllegalArgumentException( "oldX, oldY, oldZ, newX, newY, and newZ must be nonzero" );
		}
		
		shear( newOrigin , newX , newY , newZ , result );
		shear( oldOrigin , oldX , oldY , oldZ , x1 );
		x1.invert( );
		result.mul( x1 );
		return result;
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
	 *            the Transform3D to set such that (ignoring floating point inaccuracy):
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
	 * @see #orient(Point3f, Vector3f, Vector3f, Point3f, Vector3f, Vector3f, Transform3D)
	 */
	public Transform3D orient( Vector3f oldX , Vector3f newX , Transform3D result )
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
	 *            the Transform3D to set such that (ignoring floating point inaccuracy):
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
	 * @see #orient(Point3f, Vector3f, Vector3f, Point3f, Vector3f, Vector3f, Transform3D)
	 */
	public Transform3D orient( Point3f oldOrigin , Vector3f oldX , Point3f newOrigin , Vector3f newX , Transform3D result )
	{
		v1.normalize( oldX );
		v4.normalize( newX );
		// pick y normal to place of rotation for oldY and newY
		v2.cross( v1 , v4 );
		v5.set( v2 );
		if( v2.equals( ZEROF ) )
		{
			// no rotation necessary; just translate
			Arrays.fill( m , 0 );
			m[ 0 ] = 1;
			m[ 3 ] = newOrigin.x - oldOrigin.x;
			m[ 5 ] = 1;
			m[ 7 ] = newOrigin.y - oldOrigin.y;
			m[ 10 ] = 1;
			m[ 11 ] = newOrigin.z - oldOrigin.z;
			m[ 15 ] = 1;
			result.set( m );
		}
		else
		{
			// compute oldZ and newZ
			v3.cross( v1 , v2 );
			v6.cross( v4 , v5 );
			shear( oldOrigin , v1 , v2 , v3 , newOrigin , v4 , v5 , v6 , result );
		}
		return result;
	}
	
	/**
	 * Creates a transform that orients and object from one coordinate reference frame to another without translation.
	 * 
	 * @see #orient(Point3f, Vector3f, Vector3f, Point3f, Vector3f, Vector3f, Transform3D)
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
	 *            the Transform3D to set such that (ignoring floating point inaccuracy):
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
	public Transform3D orient( Vector3f oldX , Vector3f oldY , Vector3f newX , Vector3f newY , Transform3D result )
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
	 *            the Transform3D to set such that (ignoring floating point inaccuracy):
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
	public Transform3D orient( Point3f oldOrigin , Vector3f oldX , Vector3f oldY , Point3f newOrigin , Vector3f newX , Vector3f newY , Transform3D result )
	{
		if( oldX.equals( ZEROF ) || oldY.equals( ZEROF ) || newX.equals( ZEROF ) || newY.equals( ZEROF ) )
		{
			throw new IllegalArgumentException( "oldX, oldY, newX, and newY must be nonzero" );
		}
		
		v1.normalize( oldX ); // corrected oldX
		v3.cross( oldX , oldY ); // oldZ
		if( v3.equals( ZEROF ) )
		{
			throw new IllegalArgumentException( "oldX and oldY must not be parallel" );
		}
		v3.normalize( );
		v2.cross( v3 , v1 ); // corrected oldY
		v4.normalize( newX ); // corrected newX
		v6.cross( newX , newY ); // newZ
		if( v6.equals( ZEROF ) )
		{
			throw new IllegalArgumentException( "newX and newY must not be parallel" );
		}
		v6.normalize( );
		v5.cross( v6 , v4 ); // corrected newY
		
		shear( oldOrigin , v1 , v2 , v3 , newOrigin , v4 , v5 , v6 , result );
		return result;
	}
	
	/**
	 * Computes a transform that will transform coordinates from one {@link Node}'s local coordinate system to another {@link Node}'s local coordinate system.<br>
	 * <br>
	 * 
	 * For example, imagine the scene has two cones such that the tip of coneA is at (1, 0, 0) in A's local coordinate system, and the tip of coneB is at (2, 0,
	 * 0) in B's local coordinate system. Imagine these cones are transformed so that their tips are touching in the 3D view. Then if we
	 * <code>localToLocal( coneA, conB, result )</code>, then <code>result.transform( new Point3d( 1, 0, 0 ) )</code> equals (ignoring floating-point
	 * inaccuracy) <code>(2, 0, 0)</code>.
	 * 
	 * @param source
	 *            the {@link Node} in the local coordinate system of points to input to the transform.
	 * @param dest
	 *            the {@link Node} in the local coordinate system to output from the transform
	 * @param result
	 *            the Transform3D to set.
	 * @return <code>result</code>
	 */
	public Transform3D localToLocal( Node source , Node dest , Transform3D result )
	{
		source.getLocalToVworld( result );
		dest.getLocalToVworld( x1 );
		x1.invert( );
		result.mul( x1 );
		return result;
	}
	
	/**
	 * Extracts the pan, tilt, and roll components of a transform. More specifically, it calculates the pan, tilt, and roll angles that transform the identity
	 * orientation to the same orientation that the given transform does. <br>
	 * <br>
	 * Pan, tilt, and roll are defined as rotations about the Z, Y, and X axes respectively, and they are defined to occur in the order <b>roll first, then
	 * tilt, then pan</b>. <br>
	 * Thus the transform <code>rotZ(pan)*rotY(tilt)*rotX(roll)</code> rotates to the same orientation as the given transform does. <br>
	 * <br>
	 * If the tilt is +/- 90 degrees (i.e. tilting forward to point straight +Z or -Z), pan and roll become gimbal locked. In this case, pan is used to perform
	 * the "roll" around the Z axis instead of actual roll, because we are usually operating on a camera with zero roll, and we want it to tilt away from
	 * vertical to the same pan it began with. However, this means that if you tilt a camera with nonzero roll up 90 degrees, its roll will turn into pan, and
	 * if you tilt it back down using pan/tilt/roll values obtained from this method it will tilt down to a different pan.
	 * 
	 * @param xform
	 *            the transform to analyze. It must be congruent and right-handed - that is it must not scale anisotropically and it must have a positive
	 *            determinant.
	 * @param result
	 *            the (roll, tilt, pan) angle result vector
	 * @return <code>result</code>
	 */
	public Vector3f getRollTiltPan( Transform3D xform , Vector3f result )
	{
		if( xform.getBestType( ) == Transform3D.AFFINE )
		{
			throw new BadTransformException( "xform must be congruent" );
		}
		if( !xform.getDeterminantSign( ) )
		{
			throw new BadTransformException( "xform must have a positive determinant" );
		}
		
		x1.set( xform );
		v1.set( 0 , 0 , 0 );
		x1.setTranslation( v1 );
		
		// calculate pan:
		// F' = Qzyx * F
		// pan = atan2(F'.y, F'.x)
		// UNLESS F' points straight +Z or -Z:
		// R' = Qzyx * R
		// pan = atan2(R'.y, R'.x) - PI / 2
		v1.set( 1 , 0 , 0 );
		x1.transform( v1 );
		if( v1.x == 0 && v1.y == 0 )
		{
			v1.set( 0 , 1 , 0 );
			x1.transform( v1 );
			result.z = (float) Math.atan2( v1.y , v1.x ) - (float) Math.PI / 2;
		}
		else
		{
			result.z = (float) Math.atan2( v1.y , v1.x );
		}
		
		// de-pan the transform
		// Qyx = Q-zQzyx
		x2.rotZ( -result.z );
		x1.mul( x2 , x1 );
		
		// calculate tilt:
		// F' = Qyx * F
		// tilt = atan2(-F'.z, F'.x)
		v1.set( 1 , 0 , 0 );
		x1.transform( v1 );
		result.y = (float) Math.atan2( -v1.z , v1.x );
		
		// de-tilt the transform
		// Qx = Q-yQyx
		x2.rotY( -result.y );
		x1.mul( x2 , x1 );
		
		// calculate roll:
		// R' = Qx * R
		// roll = atan2(R'.z, R'.y)
		v1.set( 0 , 1 , 0 );
		x1.transform( v1 );
		result.x = ( float ) Math.atan2( v1.z , v1.y );
		
		return result;
	}
	
	/**
	 * Sets up a roll-tilt-pan transform. This is defined as <code>rotZ(pan)*rotY(tilt)*rotX(roll)</code>.
	 * 
	 * @param rtp
	 *            the (roll, till, pan) angle vector
	 * @param result
	 *            {@link Transform3D} to store the result in.
	 * @return <code>result</code>
	 * @see #getRollTiltPan(Transform3D, Vector3f)
	 */
	public Transform3D setRollTiltPan( Vector3f rtp , Transform3D result )
	{
		result.rotZ( rtp.z );
		x1.rotY( rtp.y );
		result.mul( x1 );
		x1.rotX( rtp.x );
		result.mul( x1 );
		return result;
	}
}
