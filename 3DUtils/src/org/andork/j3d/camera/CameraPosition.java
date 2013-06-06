
package org.andork.j3d.camera;

import javax.media.j3d.BadTransformException;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.andork.j3d.math.TransformComputer3d;

/**
 * Encapsulates all the necessary information about a camera location and orientation, and methods for positioning the camera. <br>
 * <br>
 * The nominal orientation of the camera (i.e. with the identity transform) is looking forward in the +X direction, with +Y to the right and +Z downward.
 * <b>This is not the nominal orientation of ViewPlatform, which is -Z, X, -Y.</b> <br>
 * The camera orientation can be broken down into pan, tilt, and roll; it is a rotation about the X axis (roll) followed by a rotation about the Y axis (tilt)
 * followed by a rotation about the Z axis (pan).
 * 
 * @author andy.edwards
 */
public class CameraPosition
{
	public CameraPosition( )
	{
	}
	
	public CameraPosition( Point3d location , Point3d lookAt )
	{
		setLocation( location );
		lookAt( lookAt );
	}
	
	public static final double			MAX_TILT		= Math.PI / 2.001;
	
	private final Transform3D			xform			= new Transform3D( );
	
	private final Transform3D			invXform		= new Transform3D( );
	private boolean						invXformUpToDate;
	
	private final Matrix3d				rotation		= new Matrix3d( );
	private boolean						rotationUpToDate;
	
	private final Vector3d				tempVec			= new Vector3d( );
	private final Point3d				tempPt			= new Point3d( );
	
	private final Vector3d				location		= new Vector3d( );
	private boolean						locationUpToDate;
	
	// private final PanTiltRollContext panTiltRollContext = new PanTiltRollContext( );
	private final TransformComputer3d	xformComputer	= new TransformComputer3d( );
	private final Vector3d				rollTiltPan		= new Vector3d( );
	private boolean						panTiltRollUpToDate;
	
	private final Vector3d				forward			= new Vector3d( );
	private final Vector3d				right			= new Vector3d( );
	private final Vector3d				down			= new Vector3d( );
	private boolean						vectorsUpToDate;
	
	public void copy( CameraPosition other )
	{
		xform.set( other.xform );
		
		invXformUpToDate = other.invXformUpToDate;
		if( invXformUpToDate )
		{
			invXform.set( other.invXform );
		}
		
		rotationUpToDate = other.rotationUpToDate;
		if( rotationUpToDate )
		{
			rotation.set( other.rotation );
		}
		
		locationUpToDate = other.locationUpToDate;
		if( locationUpToDate )
		{
			location.set( other.location );
		}
		
		panTiltRollUpToDate = other.panTiltRollUpToDate;
		if( panTiltRollUpToDate )
		{
			rollTiltPan.z = other.rollTiltPan.z;
			rollTiltPan.y = other.rollTiltPan.y;
			rollTiltPan.x = other.rollTiltPan.x;
		}
		
		vectorsUpToDate = other.vectorsUpToDate;
		if( vectorsUpToDate )
		{
			forward.set( other.forward );
			right.set( other.right );
			down.set( other.down );
		}
	}
	
	/**
	 * Places the camera transform into <code>result</code> and returns it.
	 */
	public Transform3D getTransform( Transform3D result )
	{
		result.set( xform );
		return result;
	}
	
	/**
	 * Sets the camera transform to <code>xform</code>.
	 * 
	 * @param xform
	 *            a congruent transform with a positive determinant. (In other words, xform must not scale or transform to a left-handed coordinate system.)
	 * @throws BadTransformException
	 *             if xform is not congruent or has a negative determinant
	 */
	public void setTransform( Transform3D xform )
	{
		if( xform.getBestType( ) == Transform3D.AFFINE )
		{
			throw new BadTransformException( "xform must be congruent" );
		}
		if( !xform.getDeterminantSign( ) )
		{
			throw new BadTransformException( "xform must have a positive determinant" );
		}
		
		this.xform.set( xform );
		invXformUpToDate = false;
		rotationUpToDate = false;
		locationUpToDate = false;
		panTiltRollUpToDate = false;
		vectorsUpToDate = false;
	}
	
	public Point3d vworldToLocal( Point3d p )
	{
		xform.transform( p );
		return p;
	}
	
	public Point3f vworldToLocal( Point3f p )
	{
		xform.transform( p );
		return p;
	}
	
	public Vector3d vworldToLocal( Vector3d v )
	{
		xform.transform( v );
		return v;
	}
	
	public Vector3f vworldToLocal( Vector3f v )
	{
		xform.transform( v );
		return v;
	}
	
	private void updateInvXform( )
	{
		if( !invXformUpToDate )
		{
			invXform.invert( xform );
			invXformUpToDate = true;
		}
	}
	
	public Transform3D getInverseTransform( Transform3D result )
	{
		updateInvXform( );
		result.set( invXform );
		return result;
	}
	
	public Point3d localToVworld( Point3d p )
	{
		updateInvXform( );
		invXform.transform( p );
		return p;
	}
	
	public Point3f localToVworld( Point3f p )
	{
		updateInvXform( );
		invXform.transform( p );
		return p;
	}
	
	public Vector3d localToVworld( Vector3d v )
	{
		updateInvXform( );
		invXform.transform( v );
		return v;
	}
	
	public Vector3f localToVworld( Vector3f v )
	{
		updateInvXform( );
		invXform.transform( v );
		return v;
	}
	
	private void updateLocation( )
	{
		if( !locationUpToDate )
		{
			xform.get( location );
			locationUpToDate = true;
		}
	}
	
	static void checkValid( Tuple3f t )
	{
		if( Double.isNaN( t.x ) || Double.isNaN( t.y ) || Double.isNaN( t.z ) )
		{
			throw new IllegalArgumentException( "tuple has NaN values" );
		}
		if( Double.isInfinite( t.x ) || Double.isInfinite( t.y ) || Double.isInfinite( t.z ) )
		{
			throw new IllegalArgumentException( "tuple has Infinite values" );
		}
	}
	
	static void checkValid( Tuple3d t )
	{
		if( Double.isNaN( t.x ) || Double.isNaN( t.y ) || Double.isNaN( t.z ) )
		{
			throw new IllegalArgumentException( "tuple has NaN values" );
		}
		if( Double.isInfinite( t.x ) || Double.isInfinite( t.y ) || Double.isInfinite( t.z ) )
		{
			throw new IllegalArgumentException( "tuple has Infinite values" );
		}
	}
	
	static void checkValid( BoundingSphere bounds )
	{
		if( bounds.isEmpty( ) )
		{
			throw new IllegalArgumentException( "restriction bounds must not be empty" );
		}
	}
	
	/**
	 * Sets the camera location to <code>newLocation</code> without affecting the rotation.
	 */
	public void setLocation( Tuple3d newLocation )
	{
		checkValid( newLocation );
		location.set( newLocation );
		xform.setTranslation( location );
		invXformUpToDate = false;
		locationUpToDate = true;
	}
	
	/**
	 * Sets the camera location to <code>newLocation</code> without affecting the rotation.
	 */
	public void setLocation( Tuple3f newLocation )
	{
		checkValid( newLocation );
		tempVec.set( newLocation );
		setLocation( tempVec );
	}
	
	/**
	 * Pans and tilts the camera to point at <code>lookAt</code>.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void lookAt( Tuple3d lookAt )
	{
		checkValid( lookAt );
		updateLocation( );
		forward.sub( lookAt , location );
		forward.normalize( );
		final boolean setPan = forward.x != 0 || forward.y != 0;
		
		final double pan = setPan ? Math.atan2( forward.y , forward.x ) : 0;
		final double dxy = Math.sqrt( forward.x * forward.x + forward.y * forward.y );
		final double tilt = Math.atan2( dxy , forward.z ) - Math.PI / 2;
		
		if( setPan )
		{
			setPanTilt( pan , tilt );
		}
		else
		{
			setTilt( tilt ); // preserve pan
		}
	}
	
	/**
	 * Pans and tilts the camera to point at <code>lookAt</code>.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void lookAt( Tuple3f lookAt )
	{
		checkValid( lookAt );
		tempVec.set( lookAt );
		lookAt( tempVec );
	}
	
	/**
	 * Places the location of the camera in <code>result</code> and returns it.
	 */
	public Point3d getLocation( Point3d result )
	{
		updateLocation( );
		result.set( location );
		return result;
	}
	
	/**
	 * Places the location of the camera in <code>result</code> and returns it.
	 */
	public Point3f getLocation( Point3f result )
	{
		updateLocation( );
		result.set( location );
		return result;
	}
	
	/**
	 * Places the location of the camera in <code>result</code> and returns it.
	 */
	public Vector3d getLocation( Vector3d result )
	{
		updateLocation( );
		result.set( location );
		return result;
	}
	
	/**
	 * Places the location of the camera in <code>result</code> and returns it.
	 */
	public Vector3f getLocation( Vector3f result )
	{
		updateLocation( );
		result.set( location );
		return result;
	}
	
	public Point3d getLookAt( Point3d result )
	{
		updateLocation( );
		getForward( tempVec );
		tempVec.add( location );
		result.set( tempVec );
		return result;
	}
	
	public Point3f getLookAt( Point3f result )
	{
		updateLocation( );
		getForward( tempVec );
		tempVec.add( location );
		result.set( tempVec );
		return result;
	}
	
	/**
	 * Translates the camera.
	 * 
	 * @param translation
	 *            offset in virtual world coordinates
	 */
	public void translate( Vector3d translation )
	{
		checkValid( translation );
		updateLocation( );
		location.add( translation );
		xform.setTranslation( location );
		invXformUpToDate = false;
	}
	
	/**
	 * Translates the camera.
	 * 
	 * @param translation
	 *            offset in virtual world coordinates
	 */
	public void translate( Vector3f translation )
	{
		checkValid( translation );
		tempVec.set( translation );
		translate( tempVec );
	}
	
	/**
	 * Translates the camera.
	 * 
	 * @param dx
	 *            x offset in virtual world coordinates
	 * @param dy
	 *            y offset in virtual world coordinates
	 * @param dz
	 *            z offset in virtual world coordinates
	 */
	public void translate( double dx , double dy , double dz )
	{
		if( Double.isNaN( dx ) || Double.isNaN( dy ) || Double.isNaN( dz ) || Double.isInfinite( dx ) || Double.isInfinite( dy ) || Double.isInfinite( dz ) )
		{
			throw new IllegalArgumentException( "offsets must not be NaN or Infinite" );
		}
		tempVec.set( dx , dy , dz );
		translate( tempVec );
	}
	
	/**
	 * Moves the camera relative to its orientation.
	 * 
	 * @param dForward
	 *            distance to move forward
	 * @param dRight
	 *            distance to move to the right
	 * @param dDown
	 *            distance to move downward
	 */
	public void move( double dForward , double dRight , double dDown )
	{
		if( Double.isNaN( dForward ) || Double.isNaN( dRight ) || Double.isNaN( dDown ) || Double.isInfinite( dForward ) || Double.isInfinite( dRight ) || Double.isInfinite( dDown ) )
		{
			throw new IllegalArgumentException( "distances must not be NaN or Infinite" );
		}
		updateLocation( );
		updateVectors( );
		
		location.scaleAdd( dForward , forward , location );
		location.scaleAdd( dRight , right , location );
		location.scaleAdd( dDown , down , location );
		
		xform.setTranslation( location );
		invXformUpToDate = false;
	}
	
	/**
	 * Moves the camera relative to its orientation.
	 * 
	 * @param motion
	 *            vector specifying distances to move (forward, right, down)
	 */
	public void move( Vector3d motion )
	{
		checkValid( motion );
		move( motion.x , motion.y , motion.z );
	}
	
	/**
	 * Moves the camera relative to its orientation.
	 * 
	 * @param motion
	 *            vector specifying distances to move (forward, right, down)
	 */
	public void move( Vector3f motion )
	{
		checkValid( motion );
		move( motion.x , motion.y , motion.z );
	}
	
	private void updatePanTiltRoll( )
	{
		if( !panTiltRollUpToDate )
		{
			// TvdUtils.getPanTiltRoll( xform , panTiltRollContext );
			xformComputer.getRollTiltPan( xform , rollTiltPan );
			panTiltRollUpToDate = true;
		}
	}
	
	/**
	 * Sets the camera pan angle to <code>pan</code>. Pan is defined as rotation around the Z axis after tilt and roll have been applied.
	 * 
	 * @see #pan(double)
	 * @see #setTilt(double)
	 * @see #setRoll(double)
	 */
	public void setPan( double pan )
	{
		if( Double.isNaN( pan ) || Double.isInfinite( pan ) )
		{
			throw new IllegalArgumentException( "pan must not be NaN or infinite" );
		}
		updatePanTiltRoll( );
		if( rollTiltPan.z != pan )
		{
			rollTiltPan.z = pan;
			applyPanTiltRoll( );
		}
	}
	
	/**
	 * Increments the camera pan angle by <code>inc</code>. Pan is defined as rotation around the Z axis after tilt and roll have been applied.
	 * 
	 * @see #setPan(double)
	 * @see #tilt(double)
	 * @see #roll(double)
	 */
	public void pan( double inc )
	{
		if( inc != 0.0 )
		{
			if( Double.isNaN( inc ) || Double.isInfinite( inc ) )
			{
				throw new IllegalArgumentException( "inc must not be NaN or infinite" );
			}
			updatePanTiltRoll( );
			rollTiltPan.z += inc;
			applyPanTiltRoll( );
		}
	}
	
	/**
	 * Sets the camera tilt angle to <code>tilt</code>. Tilt is defined as rotation around the Y axis before pan has been applied and after roll has been
	 * applied. <b>Note:</b> the tilt is clamped to the range (-{@link #MAX_TILT}, {@link #MAX_TILT}) to prevent gimbal lock at the up/down singularities.
	 * 
	 * @see #tilt(double)
	 * @see #setPan(double)
	 * @see #setRoll(double)
	 */
	public void setTilt( double tilt )
	{
		if( Double.isNaN( tilt ) || Double.isInfinite( tilt ) )
		{
			throw new IllegalArgumentException( "tilt must not be NaN or infinite" );
		}
		// restrict tilt to prevent extreme weirdness
		tilt = Math.max( -MAX_TILT , Math.min( MAX_TILT , tilt ) );
		updatePanTiltRoll( );
		if( rollTiltPan.y != tilt )
		{
			rollTiltPan.y = tilt;
			applyPanTiltRoll( );
		}
	}
	
	/**
	 * Increments the camera tilt angle by <code>inc</code>. Tilt is defined as rotation around the Y axis before pan has been applied and after roll has been
	 * applied. <b>Note:</b> the tilt is clamped to the range (-{@link #MAX_TILT}, {@link #MAX_TILT}) to prevent gimbal lock at the up/down singularities.
	 * 
	 * @see #setTilt(double)
	 * @see #pan(double)
	 * @see #roll(double)
	 */
	public void tilt( double inc )
	{
		if( inc != 0.0 )
		{
			if( Double.isNaN( inc ) || Double.isInfinite( inc ) )
			{
				throw new IllegalArgumentException( "inc must not be NaN or infinite" );
			}
			updatePanTiltRoll( );
			rollTiltPan.y += inc;
			// restrict tilt to prevent extreme weirdness
			rollTiltPan.y = Math.max( -MAX_TILT , Math.min( MAX_TILT , rollTiltPan.y ) );
			applyPanTiltRoll( );
		}
	}
	
	/**
	 * Sets the camera roll angle to <code>roll</code>. Roll is defined as rotation around the X axis before pan and tilt are applied.
	 * 
	 * @see #roll(double)
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void setRoll( double roll )
	{
		if( Double.isNaN( roll ) || Double.isInfinite( roll ) )
		{
			throw new IllegalArgumentException( "roll must not be NaN or infinite" );
		}
		updatePanTiltRoll( );
		if( rollTiltPan.x != roll )
		{
			rollTiltPan.x = roll;
			applyPanTiltRoll( );
		}
	}
	
	/**
	 * Increments the camera roll angle by <code>inc</code>. Roll is defined as rotation around the X axis before pan and tilt are applied.
	 * 
	 * @see #setRoll(double)
	 * @see #pan(double)
	 * @see #tilt(double)
	 */
	public void roll( double inc )
	{
		if( inc != 0.0 )
		{
			if( Double.isNaN( inc ) || Double.isInfinite( inc ) )
			{
				throw new IllegalArgumentException( "inc must not be NaN or infinite" );
			}
			updatePanTiltRoll( );
			rollTiltPan.x += inc;
			applyPanTiltRoll( );
		}
	}
	
	/**
	 * Sets the pan and tilt simultaneously.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void setPanTilt( double pan , double tilt )
	{
		if( Double.isNaN( pan ) || Double.isInfinite( pan ) )
		{
			throw new IllegalArgumentException( "pan must not be NaN or infinite" );
		}
		if( Double.isNaN( tilt ) || Double.isInfinite( tilt ) )
		{
			throw new IllegalArgumentException( "tilt must not be NaN or infinite" );
		}
		// restrict tilt to prevent extreme weirdness
		tilt = Math.max( -MAX_TILT , Math.min( MAX_TILT , tilt ) );
		updatePanTiltRoll( );
		if( rollTiltPan.z != pan || rollTiltPan.y != tilt )
		{
			rollTiltPan.z = pan;
			rollTiltPan.y = tilt;
			applyPanTiltRoll( );
		}
	}
	
	/**
	 * Increments the pan and tilt simultaneously.
	 * 
	 * @see #pan(double)
	 * @see #tilt(double)
	 */
	public void panTilt( double panInc , double tiltInc )
	{
		if( Double.isNaN( panInc ) || Double.isInfinite( panInc ) )
		{
			throw new IllegalArgumentException( "panInc must not be NaN or infinite" );
		}
		if( Double.isNaN( tiltInc ) || Double.isInfinite( tiltInc ) )
		{
			throw new IllegalArgumentException( "tiltInc must not be NaN or infinite" );
		}
		if( panInc != 0.0 || tiltInc != 0.0 )
		{
			updatePanTiltRoll( );
			rollTiltPan.z += panInc;
			rollTiltPan.y += tiltInc;
			// restrict tilt to prevent extreme weirdness
			rollTiltPan.y = Math.max( -MAX_TILT , Math.min( MAX_TILT , rollTiltPan.y ) );
			applyPanTiltRoll( );
		}
	}
	
	/**
	 * Sets the pan, tilt, and roll simultaneously.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 * @see #setRoll(double)
	 */
	public void setPanTiltRoll( double pan , double tilt , double roll )
	{
		if( Double.isNaN( pan ) || Double.isInfinite( pan ) )
		{
			throw new IllegalArgumentException( "pan must not be NaN or infinite" );
		}
		if( Double.isNaN( tilt ) || Double.isInfinite( tilt ) )
		{
			throw new IllegalArgumentException( "tilt must not be NaN or infinite" );
		}
		if( Double.isNaN( roll ) || Double.isInfinite( roll ) )
		{
			throw new IllegalArgumentException( "roll must not be NaN or infinite" );
		}
		// restrict tilt to prevent extreme weirdness
		tilt = Math.max( -MAX_TILT , Math.min( MAX_TILT , tilt ) );
		updatePanTiltRoll( );
		if( rollTiltPan.z != pan || rollTiltPan.y != tilt || rollTiltPan.x != roll )
		{
			rollTiltPan.z = pan;
			rollTiltPan.y = tilt;
			rollTiltPan.x = roll;
			applyPanTiltRoll( );
		}
	}
	
	private void applyPanTiltRoll( )
	{
		updateLocation( ); // save translation
		// TvdUtils.setPanTiltRoll( xform , panTiltRollContext );
		xformComputer.setRollTiltPan( rollTiltPan , xform );
		xform.setTranslation( location ); // restore translation
		invXformUpToDate = false;
		rotationUpToDate = false;
		vectorsUpToDate = false;
	}
	
	/**
	 * Gets the camera pan angle, which is defined as rotation around the Z axis after tilt and roll have been applied.
	 * 
	 * @see #setPan(double)
	 * @see #getTilt(double)
	 * @see #getRoll(double)
	 */
	public double getPan( )
	{
		updatePanTiltRoll( );
		return rollTiltPan.z;
	}
	
	/**
	 * Gets the camera tilt angle, which is defined as rotation around the Y axis before pan is applied and after roll has been applied.
	 * 
	 * @see #setTilt(double)
	 * @see #getPan(double)
	 * @see #getRoll(double)
	 */
	public double getTilt( )
	{
		updatePanTiltRoll( );
		return rollTiltPan.y;
	}
	
	/**
	 * Gets the camera roll angle, which is defined as rotation around the X axis before pan and tilt are applied.
	 * 
	 * @see #setRoll(double)
	 * @see #getPan(double)
	 * @see #getTilt(double)
	 */
	public double getRoll( )
	{
		updatePanTiltRoll( );
		return rollTiltPan.x;
	}
	
	private void updateRotation( )
	{
		if( !rotationUpToDate )
		{
			xform.get( rotation );
			rotationUpToDate = true;
		}
	}
	
	/**
	 * Places the rotational component of the camera transform in <code>result</code> and returns it.
	 */
	public Matrix3d getRotation( Matrix3d result )
	{
		updateRotation( );
		result.set( rotation );
		return result;
	}
	
	/**
	 * Places the rotational component of the camera transform in <code>result</code> and returns it.
	 */
	public Matrix3f getRotation( Matrix3f result )
	{
		updateRotation( );
		result.set( rotation );
		return result;
	}
	
	private void updateVectors( )
	{
		if( !vectorsUpToDate )
		{
			updateRotation( );
			forward.set( 1 , 0 , 0 );
			right.set( 0 , 1 , 0 );
			down.set( 0 , 0 , 1 );
			rotation.transform( forward );
			rotation.transform( right );
			rotation.transform( down );
			vectorsUpToDate = true;
		}
	}
	
	/**
	 * Sets <code>result</code> to point forward from the camera and returns it.
	 */
	public Vector3d getForward( Vector3d result )
	{
		updateVectors( );
		result.set( forward );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point backward from the camera and returns it.
	 */
	public Vector3d getBackward( Vector3d result )
	{
		updateVectors( );
		result.negate( forward );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point to the right of camera and returns it.
	 */
	public Vector3d getRight( Vector3d result )
	{
		updateVectors( );
		result.set( right );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point to the left of camera and returns it.
	 */
	public Vector3d getLeft( Vector3d result )
	{
		updateVectors( );
		result.negate( right );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point down from the camera and returns it.
	 */
	public Vector3d getDown( Vector3d result )
	{
		updateVectors( );
		result.set( down );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point up from the camera and returns it.
	 */
	public Vector3d getUp( Vector3d result )
	{
		updateVectors( );
		result.negate( down );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point forward from the camera and returns it.
	 */
	public Vector3f getForward( Vector3f result )
	{
		getForward( tempVec );
		result.set( tempVec );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point backward from the camera and returns it.
	 */
	public Vector3f getBackward( Vector3f result )
	{
		getBackward( tempVec );
		result.set( tempVec );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point to the right of camera and returns it.
	 */
	public Vector3f getRight( Vector3f result )
	{
		getRight( tempVec );
		result.set( tempVec );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point to the left of camera and returns it.
	 */
	public Vector3f getLeft( Vector3f result )
	{
		getLeft( tempVec );
		result.set( tempVec );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point down from the camera and returns it.
	 */
	public Vector3f getDown( Vector3f result )
	{
		getDown( tempVec );
		result.set( tempVec );
		return result;
	}
	
	/**
	 * Sets <code>result</code> to point up from the camera and returns it.
	 */
	public Vector3f getUp( Vector3f result )
	{
		getUp( tempVec );
		result.set( tempVec );
		return result;
	}
	
	/**
	 * If the position is not currently inside the given bounds, it is translated to the closest point within the bounds.
	 */
	public void restrict( BoundingSphere restriction )
	{
		checkValid( restriction );
		updateLocation( );
		tempPt.set( location );
		if( !restriction.intersect( tempPt ) )
		{
			restriction.getCenter( tempPt );
			tempVec.sub( tempPt , location );
			double dist = tempVec.length( );
			// dist can't be zero since the location didn't intersect the bounding sphere
			tempVec.scale( ( dist - restriction.getRadius( ) ) / dist );
			tempVec.scale( 1.001 );
			// tempVec is now the right size to translate the camera to the edge
			// of the BoundingSphere
			translate( tempVec );
		}
	}
	
	@Override
	public String toString( )
	{
		return String.format( "CameraPosition[Location: %s, Roll/Tilt/Pan: %s, Transform: %s]" , location.toString( ) , rollTiltPan.toString( ) , xform.toString( ) );
	}
	
	public void transform( Transform3D xform2 )
	{
		if( xform.getBestType( ) == Transform3D.AFFINE )
		{
			throw new BadTransformException( "xform must be congruent" );
		}
		if( !xform.getDeterminantSign( ) )
		{
			throw new BadTransformException( "xform must have a positive determinant" );
		}
		
		xform.mul( xform2 , xform );
		invXformUpToDate = false;
		rotationUpToDate = false;
		locationUpToDate = false;
		panTiltRollUpToDate = false;
		vectorsUpToDate = false;
	}
	
	@Override
	public boolean equals( Object o )
	{
		if( o == null )
		{
			return false;
		}
		if( o == this )
		{
			return true;
		}
		if( o instanceof CameraPosition )
		{
			CameraPosition cp = ( CameraPosition ) o;
			return xform.equals( cp.xform );
		}
		return false;
	}
	
	public boolean epsilonEquals( CameraPosition other , double epsilon )
	{
		return xform.epsilonEquals( other.xform , epsilon );
	}
	
	@Override
	public int hashCode( )
	{
		return xform.hashCode( );
	}
}
