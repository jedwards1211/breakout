package org.andork.jogl.basic.awt;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.andork.jogl.basic.BasicJOGLScene;
import org.andork.vecmath.Vecmath;


public class BasicNavigator extends MouseAdapter
{
	final Component			canvas;
	final BasicJOGLScene	scene;
	
	MouseEvent				lastEvent	= null;
	float[ ]				v			= new float[ 3 ];
	MouseEvent				pressEvent	= null;
	
	final float[ ]			temp		= Vecmath.newMat4f( );
	final float[ ]			cam			= Vecmath.newMat4f( );
	
	float					lastPan		= 0;
	
	boolean					active		= true;
	boolean					callDisplay	= true;
	
	float					moveFactor	= 0.05f;
	float					panFactor	= ( float ) Math.PI;
	float					tiltFactor	= ( float ) Math.PI;
	float					wheelFactor	= 0.5f;
	
	public BasicNavigator( BasicJOGLSetup setup )
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
	
	public float getMoveFactor( )
	{
		return moveFactor;
	}
	
	public void setMoveFactor( float moveFactor )
	{
		this.moveFactor = moveFactor;
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
	
	public float getWheelFactor( )
	{
		return wheelFactor;
	}
	
	public void setWheelFactor( float wheelFactor )
	{
		this.wheelFactor = wheelFactor;
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
		if( !active || pressEvent == null || pressEvent.isControlDown( ) )
		{
			return;
		}
		
		int dx = e.getX( ) - lastEvent.getX( );
		int dy = e.getY( ) - lastEvent.getY( );
		lastEvent = e;
		
		Vecmath.invAffine( scene.v , cam );
		
		Component canvas = ( Component ) e.getSource( );
		
		if( pressEvent.getButton( ) == MouseEvent.BUTTON1 )
		{
			Vecmath.mvmulAffine( cam , 0 , 0 , 1 , v );
			
			float xz = ( float ) Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 2 ] * v[ 2 ] );
			
			float tilt = ( float ) Math.atan2( v[ 1 ] , xz );
			float pan = Math.abs( tilt ) == Math.PI / 2 ? lastPan : ( float ) Math.atan2( v[ 0 ] , v[ 2 ] );
			
			lastPan = pan;
			
			float dpan = ( float ) ( dx * panFactor / canvas.getWidth( ) );
			float dtilt = ( float ) ( dy * tiltFactor / canvas.getHeight( ) );
			
			Vecmath.rotY( temp , dpan );
			Vecmath.mmulRotational( temp , cam , cam );
			
			Vecmath.mvmulAffine( cam , 1 , 0 , 0 , v );
			Vecmath.setRotation( temp , v , dtilt );
			Vecmath.mmulRotational( temp , cam , cam );
			
			Vecmath.invAffine( cam , scene.v );
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON2 )
		{
			cam[ 12 ] += cam[ 8 ] * dy * moveFactor;
			cam[ 13 ] += cam[ 9 ] * dy * moveFactor;
			cam[ 14 ] += cam[ 10 ] * dy * moveFactor;
			Vecmath.invAffine( cam , scene.v );
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON3 )
		{
			cam[ 12 ] += cam[ 0 ] * -dx * moveFactor + cam[ 4 ] * dy * moveFactor;
			cam[ 13 ] += cam[ 1 ] * -dx * moveFactor + cam[ 5 ] * dy * moveFactor;
			cam[ 14 ] += cam[ 2 ] * -dx * moveFactor + cam[ 6 ] * dy * moveFactor;
			Vecmath.invAffine( cam , scene.v );
		}
		
		if( callDisplay )
		{
			this.canvas.repaint( );
		}
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( !active || e.isControlDown( ) )
		{
			return;
		}
		
		Vecmath.invAffine( scene.v , cam );
		
		float distance = e.getWheelRotation( ) * wheelFactor;
		
		cam[ 12 ] += cam[ 8 ] * distance;
		cam[ 13 ] += cam[ 9 ] * distance;
		cam[ 14 ] += cam[ 10 ] * distance;
		
		Vecmath.invAffine( cam , scene.v );
		
		if( callDisplay )
		{
			this.canvas.repaint( );
		}
	}
}
