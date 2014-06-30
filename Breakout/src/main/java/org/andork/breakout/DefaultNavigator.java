package org.andork.breakout;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.andork.jogl.neu.JoglScene;
import org.andork.jogl.neu.awt.BasicJoglSetup;
import org.andork.math3d.Vecmath;

public class DefaultNavigator extends MouseAdapter
{
	final Component	canvas;
	final JoglScene	scene;
	
	MouseEvent		lastEvent	= null;
	float[ ]		v			= new float[ 3 ];
	MouseEvent		pressEvent	= null;
	
	final float[ ]	temp		= Vecmath.newMat4f( );
	final float[ ]	cam			= Vecmath.newMat4f( );
	
	float			lastPan		= 0;
	
	boolean			active		= true;
	boolean			callDisplay	= true;
	
	float			moveFactor	= 0.05f;
	float			panFactor	= ( float ) Math.PI;
	float			tiltFactor	= ( float ) Math.PI;
	float			wheelFactor	= 1f;
	
	float			sensitivity	= 1f;
	
	public DefaultNavigator( BasicJoglSetup setup )
	{
		super( );
		this.canvas = setup.getCanvas( );
		this.scene = setup.getScene( );
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
	
	public float getSensitivity( )
	{
		return sensitivity;
	}
	
	public void setSensitivity( float sensitivity )
	{
		this.sensitivity = sensitivity;
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
		if( pressEvent == null )
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
		
		float dx = e.getX( ) - lastEvent.getX( );
		float dy = e.getY( ) - lastEvent.getY( );
		if( e.isControlDown( ) )
		{
			dx /= 10f;
			dy /= 10f;
		}
		lastEvent = e;
		
		scene.getViewXform( cam );
		Vecmath.invAffine( cam );
		
		Vecmath.mvmulAffine( cam , 0 , 0 , 1 , v );
		
		float xz = ( float ) Math.sqrt( v[ 0 ] * v[ 0 ] + v[ 2 ] * v[ 2 ] );
		
		float tilt = ( float ) Math.atan2( v[ 1 ] , xz );
		float pan = Math.abs( tilt ) == Math.PI / 2 ? lastPan : ( float ) Math.atan2( v[ 0 ] , v[ 2 ] );
		
		lastPan = pan;
		
		Component canvas = ( Component ) e.getSource( );
		
		float scaledMoveFactor = moveFactor * sensitivity;
		if( pressEvent.getButton( ) == MouseEvent.BUTTON1 )
		{
			if( e.isShiftDown( ) )
			{
				float dpan = ( float ) ( dx * panFactor * sensitivity / canvas.getWidth( ) );
				
				Vecmath.rotY( temp , dpan );
				Vecmath.mmulRotational( temp , cam , cam );
				
				float dtilt = ( float ) ( dy * tiltFactor * sensitivity / canvas.getHeight( ) );
				Vecmath.mvmulAffine( cam , 1 , 0 , 0 , v );
				Vecmath.setRotation( temp , v , dtilt );
				Vecmath.mmulRotational( temp , cam , cam );
				
				Vecmath.invAffine( cam );
				scene.setViewXform( cam );
			}
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON2 )
		{
			cam[ 12 ] += cam[ 8 ] * dy * scaledMoveFactor;
			cam[ 13 ] += cam[ 9 ] * dy * scaledMoveFactor;
			cam[ 14 ] += cam[ 10 ] * dy * scaledMoveFactor;
			Vecmath.invAffine( cam );
			scene.setViewXform( cam );
		}
		else if( pressEvent.getButton( ) == MouseEvent.BUTTON3 )
		{
			if( e.isShiftDown( ) )
			{
				float dpan = ( float ) ( dx * panFactor * sensitivity / canvas.getWidth( ) );
				
				Vecmath.rotY( temp , dpan );
				Vecmath.mmulRotational( temp , cam , cam );
				
				cam[ 12 ] -= cam[ 8 ] / xz * dy * scaledMoveFactor;
				cam[ 14 ] -= cam[ 10 ] / xz * dy * scaledMoveFactor;
				
				Vecmath.invAffine( cam );
				scene.setViewXform( cam );
			}
			else
			{
				cam[ 12 ] += cam[ 0 ] * -dx * scaledMoveFactor + cam[ 4 ] * dy * scaledMoveFactor;
				cam[ 13 ] += cam[ 1 ] * -dx * scaledMoveFactor + cam[ 5 ] * dy * scaledMoveFactor;
				cam[ 14 ] += cam[ 2 ] * -dx * scaledMoveFactor + cam[ 6 ] * dy * scaledMoveFactor;
				Vecmath.invAffine( cam );
				scene.setViewXform( cam );
			}
		}
		
		if( callDisplay )
		{
			this.canvas.repaint( );
		}
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e )
	{
		if( !active )
		{
			return;
		}
		
		scene.getViewXform( cam );
		Vecmath.invAffine( cam );
		
		float distance = e.getWheelRotation( ) * wheelFactor * sensitivity;
		if( e.isControlDown( ) )
		{
			distance /= 10f;
		}
		
		cam[ 12 ] += cam[ 8 ] * distance;
		cam[ 13 ] += cam[ 9 ] * distance;
		cam[ 14 ] += cam[ 10 ] * distance;
		
		Vecmath.invAffine( cam );
		scene.setViewXform( cam );
		
		if( callDisplay )
		{
			this.canvas.repaint( );
		}
	}
}
