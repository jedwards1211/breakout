package org.andork.jogl.util;

import static org.andork.vecmath.FloatArrayVecmath.cross;
import static org.andork.vecmath.FloatArrayVecmath.invAffine;
import static org.andork.vecmath.FloatArrayVecmath.mmulAffine;
import static org.andork.vecmath.FloatArrayVecmath.mvmulAffine;
import static org.andork.vecmath.FloatArrayVecmath.normalize3;
import static org.andork.vecmath.FloatArrayVecmath.rotY;
import static org.andork.vecmath.FloatArrayVecmath.setColumn3;
import static org.andork.vecmath.FloatArrayVecmath.setIdentity;
import static org.andork.vecmath.FloatArrayVecmath.setRotation;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.media.opengl.GLAutoDrawable;

import org.andork.vecmath.FloatArrayVecmath;

public class BasicOrbiter extends MouseAdapter
{
	
	final BasicGL3Scene	scene;
	MouseEvent			lastEvent	= null;
	final float[ ]		axis		= new float[ 3 ];
	final float[ ]		center		= new float[ 3 ];
	MouseEvent			pressEvent	= null;
	final float[ ]		m1			= FloatArrayVecmath.newIdentityMatrix( );
	final float[ ]		m2			= FloatArrayVecmath.newIdentityMatrix( );
	boolean				active		= true;
	boolean				callDisplay	= true;
	float				panFactor	= ( float ) Math.PI;
	float				tiltFactor	= ( float ) Math.PI;
	
	public BasicOrbiter( BasicGL3Scene scene )
	{
		super( );
		this.scene = scene;
	}
	
	public boolean isActive( )
	{
		return active;
	}
	
	public void setActive( boolean active )
	{
		this.active = active;
	}
	
	public boolean isCallDisplay( )
	{
		return callDisplay;
	}
	
	public void setCallDisplay( boolean callDisplay )
	{
		this.callDisplay = callDisplay;
	}
	
	public float getPanFactor( )
	{
		return panFactor;
	}
	
	public void setPanFactor( float panFactor )
	{
		this.panFactor = panFactor;
	}
	
	public float getTiltFactor( )
	{
		return tiltFactor;
	}
	
	public void setTiltFactor( float tiltFactor )
	{
		this.tiltFactor = tiltFactor;
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		pressEvent = e;
		lastEvent = e;
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		if( !active )
		{
			return;
		}
		
		if( pressEvent.getButton( ) == MouseEvent.BUTTON1 && pressEvent.isControlDown( ) )
		{
			int dx = e.getX( ) - lastEvent.getX( );
			int dy = e.getY( ) - lastEvent.getY( );
			lastEvent = e;
			
			Component glCanvas = e.getComponent( );
			
			invAffine( scene.v , m1 );
			mvmulAffine( m1 , 0 , 0 , 1 , axis );
			cross( 0 , 1 , 0 , axis , axis );
			normalize3( axis );
			
			setIdentity( m1 );
			setIdentity( m2 );
			
			m2[ 3 ] = -center[ 0 ];
			m2[ 7 ] = -center[ 1 ];
			m2[ 11 ] = -center[ 2 ];
			
			float dpan = ( float ) ( dx * panFactor / glCanvas.getWidth( ) );
			float dtilt = ( float ) ( dy * tiltFactor / glCanvas.getHeight( ) );
			
			rotY( m1 , dpan );
			
			mmulAffine( m1 , m2 , m2 );
			
			setRotation( m1 , axis , dtilt );
			mmulAffine( m1 , m2 , m2 );
			
			setIdentity( m1 );
			setColumn3( m1 , 3 , center );
			
			mmulAffine( m1 , m2 , m2 );
			mmulAffine( scene.v , m2 , scene.v );
		}
		
		if( callDisplay )
		{
			( ( GLAutoDrawable ) e.getComponent( ) ).display( );
		}
	}
}
