package org.andork.jogl.neu.awt;

import static org.andork.math3d.Vecmath.invAffine;
import static org.andork.math3d.Vecmath.mmulAffine;
import static org.andork.math3d.Vecmath.mvmulAffine;
import static org.andork.math3d.Vecmath.normalize3;
import static org.andork.math3d.Vecmath.rotY;
import static org.andork.math3d.Vecmath.setColumn3;
import static org.andork.math3d.Vecmath.setIdentity;
import static org.andork.math3d.Vecmath.setRotation;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.andork.jogl.neu.JoglScene;
import org.andork.math3d.Vecmath;

public class BasicOrbiter extends MouseAdapter
{
	final Component	canvas;
	final JoglScene	scene;
	MouseEvent		lastEvent	= null;
	final float[ ]	center		= { 0 , 0 , 0 };
	final float[ ]	axis		= new float[ 3 ];
	MouseEvent		pressEvent	= null;
	final float[ ]	v			= Vecmath.newMat4f( );
	final float[ ]	m1			= Vecmath.newMat4f( );
	final float[ ]	m2			= Vecmath.newMat4f( );
	boolean			active		= true;
	boolean			callDisplay	= true;
	float			panFactor	= ( float ) Math.PI;
	float			tiltFactor	= ( float ) Math.PI;
	float			sensitivity	= 1f;
	
	public BasicOrbiter( BasicJoglSetup setup )
	{
		super( );
		this.canvas = setup.canvas;
		this.scene = setup.scene;
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
	
	public float getSensitivity( )
	{
		return sensitivity;
	}
	
	public void setSensitivity( float sensitivity )
	{
		this.sensitivity = sensitivity;
	}
	
	public void setTiltFactor( float tiltFactor )
	{
		this.tiltFactor = tiltFactor;
	}
	
	public void setCenter( float[ ] center )
	{
		Vecmath.setf( this.center , center );
	}
	
	public void getCenter( float[ ] out )
	{
		Vecmath.setf( out , center );
	}
	
	@Override
	public void mouseReleased( MouseEvent e )
	{
		if( pressEvent != null && e.getButton( ) == pressEvent.getButton( ) )
		{
			pressEvent = null;
		}
	}
	
	@Override
	public void mousePressed( MouseEvent e )
	{
		if( pressEvent == null && !e.isShiftDown( ) && !e.isAltDown( ) )
		{
			pressEvent = e;
			lastEvent = e;
		}
	}
	
	@Override
	public void mouseDragged( MouseEvent e )
	{
		if( !active || pressEvent == null )
		{
			return;
		}
		
		int button = pressEvent.getButton( );
		
		if( e.isAltDown( ) )
		{
			if( button == MouseEvent.BUTTON1 )
			{
				button = MouseEvent.BUTTON3;
			}
			else if( button == MouseEvent.BUTTON3 )
			{
				button = MouseEvent.BUTTON1;
			}
		}
		
		for( float f : center )
		{
			if( Float.isNaN( f ) || Float.isInfinite( f ) )
			{
				return;
			}
		}
		for( float f : axis )
		{
			if( Float.isNaN( f ) || Float.isInfinite( f ) )
			{
				return;
			}
		}
		float dx = e.getX( ) - lastEvent.getX( );
		float dy = e.getY( ) - lastEvent.getY( );
		if( e.isControlDown( ) )
		{
			dx /= 10f;
			dy /= 10f;
		}
		lastEvent = e;
		
		Component canvas = ( Component ) e.getSource( );
		
		if( button == MouseEvent.BUTTON1 && !e.isShiftDown( ) )
		{
			scene.getViewXform( v );
			invAffine( v , m1 );
			mvmulAffine( m1 , 1 , 0 , 0 , axis );
			normalize3( axis );
			
			setIdentity( m1 );
			setIdentity( m2 );
			
			m2[ 12 ] = -center[ 0 ];
			m2[ 13 ] = -center[ 1 ];
			m2[ 14 ] = -center[ 2 ];
			
			float dpan = ( float ) ( dx * panFactor * sensitivity / canvas.getWidth( ) );
			float dtilt = ( float ) ( dy * tiltFactor * sensitivity / canvas.getHeight( ) );
			
			rotY( m1 , dpan );
			mmulAffine( m1 , m2 , m2 );
			
			setRotation( m1 , axis , dtilt );
			mmulAffine( m1 , m2 , m2 );
			
			setIdentity( m1 );
			setColumn3( m1 , 3 , center );
			
			mmulAffine( m1 , m2 , m2 );
			mmulAffine( v , m2 , v );
			scene.setViewXform( v );
		}
		
		if( callDisplay )
		{
			this.canvas.repaint( );
		}
	}
}
