
package org.andork.j3d.camera;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.universe.ViewingPlatform;

public class Camera3D
{
	private TransformGroup				m_lightGroup;
	private Light						m_headlight			= null;
	
	private final ViewingPlatform		m_platform;
	private final TransformGroup		m_transformGroup;
	private final Transform3D			m_initXform			= new Transform3D( );
	private final Transform3D			m_tempXform			= new Transform3D( );
	private final Transform3D			m_transform			= new Transform3D( );
	
	private final CameraPosition		m_currentPosition	= new CameraPosition( );
	private final CameraPosition		m_pendingPosition	= new CameraPosition( );
	
	public static final Point3d			RESET_LOCATION		= new Point3d( 0 , 2000 , 0 );
	public static final Point3d			RESET_LOOKAT		= new Point3d( 0 , 0 , 0 );
	
	private BoundingSphere				m_restrictionBounds	= null;
	
	private static final CameraPosition	RESET_POSITION		= new CameraPosition( RESET_LOCATION , RESET_LOOKAT );
	
	public Camera3D( ViewingPlatform viewPlatform )
	{
		m_platform = viewPlatform;
		m_transformGroup = m_platform.getViewPlatformTransform( );
		
		m_initXform.rotY( -Math.PI / 2 );
		m_tempXform.rotZ( Math.PI / 2 );
		m_initXform.mul( m_tempXform );
		m_initXform.setIdentity( );
		
		importVPT( );
		
		// TvdProperties.getInstance().setProperty(TvdProperties.TVD_HEADLIGHT_ON_KEY, "true");
		
		createHeadlight( );
		setHeadlightOn( true );
		reset( );
	}
	
	/**
	 * Imports the view platform transform into the current and pending positions.
	 */
	private void importVPT( )
	{
		m_transformGroup.getTransform( m_transform );
		m_tempXform.invert( m_initXform );
		m_transform.mul( m_tempXform ); // remove initial transform from ViewingPlatform transform
		
		m_currentPosition.setTransform( m_transform );
		m_pendingPosition.setTransform( m_transform );
	}
	
	/**
	 * Restricts the pending position to the restriction bounds, copies the pending position to the current position, and updates the view platform transform.
	 */
	private void exportVPT( )
	{
		if( m_restrictionBounds != null )
		{
			m_pendingPosition.restrict( m_restrictionBounds );
		}
		m_pendingPosition.getTransform( m_transform );
		m_transform.mul( m_initXform );
		try
		{
			m_transformGroup.setTransform( m_transform );
		}
		catch( Throwable t )
		{
			t.printStackTrace( );
			System.out.println( "Current position: " + m_currentPosition );
			System.out.println( "Pending position: " + m_pendingPosition );
		}
		m_currentPosition.copy( m_pendingPosition );
		
		fireOrientationChangedEvent( new CameraEvent( this ) );
	}
	
	public void reset( )
	{
		setLocation( RESET_LOCATION , false );
		lookAt( RESET_LOOKAT , true );
	}
	
	public Point3f getResetLocation( Point3f result )
	{
		return RESET_POSITION.getLocation( result );
	}
	
	public Point3f getResetLookAt( Point3f result )
	{
		return RESET_POSITION.getLookAt( result );
	}
	
	public Point3d getResetLocation( Point3d result )
	{
		return RESET_POSITION.getLocation( result );
	}
	
	public Point3d getResetLookAt( Point3d result )
	{
		return RESET_POSITION.getLookAt( result );
	}
	
	/**
	 * Gets the zone the camera is prevented from moving out of. If <code>null</code>, there is no restriction.
	 */
	public BoundingSphere getRestrictionBounds( )
	{
		return m_restrictionBounds;
	}
	
	/**
	 * Sets the zone the camera is prevented from moving out of. If <code>null</code>, there is no restriction.
	 */
	public void setRestrictionBounds( BoundingSphere bounds )
	{
		CameraPosition.checkValid( bounds );
		m_restrictionBounds = bounds;
	}
	
	/**
	 * Sets the camera location to <code>newLocation</code> without affecting the rotation.
	 */
	public void setLocation( Tuple3d newLocation , boolean applyNow )
	{
		m_pendingPosition.setLocation( newLocation );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Sets the camera location to <code>newLocation</code> without affecting the rotation.
	 */
	public void setLocation( Tuple3f newLocation , boolean applyNow )
	{
		m_pendingPosition.setLocation( newLocation );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Pans and tilts the camera to point at <code>lookAt</code>.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void lookAt( Tuple3d lookAt , boolean applyNow )
	{
		m_pendingPosition.lookAt( lookAt );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Pans and tilts the camera to point at <code>lookAt</code>.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void lookAt( Tuple3f lookAt , boolean applyNow )
	{
		m_pendingPosition.lookAt( lookAt );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Moves the camera to the specified location and pan and tilts it to point at <code>lookAt</code>.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void lookAt( Tuple3d loc , Tuple3d lookAt , boolean applyNow )
	{
		m_pendingPosition.setLocation( loc );
		m_pendingPosition.lookAt( lookAt );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Moves the camera to the specified location and pan and tilts it to point at <code>lookAt</code>.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void lookAt( Tuple3f loc , Tuple3f lookAt , boolean applyNow )
	{
		m_pendingPosition.setLocation( loc );
		m_pendingPosition.lookAt( lookAt );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Places the location of the camera in <code>result</code> and returns it.
	 */
	public Point3d getLocation( Point3d result )
	{
		return m_pendingPosition.getLocation( result );
	}
	
	/**
	 * Places the location of the camera in <code>result</code> and returns it.
	 */
	public Point3f getLocation( Point3f result )
	{
		return m_pendingPosition.getLocation( result );
	}
	
	/**
	 * Places the location of the camera in <code>result</code> and returns it.
	 */
	public Vector3d getLocation( Vector3d result )
	{
		return m_pendingPosition.getLocation( result );
	}
	
	/**
	 * Places the location of the camera in <code>result</code> and returns it.
	 */
	public Vector3f getLocation( Vector3f result )
	{
		return m_pendingPosition.getLocation( result );
	}
	
	public Point3d getLookAt( Point3d result )
	{
		return m_pendingPosition.getLookAt( result );
	}
	
	public Point3f getLookAt( Point3f result )
	{
		return m_pendingPosition.getLookAt( result );
	}
	
	/**
	 * Translates the camera.
	 * 
	 * @param translation
	 *            offset in virtual world coordinates
	 */
	public void translate( Vector3d translation , boolean applyNow )
	{
		m_pendingPosition.translate( translation );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Translates the camera.
	 * 
	 * @param translation
	 *            offset in virtual world coordinates
	 */
	public void translate( Vector3f translation , boolean applyNow )
	{
		m_pendingPosition.translate( translation );
		if( applyNow )
		{
			exportVPT( );
		}
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
	public void translate( double dx , double dy , double dz , boolean applyNow )
	{
		m_pendingPosition.translate( dx , dy , dz );
		if( applyNow )
		{
			exportVPT( );
		}
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
	public void move( double dForward , double dRight , double dDown , boolean applyNow )
	{
		m_pendingPosition.move( dForward , dRight , dDown );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Moves the camera relative to its orientation.
	 * 
	 * @param motion
	 *            vector specifying distances to move (forward, right, down)
	 */
	public void move( Vector3d motion , boolean applyNow )
	{
		m_pendingPosition.move( motion );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Moves the camera relative to its orientation.
	 * 
	 * @param motion
	 *            vector specifying distances to move (forward, right, down)
	 */
	public void move( Vector3f motion , boolean applyNow )
	{
		m_pendingPosition.move( motion );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Sets the camera pan angle to <code>pan</code>. Pan is defined as rotation around the Z axis after tilt and roll have been applied.
	 * 
	 * @see #pan(double)
	 * @see #setTilt(double)
	 * @see #setRoll(double)
	 */
	public void setPan( double pan , boolean applyNow )
	{
		m_pendingPosition.setPan( pan );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Increments the camera pan angle by <code>inc</code>. Pan is defined as rotation around the Z axis after tilt and roll have been applied.
	 * 
	 * @see #setPan(double)
	 * @see #tilt(double)
	 * @see #roll(double)
	 */
	public void pan( double inc , boolean applyNow )
	{
		m_pendingPosition.pan( inc );
		if( applyNow )
		{
			exportVPT( );
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
	public void setTilt( double tilt , boolean applyNow )
	{
		m_pendingPosition.setTilt( tilt );
		if( applyNow )
		{
			exportVPT( );
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
	public void tilt( double inc , boolean applyNow )
	{
		m_pendingPosition.tilt( inc );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Sets the camera roll angle to <code>roll</code>. Roll is defined as rotation around the X axis before pan and tilt are applied.
	 * 
	 * @see #roll(double)
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void setRoll( double roll , boolean applyNow )
	{
		m_pendingPosition.setRoll( roll );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Increments the camera roll angle by <code>inc</code>. Roll is defined as rotation around the X axis before pan and tilt are applied.
	 * 
	 * @see #setRoll(double)
	 * @see #pan(double)
	 * @see #tilt(double)
	 */
	public void roll( double inc , boolean applyNow )
	{
		m_pendingPosition.roll( inc );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Sets the pan and tilt simultaneously.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 */
	public void setPanTilt( double pan , double tilt , boolean applyNow )
	{
		m_pendingPosition.setPanTilt( pan , tilt );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Increments the pan and tilt simultaneously.
	 * 
	 * @see #pan(double)
	 * @see #tilt(double)
	 */
	public void panTilt( double panInc , double tiltInc , boolean applyNow )
	{
		m_pendingPosition.panTilt( panInc , tiltInc );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	/**
	 * Sets the pan, tilt, and roll simultaneously.
	 * 
	 * @see #setPan(double)
	 * @see #setTilt(double)
	 * @see #setRoll(double)
	 */
	public void setPanTiltRoll( double pan , double tilt , double roll , boolean applyNow )
	{
		m_pendingPosition.setPanTiltRoll( pan , tilt , roll );
		if( applyNow )
		{
			exportVPT( );
		}
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
		return m_pendingPosition.getPan( );
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
		return m_pendingPosition.getTilt( );
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
		return m_pendingPosition.getRoll( );
	}
	
	/**
	 * Places the rotational component of the camera transform in <code>result</code> and returns it.
	 */
	public Matrix3d getRotation( Matrix3d result )
	{
		return m_pendingPosition.getRotation( result );
	}
	
	/**
	 * Places the rotational component of the camera transform in <code>result</code> and returns it.
	 */
	public Matrix3f getRotation( Matrix3f result )
	{
		return m_pendingPosition.getRotation( result );
	}
	
	/**
	 * Sets <code>result</code> to point forward from the camera and returns it.
	 */
	public Vector3d getForward( Vector3d result )
	{
		return m_pendingPosition.getForward( result );
	}
	
	/**
	 * Sets <code>result</code> to point backward from the camera and returns it.
	 */
	public Vector3d getBackward( Vector3d result )
	{
		return m_pendingPosition.getBackward( result );
	}
	
	/**
	 * Sets <code>result</code> to point to the right of camera and returns it.
	 */
	public Vector3d getRight( Vector3d result )
	{
		return m_pendingPosition.getRight( result );
	}
	
	/**
	 * Sets <code>result</code> to point to the left of camera and returns it.
	 */
	public Vector3d getLeft( Vector3d result )
	{
		return m_pendingPosition.getLeft( result );
	}
	
	/**
	 * Sets <code>result</code> to point down from the camera and returns it.
	 */
	public Vector3d getDown( Vector3d result )
	{
		return m_pendingPosition.getDown( result );
	}
	
	/**
	 * Sets <code>result</code> to point up from the camera and returns it.
	 */
	public Vector3d getUp( Vector3d result )
	{
		return m_pendingPosition.getUp( result );
	}
	
	/**
	 * Sets <code>result</code> to point forward from the camera and returns it.
	 */
	public Vector3f getForward( Vector3f result )
	{
		return m_pendingPosition.getForward( result );
	}
	
	/**
	 * Sets <code>result</code> to point backward from the camera and returns it.
	 */
	public Vector3f getBackward( Vector3f result )
	{
		return m_pendingPosition.getBackward( result );
	}
	
	/**
	 * Sets <code>result</code> to point to the right of camera and returns it.
	 */
	public Vector3f getRight( Vector3f result )
	{
		return m_pendingPosition.getRight( result );
	}
	
	/**
	 * Sets <code>result</code> to point to the left of camera and returns it.
	 */
	public Vector3f getLeft( Vector3f result )
	{
		return m_pendingPosition.getLeft( result );
	}
	
	/**
	 * Sets <code>result</code> to point down from the camera and returns it.
	 */
	public Vector3f getDown( Vector3f result )
	{
		return m_pendingPosition.getDown( result );
	}
	
	/**
	 * Sets <code>result</code> to point up from the camera and returns it.
	 */
	public Vector3f getUp( Vector3f result )
	{
		return m_pendingPosition.getUp( result );
	}
	
	/**
	 * Places the pending camera position in <code>position</code> and returns it.
	 */
	public CameraPosition getPosition( CameraPosition position )
	{
		position.copy( m_pendingPosition );
		return position;
	}
	
	/**
	 * Sets the camera position to <code>newPosition</code>.
	 */
	public void setPosition( CameraPosition newPosition , boolean applyNow )
	{
		m_pendingPosition.copy( newPosition );
		if( applyNow )
		{
			exportVPT( );
		}
	}
	
	public Point3d localToVworld( Point3d p )
	{
		m_pendingPosition.localToVworld( p );
		return p;
	}
	
	public Point3f localToVworld( Point3f p )
	{
		m_pendingPosition.localToVworld( p );
		return p;
	}
	
	public Vector3d localToVworld( Vector3d v )
	{
		m_pendingPosition.localToVworld( v );
		return v;
	}
	
	public Vector3f localToVworld( Vector3f v )
	{
		m_pendingPosition.localToVworld( v );
		return v;
	}
	
	public Point3d vworldToLocal( Point3d p )
	{
		m_pendingPosition.vworldToLocal( p );
		return p;
	}
	
	public Point3f vworldToLocal( Point3f p )
	{
		m_pendingPosition.vworldToLocal( p );
		return p;
	}
	
	public Vector3d vworldToLocal( Vector3d v )
	{
		m_pendingPosition.vworldToLocal( v );
		return v;
	}
	
	public Vector3f vworldToLocal( Vector3f v )
	{
		m_pendingPosition.vworldToLocal( v );
		return v;
	}
	
	protected void createHeadlight( )
	{
		final Color3f lightColor = new Color3f( 0.9f , 0.9f , 0.9f );
		final DirectionalLight light = new DirectionalLight( );
		light.setCapability( Light.ALLOW_STATE_WRITE );
		light.setColor( lightColor );
		
		final BoundingSphere worldBounds = new BoundingSphere( new Point3d( 0.0 , 0.0 , 0.0 ) , 100000.0 ); // Center, Extent
		light.setInfluencingBounds( worldBounds );
		
		m_headlight = light;
		final BranchGroup bg = new BranchGroup( );
		m_lightGroup = new TransformGroup( );
		m_lightGroup.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		bg.addChild( m_lightGroup );
		m_lightGroup.addChild( m_headlight );
		m_transformGroup.addChild( bg );
	}
	
	public void setHeadlightOn( boolean value )
	{
		if( m_headlight != null )
		{
			m_headlight.setEnable( value );
		}
	}
	
	public boolean isHeadlightOn( )
	{
		return m_headlight.getEnable( );
	}
	
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// CONFIGURATION
	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	// LISTENERS
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	List<CameraListener>	m_listeners	= new ArrayList<CameraListener>( );
	
	public void addCameraListener( CameraListener listener )
	{
		m_listeners.add( listener );
	}
	
	public void removeCameraListener( CameraListener listener )
	{
		m_listeners.remove( listener );
	}
	
	private void fireOrientationChangedEvent( CameraEvent event )
	{
		for( final CameraListener listener : m_listeners )
		{
			try
			{
				listener.orientationChanged( event );
			}
			catch( final Throwable t )
			{
				t.printStackTrace( );
			}
		}
	}
	
	public void transform( Transform3D xform , boolean applyNow )
	{
		m_pendingPosition.transform( xform );
		if( applyNow )
		{
			exportVPT( );
		}
	}
}
