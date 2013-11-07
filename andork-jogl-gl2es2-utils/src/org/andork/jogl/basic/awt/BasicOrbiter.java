package org.andork.jogl.basic.awt;

import static org.andork.vecmath.Vecmath.invAffine;
import static org.andork.vecmath.Vecmath.mmulAffine;
import static org.andork.vecmath.Vecmath.mvmulAffine;
import static org.andork.vecmath.Vecmath.normalize3;
import static org.andork.vecmath.Vecmath.rotY;
import static org.andork.vecmath.Vecmath.setColumn3;
import static org.andork.vecmath.Vecmath.setIdentity;
import static org.andork.vecmath.Vecmath.setRotation;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.vecmath.Vecmath;

public class BasicOrbiter extends MouseAdapter
{
	final Component			canvas;
	final BasicJOGLScene	scene;
	MouseEvent				lastEvent	= null;
	final float[ ]			center		= { 0 , 0 , 0 };
	final float[ ]			axis		= new float[ 3 ];
	MouseEvent				pressEvent	= null;
	final float[ ]			m1			= Vecmath.newMat4f( );
	final float[ ]			m2			= Vecmath.newMat4f( );
	boolean					active		= true;
	boolean					callDisplay	= true;
	float					panFactor	= ( float ) Math.PI;
	float					tiltFactor	= ( float ) Math.PI;
	
	public BasicOrbiter( BasicJOGLSetup setup )
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
	
	public void setTiltFactor( float tiltFactor )
	{
		this.tiltFactor = tiltFactor;
	}
	
	public void setCenter( float[ ] center )
	{
		Vecmath.setf( this.center , center );
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
		
		Component canvas = ( Component ) e.getSource( );
		
		if( pressEvent.getButton( ) == MouseEvent.BUTTON1 && pressEvent.isControlDown( ) )
		{
			int dx = e.getX( ) - lastEvent.getX( );
			int dy = e.getY( ) - lastEvent.getY( );
			lastEvent = e;
			
			invAffine( scene.v , m1 );
			mvmulAffine( m1 , 1 , 0 , 0 , axis );
			normalize3( axis );
			
			setIdentity( m1 );
			setIdentity( m2 );
			
			m2[ 3 ] = -center[ 0 ];
			m2[ 7 ] = -center[ 1 ];
			m2[ 11 ] = -center[ 2 ];
			
			float dpan = ( float ) ( dx * panFactor / canvas.getWidth( ) );
			float dtilt = ( float ) ( dy * tiltFactor / canvas.getHeight( ) );
			
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
			this.canvas.repaint( );
		}
	}
}
